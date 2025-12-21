package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;

public enum QuestLevels implements LevelSchemeLike {

    MINING(3, "mining"),
    IMP(4, "imp");

    public final int ID;
    private final String name;

    QuestLevels(int ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    public static QuestLevels get(int id) {
        switch (id) {
            case 3: return MINING;
            case 4: return IMP;
        }
        return null;
    }

    public String getName() {
        return Messages.get(QuestLevels.class, name);
    }

    public static int getRegion(int id) {
        switch (id) {
            case 3: return LevelScheme.REGION_CAVES;
            case 4: return LevelScheme.REGION_CITY;
        }
        return LevelScheme.REGION_NONE;
    }

    public int getRegion() {
        return getRegion(ID);
    }

    public Image createForegroundIcon() {
        switch (this) {
            case MINING: return new ItemSprite(ItemSpriteSheet.PICKAXE);
            case IMP: return new ItemSprite(ItemSpriteSheet.TOKEN);
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
