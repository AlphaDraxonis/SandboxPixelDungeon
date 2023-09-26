package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroClass;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.StyledItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.effects.BadgeBanner;
import com.alphadraxonis.sandboxpixeldungeon.items.EnergyCrystal;
import com.alphadraxonis.sandboxpixeldungeon.items.Gold;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.KindofMisc;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
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
                tabs = new TabControlButton[HeroClass.values().length + 1];
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
        int currentIndex = outsideSp == null ? 0 : outsideSp.currentIndex;
        heroTabs[currentIndex].visible = heroTabs[currentIndex].active = false;
        heroTabs[index].visible = heroTabs[index].active = true;

        if (title != null) {

            title.icon(createTabIcon(index));
            title.label(getTabName(index));

            GeneralTab.updateLayout();
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
        int index = outsideSp == null ? 0 : outsideSp.currentIndex;
        return title = new IconTitle(createTabIcon(index), getTabName(index));
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
        private final ItemContainerWithLabel<Item> startItems;
        private final ItemContainerWithLabel<Bag> startBags;
        private final ItemSelector startWeapon, startArmor, startRing, startArti, startMisc;
        private final StyledSpinner startGold, startEnergy;

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
            }

            HeroStartItemsData data = Dungeon.customDungeon.startItems[index];

            itemSelectorParent = new Component();

            startWeapon = new StyledItemSelector(Messages.get(HeroSettings.class, "weapon"), MeleeWeapon.class, data.weapon, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.weapon = (Weapon) selectedItem;
                }
            };
            startWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
            itemSelectorParent.add(startWeapon);
            startArmor = new StyledItemSelector(Messages.get(HeroSettings.class, "armor"), Armor.class, data.armor, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.armor = (Armor) selectedItem;
                }
            };
            startArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
            itemSelectorParent.add(startArmor);
            startRing = new StyledItemSelector(Messages.get(HeroSettings.class, "ring"), Ring.class, data.ring, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.ring = (Ring) selectedItem;
                }
            };
            startRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
            itemSelectorParent.add(startRing);
            startArti = new StyledItemSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, data.artifact, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.artifact = (Artifact) selectedItem;
                }
            };
            startArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
            itemSelectorParent.add(startArti);
            startMisc = new StyledItemSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, data.misc, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.misc = (KindofMisc) selectedItem;
                }
            };
            startMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            itemSelectorParent.add(startMisc);

            startGold = new StyledSpinner(new SpinnerIntegerModel(0, 10000, data.gold, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 150;
                }
            }, Messages.get(Gold.class, "name"), 10, Icons.GOLD.get());
            startGold.icon().scale = new PointF(0.5f, 0.5f);
            startGold.addChangeListener(() -> data.gold = (int) startGold.getValue());
            itemSelectorParent.add(startGold);
            startEnergy = new StyledSpinner(new SpinnerIntegerModel(0, 1000, data.energy, 1, false, null) {
                @Override
                public float getInputFieldWith(float height) {
                    return Spinner.FILL;
                }

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15;
                }
            }, Messages.get(EnergyCrystal.class, "name"), 10, Icons.ENERGY.get());
            startEnergy.icon().scale = new PointF(0.5f, 0.5f);
            startEnergy.addChangeListener(() -> data.energy = (int) startEnergy.getValue());
            itemSelectorParent.add(startEnergy);

            add(itemSelectorParent);

            startBags = new ItemContainerWithLabel<Bag>(data.bags, Messages.get(HeroSettings.class, "bags")) {
                @Override
                public boolean itemSelectable(Item item) {
                    return item instanceof Bag;
                }

                @Override
                protected void onSlotNumChange() {
                    if (startItems != null) {
                        GeneralTab.updateLayout();
                    }
//                    boolean wasVisible = addBtn.visible;
//                    addBtn.visible = addBtn.active = getNumSlots() < 4;
//                    if (addBtn.visible != wasVisible) {
//                        WndEditorSettings wnd = (WndEditorSettings) EditorUtilies.getParentWindow(this);
//                        if (wnd != null) wnd.getGeneralTab().layout();
//                    }
                }

                @Override
                protected void showSelectWindow() {
                    Set<Class<? extends Item>> exclude = new HashSet<>(5);
//                    for (Bag b : data.bags) exclude.add(b.getClass());
                    ItemSelector.showSelectWindow(startBags, ItemSelector.NullTypeSelector.NONE, Bag.class, exclude);
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
                        GeneralTab.updateLayout();
                    }
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
            itemSelectorParent.setPos(x, posY);
            EditorUtilies.layoutStyledCompsInRectangles(gap, width, itemSelectorParent,
                    new Component[]{startWeapon, startArmor, startRing, startArti, startMisc, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                            startGold, startEnergy});
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

    public static class HeroStartItemsData implements Bundlable {
        public Weapon weapon;
        public Armor armor;
        public Ring ring;
        public Artifact artifact;
        public KindofMisc misc;
        public List<Bag> bags = new ArrayList<>(2);
        public List<Item> items = new ArrayList<>(3);
        public int gold, energy;

        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        private static final String RING = "ring";
        private static final String ARTIFACT = "artifact";
        private static final String MISC = "misc";
        private static final String GOLD = "gold";
        private static final String ENERGY = "energy";
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
    }
}