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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.NotAllowedInLua;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

import java.util.HashSet;
import java.util.Set;

@NotAllowedInLua
public class CellSelector extends ScrollArea {

	public Listener listener = null;
	
	public boolean enabled;
	
	private float dragThreshold;


	private boolean isPointerDown;
	private boolean dragClicking, wasJustDragClicking;
	private float time;
	private final Set<Integer> lastSelectedCells = new HashSet<>();
	private int lastCell = -1;
	private PointF lastPoint;
	
	public CellSelector( DungeonTilemap map ) {
		super( map );
		camera = map.camera();
		
		dragThreshold = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2;
		
		mouseZoom = camera.zoom;
		KeyEvent.addKeyListener( keyListener );
	}
	
	private float mouseZoom;
	
	@Override
	protected void onScroll( ScrollEvent event ) {
		float diff = event.amount/10f;
		
		//scale zoom difference so zooming is consistent
		diff /= ((camera.zoom+1)/camera.zoom)-1;
		diff = Math.min(1, diff);
		mouseZoom = GameMath.gate( PixelScene.minZoom, mouseZoom - diff, PixelScene.maxZoom );
		
		zoom( Math.round(mouseZoom) );
	}
	
	@Override
	protected void onClick( PointerEvent event ) {
		if (dragging) {
			dragging = false;
			wasJustDragClicking = false;
		} else if (wasJustDragClicking) {
			wasJustDragClicking = false;
		} else handleClick(event);
	}

	protected void handleClick(PointerEvent event) {
		PointF p = Camera.main.screenToCamera( (int) event.current.x, (int) event.current.y );

		//Prioritizes a sprite if it and a tile overlap, so long as that sprite isn't more than 4 pixels into another tile.
		//The extra check prevents large sprites from blocking the player from clicking adjacent tiles

		//hero first
		if (Dungeon.hero.sprite != null && Dungeon.hero.sprite.overlapsPoint( p.x, p.y )){
			PointF c = DungeonTilemap.tileCenterToWorld(Dungeon.hero.pos);
			if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
				select(Dungeon.hero.pos, event.button, false);
				return;
			}
		}

		//then mobs
		for (Char mob : Dungeon.level.mobs.toArray(new Mob[0])){
			if (mob.sprite != null && mob.sprite.overlapsPoint( p.x, p.y )){
				PointF c = DungeonTilemap.tileCenterToWorld(mob.pos);
				if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
					select(mob.pos, event.button, false);
					return;
				}
			}
		}

		//then heaps
		for (Heap heap : Dungeon.level.heaps.valueList()){
			if (heap.sprite != null && heap.sprite.overlapsPoint( p.x, p.y)){
				PointF c = DungeonTilemap.tileCenterToWorld(heap.pos);
				if (Math.abs(p.x - c.x) <= 12 && Math.abs(p.y - c.y) <= 12) {
					select(heap.pos, event.button, false);
					return;
				}
			}
		}
			
		select( ((DungeonTilemap)target).screenToTile(
			(int) event.current.x,
			(int) event.current.y,
			true ), event.button, false );
	}

	protected void handleDragClick(PointerEvent event) {
		int cell = ((DungeonTilemap) target).screenToTile(
				(int) event.current.x,
				(int) event.current.y,
				true);
		if (lastSelectedCells.contains(cell)) {
			lastPoint = event.current;
			lastCell = cell;
			return; //don't trigger every frame, only when a new cell was entered
		}
		lastSelectedCells.add(cell);
		if (lastPoint != null) {
			stackOverflowChecker = 0;
			checkForMissingCells(cell, lastCell, event.current, lastPoint, event.button);
		}
		lastPoint = event.current;
		lastCell = cell;
		select(cell, event.button, true);
	}

	private int stackOverflowChecker;

	//if pointer is faster than the fps, some cells are skipped, this method corrects this by drawing straight lines between the known points
	private void checkForMissingCells(int cell, int lastCell, PointF now, PointF was, int button) {
		if (stackOverflowChecker >= 100) return;
		stackOverflowChecker++;

		for (int i : PathFinder.NEIGHBOURS9) {
			if (cell + i == lastCell) return;
		}

		PointF middle = new PointF(now.x + (was.x - now.x) / 2, now.y + (was.y - now.y) / 2);

		int middleCell = ((DungeonTilemap) target).screenToTile(
				(int) middle.x,
				(int) middle.y,
				true);

		if (middleCell == -1) return;

		checkForMissingCells(middleCell, cell, middle, now, button);//Recursively go from the middle to the borders
		checkForMissingCells(middleCell, lastCell, middle, was, button);

		if (lastSelectedCells.contains(middleCell))
			return;
		lastSelectedCells.add(middleCell);
		select(middleCell, button, true);
	}

	private float zoom( float value ) {

		lastPoint = null;

		value = GameMath.gate( PixelScene.minZoom, value, PixelScene.maxZoom );
		SPDSettings.zoom((int) (value - PixelScene.defaultZoom));
		camera.zoom( value );

		//Resets char and item sprite positions with the new camera zoom
		//This is important as sprites are centered on a 16x16 tile, but may have any sprite size
		//This can lead to none-whole coordinate, which need to be aligned with the zoom
		for (Char c : Actor.chars()){
			if (c.sprite != null && !c.sprite.isMoving){
				c.sprite.point(c.sprite.worldToCamera(c.pos));
			}
		}
		for (Heap heap : Dungeon.level.heaps.valueList()){
			if (heap.sprite != null){
				heap.sprite.point(heap.sprite.worldToCamera(heap.pos));
			}
		}

		return value;
	}
	
	public void select( int cell, int button, boolean dragClick ) {
		if (enabled && Dungeon.hero.ready && !GameScene.interfaceBlockingHero()
				&& listener != null && cell != -1) {

			switch (button){
				default:
					if (dragClick) listener.onSelectDragging(cell);
                    else listener.onSelect(cell);
					break;
				case PointerEvent.RIGHT:
					listener.onRightClick( cell );
					break;
			}
			GameScene.ready();
			
		} else {
			
			GameScene.cancel();
			
		}
	}
	
	private boolean pinching = false;
	private PointerEvent another;
	private float startZoom;
	private float startSpan;

	private boolean dragClickEnabled;
	
	@Override
	protected void onPointerDown( PointerEvent event ) {
		isPointerDown = true;
		camera.edgeScroll.set(-1);
		if (listener == null) return;
		dragClickEnabled = listener.dragClickEnabled();
		if (event != curEvent && another == null) {
					
			if (curEvent.type == PointerEvent.Type.UP) {
				curEvent = event;
				onPointerDown( event );
				return;
			}
			
			pinching = true;
			
			another = event;
			startSpan = PointF.distance( curEvent.current, another.current );
			startZoom = camera.zoom;

			dragging = false;
		} else if (event != curEvent) {
			reset();
		}
	}
	
	@Override
	protected void onPointerUp( PointerEvent event ) {

		if(dragClicking) wasJustDragClicking = true;
		dragClicking = false;
		isPointerDown = false;
		time = 0;
		lastSelectedCells.clear();
		lastCell = -1;
		lastPoint = null;

		camera.edgeScroll.set(1);
		if (pinching && (event == curEvent || event == another)) {
			
			pinching = false;
			
			zoom(Math.round( camera.zoom ));
			
			dragging = true;
			if (event == curEvent) {
				curEvent = another;
			}
			another = null;
			lastPos.set( curEvent.current );
		}
	}
	
	protected boolean dragging = false;
	private PointF lastPos = new PointF();
	
	@Override
	protected void onDrag( PointerEvent event ) {

		if (pinching) {

			float curSpan = PointF.distance( curEvent.current, another.current );
			float zoom = (startZoom * curSpan / startSpan);
			camera.zoom( GameMath.gate(
				PixelScene.minZoom,
					zoom - (zoom % 0.1f),
				PixelScene.maxZoom ) );

		} else {
		
			if (!dragging && PointF.distance( event.current, event.start ) > dragThreshold) {

				if (dragClicking && dragClickEnabled) {
					handleDragClick(event);
				} else {
					dragging = true;
					lastPos.set(event.current);
				}
				
			} else if (dragging) {
				camera.shift( PointF.diff( lastPos, event.current ).invScale( camera.zoom ) );
				lastPos.set( event.current );
			}
		}
		
	}

	//used for movement
	private GameAction heldAction1 = SPDAction.NONE;
	private GameAction heldAction2 = SPDAction.NONE;
	//not used for movement, but helpful if the player holds 3 keys briefly
	private GameAction heldAction3 = SPDAction.NONE;

	private float heldDelay = 0f;
	private boolean delayingForRelease = false;

	private static float initialDelay(){
		switch (SPDSettings.movementHoldSensitivity()){
			case 0:
				return Float.POSITIVE_INFINITY;
			case 1:
				return 0.13f;
			case 2:
				return 0.09f;
			//note that delay starts ticking down on the frame it is processed
			// so in most cases the actual default wait is 50-58ms
			case 3: default:
				return 0.06f;
			case 4:
				return 0.03f;
		}
	}

	protected boolean controlHolding, shiftHolding;
	
	private Signal.Listener<KeyEvent> keyListener = new Signal.Listener<KeyEvent>() {
		@Override
		public boolean onSignal(KeyEvent event) {
			GameAction action = KeyBindings.getActionForKey(event);

			if (action == SPDAction.CONTROL) controlHolding = event.pressed;
			if (action == SPDAction.SHIFT) {
				shiftHolding = event.pressed;
				if (shiftKeyAction()) return true;
			}

			if (!event.pressed) {

				if (handleZoom(action)) return true;

				if (heldAction1 != SPDAction.NONE && heldAction1 == action) {
					heldAction1 = SPDAction.NONE;
					if (heldAction2 != SPDAction.NONE) {
						heldAction1 = heldAction2;
						heldAction2 = SPDAction.NONE;
						if (heldAction3 != SPDAction.NONE) {
							heldAction2 = heldAction3;
							heldAction3 = SPDAction.NONE;
						}
					}
				} else if (heldAction2 != SPDAction.NONE && heldAction2 == action) {
					heldAction2 = SPDAction.NONE;
					if (heldAction3 != SPDAction.NONE) {
						heldAction2 = heldAction3;
						heldAction3 = SPDAction.NONE;
					}
				} else if (heldAction3 != SPDAction.NONE && heldAction3 == action) {
					heldAction3 = SPDAction.NONE;
				}

				//move from the action immediately if it was being delayed
				// and another key wasn't recently released
				if (heldDelay > 0f && !delayingForRelease) {
					heldDelay = 0f;
					moveFromActions(action, heldAction1, heldAction2);
				}

				if (heldAction1 == GameAction.NONE && heldAction2 == GameAction.NONE) {
					resetKeyHold();
					return true;
				} else {
					delayingForRelease = true;
					//in case more keys are being released
					//note that this delay can tick down while the hero is moving
					heldDelay = initialDelay();
				}

			} else {
				if (!directionFromAction(action).isZero()) {

					if (Dungeon.hero != null) Dungeon.hero.resting = false;
					lastCellMoved = -1;
					if (heldAction1 == SPDAction.NONE) {
						heldAction1 = action;
						heldDelay = initialDelay();
						delayingForRelease = false;
					} else if (heldAction2 == SPDAction.NONE) {
						heldAction2 = action;
					} else {
						heldAction3 = action;
					}

					return true;
				} else if (Dungeon.hero != null && Dungeon.hero.resting) {
					Dungeon.hero.resting = false;
					return true;
				}

				return false;
			}
			return false;
		}
	};

	private boolean handleZoom(GameAction action) {
		if (action == SPDAction.ZOOM_IN) {
			zoom(camera.zoom + 1);
			mouseZoom = camera.zoom;
			return true;

		} else if (action == SPDAction.ZOOM_OUT) {
			zoom(camera.zoom - 1);
			mouseZoom = camera.zoom;
			return true;
		}
		return false;
	}

	protected boolean shiftKeyAction(){
		return false;
	}


	private GameAction leftStickAction = SPDAction.NONE;

	@Override
	public void update() {
		super.update();

		GameAction newLeftStick = actionFromStick(ControllerHandler.leftStickPosition.x,
				ControllerHandler.leftStickPosition.y);

		//skip logic here if there's no input, or if input is blocked
		if (!CustomDungeon.isEditing()
				&& ((newLeftStick == leftStickAction
					&& leftStickAction == GameAction.NONE
					&& heldAction1 == SPDAction.NONE)
					|| GameScene.interfaceBlockingHero())){
			return;
		}

		if (newLeftStick != leftStickAction){
			if (leftStickAction == SPDAction.NONE){
				heldDelay = initialDelay();
				if (Dungeon.hero != null) Dungeon.hero.resting = false;
			} else if (newLeftStick == SPDAction.NONE && heldDelay > 0f){
				heldDelay = 0f;
				moveFromActions(leftStickAction);
			}
			leftStickAction = newLeftStick;
		}

		if (heldDelay > 0){
			heldDelay -= Game.elapsed;
		}

		if ((heldAction1 != SPDAction.NONE || leftStickAction != SPDAction.NONE) && Dungeon.hero.ready){
			processKeyHold();
		} else if (Dungeon.hero != null && Dungeon.hero.ready) {
			lastCellMoved = -1;
		}

		if (isPointerDown && !dragClicking) {// long click detection
			time += Game.elapsed;
			if (time >= Button.longClick) {
				dragClicking = true;
				if (dragClickEnabled && !pinching && !dragging) {
					SandboxPixelDungeon.vibrate(50);
					handleDragClick(curEvent);
				}
			}
		}
	}

	//prevents repeated inputs when the hero isn't moving
	private int lastCellMoved = 0;

	protected boolean moveFromActions(GameAction... actions){
		if (Dungeon.hero == null || !Dungeon.hero.ready){
			return false;
		}

		if (GameScene.cancelCellSelector()){
			return false;
		}

		Point direction = new Point();
		for (GameAction action : actions) {
			direction.offset(directionFromAction(action));
		}
		int cell = Dungeon.hero.pos;
		//clamp to adjacent values (-1 to +1)
		cell += GameMath.gate(-1, direction.x, +1);
		cell += GameMath.gate(-1, direction.y, +1) * Dungeon.level.width();

		if (cell != Dungeon.hero.pos && cell != lastCellMoved){
			lastCellMoved = cell;
			if (Dungeon.hero.handle( cell )) {
				Dungeon.hero.next();
			}
			return true;

		} else {
			return false;
		}

	}

	protected Point directionFromAction(GameAction action){
		if (action == SPDAction.N)  return new Point( 0, -1);
		if (action == SPDAction.NE) return new Point(+1, -1);
		if (action == SPDAction.E)  return new Point(+1,  0);
		if (action == SPDAction.SE) return new Point(+1, +1);
		if (action == SPDAction.S)  return new Point( 0, +1);
		if (action == SPDAction.SW) return new Point(-1, +1);;
		if (action == SPDAction.W)  return new Point(-1,  0);
		if (action == SPDAction.NW) return new Point(-1, -1);
		else                        return new Point();
	}

	//~80% deadzone
	private GameAction actionFromStick(float x, float y){
		if (x > 0.5f){
			if (y < -0.5f){
				return SPDAction.NE;
			} else if (y > 0.5f){
				return SPDAction.SE;
			} else if (x > 0.8f){
				return SPDAction.E;
			}
		} else if (x < -0.5f){
			if (y < -0.5f){
				return SPDAction.NW;
			} else if (y > 0.5f){
				return SPDAction.SW;
			} else if (x < -0.8f){
				return SPDAction.W;
			}
		} else if (y > 0.8f){
			return SPDAction.S;
		} else if (y < -0.8f){
			return SPDAction.N;
		}
		return SPDAction.NONE;
	}

	public void processKeyHold() {
		//prioritize moving by controller stick over moving via keys
		if (!directionFromAction(leftStickAction).isZero() && heldDelay < 0) {
			enabled = true;
			if (Dungeon.hero != null) {
				Dungeon.hero.ready = true;
				Dungeon.observe();
				if (moveFromActions(leftStickAction)) {
					Dungeon.hero.ready = false;
				}
			}
		} else if (!(directionFromAction(heldAction1).offset(directionFromAction(heldAction2)).isZero())
				&& heldDelay <= 0){
			enabled = true;
			if (Dungeon.hero != null) {
				Dungeon.hero.ready = true;
				Dungeon.observe();
				if (moveFromActions(heldAction1, heldAction2)) {
					Dungeon.hero.ready = false;
				}
			}
		}
	}
	
	public void resetKeyHold(){
		heldAction1 = heldAction2 = heldAction3 = SPDAction.NONE;
	}
	
	public void cancel() {
		
		if (listener != null) {
			listener.onSelect( null );
		}

		DungeonScene.ready();
	}

	@Override
	public void reset() {
		super.reset();
		another = null;
		if (pinching){
			pinching = false;

			zoom( Math.round( camera.zoom ) );
		}
	}

	public void enable(boolean value){
		if (enabled != value){
			enabled = value;
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		KeyEvent.removeKeyListener( keyListener );
	}
	
	public static abstract class Listener {

		public long minShowingTime = -1;

		public abstract void onSelect(Integer cell );

		public void onSelectDragging(Integer cell) {
			onSelect(cell);
		}

		public void onRightClick( Integer cell ){} //do nothing by default

		public void onMiddleClick(Integer cell) {
			onSelect(cell);
		}

		public abstract String prompt();
		public Component promptComp() {return null;}

		protected boolean dragClickEnabled() {
			return false;
		}
	}
}
