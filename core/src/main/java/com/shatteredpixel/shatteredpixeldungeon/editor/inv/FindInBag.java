/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class FindInBag implements Bundlable {

	private Type type;
	private Object value;
	private final Object source;//not bundled!

	public FindInBag() {
		source = null;
	}

	public FindInBag(Type type, Object value, Object source) {
		this.type = type;
		this.value = value;
		this.source = source;
	}

	public FindInBag(Object source) {
		this.source = source;

		if (source instanceof LuaClass) {
			type = Type.CUSTOM_OBJECT;
			value = ((LuaClass) source).getIdentifier();
		}

		else if (source instanceof Integer) {
			type = Type.TILE;
			value = source;
		}

		else if (source instanceof CustomParticle) {
			type = Type.PARTICLE;
			value = ((CustomParticle) source).particleID;
		}

		else if (source instanceof CustomParticle.ParticleProperty) {
			type = Type.PARTICLE;
			value = ((CustomParticle.ParticleProperty) source).particleID();
		}

		else if (source  instanceof CustomTileLoader.UserCustomTile) {
			type = Type.CUSTOM_TILE;
			value = ((CustomTileLoader.UserCustomTile) source).identifier;
		}

		else if (source  == EditorItem.REMOVER_ITEM) {
			type = Type.REMOVER;
			value = null;
		}

		else if (source  instanceof Class) {
			type = Type.CLASS;
			value = source;
		}

		else if (source  instanceof FindInBag) {
			type = ((FindInBag) source).getType();
			value = ((FindInBag) source).getValue();
		}

		else {
			type = Type.CLASS;
			value = source.getClass();
		}
	}

	public Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public Object getSource() {
		return source;
	}

	public <T> EditorItem<?> getAsInBag() {
		switch (type) {
			case TILE:
			case CUSTOM_TILE:
			case PARTICLE:
				return (EditorItem<?>) Tiles.bag.findItem(this);
			case CUSTOM_OBJECT:
				return (EditorItem<?>) Mobs.bag.findItem(this);
			case REMOVER: return EditorItem.REMOVER_ITEM;
			case CLASS:
				Class<?> clazz = (Class<?>) value;
				EditorItem<T> inBag;
				if (Item.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Items.bag.findItem(this);
				else if (Mob.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Mobs.bag.findItem(this);
				else if (Trap.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Traps.bag.findItem(this);
				else if (Plant.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Plants.bag.findItem(this);
				else if (Blob.class.isAssignableFrom(clazz)) {
					BlobItem realInBag = (BlobItem) Tiles.bag.findItem(this);//Blobs
					realInBag.setObject((Class<? extends Blob>) clazz);
					return realInBag;
				}
				else if (Barrier.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Tiles.bag.findItem(this);
				else if (ArrowCell.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Tiles.bag.findItem(this);
				else if (CustomTilemap.class.isAssignableFrom(clazz)) inBag = (EditorItem<T>) Tiles.bag.findItem(this);
				else return null;
				if (inBag != null && source != null) inBag.setObject((T) source);
				return inBag;
		}
		return null;
	}

	public static Object getObjAtCell(int cell) {
		Level level = Dungeon.level;
		Object obj;
		if ((obj = level.findMob(cell)) != null) return obj;
		Heap heap = level.heaps.get(cell);
		if (heap != null && (obj = heap.peek()) != null) return obj;
		if ((obj = level.plants.get(cell)) != null) return obj;
		if ((obj = level.traps.get(cell)) != null) return obj;
		if ((obj = level.arrowCells.get(cell)) != null) return obj;
		if ((obj = level.barriers.get(cell)) != null) return obj;

		for (int i = 0; i < BlobActionPart.BlobData.BLOB_CLASSES.length; i++) {
			Blob b = Dungeon.level.blobs.getOnly(BlobActionPart.BlobData.BLOB_CLASSES[i]);
			if (b != null && !(b instanceof WellWater) && b.cur != null && b.cur[cell] > 0) return b;
		}

		for (CustomParticle particle : Dungeon.level.particles.values()) {
			if (particle != null && particle.cur != null && particle.cur[cell] > 0) return new FindInBag(Type.PARTICLE, particle.particleID, particle);
		}

		CustomTilemap customTile = CustomTileItem.findCustomTileAt(cell, false);
		if (customTile == null) customTile = CustomTileItem.findCustomTileAt(cell, true);
		if (customTile != null) return customTile;

		return level.map[cell];
	}

	private static final String TYPE = "type";
	private static final String VALUE = "value";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		type = bundle.getEnum(TYPE, Type.class);
		value = type.restoreValueFromBundle(bundle);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(TYPE, type);
		type.storeValueInBundle(bundle, value);
	}

	public enum Type {

		CLASS,
		TILE,
		CUSTOM_TILE,
		PARTICLE,
		CUSTOM_OBJECT,
		REMOVER;

		private void storeValueInBundle(Bundle bundle, Object value) {
			switch (this) {
				case CLASS: bundle.put(VALUE, (Class<?>) value);
					break;
				case TILE:
				case PARTICLE:
				case CUSTOM_OBJECT: bundle.put(VALUE, (int) value);
					break;
				case CUSTOM_TILE: bundle.put(VALUE, (String) value);
					break;
			}
		}

		private Object restoreValueFromBundle(Bundle bundle) {
			switch (this) {
				case CLASS: return bundle.getClass(VALUE);
				case TILE:
				case PARTICLE:
				case CUSTOM_OBJECT: return bundle.getInt(VALUE);
				case CUSTOM_TILE: return bundle.getString(VALUE);
			}
			return null;
		}

	}
}