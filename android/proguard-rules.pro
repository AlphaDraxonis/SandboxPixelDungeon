# retain these to support class references for the bundling and translation systems
-keepnames class com.shatteredpixel.** { *; }
-keepnames class com.watabou.** { *; }

# keep members of classes that are instantiated via reflection
-keepclassmembers class * extends com.watabou.glscripts.Script
-keepclassmembers class * implements com.watabou.utils.Bundlable

# retained to support meaningful stack traces
# note that the mapping file must be referenced in order to make sense of line numbers
# mapping file can be found in core/build/outputs/mapping after running a release build
-keepattributes SourceFile,LineNumberTable

# Lua stuff
-keep class org.luaj.vm2.** { *; }

#-keep class com.shatteredpixel.shatteredpixeldungeon.editor.lua.MethodOverride$* { *; }

-keep class * extends com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite {
    public <fields>;
    public <methods>;
}
-keep class * implements com.watabou.utils.Bundlable {
    public <fields>;
    public <methods>;
}
-keep enum com.shatteredpixel.shatteredpixeldungeon.** {
    public <fields>;
    public <methods>;
}
-keepclassmembers class * {
    @com.shatteredpixel.shatteredpixeldungeon.editor.lua.annotations.KeepProguard *;
}

# libGDX stuff
-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*

# needed for libGDX skin reflection used in text fields. Perhaps just don't use skin?
-keep class com.badlogic.gdx.graphics.Color { *; }
-keep class com.badlogic.gdx.scenes.scene2d.ui.TextField$TextFieldStyle { *; }

# needed for libGDX controllers
-keep class com.badlogic.gdx.controllers.android.AndroidControllers { *; }

-keepclassmembers class com.badlogic.gdx.backends.android.AndroidInput* {
    <init>(com.badlogic.gdx.Application, android.content.Context, java.lang.Object, com.badlogic.gdx.backends.android.AndroidApplicationConfiguration);
}

-keepclassmembers class com.badlogic.gdx.physics.box2d.World {
    boolean contactFilter(long, long);
    void    beginContact(long);
    void    endContact(long);
    void    preSolve(long, long);
    void    postSolve(long, long);
    boolean reportFixture(long);
    float   reportRayFixture(long, float, float, float, float, float);
}


#Byte Buddy
-keepclassmembers class * {
    @net.bytebuddy.implementation.bind.annotation.RuntimeType *;
}
-keep class net.bytebuddy.implementation.bind.annotation.** {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.cf.direct.DirectClassFile {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.cf.direct.StdAttributeFactory {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.dex.DexOptions {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.dex.cf.CfOptions {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.dex.cf.CfTranslator {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.dex.file.ClassDefItem {
    public <fields>;
    public <methods>;
}
-keep class com.android.dx.dex.file.DexFile {
    public <fields>;
    public <methods>;
}


#The Android Gradle plugin said I should add this, and because it wouldn't work otherwise, I added this without knowing why it is necessary...
-dontwarn javax.script.AbstractScriptEngine
-dontwarn javax.script.Bindings
-dontwarn javax.script.Compilable
-dontwarn javax.script.CompiledScript
-dontwarn javax.script.ScriptContext
-dontwarn javax.script.ScriptEngine
-dontwarn javax.script.ScriptEngineFactory
-dontwarn javax.script.ScriptException
-dontwarn javax.script.SimpleBindings
-dontwarn javax.script.SimpleScriptContext
-dontwarn org.apache.bcel.classfile.Field
-dontwarn org.apache.bcel.classfile.JavaClass
-dontwarn org.apache.bcel.classfile.Method
-dontwarn org.apache.bcel.generic.AASTORE
-dontwarn org.apache.bcel.generic.ALOAD
-dontwarn org.apache.bcel.generic.ANEWARRAY
-dontwarn org.apache.bcel.generic.ASTORE
-dontwarn org.apache.bcel.generic.ArrayInstruction
-dontwarn org.apache.bcel.generic.ArrayType
-dontwarn org.apache.bcel.generic.BasicType
-dontwarn org.apache.bcel.generic.BranchHandle
-dontwarn org.apache.bcel.generic.BranchInstruction
-dontwarn org.apache.bcel.generic.ClassGen
-dontwarn org.apache.bcel.generic.CompoundInstruction
-dontwarn org.apache.bcel.generic.ConstantPoolGen
-dontwarn org.apache.bcel.generic.FieldGen
-dontwarn org.apache.bcel.generic.FieldInstruction
-dontwarn org.apache.bcel.generic.GETSTATIC
-dontwarn org.apache.bcel.generic.GOTO
-dontwarn org.apache.bcel.generic.IFEQ
-dontwarn org.apache.bcel.generic.IFNE
-dontwarn org.apache.bcel.generic.Instruction
-dontwarn org.apache.bcel.generic.InstructionConstants
-dontwarn org.apache.bcel.generic.InstructionFactory
-dontwarn org.apache.bcel.generic.InstructionHandle
-dontwarn org.apache.bcel.generic.InstructionList
-dontwarn org.apache.bcel.generic.InvokeInstruction
-dontwarn org.apache.bcel.generic.LineNumberGen
-dontwarn org.apache.bcel.generic.LocalVariableGen
-dontwarn org.apache.bcel.generic.LocalVariableInstruction
-dontwarn org.apache.bcel.generic.MethodGen
-dontwarn org.apache.bcel.generic.NEW
-dontwarn org.apache.bcel.generic.ObjectType
-dontwarn org.apache.bcel.generic.PUSH
-dontwarn org.apache.bcel.generic.PUTSTATIC
-dontwarn org.apache.bcel.generic.ReturnInstruction
-dontwarn org.apache.bcel.generic.StackInstruction
-dontwarn org.apache.bcel.generic.Type
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn javax.annotation.Nonnull