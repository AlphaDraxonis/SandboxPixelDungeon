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
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RageTrap;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class EditTrapComp extends DefaultEditComp<Trap> {


    protected StyledCheckBox visible, active;
    protected StyledCheckBox searchable, searchableByMagic, revealedWhenTriggered, disarmedByActivation;
    protected StyledButton gatewayTelePos;
    protected Spinner radius, pitfallDelay;
    protected ItemContainer<MobItem> summonMobs;
    protected RedButton randomTrap;
    private Window windowInstance;

    private final TrapItem trapItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditTrapComp(Trap item) {
        super(item);
        trapItem = null;
        initComps();
    }

    public EditTrapComp(TrapItem trapItem) {
        super(trapItem.getObject());
        this.trapItem = trapItem;
        initComps();
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

            visible = new StyledCheckBox(Messages.get(this, "visible"));
            visible.icon(new BuffIcon(BuffIndicator.FORESIGHT, true));
            visible.checked(obj.visible);
            visible.addChangeListener(v -> {
                obj.visible = v;
                updateObj();
            });
            add(visible);

            active = new StyledCheckBox(Messages.get(this, "active"));
            active.icon(EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.RING_ACCURACY));
            active.icon().scale.set(ItemSpriteSheet.SIZE / active.icon().width());
            active.checked(obj.active);
            active.addChangeListener(v -> {
                obj.active = v;
                EditTrapComp.this.visible.enable(v);
                updateObj();
            });
            add(active);

            searchable = new StyledCheckBox(Messages.get(this, "searchable"));
            searchable.icon(Icons.MAGNIFY.get());
            searchable.icon().scale.set(ItemSpriteSheet.SIZE / searchable.icon().width());
            searchable.checked(obj.canBeSearched);
            searchable.addChangeListener(v -> {
                obj.canBeSearched = v;
                updateObj();
            });
            add(searchable);

            searchableByMagic = new StyledCheckBox(Messages.get(this, "searchable_by_magic"));
            searchableByMagic.icon(EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP));
            searchableByMagic.icon().scale.set(ItemSpriteSheet.SIZE / searchableByMagic.icon().width());
            searchableByMagic.checked(obj.canBeSearchedByMagic);
            searchableByMagic.addChangeListener(v -> {
                obj.canBeSearchedByMagic = v;
                updateObj();
            });
            add(searchableByMagic);

            revealedWhenTriggered = new StyledCheckBox(Messages.get(this, "revealed_when_triggered"));
            revealedWhenTriggered.checked(obj.revealedWhenTriggered);
            revealedWhenTriggered.addChangeListener(v -> {
                obj.revealedWhenTriggered = v;
                updateObj();
            });
            add(revealedWhenTriggered);

            disarmedByActivation = new StyledCheckBox(Messages.get(this, "disarmed_by_activation"));
            disarmedByActivation.icon(new ItemSprite(ItemSpriteSheet.STONE_DISARM));
            disarmedByActivation.checked(obj.disarmedByActivation);
            disarmedByActivation.addChangeListener(v -> {
                obj.disarmedByActivation = v;
                updateObj();
            });
            add(disarmedByActivation);


            if (obj instanceof GatewayTrap && obj.pos != -1) {
                int telePos = ((GatewayTrap) obj).telePos;
                gatewayTelePos = new StyledButton(Chrome.Type.GREY_BUTTON_TR, "") {
                    {
                        text.align(RenderedTextBlock.CENTER_ALIGN);
                    }
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
                if (telePos == -1) gatewayTelePos.text(Messages.get(this, "gateway_trap_random"));
                else gatewayTelePos.text(Messages.get(this, "gateway_trap_pos", EditorUtilies.cellToString(telePos)));
                add(gatewayTelePos);

            }

            if (obj instanceof PitfallTrap || obj instanceof RageTrap) {
                int initValue = 0;
                Runnable listener = null;
                if (obj instanceof PitfallTrap) {
                    initValue = ((PitfallTrap) obj).radius;
                    listener = () -> ((PitfallTrap) obj).radius = (int) radius.getValue();
                } else if (obj instanceof RageTrap) {
                    initValue = ((RageTrap) obj).radius;
                    listener = () -> ((RageTrap) obj).radius = (int) radius.getValue();
                }

                radius = new StyledSpinner(new SpinnerIntegerModel(0, 100, initValue, 1, false, null) {
                    {
                        setAbsoluteMaximum(100f);
                    }
                }, Messages.get(EditMobComp.class, "radius"));
                radius.addChangeListener(listener);
                add(radius);

            }

            if (obj instanceof PitfallTrap) {
                    pitfallDelay = new StyledSpinner(new SpinnerIntegerModel(0, 100, ((PitfallTrap) obj).delay, 1, false, null),
                        Messages.get(EditMobComp.class, "delay"));
                pitfallDelay.addChangeListener(() -> ((PitfallTrap) obj).delay = (int) pitfallDelay.getValue());
                add(pitfallDelay);
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

        if (PixelScene.landscape()) {
            comps = new Component[]{
                    visible, active, disarmedByActivation,
                    pitfallDelay, radius, revealedWhenTriggered,
                    searchable, searchableByMagic, gatewayTelePos};
        } else {
            comps = new Component[]{
                    visible, active,
                    pitfallDelay, radius,
                    searchable, searchableByMagic,
                    revealedWhenTriggered, disarmedByActivation,
                    gatewayTelePos};
        }


    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
        if (summonMobs != null) layoutCompsLinear(summonMobs);
        if (randomTrap != null) layoutCompsLinear(randomTrap);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(obj.title());
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return obj.getSprite();
    }

    @Override
    protected void updateObj() {
        if (!obj.active && !obj.visible) {
            visible.checked(true);
            return;
        }

        if (trapItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(trapItem);
            if (slot != null) slot.item(trapItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Trap a, Trap b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;

        if (a.visible != b.visible) return false;
        if (a.active != b.active) return false;

        if (a.canBeSearched != b.canBeSearched) return false;
        if (a.revealedWhenTriggered != b.revealedWhenTriggered) return false;
        if (a.disarmedByActivation != b.disarmedByActivation) return false;

        if (a instanceof GatewayTrap) {
            if (((GatewayTrap) a).telePos != ((GatewayTrap) b).telePos) return false;
        }
        if (a instanceof PitfallTrap) {
            if (((PitfallTrap) a).radius != ((PitfallTrap) b).radius) return false;
            if (((PitfallTrap) a).delay != ((PitfallTrap) b).delay) return false;
        }
        if (a instanceof SummoningTrap) {
            if (!EditMobComp.isMobListEqual(((SummoningTrap) a).spawnMobs, ((SummoningTrap) b).spawnMobs)) return false;
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

                if (trap.telePos == -1) {
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_random"));
                } else {
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_pos", EditorUtilies.cellToString(trap.telePos)));
                }

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