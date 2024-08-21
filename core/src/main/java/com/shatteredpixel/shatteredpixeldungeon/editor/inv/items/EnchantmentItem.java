package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditEnchantmentComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class EnchantmentItem extends EditorItem<EnchantmentLike> {

    public EnchantmentItem(EnchantmentLike ench) {
        this.obj = ench;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditEnchantmentComp(this);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        return getImage(getObject());
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    @Override
    public Item getCopy() {
        return new EnchantmentItem(getObject());
    }

    public static Image getImage(EnchantmentLike ench) {

        if (ench instanceof Blazing) return new BuffIcon(BuffIndicator.FIRE, true);
        if (ench instanceof Blocking) {
            Image result = new BuffIcon(BuffIndicator.ARMOR, true);
            result.hardlight(0.5f, 1f, 2f);
            return result;
        }
        if (ench instanceof Blooming) return new TileSprite(Assets.Environment.TILES_CAVES, Terrain.HIGH_GRASS);
        if (ench instanceof Chilling) return new BuffIcon(BuffIndicator.FROST, true);
        if (ench instanceof Corrupting) return new BuffIcon(BuffIndicator.CORRUPT, true);
        if (ench instanceof Grim) {
            Image result = new BuffIcon(BuffIndicator.PREPARATION, true);
            result.hardlight(1f, 0f, 0f);
            return result;
        }
        if (ench instanceof Kinetic) return new BuffIcon(BuffIndicator.WEAPON, true);
        if (ench instanceof Lucky) {
            Image result = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.RING_WEALTH);
            result.scale.set(ItemSpriteSheet.SIZE / Math.max(result.width, result.height));
            return result;
        }
        if (ench instanceof Projecting) return new ItemSprite(ItemSpriteSheet.SPEAR);
        if (ench instanceof Shocking) return new ItemSprite(ItemSpriteSheet.WAND_LIGHTNING);
        if (ench instanceof Vampiric) {
            Image result = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.POTION_HEALING);
            result.scale.set(ItemSpriteSheet.SIZE / Math.max(result.width, result.height));
            return result;
        }
        if (ench instanceof Sacrificial) return new BuffIcon(BuffIndicator.SACRIFICE, true);

        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }
}