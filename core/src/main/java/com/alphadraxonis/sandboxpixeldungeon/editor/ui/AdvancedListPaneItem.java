package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;

public abstract class AdvancedListPaneItem extends ScrollingListPane.ListItem {

    protected Image subIcon;
    protected ColorBlock bg;
    protected BitmapText lvlLabel;

    public AdvancedListPaneItem(Image icon, Image subIcon, String text) {
        super(icon, text);
        if ((this.subIcon = subIcon) != null) add(subIcon);

        onUpdate();
    }

    @Override
    protected void createChildren(Object... params) {
        bg = new ColorBlock(1, 1, -16777216);
        bg.color(0.5882f, 0.2117f, 0.2745f);//150 54 70 255
        bg.visible = false;
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

        if (subIcon != null && icon != null) {
            subIcon.x = x + ICON_WIDTH - (ItemSpriteSheet.Icons.SIZE + subIcon.width()) / 2f;
            subIcon.y = y + 0.5f + (ItemSpriteSheet.Icons.SIZE - subIcon.height) / 2f;
            PixelScene.align(subIcon);
        }
        if (lvlLabel != null) {
            lvlLabel.x = x + (ICON_WIDTH - lvlLabel.width());
            lvlLabel.y = y + (height - lvlLabel.baseLine() - 1);
            PixelScene.align(lvlLabel);
        }
    }

    public void onUpdate() {
        layout();
    }

    public void onUpdateIfUsedForItem(Item item){
        if (item == null) return;

        //IMPORTANT: any change made here should also be made in ItemSlot#updateText()

        bg.visible = item.cursed;
        label.text(Messages.titleCase(item.title()));

        if (icon != null) remove(icon);
        icon = CustomDungeon.getDungeon().getItemImage(item);
        addToBack(icon);
        remove(bg);
        addToBack(bg);

        //IMPORTANT: any change made here should also be made in ItemSlot#updateText()

        int lvl = item.level();
        if (lvl != 0) {
            lvlLabel.text(Messages.format(ItemSlot.TXT_LEVEL, lvl));
            lvlLabel.measure();
            if ((item instanceof Weapon && ((Weapon) item).curseInfusionBonus)
                    || (item instanceof Armor && ((Armor) item).curseInfusionBonus)
                    || (item instanceof Wand && ((Wand) item).curseInfusionBonus)) {
                lvlLabel.hardlight(ItemSlot.CURSE_INFUSED);
            } else {
                lvlLabel.hardlight(ItemSlot.UPGRADED);
            }
        } else lvlLabel.text(null);

    }

}