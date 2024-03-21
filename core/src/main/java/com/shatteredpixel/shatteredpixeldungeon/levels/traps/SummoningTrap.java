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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobBasedOnDepth;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SummoningTrap extends Trap {

	private static final float DELAY = 2f;

	{
		color = TEAL;
		shape = WAVES;
	}

	public List<Mob> spawnMobs = new ArrayList<>(5);

	@Override
	public void activate() {

		boolean useCustomConfig = !spawnMobs.isEmpty();

		int nMobs = 1;
		if (Random.Int( 2 ) == 0) {
			nMobs++;
			if (Random.Int( 2 ) == 0) {
				nMobs++;
			}
		}
		if (useCustomConfig) nMobs = spawnMobs.size();

		ArrayList<Integer> candidates = new ArrayList<>();

		Mob enemyMob = new Mob(){};

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS8[i];
			if (Barrier.canEnterCell(p, enemyMob, true, true)) {
				candidates.add( p );
			}
		}

		ArrayList<Integer> respawnPoints = new ArrayList<>();

		while (nMobs > 0 && candidates.size() > 0) {
			int index = Random.index( candidates );

			respawnPoints.add( candidates.remove( index ) );
			nMobs--;
		}

		ArrayList<Mob> mobs = new ArrayList<>();
		LinkedList<Mob> largeMobsAddLater = new LinkedList<>();

		int index = 0;
		for (Integer point : respawnPoints) {
			Mob mob;
			if (Dungeon.level.openSpace[point] && !largeMobsAddLater.isEmpty()) mob = largeMobsAddLater.removeFirst();
			else {
				boolean repeat;
				int tries = 20;
				do {
					mob = useCustomConfig ? spawnMobs.get(index) : Dungeon.level.createMob();
					if (useCustomConfig && mob instanceof MobBasedOnDepth) ((MobBasedOnDepth) mob).setLevel(Dungeon.depth);
					index++;
					tries--;
					repeat = Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point] || Barrier.stopChar(point, mob);
					if (repeat) largeMobsAddLater.add(mob);
				} while (repeat && (tries > 0 || useCustomConfig));
			}
			if (mob != null) {
				mob.state = mob.WANDERING;
				mob.pos = point;
				GameScene.add(mob, DELAY);
				mobs.add(mob);
			}
		}


		//important to process the visuals and pressing of cells last, so spawned mobs have a chance to occupy cells first
		Trap t;
		for (Mob mob : mobs){
			//manually trigger traps first to avoid sfx spam
			if ((t = Dungeon.level.traps.get(mob.pos)) != null && t.active){
				if (t.disarmedByActivation) t.disarm();
				t.reveal();
				t.activate();
			}
			ScrollOfTeleportation.appear(mob, mob.pos);
			Dungeon.level.occupyCell(mob);
		}

		if (mobs.isEmpty()) {
			GLog.w(Messages.get(this, "no_mobs"));
		}

	}

	private static final String SPAWN_MOBS = "spawn_mobs";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		Collection<Bundlable> collection = bundle.getCollection( SPAWN_MOBS );
		for (Bundlable b : collection)
			spawnMobs.add((Mob) b);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( SPAWN_MOBS, spawnMobs );
	}
}