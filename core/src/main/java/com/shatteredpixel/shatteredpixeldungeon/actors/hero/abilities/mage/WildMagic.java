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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WildMagic extends ArmorAbility {

	{
		baseChargeUse = 25f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {//if something is changed here, also look down!
		if (target == null){//if something is changed here, also look down!
			return;//if something is changed here, also look down!
		}

		if (target == hero.pos){//if something is changed here, also look down!
			GLog.w(Messages.get(this, "self_target"));//if something is changed here, also look down!
			return;
		}

		ArrayList<Wand> wands = hero.belongings.getAllItems(Wand.class);//if something is changed here, also look down!
		Random.shuffle(wands);//if something is changed here, also look down!

		float chargeUsePerShot = 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));

		for (Wand w : wands.toArray(new Wand[0])){
			if (w.curCharges < 1 && w.partialCharge < chargeUsePerShot){
				wands.remove(w);
			}
		}

		int maxWands = 4 + hero.pointsInTalent(Talent.FIRE_EVERYTHING);

		//second and third shots
		if (wands.size() < maxWands){
			ArrayList<Wand> seconds = new ArrayList<>(wands);
			ArrayList<Wand> thirds = new ArrayList<>(wands);

			for (Wand w : wands){
				float totalCharge = w.curCharges + w.partialCharge;
				if (totalCharge < 2*chargeUsePerShot){
					seconds.remove(w);
				}
				if (totalCharge < 3*chargeUsePerShot
					|| Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING)){
					thirds.remove(w);
				}
			}

			Random.shuffle(seconds);
			while (!seconds.isEmpty() && wands.size() < maxWands){
				wands.add(seconds.remove(0));
			}

			Random.shuffle(thirds);
			while (!thirds.isEmpty() && wands.size() < maxWands){
				wands.add(thirds.remove(0));
			}
		}

		if (wands.size() == 0){//if something is changed here, also look down!
			GLog.w(Messages.get(this, "no_wands"));//if something is changed here, also look down!
			return;
		}

		hero.busy();//if something is changed here, also look down!

		Random.shuffle(wands);//if something is changed here, also look down!

		Buff.affect(hero, WildMagicTracker.class, 0f);//if something is changed here, also look down!

		armor.charge -= chargeUse(hero);//if something is changed here, also look down!
		armor.updateQuickslot();

		zapWand(wands, hero, target);//if something is changed here, also look down!

	}

	protected void activateCopy(Hero hero, Integer target, ArrayList<Wand> wands) {

		new Item() {
			{
				GameScene.cancel();
				Item.curUser = hero;
				Item.curItem = this;
			}
		};

		if (target == null){
			return;
		}

		if (target == hero.pos){
			GLog.w(Messages.get(this, "self_target"));
			return;
		}

//		wands = wands;
//		Random.shuffle(wands); // we don' shuffle here as WandOfWarding should be last

//		float chargeUsePerShot = 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
//
//		for (Wand w : wands.toArray(new Wand[0])){
//			if (w.curCharges < 1 && w.partialCharge < chargeUsePerShot){
//				wands.remove(w);
//			}
//		}

//		int maxWands = 4 + hero.pointsInTalent(Talent.FIRE_EVERYTHING);
//
//		//second and third shots
//		if (wands.size() < maxWands){
//			ArrayList<Wand> seconds = new ArrayList<>(wands);
//			ArrayList<Wand> thirds = new ArrayList<>(wands);
//
//			for (Wand w : wands){
//				float totalCharge = w.curCharges + w.partialCharge;
//				if (totalCharge < 2*chargeUsePerShot){
//					seconds.remove(w);
//				}
//				if (totalCharge < 3*chargeUsePerShot
//						|| Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING)){
//					thirds.remove(w);
//				}
//			}
//
//			Random.shuffle(seconds);
//			while (!seconds.isEmpty() && wands.size() < maxWands){
//				wands.add(seconds.remove(0));
//			}
//
//			Random.shuffle(thirds);
//			while (!thirds.isEmpty() && wands.size() < maxWands){
//				wands.add(thirds.remove(0));
//			}
//		}

		if (wands.size() == 0){
			GLog.w(Messages.get(this, "no_wands"));
			return;
		}

		hero.busy();

//		Random.shuffle(wands); // we don' shuffle here as WandOfWarding should be last

		Buff.affect(hero, WildMagicTracker.class, 0f);

//		armor.charge -= chargeUse(hero);
//		armor.updateQuickslot();

		zapWand(wands, hero, target);

	}

	public static class WildMagicTracker extends FlavourBuff {};

	Actor wildMagicActor = null;

	private void zapWand( ArrayList<Wand> wands, Hero hero, int cell){
		Wand cur = wands.remove(0);

		Ballistica aim = new Ballistica(hero.pos, cell, cur.collisionProperties(cell), null);

		hero.sprite.zap(cell);

		float startTime = Game.timeTotal;
		if (cur.tryToZap(hero, cell)) {
			if (!cur.cursed) {
				cur.fx(aim, new Callback() {
					@Override
					public void call() {
						cur.onZap(aim);
						boolean alsoCursedZap = Random.Float() < WondrousResin.extraCurseEffectChance(hero);
						if (Game.timeTotal - startTime < 0.33f) {
							hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
								@Override
								protected void onComplete() {
									if (alsoCursedZap){
										CursedWand.cursedZap(cur,
												hero,
												new Ballistica(hero.pos, cell, Ballistica.REAL_MAGIC_BOLT, null),
												new Callback() {
													@Override
													public void call() {
														afterZap(cur, wands, hero, cell);
													}
												});
									} else {
										afterZap(cur, wands, hero, cell);
									}
								}
							});
						} else {
							if (alsoCursedZap){
								CursedWand.cursedZap(cur,
										hero,
										new Ballistica(hero.pos, cell, Ballistica.REAL_MAGIC_BOLT, null),
										new Callback() {
											@Override
											public void call() {
												afterZap(cur, wands, hero, cell);
											}
										});
							} else {
								afterZap(cur, wands, hero, cell);
							}
						}
					}
				});

			} else {
				CursedWand.cursedZap(cur,
						hero,
						new Ballistica(hero.pos, cell, Ballistica.REAL_MAGIC_BOLT, null),
						new Callback() {
							@Override
							public void call() {
								if (Game.timeTotal - startTime < 0.33f) {
									hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)) {
										@Override
										protected void onComplete() {
											afterZap(cur, wands, hero, cell);
										}
									});
								} else {
									afterZap(cur, wands, hero, cell);
								}
							}
						});
			}
		} else {
			afterZap(cur, wands, hero, cell);
		}
	}

	private void afterZap( Wand cur, ArrayList<Wand> wands, Hero hero, int target){
		cur.partialCharge -= 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
		if (cur.partialCharge < 0) {
			cur.partialCharge++;
			cur.curCharges--;
		}
		if (wildMagicActor != null){
			wildMagicActor.next();
			wildMagicActor = null;
		}

		Char ch = Actor.findChar(target);
		if (!wands.isEmpty() && hero.isAlive()) {
			Actor.add(new Actor() {
				{
					actPriority = VFX_PRIO-1;
				}

				@Override
				protected boolean act() {
					wildMagicActor = this;
					zapWand(wands, hero, ch == null ? target : ch.pos);
					Actor.remove(this);
					return false;
				}
			});
			hero.next();
		} else {
			if (hero.buff(WildMagicTracker.class) != null) {
				hero.buff(WildMagicTracker.class).detach();
			}
			Item.updateQuickslot();
			Invisibility.dispel();
			if (Random.Int(4) >= hero.pointsInTalent(Talent.CONSERVED_MAGIC)) {
				hero.spendAndNext(Actor.TICK);
			} else {
				hero.next();
			}
		}
	}

	@Override
	public int icon() {
		return HeroIcon.WILD_MAGIC;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.WILD_POWER, Talent.FIRE_EVERYTHING, Talent.CONSERVED_MAGIC, Talent.HEROIC_ENERGY};
	}
}