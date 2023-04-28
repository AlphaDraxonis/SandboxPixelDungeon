package com.shatteredpixel.shatteredpixeldungeon.levels.editor;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.ENTRANCE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EXIT;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WALL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WATER;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Floor extends Level {

    private int width = 10, height = 10;
    private String theme = Assets.Environment.TILES_SEWERS;
    private String waterTexture = Assets.Environment.WATER_PRISON;

    private Set<Mob> startMobs = new HashSet<>();//Mobs for createMobs(), already need to have their pos etc
    private boolean enableMobSpawning = true;//If new mobs spawn over time
    //    private boolean fillRemainingMobsWhenCreated = false;//if createMobs() didnt reach the mob cap, this spawns new mobs using mobRotation
    private boolean swapForMutations = true;//Chance of changing eg a rat to an albino rat
    private List<Class<? extends Mob>> mobRotation = new ArrayList<>();//More of same mob means higher chance,
    private int mobLimit = 10;


    private List<ItemWithPos> startItems = new ArrayList<>();

    public static class ItemWithPos {
        private Item item;
        private int pos;

        public ItemWithPos(Item item, int pos) {
            this.item = item;
            this.pos = pos;
        }

        public int pos() {
            return pos;
        }

        public Item item() {
            return item;
        }
    }

    private final int[] terrains = {
            WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
            WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EXIT, EMPTY, WALL,
            WALL, WATER, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WATER, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ENTRANCE, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EXIT, WALL,
            WALL, EXIT, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
    };
    private boolean ignoreTerrainForExploringScore = false;//ignores Barricades, Locked doors and secret doors in isFullyExplored()
    //Achtung: direkt auf map[] arbeiten!

    public boolean[] isRoom;//variable of cell to differantiate between room and hallway, used for spawning different stuff or traps TODO unused!
    public boolean[] canSpawnThings;//variable of cell

    private Painter decorationPainter;//used for decoration and placing water and grass, not for painting rooms; usually one of the 5 default painters, or null


    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;

        mobRotation.add(Rat.class);
        Mob m1 = new Rat();
        m1.pos=27;
        startMobs.add(m1);
        startItems.add(new ItemWithPos(new Torch(), 22));

        setSize(getWidth(), getHeight());
    }

    @Override
    public String tilesTex() {
        return theme;
    }

    @Override
    public String waterTex() {
        return waterTexture;
    }

    @Override
    protected boolean build() {

        setSize(width, height);

        for (int i = 0; i < terrains.length; i++) {
            map[i] = terrains[i];
        }

        addTransitions();

        return true;
    }

    protected void addTransitions() {
        for (int i = 0; i < terrains.length; i++) {
            if (terrains[i] == ENTRANCE) {
                LevelTransition transition = new LevelTransition(this, i, Dungeon.depth == 1 ? LevelTransition.Type.SURFACE : LevelTransition.Type.REGULAR_ENTRANCE);
//                transition.destBranch = parentBranch;
                transitions.add(transition);
            } else if (terrains[i] == EXIT) {
                LevelTransition transition = new LevelTransition(this, i, LevelTransition.Type.REGULAR_EXIT);
//                transition.destBranch = curBranch;
                transitions.add(transition);
//                curBranch++;
            }
        }
    }

    @Override
    public ArrayList<Class<? extends Mob>> getMobRotation() {
        return Bestiary.getMobRotation(mobRotation, swapForMutations);
    }

    @Override
    protected void createMobs() {
        mobs.addAll(startMobs);
    }

    @Override
    public int mobLimit() {
        return mobLimit;
    }

    @Override
    public boolean spawnMob(int disLimit) {
        if (enableMobSpawning) return super.spawnMob(disLimit);
        return false;
    }

    @Override
    protected void createItems() {

        for (ItemWithPos i : startItems) {
            drop(i.item, i.pos);
        }

    }

    protected int randomDropCell() {
        int tries = 100;
        while (tries-- > 0) {
            int pos = Random.Int(length());
            if (passable[pos] && !solid[pos] && canSpawnThings[pos]
                    && map[pos] != ENTRANCE
                    && map[pos] != EXIT
                    && heaps.get(pos) == null
                    && findMob(pos) == null) {

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
    public boolean isLevelExplored(int depth) {
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
        //Removed journal keys...

        return true;
    }


    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    @Override
    public float respawnCooldown() {
        return TIME_TO_RESPAWN;
    }

    @Override
    public void setSize(int w, int h) {
        super.setSize(w, h);
        isRoom = new boolean[passable.length];
        canSpawnThings = new boolean[passable.length];
    }

    public void buildFlagMaps() {
        super.buildFlagMaps();

        for (int i = 0; i < length(); i++) {
            isRoom[i] = true;
            canSpawnThings[i] = true;
        }
    }

    public Set<Mob> getStartMobs() {
        return startMobs;
    }

    public List<ItemWithPos> getStartItems() {
        return startItems;
    }
}
