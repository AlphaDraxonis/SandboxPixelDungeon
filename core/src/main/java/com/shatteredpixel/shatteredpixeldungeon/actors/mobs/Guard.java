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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GuardSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Guard extends Mob {

	//they can only use their chains once in Shattered
	private int chainCooldown = 0;
	public int maxChainCooldown = 1_000_000;

	{
		spriteClass = GuardSprite.class;

		HP = HT = 40;
		defenseSkill = 10;
		attackSkill = 12;
		damageRollMin = 4;
		damageRollMax = 12;
		damageReductionMax = 7;

		EXP = 7;
		maxLvl = 14;

		loot = Generator.Category.ARMOR;
		lootChance = 0.2f; //by default, see lootChance()

		properties.add(Property.UNDEAD);
		
		HUNTING = new Hunting();
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange(4, 12);
//	}

	public boolean chain(int target){
		if (chainCooldown > 0 || enemy.properties().contains(Property.IMMOVABLE))
			return false;

		Ballistica chain = new Ballistica(pos, target, Ballistica.REAL_PROJECTILE, null);

		if (chain.collisionPos != enemy.pos
				|| chain.path.size() < 2
				|| Dungeon.level.pit[chain.path.get(1)]
				|| !Dungeon.level.isPassable(chain.path.get(1), enemy))
			return false;
		else {
			int newPos = -1;
			for (int i : chain.subPath(1, chain.dist)){
				if (!Dungeon.level.solid[i] && Actor.findChar(i) == null){
					newPos = i;
					break;
				}
			}

			if (newPos == -1){
				return false;
			} else {
				final int newPosFinal = newPos;
				this.target = newPos;

				if (sprite.visible || enemy.sprite.visible) {
					yell(Messages.get(this, "scorpion"));
					new Item().throwSound();
					Sample.INSTANCE.play(Assets.Sounds.CHAINS);
					sprite.parent.add(new Chains(sprite.center(),
							enemy.sprite.destinationCenter(),
							Effects.Type.CHAIN,
							new Callback() {
						public void call() {
							Actor.add(new Pushing(enemy, enemy.pos, newPosFinal, new Callback() {
								public void call() {
									pullEnemy(enemy, newPosFinal);
								}
							}));
							next();
						}
					}));
				} else {
					pullEnemy(enemy, newPos);
				}
			}
		}
		chainCooldown = maxChainCooldown;
		return true;
	}

	private void pullEnemy( Char enemy, int pullPos ){
		enemy.pos = pullPos;
		enemy.sprite.place(pullPos);
		Dungeon.level.occupyCell(enemy);
		Cripple.prolong(enemy, Cripple.class, 4f);
		if (enemy == Dungeon.hero) {
			Dungeon.hero.interrupt();
			Dungeon.observe();
			GameScene.updateFog();
		}
	}

//	@Override
//	public int attackSkill( Char target ) {
//		return 12;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 7);
//	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/5, 1/15, 1/45, 1/135, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.GUARD_ARM.count);
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = super.convertLootToRandomItemData();
		int set;
		if (Dungeon.level == null || Dungeon.level.levelScheme == null) set = (int) (Math.random() * 5);
		else set = Dungeon.level.levelScheme.getRegion() - 1;
		Generator.convertRandomArmorToCustomLootInfo(customLootInfo, set);
		customLootInfo.setLootChance(customLootInfo.calculateSum() * 4);
		return customLootInfo;
	}

	@Override
	public void increaseLimitedDropCount(Item generatedLoot) {
		if (generatedLoot instanceof Armor)
			Dungeon.LimitedDrops.DM200_EQUIP.count++;
		super.increaseLimitedDropCount(generatedLoot);
	}

	private final String CHAIN_COOLDOWN = "chain_cooldown";
	private final String MAX_CHAIN_COOLDOWN = "max_chain_cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHAIN_COOLDOWN, chainCooldown);
		bundle.put(MAX_CHAIN_COOLDOWN, maxChainCooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains("chainsused")){
			chainCooldown = bundle.getBoolean("chainsused") ? 0 : 1_000_000;
			maxChainCooldown = 1_000_000;
		} else {
			chainCooldown = bundle.getInt(CHAIN_COOLDOWN);
			maxChainCooldown = bundle.getInt(MAX_CHAIN_COOLDOWN);
		}
	}

	@Override
	protected boolean act() {
		chainCooldown--;
		return super.act();
	}

	private class Hunting extends Mob.Hunting{
		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			
			if (chainCooldown <= 0
					&& enemyInFOV
					&& !isCharmedBy( enemy )
					&& !canAttack( enemy )
					&& Dungeon.level.distance( pos, enemy.pos ) < 5
					&& enemy.invisible <= 0

					
					&& chain(enemy.pos)){
				return !(sprite.visible || enemy.sprite.visible);
			} else {
				return super.act( enemyInFOV, justAlerted );
			}
			
		}
	}
}
