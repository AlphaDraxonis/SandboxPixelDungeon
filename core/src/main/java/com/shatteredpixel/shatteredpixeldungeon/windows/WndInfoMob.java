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

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeMobCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ui.Component;

public class WndInfoMob extends WndTitledMessage {
	
	public WndInfoMob( Mob mob ) {
		this(mob, Mimic.isLikeMob(mob));
	}
	
	public WndInfoMob(Mob mob, boolean includeHealthBar) {
		super(new MobTitle(mob, includeHealthBar), text(mob));
	}
	
	public static String text(Mob mob) {
		return mob.info() + (!mob.dialogs.isEmpty() ? "\n\n_" + Messages.get(ChangeMobCustomizable.class, "dialog_label") + ":_\n" + mob.dialogs.get(mob.nextDialog) : "");
	}
	
	public static class MobTitle extends Component {

		private static final int GAP	= 2;
		
		public CharSprite image;
		private RenderedTextBlock name;
		private HealthBar health;
		private BuffIndicator buffs;
		
		public MobTitle( Mob mob, boolean includeHealthBar ) {
			
			name = PixelScene.renderTextBlock( createTitle(mob) + EditorUtilities.appendBoss(mob), 9 );
			name.hardlight( TITLE_COLOR );
			add( name );
			
			image = mob.createSprite();
			add( image );
			
			if (includeHealthBar) {
				health = new HealthBar();
				health.level(mob);
				add( health );
			}

			buffs = new BuffIndicator( mob, false );
			buffs.visible = CustomDungeon.knowsEverything() || Mimic.isLikeMob(mob);
			add( buffs );
		}
		
		protected BuffIndicator createBuffIndicator( Mob mob, boolean large ){
			return new BuffIndicator(mob,large);
		}
		
		public String createTitle( Mob mob ){
			return Messages.titleCase(mob.name());
		}
		
		public void updateImageNoLayout( Mob mob ) {
			if (image != null) {
				image.remove();
				image.destroy();
			}
			image = mob.createSprite();
			add(image);
		}
		
		@Override
		protected void layout() {
			
			boolean hasHealth = health != null;
			float heightHealth = hasHealth ? health.height() : 0;
			
			image.x = 0;
			image.y = Math.max( 0, name.height() + heightHealth - image.height() );

			float w = width - image.width() - GAP;

			name.setPos(x + image.width() + GAP,
					image.height() > name.height() ? y +(image.height() - name.height()) / 2 : y);
			
			if (hasHealth) {
				health.setRect(image.width() + GAP, name.bottom() + GAP, w, health.height());
			}

			buffs.maxBuffs = 50; //infinite, effectively
			buffs.setRect(name.right(), hasHealth ? name.bottom() - BuffIndicator.SIZE_SMALL - 2 : name.bottom() - BuffIndicator.SIZE_SMALL - 1, w - name.width(), 8);

			//If buff bar doesn't have enough room, move it below
			if (!buffs.allBuffsVisible()){
				buffs.setRect(0, hasHealth ? health.bottom() : name.bottom(), width, 8);
				height = Math.max(image.y + image.height(), buffs.bottom());
			} else {
				height = hasHealth ? Math.max(image.y + image.height(), health.bottom()) : name.bottom() + 2 * GAP;
			}
		}

        public void setText(String text) {
            name.text(text);
        }
	}
}
