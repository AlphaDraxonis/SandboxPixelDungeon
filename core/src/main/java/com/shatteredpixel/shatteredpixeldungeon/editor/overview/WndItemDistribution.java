package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.ItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.List;

public class WndItemDistribution extends Window {

    private ScrollingListPane distributions;
    private List<DistributionCompInList> distributionComps;

    public WndItemDistribution() {

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 11);
        title.hardlight(Window.TITLE_COLOR);
        title.maxWidth(width);
        title.setPos((width - title.width()) * 0.5f, 3);
        add(title);

        RedButton[] addDistr = new RedButton[2];
        addDistr[0] = new RedButton(Messages.get(this, "add_items")) {
            @Override
            protected void onClick() {
                showCreateDistrDialog(new ItemDistribution.Items());
            }
        };
        addDistr[0].icon(Icons.get(Icons.SEED_POUCH));

        addDistr[1] = new RedButton(Messages.get(this, "add_mobs")) {
            @Override
            protected void onClick() {
                showCreateDistrDialog(new ItemDistribution.Mobs());
            }
        };
        addDistr[1].icon(new GnollSprite());

        float widthPerBtn = (width - addDistr.length * 2 - 6 + 2) / (float) addDistr.length;
        for (int i = 0; i < addDistr.length; i++) {
            addDistr[i].setRect(3 + (widthPerBtn + 2) * i, height - 19.5f, widthPerBtn, 18);
            add(addDistr[i]);
        }

        distributions = new ScrollingListPane();
        add(distributions);

        distributionComps = new ArrayList<>();

        for (ItemDistribution<?> distr : CustomDungeon.getDungeon().getItemDistributions()) {
            DistributionCompInList comp = new DistributionCompInList(distr);
            distributions.addItem(comp);
            distributionComps.add(comp);
        }

        distributions.setRect(0, title.bottom() + 4, width, height - 18 - 3 - title.bottom() - 4);
        add(distributions);
    }

    public void removeDistribution(DistributionCompInList comp) {
        distributionComps.remove(comp);
        distributions.removeItem(comp);
        comp.destroy();
        Dungeon.customDungeon.getItemDistributions().remove(comp.distribution);
    }

    public void addDistribution(ItemDistribution<?> distribution) {
        DistributionCompInList comp = new DistributionCompInList(distribution);
        distributionComps.add(comp);
        distributions.addItem(comp);
        Dungeon.customDungeon.getItemDistributions().add(distribution);
    }

    private <T extends Item> void showCreateDistrDialog(ItemDistribution<T> distribution) {
        Window w = new WndEditItemDistribution<T>(distribution, Messages.get(WndNewFloor.class, "create_label")) {
            @Override
            protected void doAfterPositive() {
                addDistribution(distribution);
            }

        };
        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);
    }

    public static void showWindow() {
        if (Game.scene() instanceof EditorScene) EditorScene.show(new WndItemDistribution());
        else Game.scene().addToFront(new WndItemDistribution());
    }


    private class DistributionCompInList extends AdvancedListPaneItem {
        private final ItemDistribution<?> distribution;
        private IconButton remove;

        public DistributionCompInList(ItemDistribution<?> distribution) {
            super(new Image(), null, "");

            remove = new IconButton(Icons.get(Icons.CLOSE)) {
                @Override
                protected void onClick() {
                    removeDistribution(DistributionCompInList.this);
                }
            };
            add(remove);

            this.distribution = distribution;
        }

        @Override
        protected void onClick() {
            EditorScene.show(new WndEditItemDistribution(distribution, Messages.get(WndItemDistribution.class, "save")) {
                @Override
                protected void doAfterPositive() {
                    updateUI();
                    layout2();
                }
            });
        }

        public void updateUI() {
            label.text(createTitle(distribution, (int) ((width - ICON_WIDTH - 1 - 4) * 2), 9));
            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            if (subIcon != null) {
                remove(subIcon);
                subIcon.destroy();
            }
            if (distribution.getObjectsToDistribute().isEmpty())
                icon = new ItemSprite(ItemSpriteSheet.SOMETHING);
            else {
                if (distribution instanceof ItemDistribution.Items) {
                    Item i = ((ItemDistribution.Items) distribution).getObjectsToDistribute().get(0);
                    icon = CustomDungeon.getDungeon().getItemImage(i);
                    subIcon = EditorUtilies.createSubIcon(i);
                    if (i.level() != 0) {
                        lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, i.level()));
                        lvlLabel.measure();
                        if ((i instanceof Weapon && ((Weapon) i).curseInfusionBonus)
                                || (i instanceof Armor && ((Armor) i).curseInfusionBonus)
                                || (i instanceof Wand && ((Wand) i).curseInfusionBonus)) {
                            lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
                        } else {
                            lvlLabel.hardlight(ItemSlot.UPGRADED);
                        }
                    } else lvlLabel.text(null);
                } else {
                    subIcon = null;
                    lvlLabel.text(null);
                    if (distribution instanceof ItemDistribution.Mobs)
                        icon = ((ItemDistribution.Mobs) distribution).getObjectsToDistribute().get(0).mob().sprite();
                    else icon = new ItemSprite(ItemSpriteSheet.SOMETHING);
                }
            }
            lvlLabel.measure();
            add(icon);
            bringToFront(lvlLabel);
            if (subIcon != null) add(subIcon);
        }

        @Override
        protected void layout() {
            if (distribution != null) updateUI();
            layout2();
        }

        private void layout2() {
            if (remove != null && remove.visible) {
                remove.setRect(x + width - 16, y + (height - 16) * 0.5f, 16, 16);
                super.layout();
                hotArea.width -= remove.width() + 2;
            } else {
                super.layout();
            }
        }

        @Override
        protected int getLabelMaxWidth() {
            return super.getLabelMaxWidth() - 16 - 4;
        }
    }

    private static String createTitle(ItemDistribution<?> distribution, int width, int fontSize) {
        StringBuilder b = new StringBuilder();
        for (String l : distribution.getLevels()) {
            b.append(l).append(", ");
        }
        int length = b.length();
        if (length > 0 && width > 0) {
            b.delete(length - 2, length);
            RenderedTextBlock tester = PixelScene.renderTextBlock(fontSize);
            tester.text(b.toString());
            if (tester.width() > width) {
                while (tester.width() > width && length >= 4) {
                    b.delete(length - 4, length - 2);
                    length -= 2;
                    tester.text(b.toString());
                }
                length = b.length();
                if (length > 0 && b.substring(length - 1, length).equals(",")) b.append(" ");
                b.append("...");
            }
            tester.destroy();
        } else b.append(" ");
        return b.toString();
    }
}