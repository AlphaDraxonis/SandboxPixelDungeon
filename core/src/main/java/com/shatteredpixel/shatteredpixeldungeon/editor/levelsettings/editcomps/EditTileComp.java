package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.function.Consumer;

public class EditTileComp extends DefaultEditComp<TileItem> {


    private TransitionEditPart transitionEdit;
    private RedButton addTransition;

    public EditTileComp(TileItem item) {
        super(item);

        if (item.cell() != -1 && (item.terrainType() == Terrain.ENTRANCE || TileItem.isExitTerrainCell(item.terrainType()))) {

            addTransition = new RedButton("Add transition", 9) {
                @Override
                protected void onClick() {
                    addTransition(createNewTransition(item.cell()));
                }
            };
            add(addTransition);

            if (EditorScene.customLevel().transitions.get(item.cell()) != null) {
                addTransition(EditorScene.customLevel().transitions.get(item.cell()));
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
        transitionEdit = addTransition(item.terrainType(), transition, EditorScene.customLevel().levelScheme, t -> {
            EditorScene.customLevel().transitions.remove(transition.cell());
            EditorScene.remove(transition);
        });
        add(transitionEdit);
        addTransition.visible = false;
//        addTransition.active=false;
        layout();
        updateItem();//for resize
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
        }

        if (addTransition != null && addTransition.visible) {
            addTransition.setRect(x, pos, width, WndMenuEditor.BTN_HEIGHT);
            pos = addTransition.bottom() + WndTitledMessage.GAP;
        }

        height = pos - y - WndTitledMessage.GAP + 1;
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    protected String createTitleText() {
        String posString;
        if (item.cell() == -1) posString = "";
        else posString = " " + new Koord(item.cell());
        return Messages.titleCase(EditorScene.customLevel().tileName(item.terrainType())) + posString;
    }

    @Override
    protected String createDescription() {
        CustomLevel level = EditorScene.customLevel();
        String desc = level.tileDesc(item.terrainType());
        return desc.length() == 0 ? Messages.get(WndInfoCell.class, "nothing") : desc;
    }

    @Override
    public Image getIcon() {
        return createImage(item.terrainType(), EditorScene.customLevel(), item.image());
    }

    @Override
    protected void updateItem() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());
        super.updateItem();
    }


    private static Image createImage(int terrainFeature, Level level, int image) {
        if (terrainFeature == Terrain.WATER) {
            Image water = new Image(level.waterTex());
            water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            return water;
        } else {
            Image img = new Image(TextureCache.get(level.tilesTex()));
            img.frame(EditorScene.customLevel().getTextureFilm().get(image));
            return img;
        }
    }
}