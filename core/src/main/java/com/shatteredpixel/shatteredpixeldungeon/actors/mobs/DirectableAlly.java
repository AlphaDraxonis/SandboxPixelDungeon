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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

//use composition instead of inheritance
public class DirectableAlly implements Bundlable {

	//**SUPER IMPORTANT METHODS!!! MAKE SURE TO OVERRIDE THESE 2 WITHOUT! CALLING SUPER!
	//and getDirectableAlly()!!
	//and don't forget to store/restore from bundle
	public void aggroOverride(Char ch) {
		mob.enemy = ch;
		if (!movingToDefendPos && mob.state != mob.PASSIVE){
			mob.state = mob.HUNTING;
		}
	}

	//**SUPER IMPORTANT METHODS!!! MAKE SURE TO OVERRIDE THESE 2 WITHOUT! CALLING SUPER!

	public void beckonOverride(int cell) {
		//use from NPC, do nothing
	}

	//**SUPER IMPORTANT METHODS!!! MAKE SURE TO OVERRIDE THESE 2 WITHOUT! CALLING SUPER!


	protected /*final*/ Mob mob;

	public DirectableAlly() {
	}
	
	public void setMob(Mob mob) {
		this.mob = mob;
		
		mob.intelligentAlly = true;
		
		Mob.AiState oldWandering = mob.WANDERING;
		Mob.AiState oldHunting = mob.HUNTING;
		mob.WANDERING = createWandering(mob);
		mob.HUNTING = createHunting(mob);
		if (mob.state == oldWandering) mob.state = mob.WANDERING;
		else if (mob.state == oldHunting) mob.state = mob.HUNTING;
		
		//before other mobs
		mob.setActPriority_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(Actor.MOB_PRIO + 1);
	}

	public boolean attacksAutomatically = true;

	public int defendingPos = -1;
	protected boolean movingToDefendPos = false;

	public void defendPos( int cell ){
		defendingPos = cell;
		movingToDefendPos = true;
		mob.aggro(null);
		mob.state = mob.WANDERING;
	}

	public void clearDefensingPos(){
		defendingPos = -1;
		movingToDefendPos = false;
	}

	public void followHero(){
		defendingPos = -1;
		movingToDefendPos = false;
		mob.aggro(null);
		mob.state = mob.WANDERING;
	}

	public void targetChar( Char ch ){
		defendingPos = -1;
		movingToDefendPos = false;
		mob.aggro(ch);
		mob.target = ch.pos;
	}

	public void directTocell( int cell ){
		Char charAtCell;
		if (!Dungeon.level.heroFOV[cell]
				|| (charAtCell = Actor.findChar(cell)) == null
				|| (charAtCell != Dungeon.hero && charAtCell.alignment != Char.Alignment.ENEMY)){
			defendPos( cell );
			return;
		}

		if (charAtCell == Dungeon.hero){
			followHero();

		} else if (charAtCell.alignment == Char.Alignment.ENEMY){
			targetChar(charAtCell);

		}
	}

	private static final String DEFEND_POS = "defend_pos";
	private static final String MOVING_TO_DEFEND = "moving_to_defend";
	
	@Deprecated/*(forRemoval = true)*/
	public boolean maybeRestore(Bundle bundle) {
		if (bundle.contains("directable_ally")) {
			StoreInBundle stored = (StoreInBundle) bundle.get("directable_ally");
			defendingPos = stored.defendingPos;
			movingToDefendPos = stored.movingToDefendPos;
			return true;
		}
		return false;
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		if (bundle.contains(DEFEND_POS)) defendingPos = bundle.getInt(DEFEND_POS);
		movingToDefendPos = bundle.getBoolean(MOVING_TO_DEFEND);
	}
	
	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(DEFEND_POS, defendingPos);
		bundle.put(MOVING_TO_DEFEND, movingToDefendPos);
	}


	@Deprecated/*(forRemoval = true)*/
	public static class StoreInBundle implements Bundlable {

		private int defendingPos;
		private boolean movingToDefendPos;

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put(DEFEND_POS, defendingPos);
			bundle.put(MOVING_TO_DEFEND, movingToDefendPos);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			if (bundle.contains(DEFEND_POS)) defendingPos = bundle.getInt(DEFEND_POS);
			movingToDefendPos = bundle.getBoolean(MOVING_TO_DEFEND);
		}

	}

	private Mob.Wandering createWandering(Mob mob) {
		return mob.new Wandering() {

			@Override
			public boolean act(boolean enemyInFOV, boolean justAlerted) {
				if (enemyInFOV
						&& attacksAutomatically
						&& !movingToDefendPos
						&& (defendingPos == -1 || !Dungeon.level.heroFOV[defendingPos] || mob.canAttack(mob.enemy))) {

					mob.enemySeen = true;

					mob.notice();
					mob.alerted = true;
					mob.state = mob.HUNTING;
					mob.target = mob.enemy.pos;

				} else {

					mob.enemySeen = false;

					int oldPos = mob.pos;
					mob.target = defendingPos != -1 ? defendingPos : Dungeon.hero.pos;
					//always move towards the hero when wandering
					if (mob.getCloser(mob.target)) {
						mob.spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(1 / mob.speed());
						if (mob.pos == defendingPos) movingToDefendPos = false;
						return mob.moveSprite(oldPos, mob.pos);
					} else {
						//if it can't move closer to defending pos, then give up and defend current position
						if (movingToDefendPos) {
							defendingPos = mob.pos;
							movingToDefendPos = false;
						}
						mob.spend_DO_NOT_CALL_UNLESS_ABSOLUTELY_NECESSARY(Actor.TICK);
					}

				}
				return true;
			}
		};

	}

	private Mob.Hunting createHunting(Mob mob) {
		return mob.new Hunting() {

			@Override
			public boolean act(boolean enemyInFOV, boolean justAlerted) {
				if (enemyInFOV && defendingPos != -1 && Dungeon.level.heroFOV[defendingPos] && !mob.canAttack(mob.enemy)) {
					mob.target = defendingPos;
					mob.state = mob.WANDERING;
					return true;
				}
				return super.act(enemyInFOV, justAlerted);
			}

		};
	}

}
