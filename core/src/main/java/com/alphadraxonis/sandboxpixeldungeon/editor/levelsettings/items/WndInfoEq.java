package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.items;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;

public class WndInfoEq extends WndTitledMessage {

    private final ItemTab.CatalogItem catalogItem;

    public WndInfoEq(Item item, ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
        this(new IconTitle(item), catalogItem, bodyFactory);
    }

    public WndInfoEq(Image icon, Image subIcon, String title, ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
        this(new IconTitleWithSubIcon(icon, subIcon, title), catalogItem, bodyFactory);
    }

    public WndInfoEq(IconTitle title, ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
        super(title, bodyFactory);
        this.catalogItem = catalogItem;
        ((WndInfoEq.Body) body()).wnd = this;
    }

    public static class Body extends WndTitledMessage.Body {

        private WndInfoEq wnd;
        private final RenderedTextBlock text;
        protected final CurseButton curseBtn;
        protected final LevelSpinner levelSpinner;
        protected final AugumentationSpinner augumentationSpinner;
        protected final RedButton enchantBtn;
        protected final Item item;

        public Body(Item item) {
            super();
            this.item = item;
            text = PixelScene.renderTextBlock(createText(), 6);

            if (!(item instanceof MissileWeapon)) {
                curseBtn = new CurseButton(item) {
                    @Override
                    protected void onChange() {
                        updateItem();
                    }
                };
                add(curseBtn);
            } else curseBtn = null;

            if (item.isUpgradable()) {
                levelSpinner = new LevelSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateItem();
                    }
                };
                add(levelSpinner);
            } else levelSpinner = null;

            if (item instanceof Weapon || item instanceof Armor) {//Missiles support enchantments too
                enchantBtn = new RedButton(Messages.get(WndInfoEq.class,"enchant")) {
                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndChooseEnchant(item) {
                            @Override
                            protected void finish() {
                                super.finish();
                                updateItem();
                            }
                        });
                    }
                };
                add(enchantBtn);
            } else enchantBtn = null;

            if (ScrollOfEnchantment.enchantable(item)) {
                augumentationSpinner = new AugumentationSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateItem();
                    }
                };
                add(augumentationSpinner);
            } else augumentationSpinner = null;

            add(text);
        }

        @Override
        public void setMaxWith(int width) {
            text.maxWidth(width);
        }

        @Override
        protected void layout() {
            float posY = y;

            text.setRect(x, posY, text.width(), text.height());
            posY = text.bottom() + 2 * GAP;

            if (levelSpinner != null) {
                levelSpinner.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                posY = levelSpinner.bottom() + GAP;
            }

            if (augumentationSpinner != null) {
                augumentationSpinner.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                posY = augumentationSpinner.bottom() + GAP;
            }

            if (curseBtn != null) {
                curseBtn.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                posY = curseBtn.bottom() + GAP;
            }

            if (enchantBtn != null) {
                enchantBtn.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                posY = enchantBtn.bottom() + GAP;
            }

            height = posY - y - GAP * 1.5f;
        }

        private void updateItem() {
            text.text(createText());
            ((IconTitle) (wnd.titlebar)).label(item.title());
            ((IconTitle) (wnd.titlebar)).icon(CustomDungeon.getDungeon().getItemImage(item));
            if (wnd.catalogItem != null) wnd.catalogItem.onUpdate();
            wnd.layout(wnd.width);
//            wnd.resize(wnd.width,wnd.height);
        }

        protected String createText() {
            return item.info();
        }
    }

}