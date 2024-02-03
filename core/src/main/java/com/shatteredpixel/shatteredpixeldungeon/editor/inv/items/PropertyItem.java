package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPropertyComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
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

            case BOSS:
                break;
            case MINIBOSS:
                break;
            case BOSS_MINION:
                break;
            case UNDEAD:
                break;
            case DEMONIC:
                break;
            case INORGANIC:
                break;
            case FIERY:
                break;
            case ICY:
                break;
            case ACIDIC:
                break;
            case ELECTRIC:
                break;
            case PERMEABLE:
                break;
            case LARGE:
                break;
            case IMMOVABLE:
                break;
        }

        return new ItemSprite(ItemSpriteSheet.SOMETHING);
    }
}