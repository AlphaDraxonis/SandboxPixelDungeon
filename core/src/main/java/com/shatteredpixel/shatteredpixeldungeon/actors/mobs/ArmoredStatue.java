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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.ItemSelectables;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;

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
		return super.drRoll() + Char.combatRoll( armor.DRMin(), armor.DRMax());
	}

	@Override
	public boolean isImmune(Class effect) {
		if (effect == Burning.class
				&& armor != null
				&& armor.hasGlyph(Brimstone.class, this)){
			return true;
		}
		return super.isImmune(effect);
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (armor != null) damage = armor.proc(enemy, this, damage);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int dmg, Object src) {
		//TODO improve this when I have proper damage source logic
		if (armor != null && armor.hasGlyph(AntiMagic.class, this)
				&& AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(this, armor.buffedLvl());
			dmg = Math.max(dmg, 0);
		}

		super.damage( dmg, src );

		//for the rose status indicator
		Item.updateQuickslot();
	}

	@Override
	public CharSprite sprite() {
		CharSprite sprite = super.sprite();
		((StatueSprite)sprite).setArmor(armor == null ? 0 : armor.tier);
		return sprite;
	}

	@Override
	public float speed() {
		if (armor == null) return super.speed();
		return armor.speedFactor(this, super.speed());
	}

	@Override
	public float stealth() {
		if (armor == null) return super.stealth();
		return armor.stealthFactor(this, super.stealth());
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
	public String description() {
		if (customDesc != null || armor == null && Dungeon.hero != null) return super.description();
		return Messages.get(this, "desc", weapon == null ? "___" : weapon().name(), armor == null ? "___" : armor().name());
	}

}