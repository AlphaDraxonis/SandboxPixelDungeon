package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroClass;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
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
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.StyledButton;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeroSettings extends Component {

    private static int currentIndex = 0;

    private final Component outsideSp;
    private IconTitle title;

    private TabControlButton[] switchTabs;

    private final HeroTab[] heroTabs;


    public HeroSettings() {

        heroTabs = new HeroTab[HeroClass.values().length + 1];

        for (int i = 0; i < heroTabs.length; i++) {
            heroTabs[i] = new HeroTab(i);
            heroTabs[i].visible = heroTabs[i].active = false;
            add(heroTabs[i]);
        }

        outsideSp = new Component() {
            private Signal.Listener<KeyEvent> keyListener;
            private GameAction curAction;
            private float time;
            private static final float INTERVAL = 0.3f;// 3 cps
            private boolean isHolding;

            @Override
            protected void createChildren(Object... params) {
                switchTabs = new TabControlButton[HeroClass.values().length + 1];
                for (int j = 0; j < switchTabs.length; j++) {
                    switchTabs[j] = new TabControlButton(j);
                    switchTabs[j].icon(createTabIcon(j));
                    add(switchTabs[j]);

                    KeyEvent.addKeyListener(keyListener = keyEvent -> {
                        GameAction action = KeyBindings.getActionForKey(keyEvent);

                        if (keyEvent.pressed) {
                            curAction = action;
                            return processKey();
                        }
                        curAction = null;
                        time = 0;
                        isHolding = false;
                        return false;
                    });
                }

                selectTab(currentIndex);
            }

            @Override
            protected void layout() {
                float buttonWidth = width() / switchTabs.length;
                for (int i = 0; i < switchTabs.length; i++) {
                    switchTabs[i].setRect(x + i * buttonWidth, y, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(switchTabs[i]);
                }
                height = ITEM_HEIGHT;
            }

            private boolean processKey() {
                if (curAction == SPDAction.E) {
                    selectTab((currentIndex + 1) % switchTabs.length);
                    return true;
                }
                if (curAction == SPDAction.W) {
                    int index = currentIndex - 1;
                    if (index < 0) index = switchTabs.length - 1;
                    selectTab(index);
                    return true;
                }
                return false;
            }

            @Override
            public synchronized void update() {
                super.update();
                if (curAction != null) {
                    time += Game.elapsed;
                    if (!isHolding) {
                        if (time >= Button.longClick) {
                            isHolding = true;
                            time -= Button.longClick;
                            SandboxPixelDungeon.vibrate(50);
                        }
                    } else {
                        if (time >= INTERVAL) {
                            time -= INTERVAL;
                            processKey();
                        }
                    }
                }
            }

            @Override
            public synchronized void destroy() {
                super.destroy();
                KeyEvent.removeKeyListener(keyListener);
            }
        };
    }

    public void selectTab(int index) {
        heroTabs[currentIndex].visible = heroTabs[currentIndex].active = false;
        heroTabs[index].visible = heroTabs[index].active = true;
        currentIndex = index;

        for (int i = 0; i < switchTabs.length; i++) {
            switchTabs[i].setSelected(i == index);
        }

        if (title != null) {

            title.icon(createTabIcon(index));
            title.label(getTabName(index));

            ((WndEditorSettings) EditorUtilies.getParentWindow(this)).getGeneralTab().layout();
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
        return title = new IconTitle(createTabIcon(currentIndex), getTabName(currentIndex));
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
            }, Messages.get(HeroSettings.class, "gold"), 10, Icons.GOLD.get());
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
            }, Messages.get(HeroSettings.class, "energy"), 10, Icons.ENERGY.get());
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
                        ((WndEditorSettings) EditorUtilies.getParentWindow(this)).getGeneralTab().layout();
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
                        ((WndEditorSettings) EditorUtilies.getParentWindow(this)).getGeneralTab().layout();
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

    private class TabControlButton extends StyledButton {

        private static final float SELECTED_R = 2f, SELECTED_G = 2f, SELECTED_B = 2f;

        private boolean selected;

        private final int index;

        public TabControlButton(int index) {
            super(Chrome.Type.GREY_BUTTON_TR, "");
            this.index = index;

            bg.remove();
            bg.destroy();
            bg = new NinePatch(Assets.Interfaces.CHROME, 20, 9, 9, 9, 4) {//Chrome.Type.GREY_BUTTON_TR

                @Override
                public void resetColor() {
                    super.resetColor();
                    if (selected) hardlight(SELECTED_R, SELECTED_G, SELECTED_B);
                }

                @Override
                public void brightness(float value) {
                    rm += value - 1f;
                    gm += value - 1f;
                    bm += value - 1f;
                }
            };
            addToBack(bg);
        }

        @Override
        protected void onClick() {
            selectTab(index);
        }

        @Override
        protected String hoverText() {
            return getTabName(index);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            bg.resetColor();
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