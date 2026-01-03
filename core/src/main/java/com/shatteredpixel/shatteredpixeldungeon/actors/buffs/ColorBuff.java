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

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ColorBuff extends FlavourBuff {
	
	{
		type = buffType.NEUTRAL;
	}
	
	public int color = Window.TITLE_COLOR;
	
	@Override
	public void fx(boolean on) {
		if (on && !alwaysHidesFx) target.sprite.applyColor(color);
		else target.sprite.remove( CharSprite.State.COLORED );
	}
	
	
	public static Image createIcon() {
		Image icon = Icons.COLORS.get(); // 13*11 pixels
		icon.scale.set(1.23f); // 16 ÷ 13 = 1.23
		return icon;
	}
	
	
	private static final String COLOR = "color";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COLOR, color);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		color = bundle.getInt(COLOR);
	}
}