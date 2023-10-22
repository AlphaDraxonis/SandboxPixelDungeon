package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.levels.PrisonLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WandmakerQuest extends Quest {

    public static final int NUM_QUESTS = 3;
    public static final int ASH = 0, SEED = 1, CANDLE = 2;

    public Wand wand1;
    public Wand wand2;

    public boolean spawnQuestRoom = true;

    public static int questsActive;//used for music tracking


    @Override
    public void initRandom(LevelScheme levelScheme) {

        if (wand1 == null) {
            wand1 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
            wand1.cursed = false;
            wand1.upgrade();
        } else if (wand1.identifyOnStart) wand1.identify();
        if (wand2 == null) {
            do {
                wand2 = (Wand) Generator.randomUsingDefaults(Generator.Category.WAND);
            } while (wand2.getClass().equals(wand1.getClass()));
            wand2.cursed = false;
            wand2.upgrade();
        } else if (wand2.identifyOnStart) wand2.identify();

        if (type == BASED_ON_DEPTH) type = levelScheme.generateWandmakerQuest();
        else if (type == RANDOM) type = Random.Int(NUM_QUESTS);

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
        if (type() != CANDLE && type() != ASH) questsActive--;//already reduced when killing elemental
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.WANDMAKER);
        if (type() != CANDLE && type() != ASH) questsActive++;//already increased when summoning elemental
    }


    private static final String WAND1 = "wand1";
    private static final String WAND2 = "wand2";
    private static final String SPAWN_QUEST_ROOM = "spawn_quest_room";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAND1, wand1);
        bundle.put(WAND2, wand2);
        bundle.put(SPAWN_QUEST_ROOM, spawnQuestRoom);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        wand1 = (Wand) bundle.get(WAND1);
        wand2 = (Wand) bundle.get(WAND2);
        spawnQuestRoom = bundle.getBoolean(SPAWN_QUEST_ROOM);
    }

    private static final String NODE = "wandmaker";
    private static final String QUESTS_ACTIVE = "quests_active";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(QUESTS_ACTIVE, questsActive);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        if (b.contains(QUESTS_ACTIVE)) {
            questsActive = b.getInt(QUESTS_ACTIVE);
        } else reset();
    }

    public static boolean areQuestsActive() {
        return questsActive > 0;
    }

    public static void reset() {
        questsActive = 0;
    }

    public static void maybeStartPlayingQuestMusic(){
        WandmakerQuest.questsActive++;
        if (!PrisonLevel.playingQuestMusic && Dungeon.level.playsMusicFromRegion() == LevelScheme.REGION_PRISON)
            Dungeon.level.playLevelMusic();
    }

    public static void maybeStopPlayingQuestMusic(){
        WandmakerQuest.questsActive--;
        if (Dungeon.level.playsMusicFromRegion() == LevelScheme.REGION_PRISON) {
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(1f, new Callback() {
                        @Override
                        public void call() {
                            if (Dungeon.level != null) {
                                Dungeon.level.playLevelMusic();
                            }
                        }
                    });
                }
            });
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