package com.alphadraxonis.sandboxpixeldungeon.editor.util;

@FunctionalInterface
public interface BiPredicate<T,U> {
    public boolean test(T t, U u);
}