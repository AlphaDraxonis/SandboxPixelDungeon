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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.watabou.noosa.audio.Sample;

public class Pasty extends Food {

	{
//		reset();
		image = ItemSpriteSheet.PASTY;

		energy = Hunger.STARVING;

		bones = true;
	}

	Holiday holiday = Holiday.NONE;

	@Override
	public void reset() {
		super.reset();
		switch(holiday){
			case NONE: default:
				image = ItemSpriteSheet.PASTY;
				break;
			case LUNAR_NEW_YEAR:
				image = ItemSpriteSheet.STEAMED_FISH;
				break;
			case APRIL_FOOLS:
				image = ItemSpriteSheet.CHOC_AMULET;
				break;
			case EASTER:
				image = ItemSpriteSheet.EASTER_EGG;
				break;
			case HALLOWEEN:
				image = ItemSpriteSheet.PUMPKIN_PIE;
				break;
			case WINTER_HOLIDAYS:
				image = ItemSpriteSheet.CANDY_CANE;
				break;
		}
	}
	
	@Override
	protected void satisfy(Hero hero) {
		if (holiday == Holiday.LUNAR_NEW_YEAR){
			//main item only clears 300 hunger on lunar new year...
			energy = Hunger.HUNGRY;
		}

		super.satisfy(hero);
		
		switch(holiday){
			default:
				break; //do nothing extra
			case LUNAR_NEW_YEAR:
				//...but it also awards an extra item that restores 150 hunger
				FishLeftover left = new FishLeftover();
				if (!left.collect()){
					Dungeon.level.drop(left, hero.pos).sprite.drop();
				}
				break;
			case APRIL_FOOLS:
				Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			case EASTER:
				ArtifactRecharge.chargeArtifacts(hero, 2f);
				ScrollOfRecharging.charge( hero );
				break;
			case HALLOWEEN:
				//heals for 5% max hp, min of 3
				int toHeal = Math.max(3, hero.HT/20);
				hero.HP = Math.min(hero.HP + toHeal, hero.HT);
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING );
				break;
			case WINTER_HOLIDAYS:
				hero.belongings.charge(0.5f); //2 turns worth
				ScrollOfRecharging.charge( hero );
				break;
		}
	}

	@Override
	public String name() {
		if (customName != null) return super.name();
		switch(holiday){
			case NONE: default:
				return super.name();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_name");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_name");
			case EASTER:
				return Messages.get(this, "egg_name");
			case HALLOWEEN:
				return Messages.get(this, "pie_name");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_name");
		}
	}

	@Override
	public String info() {
		if (customDesc != null) return super.info();
		switch(holiday){
			case NONE: default:
				return super.info();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_desc");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_desc");
			case EASTER:
				return Messages.get(this, "egg_desc");
			case HALLOWEEN:
				return Messages.get(this, "pie_desc");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_desc");
		}
	}
	
	@Override
	public int value() {
		return 20 * quantity;
	}

	public static class FishLeftover extends Food {

		{
			image = ItemSpriteSheet.FISH_LEFTOVER;
			energy = Hunger.HUNGRY/2;
		}

		@Override
		public int value() {
			return 10 * quantity;
		}
	}

	public static class PastyLunar extends Pasty {
		{
			holiday = Holiday.LUNAR_NEW_YEAR;
			reset();
		}
	}
	public static class PastyAprilFools extends Pasty {
		{
			holiday = Holiday.APRIL_FOOLS;
			reset();
		}
	}
	public static class PastyEaster extends Pasty {
		{
			holiday = Holiday.EASTER;
			reset();
		}
	}
	public static class PastyHWeen extends Pasty {
		{
			holiday = Holiday.HALLOWEEN;
			reset();
		}
	}
	public static class PastyXMas extends Pasty {
		{
			holiday = Holiday.WINTER_HOLIDAYS;
			reset();
		}
	}
}