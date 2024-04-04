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

import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

import java.util.*;

public interface EditorInvCategory<T> {

    String name();

    Class<?>[] classes();
    Image getSprite();

    default String getName() {
        return Messages.get(this, name().toLowerCase(Locale.ENGLISH));
    }

    static <T> Class<?>[][] getAll(EditorInvCategory<T>[] all) {
        return getAll(all, null);
    }

    static <T> Class<?>[][] getAll(EditorInvCategory<T>[] all, Set<Class<? extends T>> toIgnore) {
        Class<?>[][] ret = new Class[all.length][];
        for (int i = 0; i < all.length; i++) {
            List<Class<?>> list = new ArrayList<>(Arrays.asList(all[i].classes()));
            if (toIgnore != null) list.removeAll(toIgnore);
            ret[i] = list.toArray(EditorUtilies.EMPTY_CLASS_ARRAY);
        }
        return ret;
    }

    static <T> Class<? extends T> getRandom(EditorInvCategory<T>[] all) {
        return getRandom(all, null);
    }

    static <T> Class<? extends T> getRandom(EditorInvCategory<T>[] all, Set<Class<? extends T>> toIgnore) {
        List<Class<?>> list = new ArrayList<>();
        for (int i = 0; i < all.length; i++) {
            list.addAll(Arrays.asList(all[i].classes()));
        }
        if (toIgnore != null) list.removeAll(toIgnore);

        int length = list.size();
        if (length == 0) return null;
        return (Class<? extends T>) list.get(Random.Int(length));
    }


    class EditorInvCategoryBag extends EditorItemBag {
        private final EditorInvCategory<?> cat;

        public <T> EditorInvCategoryBag(EditorInvCategory<T> cat) {
            super(cat.name().toLowerCase(Locale.ENGLISH), 0);
            this.cat = cat;
        }

        @Override
        public Image getCategoryImage() {
            return cat.getSprite();
        }

        @Override
        public String name() {
            return cat.getName();
        }
    }

}