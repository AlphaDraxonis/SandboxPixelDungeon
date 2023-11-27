package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

public class FakeTenguBomb extends Bomb {

    {
        image = ItemSpriteSheet.TENGU_BOMB;
        dropsDownHeap = true;
    }

    private int throwPos;//we do not need to bundle this

    @Override
    protected void onThrow(int cell) {
        throwPos = cell;
        super.onThrow(cell);
    }

    @Override
    protected Fuse createFuse() {
        TenguBombFuse fuse = new TenguBombFuse();
        fuse.bombPos = throwPos;
        fuse.actAfterThrow();
        return fuse;
    }

    @Override
    public Emitter emitter() {
        return fuse == null ? super.emitter() : Tengu.BombAbility.BombItem.staticEmitter();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return null;
    }

    @Override
    public void explode(int cell) {
        //TenguBombFuse does everything
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {

        if (fuse == null) return super.doPickUp(hero, pos);

        GLog.w( Messages.get(Tengu.BombAbility.BombItem.class, "cant_pickup") );
        return false;
    }

    @Override
    public String name() {
        return Messages.get(Tengu.BombAbility.BombItem.class, "name");
    }

    @Override
    public String desc() {
        return Messages.get(Tengu.BombAbility.BombItem.class, "desc");
    }

    public static class TenguBombFuse extends Fuse {

        {
            actPriority = BUFF_PRIO;//same as Tengu.BombAbility
        }

        private int bombPos;
        private TenguBombAbilityBuff bombAbility;
        private int bombAbilityId = -1;

        @Override
        public boolean freeze() {
            //tengu bombs cannot have their fuse snuffed
            return false;
        }

        @Override
        protected boolean act() {
            if (bombAbility == null) {
                if (bombAbilityId == -1) {
                    bombAbility = new TenguBombAbilityBuff();
                    bombAbility.bombPos = bombPos;
                    bombAbility.fuse = this;
                } else {
                    bombAbility = (TenguBombAbilityBuff) Actor.findById(bombAbilityId);
                    bombAbility.fuse = this;
                }
            }
            else spend(TICK);
            return bombAbility.act();
        }

        private void actAfterThrow() {
            clearTime();
            act();
            spendConstant(-1f);
        }

        private static final String BOMB_ABILITY = "bomb_ability";
        private static final String BOMB_POS = "bomb_pos";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOMB_ABILITY, bombAbility.id());
            bundle.put(BOMB_POS, bombPos);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            bombAbilityId = bundle.getInt(BOMB_ABILITY);
            bombPos = bundle.getInt(BOMB_POS);
        }
    }

    private static class TenguBombAbilityBuff extends Tengu.BombAbility {

        private TenguBombFuse fuse;

        @Override
        public void detach() {
            fuse.trigger(Dungeon.level.heaps.get(bombPos));
            fx(false);
        }

        @Override
        protected void reduceBossScore() {
        }
    }
}