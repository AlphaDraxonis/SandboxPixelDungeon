package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class CoinDoor implements Bundlable {

    public static final int DEFAULT_COST = 1000;

    public static int costInInventory = DEFAULT_COST;


    public int cost;
    public int pos;


    public CoinDoor(){}
    public CoinDoor(int pos, int cost){
        this.cost = cost;
        this.pos = pos;
    }

    private static final String COST = "cost";
    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(COST, cost);
        bundle.put(POS,pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        cost = bundle.getInt(COST);
        pos = bundle.getInt(POS);
    }

    public CoinDoor getCopy(){
        return new CoinDoor(pos, cost);
    }
}