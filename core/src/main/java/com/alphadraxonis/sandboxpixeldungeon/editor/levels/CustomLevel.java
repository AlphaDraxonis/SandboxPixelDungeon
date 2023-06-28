package com.alphadraxonis.sandboxpixeldungeon.editor.levels;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_CAVES;
import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_CITY;
import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_HALLS;
import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_NONE;
import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_PRISON;
import static com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme.REGION_SEWERS;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EMPTY;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.ENTRANCE;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.EXIT;
import static com.alphadraxonis.sandboxpixeldungeon.levels.Terrain.WALL;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.SacrificialFire;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Bestiary;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Statue;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.Key;
import com.alphadraxonis.sandboxpixeldungeon.levels.CavesLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.CityLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.DeadEndLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.HallsLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.LastLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.PrisonLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.RegularLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.SewerLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.levels.painters.Painter;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.secret.SecretRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.SpecialRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.BlazingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.BurningTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ChillingTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.DisintegrationTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.ExplosiveTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.FrostTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.PitfallTrap;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Group;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CustomLevel extends Level {


    private static final Map<String, TextureFilm> textureFilms = new HashMap<>();
    public boolean isEditing = true;

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
            levelScheme.itemsToSpawn.clear();

            Dungeon.customDungeon.addFloor(temp);
            Dungeon.levelName = Level.NONE;

            tempDungeonNameForKey = name;
            Random.pushGenerator(seed + 1);
            SpecialRoom.initForRun();
            SecretRoom.initForRun();
            Random.popGenerator();

            Dungeon.seed = seed + 4;
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
                if (map[i] == Terrain.SECRET_DOOR) map[i] = Terrain.DOOR;//TODO add secret doors!
                else if (map[i] == ENTRANCE) levelScheme.entranceCells.add(i);
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
            customTiles = level.customTiles;
            customWalls = level.customWalls;
            blobs = level.blobs;

//           TODO  respawner!!

            Dungeon.customDungeon.removeFloor(temp);
            Dungeon.levelName = name;

            increaseMapSize(this, 85, 85);
        } else {
            this.feeling = feeling;
            levelScheme.setFeeling(this.feeling);
        }

    }

    public CustomLevel(CustomLevel customLevel) {
        isEditing = false;
        width = customLevel.width;
        height = customLevel.height;
        setSize(width, height);
        terrains = new int[length];

        region = customLevel.region;
        waterTexture = customLevel.waterTexture;
        music = customLevel.music;
        enableRespawning = customLevel.enableRespawning;
        respawnCooldown = customLevel.respawnCooldown;
        swapForMutations = customLevel.swapForMutations;
        mobLimit = customLevel.mobLimit;
        viewDistance = customLevel.viewDistance;
        System.arraycopy(customLevel.map, 0, terrains, 0, customLevel.map.length);
        ignoreTerrainForExploringScore = customLevel.ignoreTerrainForExploringScore;
        mobRotation = customLevel.mobRotation;
        feeling = customLevel.feeling;
        System.arraycopy(terrains, 0, map, 0, terrains.length);

        transitions = new HashMap<>();
        for (LevelTransition trans : customLevel.transitions.values()) {
            transitions.put(trans.departCell, trans.getCopy());
        }

        mobs = new HashSet<>();
        for (Mob m : customLevel.mobs) {
            Mob clone = (Mob) m.getCopy();
            if (clone instanceof Mimic) ((Mimic) clone).adjustStats(Dungeon.depth);
            mobs.add(clone);
        }

        heaps = new SparseArray<>();
        for (Heap h : customLevel.heaps.valueList()) {
            Heap nh = h.getCopy();
            heaps.put(h.pos, nh);
            for (Item item : nh.items) item.reset();//important for scroll runes being inited
        }

        blobs = new HashMap<>();
        plants = new SparseArray<>();

        traps = new SparseArray<>();
        for (Trap trap : customLevel.traps.valueList()) {
            traps.put(trap.pos, trap.getCopy());
        }

        customTiles = new HashSet<>();
        customWalls = new HashSet<>();

        buildFlagMaps();
        cleanWalls();

        createItems();
    }

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
        int m = this.music;
        if (m == REGION_NONE) m = region;
        switch (m) {
            case REGION_SEWERS:
                Music.INSTANCE.playTracks(
                        new String[]{Assets.Music.SEWERS_1, Assets.Music.SEWERS_2, Assets.Music.SEWERS_2},
                        new float[]{1, 1, 0.5f},
                        false);
                break;
            case REGION_PRISON:
                Music.INSTANCE.playTracks(
                        new String[]{Assets.Music.PRISON_1, Assets.Music.PRISON_2, Assets.Music.PRISON_2},
                        new float[]{1, 1, 0.5f},
                        false);
                break;
            case REGION_CAVES:
                Music.INSTANCE.playTracks(
                        new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2},
                        new float[]{1, 1, 0.5f},
                        false);
                break;
            case REGION_CITY:
                Music.INSTANCE.playTracks(
                        new String[]{Assets.Music.CITY_1, Assets.Music.CITY_2, Assets.Music.CITY_2},
                        new float[]{1, 1, 0.5f},
                        false);
                break;
            case REGION_HALLS:
                Music.INSTANCE.playTracks(
                        new String[]{Assets.Music.HALLS_1, Assets.Music.HALLS_2, Assets.Music.HALLS_2},
                        new float[]{1, 1, 0.5f},
                        false);
                break;

            default:
                super.playLevelMusic();
                break;
        }
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


        setSize(85, 85);

//        for (int i = 0; i < terrains.length; i++) {
//            map[i] = terrains[i];
//        }

        int add = 80 / 2 + 80 / 2 * 85;
        increaseArrayForMapSize(terrains, map, add, 5, 85);

        updateTransitionCells();

        return true;
    }


    protected void updateTransitionCells() {
        levelScheme.entranceCells.clear();
        levelScheme.exitCells.clear();
        for (int i = 0; i < map.length; i++) {
            int terrain = map[i];
            if (terrain == ENTRANCE) levelScheme.entranceCells.add(i);
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
                    && heaps.get(pos) == null
                    && (tries <= lengthHalf || findMob(pos) == null)) {

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
        int w = width();
        int remain = cell % w;
        return cell < w || remain == 0 || remain == w - 1 || cell > height() * (w - 1);
    }


    public CustomLevel createCopiedFloor() {
        return new CustomLevel(this);
    }


    public static void increaseMapSize(Level level, int newWidth, int newHeight) {
        if (level.width() > newWidth || level.height() > newHeight) return;

        int diffH = newHeight - level.height();
        int diffW = newWidth - level.width();
        int newLenght = newWidth * newHeight;

        int addLeft = diffW / 2;
        int addTop = diffH / 2 * newWidth;
        int add = addLeft + addTop;

        int[] oldMap = level.map;
        boolean[] oldVisited = level.visited;
        boolean[] oldMapped = level.mapped;

        int levelWidth = level.width();
        level.setSize(newWidth, newHeight);
        increaseArrayForMapSize(oldMap, level.map, add, levelWidth, newWidth);

        increaseArrayForMapSize(oldVisited, level.visited, add, levelWidth, newWidth);
        increaseArrayForMapSize(oldMapped, level.mapped, add, levelWidth, newWidth);

        boolean[] nDiscoverable = new boolean[newLenght];
        increaseArrayForMapSize(level.discoverable, nDiscoverable, add, levelWidth, newWidth);
        level.discoverable = nDiscoverable;

        for (Mob m : level.mobs) {
            m.pos = m.pos + add + m.pos / levelWidth * diffW;
        }
        SparseArray<Heap> nHeaps = new SparseArray<>();
        for (Heap m : level.heaps.valueList()) {
            m.pos = m.pos + add + m.pos / levelWidth * diffW;
            nHeaps.put(m.pos, m);
        }
        level.heaps.clear();
        level.heaps.putAll(nHeaps);

        SparseArray<Trap> nTrap = new SparseArray<>();
        for (Trap m : level.traps.valueList()) {
            m.pos = m.pos + add + m.pos / levelWidth * diffW;
            nTrap.put(m.pos, m);
        }
        level.traps.clear();
        level.traps.putAll(nTrap);

        SparseArray<Plant> nPlant = new SparseArray<>();
        for (Plant m : level.plants.valueList()) {
            m.pos = m.pos + add + m.pos / levelWidth * diffW;
            nPlant.put(m.pos, m);
        }
        level.plants.clear();
        level.plants.putAll(nPlant);

        for (Blob b : level.blobs.values()) {
            b.cur = new int[newLenght];
        }

        List<Integer> cells = new ArrayList<>(level.levelScheme.entranceCells);
        level.levelScheme.entranceCells.clear();
        for (int cell : cells) {
            level.levelScheme.entranceCells.add(cell + add + cell / levelWidth * diffW);
        }
        Collections.sort(level.levelScheme.entranceCells);

        cells = new ArrayList<>(level.levelScheme.exitCells);
        level.levelScheme.exitCells.clear();
        for (int cell : cells) {
            level.levelScheme.exitCells.add(cell + add + cell / levelWidth * diffW);
        }
        Collections.sort(level.levelScheme.exitCells);

        Map<Integer, LevelTransition> nTrans = new HashMap<>();
        for (LevelTransition transition : level.transitions.values()) {
            transition.departCell = transition.centerCell = transition.departCell + add + transition.departCell / levelWidth * diffW;
            //TODO maybe not so good to set centerCell!!
            nTrans.put(transition.departCell, transition);
            Point p = level.cellToPoint(transition.departCell);
            transition.set(p.x, p.y, p.x, p.y);
        }
        level.transitions.clear();
        level.transitions = nTrans;

//        transitions = level.transitions;//change
//        customTiles = level.customTiles;//change
//        customWalls = level.customWalls;//change
    }

    private static void increaseArrayForMapSize(int[] src, int[] dest, int add, int levelWidth, int newWidth) {
        for (int i = 0; i < src.length; i++) {
            dest[i + add + i / levelWidth * (newWidth - levelWidth)] = src[i];//
        }
    }

    private static void increaseArrayForMapSize(boolean[] src, boolean[] dest, int add, int levelWidth, int newWidth) {
        for (int i = 0; i < src.length; i++) {
            dest[i + add + i / levelWidth * (newWidth - levelWidth)] = src[i];//
        }
    }

}