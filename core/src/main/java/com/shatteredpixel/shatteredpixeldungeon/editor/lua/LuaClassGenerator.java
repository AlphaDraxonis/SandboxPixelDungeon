/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInvCategory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class LuaClassGenerator {

    private static final String ROOT_DIR = System.getProperty("user.home")
            + "/ZZDaten/Freizeit/Programmieren/ShatteredPD/SPD-Sandbox/Projekt/core/src/main/java/";

    private LuaClassGenerator() {
    }

    public static void genaaa_IMPORTANT_BeforeGeneratingMakeSure_ACCESS_CHECHER_isPublicgenerateLevelSourceFilesgenerateMobFiles(){}

    public static void generateAll() {
        generateLevelSourceFiles();
        generateMobSourceFiles();
        generateDungeonScriptSourceFile();
    }

    private static void generateLevelSourceFiles() {
        generateLevelFile(CavesBossLevel.class);
        generateLevelFile(CavesLevel.class);
        generateLevelFile(CavesBossLevel.class);
        generateLevelFile(CityBossLevel.class);
        generateLevelFile(CityLevel.class);
        generateLevelFile(DeadEndLevel.class);
        generateLevelFile(HallsBossLevel.class);
        generateLevelFile(HallsLevel.class);
        generateLevelFile(LastLevel.class);
        generateLevelFile(LastShopLevel.class);
        generateLevelFile(MiningLevel.class);
        generateLevelFile(PrisonBossLevel.class);
        generateLevelFile(PrisonLevel.class);
        generateLevelFile(SewerBossLevel.class);
        generateLevelFile(SewerLevel.class);
        generateLevelFile(CustomLevel.class);
    }

    public static void generateMobSourceFiles() {

        Class<?>[][] mobs = EditorInvCategory.getAll(Mobs.values());

        for (Class<?>[] classes : mobs) {
            for (Class<?> c : classes) {
                generateMobFile(c);
            }
        }
    }

    private static void generateLevelFile(Class<?> inputClass) {
        String source = generateSourceCodeLevel(inputClass);

        String path = ROOT_DIR
                + (Level.class.getPackage().getName() + ".lualevels.").replaceAll("\\.", "/")
                + inputClass.getSimpleName() + "_lua.java";

        File f = new File(path);

        if (f.exists()) f.delete();

        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(f)) {
            writer.write(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateMobFile(Class<?> inputClass) {
        String source = generateSourceCodeMob(inputClass);

        String path = ROOT_DIR
                + (Mob.class.getPackage().getName() + ".luamobs.").replaceAll("\\.", "/")
                + inputClass.getSimpleName() + "_lua.java";

        File f = new File(path);

        if (f.exists()) f.delete();

        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(f)) {
            writer.write(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateDungeonScriptSourceFile() {
        String source = generateSourceCodeDungeonScript();

        String path = ROOT_DIR
                + (DungeonScript.class.getPackage().getName()).replaceAll("\\.", "/") + "/"
                + DungeonScript.class.getSimpleName() + "_lua.java";

        File f = new File(path);

        if (f.exists()) f.delete();

        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(f)) {
            writer.write(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateSourceCodeDungeonScript() {
        String pckge = "package " + DungeonScript.class.getPackage().getName() + ";\n\n";
        String imprt = "import " + Messages.MAIN_PACKAGE_NAME + "actors.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.buffs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.mobs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.mobs.npcs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.hero.Hero;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.lua.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.ui.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.util.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.armor.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.weapon.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.wands.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.rooms.special.SentryRoom;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "sprites.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "windows.WndError;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "ui.Window;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "scenes.DungeonScene;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "GameObject;\n" +
                "import com.watabou.noosa.Game;\n" +
                "import com.watabou.utils.*;\n" +
                "import org.luaj.vm2.*;\n" +
                "import org.luaj.vm2.lib.jse.CoerceJavaToLua;\n" +
                "import java.util.*;\n\n";
        String extents = DungeonScript.class.getSimpleName();
        String classHead = "public class " + DungeonScript.class.getSimpleName() + "_lua extends " + extents + " implements LuaClass {\n\n";

        String implementLuaClassStuff =
                "\n" +
                        "    private LuaValue vars;\n\n" +
                        "    @Override\n" +
                        "    public void setIdentifier(int identifier) {\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public int getIdentifier() {\n" +
                        "        return -1;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public LuaClass newInstance() {\n" +
                        "        return new DungeonScript_lua();\n" +
                        "    }\n\n" +
                        "    public void storeInBundle(Bundle bundle) {\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreFromBundle(Bundle bundle) {\n" +
                        "    }\n\n";

        Map<String, Method> methods = new HashMap<>();
        findAllMethodsToOverride(DungeonScript.class, DungeonScript.class, methods);
        methods.remove("storeInBundle");
        methods.remove("restoreFromBundle");

        return pckge
                + imprt
                + classHead
                + implementLuaClassStuff
                + overrideMethods(methods.values(), "getScript()")
                + "}";
    }

    public static String generateSourceCodeMob(Class<?> inputClass) {
        String pckge = "package " + Mob.class.getPackage().getName() + ".luamobs;\n\n";
        String imprt = "import " + Messages.MAIN_PACKAGE_NAME + "actors.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.buffs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.mobs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.mobs.npcs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.hero.Hero;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.lua.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.ui.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.util.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.armor.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.weapon.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.wands.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.rooms.special.SentryRoom;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "sprites.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "windows.WndError;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "ui.Window;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "scenes.DungeonScene;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "GameObject;\n" +
                "import com.watabou.noosa.Game;\n" +
                "import com.watabou.utils.*;\n" +
                "import org.luaj.vm2.*;\n" +
                "import org.luaj.vm2.lib.jse.CoerceJavaToLua;\n" +
                "import java.util.*;\n\n";
        String extents = inputClass.getSimpleName();
        if (inputClass.getEnclosingClass() != null) extents = inputClass.getEnclosingClass().getSimpleName() + "." + extents;
        String classHead = "public class " + inputClass.getSimpleName() + "_lua extends " + extents + " implements LuaMob {\n\n";
        String declaringVars = "    private int identifier;\n"
                + "    private boolean inheritsStats = true;\n"
                + "    private LuaTable vars;\n";
        String implementLuaClassStuff =
                "\n" +
                        "    @Override\n" +
                        "    public void setIdentifier(int identifier) {\n" +
                        "        this.identifier = identifier;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public int getIdentifier() {\n" +
                        "        return this.identifier;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void setInheritsStats(boolean inheritsStats) {\n" +
                        "        this.inheritsStats = inheritsStats;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public boolean getInheritsStats() {\n" +
                        "        return inheritsStats;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public LuaClass newInstance() {\n" +
                        "        return (LuaClass) getCopy();\n" +
                        "    }\n\n";
        String bundlingMethods =
                "@Override\n" +
                        "    public void storeInBundle(Bundle bundle) {\n" +
                        "        super.storeInBundle(bundle);\n" +
                        "        bundle.put(LuaClass.IDENTIFIER, identifier);\n" +
                        "        bundle.put(LuaMob.INHERITS_STATS, inheritsStats);\n" +
                        "        if (vars != null && !CustomDungeon.isEditing()) {\n" +
                        "            LuaManager.storeVarInBundle(bundle, vars, VARS);\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreFromBundle(Bundle bundle) {\n" +
                        "        super.restoreFromBundle(bundle);\n" +
                        "        identifier = bundle.getInt(LuaClass.IDENTIFIER);\n" +
                        "        inheritsStats = bundle.getBoolean(LuaMob.INHERITS_STATS);\n" +
                        "\n" +
                        "        LuaValue script;\n" +
                        "        if (!CustomDungeon.isEditing() && (script = CustomObject.getScript(identifier)) != null && script.get(\"vars\").istable()) {\n" +
                        "            vars = LuaManager.deepCopyLuaValue(script.get(\"vars\")).checktable();\n" +
                        "\n" +
                        "            LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, VARS);\n" +
                        "            if (loaded != null && loaded.istable()) vars = loaded.checktable();\n" +
                        "            if (script.get(\"static\").istable()) vars.set(\"static\", script.get(\"static\"));\n" +
                        "        }\n" +
                        "    }\n\n";

        Map<String, Method> methods = new HashMap<>();
        findAllMethodsToOverride(inputClass, Actor.class, methods);
        methods.remove("storeInBundle");
        methods.remove("restoreFromBundle");

        methods.remove("getCopy");
        methods.remove("onRenameLevelScheme");
        methods.remove("onDeleteLevelScheme");
        methods.remove("setDurationForBuff");
        methods.remove("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob");
        methods.remove("getPropertiesVar_ACCESS_ONLY_FOR_EDITING_UI");
        methods.remove("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY");
        methods.remove("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME");

        return pckge
                + imprt
                + classHead
                + declaringVars
                + implementLuaClassStuff
                + bundlingMethods
                + overrideMethods(methods.values(), "CustomObject.getScript(identifier)")
                + "}";
    }

    public static String generateSourceCodeLevel(Class<?> inputClass) {
        String accessScript = "levelScheme.luaScript.getScript()";
        String pckge = "package " + Level.class.getPackage().getName() + ".lualevels;\n\n";
        String imprt = "import " + Messages.MAIN_PACKAGE_NAME + "actors.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.buffs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.mobs.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "actors.hero.Hero;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.lua.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.ui.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "editor.util.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "items.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "sprites.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "windows.WndError;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "scenes.DungeonScene;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.builders.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.painters.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.features.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.rooms.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "levels.traps.*;\n" +
                "import " + Messages.MAIN_PACKAGE_NAME + "plants.Plant;\n" +
                "import com.watabou.noosa.Game;\n" +
                "import com.watabou.utils.Bundle;\n" +
                "import org.luaj.vm2.*;\n" +
                "import org.luaj.vm2.lib.jse.CoerceJavaToLua;\n" +
                "import java.util.*;\n\n";
        String classHead = "public class " + inputClass.getSimpleName() + "_lua extends " + inputClass.getSimpleName() + " implements LuaLevel {\n\n";
        String declaringVars = "    private LuaTable vars;\n";
        String bundlingMethods =
                "    @Override\n" +
                        "    public void setVars(LuaTable vars) {\n" +
                        "        this.vars = vars;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void storeInBundle(Bundle bundle) {\n" +
                        "        super.storeInBundle(bundle);\n" +
                        "        if (vars != null) {\n" +
                        "            LuaManager.storeVarInBundle(bundle, vars, LuaClass.VARS);\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreFromBundle(Bundle bundle) {\n" +
                        "        super.restoreFromBundle(bundle);\n" +
                        "\n" +
                        "        LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, LuaClass.VARS);\n" +
                        "        if (loaded != null && loaded.istable()) vars = loaded.checktable();\n" +
                        "    }\n\n";

        Map<String, Method> methods = new HashMap<>();
        findAllMethodsToOverride(inputClass, Level.class, methods);
        methods.remove("storeInBundle");
        methods.remove("restoreFromBundle");

        methods.remove("setSize");
        methods.remove("width");
        methods.remove("height");
        methods.remove("tilesTex");
        methods.remove("waterTex");
        methods.remove("getTransition");
        methods.remove("addVisuals");
        methods.remove("addWallVisuals");
        methods.remove("findMob");
        methods.remove("addRespawner");
        methods.remove("addZoneRespawner");
        methods.remove("buildFlagMaps");
        methods.remove("isPassable");
        methods.remove("isPassableHero");
        methods.remove("isPassableMob");
        methods.remove("isPassableAlly");
        methods.remove("getPassableVar");
        methods.remove("getPassableHeroVar");
        methods.remove("getPassableMobVar");
        methods.remove("getPassableAllyVar");
        methods.remove("getPassableAndAnyVarForBoth");
        methods.remove("getPassableAndAvoidVar");
        methods.remove("removeSimpleCustomTile");
        methods.remove("cleanWalls");
        methods.remove("cleanWallCell");
        methods.remove("distance");
        methods.remove("adjacent");
        methods.remove("trueDistance");
        methods.remove("insideMap");
        methods.remove("cellToPoint");
        methods.remove("pointToCell");
        methods.remove("appendNoTransWarning");

        //CustomLevel
        methods.remove("updateTransitionCells");

//        methods.remove("getCopy");
//        methods.remove("onRenameLevelScheme");
//        methods.remove("onDeleteLevelScheme");
//        methods.remove("setDurationForBuff");
//        methods.remove("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob");
//        methods.remove("getPropertiesVar_ACCESS_ONLY_FOR_EDITING_UI");
//        methods.remove("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY");
//        methods.remove("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME");

        return pckge
                + imprt
                + classHead
                + declaringVars
                + bundlingMethods
                + overrideMethods(methods.values(), accessScript)
                + "}";
    }

    private static String overrideMethods(Collection<Method> methods, String accessScript) {
        StringBuilder overrideMethods = new StringBuilder();

        for (Method m : methods) {

            Class<?> returnType = m.getReturnType();
            String returnTypeName = className(returnType);

            overrideMethods.append('\n');
            overrideMethods.append("    @Override\n    ");
            overrideMethods.append(Modifier.toString(m.getModifiers())).append(" ");

            overrideMethods.append(returnTypeName).append(" ");
            overrideMethods.append(m.getName()).append("(");

            Class<?>[] paramTypes = m.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append(className(paramTypes[i]));
                overrideMethods.append(" arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }

            String returnString = returnType == void.class ? "" : returnTypeName + " ret = ";
            if (!returnType.isPrimitive() && returnType != String.class) returnString += "(" + returnTypeName + ") ";
            String returnTypeString;
            if (returnType == String.class) returnTypeString = ".tojstring()";
            else if (returnType == void.class) returnTypeString = "";
            else if (returnType.isPrimitive()) returnTypeString = ".to" + returnType + "()";
            else returnTypeString = ".touserdata()";

            boolean useInvoke = paramTypes.length >= 2;

            overrideMethods.append(") {\n");
            overrideMethods.append("        LuaValue luaScript = " + accessScript + ";\n");
            overrideMethods.append("        if (luaScript != null && !luaScript.get(\"").append(m.getName()).append("\").isnil()) {\n");

            overrideMethods.append("            try {\n");
//            overrideMethods.append("                MethodOverride");
//            if (paramTypes.length <= 10) overrideMethods.append('.');
//            if (returnType == void.class) overrideMethods.append("Void");
//            if (paramTypes.length <= 10) overrideMethods.append('A').append(paramTypes.length);
//            if (returnType != void.class) {
//                overrideMethods.append('<');
//                if (returnType.isPrimitive()) {
//                    if (returnType == int.class) overrideMethods.append("Integer");
//                    else if (returnType == char.class) overrideMethods.append("Character");
//                    else overrideMethods.append(Messages.capitalize(returnTypeName));
//                } else overrideMethods.append(returnTypeName);
//                overrideMethods.append('>');
//            }
//
//            overrideMethods.append(" superMethod = (");
//            for (int i = 0; i < paramTypes.length; i++) {
//                overrideMethods.append('a').append(i);
//                if (i + 1 < paramTypes.length) overrideMethods.append(", ");
//            }
//            overrideMethods.append(") -> super.");
//            overrideMethods.append(m.getName()).append('(');
//            for (int i = 0; i < paramTypes.length; i++) {
//                if (paramTypes[i] != Object.class) {
//                    overrideMethods.append('(');
//                    overrideMethods.append(className(paramTypes[i]));
//                    overrideMethods.append(')');
//                    overrideMethods.append(' ');
//                }
//                overrideMethods.append('a').append(i);
//                if (i + 1 < paramTypes.length) overrideMethods.append(", ");
//            }
//            overrideMethods.append(");\n");

            overrideMethods.append("                ").append(returnString).append("luaScript.get(\"")
                    .append(m.getName()).append("\").").append(useInvoke ? "invoke" : "call").append('(');

            if (useInvoke) overrideMethods.append("new LuaValue[]{");
            overrideMethods.append("CoerceJavaToLua.coerce(this), ");
            overrideMethods.append("vars");
//            overrideMethods.append(", CoerceJavaToLua.coerce(superMethod)");
            if (paramTypes.length > 0) overrideMethods.append(", ");

            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i].isPrimitive()) {
                    overrideMethods.append("LuaValue.valueOf(");
                } else {
                    overrideMethods.append("CoerceJavaToLua.coerce(");
                }
                overrideMethods.append("arg").append(i);
                overrideMethods.append(")");
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }

            if (useInvoke) overrideMethods.append('}');
            overrideMethods.append(')');
            if (useInvoke) overrideMethods.append(".arg1()");
            overrideMethods.append(returnTypeString).append(";\n");
            if (!returnString.isEmpty())
                overrideMethods.append("                return ret;\n");
            else overrideMethods.append("                return;\n");
            overrideMethods.append("            } catch (LuaError error) { Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error))); }\n");
            overrideMethods.append("        }\n");

            overrideMethods.append("        ");
            if (returnType != void.class) {
                overrideMethods.append("return ");
            }
            overrideMethods.append("super.").append(m.getName()).append("(");
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append("arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }
            overrideMethods.append(");\n");

            overrideMethods.append("    }\n");


            //for calling super
            overrideMethods.append("\n    ").append("public").append(" ");
            overrideMethods.append(returnTypeName).append(" ");
            overrideMethods.append("super_").append(m.getName()).append("(");
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append(className(paramTypes[i]));
                overrideMethods.append(" arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }
            overrideMethods.append(") {\n        ");

            if (returnType != void.class) overrideMethods.append("return ");
            overrideMethods.append("super.").append(m.getName()).append("(");
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append("arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }
            overrideMethods.append(");\n");
            overrideMethods.append("    }\n");

        }

        return overrideMethods.toString();
    }

    private static String className(Class<?> clazz) {
        Class<?> enclosingClass = clazz.getEnclosingClass();
        if (enclosingClass != null && Modifier.isStatic(clazz.getModifiers())) {
            return className(enclosingClass) + "." + clazz.getSimpleName();
        }
        return clazz.getSimpleName();
    }

    private static void findAllMethodsToOverride(Class<?> currentClass, Class<?> highestClass, Map<String, Method> currentMethods) {
        for (Method m : currentClass.getDeclaredMethods()) {
            int mods = m.getModifiers();
            if (Modifier.isPrivate(mods) || Modifier.isFinal(mods) || Modifier.isStatic(mods)) {
                //don't override these
                continue;
            }
            if (!currentMethods.containsKey(m.getName()))
                currentMethods.put(m.getName(), m);
        }
        if (currentClass != highestClass) {
            findAllMethodsToOverride(currentClass.getSuperclass(), highestClass, currentMethods);
        }
    }

}