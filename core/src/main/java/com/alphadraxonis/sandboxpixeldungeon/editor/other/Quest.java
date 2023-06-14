package com.alphadraxonis.sandboxpixeldungeon.editor.other;

import com.alphadraxonis.sandboxpixeldungeon.Statistics;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Quest implements Bundlable {

    protected int type = -1;

    public boolean given;
    public boolean processed;

    public abstract boolean completed();

    public abstract void complete();
    void addScore(int slot, int score){
        if (Statistics.questScores[slot] >= score) Statistics.questScores[slot] += score/10;
        else Statistics.questScores[slot] = score;
    }

    public boolean processed() {
        return processed;
    }

    public int type() {
        return type;
    }


    private static final String TYPE = "type";
    private static final String GIVEN = "given";
    private static final String PROCESSED = "processed";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TYPE, type);
        bundle.put(GIVEN, given);
        bundle.put(PROCESSED, processed);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        type = bundle.getInt(TYPE);
        given = bundle.getBoolean(GIVEN);
        processed = bundle.getBoolean(PROCESSED);
    }


}