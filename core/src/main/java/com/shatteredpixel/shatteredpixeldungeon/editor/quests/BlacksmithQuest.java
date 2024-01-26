package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrystalGuardianSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGuardSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlacksmithQuest extends Quest {

    public static final int GOLD = 0, BLOOD = 1, CRYSTAL = 2, GNOLL = 3, FUNGI = 4;

    private int id;
    private boolean started;

    private boolean bossBeaten;

    //reward tracking. Stores remaining favor, the pickaxe, and how many of each reward has been chosen
    public int favor;
    public Item pickaxe = new Pickaxe().identify(!CustomDungeon.isEditing());
    public int reforges; //also used by the pre-v2.2.0 version of the quest
    public int hardens;
    public int upgrades;
    public int smiths;

    public int questScore;

    //pre-generate these so they are consistent between seeds
    public ArrayList<Item> smithRewards;

    private static int oldGoldQuestsActive;
    private static final Map<Integer, BlacksmithQuest> quests = new HashMap<>();
    private static int nextId = 0;

    @Override
    public void initRandom(LevelScheme levelScheme) {

        if (smithRewards == null) smithRewards = new ArrayList<>();

        boolean useDecks = true;

        if (smithRewards.size() < 3) {

            //30%:+0, 45%:+1, 20%:+2, 5%:+3
            int rewardLevel;
            float itemLevelRoll = Random.Float();
            if (itemLevelRoll < 0.3f) rewardLevel = 0;
            else if (itemLevelRoll < 0.75f) rewardLevel = 1;
            else if (itemLevelRoll < 0.95f) rewardLevel = 2;
            else rewardLevel = 3;

            int weapons = 0, armors = 0;
            for (Item i : smithRewards) {
                if (i instanceof Weapon) weapons++;
                else if (i instanceof Armor) armors++;
            }
            if (weapons == 0) {
                Weapon w;
                smithRewards.add(w = Generator.randomWeapon(3, useDecks));
                w.level(rewardLevel);
                w.enchant(null);
                w.cursed = false;
                weapons++;
            }
            if (weapons + armors < 3 && weapons == 1) {
                Class<? extends Weapon> cl = null;
                for (Item i : smithRewards) {
                    if (i instanceof Weapon) {
                        cl = (Class<? extends Weapon>) i.getClass();
                        break;
                    }
                }
                ArrayList<Item> toUndo = new ArrayList<>();
                Weapon w = Generator.randomWeapon(3, useDecks);
                while (cl == w.getClass()) {
                    w = Generator.randomWeapon(3, useDecks);
                    if (useDecks) toUndo.add(w);
                }
                w.level(rewardLevel);
                w.enchant(null);
                w.cursed = false;
                smithRewards.add(w);
                if (useDecks) toUndo.add(w);
                for (Item i : toUndo) {
                    Generator.undoDrop(i);
                }
            }
            if (armors == 0) {
                Armor a;
                smithRewards.add(a = Generator.randomArmor(3));
                a.level(rewardLevel);
                a.inscribe(null);
                a.cursed = false;
            }
        }

        RandomItem.replaceRandomItemsInList(smithRewards);

        if (type == BASED_ON_DEPTH) {
            type = levelScheme.generateBlacksmithQuest();
            levelScheme.roomsToSpawn.add(new BlacksmithRoom());
        } else if (type == RANDOM) type = CRYSTAL + Random.Int(getNumQuests()-2);//Do not generate the old quests
    }

    @Override
    public void complete() {
        super.complete();

        if (type() == GOLD || type == BLOOD) {
            if (type() == GOLD) oldGoldQuestsActive--;
            addScore(2, 3000);
            return;
        }

        favor = 0;
        DarkGold gold = Dungeon.hero.belongings.getItem(DarkGold.class);
        if (gold != null) {
            favor += Math.min(2000, gold.quantity() * 50);
            gold.detachAll(Dungeon.hero.belongings.backpack);
        }

        pickaxe = Dungeon.hero.belongings.getItem(Pickaxe.class);
        if (pickaxe.isEquipped(Dungeon.hero)) {
            boolean wasCursed = pickaxe.cursed;
            pickaxe.cursed = false; //so that it can always be removed
            ((EquipableItem) pickaxe).doUnequip(Dungeon.hero, false);
            pickaxe.cursed = wasCursed;
        }
        pickaxe.detach(Dungeon.hero.belongings.backpack);

        if (bossBeaten) favor += 1000;

        addScore(2, favor);
        questScore = favor;
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.TROLL);
        if (type() == GOLD) oldGoldQuestsActive++;
    }

    public boolean rewardsAvailable(){
        return favor > 0
                || (smithRewards != null && smiths > 0)
                || (pickaxe != null && Statistics.questScores[2] >= 2500);
    }

    public void actualStart() {
        started = true;
    }

    public boolean bossBeaten() {
        return bossBeaten;
    }

    public void beatBoss() {
        bossBeaten = true;
    }

    public boolean started() {
        return started;
    }

    private static final String ID = "id";
    private static final String STARTED = "started";
    private static final String BOSS_BEATEN = "boss_beaten";
    private static final String FAVOR = "favor";
    private static final String PICKAXE = "pickaxe";
    private static final String REFORGES = "reforges";
    private static final String HARDENS = "hardens";
    private static final String UPGRADES = "upgrades";
    private static final String SMITHS = "smiths";
    private static final String SMITH_REWARDS = "smith_rewards";
    private static final String QUEST_SCORE = "quest_score";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(ID, id);

        bundle.put(STARTED, started);
        bundle.put(BOSS_BEATEN, bossBeaten);

        bundle.put(FAVOR, favor);
        bundle.put(REFORGES, reforges);
        bundle.put(HARDENS, hardens);
        bundle.put(UPGRADES, upgrades);
        bundle.put(SMITHS, smiths);
        bundle.put(QUEST_SCORE, questScore);

        if (smithRewards != null) bundle.put(SMITH_REWARDS, smithRewards);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        id = bundle.getInt(ID);

        started = bundle.getBoolean(STARTED);
        bossBeaten = bundle.getBoolean(BOSS_BEATEN);

        favor = bundle.getInt(FAVOR);

        if (bundle.contains(PICKAXE)) pickaxe = (Item) bundle.get(PICKAXE);
        else pickaxe = null;

        if (bundle.contains("reforged")) {
            //pre-v2.2.0 saves
            reforges = bundle.getBoolean("reforged") ? 1 : 0;
        } else {
            reforges = bundle.getInt(REFORGES);
        }

        hardens = bundle.getInt(HARDENS);
        upgrades = bundle.getInt(UPGRADES);
        smiths = bundle.getInt(SMITHS);
        questScore = bundle.getInt(QUEST_SCORE);

        if (bundle.contains(SMITH_REWARDS)) {
            smithRewards = new ArrayList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(SMITH_REWARDS)));
        }
    }

    private static final String NODE = "blacksmith";
    private static final String OLD_GOLD_QUESTS_ACTIVE = "old_gold_quests_active";
    private static final String QUEST_IDS = "quest_ids";
    private static final String QUEST_VALUE = "quest_value_";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(OLD_GOLD_QUESTS_ACTIVE, oldGoldQuestsActive);

        int[] ids = new int[quests.size()];
        int i = 0;
        for (int id : quests.keySet()) {
            ids[i] = id;
            node.put(QUEST_VALUE + id, quests.get(id));
            i++;
        }
        node.put(QUEST_IDS, ids);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        quests.clear();
        if (b != null && b.contains(OLD_GOLD_QUESTS_ACTIVE)) {
            oldGoldQuestsActive = b.getInt(OLD_GOLD_QUESTS_ACTIVE);

            int[] ids = bundle.getIntArray(QUEST_IDS);
            if (ids != null) {
                for (int id : ids) {
                    quests.put(id, (BlacksmithQuest) bundle.get(QUEST_VALUE + id));
                    nextId = Math.max(id + 1, nextId);
                }
            }

        } else {
            oldGoldQuestsActive = 1;
            nextId = 0;
        }
    }

    public int registerQuest() {
        if (!quests.containsValue(this)) {
            id = nextId++;
            quests.put(id, this);
            return id;
        }
        return -1;
    }

    public int id() {
        return id;
    }

    public static BlacksmithQuest findById(int id) {
        return quests.get(id);
    }

    public static void reset() {
        oldGoldQuestsActive = 0;
        nextId = 0;
        quests.clear();
    }

    public static boolean isOldGoldQuestsActive() {
        return oldGoldQuestsActive > 0;
    }

    @Override
    public int getNumQuests() {
        return 4;
    }

    @Override
    public Image getIcon() {
        switch (type) {
            case GOLD:
                return new ItemSprite(ItemSpriteSheet.ORE);
            case BLOOD:
                return new BatSprite();
            case CRYSTAL:
                return new CrystalGuardianSprite() {
                    @Override
                    protected int texOffset() {
                        return 0;
                    }
                };
            case GNOLL:
                return new GnollGuardSprite();
            case FUNGI:
                return new ItemSprite();
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
        if (type == CRYSTAL) return "crystal";
        if (type == FUNGI) return "fungi";
        if (type == GNOLL) return "gnoll";
        return null;
    }
}