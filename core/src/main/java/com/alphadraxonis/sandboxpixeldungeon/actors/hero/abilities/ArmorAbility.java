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

package com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities;

import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Talent;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.ClassArmor;
import com.alphadraxonis.sandboxpixeldungeon.mechanics.Ballistica;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.HeroIcon;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class ArmorAbility implements Bundlable {

	protected float baseChargeUse = 35;

	public void use( ClassArmor armor, Hero hero ){
		if (targetingPrompt() == null){
			activate(armor, hero, hero.pos);
		} else {
			GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					activate(armor, hero, cell);
				}

				@Override
				public String prompt() {
					return targetingPrompt();
				}
			});
		}
	}

	//leave null for no targeting
	public String targetingPrompt(){
		return null;
	}

	public boolean useTargeting(){
		return targetingPrompt() != null;
	}

	public int targetedPos( Char user, int dst ){
		return new Ballistica( user.pos, dst, Ballistica.PROJECTILE ).collisionPos;
	}

	public float chargeUse( Hero hero ){
		float chargeUse = baseChargeUse;
		if (hero.hasTalent(Talent.HEROIC_ENERGY)){
			//reduced charge use by 12%/23%/32%/40%
			chargeUse *= Math.pow( 0.8804, hero.pointsInTalent(Talent.HEROIC_ENERGY));
		}
		return chargeUse;
	}

	protected abstract void activate( ClassArmor armor, Hero hero, Integer target );

	public String name(){
		return Messages.get(this, "name");
	}

	public String shortDesc(){
		return Messages.get(this, "short_desc");
	}

	public String desc(){
		return Messages.get(this, "desc") + "\n\n" + Messages.get(this, "cost", (int)baseChargeUse);
	}

	public int icon(){
		return HeroIcon.NONE;
	}

	public abstract Talent[] talents();

	@Override
	public void storeInBundle(Bundle bundle) {
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
	}
}