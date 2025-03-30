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
import com.badlogic.gdx.utils.Base64Coder;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.ServerDungeonList;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@NotAllowedInLua
public class UploadDungeonAction {

	private final ServerCommunication.UploadCallback callback;

	private int openResponses;
	private boolean canceled;
	
	private DungeonPreview uploadPreview;

	private List<Throwable> errors = new ArrayList<>(2);
	private List<Net.HttpRequest> openRequests = new ArrayList<>();

	private String folderID;

	//TODO maybe ask if want to include a dungeon before!
	public UploadDungeonAction(String dungeonName, String description, String userName, int difficulty, ServerCommunication.UploadCallback callback) {
		this.callback = callback;
		try {

			uploadPreview = new DungeonPreview();
			uploadPreview.title = dungeonName;
			uploadPreview.description = description;
			uploadPreview.version = Game.version;
			uploadPreview.intVersion = Game.versionCode;
			uploadPreview.uploader = userName;
			uploadPreview.difficulty = difficulty;

			Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
			httpRequest.setUrl(ServerCommunication.getURL()
					+ "?action=uploadStart"
					+ "&userID=" + ServerCommunication.getUUID()
					+ uploadPreview.writeArgumentsForURL());
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpRequest.setContent("empty");

			final FileHandle[] files = CustomDungeonSaves.uploadDungeon(dungeonName);

			if (files == null) {
				callback.failed(new FileNotFoundException());
				return;
			}

			callback.showWindow(httpRequest, () -> {
				canceled = true;
				for (Net.HttpRequest request : openRequests.toArray(new Net.HttpRequest[0])) Gdx.net.cancelHttpRequest(request);
				openRequests.clear();
				openResponses = 0;

				if (folderID != null) {
					sendCancel(folderID, folderID);
				}
			});

			Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
				@Override
				public void handleHttpResponse(Net.HttpResponse httpResponse) {
					int statusCode = httpResponse.getStatus().getStatusCode();
					if (statusCode == 200) {
						String result = httpResponse.getResultAsString();
						if (result.startsWith("true")) {
							folderID = result.substring(4);
							if (canceled) {
								UploadDungeonAction.sendCancel(folderID, folderID);
								return;
							}
							ServerDungeonList.dungeons = null;
							openResponses += files.length;

							Game.runOnRenderThread(() -> {
								callback.appendMessage(Messages.get(ServerCommunication.class, "connection_established"));
							});

							for (FileHandle f : files) {
								uploadFile(f);
							}
						}
						else if (result.startsWith("banned")) Game.runOnRenderThread(() -> callback.failed(new ServerCommunication.Banned()));
						else Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
					} else {
						Game.runOnRenderThread(() -> callback.failed(new SocketException(String.valueOf(statusCode))));
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

	private void uploadFile(FileHandle file) {
		if (file.isDirectory()) {
			uploadDirectory(file);
			return;
		}
		try {

			Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
			httpRequest.setUrl(ServerCommunication.getURL()
					+ "?action=uploadFile"
					+ "&userID=" + ServerCommunication.getUUID()
					+ "&folderID=" + folderID
					+ "&fileName=" + URLEncoder.encode(CustomDungeonSaves.cutBasePathFromFileName(file), "UTF-8"));
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
			openResponses += files.length - 1;
			for (FileHandle file : files) {
				uploadFile(file);
			}
		} else if (dir.exists()) uploadFile(dir);
	}

	private class FileUploadListener implements Net.HttpResponseListener {

		private final String fileName;

		private FileUploadListener(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void handleHttpResponse(Net.HttpResponse httpResponse) {
			int statusCode = httpResponse.getStatus().getStatusCode();
			if (statusCode == 200) {
				String result = httpResponse.getResultAsString();
				if (!result.startsWith("true")) {
					if (result.startsWith("banned")) errors.add(new ServerCommunication.Banned());
					else errors.add(new Exception(result));
				}
				else Game.runOnRenderThread(() -> {
					callback.appendMessage(Messages.get(ServerCommunication.class, "sent", fileName));
				});
			} else {
				errors.add((new SocketException(String.valueOf(statusCode))));
			}
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

		protected void decreaseOpenResponses() {
			openResponses--;
			if (openResponses <= 0) {

				if (canceled) return;

				if (errors.isEmpty()) {
					sendFinish();
				}
				else Game.runOnRenderThread(() -> callback.failed(errors.get(0)));
			}
		}

	}

	private void sendFinish() {
		callback.hideCancel();

		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
		httpRequest.setUrl(ServerCommunication.getURL()
				+ "?action=finishUpload"
				+ "&dungeonID=" + folderID
				+ "&userID=" + ServerCommunication.getUUID()
				+ uploadPreview.writeArgumentsForURL());
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpRequest.setContent("empty");

		Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				int statusCode = httpResponse.getStatus().getStatusCode();
				if (statusCode == 200) {
					String result = httpResponse.getResultAsString();
					if (result.startsWith("true")) {
						Game.runOnRenderThread(() -> callback.successful(folderID));
					} else if (result.startsWith("banned"))
						Game.runOnRenderThread(() -> callback.failed(new ServerCommunication.Banned()));
					else Game.runOnRenderThread(() -> callback.failed(new Exception(result)));
				} else {
					Game.runOnRenderThread(() -> callback.failed(new SocketException(String.valueOf(statusCode))));
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
	}

	public static void sendCancel(String dungeonID, String userIdFileName) {
		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
		httpRequest.setUrl(ServerCommunication.getURL()
				+ "?action=cancel"
				+ "&dungeonID=" + dungeonID
				+ "&userIdFileName=" + userIdFileName
				+ "&userID=" + ServerCommunication.getUUID());
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpRequest.setContent("empty");

		Gdx.net.sendHttpRequest(httpRequest, null);
	}
}
