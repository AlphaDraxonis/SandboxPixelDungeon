/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.alphadraxonis.sandboxpixeldungeon.actors.mobs;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Badges;
import com.alphadraxonis.sandboxpixeldungeon.Challenges;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.Statistics;
import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Adrenaline;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AllyBuff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Amok;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AscensionChallenge;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.ChampionEnemy;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Charm;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Corruption;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Dread;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Haste;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Hunger;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Invisibility;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.LockedFloor;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MindVision;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MonkEnergy;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Preparation;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Sleep;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.SoulMark;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Terror;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroClass;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroSubClass;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Talent;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.stateditor.DefaultStatsCache;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.effects.CellEmitter;
import com.alphadraxonis.sandboxpixeldungeon.effects.Speck;
import com.alphadraxonis.sandboxpixeldungeon.effects.Surprise;
import com.alphadraxonis.sandboxpixeldungeon.effects.Wound;
import com.alphadraxonis.sandboxpixeldungeon.effects.particles.ShadowParticle;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.MasterThievesArmband;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.RingOfWealth;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfAggression;
import com.alphadraxonis.sandboxpixeldungeon.items.stones.StoneOfAugmentation;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.SpiritBow;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.enchantments.Lucky;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.Dart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.Chasm;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Swiftthistle;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.CharSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.BossHealthBar;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public abstract class Mob extends Char {

    {
        actPriority = MOB_PRIO;

        alignment = Alignment.ENEMY;
    }

    private static final String TXT_DIED = "You hear something died in the distance";

    protected static final String TXT_NOTICE1 = "?!";
    protected static final String TXT_RAGE = "#$%^";
    protected static final String TXT_EXP = "%+dEXP";

    public AiState SLEEPING = new Sleeping();
    public AiState HUNTING = new Hunting();
    public AiState WANDERING = new Wandering();
    public AiState FLEEING = new Fleeing();
    public AiState PASSIVE = new Passive();
    public AiState state = SLEEPING;

    public Class<? extends CharSprite> spriteClass;

    protected int target = -1;

    public int defenseSkill = 0;//evasion
    public int attackSkill = 0;//accuracy
    public int damageRollMin = 0, damageRollMax = 0;
    public float statsScale = 1f;//only used in subclasses!

    public boolean isBossMob;//only real value while playing, use level.bossmobAt instead!, not meant for shattered bosses except goo!

    public int EXP = 1;
    public int maxLvl = Hero.MAX_LEVEL - 1;

    protected Char enemy;
    protected int enemyID = -1; //used for save/restore
    protected boolean enemySeen;
    protected boolean alerted = false;

    protected static final float TIME_TO_WAKE_UP = 1f;

    protected boolean firstAdded = true;

    protected void onAdd() {
        if (firstAdded) {
            //modify health for ascension challenge if applicable, only on first add
            float percent = HP / (float) HT;
            HT = Math.round(HT * AscensionChallenge.statModifier(this));
            HP = Math.round(HT * percent);
            firstAdded = false;
        }
    }

    private static final String STATE = "state";
    private static final String SEEN = "seen";
    private static final String TARGET = "target";
    private static final String MAX_LVL = "max_lvl";
    private static final String DEFENSE_SKILL = "defense_skill";
    private static final String ATTACK_SKILL = "attack_skill";
    private static final String DAMAGE_ROLL_MIN = "damage_roll_min";
    private static final String DAMAGE_ROLL_MAX = "damage_roll_max";
    private static final String XP = "xp";
    private static final String STATS_SCALE = "stats_scale";
    private static final String IS_BOSS_MOB = "is_boss_mob";

    private static final String ENEMY_ID = "enemy_id";

    @Override
    public void storeInBundle(Bundle bundle) {

        super.storeInBundle(bundle);

        if (state == SLEEPING) {
            bundle.put(STATE, Sleeping.TAG);
        } else if (state == WANDERING) {
            bundle.put(STATE, Wandering.TAG);
        } else if (state == HUNTING) {
            bundle.put(STATE, Hunting.TAG);
        } else if (state == FLEEING) {
            bundle.put(STATE, Fleeing.TAG);
        } else if (state == PASSIVE) {
            bundle.put(STATE, Passive.TAG);
        }
        bundle.put(SEEN, enemySeen);
        bundle.put(TARGET, target);
        bundle.put(MAX_LVL, maxLvl);

        Mob defaultMob = DefaultStatsCache.getDefaultObject(getClass());
        if (defaultMob != null) {
            if (defaultMob.defenseSkill != defenseSkill) bundle.put(DEFENSE_SKILL, defenseSkill);
            if (defaultMob.attackSkill != attackSkill) bundle.put(ATTACK_SKILL, attackSkill);
            if (defaultMob.damageRollMin != damageRollMin) bundle.put(DAMAGE_ROLL_MIN, damageRollMin);
            if (defaultMob.damageRollMax != damageRollMax) bundle.put(DAMAGE_ROLL_MAX, damageRollMax);
            if (defaultMob.EXP != EXP) bundle.put(XP, EXP);
            if (defaultMob.statsScale != statsScale) bundle.put(STATS_SCALE, statsScale);
        }

        bundle.put(IS_BOSS_MOB, isBossMob);

        if (enemy != null) {
            bundle.put(ENEMY_ID, enemy.id());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

        String state = bundle.getString(STATE);
        if (state.equals(Sleeping.TAG)) {
            this.state = SLEEPING;
        } else if (state.equals(Wandering.TAG)) {
            this.state = WANDERING;
        } else if (state.equals(Hunting.TAG)) {
            this.state = HUNTING;
        } else if (state.equals(Fleeing.TAG)) {
            this.state = FLEEING;
        } else if (state.equals(Passive.TAG)) {
            this.state = PASSIVE;
        }

        enemySeen = bundle.getBoolean(SEEN);

        target = bundle.getInt(TARGET);

        if (bundle.contains(MAX_LVL)) maxLvl = bundle.getInt(MAX_LVL);

        if (bundle.contains(ENEMY_ID)) {
            enemyID = bundle.getInt(ENEMY_ID);
        }
        //no need to actually save this, must be false
        firstAdded = false;

        if (bundle.contains(DEFENSE_SKILL)) defenseSkill = bundle.getInt(DEFENSE_SKILL);
        if (bundle.contains(ATTACK_SKILL)) attackSkill = bundle.getInt(ATTACK_SKILL);
        if (bundle.contains(DAMAGE_ROLL_MIN)) damageRollMin = bundle.getInt(DAMAGE_ROLL_MIN);
        if (bundle.contains(DAMAGE_ROLL_MAX)) damageRollMax = bundle.getInt(DAMAGE_ROLL_MAX);
        if (bundle.contains(XP)) EXP = bundle.getInt(XP);
        if (bundle.contains(STATS_SCALE)) statsScale = bundle.getInt(STATS_SCALE);

        if (bundle.contains(IS_BOSS_MOB)) {
            isBossMob = bundle.getBoolean(IS_BOSS_MOB);
            if (isBossMob) {
                Dungeon.level.bossMob = this;
                if (Dungeon.level.bossFound) BossHealthBar.assignBoss(this);
                if ((HP*2 <= HT)) BossHealthBar.bleed(true);
            }
        }
    }

    //mobs need to remember their targets after every actor is added
    public void restoreEnemy() {
        if (enemyID != -1 && enemy == null) enemy = (Char) Actor.findById(enemyID);
    }

    public CharSprite sprite() {
        return Reflection.newInstance(spriteClass);
    }

    @Override
    protected boolean act() {

        if (isBossMob && HP*2 > HT) {
            BossHealthBar.bleed(false);
//            ((GooSprite)sprite).spray(false);
        }

        super.act();

        boolean justAlerted = alerted;
        alerted = false;

        if (justAlerted) {
            sprite.showAlert();
        } else {
            sprite.hideAlert();
            sprite.hideLost();
        }

        if (paralysed > 0) {
            enemySeen = false;
            spend(TICK);
            return true;
        }

        if (buff(Terror.class) != null || buff(Dread.class) != null) {
            state = FLEEING;
        }

        enemy = chooseEnemy();

        boolean enemyInFOV = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;

        //prevents action, but still updates enemy seen status
        if (buff(Feint.AfterImage.FeintConfusion.class) != null) {
            enemySeen = enemyInFOV;
            spend(TICK);
            return true;
        }

        return state.act(enemyInFOV, justAlerted);
    }

    //FIXME this is sort of a band-aid correction for allies needing more intelligent behaviour
    protected boolean intelligentAlly = false;

    protected Char chooseEnemy() {

        Dread dread = buff(Dread.class);
        if (dread != null) {
            Char source = (Char) Actor.findById(dread.object);
            if (source != null) {
                return source;
            }
        }

        Terror terror = buff(Terror.class);
        if (terror != null) {
            Char source = (Char) Actor.findById(terror.object);
            if (source != null) {
                return source;
            }
        }

        //if we are an alert enemy, auto-hunt a target that is affected by aggression, even another enemy
        if (alignment == Alignment.ENEMY && state != PASSIVE && state != SLEEPING) {
            if (enemy != null && enemy.buff(StoneOfAggression.Aggression.class) != null) {
                state = HUNTING;
                return enemy;
            }
            for (Char ch : Actor.chars()) {
                if (ch != this && fieldOfView[ch.pos] &&
                        ch.buff(StoneOfAggression.Aggression.class) != null) {
                    state = HUNTING;
                    return ch;
                }
            }
        }

        //find a new enemy if..
        boolean newEnemy = false;
        //we have no enemy, or the current one is dead/missing
        if (enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
            newEnemy = true;
            //We are amoked and current enemy is the hero
        } else if (buff(Amok.class) != null && enemy == Dungeon.hero) {
            newEnemy = true;
            //We are charmed and current enemy is what charmed us
        } else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id()) {
            newEnemy = true;
        }

        //additionally, if we are an ally, find a new enemy if...
        if (!newEnemy && alignment == Alignment.ALLY) {
            //current enemy is also an ally
            if (enemy.alignment == Alignment.ALLY) {
                newEnemy = true;
                //current enemy is invulnerable
            } else if (enemy.isInvulnerable(getClass())) {
                newEnemy = true;
            }
        }

        if (newEnemy) {

            HashSet<Char> enemies = new HashSet<>();

            //if we are amoked...
            if (buff(Amok.class) != null) {
                //try to find an enemy mob to attack first.
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ENEMY && mob != this
                            && fieldOfView[mob.pos] && mob.invisible <= 0) {
                        enemies.add(mob);
                    }

                if (enemies.isEmpty()) {
                    //try to find ally mobs to attack second.
                    for (Mob mob : Dungeon.level.mobs)
                        if (mob.alignment == Alignment.ALLY && mob != this
                                && fieldOfView[mob.pos] && mob.invisible <= 0) {
                            enemies.add(mob);
                        }

                    if (enemies.isEmpty()) {
                        //try to find the hero third
                        if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
                            enemies.add(Dungeon.hero);
                        }
                    }
                }

                //if we are an ally...
            } else if (alignment == Alignment.ALLY) {
                //look for hostile mobs to attack
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ENEMY && fieldOfView[mob.pos]
                            && mob.invisible <= 0 && !mob.isInvulnerable(getClass()))
                        //intelligent allies do not target mobs which are passive, wandering, or asleep
                        if (!intelligentAlly ||
                                (mob.state != mob.SLEEPING && mob.state != mob.PASSIVE && mob.state != mob.WANDERING)) {
                            enemies.add(mob);
                        }

                //if we are an enemy...
            } else if (alignment == Alignment.ENEMY) {
                //look for ally mobs to attack
                for (Mob mob : Dungeon.level.mobs)
                    if (mob.alignment == Alignment.ALLY && fieldOfView[mob.pos] && mob.invisible <= 0)
                        enemies.add(mob);

                //and look for the hero
                if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
                    enemies.add(Dungeon.hero);
                }

            }

            //do not target anything that's charming us
            Charm charm = buff(Charm.class);
            if (charm != null) {
                Char source = (Char) Actor.findById(charm.object);
                if (source != null && enemies.contains(source) && enemies.size() > 1) {
                    enemies.remove(source);
                }
            }

            //neutral characters in particular do not choose enemies.
            if (enemies.isEmpty()) {
                return null;
            } else {
                //go after the closest potential enemy, preferring enemies that can be reached/attacked, and the hero if two are equidistant
                PathFinder.buildDistanceMap(pos, Dungeon.findPassable(this, Dungeon.level.passable, fieldOfView, true));

                Char closest = null;
                for (Char curr : enemies){
                    if (closest == null){
                        closest = curr;
                    } else if (canAttack(closest) && !canAttack(curr)){
                        continue;
                    } else if ((canAttack(curr) && !canAttack(closest))
                            || (PathFinder.distance[curr.pos] < PathFinder.distance[closest.pos])){
                        closest = curr;
                    } else if ( curr == Dungeon.hero &&
                            (PathFinder.distance[curr.pos] == PathFinder.distance[closest.pos]) || (canAttack(curr) && canAttack(closest))){
                        closest = curr;
                    }
                }
                //if we were going to target the hero, but an afterimage exists, target that instead
                if (closest == Dungeon.hero) {
                    for (Char ch : enemies) {
                        if (ch instanceof Feint.AfterImage) {
                            closest = ch;
                            break;
                        }
                    }
                }

                return closest;
            }

        } else
            return enemy;
    }

    @Override
    public boolean add(Buff buff) {
        if (super.add(buff)) {
            if (buff instanceof Amok || buff instanceof AllyBuff) {
                state = HUNTING;
            } else if (buff instanceof Terror || buff instanceof Dread) {
                state = FLEEING;
            } else if (buff instanceof Sleep) {
                state = SLEEPING;
                postpone(Sleep.SWS);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Buff buff) {
        if (super.remove(buff)) {
            if ((buff instanceof Terror && buff(Dread.class) == null)
                    || (buff instanceof Dread && buff(Terror.class) == null)) {
                if (enemySeen) {
                    sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "rage"));
                    state = HUNTING;
                } else {
                    state = WANDERING;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean canAttack(Char enemy) {
        if (Dungeon.level.adjacent(pos, enemy.pos)) {
            return true;
        }
        for (ChampionEnemy buff : buffs(ChampionEnemy.class)) {
            if (buff.canAttackWithExtraReach(enemy)) {
                return true;
            }
        }
        return false;
    }

    private boolean cellIsPathable(int cell) {
        if (!Dungeon.level.passable[cell]) {
            if (flying || buff(Amok.class) != null) {
                if (!Dungeon.level.avoid[cell]) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) {
            return false;
        }
        if (Actor.findChar(cell) != null) {
            return false;
        }

        return true;
    }

    protected boolean getCloser(int target) {

        if (rooted || target == pos) {
            return false;
        }

        int step = -1;

        if (Dungeon.level.adjacent(pos, target)) {

            path = null;

            if (cellIsPathable(target)) {
                step = target;
            }

        } else {

            boolean newPath = false;
            //scrap the current path if it's empty, no longer connects to the current location
            //or if it's extremely inefficient and checking again may result in a much better path
            if (path == null || path.isEmpty()
                    || !Dungeon.level.adjacent(pos, path.getFirst())
                    || path.size() > 2 * Dungeon.level.distance(pos, target))
                newPath = true;
            else if (path.getLast() != target) {
                //if the new target is adjacent to the end of the path, adjust for that
                //rather than scrapping the whole path.
                if (Dungeon.level.adjacent(target, path.getLast())) {
                    int last = path.removeLast();

                    if (path.isEmpty()) {

                        //shorten for a closer one
                        if (Dungeon.level.adjacent(target, pos)) {
                            path.add(target);
                            //extend the path for a further target
                        } else {
                            path.add(last);
                            path.add(target);
                        }

                    } else {
                        //if the new target is simply 1 earlier in the path shorten the path
                        if (path.getLast() == target) {

                            //if the new target is closer/same, need to modify end of path
                        } else if (Dungeon.level.adjacent(target, path.getLast())) {
                            path.add(target);

                            //if the new target is further away, need to extend the path
                        } else {
                            path.add(last);
                            path.add(target);
                        }
                    }

                } else {
                    newPath = true;
                }

            }

            //checks if the next cell along the current path can be stepped into
            if (!newPath) {
                int nextCell = path.removeFirst();
                if (!cellIsPathable(nextCell)) {

                    newPath = true;
                    //If the next cell on the path can't be moved into, see if there is another cell that could replace it
                    if (!path.isEmpty()) {
                        for (int i : PathFinder.NEIGHBOURS8) {
                            if (Dungeon.level.adjacent(pos, nextCell + i) && Dungeon.level.adjacent(nextCell + i, path.getFirst())) {
                                if (cellIsPathable(nextCell + i)) {
                                    path.addFirst(nextCell + i);
                                    newPath = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    path.addFirst(nextCell);
                }
            }

            //generate a new path
            if (newPath) {
                //If we aren't hunting, always take a full path
                PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, true);
                if (state != HUNTING) {
                    path = full;
                } else {
                    //otherwise, check if other characters are forcing us to take a very slow route
                    // and don't try to go around them yet in response, basically assume their blockage is temporary
                    PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.passable, fieldOfView, false);
                    if (ignoreChars != null && (full == null || full.size() > 2 * ignoreChars.size())) {
                        //check if first cell of shorter path is valid. If it is, use new shorter path. Otherwise do nothing and wait.
                        path = ignoreChars;
                        if (!cellIsPathable(ignoreChars.getFirst())) {
                            return false;
                        }
                    } else {
                        path = full;
                    }
                }
            }

            if (path != null) {
                step = path.removeFirst();
            } else {
                return false;
            }
        }
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    protected boolean getFurther(int target) {
        if (rooted || target == pos) {
            return false;
        }

        int step = Dungeon.flee(this, target, Dungeon.level.passable, fieldOfView, true);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (Dungeon.hero != null
                && (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
                || Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null))
            sprite.add(CharSprite.State.PARALYSED);
    }

    public float attackDelay() {
        float delay = 1f;
        if (buff(Adrenaline.class) != null) delay /= 1.5f;
        return delay;
    }

    protected boolean doAttack(Char enemy) {

        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.attack(enemy.pos);
            return false;

        } else {
            attack(enemy);
            Invisibility.dispel(this);
            spend(attackDelay());
            return true;
        }
    }

    @Override
    public void onAttackComplete() {
        attack(enemy);
        Invisibility.dispel(this);
        spend(attackDelay());
        super.onAttackComplete();
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (!surprisedBy(enemy)
                && paralysed == 0
                && !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
            return this.defenseSkill;
        } else {
            return 0;
        }
    }

    @Override
    public int attackSkill(Char target) {
        return attackSkill;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(damageRollMin, damageRollMax);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        if (enemy instanceof Hero
                && ((Hero) enemy).belongings.attackingWeapon() instanceof MissileWeapon) {
            Statistics.thrownAttacks++;
            Badges.validateHuntressUnlock();
        }

        if (surprisedBy(enemy)) {
            Statistics.sneakAttacks++;
            Badges.validateRogueUnlock();
            //TODO this is somewhat messy, it would be nicer to not have to manually handle delays here
            // playing the strong hit sound might work best as another property of weapon?
            if (Dungeon.hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow
                    || Dungeon.hero.belongings.attackingWeapon() instanceof Dart) {
                Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_STRONG, 0.125f);
            } else {
                Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
            }
            if (enemy.buff(Preparation.class) != null) {
                Wound.hit(this);
            } else {
                Surprise.hit(this);
            }
        }

        //if attacked by something else than current target, and that thing is closer, switch targets
        if (this.enemy == null
                || (enemy != this.enemy && (Dungeon.level.distance(pos, enemy.pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
            aggro(enemy);
            target = enemy.pos;
        }

        if (buff(SoulMark.class) != null) {
            int restoration = Math.min(damage, HP + shielding());

            //physical damage that doesn't come from the hero is less effective
            if (enemy != Dungeon.hero) {
                restoration = Math.round(restoration * 0.4f * Dungeon.hero.pointsInTalent(Talent.SOUL_SIPHON) / 3f);
            }
            if (restoration > 0) {
                Buff.affect(Dungeon.hero, Hunger.class).affectHunger(restoration * Dungeon.hero.pointsInTalent(Talent.SOUL_EATER) / 3f);
                Dungeon.hero.HP = (int) Math.ceil(Math.min(Dungeon.hero.HT, Dungeon.hero.HP + (restoration * 0.4f)));
                Dungeon.hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
            }
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public float speed() {
        return super.speed() * AscensionChallenge.enemySpeedModifier(this);
    }

    public final boolean surprisedBy(Char enemy) {
        return surprisedBy(enemy, true);
    }

    public boolean surprisedBy(Char enemy, boolean attacking) {
        return enemy == Dungeon.hero
                && (enemy.invisible > 0 || !enemySeen || (fieldOfView != null && fieldOfView.length == Dungeon.level.length() && !fieldOfView[enemy.pos]))
                && (!attacking || enemy.canSurpriseAttack());
    }

    public void aggro(Char ch) {
        enemy = ch;
        if (state != PASSIVE) {
            state = HUNTING;
        }
    }

    public void clearEnemy() {
        enemy = null;
        enemySeen = false;
        if (state == HUNTING) state = WANDERING;
    }

    public boolean isTargeting(Char ch) {
        return enemy == ch;
    }

    @Override
    public void damage(int dmg, Object src) {

        boolean bleeding;
        if (isBossMob && !BossHealthBar.isAssigned()){
            BossHealthBar.assignBoss( this );
            Dungeon.level.seal();
            bleeding = (HP*2 <= HT);
        } else bleeding = false;

        if (state == SLEEPING) {
            state = WANDERING;
        }
        if (state != HUNTING && !(src instanceof Corruption)) {
            alerted = true;
        }

        super.damage(dmg, src);

        if (isBossMob) {
            if ((HP * 2 <= HT) && !bleeding) {
                BossHealthBar.bleed(true);
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


    @Override
    public void destroy() {

        super.destroy();

        Dungeon.level.mobs.remove(this);

        if (CustomDungeon.isEditing()) return;

        if (Dungeon.hero.buff(MindVision.class) != null) {
            Dungeon.observe();
            GameScene.updateFog(pos, 2);
        }

        if (Dungeon.hero.isAlive()) {

            if (alignment == Alignment.ENEMY) {
                Statistics.enemiesSlain++;
                Badges.validateMonstersSlain();
                Statistics.qualifiedForNoKilling = false;

                AscensionChallenge.processEnemyKill(this);

                int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;
                //during ascent, under-levelled enemies grant 10 xp each until level 30
                // after this enemy kills which reduce the amulet curse still grant 10 effective xp
                // for the purposes of on-exp effects, see AscensionChallenge.processEnemyKill
                if (Dungeon.hero.buff(AscensionChallenge.class) != null &&
                        exp == 0 && maxLvl > 0 && EXP > 0 && Dungeon.hero.lvl < Hero.MAX_LEVEL) {
                    exp = Math.round(10 * spawningWeight());
                }
                if (exp > 0) {
                    Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "exp", exp));
                }
                Dungeon.hero.earnExp(exp, getClass());

                if (Dungeon.hero.subClass == HeroSubClass.MONK) {
                    Buff.affect(Dungeon.hero, MonkEnergy.class).gainEnergy(this);
                }
            }
        }
    }

    @Override
    public void die(Object cause) {

        if (cause == Chasm.class) {
            //50% chance to round up, 50% to round down
            if (EXP % 2 == 1) EXP += Random.Int(2);
            EXP /= 2;
        }

        if (alignment == Alignment.ENEMY) {
            rollToDropLoot();

            if (cause == Dungeon.hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment) {
                if (Dungeon.hero.hasTalent(Talent.LETHAL_MOMENTUM)
                        && Random.Float() < 0.34f + 0.33f * Dungeon.hero.pointsInTalent(Talent.LETHAL_MOMENTUM)) {
                    Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 0f);
                }
                if (Dungeon.hero.heroClass != HeroClass.DUELIST
                        && Dungeon.hero.hasTalent(Talent.LETHAL_HASTE)
                        && Dungeon.hero.buff(Talent.LethalHasteCooldown.class) == null) {
                    Buff.affect(Dungeon.hero, Talent.LethalHasteCooldown.class, 100f);
                    Buff.affect(Dungeon.hero, Haste.class, 1.67f + Dungeon.hero.pointsInTalent(Talent.LETHAL_HASTE));
                }
            }

        }

        if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
            GLog.i(Messages.get(this, "died"));
        }

        boolean soulMarked = buff(SoulMark.class) != null;

        super.die(cause);

        if (!(this instanceof Wraith)
                && soulMarked
                && Random.Float() < (0.4f * Dungeon.hero.pointsInTalent(Talent.NECROMANCERS_MINIONS) / 3f)) {
            Wraith w = Wraith.spawnAt(pos, false);
            if (w != null) {
                Buff.affect(w, Corruption.class);
                if (Dungeon.level.heroFOV[pos]) {
                    CellEmitter.get(pos).burst(ShadowParticle.CURSE, 6);
                    Sample.INSTANCE.play(Assets.Sounds.CURSED);
                }
            }
        }

        if (isBossMob) {
            Dungeon.level.unseal();
            GameScene.bossSlain();
        }
    }

    public float lootChance() {
        float lootChance = this.lootChance;

        float dropBonus = RingOfWealth.dropChanceMultiplier(Dungeon.hero);

        Talent.BountyHunterTracker bhTracker = Dungeon.hero.buff(Talent.BountyHunterTracker.class);
        if (bhTracker != null) {
            Preparation prep = Dungeon.hero.buff(Preparation.class);
            if (prep != null) {
                // 2/4/8/16% per prep level, multiplied by talent points
                float bhBonus = 0.02f * (float) Math.pow(2, prep.attackLevel() - 1);
                bhBonus *= Dungeon.hero.pointsInTalent(Talent.BOUNTY_HUNTER);
                dropBonus += bhBonus;
            }
        }

        return lootChance * dropBonus;
    }

    public void rollToDropLoot() {
        if (Dungeon.hero.lvl > maxLvl + 2) return;

        MasterThievesArmband.StolenTracker stolen = buff(MasterThievesArmband.StolenTracker.class);
        if (stolen == null || !stolen.itemWasStolen()) {
            if (Random.Float() < lootChance()) {
                Item loot = createLoot();
                if (loot != null) {
                    Dungeon.level.drop(loot, pos).sprite.drop();
                }
            }
        }

        //ring of wealth logic
        if (Ring.getBuffedBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
            int rolls = 1;
            if (properties.contains(Property.BOSS)) rolls = 15;
            else if (properties.contains(Property.MINIBOSS)) rolls = 5;
            ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
            if (bonus != null && !bonus.isEmpty()) {
                for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
                RingOfWealth.showFlareForBonusDrop(sprite);
            }
        }

        //lucky enchant logic
        if (buff(Lucky.LuckProc.class) != null) {
            Dungeon.level.drop(buff(Lucky.LuckProc.class).genLoot(), pos).sprite.drop();
            Lucky.showFlare(sprite);
        }

        //soul eater talent
        if (buff(SoulMark.class) != null &&
                Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)) {
            Talent.onFoodEaten(Dungeon.hero, 0, null);
        }

    }

    protected Object loot = null;
    protected float lootChance = 0;

    @SuppressWarnings("unchecked")
    public Item createLoot() {
        Item item;
        if (loot instanceof Generator.Category) {

            item = Generator.randomUsingDefaults((Generator.Category) loot);

        } else if (loot instanceof Class<?>) {

            item = Generator.random((Class<? extends Item>) loot);

        } else {

            item = (Item) loot;

        }
        return item;
    }

    //how many mobs this one should count as when determining spawning totals
    public float spawningWeight() {
        return 1;
    }

    public boolean reset() {
        return false;
    }

    public void beckon(int cell) {

        notice();

        if (state != HUNTING && state != FLEEING) {
            state = WANDERING;
        }
        target = cell;
    }

    public String description() {
        return Messages.get(this, "desc");
    }

    public String info() {
        String desc = description();

        for (Buff b : buffs(ChampionEnemy.class)) {
            desc += "\n\n_" + Messages.titleCase(b.name()) + "_\n" + b.desc();
        }

        Mob defaultStats = DefaultStatsCache.getDefaultObject(getClass());
        if (defaultStats != null) {

            if (DefaultStatsCache.useStatsScale(this)) {
                if (defaultStats.baseSpeed != baseSpeed || defaultStats.statsScale != statsScale
                        || this instanceof Brute && (
                        defaultStats.HT != HT || defaultStats.damageReductionMax != damageReductionMax
                                || defaultStats.attackSkill != attackSkill || defaultStats.defenseSkill != defenseSkill
                                || defaultStats.EXP != EXP
                )){
                    desc += "\n\n" + Messages.get(Mob.class, "base_stats_changed");
                    if (defaultStats.statsScale != statsScale)
                        desc += "\n" + Messages.get(Mob.class, "stats_scale") + ": " + defaultStats.statsScale + " -> _" + statsScale + "_";
                    if (defaultStats.baseSpeed != baseSpeed)
                        desc += "\n" + Messages.get(StoneOfAugmentation.WndAugment.class, "speed") + ": " + defaultStats.baseSpeed + " -> _" + baseSpeed + "_";
                    if (this instanceof Brute) {
                        desc += infoStatsChangedHPAccuracyEvasionArmor(defaultStats);
                    }

                    if (defaultStats.EXP != EXP)
                        desc += "\n" + Messages.get(Mob.class, "xp") + ": " + defaultStats.EXP + " -> _" + EXP + "_";
                }
            } else {

                if (defaultStats.HT != HT || defaultStats.baseSpeed != baseSpeed
                        || defaultStats.attackSkill != attackSkill || defaultStats.defenseSkill != defenseSkill
                        || defaultStats.damageRollMin != damageRollMin || defaultStats.damageRollMax != damageRollMax
                        || defaultStats.damageReductionMax != damageReductionMax || defaultStats.EXP != EXP) {
                    desc += "\n\n" + Messages.get(Mob.class, "base_stats_changed");

                    if (defaultStats.baseSpeed != baseSpeed)
                        desc += "\n" + Messages.get(StoneOfAugmentation.WndAugment.class, "speed") + ": " + defaultStats.baseSpeed + " -> _" + baseSpeed + "_";
                    desc += infoStatsChangedHPAccuracyEvasionArmor(defaultStats);
                    if (defaultStats.damageRollMin != damageRollMin)
                        desc += "\n" + Messages.get(Mob.class, "dmg_min") + ": " + defaultStats.damageRollMin + " -> _" + damageRollMin + "_";
                    if (defaultStats.damageRollMax != damageRollMax)
                        desc += "\n" + Messages.get(Mob.class, "dmg_max") + ": " + defaultStats.damageRollMax + " -> _" + damageRollMax + "_";

                    if (defaultStats.EXP != EXP)
                        desc += "\n" + Messages.get(Mob.class, "xp") + ": " + defaultStats.EXP + " -> _" + EXP + "_";
                }

            }
        }

        return desc;
    }

    private String infoStatsChangedHPAccuracyEvasionArmor(Mob defaultStats) {
        String ret = "";
        if (defaultStats.HT != HT)
            ret += "\n\n" + Messages.get(Mob.class, "hp") + ": " + defaultStats.HT + " -> _" + HT + "_";
        if (defaultStats.attackSkill != attackSkill)
            ret += "\n" + Messages.get(Mob.class, "accuracy") + ": " + defaultStats.attackSkill + " -> _" + attackSkill + "_";
        if (defaultStats.defenseSkill != defenseSkill)
            ret += "\n" + Messages.get(StoneOfAugmentation.WndAugment.class, "evasion") + ": " + defaultStats.defenseSkill + " -> _" + defenseSkill + "_";
        if (defaultStats.damageReductionMax != damageReductionMax)
            ret += "\n" + Messages.get(Mob.class, "armor") + ": " + defaultStats.damageReductionMax + " -> _" + damageReductionMax + "_";
        return ret;
    }

    public void addBossProperty() {
        if (isBossMob) properties.add(Property.BOSS);
    }

    public void notice() {
        sprite.showAlert();
        if (isBossMob) {
            if (!BossHealthBar.isAssigned()) {
                BossHealthBar.assignBoss(this);
                Dungeon.level.seal();
//                yell(Messages.get(this, "notice"));
//                for (Char ch : Actor.chars()) {
//                    if (ch instanceof DriedRose.GhostHero) {
//                        ((DriedRose.GhostHero) ch).sayBoss();
//                    }
//                }
            }
        }
    }

    public void yell(String str) {
        GLog.newLine();
        GLog.n("%s: \"%s\" ", Messages.titleCase(name()), str);
    }

    public interface AiState {
        boolean act(boolean enemyInFOV, boolean justAlerted);
    }

    protected class Sleeping implements AiState {

        public static final String TAG = "SLEEPING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {

            //debuffs cause mobs to wake as well
            for (Buff b : buffs()) {
                if (b.type == Buff.buffType.NEGATIVE) {
                    awaken(enemyInFOV);
                    return true;
                }
            }

            if (enemyInFOV) {

                float enemyStealth = enemy.stealth();

                if (enemy instanceof Hero && ((Hero) enemy).hasTalent(Talent.SILENT_STEPS)) {
                    if (Dungeon.level.distance(pos, enemy.pos) >= 4 - ((Hero) enemy).pointsInTalent(Talent.SILENT_STEPS)) {
                        enemyStealth = Float.POSITIVE_INFINITY;
                    }
                }

                if (Random.Float(distance(enemy) + enemyStealth) < 1) {
                    awaken(enemyInFOV);
                    return true;
                }

            }

            enemySeen = false;
            spend(TICK);

            return true;
        }

        protected void awaken(boolean enemyInFOV) {
            if (enemyInFOV) {
                enemySeen = true;
                notice();
                state = HUNTING;
                target = enemy.pos;
            } else {
                notice();
                state = WANDERING;
                target = Dungeon.level.randomDestination(Mob.this);
            }

            if (alignment == Alignment.ENEMY && Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.paralysed <= 0
                            && Dungeon.level.distance(pos, mob.pos) <= 8
                            && mob.state != mob.HUNTING) {
                        mob.beckon(target);
                    }
                }
            }
            spend(TIME_TO_WAKE_UP);
        }
    }

    protected class Wandering implements AiState {

        public static final String TAG = "WANDERING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && (justAlerted || Random.Float(distance(enemy) / 2f + enemy.stealth()) < 1)) {

                return noticeEnemy();

            } else {

                return continueWandering();

            }
        }

        protected boolean noticeEnemy() {
            enemySeen = true;

            notice();
            alerted = true;
            state = HUNTING;
            target = enemy.pos;

            if (alignment == Alignment.ENEMY && Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.paralysed <= 0
                            && Dungeon.level.distance(pos, mob.pos) <= 8
                            && mob.state != mob.HUNTING) {
                        mob.beckon(target);
                    }
                }
            }

            return true;
        }

        protected boolean continueWandering() {
            enemySeen = false;

            int oldPos = pos;
            if (target != -1 && getCloser(target)) {
                spend(1 / speed());
                return moveSprite(oldPos, pos);
            } else {
                target = Dungeon.level.randomDestination(Mob.this);
                spend(TICK);
            }

            return true;
        }

    }

    protected class Hunting implements AiState {

        public static final String TAG = "HUNTING";

        //prevents rare infinite loop cases
        private boolean recursing = false;

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                target = enemy.pos;
                return doAttack(enemy);

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else if (enemy == null) {
                    sprite.showLost();
                    state = WANDERING;
                    target = Dungeon.level.randomDestination(Mob.this);
                    spend(TICK);
                    return true;
                }

                int oldPos = pos;
                if (target != -1 && getCloser(target)) {

                    spend(1 / speed());
                    return moveSprite(oldPos, pos);

                } else {

                    //if moving towards an enemy isn't possible, try to switch targets to another enemy that is closer
                    //unless we have already done that and still can't move toward them, then move on.
                    if (!recursing) {
                        Char oldEnemy = enemy;
                        enemy = null;
                        enemy = chooseEnemy();
                        if (enemy != null && enemy != oldEnemy) {
                            recursing = true;
                            boolean result = act(enemyInFOV, justAlerted);
                            recursing = false;
                            return result;
                        }
                    }

                    spend(TICK);
                    if (!enemyInFOV) {
                        sprite.showLost();
                        state = WANDERING;
                        target = Dungeon.level.randomDestination(Mob.this);
                    }
                    return true;
                }
            }
        }
    }

    //FIXME this works fairly well but is coded poorly. Should refactor
    protected class Fleeing implements AiState {

        public static final String TAG = "FLEEING";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            //loses target when 0-dist rolls a 6 or greater.
            if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6) {
                target = -1;

                //if enemy isn't in FOV, keep running from their previous position.
            } else if (enemyInFOV) {
                target = enemy.pos;
            }

            int oldPos = pos;
            if (target != -1 && getFurther(target)) {

                spend(1 / speed());
                return moveSprite(oldPos, pos);

            } else {

                spend(TICK);
                nowhereToRun();

                return true;
            }
        }

        protected void nowhereToRun() {
        }
    }

    protected class Passive implements AiState {

        public static final String TAG = "PASSIVE";

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            spend(TICK);
            return true;
        }
    }


    private static ArrayList<Mob> heldAllies = new ArrayList<>();

    public static void holdAllies(Level level) {
        holdAllies(level, Dungeon.hero.pos);
    }

    public static void holdAllies(Level level, int holdFromPos) {
        heldAllies.clear();
        for (Mob mob : level.mobs.toArray(new Mob[0])) {
            //preserve directable allies no matter where they are
            if (mob instanceof DirectableAlly) {
                ((DirectableAlly) mob).clearDefensingPos();
                level.mobs.remove(mob);
                heldAllies.add(mob);

                //preserve intelligent allies if they are near the hero
            } else if (mob.alignment == Alignment.ALLY
                    && mob.intelligentAlly
                    && Dungeon.level.distance(holdFromPos, mob.pos) <= 5) {
                level.mobs.remove(mob);
                heldAllies.add(mob);
            }
        }
    }

    public static void restoreAllies(Level level, int pos) {
        restoreAllies(level, pos, -1);
    }

    public static void restoreAllies(Level level, int pos, int gravitatePos) {
        if (!heldAllies.isEmpty()) {

            ArrayList<Integer> candidatePositions = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.solid[i + pos] && level.findMob(i + pos) == null) {
                    candidatePositions.add(i + pos);
                }
            }

            //gravitate pos sets a preferred location for allies to be closer to
            if (gravitatePos == -1) {
                Collections.shuffle(candidatePositions);
            } else {
                Collections.sort(candidatePositions, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer t1, Integer t2) {
                        return Dungeon.level.distance(gravitatePos, t1) -
                                Dungeon.level.distance(gravitatePos, t2);
                    }
                });
            }

            for (Mob ally : heldAllies) {
                level.mobs.add(ally);
                ally.state = ally.WANDERING;

                if (!candidatePositions.isEmpty()) {
                    ally.pos = candidatePositions.remove(0);
                } else {
                    ally.pos = pos;
                }
                if (ally.sprite != null) ally.sprite.place(ally.pos);

                if (ally.fieldOfView == null || ally.fieldOfView.length != level.length()) {
                    ally.fieldOfView = new boolean[level.length()];
                }
                Dungeon.level.updateFieldOfView(ally, ally.fieldOfView);

            }
        }
        heldAllies.clear();
    }

    public static void clearHeldAllies() {
        heldAllies.clear();
    }
}