package com.watabou.utils;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}