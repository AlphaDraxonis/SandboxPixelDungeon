package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.watabou.utils.Bundle;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class HeroMob extends Mob {

    //TODO set level and dont forget exp,   exp cannot be manually changed
    //desc

    {
        spriteClass = null;

        HP = HT = 20;

        maxLvl = 29;
    }

    private InternalHero internalHero;

    public HeroMob() {
    }

    protected HeroMob(InternalHero internalHero) {
        setInternalHero(internalHero);
    }

    @Override
    public CharSprite sprite() {
        HeroSprite.HeroMobSprite sprite = new HeroSprite.HeroMobSprite(internalHero, this) {
            @Override
            public void link(Char ch) {
                super.link(ch);
                internalHero.sprite = this;
            }
        };
        sprite.updateArmor(internalHero);
        return sprite;
    }

    @Override
    public String name() {
        return internalHero.name();
    }

    @Override
    public void hitSound(float pitch) {
        updateInternalStats();
        internalHero.hitSound(pitch);
        updateStats();
    }

    @Override
    public boolean blockSound(float pitch) {
        updateInternalStats();
        boolean ret = internalHero.blockSound(pitch);
        updateStats();
        return ret;
    }

    @Override
    public String defenseVerb() {
        updateInternalStats();
        String ret = internalHero.defenseVerb();
        updateStats();
        return ret;
    }

    @Override
    public int attackSkill(Char target) {
        updateInternalStats();
        int ret = (int) (internalHero.attackSkill(target) * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public int defenseSkill(Char enemy) {
        updateInternalStats();
        int ret = (int) (internalHero.defenseSkill(enemy) * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public int drRoll() {
        updateInternalStats();
        int ret = (int) (internalHero.drRoll() * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public int damageRoll() {
        updateInternalStats();
        int ret = (int) (internalHero.damageRoll() * statsScale);
        updateStats();
        return ret;
    }

    @Override
    public float speed() {
        //tzz TODO set internal hero base speed
        updateInternalStats();
        float ret = internalHero.speed();
        updateStats();
        return ret;
    }

    @Override
    public boolean canSurpriseAttack() {
        updateInternalStats();
        boolean ret = internalHero.canSurpriseAttack();
        updateStats();
        return ret;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        updateInternalStats();
        int ret = internalHero.attackProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        updateInternalStats();
        int ret = internalHero.defenseProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public void damage(int dmg, Object src) {
        updateInternalStats();
        internalHero.damage(dmg, src);
        updateStats();
    }

    public void earnExp( int EXP, Class source ) {
        updateInternalStats();
        int exp = internalHero.lvl <= maxLvl ? EXP : 0;
        if (exp > 0) {
            sprite.showStatus(CharSprite.POSITIVE, Messages.get(source, "exp", exp));
        }
        internalHero.earnExp(exp, source);
        if (internalHero.subClass == HeroSubClass.MONK){
            Buff.affect(internalHero, MonkEnergy.class).gainEnergy(this);
        }
        updateEXP();
        updateStats();
    }

    private void updateEXP() {
        EXP = (Hero.totalExp(internalHero.lvl) + internalHero.exp) / 3;
    }

    @Override
    public float stealth() {
        updateInternalStats();
        float ret = internalHero.stealth();
        updateStats();
        return ret;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
//        internalHero.superDie(cause);
    }

    @Override
    public boolean isActive() {
        updateInternalStats();
        boolean ret = internalHero.isActive();
        updateStats();
        return ret;
    }

    @Override
    public void move(int step, boolean travelling) {
        updateInternalStats();
        internalHero.moveNoSound(step, travelling);
        updateStats();
    }

    @Override
    public void onAttackComplete() {
        updateInternalStats();
        internalHero.onAttackComplete();
        updateStats();
    }

    @Override
    public boolean isImmune(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isImmune(effect);
        updateStats();
        return ret;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (internalHero == null) return false;
        updateInternalStats();
        boolean ret = internalHero.isInvulnerable(effect);
        updateStats();
        return ret;
    }

    public boolean isStarving() {
        return internalHero.isStarving();
    }

    @Override
    protected void spend(float time) {
        updateInternalStats();
        super.spend(time);
        internalHero.superSpend(time);
        updateStats();
    }

    public boolean killedMob(Object cause) {
        return cause == this || cause == internalHero || cause instanceof Item && internalHero.belongings.contains((Item) cause);
    }

    private void updateInternalStats() {
        updateStats(this, internalHero);
        internalHero.setEnemy(enemy);
    }

    private void updateStats() {
        updateStats(internalHero, this);
        enemy = internalHero.enemy();
    }

    private void updateStats(Char src, Char dest) {
        dest.HP = src.HP;
        dest.HT = src.HT;
        dest.paralysed = src.paralysed;
        dest.rooted = src.rooted;
        dest.flying = src.flying;
        dest.invisible = src.invisible;
        dest.viewDistance = src.viewDistance;
        dest.baseSpeed = src.baseSpeed;
        dest.pos = src.pos;
    }

    public void setInternalHero(InternalHero hero) {
        internalHero = hero;
        internalHero.owner = this;
        internalHero.alignment = alignment;
        internalHero.live();
        updateStats();
        updateEXP();
    }

    private static final String INTERNAL_HERO = "internal_hero";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERNAL_HERO, internalHero);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        internalHero = (InternalHero) bundle.get(INTERNAL_HERO);
        internalHero.owner = this;
    }

    public static class InternalHero extends Hero {

        HeroMob owner;

        @Override
        public void die(Object cause) {
            owner.die(cause);
        }

        public void superDie(Object cause) {
            super.die(cause);
        }

        @Override
        public void spend(float time) {
            owner.spend(time);
        }

        public void superSpend(float time) {
            super.spend(time);
        }

        @Override
        public void next() {
            owner.next();
        }

        protected void setEnemy(Char enemy) {
            this.enemy = enemy;
        }

        @Override
        public synchronized <T extends Buff> T buff(Class<T> c) {
            if (owner == null) return super.buff(c);
            return owner.buff(c);
        }

        @Override
        public synchronized <T extends Buff> HashSet<T> buffs(Class<T> c) {
            if (owner == null) return super.buffs(c);
            return owner.buffs(c);
        }

        @Override
        public synchronized LinkedHashSet<Buff> buffs() {
            if (owner == null) return super.buffs();
            return owner.buffs();
        }

        @Override
        public boolean add(Buff buff) {
            if (super.add(buff)) {
                moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(buff, owner);
                return true;
            }
            return false;
        }
    }
}