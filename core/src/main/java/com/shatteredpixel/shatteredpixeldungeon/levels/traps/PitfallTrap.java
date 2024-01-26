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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PitfallParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class PitfallTrap extends Trap {

	{
		color = RED;
		shape = DIAMOND;
	}

	public int delay = 1, radius = 1;

	@Override
	public void activate() {

		if (Dungeon.bossLevel() || Dungeon.curLvlScheme().getChasm() == null || Dungeon.branch != 0) {
			GLog.w(Messages.get(this, "no_pit"));
			return;
		}

		DelayedPit p = Buff.append(Dungeon.hero, DelayedPit.class, delay);
		p.activatedOn = Dungeon.levelName;
		p.branch = Dungeon.branch;
		p.pos = pos;
		p.radius = radius;
		if (delay == 0) p.act();

		for (int i : PathFinder.GET_ALL_CELLS_IN_RADIUS(radius)){
			int cell = pos + i;
			if (cell < 0 || cell > Dungeon.level.length()) continue;
			if (!Dungeon.level.solid[cell] || Dungeon.level.isPassable(cell)){
				CellEmitter.floor(cell).burst(PitfallParticle.FACTORY4, 8);
			}
		}

		if (pos == Dungeon.hero.pos){
			GLog.n(Messages.get(this, "triggered_hero"));
		} else if (Dungeon.level.heroFOV[pos]){
			GLog.n(Messages.get(this, "triggered"));
		}

	}

	public static class DelayedPit extends FlavourBuff {

		{
			revivePersists = true;
		}

		int pos;
		String activatedOn;
		int branch;
		int radius, delay;

		@Override
		public boolean act() {
			delay--;

			boolean herofell = false;
			if (activatedOn.equals(Dungeon.levelName) && branch == Dungeon.branch) {
				for (int i : PathFinder.GET_ALL_CELLS_IN_RADIUS(radius)) {

					int cell = pos + i;
					if (cell < 0 || cell > Dungeon.level.length()) continue;

					if (delay > 0 ) {
						if (!Dungeon.level.solid[cell] || Dungeon.level.isPassable(cell)){
							CellEmitter.floor(cell).burst(PitfallParticle.FACTORY4, 8);
						}
						continue;
					}

					if (Dungeon.level.solid[pos+i] && !Dungeon.level.isPassable(pos+i)){
						continue;
					}

					CellEmitter.floor(pos+i).burst(PitfallParticle.FACTORY8, 12);

					Heap heap = Dungeon.level.heaps.get(cell);

					if (heap != null && heap.type != Heap.Type.FOR_SALE
							&& heap.type != Heap.Type.LOCKED_CHEST
							&& heap.type != Heap.Type.CRYSTAL_CHEST) {
						for (Item item : heap.items) {
							Dungeon.dropToChasm(item);
						}
						heap.sprite.kill();
						GameScene.discard(heap);
						heap.sprite.drop();
						Dungeon.level.heaps.remove(cell);
					}

					Char ch = Actor.findChar(cell);

					//don't trigger on flying chars, or immovable neutral chars
					if (ch != null && !ch.avoidsHazards() && ! ch.isFlying()
						&& !(ch.alignment == Char.Alignment.NEUTRAL && Char.hasProp(ch, Char.Property.IMMOVABLE))) {
						if (ch == Dungeon.hero) {
							Chasm.heroFall(cell);
							herofell = true;
						} else {
							Chasm.mobFall((Mob) ch);
						}
					}

				}
			}

			detach();
			return !herofell;
		}

		private static final String POS = "pos";
		private static final String ACTIVATED_ON = "activated_on";
		private static final String BRANCH = "branch";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
			bundle.put(ACTIVATED_ON, activatedOn);
			bundle.put(BRANCH, branch);
			bundle.put(RADIUS, radius);
			bundle.put(DELAY, delay);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
			activatedOn = bundle.getString(ACTIVATED_ON);
			branch = bundle.getInt(BRANCH);
			radius = bundle.getInt(RADIUS);
			delay = bundle.getInt(DELAY);
		}

	}

	private static final String RADIUS = "radius";
	private static final String DELAY = "delay";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(RADIUS, radius);
		bundle.put(DELAY, delay);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(RADIUS)) {
			radius = bundle.getInt(RADIUS);
			delay = bundle.getInt(DELAY);
		}
	}
}