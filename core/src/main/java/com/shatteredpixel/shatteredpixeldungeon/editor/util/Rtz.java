package com.shatteredpixel.shatteredpixeldungeon.editor.util;

//Debug class, the names are chosen so that they can be typed and searched faster
public final class Rtz {

    private Rtz() {
    }

    public static void z(long info) {
        print(Long.toString(info));
    }

    public static void z(int info) {
        print(Integer.toString(info));
    }

    public static void z(float info) {
        print(Float.toString(info));
    }

    public static void z(String info) {
        print(info);
    }

    public static void z(Object[] info) {
        StringBuilder b = new StringBuilder("{ ");
        if (info != null) for (Object o : info) b.append(o.toString()).append(",");
        b.append(" }");
        print(b.toString());
    }

    public static void z(Object info) {
        if (info == null) print("null");
        else print(info.toString());
    }

    private static void print(String info) {
        StackTraceElement element = new Exception().getStackTrace()[2];
        System.err.println(info + " at " + element);
    }
}