package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.utils.Bundle;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class HeroMob extends Mob implements ItemSelectables.WeaponSelectable, ItemSelectables.ArmorSelectable, MobBasedOnDepth {

    {
        spriteClass = null;

        maxLvl = 29;
    }

    private InternalHero internalHero;

    public HeroMob() {
        setInternalHero(new InternalHero());
    }

    @Override
    public void setLevel(int depth) {
        internalHero.baseSpeed = baseSpeed;
        if (internalHero.belongings.weapon != null)
            internalHero.belongings.weapon.activate(internalHero);
        if (internalHero.belongings.armor != null)
            internalHero.belongings.armor.activate(internalHero);
        if (internalHero.belongings.ring != null)
            internalHero.belongings.ring.activate(internalHero);
        if (internalHero.belongings.artifact != null)
            internalHero.belongings.artifact.activate(internalHero);
        if (internalHero.belongings.misc != null)
            internalHero.belongings.misc.activate(internalHero);

        if (!hpSet) {
            HP = HT = internalHero.HP = internalHero.HT = (int) (internalHero.HT * statsScale);
            hpSet = !CustomDungeon.isEditing();
        }

        if (buff(Regeneration.class) == null) {
            Buff.affect(this, Regeneration.class);
            Buff.affect(this, Hunger.class);
        }
        updateEXP();
    }

    @Override
    public void initRandoms() {
        super.initRandoms();
        Belongings belongings = hero().belongings;
        belongings.weapon = RandomItem.initRandomStatsForItemSubclasses(belongings.weapon);
        belongings.armor = RandomItem.initRandomStatsForItemSubclasses(belongings.armor);
        belongings.ring = RandomItem.initRandomStatsForItemSubclasses(belongings.ring);
        belongings.artifact = RandomItem.initRandomStatsForItemSubclasses(belongings.artifact);
        belongings.misc = RandomItem.initRandomStatsForItemSubclasses(belongings.misc);
    }

    @Override
    public CharSprite sprite() {
        HeroSprite.HeroMobSprite sprite = new HeroSprite.HeroMobSprite(internalHero) {
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
        return customName == null ? internalHero.name() : super.name();
    }

    @Override
    public String description() {
        return customDesc == null ? internalHero.heroClass.shortDesc() : super.description();
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
    public int defenseSkill(Char enemy) {
        int defenseSkill = super.defenseSkill(enemy);
        if (defenseSkill == 0 || defenseSkill == INFINITE_EVASION) return defenseSkill;//normal defense skill is always at least 5 (Hero.STARTING_DEF_SKILL)
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
    public float speed() {
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
    protected boolean canAttack( Char enemy ) {
        return super.canAttack(enemy)
                || internalHero.belongings.weapon() instanceof SpiritBow
                    && new Ballistica( pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent( pos, enemy.pos ) || !(internalHero.belongings.weapon() instanceof SpiritBow)
                || new Ballistica( pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos != enemy.pos) {

            return super.doAttack( enemy );

        } else {

            shootMissile();

            if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
                return false;
            } else {
                return true;
            }
        }
    }

    protected void shootMissile() {

        if (!(internalHero.belongings.weapon() instanceof SpiritBow)) {
            return;
        }

        Invisibility.dispel(this);

        SpiritBow bow = (SpiritBow) internalHero.belongings.weapon();

        bow.knockArrow().cast(internalHero, target);
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
        if (surprisedBy(enemy)) Surprise.hit(this);
        updateInternalStats();
        int ret = internalHero.defenseProc(enemy, damage);
        updateStats();
        return ret;
    }

    @Override
    public void damage(int dmg, Object src) {

        //from super.damage()
        boolean bleedingCheck;
        if (isBossMob && !BossHealthBar.isAssigned(this)){
            BossHealthBar.addBoss( this );
            Dungeon.level.seal();
            bleedingCheck = (HP*2 <= HT);
        } else bleedingCheck = false;

        if (!isInvulnerable(src.getClass())) {
            if (state == SLEEPING) {
                state = WANDERING;
            }
            if (state != HUNTING && !(src instanceof Corruption)) {
                alerted = true;
            }
        }

        updateInternalStats();
        internalHero.damage(dmg, src);
        updateStats();

        if (isBossMob) {
            if ((HP * 2 <= HT) && !bleedingCheck) {
                bleeding = true;
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
//                ((GooSprite) sprite).spray(true);
            }
            LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
            if (lock != null) {
                if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) lock.addTime(dmg);
                else lock.addTime(dmg * 1.5f);
            }
        }
    }

    public void earnExp( int EXP, Class source ) {
        updateInternalStats();
        int exp = internalHero.lvl <= maxLvl ? EXP : 0;
        if (exp > 0 && sprite != null) {
            sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
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
        internalHero.resting = state == SLEEPING;
    }

    private void updateStats() {
        updateStats(internalHero, this);
        enemy = internalHero.enemy();
        if (internalHero.resting) state = SLEEPING;
    }

    private void updateStats(Char src, Char dest) {
        dest.HP = src.HP;
        dest.HT = src.HT;
        dest.paralysed = src.paralysed;
        dest.rooted = src.rooted;
        dest.setFlying(src.isFlying());
        dest.invisible = src.invisible;
        dest.viewDistance = src.viewDistance;
        dest.baseSpeed = src.baseSpeed;
        dest.pos = src.pos;
    }

    public void setInternalHero(InternalHero hero) {
        internalHero = hero;
        internalHero.alignment = alignment;
        internalHero.setOwner(this);
        updateStats();
        updateEXP();
    }

    public InternalHero hero() {
        return internalHero;
    }

    @Override
    public Weapon weapon() {
        return (Weapon) internalHero.belongings.weapon;
    }

    @Override
    public void weapon(Weapon weapon) {
        internalHero.belongings.weapon = weapon;
    }

    @Override
    public Armor armor() {
        return internalHero.belongings.armor;
    }

    @Override
    public void armor(Armor armor) {
        internalHero.belongings.armor = armor;
    }

    public void setHeroLvl(int lvl) {
        internalHero.setLvl(lvl);
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
        internalHero = null;
        super.restoreFromBundle(bundle);
        internalHero = (InternalHero) bundle.get(INTERNAL_HERO);
        internalHero.owner = this;
    }

    public static class InternalHero extends Hero {

        private HeroMob owner;

        public void setOwner(HeroMob owner) {
            this.owner = owner;
            for (Buff b : super.buffs()) {
                moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(b, owner);
            }
        }

        @Override
        public int attackSkill(Char target) {
            return (int) (super.attackSkill(target) * owner.statsScale);
        }

        @Override
        public int damageRoll() {
            return (int) (super.damageRoll() * owner.statsScale);
        }

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
                if (owner != null) moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(buff, owner);
                return true;
            }
            return false;
        }
    }
}