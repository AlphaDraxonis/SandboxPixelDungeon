package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.OutsideSpSwitchTabs;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.AbstractWndChooseSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeroSettings extends Component {

    private final OutsideSpSwitchTabs outsideSp;
    private IconTitle title;

    private final HeroTab[] heroTabs;


    public HeroSettings() {

        heroTabs = new HeroTab[HeroClass.values().length + 1];

        for (int i = 0; i < heroTabs.length; i++) {
            heroTabs[i] = new HeroTab(i);
            heroTabs[i].visible = heroTabs[i].active = false;
            add(heroTabs[i]);
        }

        outsideSp = new OutsideSpSwitchTabs() {

            @Override
            protected void createChildren(Object... params) {
                tabs = new TabControlButton[heroTabs.length];
                for (int j = 0; j < tabs.length; j++) {
                    tabs[j] = new OutsideSpSwitchTabs.TabControlButton(j);
                    tabs[j].icon(createTabIcon(j));
                    add(tabs[j]);
                }

                super.createChildren(params);

                select(currentIndex);
            }

            @Override
            public void select(int index) {
                HeroSettings.this.selectTab(index);
                super.select(index);
            }

            @Override
            public String getTabName(int index) {
                return HeroSettings.getTabName(index);
            }
        };
    }

    public void selectTab(int index) {
        int currentIndex = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
        heroTabs[currentIndex].visible = heroTabs[currentIndex].active = false;
        heroTabs[index].visible = heroTabs[index].active = true;

        if (title != null) {

            title.icon(createTabIcon(index));
            title.label(Messages.titleCase(getTabName(index)));

            DungeonTab.updateLayout();
        }
    }

    @Override
    protected void layout() {
        for (HeroTab tab : heroTabs) {
            if (tab.visible) {
                tab.setRect(x, y, width, -1);
                height = tab.height();
                break;
            }
        }
    }

    public Component createTitle() {
        int index = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
        return title = new IconTitle(createTabIcon(index), Messages.titleCase(getTabName(index)));
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    public static Image createTabIcon(int index) {
        if (index == 0) return Icons.ANY_HERO.get();
        return BadgeBanner.image(index - 1);
    }

    public static String getTabName(int index) {
        if (index > 0) return HeroClass.values()[index - 1].title();
        return Messages.get(HeroSettings.class, "general");
    }

    private static class HeroTab extends Component {

        private CheckBox heroEnabled;
        private RedButton subClassesEnabled;
        private final ItemContainerWithLabel<Item> startItems;
        private final ItemContainerWithLabel<Bag> startBags;
        private final ItemSelector startWeapon, startArmor, startRing, startArti, startMisc;
        private final StyledSpinner startGold, startEnergy, plusLvl, plusStr;

        private final Component itemSelectorParent;

        public HeroTab(int index) {

            if (index >= 1) {
                heroEnabled = new CheckBox(Messages.get(HeroSettings.class, "unlocked")) {
                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        Dungeon.customDungeon.heroesEnabled[index - 1] = value;
                    }
                };
                heroEnabled.checked(Dungeon.customDungeon.heroesEnabled[index - 1]);
                add(heroEnabled);

                final HeroClass heroClass = HeroClass.getFromIndex(index - 1);
                subClassesEnabled = new RedButton(createSubclassBtnLabel(heroClass)) {
                    @Override
                    protected void onClick() {
                        RenderedTextBlock titlebar = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses")),9);
                        Window w = new AbstractWndChooseSubclass(titlebar, null, null, heroClass, null) {
                            @Override
                            protected StyledButton createHeroSubClassButton(TengusMask tome, HeroSubClass subCls) {
                                final int idx = subCls.getIndex();
                                CheckBox clsBtn = new CheckBox(subCls.title()){
                                    @Override
                                    public void checked(boolean value) {
                                        Dungeon.customDungeon.heroSubClassesEnabled[idx] = value;
                                        if (value != checked()) subClassesEnabled.text(createSubclassBtnLabel(heroClass));
                                        super.checked(value);
                                    }
                                };
                                clsBtn.checked(Dungeon.customDungeon.heroSubClassesEnabled[idx]);
                                return clsBtn;
                            }
                        };
                        EditorScene.show(w);
                    }
                };
                subClassesEnabled.leftJustify = true;
                add(subClassesEnabled);
            }

            HeroStartItemsData data = Dungeon.customDungeon.startItems[index];

            itemSelectorParent = new Component();

            startWeapon = new StyledItemSelector(Messages.get(HeroSettings.class, "weapon"), MeleeWeapon.class, data.weapon, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.weapon = (Weapon) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
            itemSelectorParent.add(startWeapon);
            startArmor = new StyledItemSelector(Messages.get(HeroSettings.class, "armor"), Armor.class, data.armor, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.armor = (Armor) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
            itemSelectorParent.add(startArmor);
            startRing = new StyledItemSelector(Messages.get(HeroSettings.class, "ring"), Ring.class, data.ring, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.ring = (Ring) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
            itemSelectorParent.add(startRing);
            startArti = new StyledItemSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, data.artifact, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.artifact = (Artifact) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
            itemSelectorParent.add(startArti);
            startMisc = new StyledItemSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, data.misc, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.misc = (KindofMisc) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            itemSelectorParent.add(startMisc);

            startGold = new StyledSpinner(new SpinnerIntegerModel(0, 10000, data.gold, 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 150;
                }
            }, Messages.titleCase(Messages.get(Gold.class, "name")), 10, Icons.GOLD.get());
            startGold.icon().scale = new PointF(0.5f, 0.5f);
            startGold.addChangeListener(() -> data.gold = (int) startGold.getValue());
            itemSelectorParent.add(startGold);
            startEnergy = new StyledSpinner(new SpinnerIntegerModel(0, 1000, data.energy, 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return super.getClicksPerSecondWhileHolding() / 10;
                }
            }, Messages.titleCase(Messages.get(EnergyCrystal.class, "name")), 10, Icons.ENERGY.get());
            startEnergy.icon().scale = new PointF(0.5f, 0.5f);
            startEnergy.addChangeListener(() -> data.energy = (int) startEnergy.getValue());
            itemSelectorParent.add(startEnergy);

            plusLvl = new StyledSpinner(new SpinnerIntegerModel(1, 30, 1 + data.plusLvl, 1, false, null) {
                {
                    setAbsoluteMinimum(1);
                }
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.titleCase(Messages.get(HeroSettings.class, "lvl")), 10, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_EXP));
            plusLvl.addChangeListener(() -> data.plusLvl = (int) plusLvl.getValue() - 1);
            itemSelectorParent.add(plusLvl);

            plusStr = new StyledSpinner(new SpinnerIntegerModel(0, 50, Hero.STARTING_STR + data.plusStr, 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.titleCase(Messages.get(WndGameInProgress.class, "str")), 10, EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_STRENGTH));
            plusStr.addChangeListener(() -> data.plusStr = (int) plusStr.getValue() - Hero.STARTING_STR);
            itemSelectorParent.add(plusStr);

            add(itemSelectorParent);

            startBags = new ItemContainerWithLabel<Bag>(data.bags, Messages.get(HeroSettings.class, "bags")) {
                @Override
                public boolean itemSelectable(Item item) {
                    Item i = item instanceof ItemItem ? ((ItemItem) item).item() : item;
                    return i instanceof Bag;
                }

                @Override
                protected void onSlotNumChange() {
                    if (startItems != null) {
                        DungeonTab.updateLayout();
                    }
                }

                @Override
                protected void showSelectWindow() {
                    Set<Class<?>> exclude = new HashSet<>(5);
//                    for (Bag b : data.bags) exclude.add(b.getClass());
                    ItemSelector.showSelectWindow(startBags, ItemSelector.NullTypeSelector.DISABLED, Bag.class, Items.bag, exclude);
                }

                @Override
                protected void addItemToUI(Item item, boolean last) {
                    super.addItemToUI(item, last);
                    if (item != null && item.reservedQuickslot == 0) item.reservedQuickslot = -1;
                }
            };
            add(startBags);
            startItems = new ItemContainerWithLabel<Item>(data.items, Messages.get(HeroSettings.class, "items")) {

                @Override
                public boolean itemSelectable(Item item) {
                    return !(item instanceof Gold || item instanceof EnergyCrystal || item instanceof Bag);
                }

                @Override
                protected void onSlotNumChange() {
                    if (startItems != null) {
                        DungeonTab.updateLayout();
                    }
                }

                @Override
                protected void addItemToUI(Item item, boolean last) {
                    super.addItemToUI(item, last);
                    if (item != null && item.reservedQuickslot == 0) item.reservedQuickslot = -1;
                }
            };
            add(startItems);
            if (startItems.getStartColumnPos() > startBags.getStartColumnPos())
                startBags.setStartColumnPos(startItems.getStartColumnPos());
            else startItems.setStartColumnPos(startBags.getStartColumnPos());
        }

        @Override
        protected void layout() {

            int gap = 2;

            float posY = y;

            if (heroEnabled != null) {
                heroEnabled.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
                PixelScene.align(heroEnabled);
                posY = heroEnabled.bottom() + gap;
            }
            if (subClassesEnabled != null) {
                subClassesEnabled.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
                PixelScene.align(subClassesEnabled);
                posY = subClassesEnabled.bottom() + gap;
            }
            itemSelectorParent.setSize(width, -1);
            itemSelectorParent.setRect(x, posY, width,  EditorUtilies.layoutStyledCompsInRectangles(gap, width, itemSelectorParent,
                    new Component[]{startWeapon, startArmor, startRing, startArti, startMisc, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                            startGold, startEnergy, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                            plusLvl, plusStr}));
            PixelScene.align(itemSelectorParent);
            posY = itemSelectorParent.bottom() + gap;
            startBags.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(startBags);
            posY = startBags.bottom() + gap;
            startItems.setRect(x, posY, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(startItems);
            posY = startItems.bottom() + gap;

            height = (posY - y - gap);
        }
    }

    private static String createSubclassBtnLabel(HeroClass heroClass) {
        int activeSubCls = 0;
        for (HeroSubClass cls : heroClass.subClasses())
            if (Dungeon.customDungeon.heroSubClassesEnabled[cls.getIndex()]) activeSubCls++;
        return Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses"))
                + " (" + activeSubCls + "/" + heroClass.subClasses().length + ")";
    }

    public static class HeroStartItemsData implements Bundlable {
        public Weapon weapon;
        public Armor armor;
        public Ring ring;
        public Artifact artifact;
        public KindofMisc misc;
        public List<Bag> bags = new ArrayList<>(2);
        public List<Item> items = new ArrayList<>(3);
        public int gold, energy;
        public int plusLvl;//default is 0
        public int plusStr;//default is 0

        private boolean needToAddDefaultConfiguration = false;

        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        private static final String RING = "ring";
        private static final String ARTIFACT = "artifact";
        private static final String MISC = "misc";
        private static final String GOLD = "gold";
        private static final String ENERGY = "energy";
        private static final String LVL = "lvl";
        private static final String STR = "str";
        private static final String BAGS = "bags";
        private static final String ITEMS = "items";

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(WEAPON, weapon);
            bundle.put(ARMOR, armor);
            bundle.put(RING, ring);
            bundle.put(ARTIFACT, artifact);
            bundle.put(MISC, misc);
            bundle.put(GOLD, gold);
            bundle.put(ENERGY, energy);
            bundle.put(ENERGY, energy);
            bundle.put(LVL, plusLvl);
            bundle.put(STR, plusStr);

            bundle.put(BAGS, bags);
            bundle.put(ITEMS, items);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            weapon = (Weapon) bundle.get(WEAPON);
            armor = (Armor) bundle.get(ARMOR);
            ring = (Ring) bundle.get(RING);
            artifact = (Artifact) bundle.get(ARTIFACT);
            misc = (KindofMisc) bundle.get(MISC);
            gold = bundle.getInt(GOLD);
            energy = bundle.getInt(ENERGY);

            needToAddDefaultConfiguration = !bundle.contains(LVL);
            plusLvl = bundle.getInt(LVL);
            plusStr = bundle.getInt(STR);

            for (Bundlable b : bundle.getCollection(BAGS)) {
                bags.add((Bag) b);
            }
            for (Bundlable b : bundle.getCollection(ITEMS)) {
                items.add((Item) b);
            }
        }

        public HeroStartItemsData getCopy() {
            Bundle bundle = new Bundle();
            bundle.put("DATA", this);
            return (HeroStartItemsData) bundle.get("DATA");
        }

        public void maybeInitDefault(int heroIndex) {
            if (!needToAddDefaultConfiguration) return;
            switch (heroIndex) {
                case 0:
                    HeroClass.initGeneral(this);
                    break;
                case 1:
                    HeroClass.initWarrior(this);
                    break;
                case 2:
                    HeroClass.initMage(this);
                    break;
                case 3:
                    HeroClass.initRouge(this);
                    break;
                case 4:
                    HeroClass.initHuntress(this);
                    break;
                case 5:
                    HeroClass.initDuelist(this);
                    break;
            }
        }

        public static HeroStartItemsData[] getDefault(){
            HeroStartItemsData[] startItems = new HeroStartItemsData[HeroClass.values().length + 1];
            for (int i = 0; i < startItems.length; i++) {
                startItems[i] = new HeroSettings.HeroStartItemsData();
                startItems[i].needToAddDefaultConfiguration = true;
                startItems[i].maybeInitDefault(i);
            }
            return startItems;
        }

        public void initRandoms() {
            weapon = RandomItem.initRandomStatsForItemSubclasses(weapon);
            armor = RandomItem.initRandomStatsForItemSubclasses(armor);
            ring = RandomItem.initRandomStatsForItemSubclasses(ring);
            artifact = RandomItem.initRandomStatsForItemSubclasses(artifact);
            misc = RandomItem.initRandomStatsForItemSubclasses(misc);
            if (weapon != null) weapon.setCursedKnown(true);
            if (armor != null) armor.setCursedKnown(true);
            if (ring != null) ring.setCursedKnown(true);
            if (artifact != null) artifact.setCursedKnown(true);
            if (misc != null) misc.setCursedKnown(true);
            RandomItem.replaceRandomItemsInList(items);
            RandomItem.replaceRandomItemsInList(bags);
        }

    }
}