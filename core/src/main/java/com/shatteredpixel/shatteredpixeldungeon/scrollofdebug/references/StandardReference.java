/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2025 AlphaDraxonis
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

public class StandardReference extends Reference {

	private final Reference parent;
	private final FieldLike parentField;

	public StandardReference(Class<?> type, Object value, String name, Reference parent, FieldLike parentField) {
		super(type, value, name);
		this.parent = parent;
		this.parentField = parentField;
	}

	public Object valueViaParent() throws ReferenceNotFoundException {
		Object parentValue = parent.valueViaParent();
		try {
			return parentField.get(parentValue);
		} catch (Exception e) {
			throw new ReferenceNotFoundException(e);
		}
	}

}
