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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Function;
import com.watabou.utils.PathFinder;

import java.util.List;

public class QuickSlotButton extends Button {
	
	private static QuickSlotButton[] instance = new QuickSlotButton[QuickSlot.SIZE];
	private int slotNum;

	private ItemSlot slot;
	
	private Image crossB;
	private Image crossM;
	
	public static int targetingSlot = -1;
	public static Char lastTarget = null;
	
	public QuickSlotButton( int slotNum ) {
		super();
		this.slotNum = slotNum;
		item( select( slotNum ) );
		
		instance[slotNum] = this;
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		reset();
	}

	public static void reset() {
		instance = new QuickSlotButton[QuickSlot.SIZE];

		lastTarget = null;
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();

		if (CustomDungeon.isEditing()) {
			slot = new QuickItemSlot() {
				@Override
				protected void onClick() {
					EToolbar.select(slotNum);
				}
			};
		} else {
			slot = new QuickItemSlot() {
				@Override
				protected void onClick() {
					if (!Dungeon.hero.isAlive() || !Dungeon.hero.ready) {
						return;
					}
					if (targetingSlot == slotNum) {
						int cell = autoAim(lastTarget, select(slotNum));

						if (cell != -1) {
							GameScene.handleCell(cell);
						} else {
							//couldn't auto-aim, just target the position and hope for the best.
							GameScene.handleCell(lastTarget.pos);
						}
					} else {
						Item item = select(slotNum);
						if (Dungeon.hero.belongings.contains(item) && !GameScene.cancel()) {
							GameScene.centerNextWndOnInvPane();

							Dungeon.dungeonScript.executeItem(item, Dungeon.hero, new DungeonScript.Executer() {
								@Override
								protected void execute(Item item, Hero hero, String action) {
									super.execute(item, hero, action);
									if (item.usesTargeting) {
										useTargeting();
									}
								}
							});

						}
					}
				}
			};
		}
		slot.showExtraInfo(false);
		add(slot);

		crossB = Icons.TARGET.get();
		crossB.visible = false;
		add(crossB);

		crossM = new Image();
		crossM.copy(crossB);

	}

	private abstract class QuickItemSlot extends ItemSlot {
		public QuickItemSlot() {
			super();
		}

		@Override
		protected void onRightClick() {
			QuickSlotButton.this.onLongClick();
		}

		@Override
		protected void onMiddleClick() {
			onClick();
		}

		@Override
		public GameAction keyAction() {
			return QuickSlotButton.this.keyAction();
		}

		@Override
		public GameAction secondaryTooltipAction() {
			return QuickSlotButton.this.secondaryTooltipAction();
		}

		@Override
		protected boolean onLongClick() {
			return QuickSlotButton.this.onLongClick();
		}

		@Override
		protected void onPointerDown() {
			sprite.lightness(CustomDungeon.isEditing() ? 0.55f : 0.7f);
			if (CustomDungeon.isEditing()) Sample.INSTANCE.play(Assets.Sounds.CLICK);
		}

		@Override
		protected void onPointerUp() {
			sprite.resetColor();
		}

		@Override
		protected String hoverText() {
			if (item == null) {
				return Messages.titleCase(Messages.get(WndKeyBindings.class, "quickslot", (slotNum + 1)));
			} else {
				return super.hoverText();
			}
		}

		@Override
		protected void viewSprite(Item item) {
			if (sprite != null) {
				remove(sprite);
				sprite.destroy();
			}
			if (item instanceof EditorItem) sprite = ((EditorItem<?>) item).getSprite();
			else sprite = new ItemSprite(item);
			if (sprite != null) {
				addToBack(sprite);
			}
		}
	};

	@Override
	protected void layout() {
		super.layout();
		
		slot.fill( this );
		
		crossB.x = x + (width - crossB.width) / 2;
		crossB.y = y + (height - crossB.height) / 2;
		PixelScene.align(crossB);
	}

	public void alpha( float value ){
		slot.alpha(value);
	}

	@Override
	public void update() {
		super.update();
		if (targetingSlot != -1 && lastTarget != null && lastTarget.sprite != null){
			crossM.point(lastTarget.sprite.center(crossM));
		}
	}

	@Override
	public GameAction keyAction() {
		switch (slotNum){
			case 0:
				return SPDAction.QUICKSLOT_1;
			case 1:
				return SPDAction.QUICKSLOT_2;
			case 2:
				return SPDAction.QUICKSLOT_3;
			case 3:
				return SPDAction.QUICKSLOT_4;
			case 4:
				return SPDAction.QUICKSLOT_5;
			case 5:
				return SPDAction.QUICKSLOT_6;
			case 6:
				return SPDAction.QUICKSLOT_7;
			case 7:
				return SPDAction.QUICKSLOT_8;
			case 8:
				return SPDAction.QUICKSLOT_9;
			case 9:
				return SPDAction.QUICKSLOT_10;
			default:
				return super.keyAction();
		}
	}

	@Override
	public GameAction secondaryTooltipAction() {
		return SPDAction.QUICKSLOT_SELECTOR;
	}

	@Override
	protected String hoverText() {
		if (slot.item == null){
			return Messages.titleCase(Messages.get(WndKeyBindings.class, "quickslot", (slotNum+1)));
		} else {
			return super.hoverText();
		}
	}
	
	@Override
	protected void onClick() {
		if (CustomDungeon.isEditing()) {
			EditorScene.selectItem(editorItemSelector);
			return;
		}
		if (Dungeon.hero.ready && !GameScene.cancel()) {
			GameScene.selectItem(itemSelector);
		}
	}

	@Override
	protected void onRightClick() {
		onClick();
	}

	@Override
	protected void onMiddleClick() {
		onClick();
	}

	@Override
	protected boolean onLongClick() {
		onClick();
		return true;
	}

	private WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(QuickSlotButton.class, "select_item");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item.defaultAction() != null;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				set( slotNum , item );
			}
		}
	};

	private WndBag.ItemSelector editorItemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(QuickSlotButton.class, "select_item");
		}

		@Override
		public List<Bag> getBags() {
			return EditorInventory.getMainBags();
		}

		@Override
		public boolean itemSelectable(Item item) {
			return true;
		}

		@Override
		public void onSelect(Item item) {
			if (item != null) {
				set(slotNum, item);
			}
		}

		@Override
		public EditorItem.NullItemClass getItemForNull() {
			return EditorItem.REMOVER_ITEM;
		}
	};

	public static int lastVisible = instance.length;

	public static void set(Item item){
		boolean containsItem = containsItem(item) != null;
		if (EToolbar.getSelectedSlot() != -1 && select(EToolbar.getSelectedSlot()) == null && !containsItem) {
			set(EToolbar.getSelectedSlot(), item);
			return;
		}
		for (int i = 0; i < lastVisible; i++) {
			if ((select(i) == null && (instance[i].active || (Toolbar.SWAP_INSTANCE != null && Toolbar.SWAP_INSTANCE.active))) && !containsItem
					|| select(i) == item) {
				set(i, item);
				return;
			}
		}
		set(EToolbar.getSelectedSlot(), item);
	}

	public static void set(int slotNum, Item item){
		EToolbar.select(slotNum);
		Dungeon.quickslot.setSlot( slotNum , item );
		refresh();

		//Remember if the player adds the waterskin as one of their first actions.
		if (Statistics.duration + Actor.now() <= 10){
			boolean containsWaterskin = false;
			for (int i = 0; i < instance.length; i++) {
				if (select(i) instanceof Waterskin) containsWaterskin = true;
			}
			if (containsWaterskin) SPDSettings.quickslotWaterskin(true);
		}
	}

	private static Item select(int slotNum){
		return Dungeon.quickslot.getItem( slotNum );
	}
	
	public void item( Item item ) {
		slot.item( item );
		enableSlot();
	}

	public void enable( boolean value ) {
		active = value;
		if (value) {
			enableSlot();
		} else {
			slot.enable( false );
		}
	}
	
	private void enableSlot() {
		slot.enable(Dungeon.quickslot.isNonePlaceholder( slotNum )
				&& (Dungeon.hero == null || !Dungeon.hero.belongings.lostInventory() || Dungeon.quickslot.getItem(slotNum).keptThroughLostInventory()));
	}

	public void slotMargins( int left, int top, int right, int bottom){
		slot.setMargins(left, top, right, bottom);
	}

	public static void useTargeting(int idx){
		instance[idx].useTargeting();
	}

	private void useTargeting() {

		if (lastTarget != null &&
				Actor.chars().contains( lastTarget ) &&
				lastTarget.isAlive() &&
				lastTarget.alignment != Char.Alignment.ALLY &&
				Dungeon.level.heroFOV[lastTarget.pos]) {

			targetingSlot = slotNum;
			CharSprite sprite = lastTarget.sprite;

			if (sprite.parent != null) {
				sprite.parent.addToFront(crossM);
				crossM.point(sprite.center(crossM));
			}

			crossB.point(slot.sprite.center(crossB));
			crossB.visible = true;

		} else {

			lastTarget = null;
			targetingSlot = -1;

		}

	}

	public static int autoAim(Char target){
		//will use generic projectile logic if no item is specified
		return autoAim(target, new Item());
	}

	//FIXME: this is currently very expensive, should either optimize ballistica or this, or both
	public static int autoAim(Char target, Item item){

		//first try to directly target
		if (item.targetingPos(Dungeon.hero, target.pos) == target.pos) {
			return target.pos;
		}

		//Otherwise pick nearby tiles to try and 'angle' the shot, auto-aim basically.
		PathFinder.buildDistanceMapForEnvironmentals( target.pos, BArray.not( new boolean[Dungeon.level.length()], null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE
					&& item.targetingPos(Dungeon.hero, i) == target.pos)
				return i;
		}

		//couldn't find a cell, give up.
		return -1;
	}

	public static void refresh() {
		for (int i = 0; i < instance.length; i++) {
			if (instance[i] != null) {
				instance[i].item(select(i));
				instance[i].enable(instance[i].active);
			}
		}
		if (Toolbar.SWAP_INSTANCE != null){
			Toolbar.SWAP_INSTANCE.updateVisuals();
		}
		//Remember if the player removes the waterskin as one of their first actions.
		if (Statistics.duration + Actor.now() <= 10){
			boolean containsWaterskin = false;
			for (int i = 0; i < instance.length; i++) {
				if (select(i) instanceof Waterskin) containsWaterskin = true;
			}
			if (!containsWaterskin) SPDSettings.quickslotWaterskin(false);
		}
	}
	
	public static void target( Char target ) {
		if (target != null && target.alignment != Char.Alignment.ALLY) {
			lastTarget = target;
			
			TargetHealthIndicator.instance.target( target );
			InventoryPane.lastTarget = target;
		}
	}
	
	public static void cancel() {
		if (targetingSlot != -1) {
			for (QuickSlotButton btn : instance) {
				btn.crossB.visible = false;
				btn.crossM.remove();
				targetingSlot = -1;
			}
		}
	}

	public static ItemSlot containsItem(Item item) {
		for (int i = 0; i < instance.length; i++) {
			if (instance[i] != null && instance[i].slot.item == item) return instance[i].slot;
		}
		return null;
	}

	public static ItemSlot containsObject(Object obj) {
		for (int i = 0; i < instance.length; i++) {
			if (instance[i] != null) {
				Item itemInSlot = instance[i].slot.item;
				if (itemInSlot == obj || itemInSlot instanceof EditorItem && ((EditorItem<?>) itemInSlot).getObject() == obj) {
					return instance[i].slot;
				}
			}
		}
		return null;
	}

	public static void doOnAll(Function<GameObject, GameObject.ModifyResult> whatToDo) {
		if (instance == null) return;

		for (int i = 0; i < instance.length; i++) {
			if (instance[i] != null) {
				final int index = i;
				Object itemInSlot = instance[i].slot.item;
				if (itemInSlot instanceof EditorItem) itemInSlot = ((EditorItem<?>) itemInSlot).getObject();
				if (itemInSlot instanceof GameObject) {
					GameObject.doOnSingleObject(((GameObject) itemInSlot), whatToDo, obj -> {
						QuickSlotButton.set(index, new FindInBag(obj).getAsInBag());
					});
				}
			}
		}
	}
}
