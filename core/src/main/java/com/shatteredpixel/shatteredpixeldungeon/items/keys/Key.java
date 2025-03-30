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

package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.IntFunction;

import java.util.Objects;

public abstract class Key extends Item {
	
	public enum Type {
		IRON(IronKey.class),
		GOLD(GoldenKey.class),
		CRYSTAL(CrystalKey.class),
		SKELETON(SkeletonKey.class);
		
		private final Class<? extends Key> asKeyClass;
		
		Type(Class<? extends Key> asKeyClass) {
			this.asKeyClass = asKeyClass;
		}
		
		public Class<? extends Key> asKeyClass() {
			return asKeyClass;
		}
	}

	public static final float TIME_TO_UNLOCK = 1f;
	
	{
		stackable = true;
		unique = true;
	}

	protected Type type;
	public String levelName;
	public int cell;
	
	public Type type() {
		return type;
	}
	
	@Override
	public boolean isSimilar( Item item ) {
		return super.isSimilar(item) && ((Key)item).levelName.equals(levelName) && cell == ((Key) item).cell;
	}

	@Override
	public String name() {
		return super.name() + (cell == -1 ? "" : " (" + Messages.get(this, "cell_name", EditorUtilities.cellToStringNoBrackets(cell, Dungeon.level.width()))+")");
	}

	@Override
	public String desc() {
		return super.desc() + (cell == -1 ? "" : " \n\n" + Messages.get(this, "cell_desc", EditorUtilities.cellToStringNoBrackets(cell, Dungeon.level.width())));
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		instantPickupKey(pos);
		hero.spendAndNext( TIME_TO_PICK_UP );
		Sample.INSTANCE.play( Assets.Sounds.ITEM );
		return true;
	}

	@Override
	public boolean collect(Bag bag) {
		instantPickupKey(Dungeon.hero.pos);
		return true;
	}

	public void instantPickupKey(int pos) {
		Catalog.setSeen(getClass());
		Statistics.itemTypesDiscovered.add(getClass());
		GameScene.pickUpJournal(this, pos);
		WndJournal.last_index = 0;
		Notes.add(this);
		GameScene.updateKeyDisplay();
	}

	private static final String LEVEL_NAME = "levelName";
	private static final String CELL = "cell";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(LEVEL_NAME, levelName );
		bundle.put(CELL, cell+1 );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		levelName = bundle.getString(LEVEL_NAME);
		cell = bundle.getInt(CELL) - 1;
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public ModifyResult onDeleteLevelScheme(String name) {
		return levelName.equals(name) ? ModifyResult.removeFully() : super.onDeleteLevelScheme(name);
	}

	@Override
	public ModifyResult onRenameLevelScheme(String oldName, String newName) {
		return overrideResult(super.onRenameLevelScheme(oldName, newName), key -> {
			if (((Key) key).levelName.equals(oldName)) {
				((Key) key).levelName = newName;
				return true;
			}
			return false;
		});
	}

	@Override
	public void onMapSizeChange(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		if (cell != -1 && Objects.equals(Dungeon.levelName, levelName)) {
			int nCell = newPosition.apply(cell);
			cell = isPositionValid.test(cell, nCell) ? nCell : -1;
		}
		super.onMapSizeChange(newPosition, isPositionValid);
	}

}
