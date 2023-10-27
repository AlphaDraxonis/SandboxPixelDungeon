package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CAVES;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CITY;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_HALLS;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_NONE;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_PRISON;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_SEWERS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ENTRANCE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WALL;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomLevel extends Level {


    private static final Map<String, TextureFilm> textureFilms = new HashMap<>();

    int region;
    private int waterTexture = REGION_NONE;
    private int music = REGION_NONE;

    {
        setRegion(REGION_SEWERS);
    }

    private boolean enableRespawning = true;
    private float respawnCooldown = TIME_TO_RESPAWN;//How often new mobs spawn
    //    private boolean fillRemainingMobsWhenCreated = false;//if createMobs() didnt reach the mob cap, this spawns new mobs using mobRotation
    private boolean swapForMutations = true;//Chance of changing eg a rat to an albino rat
    private List<Class<? extends Mob>> mobRotation = new ArrayList<>();//More of same mob means higher chance,
    private int mobLimit = 10;

    //    private SparseArray<Heap> startHeaps = new SparseArray<>();
    private int[] terrains = {//Template for new Floors
            WALL, WALL, WALL, WALL, WALL,
            WALL, EMPTY, EXIT, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, WALL,
            WALL, EMPTY, ENTRANCE, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL
    };
    private boolean ignoreTerrainForExploringScore = false;//ignores Barricades, Locked doors and secret doors in isFullyExplored()
    //Achtung: direkt auf map[] arbeiten!

    public boolean[] isRoom;//variable of cell to differantiate between room and hallway, used for spawning different stuff or traps TODO unused!
    public boolean[] canSpawnThings;//variable of cell  TODO unused

    private Painter decorationPainter;//used for decoration and placing water and grass, not for painting rooms; usually one of the 5 default painters, or null


    public static class ItemWithPos {
        private Item item;
        private int pos;

        public ItemWithPos(Item item, int pos) {
            this.item = item;
            this.pos = pos;
        }

        public ItemWithPos(Item item, Heap heap) {
            this.item = item;
            this.pos = heap.pos;
        }

        public int pos() {
            return pos;
        }

        public Item item() {
            return item;
        }
    }

    public CustomLevel() {
    }

    public static String tempDungeonNameForKey = null;

    //avg max map size is 85x85 (v2.0.2)
    public CustomLevel(String name, Class<? extends Level> levelTemplate, Level.Feeling feeling, Long seed, int numInRegion, int depth, LevelScheme levelScheme) {
        super();
        this.name = name;
        this.levelScheme = levelScheme;

        Dungeon.depth = depth;
        if (seed == null) seed = DungeonSeed.randomSeed();

        if (levelTemplate != null) {

            LevelScheme temp = new LevelScheme(null, numInRegion);
            temp.setSeed(seed);
            temp.roomsToSpawn = levelScheme.roomsToSpawn;
            temp.spawnStandartRooms = levelScheme.spawnStandartRooms;
            temp.spawnSpecialRooms = levelScheme.spawnSpecialRooms;
            temp.spawnSecretRooms = levelScheme.spawnSecretRooms;
            temp.spawnMobs = levelScheme.spawnMobs;
            temp.spawnItems = levelScheme.spawnItems;

            temp.mobsToSpawn = new ArrayList<>(levelScheme.mobsToSpawn);
            levelScheme.mobsToSpawn.clear();
            temp.itemsToSpawn = new ArrayList<>(levelScheme.itemsToSpawn);
            temp.itemsToSpawn.addAll(levelScheme.prizeItemsToSpawn);
            levelScheme.itemsToSpawn.clear();
            levelScheme.prizeItemsToSpawn.clear();

            temp.setDepth(depth);

            Dungeon.customDungeon.addFloor(temp);
            Dungeon.levelName = Level.NONE;

            tempDungeonNameForKey = name;
            Random.pushGenerator(seed + 1);
            SpecialRoom.initForRun();
            SecretRoom.initForRun();
            Random.popGenerator();

            Dungeon.seed = seed + 12253;
            Level level = Reflection.newInstance(levelTemplate);
            temp.setLevel(level);
            level.name = Level.NONE;
            level.feeling = feeling;
            Dungeon.level = level;

            Random.pushGenerator(seed);
            for (Mob m : temp.mobsToSpawn) {
                if (m instanceof Wandmaker && ((Wandmaker) m).quest.spawnQuestRoom)
                    ((Wandmaker) m).initQuest(temp);
            }
            Random.popGenerator();

            Dungeon.hero = new Hero();//Dried rose for example checks hero
            level.create();
            Dungeon.hero = null;
            tempDungeonNameForKey = null;

            Random.pushGenerator(seed + 229203);
            for (Item item : temp.itemsToSpawn) {
                int cell;
                if (level instanceof CustomLevel) cell = ((CustomLevel) level).randomDropCell();
                else cell = ((RegularLevel) level).randomDropCell();
                if (level.map[cell] == Terrain.HIGH_GRASS || level.map[cell] == Terrain.FURROWED_GRASS) {
                    level.map[cell] = Terrain.GRASS;
                    level.losBlocking[cell] = false;
                }
                level.drop(item, cell).type = Heap.Type.HEAP;
            }
            Random.popGenerator();

            setSize(level.width(), level.height());
//                maxW = Math.max(maxW, level.width());
//                maxH = Math.max(maxH, level.height());
            map = level.map;

            for (int i = 0; i < map.length; i++) {
                if (map[i] == ENTRANCE) levelScheme.entranceCells.add(i);
                else if (TileItem.isExitTerrainCell(map[i])) levelScheme.exitCells.add(i);
            }
            Collections.sort(levelScheme.entranceCells);
            Collections.sort(levelScheme.exitCells);

            this.feeling = level.feeling;
            levelScheme.setFeeling(this.feeling);
            mobs = level.mobs;
            heaps = level.heaps;
            viewDistance = level.viewDistance;
            version = level.version;
            color1 = level.color1;
            color2 = level.color2;
            region = temp.getRegion();
            visited = level.visited;
            mapped = level.mapped;
            discoverable = level.discoverable;
            heroFOV = level.heroFOV;
            passable = level.passable;
            losBlocking = level.losBlocking;
            flamable = level.flamable;
            secret = level.secret;
            solid = level.solid;
            avoid = level.avoid;
            water = level.water;
            pit = level.pit;
            openSpace = level.openSpace;
            locked = level.locked;
            if (levelTemplate != LastLevel.class && levelTemplate != DeadEndLevel.class)
                mobRotation = level.getMobRotation();


            addVisuals();

            transitions = level.transitions;
            plants = level.plants;
            traps = level.traps;
            signs = level.signs;
            customTiles = level.customTiles;
            customWalls = level.customWalls;
            blobs = level.blobs;

//           TODO  respawner!!

            Dungeon.customDungeon.removeFloor(temp);
            Dungeon.levelName = name;


            for (LevelTransition t : transitions.values()) {
                if (Level.NONE.equals(t.destLevel)) t.destLevel = name;
                if (t.destCell == TransitionEditPart.DEFAULT && t.type == LevelTransition.Type.REGULAR_ENTRANCE) {
                    LevelScheme destLevelScheme = Dungeon.customDungeon.getFloor(t.destLevel);
                    if (destLevelScheme != null){
                        int size = destLevelScheme.exitCells.size();
                        if (size > 0) {
                            int destCell = destLevelScheme.exitCells.get(0);
                            if (size > 1 && destCell == TransitionEditPart.NONE) destCell = destLevelScheme.exitCells.get(1);
                            if (destCell != TransitionEditPart.NONE) t.destCell = destCell;
                        }
                    }
                }
            }

            for (Mob m : mobs) {
                if (m instanceof SentryRoom.Sentry) ((SentryRoom.Sentry) m).room = null;
            }

//            changeMapSize(this, 85, 85);
        } else {
            this.feeling = feeling;
            levelScheme.setFeeling(this.feeling);
        }

    }

//    public CustomLevel(CustomLevel customLevel) {
//        width = customLevel.width;
//        height = customLevel.height;
//        setSize(width, height);
//        terrains = new int[length];
//
//        region = customLevel.region;
//        waterTexture = customLevel.waterTexture;
//        music = customLevel.music;
//        enableRespawning = customLevel.enableRespawning;
//        respawnCooldown = customLevel.respawnCooldown;
//        swapForMutations = customLevel.swapForMutations;
//        mobLimit = customLevel.mobLimit;
//        viewDistance = customLevel.viewDistance;
//        System.arraycopy(customLevel.map, 0, terrains, 0, customLevel.map.length);
//        ignoreTerrainForExploringScore = customLevel.ignoreTerrainForExploringScore;
//        mobRotation = customLevel.mobRotation;
//        feeling = customLevel.feeling;
//        System.arraycopy(terrains, 0, map, 0, terrains.length);
//
//        transitions = new HashMap<>();
//        for (LevelTransition trans : customLevel.transitions.values()) {
//            transitions.put(trans.departCell, trans.getCopy());
//        }
//
//        mobs = new HashSet<>();
//        for (Mob m : customLevel.mobs) {
//            Mob clone = (Mob) m.getCopy();
//            if (clone instanceof Mimic) ((Mimic) clone).setLevel(Dungeon.depth);
//            mobs.add(clone);
//        }
//
//        heaps = new SparseArray<>();
//        for (Heap h : customLevel.heaps.valueList()) {
//            Heap nh = h.getCopy();
//            heaps.put(h.pos, nh);
//            for (Item item : nh.items) item.reset();//important for scroll runes being inited
//        }
//
//        blobs = new HashMap<>();
//        plants = new SparseArray<>();
//        for (Plant plant : customLevel.plants.valueList()) {
//            plants.put(plant.pos, plant.getCopy());
//        }
//
//        traps = new SparseArray<>();
//        for (Trap trap : customLevel.traps.valueList()) {
//            traps.put(trap.pos, trap.getCopy());
//        }
//
//        signs = new SparseArray<>();
//        for (Sign sign : customLevel.signs.valueList()) {
//            signs.put(sign.pos, sign.getCopy());
//        }
//
//        customTiles = new HashSet<>();
//        customWalls = new HashSet<>();
//
//        buildFlagMaps();
//        cleanWalls();
//
//        createItems();
//    }

    @Override
    public String tilesTex() {
        switch (region) {
            case REGION_PRISON:
                return Assets.Environment.TILES_PRISON;
            case REGION_CAVES:
                return Assets.Environment.TILES_CAVES;
            case REGION_CITY:
                return Assets.Environment.TILES_CITY;
            case REGION_HALLS:
                return Assets.Environment.TILES_HALLS;

            default:
                return Assets.Environment.TILES_SEWERS;
        }
    }

    @Override
    public String waterTex() {
        int w = waterTexture;
        if (w == REGION_NONE) w = region;
        switch (w) {
            case REGION_PRISON:
                return Assets.Environment.WATER_PRISON;
            case REGION_CAVES:
                return Assets.Environment.WATER_CAVES;
            case REGION_CITY:
                return Assets.Environment.WATER_CITY;
            case REGION_HALLS:
                return Assets.Environment.WATER_HALLS;

            default:
                return Assets.Environment.WATER_SEWERS;
        }
    }

    public int getRegionValue() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
        switch (region) {
            //region colors and music are hardcoded in their region level...; not gonna create a new level just to read the color values
            case REGION_PRISON:
                color1 = 0x6a723d;
                color2 = 0x88924c;
                break;
            case REGION_CAVES:
                color1 = 0x534f3e;
                color2 = 0xb9d661;
                break;
            case REGION_CITY:
                color1 = 0x4b6636;
                color2 = 0xf2f2f2;
                break;
            case REGION_HALLS:
                color1 = 0x801500;
                color2 = 0xa68521;
                break;

            default:
                color1 = 0x48763c;
                color2 = 0x59994a;
                break;
        }
    }

    public void setWaterTexture(int waterTexture) {
        this.waterTexture = waterTexture;
    }

    public int getWaterTextureValue() {
        return waterTexture;
    }

    public void setMusic(int music) {
        this.music = music;
    }

    public int getMusicValue() {
        return music;
    }

    @Override
    public void playLevelMusic() {
        playLevelMusic(music == REGION_NONE ? region : music);
    }

    @Override
    public Group addVisuals() {
        Group g = super.addVisuals();
        switch (region) {
            case REGION_SEWERS:
                SewerLevel.addSewerVisuals(this, g);
                break;
            case REGION_PRISON:
                PrisonLevel.addPrisonVisuals(this, g);
                break;
            case REGION_CAVES:
                CavesLevel.addCavesVisuals(this, g);
                break;
            case REGION_CITY:
                CityLevel.addCityVisuals(this, g);
                break;
            case REGION_HALLS:
                HallsLevel.addHallsVisuals(this, g);
                break;

            default:
                break;
        }
        if (waterTexture == REGION_HALLS && region != REGION_HALLS) {
            HallsLevel.addHallsVisuals(this, g);//(only) HallsLevel adds WaterVisuals
        }
        return g;
    }

    @Override
    protected boolean build() {

        int w = 25, h = 25;
        final int oldW = 5, oldH = 5;
        setSize(w, h);
        int[] newTerrain = new int[oldH * w];
        Arrays.fill(newTerrain, WALL);
        changeArrayForMapSizeHeight(terrains, newTerrain, (h - oldH) / 2 * oldW, oldW, oldW);
        changeArrayForMapSizeWidth(newTerrain, map, (w - oldW) / 2, oldW, w);
        updateTransitionCells();

        return true;
    }


    protected void updateTransitionCells() {
        levelScheme.entranceCells.clear();
        levelScheme.exitCells.clear();
        for (int i = 0; i < map.length; i++) {
            int terrain = map[i];
            if (terrain == ENTRANCE) {
                levelScheme.entranceCells.add(i);
                String dest = Dungeon.customDungeon.getFloor(Dungeon.levelName).getDefaultAbove();
                if (Level.SURFACE.equals(dest)) {
                    transitions.put(i, new LevelTransition(this, i, LevelTransition.Type.SURFACE));
                } else {
                    LevelScheme destLevelScheme = Dungeon.customDungeon.getFloor(dest);
                    if (destLevelScheme != null){
                        int size = destLevelScheme.exitCells.size();
                        if (size > 0) {
                            int destCell = destLevelScheme.exitCells.get(0);
                            if (size > 1 && destCell == TransitionEditPart.NONE) destCell = destLevelScheme.exitCells.get(1);
                            if (destCell != TransitionEditPart.NONE){
                                transitions.put(i, new LevelTransition(this, i, destCell, dest));
                            }
                        }
                    }
                }
            }
            else if (TileItem.isExitTerrainCell(terrain)) levelScheme.exitCells.add(i);
        }
        Collections.sort(levelScheme.entranceCells);
        Collections.sort(levelScheme.exitCells);
    }

    @Override
    public ArrayList<Class<? extends Mob>> getMobRotation() {
        return Bestiary.getMobRotation(mobRotation, swapForMutations);
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public int mobLimit() {
        return mobLimit;
    }

    @Override
    public boolean spawnMob(int disLimit) {
        if (enableRespawning && !getMobRotation().isEmpty()) return super.spawnMob(disLimit);
        return false;
    }

    @Override
    protected void createItems() {
    }

    protected int randomDropCell() {
        int tries = length;
        int lengthHalf = length / 2;
        while (tries-- > 0) {
            int pos = Random.Int(length());
            if (passable[pos] && !solid[pos] && canSpawnThings[pos]
                    && map[pos] != ENTRANCE
                    && map[pos] != EXIT
                    && (tries <= lengthHalf || (heaps.get(pos) == null && findMob(pos) == null))) {

                Trap t = traps.get(pos);

                //items cannot spawn on traps which destroy items
                if (t == null ||
                        !(t instanceof BurningTrap || t instanceof BlazingTrap
                                || t instanceof ChillingTrap || t instanceof FrostTrap
                                || t instanceof ExplosiveTrap || t instanceof DisintegrationTrap
                                || t instanceof PitfallTrap)) {
                    return pos;
                }
            }
        }
        return -1;
    }

    public static int randomDropCell(Level level) {
        int tries = level.length();
        int lengthHalf = level.length() / 2;
        while (tries-- > 0) {
            int pos = Random.Int(level.length());
            if (level.passable[pos] && !level.solid[pos] /*&& canSpawnThings[pos]*/
                    && level.map[pos] != ENTRANCE
                    && level.map[pos] != EXIT
                    && (tries <= lengthHalf || (level.heaps.get(pos) == null && level.findMob(pos) == null))) {

                Trap t = level.traps.get(pos);

                //items cannot spawn on traps which destroy items
                if (!(t instanceof BurningTrap || t instanceof BlazingTrap
                        || t instanceof ChillingTrap || t instanceof FrostTrap
                        || t instanceof ExplosiveTrap || t instanceof DisintegrationTrap
                        || t instanceof PitfallTrap)) {
                    return pos;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean isLevelExplored(String levelName) {
        //From RegularLevel

        //A level is considered fully explored if:

        //There are no levelgen heaps which are undiscovered, in an openable container, or which contain keys
        for (Heap h : heaps.valueList()) {
            if (h.autoExplored) continue;
            if (!h.seen || (h.type != Heap.Type.HEAP && h.type != Heap.Type.FOR_SALE && h.type != Heap.Type.CRYSTAL_CHEST)) {
                return false;
            }
            for (Item i : h.items) {
                if (i instanceof Key) return false;
            }
        }
        //There is no magical fire or sacrificial fire
        for (Blob b : blobs.values()) {
            if (b.volume > 0 && (b instanceof MagicalFireRoom.EternalFire || b instanceof SacrificialFire))
                return false;
        }
        //There are no statues or mimics (unless they were made allies)
        for (Mob m : mobs.toArray(new Mob[0])) {
            if (m.alignment != Char.Alignment.ALLY) {
                if (m instanceof Statue && ((Statue) m).levelGenStatue) return false;
                if (m instanceof Mimic) return false;
            }
        }
        if (!ignoreTerrainForExploringScore) {
            //There are no barricades, locked doors, or hidden doors
            for (int i = 0; i < length; i++) {
                if (map[i] == Terrain.BARRICADE || map[i] == Terrain.LOCKED_DOOR || map[i] == Terrain.SECRET_DOOR) {
                    return false;
                }
            }
        }
        //Removed journal keys here...

        return true;
    }


    @Override
    public float respawnCooldown() {
        return respawnCooldown;
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        isRoom = new boolean[passable.length];
        canSpawnThings = new boolean[passable.length];
    }

    @Override
    public void buildFlagMaps() {
        super.buildFlagMaps();

        for (int i = 0; i < length(); i++) {
            isRoom[i] = true;
            canSpawnThings[i] = true;
        }
    }

    public List<ItemWithPos> getItems() {
        List<ItemWithPos> ret = new ArrayList<>();
        for (Heap h : heaps.valueList()) {
            for (Item i : h.items) {
                ret.add(new ItemWithPos(i, h.pos));
            }
        }
        return ret;
    }

    private static final String REGION = "region";
    private static final String WATER_TEXTUTE = "water_texture";
    private static final String MUSIC = "music";
    private static final String ENABLE_RESPAWNING = "enable_respawning";
    private static final String RESPAWN_COOLDOWN = "respawn_cooldown";
    private static final String SWAP_FOR_MUTATIONS = "swap_for_mutations";
    private static final String MOB_LIMIT = "mob_limit";
    private static final String MOB_ROTATION = "mob_rotation";
    private static final String IGNORE_TERRAIN_FOR_EXPLORING_SCORE = "ignore_terrain_for_exploring_score";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(REGION, region);
        bundle.put(WATER_TEXTUTE, waterTexture);
        bundle.put(MUSIC, music);
        bundle.put(ENABLE_RESPAWNING, enableRespawning);
        bundle.put(RESPAWN_COOLDOWN, respawnCooldown);
        bundle.put(SWAP_FOR_MUTATIONS, swapForMutations);
        bundle.put(MOB_LIMIT, mobLimit);
        bundle.put(MOB_ROTATION, mobRotation.toArray(new Class[0]));
        bundle.put(IGNORE_TERRAIN_FOR_EXPLORING_SCORE, ignoreTerrainForExploringScore);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        region = bundle.getInt(REGION);
        waterTexture = bundle.getInt(WATER_TEXTUTE);
        music = bundle.getInt(MUSIC);
        enableRespawning = bundle.getBoolean(ENABLE_RESPAWNING);
        respawnCooldown = bundle.getInt(RESPAWN_COOLDOWN);
        swapForMutations = bundle.getBoolean(SWAP_FOR_MUTATIONS);
        mobLimit = bundle.getInt(MOB_LIMIT);
        mobRotation = Arrays.asList(bundle.getClassArray(MOB_ROTATION));
        mobRotation = new ArrayList<>(mobRotation);
        ignoreTerrainForExploringScore = bundle.getBoolean(IGNORE_TERRAIN_FOR_EXPLORING_SCORE);

        for (Mob m : mobs) {
            m.clearTime();//Fix wrong time caused by v0.7
        }
    }
    //----------------------

    public boolean isRespawEnabled() {
        return enableRespawning;
    }

    public void enableRespawning(boolean enableRespawning) {
        this.enableRespawning = enableRespawning;
    }


    public boolean isSwapForMutations() {
        return swapForMutations;
    }

    public void setSwapForMutations(boolean swapForMutations) {
        this.swapForMutations = swapForMutations;
    }

    public void setMobLimit(int mobLimit) {
        this.mobLimit = mobLimit;
    }

    public void setRespawnCooldown(float respawnCooldown) {
        this.respawnCooldown = respawnCooldown;
    }

    public List<Class<? extends Mob>> getMobRotationVar() {
        return mobRotation;
    }


    public static TextureFilm getTextureFilm(String theme) {
        TextureFilm tf = textureFilms.get(theme);
        if (tf == null) {
            tf = new TextureFilm(theme, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            textureFilms.put(theme, tf);
        }
        return tf;
    }

    public boolean isBorder(int cell) {
        int row = cell / width;
        int col = cell % width;
        return (row == 0 || row == height - 1 || col == 0 || col == width - 1);
    }


//    public CustomLevel createCopiedFloor() {
//        return new CustomLevel(this);
//    }


    public static void changeMapSize(Level level, int newWidth, int newHeight) {
        int diffH = newHeight - level.height();
        int diffW = newWidth - level.width();
//        int newLenght = newWidth * newHeight;

        int addLeft = diffW / 2;
        int addTop = diffH / 2;
        changeMapSize(level, newWidth, newHeight, addTop, addLeft);
    }

    public static void changeMapSize(Level level, int newWidth, int newHeight, int addTop, int addLeft) {
        if (level.width() == newWidth && level.height() == newHeight) return;

        if (newHeight != level.height()) changeMapHeight(level, newHeight, addTop);
        if (newWidth != level.width()) changeMapWidth(level, newWidth, addLeft);

        PathFinder.setMapSize(newWidth, newHeight);

        level.buildFlagMaps();
    }

    private static void changeMapHeight(Level level, int newHeight, int addTop) {

        int width = level.width();
        int newLength = width * newHeight;
        int add = addTop * width;

        int[] oldMap = level.map;
        boolean[] oldVisited = level.visited;
        boolean[] oldMapped = level.mapped;

        int levelWidth = level.width();
        level.setSize(width, newHeight);

        boolean[] nDiscoverable = new boolean[newLength];

        changeArrayForMapSizeHeight(oldMap, level.map, add, levelWidth, width);
        changeArrayForMapSizeHeight(oldVisited, level.visited, add, levelWidth, width);
        changeArrayForMapSizeHeight(oldMapped, level.mapped, add, levelWidth, width);
        changeArrayForMapSizeHeight(level.discoverable, nDiscoverable, add, levelWidth, width);

        level.discoverable = nDiscoverable;

        Set<CustomTilemap> removeCustomTiles = new HashSet<>(5);
        for (CustomTilemap customTile : level.customTiles) {
            customTile.tileY += addTop;
            if (customTile.tileY < 0 || customTile.tileY + customTile.tileH > newHeight)
                removeCustomTiles.add(customTile);
        }
        level.customTiles.removeAll(removeCustomTiles);
        removeCustomTiles.clear();
        for (CustomTilemap customTile : level.customWalls) {
            customTile.tileY += addTop;
            if (customTile.tileY < 0 || customTile.tileY + customTile.tileH > newHeight)
                removeCustomTiles.add(customTile);
        }
        level.customWalls.removeAll(removeCustomTiles);

        IntFunction<Integer> newPosition = old -> old + add;
        BiPredicate<Integer, Integer> isPositionValid = (old, neu) -> neu >= 0 && neu < newLength && level.insideMap(neu);

        recalculateNewPositions(newPosition, isPositionValid, level, newLength);
    }

    private static void changeMapWidth(Level level, int newWidth, int addLeft) {

        int diffW = newWidth - level.width();
        int height = level.height();
        int newLength = height * newWidth;

        int[] oldMap = level.map;
        boolean[] oldVisited = level.visited;
        boolean[] oldMapped = level.mapped;

        int levelWidth = level.width();
        level.setSize(newWidth, height);

        boolean[] nDiscoverable = new boolean[newLength];

        changeArrayForMapSizeWidth(oldMap, level.map, addLeft, levelWidth, newWidth);
        changeArrayForMapSizeWidth(oldVisited, level.visited, addLeft, levelWidth, newWidth);
        changeArrayForMapSizeWidth(oldMapped, level.mapped, addLeft, levelWidth, newWidth);
        changeArrayForMapSizeWidth(level.discoverable, nDiscoverable, addLeft, levelWidth, newWidth);

        level.discoverable = nDiscoverable;

        Set<CustomTilemap> removeCustomTiles = new HashSet<>(5);
        for (CustomTilemap customTile : level.customTiles) {
            customTile.tileX += addLeft;
            if (customTile.tileX < 0 || customTile.tileX + customTile.tileW > newWidth)
                removeCustomTiles.add(customTile);
        }
        level.customTiles.removeAll(removeCustomTiles);
        removeCustomTiles.clear();
        for (CustomTilemap customTile : level.customWalls) {
            customTile.tileX += addLeft;
            if (customTile.tileX < 0 || customTile.tileX + customTile.tileW > newWidth)
                removeCustomTiles.add(customTile);
        }
        level.customWalls.removeAll(removeCustomTiles);

        IntFunction<Integer> newPosition = old -> old + addLeft + diffW * (old / levelWidth);
        BiPredicate<Integer, Integer> isPositionValid = (old, neu) -> neu >= 0 && neu < newLength && level.insideMap(neu)
                && old / levelWidth == neu / newWidth;

        recalculateNewPositions(newPosition, isPositionValid, level, newLength);
    }

    private static void recalculateNewPositions(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid, Level level, int newLength) {
        List<Mob> removeEntities = new ArrayList<>();
        for (Mob m : level.mobs) {
            int nPos = newPosition.get(m.pos);
            if (!isPositionValid.test(m.pos, nPos)) removeEntities.add(m);
            else m.pos = nPos;
        }
        level.mobs.removeAll(removeEntities);
        removeEntities.clear();

        if (level.bossmobAt != -1) {
            int old = level.bossmobAt;
            level.bossmobAt = newPosition.get(old);
            if (!isPositionValid.test(old, level.bossmobAt)) level.bossmobAt = Level.NO_BOSS_MOB;
        }

        //Cant avoid some copy paste because Shattered has really good code
        SparseArray<Heap> nHeaps = new SparseArray<>();
        for (Heap h : level.heaps.valueList()) {
            int nPos = newPosition.get(h.pos);
            if (isPositionValid.test(h.pos, nPos)) {
                nHeaps.put(nPos, h);
                h.pos = nPos;
            }
        }
        level.heaps.clear();
        level.heaps.putAll(nHeaps);

        SparseArray<Trap> nTrap = new SparseArray<>();
        for (Trap t : level.traps.valueList()) {
            int nPos = newPosition.get(t.pos);
            if (isPositionValid.test(t.pos, nPos)) {
                nTrap.put(nPos, t);
                t.pos = nPos;
            }
        }
        level.traps.clear();
        level.traps.putAll(nTrap);

        SparseArray<Sign> nSign = new SparseArray<>();
        for (Sign s : level.signs.valueList()) {
            int nPos = newPosition.get(s.pos);
            if (isPositionValid.test(s.pos, nPos)) {
                nSign.put(nPos, s);
                s.pos = nPos;
            }
        }
        level.signs.clear();
        level.signs.putAll(nSign);

        SparseArray<Plant> nPlant = new SparseArray<>();
        for (Plant p : level.plants.valueList()) {
            if (p != null) {
                int nPos = newPosition.get(p.pos);
                if (isPositionValid.test(p.pos, nPos)) {
                    nPlant.put(nPos, p);
                    p.pos = nPos;
                }
            }
        }
        level.plants.clear();
        level.plants.putAll(nPlant);

        for (Blob b : level.blobs.values()) {
            if (b != null) {
                int[] nCur = new int[newLength];
                b.volume = 0;
                if (b.cur != null) {
                    for (int i = 0; i < b.cur.length; i++) {
                        int newIndex = newPosition.get(i);
                        if (isPositionValid.test(i, newIndex)) {
                            b.volume += b.cur[i];
                            nCur[newIndex] = b.cur[i];
                        }
                    }
                }
                b.cur = nCur;
                b.changeSizeOfOffToNewMapSizeAndClearIt(newLength);
                b.setupArea();
            }
        }

        List<Integer> cells = new ArrayList<>(level.levelScheme.entranceCells);
        level.levelScheme.entranceCells.clear();
        for (int cell : cells) {
            int pos = newPosition.get(cell);
            if (isPositionValid.test(cell, pos)) level.levelScheme.entranceCells.add(pos);
        }
        Collections.sort(level.levelScheme.entranceCells);

        cells = new ArrayList<>(level.levelScheme.exitCells);
        level.levelScheme.exitCells.clear();
        for (int cell : cells) {
            int pos = newPosition.get(cell);
            if (isPositionValid.test(cell, pos)) level.levelScheme.exitCells.add(pos);
        }
        Collections.sort(level.levelScheme.exitCells);

        //Check depart cells
        Map<Integer, LevelTransition> nTrans = new HashMap<>();
        for (LevelTransition transition : level.transitions.values()) {
            int posDepart = newPosition.get(transition.departCell);
            int posCenter = newPosition.get(transition.centerCell);
            //TODO consider the size of the transitions but atm they cant be set!
//            int left = newPosition.get(transition.left);
//            int top = newPosition.get(transition.top);
//            int right = newPosition.get(transition.right);
//            int bottom = newPosition.get(transition.bottom);
            if (isPositionValid.test(transition.departCell, posDepart)
                    && isPositionValid.test(transition.centerCell, posCenter)
//                    && isPositionValid.test(transition.left, left)
//                    && isPositionValid.test(transition.right, right)
//                    && isPositionValid.test(transition.top, top)
//                    && isPositionValid.test(transition.bottom, bottom)
            ) {
                transition.departCell = posDepart;
                transition.centerCell = posCenter;
                nTrans.put(transition.departCell, transition);
                Point p = level.cellToPoint(transition.departCell);
                transition.set(p.x, p.y, p.x, p.y);
//                transition.set(left, top, right, bottom);
            }
        }
        level.transitions.clear();
        level.transitions = nTrans;


        //Check destCells
        for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {

            if (ls.getType() == CustomLevel.class) {
                boolean load = ls.getLevel() == null;
                Level l;
                if (load) l = ls.loadLevel(false);
                else l = ls.getLevel();
                boolean changedSth = false;
                for (LevelTransition transition : l.transitions.values()) {
                    if (transition != null && Objects.equals(transition.destLevel, level.name)) {
                        int dest = newPosition.get(transition.destCell);
                        if (isPositionValid.test(transition.destCell, dest)) {
                            if (dest != transition.destCell) {
                                changedSth = true;
                                transition.destCell = dest;
                            }
                        } else {
                            changedSth = true;
                            l.transitions.remove(transition.departCell);
                        }
                    }
                }

                if (changedSth) {
                    try {
                        CustomDungeonSaves.saveLevel(l);
                    } catch (IOException e) {
                        //If saving is not successful, this will only result in some disappeared transitions, nothing to worry about.
                    }
                }
                if (load) ls.unloadLevel();
            } else {
                checkRegularLevelTransitions(ls.getEntranceTransitionRegular(), level.name, newPosition, isPositionValid);
                checkRegularLevelTransitions(ls.getExitTransitionRegular(), level.name, newPosition, isPositionValid);
            }
        }
    }

    private static void checkRegularLevelTransitions(LevelTransition transition, String levelWithChanges,
                                                     IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
        if (transition != null && Objects.equals(transition.destLevel, levelWithChanges)) {
            int dest = newPosition.get(transition.destCell);
            if (isPositionValid.test(transition.destCell, dest)) {
                if (dest != transition.destCell) transition.destCell = dest;
            } else {
                transition.destCell = -1;
                transition.destLevel = null;
            }
        }
    }

    //add must be multiplied with width before!!
    private static void changeArrayForMapSizeHeight(int[] src, int[] dest, int add, int levelWidth, int newWidth) {
        int diffW = newWidth - levelWidth;
        for (int i = 0; i < src.length; i++) {
            int index = i + add + i / levelWidth * diffW;
            if (index >= 0 && index < dest.length) dest[index] = src[i];
        }
    }

    private static void changeArrayForMapSizeWidth(int[] src, int[] dest, int add, int levelWidth, int newWidth) {
        int diffW = newWidth - levelWidth;
        for (int i = 0; i < src.length; i++) {
            int index = i + add + diffW * (i / levelWidth);
            if (index >= 0 && index < dest.length && i / levelWidth == index / newWidth)
                dest[index] = src[i];
        }
    }

    private static void changeArrayForMapSizeHeight(boolean[] src, boolean[] dest, int add, int levelWidth, int newWidth) {
        int diffW = newWidth - levelWidth;
        for (int i = 0; i < src.length; i++) {
            int index = i + add + i / levelWidth * diffW;
            if (index >= 0 && index < dest.length) dest[index] = src[i];
        }
    }

    private static void changeArrayForMapSizeWidth(boolean[] src, boolean[] dest, int add, int levelWidth, int newWidth) {
        int diffW = newWidth - levelWidth;
        for (int i = 0; i < src.length; i++) {
            int index = i + add + diffW * (i / levelWidth);
            if (index >= 0 && index < dest.length && i / levelWidth == index / newWidth)
                dest[index] = src[i];
        }
    }

}