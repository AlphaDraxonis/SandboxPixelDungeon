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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Dewdrop extends Item {
	
	{
		image = ItemSpriteSheet.DEWDROP;
		
		stackable = true;
		dropsDownHeap = true;
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		
		Waterskin flask = hero.belongings.getItem( Waterskin.class );
		
		if (flask != null && !flask.isFull()){

			if (quantity() > flask.volumeRemaining()){
				Dewdrop collect = new Dewdrop();
				collect.quantity = flask.volumeRemaining();
				quantity = quantity() - collect.quantity();
				flask.collectDew(collect);

				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				hero.spendAndNext( TIME_TO_PICK_UP );

				return false;
			} else {
				flask.collectDew( this );
				GameScene.pickUp( this, pos );
			}


		} else {

			int terr = Dungeon.level.map[pos];
			boolean force = TileItem.isExitTerrainCell(terr) || TileItem.isEntranceTerrainCell(terr);;
			int[] lastResult = new int[1];
			int totalHealing = 0, totalShield = 0;
			while (quantity > 0 && (lastResult = consumeDew(1, hero, force))[0] > 0){
				quantity--;
				totalHealing += lastResult[1];
				totalShield += lastResult[2];
			}
			if (totalHealing > 0 || totalShield > 0 || lastResult[0] == -1) {

				if (totalHealing > 0){
					hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(totalHealing), FloatingText.HEALING);
				}

				if (totalShield > 0) {
					Buff.affect(hero, Barrier.class).incShield(totalShield);
					hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(totalShield), FloatingText.SHIELDING );
				}

				Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
				hero.spendAndNext( TIME_TO_PICK_UP );
			}
			return quantity == 0;

		}
		
		Sample.INSTANCE.play( Assets.Sounds.DEWDROP );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		return true;
	}

	public static int[] consumeDew(int quantity, Hero hero, boolean force){
		//20 drops for a full heal
		int heal = Math.round( hero.HT * 0.05f * quantity );

		int effect = Math.min( hero.HT - hero.HP, heal );
		int shield = 0;
		if (hero.hasTalent(Talent.SHIELDING_DEW)){
			shield = heal - effect;
			int maxShield = Math.round(hero.HT *0.2f*hero.pointsInTalent(Talent.SHIELDING_DEW));
			int curShield = 0;
			if (hero.buff(Barrier.class) != null) curShield = hero.buff(Barrier.class).shielding();
			shield = Math.min(shield, maxShield-curShield);
		}
		if (effect > 0 || shield > 0) {
			hero.HP += effect;
			if (effect > 0 && shield > 0){
				return new int[]{3, effect, shield};
			} else if (effect > 0){
				return new int[]{2, effect, shield};
			} else {
				return new int[]{1, effect, shield};
			}

		} else if (!force) {
			GLog.i( Messages.get(Dewdrop.class, "already_full") );
			return new int[]{0, effect, shield};
		}

		return new int[]{-1, effect, shield};
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	//max of one dew in a stack

//	@Override
//	public Item merge( Item other ){
//		if (CustomDungeon.isEditing()) return super.merge(other);
//		if (isSimilar( other )){
//			quantity = 1;
//			other.quantity = 0;
//		}
//		return this;
//	}
//
//	@Override
//	public Item quantity(int value) {
//		if (CustomDungeon.isEditing()) return super.quantity(value);
//		quantity = Math.min( value, 1);
//		return this;
//	}

}