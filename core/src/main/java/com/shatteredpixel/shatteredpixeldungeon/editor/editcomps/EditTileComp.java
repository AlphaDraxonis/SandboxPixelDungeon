package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.editor.CoinDoor;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.WellWaterSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ParticleItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomTerrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.DefaultListItemWithRemoveBtn;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.SignActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.RitualSiteRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

public class EditTileComp extends DefaultEditComp<TileItem> {


    private TransitionEditPart transitionEdit;
    private RedButton addTransition;
    private RedButton editSignText;
    private StyledCheckBox signBurnOnRead;
    private WellWaterSpinner wellWaterSpinner;
    private EditBlobComp.VolumeSpinner volumeSpinner;
    private EditBlobComp.SacrificialFirePrize sacrificialFirePrize;
    private Spinner coinDoorCost;

    private List<ScrollingListPane.ListItem> customParticles;

    //for custom tiles
    private ContainerWithLabel.ForMobs summonMobs;

    public EditTileComp(TileItem item) {
        super(item);

        final int cell = item.cell();
        if (cell != -1) {
            if (TileItem.isEntranceTerrainCell(item.terrainType()) || TileItem.isExitTerrainCell(item.terrainType())) {

                addTransition = new RedButton(Messages.get(EditTileComp.class, "add_transition"), 9) {
                    @Override
                    protected void onClick() {
                        addTransition(createNewTransition(cell));
                    }
                };
                add(addTransition);

                if (Dungeon.level.transitions.get(cell) != null) {
                    addTransition(Dungeon.level.transitions.get(cell));
                }

            } else if (TileItem.isSignTerrainCell(item.terrainType())) {

                Sign sign = Dungeon.level.signs.get(cell);

                if (sign != null) {
                    signBurnOnRead = new StyledCheckBox(Messages.get(EditTileComp.class, "burn_sign_on_read"));
                    signBurnOnRead.icon(BlobItem.createIcon(MagicalFireRoom.EternalFire.class));
                    signBurnOnRead.checked(sign.burnOnRead);
                    signBurnOnRead.addChangeListener(v -> sign.burnOnRead = v);
                    add(signBurnOnRead);
                }

                editSignText = new RedButton(Messages.get(EditTileComp.class, "edit_sign_title"), 9) {

                    private Sign oldSign;

                    {
                        if (sign != null) oldSign = sign.getCopy();
                        else oldSign = null;
                    }

                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndTextInput(
                                Messages.get(EditTileComp.class, "edit_sign_title"),
                                Messages.get(EditTileComp.class, "edit_sign_body"),
                                (oldSign == null || oldSign.text == null ? "" : oldSign.text), 30000,
                                true, Messages.get(WndItemDistribution.class, "save"),
                                Messages.get(WndNewFloor.class, "cancel_label")) {
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive) {
                                    Sign newSign = Dungeon.level.signs.get(cell);
                                    if (newSign == null) {
                                        newSign = new Sign();
                                        newSign.pos = item.cell();
                                    }
                                    newSign.text = text;
                                    ActionPart actionPart = new SignActionPart.ActionPart(cell, oldSign, newSign);
                                    if (actionPart.hasContent()) {
                                        Undo.startAction();//this is maybe not so good, better if using TileModify?
                                        Undo.addActionPart(actionPart);
                                        Undo.endAction();
                                        actionPart.redo();
                                        updateObj();
                                    }
                                    oldSign = newSign.getCopy();
                                }
                            }
                        });
                    }
                };
                add(editSignText);
            } else if (item.terrainType() == Terrain.WELL) {
                wellWaterSpinner = new WellWaterSpinner(cell);
                wellWaterSpinner.addChangeListener(this::updateObj);
                add(wellWaterSpinner);
            }

            //TODO fix this if more blobs have attributes
            SacrificialFire blobAtCell = null;
            for (int i = 0; i < BlobActionPart.BlobData.BLOB_CLASSES.length; i++) {
                Blob b = Dungeon.level.blobs.getOnly(BlobActionPart.BlobData.BLOB_CLASSES[i]);
                if (b != null && !(b instanceof WellWater) && b.cur != null && b.cur[cell] > 0) {
                    if (b instanceof SacrificialFire) {
                        blobAtCell = (SacrificialFire) b;
                        break;
                    }
                }
            }

            if (blobAtCell != null && blobAtCell instanceof SacrificialFire) {
                final Blob finalBlobAtCell = blobAtCell;
                volumeSpinner = new EditBlobComp.VolumeSpinner(finalBlobAtCell.cur[cell]);
                volumeSpinner.addChangeListener(() -> {
                    int old = finalBlobAtCell.cur[cell];
                    finalBlobAtCell.cur[cell] = (int) volumeSpinner.getValue();
                    finalBlobAtCell.volume += finalBlobAtCell.cur[cell] - old;
                });
                add(volumeSpinner);
            }

            if (blobAtCell instanceof SacrificialFire) {
                SacrificialFire sacrificialFire = (SacrificialFire) blobAtCell;
                sacrificialFirePrize = new EditBlobComp.SacrificialFirePrize(sacrificialFire.getPrize(cell)) {
                    @Override
                    public void setSelectedItem(Item selectedItem) {
                        super.setSelectedItem(selectedItem);
                        sacrificialFire.setPrize(cell, selectedItem);
                    }
                };
                add(sacrificialFirePrize);
            }


            customParticles = new ArrayList<>(4);
            for (CustomParticle particle : Dungeon.level.particles.values()) {
                if (particle != null && particle.cur != null && particle.cur[cell] > 0) {
                    EditorItem<?> particleItem = new FindInBag(particle).getAsInBag();
                    if (particleItem != null) {
                        ScrollingListPane.ListItem listItem = new DefaultListItemWithRemoveBtn(
                                particleItem, null, particleItem.name(), particleItem.getSprite()) {
                            @Override
                            protected void onRemove() {
                                Undo.startAction();
                                Undo.addActionPart(ParticleItem.remove(cell));
                                Undo.endAction();
                                customParticles.remove(this);
                                remove();
                                destroy();
                                killAndErase();
                                updateObj();
                            }
                        };
                        add(listItem);
                        customParticles.add(listItem);
                    }
                }
            }

            CustomTilemapAndPosWrapper customTileWrapper = findCustomTile();
            if (customTileWrapper != null) {
                CustomTilemap customTile = customTileWrapper.customTilemap;
                if (customTile instanceof RitualSiteRoom.RitualMarker) {
                    RitualSiteRoom.RitualMarker marker = (RitualSiteRoom.RitualMarker) customTile;
                    summonMobs = new ContainerWithLabel.ForMobs(marker.summons, this, EditMobComp.label("summon_mob"));
                    add(summonMobs);
                }
            }

        }

        if (item.terrainType() == Terrain.COIN_DOOR) {
            coinDoorCost = new Spinner(new SpinnerIntegerModel(1, 100_000, Dungeon.level.getCoinDoorCost(cell), 10, false) {
                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 150;
                }
            }, Messages.get(this, "coin_door_cost"), 9);
            coinDoorCost.addChangeListener(()-> {
                if (cell == -1) CoinDoor.costInInventory = (int) coinDoorCost.getValue();
                else Dungeon.level.setCoinDoorCost(cell, (int) coinDoorCost.getValue());
                updateObj();
            });
            add(coinDoorCost);
        }

    }

    public static LevelTransition createNewTransition(int cell) {
        LevelTransition transition = new LevelTransition(Dungeon.level, cell, TransitionEditPart.DEFAULT, null);
        Dungeon.level.transitions.put(cell, transition);
        EditorScene.add(transition);
        return transition;
    }

    private void addTransition(LevelTransition transition) {
        transitionEdit = addTransition(obj.terrainType(), transition, Dungeon.level.levelScheme, t -> {
            Dungeon.level.transitions.remove(transition.cell());
            EditorScene.remove(transition);
        }, this::updateObj);
        add(transitionEdit);
        addTransition.setVisible(false);

        layout();
        updateObj();//for resize
    }

    public static TransitionEditPart addTransition(int terrainType, LevelTransition transition,
                                                   LevelScheme levelScheme, Consumer<LevelTransition> deleteTransition, Runnable updateParent) {
        String suggestion;
        if (TileItem.isEntranceTerrainCell(terrainType))
            suggestion = levelScheme.getDefaultAbove();
        else {
            suggestion = levelScheme.getChasm();
            if (suggestion == null) suggestion = levelScheme.getDefaultBelow();
        }
        if (transition.destLevel != null) suggestion = transition.destLevel;
        return new TransitionEditPart(transition, EditorUtilities.getLevelScheme(suggestion), terrainType == -12345 ? null : !TileItem.isEntranceTerrainCell(terrainType),
                levelScheme.getDepth()) {
            @Override
            protected void deleteTransition(LevelTransition transition) {
                deleteTransition.accept(transition);
            }
            
            @Override
            protected void layoutParent() {
                updateParent.run();
            }
        };
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(signBurnOnRead);
        layoutCompsLinear(//transitionEdit is later instantiated
                transitionEdit, addTransition, editSignText, wellWaterSpinner, volumeSpinner, sacrificialFirePrize, summonMobs, coinDoorCost
        );
        if (customParticles != null && !customParticles.isEmpty()) {
            layoutCompsLinear(customParticles.toArray(EditorUtilities.EMPTY_COMP_ARRAY));
        }
    }

    @Override
    protected void updateStates() {
        super.updateStates();

        if (summonMobs != null) summonMobs.updateState(((RitualSiteRoom.RitualMarker) obj.getObject()).summons);
    }

    protected static final class CustomTilemapAndPosWrapper {
        private final int x, y;
        private final CustomTilemap customTilemap;

        public CustomTilemapAndPosWrapper(int x, int y, CustomTilemap customTile) {
            this.x = x;
            this.y = y;
            this.customTilemap = customTile;
        }
    }

    protected CustomTilemapAndPosWrapper findCustomTile() {
        int x, y;
        if (obj.cell() != -1) {
            x = obj.cell() % Dungeon.level.width();
            y = obj.cell() / Dungeon.level.width();
            for (CustomTilemap i : Dungeon.level.customTiles) {
                if ((x >= i.tileX && x < i.tileX + i.tileW) &&
                        (y >= i.tileY && y < i.tileY + i.tileH)) {
                    if (i.image(x - i.tileX, y - i.tileY) != null) {
                        x -= i.tileX;
                        y -= i.tileY;
                        return new CustomTilemapAndPosWrapper(x, y, i);
                    }
                }
            }
            for (CustomTilemap i : Dungeon.level.customWalls) {
                if ((x >= i.tileX && x < i.tileX + i.tileW) &&
                        (y >= i.tileY && y < i.tileY + i.tileH)) {
                    if (i.image(x - i.tileX, y - i.tileY) != null) {
                        x -= i.tileX;
                        y -= i.tileY;
                        return new CustomTilemapAndPosWrapper(x, y, i);
                    }
                }
            }
        }
        return null;
    }


    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    protected String createTitleText() {
        return Messages.titleCase(TileItem.getName(obj.terrainType(), obj.cell()));
    }

    @Override
    protected String createDescription() {
        Level level = Dungeon.level;

        CustomTilemapAndPosWrapper customTileWr = findCustomTile();

        String desc = null;
        if (TileItem.isSignTerrainCell(obj.terrainType())) {
            Sign sign = level.signs.get(obj.cell());
            if (sign == null || sign.text == null) desc = "";
            else desc = sign.text;
        }
        if (customTileWr != null) {

            if (desc != null && !"".equals(desc)) desc += "\n\n";//If we have sign text
            else desc = "";
            String customDesc = customTileWr.customTilemap.desc(customTileWr.x, customTileWr.y);
            if (customDesc != null) desc += customDesc;
            else desc += level.tileDesc(obj.terrainType(), obj.cell());

            String terrainName = customTileWr.customTilemap instanceof CustomTerrain
                    ? Messages.get(EditCustomTileComp.class, "custom_terrain")
                    : TileItem.getName(obj.terrainType(), -1);
            desc += "\n" + Messages.get(EditCustomTileComp.class, "terrain") + ": " + terrainName;
            if (TileItem.isExitTerrainCell(obj.terrainType()) || TileItem.isEntranceTerrainCell(obj.terrainType()))
                desc += Dungeon.level.appendNoTransWarning(obj.cell());

        } else {
            if (desc == null) desc = level.tileDesc(obj.terrainType(), obj.cell());
        }

        //TODO make own statistic page
        if (obj.terrainType() == Terrain.LOCKED_DOOR || obj.terrainType() == Terrain.SECRET_LOCKED_DOOR)
            desc = EditorUtilities.addIronKeyDescription(desc, level);
        else if (obj.terrainType() == Terrain.CRYSTAL_DOOR || obj.terrainType() == Terrain.SECRET_CRYSTAL_DOOR)
            desc = EditorUtilities.addCrystalKeyDescription(desc, level);
        else if (obj.terrainType() == Terrain.LOCKED_EXIT) desc = EditorUtilities.addSkeletonKeyDescription(desc, level);
        else if (obj.terrainType() == Terrain.COIN_DOOR) desc = EditorUtilities.addCoinDoorDescription(desc, level);

        if (obj.cell() >= 0) {
            for (Blob blob : Dungeon.level.blobs.values()) {
                if (blob != null && blob.volume > 0 && blob.cur[obj.cell()] > 0 && blob.tileDesc() != null) {
                    if (desc.length() > 0) desc += "\n\n";
                    desc += blob.tileDesc();
                }
            }
        }

        return desc.length() == 0 ? Messages.get(WndInfoCell.class, "nothing") : desc;
    }

    @Override
    public Image getIcon() {
        return createImage(obj.terrainType(), Dungeon.level, obj.image(), obj.cell());
    }

    private static Image createImage(int terrainFeature, Level level, int image, int cell) {

        Image customImage = null;
        if (cell != -1) {
            int x = cell % Dungeon.level.width();
            int y = cell / Dungeon.level.width();
            for (CustomTilemap i : Dungeon.level.customTiles) {
                if ((x >= i.tileX && x < i.tileX + i.tileW) &&
                        (y >= i.tileY && y < i.tileY + i.tileH)) {
                    if ((customImage = i.image(x - i.tileX, y - i.tileY)) != null) {
                        break;
                    }
                }
            }
        }

        if (customImage != null) {
            return customImage;
        } else {
            if (terrainFeature == Terrain.WATER) {
                Image water = new Image(Dungeon.level.waterTex());
                water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
                return water;
            } else {
                Image img = new Image(TextureCache.get(level.tilesTex()));
                img.frame(CustomLevel.getTextureFilm(Dungeon.level.tilesTex()).get(image));
                return img;
            }
        }
    }
}