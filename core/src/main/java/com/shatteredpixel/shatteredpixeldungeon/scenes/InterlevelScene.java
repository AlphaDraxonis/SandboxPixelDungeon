/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.utils.BArray;
import com.watabou.utils.DeviceCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class InterlevelScene extends PixelScene {
	
	//slow fade on entering a new region
	private static final float SLOW_FADE = 1f; //.33 in, 1.33 steady, .33 out, 2 seconds total
	//norm fade when loading, falling, returning, or descending to a new floor
	private static final float NORM_FADE = 0.67f; //.33 in, .67 steady, .33 out, 1.33 seconds total
	//fast fade when ascending, or descending to a floor you've been on
	private static final float FAST_FADE = 0.50f; //.33 in, .33 steady, .33 out, 1 second total
	
	private static float fadeTime;
	
	public enum Mode {
		DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL, RESET, NONE
	}
	public static Mode mode;

    public static LevelTransition curTransition = null;
    public static String returnLevel;
    public static int returnPos, returnBranch;

	public static boolean fallIntoPit;
	
	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}
	private Phase phase;
	private float timeLeft;
	
	private RenderedTextBlock message;
	
	private static Thread thread;
	private static Exception error = null;
	private float waitingTime;

	public static int lastRegion = -1;

    {
        inGameScene = true;
    }

    @Override
    public void create() {
        super.create();

        String loadingAsset;
        int loadingDepth;
        final float scrollSpeed;
        fadeTime = NORM_FADE;
        switch (mode) {
            default:
                scrollSpeed = 0;
                break;
            case CONTINUE:
                scrollSpeed = 5;
                break;
            case DESCEND:
                if (Dungeon.hero == null) {
                    fadeTime = SLOW_FADE;
                } else {
                    loadingDepth = Dungeon.customDungeon.getFloor(curTransition.destLevel).getDepth();
                    if (Statistics.deepestFloor >= loadingDepth) {
                        fadeTime = FAST_FADE;
                    } else if (Dungeon.customDungeon.getFloor(Dungeon.levelName).hasBoss()) {
                        fadeTime = SLOW_FADE;
                    }
                }
                scrollSpeed = 5;
                break;
            case FALL:
                scrollSpeed = 50;
                break;
            case ASCEND:
                fadeTime = FAST_FADE;
                scrollSpeed = -5;
                break;
            case RETURN:
                scrollSpeed = Dungeon.customDungeon.getFloor(returnLevel).getDepth() > Dungeon.depth ? 15 : -15;
                break;
        }

        //flush the texture cache whenever moving between regions, helps reduce memory load
        if (curTransition != null) {
            int region = Dungeon.customDungeon.getFloor(curTransition.destLevel).getRegion();
            if (region != lastRegion) {
                TextureCache.clear();
                lastRegion = region;
            }
        }

		if      (lastRegion == 1)    loadingAsset = Assets.Interfaces.LOADING_SEWERS;
		else if (lastRegion == 2)    loadingAsset = Assets.Interfaces.LOADING_PRISON;
		else if (lastRegion == 3)    loadingAsset = Assets.Interfaces.LOADING_CAVES;
		else if (lastRegion == 4)    loadingAsset = Assets.Interfaces.LOADING_CITY;
		else if (lastRegion == 5)    loadingAsset = Assets.Interfaces.LOADING_HALLS;
		else                         loadingAsset = Assets.Interfaces.SHADOW;
		
		if (DeviceCompat.isDebug()){
			fadeTime = 0f;
		}
		
		SkinnedBlock bg = new SkinnedBlock(Camera.main.width, Camera.main.height, loadingAsset ){
			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}
			
			@Override
			public void draw() {
				Blending.disable();
				super.draw();
				Blending.enable();
			}
			
			@Override
			public void update() {
				super.update();
				offset(0, Game.elapsed * scrollSpeed);
			}
		};
		bg.scale(4, 4);
		bg.autoAdjust = true;
		add(bg);
		
		Image im = new Image(TextureCache.createGradient(0xAA000000, 0xBB000000, 0xCC000000, 0xDD000000, 0xFF000000)){
			@Override
			public void update() {
				super.update();
				if (phase == Phase.FADE_IN)         aa = Math.max( 0, (timeLeft - (fadeTime - 0.333f)));
				else if (phase == Phase.FADE_OUT)   aa = Math.max( 0, (0.333f - timeLeft));
				else                                aa = 0;
			}
		};
		im.angle = 90;
		im.x = Camera.main.width;
		im.scale.x = Camera.main.height/5f;
		im.scale.y = Camera.main.width;
		add(im);

		String text = Messages.get(Mode.class, mode.name());
		
		message = PixelScene.renderTextBlock( text, 9 );
		message.setPos(
				(Camera.main.width - message.width()) / 2,
				(Camera.main.height - message.height()) / 2
		);
		align(message);
		add( message );

		phase = Phase.FADE_IN;
		timeLeft = fadeTime;
		
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					
					try {

						Actor.fixTime();

						switch (mode) {
							case DESCEND:
								descend();
								break;
							case ASCEND:
								ascend();
								break;
							case CONTINUE:
								restore();
								break;
							case RESURRECT:
								resurrect();
								break;
							case RETURN:
								returnTo();
								break;
							case FALL:
								fall();
								break;
							case RESET:
								reset();
								break;
						}
						
					} catch (Exception e) {
						
						error = e;
						
					}

					synchronized (thread) {
						if (phase == Phase.STATIC && error == null) {
							phase = Phase.FADE_OUT;
							timeLeft = fadeTime;
						}
					}
				}
			};
			thread.start();
		}
		waitingTime = 0f;
	}
	
	@Override
	public void update() {
		super.update();

		waitingTime += Game.elapsed;
		
		float p = timeLeft / fadeTime;
		
		switch (phase) {
		
		case FADE_IN:
			message.alpha( 1 - p );
			if ((timeLeft -= Game.elapsed) <= 0) {
				synchronized (thread) {
					if (!thread.isAlive() && error == null) {
						phase = Phase.FADE_OUT;
						timeLeft = fadeTime;
					} else {
						phase = Phase.STATIC;
					}
				}
			}
			break;
			
		case FADE_OUT:
			message.alpha( p );
			
			if ((timeLeft -= Game.elapsed) <= 0) {
				Game.switchScene( GameScene.class );
				thread = null;
				error = null;
			}
			break;
			
		case STATIC:
			if (error != null) {
				String errorMsg;
				if (error instanceof FileNotFoundException)     errorMsg = Messages.get(this, "file_not_found");
				else if (error instanceof IOException)          errorMsg = Messages.get(this, "io_error");
				else if (error.getMessage() != null &&
						error.getMessage().equals("old save")) errorMsg = Messages.get(this, "io_error");
				else if (error.getCause() instanceof CustomDungeonSaves.RenameRequiredException) errorMsg = error.getCause().getMessage();

				else throw new RuntimeException("fatal error occured while moving between floors. " +
							"Seed:" + Dungeon.seed + " depth:" + Dungeon.depth, error);

                    add(new WndError(errorMsg) {
                        {
                            setHighligtingEnabled(!(error.getCause() instanceof CustomDungeonSaves.RenameRequiredException));
                        }
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                    thread = null;
                    error = null;
                } else if (thread != null && (int) waitingTime == 10) {
                    waitingTime = 11f;
                    String s = "";
                    for (StackTraceElement t : thread.getStackTrace()) {
                        s += "\n";
                        s += t.toString();
                    }
                    SandboxPixelDungeon.reportException(
                            new RuntimeException("waited more than 10 seconds on levelgen. " +
                                    "Seed:" + Dungeon.seed + " depth:" + Dungeon.depth + " trace:" +
                                    s)
                    );
                    add(new WndError(Messages.get(InterlevelScene.class,"could_not_generate", Dungeon.seed)) {
                        public void onBackPressed() {
                            super.onBackPressed();
                            Game.switchScene(StartScene.class);
                        }
                    });
                }
                break;
        }
    }

	private void descend() throws IOException {

		if (Dungeon.hero == null) {
			Mob.clearHeldAllies();
			Dungeon.init();
			GameLog.wipe();

        	Level level = Dungeon.newLevel(null, 0);
        	Dungeon.switchLevel(level, -1);
    	} else {
        	Mob.holdAllies(Dungeon.level);
        	Dungeon.saveAll();

        	Level level;
        	Dungeon.depth = Dungeon.customDungeon.getFloor(curTransition.destLevel).getDepth();
			String oldLvlName = Dungeon.levelName;
			int oldBranch = Dungeon.branch;
        	Dungeon.levelName = curTransition.destLevel;
        	Dungeon.branch = curTransition.destBranch;
        	if (Dungeon.levelHasBeenGenerated(Dungeon.levelName, Dungeon.branch)) {
            	level = Dungeon.loadLevel(GamesInProgress.curSlot);
        	} else {
            	level = Dungeon.newLevel(oldLvlName, oldBranch);
        	}

        	int destCell = curTransition.destCell;
        	curTransition = null;
        	Dungeon.switchLevel(level, destCell);
    	}

	}

	private void fall() throws IOException {
		
		Mob.holdAllies( Dungeon.level );
		
		Buff.affect( Dungeon.hero, Chasm.Falling.class );
		Dungeon.saveAll();

        Level level;
		String oldLvlName = Dungeon.levelName;
		int oldBranch = Dungeon.branch;
        Dungeon.levelName = Dungeon.customDungeon.getFloor(Dungeon.levelName).getChasm();
        Dungeon.depth = Dungeon.customDungeon.getFloor(Dungeon.levelName).getDepth();
        if (Dungeon.levelHasBeenGenerated(Dungeon.levelName, Dungeon.branch)) {
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        } else {
            level = Dungeon.newLevel(oldLvlName, oldBranch);
        }
        Dungeon.switchLevel(level, level.fallCell(fallIntoPit));
    }

    private void ascend() throws IOException {

        Mob.holdAllies(Dungeon.level);

        Dungeon.saveAll();
        Dungeon.depth = Dungeon.customDungeon.getFloor(curTransition.destLevel).getDepth();
		String oldLvlName = Dungeon.levelName;
		int oldBranch = Dungeon.branch;
        Dungeon.levelName = curTransition.destLevel;
        Dungeon.branch = curTransition.destBranch;
        Level level;
        if (Dungeon.levelHasBeenGenerated(Dungeon.levelName, Dungeon.branch)) {
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        } else {
            level = Dungeon.newLevel(oldLvlName, oldBranch);
        }

        int destCell;
        if (curTransition.destCell != -1) destCell = curTransition.destCell;
        else {
            curTransition.destType = LevelTransition.Type.REGULAR_EXIT;
            destCell = level.getTransition(curTransition.destCell).departCell;
        }
        curTransition = null;
        Dungeon.switchLevel(level, destCell);
//        LevelTransition destTransition = level.getTransition(curTransition.departCell);
//        curTransition = null;
//        Dungeon.switchLevel(level, destTransition.cell());
    }

    private void returnTo() throws IOException {

        Mob.holdAllies(Dungeon.level);

        Dungeon.saveAll();
        Dungeon.depth = Dungeon.customDungeon.getFloor(returnLevel).getDepth();
		String oldLvlName = Dungeon.levelName;
		int oldBranch = Dungeon.branch;
        Dungeon.levelName = returnLevel;
        Dungeon.branch = returnBranch;
        Level level;
        if (Dungeon.levelHasBeenGenerated(Dungeon.levelName, Dungeon.branch)) {
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        } else {
            level = Dungeon.newLevel(oldLvlName, oldBranch);
        }
        Dungeon.switchLevel(level, returnPos);
    }

    private void restore() throws IOException {

        Mob.clearHeldAllies();

		GameLog.wipe();

        Dungeon.loadGame(GamesInProgress.curSlot);
        Level level;
		if (Dungeon.levelHasBeenGenerated(Dungeon.levelName, Dungeon.branch)) {
            level = Dungeon.loadLevel(GamesInProgress.curSlot);
        } else {
            level = Dungeon.newLevel(null, 0);
        }
        Dungeon.switchLevel(level, Dungeon.hero.pos);
    }

    private void resurrect() {

        Mob.holdAllies(Dungeon.level);

		Level level;
		if (Dungeon.level.locked) {
			ArrayList<Item> preservedItems = Dungeon.level.getItemsToPreserveFromSealedResurrect();

			Dungeon.hero.resurrect();
			level = Dungeon.newLevel(null, 0);
			Dungeon.hero.pos = level.randomRespawnCell(Dungeon.hero);
			if (Dungeon.hero.pos == -1) Dungeon.hero.pos = level.entrance();

			for (Item i : preservedItems){
				int pos = level.randomRespawnCell(null);
				if (pos == -1) pos = level.entrance();
				level.drop(i, pos);
			}
			int pos = level.randomRespawnCell(null);
			if (pos == -1) pos = level.entrance();
			level.drop(new LostBackpack(), pos);

		} else {
			level = Dungeon.level;
			BArray.setFalse(level.heroFOV);
			BArray.setFalse(level.visited);
			BArray.setFalse(level.mapped);
			int invPos = Dungeon.hero.pos;
			int tries = 0;
			do {
				Dungeon.hero.pos = level.randomRespawnCell(Dungeon.hero);
				tries++;

                //prevents spawning on traps or plants, prefers farther locations first
            } while (Dungeon.hero.pos == -1
                    || level.traps.get(Dungeon.hero.pos) != null
                    || (level.plants.get(Dungeon.hero.pos) != null && tries < 500)
                    || level.trueDistance(invPos, Dungeon.hero.pos) <= 30 - (tries / 10));

			//directly trample grass
			if (level.map[Dungeon.hero.pos] == Terrain.HIGH_GRASS || level.map[Dungeon.hero.pos] == Terrain.FURROWED_GRASS){
				level.map[Dungeon.hero.pos] = Terrain.GRASS;
			}
			Dungeon.hero.resurrect();
			level.drop(new LostBackpack(), invPos);
		}

		Dungeon.switchLevel( level, Dungeon.hero.pos );
	}

	private void reset() throws IOException {
		
		Mob.holdAllies( Dungeon.level );

        SpecialRoom.resetPitRoom(Dungeon.customDungeon.getFloor(Dungeon.levelName).getChasm());

		Level level = Dungeon.newLevel(null, 0);
		Dungeon.switchLevel( level, level.entrance() );
	}

	@Override
	protected void onBackPressed() {
		//Do nothing
	}
}