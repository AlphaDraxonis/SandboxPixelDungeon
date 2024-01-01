package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

public abstract class StaticReference<T> extends Reference {

    public StaticReference(Class<T> type, String name) {
        super(type, null, name);
    }

    @Override
    public abstract T getValue();

}