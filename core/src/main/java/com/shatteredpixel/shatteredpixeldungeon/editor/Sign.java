package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class Sign implements Bundlable {

    public String text;
    public  int pos;


    public Sign(){}
    public Sign(String text,int pos){
        this.text = text;
        this.pos = pos;
    }

    private static final String TEXT = "text";
    private static final String POS = "pos";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TEXT,text);
        bundle.put(POS,pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        text = bundle.getString(TEXT);
        pos = bundle.getInt(POS);
    }

    public Sign getCopy(){
        return new Sign(text,pos);
    }
}