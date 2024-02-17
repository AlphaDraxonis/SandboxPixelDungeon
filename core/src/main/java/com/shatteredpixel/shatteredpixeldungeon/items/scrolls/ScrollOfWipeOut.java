/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class ScrollOfWipeOut extends Scroll {
	
	{
		image = ItemSpriteSheet.SCROLL_WIPE_OUT;
	}

	@Override
	public void doRead() {
		GameScene.flash( 0xFF6644 );
		
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (!(mob instanceof Goo || mob instanceof Tengu || mob instanceof DM300 || mob instanceof DwarfKing || mob instanceof YogDzewa) ) {
				Sample.INSTANCE.play( Assets.Sounds.CURSED, 0.3f, 0.3f, Random.Float( 0.6f, 0.9f ) );
				mob.die( this );
			}
		}
		
		for (Heap heap : Dungeon.level.heaps.values()) {
			if (heap.type == Heap.Type.FOR_SALE) {
				heap.type = Heap.Type.HEAP;
				if (Dungeon.level.heroFOV[heap.pos]) {
					CellEmitter.center( heap.pos ).burst( Speck.factory( Speck.COIN ), 2 );
				}
			}
		}
		
		curUser.spend( TIME_TO_READ );
		curUser.busy();
		((HeroSprite)curUser.sprite).read();
	}

	@Override
	public boolean isKnown() {
		return true;
	}

	@Override
	public int value() {
		return 100 * quantity;
	}
}