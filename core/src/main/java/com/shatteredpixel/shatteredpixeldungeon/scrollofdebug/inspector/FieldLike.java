package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface FieldLike extends Member {

	Class<?> getType();

	Object get(Object obj) throws IllegalAccessException;

	void set(Object obj, Object value) throws IllegalAccessException;

	default Type[] getActualTypeArguments() {
		return null;
	}

	class RealField implements FieldLike {

		private final Field field;

		public RealField(Field field) {
			this.field = field;
		}

		public Field getField() {
			return field;
		}

		@Override
		public Class<?> getDeclaringClass() {
			return field.getDeclaringClass();
		}

		@Override
		public String getName() {
			return field.getName();
		}

		@Override
		public int getModifiers() {
			return field.getModifiers();
		}

		@Override
		public boolean isSynthetic() {
			return field.isSynthetic();
		}

		@Override
		public Class<?> getType() {
			return field.getType();
		}

		@Override
		public Type[] getActualTypeArguments() {
			Type type = field.getGenericType();
			if (type instanceof ParameterizedType) {
				return ((ParameterizedType) type).getActualTypeArguments();
			}
			return FieldLike.super.getActualTypeArguments();
		}

		@Override
		public Object get(Object obj) throws IllegalAccessException {
			field.setAccessible(true);
			return field.get(obj);
		}

		@Override
		public void set(Object obj, Object value) throws IllegalAccessException {
			field.setAccessible(true);
			field.set(obj, value);
		}
	}

	abstract class FakeField implements FieldLike {

		private final Class<?> clazz;
		private final String name;
		private final int modifiers;

		protected FakeField(Class<?> clazz, String name, int modifiers) {
			this.clazz = clazz;
			this.name = name;
			this.modifiers = modifiers;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getModifiers() {
			return modifiers;
		}

		@Override
		public Class<?> getType() {
			return clazz;
		}

		@Override
		public boolean isSynthetic() {
			return true;
		}

		@Override
		public Class<?> getDeclaringClass() {
			return null;
		}
	}

}