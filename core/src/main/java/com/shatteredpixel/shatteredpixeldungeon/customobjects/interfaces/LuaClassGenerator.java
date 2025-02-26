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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.TypeCache;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public final class LuaClassGenerator {

	//Android APi 21+, requires at least Android 5.0, released 10 years ago, android 4 has market share of about 0.3%
	//ClassLoadingStrategy strategy = new AndroidClassLoadingStrategy.Wrapping(context.getDir(
	//  "generated",
	//  Context.MODE_PRIVATE))

    private LuaClassGenerator() {
    }

    /**
     * Each class only has one lua version!
     */
    private static final TypeCache<Class<?>> CACHE = new TypeCache<>();

    /**
     * @return a class capable of overriding methods with a lua script
     */
    public static <T> Class<? extends T> luaUserContentClass(Class<T> originalClass) {
        return (Class<? extends T>) CACHE.findOrInsert(LuaClassGenerator.class.getClassLoader(), originalClass, () -> build(originalClass));
    }

	private static <T> Class<? extends T> build(Class<T> originalClass) {
		DynamicType.Builder<T> builder =
				new ByteBuddy()
						.subclass(originalClass)
						.implement(LuaCustomObjectClass.luaInterfaceClass(originalClass));

		builder = builder
				.defineField("vars", LuaTable.class, Visibility.PRIVATE)
				.defineMethod("setVars", void.class, Visibility.PUBLIC).withParameters(LuaTable.class)
				.intercept(FieldAccessor.ofField("vars"))
				.defineMethod("getVars", LuaTable.class, Visibility.PUBLIC)
				.intercept(FieldAccessor.ofField("vars"));

		if (!DungeonScript.class.isAssignableFrom(originalClass)) {

			builder = builder
					.defineField("identifier", int.class, Visibility.PRIVATE)
					.defineMethod("setIdentifier", void.class, Visibility.PUBLIC).withParameters(int.class)
					.intercept(FieldAccessor.ofField("identifier"))
					.defineMethod("getIdentifier", int.class, Visibility.PUBLIC)
					.intercept(FieldAccessor.ofField("identifier"));

			builder = builder
					.defineField("inheritStats", boolean.class, Visibility.PRIVATE).value(true)
					.defineMethod("setInheritStats", void.class, Visibility.PUBLIC).withParameters(boolean.class)
					.intercept(FieldAccessor.ofField("inheritStats"))
					.defineMethod("getInheritStats", boolean.class, Visibility.PUBLIC)
					.intercept(FieldAccessor.ofField("inheritStats"));

			builder = builder

					.defineMethod("newInstance", LuaCustomObjectClass.class, Visibility.PUBLIC)
					.intercept(MethodDelegation.to(InterceptorNewInstance.class));

			if (Bundlable.class.isAssignableFrom(originalClass)) {
				builder = builder
						.defineMethod("storeInBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
						.intercept(MethodDelegation.to(InterceptorStoreInBundleWithSuper.class))
						.defineMethod("restoreFromBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
						.intercept(MethodDelegation.to(InterceptorRestoreFromBundleWithSuper.class)); // call super after, so we can restore the id
//				builder = builder
//						.defineMethod("storeInBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
//						.intercept(SuperMethodCall.INSTANCE // first call super
//								.andThen(Advice.to(AdviceStoreInBundle.class)))
//						.defineMethod("restoreFromBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
//						.intercept(MethodDelegation.to(AdviceStoreInBundle.class)
//								.andThen(SuperMethodCall.INSTANCE)); // call super after, so we can restore the id
			} else {
				//don't call super hehehe
				builder = builder
						.defineMethod("storeInBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
						.intercept(MethodDelegation.to(InterceptorStoreInBundle.class))
						.defineMethod("restoreFromBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
						.intercept(MethodDelegation.to(InterceptorRestoreFromBundle.class));
//				builder = builder
//						.defineMethod("storeInBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
//						.intercept(MethodDelegation.to(AdviceStoreInBundle.class))
//						.defineMethod("restoreFromBundle", void.class, Visibility.PUBLIC).withParameter(Bundle.class)
//						.intercept(MethodDelegation.to(AdviceRestoreFromBundle.class));
			}

		} else {
			builder = builder
					.defineMethod("setIdentifier", void.class, Visibility.PUBLIC).withParameters(int.class)
					.intercept(StubMethod.INSTANCE)
					.defineMethod("getIdentifier", int.class, Visibility.PUBLIC)
					.intercept(FixedValue.value(-1));

			builder = builder
					.defineMethod("newInstance", LuaCustomObjectClass.class, Visibility.PUBLIC)
					.intercept(MethodDelegation.to(InterceptorDungeonScriptNewInstance.class));
		}


		builder = interceptMethods(builder, originalClass);

		return builder
				.make()
				.load(LuaClassGenerator.class.getClassLoader(), Game.platform.getClassLoadingStrategy())
				.getLoaded();
	}

	private static <T> DynamicType.Builder<T> interceptMethods(DynamicType.Builder<T> builder, Class<T> originalClass) {

		for (Method method : methodsToIntercept(originalClass)) {

			//define method for calling super
				builder = defineMethodForCallingSuper(builder, method);

			//override own method
				builder = builder.method(ElementMatchers.named(method.getName()))
						.intercept(MethodDelegation.to(methodInterceptor(originalClass)));

		}

		//CharSprite specifically overrides  texture(Object)  so it returns what the user has set
		if (CharSprite.class.isAssignableFrom(originalClass)) {
			builder = builder.method(ElementMatchers.named("texture").and(ElementMatchers.takesArguments(Object.class)))
					.intercept(MethodDelegation.to(InterceptorCharSpriteTexture.class));
		}

		try {
			builder = defineMethodForCallingSuper(builder, originalClass.getMethod("name"));

			//override name method to return the name of the custom object
			builder = builder.method(ElementMatchers.named("name"))
					.intercept(MethodDelegation.to(InterceptorName.class));
		} catch (NoSuchMethodException e) {
			//if such method doesn't exist, just ignore it
		}

		return builder;
	}

	private static <T> DynamicType.Builder<T> defineMethodForCallingSuper(DynamicType.Builder<T> builder, Method method) {
		String superMethodName = "super_" + method.getName();
		Class<?> returnType = method.getReturnType();
		Class<?>[] parameterTypes = method.getParameterTypes();

		try {
			builder = builder
					.defineMethod(superMethodName, returnType, Visibility.PUBLIC).withParameters(parameterTypes)
					.intercept(
							MethodCall.invoke(method)
									.onSuper()
									.withAllArguments()
					);
		} catch (Exception e) {
			Game.reportException(e);
		}
		return builder;
	}

    //lua code cannot override these methods
    private static Collection<Method> methodsToIntercept(Class<?> originalClass) {
		Map<String, Method> methods = new HashMap<>();

        if (Char.class.isAssignableFrom(originalClass)) {

			findAllMethodsToOverride(originalClass, Actor.class, methods);

			methods.remove("onRenameLevelScheme");
			methods.remove("onDeleteLevelScheme");
			methods.remove("setDurationForBuff");
			methods.remove("moveBuffSilentlyToOtherChar_ACCESS_ONLY_FOR_HeroMob");
			methods.remove("getPropertiesVar_ACCESS_ONLY_FOR_EDITING_UI");
			methods.remove("spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY");
			methods.remove("setFirstAddedToTrue_ACCESS_ONLY_FOR_CUSTOMLEVELS_THAT_ARE_ENTERED_FOR_THE_FIRST_TIME");
		}

		else if (Level.class.isAssignableFrom(originalClass)) {

			findAllMethodsToOverride(originalClass, Level.class, methods);

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
			methods.remove("setLevelScheme");

			// CustomLevel
			methods.remove("updateTransitionCells");
		}
		else if (CharSprite.class.isAssignableFrom(originalClass)) {

			findAllMethodsToOverride(originalClass, Visual.class, methods);

			methods.remove("texture");
		}
		else {
			//null means all classes except Object.class
			findAllMethodsToOverride(originalClass, null, methods);
		}

		methods.remove("storeInBundle");
		methods.remove("restoreFromBundle");
		
		methods.remove("getCopy");

		//will be force-added later
		methods.remove("name");

		//interface NameCustomizable
		methods.remove("getCustomName");
		methods.remove("setCustomName");

		return methods.values();
    }

    public static class InterceptorNewInstance {
		@RuntimeType
        public static LuaCustomObjectClass newInstance(@This Object self) {
            if (self instanceof Copyable) return ((Copyable<? extends LuaCustomObjectClass>) self).getCopy();

			LuaCustomObjectClass instance = (LuaCustomObjectClass) Reflection.newInstance(self.getClass());
			instance.setIdentifier(((LuaCustomObjectClass) self).getIdentifier());
			return instance;
        }
    }

	public static class InterceptorDungeonScriptNewInstance {
		@RuntimeType
		public static LuaCustomObjectClass newInstance(@This Object self) {
			return Reflection.newInstance(((Class<? extends LuaCustomObjectClass>) luaUserContentClass(DungeonScript.class)));
		}
	}
	
	public static class InterceptorStoreInBundleWithSuper {
		@RuntimeType
		public static void storeInBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle, @SuperCall Callable<?> originalMethod) throws Exception {
			self.onStoreInBundle(bundle);
			originalMethod.call();
		}
	}

	public static class InterceptorRestoreFromBundleWithSuper {
		@RuntimeType
		public static void restoreFromBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle, @SuperCall Callable<?> originalMethod) throws Exception {
			self.onRestoreFromBundle(bundle);
			originalMethod.call();
		}
	}
	
	public static class InterceptorStoreInBundle {
		@RuntimeType
		public static void storeInBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle) throws Exception {
			self.onStoreInBundle(bundle);
		}
	}
	
	public static class InterceptorRestoreFromBundle {
		@RuntimeType
		public static void restoreFromBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle) throws Exception {
			self.onRestoreFromBundle(bundle);
		}
	}


	private static final Map<Class<?>, MethodInterceptor> METHOD_INTERCEPTOR_CACHE = new HashMap<>(5);
	private static MethodInterceptor methodInterceptor(Class<?> originalClass) {
		MethodInterceptor result = METHOD_INTERCEPTOR_CACHE.get(originalClass);

		if (result == null) {
			result = DungeonScript.class.isAssignableFrom(originalClass) ? new DungeonScriptMethodInterceptor()
					: new MethodInterceptor();
			METHOD_INTERCEPTOR_CACHE.put(originalClass, result);
		}

		return result;
	}

	public static class MethodInterceptor {

		@RuntimeType
		public Object intercept(@This LuaCustomObjectClass self, @Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> originalMethod) throws Exception {
			LuaValue script = getScript(self);
			if (script != null && script.get(method.getName()).isfunction()) {
				try {
					return LuaRestrictionProxy.coerceLuaToJava( script.get(method.getName()).invoke(convertArgsToLua(self, self.getVars(), args)).arg1(), method.getReturnType() );
				} catch (LuaError error) {
					Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error)));
				}
			}
			return originalMethod.call();
		}

		protected LuaValue getScript(LuaCustomObjectClass self) {
			return CustomObjectManager.getScript(self.getIdentifier());
		}

		private static LuaValue[] convertArgsToLua(Object self, LuaValue vars, Object[] args) {
			LuaValue[] luaArgs = new LuaValue[args.length + 2];
			int i = 0;
			luaArgs[i++] = LuaRestrictionProxy.wrapObject(self);
			luaArgs[i++] = vars == null ? LuaValue.NIL : vars;
			for (; i < luaArgs.length; i++) {
				luaArgs[i] = LuaRestrictionProxy.wrapObject(args[i-2]);
			}
			return luaArgs;
		}
	}

	public static class DungeonScriptMethodInterceptor extends MethodInterceptor {
		@Override
		protected LuaValue getScript(LuaCustomObjectClass self) {
			return ((DungeonScript) self).getScript();
		}
	}

	public static final class InterceptorCharSpriteTexture {
		@RuntimeType
		public static Object intercept(@This CustomObjectClass self, @SuperCall Callable<?> superCall, @Argument(0) Object arg, @SuperMethod Method superMethod) throws Exception {
			if (self.getIdentifier() == 0) return superCall.call();
			String resourcePath = CustomObjectManager.getUserContent(self.getIdentifier(), CustomCharSprite.class).getResourcePath();
			return superMethod.invoke(self,
					resourcePath == null
							? (arg != null ? arg : ((CharSprite) self).texture)
							: CustomObject.filePathToCreateImage(resourcePath));
		}
	}

	public static final class InterceptorName {
		@RuntimeType
		public static Object intercept(@This LuaCustomObjectClass self, @Origin Method method, @AllArguments Object[] args, @SuperCall Callable<?> originalMethod) throws Exception {
			if (self.getIdentifier() == 0) return originalMethod.call();

			LuaValue script = CustomObjectManager.getScript(self.getIdentifier());
			if (script != null && script.get(method.getName()).isfunction()) {
				try {
					return LuaRestrictionProxy.coerceLuaToJava( script.get(method.getName()).invoke(MethodInterceptor.convertArgsToLua(self, self.getVars(), args)).arg1(), method.getReturnType() );
				} catch (LuaError error) {
					Game.runOnRenderThread(() -> DungeonScene.show(new WndError(error)));
				}
			}
			
			if (!CustomDungeon.isEditing()) {
				return originalMethod.call();
			}

			CustomObject obj =  CustomObjectManager.getUserContent(self.getIdentifier(), null);
			return obj == null ? "error: " + self.getIdentifier() : obj.getName();
		}
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
			Class<?> superClass = currentClass.getSuperclass();
			if (highestClass == null && superClass == Object.class) {
				return;
			}
            findAllMethodsToOverride(superClass, highestClass, currentMethods);
        }
    }

}