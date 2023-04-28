package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;

public class WndInfoEq extends WndTitledMessage {

    private final WndEditorSettings.ItemTab.CatalogItem catalogItem;

    public WndInfoEq(Item item, WndEditorSettings.ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
        this(new IconTitle(item), catalogItem, bodyFactory);
    }

    public WndInfoEq(Image icon, Image subIcon, String title, WndEditorSettings.ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
        this(new IconTitleWithSubIcon(icon, subIcon, title), catalogItem, bodyFactory);
    }

    public WndInfoEq(IconTitle title, WndEditorSettings.ItemTab.CatalogItem catalogItem, BodyFactory bodyFactory) {
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
            curseBtn = new CurseButton(item) {
                @Override
                protected void onChange() {
                    updateItem();
                }
            };

            if (item.isUpgradable()) {
                levelSpinner = new LevelSpinner(item) {
                    @Override
                    protected void onChange() {
                        updateItem();
                    }
                };
                add(levelSpinner);
            } else levelSpinner = null;

            if (item instanceof Weapon || item instanceof Armor) {//Missles support enchantments too
                enchantBtn = new RedButton("Enchant") {
                    @Override
                    protected void onClick() {
                        if (item instanceof Weapon || item instanceof Armor)
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
            add(curseBtn);
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

            curseBtn.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
            posY = curseBtn.bottom() + GAP;

            if (enchantBtn != null) {
                enchantBtn.setRect(text.left(), posY, width, WndMenuEditor.BTN_HEIGHT);
                posY = enchantBtn.bottom();
            }

            height = posY - y;
        }

        private void updateItem() {
            text.text(createText());
            ((IconTitle) (wnd.titlebar)).label(item.title());
            ((IconTitle) (wnd.titlebar)).icon(CustomDungeon.getDungeon().getItemImage(item));
            if (wnd.catalogItem != null) wnd.catalogItem.updateItem();
            wnd.layout();
//            wnd.resize(wnd.width,wnd.height);
        }

        protected String createText() {
            return item.info();
        }
    }

}