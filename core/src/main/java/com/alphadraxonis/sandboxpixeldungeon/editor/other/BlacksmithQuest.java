package com.alphadraxonis.sandboxpixeldungeon.editor.other;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.watabou.utils.Bundle;

public class BlacksmithQuest extends Quest{

    public boolean reforged;


    public static BlacksmithQuest createRandom(LevelScheme levelScheme) {
        BlacksmithQuest quest = new BlacksmithQuest();

        quest.given = false;

        // decide between 0 or 1 for quest type.
        quest.type = levelScheme.getBlacksmithQuest();

        levelScheme.roomsToSpawn.add(BlacksmithRoom.class);

        return quest;
    }

    @Override
    public boolean completed() {
        return false;
    }

    @Override
    public void complete() {
        processed = true;
        reforged = false;

        addScore(2,3000);
    }

    public boolean reforged(){
        return reforged;
    }


    private static final String REFORGED = "reforged";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(REFORGED,reforged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        reforged = bundle.getBoolean(REFORGED);
    }
}