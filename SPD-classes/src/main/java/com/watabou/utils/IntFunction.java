package com.watabou.utils;

@FunctionalInterface
public interface IntFunction<T> {
    int apply(T t);
}