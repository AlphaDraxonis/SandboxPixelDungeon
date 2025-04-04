package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.LuaLevelScript;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.ImpQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.RoomLayoutLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.watabou.NotAllowedInLua;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class LevelScheme implements Bundlable, Comparable<LevelScheme>, LevelSchemeLike {

    public static final LevelScheme SURFACE_LEVEL_SCHEME = new LevelScheme();//Placeholder for selecting levels
    public static final LevelScheme NO_LEVEL_SCHEME = new LevelScheme();//Placeholder for selecting levels
    public static final LevelScheme ANY_LEVEL_SCHEME = new LevelScheme();//Placeholder for key working on any level

    static {
        initSpecialLevelSchemeNames();
    }

    public static void initSpecialLevelSchemeNames() {
        SURFACE_LEVEL_SCHEME.name = EditorUtilities.getDispayName(Level.SURFACE);
        NO_LEVEL_SCHEME.name = EditorUtilities.getDispayName(Level.NONE);
        ANY_LEVEL_SCHEME.name = EditorUtilities.getDispayName(Level.ANY);
    }


    String name;
    CustomDungeon customDungeon;

    String levelCreatedBefore, levelCreatedAfter;

    private int depth;
    private String chasm = null, passage;
    private LevelTransition entranceTransitionRegular, exitTransitionRegular;//only for type Regular

    private Class<? extends Level> type;
    private Level level;
    int region = REGION_SEWERS;
    private int numInRegion = 3;

    public int waterTexture = REGION_NONE;
    public int musicRegion = REGION_NONE;
    public String musicFile = null;
    public String customTilesTex, customWaterTex;

    private Level.Feeling feeling = Level.Feeling.NONE;
    public float shopPriceMultiplier = 1f;

    private long seed;
    private boolean seedSet = false;
    public List<Integer> entranceCells, exitCells;
    public Set<String> zones;

    public int floorColor, wallColor, waterColor;
    public float floorAlpha = 0f, wallAlpha = 0f, waterAlpha = 0f;

    public List<Mob> mobsToSpawn;
    public List<Room> roomsToSpawn;
    public Builder builder;
    public List<Item> itemsToSpawn, prizeItemsToSpawn;

    public boolean spawnStandardRooms = true, spawnSecretRooms = true, spawnSpecialRooms = true;
    public boolean spawnMobs = true, spawnItems = true;

    public float hungerSpeed = 1f, naturalRegenSpeed = 1f;
    public boolean allowPickaxeMining = false;
    public boolean rememberLayout = true;
    public boolean magicMappingDisabled = false;

    //Challenge stuff
    public boolean reduceViewDistanceIfDarkness = true,
            affectedByNoScrolls = true, rollForChampionIfChampionChallenge = true;


    public int luaScriptID;


    public LevelScheme() {
        zones = new HashSet<>(3);
        
        mobsToSpawn = new ArrayList<>(4);
        roomsToSpawn = new ArrayList<>(4);
        itemsToSpawn = new ArrayList<>(4);
        prizeItemsToSpawn = new ArrayList<>(4);
        
        exitCells = new ArrayList<>(3);
        entranceCells = new ArrayList<>(3);
    }

    //Wrapper for creating templates
    LevelScheme(Level level, int numInRegion) {
        this();
        this.name = Level.NONE;
        this.level = level;
        this.region = getRegion(level);
        this.numInRegion = numInRegion;
    }

    public LevelScheme(RoomLayoutLevel roomLayout) {
        this();
        this.name = roomLayout.name = NO_LEVEL_SCHEME.getName();
        this.level = roomLayout;
        roomLayout.levelScheme = this;

        type = RoomLayoutLevel.class;

        Dungeon.customDungeon.addFloor(this);
        shopPriceMultiplier = Dungeon.getSimulatedDepth(this) / 5 + 1;
    }

    @NotAllowedInLua
    public void initNewLevelScheme(String name, Class<? extends Level> levelTemplate) {
        this.name = name;

        if (depth == 6 || depth == 11 || depth == 16) roomsToSpawn.add(new ShopRoom());

        if (type == CustomLevel.class) {
            level = new CustomLevel(name, levelTemplate, feeling, seedSet ? seed : null, numInRegion, depth, this);
        }
        shopPriceMultiplier = Dungeon.getSimulatedDepth(this) / 5 + 1;

    }

    //These setters are ONLY for NewFloorComp
    @NotAllowedInLua
    public void setType(Class<? extends Level> type) {
        if (type.getSimpleName().endsWith("_lua")) type = (Class<? extends Level>) type.getSuperclass();
        if (CustomLevel.class.isAssignableFrom(type)) type = CustomLevel.class;
        this.type = type;
        if (type != CustomLevel.class) region = getRegion(type);
    }

    public void setNumInRegion(int numInRegion) {
        this.numInRegion = numInRegion;
    }

    //These setters/getters are ONLY for LevelGenComp
    public void resetSeed() {
        seed = 0;
        seedSet = false;
    }

    public boolean isSeedSet() {
        return seedSet;
    }

    public LevelScheme(String name, int depth, CustomDungeon customDungeon) {
        this.name = name;
        this.depth = depth;
        this.customDungeon = customDungeon;

        mobsToSpawn = new ArrayList<>(4);
        roomsToSpawn = new ArrayList<>(4);
        itemsToSpawn = new ArrayList<>(4);
        prizeItemsToSpawn = new ArrayList<>(4);
        if (depth < 26) setChasm(Integer.toString(depth + 1), false);

        switch (depth) {
            case 1:
            case 2:
            case 3:
            case 4:
                type = SewerLevel.class;
                break;
            case 5:
                type = SewerBossLevel.class;
                customDungeon.addRatKingLevel(name);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                type = PrisonLevel.class;
                break;
            case 10:
                type = PrisonBossLevel.class;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                type = CavesLevel.class;
                break;
            case 15:
                type = CavesBossLevel.class;
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                type = CityLevel.class;
                break;
            case 20:
                type = CityBossLevel.class;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
                type = HallsLevel.class;
                break;
            case 25:
                type = HallsBossLevel.class;
                break;
            case 26:
                type = LastLevel.class;
                break;
            default:
                type = DeadEndLevel.class;
        }
        region = getRegion(type);
        numInRegion = (depth - 1) % 5 + 1;

        if (depth == 6 || depth == 11 || depth == 16) roomsToSpawn.add(new ShopRoom());

        shopPriceMultiplier = depth / 5 + 1;
        passage = Integer.toString(((depth - 1) / 5) * 5 + 1);

        exitCells = new ArrayList<>(3);
        entranceCells = new ArrayList<>(3);
        zones = new HashSet<>(3);
        initExitEntranceCellsForRandomLevel();
        if (depth > 1) {
            entranceTransitionRegular.destLevel = Integer.toString(depth - 1);
        }
        exitTransitionRegular.destLevel = Integer.toString(depth + 1);
    }

    public void initExitEntranceCellsForRandomLevel() {
        if (type != DeadEndLevel.class && type != LastLevel.class)
            exitCells.add(TransitionEditPart.DEFAULT);
        else exitCells.add(TransitionEditPart.NONE);
        entranceCells.add(TransitionEditPart.DEFAULT);
        exitTransitionRegular = new LevelTransition(null, -1);

        entranceTransitionRegular = new LevelTransition(getDefaultAbove(), -1);
        LevelScheme entranceTrans = customDungeon.getFloor(entranceTransitionRegular.destLevel);
        if (entranceTrans != null && !entranceTrans.exitCells.isEmpty()) entranceTransitionRegular.destCell = entranceTrans.exitCells.get(0);
    }

    /**
     * All unassign exits get defaultBelow as dest level, sets chasm if null
     */
    public void setToDefaultExits() {
        String defaultBelow = getDefaultBelow();
        if (customDungeon.getFloor(defaultBelow) == null) return;
//        if (Objects.equals(getDefaultAbove(),defaultBelow)) return;
        if (getChasm() == null) setChasm(defaultBelow, true);

        if (type == CustomLevel.class) {
            List<Integer> possibleEntrances = customDungeon.getFloor(defaultBelow).entranceCells;
            int size = possibleEntrances.size();
            if (size > 0) {
                boolean load = level == null;
                if (load) loadLevel();
                boolean save = false;
                int destCell = possibleEntrances.get(0);
                if (destCell == TransitionEditPart.NONE && size > 1) destCell = possibleEntrances.get(1);
                if (destCell != TransitionEditPart.NONE) {
                    for (int i : exitCells) {
                        if (!level.transitions.containsKey(i)) {
                            LevelTransition t = new LevelTransition(level, i, destCell, defaultBelow);
                            t.type = LevelTransition.Type.REGULAR_EXIT;
                            level.transitions.put(i, t);
                            save = true;
                        }
                    }
                    if (exitCells.size() > 0 && save) {
                        try {
                            saveLevel();//Need to always save because otherwise, it will be unloaded without any additional checks
                        } catch (IOException ignored) {
                        }
                    }
                }
                if (load) unloadLevel();
            }
        } else {
            exitTransitionRegular.destLevel = defaultBelow;
            List<Integer> possibleEntrances = customDungeon.getFloor(defaultBelow).entranceCells;
            int size = possibleEntrances.size();
            if (size > 0) {
                int destCell = possibleEntrances.get(0);
                if (destCell == TransitionEditPart.NONE && size > 1) destCell = possibleEntrances.get(1);
                if (destCell != TransitionEditPart.NONE) exitTransitionRegular.destCell = destCell;
            }
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getDepth() {
        return depth;
    }

    public String getChasm() {
        if (chasm == null) {
            String below = getDefaultBelow();
//            if (Objects.equals(below, getDefaultAbove())) return null;
            return below;
        }
        return chasm;
    }

    public String getDefaultAbove() {
        if (entranceTransitionRegular == null || entranceTransitionRegular.destLevel == null) {
            LevelScheme suggestion = customDungeon.getFloor(levelCreatedBefore);
            if (suggestion != null && suggestion.depth <= depth) return suggestion.getName();
            suggestion = customDungeon.getFloor(levelCreatedAfter);
            if (suggestion != null && suggestion.depth < depth) return suggestion.getName();
            return Level.SURFACE;
        }
        return entranceTransitionRegular.destLevel;
    }

    public String getDefaultBelow() {
        if (exitTransitionRegular == null || exitTransitionRegular.destLevel == null) {
            LevelScheme suggestion = customDungeon.getFloor(levelCreatedAfter);
            if (suggestion != null && suggestion.depth >= depth) return suggestion.getName();
            suggestion = customDungeon.getFloor(levelCreatedBefore);
            if (suggestion != null && suggestion.depth > depth) return suggestion.getName();
            return null;
        }
        return exitTransitionRegular.destLevel;
    }

    public LevelTransition getEntranceTransitionRegular() {
        return entranceTransitionRegular;
    }

    public LevelTransition getExitTransitionRegular() {
        return exitTransitionRegular;
    }

    public String getPassage() {//Passage entry
        return passage == null
                ? Dungeon.customDungeon.getStart()
                : passage;
    }

    public Class<? extends Level> getType() {
        return type;
    }

    public Level getLevel() {
        return level;
    }

    public CustomDungeon getCustomDungeon() {
        return customDungeon;
    }

    public Level.Feeling getFeeling() {
        return feeling;
    }

    public boolean hasBoss() {
        return getBoss() != REGION_NONE;
    }

    public int generateGhostQuestNotRandom() {
        if (numInRegion <= 2) return GhostQuest.RAT;
        if (numInRegion == 3) return GhostQuest.GNOLL;
        return GhostQuest.CRAB;
    }

    public int generateWandmakerQuest() {
        return Random.Int(WandmakerQuest.NUM_QUESTS);
    }

    public int generateBlacksmithQuest() {
        return BlacksmithQuest.CRYSTAL + Random.Int(2);
    }

    public int generateImpQuestNotRandom() {
        //always assigns monks on floor 17, golems on floor 19, and 50/50 between either on 18
        if (numInRegion <= 2) return ImpQuest.MONK_QUEST;
        if (numInRegion >= 4) return ImpQuest.GOLEM_QUEST;
        return Random.Int(2);
    }

    public void setChasm(String chasm, boolean revalidateZones) {
        this.chasm = chasm;
        LevelScheme newChasm = customDungeon.getFloor(chasm);
        if (revalidateZones && type == CustomLevel.class && level != null) {
            for (Zone zone : level.zoneMap.values()) {
                if (zone.chasmDestZone != null && (newChasm == null || !newChasm.zones.contains(zone.chasmDestZone)))
                    zone.chasmDestZone = null;
            }
        }
    }

    public void setPassage(String passage) {
        if (!Objects.equals(passage, customDungeon.getStart())) {
            this.passage = passage;
        } else {
            this.passage = null;
        }
    }

    public void setEntranceTransitionRegular(LevelTransition entranceTransitionRegular) {
        this.entranceTransitionRegular = entranceTransitionRegular;
    }

    public void setExitTransitionRegular(LevelTransition exitTransitionRegular) {
        this.exitTransitionRegular = exitTransitionRegular;
    }

    @NotAllowedInLua
    public void setLevel(Level level) {
        this.level = level;
        setType(level.getClass());
        level.setLevelScheme(this);
    }

    public void setFeeling(Level.Feeling feeling) {
        this.feeling = feeling;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @NotAllowedInLua
    public Level initLevel() {
        int oldHeroPos = Dungeon.hero.pos;
        Dungeon.hero.pos = -1;

        if (type != CustomLevel.class) {
            Random.pushGenerator(seed);
            level = luaScriptID == 0 ? Reflection.newInstance(type)
                    : (Level) Reflection.newInstance(LuaClassGenerator.luaUserContentClass(type));
            level.setLevelScheme(this);

            if (luaScriptID != 0) ((LuaLevel) level).setIdentifier(luaScriptID);

            Dungeon.level = level;
            Dungeon.levelName = name;
            level.feeling = feeling;
            initRandomStats(Random.Long());
            level.create();
            spawnItemsAndMobs(Random.Long());
            Random.popGenerator();
        } else {
            try {
                //make sure the levels are different objects
                level = CustomDungeonSaves.loadLevel(name, luaScriptID);
            } catch (IOException e) {
                SandboxPixelDungeon.reportException(e);
            } catch (CustomDungeonSaves.RenameRequiredException e) {
                throw new RuntimeException(e);//Caught by InterlevelScene
            }
            level.setLevelScheme(this);

            if (luaScriptID != 0) ((LuaLevel) level).setIdentifier(luaScriptID);

            Dungeon.level = level;
            Dungeon.levelName = name;
            initRandomStats(seed);

            if (Dungeon.isChallenged(Challenges.DARKNESS)) {
                if (reduceViewDistanceIfDarkness) level.viewDistance = level.viewDistance* 5/8;
            }

            spawnItemsAndMobs(seed + 229203);

            if (Dungeon.isChallenged(Challenges.CHAMPION_ENEMIES) && rollForChampionIfChampionChallenge) {
                Random.pushGenerator(seed + 9488532);
                List<Mob> sortedMobs = new ArrayList<>(level.mobs);
                Collections.sort(sortedMobs, (m1, m2) -> m1.pos - m2.pos);
                for (Mob m : sortedMobs) {
                    if (m.buffs(ChampionEnemy.class).isEmpty()) {
                        ChampionEnemy.rollForChampion(m);
                    }
                }
                Random.popGenerator();
            }

            if (feeling == null) {
                Random.pushGenerator(seed + 56709);
                switch (Random.Int(14)) {
                    case 0:
                        level.feeling = Level.Feeling.CHASM;
                        break;
                    case 1:
                        level.feeling = Level.Feeling.WATER;
                        break;
                    case 2:
                        level.feeling = Level.Feeling.GRASS;
                        break;
                    case 3:
                        level.feeling = Level.Feeling.DARK;
                        break;
                    case 4:
                        level.feeling = Level.Feeling.LARGE;
                        break;
                    case 5:
                        level.feeling = Level.Feeling.TRAPS;
                        break;
                    case 6:
                        level.feeling = Level.Feeling.SECRETS;
                        break;
                    default:
                        level.feeling = Level.Feeling.NONE;
                }
                if (level.feeling == Level.Feeling.DARK)
                    level.viewDistance = Math.round(level.viewDistance / 2f);
                Random.popGenerator();
            } else level.feeling = feeling;
        }
        if (Dungeon.isChallenged(Challenges.NO_SCROLLS) && affectedByNoScrolls) {
            customDungeon.removeEverySecondSoU(level);
        }
        Dungeon.hero.pos = oldHeroPos;
        for (Mob m : level.mobs) {
            m.setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME();
        }
        level.initForPlay();
        return level;
    }

    @NotAllowedInLua
    private void initRandomStats(long seed) {
        Random.pushGenerator(seed);

		try {
			CustomDungeon.doOnEverythingInLevelScheme(this, true, GameObject::initRandoms, false, null, null);
		} catch (IOException e) {
			//should already be loaded
		}

        Random.popGenerator();
    }

    @NotAllowedInLua
    private void spawnItemsAndMobs(long seed) {
        Random.pushGenerator(seed);

        if (!(level instanceof RegularLevel)) {
            for (Mob m : mobsToSpawn) {
                if (m.pos <= 0) {
                    int tries = level.length();
                    do {
                        m.pos = level.randomRespawnCell(m);
                        tries--;
                    } while (m.pos == -1 && tries > 0);
                    if (m.pos != -1) {
                        if (!(m instanceof NPC) && m.buffs(ChampionEnemy.class).isEmpty()) {
                            ChampionEnemy.rollForChampion(m);
                        }
                        level.mobs.add(m);
                        if (level.map[m.pos] == Terrain.HIGH_GRASS)
                            level.map[m.pos] = Terrain.GRASS;
                    }
                }
            }
        }
        if (type == CustomLevel.class) itemsToSpawn.addAll(prizeItemsToSpawn);

        for (Item item : itemsToSpawn) {
            int cell;
            if (level instanceof CustomLevel) cell = ((CustomLevel) level).randomDropCell();
            else if (level instanceof RegularLevel) cell = ((RegularLevel) level).randomDropCell();
            else cell = CustomLevel.randomDropCell(level);
            if (level.map[cell] == Terrain.HIGH_GRASS || level.map[cell] == Terrain.FURROWED_GRASS) {
                level.map[cell] = Terrain.GRASS;
                level.losBlocking[cell] = false;
            }
            Heap h = level.drop(item, cell);
            if (h.type == null) h.type = Heap.Type.HEAP;
        }

        Random.popGenerator();
    }


    public long getSeed() {
        if (!seedSet && seed == 0) return 1234567;//TODO tzz use random seed!
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        seedSet = true;
    }

    public void initSeed() {
        if (!seedSet) {
            int lookAhead = depth;
//            if (name.equals(String.valueOf(depth)))
            lookAhead += name.hashCode() % 100;

            for (int i = 0; i < lookAhead; i++) {
                Random.Long(); //we don't care about these values, just need to go through them
            }
            seed = Random.Long();
        }
    }


    private static final String NAME = "name";
    private static final String LEVEL_CREATED_BEFORE = "level_created_before";
    private static final String LEVEL_CREATED_AFTER = "level_created_after";
    private static final String ENTRANCE_CELLS = "entrance_cells";
    private static final String EXIT_CELLS = "exit_cells";
    private static final String ZONES = "zones";
    private static final String CHASM = "chasm";
    private static final String DEST_ENTRANCE_REGULAR = "dest_entrance_regular";
    private static final String DEST_EXIT_REGULAR = "dest_exit_regular";
    private static final String PASSAGE = "passage";
    private static final String DEPTH = "depth";
    private static final String TYPE = "type";
            static final String REGION = "region";
            static final String WATER_TEXTURE = "water_texture";
            static final String MUSIC_REGION = "music_region";
            static final String MUSIC_FILE = "music_file";
    private static final String CUS_TILES_TEX = "cus_tiles_tex";
    private static final String CUS_WATER_TEX = "cus_water_tex";
    private static final String NUM_IN_REGION = "num_in_region";
    private static final String FEELING = "feeling";
    private static final String SEED = "seed";
    private static final String SEED_SET = "seed_set";
    private static final String SHOP_PRICE_MULTIPLIER = "shop_price_multiplier";
    private static final String ALLOW_PICKAXE_MINING = "allow_pickaxe_mining";
    private static final String REMEMBER_LAYOUT = "remember_layout";
    private static final String MAGIC_MAPPING_DISABLED = "magic_mapping_disabled";

    private static final String MOBS_TO_SPAWN = "mobs_to_spawn";
    private static final String ROOMS_TO_SPAWN = "rooms_to_spawn";
    private static final String ITEMS_TO_SPAWN = "items_to_spawn";
    private static final String PRIZE_ITEMS_TO_SPAWN = "prize_items_to_spawn";
    private static final String SPAWN_STANDART_ROOMS = "spawn_standart_rooms";
    private static final String SPAWN_SECRET_ROOMS = "spawn_secret_rooms";
    private static final String SPAWN_SPECIAL_ROOMS = "spawn_special_rooms";
    private static final String SPAWN_MOBS = "spawn_mobs";
    private static final String SPAWN_ITEMS = "spawn_items";
    private static final String NATURAL_REGEN_SPEED = "natural_regen_speed";
    private static final String HUNGER_SPEED = "hunger_speed";
    private static final String BUILDER = "builder";
    private static final String REDUCE_VIEW_DISTANCE_IF_DARKNESS = "reduce_view_distance_if_darkness";
    private static final String AFFECTED_BY_NO_SCROLLS = "affected_by_no_scrolls";
    private static final String ROLL_FOR_CHAMPION_IF_CHAMPION_CHALLENGE = "roll_for_champion_if_champion_challenge";

    private static final String LEVEL_COLORING = "level_coloring";
    private static final String LEVEL_COLORING_ALPHA = "level_coloring_alpha";

    private static final String SCRIPT_ID = "script_id";

    @Override
    @NotAllowedInLua
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        if (chasm != null) bundle.put(CHASM, chasm);
        if (levelCreatedBefore != null) bundle.put(LEVEL_CREATED_BEFORE, levelCreatedBefore);
        if (levelCreatedAfter != null) bundle.put(LEVEL_CREATED_AFTER, levelCreatedAfter);
        if (entranceTransitionRegular != null)
            bundle.put(DEST_ENTRANCE_REGULAR, entranceTransitionRegular);
        if (exitTransitionRegular != null) bundle.put(DEST_EXIT_REGULAR, exitTransitionRegular);
        bundle.put(PASSAGE, passage);
        bundle.put(DEPTH, depth);
        bundle.put(TYPE, type);
        bundle.put(REGION, region);
        bundle.put(NUM_IN_REGION, numInRegion);
        bundle.put(SEED, seed);
        bundle.put(SEED_SET, seedSet);
        bundle.put(FEELING, feeling);
        bundle.put(SHOP_PRICE_MULTIPLIER, shopPriceMultiplier);
        bundle.put(ALLOW_PICKAXE_MINING, allowPickaxeMining);
        bundle.put(REMEMBER_LAYOUT, rememberLayout);
        bundle.put(MAGIC_MAPPING_DISABLED, magicMappingDisabled);

        bundle.put(REDUCE_VIEW_DISTANCE_IF_DARKNESS, reduceViewDistanceIfDarkness);
        bundle.put(AFFECTED_BY_NO_SCROLLS, affectedByNoScrolls);
        bundle.put(ROLL_FOR_CHAMPION_IF_CHAMPION_CHALLENGE, rollForChampionIfChampionChallenge);

        bundle.put(LEVEL_COLORING, new int[] {floorColor, wallColor, waterColor});
        bundle.put(LEVEL_COLORING_ALPHA, new float[] {floorAlpha, wallAlpha, waterAlpha});

        bundle.put(SCRIPT_ID, luaScriptID);

        int[] entrances = new int[entranceCells.size()];
        int i = 0;
        for (int cell : entranceCells) {
            entrances[i] = cell;
            i++;
        }
        bundle.put(ENTRANCE_CELLS, entrances);
        int[] exits = new int[exitCells.size()];
        i = 0;
        for (int cell : exitCells) {
            exits[i] = cell;
            i++;
        }
        bundle.put(EXIT_CELLS, exits);
        bundle.put(ZONES, zones.toArray(EditorUtilities.EMPTY_STRING_ARRAY));

        bundle.put(MOBS_TO_SPAWN, mobsToSpawn);
        bundle.put(ITEMS_TO_SPAWN, itemsToSpawn);
        bundle.put(PRIZE_ITEMS_TO_SPAWN, prizeItemsToSpawn);
        bundle.put(ROOMS_TO_SPAWN, roomsToSpawn);
        bundle.put(SPAWN_STANDART_ROOMS, spawnStandardRooms);
        bundle.put(SPAWN_SPECIAL_ROOMS, spawnSpecialRooms);
        bundle.put(SPAWN_SECRET_ROOMS, spawnSecretRooms);
        bundle.put(SPAWN_MOBS, spawnMobs);
        bundle.put(SPAWN_ITEMS, spawnItems);
        bundle.put(HUNGER_SPEED, hungerSpeed);
        bundle.put(NATURAL_REGEN_SPEED, naturalRegenSpeed);

        bundle.put(WATER_TEXTURE, waterTexture);
        bundle.put(MUSIC_REGION, musicRegion);
        if (musicFile != null) bundle.put(MUSIC_FILE, musicFile);
        if (customTilesTex != null) bundle.put(CUS_TILES_TEX, customTilesTex);
        if (customWaterTex != null) bundle.put(CUS_WATER_TEX, customWaterTex);

        if (builder != null) bundle.put(BUILDER, builder);
    }

    @Override
    @NotAllowedInLua
    public void restoreFromBundle(Bundle bundle) {
        name = bundle.getString(NAME);
        if (bundle.contains(CHASM)) chasm = bundle.getString(CHASM);
        if (bundle.contains(PASSAGE)) {
            passage = bundle.getString(PASSAGE);
            if (passage.isEmpty()) passage = null;
        }

        if (bundle.contains(LEVEL_CREATED_BEFORE)) levelCreatedBefore = bundle.getString(LEVEL_CREATED_BEFORE);
        if (bundle.contains(LEVEL_CREATED_AFTER)) levelCreatedAfter = bundle.getString(LEVEL_CREATED_AFTER);

        if (bundle.contains(DEST_ENTRANCE_REGULAR))
            entranceTransitionRegular = (LevelTransition) bundle.get(DEST_ENTRANCE_REGULAR);
        if (bundle.contains(DEST_EXIT_REGULAR))
            exitTransitionRegular = (LevelTransition) bundle.get(DEST_EXIT_REGULAR);
        
        depth = bundle.getInt(DEPTH);
        if (bundle.contains(FEELING)) feeling = bundle.getEnum(FEELING, Level.Feeling.class);
        shopPriceMultiplier = bundle.getFloat(SHOP_PRICE_MULTIPLIER);
        allowPickaxeMining = bundle.getBoolean(ALLOW_PICKAXE_MINING);
        rememberLayout = !bundle.contains(REMEMBER_LAYOUT) || bundle.getBoolean(REMEMBER_LAYOUT);
        magicMappingDisabled = bundle.getBoolean(MAGIC_MAPPING_DISABLED);

        reduceViewDistanceIfDarkness = bundle.getBoolean(REDUCE_VIEW_DISTANCE_IF_DARKNESS);
        affectedByNoScrolls = bundle.getBoolean(AFFECTED_BY_NO_SCROLLS);
        rollForChampionIfChampionChallenge = bundle.getBoolean(ROLL_FOR_CHAMPION_IF_CHAMPION_CHALLENGE);

        int[] intArray = bundle.getIntArray(LEVEL_COLORING);
        if (intArray != null) {
            floorColor = intArray[0];
            wallColor = intArray[1];
            waterColor = intArray[2];
            float[] floatArray = bundle.getFloatArray(LEVEL_COLORING_ALPHA);
            floorAlpha = floatArray[0];
            wallAlpha = floatArray[1];
            waterAlpha = floatArray[2];
        }

        int[] entrances = bundle.getIntArray(ENTRANCE_CELLS);
        entranceCells = new ArrayList<>(entrances.length + 1);
        for (int entrance : entrances) entranceCells.add(entrance);
        Collections.sort(entranceCells);//should actually already be sorted

        int[] exits = bundle.getIntArray(EXIT_CELLS);
        exitCells = new ArrayList<>(exits.length + 1);
        for (int exit : exits) exitCells.add(exit);
        Collections.sort(exitCells);

        String[] zs = bundle.getStringArray(ZONES);
        if (zs == null) zones = new HashSet<>(3);
        else {
            zones = new HashSet<>(zs.length + 1);
            Collections.addAll(zones, zs);
        }

        type = bundle.getClass(TYPE);
        region = bundle.getInt(REGION);
        if (region == REGION_NONE) region = getRegion(type);

        waterTexture = bundle.getInt(WATER_TEXTURE);

        musicRegion = bundle.getInt(MUSIC_REGION) + bundle.getInt("music");
        if (bundle.contains(MUSIC_FILE)) musicFile = bundle.getString(MUSIC_FILE);
        if (bundle.contains(CUS_TILES_TEX)) customTilesTex = bundle.getString(CUS_TILES_TEX);
        if (bundle.contains(CUS_WATER_TEX)) customWaterTex = bundle.getString(CUS_WATER_TEX);

        numInRegion = bundle.getInt(NUM_IN_REGION);
        seed = bundle.getLong(SEED);
        seedSet = bundle.getBoolean(SEED_SET);

        mobsToSpawn = new ArrayList<>();
        if (bundle.contains(MOBS_TO_SPAWN))
            for (Bundlable l : bundle.getCollection(MOBS_TO_SPAWN)) mobsToSpawn.add((Mob) l);

        itemsToSpawn = new ArrayList<>();
        if (bundle.contains(ITEMS_TO_SPAWN))
            for (Bundlable l : bundle.getCollection(ITEMS_TO_SPAWN)) itemsToSpawn.add((Item) l);

        prizeItemsToSpawn = new ArrayList<>();
        if (bundle.contains(PRIZE_ITEMS_TO_SPAWN))
            for (Bundlable l : bundle.getCollection(PRIZE_ITEMS_TO_SPAWN))
                prizeItemsToSpawn.add((Item) l);

        roomsToSpawn = new ArrayList<>();
        if (bundle.contains(ROOMS_TO_SPAWN))
            for (Bundlable l : bundle.getCollection(ROOMS_TO_SPAWN)) roomsToSpawn.add((Room) l);

        spawnStandardRooms = bundle.getBoolean(SPAWN_STANDART_ROOMS);
        spawnSecretRooms = bundle.getBoolean(SPAWN_SECRET_ROOMS);
        spawnSpecialRooms = bundle.getBoolean(SPAWN_SPECIAL_ROOMS);
        spawnMobs = bundle.getBoolean(SPAWN_MOBS);
        spawnItems = bundle.getBoolean(SPAWN_ITEMS);
        builder = (Builder) bundle.get(BUILDER);
        if (bundle.contains(NATURAL_REGEN_SPEED)) {
            if (bundle.contains("natural_regen")) naturalRegenSpeed = bundle.getBoolean("natural_regen") ? 1f: 0f;
            else naturalRegenSpeed = bundle.getFloat(NATURAL_REGEN_SPEED);
            if (bundle.contains("hunger_depletion")) hungerSpeed = bundle.getBoolean("hunger_depletion") ? 1f: 0f;
            else hungerSpeed = bundle.getFloat(HUNGER_SPEED);
        }

        if (bundle.contains("lua_script")) {
            LuaLevelScript lco = CustomObjectManager.createNewCustomObject(LuaLevelScript.class, "", getType().getName());
            lco.setLuaScriptPath(bundle.getBundle("lua_script").getString("path_to_script"));
            luaScriptID = lco.getIdentifier();
        }
        else luaScriptID = bundle.getInt(SCRIPT_ID);
    }

    @NotAllowedInLua
    public Point getSizeIfUnloaded() {
        boolean unloadLevel = level == null;
        if (unloadLevel) loadLevel();
        if (level == null) return new Point(-1, -1);//For non CustomLevels
        Point ret = new Point(level.width(), level.height());
        if (unloadLevel) {
            unloadLevel();
            EditorScene.updatePathfinder();
        }
        return ret;
    }

    public Exception levelLoadingException;

    @NotAllowedInLua
    public Level loadLevel() {
        return loadLevel(true);
    }

    @NotAllowedInLua
    public Level loadLevel(boolean removeInvalidTransitions) {
        if (type == CustomLevel.class) {
            try {
                level = CustomDungeonSaves.loadLevel(name, removeInvalidTransitions, 0);
                level.setLevelScheme(this);

                if (((CustomLevel) level).dataTransferToLevelScheme != null) {
                    ((CustomLevel) level).dataTransferToLevelScheme.apply(this);
                    ((CustomLevel) level).dataTransferToLevelScheme = null;
                }

                levelLoadingException = null;
            } catch (GdxRuntimeException gdxEx) {
                levelLoadingException = gdxEx.getCause() instanceof IOException ? (Exception) gdxEx.getCause() : gdxEx;
                SandboxPixelDungeon.reportException(levelLoadingException);
            } catch (IOException e) {
                levelLoadingException = e;
                SandboxPixelDungeon.reportException(e);
            } catch (CustomDungeonSaves.RenameRequiredException ex) {
                ex.showExceptionWindow();
                levelLoadingException = ex;
            }
        }
        return level;
    }

    @NotAllowedInLua
    public void saveLevel() throws IOException {
        if (type == CustomLevel.class) CustomDungeonSaves.saveLevel(level);
    }

    @NotAllowedInLua
    public void unloadLevel() {
        level = null;
    }


    public static final int REGION_NONE = 0, REGION_SEWERS = 1, REGION_PRISON = 2, REGION_CAVES = 3, REGION_CITY = 4, REGION_HALLS = 5;

    public static int getRegion(Level level) {
        if (Dungeon.branch != 0) {
            return QuestLevels.getRegion(Dungeon.branch);
        }
        return level == null ? REGION_NONE : level.getRegionValue();
    }

    public static int getRegion(Class<? extends Level> level) {
        if (SewerLevel.class.isAssignableFrom(level) || SewerBossLevel.class.isAssignableFrom(level)) return REGION_SEWERS;
        if (PrisonLevel.class.isAssignableFrom(level) || PrisonBossLevel.class.isAssignableFrom(level)) return REGION_PRISON;
        if (CavesLevel.class.isAssignableFrom(level) || CavesBossLevel.class.isAssignableFrom(level) || MiningLevel.class.isAssignableFrom(level)) return REGION_CAVES;
        if (CityLevel.class.isAssignableFrom(level) || CityBossLevel.class.isAssignableFrom(level)) return REGION_CITY;
        if (HallsLevel.class.isAssignableFrom(level) || HallsBossLevel.class.isAssignableFrom(level)
                || LastLevel.class.isAssignableFrom(level) || DeadEndLevel.class.isAssignableFrom(level))
            return REGION_HALLS;
        return REGION_NONE;
    }

    public static int getBoss(Class<? extends Level> level) {
        if (SewerBossLevel.class.isAssignableFrom(level)) return REGION_SEWERS;
        if (PrisonBossLevel.class.isAssignableFrom(level)) return REGION_PRISON;
        if (CavesBossLevel.class.isAssignableFrom(level)) return REGION_CAVES;
        if (CityBossLevel.class.isAssignableFrom(level)) return REGION_CITY;
        if (HallsBossLevel.class.isAssignableFrom(level)) return REGION_HALLS;
        return REGION_NONE;
    }

    public final int getRegion() {
        if (Dungeon.branch == QuestLevels.MINING.ID) return REGION_CAVES;
        if (type == null) return LevelScheme.REGION_NONE;
        if (CustomLevel.class.isAssignableFrom(type)) return region;
        return getRegion(type);
    }

    public final int getVisualRegion() {
        if (Dungeon.branch == QuestLevels.MINING.ID) return REGION_CAVES;
        return region;
    }

    public final void setRegion(int region) {
        this.region = region;
        if (level != null) level.initRegionColors();
    }

    public final int getBoss() {
        return getBoss(level == null ? type : level.getClass());
    }

    public int getNumInRegion() {//if it is the first, second, 3rd etc floor of a region (only good for setting default playing)
        return numInRegion;
    }

    @Override
    public int compareTo(LevelScheme o) {
        if (getDepth() != o.getDepth())
            return (getDepth() - o.getDepth()) * 1000;//TODO might wanna use more complex sorting, like used for ItemTab
        return getName().compareTo(o.getName());
    }

    //Depth nur für Scaling, Region und numInRegionFür LevelGen
}
