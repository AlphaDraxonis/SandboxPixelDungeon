package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.luamobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.watabou.noosa.Game;
import com.watabou.utils.*;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import java.util.*;

public class DarkFist_lua extends YogFist.DarkFist implements LuaMob {

    private int identifier;
    private boolean inheritsStats = true;
    private LuaTable vars;

    @Override
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public int getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setInheritsStats(boolean inheritsStats) {
        this.inheritsStats = inheritsStats;
    }

    @Override
    public boolean getInheritsStats() {
        return inheritsStats;
    }

    @Override
    public LuaClass newInstance() {
        return (LuaClass) getCopy();
    }

@Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LuaClass.IDENTIFIER, identifier);
        bundle.put(LuaMob.INHERITS_STATS, inheritsStats);
        if (vars != null && !CustomDungeon.isEditing()) {
            LuaManager.storeVarInBundle(bundle, vars, VARS);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        identifier = bundle.getInt(LuaClass.IDENTIFIER);
        inheritsStats = bundle.getBoolean(LuaMob.INHERITS_STATS);

        LuaValue script;
        if (!CustomDungeon.isEditing() && (script = CustomObject.getScript(identifier)) != null && script.get("vars").istable()) {
            vars = LuaManager.deepCopyLuaValue(script.get("vars")).checktable();

            LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, VARS);
            if (loaded != null && loaded.istable()) vars = loaded.checktable();
            if (script.get("static").istable()) vars.set("static", script.get("static"));
        }
    }


    @Override
    protected boolean canAttack(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canAttack").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.canAttack((Char) a0);
               boolean ret = luaScript.get("canAttack").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canAttack(arg0);
    }

    @Override
    public boolean heroShouldInteract() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("heroShouldInteract").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.heroShouldInteract();
               boolean ret = luaScript.get("heroShouldInteract").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.heroShouldInteract();
    }

    @Override
    protected void throwItems() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("throwItems").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.throwItems();
               luaScript.get("throwItems").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.throwItems();
    }

    @Override
    public boolean isTargeting(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isTargeting").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isTargeting((Char) a0);
               boolean ret = luaScript.get("isTargeting").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isTargeting(arg0);
    }

    @Override
    protected void onAdd() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAdd").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onAdd();
               luaScript.get("onAdd").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onAdd();
    }

    @Override
    public void onOperateComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onOperateComplete").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onOperateComplete();
               luaScript.get("onOperateComplete").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onOperateComplete();
    }

    @Override
    public float spawningWeight() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spawningWeight").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.spawningWeight();
               float ret = luaScript.get("spawningWeight").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.spawningWeight();
    }

    @Override
    public void setFlying(boolean arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setFlying").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setFlying((boolean) a0);
               luaScript.get("setFlying").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setFlying(arg0);
    }

    @Override
    public GameObject.ModifyResult initRandoms() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("initRandoms").isnil()) {
            try {
                MethodOverride.A0<GameObject.ModifyResult> superMethod = () -> super.initRandoms();
               GameObject.ModifyResult ret = (GameObject.ModifyResult) luaScript.get("initRandoms").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.initRandoms();
    }

    @Override
    public float stealth() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("stealth").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.stealth();
               float ret = luaScript.get("stealth").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.stealth();
    }

    @Override
    public float cooldown() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cooldown").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.cooldown();
               float ret = luaScript.get("cooldown").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.cooldown();
    }

    @Override
    public synchronized LinkedHashSet buffs() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buffs").isnil()) {
            try {
                MethodOverride.A0<LinkedHashSet> superMethod = () -> super.buffs();
               LinkedHashSet ret = (LinkedHashSet) luaScript.get("buffs").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.buffs();
    }

    @Override
    public int id() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("id").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.id();
               int ret = luaScript.get("id").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.id();
    }

    @Override
    public void onAttackComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAttackComplete").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onAttackComplete();
               luaScript.get("onAttackComplete").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onAttackComplete();
    }

    @Override
    public boolean shouldSpriteBeVisible() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shouldSpriteBeVisible").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.shouldSpriteBeVisible();
               boolean ret = luaScript.get("shouldSpriteBeVisible").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.shouldSpriteBeVisible();
    }

    @Override
    public void timeToNow() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("timeToNow").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.timeToNow();
               luaScript.get("timeToNow").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.timeToNow();
    }

    @Override
    public void playZapAnim(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("playZapAnim").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.playZapAnim((int) a0);
               luaScript.get("playZapAnim").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playZapAnim(arg0);
    }

    @Override
    protected void incrementRangedCooldown() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("incrementRangedCooldown").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.incrementRangedCooldown();
               luaScript.get("incrementRangedCooldown").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.incrementRangedCooldown();
    }

    @Override
    protected Char chooseEnemyImpl() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("chooseEnemyImpl").isnil()) {
            try {
                MethodOverride.A0<Char> superMethod = () -> super.chooseEnemyImpl();
               Char ret = (Char) luaScript.get("chooseEnemyImpl").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.chooseEnemyImpl();
    }

    @Override
    public float lootChance() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("lootChance").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.lootChance();
               float ret = luaScript.get("lootChance").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.lootChance();
    }

    @Override
    public void zap() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("zap").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.zap();
               luaScript.get("zap").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.zap();
    }

    @Override
    public void destroy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.destroy();
               luaScript.get("destroy").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.destroy();
    }

    @Override
    public String defenseVerb() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseVerb").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.defenseVerb();
               String ret = luaScript.get("defenseVerb").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseVerb();
    }

    @Override
    public boolean doOnAllGameObjects(Function arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doOnAllGameObjects").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.doOnAllGameObjects((Function) a0);
               boolean ret = luaScript.get("doOnAllGameObjects").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.doOnAllGameObjects(arg0);
    }

    @Override
    public boolean reset() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.reset();
               boolean ret = luaScript.get("reset").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.reset();
    }

    @Override
    protected void spendConstant(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendConstant").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.spendConstant((float) a0);
               luaScript.get("spendConstant").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spendConstant(arg0);
    }

    @Override
    public int drRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("drRoll").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.drRoll();
               int ret = luaScript.get("drRoll").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.drRoll();
    }

    @Override
    protected int randomDestination() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.randomDestination();
               int ret = luaScript.get("randomDestination").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomDestination();
    }

    @Override
    protected void tellDialog() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("tellDialog").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.tellDialog();
               luaScript.get("tellDialog").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.tellDialog();
    }

    @Override
    public boolean isFlying() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isFlying").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.isFlying();
               boolean ret = luaScript.get("isFlying").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isFlying();
    }

    @Override
    public boolean[] modPassable(boolean[] arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("modPassable").isnil()) {
            try {
                MethodOverride.A1<boolean[]> superMethod = (a0) -> super.modPassable((boolean[]) a0);
               boolean[] ret = (boolean[]) luaScript.get("modPassable").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.modPassable(arg0);
    }

    @Override
    public void damage(int arg0, Object arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damage").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.damage((int) a0, a1);
               luaScript.get("damage").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.damage(arg0, arg1);
    }

    @Override
    public void restoreEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreEnemy").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.restoreEnemy();
               luaScript.get("restoreEnemy").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.restoreEnemy();
    }

    @Override
    protected boolean getFurther(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getFurther").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.getFurther((int) a0);
               boolean ret = luaScript.get("getFurther").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getFurther(arg0);
    }

    @Override
    public void onZapComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onZapComplete").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onZapComplete();
               luaScript.get("onZapComplete").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onZapComplete();
    }

    @Override
    protected boolean cellIsPathable(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cellIsPathable").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.cellIsPathable((int) a0);
               boolean ret = luaScript.get("cellIsPathable").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.cellIsPathable(arg0);
    }

    @Override
    public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("convertLootToRandomItemData").isnil()) {
            try {
                MethodOverride.A0<ItemsWithChanceDistrComp.RandomItemData> superMethod = () -> super.convertLootToRandomItemData();
               ItemsWithChanceDistrComp.RandomItemData ret = (ItemsWithChanceDistrComp.RandomItemData) luaScript.get("convertLootToRandomItemData").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.convertLootToRandomItemData();
    }

    @Override
    public int attackSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackSkill").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.attackSkill((Char) a0);
               int ret = luaScript.get("attackSkill").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackSkill(arg0);
    }

    @Override
    public boolean attack(Char arg0, float arg1, float arg2, float arg3) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attack").isnil()) {
            try {
                MethodOverride.A4<Boolean> superMethod = (a0, a1, a2, a3) -> super.attack((Char) a0, (float) a1, (float) a2, (float) a3);
               boolean ret = luaScript.get("attack").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1), LuaValue.valueOf(arg2), LuaValue.valueOf(arg3)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attack(arg0, arg1, arg2, arg3);
    }

    @Override
    public CharSprite sprite() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("sprite").isnil()) {
            try {
                MethodOverride.A0<CharSprite> superMethod = () -> super.sprite();
               CharSprite ret = (CharSprite) luaScript.get("sprite").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.sprite();
    }

    @Override
    protected Item createLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createLoot").isnil()) {
            try {
                MethodOverride.A0<Item> superMethod = () -> super.createLoot();
               Item ret = (Item) luaScript.get("createLoot").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createLoot();
    }

    @Override
    public void updateSpriteVisibility() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteVisibility").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.updateSpriteVisibility();
               luaScript.get("updateSpriteVisibility").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateSpriteVisibility();
    }

    @Override
    protected boolean getCloser(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCloser").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.getCloser((int) a0);
               boolean ret = luaScript.get("getCloser").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCloser(arg0);
    }

    @Override
    public String getCustomName() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomName").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.getCustomName();
               String ret = luaScript.get("getCustomName").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCustomName();
    }

    @Override
    public void setPlayerAlignment(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setPlayerAlignment").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setPlayerAlignment((int) a0);
               luaScript.get("setPlayerAlignment").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setPlayerAlignment(arg0);
    }

    @Override
    protected boolean moveSprite(int arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("moveSprite").isnil()) {
            try {
                MethodOverride.A2<Boolean> superMethod = (a0, a1) -> super.moveSprite((int) a0, (int) a1);
               boolean ret = luaScript.get("moveSprite").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.moveSprite(arg0, arg1);
    }

    @Override
    public int sparseArrayKey() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("sparseArrayKey").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.sparseArrayKey();
               int ret = luaScript.get("sparseArrayKey").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.sparseArrayKey();
    }

    @Override
    public String getCustomDesc() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomDesc").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.getCustomDesc();
               String ret = luaScript.get("getCustomDesc").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCustomDesc();
    }

    @Override
    public DirectableAlly getDirectableAlly() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getDirectableAlly").isnil()) {
            try {
                MethodOverride.A0<DirectableAlly> superMethod = () -> super.getDirectableAlly();
               DirectableAlly ret = (DirectableAlly) luaScript.get("getDirectableAlly").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getDirectableAlly();
    }

    @Override
    protected void diactivate() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("diactivate").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.diactivate();
               luaScript.get("diactivate").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.diactivate();
    }

    @Override
    public void aggro(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("aggro").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.aggro((Char) a0);
               luaScript.get("aggro").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.aggro(arg0);
    }

    @Override
    public void onMotionComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMotionComplete").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onMotionComplete();
               luaScript.get("onMotionComplete").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onMotionComplete();
    }

    @Override
    protected boolean isNearYog() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isNearYog").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.isNearYog();
               boolean ret = luaScript.get("isNearYog").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isNearYog();
    }

    @Override
    public void updateSpriteState() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteState").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.updateSpriteState();
               luaScript.get("updateSpriteState").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateSpriteState();
    }

    @Override
    protected synchronized void onRemove() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onRemove").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.onRemove();
               luaScript.get("onRemove").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onRemove();
    }

    @Override
    public synchronized Buff buff(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buff").isnil()) {
            try {
                MethodOverride.A1<Buff> superMethod = (a0) -> super.buff((Class) a0);
               Buff ret = (Buff) luaScript.get("buff").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.buff(arg0);
    }

    @Override
    public void die(Object arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("die").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.die(a0);
               luaScript.get("die").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.die(arg0);
    }

    @Override
    public boolean blockSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("blockSound").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.blockSound((float) a0);
               boolean ret = luaScript.get("blockSound").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.blockSound(arg0);
    }

    @Override
    public synchronized boolean isCharmedBy(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isCharmedBy").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isCharmedBy((Char) a0);
               boolean ret = luaScript.get("isCharmedBy").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isCharmedBy(arg0);
    }

    @Override
    public void setCustomName(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomName").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setCustomName((String) a0);
               luaScript.get("setCustomName").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCustomName(arg0);
    }

    @Override
    public List createActualLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createActualLoot").isnil()) {
            try {
                MethodOverride.A0<List> superMethod = () -> super.createActualLoot();
               List ret = (List) luaScript.get("createActualLoot").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createActualLoot();
    }

    @Override
    public void clearTime() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearTime").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.clearTime();
               luaScript.get("clearTime").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.clearTime();
    }

    @Override
    protected void doDropLoot(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doDropLoot").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.doDropLoot((Item) a0);
               luaScript.get("doDropLoot").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.doDropLoot(arg0);
    }

    @Override
    public boolean isActive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isActive").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.isActive();
               boolean ret = luaScript.get("isActive").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isActive();
    }

    @Override
    protected boolean act() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("act").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.act();
               boolean ret = luaScript.get("act").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.act();
    }

    @Override
    public void onMapSizeChange(IntFunction arg0, BiPredicate arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMapSizeChange").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.onMapSizeChange((IntFunction) a0, (BiPredicate) a1);
               luaScript.get("onMapSizeChange").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onMapSizeChange(arg0, arg1);
    }

    @Override
    public void hitSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("hitSound").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.hitSound((float) a0);
               luaScript.get("hitSound").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.hitSound(arg0);
    }

    @Override
    public void spendToWhole() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendToWhole").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.spendToWhole();
               luaScript.get("spendToWhole").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spendToWhole();
    }

    @Override
    public void restoreCurrentZone(Level arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreCurrentZone").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.restoreCurrentZone((Level) a0);
               luaScript.get("restoreCurrentZone").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.restoreCurrentZone(arg0);
    }

    @Override
    public String info() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("info").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.info();
               String ret = luaScript.get("info").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.info();
    }

    @Override
    public boolean add(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("add").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.add((Buff) a0);
               boolean ret = luaScript.get("add").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.add(arg0);
    }

    @Override
    public boolean canInteract(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canInteract").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.canInteract((Char) a0);
               boolean ret = luaScript.get("canInteract").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canInteract(arg0);
    }

    @Override
    public void increaseLimitedDropCount(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("increaseLimitedDropCount").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.increaseLimitedDropCount((Item) a0);
               luaScript.get("increaseLimitedDropCount").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.increaseLimitedDropCount(arg0);
    }

    @Override
    public float resist(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("resist").isnil()) {
            try {
                MethodOverride.A1<Float> superMethod = (a0) -> super.resist((Class) a0);
               float ret = luaScript.get("resist").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.resist(arg0);
    }

    @Override
    public int attackProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackProc").isnil()) {
            try {
                MethodOverride.A2<Integer> superMethod = (a0, a1) -> super.attackProc((Char) a0, (int) a1);
               int ret = luaScript.get("attackProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackProc(arg0, arg1);
    }

    @Override
    public boolean isAlive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isAlive").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.isAlive();
               boolean ret = luaScript.get("isAlive").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isAlive();
    }

    @Override
    public void setCustomDesc(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomDesc").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setCustomDesc((String) a0);
               luaScript.get("setCustomDesc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCustomDesc(arg0);
    }

    @Override
    public void yell(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("yell").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.yell((String) a0);
               luaScript.get("yell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.yell(arg0);
    }

    @Override
    public String name() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("name").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.name();
               String ret = luaScript.get("name").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.name();
    }

    @Override
    public int damageRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damageRoll").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.damageRoll();
               int ret = luaScript.get("damageRoll").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.damageRoll();
    }

    @Override
    protected void postpone(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("postpone").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.postpone((float) a0);
               luaScript.get("postpone").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.postpone(arg0);
    }

    @Override
    public boolean isImmune(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isImmune").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isImmune((Class) a0);
               boolean ret = luaScript.get("isImmune").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isImmune(arg0);
    }

    @Override
    public void next() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("next").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.next();
               luaScript.get("next").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.next();
    }

    @Override
    public void setDurationForFlavourBuff(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setDurationForFlavourBuff").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setDurationForFlavourBuff((int) a0);
               luaScript.get("setDurationForFlavourBuff").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setDurationForFlavourBuff(arg0);
    }

    @Override
    public int distance(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("distance").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.distance((Char) a0);
               int ret = luaScript.get("distance").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.distance(arg0);
    }

    @Override
    public String description() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("description").isnil()) {
            try {
                MethodOverride.A0<String> superMethod = () -> super.description();
               String ret = luaScript.get("description").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.description();
    }

    @Override
    public boolean interact(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("interact").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.interact((Char) a0);
               boolean ret = luaScript.get("interact").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.interact(arg0);
    }

    @Override
    public float attackDelay() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackDelay").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.attackDelay();
               float ret = luaScript.get("attackDelay").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackDelay();
    }

    @Override
    public boolean remove(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("remove").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.remove((Buff) a0);
               boolean ret = luaScript.get("remove").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.remove(arg0);
    }

    @Override
    public float speed() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("speed").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.speed();
               float ret = luaScript.get("speed").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.speed();
    }

    @Override
    public boolean surprisedBy(Char arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("surprisedBy").isnil()) {
            try {
                MethodOverride.A2<Boolean> superMethod = (a0, a1) -> super.surprisedBy((Char) a0, (boolean) a1);
               boolean ret = luaScript.get("surprisedBy").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.surprisedBy(arg0, arg1);
    }

    @Override
    protected void spend(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spend").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.spend((float) a0);
               luaScript.get("spend").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spend(arg0);
    }

    @Override
    public void clearEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearEnemy").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.clearEnemy();
               luaScript.get("clearEnemy").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.clearEnemy();
    }

    @Override
    public int defenseSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseSkill").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.defenseSkill((Char) a0);
               int ret = luaScript.get("defenseSkill").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseSkill(arg0);
    }

    @Override
    public void notice() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("notice").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.notice();
               luaScript.get("notice").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.notice();
    }

    @Override
    public void move(int arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("move").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.move((int) a0, (boolean) a1);
               luaScript.get("move").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.move(arg0, arg1);
    }

    @Override
    protected void playBossMusic(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("playBossMusic").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.playBossMusic((String) a0);
               luaScript.get("playBossMusic").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playBossMusic(arg0);
    }

    @Override
    public void dieOnLand() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("dieOnLand").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.dieOnLand();
               luaScript.get("dieOnLand").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.dieOnLand();
    }

    @Override
    public boolean isInvulnerable(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isInvulnerable").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isInvulnerable((Class) a0);
               boolean ret = luaScript.get("isInvulnerable").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isInvulnerable(arg0);
    }

    @Override
    public void addBossProperty() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("addBossProperty").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.addBossProperty();
               luaScript.get("addBossProperty").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.addBossProperty();
    }

    @Override
    public void beckon(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("beckon").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.beckon((int) a0);
               luaScript.get("beckon").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.beckon(arg0);
    }

    @Override
    public boolean areStatsEqual(Mob arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("areStatsEqual").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.areStatsEqual((Mob) a0);
               boolean ret = luaScript.get("areStatsEqual").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.areStatsEqual(arg0);
    }

    @Override
    public boolean canSurpriseAttack() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canSurpriseAttack").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.canSurpriseAttack();
               boolean ret = luaScript.get("canSurpriseAttack").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canSurpriseAttack();
    }

    @Override
    public int shielding() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shielding").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.shielding();
               int ret = luaScript.get("shielding").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.shielding();
    }

    @Override
    public void rollToDropLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("rollToDropLoot").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.rollToDropLoot();
               luaScript.get("rollToDropLoot").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.rollToDropLoot();
    }

    @Override
    public int defenseProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseProc").isnil()) {
            try {
                MethodOverride.A2<Integer> superMethod = (a0, a1) -> super.defenseProc((Char) a0, (int) a1);
               int ret = luaScript.get("defenseProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseProc(arg0, arg1);
    }

    @Override
    public boolean avoidsHazards() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("avoidsHazards").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.avoidsHazards();
               boolean ret = luaScript.get("avoidsHazards").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.avoidsHazards();
    }

    @Override
    protected boolean doAttack(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doAttack").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.doAttack((Char) a0);
               boolean ret = luaScript.get("doAttack").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.doAttack(arg0);
    }

    @Override
    public HashSet properties() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("properties").isnil()) {
            try {
                MethodOverride.A0<HashSet> superMethod = () -> super.properties();
               HashSet ret = (HashSet) luaScript.get("properties").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.properties();
    }
}