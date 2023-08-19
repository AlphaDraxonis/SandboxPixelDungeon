package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.stateditor;

import com.alphadraxonis.sandboxpixeldungeon.items.food.Food;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
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

    //Mobs
    //HT (current HP) and HP (max HP)
    //base Speed
    //view distance (exclude Hero)
    //EXP (xp on death)
    //maxLevel (hero level until they drop xp incl)
    //loot chance
    //loot (braucht noch generator und classes)
    //defenseSkill (evasion)
    //properties?

    private static Map<Class<? extends Bundlable>, Bundlable> cache = new HashMap<>();

    public static <T extends Bundlable> T getDefaultObject(Class<T> clazz) {
        T ret = (T) cache.get(clazz);
        if (ret == null) {
            ret = Reflection.newInstance(clazz);
            cache.put(clazz, ret);
        }
        return ret;
    }

    public static boolean canModifyStats(Object obj){
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