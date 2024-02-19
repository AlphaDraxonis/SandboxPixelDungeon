package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MonkSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ImpQuest extends Quest {

    public static final int MONK_QUEST = 0, GOLEM_QUEST = 1;


    private static int activeMonkQuests, activeGolemQuests;//maybe use array instead and type is index
    private static int numComleted;//not actual num because it gets lower when imp shop rooms spawn

    public Ring reward;

    @Override
    public void initRandom(LevelScheme levelScheme) {
        if (type == BASED_ON_DEPTH) type = levelScheme.generateImpQuestNotRandom();
        else if (type == RANDOM) type = Random.Int(2);
        if (reward == null) {
            do {
                reward = (Ring) Generator.randomUsingDefaults(Generator.Category.RING);
            } while (reward.cursed);
            reward.upgrade(2);
            reward.cursed = true;
        } else {
            reward = RandomItem.initRandomStatsForItemSubclasses(reward);
            if (reward != null && reward.identifyOnStart) reward.identify();
        }
    }


    @Override
    public void complete() {
        super.complete();
        reward = null;
        addScore(3, 4000);

        if (type == MONK_QUEST) activeMonkQuests--;
        else if (type == GOLEM_QUEST) activeGolemQuests--;

        numComleted++;

        Notes.remove(Notes.Landmark.IMP);
    }

    public void start() {
        super.start();
        Notes.add(Notes.Landmark.IMP);

        if (type == ImpQuest.MONK_QUEST) activeMonkQuests++;
        else if (type == ImpQuest.GOLEM_QUEST) activeGolemQuests++;
    }

    public static void process(Mob mob) {
        if ((activeMonkQuests > 0 && mob instanceof Monk) || (activeGolemQuests > 0 && mob instanceof Golem)) {
            Dungeon.level.drop(new DwarfToken(), mob.pos).sprite.drop();
        }
    }

    public int getRequiredQuantity() {
        if (type == ImpQuest.MONK_QUEST) return 5;
        else if (type == ImpQuest.GOLEM_QUEST) return 4;
        return 1;
    }


    public static boolean completedOnce() {
        return numComleted > 0;
    }

    public static void decreaseCompletedCounter() {
        numComleted--;
    }

    private static final String RING = "ring";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(RING, reward);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        reward = (Ring) bundle.get(RING);
    }

    private static final String NODE = "imp";
    private static final String MONKS = "monks";
    private static final String GOLEMS = "golems";
    private static final String NUM_COMPLETED = "num_completed";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(MONKS, activeMonkQuests);
        node.put(GOLEMS, activeGolemQuests);
        node.put(NUM_COMPLETED, numComleted);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        activeMonkQuests = b.getInt(MONKS);
        activeGolemQuests = b.getInt(GOLEMS);
        numComleted = b.getInt(NUM_COMPLETED);
    }

    public static void reset() {
        activeMonkQuests = 0;
        activeGolemQuests = 0;
        numComleted = 0;
    }

    @Override
    public int getNumQuests() {
        return 2;
    }

    @Override
    public Image getIcon() {
        switch (type) {
            case MONK_QUEST:
                return new MonkSprite();
            case GOLEM_QUEST:
                return new GolemSprite();
        }
        return null;
    }

    @Override
    public String getMessageString() {
        return getMessageString(type);
    }
    @Override
    public String getMessageString(int type) {
        if (type == MONK_QUEST) return "monks";
        if (type == GOLEM_QUEST) return "golems";
        return null;
    }


}