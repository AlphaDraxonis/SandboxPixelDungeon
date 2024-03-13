package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.luamobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.editor.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.LuaClassGenerator;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Mob_lua extends Rat implements LuaClass {

    private String identifier;
    private LuaValue luaVars;//LuaTable mit variablen, wird gespeichert,  bei restoreFromBundle() werden nur die Werte übernommen, die tatsächlich noch vorhanden sind

    {
        //TODO tzz find better way of copying, extract methods and make static
        LuaTable originalVars = LuaClassGenerator.luaScript.get("vars").checktable();
        luaVars = new LuaTable();
        for (LuaValue key : originalVars.keys()) {
            LuaValue value = originalVars.get(key);
            if (value.isuserdata()) {
                Object obj = value.touserdata();
                if (obj instanceof Copyable) obj = ((Copyable) obj).getCopy();
                luaVars.set(key, obj);
            } else {
                luaVars.set(key, value);
            }
        }

        //in restoreFromBundle: check what has been saved, and only override those vars that are still present
        //in storeInBundle: find a way to properly store all of that automatically
        //test if they are separate
    }


    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    protected boolean canAttack(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("canAttack").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("canAttack").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.canAttack(arg0);
        }
    }

    @Override
    public boolean heroShouldInteract() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("heroShouldInteract").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("heroShouldInteract").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.heroShouldInteract();
        }
    }

    @Override
    protected void throwItems() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("throwItems").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("throwItems").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.throwItems();
        }
    }

    @Override
    public boolean isTargeting(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isTargeting").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isTargeting").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isTargeting(arg0);
        }
    }

    @Override
    protected void onAdd() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onAdd").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onAdd").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onAdd();
        }
    }

    @Override
    public void onOperateComplete() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onOperateComplete").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onOperateComplete").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onOperateComplete();
        }
    }

    @Override
    public float spawningWeight() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("spawningWeight").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("spawningWeight").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.spawningWeight();
        }
    }

    @Override
    public void setFlying(boolean arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setFlying").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setFlying").call(CoerceJavaToLua.coerce(this), LuaBoolean.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setFlying(arg0);
        }
    }

    @Override
    public boolean onDeleteLevelScheme(String arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onDeleteLevelScheme").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("onDeleteLevelScheme").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.onDeleteLevelScheme(arg0);
        }
    }

    @Override
    public void initRandoms() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("initRandoms").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("initRandoms").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.initRandoms();
        }
    }

    @Override
    public float stealth() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("stealth").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("stealth").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.stealth();
        }
    }

    @Override
    public float cooldown() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("cooldown").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("cooldown").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.cooldown();
        }
    }

    @Override
    public void restoreFromBundle(Bundle arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("restoreFromBundle").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("restoreFromBundle").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.restoreFromBundle(arg0);
        }
    }

    @Override
    public synchronized LinkedHashSet buffs() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("buffs").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            LinkedHashSet ret = (LinkedHashSet) luaScript.get("buffs").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.buffs();
        }
    }

    @Override
    public int id() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("id").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("id").call(CoerceJavaToLua.coerce(this)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.id();
        }
    }

    @Override
    public void onAttackComplete() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onAttackComplete").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onAttackComplete").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onAttackComplete();
        }
    }

    @Override
    public boolean shouldSpriteBeVisible() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("shouldSpriteBeVisible").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("shouldSpriteBeVisible").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.shouldSpriteBeVisible();
        }
    }

    @Override
    public void timeToNow() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("timeToNow").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("timeToNow").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.timeToNow();
        }
    }

    @Override
    public void setDurationForBuff(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setDurationForBuff").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setDurationForBuff").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setDurationForBuff(arg0);
        }
    }

    @Override
    public float lootChance() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("lootChance").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("lootChance").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.lootChance();
        }
    }

    @Override
    protected void zap() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("zap").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("zap").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.zap();
        }
    }

    @Override
    public void destroy() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("destroy").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.destroy();
        }
    }

    @Override
    public String defenseVerb() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("defenseVerb").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = (String) luaScript.get("defenseVerb").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.defenseVerb();
        }
    }

    @Override
    public void spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(arg0);
        }
    }

    @Override
    public boolean reset() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("reset").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.reset();
        }
    }

    @Override
    public int drRoll() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("drRoll").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("drRoll").call(CoerceJavaToLua.coerce(this)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.drRoll();
        }
    }

    @Override
    protected void spendConstant(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("spendConstant").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("spendConstant").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.spendConstant(arg0);
        }
    }

    @Override
    protected int randomDestination() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("randomDestination").call(CoerceJavaToLua.coerce(this)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.randomDestination();
        }
    }

    @Override
    protected void tellDialog() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("tellDialog").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("tellDialog").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.tellDialog();
        }
    }

    @Override
    public boolean isFlying() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isFlying").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isFlying").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isFlying();
        }
    }

    @Override
    public boolean[] modPassable(boolean[] arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("modPassable").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean[] ret = (boolean[]) luaScript.get("modPassable").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.modPassable(arg0);
        }
    }

    @Override
    public void damage(int arg0, Object arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("damage").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("damage").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0), CoerceJavaToLua.coerce(arg1));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.damage(arg0, arg1);
        }
    }

    @Override
    public void restoreEnemy() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("restoreEnemy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("restoreEnemy").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.restoreEnemy();
        }
    }

    @Override
    protected boolean getFurther(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("getFurther").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("getFurther").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.getFurther(arg0);
        }
    }

    @Override
    public void onZapComplete() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onZapComplete").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onZapComplete").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onZapComplete();
        }
    }

    @Override
    protected boolean cellIsPathable(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("cellIsPathable").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("cellIsPathable").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.cellIsPathable(arg0);
        }
    }

    @Override
    public int attackSkill(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("attackSkill").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("attackSkill").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.attackSkill(arg0);
        }
    }

    @Override
    public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("convertLootToRandomItemData").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            ItemsWithChanceDistrComp.RandomItemData ret = (ItemsWithChanceDistrComp.RandomItemData) luaScript.get("convertLootToRandomItemData").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.convertLootToRandomItemData();
        }
    }

    @Override
    public boolean onRenameLevelScheme(String arg0, String arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onRenameLevelScheme").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("onRenameLevelScheme").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.onRenameLevelScheme(arg0, arg1);
        }
    }

    @Override
    public boolean attack(Char arg0, float arg1, float arg2, float arg3) {//hjk
//        LuaValue luaScript = LuaClassGenerator.luaScript;
//        if (luaScript != null && !luaScript.get("attack").isnil()) {
//            LuaClassGenerator.scriptsRunning++;
//            boolean ret = luaScript.get("attack").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1), LuaValue.valueOf(arg2), LuaValue.valueOf(arg3)).toboolean();
//            LuaClassGenerator.scriptsRunning--;
//            return ret;
//        } else {
            return super.attack(arg0, arg1, arg2, arg3);
//        }
    }

    @Override
    public CharSprite sprite() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("sprite").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            CharSprite ret = (CharSprite) luaScript.get("sprite").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.sprite();
        }
    }

    @Override
    protected Item createLoot() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("createLoot").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            Item ret = (Item) luaScript.get("createLoot").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.createLoot();
        }
    }

    @Override
    public void updateSpriteVisibility() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("updateSpriteVisibility").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("updateSpriteVisibility").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.updateSpriteVisibility();
        }
    }

    @Override
    protected boolean getCloser(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("getCloser").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("getCloser").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.getCloser(arg0);
        }
    }

    @Override
    public String getCustomName() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("getCustomName").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = (String) luaScript.get("getCustomName").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.getCustomName();
        }
    }

    @Override
    public void setPlayerAlignment(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setPlayerAlignment").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setPlayerAlignment").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setPlayerAlignment(arg0);
        }
    }

    @Override
    protected boolean moveSprite(int arg0, int arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("moveSprite").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("moveSprite").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0), LuaInteger.valueOf(arg1)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.moveSprite(arg0, arg1);
        }
    }

    @Override
    public String getCustomDesc() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("getCustomDesc").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = (String) luaScript.get("getCustomDesc").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.getCustomDesc();
        }
    }

    @Override
    protected void diactivate() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("diactivate").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("diactivate").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.diactivate();
        }
    }

    @Override
    public void aggro(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("aggro").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("aggro").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.aggro(arg0);
        }
    }

    @Override
    public void onMotionComplete() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onMotionComplete").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onMotionComplete").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onMotionComplete();
        }
    }

    @Override
    public void updateSpriteState() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("updateSpriteState").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("updateSpriteState").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.updateSpriteState();
        }
    }

    @Override
    protected synchronized void onRemove() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onRemove").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onRemove").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onRemove();
        }
    }

    @Override
    public synchronized Buff buff(Class arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("buff").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            Buff ret = (Buff) luaScript.get("buff").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.buff(arg0);
        }
    }

    @Override
    public void die(Object arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("die").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("die").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.die(arg0);
        }
    }

    @Override
    public boolean blockSound(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("blockSound").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("blockSound").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.blockSound(arg0);
        }
    }

    @Override
    public synchronized boolean isCharmedBy(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isCharmedBy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isCharmedBy").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isCharmedBy(arg0);
        }
    }

    @Override
    public void setCustomName(String arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setCustomName").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setCustomName").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setCustomName(arg0);
        }
    }

    @Override
    public List createActualLoot() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("createActualLoot").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            List ret = (List) luaScript.get("createActualLoot").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.createActualLoot();
        }
    }

    @Override
    public void clearTime() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("clearTime").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("clearTime").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.clearTime();
        }
    }

    @Override
    protected void doDropLoot(Item arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("doDropLoot").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("doDropLoot").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.doDropLoot(arg0);
        }
    }

    @Override
    public boolean isActive() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isActive").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isActive").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isActive();
        }
    }

    @Override
    protected boolean act() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("act").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("act").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.act();
        }
    }

    @Override
    public Actor getCopy() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("getCopy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            Actor ret = (Actor) luaScript.get("getCopy").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.getCopy();
        }
    }

    @Override
    public void onMapSizeChange(IntFunction arg0, BiPredicate arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("onMapSizeChange").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("onMapSizeChange").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.onMapSizeChange(arg0, arg1);
        }
    }

    @Override
    public void storeInBundle(Bundle arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("storeInBundle").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("storeInBundle").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.storeInBundle(arg0);
        }
    }

    @Override
    public void hitSound(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("hitSound").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("hitSound").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.hitSound(arg0);
        }
    }

    @Override
    public void spendToWhole() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("spendToWhole").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("spendToWhole").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.spendToWhole();
        }
    }

    @Override
    public void restoreCurrentZone(Level arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("restoreCurrentZone").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("restoreCurrentZone").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.restoreCurrentZone(arg0);
        }
    }

    @Override
    public String info() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("info").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = luaScript.get("info").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.info();
        }
    }

    @Override
    public boolean add(Buff arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("add").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("add").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.add(arg0);
        }
    }

    @Override
    public boolean canInteract(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("canInteract").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("canInteract").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.canInteract(arg0);
        }
    }

    @Override
    public void increaseLimitedDropCount(Item arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("increaseLimitedDropCount").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("increaseLimitedDropCount").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.increaseLimitedDropCount(arg0);
        }
    }

    @Override
    public float resist(Class arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("resist").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("resist").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.resist(arg0);
        }
    }

    @Override
    public int attackProc(Char arg0, int arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("attackProc").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("attackProc").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.attackProc(arg0, arg1);
        }
    }

    @Override
    public boolean isAlive() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isAlive").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isAlive").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isAlive();
        }
    }

    @Override
    public void setCustomDesc(String arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setCustomDesc").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setCustomDesc").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setCustomDesc(arg0);
        }
    }

    @Override
    public void yell(String arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("yell").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("yell").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.yell(arg0);
        }
    }

    @Override
    public String name() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("name").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = (String) luaScript.get("name").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.name();
        }
    }

    @Override
    public int damageRoll() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("damageRoll").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("damageRoll").call(CoerceJavaToLua.coerce(this)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.damageRoll();
        }
    }

    @Override
    protected void postpone(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("postpone").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("postpone").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.postpone(arg0);
        }
    }

    @Override
    public boolean isImmune(Class arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isImmune").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isImmune").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isImmune(arg0);
        }
    }

    @Override
    public void next() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("next").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("next").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.next();
        }
    }

    @Override
    public int distance(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("distance").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("distance").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.distance(arg0);
        }
    }

    @Override
    public String description() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("description").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            String ret = (String) luaScript.get("description").call(CoerceJavaToLua.coerce(this)).tojstring();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.description();
        }
    }

    @Override
    public boolean interact(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("interact").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("interact").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.interact(arg0);
        }
    }

    @Override
    public float attackDelay() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("attackDelay").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("attackDelay").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.attackDelay();
        }
    }

    @Override
    public boolean remove(Buff arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("remove").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("remove").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.remove(arg0);
        }
    }

    @Override
    public float speed() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("speed").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            float ret = luaScript.get("speed").call(CoerceJavaToLua.coerce(this)).tofloat();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.speed();
        }
    }

    @Override
    public boolean surprisedBy(Char arg0, boolean arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("surprisedBy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("surprisedBy").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), LuaBoolean.valueOf(arg1)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.surprisedBy(arg0, arg1);
        }
    }

    @Override
    protected void spend(float arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("spend").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("spend").call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.spend(arg0);
        }
    }

    @Override
    public void clearEnemy() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("clearEnemy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("clearEnemy").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.clearEnemy();
        }
    }

    @Override
    public int defenseSkill(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("defenseSkill").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("defenseSkill").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.defenseSkill(arg0);
        }
    }

    @Override
    public void notice() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("notice").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("notice").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.notice();
        }
    }

    @Override
    protected void moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(Buff arg0, Char arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob(arg0, arg1);
        }
    }

    @Override
    public void move(int arg0, boolean arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("move").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("move").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0), LuaBoolean.valueOf(arg1));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.move(arg0, arg1);
        }
    }

    @Override
    protected void playBossMusic(String arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("playBossMusic").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("playBossMusic").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.playBossMusic(arg0);
        }
    }

    @Override
    protected Char chooseEnemy() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("chooseEnemy").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            Char ret = (Char) luaScript.get("chooseEnemy").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.chooseEnemy();
        }
    }

    @Override
    public boolean isInvulnerable(Class arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("isInvulnerable").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("isInvulnerable").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.isInvulnerable(arg0);
        }
    }

    @Override
    public void addBossProperty() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("addBossProperty").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("addBossProperty").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.addBossProperty();
        }
    }

    @Override
    public void beckon(int arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("beckon").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("beckon").call(CoerceJavaToLua.coerce(this), LuaInteger.valueOf(arg0));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.beckon(arg0);
        }
    }

    @Override
    public boolean areStatsEqual(Mob arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("areStatsEqual").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("areStatsEqual").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.areStatsEqual(arg0);
        }
    }

    @Override
    public int shielding() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("shielding").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("shielding").call(CoerceJavaToLua.coerce(this)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.shielding();
        }
    }

    @Override
    public boolean canSurpriseAttack() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("canSurpriseAttack").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("canSurpriseAttack").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.canSurpriseAttack();
        }
    }

    @Override
    public void rollToDropLoot() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("rollToDropLoot").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("rollToDropLoot").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.rollToDropLoot();
        }
    }

    @Override
    public void setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            luaScript.get("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME").call(CoerceJavaToLua.coerce(this));
            LuaClassGenerator.scriptsRunning--;
        } else {
            super.setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME();
        }
    }

    @Override
    public int defenseProc(Char arg0, int arg1) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("defenseProc").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            int ret = luaScript.get("defenseProc").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0), LuaInteger.valueOf(arg1)).toint();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.defenseProc(arg0, arg1);
        }
    }

    @Override
    public boolean avoidsHazards() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("avoidsHazards").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("avoidsHazards").call(CoerceJavaToLua.coerce(this)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.avoidsHazards();
        }
    }

    @Override
    protected boolean doAttack(Char arg0) {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("doAttack").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            boolean ret = luaScript.get("doAttack").call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.doAttack(arg0);
        }
    }

    @Override
    public HashSet properties() {
        LuaValue luaScript = LuaClassGenerator.luaScript;
        if (luaScript != null && !luaScript.get("properties").isnil()) {
            LuaClassGenerator.scriptsRunning++;
            HashSet ret = (HashSet) luaScript.get("properties").call(CoerceJavaToLua.coerce(this)).touserdata();
            LuaClassGenerator.scriptsRunning--;
            return ret;
        } else {
            return super.properties();
        }
    }
}