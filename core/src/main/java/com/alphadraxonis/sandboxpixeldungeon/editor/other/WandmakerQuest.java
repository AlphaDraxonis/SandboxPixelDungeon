package com.alphadraxonis.sandboxpixeldungeon.editor.other;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.CeremonialCandle;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MassGraveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.RotGardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.RitualSiteRoom;
import com.watabou.utils.Bundle;

public class WandmakerQuest extends Quest {


    public Wand wand1;
    public Wand wand2;

    public static WandmakerQuest createRandom(LevelScheme levelScheme) {
        WandmakerQuest quest = new WandmakerQuest();

        quest.given = false;
        quest.wand1 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
        quest.wand1.cursed = false;
        quest.wand1.upgrade();

        do {
            quest.wand2 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
        } while (quest.wand2.getClass().equals(quest.wand1.getClass()));
        quest.wand2.cursed = false;
        quest.wand2.upgrade();

        // decide between 1,2, or 3 for quest type.
        quest.type = levelScheme.getWandmakerQuest();

        switch (quest.type){
            case 1: default:
                levelScheme.roomsToSpawn.add(MassGraveRoom.class);
                break;
            case 2:
                levelScheme.roomsToSpawn.add(RitualSiteRoom.class);
                break;
            case 3:
                levelScheme.roomsToSpawn.add(RotGardenRoom.class);
                break;
        }

        return quest;
    }

    @Override
    public boolean completed() {
        return false;
    }

    @Override
    public void complete() {
        wand1 = null;
        wand2 = null;

        Notes.remove( Notes.Landmark.WANDMAKER );
        addScore(1,2000);
    }


    private static final String WAND1 = "wand1";
    private static final String WAND2 = "wand2";
    private static final String RITUALPOS = "ritualpos";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAND1, wand1);
        bundle.put(WAND2, wand2);
        if (type == 2) {
            bundle.put(RITUALPOS, CeremonialCandle.ritualPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        wand1 = (Wand) bundle.get(WAND1);
        wand2 = (Wand) bundle.get(WAND2);
        if (type == 2) {
            CeremonialCandle.ritualPos = bundle.getInt(RITUALPOS);//TODO doesn't work for multiple
        }
    }
}