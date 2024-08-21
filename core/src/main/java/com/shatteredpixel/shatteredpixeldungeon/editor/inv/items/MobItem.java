package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.CustomObjectClass;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

public class MobItem extends EditorItem<Mob> {

    public MobItem() {
    }

    public MobItem(Mob mob) {
        this.obj = mob;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditMobComp(getObject());
    }

    @Override
    public String name() {
        Mob mob = getObject();

        if (mob instanceof CustomObjectClass) {
            return UserContentManager.getName(((CustomObjectClass) mob).getIdentifier());
        }

        if (MobSpriteItem.canChangeSprite(mob)) {
            Mob defaultMob = DefaultStatsCache.getDefaultObject(mob.getClass());
            if (defaultMob == null && MobSpriteItem.canChangeSprite(mob)) defaultMob = Reflection.newInstance(mob.getClass());
            if (MobSpriteItem.isSpriteChanged(mob)) {
                    return mob.name() + " (" + defaultMob.name() + ")";
            }
        }
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        Mob mob = getObject();
        if (mob.sprite == null) mob.sprite = mob.sprite();
        return getObject().sprite();
    }

    @Override
    public Item getCopy() {
        return new MobItem((Mob) getObject().getCopy());
    }

    @Override
    public void setObject(Mob obj) {
        Mob copy = (Mob) obj.getCopy();
        copy.pos = -1;
        copy.turnToCell = -1;
        super.setObject(copy);
    }

    @Override
    public void place(int cell) {
        Mob place = (Mob) getObject().getCopy();
        Mob remove = Dungeon.level.findMob(cell);

        if (!invalidPlacement(place, cell) && !EditMobComp.areEqual(place, remove)) {
            Undo.addActionPart(remove(remove));
            Undo.addActionPart(place(place, cell));
        }
    }

    public static boolean invalidPlacement(Mob mob, int cell) {
        return (Dungeon.level.solid[cell] && (!(mob instanceof EbonyMimic) || !TileItem.isDoor(Dungeon.level.map[cell])))
                || (Dungeon.level.pit[cell] && !mob.isFlying()) || !Dungeon.level.insideMap(cell)
                || (!Dungeon.level.openSpace[cell] && Char.hasProp(mob, Char.Property.LARGE));
    }

    public static ActionPart remove(Mob mob) {
        if (mob != null) {
            return new MobActionPart.Remove(mob);
        }
        return null;
    }

    private static ActionPart place(Mob mob, int cell) {
        if (mob != null) {
            mob.pos = cell;
            return new MobActionPart.Place(mob);
        }
        return null;
    }


    private static final String MOB = "mob";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(MOB, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Mob) bundle.get(MOB);
    }

    public Mob mob() {
        return getObject();
    }
}