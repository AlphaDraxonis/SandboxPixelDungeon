package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WndChooseEnchant extends Window {

    public static final int BUTTON_HEIGHT = 18, GAP = 1;
    private final int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
    private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.9);

    private final Item item;

    private final IconTitle titleBar;
    private final RenderedTextBlock text;
    private final ScrollPane sp;
    private final Component wrapper;
    private final BtnRow randomEnchant, randomCurse, remove;
    private final RedButton scrollUp, scrollDown, cancel;

    private final Integer[] titlePos;

    public WndChooseEnchant(Item item) {
        super();
        this.item = item;

        titleBar = new IconTitle(new ItemSprite(item.image), Messages.titleCase("Choose enchantment/glyph"));
        add(titleBar);

        text = PixelScene.renderTextBlock("Select a curse or enchantment/glyph or remove existing one", 6);
        add(text);

        String titleRandom = item instanceof Weapon ? "Random enchantment" : "Random glyph";
        String descRandom = item instanceof Weapon ?
                "Assigns a random, but different enchantment.\n" + showChances(Weapon.Enchantment.typeChances) :
                "Assigns a random, but different glyph.\n" + showChances(Armor.Glyph.typeChances);
        randomEnchant = new BtnRow(titleRandom, descRandom) {
            @Override
            protected void onClick() {
                randomEnchantment(item);
                finish();
            }
        };
        randomCurse = new BtnRow("Random curse", "Assigns a random, but different curse") {
            @Override
            protected void onClick() {
                randomCurse(item);
                finish();
            }
        };
        String descRemove = item instanceof Weapon ? "Removes the current curse or enchantment" : "Removes the current curse or glyph";
        remove = new BtnRow("Remove", descRemove) {
            @Override
            protected void onClick() {
                removeEnchantment(item);
                finish();
            }
        };

        scrollUp = new RedButton("Scroll up", 7) {
            @Override
            protected void onClick() {
                scrollUp();
            }
        };
        add(scrollUp);
        scrollDown = new RedButton("Scroll down", 7) {
            @Override
            protected void onClick() {
                scrollDown();
            }
        };
        add(scrollDown);
        cancel = new RedButton("Cancel", 7) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        add(cancel);


        wrapper = new Component();

        resize(WIDTH, HEIGHT);

        titleBar.setRect(0, 0, WIDTH, titleBar.height());

        text.maxWidth(WIDTH - GAP * 2);
        text.setRect(0, titleBar.bottom() + GAP * 4, WIDTH, text.height());

        Class<?>[][] enchantments = new Class[4][];

        if (item instanceof Weapon) {
            enchantments[0] = Weapon.Enchantment.common;
            enchantments[1] = Weapon.Enchantment.uncommon;
            enchantments[2] = Weapon.Enchantment.rare;
            enchantments[3] = Weapon.Enchantment.curses;
        } else {
            enchantments[0] = Armor.Glyph.common;
            enchantments[1] = Armor.Glyph.uncommon;
            enchantments[2] = Armor.Glyph.rare;
            enchantments[3] = Armor.Glyph.curses;
        }

        float pos = 0;

        List<Integer> titlePosList = new ArrayList<>();
        pos = addEnchantmentCategory(wrapper, pos, titlePosList,"Other", randomEnchant, randomCurse, remove);
        for (int i = 0; i < enchantments.length; i++) {
            pos = addEnchantmentCategory(wrapper, pos, titlePosList,
                    Messages.titleCase(getRarityName(i)),
                    createEnchantmentCategoryRows(enchantments[i]));
        }
        titlePos = titlePosList.toArray(new Integer[0]);

        wrapper.setSize(WIDTH, pos);

        float bh = BUTTON_HEIGHT * 2 / 3f;
        pos = HEIGHT - bh;
        float bw = WIDTH / 3f;
        scrollUp.setRect(0, pos, bw, bh);
        scrollDown.setRect(scrollUp.right(), pos, bw, bh);
        cancel.setRect(scrollDown.right(), pos, bw, bh);

        sp = new ScrollPane(wrapper);
        add(sp);
        float posY = text.bottom() + GAP * 2.5f;
        sp.setRect(0, posY, WIDTH, HEIGHT - posY - 2 * GAP - bh);
    }


    private BtnRow[] createEnchantmentCategoryRows(Class<?>[] enchantments) {
        BtnRow[] ret = new BtnRow[enchantments.length];
        for (int i = 0; i < enchantments.length; i++) {
            if (item instanceof Weapon) {
                Weapon.Enchantment enchantment = (Weapon.Enchantment) Reflection.newInstance(enchantments[i]);
                ret[i] = new BtnRow(enchantment.name(), enchantment.desc()) {
                    @Override
                    protected void onClick() {
                        ((Weapon) item).enchant(enchantment);
                        finish();
                    }
                };
            } else if (item instanceof Armor) {
                Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(enchantments[i]);
                ret[i] = new BtnRow(glyph.name(), glyph.desc()) {
                    @Override
                    protected void onClick() {
                        ((Armor) item).inscribe(glyph);
                        finish();
                    }
                };
            } else ret[i] = null;
        }
        return ret;
    }

    private float addEnchantmentCategory(Component wrapper, float pos, List<Integer> titlePosList, String name, BtnRow... rows) {
        titlePosList.add((int) pos);
        pos += GAP * 3;
        RenderedTextBlock tb = PixelScene.renderTextBlock(name, 10);
        tb.maxWidth(WIDTH - GAP * 2);
        tb.hardlight(Window.TITLE_COLOR);
        tb.setPos((int) ((WIDTH - tb.width()) / 2), pos);
        PixelScene.align(tb);
        wrapper.add(tb);
        pos = tb.bottom() + 3 * GAP;

        for (BtnRow row : rows) {
            row.setRect(0, pos, WIDTH, BUTTON_HEIGHT);
            pos = row.bottom() + GAP;
            wrapper.add(row);
        }

        return pos;
    }

    private void scrollUp() {
        float y = sp.content().camera.scroll.y - 3 * GAP;
        for (int i = titlePos.length - 1; i >= 0; i--) {
            if (titlePos[i] < y) {
                y = titlePos[i];
                break;
            }
        }
        sp.scrollTo(sp.camera.x, y);
    }

    private void scrollDown() {
        float y = sp.content().camera.scroll.y + 3 * GAP;
        for (int i = 0; i < titlePos.length; i++) {
            if (titlePos[i] > y) {
                y = titlePos[i];
                break;
            }
        }
        sp.scrollTo(sp.camera.x, y);
    }

    protected void finish() {
        hide();
    }


    private static String showChances(float[] typeChances) {
        StringBuilder b = new StringBuilder("The chances for each rarity are:");
        for (int i = 0; i < typeChances.length; i++) {
            b.append("\n_-_ ").
                    append(Messages.decimalFormat("#.##", typeChances[i])).
                    append("% for ").append(getRarityName(i));
        }
        return b.toString();
    }

    public static String getRarityName(int index) {
        switch (index) {
            case 0:
                return "common";
            case 1:
                return "uncommon";
            case 2:
                return "rare";
            case 3:
                return "curses";
        }
        return Messages.NO_TEXT_FOUND;
    }

    public static Item randomCurse(Item item) {
        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            return w.enchant(Weapon.Enchantment.randomCurse(w.enchantment != null ? w.enchantment.getClass() : null));
        }
        if (item instanceof Armor) {
            Armor a = (Armor) item;
            return a.inscribe(Armor.Glyph.randomCurse(a.glyph != null ? a.glyph.getClass() : null));
        }
        return null;
    }

    public static Item randomEnchantment(Item item) {
        if (item instanceof Weapon) return ((Weapon) item).enchant();
        if (item instanceof Armor) return ((Armor) item).inscribe();
        return null;
    }

    public static void removeEnchantment(Item item) {
        if (item instanceof Weapon) ((Weapon) item).enchantment = null;
        else if (item instanceof Armor) ((Armor) item).glyph = null;
    }


    private static class BtnRow extends Component {

        private RedButton btn;
        private IconButton infoBtn;

        private String name, info;

        public BtnRow(String name, String info) {
            super();
            this.name = Messages.titleCase(name);
            this.info = info;

            btn = new RedButton(this.name, 8) {
                @Override
                protected void onClick() {
                    BtnRow.this.onClick();
                }
            };
            add(btn);
            infoBtn = new IconButton(Icons.get(Icons.INFO)) {
                @Override
                protected void onClick() {
                    onInfo();
                }
            };
            add(infoBtn);
        }

        protected void onClick() {
        }

        protected void onInfo() {
            EditorScene.show(new WndTitledMessage(
                    Icons.get(Icons.INFO),
                    name, info));
        }

        @Override
        protected void layout() {
            btn.setRect(x, y, width - BUTTON_HEIGHT, BUTTON_HEIGHT);
            infoBtn.setRect(width - BUTTON_HEIGHT, y, BUTTON_HEIGHT, BUTTON_HEIGHT);
        }
    }

}
