package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

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

    public static final int ASH = 1, SEED = 2, CANDLE = 3;

    public Wand wand1;
    public Wand wand2;


    @Override
    public void initRandom(LevelScheme levelScheme) {
        if (type == -1) {// decide between 1,2, or 3 for quest type.
            type = levelScheme.getWandmakerQuest();
        }

        if (wand1 == null) {
            wand1 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
            wand1.cursed = false;
            wand1.upgrade();
        }
        if (wand2 == null) {
            do {
                wand2 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
            } while (wand2.getClass().equals(wand1.getClass()));
            wand2.cursed = false;
            wand2.upgrade();
        }

        switch (type) {
            case ASH:
            default:
                levelScheme.roomsToSpawn.add(MassGraveRoom.class);
                break;
            case CANDLE:
                levelScheme.roomsToSpawn.add(RitualSiteRoom.class);
                break;
            case SEED:
                levelScheme.roomsToSpawn.add(RotGardenRoom.class);
                break;
        }
    }

    @Override
    public boolean completed() {
        return false;
    }

    @Override
    public void complete() {
        wand1 = null;
        wand2 = null;

        Notes.remove(Notes.Landmark.WANDMAKER);
        addScore(1, 2000);
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.WANDMAKER);
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