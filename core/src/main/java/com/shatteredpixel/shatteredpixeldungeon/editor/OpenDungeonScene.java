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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.io.FileNotFoundException;
import java.io.IOException;

@NotAllowedInLua
public class OpenDungeonScene extends PixelScene {
	
	private static final float FADE = 0.2f;
	private static final float DOT_SPEED = 1f;
	
	public static void openDungeon(String dungeonName, Mode mode) {
		OpenDungeonScene.mode = mode;
		OpenDungeonScene.dungeonName = dungeonName;
		Game.switchScene( OpenDungeonScene.class );
	}
	
	public static void quickOpenDungeon(String dungeonName, Mode mode) {
		try {
			OpenDungeonScene.mode = mode;
			loadDungeon(dungeonName);
			mode.actuallyEnter();
		} catch (CustomDungeonSaves.RenameRequiredException e) {
			e.showExceptionWindow();
		} catch (Exception e) {
			error = e;
		}
	}
	
	public enum Mode {
		EDITOR_LOAD,
		GAME_LOAD;
		
		public void afterLoading() {
			switch (this) {
				case EDITOR_LOAD:
					EditorScene.isEditing = true;
					CustomTileLoader.loadTiles(EditorScene.openDifferentLevel);
					break;
				case GAME_LOAD:
					EditorScene.isEditing = false;
					CustomTileLoader.loadTiles(true);
					break;
			}
		}
		
		public void actuallyEnter() {
			switch (this) {
				case EDITOR_LOAD:
					String lastEditedFloor = Dungeon.customDungeon.getLastEditedFloor();
					LevelScheme l;
					if (Dungeon.customDungeon.getNumFloors() == 0 || lastEditedFloor == null || (l = Dungeon.customDungeon.getFloor(lastEditedFloor)) == null) {
						SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
					} else if (l.getType() != CustomLevel.class) {
						SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
					} else {
						l.loadLevel();
						if (l.getLevel() == null) {
							SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
						} else {
							EditorScene.open((CustomLevel) l.getLevel());
						}
					}
					break;
					
				case GAME_LOAD:
					SandboxPixelDungeon.switchScene(HeroSelectScene.class);
					break;
			}
		}
	}
	
	private static Mode mode;
	
	
	private static float fadeTime;

    public static String dungeonName = null;

	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}
	private Phase phase;
	private float timeLeft;

	public Image background;

	private RenderedTextBlock loadingText;
	
	private static Thread thread;
	private static Exception error = null;
	private float waitingTime;
	
	@Override
	public void create() {
		super.create();

		fadeTime = FADE;
		
		int loadingCenter = 400;

		//for portrait users, each run the splashes change what details they focus on
		
		String loadingAsset;
		switch (Random.Int(1, 5)){
			case 1:
				loadingAsset = Assets.Splashes.SEWERS;
				switch (Random.Int(2)){
					case 0: loadingCenter = 180; break; //focus on rats and left side
					case 1: loadingCenter = 485; break; //focus on center pipe and door
				}
				break;
			case 2:
				loadingAsset = Assets.Splashes.PRISON;
				switch (Random.Int(3)){
					case 0: loadingCenter = 190; break; //focus on left skeleton
					case 1: loadingCenter = 402; break; //focus on center arch
				}
				break;
			case 3:
				loadingAsset = Assets.Splashes.CAVES;
				switch (Random.Int(3)){
					case 0: loadingCenter = 340; break; //focus on center gnoll groups
					case 1: loadingCenter = 625; break; //focus on right gnoll
				}
				break;
			case 4:
				loadingAsset = Assets.Splashes.CITY;
				switch (Random.Int(3)){
					case 0: loadingCenter = 275; break; //focus on left bookcases
					case 1: loadingCenter = 485; break; //focus on center pathway
				}
				break;
			case 5: default:
				loadingAsset = Assets.Splashes.HALLS;
				switch (Random.Int(3)){
					case 0: loadingCenter = 145; break; //focus on left arches
					case 1: loadingCenter = 400; break; //focus on ripper demon
				}
				break;
		}

		background = new Image(loadingAsset);
		background.scale.set(Camera.main.height/background.height);

		if (Camera.main.width >= background.width()){
			background.x = (Camera.main.width - background.width())/2f;
		} else {
			background.x = Camera.main.width / 2f - loadingCenter * background.scale.x;
			background.x = GameMath.gate(Camera.main.width - background.width(), background.x, 0);
		}
		background.y = (Camera.main.height - background.height()) / 2f;
		PixelScene.align(background);
		add(background);

		Image fadeLeft, fadeRight;
		fadeLeft = new Image(TextureCache.createGradient(0xFF000000, 0xFF000000, 0x00000000));
		fadeLeft.x = background.x-2;
		fadeLeft.scale.set(3, background.height());
		fadeLeft.visible = background.x > 0;
		add(fadeLeft);

		fadeRight = new Image(fadeLeft);
		fadeRight.x = background.x + background.width() + 2;
		fadeRight.y = background.y + background.height();
		fadeRight.angle = 180;
		fadeRight.visible = fadeLeft.visible;
		add(fadeRight);

		Image im = new Image(TextureCache.createGradient(0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0xFF000000)){
			@Override
			public void update() {
				super.update();
				     if (phase == Phase.FADE_IN)    aa = Math.max( 0, 2*(timeLeft - (fadeTime - 0.333f)));
				else if (phase == Phase.FADE_OUT)   aa = Math.max( 0, 2*(0.333f - timeLeft));
				//else                                aa = 0;
			}
		};
		im.angle = 90;
		im.x = Camera.main.width;
		im.scale.x = Camera.main.height/5f;
		im.scale.y = Camera.main.width;
		add(im);

		String text = Messages.get(this, "loading");
		
		loadingText = PixelScene.renderTextBlock( text, 9 );
		loadingText.setPos(
				(Camera.main.width - loadingText.width() - 8),
				(Camera.main.height - loadingText.height() - 6)
		);
		align(loadingText);
		add(loadingText);


		phase = Phase.FADE_IN;
		timeLeft = fadeTime;

		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					
					try {
						loadDungeon(dungeonName);
					} catch (Exception e) {
						error = e;
					}

					if (thread != null) {
						synchronized (thread) {
							if (phase == Phase.STATIC && error == null) {
								afterLoading();
							}
						}
					}
				}
			};
			thread.start();
		}
		waitingTime = 0f;
	}

	private int dots = 0;
	private boolean textFadingIn = true;

	@Override
	public void update() {
		super.update();

		waitingTime += Game.elapsed;

		if (dots != Math.ceil(waitingTime / ((2*DOT_SPEED)/3f))) {
			String text = Messages.get(this, "loading");
			dots = (int)Math.ceil(waitingTime / ((2*DOT_SPEED)/3f))%3;
			switch (dots){
				case 1: default:
					loadingText.text(text + ".");
					break;
				case 2:
					loadingText.text(text + "..");
					break;
				case 0:
					loadingText.text(text + "...");
					break;
			}
		}
		
		switch (phase) {
		
		case FADE_IN:
			loadingText.alpha( Math.max(0, fadeTime - (timeLeft-0.333f)));
			if ((timeLeft -= Game.elapsed) <= 0) {
				synchronized (thread) {
					if (!thread.isAlive() && error == null) {
						afterLoading();
					} else {
						phase = Phase.STATIC;
					}
				}
			}
			break;
			
		case FADE_OUT:
			background.acc.set(0);
			background.speed.set(0);

			loadingText.alpha( Math.min(1, timeLeft+0.333f) );
			
			if ((timeLeft -= Game.elapsed) <= 0) {
				mode.actuallyEnter();
				thread = null;
				error = null;
			}
			break;
			
		case STATIC:

			if (error != null) {
				String errorMsg;
				if (error instanceof FileNotFoundException)     errorMsg = Messages.get(this, "file_not_found");
				else if (error instanceof IOException)          errorMsg = Messages.get(this, "io_error");
				else if (error.getCause() instanceof CustomDungeonSaves.RenameRequiredException) errorMsg = error.getCause().getMessage();

				else throw new RuntimeException("fatal error occurred during loading!");

				add( new WndError( errorMsg ) {
					{
						if (error.getCause() instanceof CustomDungeonSaves.RenameRequiredException) {
							setHighlightingEnabled(false);
						}
					}
					public void onBackPressed() {
						super.onBackPressed();
						CustomObjectManager.loadUserContentFromFiles();
						Game.switchScene( TitleScene.class );
						thread = null;
						error = null;
					}
				} );
			} else if (thread != null && (int)waitingTime == 60){
				waitingTime = 11f;
				String s = "";
				for (StackTraceElement t : thread.getStackTrace()){
					s += "\n";
					s += t.toString();
				}
				//we care about reporting game logic exceptions, not slow IO
//				if (!s.contains("FileUtils.bundleToFile")){
//					SandboxPixelDungeon.reportException(
//							new RuntimeException("waited more than 10 seconds on levelgen. " +
//									"Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" +
//									s));
//					add(new WndError(Messages.get(OpenDungeonScene.class,"could_not_generate", Dungeon.seed)) {
//						public void onBackPressed() {
//							super.onBackPressed();
//							CustomObjectManager.loadUserContentFromFiles();
//							Game.switchScene(StartScene.class);
//							thread = null;
//						}
//					});
//				}
			}
			break;
		}
	}
	
	private void afterLoading(){
		phase = Phase.FADE_OUT;
		timeLeft = fadeTime;
		dungeonName = null;
	}

	private static void loadDungeon(String dungeonName) throws Exception {
		
		Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(dungeonName);
		
		mode.afterLoading();
	}

	@Override
	protected void onBackPressed() {
		//Do nothing
	}
}