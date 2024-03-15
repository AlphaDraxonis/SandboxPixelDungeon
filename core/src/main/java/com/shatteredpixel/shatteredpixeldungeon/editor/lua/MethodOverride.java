package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

@FunctionalInterface
public interface MethodOverride<R> {
    R call(Object... args);
}