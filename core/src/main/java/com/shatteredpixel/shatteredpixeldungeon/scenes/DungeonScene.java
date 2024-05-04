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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.LevelColoring;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.SideControlPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DiscardedItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.*;
import com.shatteredpixel.shatteredpixeldungeon.ui.Banner;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

import java.util.*;

public abstract class DungeonScene extends PixelScene {

	private static DungeonScene scene;

	protected SkinnedBlock water;
	protected GridTileMap visualGrid;
	protected TerrainFeaturesTilemap terrainFeatures;
	protected BarrierTilemap barriers;
	protected ArrowCellTilemap arrowCells;

	protected Group terrain;
	protected Group customTiles;
	protected Group levelVisuals;
	protected Group levelWallVisuals;
	protected Group customWalls;
	protected Group ripples;
	protected Group heaps;
	protected Group mobs;
	protected Group floorEmitters;
	protected Group emitters;
	protected Group effects;
	protected Group gases;
	protected Group spells;
	protected Group statuses;
	protected Group emoicons;
	protected Group overFogEffects;
	protected Group healthIndicators;


	protected static CellSelector cellSelector;

	protected SideControlPane sideControlPane;

	protected Component prompt;

	protected InventoryPane inventory;


	//sometimes UI changes can be prompted by the actor thread.
	// We queue any removed element destruction, rather than destroying them in the actor thread.
	protected ArrayList<Gizmo> toDestroy = new ArrayList<>();

	@Override
	public void create() {
		super.create();
		scene = this;
	}

	protected void initBasics() {

		terrain = new Group();
		add(terrain);

		water = new SkinnedBlock(
				Dungeon.level.width() * DungeonTilemap.SIZE,
				Dungeon.level.height() * DungeonTilemap.SIZE,
				Dungeon.level.waterTex()) {

			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}

			@Override
			public void draw() {
				//water has no alpha component, this improves performance
				Blending.disable();
				super.draw();
				Blending.enable();
			}
		};
		water.autoAdjust = true;
		terrain.add(water);

		terrain.add( LevelColoring.getWater() );

		ripples = new Group();
		terrain.add( ripples );

		DungeonTileSheet.setupVariance(Dungeon.level.map.length, Dungeon.seedCurLevel());

		initAndAddDungeonTilemap();

		customTiles = new Group();
		terrain.add(customTiles);

		for( CustomTilemap visual : Dungeon.level.customTiles){
			addCustomTile(visual);
		}

		visualGrid = new GridTileMap();
		terrain.add( visualGrid );

		terrainFeatures = new TerrainFeaturesTilemap(Dungeon.level.plants, Dungeon.level.traps);
		terrain.add(terrainFeatures);

		arrowCells = new ArrowCellTilemap(Dungeon.level.arrowCells);
		terrain.add(arrowCells);

		barriers = new BarrierTilemap(Dungeon.level.barriers);
		terrain.add(barriers);

		terrain.add( LevelColoring.getFloor() );
	}

	protected abstract void initAndAddDungeonTilemap();

	@Override
	public void update() {
		lastOffset = null;
		super.update();
	}

	protected static Point lastOffset = null;

	@Override
	public synchronized Gizmo erase (Gizmo g) {
		Gizmo result = super.erase(g);
		if (result instanceof Window){
			lastOffset = ((Window) result).getOffset();
		}
		return result;
	}

	@Override
	public void destroy() {
		if (scene == this) scene = null;
		super.destroy();
	}

	//ensures that mob sprites are drawn from top to bottom, in case of overlap
	public static void sortMobSprites(){
		if (scene != null){
			synchronized (scene) {
				scene.mobs.sort(new Comparator() {
					@Override
					public int compare(Object a, Object b) {
						//elements that aren't visual go to the end of the list
						if (a instanceof Visual && b instanceof Visual) {
							return (int) Math.signum((((Visual) a).y + ((Visual) a).height())
									- (((Visual) b).y + ((Visual) b).height()));
						} else if (a instanceof Visual){
							return -1;
						} else if (b instanceof Visual){
							return 1;
						} else {
							return 0;
						}
					}
				});
			}
		}
	}

	protected synchronized void prompt(String text) {
		prompt(text == null ? null : new Toast(text) {
			@Override
			protected void onClose() {
				cancel();
			}
		});
	}

	public static synchronized void promptStatic(Component newPrompt) {
		if (scene != null) scene.prompt(newPrompt);
	}

	protected synchronized void prompt(Component newPrompt) {
		if (prompt != null && prompt != newPrompt) {
			prompt.killAndErase();
			toDestroy.add(prompt);
			prompt = null;
		}

		if (newPrompt != null) {
			prompt = newPrompt;
			prompt.camera = uiCamera;
			Toast.placeToastOnScreen(prompt);

			add(prompt);
		}
	}

	public static void updateMap() {
		scene.updateMapImpl();
	}

	public static void updateMap(int cell) {
		scene.updateMapImpl(cell);
	}

	protected abstract void updateMapImpl();
	protected abstract void updateMapImpl(int cell);

	public void addCustomTile(CustomTilemap visual) {
		customTiles.add(visual.create());

		if (visual instanceof CustomTileLoader.SimpleCustomTile) {
			((CustomTileLoader.SimpleCustomTile) visual).placed = true;
			int pos = visual.tileX + visual.tileY * Dungeon.level.width();
			Dungeon.level.visualMap[pos] = ((CustomTileLoader.SimpleCustomTile) visual).imageTerrain;
			Dungeon.level.visualRegions[pos] = ((CustomTileLoader.SimpleCustomTile) visual).region;
		}
	}

	public void addCustomWall(CustomTilemap visual) {
		customWalls.add( visual.create() );

		if (visual instanceof CustomTileLoader.SimpleCustomTile) {
			((CustomTileLoader.SimpleCustomTile) visual).placed = true;
			int pos = visual.tileX + visual.tileY * Dungeon.level.width();
			Dungeon.level.visualMap[pos] = ((CustomTileLoader.SimpleCustomTile) visual).imageTerrain;
			Dungeon.level.visualRegions[pos] = ((CustomTileLoader.SimpleCustomTile) visual).region;
		}
	}

	public static void add(CustomTilemap visual) {
		if (scene == null) return;
		if (visual instanceof CustomTileLoader.SimpleCustomTile)
			((CustomTileLoader.SimpleCustomTile) visual).placed = true;
		if (visual.wallVisual) {
			scene.addCustomWall(visual);
		} else {
			scene.addCustomTile(visual);
		}
	}

	public static void remove(CustomTilemap visual) {
		if (scene == null) return;
		scene.removeImpl(visual);
		if (visual.wallVisual) scene.customWalls.remove(visual.killVisual());
		else scene.customTiles.remove(visual.killVisual());
	}

	protected void removeImpl(CustomTilemap visual) {}

	public static void revalidateCustomTiles(){
		if (scene == null) return;

		scene.customTiles.parent.erase(scene.customTiles);
		scene.customTiles.destroy();
		scene.customTiles = new Group();
		scene.terrain.add(scene.customTiles);

		scene.customWalls.parent.erase(scene.customWalls);
		scene.customWalls.destroy();
		scene.customWalls = new Group();
		scene.add(scene.customWalls);
		Set<CustomTilemap> toRemove = new HashSet<>(4);
		for (CustomTilemap visual : Dungeon.level.customTiles) {
			if (visual instanceof CustomTileLoader.SimpleCustomTile) {
				((CustomTileLoader.SimpleCustomTile) visual).updateValues();
				if (((CustomTileLoader.SimpleCustomTile) visual).identifier == null) toRemove.add(visual);
				else add(visual);
			} else add(visual);
		}
		Dungeon.level.customTiles.removeAll(toRemove);
		toRemove.clear();
		for (CustomTilemap visual : Dungeon.level.customWalls) {
			if (visual instanceof CustomTileLoader.SimpleCustomTile) {
				((CustomTileLoader.SimpleCustomTile) visual).updateValues();
				if (((CustomTileLoader.SimpleCustomTile) visual).identifier == null) toRemove.add(visual);
				else add(visual);
			} else add(visual);
		}
		Dungeon.level.customWalls.removeAll(toRemove);
	}

	protected void addHeapSprite(Heap heap) {
		heap.sprite = (ItemSprite) heaps.recycle(ItemSprite.class);
		heap.sprite.revive();
		heap.linkSprite(heap);
		heap.addHeapComponents(heaps);
	}

	protected void addDiscardedSprite(Heap heap) {
		heap.sprite = (DiscardedItemSprite) heaps.recycle(DiscardedItemSprite.class);
		heap.sprite.revive();
		heap.linkSprite(heap);
		heap.addHeapComponents(heaps);
	}

	public static void updateHeapImagesAndSubIcons() {
		if (scene == null) return;
		for (Heap heap : Dungeon.level.heaps.valueList()) {
			if (heap.sprite == null) continue;
			updateHeapImage(heap);
			heap.updateSubicon();
		}
	}

	public static void updateHeapImages() {
		if (scene == null) return;
		for (Heap heap : Dungeon.level.heaps.valueList()) {
			updateHeapImage(heap);
		}
	}

	public static void updateHeapImage(Heap heap) {
		Item i = heap.peek();
		i.image = CustomDungeon.getDungeon().getItemSpriteOnSheet(i);
		heap.sprite.view(heap);
		heap.sprite.place(heap.pos);
	}

	protected void addBlobSprite( final Blob gas ) {
		if (gas.emitter == null) {
			gases.add( new BlobEmitter( gas ) );
		}
	}

	protected void addParticleSprite(final CustomParticle particle) {
		if (particle.emitter == null) {
			gases.add(new CustomParticle.ParticleEmitter(particle));
		}
	}

	protected abstract void addMobSprite(Mob mob);

	public static void replaceMobSprite(Mob mob, Class<? extends CharSprite> newSprite) {
		if (scene != null) {
			CharSprite sprite = mob.sprite;
			sprite.clearAura();
			sprite.killAndErase();
			if (sprite.realCharSprite != null) {
				sprite.realCharSprite.killAndErase();
			}
			mob.spriteClass = newSprite;
			new Thread(() -> {//don't ask me why this works... (2 different buffers apparently would share the same id)
				try {Thread.sleep(50);} catch (InterruptedException ignored) {}
				scene.addMobSprite(mob);
			}).start();

		}
	}

	protected void showBanner( Banner banner ) {
		banner.camera = uiCamera;

		float offset = Camera.main.centerOffset.y;
		banner.x = align( uiCamera, (uiCamera.width - banner.width) / 2 );
		banner.y = align( uiCamera, (uiCamera.height - banner.height) / 2 - banner.height/2 - 16 - offset );

		addToFront( banner );
	}

	// -------------------------------------------------------

	public static void add( Blob gas ) {
		if (!CustomDungeon.isEditing()) Actor.add(gas);
		if (scene != null) {
			scene.addBlobSprite(gas);
		}
	}

	public static void add( CustomParticle particle ) {
		if (scene != null) {
			scene.addParticleSprite(particle);
		}
	}

	public static void add( Heap heap ) {
		if (scene != null) {
			scene.addHeapSprite(heap);
			scene.doAddHeap(heap);
		}
	}

	public static void discard( Heap heap ) {
		if (scene != null) {
			scene.addDiscardedSprite(heap);
		}
	}

	protected void doAddHeap( Heap heap ){}

	public static void add( Mob mob ) {
		Dungeon.level.mobs.add(mob);
		if (!CustomDungeon.isEditing()) Actor.add(mob);
		if (scene != null) {
			scene.addMobSprite(mob);
		}
	}

	public static void add( EmoIcon icon ) {
		scene.emoicons.add( icon );
	}

	public static void effect( Visual effect ) {
		if (scene != null) scene.effects.add( effect );
	}



	public static Ripple ripple(int pos ) {
		if (scene != null) {
			Ripple ripple = (Ripple) scene.ripples.recycle(Ripple.class);
			ripple.reset(pos);
			return ripple;
		} else {
			return null;
		}
	}

	public static Emitter emitter() {

		if (scene != null) {
			Emitter emitter = (Emitter) scene.emitters.recycle(Emitter.class);
			emitter.revive();
			return emitter;
		} else {
			return null;
		}
	}

	public static void updateParticle(int id) {
		CustomParticle particle = Dungeon.level.particles.get(id);
		if (particle != null) {
			if (particle.emitter != null) {
				particle.emitter.destroy();
				particle.emitter.remove();
				particle.emitter.killAndErase();
				particle.emitter = null;
			}
			add(particle);
		}
	}




	public static void show( Window wnd ) {
		if (scene != null) {
			cancel();

			//If a window is already present (or was just present)
			// then inherit the offset it had
			if (scene.inventory != null && scene.inventory.visible){
				Point offsetToInherit = null;
				for (Gizmo g : scene.members){
					if (g instanceof Window) offsetToInherit = ((Window) g).getOffset();
				}
				if (lastOffset != null) {
					offsetToInherit = lastOffset;
				}
				if (offsetToInherit != null) {
					wnd.offset(offsetToInherit);
					wnd.boundOffsetWithMargin(3);
				}
			}
			scene.addToFront(wnd);
		}
		else Game.scene().addToFront(wnd);
	}

	public static boolean showingWindow() {
		if (scene == null) return false;

		for (Gizmo g : scene.members) {
			if (g instanceof Window) return true;
		}

		return false;
	}

	public static boolean interfaceBlockingHero(){
		if (scene == null) return false;

		if (showingWindow()) return true;

		if (scene.inventory != null && scene.inventory.isSelecting()){
			return true;
		}

		return false;
	}

	private final List<Window> temporarilyHiddenWindows = new ArrayList<>(5);

	public static synchronized void hideWindowsTemporarily() {
		if (scene == null) return;

		for (Gizmo g : scene.members.toArray(new Gizmo[0])) {
			if (g instanceof Window) {
				scene.remove(g);
				g.active = false;
				if (g instanceof WndTabbed)
					((WndTabbed) g).setBlockLevelForTabs(PointerArea.NEVER_BLOCK);
				scene.temporarilyHiddenWindows.add((Window) g);
			}
		}
	}

	public static synchronized void reshowWindows() {
		if (scene == null) return;
		for (Window w : scene.temporarilyHiddenWindows) {
			scene.addToFront(w);
			w.active = true;
			if (w instanceof WndTabbed)
				((WndTabbed) w).setBlockLevelForTabs(PointerArea.ALWAYS_BLOCK);
		}
		scene.temporarilyHiddenWindows.clear();
	}


	public static void showEditCellWindow(int cell) {
		if (scene == null) return;

		Level level = Dungeon.level;
		int terrainType = level.map[cell];
		if (TileItem.isTrapTerrainCell(terrainType)) terrainType = Terrain.EMPTY;

		Trap trap = level.traps.get(cell);
		Heap heap = level.heaps.get(cell);
		Mob mob = level.findMob(cell);
		Plant plant = level.plants.get(cell);
		Barrier barrier = level.barriers.get(cell);
		ArrowCell arrowCell = level.arrowCells.get(cell);

		DefaultEditComp.showWindow(terrainType, DungeonTileSheet.getVisualWithAlts(Tiles.getPlainImage(terrainType), cell), heap, mob, trap, plant, barrier, arrowCell, cell);
	}


	public static void handleCell( int cell ) {
		cellSelector.select( cell, PointerEvent.LEFT, false );
	}

	public static void selectCell( CellSelector.Listener listener ) {
		if (scene != null) {
			scene.selectCellImpl(listener);
		}
	}

	protected abstract void selectCellImpl( CellSelector.Listener listener );


	public static boolean cancelCellSelector() {
		if (scene != null) {
			return scene.cancelCellSelectorImpl();
		} return false;
	}

	protected abstract boolean cancelCellSelectorImpl();


	public static boolean cancel() {
		cellSelector.resetKeyHold();
		if (Dungeon.hero != null && (Dungeon.hero.curAction != null || Dungeon.hero.resting)) {

			Dungeon.hero.curAction = null;
			Dungeon.hero.resting = false;
			return true;

		} else {

			return cancelCellSelector();

		}
	}

	public static void ready() {
		if (scene != null) {
			scene.readyImpl();
		}
	}

	protected abstract void readyImpl();

	public static void updatePathfinder() {
		if (Dungeon.level != null && scene != null)
			PathFinder.setMapSize(Dungeon.level.width(), Dungeon.level.height());
	}
}