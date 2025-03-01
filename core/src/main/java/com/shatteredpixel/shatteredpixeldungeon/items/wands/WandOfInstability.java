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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.HashSet;
import java.util.Set;

public class WandOfInstability extends Wand {

	{
		image = ItemSpriteSheet.WAND_INSTABILITY;
	}

	private static final Set<Class<? extends Wand>> RANDOM_WAND_EFFECTS = new HashSet<>();
	{
		RANDOM_WAND_EFFECTS.add(WandOfBlastWave.class);
		RANDOM_WAND_EFFECTS.add(WandOfCorrosion.class);
		RANDOM_WAND_EFFECTS.add(WandOfCorruption.class);
		RANDOM_WAND_EFFECTS.add(WandOfDisintegration.class);
		RANDOM_WAND_EFFECTS.add(WandOfFireblast.class);
		RANDOM_WAND_EFFECTS.add(WandOfFrost.class);
		RANDOM_WAND_EFFECTS.add(WandOfLightning.class);
		RANDOM_WAND_EFFECTS.add(WandOfLivingEarth.class);
		RANDOM_WAND_EFFECTS.add(WandOfMagicMissile.class);
		RANDOM_WAND_EFFECTS.add(WandOfPrismaticLight.class);
		RANDOM_WAND_EFFECTS.add(WandOfRegrowth.class);
		RANDOM_WAND_EFFECTS.add(WandOfSummoning.class);
		RANDOM_WAND_EFFECTS.add(WandOfTransfusion.class);
		RANDOM_WAND_EFFECTS.add(WandOfWarding.class);
	}

	private Wand curWandEffect;

	@Override
	public int collisionProperties(int target) {
		maybeSetCurWandEffect();
		return curWandEffect.collisionProperties(target);
	}

	@Override
	public boolean tryToZap(Hero owner, int target) {
		return curWandEffect.tryToZap(owner, target);
	}

	@Override
	public void onZap(Ballistica beam) {
		curWandEffect.onZap(beam);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		maybeSetCurWandEffect();
		curWandEffect.onHit(staff, attacker, defender, damage);
		curWandEffect = null;
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curWandEffect.fx(beam, callback);
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		curWandEffect.staffFx(particle);
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		maybeSetCurWandEffect();
		return curWandEffect.targetingPos(user, dst);
	}

	private void maybeSetCurWandEffect() {
		if (curWandEffect == null) {
			curWandEffect = Reflection.newInstance(oneTimeRandomWand());
			curWandEffect.level(level());
			curWandEffect.cursed = cursed;
			curWandEffect.curCharges = curCharges;
		}
	}

	public Class<? extends Wand> oneTimeRandomWand() {
		return Random.element(RANDOM_WAND_EFFECTS);
	}

	@Override
	protected int chargesPerCast() {
		return curWandEffect.chargesPerCast();
	}

	@Override
	public void wandUsed() {
		super.wandUsed();
		curWandEffect = null;
	}
}
