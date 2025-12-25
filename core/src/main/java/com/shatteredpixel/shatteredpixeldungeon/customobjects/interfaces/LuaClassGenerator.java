/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.blueprints.CustomCharSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaRestrictionProxy;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.NotAllowedInLua;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public final class LuaClassGenerator {

	//Android APi 21+, requires at least Android 5.0, released 10 years ago, android 4 has market share of about 0.3%
	//ClassLoadingStrategy strategy = new AndroidClassLoadingStrategy.Wrapping(context.getDir(
	//  "generated",
	//  Context.MODE_PRIVATE))
	
	//useful for loading game progresses as preview
	public static boolean skipConversion = false;

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
        return skipConversion
		? originalClass
		: (Class<? extends T>) CACHE.findOrInsert(LuaClassGenerator.class.getClassLoader(), originalClass, () -> build(originalClass));
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

		//TODO it is possible to improve the performance here by about 300-400% if only methods are intercepted that are actually overridden by a script
		//the problem is that it is challenging to find out which methods that are, and it may change every time a script is changed
		for (Method method : methodsToIntercept(originalClass)) {

			//define method for calling super
				builder = defineMethodForCallingSuper(builder, method);

			//override own method
				builder = builder.method(ElementMatchers.named(method.getName()).and(ElementMatchers.takesArguments(method.getParameterTypes())))
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
		Set<String> namesOfMethodsToRemove = new HashSet<>();
		if (Char.class.isAssignableFrom(originalClass)) {
			
			findAllMethodsToOverride(originalClass, Actor.class, methods);
			
			namesOfMethodsToRemove.add("setDurationForFlavourBuff");
			
		} else if (Level.class.isAssignableFrom(originalClass)) {
			
			findAllMethodsToOverride(originalClass, Level.class, methods);
			
			namesOfMethodsToRemove.add("width");
			namesOfMethodsToRemove.add("height");
			namesOfMethodsToRemove.add("adjacent");
			namesOfMethodsToRemove.add("distance");
			namesOfMethodsToRemove.add("trueDistance");
			namesOfMethodsToRemove.add("cellToPoint");
			namesOfMethodsToRemove.add("pointToCell");
			namesOfMethodsToRemove.add("tilesTex");
			namesOfMethodsToRemove.add("waterTex");
			namesOfMethodsToRemove.add("getTransition");
			namesOfMethodsToRemove.add("getTransitionFromSurface");
			namesOfMethodsToRemove.add("setLevelScheme");
			namesOfMethodsToRemove.add("addVisuals");
			namesOfMethodsToRemove.add("addWallVisuals");
			namesOfMethodsToRemove.add("cleanWalls");
			namesOfMethodsToRemove.add("cleanWallCell");
			namesOfMethodsToRemove.add("removeSimpleCustomTile");
			namesOfMethodsToRemove.add("findMob");
			namesOfMethodsToRemove.add("addRespawner");
			namesOfMethodsToRemove.add("buildFlagMaps");
			
			namesOfMethodsToRemove.add("isPassable");
			namesOfMethodsToRemove.add("isPassableAlly");
			namesOfMethodsToRemove.add("isPassableHero");
			namesOfMethodsToRemove.add("isPassableMob");
			namesOfMethodsToRemove.add("getPassableVar");
			namesOfMethodsToRemove.add("getPassableHeroVar");
			namesOfMethodsToRemove.add("getPassableMobVar");
			namesOfMethodsToRemove.add("getPassableAndAvoidVar");
			namesOfMethodsToRemove.add("getPassableAndAvoidVarForBoth");
			
			// CustomLevel
			namesOfMethodsToRemove.add("updateTransitionCells");
			
		} else if (CharSprite.class.isAssignableFrom(originalClass)) {
			
			findAllMethodsToOverride(originalClass, Visual.class, methods);
			
			namesOfMethodsToRemove.add("texture");
		} else {
			//null means all classes except Object.class
			findAllMethodsToOverride(originalClass, null, methods);
		}
		
		namesOfMethodsToRemove.add("storeInBundle");
		namesOfMethodsToRemove.add("restoreFromBundle");
		
		//will be force-added later
		namesOfMethodsToRemove.add("name");
		
		//interface NameCustomizable
		namesOfMethodsToRemove.add("getCustomName");
		namesOfMethodsToRemove.add("setCustomName");
		
		if (GameObject.class.isAssignableFrom(originalClass)) {
			namesOfMethodsToRemove.add("onRenameLevelScheme");
			namesOfMethodsToRemove.add("onDeleteLevelScheme");
			namesOfMethodsToRemove.add("initAsInventoryItem");
		}
		
		namesOfMethodsToRemove.add("getCopy");
		
		List<Method> result = new ArrayList<>();
		for (Method m : methods.values()) {
			if (!namesOfMethodsToRemove.contains(m.getName())) {
				result.add(m);
			}
		}
		
		return result;
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
			onStoreInBundle(self, bundle);
			originalMethod.call();
		}
	}

	public static class InterceptorRestoreFromBundleWithSuper {
		@RuntimeType
		public static void restoreFromBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle, @SuperCall Callable<?> originalMethod) throws Exception {
			onRestoreFromBundle(self, bundle);
			originalMethod.call();
		}
	}
	
	public static class InterceptorStoreInBundle {
		@RuntimeType
		public static void storeInBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle) throws Exception {
			onStoreInBundle(self, bundle);
		}
	}
	
	public static class InterceptorRestoreFromBundle {
		@RuntimeType
		public static void restoreFromBundle(@This LuaCustomObjectClass self, @Argument(0) Bundle bundle) throws Exception {
			onRestoreFromBundle(self, bundle);
		}
	}
	
	//These should actually have been declared in LuaCustomObjectClass, but that throws an AbstractMethodError in signed Android apk
	private static void onStoreInBundle(LuaCustomObjectClass self, Bundle bundle) {
		if (self instanceof CustomGameObjectClass) {
			bundle.put("inherit_stats", ((CustomGameObjectClass) self).getInheritStats());
		}
		
		bundle.put("identifier", self.getIdentifier());
		if (self.getVars() != null && !CustomDungeon.isEditing()) {
			LuaManager.storeVarInBundle(bundle, self.getVars(), LuaCustomObjectClass.VARS);
		}
	}
	private static void onRestoreFromBundle(LuaCustomObjectClass self, Bundle bundle) {
		if (self instanceof CustomGameObjectClass) {
			((CustomGameObjectClass) self).setInheritStats(bundle.getBoolean("inherit_stats"));
		}
		self.setIdentifier(bundle.getInt("identifier"));
		
		if (!CustomDungeon.isEditing()) {
			LuaValue script = CustomObjectManager.getScript(self.getIdentifier());
			if (script != null) {
				if (script.get("vars").isnil()) script.set("vars", new LuaTable());
				if (script.get("static").isnil()) script.set("static", new LuaTable());
				
				if (script.get("vars").istable()) {
					self.setVars( LuaManager.deepCopyLuaValue(script.get("vars")).checktable() );
					
					LuaValue loaded = LuaManager.restoreVarFromBundle(bundle, LuaCustomObjectClass.VARS);
					if (loaded != null && loaded.istable()) self.setVars( loaded.checktable() );
					if (script.get("static").istable()) self.getVars().set("static", script.get("static"));
				}
			}
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
					//TODO possibly delegated to wrong method here in case of overloading!!!
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
            if (Modifier.isPrivate(mods) || Modifier.isFinal(mods) || Modifier.isStatic(mods) || m.isAnnotationPresent(NotAllowedInLua.class)) {
                //don't override these
                continue;
            }
			
			//prevent duplicate method signature
			String identifier = m.getName();
			for (Class<?> paramType : m.getParameterTypes()) {
				identifier += "," + paramType;
			}
			currentMethods.put(identifier, m);
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