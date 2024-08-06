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

public class PrisonBossLevel_lua extends PrisonBossLevel implements LuaLevel {

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
                boolean ret = luaScript.get("isValidSpawnCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isValidSpawnCell(arg0, arg1);
    }

    public boolean super_isValidSpawnCell(Char arg0, int arg1) {
        return super.isValidSpawnCell(arg0, arg1);
    }

    @Override
    public void playSpecialMusic(String arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("playSpecialMusic").isnil()) {
            try {
                luaScript.get("playSpecialMusic").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playSpecialMusic(arg0, arg1);
    }

    public void super_playSpecialMusic(String arg0, int arg1) {
        super.playSpecialMusic(arg0, arg1);
    }

    @Override
    public int getCoinDoorCost(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getCoinDoorCost").isnil()) {
            try {
                int ret = luaScript.get("getCoinDoorCost").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getCoinDoorCost(arg0);
    }

    public int super_getCoinDoorCost(int arg0) {
        return super.getCoinDoorCost(arg0);
    }

    @Override
    protected void updateMusic() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("updateMusic").isnil()) {
            try {
                luaScript.get("updateMusic").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateMusic();
    }

    public void super_updateMusic() {
        super.updateMusic();
    }

    @Override
    public boolean isLevelExplored(String arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("isLevelExplored").isnil()) {
            try {
                boolean ret = luaScript.get("isLevelExplored").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isLevelExplored(arg0);
    }

    public boolean super_isLevelExplored(String arg0) {
        return super.isLevelExplored(arg0);
    }

    @Override
    public void disarmTrap(int arg0, boolean arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("disarmTrap").isnil()) {
            try {
                luaScript.get("disarmTrap").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.disarmTrap(arg0, arg1);
    }

    public void super_disarmTrap(int arg0, boolean arg1) {
        super.disarmTrap(arg0, arg1);
    }

    @Override
    public void setPassableLater(int arg0, boolean arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setPassableLater").isnil()) {
            try {
                luaScript.get("setPassableLater").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setPassableLater(arg0, arg1);
    }

    public void super_setPassableLater(int arg0, boolean arg1) {
        super.setPassableLater(arg0, arg1);
    }

    @Override
    public void setTerrain(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setTerrain").isnil()) {
            try {
                luaScript.get("setTerrain").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setTerrain(arg0, arg1);
    }

    public void super_setTerrain(int arg0, int arg1) {
        super.setTerrain(arg0, arg1);
    }

    @Override
    protected void createMobs() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createMobs").isnil()) {
            try {
                luaScript.get("createMobs").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.createMobs();
    }

    public void super_createMobs() {
        super.createMobs();
    }

    @Override
    public void create() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("create").isnil()) {
            try {
                luaScript.get("create").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.create();
    }

    public void super_create() {
        super.create();
    }

    @Override
    public int tunnelTile() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tunnelTile").isnil()) {
            try {
                int ret = luaScript.get("tunnelTile").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tunnelTile();
    }

    public int super_tunnelTile() {
        return super.tunnelTile();
    }

    @Override
    protected void defaultActiveTransitionImpl(Hero arg0, LevelTransition arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("defaultActiveTransitionImpl").isnil()) {
            try {
                luaScript.get("defaultActiveTransitionImpl").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.defaultActiveTransitionImpl(arg0, arg1);
    }

    public void super_defaultActiveTransitionImpl(Hero arg0, LevelTransition arg1) {
        super.defaultActiveTransitionImpl(arg0, arg1);
    }

    @Override
    public PrisonBossLevel.State state() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("state").isnil()) {
            try {
                PrisonBossLevel.State ret = (PrisonBossLevel.State) luaScript.get("state").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.state();
    }

    public PrisonBossLevel.State super_state() {
        return super.state();
    }

    @Override
    public boolean locked() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("locked").isnil()) {
            try {
                boolean ret = luaScript.get("locked").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.locked();
    }

    public boolean super_locked() {
        return super.locked();
    }

    @Override
    public int entrance() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("entrance").isnil()) {
            try {
                int ret = luaScript.get("entrance").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.entrance();
    }

    public int super_entrance() {
        return super.entrance();
    }

    @Override
    public boolean activateTransition(Hero arg0, LevelTransition arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("activateTransition").isnil()) {
            try {
                boolean ret = luaScript.get("activateTransition").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.activateTransition(arg0, arg1);
    }

    public boolean super_activateTransition(Hero arg0, LevelTransition arg1) {
        return super.activateTransition(arg0, arg1);
    }

    @Override
    public void discover(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("discover").isnil()) {
            try {
                luaScript.get("discover").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.discover(arg0);
    }

    public void super_discover(int arg0) {
        super.discover(arg0);
    }

    @Override
    public boolean spawnMob(Mob arg0, int arg1, Zone arg2) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("spawnMob").isnil()) {
            try {
                boolean ret = luaScript.get("spawnMob").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1), CoerceJavaToLua.coerce(arg2)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.spawnMob(arg0, arg1, arg2);
    }

    public boolean super_spawnMob(Mob arg0, int arg1, Zone arg2) {
        return super.spawnMob(arg0, arg1, arg2);
    }

    @Override
    public boolean[] getFlamable() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getFlamable").isnil()) {
            try {
                boolean[] ret = (boolean[]) luaScript.get("getFlamable").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getFlamable();
    }

    public boolean[] super_getFlamable() {
        return super.getFlamable();
    }

    @Override
    public void destroy(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("destroy").isnil()) {
            try {
                luaScript.get("destroy").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.destroy(arg0);
    }

    public void super_destroy(int arg0) {
        super.destroy(arg0);
    }

    @Override
    public boolean setCellToWater(boolean arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setCellToWater").isnil()) {
            try {
                boolean ret = luaScript.get("setCellToWater").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.setCellToWater(arg0, arg1);
    }

    public boolean super_setCellToWater(boolean arg0, int arg1) {
        return super.setCellToWater(arg0, arg1);
    }

    @Override
    public int fallCell(boolean arg0, String arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("fallCell").isnil()) {
            try {
                int ret = luaScript.get("fallCell").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.fallCell(arg0, arg1);
    }

    public int super_fallCell(boolean arg0, String arg1) {
        return super.fallCell(arg0, arg1);
    }

    @Override
    public void initForPlay() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("initForPlay").isnil()) {
            try {
                luaScript.get("initForPlay").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.initForPlay();
    }

    public void super_initForPlay() {
        super.initForPlay();
    }

    @Override
    public String tileDesc(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tileDesc").isnil()) {
            try {
                String ret = luaScript.get("tileDesc").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1().tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tileDesc(arg0, arg1);
    }

    public String super_tileDesc(int arg0, int arg1) {
        return super.tileDesc(arg0, arg1);
    }

    @Override
    public String tileName(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("tileName").isnil()) {
            try {
                String ret = luaScript.get("tileName").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).tojstring();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.tileName(arg0);
    }

    public String super_tileName(int arg0) {
        return super.tileName(arg0);
    }

    @Override
    public int exit() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("exit").isnil()) {
            try {
                int ret = luaScript.get("exit").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.exit();
    }

    public int super_exit() {
        return super.exit();
    }

    @Override
    public void placeTrapsInTenguCell(float arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("placeTrapsInTenguCell").isnil()) {
            try {
                luaScript.get("placeTrapsInTenguCell").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.placeTrapsInTenguCell(arg0);
    }

    public void super_placeTrapsInTenguCell(float arg0) {
        super.placeTrapsInTenguCell(arg0);
    }

    @Override
    public void playLevelMusic() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("playLevelMusic").isnil()) {
            try {
                luaScript.get("playLevelMusic").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.playLevelMusic();
    }

    public void super_playLevelMusic() {
        super.playLevelMusic();
    }

    @Override
    public Plant plant(Plant.Seed arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("plant").isnil()) {
            try {
                Plant ret = (Plant) luaScript.get("plant").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.plant(arg0, arg1);
    }

    public Plant super_plant(Plant.Seed arg0, int arg1) {
        return super.plant(arg0, arg1);
    }

    @Override
    public void reset() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("reset").isnil()) {
            try {
                luaScript.get("reset").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.reset();
    }

    public void super_reset() {
        super.reset();
    }

    @Override
    public int randomDestination(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("randomDestination").isnil()) {
            try {
                int ret = luaScript.get("randomDestination").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomDestination(arg0);
    }

    public int super_randomDestination(Char arg0) {
        return super.randomDestination(arg0);
    }

    @Override
    public int mobCount() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("mobCount").isnil()) {
            try {
                int ret = luaScript.get("mobCount").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.mobCount();
    }

    public int super_mobCount() {
        return super.mobCount();
    }

    @Override
    public Heap drop(Item arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("drop").isnil()) {
            try {
                Heap ret = (Heap) luaScript.get("drop").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.drop(arg0, arg1);
    }

    public Heap super_drop(Item arg0, int arg1) {
        return super.drop(arg0, arg1);
    }

    @Override
    public int randomTenguCellPos() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("randomTenguCellPos").isnil()) {
            try {
                int ret = luaScript.get("randomTenguCellPos").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomTenguCellPos();
    }

    public int super_randomTenguCellPos() {
        return super.randomTenguCellPos();
    }

    @Override
    public void seal() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("seal").isnil()) {
            try {
                luaScript.get("seal").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.seal();
    }

    public void super_seal() {
        super.seal();
    }

    @Override
    public Mob createMob() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createMob").isnil()) {
            try {
                Mob ret = (Mob) luaScript.get("createMob").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.createMob();
    }

    public Mob super_createMob() {
        return super.createMob();
    }

    @Override
    public void stopSpecialMusic(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("stopSpecialMusic").isnil()) {
            try {
                luaScript.get("stopSpecialMusic").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.stopSpecialMusic(arg0);
    }

    public void super_stopSpecialMusic(int arg0) {
        super.stopSpecialMusic(arg0);
    }

    @Override
    public void setFlamable(boolean[] arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setFlamable").isnil()) {
            try {
                luaScript.get("setFlamable").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setFlamable(arg0);
    }

    public void super_setFlamable(boolean[] arg0) {
        super.setFlamable(arg0);
    }

    @Override
    public boolean isFlamable(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("isFlamable").isnil()) {
            try {
                boolean ret = luaScript.get("isFlamable").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0)).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.isFlamable(arg0);
    }

    public boolean super_isFlamable(int arg0) {
        return super.isFlamable(arg0);
    }

    @Override
    public int mobLimit() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("mobLimit").isnil()) {
            try {
                int ret = luaScript.get("mobLimit").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.mobLimit();
    }

    public int super_mobLimit() {
        return super.mobLimit();
    }

    @Override
    public void occupyCell(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("occupyCell").isnil()) {
            try {
                luaScript.get("occupyCell").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.occupyCell(arg0);
    }

    public void super_occupyCell(Char arg0) {
        super.occupyCell(arg0);
    }

    @Override
    public int randomRespawnCell(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("randomRespawnCell").isnil()) {
            try {
                int ret = luaScript.get("randomRespawnCell").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.randomRespawnCell(arg0);
    }

    public int super_randomRespawnCell(Char arg0) {
        return super.randomRespawnCell(arg0);
    }

    @Override
    public LevelTransition getTransitionFromSurface() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getTransitionFromSurface").isnil()) {
            try {
                LevelTransition ret = (LevelTransition) luaScript.get("getTransitionFromSurface").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getTransitionFromSurface();
    }

    public LevelTransition super_getTransitionFromSurface() {
        return super.getTransitionFromSurface();
    }

    @Override
    public ArrayList getMobRotation() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getMobRotation").isnil()) {
            try {
                ArrayList ret = (ArrayList) luaScript.get("getMobRotation").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getMobRotation();
    }

    public ArrayList super_getMobRotation() {
        return super.getMobRotation();
    }

    @Override
    public void setCoinDoorCost(int arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setCoinDoorCost").isnil()) {
            try {
                luaScript.get("setCoinDoorCost").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0), LuaValue.valueOf(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.setCoinDoorCost(arg0, arg1);
    }

    public void super_setCoinDoorCost(int arg0, int arg1) {
        super.setCoinDoorCost(arg0, arg1);
    }

    @Override
    public boolean[] getPassableAndAvoidVarForBoth(Char arg0, Char arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getPassableAndAvoidVarForBoth").isnil()) {
            try {
                boolean[] ret = (boolean[]) luaScript.get("getPassableAndAvoidVarForBoth").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getPassableAndAvoidVarForBoth(arg0, arg1);
    }

    public boolean[] super_getPassableAndAvoidVarForBoth(Char arg0, Char arg1) {
        return super.getPassableAndAvoidVarForBoth(arg0, arg1);
    }

    @Override
    public void cleanTenguCell() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("cleanTenguCell").isnil()) {
            try {
                luaScript.get("cleanTenguCell").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.cleanTenguCell();
    }

    public void super_cleanTenguCell() {
        super.cleanTenguCell();
    }

    @Override
    public ArrayList getItemsToPreserveFromSealedResurrect() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("getItemsToPreserveFromSealedResurrect").isnil()) {
            try {
                ArrayList ret = (ArrayList) luaScript.get("getItemsToPreserveFromSealedResurrect").call(CoerceJavaToLua.coerce(this), vars).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.getItemsToPreserveFromSealedResurrect();
    }

    public ArrayList super_getItemsToPreserveFromSealedResurrect() {
        return super.getItemsToPreserveFromSealedResurrect();
    }

    @Override
    public int length() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("length").isnil()) {
            try {
                int ret = luaScript.get("length").call(CoerceJavaToLua.coerce(this), vars).toint();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.length();
    }

    public int super_length() {
        return super.length();
    }

    @Override
    public void applyZoneBuffs(Char arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("applyZoneBuffs").isnil()) {
            try {
                luaScript.get("applyZoneBuffs").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.applyZoneBuffs(arg0);
    }

    public void super_applyZoneBuffs(Char arg0) {
        super.applyZoneBuffs(arg0);
    }

    @Override
    public Item findPrizeItem(Class arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("findPrizeItem").isnil()) {
            try {
                Item ret = (Item) luaScript.get("findPrizeItem").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0)).touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.findPrizeItem(arg0);
    }

    public Item super_findPrizeItem(Class arg0) {
        return super.findPrizeItem(arg0);
    }

    @Override
    protected boolean build() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("build").isnil()) {
            try {
                boolean ret = luaScript.get("build").call(CoerceJavaToLua.coerce(this), vars).toboolean();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.build();
    }

    public boolean super_build() {
        return super.build();
    }

    @Override
    public void pressCell(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("pressCell").isnil()) {
            try {
                luaScript.get("pressCell").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.pressCell(arg0);
    }

    public void super_pressCell(int arg0) {
        super.pressCell(arg0);
    }

    @Override
    public void progress() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("progress").isnil()) {
            try {
                luaScript.get("progress").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.progress();
    }

    public void super_progress() {
        super.progress();
    }

    @Override
    public float respawnCooldown() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("respawnCooldown").isnil()) {
            try {
                float ret = luaScript.get("respawnCooldown").call(CoerceJavaToLua.coerce(this), vars).tofloat();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.respawnCooldown();
    }

    public float super_respawnCooldown() {
        return super.respawnCooldown();
    }

    @Override
    protected void createItems() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("createItems").isnil()) {
            try {
                luaScript.get("createItems").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.createItems();
    }

    public void super_createItems() {
        super.createItems();
    }

    @Override
    public void addItemToSpawn(Item arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("addItemToSpawn").isnil()) {
            try {
                luaScript.get("addItemToSpawn").call(CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.addItemToSpawn(arg0);
    }

    public void super_addItemToSpawn(Item arg0) {
        super.addItemToSpawn(arg0);
    }

    @Override
    public void uproot(int arg0) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("uproot").isnil()) {
            try {
                luaScript.get("uproot").call(CoerceJavaToLua.coerce(this), vars, LuaValue.valueOf(arg0));
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.uproot(arg0);
    }

    public void super_uproot(int arg0) {
        super.uproot(arg0);
    }

    @Override
    public void unseal() {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("unseal").isnil()) {
            try {
                luaScript.get("unseal").call(CoerceJavaToLua.coerce(this), vars);
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.unseal();
    }

    public void super_unseal() {
        super.unseal();
    }

    @Override
    public Trap setTrap(Trap arg0, int arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("setTrap").isnil()) {
            try {
                Trap ret = (Trap) luaScript.get("setTrap").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), LuaValue.valueOf(arg1)}).arg1().touserdata();
                return ret;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        return super.setTrap(arg0, arg1);
    }

    public Trap super_setTrap(Trap arg0, int arg1) {
        return super.setTrap(arg0, arg1);
    }

    @Override
    public void updateFieldOfView(Char arg0, boolean[] arg1) {
        LuaValue luaScript = levelScheme.luaScript.getScript();
        if (luaScript != null && !luaScript.get("updateFieldOfView").isnil()) {
            try {
                luaScript.get("updateFieldOfView").invoke(new LuaValue[]{CoerceJavaToLua.coerce(this), vars, CoerceJavaToLua.coerce(arg0), CoerceJavaToLua.coerce(arg1)}).arg1();
                return;
            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }
        }
        super.updateFieldOfView(arg0, arg1);
    }

    public void super_updateFieldOfView(Char arg0, boolean[] arg1) {
        super.updateFieldOfView(arg0, arg1);
    }
}