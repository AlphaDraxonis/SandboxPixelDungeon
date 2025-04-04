package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.EffectDuration;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.HeroSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.GhostQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.ImpQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.Quest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.recipes.CustomRecipe;
import com.shatteredpixel.shatteredpixeldungeon.editor.recipes.WndDisableRecipes;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.CustomDocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.PotionCocktail;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfWipeOut;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.RoomLayoutLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
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

@NotAllowedInLua
public class CustomDungeon implements Bundlable {

    public boolean damageImmune, seeSecrets, permaMindVision, permaInvis, permaKey, extraSpeed;
    public static boolean knowsEverything;

    private String name;
    private String lastEditedFloor;

    private String startFloor;
    private Set<String> ratKingLevels;
    private List<ItemDistribution<? extends Bundlable>> itemDistributions;
    private Map<String, LevelScheme> floors = new HashMap<>();

    //heroSubClasses assumes that there are exactly 2 subclasses per hero, see HeroSubClass.getIndex() for more details
    public boolean[] heroesEnabled, heroSubClassesEnabled;
    public HeroSettings.HeroStartItemsData[] startItems;

    public EffectDuration effectDuration = new EffectDuration();
    public int forceChallenges = 0;

    private final Object[] toolbarItems = new Object[QuickSlot.SIZE];
    public int lastSelectedToolbarSlot;

    public boolean downloaded;

    public boolean view2d = false;
    public boolean seeLevelOnDeath = true;
    public boolean notRevealSecrets = false;

    public Set<CustomTileLoader.SimpleCustomTile> customTiles;

    public List<CustomRecipe> recipes;
    public Set<Class<? extends Recipe>> blockedRecipes;
    public Set<Class<? extends Item>> blockedRecipeResults;

    public int nextParticleID = 1;
    public Map<Integer, CustomParticle.ParticleProperty> particles;

    public String dungeonScriptPath;

    public List<CustomDocumentPage> foundPages;

    public CustomDungeon(String name) {

        this.name = name;
        ratKingLevels = new HashSet<>();
        itemDistributions = new ArrayList<>(5);
        customTiles = new HashSet<>(5);
        recipes = new ArrayList<>(5);
        blockedRecipes = new HashSet<>(5);
        blockedRecipeResults = new HashSet<>(5);
        particles = new HashMap<>();
        foundPages = new ArrayList<>(2);
        heroesEnabled = new boolean[HeroClass.values().length];
        heroSubClassesEnabled = new boolean[heroesEnabled.length * 2];
        Arrays.fill(heroesEnabled, true);
        Arrays.fill(heroSubClassesEnabled, true);
        startItems = HeroSettings.HeroStartItemsData.getDefault();
        
        Dungeon.LimitedDrops.reset();
        Generator.fullReset();
        SpecialRoom.initForRun();
        SecretRoom.initForRun();
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
        if (item.customImage != null) {
            return new ItemSprite(item, item.glowing());
        }
        return new ItemSprite(getItemSpriteOnSheet(item), item.glowing());
    }

    public static int getItemSpriteOnSheet(Item item) {
        if (Dungeon.isLevelTesting()) {
            if (item instanceof Scroll || item instanceof Potion || item instanceof Ring)
                item.reset();//we want to use the color from the handler
            return item.image();
        }
        if (Dungeon.customDungeon == null) {
            if (item instanceof Scroll) {
                return ItemSpriteSheet.SCROLL_HOLDER;
            } else if (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew || item instanceof PotionCocktail)) {
                return ItemSpriteSheet.POTION_HOLDER;
            } else if (item instanceof Ring) {
                return ItemSpriteSheet.RING_HOLDER;
            }
            return item.image();
        }
        return Dungeon.customDungeon.getPrivateItemSpriteOnSheet(item);
    }

    private final int getPrivateItemSpriteOnSheet(Item item) {
        int code = -1;
        Class<? extends Item> c = item.getClass();
        if (item instanceof Scroll) {
            if (item instanceof ScrollOfWipeOut) return item.image;
            if (item instanceof ExoticScroll) c = ExoticScroll.exoToReg.get(c);
            code = (scrollRuneLabels == null || !scrollRuneLabels.containsKey(c)) ?
                    ItemSpriteSheet.SCROLL_HOLDER :
                    Scroll.runes.get(scrollRuneLabels.get(c)) + (item instanceof ExoticScroll ? 16 : 0);
        } else if (item instanceof Potion && !(item instanceof Elixir || item instanceof Brew || item instanceof PotionCocktail)) {
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

    public boolean maybeFixIncorrectNameEnding() {
        if (illegalNameEnding(name)) {
            name += " ";
            return true;
        }
        return false;
    }

    public static boolean illegalNameEnding(String s) {
        return s.endsWith(".") || s.endsWith("/") || s.endsWith("\\");
    }

    public static String maybeFixIncorrectNameEnding(String s) {
        if (s.endsWith(".")) {
            s += " ";
        }
        return s;
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
        for (LevelScheme ls : floors.values()) {
            if (ls.levelCreatedAfter == null) {
                ls.levelCreatedAfter = levelScheme.getName();
                levelScheme.levelCreatedBefore = ls.getName();
                break;
            }
        }
        floors.put(levelScheme.getName(), levelScheme);
    }

    public void removeFloor(LevelScheme levelScheme) {
        floors.remove(levelScheme.getName());

        LevelScheme levelSchemeWithDeletedLevelCreatedBefore = null;
        LevelScheme levelSchemeWithDeletedLevelCreatedAfter = null;

        String remove = levelScheme.getName();
        for (LevelScheme ls : floors.values()) {
            if (remove.equals(ls.levelCreatedBefore)) levelSchemeWithDeletedLevelCreatedBefore = ls;
            if (remove.equals(ls.levelCreatedAfter)) levelSchemeWithDeletedLevelCreatedAfter = ls;
        }
        //   1 -> 2 -> 3
        // after n before
        if (levelSchemeWithDeletedLevelCreatedBefore != null) levelSchemeWithDeletedLevelCreatedBefore.levelCreatedBefore =
                levelSchemeWithDeletedLevelCreatedAfter == null ? null : levelSchemeWithDeletedLevelCreatedAfter.getName();
        if (levelSchemeWithDeletedLevelCreatedAfter != null) levelSchemeWithDeletedLevelCreatedAfter.levelCreatedAfter =
                levelSchemeWithDeletedLevelCreatedBefore == null ? null : levelSchemeWithDeletedLevelCreatedBefore.getName();
    }

    public void initExitsFromPreviousFloor(LevelScheme newlyCreatedFloor) {
        LevelScheme current = getFloor(newlyCreatedFloor.levelCreatedBefore);
        if (current != null) {
            current.setToDefaultExits();
        }
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
        soTransmutation.getObjectsToDistribute().add(new StoneOfEnchantment());//I wonder if the comment in shatteredPD is a mistake...
        for (int i = 6; i < 20; i++) {
            if (i % 5 != 0) soTransmutation.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(soTransmutation);

        ItemDistribution.Items trinket = new ItemDistribution.Items(true);
        trinket.getObjectsToDistribute().add(new TrinketCatalyst());
        for (int i = 1; i <= 3; i++) {
            trinket.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(trinket);

        //TODO bei der Erstellung automatisch ggf zu Spawnitems adden (auch food) -> dafür CategoryPlaceholders
        ItemDistribution.Items stIntu = new ItemDistribution.Items(false);
        stIntu.getObjectsToDistribute().add(new StoneOfIntuition());
        for (int i = 1; i <= 3; i++) {
            stIntu.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(stIntu);

        ItemDistribution.Items stEnch = new ItemDistribution.Items(true);
        stEnch.getObjectsToDistribute().add(new StoneOfEnchantment());
        for (int i = 6; i <= 14; i++) {
            if (i != 10) stEnch.getLevels().add(Integer.toString(i));
        }
        itemDistributions.add(stEnch);

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

    public static boolean isEditing() {
        return EditorScene.isEditing;
    }

    public static boolean knowsEverything() {
        return knowsEverything || isEditing();
    }

    public static boolean showHiddenDoors() {
        return isEditing() || Dungeon.customDungeon.seeSecrets;
    }

    public void setItemInToolbar(int slot, EditorItem<?> item) {
        if (item == null) toolbarItems[slot] = null;
        else toolbarItems[slot] = new FindInBag(item.getObject());
    }

    public void restoreToolbar() {
        int slotBefore = lastSelectedToolbarSlot;
        EditorInventory.callStaticInitializers();
        for (int i = 0; i < toolbarItems.length; i++) {
            if (toolbarItems[i] != null) {
                Object obj = toolbarItems[i];
                if (!(obj instanceof FindInBag)) {
                    if (obj instanceof Integer) obj = new FindInBag(FindInBag.Type.TILE, obj, null);
                    else if (obj instanceof String) obj = new FindInBag(FindInBag.Type.CUSTOM_TILE, obj, null);
                    else if (obj instanceof CustomParticle.ParticleProperty) obj = new FindInBag(FindInBag.Type.PARTICLE, obj, null);
                    else if (obj == EditorItem.REMOVER_ITEM.getClass()) obj = new FindInBag(FindInBag.Type.REMOVER, null, null);
                    else obj = new FindInBag(FindInBag.Type.CLASS, obj, null);
                }
                QuickSlotButton.set(i, ((FindInBag) obj).getAsInBag());
            }
        }
        lastSelectedToolbarSlot = slotBefore;
    }


    private static final String NAME = "name";
    private static final String LAST_EDITED_FLOOR = "last_edited_floor";
    private static final String START_FLOOR = "start_floor";
    private static final String RAT_KING_LEVELS = "rat_king_levels";
    private static final String ITEM_DISTRIBUTION = "item_distribution";
    private static final String LEVEL_SCHEME = "level_scheme";
    private static final String REMOVE_NEXT_SCROLL = "remove_next_scroll";
    private static final String DOWNLOADED = "downloaded";
    private static final String CUSTOM_TILES = "custom_tiles";
    private static final String RECIPES = "recipes";
    private static final String BLOCKED_RECIPES = "blocked_recipe_classes";
    private static final String BLOCKED_RECIPE_RESULTS = "blocked_recipe_results";
    private static final String PARTICLES = "particles";
    private static final String DUNGEON_SCRIPT_PATH = "dungeon_script_path";
    private static final String LIMITED_DROPS = "limited_drops";
    private static final String GENERATOR = "generator";
    private static final String SPECIAL_ROOMS = "special_rooms";
    private static final String SECRET_ROOMS = "secret_rooms";

    private static final String RUNE_LABELS = "rune_labels";
    private static final String RUNE_CLASSES = "rune_classes";
    private static final String COLOR_LABELS = "color_labels";
    private static final String COLOR_CLASSES = "color_classes";
    private static final String GEM_LABELS = "gem_labels";
    private static final String GEM_CLASSES = "gem_classes";
    private static final String TOOLBAR_SLOT = "toolbar_slot_";
    private static final String LAST_SELECTED_TOOLBAR_SLOT = "last_selected_toolbar_slot";
    private static final String HEROES_ENABLED = "heroes_enabled";
    private static final String HERO_SUBCLASSES_ENABLED = "hero_subclasses_enabled";
    private static final String EFFECT_DURATION = "effect_duration";
    private static final String START_ITEMS = "start_items";
    private static final String FORCE_CHALLENGES = "force_challenges";
    private static final String VIEW_2D = "view_2d";
    private static final String SEE_LEVEL_ON_DEATH = "see_level_on_death";
    private static final String NOT_REVEAL_SECRETS = "not_reveal_secrets";
    private static final String FOUND_PAGES = "found_pages";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        bundle.put(LAST_EDITED_FLOOR, lastEditedFloor);
        if (startFloor != null) bundle.put(START_FLOOR, startFloor);
        bundle.put(RAT_KING_LEVELS, ratKingLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(ITEM_DISTRIBUTION, itemDistributions);
        bundle.put(REMOVE_NEXT_SCROLL, removeNextScroll);
        bundle.put(DOWNLOADED, downloaded);
        bundle.put(HEROES_ENABLED, heroesEnabled);
        bundle.put(HERO_SUBCLASSES_ENABLED, heroSubClassesEnabled);
        bundle.put(EFFECT_DURATION, effectDuration);
        bundle.put(START_ITEMS, Arrays.asList(startItems));
        bundle.put(CUSTOM_TILES, customTiles);
        bundle.put(RECIPES, recipes);
        bundle.put(VIEW_2D, view2d);
        bundle.put(SEE_LEVEL_ON_DEATH, seeLevelOnDeath);
        bundle.put(NOT_REVEAL_SECRETS, notRevealSecrets);
        bundle.put(FORCE_CHALLENGES, forceChallenges);

        bundle.put(FOUND_PAGES, foundPages);

        bundle.put(BLOCKED_RECIPES, blockedRecipes.toArray(EditorUtilities.EMPTY_CLASS_ARRAY));
        bundle.put(BLOCKED_RECIPE_RESULTS, blockedRecipeResults.toArray(EditorUtilities.EMPTY_CLASS_ARRAY));

        bundle.put(PARTICLES, particles.values());
        
        Bundle node = new Bundle();
        Dungeon.LimitedDrops.store(node);
        bundle.put(LIMITED_DROPS, node);
        
        node = new Bundle();
        Generator.storeInBundle(node);
        bundle.put(GENERATOR, node);
        
        node = new Bundle();
        SpecialRoom.storeRoomsInBundle(node);
        bundle.put(SPECIAL_ROOMS, node);
        
        node = new Bundle();
        SecretRoom.storeRoomsInBundle(node);
        bundle.put(SECRET_ROOMS, node);


        if (dungeonScriptPath != null)
            bundle.put(DUNGEON_SCRIPT_PATH, dungeonScriptPath);


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
            if (levelScheme.getType() != RoomLayoutLevel.class) {
                bundle.put(LEVEL_SCHEME + "_" + i, levelScheme);
                i++;
            }
        }

        bundle.put(LAST_SELECTED_TOOLBAR_SLOT, lastSelectedToolbarSlot);
        for (int j = 0; j < toolbarItems.length; j++) {
            if (toolbarItems[j] != null) {
                Object obj = toolbarItems[j];
                if (!(obj instanceof FindInBag)) {
                    if (obj instanceof CustomParticle.ParticleProperty) obj = new FindInBag(FindInBag.Type.PARTICLE, ((CustomParticle.ParticleProperty) obj).particleID(), null);
                    else {
                        obj = new FindInBag(obj);
                    }
                }
                bundle.put(TOOLBAR_SLOT + j, (FindInBag) obj);
            }
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        name = bundle.getString(NAME);
        lastEditedFloor = bundle.getString(LAST_EDITED_FLOOR);
        if (bundle.contains(START_FLOOR)) startFloor = bundle.getString(START_FLOOR);
        ratKingLevels = new HashSet<>(Arrays.asList(bundle.getStringArray(RAT_KING_LEVELS)));
        itemDistributions = new ArrayList<>(5);
        if (bundle.contains(ITEM_DISTRIBUTION)) {
            Collection<?> col = bundle.getCollection(ITEM_DISTRIBUTION);
            itemDistributions.addAll((Collection<? extends ItemDistribution<? extends Bundlable>>) col);
        }
        removeNextScroll = bundle.getBoolean(REMOVE_NEXT_SCROLL);
        effectDuration.load((EffectDuration) bundle.get(EFFECT_DURATION));
        downloaded = bundle.getBoolean(DOWNLOADED);
        forceChallenges = bundle.getInt(FORCE_CHALLENGES);
        view2d = bundle.getBoolean(VIEW_2D);
        seeLevelOnDeath = !bundle.contains(SEE_LEVEL_ON_DEATH) || bundle.getBoolean(SEE_LEVEL_ON_DEATH);
        notRevealSecrets = bundle.getBoolean(NOT_REVEAL_SECRETS);
        if (bundle.contains(HEROES_ENABLED)) {
            heroesEnabled = bundle.getBooleanArray(HEROES_ENABLED);
            if (heroesEnabled.length < HeroClass.values().length) {
                boolean[] temp = new boolean[HeroClass.values().length];
                Arrays.fill(temp, true);
                System.arraycopy(heroesEnabled, 0, temp, 0, heroesEnabled.length);
                heroesEnabled = temp;
            }
        }
        else {
            heroesEnabled = new boolean[HeroClass.values().length];
            Arrays.fill(heroesEnabled, true);
        }
        if (bundle.contains(HERO_SUBCLASSES_ENABLED)) {
            heroSubClassesEnabled = bundle.getBooleanArray(HERO_SUBCLASSES_ENABLED);
            if (heroSubClassesEnabled.length < heroesEnabled.length * 2) {
                boolean[] temp = new boolean[heroesEnabled.length * 2];
                Arrays.fill(temp, true);
                System.arraycopy(heroSubClassesEnabled, 0, temp, 0, heroSubClassesEnabled.length);
                heroSubClassesEnabled = temp;
            }
        }
        else {
            heroSubClassesEnabled = new boolean[heroesEnabled.length * 2];
            Arrays.fill(heroSubClassesEnabled, true);
        }
        if (bundle.contains(START_ITEMS)) {
            startItems = new HeroSettings.HeroStartItemsData[heroesEnabled.length + 1];
            int i = 0;
            for (Bundlable heroStartItemsData : bundle.getCollection(START_ITEMS)) {
                startItems[i] = (HeroSettings.HeroStartItemsData) heroStartItemsData;
                startItems[i].maybeInitDefault(i);
                i++;
            }
            while (i < startItems.length) {
                startItems[i] = HeroSettings.HeroStartItemsData.getDefault(i);
                i++;
            }
        } else {
            startItems = HeroSettings.HeroStartItemsData.getDefault();
        }
        customTiles = new HashSet<>(5);
        if (bundle.contains(CUSTOM_TILES)) {
            for (Bundlable b : bundle.getCollection(CUSTOM_TILES)) {
                customTiles.add((CustomTileLoader.SimpleCustomTile) b);
            }
        }
        recipes = new ArrayList<>(5);
        if (bundle.contains(RECIPES)) {
            for (Bundlable b : bundle.getCollection(RECIPES)) {
                recipes.add((CustomRecipe) b);
            }
        }
        blockedRecipes = new HashSet<>();
        if (bundle.contains("blocked_recipes")) {
            int[] intArray = bundle.getIntArray("blocked_recipes");
            if (intArray != null) {
                for (int i : intArray)
                    blockedRecipes.add(WndDisableRecipes.indexToRecipe(i));
            }
        } else {
            for (Class<?> c : bundle.getClassArray(BLOCKED_RECIPES))
                blockedRecipes.add((Class<? extends Recipe>) c);
        }
        
        
        blockedRecipeResults = new HashSet<>(5);
        if (bundle.contains(BLOCKED_RECIPE_RESULTS))
            Collections.addAll(blockedRecipeResults, (Class<? extends Item>[]) bundle.getClassArray(BLOCKED_RECIPE_RESULTS));

        particles = new HashMap<>();
        Collection<Bundlable> collection = bundle.getCollection(PARTICLES);
        for (Bundlable bundlable : collection) {
            CustomParticle.ParticleProperty p = (CustomParticle.ParticleProperty) bundlable;
            particles.put(p.particleID(), p);
        }
        updateNextParticleID();
        
        if (bundle.contains(LIMITED_DROPS)) Dungeon.LimitedDrops.restore(bundle.getBundle(LIMITED_DROPS));
        else Dungeon.LimitedDrops.reset();
        if (bundle.contains(GENERATOR)) Generator.restoreFromBundle(bundle.getBundle(GENERATOR));
        else Generator.fullReset();
        
        if (bundle.contains(SPECIAL_ROOMS)) SpecialRoom.restoreRoomsFromBundle(bundle.getBundle(SPECIAL_ROOMS));
        else SpecialRoom.restoreRoomsFromBundle(new Bundle());
        if (bundle.contains(SECRET_ROOMS)) SecretRoom.restoreRoomsFromBundle(bundle.getBundle(SECRET_ROOMS));
        else SecretRoom.restoreRoomsFromBundle(new Bundle());
        
        if (Dungeon.dungeonScript != null) {
            Dungeon.dungeonScript.unloadScript();
        }
        if (bundle.contains(DUNGEON_SCRIPT_PATH)) {
            dungeonScriptPath = bundle.getString(DUNGEON_SCRIPT_PATH);
        }

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

        foundPages = new ArrayList<>(2);
        if (bundle.contains(FOUND_PAGES)) {
            for (Bundlable b : bundle.getCollection(FOUND_PAGES))
                foundPages.add((CustomDocumentPage) b);
        }

        CustomObjectManager.loadUserContentFromFiles(bundle, this);

        int i = 0;
        while (bundle.contains(LEVEL_SCHEME + "_" + i)) {
            LevelScheme ls = (LevelScheme) bundle.get(LEVEL_SCHEME + "_" + i);
            ls.customDungeon = this;
            floors.put(ls.getName(), ls);
            i++;

            if ("".equals(ls.levelCreatedBefore) && name.equals(getStart())) ls.levelCreatedBefore = null;
            if ("".equals(ls.levelCreatedAfter) && name.equals(getLastEditedFloor())) ls.levelCreatedAfter = null;
        }
        lastSelectedToolbarSlot = bundle.getInt(LAST_SELECTED_TOOLBAR_SLOT);
        for (i = 0; i < toolbarItems.length; i++) {
            if (bundle.contains(TOOLBAR_SLOT + i))
                toolbarItems[i] = bundle.get(TOOLBAR_SLOT + i);

            else if (bundle.contains("toolbar_item_" + i))
                toolbarItems[i] = bundle.getClass("toolbar_item_" + i);
            else if (bundle.contains("toolbar_item_int_" + i))
                toolbarItems[i] = bundle.getInt("toolbar_item_int_" + i);
            else if (bundle.contains("toolbar_item_string_" + i))
                toolbarItems[i] = bundle.getString("toolbar_item_string_" + i);
            else if (bundle.contains("toolbar_item_bundlable" + i))
                toolbarItems[i] = bundle.get("toolbar_item_bundlable" + i);
        }
    }

    public void updateNextParticleID() {
        while (particles.containsKey(nextParticleID))
            nextParticleID++;
    }

    public CustomDungeonSaves.Info createInfo() {
        return new CustomDungeonSaves.Info(getName(), Game.versionCode, getNumFloors(), 0/*hashCode()*/, downloaded);
    }

    void addRatKingLevel(String name) {
        ratKingLevels.add(name);
    }

    public String getAnyRatKingLevel() {
        if (ratKingLevels.isEmpty()) return null;
        return ratKingLevels.iterator().next();
    }


    public void doOnEverything(boolean loadLevel, Function<GameObject, GameObject.ModifyResult> whatToDo, boolean resetUndo,
                                                   Function<CustomLevel, Boolean> extraTasksCL, Runnable ifNoCL) throws IOException {

        for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
            try {
                CustomDungeon.doOnEverythingInLevelScheme(ls, loadLevel, whatToDo, resetUndo, extraTasksCL, ifNoCL);
            } catch (IOException e) {
                Game.reportException(e);
            }
        }
        for (ItemDistribution<? extends Bundlable> distr : itemDistributions) {
            GameObject.doOnAllGameObjectsList(distr.getObjectsToDistribute(), whatToDo);
        }

        for (HeroSettings.HeroStartItemsData si : startItems) {
            si.doOnAllGameObjects(whatToDo);
        }

        for (CustomRecipe recipe : Dungeon.customDungeon.recipes) {
            recipe.doOnAllGameObjects(whatToDo);
        }

        QuickSlotButton.doOnAll(whatToDo);

        EditorScene.updateDepthIcon();
        EditorScene.updatePathfinder();

        CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
    }

    public static void doOnEverythingInLevelScheme(LevelScheme levelScheme, boolean loadLevel, Function<GameObject, GameObject.ModifyResult> whatToDo, boolean resetUndo,
                                                   Function<CustomLevel, Boolean> extraTasksCL, Runnable ifNoCL) throws IOException {

        GameObject.doOnAllGameObjectsList(levelScheme.itemsToSpawn, whatToDo);
        GameObject.doOnAllGameObjectsList(levelScheme.mobsToSpawn, whatToDo);
        GameObject.doOnAllGameObjectsList(levelScheme.roomsToSpawn, whatToDo);

        if (!loadLevel) return;

        if (levelScheme.getType() == CustomLevel.class) {
            boolean load = levelScheme.getLevel() == null;
            CustomLevel level;
            if (load) level = (CustomLevel) levelScheme.loadLevel();
            else level = (CustomLevel) levelScheme.getLevel();
            if (level == null) return;//skip if level couldn't be loaded

            boolean saveNeeded = false;

            if (extraTasksCL != null && extraTasksCL.apply(level)) saveNeeded = true;

            if (GameObject.doOnAllGameObjectsSparseArray(level.heaps, whatToDo)) saveNeeded = true;
            for (Heap h : level.heaps.valueList()) {
                if (h.isEmpty()) {
                    level.heaps.remove(h.pos);
                    h.destroyImages();
                }
            }

            for (Zone zone : level.zoneMap.values()) {
                if (zone.mobRotation != null) {
                    if (GameObject.doOnAllGameObjectsList(zone.mobRotation.distrSlots, whatToDo)) saveNeeded = true;
                    for (ItemsWithChanceDistrComp.ItemWithCount mobRotationSlot
                            : zone.mobRotation.distrSlots.toArray(new ItemsWithChanceDistrComp.ItemWithCount[0])) {
                        if (mobRotationSlot.items == null || mobRotationSlot.items.isEmpty())
                            zone.mobRotation.distrSlots.remove(mobRotationSlot);
                    }
                }
            }

            if (GameObject.doOnAllGameObjectsSet(level.mobs, whatToDo)) saveNeeded = true;

			if (GameObject.doOnAllGameObjectsList(level.getMobRotationVar().distrSlots, whatToDo)) saveNeeded = true;
			for (ItemsWithChanceDistrComp.ItemWithCount mobRotationSlot
					: level.getMobRotationVar().distrSlots.toArray(new ItemsWithChanceDistrComp.ItemWithCount[0])) {
				if (mobRotationSlot.items == null || mobRotationSlot.items.isEmpty())
					level.getMobRotationVar().distrSlots.remove(mobRotationSlot);
			}


			if (GameObject.doOnAllGameObjectsSparseArray(level.traps, whatToDo)) saveNeeded = true;

            if (GameObject.doOnAllGameObjectsSparseArray(level.plants, whatToDo)) saveNeeded = true;

            //Remove invalid keys as sacrificial fire reward
            SacrificialFire sacrificialFire = level.blobs.getOnly(SacrificialFire.class);
            if (GameObject.doOnSingleObject(sacrificialFire, whatToDo, newValue -> {
                if (newValue == null) level.blobs.remove(SacrificialFire.class);
                else level.blobs.put(SacrificialFire.class, newValue);
            })) saveNeeded = true;

			if (GameObject.doOnAllGameObjectsSet(level.customTiles, whatToDo)) saveNeeded = true;
			if (GameObject.doOnAllGameObjectsSet(level.customWalls, whatToDo)) saveNeeded = true;

            if (load) {
                if (saveNeeded) CustomDungeonSaves.saveLevel(level);
                levelScheme.unloadLevel();
            }
            else if (level == EditorScene.getCustomLevel()) {
                if (resetUndo) Undo.reset();//TODO maybe not best solution to reset all
                EditorScene.updateHeapImagesAndSubIcons();
            }
        } else {
            if (ifNoCL != null) ifNoCL.run();
        }

    }


    public void delete(LevelScheme levelScheme) throws IOException {

        if (!LuaManager.checkAccess("customDungeon.delete")) return;

        String n = levelScheme.getName();
        ratKingLevels.remove(n);

        removeFloor(levelScheme);
        List<LevelScheme> fs = new ArrayList<>(levelSchemes());
        if (n.equals(startFloor)) {
            Collections.sort(fs);
            if (fs.isEmpty()) {
                startFloor = null;
                SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            } else startFloor = fs.get(0).getName();
        }
        if (EditorScene.getCustomLevel() != null && EditorScene.getCustomLevel().levelScheme == levelScheme) {
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
                Level openLevel = openS.loadLevel();
                if(openLevel == null)  SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
                else {
                    EditorScene.open((CustomLevel) openLevel);
                    EditorScene.show(new WndSwitchFloor());
                }
            }

        }

        Function<GameObject, GameObject.ModifyResult> whatToDo = obj -> obj.onDeleteLevelScheme(n);

        //Remove transitions and keys
        for (LevelScheme ls : fs) {
            if (n.equals(ls.getChasm())) ls.setChasm(null, false);
            if (n.equals(ls.getPassage())) ls.setPassage(null);

            doOnEverythingInLevelScheme(ls, true, whatToDo, true, level -> {

                Set<Integer> toRemoveTransitions = new HashSet<>(4);
                for (LevelTransition transition : level.transitions.values()) {
                    if (transition != null && Objects.equals(transition.destLevel, n)) {
                        toRemoveTransitions.add(transition.cell());
                        if (level == EditorScene.getCustomLevel()) EditorScene.remove(transition);
                    }
                }

                boolean changedSth = !toRemoveTransitions.isEmpty();

                for (int key : toRemoveTransitions) level.transitions.remove(key);

                for (Zone zone : level.zoneMap.values()) {
                    if (zone.zoneTransition != null) {
                        if (Objects.equals(zone.zoneTransition.destLevel, n)) {
                            zone.zoneTransition = null;
                            changedSth = true;
                        }
                    }
                }

                return changedSth;
            }, () -> {
                if (Objects.equals(ls.getEntranceTransitionRegular().destLevel, n)) {
                    ls.getEntranceTransitionRegular().destLevel = Level.SURFACE;
                    ls.getEntranceTransitionRegular().destCell = -1;
                } else if (Objects.equals(ls.getExitTransitionRegular().destLevel, n)) {
                    ls.getExitTransitionRegular().destLevel = null;
                    ls.getExitTransitionRegular().destCell = -1;
                }
            });
        }

        for (ItemDistribution<? extends Bundlable> distr : itemDistributions.toArray(new ItemDistribution[0])) {
            GameObject.doOnAllGameObjectsList(distr.getObjectsToDistribute(), whatToDo);
            distr.getLevels().remove(n);
            if (distr.getLevels().isEmpty()) {
                itemDistributions.remove(distr);
            }
        }

        for (HeroSettings.HeroStartItemsData si : startItems) {
            si.doOnAllGameObjects(whatToDo);
        }

        for (CustomRecipe recipe : recipes) {
            recipe.doOnAllGameObjects(whatToDo);
        }

        //Set level for keys in inv
        Items.updateKeys(n, EditorScene.getCustomLevel() == null ? null : EditorScene.getCustomLevel().name);

        EditorScene.updatePathfinder();

        CustomDungeonSaves.deleteLevelFile(n);
        CustomDungeonSaves.saveDungeon(this);
    }

    public void renameZone(Zone zone, String newName) {

        if (!LuaManager.checkAccess("renameZone")) return;

        try {
            String oldName = zone.getName();
            zone.name = newName;

            //Remove transitions and keys
            for (LevelScheme ls : levelSchemes()) {

                if (ls.getType() == CustomLevel.class) {
                    boolean load = ls.getLevel() == null;
                    Level level;
                    if (load) level = ls.loadLevel();
                    else level = ls.getLevel();
                    if (level == null) continue;//skip if level couldn't be loaded

                    boolean needsSave = false;

                    for (Zone z : level.zoneMap.values()) {
                        if (oldName.equals(z.chasmDestZone)) {
                            z.chasmDestZone = newName;
                            needsSave = true;
                        }
                        if (z == zone) {
                            ls.zones.remove(oldName);
                            ls.zones.add(newName);
                            needsSave = true;
                        }
                    }

                    if (needsSave) CustomDungeonSaves.saveLevel(level);
                    if (load) ls.unloadLevel();
                    else if (level == EditorScene.getCustomLevel()) {
                        Undo.reset();//TODO maybe not best solution to reset all
                    }
                } else {
                    //can't contain zones
                }
            }

            EditorScene.updatePathfinder();

            CustomDungeonSaves.saveDungeon(this);

            if (zone == ZonePrompt.getSelectedZone()) ZonePrompt.setSelectedZone(zone);

        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
    }

    public void deleteZone(Zone zone) throws IOException {

        if (!LuaManager.checkAccess("deleteZone")) return;

        String n = zone.getName();

        //Remove transitions and keys
        for (LevelScheme ls : levelSchemes()) {

            if (ls.getType() == CustomLevel.class) {
                boolean load = ls.getLevel() == null;
                Level level;
                if (load) level = ls.loadLevel();
                else level = ls.getLevel();
                if (level == null) continue;//skip if level couldn't be loaded

                boolean needsSave = false;

                for (Zone z : level.zoneMap.values()) {
                    if (n.equals(z.chasmDestZone)) {
                        z.chasmDestZone = null;
                        needsSave = true;
                    }
                }

                if (needsSave || Dungeon.level == level) CustomDungeonSaves.saveLevel(level);
                if (load) ls.unloadLevel();
                else if (level == EditorScene.getCustomLevel()) {
                    level.zoneMap.remove(n);
                    level.levelScheme.zones.remove(n);
                    if (zone.numCells() > 0) {
                        Zone.setupZoneArray(level);
                        Undo.reset();//TODO maybe not best solution to reset all
                    }
                    EditorScene.remove(zone);
                }
            } else {
                //can't contain zones
            }
        }

        EditorScene.updatePathfinder();

        CustomDungeonSaves.saveDungeon(this);

        EditorScene.updateMap();

        if (zone == ZonePrompt.getSelectedZone()) ZonePrompt.setSelectedZone(ZonePrompt.getFirstZoneAvailable(EditorScene.getCustomLevel()));
    }

    public static void deleteDungeon(String name) {

        if (!LuaManager.checkAccess("deleteDungeon")) return;

        if (Dungeon.customDungeon != null && Dungeon.customDungeon.name.equals(name))
            Dungeon.customDungeon = null;
        CustomDungeonSaves.deleteDungeonFile(name);
        CustomTileLoader.dungeonNameOfLastLoadedTiles = null;
    }

    public static CustomDungeonSaves.Info copyDungeon(String oldName, String newName) {

        if (!LuaManager.checkAccess("copyDungeon")) return null;

        try {
            CustomDungeon dungeon = null;
            try {
                dungeon = CustomDungeonSaves.loadDungeon(oldName);
                CustomDungeonSaves.copyLevelsForNewGame(oldName, CustomDungeonSaves.DUNGEON_FOLDER + newName.replace(' ', '_') + "/");
            } catch (CustomDungeonSaves.RenameRequiredException e) {
                return null;
            }
            dungeon.name = newName;
            CustomDungeonSaves.saveDungeon(dungeon);
            return dungeon.createInfo();
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
            throw new RuntimeException(e);
        }
    }

    public LevelScheme copyLevel(LevelScheme levelScheme, String newName) {

        if (!LuaManager.checkAccess("copyLevel")) return null;

        Bundle bundle = new Bundle();
        bundle.put("LevelScheme", levelScheme);
        LevelScheme ls = (LevelScheme) bundle.get("LevelScheme");
        ls.name = newName;
        addFloor(ls);

        String oldName = levelScheme.getName();

        if (oldName.equals(ls.getChasm())) ls.setChasm(newName, false);
        if (oldName.equals(ls.getPassage())) ls.setPassage(newName);

        if (oldName.equals(ls.levelCreatedBefore)) ls.levelCreatedBefore = newName;
        if (oldName.equals(ls.levelCreatedAfter)) ls.levelCreatedAfter = newName;

        Function<GameObject, GameObject.ModifyResult> whatToDo = obj -> obj.onRenameLevelScheme(oldName, newName);

		try {
            if (levelScheme.getType() == CustomLevel.class) {
                bundle = new Bundle();
                bundle.put("Level", levelScheme.loadLevel());
                CustomLevel level = (CustomLevel) bundle.get("Level");
                level.name = newName;
                CustomDungeonSaves.saveLevel(level);
            }

			doOnEverythingInLevelScheme(ls, true, whatToDo, true, level -> {

				//TODO refactor to use the same as renaming logic!
				for (LevelTransition transition : level.transitions.values()) {
					if (transition != null) {
						if (Objects.equals(transition.destLevel, oldName))
							transition.destLevel = newName;
						if (Objects.equals(transition.departLevel, oldName))
							transition.departLevel = newName;
					}
				}
				for (Zone zone : level.zoneMap.values()) {
					if (zone.zoneTransition != null) {
						if (Objects.equals(zone.zoneTransition.destLevel, oldName))
							zone.zoneTransition.destLevel = newName;
						if (Objects.equals(zone.zoneTransition.departLevel, oldName))
							zone.zoneTransition.departLevel = newName;
					}
				}

				return true;
			}, null);

            lastEditedFloor = newName;
            CustomDungeonSaves.saveDungeon(this);

            EditorScene.updatePathfinder();

            return ls;
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
            return null;
        }
    }

    public static void renameDungeon(String oldName, String newName) {

        if (!LuaManager.checkAccess("renameDungeon")) return;

        try {
            CustomDungeon dungeon = CustomDungeonSaves.renameDungeon(oldName, newName);
            if (dungeon != null) {
                if (oldName.equals(CustomTileLoader.dungeonNameOfLastLoadedTiles)) CustomTileLoader.dungeonNameOfLastLoadedTiles = newName;
                dungeon.name = newName;
            }
            else throw new IOException("Renaming was not successful!");
            CustomDungeonSaves.saveDungeon(dungeon);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
            throw new RuntimeException(e);
        }
    }

    public void renameLevel(LevelScheme levelScheme, String newName) {

        if (!LuaManager.checkAccess("renameLevel")) return;

        try {
            String oldName = levelScheme.getName();

            if (ratKingLevels.contains(oldName)) {
                ratKingLevels.remove(oldName);
                ratKingLevels.add(newName);
            }

            if (oldName.equals(lastEditedFloor)) lastEditedFloor = newName;
            if (oldName.equals(startFloor)) startFloor = newName;

            Function<GameObject, GameObject.ModifyResult> whatToDo = obj -> obj.onRenameLevelScheme(oldName, newName);

            //Change transitions and keys
            for (LevelScheme ls : floors.values()) {

                if (oldName.equals(ls.getChasm())) ls.setChasm(newName, false);
                if (oldName.equals(ls.getPassage())) ls.setPassage(newName);

                if (oldName.equals(ls.levelCreatedBefore)) ls.levelCreatedBefore = newName;
                if (oldName.equals(ls.levelCreatedAfter)) ls.levelCreatedAfter = newName;

                doOnEverythingInLevelScheme(ls, true, whatToDo, true, level -> {
                    boolean changedSth = false;

                    for (LevelTransition transition : level.transitions.values()) {
                        if (transition != null) {
                            if (Objects.equals(transition.destLevel, oldName)) {
                                transition.destLevel = newName;
                                changedSth = true;
                            }
                            if (Objects.equals(transition.departLevel, oldName)) {
                                transition.departLevel = newName;
                                changedSth = true;
                            }
                        }
                    }
                    for (Zone zone : level.zoneMap.values()) {
                        if (zone.zoneTransition != null) {
                            if (Objects.equals(zone.zoneTransition.destLevel, oldName)) {
                                zone.zoneTransition.destLevel = newName;
                                changedSth = true;
                            }
                            if (Objects.equals(zone.zoneTransition.departLevel, oldName)) {
                                zone.zoneTransition.departLevel = newName;
                                changedSth = true;
                            }
                        }
                    }
                    return changedSth;
                }, () -> {
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
                });

            }

            for (ItemDistribution<? extends Bundlable> distr : itemDistributions) {
                GameObject.doOnAllGameObjectsList(distr.getObjectsToDistribute(), whatToDo);
                if (distr.getLevels().contains(oldName)) {
                    distr.getLevels().remove(oldName);
                    distr.getLevels().add(newName);
                }
            }

            for (HeroSettings.HeroStartItemsData si : startItems) {
                si.doOnAllGameObjects(whatToDo);
            }

            for (CustomRecipe recipe : recipes) {
                recipe.doOnAllGameObjects(whatToDo);
            }

            //Set level for keys in inv
            Items.updateKeys(oldName, newName);

            levelScheme.name = newName;
            if (levelScheme.getType() == CustomLevel.class) {
                boolean unload = levelScheme.getLevel() == null;
                Level level;
                if (unload) level = levelScheme.loadLevel();
                else level = levelScheme.getLevel();
                if (level != null) {
                    level.name = newName;
                    CustomDungeonSaves.saveLevel(level);
                    if (unload) levelScheme.unloadLevel();
                }
            }

            if (EditorScene.getCustomLevel() != null) {
                Undo.reset();//TODO maybe not best solution to reset all, but UndoParts all store the old lvl name
                for (LevelTransition t : EditorScene.getCustomLevel().transitions.values())
                    EditorScene.updateTransitionIndicator(t);
            }
            floors.remove(oldName);
            floors.put(newName, levelScheme);

            EditorScene.updateDepthIcon();

            EditorScene.updatePathfinder();

            if (levelScheme.getType() == CustomLevel.class)
                CustomDungeonSaves.deleteLevelFile(oldName);

            CustomDungeonSaves.saveDungeon(this);

            if (Dungeon.levelName.equals(oldName)) Dungeon.levelName = newName;

        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }

    }


    @Override
    public int hashCode() {
        Bundle bundle = new Bundle();
        bundle.put("Dungeon", this);

        List<LevelScheme> sortedFloors = new ArrayList<>(floors.values());
        Collections.sort(sortedFloors);
        int i = 0;
        for (LevelScheme floor : sortedFloors) {
            if (floor.getType() == CustomLevel.class) {
                boolean load = floor.getLevel() == null;
                if (load) floor.loadLevel();
                if (floor.getLevel() == null) continue;//skip if level couldn't be loaded
                bundle.put(Integer.toString(i), floor.getLevel());
                if (load) floor.unloadLevel();
                i++;
            }
        }
        EditorScene.updatePathfinder();

        return bundle.toString().hashCode();
    }

    public static String calculateHash(String dungeonName) {
        try {
            return Integer.toHexString(CustomDungeonSaves.loadDungeon(dungeonName).hashCode());
        } catch (Exception e) {
            return "ERROR";
        }
    }
}
