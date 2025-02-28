package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.PropertyListContainer;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.MobSprites;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.OutsideSpSwitchTabs;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.AbstractWndChooseSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NotAllowedInLua
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
            
            {
                tabs = new TabControlButton[heroTabs.length];
                for (int j = 0; j < tabs.length; j++) {
                    tabs[j] = new OutsideSpSwitchTabs.TabControlButton(j);
                    tabs[j].icon(createTabIcon(j));
                    add(tabs[j]);
                }
                
                super.createChildren();
                
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
        return new Image(HeroClass.getFromIndex(index-1).spritesheet(), 0, 90, 12, 15);
    }

    public static String getTabName(int index) {
        if (index > 0) return HeroClass.values()[index - 1].title();
        return Messages.get(HeroSettings.class, "general");
    }

    private static class HeroTab extends Component {

        private StyledButton heroEnabled;
        private StyledButton subClassesEnabled;
        private final ItemContainerWithLabel<Item> startItems;
        private final ItemSelector startWeapon, startArmor, startRing, startArti, startMisc;
        private final ItemSelector sprite;
        private final StyledSpinner plusLvl, plusStr;
        private final PropertyListContainer properties;

        public HeroTab(int index) {

            if (index >= 1) {
                heroEnabled = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "") {
                    {
                        checked(Dungeon.customDungeon.heroesEnabled[index - 1]);
                    }
                    
                    @Override
                    protected void onClick() {
                        checked( !Dungeon.customDungeon.heroesEnabled[index - 1] );
                    }
                    
                    public void checked(boolean value) {
                        Dungeon.customDungeon.heroesEnabled[index - 1] = value;
                        if (value) {
                            text( Messages.get(HeroSettings.class, "unlocked") );
                            icon( Icons.UNLOCKED.get() );
                        } else {
                            text( Messages.get(HeroSettings.class, "locked") );
                            icon( Icons.LOCKED.get() );
                        }
                    }
                    
                    @Override
                    protected void layout() {
                        height = Math.max(height, getMinimumHeight(width));
                        super.layout();
                    }
                    
                    @Override
                    public float getMinimumHeight(float width) {
                        return Math.max(super.getMinimumHeight(width), WndMenuEditor.BTN_HEIGHT);
                    }
                };
                add(heroEnabled);

                final HeroClass heroClass = HeroClass.getFromIndex(index - 1);
                subClassesEnabled = new StyledButton(Chrome.Type.GREY_BUTTON_TR, createSubclassBtnLabel(heroClass)) {
                    {
                        multiline = true;
                    }
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
                    
                    @Override
                    protected void layout() {
                        height = Math.max(height, getMinimumHeight(width));
                        super.layout();
                    }
                    
                    @Override
                    public float getMinimumHeight(float width) {
                        return Math.max(super.getMinimumHeight(width), WndMenuEditor.BTN_HEIGHT);
                    }
                };
                subClassesEnabled.icon(new ItemSprite(ItemSpriteSheet.MASK));
                add(subClassesEnabled);
            }

            HeroStartItemsData data = Dungeon.customDungeon.startItems[index];

            startWeapon = new StyledItemSelector(Messages.get(HeroSettings.class, "weapon"), MeleeWeapon.class, data.weapon, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.weapon = (Weapon) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startWeapon.setShowWhenNull(ItemSpriteSheet.WEAPON_HOLDER);
            add(startWeapon);
            
            startArmor = new StyledItemSelector(Messages.get(HeroSettings.class, "armor"), Armor.class, data.armor, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.armor = (Armor) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startArmor.setShowWhenNull(ItemSpriteSheet.ARMOR_HOLDER);
            add(startArmor);
            
            startRing = new StyledItemSelector(Messages.get(HeroSettings.class, "ring"), Ring.class, data.ring, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.ring = (Ring) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startRing.setShowWhenNull(ItemSpriteSheet.RING_HOLDER);
            add(startRing);
            
            startArti = new StyledItemSelector(Messages.get(HeroSettings.class, "artifact"), Artifact.class, data.artifact, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.artifact = (Artifact) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startArti.setShowWhenNull(ItemSpriteSheet.ARTIFACT_HOLDER);
            add(startArti);
            
            startMisc = new StyledItemSelector(Messages.get(HeroSettings.class, "misc"), KindofMisc.class, data.misc, ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    data.misc = (KindofMisc) selectedItem;
                    if (selectedItem != null && selectedItem.reservedQuickslot == 0) selectedItem.reservedQuickslot = -1;
                }
            };
            startMisc.setShowWhenNull(ItemSpriteSheet.SOMETHING);
            add(startMisc);

            final MobSpriteItem curentSprite = data.spriteClass == null ? null : new MobSpriteItem(data.spriteClass);
            sprite = new StyledItemSelector(Messages.get(HeroSettings.class, "sprite"),
                    MobSpriteItem.class, curentSprite == null ? EditorItem.NULL_ITEM : curentSprite, ItemSelector.NullTypeSelector.NOTHING) {
                MobSpriteItem currentSprite = curentSprite;
                {
                    selector.preferredBag = MobSprites.bag().getClass();
                    setShowWhenNull(511);
                    setSelectedItem(currentSprite);
                }
                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);

                    if (selectedItem == currentSprite) return;

                    if (selectedItem instanceof MobSpriteItem) {
                        currentSprite = (MobSpriteItem) selectedItem;
                        data.spriteClass = currentSprite.getObject();
                    } else {
                        currentSprite = null;
                        data.spriteClass = null;
                    }

                }
            };
            add(sprite);


            plusLvl = new StyledSpinner(new SpinnerIntegerModel(1, 30, 1 + data.plusLvl) {
                {
                    setAbsoluteMinimum(1);
                }
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.titleCase(Messages.get(HeroSettings.class, "lvl")), 10, EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.POTION_EXP));
            plusLvl.addChangeListener(() -> data.plusLvl = (int) plusLvl.getValue() - 1);
            add(plusLvl);

            plusStr = new StyledSpinner(new SpinnerIntegerModel(0, 50, Hero.STARTING_STR + data.plusStr) {
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, Messages.titleCase(Messages.get(WndGameInProgress.class, "str")), 10, EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.POTION_STRENGTH));
            plusStr.addChangeListener(() -> data.plusStr = (int) plusStr.getValue() - Hero.STARTING_STR);
            add(plusStr);

            startItems = new ItemContainerWithLabel<Item>(data.items, Messages.get(HeroSettings.class, "items")) {

                @Override
                protected void onSlotNumChange() {
                    if (startItems != null) {
                        DungeonTab.updateLayout();
                    }
                }

                @Override
                protected void doAddItem(Item item) {
                    if (item.stackable) {
                        for (Item i : itemList) {
                            if (item.isSimilar( i )) {
                                i.merge( item );
                                return;
                            }
                        }
                    }
                    super.doAddItem(item);
                }

                @Override
                protected void addItemToUI(Item item, boolean last) {
                    super.addItemToUI(item, last);
                    if (item.reservedQuickslot == 0) item.reservedQuickslot = -1;
                }
            };
            add(startItems);

            properties = new PropertyListContainer(data.properties, null) {
                @Override
                protected void onSlotNumChange() {
                    super.onSlotNumChange();
                    if (properties != null) {
                        DungeonTab.updateLayout();
                    }
                }

                @Override
                protected Set<Char.Property> getPropertiesToIgnore() {
                    Set<Char.Property> result = super.getPropertiesToIgnore();
                    result.add(Char.Property.PERMEABLE);
                    return result;
                }
            };
            add(properties);
        }

        @Override
        protected void layout() {

            height = 0;

            height = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width, 2, this, heroEnabled, subClassesEnabled);
            height += WndTitledMessage.GAP;

            height = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width, this,
                    startWeapon, startArmor, startRing, startArti, startMisc, sprite, EditorUtilities.PARAGRAPH_INDICATOR_INSTANCE,
                            plusLvl, plusStr);
            height += WndTitledMessage.GAP * 1.5f;

            height = EditorUtilities.layoutCompsLinear(WndTitledMessage.GAP, this, startItems, properties);
        }
    }

    private static String createSubclassBtnLabel(HeroClass heroClass) {
        int activeSubCls = 0;
        for (HeroSubClass cls : heroClass.subClasses())
            if (Dungeon.customDungeon.heroSubClassesEnabled[cls.getIndex()]) activeSubCls++;
        return Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses"))
                + " (" + activeSubCls + "/" + heroClass.subClasses().length + ")";
    }

    public static class HeroStartItemsData extends GameObject implements Copyable<HeroStartItemsData> {
        public Weapon weapon;
        public Armor armor;
        public Ring ring;
        public Artifact artifact;
        public KindofMisc misc;
        public List<Item> items = new ArrayList<>(4);
        public int plusLvl;//default is 0
        public int plusStr;//default is 0

        public Set<Char.Property> properties = new HashSet<>();

        public Class<? extends CharSprite> spriteClass;

        private boolean needToAddDefaultConfiguration = false;

        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        private static final String RING = "ring";
        private static final String ARTIFACT = "artifact";
        private static final String MISC = "misc";
        private static final String LVL = "lvl";
        private static final String STR = "str";
        private static final String ITEMS = "items";
        private static final String PROPERTIES = "properties";
        private static final String SPRITE_CLASS = "sprite_class";

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(WEAPON, weapon);
            bundle.put(ARMOR, armor);
            bundle.put(RING, ring);
            bundle.put(ARTIFACT, artifact);
            bundle.put(MISC, misc);
            bundle.put(LVL, plusLvl);
            bundle.put(STR, plusStr);
            bundle.put(SPRITE_CLASS, spriteClass);

            int[] enumOrdinals = new int[properties.size()];
            int index = 0;
            for (Char.Property p : properties) {
                enumOrdinals[index] = p.ordinal();
                index++;
            }
            bundle.put(PROPERTIES, enumOrdinals);

            bundle.put(ITEMS, items);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            weapon = (Weapon) bundle.get(WEAPON);
            armor = (Armor) bundle.get(ARMOR);
            ring = (Ring) bundle.get(RING);
            artifact = (Artifact) bundle.get(ARTIFACT);
            misc = (KindofMisc) bundle.get(MISC);

            needToAddDefaultConfiguration = !bundle.contains(LVL);
            plusLvl = bundle.getInt(LVL);
            plusStr = bundle.getInt(STR);

            if (bundle.contains("bags")) {
                for (Bundlable b : bundle.getCollection("bags")) {
                    items.add((Item) b);
                }
            }
            if (bundle.contains("gold")) {
                int quant = bundle.getInt("gold");
                if (quant > 0) items.add(new Gold(quant));
            }
            if (bundle.contains("energy")) {
                int quant = bundle.getInt("energy");
                if (quant > 0) items.add(new EnergyCrystal(quant));
            }
            for (Bundlable b : bundle.getCollection(ITEMS)) {
                items.add((Item) b);
            }

            int[] enumOrdinals = bundle.getIntArray(PROPERTIES);
            if (enumOrdinals != null) {
                properties.clear();
                Char.Property[] props = Char.Property.values();
                for (int i = 0; i < enumOrdinals.length; i++) {
                    properties.add(props[enumOrdinals[i]]);
                }
            }

            spriteClass = bundle.getClass(SPRITE_CLASS);
        }

        @Override
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
                case 6:
                    HeroClass.initCleric(this);
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

        @Override
        public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
            return super.doOnAllGameObjects(whatToDo)
                    || doOnSingleObject(weapon  , whatToDo, newValue -> weapon   = newValue)
                    || doOnSingleObject(armor   , whatToDo, newValue -> armor    = newValue)
                    || doOnSingleObject(ring    , whatToDo, newValue -> ring     = newValue)
                    || doOnSingleObject(artifact, whatToDo, newValue -> artifact = newValue)
                    || doOnSingleObject(misc    , whatToDo, newValue -> misc     = newValue)
                    || doOnAllGameObjectsList(items, whatToDo);
        }

        @Override
        public ModifyResult initRandoms() {
            GameObject.doOnSingleObject(weapon  , GameObject::initRandoms, newValue -> weapon   = newValue);
            GameObject.doOnSingleObject(armor   , GameObject::initRandoms, newValue -> armor    = newValue);
            GameObject.doOnSingleObject(ring    , GameObject::initRandoms, newValue -> ring     = newValue);
            GameObject.doOnSingleObject(artifact, GameObject::initRandoms, newValue -> artifact = newValue);
            GameObject.doOnSingleObject(misc    , GameObject::initRandoms, newValue -> misc     = newValue);
            if (weapon != null) weapon.setCursedKnown(true);
            if (armor != null) armor.setCursedKnown(true);
            if (ring != null) ring.setCursedKnown(true);
            if (artifact != null) artifact.setCursedKnown(true);
            if (misc != null) misc.setCursedKnown(true);
            GameObject.doOnAllGameObjectsList(items, GameObject::initRandoms);

            return ModifyResult.noChange();
        }

    }
}