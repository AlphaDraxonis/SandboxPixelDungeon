package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.WellWaterSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomTerrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.SignEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditTileComp extends DefaultEditComp<TileItem> {


    private TransitionEditPart transitionEdit;
    private RedButton addTransition;
    private RedButton editSignText;
    private WellWaterSpinner wellWaterSpinner;
    private EditBlobComp.VolumeSpinner volumeSpinner;
    private EditBlobComp.SacrificialFirePrize sacrificialFirePrize;

    public EditTileComp(TileItem item) {
        super(item);

        final int cell = item.cell();
        if (cell != -1) {
            if (item.terrainType() == Terrain.ENTRANCE || TileItem.isExitTerrainCell(item.terrainType())) {

                addTransition = new RedButton(Messages.get(EditTileComp.class, "add_transition"), 9) {
                    @Override
                    protected void onClick() {
                        addTransition(createNewTransition(cell));
                    }
                };
                add(addTransition);

                if (EditorScene.customLevel().transitions.get(cell) != null) {
                    addTransition(EditorScene.customLevel().transitions.get(cell));
                }

            } else if (TileItem.isSignTerrainCell(item.terrainType())) {

                editSignText = new RedButton(Messages.get(EditTileComp.class, "edit_sign_title"), 9) {

                    @Override
                    protected void onClick() {
                        Sign sign = EditorScene.customLevel().signs.get(cell);
                        final Sign oldSign;
                        if (sign != null) oldSign = sign.getCopy();
                        else oldSign = null;
                        EditorScene.show(new WndTextInput(
                                Messages.get(EditTileComp.class, "edit_sign_title"),
                                Messages.get(EditTileComp.class, "edit_sign_body"),
                                (oldSign == null || oldSign.text == null ? "" : oldSign.text), 30000,
                                true, Messages.get(WndItemDistribution.class, "save"),
                                Messages.get(WndNewFloor.class, "cancel_label")) {
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive) {
                                    Sign newSign = EditorScene.customLevel().signs.get(cell);
                                    if (newSign == null) {
                                        newSign = new Sign();
                                        newSign.pos = item.cell();
                                    }
                                    newSign.text = text;
                                    ActionPart actionPart = new SignEditPart.ActionPart(cell, oldSign, newSign);
                                    if (actionPart.hasContent()) {
                                        Undo.startAction();//this is maybe not so good, better if using TileModify?
                                        Undo.addActionPart(actionPart);
                                        Undo.endAction();
                                        actionPart.redo();
                                        updateObj();
                                    }
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
            for (int i = 0; i < BlobEditPart.BlobData.BLOB_CLASSES.length; i++) {
                Blob b = Dungeon.level.blobs.getOnly(BlobEditPart.BlobData.BLOB_CLASSES[i]);
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
            } else volumeSpinner = null;

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
            } else sacrificialFirePrize = null;

        }

    }

    public static LevelTransition createNewTransition(int cell) {
        LevelTransition transition = new LevelTransition(EditorScene.customLevel(), cell, TransitionEditPart.DEFAULT, null);
        EditorScene.customLevel().transitions.put(cell, transition);
        EditorScene.add(transition);
        return transition;
    }

    private void addTransition(LevelTransition transition) {
        transitionEdit = addTransition(obj.terrainType(), transition, EditorScene.customLevel().levelScheme, t -> {
            EditorScene.customLevel().transitions.remove(transition.cell());
            EditorScene.remove(transition);
        });
        add(transitionEdit);
        addTransition.visible = addTransition.active = false;
//        addTransition.active=false;
        layout();
        updateObj();//for resize
    }

    public static TransitionEditPart addTransition(int terrainType, LevelTransition transition,
                                                   LevelScheme levelScheme, Consumer<LevelTransition> deleteTransition) {
        String suggestion;
        if (terrainType == Terrain.ENTRANCE)
            suggestion = levelScheme.getDefaultAbove();
        else {
            suggestion = levelScheme.getChasm();
            if (suggestion == null) suggestion = levelScheme.getDefaultBelow();
        }
        if (transition.destLevel != null) suggestion = transition.destLevel;
        return new TransitionEditPart(transition, EditorUtilies.getLevelScheme(suggestion), terrainType == -12345 ? null : terrainType != Terrain.ENTRANCE,
                levelScheme.getDepth()) {
            @Override
            protected void deleteTransition(LevelTransition transition) {
                deleteTransition.accept(transition);
            }
        };
    }

    @Override
    protected void layout() {
        super.layout();
        Component[] comps = {//transitionEdit is later instantiated
                transitionEdit, addTransition, editSignText, wellWaterSpinner, volumeSpinner, sacrificialFirePrize
        };
        layoutCompsLinear(comps);
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
        return TileItem.getName(obj.terrainType(), obj.cell());
    }

    @Override
    protected String createDescription() {
        CustomLevel level = EditorScene.customLevel();

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
            if (TileItem.isExitTerrainCell(obj.terrainType()) || obj.terrainType() == Terrain.ENTRANCE)
                desc += Dungeon.level.appendNoTransWarning(obj.cell());

        } else desc = level.tileDesc(obj.terrainType(), obj.cell());

        //TODO make own statistic page
        if (obj.terrainType() == Terrain.LOCKED_DOOR) desc = EditorUtilies.addIronKeyDescription(desc, level);
        else if (obj.terrainType() == Terrain.CRYSTAL_DOOR) desc = EditorUtilies.addCrystalKeyDescription(desc, level);
        else if (obj.terrainType() == Terrain.LOCKED_EXIT) desc = EditorUtilies.addSkeletonKeyDescription(desc, level);

        if (obj.cell() >= 0) {
            for (Blob blob : Dungeon.level.blobs.values()) {
                if (blob.volume > 0 && blob.cur[obj.cell()] > 0 && blob.tileDesc() != null) {
                    if (desc.length() > 0) desc += "\n\n";
                    desc += blob.tileDesc();
                }
            }
        }

        return desc.length() == 0 ? Messages.get(WndInfoCell.class, "nothing") : desc;
    }

    @Override
    public Image getIcon() {
        return createImage(obj.terrainType(), EditorScene.customLevel(), obj.image(), obj.cell());
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());
        super.updateObj();
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
                img.frame(CustomLevel.getTextureFilm(EditorScene.customLevel().tilesTex()).get(image));
                return img;
            }
        }
    }
}