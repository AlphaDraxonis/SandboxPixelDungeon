/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.alphadraxonis.sandboxpixeldungeon.actors.mobs;

import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.StenchGas;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Ooze;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.FetidRatSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class FetidRat extends Rat {

    {
        spriteClass = FetidRatSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 4;

        state = WANDERING;

        properties.add(Property.MINIBOSS);
        properties.add(Property.DEMONIC);
    }

    private int quest;

    public FetidRat() {
        //for bundling
    }

    public FetidRat(Ghost questGiver) {
        quest = questGiver.id();
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Ooze.class).set(Ooze.DURATION);
        }

        return damage;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        GameScene.add(Blob.seed(pos, 20, StenchGas.class));

        return super.defenseProc(enemy, damage);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Actor c = Actor.findById(quest);
        if (c instanceof Ghost) ((Ghost) c).quest.process();
        else if (quest != 0) GLog.n("Rare error occurred so that the ghost couldn't be found.");
    }

    {
        immunities.add(StenchGas.class);
    }

    private static final String QUEST = "quest";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(QUEST, quest);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        quest = bundle.getInt(QUEST);
    }
}