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

package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@NotAllowedInLua
public class WndCustomizeTalents extends Window {

//	So sehen talente aus:
//
//	für jeden hero individuell, nicht in allgemein verfügbar
//	t1 und t2, t3teil, t4teil(HEROIC_ENERGY) immer fix default visible bei helden
//	t3 subclass nach subclasses getrennt anzeigen
//	t4 nach armor ability getrennt anzeigen

	public WndCustomizeTalents(HeroClass heroClass, ArrayList<LinkedHashMap<Talent, Integer>> talents) {

		//init talents: Talent.initClassTalents(heroClass, talents);

//		while (talents.size() < Talent.MAX_TALENT_TIERS){
//			talents.add(new LinkedHashMap<>());
//		}

		//TODO test: do duplicates work?

		//Klasse 1:
		//1-5 talents per row, layout like talentstab, plus button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//line

		//Klasse 2:
		//1-5 talents per row, layout like talentstab, plus button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//line

		//Klasse 3:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//Subclass 1:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//Subclass 2:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//...

		//line

		//Klasse 4:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//Armor 1:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//Armor 2:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//Armor 3:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned

		//RatArmor:
		//1-5 talents per row, layout like talentstab, the plus-button takes as much space as one talent
		//if more than 5: layout in next row, but left-aligned
	}



}
