/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class DownloadDungeonAction {

	private final ServerCommunication.OnDungeonReceive callback;

	private int openResponses;
	private boolean canceled;

	private List<Throwable> errors = new ArrayList<>(2);
	private List<Net.HttpRequest> openRequests = new ArrayList<>();

	private String downloadToDir;
	private final String dungeonName;
	private boolean isCreator;

	public DownloadDungeonAction(String dungeonName, String folderID, ServerCommunication.OnDungeonReceive callback) {
		this.dungeonName = dungeonName;
		this.callback = callback;

		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(ServerCommunication.getURL() + "?action=downloadStart&folderID=" + folderID + "&userID=" + ServerCommunication.getUUID());

		callback.showWindow(httpRequest, () -> {
			canceled = true;
			Gdx.net.cancelHttpRequest(httpRequest);
			for (Net.HttpRequest request : openRequests.toArray(new Net.HttpRequest[0])) Gdx.net.cancelHttpRequest(request);
			openRequests.clear();
			openResponses = 0;

			if (downloadToDir != null) {
				FileHandle tempDir = FileUtils.getFileHandle(downloadToDir);
				if (tempDir.exists() && tempDir.isDirectory())
					tempDir.deleteDirectory();
			}
		});

		Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				if (canceled) return;
				int statusCode = httpResponse.getStatus().getStatusCode();
				if (statusCode == 200) {

					downloadToDir = CustomDungeonSaves.initializeDownloading(dungeonName);

					try {
						Bundle[] bundles = Bundle.read(httpResponse.getResultAsStream()).getBundleArray();
						isCreator = bundles[0].getBoolean("creator");

						Game.runOnRenderThread(() -> {
							callback.appendMessage(Messages.get(ServerCommunication.class, "connection_established"));
						});

						for (int i = 1; i < bundles.length; i++) {
							Bundle b = bundles[i];
							String id = b.getString("id");
							String path = b.getString("path");
							downloadFile(id, path);
						}
						if (bundles.length == 1) throw new RuntimeException("Files are missing on the server!");
					} catch (IOException e) {
						Game.runOnRenderThread(() -> callback.failed(e.getMessage() == null ? new IOException(String.valueOf(statusCode), e) : e));
					}

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

	private void downloadFile(String id, String path) {
		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(ServerCommunication.getURL() + "?action=downloadFile&fileID=" + id);
		openRequests.add(httpRequest);
		openResponses++;
		Gdx.net.sendHttpRequest(httpRequest, new FileDownloadListener(path) {
			@Override
			protected void decreaseOpenResponses() {
				openRequests.remove(httpRequest);
				super.decreaseOpenResponses();
			}
		});
	}

	private class FileDownloadListener implements Net.HttpResponseListener {

		private final String path;

		private FileDownloadListener(String path) {
			this.path = path;
		}

		@Override
		public void handleHttpResponse(Net.HttpResponse httpResponse) {
			if (canceled) return;
			int statusCode = httpResponse.getStatus().getStatusCode();
			if (statusCode == 200) {
				try {
					String result = httpResponse.getResultAsString();
					byte[] bytes = Base64Coder.decode(result.replace(' ', '+'));
					CustomDungeonSaves.writeBytesToFileNoBackup(downloadToDir, path, bytes);

					Game.runOnRenderThread(() -> {
						callback.appendMessage(Messages.get(ServerCommunication.class, "received", path));
					});

				} catch (Exception e) {
					errors.add(e);
				}
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

					try {
						CustomDungeonSaves.completeDownloading(downloadToDir, dungeonName);
						CustomDungeon dungeon = CustomDungeonSaves.loadDungeon(dungeonName);
						if (dungeon != null) {
							dungeon.downloaded = !isCreator;
							CustomDungeonSaves.saveDungeon(dungeon);
							Game.runOnRenderThread(() -> callback.accept(dungeon.createInfo()));
						} else throw new Exception("Dungeon is corrupted!");
					} catch (Exception e) {
						Game.runOnRenderThread(() -> callback.failed(e));
					}
				} else Game.runOnRenderThread(() -> callback.failed(errors.get(0)));
			}
		}

	}
}