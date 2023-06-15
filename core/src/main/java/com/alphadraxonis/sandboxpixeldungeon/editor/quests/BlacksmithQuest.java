package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.watabou.utils.Bundle;

public class BlacksmithQuest extends Quest{

    public boolean reforged;


    public static BlacksmithQuest createRandom(LevelScheme levelScheme) {
        BlacksmithQuest quest = new BlacksmithQuest();

        // decide between 0 or 1 for quest type.
        quest.type = levelScheme.getBlacksmithQuest();

        levelScheme.roomsToSpawn.add(BlacksmithRoom.class);

        return quest;
    }

    @Override
    public void complete() {
        super.complete();
        reforged = false;

        addScore(2,3000);
    }

    public boolean reforged(){
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
        bundle.put(REFORGED,reforged);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        reforged = bundle.getBoolean(REFORGED);
    }
}