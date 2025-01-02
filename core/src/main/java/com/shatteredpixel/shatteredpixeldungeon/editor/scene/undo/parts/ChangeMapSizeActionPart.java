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

package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.CoinDoor;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.utils.IntFunction;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.SparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ChangeMapSizeActionPart implements ActionPart {
	
	private final Level prevLevel;
	private Level curLevel;
	private final LevelScheme levelScheme;
	
	private final List<Integer> oldEntranceCells, oldExitCells;
	private final Map<String, TransitionChangesForOtherLevels> otherLevelTransitions;
	
	private final int startTop, startBottom, startLeft, startRight;
	private final int top, bottom, left, right;
	private final int nW, nH;
	
	public ChangeMapSizeActionPart(Level level,
						 int startTop, int startBottom, int startLeft, int startRight,
						 int top, int bottom, int left, int right) {
		
		this.curLevel = level;
		this.levelScheme = level.levelScheme;
		
		oldEntranceCells = new ArrayList<>(levelScheme.entranceCells);
		oldExitCells = new ArrayList<>(levelScheme.exitCells);
		
		//will be filled when changing the map size
		otherLevelTransitions = new HashMap<>();
		
		this.startTop = startTop;
		this.startBottom = startBottom;
		this.startLeft = startLeft;
		this.startRight = startRight;
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		
		nW = left + right + 1;
		nH = top + bottom + 1;
		
		if (hasContent()) {
			this.prevLevel = level.getCopy();
			redo();
		} else {
			this.prevLevel = null;
		}
	}
	
	@Override
	public void undo() {
		setActiveLevel(prevLevel);
		
		levelScheme.entranceCells.clear();
		levelScheme.entranceCells.addAll(oldEntranceCells);
		
		levelScheme.exitCells.clear();
		levelScheme.exitCells.addAll(oldExitCells);
		
		for (Map.Entry<String, TransitionChangesForOtherLevels> entry : otherLevelTransitions.entrySet()) {
			LevelScheme ls = Dungeon.customDungeon.getFloor(entry.getKey());
			entry.getValue().restoreOriginalTransitions(ls);
		}
	}
	
	@Override
	public void redo() {
		boolean workingWithOriginalInstance = curLevel != null;
		if (!workingWithOriginalInstance) {
			//first redo (actually doing the action) uses the original instance
			curLevel = prevLevel.getCopy();
			levelScheme.setLevel(curLevel);
		}
		
		otherLevelTransitions.clear();
		
		//Check destCells
		for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
			
			if (ls.getType() == CustomLevel.class) {
				boolean load = ls.getLevel() == null;
				Level l;
				if (load) l = ls.loadLevel(false);
				else l = ls.getLevel();
				if (l == null) continue;//skip if level couldn't be loaded
				
				Map<String, LevelTransition> zoneTransitions = new HashMap<>();
				for (Zone zone : l.zoneMap.values()) {
					if (zone.zoneTransition != null) {
						zoneTransitions.put(zone.getName(), zone.zoneTransition.getCopy());
					}
				}
				
				Collection<LevelTransition> levelTransitions = new HashSet<>();
				for (LevelTransition trans : l.transitions.values()) {
					levelTransitions.add(trans.getCopy());
				}
				
				otherLevelTransitions.put(ls.getName(), new TransitionChangesForOtherCustomLevels(levelTransitions, zoneTransitions));
				
				if (load) ls.unloadLevel();
			} else {
				otherLevelTransitions.put(ls.getName(), new TransitionChangesForOtherRegularLevels(ls.getEntranceTransitionRegular(), ls.getExitTransitionRegular()));
			}
		}
		
		changeMapSize(curLevel, nW, nH, top - startTop, left - startLeft);
		
		if (!workingWithOriginalInstance) {
			EditorScene.openDifferentLevel = false;
			levelScheme.setLevel(curLevel);
			EditorScene.open((CustomLevel) curLevel);
		}
		
		curLevel = null;
	}
	
	@Override
	public boolean hasContent() {
		return top != startTop || bottom != startBottom || left != startLeft || right != startRight;
	}
	
	private void setActiveLevel(Level level) {
		levelScheme.setLevel(level);
		PathFinder.setMapSize(level.width(), level.height());
		
		EditorScene.openDifferentLevel = false;
		EditorScene.open((CustomLevel) level);
	}
	
	
	private static abstract class TransitionChangesForOtherLevels {
		public abstract void restoreOriginalTransitions(LevelScheme levelScheme);
	}
	
	private static final class TransitionChangesForOtherCustomLevels extends TransitionChangesForOtherLevels {
		private final Collection<LevelTransition> levelTransitions;
		private final Map<String, LevelTransition> zoneTransitions;
		
		private TransitionChangesForOtherCustomLevels(Collection<LevelTransition> levelTransitions, Map<String, LevelTransition> zoneTransitions) {
			this.levelTransitions = levelTransitions;
			this.zoneTransitions = zoneTransitions;
		}
		
		@Override
		public void restoreOriginalTransitions(LevelScheme levelScheme) {
			boolean load = levelScheme.getLevel() == null;
			Level level;
			if (load) level = levelScheme.loadLevel(false);
			else level = levelScheme.getLevel();
			if (level == null) {
				//skip if level couldn't be loaded
				return;
			}
			
			level.transitions.clear();
			for (LevelTransition trans : levelTransitions) {
				level.transitions.put(trans.departCell, trans);
			}
			
			for (String zoneName : zoneTransitions.keySet()) {
				level.zoneMap.get(zoneName).zoneTransition = zoneTransitions.get(zoneName);
			}
			
			try {
				CustomDungeonSaves.saveLevel(level);
			} catch (IOException e) {
				//If saving is not successful, this will only result in some disappeared transitions, nothing to worry about.
			}
			
			if (load) levelScheme.unloadLevel();
		}
	}
	
	private static final class TransitionChangesForOtherRegularLevels extends TransitionChangesForOtherLevels {
		
		private final LevelTransition entrance, exit;//tzz can these be changed without causing undo changes??!!
		
		private TransitionChangesForOtherRegularLevels(LevelTransition entrance, LevelTransition exit) {
			this.entrance = entrance;
			this.exit = exit;
		}
		
		@Override
		public void restoreOriginalTransitions(LevelScheme levelScheme) {
			levelScheme.setEntranceTransitionRegular(entrance);
			levelScheme.setExitTransitionRegular(exit);
		}
	}
	
	
	
	public static void changeMapSize(Level level, int newWidth, int newHeight, int addTop, int addLeft) {
		if (level.width() == newWidth && level.height() == newHeight) return;
		
		if (newHeight != level.height()) changeMapHeight(level, newHeight, addTop);
		if (newWidth != level.width()) changeMapWidth(level, newWidth, addLeft);
		
		if (level instanceof CustomLevel) {
			if (((CustomLevel) level).bossGroundVisuals != null) {
				((CustomLevel) level).bossGroundVisuals.tileX = 0;
				((CustomLevel) level).bossGroundVisuals.tileY = 0;
				((CustomLevel) level).bossGroundVisuals.tileW = newWidth;
				((CustomLevel) level).bossGroundVisuals.tileH = newHeight;
			}
			if (((CustomLevel) level).bossWallsVisuals != null) {
				((CustomLevel) level).bossWallsVisuals.tileX = 0;
				((CustomLevel) level).bossWallsVisuals.tileY = 0;
				((CustomLevel) level).bossWallsVisuals.tileW = newWidth;
				((CustomLevel) level).bossWallsVisuals.tileH = newHeight;
			}
		}
		
		PathFinder.setMapSize(newWidth, newHeight);
		
		level.buildFlagMaps();
		Zone.setupZoneArray(level);
		
		System.arraycopy(Dungeon.level.map, 0, Dungeon.level.visualMap, 0, Dungeon.level.map.length);
		Arrays.fill(Dungeon.level.visualRegions, LevelScheme.REGION_NONE);
		for (CustomTilemap vis : Dungeon.level.customTiles) {
			if (vis instanceof CustomTileLoader.SimpleCustomTile) {
				int cell = vis.tileX + vis.tileY * newWidth;
				Dungeon.level.visualMap[cell] = ((CustomTileLoader.SimpleCustomTile) vis).imageTerrain;
				Dungeon.level.visualRegions[cell] = ((CustomTileLoader.SimpleCustomTile) vis).region;
			}
		}
		
		EditorScene.resetCameraPos();
	}
	
	private static void changeMapHeight(Level level, int newHeight, int addTop) {
		
		int width = level.width();
		int newLength = width * newHeight;
		int add = addTop * width;
		
		int[] oldMap = level.map;
		byte[] oldTileVariance = level.tileVariance;
		
		int levelWidth = level.width();
		level.setSize(width, newHeight);
		
		boolean[] nDiscoverable = new boolean[newLength];
		
		changeArrayForMapSizeHeight(oldMap, level.map, add, levelWidth, width);
		changeArrayForMapSizeHeight(level.discoverable, nDiscoverable, add, levelWidth, width);
		
		if (oldTileVariance != null)
			changeArrayForMapSizeHeight(oldTileVariance, level.tileVariance, add, levelWidth, width);
		
		level.discoverable = nDiscoverable;
		
		Set<CustomTilemap> removeCustomTiles = new HashSet<>(5);
		for (CustomTilemap customTile : level.customTiles) {
			customTile.tileY += addTop;
			if (customTile.tileY < 0 || customTile.tileY + customTile.tileH > newHeight) {
				if (!(customTile instanceof CustomTilemap.BossLevelVisuals)) removeCustomTiles.add(customTile);
			}
		}
		level.customTiles.removeAll(removeCustomTiles);
		removeCustomTiles.clear();
		for (CustomTilemap customTile : level.customWalls) {
			customTile.tileY += addTop;
			if (customTile.tileY < 0 || customTile.tileY + customTile.tileH > newHeight) {
				if (!(customTile instanceof CustomTilemap.BossLevelVisuals)) removeCustomTiles.add(customTile);
			}
		}
		level.customWalls.removeAll(removeCustomTiles);
		
		IntFunction<Integer> newPosition = old -> old + add;
		BiPredicate<Integer, Integer> isPositionValid = (old, neu) -> neu >= 0 && neu < newLength && level.insideMap(neu);
		
		recalculateNewPositions(newPosition, isPositionValid, level, newLength);
	}
	
	private static void changeMapWidth(Level level, int newWidth, int addLeft) {
		
		int diffW = newWidth - level.width();
		int height = level.height();
		int newLength = height * newWidth;
		
		int[] oldMap = level.map;
		byte[] oldTileVariance = level.tileVariance;
		
		int levelWidth = level.width();
		level.setSize(newWidth, height);
		
		boolean[] nDiscoverable = new boolean[newLength];
		
		changeArrayForMapSizeWidth(oldMap, level.map, addLeft, levelWidth, newWidth);
		changeArrayForMapSizeWidth(level.discoverable, nDiscoverable, addLeft, levelWidth, newWidth);
		
		if (oldTileVariance != null)
			changeArrayForMapSizeWidth(oldTileVariance, level.tileVariance, addLeft, levelWidth, newWidth);
		
		level.discoverable = nDiscoverable;
		
		Set<CustomTilemap> removeCustomTiles = new HashSet<>(5);
		for (CustomTilemap customTile : level.customTiles) {
			customTile.tileX += addLeft;
			if (customTile.tileX < 0 || customTile.tileX + customTile.tileW > newWidth){
				if (!(customTile instanceof CustomTilemap.BossLevelVisuals)) removeCustomTiles.add(customTile);
			}
		}
		level.customTiles.removeAll(removeCustomTiles);
		removeCustomTiles.clear();
		for (CustomTilemap customTile : level.customWalls) {
			customTile.tileX += addLeft;
			if (customTile.tileX < 0 || customTile.tileX + customTile.tileW > newWidth){
				if (!(customTile instanceof CustomTilemap.BossLevelVisuals)) removeCustomTiles.add(customTile);
			}
		}
		level.customWalls.removeAll(removeCustomTiles);
		
		IntFunction<Integer> newPosition = old -> old + addLeft + diffW * (old / levelWidth);
		BiPredicate<Integer, Integer> isPositionValid = (old, neu) -> neu >= 0 && neu < newLength && level.insideMap(neu)
				&& old / levelWidth == neu / newWidth;
		
		recalculateNewPositions(newPosition, isPositionValid, level, newLength);
	}
	
	private static void recalculateNewPositions(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid, Level level, int newLength) {
		
		List<Mob> removeEntities = new ArrayList<>();
		for (Mob m : level.mobs) {
			int nPos = newPosition.apply(m.pos);
			if (!isPositionValid.test(m.pos, nPos)) removeEntities.add(m);
			else {
				m.pos = nPos;
				m.onMapSizeChange(newPosition, isPositionValid);
			}
		}
		level.mobs.removeAll(removeEntities);
		removeEntities.clear();
		
		if (level.bossmobAt != -1) {
			int old = level.bossmobAt;
			level.bossmobAt = newPosition.apply(old);
			if (!isPositionValid.test(old, level.bossmobAt)) level.bossmobAt = Level.NO_BOSS_MOB;
		}
		
		//Cant avoid some copy paste because Shattered has really good code
		SparseArray<Heap> nHeaps = new SparseArray<>();
		for (Heap h : level.heaps.valueList()) {
			int nPos = newPosition.apply(h.pos);
			if (isPositionValid.test(h.pos, nPos)) {
				nHeaps.put(nPos, h);
				h.pos = nPos;
				for (Item i : h.items){
					i.onMapSizeChange(newPosition, isPositionValid);
				}
				
			}
		}
		level.heaps.clear();
		level.heaps.putAll(nHeaps);
		
		SparseArray<Trap> nTrap = new SparseArray<>();
		for (Trap t : level.traps.valueList()) {
			int nPos = newPosition.apply(t.pos);
			if (isPositionValid.test(t.pos, nPos)) {
				nTrap.put(nPos, t);
				t.pos = nPos;
				t.onMapSizeChange(newPosition, isPositionValid);
			}
		}
		level.traps.clear();
		level.traps.putAll(nTrap);
		
		SparseArray<Sign> nSign = new SparseArray<>();
		for (Sign s : level.signs.valueList()) {
			int nPos = newPosition.apply(s.pos);
			if (isPositionValid.test(s.pos, nPos)) {
				nSign.put(nPos, s);
				s.pos = nPos;
			}
		}
		level.signs.clear();
		level.signs.putAll(nSign);
		
		SparseArray<Barrier> nBarriers = new SparseArray<>();
		for (Barrier b : level.barriers.valueList()) {
			int nPos = newPosition.apply(b.pos);
			if (isPositionValid.test(b.pos, nPos)) {
				nBarriers.put(nPos, b);
				b.pos = nPos;
			}
		}
		level.barriers.clear();
		level.barriers.putAll(nBarriers);
		
		SparseArray<ArrowCell> nArrowCells = new SparseArray<>();
		for (ArrowCell ac : level.arrowCells.valueList()) {
			int nPos = newPosition.apply(ac.pos);
			if (isPositionValid.test(ac.pos, nPos)) {
				nArrowCells.put(nPos, ac);
				ac.pos = nPos;
			}
		}
		level.arrowCells.clear();
		level.arrowCells.putAll(nArrowCells);
		
		SparseArray<Checkpoint> nCheckpoint = new SparseArray<>();
		for (Checkpoint cp : level.checkpoints.valueList()) {
			int nPos = newPosition.apply(cp.pos);
			if (isPositionValid.test(cp.pos, nPos)) {
				nCheckpoint.put(nPos, cp);
				cp.pos = nPos;
			}
		}
		level.checkpoints.clear();
		level.checkpoints.putAll(nCheckpoint);
		
		SparseArray<CoinDoor> nCoinDoors = new SparseArray<>();
		for (CoinDoor c : level.coinDoors.valueList()) {
			int nPos = newPosition.apply(c.pos);
			if (isPositionValid.test(c.pos, nPos)) {
				nCoinDoors.put(nPos, c);
				c.pos = nPos;
			}
		}
		level.coinDoors.clear();
		level.coinDoors.putAll(nCoinDoors);
		
		SparseArray<Plant> nPlant = new SparseArray<>();
		for (Plant p : level.plants.valueList()) {
			if (p != null) {
				int nPos = newPosition.apply(p.pos);
				if (isPositionValid.test(p.pos, nPos)) {
					nPlant.put(nPos, p);
					p.pos = nPos;
					p.onMapSizeChange(newPosition, isPositionValid);
				}
			}
		}
		level.plants.clear();
		level.plants.putAll(nPlant);
		
		Zone.changeMapSize(level, newPosition, isPositionValid);
		
		recalculateBlobList(level.blobs.values(), newPosition, isPositionValid, newLength);
		recalculateBlobList(level.particles.values(), newPosition, isPositionValid, newLength);
		
		List<Integer> cells = new ArrayList<>(level.levelScheme.entranceCells);
		level.levelScheme.entranceCells.clear();
		for (int cell : cells) {
			int pos = newPosition.apply(cell);
			if (isPositionValid.test(cell, pos)) level.levelScheme.entranceCells.add(pos);
		}
		Collections.sort(level.levelScheme.entranceCells);
		
		cells = new ArrayList<>(level.levelScheme.exitCells);
		level.levelScheme.exitCells.clear();
		for (int cell : cells) {
			int pos = newPosition.apply(cell);
			if (isPositionValid.test(cell, pos)) level.levelScheme.exitCells.add(pos);
		}
		Collections.sort(level.levelScheme.exitCells);
		
		//Check depart cells
		Map<Integer, LevelTransition> nTrans = new HashMap<>();
		for (LevelTransition transition : level.transitions.values()) {
			LevelTransition t = checkLevelTransitionsDepartCell(transition, level, newPosition, isPositionValid);
			if (t != null) nTrans.put(t.departCell, t);
		}
		level.transitions.clear();
		level.transitions = nTrans;
		
		for (Zone zone : level.zoneMap.values()) {
			if (zone.zoneTransition != null) {
				if (checkLevelTransitionsDepartCell(zone.zoneTransition, level, newPosition, isPositionValid) == null)
					zone.zoneTransition = null;
			}
		}
		
		//Check destCells
		for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
			
			if (ls.getType() == CustomLevel.class) {
				boolean load = ls.getLevel() == null;
				Level l;
				if (load) l = ls.loadLevel(false);
				else l = ls.getLevel();
				if (l == null) continue;//skip if level couldn't be loaded
				boolean changedSth = false;
				for (LevelTransition transition : new HashSet<>(l.transitions.values())) {
					Boolean feedback = checkLevelTransitionsDestCell(transition, level, newPosition, isPositionValid);
					if (feedback == null) {
						feedback = true;
						l.transitions.remove(transition.departCell);
					}
					if (feedback) changedSth = true;
				}
				for (Zone zone : level.zoneMap.values()) {
					if (zone.zoneTransition != null) {
						Boolean feedback = checkLevelTransitionsDestCell(zone.zoneTransition, level, newPosition, isPositionValid);
						if (feedback == null) {
							feedback = true;
							zone.zoneTransition = null;
						}
						if (feedback) changedSth = true;
					}
				}
				
				if (changedSth) {
					try {
						CustomDungeonSaves.saveLevel(l);
					} catch (IOException e) {
						//If saving is not successful, this will only result in some disappeared transitions, nothing to worry about.
					}
				}
				if (load) ls.unloadLevel();
			} else {
				checkRegularLevelTransitions(ls.getEntranceTransitionRegular(), level.name, newPosition, isPositionValid);
				checkRegularLevelTransitions(ls.getExitTransitionRegular(), level.name, newPosition, isPositionValid);
			}
		}
	}
	
	private static LevelTransition checkLevelTransitionsDepartCell(LevelTransition transition, Level level,
																   IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		int posDepart = newPosition.apply(transition.departCell);
		int posCenter = newPosition.apply(transition.centerCell);
		//TODO consider the size of the transitions but atm they cant be set!
//            int left = newPosition.apply(transition.left);
//            int top = newPosition.apply(transition.top);
//            int right = newPosition.apply(transition.right);
//            int bottom = newPosition.apply(transition.bottom);
		if (isPositionValid.test(transition.departCell, posDepart)
				&& isPositionValid.test(transition.centerCell, posCenter)
//                    && isPositionValid.test(transition.left, left)
//                    && isPositionValid.test(transition.right, right)
//                    && isPositionValid.test(transition.top, top)
//                    && isPositionValid.test(transition.bottom, bottom)
		) {
			transition.departCell = posDepart;
			transition.centerCell = posCenter;
			Point p = level.cellToPoint(transition.departCell);
			transition.set(p.x, p.y, p.x, p.y);
//                transition.set(left, top, right, bottom);
			return transition;
		}
		return null;
	}
	
	private static Boolean checkLevelTransitionsDestCell(LevelTransition transition, Level level,
														 IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		if (transition != null && Objects.equals(transition.destLevel, level.name) && transition.destCell >= 0) {
			int dest = newPosition.apply(transition.destCell);
			if (isPositionValid.test(transition.destCell, dest)) {
				if (dest != transition.destCell) {
					transition.destCell = dest;
					return true;
				}
			} else {
				return null;
			}
		}
		return false;
	}
	
	private static boolean checkRegularLevelTransitions(LevelTransition transition, String levelWithChanges,
													 IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		if (transition != null && Objects.equals(transition.destLevel, levelWithChanges)) {
			int dest = newPosition.apply(transition.destCell);
			if (isPositionValid.test(transition.destCell, dest)) {
				if (dest != transition.destCell) {
					transition.destCell = dest;
					return true;
				}
			} else {
				transition.destCell = -1;
				transition.destLevel = null;
				return true;
			}
		}
		return false;
	}
	
	private static <T extends Blob> void recalculateBlobList(Iterable<T> blobs, IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid, int newLength) {
		for (T b : blobs) {
			if (b != null) {
				int[] nCur = new int[newLength];
				b.volume = 0;
				if (b.cur != null) {
					for (int i = 0; i < b.cur.length; i++) {
						int newIndex = newPosition.apply(i);
						if (isPositionValid.test(i, newIndex)) {
							b.volume += b.cur[i];
							nCur[newIndex] = b.cur[i];
						}
					}
				}
				b.cur = nCur;
				b.changeSizeOfOffToNewMapSizeAndClearIt(newLength);
				b.setupArea();
				
				if (b instanceof SacrificialFire) {
					Map<Integer, Item> prizes = ((SacrificialFire) b).getPrizes();
					Map<Integer, Item> newPrizePositions = new HashMap<>(3);
					for (Item i : prizes.values()){
						if (i != null) i.onMapSizeChange(newPosition, isPositionValid);
					}
					for (Integer oldPos : prizes.keySet()) {
						int nPos = newPosition.apply(oldPos);
						if (isPositionValid.test(oldPos, nPos)) {
							newPrizePositions.put(nPos, prizes.get(oldPos));
						}
					}
					((SacrificialFire) b).setPrizes(newPrizePositions);
				}
			}
		}
	}
	
	//add must be multiplied with width before!!
	public static void changeArrayForMapSizeHeight(int[] src, int[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + i / levelWidth * diffW;
			if (index >= 0 && index < dest.length) dest[index] = src[i];
		}
	}
	
	public static void changeArrayForMapSizeWidth(int[] src, int[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + diffW * (i / levelWidth);
			if (index >= 0 && index < dest.length && i / levelWidth == index / newWidth)
				dest[index] = src[i];
		}
	}
	
	private static void changeArrayForMapSizeHeight(boolean[] src, boolean[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + i / levelWidth * diffW;
			if (index >= 0 && index < dest.length) dest[index] = src[i];
		}
	}
	
	private static void changeArrayForMapSizeWidth(boolean[] src, boolean[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + diffW * (i / levelWidth);
			if (index >= 0 && index < dest.length && i / levelWidth == index / newWidth)
				dest[index] = src[i];
		}
	}
	
	private static void changeArrayForMapSizeHeight(byte[] src, byte[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + i / levelWidth * diffW;
			if (index >= 0 && index < dest.length) dest[index] = src[i];
		}
	}
	
	private static void changeArrayForMapSizeWidth(byte[] src, byte[] dest, int add, int levelWidth, int newWidth) {
		int diffW = newWidth - levelWidth;
		for (int i = 0; i < src.length; i++) {
			int index = i + add + diffW * (i / levelWidth);
			if (index >= 0 && index < dest.length && i / levelWidth == index / newWidth)
				dest[index] = src[i];
		}
	}
	
}