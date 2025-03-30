/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.services.updates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

import java.util.Date;

@NotAllowedInLua
public class Updates {

	public static UpdateService service;

	public static boolean supportsUpdates(){
		return service != null;
	}

	private static Date lastCheck = null;
	private static final long CHECK_DELAY = 1000*60*60; //1 hour

	public static boolean supportsUpdatePrompts(){
		return supportsUpdates() && service.supportsUpdatePrompts();
	}

	public static boolean supportsBetaChannel(){
		return supportsUpdates() && service.supportsBetaChannel();
	}

	public static void checkForUpdate(){
		if (!supportsUpdatePrompts()) return;
		if (lastCheck != null && (new Date().getTime() - lastCheck.getTime()) < CHECK_DELAY) return;

		//We do this so that automatically enabled beta checking (for users who DLed a beta) persists afterward
		if (SPDSettings.betas()){
			SPDSettings.betas(true);
		}

		service.checkForUpdate(!SPDSettings.WiFi(), SPDSettings.betas(), new UpdateService.UpdateResultCallback() {
			@Override
			public void onUpdateAvailable(AvailableUpdateData update) {
				lastCheck = new Date();
				updateData = update;
			}

			@Override
			public void onNoUpdateFound() {
				lastCheck = new Date();
			}

			@Override
			public void onConnectionFailed() {
				lastCheck = null;
			}
		});
	}

	public static void launchUpdate( AvailableUpdateData data ){
		service.initializeUpdate( data );
	}

	private static AvailableUpdateData updateData = null;

	public static boolean updateAvailable(){
		return updateData != null;
	}

	public static AvailableUpdateData updateData(){
		return updateData;
	}

	public static void clearUpdate(){
		updateData = null;
		lastCheck = null;
	}

	public static boolean supportsReviews() {
		return supportsUpdates() && service.supportsReviews();
	}

	public static void launchReview(Callback callback){
		if (supportsUpdates()){
			service.initializeReview(new UpdateService.ReviewResultCallback() {
				@Override
				public void onComplete() {
					callback.call();
				}
			});
		} else {
			callback.call();
		}
	}

	public static void openReviewURI(){
		if (supportsUpdates()){
			service.openReviewURI();
		}
	}


	private static Date lastDungeonsCheck = null;
	private static boolean newDungeonsAvailable = false;

	public static boolean newDungeonsAvailable() {
		return newDungeonsAvailable;
	}

	public static void dungeonsSeen() {
		newDungeonsAvailable = false;
	}

	public static void checkForNewCommunityDungeons(){

		if (SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
			return;
		}

		if (lastDungeonsCheck != null && (new Date().getTime() - lastDungeonsCheck.getTime()) < 60_000) {
			//one-minute delay
			return;
		}

		Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(ServerCommunication.getURL() + "?action=getLatestUploadTime");

		Gdx.net.sendHttpRequest(httpRequest, new com.badlogic.gdx.Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(com.badlogic.gdx.Net.HttpResponse httpResponse) {

				int statusCode = httpResponse.getStatus().getStatusCode();
				if (statusCode == 200) {
					lastCheck = new Date();
					String result = httpResponse.getResultAsString();
					try {
						long time = Long.parseLong(result);
						newDungeonsAvailable = SPDSettings.lastCheckedServer() < time;
					} catch (NumberFormatException e) {
						failed(e);
					}
				} else {
					//fail
					lastCheck = null;
				}
			}

			@Override
			public void failed(Throwable t) {
				lastCheck = null;
			}

			@Override
			public void cancelled() {
				lastCheck = null;
			}
		});
	}
}
