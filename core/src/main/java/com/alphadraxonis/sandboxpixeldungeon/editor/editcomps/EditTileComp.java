package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.blobs.Blob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Sign;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.WellWaterSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.WndItemDistribution;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.SignEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.Consumer;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.tiles.CustomTilemap;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndInfoCell;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditTileComp extends DefaultEditComp<TileItem> {


    private TransitionEditPart transitionEdit;
    private RedButton addTransition;
    private RedButton editSignText;
    private WellWaterSpinner wellWaterSpinner;

    public EditTileComp(TileItem item) {
        super(item);

        if (item.cell() != -1) {
            if (item.terrainType() == Terrain.ENTRANCE || TileItem.isExitTerrainCell(item.terrainType())) {

                addTransition = new RedButton(Messages.get(EditTileComp.class, "add_transition"), 9) {
                    @Override
                    protected void onClick() {
                        addTransition(createNewTransition(item.cell()));
                    }
                };
                add(addTransition);

                if (EditorScene.customLevel().transitions.get(item.cell()) != null) {
                    addTransition(EditorScene.customLevel().transitions.get(item.cell()));
                }

            } else if (TileItem.isSignTerrainCell(item.terrainType())) {

                editSignText = new RedButton(Messages.get(EditTileComp.class, "edit_sign_title"), 9) {

                    @Override
                    protected void onClick() {
                        Sign sign = EditorScene.customLevel().signs.get(item.cell());
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
                                    Sign newSign = EditorScene.customLevel().signs.get(item.cell());
                                    if (newSign == null) {
                                        newSign = new Sign();
                                        newSign.pos = item.cell();
                                    }
                                    newSign.text = text;
                                    ActionPart actionPart = new SignEditPart.ActionPart(item.cell(), oldSign, newSign);
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
                wellWaterSpinner = new WellWaterSpinner(item.cell());
                wellWaterSpinner.addChangeListener(this::updateObj);
                add(wellWaterSpinner);
            }
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
        else suggestion = levelScheme.getChasm();
        if (transition.destLevel != null) suggestion = transition.destLevel;
        return new TransitionEditPart(transition, EditorUtilies.getLevelScheme(suggestion), terrainType != Terrain.ENTRANCE,
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

        float pos = y + height + WndTitledMessage.GAP * 2 - 1;

        if (transitionEdit != null) {
            transitionEdit.setRect(x, pos, width, -1);
            PixelScene.align(transitionEdit);
            pos = transitionEdit.bottom() + WndTitledMessage.GAP + 1;
        } else if (addTransition != null) {
            addTransition.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(addTransition);
            pos = addTransition.bottom() + WndTitledMessage.GAP + 1;
        } else if (editSignText != null) {
            editSignText.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(editSignText);
            pos = editSignText.bottom() + WndTitledMessage.GAP + 1;
        } else if (wellWaterSpinner != null) {
            wellWaterSpinner.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            PixelScene.align(wellWaterSpinner);
            pos = wellWaterSpinner.bottom() + WndTitledMessage.GAP + 1;
        } else return;

        height = (int) (pos - y - WndTitledMessage.GAP);
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

        String desc;
        if (TileItem.isSignTerrainCell(obj.terrainType())) {
            Sign sign = level.signs.get(obj.cell());
            if (sign == null || sign.text == null) desc = "";
            else desc = sign.text;
        } else {
            if (customTileWr != null) {
                String customDesc = customTileWr.customTilemap.desc(customTileWr.x, customTileWr.y);
                desc = customDesc != null ? customDesc + Dungeon.level.appendNoTransWarning(obj.cell()) : level.tileDesc(obj.terrainType(), obj.cell());
            } else desc = level.tileDesc(obj.terrainType(), obj.cell());
        }

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