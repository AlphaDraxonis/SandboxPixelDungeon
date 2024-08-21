/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.QuestNPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.CustomObjectClass;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public abstract class GameObjectCategory<T> {

    SubCategory<T>[] values;

    private final EditorItemBag bag;
    protected final CustomObjectBag customObjectBag = new CustomObjectBag(this);

    protected GameObjectCategory(EditorItemBag bag) {
		this.bag = bag;
    }

    //we must call this individually in each constructor or else values would be null!
    final void addItemsToBag() {
        for (SubCategory<T> cat : values) {
            bag.items.add(createBag(cat));
        }
        bag.items.add(customObjectBag);
    }

    GameObjectCategory.Bag<T> createBag(SubCategory<T> category) {
        return new GameObjectCategory.Bag<>(category);
    }

    public SubCategory<T>[] values() {
        return values;
    }

    public EditorItemBag getBag() {
        return bag;
    }

    public abstract ScrollingListPane.ListButton createAddBtn();

    public void addCustomObject(CustomGameObjectClass obj) {
        if (obj != null) {
            customObjectBag.items.add(EditorItem.wrapObject(obj));
        }
    }

    public abstract void updateCustomObjects();

    protected void updateCustomObjects(Class<? extends GameObject> type) {
        //Need to keep the same reference for the Undo list
        if (Undo.canUndo() || Undo.canRedo()) {
            ArrayList<Item> newItems = new ArrayList<>();
            oneObj:
            for (GameObject obj : UserContentManager.getAllCustomObjects(type)) {
                int ident = ((CustomObjectClass) obj).getIdentifier();
                for (Item i : customObjectBag.items) {
                    EditorItem<?> item = (EditorItem<?>) i;
                    if (((CustomObjectClass) item.getObject()).getIdentifier() == ident) {
                        newItems.add(item);
                        UserContentManager.overrideOriginal((CustomObjectClass) item.getObject());
                        continue oneObj;
                    }
                }
                newItems.add(EditorItem.wrapObject(obj));
            }
            customObjectBag.items = newItems;
        } else {
            customObjectBag.clear();
            for (GameObject obj : UserContentManager.getAllCustomObjects(type)) {
                customObjectBag.items.add(EditorItem.wrapObject(obj));
            }
        }

    }

    public void updateCustomObject(CustomObject customObject) {
        Undo.startAction();
        int ident = customObject.getIdentifier();
        for (Item i : customObjectBag.items) {
            CustomGameObjectClass obj = (CustomGameObjectClass) ((EditorItem<?>) i).getObject();
            if (obj.getIdentifier() == ident) {
                obj.updateInheritStats(Dungeon.level);
            }
        }
        Undo.endAction();
        EToolbar.updateLayout();
    }


	public static <T> Class<?>[][] getAll(SubCategory<T>[] all) {
        return getAll(all, null);
    }

    public static <T> Class<?>[][] getAll(SubCategory<T>[] all, Set<Class<? extends T>> toIgnore) {
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> list = new ArrayList<>(Arrays.asList(all[i].getClasses()));
            if (toIgnore != null) list.removeAll(toIgnore);
            ret[i] = list.toArray(EditorUtilities.EMPTY_CLASS_ARRAY);
        }
        return ret;
    }

    public static <T> Class<? extends T> getRandom(SubCategory<T>[] all) {
        return getRandom(all, null);
    }

    public static <T> Class<? extends T> getRandom(SubCategory<T>[] all, Set<Class<? extends T>> toIgnore) {
        List<Class<?>> list = new ArrayList<>();
        for (int i = 0; i < all.length; i++) {
            list.addAll(Arrays.asList(all[i].getClasses()));
        }
        if (toIgnore != null) list.removeAll(toIgnore);

        int length = list.size();
        if (length == 0) return null;
        return (Class<? extends T>) list.get(Random.Int(length));
    }

    public abstract static class SubCategory<T> {

        private final Class<?>[] classes;
        GameObjectCategory.Bag<T> bag;

        protected SubCategory(Class<?>[] classes) {
			this.classes = classes;
		}

        public Class<?>[] getClasses() {
            return classes;
        }

        public abstract String messageKey();

        public abstract Image getSprite();

        public String getName() {
            return Messages.get(getClass().getEnclosingClass(), messageKey());
        }

    }

    static class Bag<T> extends EditorItemBag {
        protected final SubCategory<T> category;

        public Bag(SubCategory<T> category) {
            super(category.messageKey(), 0);
            this.category = category;
            this.category.bag = this;
            updateBag();
        }

        @Override
        public Image getCategoryImage() {
            return category.getSprite();
        }

        @Override
        public String name() {
            return category.getName();
        }

        public void updateBag() {
            items.clear();
            for (Class<?> clazz : category.classes) {
                items.add(createItem(clazz));
            }
        }


        private static Item createItem(Class<?> clazz) {
            if (Mob.class.isAssignableFrom(clazz)) return new MobItem(initMob((Class<? extends Mob>) clazz));
            if (Trap.class.isAssignableFrom(clazz)) return new TrapItem(initTrap((Class<? extends Trap>) clazz));
            if (Buff.class.isAssignableFrom(clazz)) return new BuffItem(initBuff((Class<? extends Buff>) clazz));

            //includes Item, Plant, Room
            return EditorItem.wrapObject(Reflection.newInstance(clazz));
        }

        protected static Mob initMob(Class<? extends Mob> mobClass) {
            Mob mob = Reflection.newInstance(mobClass);
            if (mob instanceof WandOfRegrowth.Lotus) {
                ((WandOfRegrowth.Lotus) mob).setLevel(7);
            }
            if (mob instanceof QuestNPC) {
                ((QuestNPC<?>) mob).createNewQuest();
            }
            if (mob instanceof HeroMob) ((HeroMob) mob).setInternalHero(new HeroMob.InternalHero());
            if (mob == null) throw new RuntimeException(mobClass.getName());
            mob.pos = -1;
            return mob;
        }

        private static Trap initTrap(Class<? extends Trap> trapClass) {
            Trap trap = Reflection.newInstance(trapClass);
            trap.pos = -1;
            trap.visible = true;
            return trap;
        }

        private static Buff initBuff(Class<? extends Buff> buffClass) {
            Buff buff = Reflection.newInstance(buffClass);
            buff.permanent = !(buff instanceof ChampionEnemy); //for description
            return buff;
        }
    }

    public static class CustomObjectBag extends EditorItemBag {

        private final GameObjectCategory<?> category;

        public CustomObjectBag(GameObjectCategory<?> category) {
            super("name", -1);
			this.category = category;
		}

        @Override
        public Image getCategoryImage() {
            return Icons.TALENT.get();
        }

        public ScrollingListPane.ListButton createAddBtn() {
            return category.createAddBtn();
        }
    }

}