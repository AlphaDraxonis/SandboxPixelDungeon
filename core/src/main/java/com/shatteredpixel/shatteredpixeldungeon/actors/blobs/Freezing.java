/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Freezing extends Blob {
	
	@Override
	protected void evolve() {
		
		int cell;
		
		Blob[] fires = Dungeon.level.blobs.get( Fire.class );
		
		for (int i = area.left-1; i <= area.right; i++) {
			for (int j = area.top-1; j <= area.bottom; j++) {
				cell = i + j*Dungeon.level.width();
				if (cur[cell] > 0) {

					boolean foundFire = false;
					for (int k = 0; k < fires.length; k++) {
						if (fires[k].cur == null) continue;
						if (fires[k].volume > 0 && fires[k].cur[cell] > 0){
							fires[k].clear(cell);
							off[cell] = cur[cell] = 0;
							foundFire = true;
						}
					}
					if (foundFire) continue;
					
					Freezing.freeze(cell);
					
					off[cell] = cur[cell] - 1;
					volume += off[cell];
				} else {
					off[cell] = 0;
				}
			}
		}
	}
	
	public static void freeze( int cell ){
		Char ch = Actor.findChar( cell );
		if (ch != null && !ch.isImmune(Freezing.class)) {
			if (ch.buff(Frost.class) != null){
				Buff.affect(ch, Frost.class, 2f);
			} else {
				Chill chill = ch.buff(Chill.class);
				float turnsToAdd = Dungeon.level.water[cell] ? 5f : 3f;
				if (chill != null){
					float chillToCap = Chill.DURATION - chill.cooldown();
					chillToCap /= ch.resist(Chill.class); //account for resistance to chill
					turnsToAdd = Math.min(turnsToAdd, chillToCap);
				}
				if (turnsToAdd > 0f) {
					Buff.affect(ch, Chill.class, turnsToAdd);
				}
				if (chill != null
						&& chill.cooldown() >= Chill.DURATION &&
						!ch.isImmune(Frost.class)){
					Buff.affect(ch, Frost.class, Frost.DURATION);
				}
			}
		}
		
		Heap heap = Dungeon.level.heaps.get( cell );
		if (heap != null) heap.freeze();
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( SnowParticle.FACTORY, 0.05f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
	
	//legacy functionality from before this was a proper blob. Returns true if this cell is visible
	public static boolean affect( int cell ) {
		
		Char ch = Actor.findChar( cell );
		if (ch != null) {
			if (Dungeon.level.water[ch.pos]){
				Buff.prolong(ch, Frost.class, Frost.DURATION * 3);
			} else {
				Buff.prolong(ch, Frost.class, Frost.DURATION);
			}
		}

		Dungeon.level.blobs.doOnEach(Fire.class, fire ->{
			if (fire.volume > 0) {
				fire.clear( cell );
			}
		});

		Dungeon.level.blobs.doOnEach(MagicalFireRoom.EternalFire.class, eternalFire ->{
			if (eternalFire.volume > 0) {
				eternalFire.clear( cell );
			}
		});
		
		Heap heap = Dungeon.level.heaps.get( cell );
		if (heap != null) {
			heap.freeze();
		}
		
		if (Dungeon.level.heroFOV[cell]) {
			CellEmitter.get( cell ).start( SnowParticle.FACTORY, 0.2f, 6 );
			return true;
		} else {
			return false;
		}
	}
}