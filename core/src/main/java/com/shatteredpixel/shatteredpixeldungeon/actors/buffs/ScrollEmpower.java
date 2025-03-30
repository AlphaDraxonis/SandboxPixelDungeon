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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class ScrollEmpower extends BuffWithDuration {

	{
		type = buffType.POSITIVE;
	}

	public void reset(int left){
		this.left = left;
		Item.updateQuickslot();
	}

	public void use(){;
		if (!permanent && (left -= TICK) <= 0){
			detach();
		}
	}

	@Override
	public void detach() {
		super.detach();
		Item.updateQuickslot();
	}

	@Override
	public int icon() {
		return BuffIndicator.WAND;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(0.84f, 0.79f, 0.65f); //scroll colors
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (3f - left) / 3f);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", 2, left) + appendDescForPermanent();
	}
}
