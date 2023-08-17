package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Sign;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.SignEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.TileItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.WndItemDistribution;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.Consumer;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
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
                                true,Messages.get( WndItemDistribution.class,"save"),
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
        return new TransitionEditPart(transition, suggestion, terrainType != Terrain.ENTRANCE,
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

        float pos = height + WndTitledMessage.GAP * 2 - 1;

        if (transitionEdit != null) {
            transitionEdit.setRect(x, pos, width, -1);
            pos = transitionEdit.bottom() + WndTitledMessage.GAP;
        } else if (addTransition != null) {
            addTransition.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            pos = addTransition.bottom() + WndTitledMessage.GAP;
        } else if (editSignText != null) {
            editSignText.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            pos = editSignText.bottom() + WndTitledMessage.GAP;
        } else return;

        height = pos - y - WndTitledMessage.GAP - 0.5f;
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

        String desc;
        if (TileItem.isSignTerrainCell(obj.terrainType())) {
            Sign sign = level.signs.get(obj.cell());
            if (sign == null || sign.text == null) desc = "";
            else desc = sign.text;
        } else desc = level.tileDesc(obj.terrainType(), obj.cell());

        return desc.length() == 0 ? Messages.get(WndInfoCell.class, "nothing") : desc;
    }

    @Override
    public Image getIcon() {
        return createImage(obj.terrainType(), EditorScene.customLevel(), obj.image());
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


    private static Image createImage(int terrainFeature, Level level, int image) {
        if (terrainFeature == Terrain.WATER) {
            Image water = new Image(level.waterTex());
            water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            return water;
        } else {
            Image img = new Image(TextureCache.get(level.tilesTex()));
            img.frame(CustomLevel.getTextureFilm(EditorScene.customLevel().tilesTex()).get(image));
            return img;
        }
    }
}