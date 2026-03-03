package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomGameObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EToolbar;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Traps;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItemDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PressurePlateTrap;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;

import java.util.List;

public class EditTrapComp extends DefaultEditComp<Trap> {


    protected StyledCheckBox visible, active;
    protected StyledCheckBox searchable, searchableByMagic, revealedWhenTriggered, disarmedByActivation;
    protected StyledButton gatewayTelePos;
    protected StyledButton pressurePlateTargetCell;
    protected StyledButton pressurePlatePlaceTerrain;
    protected Spinner radius, pitfallDelay;
    protected Spinner toxicVentStrength;
    protected ContainerWithLabel.ForMobs summonMobs;
    protected ItemContainerWithLabel<Item> summonItems;
    protected Component randomTrap;

    private final TrapItem trapItem;//used for linking the item with the sprite in the toolbar

    private Component[] rectComps, linearComps;

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

            randomTrap = new Component() {
                private RandomItemDistrComp distr = new RandomItemDistrComp((RandomItem<?>) obj) {
                    @Override
                    protected void updateParent() {
                        updateObj();
                    }

                    @Override
                    protected void showAddItemWnd(Consumer<Item> onSelect) {
                        EditorScene.selectItem(createSelector(onSelect));
                    }

                    @Override
                    protected WndBag.ItemSelector createSelector(Consumer<Item> onSelect) {
                        return createSelector(TrapItem.class, false, Traps.bag().getClass(), onSelect);
                    }
                };
                private Component outsideSp = distr.getOutsideSp();

                {
                    add(distr);
                    add(outsideSp);
                }

                @Override
                protected void layout() {
                    distr.setRect(x, y, width, 0);
                    outsideSp.setRect(x, distr.bottom() + 2, width, 6);
                    height = distr.height() + outsideSp.height() + 3;
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
            visible.enable(obj.active);
            add(visible);

            active = new StyledCheckBox(Messages.get(this, "active"));
            active.icon(EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.RING_ACCURACY));
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
            searchableByMagic.icon(EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_MAGICMAP));
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
            disarmedByActivation.icon(new ItemSprite(ItemSpriteSheet.STONE_DETECT));
            disarmedByActivation.checked(obj.disarmedByActivation);
            disarmedByActivation.addChangeListener(v -> {
                obj.disarmedByActivation = v;
                updateObj();
            });
            add(disarmedByActivation);


            if (obj instanceof GatewayTrap && obj.pos != -1) {
                int telePos = ((GatewayTrap) obj).telePos;
                gatewayTelePos = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, "") {
                    {
                        text.align(RenderedTextBlock.CENTER_ALIGN);
                    }
                    @Override
                    protected void onClick() {
                        EditorScene.hideWindowsTemporarily();
                        EditorScene.selectCell(gatewayTelePosListener);
                    }
                };
                if (telePos == -1) gatewayTelePos.text(Messages.get(this, "gateway_trap_random"));
                else gatewayTelePos.text(Messages.get(this, "gateway_trap_pos", EditorUtilities.cellToString(telePos)));
                Image teleIcon = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_TELEPORT);
                teleIcon.scale.set(12 / Math.max(teleIcon.width(), teleIcon.height()));
                gatewayTelePos.icon(teleIcon);
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

                radius = new StyledSpinner(new SpinnerIntegerModel(0, 100, initValue) {
                    {
                        setAbsoluteMaximum(100f);
                    }
                }, Messages.get(EditMobComp.class, "radius"));
                radius.addChangeListener(listener);
                add(radius);

            }

            if (obj instanceof PitfallTrap) {
                    pitfallDelay = new StyledSpinner(new SpinnerIntegerModel(0, 100, ((PitfallTrap) obj).delay),
                        Messages.get(EditMobComp.class, "delay"));
                pitfallDelay.addChangeListener(() -> ((PitfallTrap) obj).delay = (int) pitfallDelay.getValue());
                add(pitfallDelay);
            }
            
            if (obj instanceof ToxicGasRoom.ToxicVent) {
                toxicVentStrength = new StyledSpinner(new SpinnerIntegerModel(0, 1000, ((ToxicGasRoom.ToxicVent) obj).strength),
                        Messages.get(EditTrapComp.class, "vent_strength"));
                toxicVentStrength.addChangeListener(() -> {
                    ((ToxicGasRoom.ToxicVent) obj).strength = (int) toxicVentStrength.getValue();
                    if (obj.pos != -1) {
                        BlobActionPart.clearBlobAtCell(ToxicGasRoom.ToxicGasSeed.class, obj.pos);
                        BlobActionPart.place(obj.pos, ToxicGasRoom.ToxicGasSeed.class, ((ToxicGasRoom.ToxicVent) obj).strength);
                    }
                });
                add(toxicVentStrength);
            }

            if (obj instanceof SummoningTrap) {
                summonMobs = new ContainerWithLabel.ForMobs(((SummoningTrap) obj).spawnMobs, this, Messages.get(EditTrapComp.class, "summon_mobs"));
                add(summonMobs);
            }

            if (obj instanceof PressurePlateTrap) {
                summonMobs = new ContainerWithLabel.ForMobs(((PressurePlateTrap) obj).spawnMobs, this, Messages.get(EditTrapComp.class, "summon_mobs"));
                add(summonMobs);
                
                summonItems = new ItemContainerWithLabel<Item>(((PressurePlateTrap) obj).spawnItems, this, Messages.get(EditTrapComp.class, "drop_items")) {
                    @Override
                    protected void onSlotNumChange() {
                        if (summonItems != null) {
                            updateObj();
                        }
                    }
                };
                add(summonItems);
                
                pressurePlatePlaceTerrain = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "pressure_plate_changes_terrain")) {
                    {
                        text.align(RenderedTextBlock.CENTER_ALIGN);
                    }
                    
                    @Override
                    protected void onClick() {
                        //soll das terrain selected
                        EditorScene.selectItem(new WndBag.ItemSelector() {
                            @Override
                            public String textPrompt() {
                                return "";
                            }
                            
                            @Override
                            public Class<? extends Bag> preferredBag() {
                                return Tiles.bag.getClass();
                            }
                            
                            @Override
                            public List<Bag> getBags() {
                                return List.of(Tiles.bag);
                            }
                            
                            @Override
                            public boolean itemSelectable(Item item) {
                                return item instanceof TileItem;
                            }
                            
                            @Override
                            public void onSelect(Item item) {
                                if (item != null) {
                                    int terrain = item == EditorItem.NULL_ITEM ? -1 : ((TileItem) item).terrainType();
                                    ((PressurePlateTrap) obj).changeTerrain = terrain;
                                    Image image = terrain == -1 ? new ItemSprite(ItemSpriteSheet.NO_ITEM) : new TileSprite(terrain);
                                    image.scale.set(12 / Math.max(image.width(), image.height()));
                                    pressurePlatePlaceTerrain.icon(image);
                                }
                            }
                        });
                    }
                };
                Image image = ((PressurePlateTrap) obj).changeTerrain == -1 ? new ItemSprite(ItemSpriteSheet.NO_ITEM) : new TileSprite(((PressurePlateTrap) obj).changeTerrain);
                image.scale.set(12 / Math.max(image.width(), image.height()));
                pressurePlatePlaceTerrain.icon(image);
                add(pressurePlatePlaceTerrain);
                
                if (obj.pos != -1) {
                    int target = ((PressurePlateTrap) obj).targetCell;
                    pressurePlateTargetCell = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, "") {
                        {
                            text.align(RenderedTextBlock.CENTER_ALIGN);
                        }
                        
                        @Override
                        protected void onClick() {
                            EditorScene.hideWindowsTemporarily();
                            EditorScene.selectCell(pressurePlateTargetPosListener);
                        }
                    };
                    if (target == -1 || target == obj.pos) {
                        pressurePlateTargetCell.text((Messages.get(this, "pressure_plate_target", Messages.get(this, "pressure_plate_target_this_tile"))));
                    } else {
                        pressurePlateTargetCell.text(Messages.get(this, "pressure_plate_target", EditorUtilities.cellToString(target)));
                    }
                    TileSprite targetTileSprite = new TileSprite(target == -1 ? Terrain.EMPTY : Dungeon.level.map[target]);
                    targetTileSprite.scale.set(12 / Math.max(targetTileSprite.width(), targetTileSprite.height()));
                    pressurePlateTargetCell.icon(targetTileSprite);
                    add(pressurePlateTargetCell);
                }
            }
        }

        if (PixelScene.landscape()) {
            rectComps = new Component[] {
                    visible, active, disarmedByActivation,
                    pitfallDelay, radius, revealedWhenTriggered,
                    searchable, searchableByMagic, gatewayTelePos,
                    pressurePlateTargetCell, pressurePlatePlaceTerrain,
					toxicVentStrength};
        } else {
            rectComps = new Component[] {
                    visible, active,
                    pitfallDelay, radius,
                    searchable, searchableByMagic,
                    revealedWhenTriggered, disarmedByActivation,
                    gatewayTelePos, toxicVentStrength,
                    pressurePlateTargetCell, pressurePlatePlaceTerrain};
        }

        linearComps = new Component[] {
                summonItems, summonMobs, randomTrap
        };

        initializeCompsForCustomObjectClass();

    }

    @Override
    protected void updateStates() {
        super.updateStates();
        if (visible != null) visible.checked(obj.visible);
        if (active != null) active.checked(obj.active);
        if (disarmedByActivation != null) disarmedByActivation.checked(obj.disarmedByActivation);
        if (revealedWhenTriggered != null) revealedWhenTriggered.checked(obj.revealedWhenTriggered);
        if (searchable != null) searchable.checked(obj.canBeSearched);
        if (searchableByMagic != null) searchableByMagic.checked(obj.canBeSearchedByMagic);

        if (pitfallDelay != null) pitfallDelay.setValue(((PitfallTrap) obj).delay);
        if (toxicVentStrength != null) toxicVentStrength.setValue(((ToxicGasRoom.ToxicVent) obj).strength);
        if (radius != null) {
            if (obj instanceof PitfallTrap) radius.setValue(((PitfallTrap) obj).radius);
            else if (obj instanceof RageTrap) radius.setValue(((RageTrap) obj).radius);
        }
        if (gatewayTelePos != null) {
            int telePos = ((GatewayTrap) obj).telePos;
            if (telePos == -1) gatewayTelePos.text(Messages.get(this, "gateway_trap_random"));
            else gatewayTelePos.text(Messages.get(this, "gateway_trap_pos", EditorUtilities.cellToString(telePos)));
        }

        if (summonMobs != null) {
            if (obj instanceof SummoningTrap) summonMobs.updateState(((SummoningTrap) obj).spawnMobs);
            else summonMobs.updateState(((PressurePlateTrap) obj).spawnMobs);
        }
        if (summonItems != null) {
            summonItems.setItemList(((PressurePlateTrap) obj).spawnItems);
        }
        if (pressurePlateTargetCell != null) {
            int target = ((PressurePlateTrap) obj).targetCell;
            if (target == -1 || target == obj.pos) {
                pressurePlateTargetCell.text((Messages.get(this, "pressure_plate_target", Messages.get(this, "pressure_plate_target_this_tile"))));
            } else {
                pressurePlateTargetCell.text(Messages.get(this, "pressure_plate_target", EditorUtilities.cellToString(target)));
            }
            TileSprite targetTileSprite = new TileSprite(target == -1 ? Terrain.EMPTY : Dungeon.level.map[target]);
            targetTileSprite.scale.set(12 / Math.max(targetTileSprite.width(), targetTileSprite.height()));
            pressurePlateTargetCell.icon(targetTileSprite);
        }
    }

    @Override
    protected void onInheritStatsClicked(boolean flag, boolean initializing) {
        if (flag && !initializing) {
            obj.copyStats((Trap) CustomObjectManager.getLuaClass(((CustomGameObjectClass) obj).getIdentifier()));
        }

        for (Component c : rectComps) {
            if (c != null) c.visible = c.active = !flag;
        }

        for (Component c : linearComps) {
            if (c != null) c.visible = c.active = !flag;
        }

//        if (rename != null) rename.setVisible(!flag);

        ((CustomGameObjectClass) obj).setInheritStats(flag);
        
        super.onInheritStatsClicked(flag, initializing);
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(rectComps);
        layoutCompsLinear(linearComps);

        layoutCustomObjectEditor();
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
	public void updateObj() {
        if (!obj.active && !obj.visible) {
            visible.checked(true);
            return;
        }

        if (trapItem != null) {
            EToolbar.updateSlot(trapItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Trap a, Trap b) {
        if (a == b) return true;
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
        if (a instanceof ToxicGasRoom.ToxicVent) {
            if (((ToxicGasRoom.ToxicVent) a).strength != ((ToxicGasRoom.ToxicVent) b).strength) return false;
        }
        if (a instanceof SummoningTrap) {
            if (!EditMobComp.isMobListEqual(((SummoningTrap) a).spawnMobs, ((SummoningTrap) b).spawnMobs)) return false;
        }
        if (a instanceof PressurePlateTrap) {
            if (((PressurePlateTrap) a).targetCell != ((PressurePlateTrap) b).targetCell) return false;
            if (((PressurePlateTrap) a).changeTerrain != ((PressurePlateTrap) b).changeTerrain) return false;
            if (!EditItemComp.isItemListEqual(((PressurePlateTrap) a).spawnItems, ((PressurePlateTrap) b).spawnItems)) return false;
            if (!EditMobComp.isMobListEqual(((PressurePlateTrap) a).spawnMobs, ((PressurePlateTrap) b).spawnMobs)) return false;
        }
        if (a instanceof RandomItem) {
            if (!RandomItem.areEqual((RandomItem<?>) a, (RandomItem<?>) b)) return false;
        }
        
        if (a instanceof CustomGameObjectClass) {
            if (((CustomGameObjectClass) a).getInheritStats() != ((CustomGameObjectClass) b).getInheritStats()) return false;
        }
        return true;
    }


    private final CellSelector.Listener gatewayTelePosListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                boolean validDest = Dungeon.level.isPassable(cell) && !Dungeon.level.secret[cell] && Dungeon.level.findMob(cell) == null;
                GatewayTrap trap = (GatewayTrap) obj;

                if (!validDest) trap.telePos = -1;
                else trap.telePos = cell;

                if (trap.telePos == -1) {
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_random"));
                } else {
                    gatewayTelePos.text(Messages.get(EditTrapComp.class, "gateway_trap_pos", EditorUtilities.cellToString(trap.telePos)));
                }

                EditorScene.reshowWindows();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(EditTrapComp.class, "gateway_trap_prompt");
        }
    };


    private final CellSelector.Listener pressurePlateTargetPosListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            if (cell != null) {
                boolean validDest = Dungeon.level.insideMap(cell);
                PressurePlateTrap trap = (PressurePlateTrap) obj;

                if (!validDest) {
                    //no change
                    return;
                }
                trap.targetCell = cell;

                if (cell == -1 || cell == obj.pos) {
                    pressurePlateTargetCell.text((Messages.get(EditTrapComp.class, "pressure_plate_target", Messages.get(EditTrapComp.class, "pressure_plate_target_this_tile"))));
                } else {
                    pressurePlateTargetCell.text(Messages.get(EditTrapComp.class, "pressure_plate_target", EditorUtilities.cellToString(cell)));
                }

                EditorScene.reshowWindows();
            }
        }

        @Override
        public String prompt() {
            return Messages.get(EditTrapComp.class, "pressure_plate_target_prompt");
        }
    };
}
