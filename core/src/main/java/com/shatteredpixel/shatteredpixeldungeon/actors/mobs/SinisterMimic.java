package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

//Sinister mimic: pretends to be any item that a player can pick up.  Deals damage to players that try to pick it up.  However, this mimic has very little health and can be killed very quickly.
public class SinisterMimic extends Item {

    {
        stackable = false;
        unique = true;
        setPretends(new Sword());
    }

    public Item pretends;

    public void setPretends(Item pretends) {
        this.pretends = pretends;
        quantity = pretends.quantity();
        level(pretends.trueLevel());
    }

    private static final String PRETENDS = "pretends";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PRETENDS, pretends);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        pretends = (Item) bundle.get(PRETENDS);
    }

    @Override
    public String name() {
        return pretends.name();
    }

    @Override
    public String title() {
        return pretends.title();
    }

    @Override
    public String desc() {
        return pretends.desc();
    }

    @Override
    public String info() {
        return pretends.info();
    }

    @Override
    public int image() {
        return pretends.image();
    }

    @Override
    public int level() {
        return pretends.level();
    }

    @Override
    public int trueLevel_OVERRIDE_ONLY_FOR_ITEMITEM_CLASS() {
        return pretends.trueLevel_OVERRIDE_ONLY_FOR_ITEMITEM_CLASS();
    }

    @Override
    public boolean isUpgradable() {
        return pretends.isUpgradable();
    }

    @Override
    public boolean isIdentified() {
        return pretends.isIdentified();
    }

    @Override
    public int quantity() {
        return pretends.quantity();
    }

    @Override
    public String status() {
        return pretends.status();
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        Rat m = new Rat();
        m.state = m.WANDERING;
        int tries = 40;
        do {
            m.pos = PathFinder.NEIGHBOURS9[Random.Int(PathFinder.NEIGHBOURS9.length)] + pos;
        } while (!Barrier.canEnterCell(m.pos, m, false, true) && tries -- > 0);
        if (tries == 0) {
            Sample.INSTANCE.play(Assets.Sounds.ITEM);
            return false;
        }
        GameScene.add(m, -20);
        Sample.INSTANCE.play(Assets.Sounds.MIMIC);
        //Glog
        Dungeon.level.heaps.get(pos).remove(this);
        return false;
    }
}