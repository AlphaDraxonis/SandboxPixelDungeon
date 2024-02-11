package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WandmakerQuest extends Quest {

    public static final int NUM_QUESTS = 3;
    public static final int ASH = 0, SEED = 1, CANDLE = 2;

    public Wand wand1;
    public Wand wand2;

    public boolean spawnQuestRoom = true;

    public static List<String> questsActive = new ArrayList<>(3);//used for music tracking


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
        if (type() != CANDLE && type() != ASH) questsActive.remove(Dungeon.levelName);//already reduced when killing elemental
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.WANDMAKER);
        if (type() != CANDLE && type() != ASH) questsActive.add(Dungeon.levelName);//already increased when summoning elemental
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
    private static final String QUESTS_ACTIVE_LIST = "quests_active_list";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(QUESTS_ACTIVE_LIST, questsActive.toArray(EditorUtilies.EMPTY_STRING_ARRAY));
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        questsActive.clear();
        if (b.contains(QUESTS_ACTIVE_LIST)) {
            questsActive.addAll(Arrays.asList(b.getStringArray(QUESTS_ACTIVE_LIST)));
        }
    }

    public static boolean areQuestsActive() {
        return questsActive.contains(Dungeon.levelName);
    }

    public static void reset() {
        questsActive.clear();
    }

    public static void maybeStartPlayingQuestMusic(){
        WandmakerQuest.questsActive.add(Dungeon.levelName);
        if (!PrisonLevel.playingQuestMusic && Dungeon.level.playsMusicFromRegion() == LevelScheme.REGION_PRISON)
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

    public static void maybeStopPlayingQuestMusic(){
        WandmakerQuest.questsActive.remove(Dungeon.levelName);
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