package com.shatteredpixel.shatteredpixeldungeon.editor.lua;

@FunctionalInterface
public interface MethodOverride<R> {
    R call(Object... args);


    @FunctionalInterface
    interface A0<R> {
        R call();
    }
    @FunctionalInterface
    interface A1<R> {
        R call(Object arg1);
    }
    @FunctionalInterface
    interface A2<R> {
        R call(Object arg1, Object arg2);
    }
    @FunctionalInterface
    interface A3<R> {
        R call(Object arg1, Object arg2, Object arg3);
    }
    @FunctionalInterface
    interface A4<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4);
    }
    @FunctionalInterface
    interface A5<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);
    }
    @FunctionalInterface
    interface A6<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6);
    }
    @FunctionalInterface
    interface A7<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7);
    }
    @FunctionalInterface
    interface A8<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8);
    }
    @FunctionalInterface
    interface A9<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9);
    }
    @FunctionalInterface
    interface A10<R> {
        R call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10);
    }


    @FunctionalInterface
    interface Void {
        void call(Object... args);
    }
    @FunctionalInterface
    interface VoidA0 {
        void call();
    }
    @FunctionalInterface
    interface VoidA1 {
        void call(Object arg1);
    }
    @FunctionalInterface
    interface VoidA2 {
        void call(Object arg1, Object arg2);
    }
    @FunctionalInterface
    interface VoidA3 {
        void call(Object arg1, Object arg2, Object arg3);
    }
    @FunctionalInterface
    interface VoidA4 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4);
    }
    @FunctionalInterface
    interface VoidA5 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5);
    }
    @FunctionalInterface
    interface VoidA6 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6);
    }
    @FunctionalInterface
    interface VoidA7 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7);
    }
    @FunctionalInterface
    interface VoidA8 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8);
    }
    @FunctionalInterface
    interface VoidA9 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9);
    }
    @FunctionalInterface
    interface VoidA10 {
        void call(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10);
    }

}