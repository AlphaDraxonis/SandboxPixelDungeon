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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

public class ArmoredStatue extends Statue implements ItemSelectables.ArmorSelectable {

	{
		spriteClass = StatueSprite.class;
	}

	protected Armor armor;

	public ArmoredStatue(){
		super();
	}

	@Override
	public void setLevel(int depth) {
		boolean hpSet = this.hpSet;
		super.setLevel(depth);
		HT *= 2;//double HP
		if (!hpSet) HP = HT;
	}

	@Override
	public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
		return super.doOnAllGameObjects(whatToDo) | doOnSingleObject(armor, whatToDo, newValue -> armor = newValue);
	}

	@Override
	public void armor(Armor armor) {
		this.armor = armor;
	}

	//used in some glyph calculations
	@Override
	public Armor armor() {
		return armor;
	}
	
	@Override
	public ItemSelector.NullTypeSelector useNullArmor() {
		return ItemSelector.NullTypeSelector.RANDOM;
	}

	@Override
	public void createItems(boolean useDecks) {
		super.createItems(useDecks);

		if (armor == null) {
			armor = Generator.randomArmor();
			armor.cursed = false;
			armor.inscribe(Armor.Glyph.random());
		}
	}

	private static final String ARMOR	= "armor";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARMOR, armor );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		armor = (Armor)bundle.get( ARMOR );
	}

	@Override
	public int drRoll() {
		if (armor == null) return super.drRoll();
		return super.drRoll() + Random.NormalIntRange( armor.DRMin(), armor.DRMax());
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (armor != null) damage = armor.proc(enemy, this, damage);
		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public float speed() {
		float speed = super.speed();
		if (armor != null) {
			speed = armor.speedFactor(this, speed);
		}
		return speed;
	}
	
	@Override
	public int glyphLevel(Class<? extends Armor.Glyph> cls) {
		if (armor != null && armor.hasGlyph(cls, this)){
			return Math.max(super.glyphLevel(cls), armor.buffedLvl());
		} else {
			return super.glyphLevel(cls);
		}
	}

	@Override
	public CharSprite createSprite() {
		CharSprite sprite = super.createSprite();
		StatueSprite.setArmor(sprite, armor == null ? 0 : armor.tier);
		return sprite;
	}

	@Override
	public int defenseSkill(Char enemy) {
		if (armor == null) return super.defenseSkill(enemy);
		return Math.round(armor.evasionFactor(this, super.defenseSkill(enemy)));
	}

	@Override
	public void die( Object cause ) {
		if (armor != null) {
			armor.identify(false);
			Dungeon.level.drop(armor, pos).sprite.drop();
		}
		super.die( cause );
	}

	@Override
	public String desc() {
		if (customDesc != null || armor == null && Dungeon.hero != null) return super.desc();
		String desc = Messages.get(this, "desc");
		if (weapon != null && armor != null || CustomDungeon.isEditing()){
			desc += "\n\n" + Messages.get(this, "desc_arm_wep", weapon == null ? "___" : weapon().name(), armor == null ? "___" : armor().name());
		}
		return desc;
	}

}
