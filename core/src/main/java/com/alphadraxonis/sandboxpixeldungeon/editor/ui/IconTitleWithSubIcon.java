package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.items.Heap;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class IconTitleWithSubIcon extends IconTitle {


    private Image subIcon;

    public IconTitleWithSubIcon() {
        super();
        subIcon = null;
    }

    public IconTitleWithSubIcon(Item item) {
        Image icon = CustomDungeon.getDungeon().getItemImage(item);
        icon(icon);
        subIcon = createSubIcon(item);
        if (subIcon != null) add(subIcon);
        label(Messages.titleCase(item.title()));
    }

    public IconTitleWithSubIcon(Heap heap) {
        ItemSprite icon = new ItemSprite();
        subIcon = null;
        icon(icon);
        label(Messages.titleCase(heap.title()));
        icon.view(heap);
    }

    public IconTitleWithSubIcon(Image icon, Image subIcon, String label) {
        super();

        this.subIcon = subIcon;
        icon(icon);
        label(label);
        if (subIcon != null) addToFront(this.subIcon);

    }

    @Override
    protected void layout() {
        super.layout();

        if (subIcon != null && imIcon != null) {
            subIcon.x = x + Math.max(imIcon.width, 16) - (ItemSpriteSheet.Icons.SIZE + subIcon.width()) / 2f;
            subIcon.y = y - 0.5f + (ItemSpriteSheet.Icons.SIZE - subIcon.height) / 2f;
            PixelScene.align(subIcon);
        }

    }

    @Override
    public void icon(Image icon) {
        super.icon(icon);
        if (subIcon != null) bringToFront(subIcon);
    }

    public static Image createSubIcon(Item item) {
        RectF r = ItemSpriteSheet.Icons.film.get(item.icon);
        if (r == null) return null;
        Image itemIcon = new Image(Assets.Sprites.ITEM_ICONS);
        itemIcon.frame(r);
        return itemIcon;
    }

}