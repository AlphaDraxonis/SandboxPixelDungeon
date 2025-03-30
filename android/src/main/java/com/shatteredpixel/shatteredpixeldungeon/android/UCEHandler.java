/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2025 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2025 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.android;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

public class UCEHandler implements Thread.UncaughtExceptionHandler {
	
	private Activity context;
	
	public UCEHandler(Activity context) {
		this.context = context;
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable exception) {
		
		StringBuilder b = new StringBuilder();
		
		Throwable e = exception;
		while (e != null) {
			
			b.append(e.getClass().getName());
			if (e.getMessage() != null) {
				b.append(": ").append(e.getMessage());
			}
			b.append('\n');
			for (StackTraceElement elem : e.getStackTrace()) {
				b.append("  at ").append(elem.getClassName()).append('.').append(elem.getMethodName()).append('(').append(elem.getLineNumber()).append(')').append('\n');
			}
			
			e = e.getCause();
			if (e != null) {
				b.append("\nCaused by: ");
			}
		}
		
		b.append("\n\n");
		
		//Also useful, but more personal: Build.BRAND,  Build.DEVICE,  Build.MODEL,  Build.PRODUCT,
		b.append("Android API version: ").append(Build.VERSION.SDK_INT).append('\n');
		
		ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("Copied crash report to clipboard!", b.toString());
		clipboard.setPrimaryClip(clip);
		
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
}
