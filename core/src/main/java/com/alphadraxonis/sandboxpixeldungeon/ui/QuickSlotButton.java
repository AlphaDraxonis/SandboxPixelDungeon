/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.ui;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.QuickSlot;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SPDSettings;
import com.alphadraxonis.sandboxpixeldungeon.Statistics;
import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.LostInventory;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EToolbar;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.EditorItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.Waterskin;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.CharSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.BArray;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class QuickSlotButton extends Button {

    private static QuickSlotButton[] instance = new QuickSlotButton[QuickSlot.SIZE];
    private int slotNum;

    private ItemSlot slot;

    private Image crossB;
    private Image crossM;

    public static int targetingSlot = -1;
    public static Char lastTarget = null;

    public QuickSlotButton(int slotNum) {
        super();
        this.slotNum = slotNum;
        item(select(slotNum));
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
    protected void createChildren(Object... params) {
        super.createChildren(params);

        if (CustomDungeon.isEditing()) {
            slot = new QuickItemSlot(null) {
                @Override
                protected void onClick() {
                    super.onClick();
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
                            item.execute(Dungeon.hero);
                            if (item.usesTargeting) {
                                useTargeting();
                            }
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

        public QuickItemSlot(TileItem tileItem) {
            super(tileItem);
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

        private Image spriteInstance;

        @Override
        protected void viewSprite(Item item) {
            if (sprite != null) {
                remove(sprite);
                sprite.destroy();
            }
            if (item instanceof EditorItem) sprite = ((EditorItem) item).getSprite();
            else sprite = new ItemSprite(item);
            if (sprite != null) addToBack(sprite);
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
    }

    @Override
    protected void layout() {
        super.layout();

        slot.fill(this);

        crossB.x = x + (width - crossB.width) / 2;
        crossB.y = y + (height - crossB.height) / 2;
        PixelScene.align(crossB);
    }

    public void alpha(float value) {
        slot.alpha(value);
    }

    @Override
    public void update() {
        super.update();
        if (targetingSlot != -1 && lastTarget != null && lastTarget.sprite != null) {
            crossM.point(lastTarget.sprite.center(crossM));
        }
    }

    @Override
    public GameAction keyAction() {
        switch (slotNum) {
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
        if (slot.item == null) {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "quickslot", (slotNum + 1)));
        } else {
            return super.hoverText();
        }
    }

    @Override
    protected void onClick() {
        if (CustomDungeon.isEditing()) {
            EditorScene.selectItem(itemSelector);
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
                set(slotNum, item);
            }
        }
    };

    public static void set(Item item) {
        for (int i = 0; i < instance.length; i++) {
            if ((select(i) == null && (instance[i].active || (Toolbar.SWAP_INSTANCE != null && Toolbar.SWAP_INSTANCE.active))) || select(i) == item) {
                set(i, item);
                return;
            }
        }
        set(0, item);
    }

    public static void set(int slotNum, Item item) {
        EToolbar.select(slotNum);
        Dungeon.quickslot.setSlot(slotNum, item);
        refresh();

        //Remember if the player adds the waterskin as one of their first actions.
        if (Statistics.duration + Actor.now() <= 10) {
            boolean containsWaterskin = false;
            for (int i = 0; i < instance.length; i++) {
                if (select(i) instanceof Waterskin) containsWaterskin = true;
            }
            if (containsWaterskin) SPDSettings.quickslotWaterskin(true);
        }
    }

    private static Item select(int slotNum) {
        return Dungeon.quickslot.getItem(slotNum);
    }

    public void item(Item item) {
        slot.item(item);
        enableSlot();
    }

    public void enable(boolean value) {
        active = value;
        if (value) {
            enableSlot();
        } else {
            slot.enable(false);
        }
    }

    private void enableSlot() {
        slot.enable(Dungeon.quickslot.isNonePlaceholder(slotNum)
                && (Dungeon.hero == null || Dungeon.hero.buff(LostInventory.class) == null || Dungeon.quickslot.getItem(slotNum).keptThoughLostInvent));
    }

    public void slotMargins(int left, int top, int right, int bottom) {
        slot.setMargins(left, top, right, bottom);
    }

    public static void useTargeting(int idx) {
        instance[idx].useTargeting();
    }

    private void useTargeting() {

        if (lastTarget != null &&
                Actor.chars().contains(lastTarget) &&
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

    public static int autoAim(Char target) {
        //will use generic projectile logic if no item is specified
        return autoAim(target, new Item());
    }

    //FIXME: this is currently very expensive, should either optimize ballistica or this, or both
    public static int autoAim(Char target, Item item) {

        //first try to directly target
        if (item.targetingPos(Dungeon.hero, target.pos) == target.pos) {
            return target.pos;
        }

        //Otherwise pick nearby tiles to try and 'angle' the shot, auto-aim basically.
        PathFinder.buildDistanceMap(target.pos, BArray.not(new boolean[Dungeon.level.length()], null), 2);
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
        if (Toolbar.SWAP_INSTANCE != null) {
            Toolbar.SWAP_INSTANCE.updateVisuals();
        }
        //Remember if the player removes the waterskin as one of their first actions.
        if (Statistics.duration + Actor.now() <= 10) {
            boolean containsWaterskin = false;
            for (int i = 0; i < instance.length; i++) {
                if (select(i) instanceof Waterskin) containsWaterskin = true;
            }
            if (!containsWaterskin) SPDSettings.quickslotWaterskin(false);
        }
    }

    public static void target(Char target) {
        if (target != null && target.alignment != Char.Alignment.ALLY) {
            lastTarget = target;

            TargetHealthIndicator.instance.target(target);
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

    //TODO pretty expensive??
    public static ItemSlot containsItem(Item item) {
        for (int i = 0; i < instance.length; i++) {
            if (instance[i].slot.item == item) return instance[i].slot;
        }
        return null;
    }
}