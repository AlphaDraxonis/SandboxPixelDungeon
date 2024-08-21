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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.RandomCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.RandomGlyph;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

//just holds glyphs for a mob
public class GlyphArmor extends Armor {

	public LinkedHashMap<Class<? extends Glyph>, Glyph> glyphs = new LinkedHashMap<>(5);

	public GlyphArmor() {
		super( -1 );
	}

	@Override
	public boolean hasGlyph(Class<? extends Glyph> type, Char owner) {
		if (owner.buff(MagicImmune.class) == null) {
			//after this returned true, the hidden assumption glyph != null && type.isInstance(glyph) is made
			return (glyph = glyphs.get(type)) != null;
		}
		return false;
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		if (defender.buff(MagicImmune.class) == null) {
			for (Glyph glyph : glyphs.values()) {
				damage = glyph.proc( this, attacker, defender, damage );
			}
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public String info() {
		StringBuilder b = new StringBuilder();
		for (Glyph glyph : glyphs.values()) {
			b.append(glyph.name()).append(", ");
		}
		if (glyphs.size() > 1) b.setLength(b.length() - 3);
		return b.toString();
	}

	public boolean hasGlyphs() {
		return !glyphs.isEmpty();
	}


	private static final String GLYPHS = "glyphs";
	private static final String LEVEL = "level";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		level(bundle.getInt( LEVEL) );
		for (Class<? extends Glyph> glyph : bundle.getClassArray(GLYPHS)) {
			addGlyph(Reflection.newInstance(glyph));
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put( LEVEL, trueLevel() );
		bundle.put( GLYPHS, glyphs.keySet().toArray(EditorUtilities.EMPTY_CLASS_ARRAY) );
	}

	public void addGlyph(Glyph glyph) {
		if (!glyphs.containsKey(glyph.getClass())/* || glyph.getClass() == RandomGlyph.class || glyph.getClass() == RandomCurse.class*/) {
			glyphs.put(glyph.getClass(), glyph);
		}
	}

	public void removeGlyph(Class<? extends Glyph> glyph) {
		glyphs.remove(glyph);
	}

	public static boolean areEqual(GlyphArmor a, GlyphArmor b) {
		return a.glyphs.keySet().equals(b.glyphs.keySet());
	}

	public boolean replaceRandom() {
		boolean changedSth = false;
		Set<Class<? extends Glyph>> existingGlyphs = new HashSet<>();
		existingGlyphs.addAll(glyphs.keySet());

		if (glyphs.containsKey(RandomGlyph.class)) {
			int tries = 100;
			Glyph newGlyph;
			do {
				newGlyph = Glyph.random();
				if (tries-- < 0) {
					newGlyph = null;
					break;
				}
			} while (existingGlyphs.contains(newGlyph.getClass()));
			if (newGlyph != null) {
				addGlyph(glyph);
				existingGlyphs.add(glyph.getClass());
				changedSth = true;
			}
		}

		if (glyphs.containsKey(RandomCurse.class)) {
			int tries = 100;
			Glyph newGlyph;
			do {
				newGlyph = Glyph.randomCurse();
				if (tries-- < 0) {
					newGlyph = null;
					break;
				}
			} while (existingGlyphs.contains(newGlyph.getClass()));
			if (newGlyph != null) {
				addGlyph(glyph);
				existingGlyphs.add(glyph.getClass());
				changedSth = true;
			}
		}

		return changedSth;
	}
}