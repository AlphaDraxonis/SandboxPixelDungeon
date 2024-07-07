package com.shatteredpixel.shatteredpixeldungeon.levels.lualevels;

import com.shatteredpixel.shatteredpixeldungeon.actors.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.*;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.*;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.*;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import java.util.*;

public class CavesBossLevel_lua extends CavesBossLevel implements LuaLevel {

    private LuaTable vars;
    @Override
    public void setVars(LuaTable vars) {
        this.vars = vars;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (vars != null) {
            LuaManager.storeVarInBundle(bundle, vars, LuaClass.VARS);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, LuaClass.VARS);
        if (loaded != null && loaded.istable()) vars = loaded.checktable();
    }


    @Override
    protected boolean isValidSpawnCell(Char arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("isValidSpawnCell").isnil()) {
            try {
                MethodOverride.A2<Boolean> superMethod = (a0, a1) -> super.isValidSpawnCell((Char) a0, (int) a1);
               boolean ret = luaScript.get("isValidSpawnCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isValidSpawnCell(arg0, arg1);
    }

    @Override
    public void playSpecialMusic(String arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("playSpecialMusic").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.playSpecialMusic((String) a0, (int) a1);
               luaScript.get("playSpecialMusic").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playSpecialMusic(arg0, arg1);
    }

    @Override
    protected void updateMusic() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("updateMusic").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.updateMusic();
               luaScript.get("updateMusic").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateMusic();
    }

    @Override
    public int getCoinDoorCost(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getCoinDoorCost").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.getCoinDoorCost((int) a0);
               int ret = luaScript.get("getCoinDoorCost").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCoinDoorCost(arg0);
    }

    @Override
    public boolean isLevelExplored(String arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("isLevelExplored").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isLevelExplored((String) a0);
               boolean ret = luaScript.get("isLevelExplored").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isLevelExplored(arg0);
    }

    @Override
    public void disarmTrap(int arg0, boolean arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("disarmTrap").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.disarmTrap((int) a0, (boolean) a1);
               luaScript.get("disarmTrap").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.disarmTrap(arg0, arg1);
    }

    @Override
    public void setPassableLater(int arg0, boolean arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setPassableLater").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.setPassableLater((int) a0, (boolean) a1);
               luaScript.get("setPassableLater").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setPassableLater(arg0, arg1);
    }

    @Override
    public void setTerrain(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setTerrain").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.setTerrain((int) a0, (int) a1);
               luaScript.get("setTerrain").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setTerrain(arg0, arg1);
    }

    @Override
    protected void createMobs() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createMobs").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.createMobs();
               luaScript.get("createMobs").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.createMobs();
    }

    @Override
    public void create() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("create").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.create();
               luaScript.get("create").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.create();
    }

    @Override
    public int tunnelTile() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tunnelTile").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.tunnelTile();
               int ret = luaScript.get("tunnelTile").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tunnelTile();
    }

    @Override
    protected void defaultActiveTransitionImpl(Hero arg0, LevelTransition arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("defaultActiveTransitionImpl").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.defaultActiveTransitionImpl((Hero) a0, (LevelTransition) a1);
               luaScript.get("defaultActiveTransitionImpl").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.defaultActiveTransitionImpl(arg0, arg1);
    }

    @Override
    public int entrance() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("entrance").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.entrance();
               int ret = luaScript.get("entrance").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.entrance();
    }

    @Override
    public boolean locked() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("locked").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.locked();
               boolean ret = luaScript.get("locked").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.locked();
    }

    @Override
    public boolean activateTransition(Hero arg0, LevelTransition arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("activateTransition").isnil()) {
            try {
                MethodOverride.A2<Boolean> superMethod = (a0, a1) -> super.activateTransition((Hero) a0, (LevelTransition) a1);
               boolean ret = luaScript.get("activateTransition").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.activateTransition(arg0, arg1);
    }

    @Override
    public void discover(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("discover").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.discover((int) a0);
               luaScript.get("discover").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.discover(arg0);
    }

    @Override
    public boolean spawnMob(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("spawnMob").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.spawnMob((int) a0);
               boolean ret = luaScript.get("spawnMob").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.spawnMob(arg0);
    }

    @Override
    public boolean[] getFlamable() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getFlamable").isnil()) {
            try {
                MethodOverride.A0<boolean[]> superMethod = () -> super.getFlamable();
               boolean[] ret = (boolean[]) luaScript.get("getFlamable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getFlamable();
    }

    @Override
    public boolean setCellToWater(boolean arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setCellToWater").isnil()) {
            try {
                MethodOverride.A2<Boolean> superMethod = (a0, a1) -> super.setCellToWater((boolean) a0, (int) a1);
               boolean ret = luaScript.get("setCellToWater").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.setCellToWater(arg0, arg1);
    }

    @Override
    public void destroy(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.destroy((int) a0);
               luaScript.get("destroy").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.destroy(arg0);
    }

    @Override
    public int fallCell(boolean arg0, String arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("fallCell").isnil()) {
            try {
                MethodOverride.A2<Integer> superMethod = (a0, a1) -> super.fallCell((boolean) a0, (String) a1);
               int ret = luaScript.get("fallCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.fallCell(arg0, arg1);
    }

    @Override
    public Pylon activatePylon(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("activatePylon").isnil()) {
            try {
                MethodOverride.A1<Pylon> superMethod = (a0) -> super.activatePylon((int) a0);
               Pylon ret = (Pylon) luaScript.get("activatePylon").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.activatePylon(arg0);
    }

    @Override
    public void initForPlay() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("initForPlay").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.initForPlay();
               luaScript.get("initForPlay").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.initForPlay();
    }

    @Override
    public String tileDesc(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tileDesc").isnil()) {
            try {
                MethodOverride.A2<String> superMethod = (a0, a1) -> super.tileDesc((int) a0, (int) a1);
               String ret = luaScript.get("tileDesc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tileDesc(arg0, arg1);
    }

    @Override
    public String tileName(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tileName").isnil()) {
            try {
                MethodOverride.A1<String> superMethod = (a0) -> super.tileName((int) a0);
               String ret = luaScript.get("tileName").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tileName(arg0);
    }

    @Override
    public int exit() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("exit").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.exit();
               int ret = luaScript.get("exit").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.exit();
    }

    @Override
    public void playLevelMusic() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("playLevelMusic").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.playLevelMusic();
               luaScript.get("playLevelMusic").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playLevelMusic();
    }

    @Override
    public Plant plant(Plant.Seed arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("plant").isnil()) {
            try {
                MethodOverride.A2<Plant> superMethod = (a0, a1) -> super.plant((Plant.Seed) a0, (int) a1);
               Plant ret = (Plant) luaScript.get("plant").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.plant(arg0, arg1);
    }

    @Override
    public void reset() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.reset();
               luaScript.get("reset").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.reset();
    }

    @Override
    public int mobCount() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("mobCount").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.mobCount();
               int ret = luaScript.get("mobCount").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.mobCount();
    }

    @Override
    public int randomDestination(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.randomDestination((Char) a0);
               int ret = luaScript.get("randomDestination").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomDestination(arg0);
    }

    @Override
    public Heap drop(Item arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("drop").isnil()) {
            try {
                MethodOverride.A2<Heap> superMethod = (a0, a1) -> super.drop((Item) a0, (int) a1);
               Heap ret = (Heap) luaScript.get("drop").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.drop(arg0, arg1);
    }

    @Override
    public void seal() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("seal").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.seal();
               luaScript.get("seal").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.seal();
    }

    @Override
    public Mob createMob() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createMob").isnil()) {
            try {
                MethodOverride.A0<Mob> superMethod = () -> super.createMob();
               Mob ret = (Mob) luaScript.get("createMob").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createMob();
    }

    @Override
    public void stopSpecialMusic(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("stopSpecialMusic").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.stopSpecialMusic((int) a0);
               luaScript.get("stopSpecialMusic").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.stopSpecialMusic(arg0);
    }

    @Override
    public void setFlamable(boolean[] arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setFlamable").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.setFlamable((boolean[]) a0);
               luaScript.get("setFlamable").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setFlamable(arg0);
    }

    @Override
    public boolean isFlamable(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("isFlamable").isnil()) {
            try {
                MethodOverride.A1<Boolean> superMethod = (a0) -> super.isFlamable((int) a0);
               boolean ret = luaScript.get("isFlamable").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isFlamable(arg0);
    }

    @Override
    public int mobLimit() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("mobLimit").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.mobLimit();
               int ret = luaScript.get("mobLimit").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.mobLimit();
    }

    @Override
    public void occupyCell(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("occupyCell").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.occupyCell((Char) a0);
               luaScript.get("occupyCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.occupyCell(arg0);
    }

    @Override
    public int randomRespawnCell(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("randomRespawnCell").isnil()) {
            try {
                MethodOverride.A1<Integer> superMethod = (a0) -> super.randomRespawnCell((Char) a0);
               int ret = luaScript.get("randomRespawnCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomRespawnCell(arg0);
    }

    @Override
    public LevelTransition getTransitionFromSurface() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getTransitionFromSurface").isnil()) {
            try {
                MethodOverride.A0<LevelTransition> superMethod = () -> super.getTransitionFromSurface();
               LevelTransition ret = (LevelTransition) luaScript.get("getTransitionFromSurface").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getTransitionFromSurface();
    }

    @Override
    public ArrayList getMobRotation() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getMobRotation").isnil()) {
            try {
                MethodOverride.A0<ArrayList> superMethod = () -> super.getMobRotation();
               ArrayList ret = (ArrayList) luaScript.get("getMobRotation").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getMobRotation();
    }

    @Override
    public void setCoinDoorCost(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setCoinDoorCost").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.setCoinDoorCost((int) a0, (int) a1);
               luaScript.get("setCoinDoorCost").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCoinDoorCost(arg0, arg1);
    }

    @Override
    public boolean[] getPassableAndAvoidVarForBoth(Char arg0, Char arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getPassableAndAvoidVarForBoth").isnil()) {
            try {
                MethodOverride.A2<boolean[]> superMethod = (a0, a1) -> super.getPassableAndAvoidVarForBoth((Char) a0, (Char) a1);
               boolean[] ret = (boolean[]) luaScript.get("getPassableAndAvoidVarForBoth").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getPassableAndAvoidVarForBoth(arg0, arg1);
    }

    @Override
    public ArrayList getItemsToPreserveFromSealedResurrect() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getItemsToPreserveFromSealedResurrect").isnil()) {
            try {
                MethodOverride.A0<ArrayList> superMethod = () -> super.getItemsToPreserveFromSealedResurrect();
               ArrayList ret = (ArrayList) luaScript.get("getItemsToPreserveFromSealedResurrect").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getItemsToPreserveFromSealedResurrect();
    }

    @Override
    public int length() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("length").isnil()) {
            try {
                MethodOverride.A0<Integer> superMethod = () -> super.length();
               int ret = luaScript.get("length").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.length();
    }

    @Override
    public void applyZoneBuffs(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("applyZoneBuffs").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.applyZoneBuffs((Char) a0);
               luaScript.get("applyZoneBuffs").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.applyZoneBuffs(arg0);
    }

    @Override
    public Item findPrizeItem(Class arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("findPrizeItem").isnil()) {
            try {
                MethodOverride.A1<Item> superMethod = (a0) -> super.findPrizeItem((Class) a0);
               Item ret = (Item) luaScript.get("findPrizeItem").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.findPrizeItem(arg0);
    }

    @Override
    protected boolean build() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("build").isnil()) {
            try {
                MethodOverride.A0<Boolean> superMethod = () -> super.build();
               boolean ret = luaScript.get("build").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.build();
    }

    @Override
    public void pressCell(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("pressCell").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.pressCell((int) a0);
               luaScript.get("pressCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.pressCell(arg0);
    }

    @Override
    public float respawnCooldown() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("respawnCooldown").isnil()) {
            try {
                MethodOverride.A0<Float> superMethod = () -> super.respawnCooldown();
               float ret = luaScript.get("respawnCooldown").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod)).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.respawnCooldown();
    }

    @Override
    protected void createItems() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createItems").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.createItems();
               luaScript.get("createItems").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.createItems();
    }

    @Override
    public void addItemToSpawn(Item arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("addItemToSpawn").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.addItemToSpawn((Item) a0);
               luaScript.get("addItemToSpawn").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.addItemToSpawn(arg0);
    }

    @Override
    public void uproot(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("uproot").isnil()) {
            try {
                MethodOverride.VoidA1 superMethod = (a0) -> super.uproot((int) a0);
               luaScript.get("uproot").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), LuaValue.valueOf(arg0)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.uproot(arg0);
    }

    @Override
    public void unseal() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("unseal").isnil()) {
            try {
                MethodOverride.VoidA0 superMethod = () -> super.unseal();
               luaScript.get("unseal").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod));
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.unseal();
    }

    @Override
    public void updateFieldOfView(Char arg0, boolean[] arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("updateFieldOfView").isnil()) {
            try {
                MethodOverride.VoidA2 superMethod = (a0, a1) -> super.updateFieldOfView((Char) a0, (boolean[]) a1);
               luaScript.get("updateFieldOfView").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateFieldOfView(arg0, arg1);
    }

    @Override
    public Trap setTrap(Trap arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setTrap").isnil()) {
            try {
                MethodOverride.A2<Trap> superMethod = (a0, a1) -> super.setTrap((Trap) a0, (int) a1);
               Trap ret = (Trap) luaScript.get("setTrap").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(superMethod), CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.setTrap(arg0, arg1);
    }
}