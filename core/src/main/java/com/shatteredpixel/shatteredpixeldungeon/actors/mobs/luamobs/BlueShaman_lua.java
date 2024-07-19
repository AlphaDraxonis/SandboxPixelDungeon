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

public class BlueShaman_lua extends Shaman.BlueShaman implements LuaMob {

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
                boolean ret = luaScript.get("canAttack").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canAttack(arg0);
    }

    public boolean super_canAttack(Char arg0) {
        return super.canAttack(arg0);
    }

    @Override
    public boolean heroShouldInteract() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("heroShouldInteract").isnil()) {
            try {
                boolean ret = luaScript.get("heroShouldInteract").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.heroShouldInteract();
    }

    public boolean super_heroShouldInteract() {
        return super.heroShouldInteract();
    }

    @Override
    public void throwItems() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("throwItems").isnil()) {
            try {
                luaScript.get("throwItems").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.throwItems();
    }

    public void super_throwItems() {
        super.throwItems();
    }

    @Override
    public boolean isTargeting(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isTargeting").isnil()) {
            try {
                boolean ret = luaScript.get("isTargeting").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isTargeting(arg0);
    }

    public boolean super_isTargeting(Char arg0) {
        return super.isTargeting(arg0);
    }

    @Override
    protected void onAdd() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAdd").isnil()) {
            try {
                luaScript.get("onAdd").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onAdd();
    }

    public void super_onAdd() {
        super.onAdd();
    }

    @Override
    public void onOperateComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onOperateComplete").isnil()) {
            try {
                luaScript.get("onOperateComplete").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onOperateComplete();
    }

    public void super_onOperateComplete() {
        super.onOperateComplete();
    }

    @Override
    public float spawningWeight() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spawningWeight").isnil()) {
            try {
                float ret = luaScript.get("spawningWeight").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.spawningWeight();
    }

    public float super_spawningWeight() {
        return super.spawningWeight();
    }

    @Override
    public void setFlying(boolean arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setFlying").isnil()) {
            try {
                luaScript.get("setFlying").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setFlying(arg0);
    }

    public void super_setFlying(boolean arg0) {
        super.setFlying(arg0);
    }

    @Override
    public GameObject.ModifyResult initRandoms() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("initRandoms").isnil()) {
            try {
                GameObject.ModifyResult ret = (GameObject.ModifyResult) luaScript.get("initRandoms").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.initRandoms();
    }

    public GameObject.ModifyResult super_initRandoms() {
        return super.initRandoms();
    }

    @Override
    public float stealth() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("stealth").isnil()) {
            try {
                float ret = luaScript.get("stealth").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.stealth();
    }

    public float super_stealth() {
        return super.stealth();
    }

    @Override
    public float cooldown() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cooldown").isnil()) {
            try {
                float ret = luaScript.get("cooldown").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.cooldown();
    }

    public float super_cooldown() {
        return super.cooldown();
    }

    @Override
    public synchronized LinkedHashSet buffs() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buffs").isnil()) {
            try {
                LinkedHashSet ret = (LinkedHashSet) luaScript.get("buffs").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.buffs();
    }

    public LinkedHashSet super_buffs() {
        return super.buffs();
    }

    @Override
    public int id() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("id").isnil()) {
            try {
                int ret = luaScript.get("id").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.id();
    }

    public int super_id() {
        return super.id();
    }

    @Override
    public void onAttackComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onAttackComplete").isnil()) {
            try {
                luaScript.get("onAttackComplete").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onAttackComplete();
    }

    public void super_onAttackComplete() {
        super.onAttackComplete();
    }

    @Override
    public boolean shouldSpriteBeVisible() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shouldSpriteBeVisible").isnil()) {
            try {
                boolean ret = luaScript.get("shouldSpriteBeVisible").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.shouldSpriteBeVisible();
    }

    public boolean super_shouldSpriteBeVisible() {
        return super.shouldSpriteBeVisible();
    }

    @Override
    public void timeToNow() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("timeToNow").isnil()) {
            try {
                luaScript.get("timeToNow").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.timeToNow();
    }

    public void super_timeToNow() {
        super.timeToNow();
    }

    @Override
    public void playZapAnim(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("playZapAnim").isnil()) {
            try {
                luaScript.get("playZapAnim").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playZapAnim(arg0);
    }

    public void super_playZapAnim(int arg0) {
        super.playZapAnim(arg0);
    }

    @Override
    protected Char chooseEnemyImpl() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("chooseEnemyImpl").isnil()) {
            try {
                Char ret = (Char) luaScript.get("chooseEnemyImpl").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.chooseEnemyImpl();
    }

    public Char super_chooseEnemyImpl() {
        return super.chooseEnemyImpl();
    }

    @Override
    public float lootChance() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("lootChance").isnil()) {
            try {
                float ret = luaScript.get("lootChance").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.lootChance();
    }

    public float super_lootChance() {
        return super.lootChance();
    }

    @Override
    public void zap() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("zap").isnil()) {
            try {
                luaScript.get("zap").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.zap();
    }

    public void super_zap() {
        super.zap();
    }

    @Override
    public void destroy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            try {
                luaScript.get("destroy").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.destroy();
    }

    public void super_destroy() {
        super.destroy();
    }

    @Override
    public String defenseVerb() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseVerb").isnil()) {
            try {
                String ret = luaScript.get("defenseVerb").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseVerb();
    }

    public String super_defenseVerb() {
        return super.defenseVerb();
    }

    @Override
    public boolean doOnAllGameObjects(Function arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doOnAllGameObjects").isnil()) {
            try {
                boolean ret = luaScript.get("doOnAllGameObjects").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.doOnAllGameObjects(arg0);
    }

    public boolean super_doOnAllGameObjects(Function arg0) {
        return super.doOnAllGameObjects(arg0);
    }

    @Override
    protected void debuff(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("debuff").isnil()) {
            try {
                luaScript.get("debuff").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.debuff(arg0);
    }

    public void super_debuff(Char arg0) {
        super.debuff(arg0);
    }

    @Override
    public boolean reset() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            try {
                boolean ret = luaScript.get("reset").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.reset();
    }

    public boolean super_reset() {
        return super.reset();
    }

    @Override
    protected void spendConstant(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendConstant").isnil()) {
            try {
                luaScript.get("spendConstant").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spendConstant(arg0);
    }

    public void super_spendConstant(float arg0) {
        super.spendConstant(arg0);
    }

    @Override
    public int drRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("drRoll").isnil()) {
            try {
                int ret = luaScript.get("drRoll").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.drRoll();
    }

    public int super_drRoll() {
        return super.drRoll();
    }

    @Override
    protected int randomDestination() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            try {
                int ret = luaScript.get("randomDestination").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomDestination();
    }

    public int super_randomDestination() {
        return super.randomDestination();
    }

    @Override
    protected void tellDialog() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("tellDialog").isnil()) {
            try {
                luaScript.get("tellDialog").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.tellDialog();
    }

    public void super_tellDialog() {
        super.tellDialog();
    }

    @Override
    public boolean isFlying() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isFlying").isnil()) {
            try {
                boolean ret = luaScript.get("isFlying").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isFlying();
    }

    public boolean super_isFlying() {
        return super.isFlying();
    }

    @Override
    public boolean[] modPassable(boolean[] arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("modPassable").isnil()) {
            try {
                boolean[] ret = (boolean[]) luaScript.get("modPassable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.modPassable(arg0);
    }

    public boolean[] super_modPassable(boolean[] arg0) {
        return super.modPassable(arg0);
    }

    @Override
    public void damage(int arg0, Object arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damage").isnil()) {
            try {
                luaScript.get("damage").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.damage(arg0, arg1);
    }

    public void super_damage(int arg0, Object arg1) {
        super.damage(arg0, arg1);
    }

    @Override
    public void restoreEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreEnemy").isnil()) {
            try {
                luaScript.get("restoreEnemy").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.restoreEnemy();
    }

    public void super_restoreEnemy() {
        super.restoreEnemy();
    }

    @Override
    protected boolean getFurther(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getFurther").isnil()) {
            try {
                boolean ret = luaScript.get("getFurther").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getFurther(arg0);
    }

    public boolean super_getFurther(int arg0) {
        return super.getFurther(arg0);
    }

    @Override
    public void onZapComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onZapComplete").isnil()) {
            try {
                luaScript.get("onZapComplete").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onZapComplete();
    }

    public void super_onZapComplete() {
        super.onZapComplete();
    }

    @Override
    protected boolean cellIsPathable(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("cellIsPathable").isnil()) {
            try {
                boolean ret = luaScript.get("cellIsPathable").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.cellIsPathable(arg0);
    }

    public boolean super_cellIsPathable(int arg0) {
        return super.cellIsPathable(arg0);
    }

    @Override
    public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("convertLootToRandomItemData").isnil()) {
            try {
                ItemsWithChanceDistrComp.RandomItemData ret = (ItemsWithChanceDistrComp.RandomItemData) luaScript.get("convertLootToRandomItemData").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.convertLootToRandomItemData();
    }

    public ItemsWithChanceDistrComp.RandomItemData super_convertLootToRandomItemData() {
        return super.convertLootToRandomItemData();
    }

    @Override
    public int attackSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackSkill").isnil()) {
            try {
                int ret = luaScript.get("attackSkill").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackSkill(arg0);
    }

    public int super_attackSkill(Char arg0) {
        return super.attackSkill(arg0);
    }

    @Override
    public boolean attack(Char arg0, float arg1, float arg2, float arg3) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attack").isnil()) {
            try {
                boolean ret = luaScript.get("attack").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1), LuaValue.valueOf(arg2), LuaValue.valueOf(arg3)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attack(arg0, arg1, arg2, arg3);
    }

    public boolean super_attack(Char arg0, float arg1, float arg2, float arg3) {
        return super.attack(arg0, arg1, arg2, arg3);
    }

    @Override
    public CharSprite sprite() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("sprite").isnil()) {
            try {
                CharSprite ret = (CharSprite) luaScript.get("sprite").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.sprite();
    }

    public CharSprite super_sprite() {
        return super.sprite();
    }

    @Override
    protected Item createLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createLoot").isnil()) {
            try {
                Item ret = (Item) luaScript.get("createLoot").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createLoot();
    }

    public Item super_createLoot() {
        return super.createLoot();
    }

    @Override
    public void updateSpriteVisibility() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteVisibility").isnil()) {
            try {
                luaScript.get("updateSpriteVisibility").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateSpriteVisibility();
    }

    public void super_updateSpriteVisibility() {
        super.updateSpriteVisibility();
    }

    @Override
    protected boolean getCloser(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCloser").isnil()) {
            try {
                boolean ret = luaScript.get("getCloser").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCloser(arg0);
    }

    public boolean super_getCloser(int arg0) {
        return super.getCloser(arg0);
    }

    @Override
    public String getCustomName() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomName").isnil()) {
            try {
                String ret = luaScript.get("getCustomName").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCustomName();
    }

    public String super_getCustomName() {
        return super.getCustomName();
    }

    @Override
    public void setPlayerAlignment(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setPlayerAlignment").isnil()) {
            try {
                luaScript.get("setPlayerAlignment").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setPlayerAlignment(arg0);
    }

    public void super_setPlayerAlignment(int arg0) {
        super.setPlayerAlignment(arg0);
    }

    @Override
    protected boolean moveSprite(int arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("moveSprite").isnil()) {
            try {
                boolean ret = luaScript.get("moveSprite").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.moveSprite(arg0, arg1);
    }

    public boolean super_moveSprite(int arg0, int arg1) {
        return super.moveSprite(arg0, arg1);
    }

    @Override
    public int sparseArrayKey() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("sparseArrayKey").isnil()) {
            try {
                int ret = luaScript.get("sparseArrayKey").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.sparseArrayKey();
    }

    public int super_sparseArrayKey() {
        return super.sparseArrayKey();
    }

    @Override
    public String getCustomDesc() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getCustomDesc").isnil()) {
            try {
                String ret = luaScript.get("getCustomDesc").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCustomDesc();
    }

    public String super_getCustomDesc() {
        return super.getCustomDesc();
    }

    @Override
    public DirectableAlly getDirectableAlly() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("getDirectableAlly").isnil()) {
            try {
                DirectableAlly ret = (DirectableAlly) luaScript.get("getDirectableAlly").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getDirectableAlly();
    }

    public DirectableAlly super_getDirectableAlly() {
        return super.getDirectableAlly();
    }

    @Override
    protected void diactivate() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("diactivate").isnil()) {
            try {
                luaScript.get("diactivate").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.diactivate();
    }

    public void super_diactivate() {
        super.diactivate();
    }

    @Override
    public void aggro(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("aggro").isnil()) {
            try {
                luaScript.get("aggro").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.aggro(arg0);
    }

    public void super_aggro(Char arg0) {
        super.aggro(arg0);
    }

    @Override
    public void onMotionComplete() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMotionComplete").isnil()) {
            try {
                luaScript.get("onMotionComplete").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onMotionComplete();
    }

    public void super_onMotionComplete() {
        super.onMotionComplete();
    }

    @Override
    public void updateSpriteState() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("updateSpriteState").isnil()) {
            try {
                luaScript.get("updateSpriteState").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateSpriteState();
    }

    public void super_updateSpriteState() {
        super.updateSpriteState();
    }

    @Override
    protected synchronized void onRemove() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onRemove").isnil()) {
            try {
                luaScript.get("onRemove").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onRemove();
    }

    public void super_onRemove() {
        super.onRemove();
    }

    @Override
    public synchronized Buff buff(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("buff").isnil()) {
            try {
                Buff ret = (Buff) luaScript.get("buff").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.buff(arg0);
    }

    public Buff super_buff(Class arg0) {
        return super.buff(arg0);
    }

    @Override
    public void die(Object arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("die").isnil()) {
            try {
                luaScript.get("die").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.die(arg0);
    }

    public void super_die(Object arg0) {
        super.die(arg0);
    }

    @Override
    public boolean blockSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("blockSound").isnil()) {
            try {
                boolean ret = luaScript.get("blockSound").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.blockSound(arg0);
    }

    public boolean super_blockSound(float arg0) {
        return super.blockSound(arg0);
    }

    @Override
    public synchronized boolean isCharmedBy(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isCharmedBy").isnil()) {
            try {
                boolean ret = luaScript.get("isCharmedBy").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isCharmedBy(arg0);
    }

    public boolean super_isCharmedBy(Char arg0) {
        return super.isCharmedBy(arg0);
    }

    @Override
    public void setCustomName(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomName").isnil()) {
            try {
                luaScript.get("setCustomName").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCustomName(arg0);
    }

    public void super_setCustomName(String arg0) {
        super.setCustomName(arg0);
    }

    @Override
    public List createActualLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("createActualLoot").isnil()) {
            try {
                List ret = (List) luaScript.get("createActualLoot").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createActualLoot();
    }

    public List super_createActualLoot() {
        return super.createActualLoot();
    }

    @Override
    public void clearTime() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearTime").isnil()) {
            try {
                luaScript.get("clearTime").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.clearTime();
    }

    public void super_clearTime() {
        super.clearTime();
    }

    @Override
    protected void doDropLoot(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doDropLoot").isnil()) {
            try {
                luaScript.get("doDropLoot").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.doDropLoot(arg0);
    }

    public void super_doDropLoot(Item arg0) {
        super.doDropLoot(arg0);
    }

    @Override
    public boolean isActive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isActive").isnil()) {
            try {
                boolean ret = luaScript.get("isActive").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isActive();
    }

    public boolean super_isActive() {
        return super.isActive();
    }

    @Override
    protected boolean act() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("act").isnil()) {
            try {
                boolean ret = luaScript.get("act").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.act();
    }

    public boolean super_act() {
        return super.act();
    }

    @Override
    public void onMapSizeChange(IntFunction arg0, BiPredicate arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("onMapSizeChange").isnil()) {
            try {
                luaScript.get("onMapSizeChange").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onMapSizeChange(arg0, arg1);
    }

    public void super_onMapSizeChange(IntFunction arg0, BiPredicate arg1) {
        super.onMapSizeChange(arg0, arg1);
    }

    @Override
    public void hitSound(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("hitSound").isnil()) {
            try {
                luaScript.get("hitSound").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.hitSound(arg0);
    }

    public void super_hitSound(float arg0) {
        super.hitSound(arg0);
    }

    @Override
    public void spendToWhole() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spendToWhole").isnil()) {
            try {
                luaScript.get("spendToWhole").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spendToWhole();
    }

    public void super_spendToWhole() {
        super.spendToWhole();
    }

    @Override
    public void restoreCurrentZone(Level arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("restoreCurrentZone").isnil()) {
            try {
                luaScript.get("restoreCurrentZone").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.restoreCurrentZone(arg0);
    }

    public void super_restoreCurrentZone(Level arg0) {
        super.restoreCurrentZone(arg0);
    }

    @Override
    public String info() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("info").isnil()) {
            try {
                String ret = luaScript.get("info").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.info();
    }

    public String super_info() {
        return super.info();
    }

    @Override
    public boolean add(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("add").isnil()) {
            try {
                boolean ret = luaScript.get("add").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.add(arg0);
    }

    public boolean super_add(Buff arg0) {
        return super.add(arg0);
    }

    @Override
    public boolean canInteract(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canInteract").isnil()) {
            try {
                boolean ret = luaScript.get("canInteract").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canInteract(arg0);
    }

    public boolean super_canInteract(Char arg0) {
        return super.canInteract(arg0);
    }

    @Override
    public void increaseLimitedDropCount(Item arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("increaseLimitedDropCount").isnil()) {
            try {
                luaScript.get("increaseLimitedDropCount").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.increaseLimitedDropCount(arg0);
    }

    public void super_increaseLimitedDropCount(Item arg0) {
        super.increaseLimitedDropCount(arg0);
    }

    @Override
    public float resist(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("resist").isnil()) {
            try {
                float ret = luaScript.get("resist").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.resist(arg0);
    }

    public float super_resist(Class arg0) {
        return super.resist(arg0);
    }

    @Override
    public int attackProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackProc").isnil()) {
            try {
                int ret = luaScript.get("attackProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackProc(arg0, arg1);
    }

    public int super_attackProc(Char arg0, int arg1) {
        return super.attackProc(arg0, arg1);
    }

    @Override
    public boolean isAlive() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isAlive").isnil()) {
            try {
                boolean ret = luaScript.get("isAlive").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isAlive();
    }

    public boolean super_isAlive() {
        return super.isAlive();
    }

    @Override
    public void setCustomDesc(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setCustomDesc").isnil()) {
            try {
                luaScript.get("setCustomDesc").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCustomDesc(arg0);
    }

    public void super_setCustomDesc(String arg0) {
        super.setCustomDesc(arg0);
    }

    @Override
    public void yell(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("yell").isnil()) {
            try {
                luaScript.get("yell").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.yell(arg0);
    }

    public void super_yell(String arg0) {
        super.yell(arg0);
    }

    @Override
    public String name() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("name").isnil()) {
            try {
                String ret = luaScript.get("name").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.name();
    }

    public String super_name() {
        return super.name();
    }

    @Override
    public int damageRoll() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("damageRoll").isnil()) {
            try {
                int ret = luaScript.get("damageRoll").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.damageRoll();
    }

    public int super_damageRoll() {
        return super.damageRoll();
    }

    @Override
    protected void postpone(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("postpone").isnil()) {
            try {
                luaScript.get("postpone").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.postpone(arg0);
    }

    public void super_postpone(float arg0) {
        super.postpone(arg0);
    }

    @Override
    public boolean isImmune(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isImmune").isnil()) {
            try {
                boolean ret = luaScript.get("isImmune").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isImmune(arg0);
    }

    public boolean super_isImmune(Class arg0) {
        return super.isImmune(arg0);
    }

    @Override
    public void next() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("next").isnil()) {
            try {
                luaScript.get("next").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.next();
    }

    public void super_next() {
        super.next();
    }

    @Override
    public void setDurationForFlavourBuff(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("setDurationForFlavourBuff").isnil()) {
            try {
                luaScript.get("setDurationForFlavourBuff").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setDurationForFlavourBuff(arg0);
    }

    public void super_setDurationForFlavourBuff(int arg0) {
        super.setDurationForFlavourBuff(arg0);
    }

    @Override
    public int distance(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("distance").isnil()) {
            try {
                int ret = luaScript.get("distance").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.distance(arg0);
    }

    public int super_distance(Char arg0) {
        return super.distance(arg0);
    }

    @Override
    public String description() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("description").isnil()) {
            try {
                String ret = luaScript.get("description").call(CoerceJavaToLua.coerce(this), vars).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.description();
    }

    public String super_description() {
        return super.description();
    }

    @Override
    public boolean interact(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("interact").isnil()) {
            try {
                boolean ret = luaScript.get("interact").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.interact(arg0);
    }

    public boolean super_interact(Char arg0) {
        return super.interact(arg0);
    }

    @Override
    public float attackDelay() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("attackDelay").isnil()) {
            try {
                float ret = luaScript.get("attackDelay").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.attackDelay();
    }

    public float super_attackDelay() {
        return super.attackDelay();
    }

    @Override
    public boolean remove(Buff arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("remove").isnil()) {
            try {
                boolean ret = luaScript.get("remove").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.remove(arg0);
    }

    public boolean super_remove(Buff arg0) {
        return super.remove(arg0);
    }

    @Override
    public float speed() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("speed").isnil()) {
            try {
                float ret = luaScript.get("speed").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.speed();
    }

    public float super_speed() {
        return super.speed();
    }

    @Override
    public boolean surprisedBy(Char arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("surprisedBy").isnil()) {
            try {
                boolean ret = luaScript.get("surprisedBy").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.surprisedBy(arg0, arg1);
    }

    public boolean super_surprisedBy(Char arg0, boolean arg1) {
        return super.surprisedBy(arg0, arg1);
    }

    @Override
    public void spend(float arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("spend").isnil()) {
            try {
                luaScript.get("spend").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.spend(arg0);
    }

    public void super_spend(float arg0) {
        super.spend(arg0);
    }

    @Override
    public void clearEnemy() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("clearEnemy").isnil()) {
            try {
                luaScript.get("clearEnemy").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.clearEnemy();
    }

    public void super_clearEnemy() {
        super.clearEnemy();
    }

    @Override
    public int defenseSkill(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseSkill").isnil()) {
            try {
                int ret = luaScript.get("defenseSkill").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseSkill(arg0);
    }

    public int super_defenseSkill(Char arg0) {
        return super.defenseSkill(arg0);
    }

    @Override
    public void notice() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("notice").isnil()) {
            try {
                luaScript.get("notice").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.notice();
    }

    public void super_notice() {
        super.notice();
    }

    @Override
    public void move(int arg0, boolean arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("move").isnil()) {
            try {
                luaScript.get("move").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.move(arg0, arg1);
    }

    public void super_move(int arg0, boolean arg1) {
        super.move(arg0, arg1);
    }

    @Override
    protected void playBossMusic(String arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("playBossMusic").isnil()) {
            try {
                luaScript.get("playBossMusic").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playBossMusic(arg0);
    }

    public void super_playBossMusic(String arg0) {
        super.playBossMusic(arg0);
    }

    @Override
    public void dieOnLand() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("dieOnLand").isnil()) {
            try {
                luaScript.get("dieOnLand").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.dieOnLand();
    }

    public void super_dieOnLand() {
        super.dieOnLand();
    }

    @Override
    public boolean isInvulnerable(Class arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("isInvulnerable").isnil()) {
            try {
                boolean ret = luaScript.get("isInvulnerable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isInvulnerable(arg0);
    }

    public boolean super_isInvulnerable(Class arg0) {
        return super.isInvulnerable(arg0);
    }

    @Override
    public void addBossProperty() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("addBossProperty").isnil()) {
            try {
                luaScript.get("addBossProperty").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.addBossProperty();
    }

    public void super_addBossProperty() {
        super.addBossProperty();
    }

    @Override
    public void beckon(int arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("beckon").isnil()) {
            try {
                luaScript.get("beckon").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.beckon(arg0);
    }

    public void super_beckon(int arg0) {
        super.beckon(arg0);
    }

    @Override
    public boolean areStatsEqual(Mob arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("areStatsEqual").isnil()) {
            try {
                boolean ret = luaScript.get("areStatsEqual").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.areStatsEqual(arg0);
    }

    public boolean super_areStatsEqual(Mob arg0) {
        return super.areStatsEqual(arg0);
    }

    @Override
    public boolean canSurpriseAttack() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("canSurpriseAttack").isnil()) {
            try {
                boolean ret = luaScript.get("canSurpriseAttack").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.canSurpriseAttack();
    }

    public boolean super_canSurpriseAttack() {
        return super.canSurpriseAttack();
    }

    @Override
    public int shielding() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("shielding").isnil()) {
            try {
                int ret = luaScript.get("shielding").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.shielding();
    }

    public int super_shielding() {
        return super.shielding();
    }

    @Override
    public void rollToDropLoot() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("rollToDropLoot").isnil()) {
            try {
                luaScript.get("rollToDropLoot").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.rollToDropLoot();
    }

    public void super_rollToDropLoot() {
        super.rollToDropLoot();
    }

    @Override
    public int defenseProc(Char arg0, int arg1) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("defenseProc").isnil()) {
            try {
                int ret = luaScript.get("defenseProc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.defenseProc(arg0, arg1);
    }

    public int super_defenseProc(Char arg0, int arg1) {
        return super.defenseProc(arg0, arg1);
    }

    @Override
    public boolean avoidsHazards() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("avoidsHazards").isnil()) {
            try {
                boolean ret = luaScript.get("avoidsHazards").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.avoidsHazards();
    }

    public boolean super_avoidsHazards() {
        return super.avoidsHazards();
    }

    @Override
    protected boolean doAttack(Char arg0) {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("doAttack").isnil()) {
            try {
                boolean ret = luaScript.get("doAttack").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.doAttack(arg0);
    }

    public boolean super_doAttack(Char arg0) {
        return super.doAttack(arg0);
    }

    @Override
    public HashSet properties() {
        LuaValue luaScript = CustomObject.getScript(identifier);
        if (luaScript != null && !luaScript.get("properties").isnil()) {
            try {
                HashSet ret = (HashSet) luaScript.get("properties").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.properties();
    }

    public HashSet super_properties() {
        return super.properties();
    }
}