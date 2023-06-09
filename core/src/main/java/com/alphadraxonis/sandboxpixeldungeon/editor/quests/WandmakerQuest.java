package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.quest.CeremonialCandle;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.MassGraveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.special.RotGardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.RitualSiteRoom;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class WandmakerQuest extends Quest {

    public static final int NUM_QUESTS = 2;//no candle for now
    public static final int ASH = 0, SEED = 1, CANDLE = 2;

    public Wand wand1;
    public Wand wand2;

    public boolean spawnQuestRoom = true;


    @Override
    public void initRandom(LevelScheme levelScheme) {

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

        if (type == -1) {// decide between 1,2, or 3 for quest type.
            type = levelScheme.getWandmakerQuest();
        }
        if (spawnQuestRoom) {
            switch (type) {
                case ASH:
                    levelScheme.roomsToSpawn.add(new MassGraveRoom());
                    break;
                case SEED:
                    levelScheme.roomsToSpawn.add(new RotGardenRoom());
                    break;
                case CANDLE:
                    levelScheme.roomsToSpawn.add(new RitualSiteRoom());
                    break;
            }
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
    private static final String SPAWN_QUEST_ROOM = "spawn_quest_room";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAND1, wand1);
        bundle.put(WAND2, wand2);
        bundle.put(SPAWN_QUEST_ROOM, spawnQuestRoom);
        if (type == CANDLE) {
            bundle.put(RITUALPOS, CeremonialCandle.ritualPos);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        wand1 = (Wand) bundle.get(WAND1);
        wand2 = (Wand) bundle.get(WAND2);
        spawnQuestRoom = bundle.getBoolean(SPAWN_QUEST_ROOM);
        if (type == CANDLE) {
            CeremonialCandle.ritualPos = bundle.getInt(RITUALPOS);//TODO doesn't work for multiple
        }
    }

    @Override
    public int getNumQuests() {
        return NUM_QUESTS;
    }

    @Override
    public Image getIcon() {
        switch (type) {
            case ASH:
                return new ItemSprite(ItemSpriteSheet.DUST);
            case SEED:
                return new ItemSprite(ItemSpriteSheet.SEED_ROTBERRY);
            case CANDLE:
                return new ItemSprite(ItemSpriteSheet.EMBER);
        }
        return null;
    }
    @Override
    public String getMessageString() {
        return getMessageString(type);
    }
    @Override
    public String getMessageString(int type) {
        switch (type) {
            case ASH:
                return "dust";
            case SEED:
                return "berry";
            case CANDLE:
                return "ember";
        }
        return null;
    }
}