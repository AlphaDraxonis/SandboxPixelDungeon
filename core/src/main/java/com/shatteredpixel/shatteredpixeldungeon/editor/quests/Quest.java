package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public abstract class Quest extends GameObject {

    public static final int RANDOM = -1, NONE = -2, BASED_ON_DEPTH = -3;

    protected int type = RANDOM;

    private boolean given;
    private boolean completed;

    public abstract void initRandom(LevelScheme levelScheme);

    @Override
    public ModifyResult initRandoms() {
        initRandom(Dungeon.level.levelScheme);
        return ModifyResult.singeReplacement(this);
    }

    public void complete(){
        completed = true;
    }

    public static void addScore(int slot, int score){
        if (Statistics.questScores[slot] >= score) Statistics.questScores[slot] += score/10;
        else Statistics.questScores[slot] = score;
    }

    public boolean completed() {
        return completed;
    }

    public int type() {
        return type;
    }

    public final void setType(int type) {
        this.type = type;
    }

    public void start(){
        given = true;
    }

    public boolean given() {
        return given;
    }

    public abstract String getMessageString();
    public abstract String getMessageString(int type);//some places still use hardcoded message keys so don't change them only here!
    public abstract int getNumQuests();
    public abstract Image getIcon();


    private static final String TYPE = "type";
    private static final String GIVEN = "given";
    private static final String PROCESSED = "processed";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TYPE, type);
        bundle.put(GIVEN, given);
        bundle.put(PROCESSED, completed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        type = bundle.getInt(TYPE);
        given = bundle.getBoolean(GIVEN);
        completed = bundle.getBoolean(PROCESSED);
    }


}