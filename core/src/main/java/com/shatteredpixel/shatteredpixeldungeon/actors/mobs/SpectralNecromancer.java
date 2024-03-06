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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpectralNecromancerSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class SpectralNecromancer extends Necromancer {

	{
		spriteClass = SpectralNecromancerSprite.class;

		defaultTemplateClass = Wraith.class;
		Mob summon = Reflection.newInstance(defaultTemplateClass);
		summon.state = summon.HUNTING;
		summonTemplate.clear();
		summonTemplate.add(summon);
	}

	private ArrayList<Integer> wraithIDs = new ArrayList<>();
	private boolean justLoaded = true;

	@Override
	protected boolean act() {
		if (justLoaded) {
			for (int wraithID : wraithIDs) {
				Actor ch = Actor.findById(wraithID);
				if (ch instanceof Mob) {
					((Mob) ch).spawningWeight_NOT_SAVED_IN_BUNDLE = 0;//spawningWeight is not stored is bundle, but mobs act before level respawner
				}
			}
			justLoaded = false;
		}
		if (summoning && state != HUNTING){
			summoning = false;
			if (sprite instanceof SpectralNecromancerSprite) {
				((SpectralNecromancerSprite) sprite).cancelSummoning();
			}
		}
		return super.act();
	}

	@Override
	public void rollToDropLoot() {
		if (Dungeon.hero.lvl > maxLvl + Mob.DROP_LOOT_IF_ABOVE_MAX_LVL) return;

		super.rollToDropLoot();

		if (!(loot instanceof ItemsWithChanceDistrComp.RandomItemData)) {
			int ofs;
			int tries = 100;
			do {
				ofs = PathFinder.NEIGHBOURS8[Random.Int(8)];
			} while (Dungeon.level.solid[pos + ofs] && !Dungeon.level.isPassableHero(pos + ofs) && tries-- > 0);
			Dungeon.level.drop(new ScrollOfRemoveCurse(), pos + ofs).sprite.drop(pos);
		}
	}

	@Override
	public float lootChance() {
		if (this.lootChance == 1f) return 1f;
		return super.lootChance();
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = super.convertLootToRandomItemData();
		for (ItemsWithChanceDistrComp.ItemWithCount item : customLootInfo.distrSlots) {
			Item scroll = new ScrollOfRemoveCurse();
			scroll.spreadIfLoot = true;
			item.items.add(scroll);
		}
		int noLootChance = (int) ((1f - customLootInfo.lootChance()) * customLootInfo.calculateSum());
		Item scroll = new ScrollOfRemoveCurse();
		scroll.spreadIfLoot = true;
		customLootInfo.addItem(scroll, noLootChance);
		customLootInfo.setLootChance(0);
		return customLootInfo;
	}

	@Override
	public void die(Object cause) {
		for (int ID : wraithIDs){
			Actor a = Actor.findById(ID);
			if (a instanceof Mob){
				((Mob) a).die(null);
			}
		}

		super.die(cause);
	}

	private static final String WRAITH_IDS = "wraith_ids";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		int[] wraithIDArr = new int[wraithIDs.size()];
		int i = 0; for (Integer val : wraithIDs){ wraithIDArr[i] = val; i++; }
		bundle.put(WRAITH_IDS, wraithIDArr);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		wraithIDs.clear();
		for (int i : bundle.getIntArray(WRAITH_IDS)){
			wraithIDs.add(i);
		}
	}

	@Override
	public Mob summonMinion() {
		Mob summoned;
		if ((summoned = super.summonMinion()) != null) {
			wraithIDs.add(summoned.id());
			mySummon = null;
		}
		return summoned;
	}

	@Override
	protected Mob convertToSummonedMob(Mob mob) {
		mob = super.convertToSummonedMob(mob);
		mob.HP = mob.HT;
		if (mob instanceof MobBasedOnDepth) ((MobBasedOnDepth) mob).setLevel(0);
		return mob;
	}
}