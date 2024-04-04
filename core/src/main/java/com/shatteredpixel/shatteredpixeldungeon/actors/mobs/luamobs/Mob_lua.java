package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.luamobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Mob_lua extends Rat implements LuaMob {

    private int identifier;
    private LuaTable vars;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LuaClass.IDENTIFIER, identifier);
        if (vars != null && !CustomDungeon.isEditing()) {
            LuaManager.storeVarInBundle(bundle, vars, VARS);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        identifier = bundle.getInt(LuaClass.IDENTIFIER);

        LuaValue script;
        if (!CustomDungeon.isEditing() && (script = CustomObject.getScript(identifier)) != null && script.get("vars").istable()) {
            vars = LuaManager.deepCopyLuaValue(script.get("vars")).checktable();

            LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, VARS);
            if (loaded != null && loaded.istable()) vars = loaded.checktable();
            if (script.get("static").istable()) vars.set("static", script.get("static"));
        }
    }

    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public int attackSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackSkill").isnil()) {
            LuaManager.scriptsRunning++;
            try {
                int ret = luaScript.get("attackSkill").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                LuaManager.scriptsRunning--;
                return ret;
            } catch (LuaError e) {
                Game.runOnRenderThread(()->	GameScene.show(new WndError(e)));
            }
        }
        return super.attackSkill(arg0);
    }

    @Override
    public void die(Object arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("die").isnil()) {
            LuaManager.scriptsRunning++;
            MethodOverride.VoidA1 superMethod = a0 -> super.die(a0);
            luaScript.get("die").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)});
            LuaManager.scriptsRunning--;
        } else {
            super.die(arg0);
        }
    }

    @Override
    protected boolean canAttack(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canAttack").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("canAttack").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.canAttack(arg0);
        }
    }

    @Override
    public boolean heroShouldInteract() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("heroShouldInteract").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("heroShouldInteract").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.heroShouldInteract();
        }
    }

    @Override
    protected void throwItems() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("throwItems").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("throwItems").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.throwItems();
        }
    }

    @Override
    public boolean isTargeting(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isTargeting").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isTargeting").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isTargeting(arg0);
        }
    }

    @Override
    protected void onAdd() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAdd").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onAdd").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onAdd();
        }
    }

    @Override
    public void onOperateComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onOperateComplete").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onOperateComplete").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onOperateComplete();
        }
    }

    @Override
    public float spawningWeight() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spawningWeight").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("spawningWeight").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.spawningWeight();
        }
    }

    @Override
    public void setFlying(boolean arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setFlying").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("setFlying").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.setFlying(arg0);
        }
    }

    @Override
    public void initRandoms() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("initRandoms").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("initRandoms").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.initRandoms();
        }
    }

    @Override
    public float stealth() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("stealth").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("stealth").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.stealth();
        }
    }

    @Override
    public float cooldown() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cooldown").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("cooldown").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.cooldown();
        }
    }

    @Override
    public synchronized LinkedHashSet buffs() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buffs").isnil()) {
            LuaManager.scriptsRunning++;
            LinkedHashSet ret = (LinkedHashSet) luaScript.get("buffs").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.buffs();
        }
    }

    @Override
    public int id() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("id").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("id").call(CoerceJavaToLua.coerce(this), vars).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.id();
        }
    }

    @Override
    public void onAttackComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAttackComplete").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onAttackComplete").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onAttackComplete();
        }
    }

    @Override
    public boolean shouldSpriteBeVisible() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shouldSpriteBeVisible").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("shouldSpriteBeVisible").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.shouldSpriteBeVisible();
        }
    }

    @Override
    public void timeToNow() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("timeToNow").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("timeToNow").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.timeToNow();
        }
    }

    @Override
    public float lootChance() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("lootChance").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("lootChance").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.lootChance();
        }
    }

    @Override
    protected void zap() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("zap").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("zap").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.zap();
        }
    }

    @Override
    public void destroy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("destroy").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.destroy();
        }
    }

    @Override
    public String defenseVerb() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseVerb").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("defenseVerb").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.defenseVerb();
        }
    }

    @Override
    public boolean reset() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("reset").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.reset();
        }
    }

    @Override
    public int drRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("drRoll").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("drRoll").call(CoerceJavaToLua.coerce(this), vars).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.drRoll();
        }
    }

    @Override
    protected void spendConstant(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendConstant").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("spendConstant").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.spendConstant(arg0);
        }
    }

    @Override
    protected int randomDestination() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("randomDestination").call(CoerceJavaToLua.coerce(this), vars).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.randomDestination();
        }
    }

    @Override
    protected void tellDialog() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("tellDialog").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("tellDialog").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.tellDialog();
        }
    }

    @Override
    public boolean isFlying() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isFlying").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isFlying").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isFlying();
        }
    }

    @Override
    public boolean[] modPassable(boolean[] arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("modPassable").isnil()) {
            LuaManager.scriptsRunning++;
            boolean[] ret = (boolean[]) luaScript.get("modPassable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.modPassable(arg0);
        }
    }

    @Override
    public void damage(int arg0, Object arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damage").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("damage").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)});
            LuaManager.scriptsRunning--;
        } else {
            super.damage(arg0, arg1);
        }
    }

    @Override
    public void restoreEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreEnemy").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("restoreEnemy").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.restoreEnemy();
        }
    }

    @Override
    protected boolean getFurther(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getFurther").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("getFurther").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.getFurther(arg0);
        }
    }

    @Override
    public void onZapComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onZapComplete").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onZapComplete").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onZapComplete();
        }
    }

    @Override
    protected boolean cellIsPathable(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cellIsPathable").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("cellIsPathable").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.cellIsPathable(arg0);
        }
    }

    @Override
    public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("convertLootToRandomItemData").isnil()) {
            LuaManager.scriptsRunning++;
            ItemsWithChanceDistrComp.RandomItemData ret = (ItemsWithChanceDistrComp.RandomItemData) luaScript.get("convertLootToRandomItemData").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.convertLootToRandomItemData();
        }
    }

    @Override
    public boolean attack(Char arg0, float arg1, float arg2, float arg3) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attack").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("attack").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1), LuaValue.valueOf(arg2), LuaValue.valueOf(arg3)}).arg1().toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.attack(arg0, arg1, arg2, arg3);
        }
    }

    @Override
    public CharSprite sprite() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("sprite").isnil()) {
            LuaManager.scriptsRunning++;
            CharSprite ret = (CharSprite) luaScript.get("sprite").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.sprite();
        }
    }

    @Override
    protected Item createLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createLoot").isnil()) {
            LuaManager.scriptsRunning++;
            Item ret = (Item) luaScript.get("createLoot").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.createLoot();
        }
    }

    @Override
    public void updateSpriteVisibility() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteVisibility").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("updateSpriteVisibility").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.updateSpriteVisibility();
        }
    }

    @Override
    protected boolean getCloser(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCloser").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("getCloser").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.getCloser(arg0);
        }
    }

    @Override
    public String getCustomName() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomName").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("getCustomName").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.getCustomName();
        }
    }

    @Override
    public void setPlayerAlignment(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setPlayerAlignment").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("setPlayerAlignment").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.setPlayerAlignment(arg0);
        }
    }

    @Override
    protected boolean moveSprite(int arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("moveSprite").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("moveSprite").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.moveSprite(arg0, arg1);
        }
    }

    @Override
    public String getCustomDesc() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomDesc").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("getCustomDesc").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.getCustomDesc();
        }
    }

    @Override
    protected void diactivate() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("diactivate").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("diactivate").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.diactivate();
        }
    }

    @Override
    public void aggro(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("aggro").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("aggro").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.aggro(arg0);
        }
    }

    @Override
    public void onMotionComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMotionComplete").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onMotionComplete").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onMotionComplete();
        }
    }

    @Override
    public void updateSpriteState() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteState").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("updateSpriteState").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.updateSpriteState();
        }
    }

    @Override
    protected synchronized void onRemove() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onRemove").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onRemove").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.onRemove();
        }
    }

    @Override
    public synchronized Buff buff(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buff").isnil()) {
            LuaManager.scriptsRunning++;
            Buff ret = (Buff) luaScript.get("buff").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.buff(arg0);
        }
    }

    @Override
    public boolean blockSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("blockSound").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("blockSound").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.blockSound(arg0);
        }
    }

    @Override
    public synchronized boolean isCharmedBy(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isCharmedBy").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isCharmedBy").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isCharmedBy(arg0);
        }
    }

    @Override
    public void setCustomName(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomName").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("setCustomName").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.setCustomName(arg0);
        }
    }

    @Override
    public List createActualLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createActualLoot").isnil()) {
            LuaManager.scriptsRunning++;
            List ret = (List) luaScript.get("createActualLoot").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.createActualLoot();
        }
    }

    @Override
    public void clearTime() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearTime").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("clearTime").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.clearTime();
        }
    }

    @Override
    protected void doDropLoot(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doDropLoot").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("doDropLoot").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.doDropLoot(arg0);
        }
    }

    @Override
    public boolean isActive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isActive").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isActive").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isActive();
        }
    }

    @Override
    protected boolean act() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("act").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("act").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.act();
        }
    }

    @Override
    public void onMapSizeChange(IntFunction arg0, BiPredicate arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMapSizeChange").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("onMapSizeChange").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)});
            LuaManager.scriptsRunning--;
        } else {
            super.onMapSizeChange(arg0, arg1);
        }
    }

    @Override
    public void hitSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("hitSound").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("hitSound").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.hitSound(arg0);
        }
    }

    @Override
    public void spendToWhole() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendToWhole").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("spendToWhole").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.spendToWhole();
        }
    }

    @Override
    public void restoreCurrentZone(Level arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreCurrentZone").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("restoreCurrentZone").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.restoreCurrentZone(arg0);
        }
    }

    @Override
    public String info() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("info").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("info").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.info();
        }
    }

    @Override
    public boolean add(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("add").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("add").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.add(arg0);
        }
    }

    @Override
    public boolean canInteract(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canInteract").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("canInteract").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.canInteract(arg0);
        }
    }

    @Override
    public void increaseLimitedDropCount(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("increaseLimitedDropCount").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("increaseLimitedDropCount").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.increaseLimitedDropCount(arg0);
        }
    }

    @Override
    public float resist(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("resist").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("resist").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.resist(arg0);
        }
    }

    @Override
    public int attackProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackProc").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("attackProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.attackProc(arg0, arg1);
        }
    }

    @Override
    public boolean isAlive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isAlive").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isAlive").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isAlive();
        }
    }

    @Override
    public void setCustomDesc(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomDesc").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("setCustomDesc").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.setCustomDesc(arg0);
        }
    }

    @Override
    public void yell(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("yell").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("yell").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.yell(arg0);
        }
    }

    @Override
    public String name() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("name").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("name").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.name();
        }
    }

    @Override
    public int damageRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damageRoll").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("damageRoll").call(CoerceJavaToLua.coerce(this), vars).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.damageRoll();
        }
    }

    @Override
    protected void postpone(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("postpone").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("postpone").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.postpone(arg0);
        }
    }

    @Override
    public boolean isImmune(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isImmune").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isImmune").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isImmune(arg0);
        }
    }

    @Override
    public void next() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("next").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("next").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.next();
        }
    }

    @Override
    public int distance(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("distance").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("distance").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.distance(arg0);
        }
    }

    @Override
    public String description() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("description").isnil()) {
            LuaManager.scriptsRunning++;
            String ret = luaScript.get("description").call(CoerceJavaToLua.coerce(this), vars).tojstring();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.description();
        }
    }

    @Override
    public boolean interact(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("interact").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("interact").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.interact(arg0);
        }
    }

    @Override
    public float attackDelay() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackDelay").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("attackDelay").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.attackDelay();
        }
    }

    @Override
    public boolean remove(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("remove").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("remove").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.remove(arg0);
        }
    }

    @Override
    public float speed() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("speed").isnil()) {
            LuaManager.scriptsRunning++;
            float ret = luaScript.get("speed").call(CoerceJavaToLua.coerce(this), vars).tofloat();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.speed();
        }
    }

    @Override
    public boolean surprisedBy(Char arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("surprisedBy").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("surprisedBy").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.surprisedBy(arg0, arg1);
        }
    }

    @Override
    protected void spend(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spend").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("spend").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.spend(arg0);
        }
    }

    @Override
    public void clearEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearEnemy").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("clearEnemy").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.clearEnemy();
        }
    }

    @Override
    public int defenseSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseSkill").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("defenseSkill").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.defenseSkill(arg0);
        }
    }

    @Override
    public void notice() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("notice").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("notice").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.notice();
        }
    }

    @Override
    public void move(int arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("move").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("move").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)});
            LuaManager.scriptsRunning--;
        } else {
            super.move(arg0, arg1);
        }
    }

    @Override
    protected void playBossMusic(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("playBossMusic").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("playBossMusic").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.playBossMusic(arg0);
        }
    }

    @Override
    protected Char chooseEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("chooseEnemy").isnil()) {
            LuaManager.scriptsRunning++;
            Char ret = (Char) luaScript.get("chooseEnemy").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.chooseEnemy();
        }
    }

    @Override
    public boolean isInvulnerable(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isInvulnerable").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("isInvulnerable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.isInvulnerable(arg0);
        }
    }

    @Override
    public void addBossProperty() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("addBossProperty").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("addBossProperty").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.addBossProperty();
        }
    }

    @Override
    public void beckon(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("beckon").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("beckon").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
            LuaManager.scriptsRunning--;
        } else {
            super.beckon(arg0);
        }
    }

    @Override
    public boolean areStatsEqual(Mob arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("areStatsEqual").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("areStatsEqual").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.areStatsEqual(arg0);
        }
    }

    @Override
    public boolean canSurpriseAttack() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canSurpriseAttack").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("canSurpriseAttack").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.canSurpriseAttack();
        }
    }

    @Override
    public int shielding() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shielding").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("shielding").call(CoerceJavaToLua.coerce(this), vars).toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.shielding();
        }
    }

    @Override
    public void rollToDropLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("rollToDropLoot").isnil()) {
            LuaManager.scriptsRunning++;
            luaScript.get("rollToDropLoot").call(CoerceJavaToLua.coerce(this), vars);
            LuaManager.scriptsRunning--;
        } else {
            super.rollToDropLoot();
        }
    }

    @Override
    public int defenseProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseProc").isnil()) {
            LuaManager.scriptsRunning++;
            int ret = luaScript.get("defenseProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.defenseProc(arg0, arg1);
        }
    }

    @Override
    public boolean avoidsHazards() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("avoidsHazards").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("avoidsHazards").call(CoerceJavaToLua.coerce(this), vars).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.avoidsHazards();
        }
    }

    @Override
    protected boolean doAttack(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doAttack").isnil()) {
            LuaManager.scriptsRunning++;
            boolean ret = luaScript.get("doAttack").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.doAttack(arg0);
        }
    }

    @Override
    public HashSet properties() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("properties").isnil()) {
            LuaManager.scriptsRunning++;
            HashSet ret = (HashSet) luaScript.get("properties").call(CoerceJavaToLua.coerce(this), vars).touserdata();
            LuaManager.scriptsRunning--;
            return ret;
        } else {
            return super.properties();
        }
    }
}