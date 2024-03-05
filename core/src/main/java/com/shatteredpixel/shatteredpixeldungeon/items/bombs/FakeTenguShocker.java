package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
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
    public void trigger(int cell) {
        throwPos = cell;
        super.trigger(cell);
    }

    @Override
    protected Fuse createFuse() {
        TenguShockerFuse fuse = new TenguShockerFuse();
        fuse.timer = duration;
        fuse.shockerPos = throwPos;
        fuse.quantity = quantity();
        fuse.actAfterThrow(!igniteOnDrop);
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
        if (customName != null) return super.name();
        return Messages.get(Tengu.ShockerAbility.ShockerItem.class, "name");
    }

    @Override
    public String desc() {
        if (customDesc != null) return super.desc();
        return Messages.get(Tengu.ShockerAbility.ShockerItem.class, "desc");
    }

    @Override
    public boolean isSimilar(Item item) {
        return super.isSimilar(item) && ((FakeTenguShocker) item).duration == duration;
    }

    public static class TenguShockerFuse extends Fuse {

        {
            actPriority = BUFF_PRIO;//same as Tengu.BombAbility
        }

        private int shockerPos, quantity;
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
                if (bombAbilityId == -1 || (shockerAbility = (TenguShockerAbilityBuff) Actor.findById(bombAbilityId)) == null) {
                    shockerAbility = new TenguShockerAbilityBuff();
                    shockerAbility.shockerPos = shockerPos;
                    shockerAbility.quantity = quantity;
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

        private void actAfterThrow(boolean evolveBlob) {
            Tengu.ShockerAbility.ShockerBlob blob = Dungeon.level.blobs.getOnly(FakeShockerBlob.class);
            boolean newBlob = blob == null;
            clearTime();
            act();
            spendConstant(-1f);

            if (newBlob && evolveBlob) {
                blob = Dungeon.level.blobs.getOnly(FakeShockerBlob.class);
                blob.actAfterThrow();
            }
        }

        private static final String BOMB_ABILITY = "bomb_ability";
        private static final String SHOCKER_POS = "shocker_pos";
        private static final String TIMER = "timer";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOMB_ABILITY, shockerAbility == null ? -1 : shockerAbility.id());
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
        private boolean firstSpread = true;

        @Override
        protected void spreadblob(int quantity) {
            if (!stopSpreading) {
                super.spreadblob(quantity);
                if (firstSpread) {
                    ShockerBlob blob = Dungeon.level.blobs.getOnly(getBlobClass());
                    blob.seedNoCooldown(shockerPos, quantity);
                    firstSpread = false;
                }
            }
        }

        @Override
        protected Class<? extends ShockerBlob> getBlobClass() {
            return FakeShockerBlob.class;
        }

        @Override
        protected Group getGroupToAddVisuals() {
            return Dungeon.hero.sprite.parent;
        }

        private static final String FIRST_SPREAD = "first_spread";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(FIRST_SPREAD, firstSpread);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            firstSpread = bundle.getBoolean(FIRST_SPREAD);
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