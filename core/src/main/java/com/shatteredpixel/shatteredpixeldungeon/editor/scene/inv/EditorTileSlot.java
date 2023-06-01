package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public class EditorTileSlot extends InventorySlot {

    private int terrainFeature = -1;

    public EditorTileSlot(TileItem terrainFeatureItem) {
        super(terrainFeatureItem);
    }

    public EditorTileSlot(int terrainFeature) {
        super(new TileItem(terrainFeature,-1));
    }

    @Override
    public void item(Item item) {
        super.item(item);
        float size = Math.min(PixelScene.landscape() ? WndBag.SLOT_WIDTH_L : WndBag.SLOT_WIDTH_P,
                PixelScene.landscape() ? WndBag.SLOT_HEIGHT_L : WndBag.SLOT_HEIGHT_P) * 0.8f;
        float scale = size / ItemSprite.SIZE;
        sprite.scale.set(PixelScene.align(scale));
        sprite.width *= scale;
        sprite.height *= scale;
    }
}