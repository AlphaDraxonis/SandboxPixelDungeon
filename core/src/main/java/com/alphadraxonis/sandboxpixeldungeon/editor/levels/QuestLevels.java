package com.alphadraxonis.sandboxpixeldungeon.editor.levels;

import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public enum QuestLevels implements LevelSchemeLike {

    MINING(3, "mining");

    public final int ID;
    private final String name;

    QuestLevels(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public static QuestLevels get(int id) {
        switch (id) {
            case 3: return MINING;
        }
        return null;
    }

    public String getName() {
        return Messages.get(QuestLevels.class, name);
    }

    @Override
    public String toString() {
        return getName();
    }
}