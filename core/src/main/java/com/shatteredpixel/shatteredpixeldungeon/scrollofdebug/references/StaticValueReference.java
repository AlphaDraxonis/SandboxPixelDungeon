package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references;

public abstract class StaticValueReference<T> extends Reference {

    public StaticValueReference(Class<T> type, String name) {
        super(type, null, name);
    }

    @Override
    public abstract T getValue();

    @Override
    public Object valueViaParent() {
        return getValue();
    }

}