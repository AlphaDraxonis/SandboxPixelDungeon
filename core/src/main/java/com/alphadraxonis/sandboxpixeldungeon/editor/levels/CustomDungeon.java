package com.alphadraxonis.sandboxpixeldungeon.editor.levels;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mimic;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Thief;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Imp;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Items;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.BlacksmithQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.GhostQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.ImpQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.Quest;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.QuestNPC;
import com.alphadraxonis.sandboxpixeldungeon.editor.quests.WandmakerQuest;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.ItemStatusHandler;
import com.alphadraxonis.sandboxpixeldungeon.items.Stylus;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.Key;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.AlchemicalCatalyst;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.PotionOfStrength;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.brews.Brew;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.elixirs.Elixir;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.ExoticPotion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfEnchantment;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfIntuition;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
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

    public boolean damageImmune, seeSecrets, permaMindVision, permaInvis, permaKey;// maybe add ScrollOfDebug?

    {
        if (DeviceCompat.isDebug() && false) {
            damageImmune = seeSecrets = permaMindVision = permaInvis = permaKey = true;
        }
    }

    //FIXME: Was noch zu tun ist  FIXME FIXME TODO System.err.println()
    //general floor overview stuff
    //settings für EditorScene
    //custom tiles wichtig
    //select builder and painter
    //place blobs like fire
    //Category items/mobs/rooms


    //UI rework
    //restore toolbar
    //test
    //write changelog

    //to fix new save location with v0.7 on desktop
    //delete "C:\Users\you\AppData\Roaming\.alphadraxonis\Sandbox Pixel Dungeon\Sandbox-Pixel-Dungeon" - there are only empty folders (NOT REQUIRED!)
    //move "C:/Users/you/Sandbox-Pixel-Dungeon/custom_dungeons" into "C:\Users\you\AppData\Roaming\.alphadraxonis\Sandbox Pixel Dungeon" (REQUIRED!)

    //This affects everyone:
    //replace ALL spaces ( ) in the dungeon folder names and all the level file names with an underscore (_). This does not change the names displayed in game. (REQUIRED!)
    //If 2 dungeons happen to have the same name after that, move one to a different location and only change the file name of the other dungeon.
    // Then use the new renaming feature to rename the dungeon and the file. After that, just move the first dungeon back.
    //If 2 levels in the same dungeon happen to have the same name after that, you can safely edit and rename one of them as long as you don't try to enter the other one.
    //Games in progress are not affected
    //Tested on Windows and Android


    //DESKTOP ONLY: move files
    //In the same directory as your jar file, there should be folders game1 to 5. Inside each is a folder "dungeon_levels" in which a folder "levels" is located.
    //Move this folder called "levels" into C:\Users\you\AppData\Roaming\.alphadraxonis\Sandbox Pixel Dungeon\game1 (into folders 1 to 5 accordingly)


    private String name;
    private String lastEditedFloor;

    private String startFloor;
    private Set<String> ratKingLevels;
    private List<ItemDistribution<? extends Bundlable>> itemDistributions;
    private Map<String, LevelScheme> floors = new HashMap<>();
    private int startGold, startEnergy;

    private String password;

    public CustomDungeon(String name) {
        this.name = name;
        ratKingLevels = new HashSet<>();
        itemDistributions = new ArrayList<>(5);
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

    public void setLastEditedFloor(String lastEditedFloor) {
        this.lastEditedFloor = lastEditedFloor;
    }

    public void initDistribution() {
        Random.pushGenerator(Dungeon.seed + 5);
        for (ItemDistribution<?> dis : itemDistributions) {
            Random.Long();
            dis.initForPlaying();
        }
        Random.popGenerator();
    }

    public List<ItemDistribution<? extends Bundlable>> getItemDistributions() {
        return itemDistributions;
    }

    private boolean removeNextScroll;

    protected void removeEverySecondSoU(Level level) {
        for (Heap h : level.heaps.valueList()) {
            for (Item i : h.items) {
                if (i instanceof ScrollOfUpgrade) {
                    int oldQuantity = i.quantity();
                    if (oldQuantity % 2 == 1) {
                        int newQuantity = removeNextScroll ? (oldQuantity - 1) / 2 : (oldQuantity + 1) / 2;
                        if (newQuantity == 0) h.items.remove(i);
                        else i.quantity(newQuantity);
                        removeNextScroll = !removeNextScroll;
                    } else {
                        i.quantity(oldQuantity / 2);
                    }
                }
            }
            if (h.isEmpty()) {
                level.heaps.remove(h.pos);
                h.destroyImages();
            }
        }
    }

    public void initDefault() {

        for (int depth = 1; depth <= 26; depth++) {
            String name = Integer.toString(depth);
            LevelScheme l = new LevelScheme(name, depth, this);
            addFloor(l);
        }

        for (int i = 0; i < 5; i++) {
            ItemDistribution.Items sou = new ItemDistribution.Items(true);
            sou.getObjectsToDistribute().add(new ScrollOfUpgrade());
            sou.getObjectsToDistribute().add(new ScrollOfUpgrade());
            sou.getObjectsToDistribute().add(new ScrollOfUpgrade());
            sou.getLevels().add(Integer.toString(i * 5 + 1));
            sou.getLevels().add(Integer.toString(i * 5 + 2));
            sou.getLevels().add(Integer.toString(i * 5 + 3));
            sou.getLevels().add(Integer.toString(i * 5 + 4));
            itemDistributions.add(sou);
        }
        for (int i = 0; i < 10; i++) {
            ItemDistribution.Items poStr = new ItemDistribution.Items(true);
            poStr.getObjectsToDistribute().add(new PotionOfStrength());
            poStr.getLevels().add(Integer.toString(i * 2 + 1 + i / 2));
            poStr.getLevels().add(Integer.toString(i * 2 + 2 + i / 2));
            itemDistributions.add(poStr);
        }
        for (int i = 0; i < 5; i++) {
            ItemDistribution.Items sty = new ItemDistribution.Items(true);
            sty.getObjectsToDistribute().add(new Stylus());
            sty.getLevels().add(Integer.toString(i * 5 + 1));
            sty.getLevels().add(Integer.toString(i * 5 + 2));
            sty.getLevels().add(Integer.toString(i * 5 + 3));
            sty.getLevels().add(Integer.toString(i * 5 + 4));
            itemDistributions.add(sty);
        }
        ItemDistribution.Items soTransmutation = new ItemDistribution.Items(true);
        soTransmutation.getObjectsToDistribute().add(new StoneOfEnchantment());//I wonder if the comment in shatteredPD is an mistake...
        for (int i = 6; i < 20; i++) {
            if (i % 5 != 0) soTransmutation.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(soTransmutation);

        //TODO bei der Erstellung automatisch ggf zu Spawnitems adden (auch food) -> dafür CategoryPlaceholders
        ItemDistribution.Items stIntu = new ItemDistribution.Items(true);
        stIntu.getObjectsToDistribute().add(new StoneOfIntuition());
        for (int i = 1; i <= 3; i++) {
            stIntu.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(stIntu);

        ItemDistribution.Mobs ghostDistr = new ItemDistribution.Mobs();
        QuestNPC<?> questNPC = new Ghost(new GhostQuest());
        questNPC.quest.setType(Quest.BASED_ON_DEPTH);
        questNPC.pos = -1;
        ghostDistr.getObjectsToDistribute().add(new MobItem(questNPC));
        for (int i = 2; i <= 4; i++) {
            ghostDistr.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(ghostDistr);

        ItemDistribution.Mobs wandmakerDistr = new ItemDistribution.Mobs();
        questNPC = new Wandmaker(new WandmakerQuest());
        questNPC.quest.setType(Quest.BASED_ON_DEPTH);
        questNPC.pos = -1;
        wandmakerDistr.getObjectsToDistribute().add(new MobItem(questNPC));
        for (int i = 7; i <= 9; i++) {
            wandmakerDistr.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(wandmakerDistr);

        ItemDistribution.Mobs blacksmithDistr = new ItemDistribution.Mobs();
        questNPC = new Blacksmith(new BlacksmithQuest());
        questNPC.quest.setType(Quest.BASED_ON_DEPTH);
        questNPC.pos = -1;
        blacksmithDistr.getObjectsToDistribute().add(new MobItem(questNPC));
        for (int i = 12; i <= 14; i++) {
            blacksmithDistr.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(blacksmithDistr);

        ItemDistribution.Mobs impDistr = new ItemDistribution.Mobs();
        questNPC = new Imp(new ImpQuest());
        questNPC.quest.setType(Quest.BASED_ON_DEPTH);
        questNPC.pos = -1;
        impDistr.getObjectsToDistribute().add(new MobItem(questNPC));
        for (int i = 17; i <= 19; i++) {
            impDistr.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(impDistr);

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

    public static boolean showHiddenDoors() {
        return isEditing() || Dungeon.customDungeon.seeSecrets;
    }


    private static final String NAME = "name";
    private static final String LAST_EDITED_FLOOR = "last_edited_floor";
    private static final String START_FLOOR = "start_floor";
    private static final String RAT_KING_LEVELS = "rat_king_levels";
    private static final String ITEM_DISTRIBUTION = "item_distribution";
    private static final String START_GOLD = "start_gold";
    private static final String START_ENERGY = "start_energy";
    private static final String LEVEL_SCHEME = "level_scheme";
    private static final String REMOVE_NEXT_SCROLL = "remove_next_scroll";
    private static final String PASSWORD = "password";

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
        bundle.put(ITEM_DISTRIBUTION, itemDistributions);
        bundle.put(START_GOLD, startGold);
        bundle.put(START_ENERGY, startEnergy);
        bundle.put(REMOVE_NEXT_SCROLL, removeNextScroll);
        bundle.put(PASSWORD, password);

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
        itemDistributions = new ArrayList<>(5);
        if (bundle.contains(ITEM_DISTRIBUTION)) {
            Collection<?> col = bundle.getCollection(ITEM_DISTRIBUTION);
            itemDistributions.addAll((Collection<? extends ItemDistribution<? extends Bundlable>>) col);
        }
        startGold = bundle.getInt(START_GOLD);
        startEnergy = bundle.getInt(START_ENERGY);
        removeNextScroll = bundle.getBoolean(REMOVE_NEXT_SCROLL);
        password = bundle.getString(PASSWORD);
        if (password.isEmpty()) password = null;

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

        //Remove transitions and keys
        for (LevelScheme ls : fs) {
            if (n.equals(ls.getChasm())) ls.setChasm(null);
            if (n.equals(ls.getPassage())) ls.setPassage(null);

            if (ls.getType() == CustomLevel.class) {
                boolean load = ls.getLevel() == null;
                Level level;
                if (load) level = ls.loadLevel();
                else level = ls.getLevel();
                Set<Integer> toRemoveTransitions = new HashSet<>(4);
                for (LevelTransition transition : level.transitions.values()) {
                    if (transition != null && Objects.equals(transition.destLevel, n)) {
                        toRemoveTransitions.add(transition.cell());
                        if (level == EditorScene.customLevel()) EditorScene.remove(transition);
                    }
                }

                //Remove invalid keys in heaps
                boolean removedItems = false;
                for (Heap h : level.heaps.valueList()) {
                    if (removeInvalidKeys(h.items, n)) {
                        removedItems = true;
                        if (h.isEmpty()) {
                            level.heaps.remove(h.pos);
                            h.destroyImages();
                        }
                    }
                }
                //Remove invalid keys in mob containers
                for (Mob m : level.mobs) {
                    if (m instanceof Mimic && ((Mimic) m).items != null) {
                        if (removeInvalidKeys(((Mimic) m).items, n)) removedItems = true;
                    }
                    if (m instanceof Thief && isInvalidKey(((Thief) m).item, n)) {
                        ((Thief) m).item = null;
                        removedItems = true;
                    }
                }
                if (removeInvalidKeys(ls.itemsToSpawn, n)) removedItems = true;

                boolean save = removedItems || !toRemoveTransitions.isEmpty();
                for (int key : toRemoveTransitions) level.transitions.remove(key);

                if (save) CustomDungeonSaves.saveLevel(level);
                if (load) ls.unloadLevel();
                else if (level == EditorScene.customLevel() && levelScheme != level.levelScheme) {
                    Undo.reset();//TODO maybe not best solution to reset all
                    EditorScene.updateHeapImagesAndSubIcons();
                }
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

        for (ItemDistribution<? extends Bundlable> distr : itemDistributions) {
            if (distr instanceof ItemDistribution.Items) {
                removeInvalidKeys((List<Item>) distr.getObjectsToDistribute(), n);
            }
            distr.getLevels().remove(n);
        }

        //Set level for keys in inv
        Items.updateKeys(n, EditorScene.customLevel() == null ? null : EditorScene.customLevel().name);


        CustomDungeonSaves.deleteLevelFile(n);
        CustomDungeonSaves.saveDungeon(this);
    }

    private boolean removeInvalidKeys(List<Item> items, String invalidLevelName) {
        if (items == null) return false;
        boolean removedSth = false;
        for (Item i : new ArrayList<>(items)) {
            if (isInvalidKey(i, invalidLevelName)) {
                items.remove(i);
                removedSth = true;
            }
        }
        return removedSth;
    }

    private boolean isInvalidKey(Item item, String invalidLevelName) {
        if (item == null) return false;
        return item instanceof Key && ((Key) item).levelName.equals(invalidLevelName);
    }

    public static void deleteDungeon(String name) {
        if (Dungeon.customDungeon != null && Dungeon.customDungeon.name.equals(name))
            Dungeon.customDungeon = null;
        CustomDungeonSaves.deleteDungeonFile(name);
    }

    public static void renameDungeon(String oldName, String newName) {
        try {
            CustomDungeon dungeon = CustomDungeonSaves.renameDungeon(oldName, newName);
            if (dungeon != null) dungeon.name = newName;
            else throw new IOException("Renaming was not successful!");
            CustomDungeonSaves.saveDungeon(dungeon);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
            throw new RuntimeException(e);
        }
    }

    public void renameLevel(LevelScheme levelScheme, String newName) {

        try {
            String oldName = levelScheme.getName();
            boolean savedItself = false;

            if (levelScheme.getType() == CustomLevel.class)
                CustomDungeonSaves.renameLevel(oldName, newName);

            if (ratKingLevels.contains(oldName)) {
                ratKingLevels.remove(oldName);
                ratKingLevels.add(newName);
            }

            if (oldName.equals(lastEditedFloor)) lastEditedFloor = newName;
            if (oldName.equals(startFloor)) startFloor = newName;

            //Change transitions and keys
            for (LevelScheme ls : floors.values()) {
                if (oldName.equals(ls.getChasm())) ls.setChasm(newName);
                if (oldName.equals(ls.getPassage())) ls.setPassage(newName);

                if (ls.getType() == CustomLevel.class) {
                    boolean load = ls.getLevel() == null;
                    Level level;
                    if (load) level = ls.loadLevel();
                    else level = ls.getLevel();

                    boolean saveWithOgName = level == null;
                    if (saveWithOgName) {
                        level = CustomDungeonSaves.loadLevelWithOgName(ls.getName());
                        level.levelScheme = ls;
                    }

                    boolean needsSave = false;
                    for (LevelTransition transition : level.transitions.values()) {
                        if (transition != null) {
                            if (Objects.equals(transition.destLevel, oldName)) {
                                transition.destLevel = newName;
                                needsSave = true;
                            }
                            if (Objects.equals(transition.departLevel, oldName)) {
                                transition.departLevel = newName;
                                needsSave = true;
                            }
                        }
                    }

                    //Change invalid keys in heaps
                    for (Heap h : level.heaps.valueList()) {
                        if (renameInvalidKeys(h.items, oldName, newName)) {
                            needsSave = true;
                        }
                    }
                    //Change invalid keys in mob containers
                    for (Mob m : level.mobs) {
                        if (m instanceof Mimic && ((Mimic) m).items != null) {
                            if (renameInvalidKeys(((Mimic) m).items, oldName, newName))
                                needsSave = true;
                        }
                        if (m instanceof Thief && isInvalidKey(((Thief) m).item, oldName)) {
                            ((Key) ((Thief) m).item).levelName = newName;
                            needsSave = true;
                        }
                    }
                    if (renameInvalidKeys(ls.itemsToSpawn, oldName, newName)) needsSave = true;

                    if (needsSave) {
                        if (ls == levelScheme) {
                            level.name = newName;
                            savedItself = true;
                        }
                        if (saveWithOgName) CustomDungeonSaves.saveLevelWithOgName(level);
                        else CustomDungeonSaves.saveLevel(level);
                    }
                    if (load) ls.unloadLevel();
                    else if (level == EditorScene.customLevel() && levelScheme != level.levelScheme) {
                        if (needsSave) EditorScene.updateHeapImagesAndSubIcons();
                    }
                } else {
                    if (Objects.equals(ls.getEntranceTransitionRegular().destLevel, oldName)) {
                        ls.getEntranceTransitionRegular().destLevel = newName;
                    } else if (Objects.equals(ls.getExitTransitionRegular().destLevel, oldName)) {
                        ls.getExitTransitionRegular().destLevel = newName;
                    }
                    if (Objects.equals(ls.getEntranceTransitionRegular().departLevel, oldName)) {
                        ls.getEntranceTransitionRegular().departLevel = newName;
                    } else if (Objects.equals(ls.getExitTransitionRegular().departLevel, oldName)) {
                        ls.getExitTransitionRegular().departLevel = newName;
                    }
                }
            }

            for (ItemDistribution<? extends Bundlable> distr : itemDistributions) {
                if (distr instanceof ItemDistribution.Items) {
                    renameInvalidKeys((List<Item>) distr.getObjectsToDistribute(), oldName, newName);
                }
                if (distr.getLevels().contains(oldName)) {
                    distr.getLevels().remove(oldName);
                    distr.getLevels().add(newName);
                }
            }

            //Set level for keys in inv
            Items.updateKeys(oldName, newName);

            levelScheme.name = newName;
            if (!savedItself) {
                boolean unload = levelScheme.getLevel() != null;
                Level level;
                if (unload) level = levelScheme.loadLevel();
                else level = levelScheme.getLevel();
                level.name = newName;
                CustomDungeonSaves.saveLevel(level);
                if (unload) levelScheme.unloadLevel();
            }

            if (EditorScene.customLevel() != null) {
                Undo.reset();//TODO maybe not best solution to reset all, but UndoParts all store the old lvl name
                for (LevelTransition t : EditorScene.customLevel().transitions.values())
                    EditorScene.updateTransitionIndicator(t);
            }
            floors.remove(oldName);
            floors.put(newName, levelScheme);

            EditorScene.updateDepthIcon();

            CustomDungeonSaves.saveDungeon(this);

        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }

    }

    private boolean renameInvalidKeys(List<Item> items, String invalidLevelName, String newName) {
        if (items == null) return false;
        boolean removedSth = false;
        for (Item i : new ArrayList<>(items)) {
            if (isInvalidKey(i, invalidLevelName)) {
                ((Key) i).levelName = newName;
                removedSth = true;
            }
        }
        return removedSth;
    }


}