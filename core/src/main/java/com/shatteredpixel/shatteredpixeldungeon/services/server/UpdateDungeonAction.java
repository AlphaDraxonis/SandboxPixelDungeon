/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.services.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Base64Coder;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;

import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerConstants.*;

@NotAllowedInLua
public class UpdateDungeonAction {

	private final ServerCommunication.UploadCallback callback;

	private int openResponses;
	private boolean canceled;
	
	private DungeonPreview uploadPreview;

	private final List<Throwable> errors = new ArrayList<>(2);
	private final List<Net.HttpRequest> openRequests = new ArrayList<>();
	
	private String dungeonID;
	private String versionID;//folder of the version
	private String uploadTempID;
	
	private String versionName;

	private FileHandle[] files;
	
	private int filesToSend;
	private int sentFiles;

	public UpdateDungeonAction(DungeonPreview oldDungeonPreview, String dungeonName, String title, String description, int difficulty, String versionName, ServerCommunication.UploadCallback callback) {
		this.callback = callback;
		try {
			
			this.dungeonID = oldDungeonPreview.dungeonID;

			uploadPreview = new DungeonPreview();
			uploadPreview.title = title;
			uploadPreview.description = description;
			uploadPreview.version = Game.version;
			uploadPreview.intVersion = Game.versionCode;
			uploadPreview.uploader = oldDungeonPreview.uploader;
			uploadPreview.difficulty = difficulty;

			this.versionName = versionName;
			
			if (dungeonName == null) {
				files = null;
			} else {
				files = CustomDungeonSaves.getFilesToUploadDungeon(dungeonName);
			}

			Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
			httpRequest.setUrl(ServerCommunication.getURL()
					+ "?action=" + ACTION_UPDATE_START
					+ "&userID=" + ServerCommunication.getUUID()
					+ "&dungeonID=" + dungeonID
					+ "&includesNewVersion=" + (files != null)
					+ uploadPreview.writeArgumentsForURL());
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpRequest.setContent("empty");

			callback.showWindow(httpRequest, () -> {
				canceled = true;
				for (Net.HttpRequest request : openRequests.toArray(new Net.HttpRequest[0])) Gdx.net.cancelHttpRequest(request);
				openRequests.clear();
				openResponses = 0;

				if (versionID != null) {
					UploadDungeonAction.sendCancel(dungeonID);
				}
			});

			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
				@Override
				public void handleHttpResponse(Net.HttpResponse httpResponse) {
					int statusCode = httpResponse.getStatus().getStatusCode();
					String result = httpResponse.getResultAsString();
					
					if (statusCode != HttpStatus.SC_OK) {
						Game.runOnRenderThread(() -> callback.failed(new SocketException(statusCode + "\n" + result)));
						return;
					}
					
					if (result.startsWith(KEYWORD_SUCCESS)) {
						
						String jsonResponse = result.substring(KEYWORD_SUCCESS.length());
						Bundle bundle = null;
						try {
							bundle = Bundle.class.getConstructor(String.class).newInstance(jsonResponse);
						} catch (Exception e) {
							e.printStackTrace();
						}
//						Bundle bundle = new com.watabou.utils.Bundle(jsonResponse);
						//dungeonID = bundle.getString("dungeonID"); //we already know the dungeonID…
						versionID = bundle.getString("versionID");
						uploadTempID = bundle.getString("uploadTempID");
						if (canceled) {
							UploadDungeonAction.sendCancel(dungeonID);
							return;
						}
						
						if (files == null) {
							sendFinish();
						} else {
							sentFiles = 0;
							filesToSend = countFiles(files);
							Game.runOnRenderThread(() -> {
								callback.setMessage(Messages.get(ServerCommunication.class, "uploading_files", sentFiles, filesToSend));
							});
							
							for (FileHandle f : files) {
								uploadFile(f);
							}
						}
					}
					else if (result.startsWith(KEYWORD_INVALID_DUNGEON_ID)) {
						//the dungeon we want to update was not found.
						Game.runOnRenderThread(() -> callback.failed(new InvalidDungeonIDException()));
					}
					else if (result.startsWith(KEYWORD_BANNED)) {
						Game.runOnRenderThread(() -> callback.failed(new ServerCommunication.Banned()));
					}
					else {
						Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
					}
				}

				@Override
				public void failed(Throwable throwable) {
					Game.runOnRenderThread(() -> callback.failed(throwable));
				}

				@Override
				public void cancelled() {
				}
			});


		} catch (Exception e) {
			Game.runOnRenderThread(() -> callback.failed(e));
		}
	}
	
	private int countFiles(FileHandle[] files) {
		int sum = 0;
		for (FileHandle f : files) {
			if (f.isDirectory()) {
				sum += countFiles(f.list());
			}
			else {
				sum++;
			}
		}
		return sum;
	}

	private void uploadFile(FileHandle file) {
		if (file.isDirectory()) {
			uploadDirectory(file);
			return;
		}
		try {
			
			openResponses++;

			Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
			httpRequest.setUrl(ServerCommunication.getURL()
					+ "?action=" + ACTION_UPLOAD_FILE
					+ "&userID=" + ServerCommunication.getUUID()
					+ "&versionID=" + versionID
					+ "&dungeonID=" + dungeonID
					+ "&fileName=" + URLEncoder.encode(CustomDungeonSaves.cutBasePathFromFileName(file), StandardCharsets.UTF_8));
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");

			byte[] bytes = file.readBytes();

			char[] content = Base64Coder.encode(bytes);
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < content.length; i++) {
				b.append(content[i]);
			}
			httpRequest.setContent("content=" + b);

			openRequests.add(httpRequest);
			Gdx.net.sendHttpRequest(httpRequest, new FileUploadListener(file.name()) {
				@Override
				protected void decreaseOpenResponses() {
					openRequests.remove(httpRequest);
					super.decreaseOpenResponses();
				}
			});
		} catch (Exception e) {
			SandboxPixelDungeon.reportException(e);
		}
	}

	private void uploadDirectory(FileHandle dir) {
		if (dir.isDirectory()) {
			FileHandle[] files = dir.list();
			for (FileHandle file : files) {
				uploadFile(file);
			}
		} else if (dir.exists()) {
			uploadFile(dir);
		}
	}

	private class FileUploadListener implements Net.HttpResponseListener {

		private final String fileName;

		private FileUploadListener(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void handleHttpResponse(Net.HttpResponse httpResponse) {
			int statusCode = httpResponse.getStatus().getStatusCode();
			String result = httpResponse.getResultAsString();
			
			if (statusCode != HttpStatus.SC_OK) {
				errors.add((new SocketException(statusCode + "\n" + result)));
				decreaseOpenResponses();
				return;
			}
			
			if (!result.startsWith(KEYWORD_SUCCESS)) {
				if (result.startsWith(KEYWORD_BANNED)) errors.add(new ServerCommunication.Banned());
				else errors.add(new Exception(result));
				decreaseOpenResponses();
				return;
			}
			
			Game.runOnRenderThread(() -> {
				sentFiles++;
				callback.setMessage(Messages.get(ServerCommunication.class, "uploading_files", sentFiles, filesToSend));
			});
			
			decreaseOpenResponses();
		}

		@Override
		public void failed(Throwable throwable) {
			errors.add(throwable);
			decreaseOpenResponses();
		}

		@Override
		public void cancelled() {
			errors.add(new Exception("canceled"));
			decreaseOpenResponses();
		}

		protected synchronized void decreaseOpenResponses() {
			openResponses--;
			if (openResponses <= 0) {
				
				if (canceled) return;

				if (errors.isEmpty()) {
					sendFinish();
				} else {
					Game.runOnRenderThread(() -> callback.failed(errors.get(0)));
				}
			}
		}

	}

	private void sendFinish() {
		callback.hideCancel();

		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
		httpRequest.setUrl(ServerCommunication.getURL()
				+ "?action=" + ACTION_UPDATE_FINISH
				+ "&versionID=" + versionID
				+ "&dungeonID=" + dungeonID
				+ "&uploadTempID=" + uploadTempID
				+ "&userID=" + ServerCommunication.getUUID()
				+ "&includesNewVersion=" + (files != null)
				+ uploadPreview.writeArgumentsForURL()
				+ "&versionName=" + versionName);
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpRequest.setContent("empty");

		Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				int statusCode = httpResponse.getStatus().getStatusCode();
				String result = httpResponse.getResultAsString();
				
				if (statusCode != HttpStatus.SC_OK) {
					Game.runOnRenderThread(() -> callback.failed(new SocketException(statusCode + "\n" + result)));
					return;
				}
				
				if (!result.startsWith(KEYWORD_SUCCESS)) {
					if (result.startsWith(KEYWORD_BANNED)) {
						Game.runOnRenderThread(() -> callback.failed(new ServerCommunication.Banned()));
					}
					else {
						Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
					}
					return;
				}
				
				Game.runOnRenderThread(() -> callback.successful(dungeonID));
			}

			@Override
			public void failed(Throwable throwable) {
				Game.runOnRenderThread(() -> callback.failed(throwable));
			}

			@Override
			public void cancelled() {
			}
		});
	}
	
	public static class InvalidDungeonIDException extends Exception {
	
	}
}
