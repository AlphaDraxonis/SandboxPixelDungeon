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
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class LuaClassGenerator {

    public static void enterClass(){
        int i = 0;
    }

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

    //ACHTUNG variable names cannot start with "__type" !!! tzz TODO very important!

    //TODO: implement static: for this mob type, and super_static for ALL custom mobs
    //These ("static" and "globals") are seserverd names and cannot be used as names for vaiables!
    //they cannot even be declared as new variables at any time!

    //Whenever a game is started or loaded / it should also load all scripts of all custom mob types, and

    private static final String funAttackSkill =
            "function attackSkill(this) " +
                    "    return 999999" +
                    " end  ";
    private static final String funAttackProc =
            "function attackProc(this, enemy, damage) " +
                    "    this:die()" +
                    "    return damage" +
                    " end  ";
    private static final String funAttackProc2 =
            "function attackProc(this, enemy, damage) " +
                    "    test = test + 1" +
                    "    return test" +
                    " end  ";
    private static final String funDie =
            "function die(this, vars, super, cause) " +
                    "local item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
                    "level:drop(item, this.pos + level:width()).sprite:drop()" +
                    "super:call({cause})" +
                    " end  ";

    private static final String vars =
            "vars = { " +
//                    "local item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
                    "item = nil;" +
                    "test = 11;" +
                    "static = {" +
                    "aNumber = 17" +
                    "};" +
                    "globals = {" +
                    "globalValue = 99" +
                    "}" +
                    " }  ";

    public static void initStatic() {

//        LuaClassGenerator.luaScript = LuaClassGenerator.globals.load(
//                 vars +
////                         "function attackSkill(this, vars) " +
////                         "if vars.item == nil then" +
////                         "   vars.item = luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\")" +
////                         " else  level:drop(luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing\"), this.pos + level:width()).sprite:drop()" +
////                         " end  vars.static.aNumber = vars.static.aNumber + 1 return vars.static.aNumber" +
////                         " end " +
//
//                         funDie +
//
//                        "return {" +
//                         "attackSkill = attackSkill; " +
////                         "attackProc = attackProc; " +
//                         "die = die;" +
//                         "vars = vars " +
//                         "}").call();

    }

    //LuaClassGenerator.globals.load(
    //       " vars = { item = nil; test = 66 }       function attackSkill(this, vars) " + " local item = nil if item==nil then return luajava.newInstance(\"com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost\") else  return level end end" + "   " + "return {" +
    //    "attackSkill = attackSkill; " +      "vars = vars " + "}").call().get("attackSkill").call()


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

//        String path = TODO tzz need to override bundlabe in generateCode!!!!!
    }

    //private LuaValue luaVars;//LuaTable mit variablen, wird gespeichert,  bei restoreFromBundle() werden nur die Werte übernommen, die tatsächlich noch vorhanden sind
    //
    //    {
    //
    ////        //TODO tzz find better way of copying, extract methods and make static
    ////        LuaTable originalVars = LuaClassGenerator.luaScript.get("vars").checktable();
    ////        luaVars = LuaClassGenerator.deepCopyLuaValue(originalVars);
    //
    //        //in restoreFromBundle: check what has been saved, and only override those vars that are still present
    //        //in storeInBundle: find a way to properly store all of that automatically
    //        //test if they are separate

    private static String generateSourceCode(Class<?> inputClass) {
        String pckge = "package " + inputClass.getPackage().getName() + ";\n\n";
        String imprt = "import com.shatteredpixel.shatteredpixeldungeon.editor.lua.*;\n" +
                "import com.shatteredpixel.shatteredpixeldungeon.actors.*;\n" +
                "import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.*;\n" +
                "import org.luaj.vm2.*;\n" +
                "import java.util.*;\n\n";
        String classHead = "public class " + inputClass.getSimpleName() + "_lua extends " + inputClass.getSimpleName() +" implements LuaClass {\n\n";
        String declaringVars = "    private String identifier;\n"
                + "    private LuaTable vars;\n";
        String initializers = "\n{\n" +
                "        if (!CustomDungeon.isEditing() && LuaClassGenerator.luaScript != null && LuaClassGenerator.luaScript.get(\"vars\").istable()) {\n" +
                "            vars = LuaClassGenerator.deepCopyLuaValue(LuaClassGenerator.luaScript.get(\"vars\")).checktable();\n" +
                "            vars.set(\"static\", LuaClassGenerator.luaScript.get(\"vars\").get(\"static\"));\n" +
//                "            vars.set(\"globals\", LuaClassGenerator.luaScript.get(\"vars\").get(\"globals\"));\n" +
                "        }\n" +
                "    }\n";
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
                        "\n";
        String bundlingMethods =
                "@Override\n" +
                        "    public void storeInBundle(Bundle bundle) {\n" +
                        "        super.storeInBundle(bundle);\n" +
                        "        bundle.put(LuaClass.IDENTIFIER, identifier);\n" +
                        "        if (vars != null && !CustomDungeon.isEditing()) {\n" +
                        "            LuaClassGenerator.storeVarInBundle(bundle, vars, VARS);\n" +
                        "        }\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public void restoreFromBundle(Bundle bundle) {\n" +
                        "        super.restoreFromBundle(bundle);\n" +
                        "        identifier = bundle.getInt(LuaClass.IDENTIFIER);\n" +
                        "        LuaValue loaded = LuaClassGenerator.restoreVarFromBundle(bundle, VARS);\n" +
                        "        if (loaded != null && loaded.istable()) {\n" +
                        "            vars = loaded.checktable();\n" +
                        "            if (LuaClassGenerator.luaScript != null && LuaClassGenerator.luaScript.get(\"vars\").istable()) {\n" +
                        "                vars.set(\"static\", LuaClassGenerator.luaScript.get(\"vars\").get(\"static\"));\n" +
//                        "                vars.set(\"globals\", LuaClassGenerator.luaScript.get(\"vars\").get(\"globals\"));\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n\n";
        //TODO tzz don't forget Methods from Bundlable!
        //And tell: if you want to store sth like the currently targeted enemy, store the id and ...

        StringBuilder overrideMethods = new StringBuilder();

        Map<String, Method> methods = new HashMap<>();
        findAllMethodsToOverride(inputClass, Actor.class, methods);
        methods.remove("storeInBundle");
        methods.remove("restoreFromBundle");

        methods.remove("getCopy");
        methods.remove("onRenameLevelScheme");
        methods.remove("onDeleteLevelScheme");
        methods.remove("setDurationForBuff");
        methods.remove("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob");
        methods.remove("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY");
        methods.remove("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME");

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

            boolean useInvoke = paramTypes.length >= 1;

            overrideMethods.append(") {\n");
            overrideMethods.append("        LuaValue luaScript = CustomLuaObject.getScript(identifier);\n");
            overrideMethods.append("        if (luaScript != null && !luaScript.get(\"").append(m.getName()).append("\").isnil()) {\n");

            overrideMethods.append("            MethodOverride");
            if (paramTypes.length <= 10) overrideMethods.append('.');
            if (returnType == void.class) overrideMethods.append("Void");
            if (paramTypes.length <= 10) overrideMethods.append('A').append(paramTypes.length);
            if (returnType != void.class) {
                overrideMethods.append('<');
                if (returnType.isPrimitive()) {
                    if (returnType == int.class) overrideMethods.append("Integer");
                    else if (returnType == char.class) overrideMethods.append("Character");
                    else overrideMethods.append(Messages.capitalize(returnType.getSimpleName()));
                }
                else overrideMethods.append(returnType.getSimpleName());
                overrideMethods.append('>');
            }

            overrideMethods.append(" superMethod = (");
            for (int i = 0; i < paramTypes.length; i++) {
                overrideMethods.append('a').append(i);
                if (i + 1 < paramTypes.length) overrideMethods.append(", ");
            }
            overrideMethods.append(") -> super.");
            overrideMethods.append(m.getName()).append('(');
            for (int i = 0; i < paramTypes.length; i++) {
                if (paramTypes[i] != Object.class) {
                    overrideMethods.append('(');
                    overrideMethods.append(paramTypes[i].getSimpleName());
                    overrideMethods.append(')');
                    overrideMethods.append(' ');
                }
                overrideMethods.append('a').append(i);
                if (i+1 < paramTypes.length) overrideMethods.append(", ");
            }
            overrideMethods.append(");\n");

            overrideMethods.append("           ").append(returnString).append("luaScript.get(\"")
                    .append(m.getName()).append("\").").append(useInvoke ? "invoke" : "call").append('(');

            if (useInvoke) overrideMethods.append("new LuaValue[]{");
            overrideMethods.append("CoerceJavaToLua.coerce(this), ");
            overrideMethods.append("vars, ");
            overrideMethods.append("CoerceJavaToLua.coerce(superMethod), ");
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
                + initializers
                + implementLuaClassStuff
                + bundlingMethods
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



//    public static LuaTable updateTable(LuaTable returnTable, LuaTable dataSupplier) {
//        if (returnTable != null && dataSupplier != null) {
//            for (LuaValue k : dataSupplier.keys()) {
//                String keyAsString = k.toString();
//                LuaValue v = returnTable.get(keyAsString);
//                if (!v.isnil()) {
//                    returnTable.set(keyAsString, dataSupplier.get(keyAsString));
//                } else {
//
//                }
//            }
//        }
//        return returnTable;
//    }




//    private static final String VARS = "vars";
//
//    @Override
//    public void storeInBundle(Bundle bundle) {
//        super.storeInBundle(bundle);
//        if (vars != null) {
//            LuaClassGenerator.storeVarInBundle(bundle, vars, VARS);
//        }
//    }
//
//    //TODO tzz test: what happens if a bundlable is NOT stored as null in bundle, but IS in default script???
//    @Override
//    public void restoreFromBundle(Bundle bundle) {
//        super.restoreFromBundle(bundle);
//        LuaValue loaded = LuaClassGenerator.restoreVarFromBundle(bundle, VARS);
//        if (loaded != null && loaded.istable()) {
//            vars = LuaClassGenerator.updateTable(loaded.checktable(), vars);
//            if (LuaClassGenerator.luaScript != null && LuaClassGenerator.luaScript.get("vars").istable()) {
//              vars.set("static", LuaClassGenerator.luaScript.get("vars").get("static"));
//              vars.set("globals", LuaClassGenerator.luaScript.get("vars").get("globals"));
//          }
//        }
//    }

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
//                "public class " + inputClass.getSimpleName() + "_lua implements com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass {\n" +
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