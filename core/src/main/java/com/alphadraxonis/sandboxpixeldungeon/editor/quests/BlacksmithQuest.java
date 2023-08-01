package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.sprites.BatSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class BlacksmithQuest extends Quest {

    public static final int GOLD = 0, BLOOD = 1;

    public boolean reforged;

    @Override
    public void initRandom(LevelScheme levelScheme) {
        if (type == -1) type = levelScheme.getBlacksmithQuest();
    }

    @Override
    public void complete() {
        super.complete();
        reforged = false;

        addScore(2, 3000);
    }

    public boolean reforged() {
        return reforged;
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.TROLL);
    }


    private static final String REFORGED = "reforged";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(REFORGED, reforged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        reforged = bundle.getBoolean(REFORGED);
    }

    @Override
    public int getNumQuests() {
        return 2;
    }

    @Override
    public Image getIcon() {
        switch (type) {
            case GOLD:
                return new ItemSprite(ItemSpriteSheet.ORE);
            case BLOOD:
                return new BatSprite();
        }
        return null;
    }
    @Override
    public String getMessageString() {
        return getMessageString(type);
    }
    @Override
    public String getMessageString(int type) {
        if (type == GOLD) return "gold";
        if (type == BLOOD) return "blood";
        return null;
    }

}