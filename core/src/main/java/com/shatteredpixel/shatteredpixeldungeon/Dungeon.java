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

package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.ImpQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.recipes.CustomRecipe;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndResurrect;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;
import com.watabou.utils.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Dungeon {

	//enum of items which have limited spawns, records how many have spawned
	//could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
	public enum LimitedDrops {
		//limited world drops
		STRENGTH_POTIONS,
		UPGRADE_SCROLLS,
		ARCANE_STYLI,
		ENCH_STONE,
		INT_STONE,
		TRINKET_CATA,
		LAB_ROOM, //actually a room, but logic is the same

		//Health potion sources
		//enemies
		SWARM_HP,
		NECRO_HP,
		BAT_HP,
		WARLOCK_HP,
		//Demon spawners are already limited in their spawnrate, no need to limit their health drops
		//alchemy
		COOKING_HP,
		BLANDFRUIT_SEED,

		//Other limited enemy drops
		SLIME_WEP,
		SKELE_WEP,
		THEIF_MISC,
		GUARD_ARM,
		SHAMAN_WAND,
		DM200_EQUIP,
		GOLEM_EQUIP,

		//containers
		VELVET_POUCH,
		SCROLL_HOLDER,
		POTION_BANDOLIER,
		MAGICAL_HOLSTER,

		//lore documents
		LORE_SEWERS,
		LORE_PRISON,
		LORE_CAVES,
		LORE_CITY,
		LORE_HALLS;

		public int count = 0;

		//for items which can only be dropped once, should directly access count otherwise.
		public boolean dropped(){
			return count != 0;
		}
		public void drop(){
			count = 1;
		}

		public static void reset(){
			for (LimitedDrops lim : values()){
				lim.count = 0;
			}
		}

		public static void store( Bundle bundle ){
			for (LimitedDrops lim : values()){
				bundle.put(lim.name(), lim.count);
			}
		}

		public static void restore( Bundle bundle ){
			for (LimitedDrops lim : values()){
				if (bundle.contains(lim.name())){
					lim.count = bundle.getInt(lim.name());
				} else {
					lim.count = 0;
				}
				
			}

			//pre-v2.2.0 saves
			if (Dungeon.version < 750
					&& Dungeon.isChallenged(Challenges.NO_SCROLLS)
					&& UPGRADE_SCROLLS.count > 0){
				//we now count SOU fully, and just don't drop every 2nd one
				UPGRADE_SCROLLS.count += UPGRADE_SCROLLS.count-1;
			}
		}

	}

	public static int challenges;
	public static int mobsToChampion;

	public static Hero hero;
	public static Level level;

	public static QuickSlot quickslot = new QuickSlot();
	
	public static int depth;
	//determines path the hero is on. Current uses:
	// 0 is the default path
	// 1 is for quest sub-floors
	public static int branch;

	public static int gold;
	public static int energy;
	
	public static HashSet<Integer> chapters;

    public static HashMap<String, ArrayList<Item>> droppedItems;

	//first variable is only assigned when game is started, second is updated every time game is saved
	public static int initialVersion;
	public static int version;

	public static boolean daily;
	public static boolean dailyReplay;
	public static String customSeedText = "";
	public static long seed;

	public static String[] visited;
	public static String[] completed;
	private static Set<Integer> visitedDepths;

	public static CustomDungeon customDungeon;
	public static String levelName;

	public static void init() {

        String levelDir = GamesInProgress.gameFolder(GamesInProgress.curSlot) + "/";
        FileUtils.deleteDir(levelDir);
        FileUtils.resetDefaultFileType();
        try {
            CustomDungeonSaves.copyLevelsForNewGame(customDungeon.getName(), levelDir);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
        CustomDungeonSaves.setCurDirectory(levelDir);

        CustomTileLoader.loadTiles(false);
//
//        if (customDungeon == null) {
//            customDungeon = new CustomDungeon("DefaultDungeon");
//            customDungeon.initDefault();
//        } else {
//            try {
//                customDungeon = CustomDungeonSaves.loadDungeon(customDungeon.getName());
//            } catch (IOException e) {
//                SandboxPixelDungeon.reportException(e);
//            }
//        }

        visited = new String[]{};
        completed = new String[]{};
//
//        ghostLevel = RandomGenUtils.calculateQuestLevel(layout().getGhostSpawnLevels());
//        wandmakerLevel = RandomGenUtils.calculateQuestLevel(layout().getWandmakerSpawnLevels());
//        blacksmithLevel = RandomGenUtils.calculateQuestLevel(layout().getBlacksmithSpawnLevels());
//        impLevel = RandomGenUtils.calculateQuestLevel(layout().getImpSpawnLevels());
        visitedDepths = new HashSet<>();


        initialVersion = version = Game.versionCode;
        challenges = SPDSettings.challenges(true);
        mobsToChampion = -1;

		if (daily) {
			//Ensures that daily seeds are not in the range of user-enterable seeds
			seed = SPDSettings.lastDaily() + DungeonSeed.TOTAL_SEEDS;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			customSeedText = format.format(new Date(SPDSettings.lastDaily()));
		} else if (!SPDSettings.customSeed().isEmpty()){
			customSeedText = SPDSettings.customSeed();
			seed = DungeonSeed.convertFromText(customSeedText);
		} else {
			customSeedText = "";
			seed = DungeonSeed.randomSeed();
		}

		Actor.clear();
		Actor.resetNextID();

		//offset seed slightly to avoid output patterns
		Random.pushGenerator( seed+1 );

        	customDungeon.initSeeds();
        	//TODO assign groups here to the different levels

        	customDungeon.initDistribution();

        	SpecialRoom.initForRun();
       		 SecretRoom.initForRun();

        	Generator.fullReset();

        	int indexCurHero =  GamesInProgress.selectedClass.getIndex();
       		Dungeon.customDungeon.startItems[0].initRandoms();
        	Dungeon.customDungeon.startItems[indexCurHero + 1].initRandoms();

			for (CustomRecipe recipe : Dungeon.customDungeon.recipes.toArray(new CustomRecipe[0])) {
				recipe.initRandom();
				if (!recipe.isRecipeValid()) Dungeon.customDungeon.recipes.remove(recipe);
			}

       		Scroll.initLabels();
       		Potion.initColors();
        	Ring.initGems();

        Random.resetGenerators();

        Statistics.reset();
        Notes.reset();

        quickslot.reset();
        QuickSlotButton.reset();
        Toolbar.swappedQuickslots = false;

        levelName = customDungeon.getStart();
        depth = customDungeon.getFloor(levelName).getDepth();
        branch = 0;

        gold = Dungeon.customDungeon.startItems[0].gold + Dungeon.customDungeon.startItems[indexCurHero + 1].gold;
        energy = Dungeon.customDungeon.startItems[0].energy + Dungeon.customDungeon.startItems[indexCurHero + 1].energy;

        droppedItems = new HashMap<>();

        LimitedDrops.reset();

        chapters = new HashSet<>();

        GhostQuest.reset();
        WandmakerQuest.reset();
        BlacksmithQuest.reset();
        ImpQuest.reset();

        BossHealthBar.reset();

		hero = new Hero();
		hero.live();
		
		Badges.reset();

		CustomObject.loadScripts();

		GamesInProgress.selectedClass.initHero( hero );
	}

    public static boolean levelHasBeenGenerated(String levelName, int branch){
        if (branch != 0) return false;//TODO for now we always generate new floors for quests
        return Arrays.asList(Dungeon.visited).contains(convertNameAsInVisited(levelName, branch));
    }
    public static boolean levelIsCompleted(String levelName, int branch){
        return Arrays.asList(Dungeon.completed).contains(convertNameAsInVisited(levelName, branch));
    }

    public static LevelScheme curLvlScheme() {
        return customDungeon.getFloor(Dungeon.levelName);
    }

    public static int getSimulatedDepth() {//Replaces Dungeon.depth
        return getSimulatedDepth(Dungeon.curLvlScheme());
    }

    public static int getSimulatedDepth(LevelScheme levelScheme) {
        if (levelScheme.getType() == SewerBossLevel.class) return 5;
        if (levelScheme.getType() == PrisonBossLevel.class) return 10;
        if (levelScheme.getType() == CavesBossLevel.class) return 15;
        if (levelScheme.getType() == CityBossLevel.class) return 20;
        if (levelScheme.getType() == HallsBossLevel.class) return 25;
        return (levelScheme.getRegion() - 1) * 5 + levelScheme.getNumInRegion();
    }

    public static int region() {
        return LevelScheme.getRegion(Dungeon.level);
    }

    public static boolean isChallenged(int mask) {
        if (CustomDungeon.isEditing()) return false;
        return (challenges & mask) != 0;
    }

    public static boolean isLevelTesting(){
        return GamesInProgress.curSlot == GamesInProgress.TEST_SLOT;
    }

    public static Level newLevel(String oldLvlName, int oldBranch) {

		if (Dungeon.level != null) Dungeon.level.levelScheme.unloadLevel();
        Dungeon.level = null;
        Actor.clear();

        Dungeon.depth = customDungeon.getFloor(levelName).getDepth();
        Level level;
        if (branch == QuestLevels.MINING.ID) {
//            if (MiningLevel.generateWithThisQuest == null) MiningLevel.generateWithThisQuest = new Blacksmith(new BlacksmithQuest());
            level = new MiningLevel();
            level.levelScheme = customDungeon.getFloor(levelName);
            level.name = levelName;
            ((MiningLevel) level).destCell = Dungeon.hero.pos;
            level.create();
        } else level = customDungeon.getFloor(levelName).initLevel();


        if (depth > Statistics.deepestFloor && branch == 0) {
            Statistics.deepestFloor = depth;
        }

        if (branch == 0 && oldBranch == 0 && !levelIsCompleted(oldLvlName, oldBranch) && !levelName.equals(oldLvlName)) {
            Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
        }
        addLevelToCompleted(oldLvlName, oldBranch);
        addLevelToVisited(level);

        visitedDepths.add(Dungeon.depth);

        if (branch == 0) Statistics.qualifiedForNoKilling = !bossLevel();
        Arrays.fill(Statistics.qualifiedForBossChallengesBadge, true);

        return level;
    }

    private static void addLevelToVisited(Level level) {
        List<String> tempVisited = new ArrayList<>(Arrays.asList(Dungeon.visited));
        tempVisited.add(convertNameAsInVisited(level.name, Dungeon.branch));
        Dungeon.visited = tempVisited.toArray(new String[]{});
    }
    private static void addLevelToCompleted(String name, int branch) {
        List<String> tempCompleted = new ArrayList<>(Arrays.asList(Dungeon.completed));
        tempCompleted.add(convertNameAsInVisited(name, branch));
        Dungeon.completed = tempCompleted.toArray(new String[]{});
    }

    public static String convertNameAsInVisited(String name, int branch){
        return name + (branch != 0 ? (char) (1357924 + branch) : "");
    }

    public static void resetLevel() {

        Actor.clear();

        level.reset();
        switchLevel(level, level.entrance());
    }

    public static long seedCurLevel() {
        return seedForLevel(levelName, branch);
    }

    public static long seedForLevel(String levelName, int branch) {
        return customDungeon.getFloor(levelName).getSeed() + 13 * branch;
    }

    public static boolean bossLevel() {
        return bossLevel(levelName);
    }

    public static boolean bossLevel(String levelName) {
        return customDungeon.getFloor(levelName) != null && customDungeon.getFloor(levelName).hasBoss();
    }

	//value used for scaling of damage values and other effects.
	//is usually the dungeon depth, but can be set to 26 when ascending
	public static int scalingDepth(){
		if (Dungeon.hero != null && Dungeon.hero.buff(AscensionChallenge.class) != null){
			return 26;
		} else {
			return depth;
		}
	}

	public static boolean interfloorTeleportAllowed(){
		if (Dungeon.level.locked()
				|| Dungeon.level instanceof MiningLevel
				|| (Dungeon.hero != null && Dungeon.hero.belongings.getItem(Amulet.class) != null)){
			return false;
		}
		return true;
	}

	public static void switchLevel( final Level level, int pos ) {

		//Position of -2 specifically means trying to place the hero the exit
		if (pos == -2){
			LevelTransition t = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
			if (t != null) pos = t.cell();
		}

		//Place hero at the entrance if they are out of the map (often used for pox = -1)
		// or if they are in solid terrain (except in the mining level, where that happens normally)
		if (pos < 0 || pos >= level.length()
				|| (!(level instanceof MiningLevel) && !level.isPassable(pos) && !level.avoid[pos]) || Barrier.stopHero(pos, level)){
			LevelTransition t = level.getTransition(null);
			if (t == null) {
				Random.pushGenerator(Dungeon.seedCurLevel() + 5);
				pos = EditorUtilies.getRandomCellGuaranteed(level, Dungeon.hero == null ? new Hero() : Dungeon.hero);
				GameScene.errorMsg.add(Messages.get(Dungeon.class, "no_transitions_warning", level.name, Dungeon.customDungeon.getName()));
				Random.popGenerator();
			} else
				pos = t.cell();
		}

		PathFinder.setMapSize(level.width(), level.height());

		Dungeon.level = level;
		Dungeon.levelName = level.name;
		hero.pos = pos;

		if (hero.buff(AscensionChallenge.class) != null){
			hero.buff(AscensionChallenge.class).onLevelSwitch();
		}

		Mob.restoreAllies( level, pos );

		Actor.init();

		level.addRespawner();
        level.addZoneRespawner();

		for(Mob m : level.mobs){
			if (m.pos == hero.pos && !Char.hasProp(m, Char.Property.IMMOVABLE)){
				//displace mob
				for(int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(m.pos+i) == null && level.isPassable(m.pos + i, m)){
						m.pos += i;
						break;
					}
				}
			}
		}
		
		Light light = hero.buff( Light.class );
		hero.viewDistance = light == null ? level.viewDistance : Math.max( Light.DISTANCE, level.viewDistance );
		
		hero.curAction = hero.lastAction = null;

		observe();
		try {
			saveAll();
		} catch (IOException e) {
			SandboxPixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
		}

        LuaManager.updateGlobalVars();
	}

	public static void dropToChasm( Item item ) {
		String nextLevel = customDungeon.getFloor(Dungeon.levelName).getChasm();
		ArrayList<Item> dropped = Dungeon.droppedItems.get( nextLevel );
		if (dropped == null) {
			Dungeon.droppedItems.put( nextLevel, dropped = new ArrayList<>() );
		}
		dropped.add( item );
	}

    public static boolean posNeeded() {
        if (CustomDungeon.isEditing()) return Random.Int(2) == 0;
        if (visitedDepths.contains(Dungeon.depth)) return false;
        return false;//uses ItemDistribution
//        //2 POS each floor set
//        int posLeftThisSet = 2 - (LimitedDrops.STRENGTH_POTIONS.count - (depth / 5) * 2);
//        if (posLeftThisSet <= 0) return false;
//
//        int floorThisSet = (depth % 5);
//
//        //pos drops every two floors, (numbers 1-2, and 3-4) with a 50% chance for the earlier one each time.
//        int targetPOSLeft = 2 - floorThisSet / 2;
//        if (floorThisSet % 2 == 1 && Random.Int(2) == 0) targetPOSLeft--;
//
//        if (targetPOSLeft < posLeftThisSet) return true;
//        else return false;

    }

    public static boolean souNeeded() {
        if (CustomDungeon.isEditing()) return Random.Int(4) <= 2;
        if (visitedDepths.contains(Dungeon.depth)) return false;
        return false;//uses ItemDistribution
//        int souLeftThisSet;
//        //3 SOU each floor set, 1.5 (rounded) on forbidden runes challenge
//        if (isChallenged(Challenges.NO_SCROLLS)) {
//            souLeftThisSet = Math.round(1.5f - (LimitedDrops.UPGRADE_SCROLLS.count - (depth / 5) * 1.5f));
//        } else {
//            souLeftThisSet = 3 - (LimitedDrops.UPGRADE_SCROLLS.count - (depth / 5) * 3);
//        }
//        if (souLeftThisSet <= 0) return false;
//
//        int floorThisSet = (depth % 5);
//        //chance is floors left / scrolls left
//        return Random.Int(5 - floorThisSet) < souLeftThisSet;
    }

    public static boolean asNeeded() {
        if (CustomDungeon.isEditing()) return Random.Int(4) == 0;
        if (visitedDepths.contains(Dungeon.depth)) return false;
        return false;//uses ItemDistribution
//        //1 AS each floor set
//        int asLeftThisSet = 1 - (LimitedDrops.ARCANE_STYLI.count - (depth / 5));
//        if (asLeftThisSet <= 0) return false;
//
//        int floorThisSet = (depth % 5);
//        //chance is floors left / scrolls left
//        return Random.Int(5 - floorThisSet) < asLeftThisSet;
    }

    public static boolean enchStoneNeeded(){
		if (CustomDungeon.isEditing()) {
			if (region() == LevelScheme.REGION_PRISON || region() == LevelScheme.REGION_CAVES)
				return Random.Int(4) == 0;
		}
		if (visitedDepths.contains(Dungeon.depth)) return false;
		return false;//uses ItemDistribution
//		//1 enchantment stone, spawns on chapter 2 or 3
//		if (!LimitedDrops.ENCH_STONE.dropped()){
//			int region = 1+depth/5;
//			if (region > 1){
//				int floorsVisited = depth - 5;
//				if (floorsVisited > 4) floorsVisited--; //skip floor 10
//				return Random.Int(9-floorsVisited) == 0; //1/8 chance each floor
//			}
//		}
//		return false;
	}

	public static boolean intStoneNeeded(){
		if (CustomDungeon.isEditing()) {
			if (getSimulatedDepth() <= 3)
				return Random.Int(3) == 0;
		}
		if (visitedDepths.contains(Dungeon.depth)) return false;
		return false;//uses ItemDistribution
//		//one stone on floors 1-3
//		return !LimitedDrops.INT_STONE.dropped() && Random.Int(4-depth) == 0;
	}

	public static boolean trinketCataNeeded(){
		if (CustomDungeon.isEditing()) {
			if (getSimulatedDepth() <= 3)
				return Random.Int(3) == 0;
		}
		if (visitedDepths.contains(Dungeon.depth)) return false;
		return false;//uses ItemDistribution
//		//one trinket catalyst on floors 1-3
//		return !LimitedDrops.TRINKET_CATA.dropped() && Random.Int(4-depth) == 0;
	}

	public static boolean labRoomNeeded(){
		//one laboratory each floor set, in floor 3 or 4, 1/2 chance each floor
		int region = 1+depth/5;
		if (region > LimitedDrops.LAB_ROOM.count){
			int floorThisRegion = depth%5;
			if (floorThisRegion >= 4 || (floorThisRegion == 3 && Random.Int(2) == 0)){
				return true;
			}
		}
		return false;
	}

	// 1/4
	// 3/4 * 1/3 = 3/12 = 1/4
	// 3/4 * 2/3 * 1/2 = 6/24 = 1/4
	// 1/4private static final String INIT_VER = "init_ver";
    private static final String VERSION = "version";
    private static final String SEED = "seed";
    private static final String CUSTOM_SEED = "custom_seed";
    private static final String DAILY = "daily";
    private static final String DAILY_REPLAY = "daily_replay";
    private static final String CHALLENGES = "challenges";
    private static final String MOBS_TO_CHAMPION = "mobs_to_champion";
    private static final String HERO = "hero";
    private static final String DEPTH = "depth";
    private static final String BRANCH		= "branch";
    private static final String LEVEL_NAME = "level_name";
    private static final String GOLD = "gold";
    private static final String ENERGY = "energy";
    private static final String DROPPED = "dropped%s";
    private static final String PORTED = "ported%s";
    private static final String LEVEL = "level";
    private static final String LIMDROPS = "limited_drops";
    private static final String CHAPTERS = "chapters";
    private static final String QUESTS = "quests";
    private static final String BADGES = "badges";
    private static final String COMPLETED = "completed";
    private static final String VISITED = "visited";
    private static final String VISITED_DEPTHS = "visited_depths";
    private static final String CUSTOM_DUNGEON = "custom_dungeon";
    private static final String TEST_GAME = "test_game";

    public static void saveGame(int save) {
        try {
            Bundle bundle = new Bundle();

            bundle.put(TEST_GAME, isLevelTesting());
            bundle.put(INIT_VER, initialVersion);
            bundle.put(VERSION, version = Game.versionCode);
            bundle.put(SEED, seed);
            bundle.put(CUSTOM_SEED, customSeedText);
            bundle.put(DAILY, daily);
            bundle.put(DAILY_REPLAY, dailyReplay);
            bundle.put(CHALLENGES, challenges);
            bundle.put(MOBS_TO_CHAMPION, mobsToChampion);
            bundle.put(HERO, hero);
            bundle.put(DEPTH, depth);
            bundle.put( BRANCH, branch );
            bundle.put(LEVEL_NAME, levelName);

            bundle.put(VISITED, visited);
            bundle.put(COMPLETED, completed);

            int[] visitedDepthsArray = new int[visitedDepths.size()];
            int i = 0;
            for (int d : visitedDepths) {
                visitedDepthsArray[i] = d;
                i++;
            }
            bundle.put(VISITED_DEPTHS, visitedDepthsArray);

            bundle.put(GOLD, gold);
            bundle.put(ENERGY, energy);

            bundle.put(CUSTOM_DUNGEON, customDungeon);

            for (String level : droppedItems.keySet()) {
                bundle.put(Messages.format(DROPPED, level), droppedItems.get(level));
            }


			quickslot.storePlaceholders( bundle );

            Bundle limDrops = new Bundle();
            LimitedDrops.store(limDrops);
            bundle.put(LIMDROPS, limDrops);

            int count = 0;
            int[] ids = new int[chapters.size()];
            for (Integer id : chapters) {
                ids[count++] = id;
            }
            bundle.put(CHAPTERS, ids);

            Bundle quests = new Bundle();
            GhostQuest.storeStatics(quests);
            WandmakerQuest.storeStatics(quests);
            BlacksmithQuest.storeStatics(quests);
            ImpQuest.storeStatics(quests);
            bundle.put(QUESTS, quests);

            BossHealthBar.storeInBundle(bundle);

            SpecialRoom.storeRoomsInBundle(bundle);
            SecretRoom.storeRoomsInBundle(bundle);

            Statistics.storeInBundle(bundle);
            Notes.storeInBundle(bundle);
            Generator.storeInBundle(bundle);

            Scroll.save(bundle);
            Potion.save(bundle);
            Ring.save(bundle);

            Actor.storeNextID(bundle);

            Bundle badges = new Bundle();
            Badges.saveLocal(badges);
            bundle.put(BADGES, badges);

            FileUtils.bundleToFile(GamesInProgress.gameFile(save), bundle);

        } catch (IOException e) {
            GamesInProgress.setUnknown(save);
            SandboxPixelDungeon.reportException(e);
        }
    }

    public static void saveLevel(int save) throws IOException {
        Bundle bundle = new Bundle();
        bundle.put(LEVEL, level);

        FileUtils.bundleToFile(GamesInProgress.levelFile(save, levelName, branch), bundle);
    }

    public static void saveAll() throws IOException {
        if (hero != null && (hero.isAlive() || WndResurrect.instance != null)) {

            Actor.fixTime();
            updateLevelExplored();
            saveGame(GamesInProgress.curSlot);
            saveLevel(GamesInProgress.curSlot);

			GamesInProgress.set( GamesInProgress.curSlot );

		}
	}
	
	public static void loadGame( int save ) throws IOException {
		loadGame( save, true );
	}
	
	public static void loadGame( int save, boolean fullLoad ) throws IOException {
		
		Bundle bundle = FileUtils.bundleFromFile( GamesInProgress.gameFile( save ) );

        initialVersion = bundle.getInt(VERSION);

		version = bundle.getInt( VERSION );

		seed = bundle.contains( SEED ) ? bundle.getLong( SEED ) : DungeonSeed.randomSeed();
		customSeedText = bundle.getString( CUSTOM_SEED );
		daily = bundle.getBoolean( DAILY );
		dailyReplay = bundle.getBoolean( DAILY_REPLAY );

		Actor.clear();
		Actor.restoreNextID( bundle );

		quickslot.reset();
		QuickSlotButton.reset();
		Toolbar.swappedQuickslots = false;

		Dungeon.challenges = bundle.getInt( CHALLENGES );
		Dungeon.mobsToChampion = bundle.getInt( MOBS_TO_CHAMPION );
		
		Dungeon.level = null;
		Dungeon.depth = -1;
		
		Scroll.restore( bundle );
		Potion.restore( bundle );
		Ring.restore( bundle );

        visited = bundle.getStringArray(VISITED);
        completed = bundle.getStringArray(COMPLETED);
        if (completed == null) completed = visited;

        int[] visitedDepthsArray = bundle.getIntArray(VISITED_DEPTHS);
        visitedDepths = new HashSet<>();
        if (visitedDepthsArray != null) for (int d : visitedDepthsArray) visitedDepths.add(d);

        quickslot.restorePlaceholders(bundle);

        if (fullLoad) {

            LimitedDrops.restore(bundle.getBundle(LIMDROPS));

            chapters = new HashSet<>();
            int[] ids = bundle.getIntArray(CHAPTERS);
            if (ids != null) {
                for (int id : ids) {
                    chapters.add(id);
                }
            }

            Bundle quests = bundle.getBundle(QUESTS);
            if (!quests.isNull()) {
                GhostQuest.restoreStatics(quests);
                WandmakerQuest.restoreStatics(quests);
                BlacksmithQuest.restoreStatics(quests);
                ImpQuest.restoreStatics(quests);
            } else {
                GhostQuest.reset();
                WandmakerQuest.reset();
                BlacksmithQuest.reset();
                ImpQuest.reset();
            }

            BossHealthBar.restoreFromBundle(bundle);

            SpecialRoom.restoreRoomsFromBundle(bundle);
            SecretRoom.restoreRoomsFromBundle(bundle);
        }

        Bundle badges = bundle.getBundle(BADGES);
        if (!badges.isNull()) {
            Badges.loadLocal(badges);
        } else {
            Badges.reset();
        }

        Notes.restoreFromBundle(bundle);

        hero = null;
        hero = (Hero) bundle.get(HERO);

        depth = bundle.getInt(DEPTH);
        branch = bundle.getInt( BRANCH );
        levelName = bundle.getString(LEVEL_NAME);

		gold = bundle.getInt( GOLD );
		energy = bundle.getInt( ENERGY );

		customDungeon = (CustomDungeon) bundle.get( CUSTOM_DUNGEON );

		Statistics.restoreFromBundle( bundle );
		Generator.restoreFromBundle( bundle );

        droppedItems = new HashMap<>();
        for (String level : customDungeon.floorNames()) {

            //dropped items
            ArrayList<Item> items = new ArrayList<>();
            if (bundle.contains(Messages.format(DROPPED, level)))
                for (Bundlable b : bundle.getCollection(Messages.format(DROPPED, level))) {
                    items.add((Item) b);
                }
            if (!items.isEmpty()) {
                droppedItems.put(level, items);
            }

        }

        FileUtils.resetDefaultFileType();
        if (!DeviceCompat.isDesktop() && initialVersion < 743) {//v0.6 and older
            CustomDungeonSaves.setCurDirectory(GamesInProgress.gameFolder(save) + "/dungeon_levels/");
        } else CustomDungeonSaves.setCurDirectory(GamesInProgress.gameFolder(save) + "/");

        CustomTileLoader.loadTiles(true);

		CustomObject.loadScripts();
    }

    public static Level loadLevel(int save) throws IOException {

        Dungeon.level = null;
        Actor.clear();

        Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.levelFile(save, levelName, branch));

        Level level = (Level) bundle.get(LEVEL);

		if (level == null){
			throw new IOException();
		} else {
			return level;
		}
	}
	
	public static void deleteGame( int save, boolean deleteLevels ) {

        if (deleteLevels) {
            String folder = GamesInProgress.gameFolder(save);
            for (String file : FileUtils.filesInDir(folder)) {
                if (file.contains("level")) {
                    FileUtils.deleteFile(folder + "/" + file);
                }
            }
        }

        FileUtils.overwriteFile(GamesInProgress.gameFile(save), 1);

        GamesInProgress.delete(save);
    }

    public static boolean preview(GamesInProgress.Info info, Bundle bundle) {
        if (bundle.getBoolean(TEST_GAME)) return false;
        info.depth = bundle.getInt(DEPTH);
        info.levelName = bundle.getString(LEVEL_NAME);
        info.version = bundle.getInt(VERSION);
        info.challenges = bundle.getInt(CHALLENGES);
        info.seed = bundle.getLong(SEED);
        info.customSeed = bundle.getString(CUSTOM_SEED);
        info.daily = bundle.getBoolean(DAILY);
        info.dailyReplay = bundle.getBoolean(DAILY_REPLAY);
        info.dungeonName = ((CustomDungeon) bundle.get(CUSTOM_DUNGEON)).getName();

        Hero.preview(info, bundle.getBundle(HERO));
        Statistics.preview(info, bundle);

        return true;
    }

    public static void fail(Object cause) {
        if (WndResurrect.instance == null) {
            updateLevelExplored();
            Statistics.gameWon = false;
            Rankings.INSTANCE.submit(false, cause);
        }
    }

    public static void win(Object cause) {

		updateLevelExplored();
		Statistics.gameWon = true;

		hero.belongings.identify();

		Rankings.INSTANCE.submit( true, cause );
	}

	public static void updateLevelExplored(){
		if (branch == 0 && level instanceof RegularLevel || level instanceof CustomLevel && !Dungeon.bossLevel()){
			Statistics.floorsExplored.put( levelName, level.isLevelExplored(levelName));
		}
	}

	//default to recomputing based on max hero vision, in case vision just shrank/grew
	public static void observe(){
		int dist = Math.max(Dungeon.hero.viewDistance, 8);
		dist *= 1f + 0.25f*Dungeon.hero.pointsInTalent(Talent.FARSIGHT);

		if (Dungeon.hero.buff(MagicalSight.class) != null){
			dist = Math.max( dist, MagicalSight.DISTANCE );
		}

		observe( dist+1 );
	}
	
	public static void observe( int dist ) {

		if (level == null) {
			return;
		}
		
		level.updateFieldOfView(hero, level.heroFOV);

		int x = hero.pos % level.width();
		int y = hero.pos / level.width();
	
		//left, right, top, bottom
		int l = Math.max( 0, x - dist );
		int r = Math.min( x + dist, level.width() - 1 );
		int t = Math.max( 0, y - dist );
		int b = Math.min( y + dist, level.height() - 1 );
	
		int width = r - l + 1;
		int height = b - t + 1;

        if (level.levelScheme.rememberLayout || !Dungeon.hero.isAlive()) {
            int pos = l + t * level.width();
            for (int i = t; i <= b; i++) {
                BArray.or(level.visited, level.heroFOV, pos, width, level.visited);
                pos += level.width();
            }
        }
        else {
            level.visited = level.heroFOV.clone();
            for (int i=0; i < level.visited.length; i++) {
                if (level.visited[i]) level.mapped[i] = false;
            }
        }
	
		GameScene.updateFog(l, t, width, height);

        if (level.levelScheme.rememberLayout) {

            if (hero.buff(MindVision.class) != null) {
                for (Mob m : level.mobs.toArray(new Mob[0])) {
                    if (Mimic.isLikeMob(m)) {
                        BArray.or(level.visited, level.heroFOV, m.pos - 1 - level.width(), 3, level.visited);
                        BArray.or(level.visited, level.heroFOV, m.pos - 1, 3, level.visited);
                        BArray.or(level.visited, level.heroFOV, m.pos - 1 + level.width(), 3, level.visited);
                        //updates adjacent cells too
                        GameScene.updateFog(m.pos, 2);
                    }
                }
            }

            if (hero.buff(Awareness.class) != null) {
                for (Heap h : level.heaps.valueList()) {
                    BArray.or(level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited);
                    BArray.or(level.visited, level.heroFOV, h.pos - 1, 3, level.visited);
                    BArray.or(level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited);
                    GameScene.updateFog(h.pos, 2);
                }
            }

            for (TalismanOfForesight.CharAwareness c : hero.buffs(TalismanOfForesight.CharAwareness.class)) {
                Char ch = (Char) Actor.findById(c.charID);
                if (ch == null || !ch.isAlive()) continue;
                BArray.or(level.visited, level.heroFOV, ch.pos - 1 - level.width(), 3, level.visited);
                BArray.or(level.visited, level.heroFOV, ch.pos - 1, 3, level.visited);
                BArray.or(level.visited, level.heroFOV, ch.pos - 1 + level.width(), 3, level.visited);
                GameScene.updateFog(ch.pos, 2);
            }

            for (TalismanOfForesight.HeapAwareness h : hero.buffs(TalismanOfForesight.HeapAwareness.class)) {
                if (!Dungeon.levelName.equals(h.level) || Dungeon.branch != h.branch) continue;
                BArray.or(level.visited, level.heroFOV, h.pos - 1 - level.width(), 3, level.visited);
                BArray.or(level.visited, level.heroFOV, h.pos - 1, 3, level.visited);
                BArray.or(level.visited, level.heroFOV, h.pos - 1 + level.width(), 3, level.visited);
                GameScene.updateFog(h.pos, 2);
            }

            for (RevealedArea a : hero.buffs(RevealedArea.class)) {
                if (!Dungeon.levelName.equals(a.level) || Dungeon.branch != a.branch) continue;
                BArray.or(level.visited, level.heroFOV, a.pos - 1 - level.width(), 3, level.visited);
                BArray.or(level.visited, level.heroFOV, a.pos - 1, 3, level.visited);
                BArray.or(level.visited, level.heroFOV, a.pos - 1 + level.width(), 3, level.visited);
                GameScene.updateFog(a.pos, 2);
            }

        }

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfWarding.Ward
					|| ch instanceof WandOfRegrowth.Lotus
					|| ch instanceof SpiritHawk.HawkAlly){

                if (level.levelScheme.rememberLayout) {

                    x = ch.pos % level.width();
                    y = ch.pos / level.width();

                    //left, right, top, bottom
                    dist = ch.viewDistance + 1;
                    l = Math.max(0, x - dist);
                    r = Math.min(x + dist, level.width() - 1);
                    t = Math.max(0, y - dist);
                    b = Math.min(y + dist, level.height() - 1);

                    width = r - l + 1;
                    height = b - t + 1;

                    int pos = l + t * level.width();

                    for (int i = t; i <= b; i++) {
                        BArray.or(level.visited, level.heroFOV, pos, width, level.visited);
                        pos += level.width();
                    }
                }
				GameScene.updateFog(ch.pos, dist);
			}
		}

		GameScene.afterObserve();
	}

	//we store this to avoid having to re-allocate the array with each pathfind
	private static boolean[] passable;

	private static void setupPassable(){
		if (passable == null || passable.length != Dungeon.level.length())
			passable = new boolean[Dungeon.level.length()];
		else
			BArray.setFalse(passable);
	}

	public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars){
		return findPassable(ch, pass, vis, chars, chars);
	}

	public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars, boolean considerLarge){
		setupPassable();
        if (Char.hasProp(ch, Char.Property.IMMOVABLE) && (!(ch instanceof NPC) || ch instanceof Ghost || ch instanceof SentryRoom.Sentry)) {//also in Mob.java line 731 (cellIsPathable())
            BArray.setFalse(passable);
        } else {

            if (ch.isFlying() || ch.buff(Amok.class) != null) {
                BArray.or(pass, Dungeon.level.avoid, passable);
                for (Barrier b : Dungeon.level.barriers.values()) {
                    if (b.blocksChar(ch)) passable[b.pos] = false;
                }
            } else {
                System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
            }

            if (considerLarge && Char.hasProp(ch, Char.Property.LARGE)) {
                BArray.and(passable, Dungeon.level.openSpace, passable);
            }

            ch.modPassable(passable);

        }

		if (chars) {
			for (Char c : Actor.chars()) {
				if (vis[c.pos]) {
					passable[c.pos] = false;
				}
			}
		}

		return passable;
	}

	public static PathFinder.Path findPath(Char ch, int to, boolean[] pass, boolean[] vis, boolean chars) {

		return PathFinder.find( ch.pos, to, findPassable(ch, pass, vis, chars) );

	}
	
	public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars ) {

		if (Dungeon.level.adjacent( ch.pos, to ) && ArrowCell.allowsStep( ch.pos, to )) {
			return Actor.findChar( to ) == null && pass[to] ? to : -1;
		}

        setupPassable();
        if (ch.isFlying() || ch.buff(Amok.class) != null) {
            BArray.or(pass, Dungeon.level.avoid, passable);
            for (Barrier b : Dungeon.level.barriers.values()) {
                if (b.blocksChar(ch)) passable[b.pos] = false;
            }
        } else {
            System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
        }

        if (Char.hasProp(ch, Char.Property.LARGE)) {
            BArray.and(passable, Dungeon.level.openSpace, passable);
        }

        if (chars) {
            for (Char c : Actor.chars()) {
                if (visible[c.pos]) {
                    passable[c.pos] = false;
                }
            }
        }

        return PathFinder.getStep( ch.pos, to, findPassable(ch, pass, visible, chars) );

	}
	
	public static int flee( Char ch, int from, boolean[] pass, boolean[] visible, boolean chars ) {
		boolean[] passable = findPassable(ch, pass, visible, false, true);
		passable[ch.pos] = true;

		//only consider other chars impassable if our retreat step may collide with them
		if (chars) {
			for (Char c : Actor.chars()) {
				if (c.pos == from || Dungeon.level.adjacent(c.pos, ch.pos)) {
					passable[c.pos] = false;
				}
			}
		}

		//chars affected by terror have a shorter lookahead and can't approach the fear source
		boolean canApproachFromPos = ch.buff(Terror.class) == null && ch.buff(Dread.class) == null;
		return PathFinder.getStepBack( ch.pos, from, canApproachFromPos ? 8 : 4, passable, canApproachFromPos );
		
	}

}