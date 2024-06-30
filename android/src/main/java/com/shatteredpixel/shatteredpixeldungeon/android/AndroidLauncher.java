/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.android;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.ViewConfiguration;
import android.widget.Toast;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.badlogic.gdx.backends.android.AsynchronousAndroidAudio;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.UpdateImpl;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.Game;
import com.watabou.utils.Consumer;
import com.watabou.utils.FileUtils;

public class AndroidLauncher extends AndroidApplication {

	public static final boolean FILE_ACCESS_ENABLED_ON_ANDROID_11 = false;//GPlay doesn't like apps that want to do this

	static final int REQUEST_DIRECTORY = 123, REQUEST_READ_EXTERNAL_STORAGE = 124;
	static Consumer<FileHandle> selectFileCallback;
	
	public static AndroidApplication instance;
	
	private static AndroidPlatformSupport support;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//If opened via clicking on a .dun file
		Intent data = getIntent();
		if (data != null && data.getData() != null) {
			if (!hasPermissionReadExternalStorage()) {
				requestForStoragePermissions();
				if (!hasPermissionReadExternalStorage()) return;//not granted
			}
			String error = null;
			FileHandle file = getFileHandleFromIntentData(data.getData());
			if (!file.exists()) error = "Error: " + data.getData().getPath() + " - File not found (Code: 11)";
			else if (!file.file().canRead()) error = "Cannot read the file. Please make sure to GRANT the PERMISSION!";
			if (error == null) {
				try {
					FileHandle fileDest = FileUtils.getFileHandle(FileUtils.getFileTypeForCustomDungeons(),
							CustomDungeonSaves.DUNGEON_FOLDER + file.name());
					FileHandle destDungeon = FileUtils.getFileHandle(FileUtils.getFileTypeForCustomDungeons(),
							CustomDungeonSaves.DUNGEON_FOLDER + file.nameWithoutExtension());

					//copies the file into the dungeon folder so it can be auto-imported when opening the dungeon selection
					if (!fileDest.exists() && !destDungeon.exists()) {
						file.copyTo(fileDest);
					} else error = "Dungeon \"" + file.nameWithoutExtension() + "\" already exists!";
				} catch (Exception e) {
					error = e.getMessage();
				}
			}
			if (error == null) {
				Toast.makeText(this, "Successfully copied file!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			}
		}

		try {
			GdxNativesLoader.load();
			FreeType.initFreeType();
		} catch (Exception e){
			AndroidMissingNativesHandler.error = e;
			Intent intent = new Intent(this, AndroidMissingNativesHandler.class);
			startActivity(intent);
			finish();
			return;
		}

		//there are some things we only need to set up on first launch
		if (instance == null) {

			instance = this;

			try {
				Game.version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
				Game.version = "???";
			}
			try {
				Game.versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				Game.versionCode = 0;
			}

			if (UpdateImpl.supportsUpdates()) {
				Updates.service = UpdateImpl.getUpdateService();
			}
			if (NewsImpl.supportsNews()) {
				News.service = NewsImpl.getNewsService();
			}

			FileUtils.setDefaultFileProperties(Files.FileType.Local, "");

			// grab preferences directly using our instance first
			// so that we don't need to rely on Gdx.app, which isn't initialized yet.
			// Note that we use a different prefs name on android for legacy purposes,
			// this is the default prefs filename given to an android app (.xml is automatically added to it)
			SPDSettings.set(instance.getPreferences("SandboxPixelDungeon"));

		} else {
			instance = this;
		}
		
		//set desired orientation (if it exists) before initializing the app.
		if (SPDSettings.landscape() != null) {
			instance.setRequestedOrientation( SPDSettings.landscape() ?
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE :
					ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT );
		}
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.depth = 0;
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			//use rgb565 on ICS devices for better performance
			config.r = 5;
			config.g = 6;
			config.b = 5;
		}

		//we manage this ourselves
		config.useImmersiveMode = false;
		
		config.useCompass = false;
		config.useAccelerometer = false;
		
		if (support == null) support = new AndroidPlatformSupport();
		else                 support.reloadGenerators();
		
		support.updateSystemUI();

		Button.longClick = ViewConfiguration.getLongPressTimeout()/1000f;

		initialize(new SandboxPixelDungeon(support), config);
		
	}

	@Override
	public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
		return new AsynchronousAndroidAudio(context, config);
	}

	@Override
	protected void onResume() {
		//prevents weird rare cases where the app is running twice
		if (instance != this){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				finishAndRemoveTask();
			} else {
				finish();
			}
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		//do nothing, game should catch all back presses
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		support.updateSystemUI();
	}
	
	@Override
	public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
		super.onMultiWindowModeChanged(isInMultiWindowMode);
		support.updateSystemUI();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DIRECTORY && resultCode == Activity.RESULT_OK) {
			FileHandle file = getFileHandleFromIntentData(data.getData());
            if (file.extension().equals(CustomDungeonSaves.EXPORT_FILE_EXTENSION.replace(".",""))) {
				String error = null;
				if (!file.exists()) error = "Error: " + data.getData().getPath() + " - File not found (Code: 2)";
				else if (!file.file().canRead()) error = "Cannot read the file. Please make sure to GRANT the PERMISSION!";
				if (error == null) selectFileCallback.accept(file);
				else Toast.makeText(this, error, Toast.LENGTH_LONG).show();
			} else
				Toast.makeText(this, "Invalid file: Only " + CustomDungeonSaves.EXPORT_FILE_EXTENSION + " files are permitted!", Toast.LENGTH_SHORT).show();
//			selectFileCallback.accept(convertUriToFileHandle(data.getData()));
        }
    }

    public boolean hasPermissionReadExternalStorage() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {//Android 11
				return Environment.isExternalStorageManager();
			} else {
				return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
			}
		}
		return false;
	}

	void requestForStoragePermissions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				if (!FILE_ACCESS_ENABLED_ON_ANDROID_11) return;
				try {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
					Uri uri = Uri.fromParts("package", this.getPackageName(), null);
					intent.setData(uri);
					startActivity(intent);
				} catch (Exception e) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
					startActivity(intent);
				}
			} else {
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
			}
		}
	}

	private static FileHandle getFileHandleFromIntentData(Uri data) {
		String path = data.getPath();
		if (path.startsWith("/document/")) path = path.replaceFirst("/document/.*:", "");
		if (!path.contains("storage/emulated")) path = path.replaceFirst("device_storage", "storage/emulated");
		int indexStart = path.indexOf("storage/emulated/0");
		if (indexStart == -1) path = "storage/emulated/0/" + path;
		else path = path.substring(indexStart);//cut everything in front of storage/emulated
		return Gdx.files.absolute(path);
	}
}