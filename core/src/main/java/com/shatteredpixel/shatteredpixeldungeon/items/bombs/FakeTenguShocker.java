package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;

public class FakeTenguShocker extends Bomb {

    {
        image = ItemSpriteSheet.TENGU_SHOCKER;
        dropsDownHeap = true;
    }

    public int duration = 10;

    @Override
    public ItemSprite.Glowing glowing() {
        return null;
    }

    private int throwPos;//we do not need to bundle this

    @Override
    protected void onThrow(int cell) {
        throwPos = cell;
        super.onThrow(cell);
    }

    @Override
    protected Fuse createFuse() {
        TenguShockerFuse fuse = new TenguShockerFuse();
        fuse.timer = duration;
        fuse.shockerPos = throwPos;
        fuse.actAfterThrow();
        return fuse;
    }

    @Override
    public void explode(int cell) {
        //TenguShockerFuse does everything
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {

        if (fuse == null) return super.doPickUp(hero, pos);

        GLog.w(Messages.get(Tengu.ShockerAbility.ShockerItem.class, "cant_pickup"));
        return false;
    }

    @Override
    public String name() {
        return Messages.get(Tengu.ShockerAbility.ShockerItem.class, "name");
    }

    @Override
    public String desc() {
        return Messages.get(Tengu.ShockerAbility.ShockerItem.class, "desc");
    }

    public static class TenguShockerFuse extends Fuse {

        {
            actPriority = BUFF_PRIO;//same as Tengu.BombAbility
        }

        private int shockerPos;
        private TenguShockerAbilityBuff shockerAbility;
        private int bombAbilityId = -1;
        private float timer;

        @Override
        public boolean freeze() {
            //tengu bombs cannot have their fuse snuffed
            return false;
        }

        @Override
        protected boolean act() {
            if (shockerAbility == null) {
                if (bombAbilityId == -1) {
                    shockerAbility = new TenguShockerAbilityBuff();
                    shockerAbility.shockerPos = shockerPos;
                } else {
                    shockerAbility = (TenguShockerAbilityBuff) Actor.findById(bombAbilityId);
                }
            } else spend(TICK);

            if (timer <= 0) {
                trigger(Dungeon.level.heaps.get(shockerPos));
                shockerAbility.stopSpreading = true;
            }

            return shockerAbility.act();
        }

        @Override
        protected void spend(float time) {
            super.spend(time);
            timer -= time;
        }

        private void actAfterThrow() {
            clearTime();
            act();
            spendConstant(-1f);
        }

        private static final String BOMB_ABILITY = "bomb_ability";
        private static final String SHOCKER_POS = "shocker_pos";
        private static final String TIMER = "timer";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOMB_ABILITY, shockerAbility.id());
            bundle.put(SHOCKER_POS, shockerPos);
            bundle.put(TIMER, timer);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            bombAbilityId = bundle.getInt(BOMB_ABILITY);
            shockerPos = bundle.getInt(SHOCKER_POS);
            timer = bundle.getFloat(TIMER);
        }
    }

    public static class TenguShockerAbilityBuff extends Tengu.ShockerAbility {

        boolean stopSpreading = false;

        @Override
        protected void spreadblob() {
            if (!stopSpreading) super.spreadblob();
        }

        @Override
        protected Class<? extends Blob> getBlobClass() {
            return FakeShockerBlob.class;
        }

        @Override
        protected Group getGroupToAddVisuals() {
            return Dungeon.hero.sprite.parent;
        }
    }

    public static class FakeShockerBlob extends Tengu.ShockerAbility.ShockerBlob {

        @Override
        protected void reduceBossScore() {
        }
    }

    private static final String DURATION = "duration";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        duration = bundle.getInt(DURATION);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DURATION, duration);
    }
}