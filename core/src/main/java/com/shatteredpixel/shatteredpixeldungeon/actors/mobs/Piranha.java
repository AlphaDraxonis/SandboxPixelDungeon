/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Piranha extends Mob implements MobBasedOnDepth {
	
	{
		spriteClass = PiranhaSprite.class;

		baseSpeed = 2f;
		
		EXP = 0;
		
		loot = MysteryMeat.class;
		lootChance = 1f;
		
		SLEEPING = new Sleeping();
		WANDERING = new Wandering();
		HUNTING = new Hunting();

		properties.add(Property.AQUATIC);

		state = SLEEPING;

	}
	
	public Piranha() {
		super();
		
		setLevel(Dungeon.depth);
	}

	@Override
	public void setLevel(int depth) {
		HT = (int) (10 + depth * 5 * statsScale);
		defenseSkill = 10 + depth * 2;

		if (!hpSet) {
			HP = HT;
			hpSet = !CustomDungeon.isEditing();
		}
	}
	
	@Override
	public int damageRoll() {
		return (int) (Random.NormalIntRange( Dungeon.depth, 4 + Dungeon.depth * 2 ) * statsScale);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int) ((20 + Dungeon.depth * 2) * statsScale);
	}
	
	@Override
	public int drRoll() {
		return (int) (super.drRoll() + Random.NormalIntRange(0, Dungeon.depth) * statsScale);
	}

	@Override
	public boolean surprisedBy(Char enemy, boolean attacking) {
		if (enemy == Dungeon.hero && (!attacking || ((Hero)enemy).canSurpriseAttack())){
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( this, fieldOfView );
			}
			return state == SLEEPING || !fieldOfView[enemy.pos] || enemy.invisible > 0;
		}
		return super.surprisedBy(enemy, attacking);
	}

	@Override
	public void die( Object cause ) {
		super.die( cause );
		
		Statistics.piranhasKilled++;
		Badges.validatePiranhasKilled();
	}

	@Override
	public float spawningWeight() {
		return 0;
	}

	@Override
	public boolean reset() {
		return true;
	}
	
	{
		for (Class c : new BlobImmunity().immunities()){
			if (c != Electricity.class && c != Freezing.class){
				immunities.add(c);
			}
		}

	}
	
	//if there is not a path to the enemy, piranhas act as if they can't see them
	private class Sleeping extends Mob.Sleeping{
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMapForEnvironmentals(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}
	
	private class Wandering extends Mob.Wandering{
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMapForEnvironmentals(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}
	
	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			if (enemyInFOV) {
				PathFinder.buildDistanceMapForEnvironmentals(enemy.pos, Dungeon.level.water, viewDistance);
				enemyInFOV = PathFinder.distance[pos] != Integer.MAX_VALUE;
			}
			
			return super.act(enemyInFOV, justAlerted);
		}
	}

	public static Piranha random(){
		float altChance = 1/50f * RatSkull.exoticChanceMultiplier();
		if (Random.Float() < altChance){
			return new PhantomPiranha();
		} else {
			return new Piranha();
		}
	}


	public static boolean canSurviveOnCell(Char ch, int cell, Level level) {
		if (ch != null && ch.isFlying() && Char.hasProp(ch, Property.AQUATIC)) {
			return false;
		}
		return level.water[cell]
				|| ((Terrain.flags[level.map[cell]] & Terrain.WATER) == Terrain.flags[Terrain.WATER])
				|| !Char.hasProp(ch, Property.AQUATIC)
				|| ch instanceof Hero && ( (TileItem.isEntranceTerrainCell(level.map[cell]) || TileItem.isExitTerrainCell(level.map[cell])) );
	}

}
