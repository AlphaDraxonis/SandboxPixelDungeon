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
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.LinkedHashMap;

//just holds glyphs for a mob
public class GlyphArmor extends Armor {

	public LinkedHashMap<Class<? extends Glyph>, Glyph> glyphs = new LinkedHashMap<>(5);

	public GlyphArmor() {
		super( -1 );
	}

	@Override
	public boolean hasGlyph(Class<? extends Glyph> type, Char owner) {
		return glyphs.containsKey(type) && owner.buff(MagicImmune.class) == null;
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
		bundle.put( GLYPHS, glyphs.keySet().toArray(EditorUtilies.EMPTY_CLASS_ARRAY) );
	}

	public void addGlyph(Glyph glyph) {
		glyphs.put(glyph.getClass(), glyph);
	}

	public void removeGlyph(Class<? extends Glyph> glyph) {
		glyphs.remove(glyph);
	}

	public static boolean areEqual(GlyphArmor a, GlyphArmor b) {
		return a.glyphs.keySet().equals(b.glyphs.keySet());
	}
}