package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

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

    public static int getRegion(int id) {
        switch (id) {
            case 3: return LevelScheme.REGION_CAVES;
        }
        return LevelScheme.REGION_NONE;
    }

    public int getRegion() {
        return getRegion(ID);
    }

    public Image createForegroundIcon() {
        switch (this) {
            case MINING: return new ItemSprite(ItemSpriteSheet.PICKAXE);
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}