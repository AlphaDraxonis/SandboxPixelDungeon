package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class BlacksmithQuest extends Quest {

    public static final int GOLD = 0, BLOOD = 1;

    public boolean reforged;

    @Override
    public void initRandom(LevelScheme levelScheme) {
        if (type == BASED_ON_DEPTH){
            type = levelScheme.generateBlacksmithQuest();
            levelScheme.roomsToSpawn.add(new BlacksmithRoom());
        }
        else if (type == RANDOM) type = Random.Int(2);
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