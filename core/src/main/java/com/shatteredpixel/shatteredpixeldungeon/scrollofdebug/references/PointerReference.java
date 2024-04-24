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

package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references;

import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector.FieldLike;

import java.lang.ref.WeakReference;

public class PointerReference extends StandardReference {

	private final WeakReference<Object> ref;

	/**
	 * <b>Should not be available for <u>Primitives</u> and <u>String</u>!</b>
	 */
	public PointerReference(FieldLike field, Object obj, String name) {
		super(field.getType(), null, name, null, null);
		this.ref = new WeakReference<>(obj);
	}

	@Override
	public Object getValue() {
		try {
			return ref.get();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * IMPORTANT: call <code>System.gc()</code> before!
	 */
	public boolean hasNoReference() {
		return ref.get() == null;
	}

	public Object valueViaParent() throws ReferenceNotFoundException {
		Object parentValue = ref.get();
		if (parentValue == null) throw new ReferenceNotFoundException("Reference is null!");
		return parentValue;
	}

}