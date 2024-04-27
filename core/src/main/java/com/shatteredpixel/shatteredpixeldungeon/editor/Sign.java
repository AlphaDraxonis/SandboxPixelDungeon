package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class Sign implements Bundlable {

    public String text;
    public int pos;
    public boolean burnOnRead;


    public Sign(){}
    public Sign(String text,int pos){
        this.text = text;
        this.pos = pos;
    }

    public void burn() {
        Level.set(pos, Dungeon.level.map[pos] == Terrain.SIGN_SP ? Terrain.EMPTY_SP : Terrain.EMPTY);
        CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 40 );
        Sample.INSTANCE.play( Assets.Sounds.BURNING );
        Dungeon.level.signs.remove(pos);

        GameScene.updateMap(pos);
    }

    private static final String TEXT = "text";
    private static final String POS = "pos";
    private static final String BURN_ON_READ = "burn_on_read";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TEXT, text);
        bundle.put(POS, pos);
        bundle.put(BURN_ON_READ, burnOnRead);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        text = bundle.getString(TEXT);
        pos = bundle.getInt(POS);
        burnOnRead = bundle.getBoolean(BURN_ON_READ);
    }

    public Sign getCopy() {
        Sign sign = new Sign(text, pos);
        sign.burnOnRead = burnOnRead;
        return sign;
    }
}