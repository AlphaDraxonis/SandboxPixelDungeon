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
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    Bag<T> createBag(SubCategory<T> category) {
        return new Bag<>(category);
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
            ((GameObject) obj).initAsInventoryItem();
            sortCustomObjects();
        }
    }

    public abstract void updateCustomObjects();

    protected void updateCustomObjects(Class<? extends GameObject> type) {
        //Need to keep the same reference for the Undo list
        if (Undo.canUndo() || Undo.canRedo()) {
            ArrayList<Item> newItems = new ArrayList<>();
            oneObj:
            for (GameObject obj : CustomObjectManager.getAllCustomObjects(type)) {
                int ident = ((CustomObjectClass) obj).getIdentifier();
                for (Item i : customObjectBag.items) {
                    EditorItem<?> item = (EditorItem<?>) i;
                    if (((CustomObjectClass) item.getObject()).getIdentifier() == ident) {
                        newItems.add(item);
                        CustomObjectManager.overrideOriginal((CustomObjectClass) item.getObject());
                        continue oneObj;
                    }
                }
                newItems.add(EditorItem.wrapObject(obj));
            }
            customObjectBag.items = newItems;
        } else {
            customObjectBag.clear();
            for (GameObject obj : CustomObjectManager.getAllCustomObjects(type)) {
                customObjectBag.items.add(EditorItem.wrapObject(obj));
            }
        }

        sortCustomObjects();
    }
    
    private void sortCustomObjects() {
        Collections.sort(customObjectBag.items, SORT_COMPARATOR);
    }
    
    private static final Comparator<Item> SORT_COMPARATOR = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            int id1 =  ((CustomObjectClass) ((EditorItem<?>) o1).getObject()).getIdentifier();
            int id2 =  ((CustomObjectClass) ((EditorItem<?>) o2).getObject()).getIdentifier();
            String n1 = CustomObjectManager.getName(id1);
            String n2 = CustomObjectManager.getName(id2);
            return n1 == null || n2 == null ? 0 : n1.compareTo(n2);
        }
    };

    public void updateCustomObject(CustomObject customObject) {
        Undo.startAction();
        int ident = customObject.getIdentifier();
        for (Item i : customObjectBag.items) {
            CustomGameObjectClass obj = (CustomGameObjectClass) ((EditorItem<?>) i).getObject();
            if (obj.getIdentifier() == ident) {
                CustomGameObjectClass.updateInheritStats(obj, Dungeon.level);
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
        Bag<T> bag;

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
            Object obj = Reflection.newInstance(clazz);
            
            if (obj instanceof GameObject) {
                ((GameObject) obj).initAsInventoryItem();
            }

            return EditorItem.wrapObject(obj);
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
