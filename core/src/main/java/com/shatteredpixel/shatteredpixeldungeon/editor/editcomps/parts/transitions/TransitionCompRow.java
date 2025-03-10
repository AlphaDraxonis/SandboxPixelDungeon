package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.TileModify;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoCell;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ui.Component;

@NotAllowedInLua
public class TransitionCompRow extends FoldableCompWithAdd {

    public static final int CELL_DEFAULT_ENTRANCE = -3, CELL_DEFAULT_EXIT = -4;
    private final int cell;
    private final LevelScheme levelScheme;

    private TileModify tileModify;//TODO maybe use a class specialized only for transitions?

    public TransitionCompRow(int cell, LevelScheme levelScheme, boolean saveForUndo) {

        this.cell = cell;
        this.levelScheme = levelScheme;

        if (cell < 0)
            title.text(Messages.get(TransitionCompRow.class, cell == CELL_DEFAULT_ENTRANCE ? "entrance" : "exit"));
        else
            title.text(Messages.get(TransitionCompRow.class, (TileItem.isEntranceTerrainCell(levelScheme.getLevel().map[cell]) ? "entrance" : "exit"))
                    + EditorUtilities.appendCellToString(cell, levelScheme.getLevel()));

        LevelTransition transition;
        if (cell < 0) {
			transition = cell == CELL_DEFAULT_ENTRANCE
                    ? levelScheme.getEntranceTransitionRegular()
                    : levelScheme.getExitTransitionRegular();
        } else {
            transition = levelScheme.getLevel().transitions.get(cell);
        }
        if (cell < 0 || levelScheme.getLevel() != Dungeon.level || levelScheme.getLevel() == null) {
            icon = cell == CELL_DEFAULT_ENTRANCE
                    ? new TileSprite(Level.tilesTex(levelScheme), Terrain.ENTRANCE)
                    : new TileSprite(Level.tilesTex(levelScheme), Terrain.EXIT);
        } else {
            icon = WndInfoCell.cellImage(levelScheme.getLevel(), cell);
        }
        
        if (icon != null) {
            icon.scale.set(BUTTON_HEIGHT / icon.height());
            add(icon);
        }
        if (transition != null) {
            onAdd(transition, false);
            showBody(transition.showDetailsInEditor);
        } else {
            expandAndFold.setVisible(false);
        }

        if (saveForUndo) tileModify = new TileModify(transition, cell);

        if (cell < 0) {
            remover.setVisible(false);
            adder.setVisible(false);
        }
    }

    @Override
    protected void onAddClick() {
        onAdd(EditTileComp.createNewTransition(cell), true);
    }

    @Override
    protected void onAdd(Object toAdd, boolean layoutParent) {
        super.onAdd(toAdd, layoutParent);

        if (layoutParent && levelScheme.getLevel() == Dungeon.level)
            EditorScene.add((LevelTransition) toAdd);
    }

    @Override
    protected void onRemove() {

        super.onRemove();

        LevelTransition t = levelScheme.getLevel().transitions.get(cell);
        levelScheme.getLevel().transitions.remove(cell);
        EditorScene.remove(t);
    }

    @Override
    protected void showBody(boolean flag) {
        super.showBody(flag);
        ((TransitionEditPart) body).transition.showDetailsInEditor = flag;
    }

    @Override
    protected Component createBody(Object param) {

        int terrainType;
        if (cell < 0) {
            terrainType = cell == CELL_DEFAULT_ENTRANCE ? Terrain.ENTRANCE : Terrain.EXIT;
        } else {
            terrainType = levelScheme.getLevel().map[cell];
        }

        LevelTransition transition = (LevelTransition) param;

        return EditTileComp.addTransition(terrainType, transition, levelScheme, t -> {
            if (cell < 0) {
                if (cell == CELL_DEFAULT_ENTRANCE) {
                    t.destLevel = Level.SURFACE;
                } else {
                    t.destLevel = null;
                }
                t.destCell = -1;
            } else {
                levelScheme.getLevel().transitions.remove(transition.cell());
                if (levelScheme.getLevel() == Dungeon.level)
                    EditorScene.remove(transition);
            }
        }, this::layoutParent);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (tileModify != null && !WndEditorSettings.closingBecauseMapSizeChange) {
            tileModify.finish();
            Undo.addActionPart(tileModify);
        }
    }
}
