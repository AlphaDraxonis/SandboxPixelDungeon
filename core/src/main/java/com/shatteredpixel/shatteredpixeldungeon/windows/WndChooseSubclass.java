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

package com.shatteredpixel.shatteredpixeldungeon.windows;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.ui.Component;

public class WndChooseSubclass extends AbstractWndChooseSubclass {

	public WndChooseSubclass(final TengusMask tome, final Hero hero ) {

		super(createTitlebar(tome),
				Messages.get(WndChooseSubclass.class, "message"),
				Messages.get(WndChooseSubclass.class, "cancel"),
				hero.heroClass, tome);

	}

	private static Component createTitlebar(TengusMask tome){
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( tome.image(), null ) );
		titlebar.label( tome.name() );
		return titlebar;
	}

	@Override
	protected StyledButton createHeroSubClassButton(final TengusMask tome, HeroSubClass subCls) {
		if (!Dungeon.customDungeon.heroSubClassesEnabled[subCls.getIndex()]) return null;
		return new RedButton( subCls.shortDesc(), 6 ) {
			@Override
			protected void onClick() {
				GameScene.show(new WndOptions(new HeroIcon(subCls),
						Messages.titleCase(subCls.title()),
						Messages.get(WndChooseSubclass.this, "are_you_sure"),
						Messages.get(WndChooseSubclass.this, "yes"),
						Messages.get(WndChooseSubclass.this, "no")){
					@Override
					protected void onSelect(int index) {
						hide();
						if (index == 0 && WndChooseSubclass.this.parent != null){
							WndChooseSubclass.this.hide();
							tome.choose( subCls );
						}
					}
				});
			}
		};
	}

}
