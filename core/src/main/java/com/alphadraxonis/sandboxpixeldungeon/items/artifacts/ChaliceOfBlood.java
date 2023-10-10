/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.items.artifacts;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Badges;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MagicImmune;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.effects.particles.ShadowParticle;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.WandOfLivingEarth;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Earthroot;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ChaliceOfBlood extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_CHALICE1;

		levelCap = 10;
	}

	public static final String AC_PRICK = "PRICK";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero )
				&& level() < levelCap
				&& !cursed
				&& !hero.isInvulnerable(getClass())
				&& hero.buff(MagicImmune.class) == null)
			actions.add(AC_PRICK);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_PRICK)){

			int damage = 5 + 3*(level()*level());

			if (damage > hero.HP*0.75) {

				GameScene.show(
					new WndOptions(new ItemSprite(this),
							Messages.titleCase(name()),
							Messages.get(this, "prick_warn"),
							Messages.get(this, "yes"),
							Messages.get(this, "no")) {
						@Override
						protected void onSelect(int index) {
							if (index == 0)
								prick(Dungeon.hero);
						}
					}
				);

			} else {
				prick(hero);
			}
		}
	}

	private void prick(Hero hero){
		int damage = 5 + 3*(level()*level());

		Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
		if (armor != null) {
			damage = armor.absorb(damage);
		}

		WandOfLivingEarth.RockArmor rockArmor = hero.buff(WandOfLivingEarth.RockArmor.class);
		if (rockArmor != null) {
			damage = rockArmor.absorb(damage);
		}

		damage -= hero.drRoll();

		hero.sprite.operate( hero.pos );
		hero.busy();
		hero.spend(3f);
		GLog.w( Messages.get(this, "onprick") );
		if (damage <= 0){
			damage = 1;
		} else {
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
			hero.sprite.emitter().burst( ShadowParticle.CURSE, 4+(damage/10) );
		}

		hero.damage(damage, this);

		if (!hero.isAlive()) {
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		} else {
			upgrade();
		}
	}

	@Override
	public Item upgrade() {
		updateSprite(level());
		return super.upgrade();
	}

	@Override
	public void level(int value) {
		updateSprite(value - 1);
		super.level(value);
	}

	private void updateSprite(int level){
		if (level >= 6)
			image = ItemSpriteSheet.ARTIFACT_CHALICE3;
		else if (level >= 2)
			image = ItemSpriteSheet.ARTIFACT_CHALICE2;
		else
			image = ItemSpriteSheet.ARTIFACT_CHALICE1;
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (level() >= 7) image = ItemSpriteSheet.ARTIFACT_CHALICE3;
		else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_CHALICE2;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chaliceRegen();
	}
	
	@Override
	public void charge(Hero target, float amount) {
		if (cursed || target.buff(MagicImmune.class) != null) return;

		//grants 5 turns of healing up-front, if hero isn't starving
		if (target.isStarving()) return;

		float healDelay = 10f - (1.33f + level()*0.667f);
		healDelay /= amount;
		float heal = 5f/healDelay;
		//effectively 0.5/1/1.5/2/2.5 HP per turn at +0/+6/+8/+9/+10
		if (Random.Float() < heal%1){
			heal++;
		}
		if (heal >= 1f) {
			target.HP = Math.min(target.HT, target.HP + (int)heal);
		}
	}
	
	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped (Dungeon.hero)){
			desc += "\n\n";
			if (cursed)
				desc += Messages.get(this, "desc_cursed");
			else if (level() == 0)
				desc += Messages.get(this, "desc_1");
			else if (level() < levelCap)
				desc += Messages.get(this, "desc_2");
			else
				desc += Messages.get(this, "desc_3");
		}

		return desc;
	}

	public class chaliceRegen extends ArtifactBuff {
		//see Regeneration.class for effect
	}

}