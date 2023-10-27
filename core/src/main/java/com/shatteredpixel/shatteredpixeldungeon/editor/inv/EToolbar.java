package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorItemBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.input.GameAction;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.io.IOException;

public class EToolbar extends Component {


    private Toolbar.Tool btnSwitchFloor;
    private Toolbar.Tool btnEdit;
    private Toolbar.Tool btnInventory;
    private QuickslotTileTool[] btnQuick;

    private static EToolbar instance;

    public EToolbar() {
        super();

        instance = this;

        height = btnInventory.height();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (instance == this) instance = null;
    }

    @Override
    protected void createChildren(Object... params) {

        btnQuick = new QuickslotTileTool[QuickSlot.SIZE];
        for (int i = 0; i < btnQuick.length; i++) {
            add(btnQuick[i] = new QuickslotTileTool(64, 0, 22, 24, i));
        }

//        //hidden button for quickslot selector keybind
//        add(new Button() {
//            @Override
//            protected void onClick() {
//
//                if (Dungeon.hero.ready && !GameScene.cancel()) {
//
//                    //TODO
//                    String[] slotNames = new String[6];
//                    Image[] slotIcons = new Image[6];
//                    for (int i = 0; i < 6; i++) {
//                        Item item = Dungeon.quickslot.getItem(i);
//
//                        if (item != null && !Dungeon.quickslot.isPlaceholder(i) &&
//                                (Dungeon.hero.buff(LostInventory.class) == null || item.keptThoughLostInvent)) {
//                            slotNames[i] = Messages.titleCase(item.name());
//                            slotIcons[i] = new ItemSprite(item);
//                        } else {
//                            slotNames[i] = Messages.get(Toolbar.class, "quickslot_assign");
//                            slotIcons[i] = new ItemSprite(ItemSpriteSheet.SOMETHING);
//                        }
//                    }
//
//                    String info = "";
//                    if (ControllerHandler.controllerActive) {
//                        info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_select") + "\n";
//                        info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_assign") + "\n";
//                        info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel");
//                    } else {
//                        info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "quickslot_select") + "\n";
//                        info += Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "quickslot_assign") + "\n";
//                        info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "quickslot_cancel");
//                    }
//
//                    Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "quickslot_prompt"), info, slotNames, slotIcons) {
//                        @Override
//                        public void onSelect(int idx, boolean alt) {
////                            Item item = Dungeon.quickslot.getItem(idx);
////
////                            if (item == null || Dungeon.quickslot.isPlaceholder(idx)
////                                    || (Dungeon.hero.buff(LostInventory.class) != null && !item.keptThoughLostInvent)
////                                    || alt){
////                                //TODO would be nice to use a radial menu for this too
////                                // Also a bunch of code could be moved out of here into subclasses of RadialMenu
////                                GameScene.selectItem(new WndBag.ItemSelector() {
////                                    @Override
////                                    public String textPrompt() {
////                                        return Messages.get(QuickSlotButton.class, "select_item");
////                                    }
////
////                                    @Override
////                                    public boolean itemSelectable(Item item) {
////                                        return item.defaultAction() != null;
////                                    }
////
////                                    @Override
////                                    public void onSelect(Item item) {
////                                        if (item != null) {
////                                            QuickSlotButton.set(idx, item);
////                                        }
////                                    }
////                                });
////                            } else {
////
////                                item.execute(Dungeon.hero);
////                                if (item.usesTargeting) {
////                                    QuickSlotButton.useTargeting(idx);
////                                }
////                            }
//                            super.onSelect(idx, alt);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public GameAction keyAction() {
//                if (btnSwitchFloor.active) return SPDAction.QUICKSLOT_SELECTOR;
//                else				return null;
//            }
//        });

        add(btnSwitchFloor = new Toolbar.Tool(24, 0, 20, 26) {
            @Override
            protected void onClick() {
                try {
                    CustomDungeonSaves.saveLevel(EditorScene.customLevel());
                    EditorScene.show(new WndSwitchFloor());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.WAIT_OR_PICKUP;
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return SPDAction.WAIT;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "switch_floor"));
            }
        });
        btnSwitchFloor.copyFromImage(new Image(Assets.Interfaces.TILEBAR));

        add(btnEdit = new Toolbar.Tool(44, 0, 20, 26) {
            @Override
            protected void onClick() {
                EditorScene.selectCell(informerEditCell);
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.EXAMINE;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "edit"));
            }

        });
        btnEdit.copyFromImage(new Image(Assets.Interfaces.TILEBAR));

        add(btnInventory = new Toolbar.Tool(0, 0, 24, 26) {

            @Override
            protected void onClick() {
                if (!EditorScene.cancel()) {
                    EditorScene.show(new WndEditorInv(EditorItemBag.getLastBag()));
                }
            }

            @Override
            protected boolean onLongClick() {
                EditorScene.show(new WndOptions(Messages.get(EToolbar.class, "clear_toolbar_title"),
                        Messages.get(EToolbar.class, "clear_toolbar_body"),
                        Messages.get(ExoticPotion.class, "yes"), Messages.get(ExoticPotion.class, "no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            clearToolbar();
                            autoselect();
                        }
                    }
                });
                return true;
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.INVENTORY;
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return SPDAction.INVENTORY_SELECTOR;
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "inventory"));
            }
        });
        btnInventory.copyFromImage(new Image(Assets.Interfaces.TILEBAR));

//        //hidden button for inventory selector keybind
//        add(new Button() {
//            @Override
//            protected void onClick() {
////                ArrayList<Bag> bags = Dungeon.hero.belongings.getBags();
////                String[] names = new String[bags.size()];
////                Image[] images = new Image[bags.size()];
////                for (int i = 0; i < bags.size(); i++) {
////                    names[i] = Messages.titleCase(bags.get(i).name());
////                    images[i] = new ItemSprite(bags.get(i));
////                }
////                String info = "";
////                if (ControllerHandler.controllerActive) {
////                    info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "container_select") + "\n";
////                    info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "container_cancel");
////                } else {
////                    info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "container_select") + "\n";
////                    info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "container_cancel");
////                }
////
////                Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "container_prompt"), info, names, images) {
////                    @Override
////                    public void onSelect(int idx, boolean alt) {
////                        super.onSelect(idx, alt);
////                        Bag bag = bags.get(idx);
////                        ArrayList<Item> items = (ArrayList<Item>) bag.items.clone();
////
////                        for (Item i : bag.items) {
////                            if (i instanceof Bag) items.remove(i);
////                            if (Dungeon.hero.buff(LostInventory.class) != null && !i.keptThoughLostInvent)
////                                items.remove(i);
////                        }
////
////                        if (idx == 0) {
////                            Belongings b = Dungeon.hero.belongings;
////                            if (b.ring() != null) items.add(0, b.ring());
////                            if (b.misc() != null) items.add(0, b.misc());
////                            if (b.artifact() != null) items.add(0, b.artifact());
////                            if (b.armor() != null) items.add(0, b.armor());
////                            if (b.weapon() != null) items.add(0, b.weapon());
////                        }
////
////                        if (items.size() == 0) {
////                            GameScene.show(new WndMessage(Messages.get(Toolbar.class, "container_empty")));
////                            return;
////                        }
////
////                        String[] itemNames = new String[items.size()];
////                        Image[] itemIcons = new Image[items.size()];
////                        for (int i = 0; i < items.size(); i++) {
////                            itemNames[i] = Messages.titleCase(items.get(i).name());
////                            itemIcons[i] = new ItemSprite(items.get(i));
////                        }
////
////                        String info = "";
////                        if (ControllerHandler.controllerActive) {
////                            info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.LEFT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_select") + "\n";
////                            info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.RIGHT_CLICK, true)) + ": " + Messages.get(Toolbar.class, "item_use") + "\n";
////                            info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, true)) + ": " + Messages.get(Toolbar.class, "item_cancel");
////                        } else {
////                            info += Messages.get(WndKeyBindings.class, SPDAction.LEFT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "item_select") + "\n";
////                            info += Messages.get(WndKeyBindings.class, SPDAction.RIGHT_CLICK.name()) + ": " + Messages.get(Toolbar.class, "item_use") + "\n";
////                            info += KeyBindings.getKeyName(KeyBindings.getFirstKeyForAction(GameAction.BACK, false)) + ": " + Messages.get(Toolbar.class, "item_cancel");
////                        }
////
////                        Game.scene().addToFront(new RadialMenu(Messages.get(Toolbar.class, "item_prompt"), info, itemNames, itemIcons) {
////                            @Override
////                            public void onSelect(int idx, boolean alt) {
////                                super.onSelect(idx, alt);
////                                Item item = items.get(idx);
////                                if (alt && item.defaultAction() != null) {
////                                    item.execute(Dungeon.hero);
////                                } else {
////                                    Game.scene().addToFront(new WndUseItem(null, item));
////                                }
////                            }
////                        });
////                    }
////                });
//            }
//
//            @Override
//            public GameAction keyAction() {
//                return SPDAction.INVENTORY_SELECTOR;
//            }
//        });
    }

    @Override
    protected void layout() {

        float right = width;

        int quickslotsToShow = 4;
        int usedWidth = 152;
        while (PixelScene.uiCamera.width > usedWidth && quickslotsToShow < QuickSlot.SIZE) {
            quickslotsToShow++;
            usedWidth += 18;
        }

        int startingSlot;
        startingSlot = 0;
        int endingSlot = startingSlot + quickslotsToShow - 1;

        for (int i = 0; i < btnQuick.length; i++) {
            btnQuick[i].visible = i <= endingSlot;
            btnQuick[i].enable(btnQuick[i].visible);
            if (i > endingSlot) {
                btnQuick[i].setPos(btnQuick[i].left(), PixelScene.uiCamera.height);
            }
        }

        Dungeon.customDungeon.restoreToolbar();
        autoselect();

        if (SPDSettings.interfaceSize() > 0) {
            btnInventory.setPos(right - btnInventory.width(), y);
            btnSwitchFloor.setPos(btnInventory.left() - btnSwitchFloor.width(), y);
            btnEdit.setPos(btnSwitchFloor.left() - btnEdit.width(), y);

            right = btnEdit.left();
            for (int i = endingSlot; i >= startingSlot; i--) {
                if (i == endingSlot) {
                    btnQuick[i].border(0, 2);
                    btnQuick[i].frame(106, 0, 19, 24);
                } else if (i == 0) {
                    btnQuick[i].border(2, 1);
                    btnQuick[i].frame(86, 0, 20, 24);
                } else {
                    btnQuick[i].border(0, 1);
                    btnQuick[i].frame(88, 0, 18, 24);
                }
                btnQuick[i].setPos(right - btnQuick[i].width(), y + 2);
                right = btnQuick[i].left();
            }
            return;
        }

        for (int i = startingSlot; i <= endingSlot; i++) {
            if (i == startingSlot && !SPDSettings.flipToolbar() ||
                    i == endingSlot && SPDSettings.flipToolbar()) {
                btnQuick[i].border(0, 2);
                btnQuick[i].frame(106, 0, 19, 24);
            } else if (i == startingSlot && SPDSettings.flipToolbar() ||
                    i == endingSlot && !SPDSettings.flipToolbar()) {
                btnQuick[i].border(2, 1);
                btnQuick[i].frame(86, 0, 20, 24);
            } else {
                btnQuick[i].border(0, 1);
                btnQuick[i].frame(88, 0, 18, 24);
            }
        }

        float shift = 0;
        switch (Toolbar.Mode.valueOf(SPDSettings.toolbarMode())) {
            case SPLIT:
                btnSwitchFloor.setPos(x, y);
                btnEdit.setPos(btnSwitchFloor.right(), y);

                btnInventory.setPos(right - btnInventory.width(), y);

                btnQuick[startingSlot].setPos(btnInventory.left() - btnQuick[startingSlot].width(), y + 2);
                for (int i = startingSlot + 1; i <= endingSlot; i++) {
                    btnQuick[i].setPos(btnQuick[i - 1].left() - btnQuick[i].width(), y + 2);
                    shift = btnEdit.right() - btnQuick[i].left();
                }

                break;

            //center = group but.. well.. centered, so all we need to do is pre-emptively set the right side further in.
            case CENTER:
                float toolbarWidth = btnSwitchFloor.width() + btnEdit.width() + btnInventory.width();
                for (Button slot : btnQuick) {
                    if (slot.visible) toolbarWidth += slot.width();
                }
                right = (width + toolbarWidth) / 2;

            case GROUP:
                btnSwitchFloor.setPos(right - btnSwitchFloor.width(), y);
                btnEdit.setPos(btnSwitchFloor.left() - btnEdit.width(), y);
                btnInventory.setPos(btnEdit.left() - btnInventory.width(), y);

                btnQuick[startingSlot].setPos(btnInventory.left() - btnQuick[startingSlot].width(), y + 2);
                for (int i = startingSlot + 1; i <= endingSlot; i++) {
                    btnQuick[i].setPos(btnQuick[i - 1].left() - btnQuick[i].width(), y + 2);
                    shift = -btnQuick[i].left();
                }

                break;
        }

        if (shift > 0) {
            shift /= 2; //we want to center;
            for (int i = startingSlot; i <= endingSlot; i++) {
                btnQuick[i].setPos(btnQuick[i].left() + shift, btnQuick[i].top());
            }
        }

        right = width;

        if (SPDSettings.flipToolbar()) {

            btnSwitchFloor.setPos((right - btnSwitchFloor.right()), y);
            btnEdit.setPos((right - btnEdit.right()), y);
            btnInventory.setPos((right - btnInventory.right()), y);

            for (int i = startingSlot; i <= endingSlot; i++) {
                btnQuick[i].setPos(right - btnQuick[i].right(), y + 2);
            }

        }

    }

    public static void updateLayout() {
        if (instance != null) instance.layout();
    }


    public void alpha(float value) {
        btnSwitchFloor.alpha(value);
        btnEdit.alpha(value);
        btnInventory.alpha(value);
        for (QuickslotTileTool tool : btnQuick) {
            tool.alpha(value);
        }
    }

    private static int selectedSlot;
    public static void select(int slot) {
        if (instance == null) return;
        selectedSlot = slot;
        for (int i = 0; i < instance.btnQuick.length; i++) {
            if (instance.btnQuick[i].visible) instance.btnQuick[i].setSelected(i == selectedSlot);
        }
        EditorScene.cancel();

        if (Dungeon.customDungeon != null) Dungeon.customDungeon.lastSelectedToolbarSlot = slot;
    }

    private void autoselect() {

        int firstNonEmptySlot = -1;
        for (int i = 0; i < btnQuick.length; i++) {
            if (btnQuick[i].visible && Dungeon.quickslot.getItem(i) != null) {
                firstNonEmptySlot = i;
                break;
            }
        }
        int lsts = Dungeon.customDungeon.lastSelectedToolbarSlot;
        
        if (firstNonEmptySlot == -1)
            QuickSlotButton.set(0, EditorItem.REMOVER_ITEM);

        if (lsts < btnQuick.length && btnQuick[lsts].visible) select(lsts);
        else select(firstNonEmptySlot);

//        Item i = EditorItemBag.getFirstItem();
//        if (i != null) QuickSlotButton.set(1, i);
    }

    public static Item getSelectedItem() {
        if (instance == null) return null;
        for (int i = 0; i < instance.btnQuick.length; i++) {
            if (instance.btnQuick[i].selected) return Dungeon.quickslot.getItem(i);
        }
        return null;
    }

    private static int scrollUnits;
    public static void scroll(float amount) {
        if (instance == null) return;
        scrollUnits += amount;
        int actuallyScrolling = scrollUnits / 2;
        if (actuallyScrolling != 0) scrollUnits = 0;
        else return;
        int numVisibleSlots = getNumVisibleSlots();
        int slot = (int) (selectedSlot - actuallyScrolling) % numVisibleSlots;
        while (slot < 0) slot += numVisibleSlots;
        select(slot);
    }

    private static int getNumVisibleSlots() {
        if (instance == null) return -1;
        for (int i = 0; i < instance.btnQuick.length; i++) {
            if (!instance.btnQuick[i].visible) return i;
        }
        return instance.btnQuick.length;
    }

    public static int getSelectedSlot() {
        if (instance == null) return -1;
        return selectedSlot;
    }

    private static int priorSelectedSlot;

    public static void selectRemoverTemporarily() {
        if (instance == null) return;
        priorSelectedSlot = selectedSlot;
        QuickSlotButton.set(EditorItem.REMOVER_ITEM);
    }

    public static void unselectTemporarilyRemover() {
        if (instance == null) return;
        select(priorSelectedSlot);
    }

    public static void clearToolbar(){
        try {
            //couldn't find a better way...
            for (int i = 0; i < instance.btnQuick.length; i++) {
                QuickSlotButton.set(i, EditorItem.REMOVER_ITEM);
            }
            QuickSlotButton.set(0, EditorItem.REMOVER_ITEM);
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException ignored) {
        }
    }

    private static class QuickslotTileTool extends Toolbar.QuickslotTool {

        private boolean selected = false;
        private ColorBlock bg;

        public QuickslotTileTool(int x, int y, int width, int height, int slotNum) {
            super(x, y, width, height, slotNum);
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);
            bg = new ColorBlock(1, 1, -16777216);
            bg.color(0.1059f, 0.5490f, 0.1961f);//27 140 50 -> green
            add(bg);
        }

        public void setSelected(boolean selected) {
            this.selected = bg.visible = selected;
        }

        @Override
        protected void layout() {
            super.layout();
            bg.size(width - borderLeft - borderRight, height - 4);
            bg.x = x + borderLeft;
            bg.y = y + 2;
        }
    }


    public static final CellSelector.Listener informerEditCell = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) EditorScene.showEditCellWindow(cell);
        }

        @Override
        public String prompt() {
            return Messages.get(EToolbar.class, "examine_prompt");
        }
    };

}