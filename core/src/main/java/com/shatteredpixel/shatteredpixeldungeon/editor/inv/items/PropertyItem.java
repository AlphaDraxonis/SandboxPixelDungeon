package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPropertyComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CausticSlimeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RipperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SwarmSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.watabou.noosa.Image;

import java.util.Locale;

public class PropertyItem extends EditorItem<Char.Property> {

    public PropertyItem(Char.Property property) {
        this.obj = property;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditPropertyComp(this);
    }

    @Override
    public String name() {
        return getName(getObject());
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
        return new PropertyItem(getObject());
    }

    public static String getName(Char.Property p) {
        return Messages.get(PropertyItem.class, p.name().toLowerCase(Locale.ENGLISH));
    }

    public static String getDesc(Char.Property p) {
        String desc = Messages.get(PropertyItem.class, p.name().toLowerCase(Locale.ENGLISH) + "_desc");
//        if (!"".equals(desc)) {
//            desc += "\n\n";
//        }
        //resistances and immunities
        return desc;
    }

    public static Image getImage(Char.Property p) {

        switch (p) {

            case BOSS: return new KingSprite();
            case MINIBOSS: return new GreatCrabSprite();
            case BOSS_MINION: return new LarvaSprite();
            case UNDEAD: return new SkeletonSprite();
            case DEMONIC: return new RipperSprite();
            case INORGANIC: return new PylonSprite();
            case FIERY:
                Image icon = EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_LIQFLAME);
                icon.scale.set(ItemSpriteSheet.SIZE / Math.max(icon.width(), icon.height()));
                return icon;
            case ICY:
                icon = EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.POTION_FROST);
                icon.scale.set(ItemSpriteSheet.SIZE / Math.max(icon.width(), icon.height()));
                return icon;
            case ACIDIC: return new CausticSlimeSprite();
            case ELECTRIC:
                icon = EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_RECHARGE);
                icon.scale.set(ItemSpriteSheet.SIZE / Math.max(icon.width(), icon.height()));
                return icon;
            case PERMEABLE: return new GhostSprite();
            case LARGE: return new GolemSprite();
            case IMMOVABLE: return new WandmakerSprite();
            case FLYING: return new SwarmSprite();
        }

        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }
}