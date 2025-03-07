package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Alchemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ChangeMapSizeActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
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
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.*;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.*;

public class CustomLevel extends Level {


    private static final Map<String, TextureFilm> textureFilms = new HashMap<>();

    public boolean enableRespawning = true;
    private float respawnCooldown = TIME_TO_RESPAWN;//How often new mobs spawn
    //    private boolean fillRemainingMobsWhenCreated = false;//if createMobs() didnt reach the mob cap, this spawns new mobs using mobRotation
    private ItemsWithChanceDistrComp.RandomItemData mobRotation = new ItemsWithChanceDistrComp.RandomItemData();//More of same mob means higher chance
    public int mobLimit = 10;

    //    private SparseArray<Heap> startHeaps = new SparseArray<>();
    private static final int[] TEMPLATE = {
            WALL, WALL, WALL, WALL, WALL,
            WALL, EMPTY, EXIT, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, WALL,
            WALL, EMPTY, ENTRANCE, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL
    };
    private boolean ignoreTerrainForExploringScore = false;//ignores Barricades, Locked doors and secret doors in isFullyExplored()
    //Achtung: direkt auf map[] arbeiten!

    public CustomTilemap bossGroundVisuals, bossWallsVisuals;

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
        setLevelScheme(levelScheme);
        this.name = name;

        Dungeon.depth = depth;
        if (seed == null) seed = DungeonSeed.randomSeed();

        if (levelTemplate != null) {
            Statistics.reset();

            LevelScheme temp = new LevelScheme(null, numInRegion);
            temp.setSeed(seed);
            temp.builder = levelScheme.builder;
            temp.roomsToSpawn = levelScheme.roomsToSpawn;
            temp.spawnStandardRooms = levelScheme.spawnStandardRooms;
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

            level.create();
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
            visualMap = level.visualMap;
            visualRegions = level.visualRegions;

            for (int i = 0; i < map.length; i++) {
                if (TileItem.isEntranceTerrainCell(map[i])) levelScheme.entranceCells.add(i);
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
            levelScheme.region = temp.getRegion();
            visited = level.visited;
            mapped = level.mapped;
            discoverable = level.discoverable;
            heroFOV = level.heroFOV;
//            passable = level.passable;
//            losBlocking = level.losBlocking;
//            setFlamable(level.getFlamable());
//            secret = level.secret;
//            solid = level.solid;
//            avoid = level.avoid;
//            water = level.water;
//            pit = level.pit;
//            openSpace = level.openSpace;
            lockedCount = level.lockedCount;
            respawnCooldown = level.respawnCooldown();
            if (level instanceof MiningLevel) levelScheme.allowPickaxeMining = true;

            transitions = level.transitions;
            plants = level.plants;
            traps = level.traps;
            signs = level.signs;
            barriers = level.barriers;
            arrowCells = level.arrowCells;
            checkpoints = level.checkpoints;
            coinDoors = level.coinDoors;
            blobs = level.blobs;
            particles = level.particles;
            customTiles = level.customTiles;
            customWalls = level.customWalls;
            assignBossCustomTiles();

            buildFlagMaps();
            zoneMap.clear();
            zoneMap.putAll(level.zoneMap);
            levelScheme.zones.addAll(zoneMap.keySet());
            Zone.setupZoneArray(this);
            if (LevelScheme.getBoss(levelTemplate) == REGION_NONE && levelTemplate != LastLevel.class && levelTemplate != DeadEndLevel.class)
                mobRotation = MobSpawner.getRotationForDepth(Dungeon.getSimulatedDepth(temp));


            addVisuals();

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

    private void assignBossCustomTiles() {
        if (customTiles != null) {
            for (CustomTilemap cust : customTiles) {
                if (cust instanceof CustomTilemap.BossLevelVisuals) {
                    bossGroundVisuals = cust;
                    break;
                }
            }
        }
        if (customWalls != null) {
            for (CustomTilemap cust : customWalls) {
                if (cust instanceof CustomTilemap.BossLevelVisuals) {
                    bossWallsVisuals = cust;
                    break;
                }
            }
        }
    }


    @Override
    public void initForPlay() {
        super.initForPlay();
        for (int i = 0; i < map.length; i++) {
            if (visualMap[i] == Terrain.ALCHEMY)
                Blob.seed( i, 1, Alchemy.class, this );
        }
    }

    @Override
    protected boolean build() {

        int w = 25, h = 25;
        final int oldW = 5, oldH = 5;
        setSize(w, h);
        int[] newTerrain = new int[oldH * w];
        Arrays.fill(newTerrain, WALL);
        ChangeMapSizeActionPart.changeArrayForMapSizeHeight(TEMPLATE, newTerrain, (h - oldH) / 2 * oldW, oldW, oldW);
        ChangeMapSizeActionPart.changeArrayForMapSizeWidth(newTerrain, map, (w - oldW) / 2, oldW, w);
        updateTransitionCells();

        return true;
    }


    protected void updateTransitionCells() {
        levelScheme.entranceCells.clear();
        levelScheme.exitCells.clear();
        for (int i = 0; i < map.length; i++) {
            int terrain = map[i];
            if (TileItem.isEntranceTerrainCell(terrain)) {
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
    public List<? extends Mob> getMobRotation() {
        return MobSpawner.getMobRotation(mobRotation);
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public float respawnCooldown() {
        return respawnCooldown / DimensionalSundial.spawnMultiplierAtCurrentTime();
    }
    public void respawnCooldown(float cd) {
        respawnCooldown = cd;
    }
    @Override
    public int mobLimit() {
        return mobLimit;
    }
    public ItemsWithChanceDistrComp.RandomItemData getMobRotationVar() {
        return mobRotation;
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
        return randomDropCell(this);
    }

    public static int randomDropCell(Level level) {
        int tries = level.length();
        int lengthHalf = level.length() / 2;
        while (tries-- > 0) {
            int pos = Random.Int(level.length());
            if (level.isPassableHero(pos) && !level.solid[pos]
                    && !TileItem.isEntranceTerrainCell(level.map[pos])
                    && !TileItem.isExitTerrainCell(level.map[pos])
                    && Zone.canSpawnItems(level, pos)
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
        return lengthHalf;//-1 would throw IndexOutOfBoundsException without any check!
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
                if (map[i] == Terrain.BARRICADE || map[i] == Terrain.LOCKED_DOOR || map[i] == Terrain.COIN_DOOR || TileItem.isSecretDoor(map[i])) {
                    return false;
                }
            }
        }
        //Removed journal keys here...

        return true;
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

    private static final String ENABLE_RESPAWNING = "enable_respawning";
    private static final String RESPAWN_COOLDOWN = "respawn_cooldown";
    private static final String MOB_LIMIT = "mob_limit";
    private static final String MOB_ROTATION = "mob_rotation_new";
    private static final String IGNORE_TERRAIN_FOR_EXPLORING_SCORE = "ignore_terrain_for_exploring_score";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(ENABLE_RESPAWNING, enableRespawning);
        bundle.put(RESPAWN_COOLDOWN, respawnCooldown);
        bundle.put(MOB_LIMIT, mobLimit);
        bundle.put(MOB_ROTATION, mobRotation);
        bundle.put(IGNORE_TERRAIN_FOR_EXPLORING_SCORE, ignoreTerrainForExploringScore);
    }

    static class DataTransferToLevelScheme {
        private int region = REGION_NONE;
        private int waterTexture = REGION_NONE;
        private int musicRegion = REGION_NONE;
        private String musicFile = null;

        void apply(LevelScheme levelScheme) {
            if (region != REGION_NONE) levelScheme.region = region;
            levelScheme.waterTexture = waterTexture;
            levelScheme.musicRegion = musicRegion;
            levelScheme.musicFile = musicFile;
        }
    }
    DataTransferToLevelScheme dataTransferToLevelScheme;

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        assignBossCustomTiles();

        if (bundle.contains(WATER_TEXTURE) || bundle.contains(REGION)) {
            dataTransferToLevelScheme = new DataTransferToLevelScheme();
            dataTransferToLevelScheme.region = bundle.getInt(REGION);
            dataTransferToLevelScheme.waterTexture = bundle.getInt(WATER_TEXTURE);

            dataTransferToLevelScheme.musicRegion = bundle.getInt(MUSIC_REGION) + bundle.getInt("music");
            if (bundle.contains("custom_music")) dataTransferToLevelScheme.musicFile = bundle.getString("custom_music");
            else if (bundle.contains(MUSIC_FILE)) dataTransferToLevelScheme.musicFile = bundle.getString(MUSIC_FILE);

            if (levelScheme != null) {
                dataTransferToLevelScheme.apply(levelScheme);
                dataTransferToLevelScheme = null;
            }
        }

        enableRespawning = bundle.getBoolean(ENABLE_RESPAWNING);
        respawnCooldown = bundle.getInt(RESPAWN_COOLDOWN);
        mobLimit = bundle.getInt(MOB_LIMIT);
        if (bundle.contains("mob_rotation")) {
            mobRotation = MobSpawner.convertMobRotation(Arrays.asList(bundle.getClassArray("mob_rotation")));
        } else mobRotation = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(MOB_ROTATION);
        ignoreTerrainForExploringScore = bundle.getBoolean(IGNORE_TERRAIN_FOR_EXPLORING_SCORE);

        for (Mob m : mobs) {
            m.clearTime();//Fix wrong time caused by v0.7
        }

        if (bundle.contains("init_for_play_called") && !bundle.getBoolean("init_for_play_called")) {//TODO remove in 1.2
            blobs.remove(Alchemy.class);
        }
    }

    //----------------------

    public static TextureFilm getTextureFilm(String theme) {
        TextureFilm tf = textureFilms.get(theme);
        if (tf == null) {
            tf = new TextureFilm(theme, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            textureFilms.put(theme, tf);
        }
        return tf;
    }

}
