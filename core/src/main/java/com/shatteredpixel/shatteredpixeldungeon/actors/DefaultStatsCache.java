package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobBasedOnDepth;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.LuaCustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Reflection;

import java.util.HashMap;
import java.util.Map;

public class DefaultStatsCache {

    //TODO beschreibung anpassen
    //Weapon: ACC, DLY, RCH   alle Weapons
    //DMG: min ist lvl+tier,  max ist baseBax+LevelScaling, max ist base5 und scaling1  <-can be changed  nur MeleeWeapon  und siehe MeleeWeapon
    //Food: energy (=Food value)
    //Holiday pasty?
    //Ring: Effect multilpier (Achtung Ring of Force)  sage auch: all rings (except force) scale exponationally, level is exponent, eggectMultip is basis

    //ACHTUNG nur laden wenn gespeichert!!!!!!

    //Mobs exclude Bee, allBrute, Goo, GreatCrab, allMimics, allPiranha, Pylon, allStatue, allWraiths, YogDzewa, YogFist, allNPC
    //TODO maybe add attackSpeed (attackDelay)
    //HT (max HP)
    //baseSpeed
    //view distance (exclude Hero)
    //EXP (xp on death)
    //maxLevel (hero level until they drop xp incl)
    //loot chance (only if loot != null)
    //loot (braucht noch generator und classes)


    //defenseSkill (evasion)   drRoll max  (0 - set)
    //attackSkill  (accuracy)  constant
    //damage       (damageRoll)min UND max


    //properties?

    private static Map<Class<? extends Bundlable>, Bundlable> cache = new HashMap<>();

    public static <T extends Bundlable> T getDefaultObject(Class<T> clazz) {
        T ret = (T) cache.get(clazz);
        if (ret == null) {
            if (LuaCustomObjectClass.class.isAssignableFrom(clazz)) {
                return getDefaultObject((Class<T>) clazz.getSuperclass());
            }

            //Achtung Brute kann manche stats setzten, auch speed ändern!
            if (Mob.class.isAssignableFrom(clazz) &&
                    //if you change this, also check out Dungeon.java line 1078 (findPassable())
                    (NPC.class.isAssignableFrom(clazz) && !SentryRoom.Sentry.class.isAssignableFrom(clazz) && !Ghost.class.isAssignableFrom(clazz)
                            || Pylon.class.isAssignableFrom(clazz)))
                return null;

            ret = Reflection.newInstance(clazz);
            if (!(ret instanceof MobBasedOnDepth)) cache.put(clazz, ret);
            else if (!CustomDungeon.isEditing()) ((MobBasedOnDepth) ret).setLevel(Dungeon.depth);
        }
        return ret;
    }

    public static boolean useStatsScale(Mob mob){
        return mob instanceof Mimic
                || mob instanceof Piranha
                || mob instanceof Statue
                || mob instanceof Bee
                || mob instanceof Wraith
                || mob instanceof SentryRoom.Sentry
                || mob instanceof HeroMob;
    }

    public static boolean areStatsEqual(Mob a, Mob b){
        return a.areStatsEqual(b);
    }

    public static boolean canModifyStats(Object obj) {
        return obj instanceof Weapon || obj instanceof Ring || obj instanceof Food;
    }


//    final public boolean attack( Char enemy ){
//        return attack(enemy, 1f, 0f, 1f);
//    }
//
//    //applies damage multipliers, returns true if hit was successful, dmg based on damageRoll()
//    public boolean attack( Char enemy, float dmgMulti, float dmgBonus, float accMulti ) {
//
//        boolean visibleFight = Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[enemy.pos];
//
//        if (enemy.isInvulnerable(getClass())) {
//            //cant attack
//            return false;
//
//        } else if (hit( this, enemy, accMulti, false )) {
//
//            int dr = Math.round(enemy.drRoll() * AscensionChallenge.statModifier(enemy));
//
//            //dr ist 0, wenn enemy Armor ignoriert wird
//
//            //we use a float here briefly so that we don't have to constantly round while
//            // potentially applying various multiplier effects
//            float dmg = damageRoll();
//
//            dmg = Math.round(dmg*dmgMulti);
//
//            //flat damage bonus is applied after positive multipliers, but before negative ones
//            dmg += dmgBonus;
//
//
//            //Gegner kriegt noch mal Chance, Schaden zu reduzieren
//            int effectiveDamage = enemy.defenseProc( this, Math.round(dmg) );
//
//            //dr wird vom Schaden abgezogen: großer dr -> immunität oder desto weniger Schaden
//            effectiveDamage = Math.max( effectiveDamage - dr, 0 );
//
//            //Schaden wird nun noch mal verstälrlt
//            effectiveDamage = attackProc( enemy, effectiveDamage );
//
//
//            // If the enemy is already dead, interrupt the attack.
//            // This matters as defence procs can sometimes inflict self-damage, such as armor glyphs.
//            if (!enemy.isAlive()){
//                return true;
//            }
//
//            enemy.damage( effectiveDamage, this );
//
//            enemy.sprite.bloodBurstA( sprite.center(), effectiveDamage );
//            enemy.sprite.flash();
//            return true;
//
//        } else {
//            return false;
//
//        }
//    }
//
//    public static int INFINITE_ACCURACY = 1_000_000;
//
//    final public static boolean hit( Char attacker, Char defender, boolean magic ) {
//        return hit(attacker, defender, magic ? 2f : 1f, magic);
//    }
//
//    //returns true if the attackers accuracy is larger than the defenders accuracy
//    //accuracy is the same as evasion
//    //also checks numerous buffs
//    //attack- and defenseSkill are used
//    public static boolean hit( Char attacker, Char defender, float accMulti, boolean magic ) {
//        float acuStat = attacker.attackSkill( defender );
//        float defStat = defender.defenseSkill( attacker );
//
//        float acuRoll = Random.Float( acuStat );
//        float defRoll = Random.Float( defStat );
//
//        return (acuRoll * accMulti) >= defRoll;
//    }
//
//    public int ATTACKSKILL( Char target ) {//accurancy
//        return 0;
//    }
//
//    public int defenseSkill( Char enemy ) {//Evasion
//        return 0;//usually returns this.defenseSkill
//    }
//
//    public int DRROLL() { //je größer return, desto geringer eigener HP-Verlust
//        return Random.NormalIntRange( 0 , Barkskin.currentLevel(this) );
//    }
//
//    public int DAMAGEROLL() {
//        return 1;
//    }
//
//    //reduces the HP by dmg, after applying some damage resistance and other stuff
//    public void damage( int dmg, Object src ) {
//        HP -= dmg;
//        if (HP < 0) HP = 0;
//    }


}