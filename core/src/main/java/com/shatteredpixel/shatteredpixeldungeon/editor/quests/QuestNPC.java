package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;

import java.util.List;

public abstract class QuestNPC<T extends Quest> extends NPC {

    public T quest;

    public QuestNPC() {
        createNewQuest();
    }

    public QuestNPC(T quest) {
        this.quest = quest;
    }

    public void initQuest(LevelScheme levelScheme) {
        if (quest != null) {
            quest.initRandom(levelScheme);
        }
    }

    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        return super.doOnAllGameObjects(whatToDo)
                | doOnSingleObject(quest, whatToDo, newValue -> quest = newValue);
    }

    public abstract void createNewQuest();

    public abstract void place(RegularLevel level, List<Room> rooms);


    @Override
    public int defenseSkill(Char enemy) {
        return INFINITE_EVASION;
    }

    @Override
    public void damage(int dmg, Object src) {
        //do nothing
    }

    @Override
    public boolean add(Buff buff) {
        return false;
    }

    @Override
    public boolean reset() {
        return true;
    }


    private static final String QUEST = "quest";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (quest != null) bundle.put(QUEST, quest);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(QUEST)) quest = (T) bundle.get(QUEST);
    }

}