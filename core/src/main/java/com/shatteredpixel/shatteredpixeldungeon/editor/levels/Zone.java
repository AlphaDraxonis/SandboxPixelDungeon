package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Drowsy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Fury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Speed;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Stamina;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public String chasmDestZone;
    public LevelTransition zoneTransition;

    public float respawnCooldown = 50;//How often new mobs spawn
    public boolean ownMobRotationEnabled = false;
    public ItemsWithChanceDistrComp.RandomItemData mobRotation = null;

    private final Set<Integer> cells = new HashSet<>();


    private ArrayList<? extends Mob> mobsToSpawn = new ArrayList<>();

    private ArrayList<Class<? extends Buff>> heroBuffs = new ArrayList<>();
    private ArrayList<Class<? extends Buff>> mobBuffs = new ArrayList<>();


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
    public static final String CHASM_DEST_ZONE = "chasm_dest_zone";
    public static final String ZONE_TRANSITION = "zone_transition";
    public static final String RESPAWN_COOLDOWN = "respawn_cooldown";
    public static final String OWN_MOB_ROTATION_ENABLED = "own_mob_rotation_enabled";
    public static final String MOB_ROTATION = "mob_rotation";
    public static final String HERO_BUFFS = "hero_buffs";
    public static final String MOB_BUFFS = "mob_buffs";
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

        if (bundle.contains(HERO_BUFFS))
            for (Class c : bundle.getClassArray(HERO_BUFFS)) heroBuffs.add(c);
        if (bundle.contains(MOB_BUFFS))
            for (Class c : bundle.getClassArray(MOB_BUFFS)) mobBuffs.add(c);

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
        bundle.put(CHASM_DEST_ZONE, chasmDestZone);
        bundle.put(ZONE_TRANSITION, zoneTransition);
        bundle.put(RESPAWN_COOLDOWN, respawnCooldown);
        bundle.put(OWN_MOB_ROTATION_ENABLED, ownMobRotationEnabled);
        bundle.put(HERO_BUFFS, heroBuffs.toArray(EditorUtilies.EMPTY_CLASS_ARRAY));
        bundle.put(MOB_BUFFS, mobBuffs.toArray(EditorUtilies.EMPTY_CLASS_ARRAY));

        if (mobRotation != null && mobRotation.distrSlots.isEmpty()) mobRotation = null;
        else bundle.put(MOB_ROTATION, mobRotation);

        int[] cellsArray = new int[cells.size()];
        int index = 0;
        for (int cell : cells) {
            cellsArray[index] = cell;
            index++;
        }
        bundle.put(CELLS, cellsArray);

        //TODO implement these, maybe also to normal permabuff and vice versa tzz
        heroBuffs.add(Blindness.class);
        heroBuffs.add(Charm.class);
        heroBuffs.add(Chill.class);
        heroBuffs.add(Corruption.class);
        heroBuffs.add(Cripple.class);
        heroBuffs.add(Daze.class);
        heroBuffs.add(Degrade.class);
        heroBuffs.add(Doom.class);
        heroBuffs.add(Dread.class);
        heroBuffs.add(Drowsy.class);
        heroBuffs.add(EnhancedRings.class);
        heroBuffs.add(Foresight.class);
        heroBuffs.add(Frost.class);
        heroBuffs.add(Fury.class);
        heroBuffs.add(Haste.class);
        heroBuffs.add(Hex.class);
        heroBuffs.add(Invisibility.class);
        heroBuffs.add(Levitation.class);
        heroBuffs.add(Light.class);
        heroBuffs.add(MagicalSight.class);
        heroBuffs.add(MagicalSleep.class);
        heroBuffs.add(MagicImmune.class);
        heroBuffs.add(MindVision.class);
        heroBuffs.add(Ooze.class);
        heroBuffs.add(Paralysis.class);
        heroBuffs.add(Preparation.class);
        heroBuffs.add(Recharging.class);
        heroBuffs.add(Terror.class);
        heroBuffs.add(Speed.class);
        heroBuffs.add(Stamina.class);
        heroBuffs.add(Vertigo.class);
        heroBuffs.add(WellFed.class);
        heroBuffs.add(Weakness.class);
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
                int nPos = newPosition.get(cell);
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

        List<Class<? extends Buff>> buffs = ch instanceof Hero ? heroBuffs : mobBuffs;

        for (Class<? extends Buff> buffClass : buffs) {
            Buff b;
            if (FlavourBuff.class.isAssignableFrom(buffClass)) {
                b = Buff.affect(ch, (Class<? extends FlavourBuff>) buffClass, 0);
            } else {
                b = Buff.affect(ch, buffClass);
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
        if (target instanceof Hero) return heroBuffs.contains(buff);
        if (target instanceof Mob) return mobBuffs.contains(buff);
        return false;
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

    public Mob createMob() {
        return mobRotation == null || !ownMobRotationEnabled ? null
                : Bestiary.createMob(mobsToSpawn, () -> Bestiary.getMobRotation(mobRotation));
    }


    public ItemsWithChanceDistrComp.RandomItemData getMobRotationVar() {
        if (mobRotation == null) return mobRotation = new ItemsWithChanceDistrComp.RandomItemData();
        return mobRotation;
    }

}