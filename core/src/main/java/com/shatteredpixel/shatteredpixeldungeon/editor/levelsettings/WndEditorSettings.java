package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;


import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.BodyInfoArmor;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.WndInfoEq;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfDivination;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.Floor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDocument;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//From WndJournal
public class WndEditorSettings extends WndTabbed {

    public static final int WIDTH_P = 126;
    public static final int HEIGHT_P = 180;

    public static final int WIDTH_L = 200;
    public static final int HEIGHT_L = 130;

    private static final int ITEM_HEIGHT = 18;

    private final EnemyTab enemyTab;
    private final ItemTab itemTab;
    private final LoreTab loreTab;
    private final TabComp[] ownTabs;

    public static int last_index = 0;

    static boolean showsInfo = false;

    public WndEditorSettings() {

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
        int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;

        resize(width, height);

        ownTabs = new TabComp[]{
                enemyTab = new EnemyTab(),
                itemTab = new ItemTab(),
                loreTab = new LoreTab()};

        Tab[] tabs = new Tab[ownTabs.length];
        for (int i = 0; i < ownTabs.length; i++) {
            add(ownTabs[i]);
            ownTabs[i].setRect(0, 0, width, height);
            ownTabs[i].updateList();
            int index = i;
            tabs[i] = new IconTab(ownTabs[i].createIcon()) {
                protected void select(boolean value) {
                    super.select(value);
                    ownTabs[index].active = ownTabs[index].visible = value;
                    if (value) last_index = index;
                }
            };
            add(tabs[i]);
        }

        layoutTabs();
        select(last_index);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        for (TabComp tab : ownTabs) {
            tab.layout();
        }
    }


    public static abstract class TabComp extends Component {

        protected void updateList() {
        }

        @Override
        public void layout() {
            super.layout();
        }

        protected abstract Image createIcon();
    }

    public static class EnemyTab extends TabComp {

        private ScrollingListPane list;

        @Override
        protected void createChildren(Object... params) {
            list = new ScrollingListPane();
            add(list);
        }

        @Override
        public void layout() {
            super.layout();
            list.setRect(0, 0, width, height);
        }


        @Override
        protected void updateList() {

            list.clear();

            list.addTitle("Mob overview");

            List<Mob> mobsOnFloor = new ArrayList<>(EditorScene.floor().getStartMobs());

            Set<Mob> testMobs = new HashSet<>();
            testMobs.add(new Rat());
            testMobs.add(new Rat());
            testMobs.add(new Scorpio());
            testMobs.add(new YogDzewa());
            testMobs.add(new GnollTrickster());
            testMobs.add(new GnollTrickster());
            testMobs.add(new Spinner());

            for (Mob m : testMobs) {
                m.pos = Random.Int(EditorScene.floor().length());
//                Buff.affectAnyBuffAndSetDuration(m,Burning.class,10);
            }

            mobsOnFloor.addAll(testMobs);

            Collections.sort(mobsOnFloor, (m1, m2) -> m1.pos - m2.pos);

            for (Mob m : mobsOnFloor) {
                CharSprite sprite = m.sprite();
                sprite.jumpToFrame((int) (Math.random() * sprite.idle.frames.length));//Shouldn't all be synchrony

                ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(sprite,
                        EditorScene.formatTitle(m.name(), new Koord(m.pos))) {
                    @Override
                    public boolean onClick(float x, float y) {
                        if (inside(x, y) && !showsInfo) {
                            showsInfo = true;
                            EditorScene.show(new WndInfoMobInEditor(m));
                            return true;
                        }
                        return false;
                    }
                };
                list.addItem(item);
            }
            list.setRect(x, y, width, height);
        }

        @Override
        protected Image createIcon() {
            return new SkeletonSprite();
        }
    }

    public static class ItemTab extends TabComp {

        private RedButton[] itemButtons;
        private static final int NUM_BUTTONS = Items.numCategoriesForItemOverwiew();

        private static Items category = Items.getCategory(0);

        private ScrollingListPane list;

        @Override
        protected void createChildren(Object... params) {
            itemButtons = new RedButton[NUM_BUTTONS];
            for (int i = 0; i < NUM_BUTTONS; i++) {
                final int idx = i;
                itemButtons[i] = new RedButton("") {
                    @Override
                    protected void onClick() {
                        if (category.index() != idx) {
                            category = Items.getCategory(idx);
                            updateList();
                        }
                    }
                };
                itemButtons[i].icon(new ItemSprite(Items.getCategory(i).getItemSprite(), null));
                add(itemButtons[i]);
            }

            list = new ScrollingListPane();
            add(list);
        }

        @Override
        public void layout() {
            super.layout();

            if (PixelScene.landscape()) {
                float buttonWidth = width() / itemButtons.length;
                for (int i = 0; i < NUM_BUTTONS; i++) {
                    itemButtons[i].setRect(i * buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(itemButtons[i]);
                }
            } else {
                //for first row
                float buttonWidth = (float) (width() / Math.ceil(itemButtons.length / 2f));
                float y = 0;
                float x = 0;
                for (int i = 0; i < NUM_BUTTONS; i++) {
                    itemButtons[i].setRect(x, y, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(itemButtons[i]);
                    x += buttonWidth;
                    if (i == Math.ceil(itemButtons.length / 2f) - 1) {
                        y += ITEM_HEIGHT;
                        x = 0;
                        buttonWidth = (float) (width() / Math.ceil(itemButtons.length / 2f));
                    }
                }
            }
            updateList();
        }

        protected void updateList() {

            list.clear();

            List<Floor.ItemWithPos> itemsOnFloor = new ArrayList<>(EditorScene.floor().getStartItems());

            //Debug

            for (Items items : Items.values()) {
                if (items == Items.CATEGORY_OTHER) continue;
                for (Class<?> c : items.classes()) {
                    Item i = (Item) Reflection.newInstance(c);
                    itemsOnFloor.add(new Floor.ItemWithPos(i, Random.Int(EditorScene.floor().length())));
                }
            }
            for (Items items : Items.values()) {
                if (items == Items.CATEGORY_OTHER) continue;
                for (Class<?> c : items.classes()) {
                    Item i = (Item) Reflection.newInstance(c);
                    itemsOnFloor.add(new Floor.ItemWithPos(i, Random.Int(EditorScene.floor().length())));
                }
            }

            Weapon tst = new SpiritBow();
            tst.upgrade();
            tst.enchant(new Vampiric());
            itemsOnFloor.add(new Floor.ItemWithPos(tst, 7));
            itemsOnFloor.add(new Floor.ItemWithPos(new Sword(), 0));
            itemsOnFloor.add(new Floor.ItemWithPos(new ScrollOfDivination(), 8));
            itemsOnFloor.add(new Floor.ItemWithPos(new SpiritBow(), 54));
            itemsOnFloor.add(new Floor.ItemWithPos(new SpiritBow(), 15));

            //Debug end


            for (int i = 0; i < NUM_BUTTONS; i++) {
                if (i == category.index()) itemButtons[i].icon().color(TITLE_COLOR);
                else itemButtons[i].icon().resetColor();
            }

            list.scrollTo(0, 0);

            List<Floor.ItemWithPos> items = category.filterStackSortItems(itemsOnFloor);

            for (Floor.ItemWithPos item : items) {
                ScrollingListPane.ListItem listItem = new CatalogItem(item);
                list.addItem(listItem);
            }

            list.setRect(x, itemButtons[NUM_BUTTONS - 1].bottom() + 1, width,
                    height - itemButtons[NUM_BUTTONS - 1].bottom() - 1);
        }

        public static class CatalogItem extends ScrollingListPane.ListItem {

            private final Image itemIcon;
            private BitmapText lvlLabel;
            private final Floor.ItemWithPos item;
            private ColorBlock bg;

            public CatalogItem(Floor.ItemWithPos itemWithPos) {
                super(CustomDungeon.getDungeon().getItemImage(itemWithPos.item()), createTitle(itemWithPos));

                this.item = itemWithPos;
                itemIcon = IconTitleWithSubIcon.createSubIcon(itemWithPos.item());
                if (itemIcon != null) add(itemIcon);

                updateItem();
            }

            @Override
            protected void createChildren(Object... params) {
                bg = new ColorBlock(1, 1, -16777216);
                bg.color(0.5882f, 0.2117f, 0.2745f);//150 54 70 255
                add(bg);
                super.createChildren(params);

                lvlLabel = new BitmapText(PixelScene.pixelFont);
                add(lvlLabel);
            }

            @Override
            protected void layout() {
                super.layout();

                bg.size(width, height);
                bg.x = x;
                bg.y = y;

                if (itemIcon != null && icon != null) {
                    itemIcon.x = x + ICON_WIDTH - (ItemSpriteSheet.Icons.SIZE + itemIcon.width()) / 2f;
                    itemIcon.y = y + 0.5f + (ItemSpriteSheet.Icons.SIZE - itemIcon.height) / 2f;
                    PixelScene.align(itemIcon);
                }
                if (lvlLabel != null) {
                    lvlLabel.x = x + (ICON_WIDTH - lvlLabel.width());
                    lvlLabel.y = y + (height - lvlLabel.baseLine() - 1);
                    PixelScene.align(lvlLabel);
                }
            }

            public void updateItem() {
                Item i = item.item();
                bg.alpha(i.cursed ? 1 : 0);
                label.text(createTitle(item));

                if (icon != null) remove(icon);
                icon = CustomDungeon.getDungeon().getItemImage(i);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                int lvl = i.level();
                if (lvl != 0) {
                    lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, lvl));
                    lvlLabel.measure();
                    if ((i instanceof Weapon && ((Weapon) i).curseInfusionBonus)
                            || (i instanceof Armor && ((Armor) i).curseInfusionBonus)
                            || (i instanceof Wand && ((Wand) i).curseInfusionBonus)) {
                        lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
                    } else {
                        lvlLabel.hardlight(ItemSlot.UPGRADED);
                    }
                } else lvlLabel.text(null);
                layout();
            }

            @Override
            public boolean onClick(float x, float y) {
                if (inside(x, y)) {
                    if (!showInfoDialog(item.item(), this))
                        EditorScene.show(new WndTitledMessage(new Image(icon), IconTitleWithSubIcon.createSubIcon(item.item()), createTitle(item), item.item().info()));
                    return true;
                }
                return false;
            }

            private static boolean showInfoDialog(Item item, CatalogItem catalogItem) {
                if (item instanceof Armor) {
                    EditorScene.show(new WndInfoEq(item, catalogItem, () -> new BodyInfoArmor((Armor) item)));
                    return true;
                } else if (item instanceof MeleeWeapon || item instanceof SpiritBow || item instanceof Wand) {
                    EditorScene.show(new WndInfoEq(item, catalogItem, () -> new WndInfoEq.Body(item)));
                    return true;
                } else if (item instanceof Ring) {
                    EditorScene.show(new WndInfoEq(new Image(catalogItem.icon), IconTitleWithSubIcon.createSubIcon(item), Messages.titleCase(item.title()), catalogItem, () -> new WndInfoEq.Body(item)));
                    return true;
                } else
                    return false;

            }

        }

        public static String createTitle(Floor.ItemWithPos itemWP) {
            Item item = itemWP.item();
            if ((item.quantity() > 1 || !item.isUpgradable()) && !(item instanceof SpiritBow))
                return Messages.titleCase(item.title());
            return EditorScene.formatTitle(itemWP);
        }

        @Override
        protected Image createIcon() {
            return new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER);
        }
    }

    public static class LoreTab extends TabComp {

        private ScrollingListPane list;

        @Override
        protected void createChildren(Object... params) {
            list = new ScrollingListPane();
            add(list);
        }

        @Override
        public void layout() {
            super.layout();
            list.setRect(0, 0, width, height);
        }

        @Override
        protected void updateList() {
            list.addTitle(Messages.get(this, "title"));

            for (Document doc : Document.values()) {
                if (!doc.isLoreDoc()) continue;

                boolean found = doc.anyPagesFound();
                ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
                        doc.pageSprite(),
                        null,
                        found ? Messages.titleCase(doc.title()) : "???"
                ) {
                    @Override
                    public boolean onClick(float x, float y) {
                        if (inside(x, y) && found) {
                            ShatteredPixelDungeon.scene().addToFront(new WndDocument(doc));
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
                if (!found) {
                    item.hardlight(0x999999);
                    item.hardlightIcon(0x999999);
                }
                list.addItem(item);
            }

            list.setRect(x, y, width, height);
        }

        @Override
        protected Image createIcon() {
            return new ItemSprite(ItemSpriteSheet.GUIDE_PAGE, null);
        }
    }

}