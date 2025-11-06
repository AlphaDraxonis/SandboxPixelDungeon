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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class WndChooseSubclass extends AbstractWndChooseSubclass {

	private TitleClassComp titleClassComp;
	
	public WndChooseSubclass(final TengusMask tome, final Hero hero ) {

		super(createTitlebar(tome),
				Messages.get(WndChooseSubclass.class, "message"),
				Messages.get(WndChooseSubclass.class, "cancel"),
				hero.heroClass, tome);
		
		titleClassComp.random = new IconButton(Icons.SHUFFLE.get()){
			@Override
			protected void onClick() {
				super.onClick();
				GameScene.show(new WndOptions(Icons.SHUFFLE.get(),
						Messages.get(WndChooseSubclass.class, "random_title"),
						Messages.get(WndChooseSubclass.class, "random_sure"),
						Messages.get(WndChooseSubclass.class, "yes"),
						Messages.get(WndChooseSubclass.class, "no")){
					@Override
					protected void onSelect(int index) {
						super.onSelect(index);
						if (index == 0){
							WndChooseSubclass.this.hide();
							HeroSubClass cls = Random.oneOf(hero.heroClass.subClasses());
							tome.choose(cls);
							GameScene.show(new WndInfoSubclass(hero.heroClass, cls));
						}
					}
				});
			}
			
			@Override
			protected String hoverText() {
				return Messages.get(WndChooseSubclass.class, "random_title");
			}
		};

	}

	private static TitleClassComp createTitlebar(TengusMask tome){
		TitleClassComp result = new TitleClassComp();
		
		result.title = new IconTitle();
		result.title.icon( new ItemSprite( tome.image(), null ) );
		result.title.label( tome.name() );
		
		return result;
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
							Statistics.qualifiedForRandomVictoryBadge = false;
						}
					}
				});
			}
		};
	}

	private static class TitleClassComp extends Component {
		
		private IconTitle title;
		private IconButton random;
		
		@Override
		protected void layout() {
			if (random == null) {
				title.setRect(0, 0, width, 0);
			}
			else {
				title.setRect(0, 0, width-16, 0);
				random.setRect(width-16, 0, 16, 16);
			}
			height = title.height();
		}
	}
}
