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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class LuaClassGenerator {

    //TODO list
    //write tutorial (should mention most important stuff for common classes)

    //expose and update certain variables

    //add custom mob stuff (like editcomp)

    //add ui for editing scripts:
    //top: select file
    //button to open github
    //methods are listed in foldable comps, use button to enable these, navigator buttons to go to the next edited method
    //for all the methods in a SELECTED order:
    // show declaration line, including real param names
    // for the correct order, assign to each method name a array with param names and int priority
    // exclude methods that are usually not uncluded
    //field for "additonal code" (this could be used to declare other methods not included in normal methods)
    //button insert template: will override affected methods, show info which methods are changed

    //add utilies wrapper to use certain STATIC methods from items or traps, and to show windows?
    //show window: only message, wndtitledmsg, wndscroll, wndquest, wandmaker quest window with varargs num rewards, open journal(int page)

    private static Globals globals;
    public static LuaValue luaScript ;

    //TODO restrict certain methods (e.g. file reading/writing) -> throw an exception if any method is invoked
    public static int scriptsRunning = 0;

    //every time a value changes, this must be updated!
    //expose Dungeon.hero, Dungeon.customDungeon, Dungeon.depth, Dungeon, Random, Dungeon.level, All item classes, Dungeon.LimitedDrops

    static {
        globals = JsePlatform.standardGlobals();

//        globals.set("myGame", CoerceJavaToLua.coerce(this));

        //LuaClassGenerator.globals.load("function canAttack() return true end  local aaa = {canAttack = canAttack} return aaa").call().get("canAttack").call()
    }

    public static void initStatic() {

        LuaClassGenerator.luaScript = LuaClassGenerator.globals.load(
                "function attackSkill() " +
                        "return 999999" +
                  " end  " +
                "function attackProc(this, enemy, damage) " +
                        "this:die()" +
                        "return damage" +
                  " end  " +
                "function die(this, cause) " +
                        "local item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
                        "level:drop(item, this.pos + level:width()).sprite:drop()" +
                        //TODO: call super!
                  " end  " +
                        "return {attackSkill = attackSkill; attackProc = attackProc; die = die}").call();

    }

    public static void updateGlobalVars() {
        globals.set("hero", CoerceJavaToLua.coerce(Dungeon.hero));
        globals.set("dungeon", CoerceJavaToLua.coerce(Dungeon.customDungeon));
        globals.set("customDungeon", CoerceJavaToLua.coerce(Dungeon.customDungeon));
        globals.set("level", CoerceJavaToLua.coerce(Dungeon.level));
        globals.set("depth", LuaValue.valueOf(Dungeon.depth));
        globals.set("limitedDrops", CoerceJavaToLua.coerce(Dungeon.LimitedDrops.values()));
        updateGlobalPrimitives();
    }

    public static void updateGlobalPrimitives() {
        globals.set("branch", LuaValue.valueOf(Dungeon.branch));
        globals.set("gold", LuaValue.valueOf(Dungeon.gold));
        globals.set("energy", LuaValue.valueOf(Dungeon.energy));
    }


    private static final String ROOT_DIR = System.getProperty("user.home")
            + "/ZZDaten/Freizeit/Programmieren/ShatteredPD/SPD-Sandbox/Projekt/core/src/main/java/";
//            Mob.class.getPackage().getName().replace('.', '/') + "/luamobs/";

    private LuaClassGenerator() {
    }

    public static void generateSourceFiles() {

        Class[][] mobs = Mobs.getAllMobs(null);

        generateFile(Mob.class);
        generateFile(Rat.class);
        generateFile(Ghost.class);
        generateFile(SentryRoom.Sentry.class);
    }

    private static void generateFile(Class<?> inputClass) {
        String source = generateSourceCode(inputClass);

//        String path =
    }

    private static String generateSourceCode(Class<?> inputClass) {
        String pckge = "package " + inputClass.getPackage().getName() + ";\n\n";
        String imprt = "import com.shatteredpixel.shatteredpixeldungeon.editor.LuaClass;\n" +
                "import com.shatteredpixel.shatteredpixeldungeon.editor.LuaClassGenerator;\n" +
                "import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;\n" +
                "import java.util.*;\n\n";
        String classHead = "public class " + inputClass.getSimpleName() + "_lua extends " + inputClass.getSimpleName() +" implements LuaClass {\n\n";
        String declaringVars = "    private String identifier;\n";
        String implementLuaClassStuff =
                "\n" +
                        "    @Override\n" +
                        "    public void setIdentifier(String identifier) {\n" +
                        "        this.identifier = identifier;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public String getIdentifier() {\n" +
                        "        return this.identifier;\n" +
                        "    }\n";
        //TODO tzz don't forget Methods from Bundlable!
        //And tell: if you want to store sth like the currently targeted enemy, store the id and ...

        StringBuilder overrideMethods = new StringBuilder();

        Map<String, Method> methods = new HashMap<>();
        findAllMethodsToOverride(inputClass, Actor.class, methods);
        for (Method m : methods.values()) {

            overrideMethods.append('\n');
            overrideMethods.append("    @Override\n");
            overrideMethods.append(Modifier.toString(m.getModifiers())).append(" ");

            overrideMethods.append(m.getReturnType().getSimpleName()).append(" ");
            overrideMethods.append(m.getName()).append("(");

            Class<?>[] paramTypes = m.getParameterTypes();
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append(paramTypes[i].getSimpleName());
                overrideMethods.append(" arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }

            Class<?> returnType = m.getReturnType();
            String returnString = returnType == void.class ? "" : returnType.getSimpleName() + " ret = ";
            if (!returnType.isPrimitive() && returnType != String.class) returnString += "(" + returnType.getSimpleName() + ") ";
            String returnTypeString;
            if (returnType == String.class) returnTypeString = ".tojstring()";
            else if (returnType == void.class) returnTypeString = "";
            else if (returnType.isPrimitive()) returnTypeString = ".to" + returnType + "()";
            else returnTypeString = ".touserdata()";

            overrideMethods.append(") {\n");
            overrideMethods.append("        LuaValue luaScript = LuaClassGenerator.luaScript;\n");
            overrideMethods.append("        if (luaScript != null && !luaScript.get(\"").append(m.getName()).append("\").isnil()) {\n");
            overrideMethods.append("            LuaClassGenerator.scriptsRunning++;\n");
            overrideMethods.append("           ").append(returnString).append("luaScript.get(\"")
                    .append(m.getName()).append("\").call(");

            overrideMethods.append("CoerceJavaToLua.coerce(this)");
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

            overrideMethods.append(")").append(returnTypeString).append(";\n");
            overrideMethods.append("            LuaClassGenerator.scriptsRunning--;\n");
            if (!returnString.isEmpty())
                overrideMethods.append("            return ret;\n");
            overrideMethods.append("        } else {\n");

            overrideMethods.append("            ");
            if (m.getReturnType() != void.class) {
                overrideMethods.append("return ");
            }
            overrideMethods.append("super.").append(m.getName()).append("(");
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append("arg").append(i);
                if (i < paramTypes.length - 1)
                    overrideMethods.append(", ");
            }
            overrideMethods.append(");\n");

            overrideMethods.append("        }\n");
            overrideMethods.append("    }\n");

        }

        return pckge
                + imprt
                + classHead
                + declaringVars
                + implementLuaClassStuff
                + overrideMethods
                + "}";
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

//    private static Map<String, Class<? extends LuaClass>> luaClassesCache = new HashMap<>();
//
//    public static Class<? extends LuaClass> forName(String inputClass) {
//        return generateLuaClass(Reflection.forName(inputClass.replace("_lua", "")));
//    }
//
//    public static Class<? extends LuaClass> generateLuaClass(Class<?> inputClass) {
//
//        String fullyQualifiedName = inputClass.getName() + "_lua";
//        if (luaClassesCache.containsKey(fullyQualifiedName)) {
//            return luaClassesCache.get(fullyQualifiedName);
//        }
//
//        String classSource = generateClassSource(inputClass);
//        Class<? extends LuaClass> generatedClass = compileClass(fullyQualifiedName, classSource);
//
//        luaClassesCache.put(fullyQualifiedName, generatedClass);
//
//        return generatedClass;
//    }
//
//    private static String generateClassSource(Class<?> inputClass) {
//        String package2 = inputClass.getPackage().getName();
//        package2 = "com.watabou.utils";
//        String source = "package " + package2 + ";\n" +
//                "public class " + inputClass.getSimpleName() + "_lua implements com.shatteredpixel.shatteredpixeldungeon.editor.LuaClass {\n" +
//                "    private String identifier;\n" +
//                "\n" +
//                "    public void setIdentifier(String identifier) {\n" +
//                "        this.identifier = identifier;\n" +
//                "    }\n" +
//                "\n" +
//                "    public String getIdentifier() {\n" +
//                "        return this.identifier;\n" +
//                "    }\n" +
//                "}";
//
//        return source;
//    }
//
//    private static Class<? extends LuaClass> compileClass(String className, String classSource) {
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        MemoryFileManager fileManager = new MemoryFileManager(compiler.getStandardFileManager(null, null, null));
//        JavaFileObject javaFileObject = new MemoryJavaSourceFile(className, classSource);
//
//        compiler.getTask(null, fileManager, null, null, null, Collections.singletonList(javaFileObject)).call();
//
//        try {
//            return (Class<? extends LuaClass>) fileManager.getClassLoader(null).loadClass(className);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Failed to load compiled class: " + className, e);
//        }
//    }
//}
//
//class MemoryJavaClassFile extends SimpleJavaFileObject {
//    private final ByteArrayOutputStream outputStream;
//
//    MemoryJavaClassFile(String name) {
//        super(URI.create("file:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
//        outputStream = new ByteArrayOutputStream();
//    }
//
//    @Override
//    public OutputStream openOutputStream() {
//        return outputStream;
//    }
//
//    byte[] getBytes() {
//        return outputStream.toByteArray();
//    }
//}
//
//// Utility class to represent Java source code in memory
//class MemoryJavaSourceFile extends SimpleJavaFileObject {
//    private final CharSequence source;
//
//    MemoryJavaSourceFile(String name, CharSequence source) {
//        super(URI.create("file:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
//        this.source = source;
//    }
//
//    @Override
//    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
//        return source;
//    }
//}
//
//// Utility class to compile and load classes in memory
//class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
//    private final MemoryClassLoader classLoader;
//
//    MemoryFileManager(StandardJavaFileManager fileManager) {
//        super(fileManager);
//        classLoader = new MemoryClassLoader();
//    }
//
//    @Override
//    public ClassLoader getClassLoader(Location location) {
//        return classLoader;
//    }
//
//    @Override
//    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
//        return new MemoryJavaClassFile(className);
//    }
//
//    // Utility class to load classes from compiled bytes
//    private static class MemoryClassLoader extends ClassLoader {
//        private final Map<String, MemoryJavaClassFile> classFileMap = new HashMap<>();
//        void addClassFile(String className, MemoryJavaClassFile classFile) {
//            classFileMap.put(className, classFile);
//        }
//        @Override
//        protected Class<?> findClass(String name) throws ClassNotFoundException {
//            MemoryJavaClassFile file = classFileMap.get(name);
//            if (file != null) {
//                byte[] bytes = file.getBytes();
//                return defineClass(name, bytes, 0, bytes.length);
//            }
//            throw new ClassNotFoundException(name);
//        }
//    }
//}