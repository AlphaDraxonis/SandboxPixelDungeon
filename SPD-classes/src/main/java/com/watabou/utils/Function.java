package com.watabou.utils;

@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}