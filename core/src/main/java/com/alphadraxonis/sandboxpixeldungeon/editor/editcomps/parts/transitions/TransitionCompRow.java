package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.TileModify;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

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
            title.text(Messages.get(TransitionCompRow.class, (levelScheme.getLevel().map[cell] == Terrain.ENTRANCE ? "entrance" : "exit"))
                    + EditorUtilies.appendCellToString(cell, levelScheme.getLevel()));

        LevelTransition transition;
        if (cell < 0) {
            if (cell == CELL_DEFAULT_ENTRANCE)
                transition = levelScheme.getEntranceTransitionRegular();
            else transition = levelScheme.getExitTransitionRegular();
        } else transition = levelScheme.getLevel().transitions.get(cell);
        if (transition != null) {
            onAdd(transition, true);
            showBody(transition.showDetailsInEditor);
        }

        if (saveForUndo) tileModify = new TileModify(transition, cell);

        if(cell < 0)
            remover.visible = remover.active = adder.visible = adder.active = false;
    }

    @Override
    protected void onAddClick() {
        onAdd(EditTileComp.createNewTransition(cell), false);
    }

    @Override
    protected void onAdd(Object toAdd, boolean initialAdding) {
        super.onAdd(toAdd, initialAdding);

        if (!initialAdding && levelScheme.getLevel() == EditorScene.customLevel())
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
        if (cell < 0)
            terrainType = cell == CELL_DEFAULT_ENTRANCE ? Terrain.ENTRANCE : Terrain.EXIT;
        else terrainType = levelScheme.getLevel().map[cell];

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
                if (levelScheme.getLevel() == EditorScene.customLevel())
                    EditorScene.remove(transition);
            }
        });
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