package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Thief;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
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

    public boolean damageImmune, seeSecrets, permaMindVision, permaInvis, permaKey;// maybe add ScrollOfDebug?

    //FIXME: Was noch zu tun ist  FIXME FIXME TODO System.err.println()
    //general floor overview stuff
    //settings für EditorScene
    //select builder and painter

    //Custom mob attacks: externalise
    //and search: used so resistances can differentiate between melee and magical attacks

    //maybe ability to randomize loot drops between some amounts like 1-9 quantity for example

    //TODO if dungeon list is empty, show button to be the first uploader

    //FIXME after uploading a dungeon with own custom tiles, they disappperard from the map! and the tile tab was hidde

    //make you able to change enemy view distance or tiles until enemies notice your presence
    //also turns to wake up before enemies notice you

    //TODO make the tool that allows to select a thing from placed things automatically copy a mob's stats when selecting it

    //killing bosses doesn't remove the floor lock

    //make random item as loot list

    //halls boss level does not spawn yog if as template

    //Crystals etc not display on halls level custom tile

    //Make halls boss custom tile as normal ct

    //strictly differentiate between custom tiles/walls
    //Make all tiles walls????

    //TODO rename foliage to ShaftsOfLight

    //Shrouding fog trap instantly crashes the game

    //color walls

    //Barrier:
    //Walls in your level that can be placed over traps, chasms, or floors.  You can see it in the editor but the barrier is invisible to you when playing the level (red x maybe).
    //You can see beyond the barrier.

    //Custom music

    //The option to edit the description of all items should be good, like editing plants, maybe making it so that when you touch an ice plant for example,
    // it can drop an item, I don't already have it, But if not, it would be great!

    //A couple visual effects:
    //Water splash (when a mob or player steps over the tile, it makes a splash sound and shows the rings)
    //Chasm Fog (those weird particles above the Chasm)

    //add change-log

    //make you able to change what mobs will spawn in some areas
    //for an example, b areas can spawn an ally dm 300

    //Changeable stats on the mob rotation
    //ontop pf that, a way to make some of them allys too.

    //[] Hero inaccessible (acts like a wall for players that they can see through)
    //
    //[] Mob inaccessible (acts like a wall for monsters that they can see through)
    //
    //[☑] Sight (when checked off, makes it so the player can't see passed this zone, on by default]
    //
    //[] Player buff (adds buffs (or debuffs) to a player when they enter the cell.  Each debuff/buff lasts permanently until they leave the cell, which then will disappear after they leave) (buffs applied prior to entering will also be removed, so be careful)
    //
    //[] Mob buff (same as above, but for monsters).
    //
    //[] Monster Cycle (allows mobs of a completely seperate mob cycle to spawn in this zone, ignoring the levels current mob cycle).

    //Custom alchemy for specific pots
    //Maybe even custom sprites for items

    //Enemy buff ideas: Amok, Corrupted, Confusion, Prismatic Image, Terror
    //
    //Enemy Glyph ideas: Enemy just gains the Glyph that can be applied to an armor.

    //Suggestion:  for summoner type enemies, allow us to cycle what they summon (like I want an enemy to summon a skeleton AND a wraith)

    //Similar to random items, how about some random traps?
    //
    //a random trap cell will have a cell be any trap among ones chosen by the map creator.
    //
    //Random traps take [Visibility], [Active], [Searchable] [Revealed when activated], and [Disarmed when activated] buttons.
    // For teleporting traps like gateway/warpway, it will always default to random.
    //On another note... Unstable Trap [gray color]: when this trap is stepped on, it will activate a random trap effect (like the Unstable enchantment).
    // Description: "This trap radiates chaotic energy, acting as a different trap when stepped on"


    //Scale mobs if their normal stats editor is disabled: not just everything

    //Scroll of Debug with interface, reference table and commands, use reflection to access ALL methods
    //surround method calls with try-catch-block, say warning that some variables might have been modified if method not @pure
    //add simple ways to add new enemies to map, or find one

    //add way to share dungeons in game:
    //check GithubUpdates for code examples for server
    //access via new button directly from title scene
    //wnd with loading screen, sending request to server
    //ui for selecting dungeons (simple list, no search option in beginning)
    //preview with image? and download button
    //option to upload raw files or converted as .dun
    //upload as bug report

    //if possible make a release that can be downloaded in this format (taken ShPD for example) -> as .exe  no

    //"Hero creator" chocked with a pixel editor for the hero, basic starting equipment, abilities, talents, and subclasses
    //Alternative talents would be pretty neat
    //Subclasses tho will not work properly, it requires a lot of work around the code ig

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

    private String password;
    public boolean downloaded;

    public boolean view2d = false;
    public boolean seeLevelOnDeath = true;
    public boolean notRevealSecrets = false;

    public Set<CustomTileLoader.SimpleCustomTile> customTiles;

    public List<CustomRecipe> recipes;
    public Set<Integer> blockedRecipes;

    public CustomDungeon(String name) {
        this.name = name;
        ratKingLevels = new HashSet<>();
        itemDistributions = new ArrayList<>(5);
        customTiles = new HashSet<>(5);
        recipes = new ArrayList<>(5);
        blockedRecipes = new HashSet<>(5);
        heroesEnabled = new boolean[HeroClass.values().length];
        heroSubClassesEnabled = new boolean[heroesEnabled.length * 2];
        Arrays.fill(heroesEnabled, true);
        Arrays.fill(heroSubClassesEnabled, true);
        startItems = HeroSettings.HeroStartItemsData.getDefault();
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

    public void setItemInToolbar(int slot, EditorItem item) {
        if (item == null) toolbarItems[slot] = null;
        else if (item instanceof TileItem) toolbarItems[slot] = ((TileItem) item).terrainType();
        else if (item instanceof BlobItem) toolbarItems[slot] = ((BlobItem) item).blob();
        else if (item instanceof CustomTileItem) {
            CustomTilemap cust = ((CustomTileItem) item).customTile();
            if (cust instanceof CustomTileLoader.UserCustomTile) toolbarItems[slot] = ((CustomTileLoader.UserCustomTile) cust).identifier;
            else toolbarItems[slot] = item.getObject().getClass();
        } else toolbarItems[slot] = item.getObject().getClass();
    }

    public void restoreToolbar() {
        int slotBefore = lastSelectedToolbarSlot;
        EditorItemBag.callStaticInitializers();
        for (int i = 0; i < toolbarItems.length; i++) {
            if (toolbarItems[i] != null) {
                if (toolbarItems[i] instanceof Integer || toolbarItems[i] instanceof String)
                    QuickSlotButton.set(i, EditorScene.getObjAsInBag(toolbarItems[i]));
                else if (toolbarItems[i] == EditorItem.REMOVER_ITEM.getClass())
                    QuickSlotButton.set(i, EditorItem.REMOVER_ITEM);
                else
                    QuickSlotButton.set(i, EditorScene.getObjAsInBagFromClass((Class<?>) toolbarItems[i]));
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
    private static final String PASSWORD = "password";
    private static final String DOWNLOADED = "downloaded";
    private static final String CUSTOM_TILES = "custom_tiles";
    private static final String RECIPES = "recipes";
    private static final String BLOCKED_RECIPES = "blocked_recipes";

    private static final String RUNE_LABELS = "rune_labels";
    private static final String RUNE_CLASSES = "rune_classes";
    private static final String COLOR_LABELS = "color_labels";
    private static final String COLOR_CLASSES = "color_classes";
    private static final String GEM_LABELS = "gem_labels";
    private static final String GEM_CLASSES = "gem_classes";
    private static final String TOOLBAR_ITEM = "toolbar_item_";
    private static final String TOOLBAR_ITEM_INT = "toolbar_item_int_";
    private static final String TOOLBAR_ITEM_STRING = "toolbar_item_string_";
    private static final String LAST_SELECTED_TOOLBAR_SLOT = "last_selected_toolbar_slot";
    private static final String HEROES_ENABLED = "heroes_enabled";
    private static final String HERO_SUBCLASSES_ENABLED = "hero_subclasses_enabled";
    private static final String EFFECT_DURATION = "effect_duration";
    private static final String START_ITEMS = "start_items";
    private static final String FORCE_CHALLENGES = "force_challenges";
    private static final String VIEW_2D = "view_2d";
    private static final String SEE_LEVEL_ON_DEATH = "see_level_on_death";
    private static final String NOT_REVEAL_SECRETS = "not_reveal_secrets";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        bundle.put(LAST_EDITED_FLOOR, lastEditedFloor);
        if (startFloor != null) bundle.put(START_FLOOR, startFloor);
        bundle.put(RAT_KING_LEVELS, ratKingLevels.toArray(EMPTY_STRING_ARRAY));
        bundle.put(ITEM_DISTRIBUTION, itemDistributions);
        bundle.put(REMOVE_NEXT_SCROLL, removeNextScroll);
//        bundle.put(PASSWORD, password);
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

        int[] intArray = new int[blockedRecipes.size()];
        int index = 0;
        for (int i : blockedRecipes) {
            intArray[index] = i;
            index++;
        }
        bundle.put(BLOCKED_RECIPES, intArray);

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

        bundle.put(LAST_SELECTED_TOOLBAR_SLOT, lastSelectedToolbarSlot);
        for (int j = 0; j < toolbarItems.length; j++) {
            if (toolbarItems[j] != null) {
                if (toolbarItems[j] instanceof Integer)
                    bundle.put(TOOLBAR_ITEM_INT + j, (int) toolbarItems[j]);
                else if (toolbarItems[j] instanceof String)
                    bundle.put(TOOLBAR_ITEM_STRING + j, (String) toolbarItems[j]);
                else bundle.put(TOOLBAR_ITEM + j, (Class<?>) toolbarItems[j]);
            }
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
        removeNextScroll = bundle.getBoolean(REMOVE_NEXT_SCROLL);
        effectDuration.load((EffectDuration) bundle.get(EFFECT_DURATION));
//        password = bundle.getString(PASSWORD);
//        if (password.isEmpty()) password = null;
        downloaded = bundle.getBoolean(DOWNLOADED);
        forceChallenges = bundle.getInt(FORCE_CHALLENGES);
        view2d = bundle.getBoolean(VIEW_2D);
        seeLevelOnDeath = !bundle.contains(SEE_LEVEL_ON_DEATH) || bundle.getBoolean(SEE_LEVEL_ON_DEATH);
        notRevealSecrets = bundle.getBoolean(NOT_REVEAL_SECRETS);
        if (bundle.contains(HEROES_ENABLED)) heroesEnabled = bundle.getBooleanArray(HEROES_ENABLED);
        else {
            heroesEnabled = new boolean[HeroClass.values().length];
            Arrays.fill(heroesEnabled, true);
        }
        if (bundle.contains(HERO_SUBCLASSES_ENABLED)) heroSubClassesEnabled = bundle.getBooleanArray(HERO_SUBCLASSES_ENABLED);
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
        blockedRecipes = new HashSet<>(5);
        int[] intArray = bundle.getIntArray(BLOCKED_RECIPES);
        if (intArray != null) {
            for (int i : intArray)
                blockedRecipes.add(i);
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
            if (bundle.contains(TOOLBAR_ITEM + i))
                toolbarItems[i] = bundle.getClass(TOOLBAR_ITEM + i);
            else if (bundle.contains(TOOLBAR_ITEM_INT + i))
                toolbarItems[i] = bundle.getInt(TOOLBAR_ITEM_INT + i);
            else if (bundle.contains(TOOLBAR_ITEM_STRING + i))
                toolbarItems[i] = bundle.getString(TOOLBAR_ITEM_STRING + i);
        }
    }

    public CustomDungeonSaves.Info createInfo() {
        return new CustomDungeonSaves.Info(getName(), 1, getNumFloors(), 0/*hashCode()*/, downloaded);
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

        removeFloor(levelScheme);
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
                Level openLevel = openS.loadLevel();
                if(openLevel == null)  SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
                else {
                    EditorScene.open((CustomLevel) openLevel);
                    EditorScene.show(new WndSwitchFloor());
                }
            }

        }

        //Remove transitions and keys
        for (LevelScheme ls : fs) {
            if (n.equals(ls.getChasm())) ls.setChasm(null, false);
            if (n.equals(ls.getPassage())) ls.setPassage(null);

            removeInvalidKeys(ls.itemsToSpawn, n);

            if (ls.getType() == CustomLevel.class) {
                boolean load = ls.getLevel() == null;
                Level level;
                if (load) level = ls.loadLevel();
                else level = ls.getLevel();
                if (level == null) continue;//skip if level couldn't be loaded
                Set<Integer> toRemoveTransitions = new HashSet<>(4);
                for (LevelTransition transition : level.transitions.values()) {
                    if (transition != null && Objects.equals(transition.destLevel, n)) {
                        toRemoveTransitions.add(transition.cell());
                        if (level == EditorScene.customLevel()) EditorScene.remove(transition);
                    }
                }
                for (Zone zone : level.zoneMap.values()) {
                    if (zone.zoneTransition != null) {
                        if (Objects.equals(zone.zoneTransition.destLevel, n)) {
                            zone.zoneTransition = null;
                        }
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
                    if (m instanceof Thief) {
                        if (isInvalidKey(((Thief) m).item, n)) {
                            ((Thief) m).item = null;
                            removedItems = true;
                        }
                        if (((Thief) m).item instanceof RandomItem<?>) {
                            if (((RandomItem<?>) ((Thief) m).item).removeInvalidKeys(n)) removedItems = true;
                        }
                    }
                    if (m.loot instanceof LootTableComp.CustomLootInfo) {
                        Set<LootTableComp.ItemWithCount> toRemove = new HashSet<>(4);
                        for (LootTableComp.ItemWithCount itemsWithCount : ((LootTableComp.CustomLootInfo) m.loot).lootList) {
                            if (removeInvalidKeys(itemsWithCount.items, n)) {
                                removedItems = true;
                                if (itemsWithCount.items.isEmpty()) toRemove.add(itemsWithCount);
                            }
                        }
                        ((LootTableComp.CustomLootInfo) m.loot).lootList.removeAll(toRemove);
                    }
                }

                //Remove invalid keys as sacrificial fire reward
                SacrificialFire sacrificialFire = (SacrificialFire) level.blobs.getOnly(SacrificialFire.class);
                if (sacrificialFire != null) {
                    if (sacrificialFire.removeInvalidKeys(n)) removedItems = true;
                }

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

        for (HeroSettings.HeroStartItemsData si : startItems) {
            removeInvalidKeys(si.items, n);
        }

        //Set level for keys in inv
        Items.updateKeys(n, EditorScene.customLevel() == null ? null : EditorScene.customLevel().name);

        EditorScene.updatePathfinder();

        CustomDungeonSaves.deleteLevelFile(n);
        CustomDungeonSaves.saveDungeon(this);
    }

    public static boolean removeInvalidKeys(List<Item> items, String invalidLevelName) {
        if (items == null) return false;
        boolean removedSth = false;
        for (Item i : new ArrayList<>(items)) {
            if (isInvalidKey(i, invalidLevelName)) {
                items.remove(i);
                removedSth = true;
            } else if (i instanceof RandomItem<?>) {
                return ((RandomItem<?>) i).removeInvalidKeys(invalidLevelName);
            }
        }
        return removedSth;
    }

    public static boolean isInvalidKey(Item item, String invalidLevelName) {
        if (item == null) return false;
        return item instanceof Key && ((Key) item).levelName.equals(invalidLevelName);
    }

    public void renameZone(Zone zone, String newName) {

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
                    else if (level == EditorScene.customLevel()) {
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
                else if (level == EditorScene.customLevel()) {
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

        if (zone == ZonePrompt.getSelectedZone()) ZonePrompt.setSelectedZone(ZonePrompt.getFirstZoneAvailable(EditorScene.customLevel()));
    }

    public static void deleteDungeon(String name) {
        if (Dungeon.customDungeon != null && Dungeon.customDungeon.name.equals(name))
            Dungeon.customDungeon = null;
        CustomDungeonSaves.deleteDungeonFile(name);
    }

    public static CustomDungeonSaves.Info copyDungeon(String oldName, String newName) {
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

        renameInvalidKeys(ls.itemsToSpawn, oldName, newName);

        try {
            if (levelScheme.getType() == CustomLevel.class) {
                Level level = levelScheme.loadLevel();
                level.name = newName;
                level.levelScheme = ls;

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

                //Change invalid keys in heaps
                for (Heap h : level.heaps.valueList()) {
                    renameInvalidKeys(h.items, oldName, newName);
                }
                //Change invalid keys in mob containers
                for (Mob m : level.mobs) {
                    if (m instanceof Mimic && ((Mimic) m).items != null) {
                        renameInvalidKeys(((Mimic) m).items, oldName, newName);
                    } else if (m instanceof Thief) {
                        if (isInvalidKey(((Thief) m).item, oldName)) ((Key) ((Thief) m).item).levelName = newName;
                        if (((Thief) m).item instanceof RandomItem<?>)
                            ((RandomItem<?>) ((Thief) m).item).renameInvalidKeys(oldName, newName);
                    }
                    if (m.loot instanceof LootTableComp.CustomLootInfo) {
                        for (LootTableComp.ItemWithCount itemsWithCount : ((LootTableComp.CustomLootInfo) m.loot).lootList) {
                            renameInvalidKeys(itemsWithCount.items, oldName, newName);
                        }
                    }
                }

                //Remove invalid keys as sacrificial fire reward
                SacrificialFire sacrificialFire = (SacrificialFire) level.blobs.getOnly(SacrificialFire.class);
                if (sacrificialFire != null) sacrificialFire.renameInvalidKeys(oldName, newName);

                CustomDungeonSaves.saveLevel(level);
            }
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

            if (ratKingLevels.contains(oldName)) {
                ratKingLevels.remove(oldName);
                ratKingLevels.add(newName);
            }

            if (oldName.equals(lastEditedFloor)) lastEditedFloor = newName;
            if (oldName.equals(startFloor)) startFloor = newName;

            //Change transitions and keys
            for (LevelScheme ls : floors.values()) {

                if (oldName.equals(ls.getChasm())) ls.setChasm(newName, false);
                if (oldName.equals(ls.getPassage())) ls.setPassage(newName);

                if (oldName.equals(ls.levelCreatedBefore)) ls.levelCreatedBefore = newName;
                if (oldName.equals(ls.levelCreatedAfter)) ls.levelCreatedAfter = newName;

                renameInvalidKeys(ls.itemsToSpawn, oldName, newName);

                if (ls.getType() == CustomLevel.class) {
                    boolean load = ls.getLevel() == null;
                    Level level;
                    if (load) level = ls.loadLevel();
                    else level = ls.getLevel();

                    boolean saveWithOgName = level == null;
                    if (saveWithOgName) {
                        level = CustomDungeonSaves.loadLevelWithOgName(ls.getName());
                        if (level == null) continue;//skip if level couldn't be loaded
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
                    for (Zone zone : level.zoneMap.values()) {
                        if (zone.zoneTransition != null) {
                            if (Objects.equals(zone.zoneTransition.destLevel, oldName)) {
                                zone.zoneTransition.destLevel = newName;
                                needsSave = true;
                            }
                            if (Objects.equals(zone.zoneTransition.departLevel, oldName)) {
                                zone.zoneTransition.departLevel = newName;
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
                        } else if (m instanceof Thief) {
                            if (isInvalidKey(((Thief) m).item, oldName)) {
                                ((Key) ((Thief) m).item).levelName = newName;
                                needsSave = true;
                            }
                            if (((Thief) m).item instanceof RandomItem<?>) {
                                if (((RandomItem<?>) ((Thief) m).item).renameInvalidKeys(oldName, newName)) needsSave = true;
                            }
                        }
                        if (m.loot instanceof LootTableComp.CustomLootInfo) {
                            for (LootTableComp.ItemWithCount itemsWithCount : ((LootTableComp.CustomLootInfo) m.loot).lootList) {
                                if (renameInvalidKeys(itemsWithCount.items, oldName, newName)) {
                                    needsSave = true;
                                }
                            }
                        }
                    }

                    //Remove invalid keys as sacrificial fire reward
                    SacrificialFire sacrificialFire = (SacrificialFire) level.blobs.getOnly(SacrificialFire.class);
                    if (sacrificialFire != null) {
                        if (sacrificialFire.renameInvalidKeys(oldName, newName)) needsSave = true;
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

            for (HeroSettings.HeroStartItemsData si : startItems) {
                renameInvalidKeys(si.items, oldName, newName);
            }

            //Set level for keys in inv
            Items.updateKeys(oldName, newName);

            levelScheme.name = newName;
            if (!savedItself && levelScheme.getType() == CustomLevel.class) {
                boolean unload = levelScheme.getLevel() != null;
                Level level;
                if (unload) level = levelScheme.loadLevel();
                else level = levelScheme.getLevel();
                if (level != null) {
                    level.name = newName;
                    CustomDungeonSaves.saveLevel(level);
                    if (unload) levelScheme.unloadLevel();
                }
            }

            if (EditorScene.customLevel() != null) {
                Undo.reset();//TODO maybe not best solution to reset all, but UndoParts all store the old lvl name
                for (LevelTransition t : EditorScene.customLevel().transitions.values())
                    EditorScene.updateTransitionIndicator(t);
            }
            floors.remove(oldName);
            floors.put(newName, levelScheme);

            EditorScene.updateDepthIcon();

            EditorScene.updatePathfinder();

            if (levelScheme.getType() == CustomLevel.class)
                CustomDungeonSaves.deleteLevelFile(oldName);

            CustomDungeonSaves.saveDungeon(this);

        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }

    }

    public static boolean renameInvalidKeys(List<Item> items, String invalidLevelName, String newName) {
        if (items == null) return false;
        boolean removedSth = false;
        for (Item i : new ArrayList<>(items)) {
            if (isInvalidKey(i, invalidLevelName)) {
                ((Key) i).levelName = newName;
                removedSth = true;
            }
            if (i instanceof RandomItem<?>) {
                ((RandomItem<?>) i).renameInvalidKeys(invalidLevelName, newName);
            }
        }
        return removedSth;
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