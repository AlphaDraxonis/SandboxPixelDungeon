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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TormentedSpiritSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class TormentedSpirit extends Wraith {

	{
		spriteClass = TormentedSpiritSprite.class;
	}

	public Item prize;

	//50% more damage scaling than regular wraiths
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 1 + Math.round(1.5f*level)/2, 2 + Math.round(1.5f*level) );
	}

	//50% more accuracy (and by extension evasion) scaling than regular wraiths
	@Override
	public int attackSkill( Char target ) {
		return 10 + Math.round(1.5f*level);
	}

	public void cleanse(){
		Sample.INSTANCE.play( Assets.Sounds.GHOST );
		yell(Messages.get(this, "thank_you"));

		if (prize == null) prize = generatePrize();

		Dungeon.level.drop(prize, pos).sprite.drop();

		destroy();
		sprite.die();
		sprite.tint(1, 1, 1, 1);
		sprite.emitter().start( ShaftParticle.FACTORY, 0.3f, 4 );
		sprite.emitter().start( Speck.factory( Speck.LIGHT ), 0.2f, 3 );

	}

	private Item generatePrize() {
		//50/50 between weapon or armor, always uncursed
		Item prize;
		if (Random.Int(2) == 0){
			prize = Generator.randomWeapon(true);
			if (((MeleeWeapon)prize).hasCurseEnchant()){
				((MeleeWeapon) prize).enchantment = null;
			}
		} else {
			prize = Generator.randomArmor();
			if (((Armor) prize).hasCurseGlyph()){
				((Armor) prize).glyph = null;
			}
		}
		prize.cursed = false;
		prize.setCursedKnown(true);

		return prize;
	}

	private static final String PRIZE = "prize";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PRIZE, prize);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		prize = (Item) bundle.get(PRIZE);
	}

	@Override
	public boolean onDeleteLevelScheme(String name) {
		boolean changedSth = false;
		if (prize != null && prize.onDeleteLevelScheme(name)) {
			if (!(prize instanceof RandomItem)) prize = null;
			changedSth = true;
		}
		return changedSth || super.onDeleteLevelScheme(name);
	}

	@Override
	public boolean onRenameLevelScheme(String oldName, String newName) {
		boolean changedSth = prize != null && prize.onRenameLevelScheme(oldName, newName);
		return super.onRenameLevelScheme(oldName, newName) || changedSth;
	}

	@Override
	public void onMapSizeChange(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		super.onMapSizeChange(newPosition, isPositionValid);
		if (prize != null) prize.onMapSizeChange(newPosition, isPositionValid);
	}
}