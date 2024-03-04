package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class FakeTenguBomb extends Bomb {

    {
        image = ItemSpriteSheet.TENGU_BOMB;
        dropsDownHeap = true;
    }

    private int throwPos = -1;//we do not need to bundle this

    @Override
    public void trigger(int cell) {
        throwPos = cell;
        super.trigger(cell);
    }

    @Override
    protected Fuse createFuse() {
        TenguBombFuse fuse = new TenguBombFuse();
        fuse.throwPos = throwPos;
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

    private boolean showFakeParticles = true;
    @Override
    public void explode(int cell) {
        if (showFakeParticles) {
            ArrayList<Emitter> smokeEmitters = new ArrayList<>(5);
            Tengu.BombAbility.fxStatic(true, cell, smokeEmitters);
            Tengu.BombAbility.doExplode(cell, null);
            Tengu.BombAbility.fxStatic(false, cell, smokeEmitters);
        }
        else Tengu.BombAbility.doExplode(cell, null);
    }

    @Override
    public boolean explodesDestructively() {
        return false;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {

        if (fuse == null) return super.doPickUp(hero, pos);

        GLog.w( Messages.get(Tengu.BombAbility.BombItem.class, "cant_pickup") );
        return false;
    }

    @Override
    public String name() {
        if (customName != null) return customName;
        return Messages.get(Tengu.BombAbility.BombItem.class, "name");
    }

    @Override
    public String desc() {
        if (customDesc != null) return customDesc;
        return Messages.get(Tengu.BombAbility.BombItem.class, "desc");
    }

    public static class TenguBombFuse extends Fuse {

        {
            actPriority = BUFF_PRIO;//same as Tengu.BombAbility
        }

        private int throwPos = -1;

        private int timer = 3;
        private boolean fxIsQueued = false;

        protected ArrayList<Emitter> smokeEmitters = new ArrayList<>();

        @Override
        public boolean freeze() {
            //tengu bombs cannot have their fuse snuffed
            return false;
        }

        @Override
        protected boolean act() {

            if (throwPos != -1) {
                if (Game.scene() instanceof GameScene) fx(true, throwPos);
                else {
                    final int p = throwPos;
                    GameScene.runAfterCreate.add(() -> fx(true, p));
                    fxIsQueued = true;
                }
                Tengu.BombAbility.showTimer(throwPos, timer);
                timer--;
                throwPos = -1;
                return true;
            }

            //something caused our bomb to explode early, or be defused. Do nothing.
            if (bomb.fuse != this){
                Actor.remove( this );
                fx(false, -1);
                return true;
            }

            int bombPos = -1;
            //look for our bomb, remove it from its heap, and blow it up.
            for (Heap heap : Dungeon.level.heaps.valueList()) {
                if (heap.items.contains(bomb)) {
                    bombPos = heap.pos;
                    break;
                }
            }
            if (bombPos == -1) {
                //can't find our bomb, something must have removed it, do nothing.
                bomb.fuse = null;
                Actor.remove( this );
                fx(false, -1);
                return true;
            }

            if (smokeEmitters.isEmpty() && !fxIsQueued){
                fx(true, bombPos);
            }

            if (!Tengu.BombAbility.showTimer(bombPos, timer)){
                trigger(Dungeon.level.heaps.get(bombPos));
                return true;
            }

            timer--;
            spend(TICK);
            return true;
        }

        public void fx(boolean on, int bombPos) {
            Tengu.BombAbility.fxStatic(on, bombPos, smokeEmitters);
        }

        @Override
        protected void trigger(Heap heap) {
            ((FakeTenguBomb) bomb).showFakeParticles = false;
            super.trigger(heap);
            fx(false, -1);
        }

        private void actAfterThrow() {
            clearTime();
            act();
            spendConstant(-1f);
        }

        private static final String FX_IS_QUEUED = "fx_is_queued";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(FX_IS_QUEUED, fxIsQueued);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            fxIsQueued = bundle.getBoolean(FX_IS_QUEUED);
        }
    }
}