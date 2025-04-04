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

package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.watabou.NotAllowedInLua;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@NotAllowedInLua
public class GamesInProgress {

    public static final int MAX_SLOTS = 1_000_000;
	public static final int TEST_SLOT = 0;
	public static final int NO_SLOT = -1;

    //null means we have loaded info and it is empty, no entry means unknown.
    private static HashMap<Integer, Info> slotStates = new HashMap<>();
    public static int curSlot = NO_SLOT;

    public static HeroClass selectedClass;

	private static final String FOLDER = "games_in_progress/";
    private static final String GAME_FOLDER = FOLDER + "game%d";
    private static final String GAME_FILE = "game.dat";
    private static final String LEVEL_FILE = "level_%s.dat";
    private static final String DEPTH_BRANCH_FILE = "depth%d-branch%d.dat";
	private static final String CHECKPOINT_FOLDER = "checkpoint";
	public static final String TEST_SLOT_SAVE = "test_slot";

    public static boolean gameExists(int slot) {
        return FileUtils.dirExists(gameFolder(slot))
                && FileUtils.fileLength(gameFile(slot)) > 1;
    }
	
	public static boolean gameExists(FileHandle gameFolder) {
		return gameFolder.exists() && gameFolder.isDirectory()
				&& FileUtils.fileLength(gameFile(gameFolder)) > 1;
	}

    public static String gameFolder(int slot) {
        return slot == TEST_SLOT
				? TEST_SLOT_SAVE
				: Messages.format(GAME_FOLDER, slot);
    }

    public static String gameFile(int slot) {
        return gameFolder(slot) + "/" + GAME_FILE;
    }
	
	public static FileHandle gameFile(FileHandle gameFolder) {
		return gameFolder.child(GAME_FILE);
	}

    public static String levelFile(int slot, String levelName, int branch) {
        if (branch == 0) return gameFolder(slot) + "/" + Messages.format(LEVEL_FILE, levelName);
        return gameFolder(slot) + "/branch_" + branch + "/" + Messages.format(LEVEL_FILE, levelName);
    }

	public static String checkpointFolder(int slot) {
		return gameFolder(slot) + "/" + CHECKPOINT_FOLDER;
	}
	
	public static int firstEmpty(){
		for (int i = 1; i <= MAX_SLOTS; i++){
			if (check(i) == null) return i;
		}
		return -1;
	}
	
	public static synchronized ArrayList<Info> checkAll(){
		FileHandle dir = FileUtils.getFileHandle( FOLDER );
		ArrayList<Info> result = new ArrayList<>();
		
		if (dir != null && dir.isDirectory()){
			for (FileHandle file : dir.list()){
				try {
					String name = file.name();
					if (name.length() > 4) {//'game' has 4 letters
						Info curr = check(file, Integer.parseInt(name.substring(4)));
						if (curr != null) result.add(curr);
					}
				} catch (NumberFormatException ignored) {
					//there should be no files that could cause this exception
				}
			}
		}
		
		return result;
	}
	
	public static void doSort(List<Info> list) {
		switch (SPDSettings.gamesInProgressSort()){
			case "level": default:
				Collections.sort(list, levelComparator);
				break;
			case "last_played":
				Collections.sort(list, lastPlayedComparator);
				break;
		}
	}
	
	public static Info check( int slot ) {
		return check(FileUtils.getFileHandle( gameFolder(slot) ), slot);
	}
	
	public static Info check( FileHandle gameFolder, int slot ) {
		
		if (slotStates.containsKey( slot )) {

			if (slotStates.get(slot) != null && slotStates.get(slot).testGame) return null;
			return slotStates.get( slot );
			
		} else
			if (!gameExists( gameFolder )) {
			
			slotStates.put(slot, null);
			return null;
			
		} else {
			
			Info info;
			try {
				
				Bundle bundle = FileUtils.bundleFromFile(gameFile(gameFolder));

				if (bundle.getInt( "version" ) < SandboxPixelDungeon.v2_3_2) {
					info = null;
				} else {

					info = new Info();
					info.slot = slot;
					Dungeon.preview(info, bundle);
				}

			} catch (IOException e) {
				info = null;
			} catch (Exception e){
				SandboxPixelDungeon.reportException( e );
				info = null;
			}
			
			slotStates.put( slot, info );
			return info;
			
		}
	}

	public static void set(int slot) {
		Info info = new Info();
		info.slot = slot;

		info.lastPlayed = Dungeon.lastPlayed;
		
		info.depth = Dungeon.depth;
		info.levelName = Dungeon.levelName;
		info.challenges = Dungeon.challenges;

		info.seed = Dungeon.seed;
		info.customSeed = Dungeon.customSeedText;
		info.daily = Dungeon.daily;
		info.dailyReplay = Dungeon.dailyReplay;

		info.dungeonName = Dungeon.customDungeon.getName();

		info.level = Dungeon.hero.lvl;
		info.str = Dungeon.hero.STR;
		info.strBonus = Dungeon.hero.STR() - Dungeon.hero.STR;
		info.exp = Dungeon.hero.exp;
		info.hp = Dungeon.hero.HP;
		info.ht = Dungeon.hero.HT;
		info.shld = Dungeon.hero.shielding();
		info.heroClass = Dungeon.hero.heroClass;
		info.subClass = Dungeon.hero.subClass;
		info.armorTier = Dungeon.hero.tier();
		
		info.goldCollected = Statistics.goldCollected;
		info.maxDepth = Statistics.deepestFloor;

		info.testGame = Dungeon.isLevelTesting();

		info.checkpointReached = Dungeon.reachedCheckpoint != null;

		slotStates.put( slot, info );
	}
	
	public static void setUnknown( int slot ) {
		slotStates.remove( slot );
	}
	
	public static void delete( int slot ) {
		slotStates.put( slot, null );
	}
	
	public static class Info {
		public int slot;

		public int depth;
		public String levelName;
		public int version;
		public int challenges;

		public String dungeonName;

		public long seed;
		public String customSeed;
		public boolean daily;
		public boolean dailyReplay;
		public long lastPlayed;

		public int level;
		public int str;
		public int strBonus;
		public int exp;
		public int hp;
		public int ht;
		public int shld;
		public HeroClass heroClass;
		public HeroSubClass subClass;
		public int armorTier;
		
		public int goldCollected;
		public int maxDepth;

		public boolean testGame;

		public boolean checkpointReached;
		
		
		public StartScene.SaveSlotButton btn;
	}
	
	public static final Comparator<GamesInProgress.Info> levelComparator = new Comparator<GamesInProgress.Info>() {
		@Override
		public int compare(GamesInProgress.Info lhs, GamesInProgress.Info rhs ) {
			if (rhs.level != lhs.level){
				return (int)Math.signum( rhs.level - lhs.level );
			} else {
				return lastPlayedComparator.compare(lhs, rhs);
			}
		}
	};

	public static final Comparator<GamesInProgress.Info> lastPlayedComparator = new Comparator<GamesInProgress.Info>() {
		@Override
		public int compare(GamesInProgress.Info lhs, GamesInProgress.Info rhs ) {
			return (int)Math.signum( rhs.lastPlayed - lhs.lastPlayed );
		}
	};
	
	public static void moveOldSavesToNewLocation() {
		for (int slot = 1; slot <= 5; slot++) {
			FileHandle dir = FileUtils.getFileHandle( Messages.format("game%d", slot) );
			if (dir.exists() && dir.isDirectory()) {
				FileHandle old = dir;
				FileHandle neu = FileUtils.getFileHandle( gameFolder(slot) );
				old.moveTo(neu);
			}
		}
		FileHandle testGame = FileUtils.getFileHandle( Messages.format("game%d", 99) );
		testGame.deleteDirectory();
	}
}
