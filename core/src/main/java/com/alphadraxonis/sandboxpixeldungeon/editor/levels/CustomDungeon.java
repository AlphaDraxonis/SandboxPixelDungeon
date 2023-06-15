package com.alphadraxonis.sandboxpixeldungeon.editor.levels;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Imp;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.BlacksmithQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.GhostQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.ImpQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.WandmakerQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.ItemStatusHandler;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.Key;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.AlchemicalCatalyst;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.Brew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.Elixir;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.ExoticPotion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomDungeon implements Bundlable {

    public boolean opMode = true;//TODO settings what to enable exactly (dont release for now)
    //Perks:
    // - permainvisiblility
    // - permamindvision
    // - take 0 dmg
    // - infinite accuracy
    // - see hidden traps
    // - infinite keys

    //FIXME: Was noch zu tun ist  FIXME FIXME TODO System.err.println()
    //improve gui
    //plants platzierbar
    //general floor overview stuff
    //hidden doors
    //camera controllieren von EditorScene,  settings für EditorScene
    //mehr Funktionen, die das Bauen erleichtern
    //settings for generatED floors (not template): viewDistance, shopMultiplier, feeling, seed

    //TODO discord server erstellen

    //upload System.err.println() FIXME remove!!  (github, description, apk, releasebuild, discord)
    //debug

    private String name;
    private String lastEditedFloor;

    private String startFloor;
    private Set<String> ratKingLevels;
    private List<String> maybeGhostSpawnLevels, maybeWandmakerSpawnLevels, maybeBlacksmithSpawnLevels, maybeImpSpawnLevels;
    //    private Map<> itemDistribution
    private Map<String, LevelScheme> floors = new HashMap<>();
    private int startGold, startEnergy;


    public CustomDungeon(String name) {
        this.name = name;
        ratKingLevels = new HashSet<>();
        maybeGhostSpawnLevels = new ArrayList<>(5);
        maybeWandmakerSpawnLevels = new ArrayList<>(5);
        maybeBlacksmithSpawnLevels = new ArrayList<>(5);
        maybeImpSpawnLevels = new ArrayList<>(5);
    }

    public CustomDungeon() {
    }


    private LinkedHashMap<Class<? extends Scroll>, String> scrollRuneLabels;
    private LinkedHashMap<Class<? extends Potion>, String> potionColorLabels;
    private LinkedHashMap<Class<? extends Ring>, String> ringGemLabels;

    public void putScrollRuneLabel(Class<? extends Scroll> cl, String value) {
        if (scrollRuneLabels == null) scrollRuneLabels = new LinkedHashMap<>();
        if (value == null) scrollRuneLabels.remove(cl);
        else scrollRuneLabels.put(cl, value);
    }

    public void putPotionColorLabel(Class<? extends Potion> cl, String value) {
        if (potionColorLabels == null) potionColorLabels = new LinkedHashMap<>();
        if (value == null) potionColorLabels.remove(cl);
        else potionColorLabels.put(cl, value);
    }

    public void putRingGemLabel(Class<? extends Ring> cl, String value) {
        if (ringGemLabels == null) ringGemLabels = new LinkedHashMap<>();
        if (value == null) ringGemLabels.remove(cl);
        else ringGemLabels.put(cl, value);
    }


    public ItemStatusHandler<Scroll> getScrollRunes() {
        if (scrollRuneLabels == null)
            return new ItemStatusHandler<Scroll>((Class<? extends Scroll>[]) Generator.Category.SCROLL.classes, Scroll.runes);
        return new ItemStatusHandler<Scroll>((Class<? extends Scroll>[]) Generator.Category.SCROLL.classes, Scroll.runes) {
            @Override
            protected LinkedHashMap<Class<? extends Scroll>, String> assignLabels(Set<Class<? extends Scroll>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Scroll>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(scrollRuneLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(scrollRuneLabels.values()));
                return super.assignLabels(items, labelsLeft, new LinkedHashMap<>(scrollRuneLabels));
            }
        };
    }

    public ItemStatusHandler<Potion> getPotionColors() {
        if (potionColorLabels == null)
            return new ItemStatusHandler<>((Class<? extends Potion>[]) Generator.Category.POTION.classes, Potion.colors);
        return new ItemStatusHandler<Potion>((Class<? extends Potion>[]) Generator.Category.POTION.classes, Potion.colors) {
            @Override
            protected LinkedHashMap<Class<? extends Potion>, String> assignLabels(Set<Class<? extends Potion>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Potion>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(potionColorLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(potionColorLabels.values()));
                return super.assignLabels(items, labelsLeft, potionColorLabels);
            }
        };
    }

    public ItemStatusHandler<Ring> getRingGems() {
        if (ringGemLabels == null)
            return new ItemStatusHandler<>((Class<? extends Ring>[]) Generator.Category.RING.classes, Ring.gems);
        return new ItemStatusHandler<Ring>((Class<? extends Ring>[]) Generator.Category.RING.classes, Ring.gems) {
            @Override
            protected LinkedHashMap<Class<? extends Ring>, String> assignLabels(Set<Class<? extends Ring>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends Ring>, String> itemLabels) {
                items.removeAll(new LinkedHashSet<>(ringGemLabels.keySet()));
                labelsLeft.removeAll(new LinkedHashSet<>(ringGemLabels.values()));
                return super.assignLabels(items, labelsLeft, ringGemLabels);
            }
        };
    }

    public Image getItemImage(Item item) {
        ItemSprite is = new ItemSprite(getItemSpriteOnSheet(item));
        if (item.glowing() != null) is.view(item);
        return is;
    }

    public int getItemSpriteOnSheet(Item item) {
        int code = -1;
        Class<? extends Item> c = item.getClass();
        if (item instanceof Scroll) {
            if (item instanceof ExoticScroll) c = ExoticScroll.exoToReg.get(c);
            code = (scrollRuneLabels == null || !scrollRuneLabels.containsKey(c)) ?
                    ItemSpriteSheet.SCROLL_HOLDER :
                    Scroll.runes.get(scrollRuneLabels.get(c)) + (item instanceof ExoticScroll ? 16 : 0);
        } else if (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew || item instanceof AlchemicalCatalyst)) {
            if (item instanceof ExoticPotion) c = ExoticPotion.exoToReg.get(c);
            code = (potionColorLabels == null || !potionColorLabels.containsKey(c)) ?
                    ItemSpriteSheet.POTION_HOLDER :
                    Potion.colors.get(potionColorLabels.get(c)) + (item instanceof ExoticPotion ? 16 : 0);
        } else if (item instanceof Ring) {
            code = (ringGemLabels == null || !ringGemLabels.containsKey(c)) ?
                    ItemSpriteSheet.RING_HOLDER :
                    Ring.gems.get(ringGemLabels.get(c));
        }

        if (code == -1) return item.image;
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLastEditedFloor() {
        if (lastEditedFloor == null || lastEditedFloor.isEmpty()) return getStart();
        return lastEditedFloor;
    }

    public String getStart() {
        return startFloor;
    }

    public void setStart(String startFloor) {
        this.startFloor = startFloor;
    }

    public boolean isRatKingLevel(String level) {
        return ratKingLevels.contains(level);
    }

    public LevelScheme getFloor(String levelName) {
        return floors.get(levelName);
    }

    public void addFloor(LevelScheme levelScheme) {
        levelScheme.customDungeon = this;
        floors.put(levelScheme.getName(), levelScheme);
    }

    public void removeFloor(LevelScheme levelScheme) {
        floors.remove(levelScheme.getName());
    }

    public int getNumFloors() {
        return floors.size();
    }

    public Collection<String> floorNames() {
        return floors.keySet();
    }

    public Collection<LevelScheme> levelSchemes() {
        return floors.values();
    }

    public int getStartGold() {
        return startGold;
    }

    public int getStartAlchemicalEnergy() {
        return startEnergy;
    }

    public void calculateQuestLevels() {
        LevelScheme level = floors.get(maybeGhostSpawnLevels.get(Random.Int(maybeGhostSpawnLevels.size())));
        level.mobsToSpawn.add(new Ghost(new GhostQuest()));

        level = floors.get(maybeWandmakerSpawnLevels.get(Random.Int(maybeWandmakerSpawnLevels.size())));
        level.mobsToSpawn.add(new Wandmaker(new WandmakerQuest()));

        level = floors.get(maybeBlacksmithSpawnLevels.get(Random.Int(maybeBlacksmithSpawnLevels.size())));
        level.mobsToSpawn.add(new Blacksmith(new BlacksmithQuest()));

        level = floors.get(maybeImpSpawnLevels.get(Random.Int(maybeImpSpawnLevels.size())));
        level.mobsToSpawn.add(new Imp(new ImpQuest()));
    }

    public void addMaybeGhostSpawnLevel(String level) {
        maybeGhostSpawnLevels.add(level);
    }

    public void addMaybeWandmakerSpawnLevel(String level) {
        maybeWandmakerSpawnLevels.add(level);
    }

    public void addMaybeBlacksmithSpawnLevel(String level) {
        maybeBlacksmithSpawnLevels.add(level);
    }

    public void addMaybeImpSpawnLevel(String level) {
        maybeImpSpawnLevels.add(level);
    }

    public void setLastEditedFloor(String lastEditedFloor) {
        this.lastEditedFloor = lastEditedFloor;
    }

    public void initDefault() {

        for (int depth = 1; depth <= 26; depth++) {
            String name = Integer.toString(depth);
            LevelScheme l = new LevelScheme(name, depth, this);
            addFloor(l);

            l.mobsToSpawn.add(new Wandmaker(new WandmakerQuest()));
            l.mobsToSpawn.add(new Ghost(new GhostQuest()));
            l.mobsToSpawn.add(new Blacksmith(new BlacksmithQuest()));
            l.mobsToSpawn.add(new Imp(new ImpQuest()));
        }



        if (startFloor == null) startFloor = "1";
        ratKingLevels.add("5");
    }

    public void initSeeds() {
        List<LevelScheme> floorsSorted = new ArrayList<>(floors.values());
        Collections.sort(floorsSorted, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        for (LevelScheme level : floorsSorted) {
            level.initSeed();
        }
    }


    public static CustomDungeon getDungeon() {
        return Dungeon.customDungeon;
    }

    //if is in edit mode -> identify all items
    public static boolean isEditing() {
        return Game.scene() instanceof EditorScene || Game.scene() instanceof FloorOverviewScene;
    }


    private static final String NAME = "name";
    private static final String LAST_EDITED_FLOOR = "last_edited_floor";
    private static final String START_FLOOR = "start_floor";
    private static final String RAT_KING_LEVELS = "rat_king_levels";
    private static final String MAYBE_GHOST_SPAWN_LEVELS = "maybe_ghost_spawn_levels";
    private static final String MAYBE_WANDMAKER_SPAWN_LEVELS = "maybe_wandmaker_spawn_levels";
    private static final String MAYBE_BLACKSMITH_SPAWN_LEVELS = "maybe_blacksmith_spawn_levels";
    private static final String MAYBE_IMP_SPAWN_LEVELS = "maybe_imp_spawn_levels";
    private static final String START_GOLD = "start_gold";
    private static final String START_ENERGY = "start_energy";
    private static final String LEVEL_SCHEME = "level_scheme";

    private static final String RUNE_LABELS = "rune_labels";
    private static final String RUNE_CLASSES = "rune_classes";
    private static final String COLOR_LABELS = "color_labels";
    private static final String COLOR_CLASSES = "color_classes";
    private static final String GEM_LABELS = "gem_labels";
    private static final String GEM_CLASSES = "gem_classes";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        bundle.put(LAST_EDITED_FLOOR, lastEditedFloor);
        if (startFloor != null) bundle.put(START_FLOOR, startFloor);
        bundle.put(RAT_KING_LEVELS, ratKingLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(MAYBE_GHOST_SPAWN_LEVELS, maybeGhostSpawnLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(MAYBE_WANDMAKER_SPAWN_LEVELS, maybeWandmakerSpawnLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(MAYBE_BLACKSMITH_SPAWN_LEVELS, maybeBlacksmithSpawnLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(MAYBE_IMP_SPAWN_LEVELS, maybeImpSpawnLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(START_GOLD, startGold);
        bundle.put(START_ENERGY, startEnergy);

        if (scrollRuneLabels != null) {
            String[] labels = new String[scrollRuneLabels.size()];
            if (labels.length != 0) {
                Class<?>[] classes = new Class[labels.length];
                int i = 0;
                for (Class<?> clazz : scrollRuneLabels.keySet()) {
                    classes[i] = clazz;
                    labels[i] = scrollRuneLabels.get(clazz);
                    i++;
                }
                bundle.put(RUNE_LABELS, labels);
                bundle.put(RUNE_CLASSES, classes);
            }
        }
        if (potionColorLabels != null) {
            String[] labels = new String[potionColorLabels.size()];
            if (labels.length != 0) {
                Class<?>[] classes = new Class[labels.length];
                int i = 0;
                for (Class<?> clazz : potionColorLabels.keySet()) {
                    classes[i] = clazz;
                    labels[i] = potionColorLabels.get(clazz);
                    i++;
                }
                bundle.put(COLOR_LABELS, labels);
                bundle.put(COLOR_CLASSES, classes);
            }
        }
        if (ringGemLabels != null) {
            String[] labels = new String[ringGemLabels.size()];
            if (labels.length != 0) {
                Class<?>[] classes = new Class[labels.length];
                int i = 0;
                for (Class<?> clazz : ringGemLabels.keySet()) {
                    classes[i] = clazz;
                    labels[i] = ringGemLabels.get(clazz);
                    i++;
                }
                bundle.put(GEM_LABELS, labels);
                bundle.put(GEM_CLASSES, classes);
            }
        }
        int i = 0;
        for (LevelScheme levelScheme : floors.values()) {
            bundle.put(LEVEL_SCHEME + "_" + i, levelScheme);
            i++;
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {//TODO: Achtung: Strings werden falls null waren auf "" gesetzt!
        name = bundle.getString(NAME);
        lastEditedFloor = bundle.getString(LAST_EDITED_FLOOR);
        if (bundle.contains(START_FLOOR)) startFloor = bundle.getString(START_FLOOR);
        ratKingLevels = new HashSet<>(Arrays.asList(bundle.getStringArray(RAT_KING_LEVELS)));
        maybeGhostSpawnLevels = new ArrayList<>(Arrays.asList(bundle.getStringArray(MAYBE_GHOST_SPAWN_LEVELS)));
        maybeWandmakerSpawnLevels = new ArrayList<>(Arrays.asList(bundle.getStringArray(MAYBE_WANDMAKER_SPAWN_LEVELS)));
        maybeBlacksmithSpawnLevels = new ArrayList<>(Arrays.asList(bundle.getStringArray(MAYBE_BLACKSMITH_SPAWN_LEVELS)));
        maybeImpSpawnLevels = new ArrayList<>(Arrays.asList(bundle.getStringArray(MAYBE_IMP_SPAWN_LEVELS)));
        startGold = bundle.getInt(START_GOLD);
        startEnergy = bundle.getInt(START_ENERGY);

        if (bundle.contains(RUNE_LABELS)) {
            scrollRuneLabels = new LinkedHashMap<>();
            String[] labels = bundle.getStringArray(RUNE_LABELS);
            Class<? extends Scroll>[] classes = bundle.getClassArray(RUNE_CLASSES);
            for (int i = 0; i < labels.length; i++) {
                scrollRuneLabels.put(classes[i], labels[i]);
            }
        }
        if (bundle.contains(COLOR_LABELS)) {
            potionColorLabels = new LinkedHashMap<>();
            String[] labels = bundle.getStringArray(COLOR_LABELS);
            Class<? extends Potion>[] classes = bundle.getClassArray(COLOR_CLASSES);
            for (int i = 0; i < labels.length; i++) {
                potionColorLabels.put(classes[i], labels[i]);
            }
        }
        if (bundle.contains(GEM_LABELS)) {
            ringGemLabels = new LinkedHashMap<>();
            String[] labels = bundle.getStringArray(GEM_LABELS);
            Class<? extends Ring>[] classes = bundle.getClassArray(GEM_CLASSES);
            for (int i = 0; i < labels.length; i++) {
                ringGemLabels.put(classes[i], labels[i]);
            }
        }

        int i = 0;
        while (bundle.contains(LEVEL_SCHEME + "_" + i)) {
            LevelScheme levelScheme = (LevelScheme) bundle.get(LEVEL_SCHEME + "_" + i);
            levelScheme.customDungeon = this;
            floors.put(levelScheme.getName(), levelScheme);
            i++;
        }
    }

    public CustomDungeonSaves.Info createInfo() {
        return new CustomDungeonSaves.Info(getName(), 1, getNumFloors());
    }

    void addRatKingLevel(String name) {
        ratKingLevels.add(name);
    }

    public String getAnyRatKingLevel() {
        if (ratKingLevels.isEmpty()) return null;
        return ratKingLevels.iterator().next();
    }

    public String[] getMaybeGhostSpawnLevels() {
        return maybeGhostSpawnLevels.toArray(EMPTY_STRING_ARRAY);
    }

    public String[] getMaybeWandmakerSpawnLevels() {
        return maybeWandmakerSpawnLevels.toArray(EMPTY_STRING_ARRAY);
    }

    public String[] getMaybeBlacksmithSpawnLevels() {
        return maybeBlacksmithSpawnLevels.toArray(EMPTY_STRING_ARRAY);
    }

    public String[] getMaybeImpSpawnLevels() {
        return maybeImpSpawnLevels.toArray(EMPTY_STRING_ARRAY);
    }


    public void delete(LevelScheme levelScheme) throws IOException {
        String n = levelScheme.getName();
        ratKingLevels.remove(n);

        floors.remove(n);
        List<LevelScheme> fs = new ArrayList<>(levelSchemes());
        if (n.equals(startFloor)) {
            Collections.sort(fs);
            if (fs.isEmpty()) {
                startFloor = null;
                SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            } else startFloor = fs.get(0).getName();
        }
        if (EditorScene.customLevel() != null && EditorScene.customLevel().levelScheme == levelScheme) {
            LevelScheme openS = null;
            if (startFloor != null && getFloor(startFloor).getType() == CustomLevel.class)
                openS = getFloor(startFloor);
            else {
                for (LevelScheme ls : fs) {
                    if (ls.getType() == CustomLevel.class) {
                        openS = ls;
                        break;
                    }
                }
            }
            if (openS == null) SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            else {
                EditorScene.open((CustomLevel) openS.loadLevel());
                EditorScene.show(new WndSwitchFloor());
            }

        }

        //Remove transitions
        for (LevelScheme ls : fs) {
            if (n.equals(ls.getChasm())) ls.setChasm(null);
            if (n.equals(ls.getPassage())) ls.setPassage(null);

            if (ls.getType() == CustomLevel.class) {
                boolean load = ls.getLevel() == null;
                Level level;
                if (load) level = ls.loadLevel();
                else level = ls.getLevel();
                Set<Integer> toRemoveKeys = new HashSet<>(4);
                for (LevelTransition transition : level.transitions.values()) {
                    if (transition != null && Objects.equals(transition.destLevel, n)) {
                        toRemoveKeys.add(transition.cell());
                        if (level == EditorScene.customLevel()) EditorScene.remove(transition);
                    }
                }

                //Remove invalid keys
                boolean removedItems = false;
                for (Heap h : level.heaps.valueList()) {
                    for (Item i : h.items) {
                        if (i instanceof Key && ((Key) i).levelName.equals(n)) {
                            h.remove(i);
                            removedItems = true;
                        }
                    }
                }

                boolean save = removedItems || !toRemoveKeys.isEmpty();
                for (int key : toRemoveKeys) level.transitions.remove(key);

                if (save) CustomDungeonSaves.saveLevel(level);
                if (load) ls.unloadLevel();
                else if (level == EditorScene.customLevel())
                    Undo.reset();//TODO maybe not best solution to reset all
            } else {
                if (Objects.equals(ls.getEntranceTransitionRegular().destLevel, n)) {
                    ls.getEntranceTransitionRegular().destLevel = Level.SURFACE;
                    ls.getEntranceTransitionRegular().destCell = -1;
                } else if (Objects.equals(ls.getExitTransitionRegular().destLevel, n)) {
                    ls.getExitTransitionRegular().destLevel = null;
                    ls.getExitTransitionRegular().destCell = -1;
                }
            }
        }

        //Set level for keys in inv
        Items.updateKeys(n, EditorScene.customLevel() == null ? null : EditorScene.customLevel().name);


        CustomDungeonSaves.deleteLevelFile(n);
        CustomDungeonSaves.saveDungeon(this);
    }

    public static void deleteDungeon(String name) {
        if (Dungeon.customDungeon != null && Dungeon.customDungeon.name.equals(name))
            Dungeon.customDungeon = null;
        CustomDungeonSaves.deleteDungeonFile(name);
    }

}