package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BuffWithDuration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.IntFunction;
import com.watabou.utils.Reflection;

import java.util.*;

public class Zone implements Bundlable {

    public static final String NONE = "none";

    public enum GrassType {
        NONE(0, 0),
        GRASS(1, Terrain.GRASS),
        HIGH_GRASS(2, Terrain.HIGH_GRASS),
        FURROWED_GRASS(3, Terrain.FURROWED_GRASS);

        public final int index, terrain;

        GrassType(int index, int terrain) {
            this.index = index;
            this.terrain = terrain;
        }
    }

    private int color = 0x78D2FF;
    public String name;

    public boolean flamable = true;
    public boolean canSpawnMobs = true;
    public boolean canSpawnItems = true;
    public boolean canTeleportTo = true;
    public boolean canDestroyWalls = true;//If Pickaxe/DM300 can destroy walls
    public GrassType grassType = GrassType.NONE;
    public boolean blocksVision = false;
    public String music = null;

    public String chasmDestZone;
    public LevelTransition zoneTransition;

    public float respawnCooldown = 50;//How often new mobs spawn
    public boolean ownMobRotationEnabled = false;
    public ItemsWithChanceDistrComp.RandomItemData mobRotation = null;

    private final Set<Integer> cells = new HashSet<>();


    private ArrayList<? extends Mob> mobsToSpawn = new ArrayList<>();

    public HashMap<Class<? extends Buff>, Buff> heroBuffs = new LinkedHashMap<>();
    public HashMap<Class<? extends Buff>, Buff> mobBuffs = new LinkedHashMap<>();


    public String getName() {
        return name;
    }

    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String FLAMABLE = "flamable";
    public static final String CAN_SPAWN_MOBS = "can_spawn_mobs";
    public static final String CAN_SPAWN_ITEMS = "can_spawn_items";
    public static final String CAN_TELEPORT_TO = "can_teleport_to";
    public static final String CAN_DESTROY_WALLS = "can_destroy_walls";
    public static final String GRASS_TYPE = "grass_type";
    public static final String BLOCKS_VISION = "blocks_vision";
    public static final String MUSIC = "music";
    public static final String CHASM_DEST_ZONE = "chasm_dest_zone";
    public static final String ZONE_TRANSITION = "zone_transition";
    public static final String RESPAWN_COOLDOWN = "respawn_cooldown";
    public static final String OWN_MOB_ROTATION_ENABLED = "own_mob_rotation_enabled";
    public static final String MOB_ROTATION = "mob_rotation";
    public static final String HERO_BUFFS = "hero_buffs_new";
    public static final String MOB_BUFFS = "mob_buffs_new";
    public static final String CELLS = "cells";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        name = bundle.getString(NAME);
        color = bundle.getInt(COLOR);
        flamable = bundle.getBoolean(FLAMABLE);
        canSpawnMobs = bundle.getBoolean(CAN_SPAWN_MOBS);
        canSpawnItems = bundle.getBoolean(CAN_SPAWN_ITEMS);
        canTeleportTo = bundle.getBoolean(CAN_TELEPORT_TO);
        canDestroyWalls = bundle.getBoolean(CAN_DESTROY_WALLS);
        grassType = bundle.getEnum(GRASS_TYPE, GrassType.class);
        blocksVision = bundle.getBoolean(BLOCKS_VISION);
        chasmDestZone = bundle.getString(CHASM_DEST_ZONE);
        zoneTransition = (LevelTransition) bundle.get(ZONE_TRANSITION);
        respawnCooldown = bundle.getFloat(RESPAWN_COOLDOWN);
        ownMobRotationEnabled = bundle.getBoolean(OWN_MOB_ROTATION_ENABLED);
        if (bundle.contains(MOB_ROTATION)) mobRotation = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(MOB_ROTATION);

        if (bundle.contains("music_variant")) {
            int variant = bundle.getInt("music_variant");
            if (variant == -1) music = Assets.Music.THEME_FINALE;
            else if (variant == -2) music = "";
            else if (variant == -3) music = Assets.Music.VANILLA_GAME;
            else music = Level.SPECIAL_MUSIC[0][variant];
        } else if (bundle.contains(MUSIC)) music = bundle.getString(MUSIC);

        if (bundle.contains("hero_buffs"))
            for (Class c : bundle.getClassArray("hero_buffs")) heroBuffs.put(c, (Buff) Reflection.newInstance(c));
        if (bundle.contains("mob_buffs"))
            for (Class c : bundle.getClassArray("mob_buffs")) mobBuffs.put(c, (Buff) Reflection.newInstance(c));

        if (bundle.contains(HERO_BUFFS))
            for (Bundlable b : bundle.getCollection(HERO_BUFFS)) heroBuffs.put((Class<? extends Buff>) b.getClass(), (Buff) b);
        if (bundle.contains(MOB_BUFFS))
            for (Bundlable b : bundle.getCollection(MOB_BUFFS)) mobBuffs.put((Class<? extends Buff>) b.getClass(), (Buff) b);

        if (respawnCooldown == 0) respawnCooldown = 50;

        if ("".equals(chasmDestZone)) chasmDestZone = null;

        cells.clear();
        for (int cell : bundle.getIntArray(CELLS)) {
            cells.add(cell);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        bundle.put(COLOR, color);
        bundle.put(FLAMABLE, flamable);
        bundle.put(CAN_SPAWN_MOBS, canSpawnMobs);
        bundle.put(CAN_SPAWN_ITEMS, canSpawnItems);
        bundle.put(CAN_TELEPORT_TO, canTeleportTo);
        bundle.put(CAN_DESTROY_WALLS, canDestroyWalls);
        bundle.put(GRASS_TYPE, grassType);
        bundle.put(BLOCKS_VISION, blocksVision);
        if (music != null) bundle.put(MUSIC, music);
        bundle.put(CHASM_DEST_ZONE, chasmDestZone);
        bundle.put(ZONE_TRANSITION, zoneTransition);
        bundle.put(RESPAWN_COOLDOWN, respawnCooldown);
        bundle.put(OWN_MOB_ROTATION_ENABLED, ownMobRotationEnabled);
        bundle.put(HERO_BUFFS, heroBuffs.values());
        bundle.put(MOB_BUFFS, mobBuffs.values());

        if (mobRotation != null && mobRotation.distrSlots.isEmpty()) mobRotation = null;
        else bundle.put(MOB_ROTATION, mobRotation);

        int[] cellsArray = new int[cells.size()];
        int index = 0;
        for (int cell : cells) {
            cellsArray[index] = cell;
            index++;
        }
        bundle.put(CELLS, cellsArray);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        if (ZonePrompt.getSelectedZone() == this) {
            ZonePrompt.updateSelectedZoneColor();
        }
        if (Dungeon.level != null && Dungeon.level.zoneMap.containsKey(name)) {
            EditorScene.updateZoneColors();
        }
    }

    public void initTransitions(Level level) {
        if (zoneTransition != null) {
            for (int cell : cells) {
                if (!level.transitions.containsKey(cell)) {
                    LevelTransition t = new LevelTransition(level, cell, zoneTransition.destCell, zoneTransition.destLevel);
                    t.type = zoneTransition.type;
                    level.transitions.put(cell, t);
                }
            }
        }
    }

    public void addCell(int cell, Level level) {
        level.zone[cell] = this;
        cells.add(cell);
    }

    public static Zone removeCell(int cell, Level level) {
        Zone z = level.zone[cell];
        z.cells.remove(cell);
        level.zone[cell] = null;
        return z;
    }

    public int numCells() {
        return cells.size();
    }

    public static void changeMapSize(Level level, IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
        Map<String, Zone> nZones = new HashMap<>(3);
        for (Zone z : level.zoneMap.values()) {
            Set<Integer> cellIterator = new HashSet<>(z.cells);
            z.cells.clear();
            for (Integer cell : cellIterator) {
                int nPos = newPosition.apply(cell);
                if (isPositionValid.test(cell, nPos)) {
                    z.cells.add(nPos);
                }
            }
            if (!z.cells.isEmpty()) nZones.put(z.name, z);

        }
        level.zoneMap.clear();
        level.zoneMap.putAll(nZones);
        //setupZoneArray() needs to be called later!!
    }


    public static void setupZoneArray(Level level) {
        level.zone = new Zone[level.length()];
        for (Zone z : level.zoneMap.values()) {
            for (int cell : z.cells) {
                level.zone[cell] = z;
            }
        }
    }

    public void setGrassType(GrassType grassType) {
        this.grassType = grassType;
        for (int cell : cells) {
            EditorScene.updateMap(cell);
        }
    }

    public void affectBuffs(Char ch) {

        Collection<Buff> buffs = ch instanceof Hero ? heroBuffs.values() : mobBuffs.values();

        for (Buff buff : buffs) {
            Buff b;
            if (buff instanceof FlavourBuff) {
                b = Buff.affect(ch, (Class<? extends FlavourBuff>) buff.getClass(), 0);
            } else {
                b = Buff.affect(ch, buff.getClass());
            }
            if (b instanceof BuffWithDuration) {
                ((BuffWithDuration) b).set((BuffWithDuration) buff, getClass());
            }
            if (!b.permanent) {
                b.makePermanent(b.zoneBuff = true);
            }
        }
    }

    public void removeBuffs(Char ch) {
        for (Buff b : ch.buffs()) {
            if (b != null && b.zoneBuff) {
                b.makePermanent(b.zoneBuff = false);
            }
        }
    }

    public boolean appliesBuff(Class<? extends Buff> buff, Char target) {
        if (target.isImmune(buff)) return false;
        if (target instanceof Hero) return heroBuffs.containsKey(buff);
        if (target instanceof Mob) return mobBuffs.containsKey(buff);
        return false;
    }

    public void onZoneEntered(Char ch) {
        //use Dungeon.level to get the level, and ch.pos to get the cell
    }

    public static boolean isFlamable(Level level, int cell) {
        Zone z;
        return (z = level.zone[cell]) == null || z.flamable;
    }

    public static boolean canSpawnMobs(Level level, int cell) {
        Zone z;
        return (z = level.zone[cell]) == null || z.canSpawnMobs;
    }

    public static boolean canSpawnItems(Level level, int cell) {
        Zone z;
        return (z = level.zone[cell]) == null || z.canSpawnItems;
    }

    public static boolean canTeleportTo(Level level, int cell) {
        Zone z;
        return (z = level.zone[cell]) == null || z.canTeleportTo;
    }

    public static boolean canDestroyWall(Level level, int cell) {
        Zone z;
        return (z = level.zone[cell]) == null || z.canDestroyWalls;
    }

    public static GrassType getGrassType(Level level, int cell) {
        Zone z = level.zone[cell];
        return z == null ? GrassType.NONE : z.grassType;
    }

    public static String getMusic(Level level, int cell) {
        Zone z = level.zone[cell];
        return z == null ? null : z.music;
    }

    public Mob createMob() {
        return mobRotation == null || !ownMobRotationEnabled ? null
                : Bestiary.createMob(mobsToSpawn, () -> Bestiary.getMobRotation(mobRotation));
    }


    public ItemsWithChanceDistrComp.RandomItemData getMobRotationVar() {
        if (mobRotation == null) return mobRotation = new ItemsWithChanceDistrComp.RandomItemData();
        return mobRotation;
    }

}