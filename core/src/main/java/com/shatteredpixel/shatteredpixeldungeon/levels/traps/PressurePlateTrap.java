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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.IntFunction;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;
import java.util.List;

public class PressurePlateTrap extends Trap {

	{
		//ignores color and shape, uses custom image for rendering
		color = shape = 0;
		
		
	}
	
	public int targetCell = -1;
	public int changeTerrain = -1;
	public List<Item> spawnItems = new ArrayList<>(4);
	public List<Mob> spawnMobs = new ArrayList<>(4);
	
	//maybe auch activation Rule (für alle Traps) machen: Projektile, Spieler, Gegner → die Druckplatte hat für „nur Projektile“ eigenes Bild und Beschreibung

	@Override
	public void activate() {
		
		int target = targetCell;
		if (target == -1) target = pos;
		
		boolean wouldChangeRegion = Dungeon.level.visualRegions[target] != LevelScheme.REGION_NONE && Dungeon.level.visualRegions[target] != Dungeon.level.levelScheme.getVisualRegion();
		if (changeTerrain != -1 && changeTerrain != Dungeon.level.map[target] || wouldChangeRegion) {
			Dungeon.level.visualRegions[target] = LevelScheme.REGION_NONE;
			Level.set( target, changeTerrain );
			GameScene.updateMap( target );
			Dungeon.level.addVisualsAtTile(target);
			Dungeon.level.addWallVisualsAtTile(target);
		}
	
		for (Item item : spawnItems) {
			item = item.getCopy();
			if (Barrier.canEnterCell(target, Dungeon.hero, true, false)) {
				Dungeon.level.drop(item, target).sprite.drop(target);
			} else {
				LooseItemsTrap.dropAround(item, target, Dungeon.hero, PathFinder.NEIGHBOURS8);
			}
		}
		
		if (!spawnMobs.isEmpty()) {
			SummoningTrap summoningTrap = new SummoningTrap();
			summoningTrap.pos = target;
			summoningTrap.spawnMobs = spawnMobs;
			summoningTrap.activate();
		}
		
		if (Dungeon.level.heroFOV[target]) {
			CellEmitter.get(target).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
		}
		
		if (!Dungeon.level.heroFOV[pos] && Dungeon.level.heroFOV[target]) {
			Sample.INSTANCE.play(Assets.Sounds.TRAP);
		}
	}
	
	@Override
	public int getImagePosOnSpriteSheet(boolean forceShowIfHidden) {
		//active/inactive, invisible/half-visible/visible
		if (visible) {
			return active ? 175 : 191;
		}
		if (forceShowIfHidden || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing()){
			return active ? 207 : 223;
		}
		return -1;
	}
	
	
	private static final String TARGET_CELL = "target_cell";
	private static final String CHANGE_TERRAIN = "change_terrain";
	private static final String SPAWN_ITEMS = "spawn_items";
	private static final String SPAWN_MOBS = "spawn_mobs";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TARGET_CELL, targetCell);
		bundle.put(CHANGE_TERRAIN, changeTerrain);
		bundle.put(SPAWN_ITEMS, spawnItems);
		bundle.put(SPAWN_MOBS, spawnMobs);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		
		targetCell = bundle.getInt(TARGET_CELL);
		changeTerrain = bundle.getInt(CHANGE_TERRAIN);
		
		spawnItems.clear();
		for (Bundlable b : bundle.getCollection(SPAWN_ITEMS))
			spawnItems.add((Item) b);
		
		spawnMobs.clear();
		for (Bundlable b : bundle.getCollection(SPAWN_MOBS))
			spawnMobs.add((Mob) b);
	}
	
	@Override
	public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
		return super.doOnAllGameObjects(whatToDo)
				| doOnAllGameObjectsList(spawnItems, whatToDo)
				| doOnAllGameObjectsList(spawnMobs, whatToDo);
	}
	
	@Override
	public void onMapSizeChange(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		super.onMapSizeChange(newPosition, isPositionValid);
		if (targetCell != -1) {
			int nPos = newPosition.apply(targetCell);
			targetCell = isPositionValid.test(targetCell, nPos) ? nPos : -1;
		}
	}
}
