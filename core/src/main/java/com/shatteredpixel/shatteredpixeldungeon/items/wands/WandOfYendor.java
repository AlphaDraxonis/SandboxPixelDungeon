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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WandOfYendor extends Wand {

	{
		image = ItemSpriteSheet.WAND_YENDOR;

		unique = true;

		collisionProperties = Ballistica.WONT_STOP;//highest collision rule of all wands
		//like this:
//		for (Wand w : wands) {
//			collisionProperties &= w.collisionProperties;
//		}
	}

	private static final Set<Class<? extends Wand>> WAND_EFFECTS = new HashSet<>();
	{
		WAND_EFFECTS.add(WandOfBlastWave.class);
		WAND_EFFECTS.add(WandOfCorrosion.class);
		WAND_EFFECTS.add(WandOfCorruption.class);
		WAND_EFFECTS.add(WandOfDisintegration.class);
		WAND_EFFECTS.add(WandOfFireblast.class);
		WAND_EFFECTS.add(WandOfFrost.class);
		WAND_EFFECTS.add(WandOfLightning.class);
		WAND_EFFECTS.add(WandOfLivingEarth.class);
		WAND_EFFECTS.add(WandOfMagicMissile.class);
		WAND_EFFECTS.add(WandOfPrismaticLight.class);
		WAND_EFFECTS.add(WandOfRegrowth.class);
		WAND_EFFECTS.add(WandOfSummoning.class);
		WAND_EFFECTS.add(WandOfTransfusion.class);
		WAND_EFFECTS.add(WandOfWarding.class);
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (hero.buff(AscensionChallenge.class) != null){
			actions.clear();
		} else {
			actions.add(Amulet.AC_END);
		}
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(Amulet.AC_END)) {
			Amulet.showAmuletScene(true, WandOfYendor.this);
		}
	}

	@Override
	public String actionName(String action, Hero hero) {
		if (action.equals(Amulet.AC_END)) {
			return Messages.get(Amulet.class, "ac_" + action);
		}
		return super.actionName(action, hero);
	}

	@Override
	public boolean tryToZap(Hero owner, int target) {
		if (super.tryToZap(owner, target)) {
			this.target = target;
			return true;
		}
		return false;
	}

	@Override
	public void onZap(Ballistica beam) {
		//shouldn't be called
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		//actually totally useless...
		Set<Class<? extends Wand>> wandAttacksLeft = new HashSet<>(WAND_EFFECTS);
		while (!wandAttacksLeft.isEmpty()) {

			Class<? extends Wand> wandClass = Random.element(wandAttacksLeft);
			Wand w = Reflection.newInstance(wandClass);
			wandAttacksLeft.remove(wandClass);
			w.level(level());
			w.curCharges = 1;

			w.onHit(staff, attacker, defender, damage);

		}

	}

	private int target;

	@Override
	public void fx(Ballistica beam, Callback callback) {

		if (cursed) {
			super.performZap(target, curUser);
			return;
		}

		new WildMagic() {
			{
				ArrayList<Wand> wands = new ArrayList<>();
				WandOfWarding wandOfWarding = null;
				for (Class<? extends Wand> wandEffect : WAND_EFFECTS) {
					Wand w = Reflection.newInstance(wandEffect);
					w.level(level());
					w.curCharges = 1;

					final Ballistica shot = new Ballistica( curUser.pos, target, w.collisionProperties(target), null);
					if (shot.collisionPos == curUser.pos) {
						continue;//don't self-target
					}

					/*if (!(w instanceof WandOfWarding))*/ wands.add(w);
//					else wandOfWarding = (WandOfWarding) w;
				}
				Random.shuffle(wands);
				if (wandOfWarding != null) wands.add(wandOfWarding);

				activateCopy(curUser, target, wands);
			}
		};

		wandUsed();
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		{
			inputs =  new Class[]{Amulet.class, KingsCrown.class, TengusMask.class};
			inQuantity = new int[]{1, 1, 1};

			cost = 10;

			output = WandOfYendor.class;
			outQuantity = 1;
		}

	}

}
