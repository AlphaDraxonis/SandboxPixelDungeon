/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.Customizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;

public abstract class Plant extends GameObject implements Customizable, Copyable<Plant> {

    public int image;
    public int pos;

    public boolean activateOnTrigger = true;
    private String customName, customDesc;
    public Item dropItem;

    protected Class<? extends Plant.Seed> seedClass;

    @Override
    public int sparseArrayKey() {
        return pos;
    }

    public void trigger() {

        if (!activateOnTrigger) return;

        Char ch = Actor.findChar(pos);

        if (ch instanceof Hero) {
            ((Hero) ch).interrupt();
        }

        if (Dungeon.level.heroFOV[pos] && Dungeon.hero.hasTalent(Talent.NATURES_AID)) {
            // 3/5 turns based on talent points spent
            Barkskin.conditionallyAppend(Dungeon.hero, 2, 1 + 2*(Dungeon.hero.pointsInTalent(Talent.NATURES_AID)));
        }

        wither();

        if (dropItem != null) {
            Dungeon.level.drop(dropItem, pos).sprite.drop();
        }

        activate(ch);
    }

    public abstract void activate(Char ch);

    public void wither() {
        Dungeon.level.uproot(pos);

        if (Dungeon.level.heroFOV[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
        }

        float seedChance = 0f;
        for (Char c : Actor.chars()) {
            if (c instanceof WandOfRegrowth.Lotus) {
                WandOfRegrowth.Lotus l = (WandOfRegrowth.Lotus) c;
                if (l.inRange(pos)) {
                    seedChance = Math.max(seedChance, l.seedPreservation());
                }
            }
        }

        if (Random.Float() < seedChance) {
            if (seedClass != null && seedClass != Rotberry.Seed.class) {
                Dungeon.level.drop(Reflection.newInstance(seedClass), pos).sprite.drop();
            }
        }

    }

    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        return super.doOnAllGameObjects(whatToDo)
                | doOnSingleObject(dropItem, whatToDo, newValue -> dropItem = newValue);
    }

    private static final String POS = "pos";
    private static final String ACTIVATE_ON_TRIGGER = "activate_on_trigger";
    private static final String DROP_ITEM = "drop_item";
    private static final String CUSTOM_NAME = "custom_name";
    private static final String CUSTOM_DESC = "custom_desc";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        activateOnTrigger = !bundle.contains(ACTIVATE_ON_TRIGGER) || bundle.getBoolean(ACTIVATE_ON_TRIGGER);
        dropItem = (Item) bundle.get(DROP_ITEM);

        if (bundle.contains(CUSTOM_NAME)) customName = bundle.getString(CUSTOM_NAME);
        if (bundle.contains(CUSTOM_DESC)) customDesc = bundle.getString(CUSTOM_DESC);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(ACTIVATE_ON_TRIGGER, activateOnTrigger);
        bundle.put(DROP_ITEM, dropItem);

        if (customName != null) bundle.put(CUSTOM_NAME, customName);
        if (customDesc != null) bundle.put(CUSTOM_DESC, customDesc);
    }

    @Override
    public Plant getCopy(){
        Bundle b = new Bundle();
        b.put("PLANT", this);
        return (Plant) b.get("PLANT");
    }

    public String name() {
        String msg;
        return customName == null ? Messages.get(this, "name") : Messages.NO_TEXT_FOUND.equals(msg = Messages.get(customName)) ? customName : msg;
    }

    public String desc() {
        String msg;
        return customDesc == null ? regularDesc(this) : Messages.NO_TEXT_FOUND.equals(msg = Messages.get(customDesc)) ? customDesc : msg;
    }

    private static String regularDesc(Plant plant) {
        String desc = Messages.get(plant, "desc");
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN) {
            desc += "\n\n" + Messages.get(plant, "warden_desc");
        }
        return desc;
    }

    public Image getSprite() {
        return EditorUtilies.getTerrainFeatureTexture(image + 7 * 16);
    }

    @Override
    public String getCustomName() {
        return customName;
    }

    @Override
    public void setCustomName(String name) {
        customName = name;
    }

    @Override
    public String getCustomDesc() {
        return customDesc;
    }

    @Override
    public void setCustomDesc(String desc) {
        customDesc = desc;
    }

    public static class Seed extends Item {

        public static final String AC_PLANT = "PLANT";

        private static final float TIME_TO_PLANT = 1f;

        {
            stackable = true;
            defaultAction = AC_THROW;
        }

        protected Class<? extends Plant> plantClass;

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_PLANT);
            return actions;
        }

        @Override
        protected void onThrow(int cell) {
            if (Dungeon.level.map[cell] == Terrain.ALCHEMY
                    || Dungeon.level.pit[cell]
                    || Dungeon.level.traps.get(cell) != null
                    || Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
                super.onThrow(cell);
            } else {
                Dungeon.level.plant(this, cell);
                if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                    for (int i : PathFinder.NEIGHBOURS8) {
                        int c = Dungeon.level.map[cell + i];
                        if (c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
                                || c == Terrain.EMBERS || c == Terrain.GRASS) {
                            Level.set(cell + i, Terrain.FURROWED_GRASS);
                            GameScene.updateMap(cell + i);
                            CellEmitter.get(cell + i).burst(LeafParticle.LEVEL_SPECIFIC, 4);
                        }
                    }
                }
            }
        }

        @Override
        public void execute(Hero hero, String action) {

            super.execute(hero, action);

            if (action.equals(AC_PLANT)) {

                hero.busy();
                ((Seed) detach(hero.belongings.backpack)).onThrow(hero.pos);
                hero.spend(TIME_TO_PLANT);

                hero.sprite.operate(hero.pos);

            }
        }

        public Plant couch(int pos, Level level) {
            if (level != null && level.heroFOV != null && level.heroFOV[pos]) {
                Sample.INSTANCE.play(Assets.Sounds.PLANT);
            }
            Plant plant = Reflection.newInstance(plantClass);
            plant.pos = pos;
            return plant;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public int value() {
            return 10 * quantity;
        }

        @Override
        public int energyVal() {
            return 2 * quantity;
        }

        @Override
        public String desc() {
            String desc = customDesc == null ? Messages.get(plantClass, "desc") : customDesc;
            if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.WARDEN) {
                desc += "\n\n" + Messages.get(plantClass, "warden_desc");
            }
            return desc;
        }

        @Override
        public String info() {
            return Messages.get(Seed.class, "info", desc());
        }

        public static class PlaceHolder extends Seed {

            {
                image = ItemSpriteSheet.SEED_HOLDER;
            }

            @Override
            public boolean isSimilar(Item item) {
                return item instanceof Plant.Seed;
            }

            @Override
            public String info() {
                return "";
            }
        }
    }
}