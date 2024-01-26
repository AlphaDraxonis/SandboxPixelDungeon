package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.FistSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItemDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class EditTrapComp extends DefaultEditComp<Trap> {


    protected StyledCheckBox visible, active;
    protected StyledCheckBox searchable, revealedWhenTriggered, disarmedByActivation;
    protected StyledButton gatewayTelePos;
    protected Spinner pitfallRadius, pitfallDelay;
    protected ItemContainer<MobItem> summonMobs;
    protected RedButton randomTrap;
    private Window windowInstance;

    private final TrapItem trapItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditTrapComp(Trap item) {
        super(item);
        initComps();
        trapItem = null;
    }

    public EditTrapComp(TrapItem trapItem) {
        super(trapItem.getObject());
        initComps();
        this.trapItem = trapItem;
    }

    private void initComps() {

        if (obj instanceof RandomItem.RandomTrap) {

            randomTrap = new RedButton(EditItemComp.label("edit_random")) {
                @Override
                protected void onClick() {
                    RandomItemDistrComp randomItemDistrComp = new RandomItemDistrComp((RandomItem<?>) obj) {
                        @Override
                        protected void showAddItemWnd() {
                            EditorScene.selectItem(createSelector(TrapItem.class, false, Traps.bag.getClass()));
                        }
                    };
                    SimpleWindow w = new SimpleWindow((int) Math.ceil(width), (int) (PixelScene.uiCamera.height * 0.75));
                    w.initComponents(randomItemDistrComp.createTitle(), randomItemDistrComp, randomItemDistrComp.getOutsideSp(), 0f, 0.5f);
                    w.offset(EditorUtilies.getParentWindow(EditTrapComp.this).getOffset());
                    EditorScene.show(w);
                }
            };
            add(randomTrap);

        } else {

            visible = new StyledCheckBox(Messages.get(EditTrapComp.class, "visible")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    obj.visible = value;
                    updateObj();
                }
            };
            visible.icon(new BuffIcon(BuffIndicator.FORESIGHT, true));
            add(visible);
            active = new StyledCheckBox(Messages.get(EditTrapComp.class, "active")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    obj.active = value;
                    EditTrapComp.this.visible.enable(value);
                    updateObj();
                }
            };
            active.icon(IconTitleWithSubIcon.createSubIcon(ItemSpriteSheet.Icons.RING_ACCURACY));
            active.icon().scale.set(ItemSpriteSheet.SIZE / active.icon().width());
            add(active);

            visible.checked(obj.visible);
            active.checked(obj.active);

            searchable = new StyledCheckBox(Messages.get(EditTrapComp.class, "searchable")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    obj.canBeSearched = value;
                    updateObj();
                }
            };
            searchable.icon(Icons.MAGNIFY.get());
            searchable.icon().scale.set(ItemSpriteSheet.SIZE / searchable.icon().width());
            add(searchable);
            revealedWhenTriggered = new StyledCheckBox(Messages.get(EditTrapComp.class, "revealed_when_triggered")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    obj.revealedWhenTriggered = value;
                    updateObj();
                }
            };
            add(revealedWhenTriggered);
            disarmedByActivation = new StyledCheckBox(Messages.get(EditTrapComp.class, "disarmed_by_activation")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    obj.disarmedByActivation = value;
                    updateObj();
                }
            };
            disarmedByActivation.icon(new ItemSprite(ItemSpriteSheet.STONE_DISARM));
            add(disarmedByActivation);

            searchable.checked(obj.canBeSearched);
            revealedWhenTriggered.checked(obj.revealedWhenTriggered);
            disarmedByActivation.checked(obj.disarmedByActivation);

            if (obj instanceof GatewayTrap && obj.pos != -1) {
                int telePos = ((GatewayTrap) obj).telePos;
                gatewayTelePos = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "") {
                    @Override
                    protected void onClick() {
                        EditorScene.selectCell(gatewayTelePosListener);
                        windowInstance = EditorUtilies.getParentWindow(gatewayTelePos);
                        windowInstance.active = false;
                        if (windowInstance instanceof WndTabbed)
                            ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.NEVER_BLOCK);
                        Game.scene().remove(windowInstance);
                    }
                };
                gatewayTelePos.multiline = true;
                if (telePos == -1) gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_random"));
                else gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_pos", EditorUtilies.cellToString(telePos)));
                add(gatewayTelePos);
            } else gatewayTelePos = null;

            if (obj instanceof PitfallTrap) {
                pitfallRadius = new StyledSpinner(new SpinnerIntegerModel(0, 100, ((PitfallTrap) obj).radius, 1, false, null) {
                    {
                        setAbsoluteMaximum(100f);
                    }

                    @Override
                    public int getClicksPerSecondWhileHolding() {
                        return 30;
                    }
                }, Messages.get(EditMobComp.class, "radius"));
                pitfallRadius.addChangeListener(() -> ((PitfallTrap) obj).radius = (int) pitfallRadius.getValue());
                add(pitfallRadius);

                pitfallDelay = new StyledSpinner(new SpinnerIntegerModel(0, 100, ((PitfallTrap) obj).delay, 1, false, null) {
                    @Override
                    public int getClicksPerSecondWhileHolding() {
                        return 30;
                    }
                }, Messages.get(EditMobComp.class, "delay") + ":", 9);
                pitfallDelay.addChangeListener(() -> ((PitfallTrap) obj).delay = (int) pitfallDelay.getValue());
                add(pitfallDelay);
            } else {
                pitfallDelay = null;
                pitfallRadius = null;
            }

            if (obj instanceof SummoningTrap) {
                summonMobs = new ItemContainerWithLabel<MobItem>(FistSelector.createMobItems(((SummoningTrap) obj).spawnMobs), this, Messages.get(EditTrapComp.class, "summon_mobs")) {
                    @Override
                    public boolean itemSelectable(Item item) {
                        return item instanceof MobItem && ((MobItem) item).mob() instanceof YogFist;
                    }

                    @Override
                    protected void doAddItem(MobItem item) {
                        super.doAddItem(item);
                        ((SummoningTrap) obj).spawnMobs.add(item.mob());
                    }

                    @Override
                    protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                        if (super.removeSlot(slot)) {
                            ((SummoningTrap) obj).spawnMobs.remove(((MobItem) slot.item()).mob());
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public Class<? extends Bag> preferredBag() {
                        return Mobs.bag.getClass();
                    }
                };
                add(summonMobs);
            }
        }

        comps = new Component[]{visible, active, gatewayTelePos, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                pitfallDelay, pitfallRadius, EditorUtilies.PARAGRAPH_INDICATOR_INSTANCE,
                searchable, revealedWhenTriggered, disarmedByActivation};
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
        if (summonMobs != null) layoutCompsLinear(summonMobs);
        if (randomTrap != null) layoutCompsLinear(randomTrap);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), TrapItem.createTitle(obj));
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return TrapItem.getTrapImage(obj);
    }

    @Override
    protected void updateObj() {
        if (!obj.active && !obj.visible) {
            visible.checked(true);
            return;
        }
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(TrapItem.createTitle(obj));
            ((IconTitle) title).icon(TrapItem.getTrapImage(obj));
        }
        desc.text(createDescription());

        if (trapItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(trapItem);
            if (slot != null) slot.item(trapItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Trap a, Trap b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.visible != b.visible) return false;
        if (a.active != b.active) return false;
        if (a.canBeSearched != b.canBeSearched) return false;
        if (a.revealedWhenTriggered != b.revealedWhenTriggered) return false;
        if (a.disarmedByActivation != b.disarmedByActivation) return false;
        if (a instanceof GatewayTrap && ((GatewayTrap) a).telePos != ((GatewayTrap) b).telePos)
            return false;
        if (a instanceof PitfallTrap) {
            return ((PitfallTrap) a).radius == ((PitfallTrap) b).radius && ((PitfallTrap) a).delay == ((PitfallTrap) b).delay;
        }
        if (a instanceof SummoningTrap) {
            return EditMobComp.isMobListEqual(((SummoningTrap) a).spawnMobs, ((SummoningTrap) b).spawnMobs);
        }
        return true;
    }


    private final CellSelector.Listener gatewayTelePosListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                boolean validDest = Dungeon.level.isPassable(cell) && !Dungeon.level.secret[cell] && EditorScene.customLevel().findMob(cell) == null;
                GatewayTrap trap = (GatewayTrap) obj;
                if (!validDest) trap.telePos = -1;
                else trap.telePos = cell;
                if (trap.telePos == -1)
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_random"));
                else
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_pos", EditorUtilies.cellToString(trap.telePos)));
                windowInstance.active = true;
                if (windowInstance instanceof WndTabbed)
                    ((WndTabbed) windowInstance).setBlockLevelForTabs(PointerArea.ALWAYS_BLOCK);
                EditorScene.show(windowInstance);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(EditTrapComp.class, "gateway_trap_prompt");
        }
    };
}