package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MassGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RotGardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class WandmakerQuest extends Quest {

    public static final int NUM_QUESTS = 3;
    public static final int DUST = 0, SEED = 1, CANDLE = 2;

    public Wand wand1;
    public Wand wand2;

    public boolean spawnQuestRoom = true;

    public static int[] questsActive = new int[NUM_QUESTS];//used for music tracking


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
                case DUST:
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
        if (type() != SEED) addScore(1, 2000);
        if (type() != CANDLE) questsActive[type()]--;
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.WANDMAKER);
        if (type() != CANDLE) questsActive[type()]++;
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
    private static final String QUESTS_ACTIVE_ARRAY = "quests_active_array";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(QUESTS_ACTIVE_ARRAY, questsActive);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        if (b.contains(QUESTS_ACTIVE_ARRAY)) questsActive = b.getIntArray(QUESTS_ACTIVE_ARRAY);
        else questsActive = new int[NUM_QUESTS];
    }

    public static void reset() {
        questsActive = new int[NUM_QUESTS];
        wandmakerQuestWasActive = null;
    }

    private static Boolean wandmakerQuestWasActive = null;

    public static boolean updateMusic() {

        if (LevelScheme.getRegion(Dungeon.level) != LevelScheme.REGION_PRISON) {
            return false;
        }

        boolean nowActive = WandmakerQuest.active();
        if (wandmakerQuestWasActive == null) {
            wandmakerQuestWasActive = nowActive;
            return false;
        }
        if (nowActive != wandmakerQuestWasActive) {
            wandmakerQuestWasActive = nowActive;

            Game.runOnRenderThread(() -> Music.INSTANCE.fadeOut(1f, () -> {
                if (Dungeon.level != null) {
                    Dungeon.level.playLevelMusic();
                }
            }));
            return true;
        }
        return false;
    }

    public static void setMusicPlaying(boolean questActive) {
        wandmakerQuestWasActive = questActive;
    }

    //quest is active if:
    public static boolean active() {
        //it is not completed
        if (Dungeon.hero == null) {
            return false;
        }

        if (Dungeon.level instanceof RegularLevel) {

            RegularLevel l = (RegularLevel) Dungeon.level;

            if (true || questsActive[DUST] > 0) {
                //hero is in the mass grave room
                if (l.room(Dungeon.hero.pos) instanceof MassGraveRoom) {
                    return true;
                }
                //or if they are corpse dust cursed
                for (Buff b : Dungeon.hero.buffs()) {
                    if (b instanceof CorpseDust.DustGhostSpawner) {
                        return true;
                    }
                }
            }

            if (true || questsActive[SEED] > 0) {
                //hero is in the rot garden room and the rot heart in the same room is alive
                Room room = l.room(Dungeon.hero.pos);
                if (l.room(Dungeon.hero.pos) instanceof RotGardenRoom) {
                    for (Mob m : Dungeon.level.mobs) {
                        if (m instanceof RotHeart && l.room(m.pos) == room) {
                            return true;
                        }
                    }
                }
            }

            if (true || questsActive[CANDLE] > 0) {
                //hero has summoned the newborn elemental
                for (Mob m : Dungeon.level.mobs) {
                    if (m instanceof Elemental.NewbornFireElemental && ((Elemental.NewbornFireElemental) m).spawnedByQuest) {
                        return true;
                    }
                }
                //or hero is in the ritual room and all 4 candles are with them
                if (l.room(Dungeon.hero.pos) instanceof RitualSiteRoom) {
                    int candles = 0;
                    if (Dungeon.hero.belongings.getItem(CeremonialCandle.class) != null){
                        candles += Dungeon.hero.belongings.getItem(CeremonialCandle.class).quantity();
                    }
                    if (candles >= 4) return true;

                    for (Heap h : Dungeon.level.heaps.valueList()){
                        if (l.room(h.pos) instanceof RitualSiteRoom){
                            for (Item i : h.items){
                                if (i instanceof CeremonialCandle){
                                    candles += i.quantity();
                                }
                            }
                        }
                    }
                    if (candles >= 4) return true;
                }
            }
        } else {

            if (questsActive[DUST] > 0) {
                //hero is on mass grave room floor
                if (CustomTileItem.findCustomTileAt(Dungeon.hero.pos, false) instanceof MassGraveRoom.Bones) {
                    return true;
                }
            }
            //or if they are corpse dust cursed
            for (Buff b : Dungeon.hero.buffs()) {
                if (b instanceof CorpseDust.DustGhostSpawner) {
                    return true;
                }
            }

            if (questsActive[SEED] > 0) {
                //hero can see the rot heart or the lasher
                for (Mob m : Dungeon.level.mobs) {
                    if ( (m instanceof RotLasher || m instanceof RotHeart) && Dungeon.level.heroFOV[m.pos]) {
                        return true;
                    }
                }
            }

            if (questsActive[CANDLE] > 0 || true) {
                //hero has summoned the newborn elemental
                for (Mob m : Dungeon.level.mobs) {
                    if (m instanceof Elemental.NewbornFireElemental && ((Elemental.NewbornFireElemental) m).spawnedByQuest) {
                        return true;
                    }
                }

                //level contains ritual marker and hero has 4 candles

                boolean containsMarker = false;
                for (CustomTilemap ct : Dungeon.level.customTiles) {
                    if (ct instanceof RitualSiteRoom.RitualMarker && !((RitualSiteRoom.RitualMarker) ct).used) {
                        containsMarker = true;
                        break;
                    }
                }

                if (containsMarker) {
                    int candles = 0;
                    if (Dungeon.hero.belongings.getItem(CeremonialCandle.class) != null){
                        candles += Dungeon.hero.belongings.getItem(CeremonialCandle.class).quantity();
                    }
                    if (candles >= 4) return true;

                    for (CustomTilemap ct : Dungeon.level.customTiles) {
                        if (ct instanceof RitualSiteRoom.RitualMarker && !((RitualSiteRoom.RitualMarker) ct).used) {
                            int ritualPos = (ct.tileX + 1) + (ct.tileY + 1) * Dungeon.level.width();
                            Heap[] candleHeaps = new Heap[4];
                            candleHeaps[0] = Dungeon.level.heaps.get(ritualPos - Dungeon.level.width());
                            candleHeaps[1] = Dungeon.level.heaps.get(ritualPos + 1);
                            candleHeaps[2] = Dungeon.level.heaps.get(ritualPos + Dungeon.level.width());
                            candleHeaps[3] = Dungeon.level.heaps.get(ritualPos - 1);
                            for (Heap h : candleHeaps) {
                                if (h != null) {
                                    for (Item i : h.items){
                                        if (i instanceof CeremonialCandle){
                                            candles += i.quantity();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (candles >= 4) return true;
                }
            }

        }

        return false;
    }

    @Override
    public int getNumQuests() {
        return NUM_QUESTS;
    }


    @Override
    public Image getIcon() {
        switch (type) {
            case DUST:
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
            case DUST:
                return "dust";
            case SEED:
                return "berry";
            case CANDLE:
                return "ember";
        }
        return null;
    }
}