package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobSpriteComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.MobSprites;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class MobSpriteItem extends EditorItem<Class<? extends CharSprite>> {

    private Mob mob;

    public MobSpriteItem() {
    }

    public MobSpriteItem(Mob mob) {
        this.mob = mob;
        this.obj = mob.spriteClass;
    }

    public MobSpriteItem(Mob mob, Class<? extends CharSprite> sprite) {
        this.mob = mob;
        this.obj = sprite;
        this.mob.spriteClass = sprite;
    }

    public MobSpriteItem(Class<? extends CharSprite> sprite) {
        this.obj = sprite;
        this.mob = (Mob) ((MobSpriteItem)MobSprites.bag.findItem(new FindInBag(FindInBag.Type.CLASS, sprite, null))).mob().getCopy();
        this.mob.spriteClass = obj;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditMobSpriteComp(this);
    }

    @Override
    public String name() {
        return mob.name();
    }

    @Override
    public Image getSprite() {
        return Reflection.newInstance(getObject());
    }

    @Override
    public Item getCopy() {
        Mob copy = (Mob) mob.getCopy();
        copy.spriteClass = obj;
        return new MobSpriteItem(copy);
    }

    @Override
    public void place(int cell) {
        //can't be placed
    }

    public Mob mob() {
        return mob;
    }

    public static boolean isSpriteChanged(Mob mob) {
        return isSpriteChanged(mob, mob.sprite);
    }

    public static boolean isSpriteChanged(Mob mob, CharSprite mobSprite) {
        Mob defMob = DefaultStatsCache.getDefaultObject(mob.getClass());
        if (defMob == null && MobSpriteItem.canChangeSprite(mob)) defMob = Reflection.newInstance(mob.getClass());
        return mobSprite != null && defMob != null && defMob.spriteClass != mob.spriteClass
                && (mobSprite.getClass().getEnclosingClass() == null ||
                mobSprite.getClass().getEnclosingClass() != defMob.spriteClass.getEnclosingClass()
                || mob.getClass().getEnclosingClass() != null);
    }

    public static boolean canChangeSprite(Mob mob) {
        return canSpriteBeUsedForOthers(mob)
                && !(mob instanceof Ghoul || mob instanceof Golem || mob instanceof Eye || mob instanceof DM300 || mob instanceof GnollGuard
                || mob instanceof Goo || mob instanceof RipperDemon || mob instanceof SentryRoom.Sentry || mob instanceof CrystalGuardian || mob instanceof Pylon);
    }

    public static boolean canSpriteBeUsedForOthers(Mob mob) {
        return !(mob instanceof Mimic || mob instanceof Statue || mob instanceof HeroMob || mob instanceof CrystalSpire
                || mob instanceof SentryRoom.Sentry || mob instanceof GnollGeomancer);
    }
}