package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.List;

public class DungeonScript_lua extends DungeonScript implements LuaClass {


    private LuaValue vars;

    @Override
    public void setIdentifier(int identifier) {
    }

    @Override
    public int getIdentifier() {
        return -1;
    }

    @Override
    public LuaClass newInstance() {
        return new DungeonScript_lua();
    }

    public void storeInBundle(Bundle bundle) {
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
    }


    @Override
    public void executeItem(Item arg0, Hero arg1, String arg2, DungeonScript.Executer arg3) {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("executeItem").isnil()) {
            try {
                luaScript.get("executeItem").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1), CoerceJavaToLua.coerce(arg2), CoerceJavaToLua.coerce(arg3)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.executeItem(arg0, arg1, arg2, arg3);
    }

    public void super_executeItem(Item arg0, Hero arg1, String arg2, DungeonScript.Executer arg3) {
        super.executeItem(arg0, arg1, arg2, arg3);
    }

    @Override
    public List getMobRotation(int arg0) {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("getMobRotation").isnil()) {
            try {
                List ret = (List) luaScript.get("getMobRotation").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getMobRotation(arg0);
    }

    public List super_getMobRotation(int arg0) {
        return super.getMobRotation(arg0);
    }

    @Override
    public boolean isItemBlocked(Item arg0) {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("isItemBlocked").isnil()) {
            try {
                boolean ret = luaScript.get("isItemBlocked").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isItemBlocked(arg0);
    }

    public boolean super_isItemBlocked(Item arg0) {
        return super.isItemBlocked(arg0);
    }

    @Override
    public int onEarnXP(int arg0, Class arg1) {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("onEarnXP").isnil()) {
            try {
                int ret = luaScript.get("onEarnXP").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.onEarnXP(arg0, arg1);
    }

    public int super_onEarnXP(int arg0, Class arg1) {
        return super.onEarnXP(arg0, arg1);
    }

    @Override
    public boolean onItemCollected(Item arg0) {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("onItemCollected").isnil()) {
            try {
                boolean ret = luaScript.get("onItemCollected").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.onItemCollected(arg0);
    }

    public boolean super_onItemCollected(Item arg0) {
        return super.onItemCollected(arg0);
    }

    @Override
    public void onLevelUp() {
        LuaValue luaScript = getScript();
        if (luaScript != null && !luaScript.get("onLevelUp").isnil()) {
            try {
                luaScript.get("onLevelUp").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.onLevelUp();
    }

    public void super_onLevelUp() {
        super.onLevelUp();
    }
}