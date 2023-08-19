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

package com.alphadraxonis.sandboxpixeldungeon;

import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Amok;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Awareness;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Light;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MagicalSight;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MindVision;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.RevealedArea;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Talent;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.GhostQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.ImpQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.items.Amulet;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.TalismanOfForesight;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfRegrowth;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfWarding;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.CavesBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.CityBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.HallsBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.MiningLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.PrisonBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.RegularLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.SewerBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.SpecialRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.QuickSlotButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Toolbar;
import com.alphadraxonis.sandboxpixeldungeon.utils.BArray;
import com.alphadraxonis.sandboxpixeldungeon.utils.DungeonSeed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndResurrect;
import com.badlogic.gdx.Files;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class Dungeon {

    //enum of items which have limited spawns, records how many have spawned
    //could all be their own separate numbers, but this allows iterating, much nicer for bundling/initializing.
    public static enum LimitedDrops {
        //limited world drops
        STRENGTH_POTIONS,
        UPGRADE_SCROLLS,
        ARCANE_STYLI,

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
        public boolean dropped() {
            return count != 0;
        }

        public void drop() {
            count = 1;
        }

        public static void reset() {
            for (LimitedDrops lim : values()) {
                lim.count = 0;
            }
        }

        public static void store(Bundle bundle) {
            for (LimitedDrops lim : values()) {
                bundle.put(lim.name(), lim.count);
            }
        }

        public static void restore(Bundle bundle) {
            for (LimitedDrops lim : values()) {
                if (bundle.contains(lim.name())) {
                    lim.count = bundle.getInt(lim.name());
                } else {
                    lim.count = 0;
                }

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

    //keeps track of what levels the game should try to load instead of creating fresh
    public static ArrayList<Integer> generatedLevels = new ArrayList<>();

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
    private static Set<Integer> visitedDepths;

    public static CustomDungeon customDungeon;
    public static String levelName;

    public static void init() {

        String levelDir = GamesInProgress.getCustomDungeonLevelFolder(GamesInProgress.curSlot);
        try {
            CustomDungeonSaves.copyLevelsForNewGame(customDungeon.getName(), levelDir);
        } catch (IOException e) {
            e.printStackTrace();
            SandboxPixelDungeon.reportException(e);
        }
        CustomDungeonSaves.setCurDirectory(levelDir);
        CustomDungeonSaves.setFileType(Files.FileType.Local);
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
//
//        ghostLevel = RandomGenUtils.calculateQuestLevel(layout().getGhostSpawnLevels());
//        wandmakerLevel = RandomGenUtils.calculateQuestLevel(layout().getWandmakerSpawnLevels());
//        blacksmithLevel = RandomGenUtils.calculateQuestLevel(layout().getBlacksmithSpawnLevels());
//        impLevel = RandomGenUtils.calculateQuestLevel(layout().getImpSpawnLevels());
        visitedDepths = new HashSet<>();


        initialVersion = version = Game.versionCode;
        challenges = SPDSettings.challenges();
        mobsToChampion = -1;

        if (daily) {
            //Ensures that daily seeds are not in the range of user-enterable seeds
            seed = SPDSettings.lastDaily() + DungeonSeed.TOTAL_SEEDS;
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            customSeedText = format.format(new Date(SPDSettings.lastDaily()));
        } else if (!SPDSettings.customSeed().isEmpty()) {
            customSeedText = SPDSettings.customSeed();
            seed = DungeonSeed.convertFromText(customSeedText);
        } else {
            customSeedText = "";
            seed = DungeonSeed.randomSeed();
        }

        Actor.clear();
        Actor.resetNextID();


        //offset seed slightly to avoid output patterns
        Random.pushGenerator(seed + 1);

        customDungeon.initSeeds();
        //TODO assign groups here to the different levels

        customDungeon.initDistribution();

        SpecialRoom.initForRun();
        SecretRoom.initForRun();

        Generator.fullReset();

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
        generatedLevels.clear();

        gold = CustomDungeon.getDungeon().getStartGold();
        energy = CustomDungeon.getDungeon().getStartAlchemicalEnergy();

        droppedItems = new HashMap<>();

        LimitedDrops.reset();

        chapters = new HashSet<>();

        GhostQuest.reset();
//        Wandmaker.Quest.reset();
//        Blacksmith.Quest.reset();
        ImpQuest.reset();

        hero = new Hero();
        hero.live();

        Badges.reset();

        GamesInProgress.selectedClass.initHero(hero);
    }

    public static boolean levelHasBeenGenerated(int depth, int branch){
        return generatedLevels.contains(depth + 1000*branch);
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

    public static boolean isChallenged(int mask) {
        if (CustomDungeon.isEditing()) return false;
        return (challenges & mask) != 0;
    }

    public static Level newLevel() {

        Dungeon.level = null;
        Actor.clear();

        Dungeon.depth = customDungeon.getFloor(levelName).getDepth();//TODO maybe move above Statistics.deepestFloor check?
        Level level;
        if(branch == 1 && customDungeon.getFloor(levelName).getRegion() == LevelScheme.REGION_CAVES){
            level = new MiningLevel();
            level.create();
        } else level = customDungeon.getFloor(levelName).initLevel();

        //this assumes that we will never have a depth value outside the range 0 to 999
        // or -500 to 499, etc.
        if (!generatedLevels.contains(depth + 1000*branch)) {
            generatedLevels.add(depth + 1000 * branch);
        }
        if (depth > Statistics.deepestFloor && branch == 0) {
            Statistics.deepestFloor = depth;
        }
        if (!Arrays.asList(visited).contains(levelName)) {
            Statistics.completedWithNoKilling = Statistics.qualifiedForNoKilling;
        }

        visitedDepths.add(Dungeon.depth);

        if (branch == 0) Statistics.qualifiedForNoKilling = !bossLevel();
        Statistics.qualifiedForBossChallengeBadge = false;

        return level;
    }

    public static void resetLevel() {

        Actor.clear();

        level.reset();
        switchLevel(level, level.entrance());
    }

    public static long seedCurLevel() {
        return seedForLevel(levelName);
    }

    public static long seedForLevel(String levelName) {
        return customDungeon.getFloor(levelName).getSeed();
    }

    public static boolean bossLevel() {
        return bossLevel(levelName);
    }

    public static boolean bossLevel(String levelName) {
        return customDungeon.getFloor(levelName) != null && customDungeon.getFloor(levelName).hasBoss();
    }

    //value used for scaling of damage values and other effects.
    //is usually the dungeon depth, but can be set to 26 when ascending
    public static int scalingDepth() {
        if (Dungeon.hero != null && Dungeon.hero.buff(AscensionChallenge.class) != null) {
            return 26;
        } else {
            return depth;
        }
    }

    public static boolean interfloorTeleportAllowed() {
        if (Dungeon.level.locked || (Dungeon.hero != null && Dungeon.hero.belongings.getItem(Amulet.class) != null)) {
            return false;
        }
        return true;
    }

    public static void switchLevel(final Level level, int pos) {

        if (pos == -2) {
            LevelTransition t = level.getTransition(LevelTransition.Type.REGULAR_EXIT);
            if (t != null) pos = t.cell();
        }

        if (pos < 0 || pos >= level.length() || (!level.passable[pos] && !level.avoid[pos])) {
            LevelTransition t = level.getTransition(null);
            if (t == null) {
                int tries = level.length();
                Random.pushGenerator(Dungeon.seedCurLevel() + 5);
                do {
                    pos = Random.Int(level.length());//Choose a random cell
                    tries--;
                } while ((!level.passable[pos] || level.avoid[pos]) && tries >= 0);
                if (!level.passable[pos] || level.avoid[pos]) {
                    int l = level.length();
                    for (pos = 0; pos < l; pos++) {
                        if (level.passable[pos] && !level.avoid[pos])
                            break;//choose first valid cell
                    }
                    if (pos == l) pos = l / 2;//if all positions are invalid, just take the center
                }
                GameScene.errorMsg.add(Messages.get(Dungeon.class, "no_transitions_warning", level.name, Dungeon.customDungeon.getName()));
                Random.popGenerator();
            } else
                pos = t.cell();
        }

        PathFinder.setMapSize(level.width(), level.height());

        Dungeon.level = level;
        Dungeon.levelName = level.name;
        hero.pos = pos;

        if (hero.buff(AscensionChallenge.class) != null) {
            hero.buff(AscensionChallenge.class).onLevelSwitch();
        }

        Mob.restoreAllies(level, pos);

        Actor.init();

        level.addRespawner();

        for (Mob m : level.mobs) {
            if (m.pos == hero.pos && !Char.hasProp(m, Char.Property.IMMOVABLE)) {
                //displace mob
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Actor.findChar(m.pos + i) == null && level.passable[m.pos + i]) {
                        m.pos += i;
                        break;
                    }
                }
            }
        }

        Light light = hero.buff(Light.class);
        hero.viewDistance = light == null ? level.viewDistance : Math.max(Light.DISTANCE, level.viewDistance);

        hero.curAction = hero.lastAction = null;

        observe();
        try {
            saveAll();
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
			/*This only catches IO errors. Yes, this means things can go wrong, and they can go wrong catastrophically.
			But when they do the user will get a nice 'report this issue' dialogue, and I can fix the bug.*/
        }
    }

    public static void dropToChasm(Item item) {
        String nextLevel = customDungeon.getFloor(Dungeon.levelName).getChasm();
        ArrayList<Item> dropped = Dungeon.droppedItems.get(nextLevel);
        if (dropped == null) {
            Dungeon.droppedItems.put(nextLevel, dropped = new ArrayList<>());
        }
        dropped.add(item);
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

    private static final String INIT_VER = "init_ver";
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
    private static final String GENERATED_LEVELS    = "generated_levels";
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
    private static final String VISITED = "visited";
    private static final String VISITED_DEPTHS = "visited_depths";
    private static final String CUSTOM_DUNGEON = "custom_dungeon";

    public static void saveGame(int save) {
        try {
            Bundle bundle = new Bundle();

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


            quickslot.storePlaceholders(bundle);

            Bundle limDrops = new Bundle();
            LimitedDrops.store(limDrops);
            bundle.put(LIMDROPS, limDrops);

            int count = 0;
            int ids[] = new int[chapters.size()];
            for (Integer id : chapters) {
                ids[count++] = id;
            }
            bundle.put(CHAPTERS, ids);

            Bundle quests = new Bundle();
            GhostQuest.storeStatics(quests);
//            Wandmaker.Quest.storeInBundle(quests);
//            Blacksmith.Quest.storeInBundle(quests);
            ImpQuest.storeStatics(quests);
            bundle.put(QUESTS, quests);

            SpecialRoom.storeRoomsInBundle(bundle);
            SecretRoom.storeRoomsInBundle(bundle);

            Statistics.storeInBundle(bundle);
            Notes.storeInBundle(bundle);
            Generator.storeInBundle(bundle);

            int[] bundleArr = new int[generatedLevels.size()];
            for (i = 0; i < generatedLevels.size(); i++){
                bundleArr[i] = generatedLevels.get(i);
            }
            bundle.put( GENERATED_LEVELS, bundleArr);

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

        FileUtils.bundleToFile(GamesInProgress.levelFile(save, levelName), bundle);
    }

    public static void saveAll() throws IOException {
        if (hero != null && (hero.isAlive() || WndResurrect.instance != null)) {

            Actor.fixTime();
            updateLevelExplored();
            saveGame(GamesInProgress.curSlot);
            saveLevel(GamesInProgress.curSlot);

            GamesInProgress.set(GamesInProgress.curSlot);

        }
    }

    public static void loadGame(int save) throws IOException {
        loadGame(save, true);
    }

    public static void loadGame(int save, boolean fullLoad) throws IOException {

        Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(save));

        initialVersion = bundle.getInt(VERSION);

        version = bundle.getInt(VERSION);

        seed = bundle.contains(SEED) ? bundle.getLong(SEED) : DungeonSeed.randomSeed();
        customSeedText = bundle.getString(CUSTOM_SEED);
        daily = bundle.getBoolean(DAILY);
        dailyReplay = bundle.getBoolean(DAILY_REPLAY);

        Actor.clear();
        Actor.restoreNextID(bundle);

        quickslot.reset();
        QuickSlotButton.reset();
        Toolbar.swappedQuickslots = false;

        Dungeon.challenges = bundle.getInt(CHALLENGES);
        Dungeon.mobsToChampion = bundle.getInt(MOBS_TO_CHAMPION);

        Dungeon.level = null;
        Dungeon.depth = -1;

        Scroll.restore(bundle);
        Potion.restore(bundle);
        Ring.restore(bundle);

        visited = bundle.getStringArray(VISITED);

        int[] visitedDepthsArray = bundle.getIntArray(VISITED_DEPTHS);
        visitedDepths = new HashSet<>();
        if (visitedDepthsArray != null) for (int d : visitedDepthsArray) visitedDepths.add(d);

        quickslot.restorePlaceholders(bundle);

        if (fullLoad) {

            LimitedDrops.restore(bundle.getBundle(LIMDROPS));

            chapters = new HashSet<>();
            int ids[] = bundle.getIntArray(CHAPTERS);
            if (ids != null) {
                for (int id : ids) {
                    chapters.add(id);
                }
            }

            Bundle quests = bundle.getBundle(QUESTS);
            if (!quests.isNull()) {
                GhostQuest.restoreStatics(quests);
//                Wandmaker.Quest.restoreFromBundle(quests);
//                Blacksmith.Quest.restoreFromBundle(quests);
                ImpQuest.restoreStatics(quests);
            } else {
                GhostQuest.reset();
//                Wandmaker.Quest.reset();
//                Blacksmith.Quest.reset();
                ImpQuest.reset();
            }

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

        gold = bundle.getInt(GOLD);
        energy = bundle.getInt(ENERGY);

        customDungeon = (CustomDungeon) bundle.get(CUSTOM_DUNGEON);

        Statistics.restoreFromBundle(bundle);
        Generator.restoreFromBundle(bundle);

        generatedLevels.clear();
        if (bundle.contains(GENERATED_LEVELS)){
            for (int i : bundle.getIntArray(GENERATED_LEVELS)){
                generatedLevels.add(i);
            }
            //pre-v2.1.1 saves
        } else  {
            for (int i = 1; i <= Statistics.deepestFloor; i++){
                generatedLevels.add(i);
            }
        }

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

        CustomDungeonSaves.setFileType(Files.FileType.Local);
        CustomDungeonSaves.setCurDirectory(GamesInProgress.getCustomDungeonLevelFolder(save));
    }

    public static Level loadLevel(int save) throws IOException {

        Dungeon.level = null;
        Actor.clear();

        Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.levelFile(save, levelName));

        Level level = (Level) bundle.get(LEVEL);

        if (level == null) {
            throw new IOException();
        } else {
            return level;
        }
    }

    public static void deleteGame(int save, boolean deleteLevels) {

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

    public static void preview(GamesInProgress.Info info, Bundle bundle) {
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

        Rankings.INSTANCE.submit(true, cause);
    }

    public static void updateLevelExplored() {
        if (branch == 0&&level instanceof RegularLevel && !Dungeon.bossLevel()) {
            Statistics.floorsExplored.put(levelName, level.isLevelExplored(levelName));
        }
    }

    //default to recomputing based on max hero vision, in case vision just shrank/grew
    public static void observe() {
        int dist = Math.max(Dungeon.hero.viewDistance, 8);
        dist *= 1f + 0.25f * Dungeon.hero.pointsInTalent(Talent.FARSIGHT);

        if (Dungeon.hero.buff(MagicalSight.class) != null) {
            dist = Math.max(dist, MagicalSight.DISTANCE);
        }

        observe(dist + 1);
    }

    public static void observe(int dist) {

        if (level == null) {
            return;
        }

        level.updateFieldOfView(hero, level.heroFOV);

        int x = hero.pos % level.width();
        int y = hero.pos / level.width();

        //left, right, top, bottom
        int l = Math.max(0, x - dist);
        int r = Math.min(x + dist, level.width() - 1);
        int t = Math.max(0, y - dist);
        int b = Math.min(y + dist, level.height() - 1);

        int width = r - l + 1;
        int height = b - t + 1;

        int pos = l + t * level.width();

        for (int i = t; i <= b; i++) {
            BArray.or(level.visited, level.heroFOV, pos, width, level.visited);
            pos += level.width();
        }

        GameScene.updateFog(l, t, width, height);

        if (hero.buff(MindVision.class) != null) {
            for (Mob m : level.mobs.toArray(new Mob[0])) {
                BArray.or(level.visited, level.heroFOV, m.pos - 1 - level.width(), 3, level.visited);
                BArray.or(level.visited, level.heroFOV, m.pos - 1, 3, level.visited);
                BArray.or(level.visited, level.heroFOV, m.pos - 1 + level.width(), 3, level.visited);
                //updates adjacent cells too
                GameScene.updateFog(m.pos, 2);
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
            if (!Dungeon.levelName.equals(h.level)|| Dungeon.branch != h.branch) continue;
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

        for (Char ch : Actor.chars()) {
            if (ch instanceof WandOfWarding.Ward
                    || ch instanceof WandOfRegrowth.Lotus
                    || ch instanceof SpiritHawk.HawkAlly) {
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

                pos = l + t * level.width();

                for (int i = t; i <= b; i++) {
                    BArray.or(level.visited, level.heroFOV, pos, width, level.visited);
                    pos += level.width();
                }
                GameScene.updateFog(ch.pos, dist);
            }
        }

        GameScene.afterObserve();
    }

    //we store this to avoid having to re-allocate the array with each pathfind
    private static boolean[] passable;

    private static void setupPassable() {
        if (passable == null || passable.length != Dungeon.level.length())
            passable = new boolean[Dungeon.level.length()];
        else
            BArray.setFalse(passable);
    }

    public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars) {
        return findPassable(ch, pass, vis, chars, chars);
    }

    public static boolean[] findPassable(Char ch, boolean[] pass, boolean[] vis, boolean chars, boolean considerLarge){
        setupPassable();
        if (ch.flying || ch.buff(Amok.class) != null) {
            BArray.or(pass, Dungeon.level.avoid, passable);
        } else {
            System.arraycopy(pass, 0, passable, 0, Dungeon.level.length());
        }

        if (considerLarge && Char.hasProp(ch, Char.Property.LARGE)){
            BArray.and( passable, Dungeon.level.openSpace, passable );
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

    public static int findStep(Char ch, int to, boolean[] pass, boolean[] visible, boolean chars) {

        if (Dungeon.level.adjacent(ch.pos, to)) {
            return Actor.findChar(to) == null && (pass[to] || Dungeon.level.avoid[to]) ? to : -1;
        }

        setupPassable();
        if (ch.flying || ch.buff(Amok.class) != null) {
            BArray.or(pass, Dungeon.level.avoid, passable);
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
        //only consider chars impassable if our retreat path runs into them
        boolean[] passable = findPassable(ch, pass, visible, false, true);
        passable[ch.pos] = true;

        int step = PathFinder.getStepBack( ch.pos, from, passable );
        while (step != -1 && Actor.findChar(step) != null && chars){
            passable[step] = false;
            step = PathFinder.getStepBack( ch.pos, from, passable );
        }
        return step;

    }

}