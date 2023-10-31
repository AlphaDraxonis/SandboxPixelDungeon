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

package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SkeletonKey extends Key {
	
	{
		image = ItemSpriteSheet.SKELETON_KEY;
	}
	
	public SkeletonKey() {
		this( Dungeon.levelName, -1);
	}

	public SkeletonKey( String levelName, int cell ) {
		super();
		if (Level.NONE.equals(levelName)) this.levelName = CustomLevel.tempDungeonNameForKey;
		else this.levelName = levelName;
		this.cell = cell;
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
//		if(!SPDSettings.supportNagged()){
//			try {
//				Dungeon.saveAll();
//				Game.runOnRenderThread(new Callback() {
//					@Override
//					public void call() {
//						SandboxPixelDungeon.scene().add(new WndSupportPrompt());
//					}
//				});
//			} catch (IOException e) {
//				SandboxPixelDungeon.reportException(e);
//			}
//
//		}
		
		return super.doPickUp(hero, pos);
	}

}