package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobSpriteComp;
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
        this.mob = (Mob) ((MobSpriteItem)MobSprites.bag.findItem(sprite)).mob().getCopy();
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

    public static boolean canChangeSprite(Mob mob) {
        return canSpriteBeUsedForOthers(mob)
                && !(mob instanceof Ghoul || mob instanceof Golem || mob instanceof Eye
                || mob instanceof Goo || mob instanceof RipperDemon || mob instanceof SentryRoom.Sentry || mob instanceof CrystalGuardian);
    }

    public static boolean canSpriteBeUsedForOthers(Mob mob) {
        return !(mob instanceof Mimic || mob instanceof Statue || mob instanceof HeroMob || mob instanceof CrystalSpire || mob instanceof DM300
                || mob instanceof SentryRoom.Sentry || mob instanceof GnollGeomancer || mob instanceof GnollGuard || mob instanceof Pylon);
    }
}