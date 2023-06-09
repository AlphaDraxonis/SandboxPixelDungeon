package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTileComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.TileModify;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.features.LevelTransition;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class TransitionCompRow extends Component {

    public static final int CELL_DEFAULT_ENTRANCE = -3, CELL_DEFAULT_EXIT = -4;
    private final int cell;
    private final LevelScheme levelScheme;
    protected TransitionEditPart transitionEditPart;
    protected ColorBlock line;
    protected RenderedTextBlock title;
    protected IconButton remover, adder;
    protected IconButton expand, fold;

    private TileModify tileModify;//TODO maybe use a class specialiezed only for transitions?

    public TransitionCompRow(int cell, LevelScheme levelScheme, boolean saveForUndo) {
        super();
        this.cell = cell;
        this.levelScheme = levelScheme;

        line = new ColorBlock(1, 1, 0xFF222222);
        add(line);

        title = PixelScene.renderTextBlock(9);
        if (cell < 0)
            title.text(Messages.get(TransitionCompRow.class, cell == CELL_DEFAULT_ENTRANCE ? "entrance" : "exit"));
        else
            title.text(Messages.get(TransitionCompRow.class, (levelScheme.getLevel().map[cell] == Terrain.ENTRANCE ? "entrance" : "exit"))
                    + EditorUtilies.appendCellToString(cell, levelScheme.getLevel()));
        add(title);

        remover = new IconButton(Icons.get(Icons.CLOSE)) {
            @Override
            protected void onClick() {
                removeTransition();
            }
        };

        adder = new IconButton(Icons.get(Icons.PLUS)) {
            @Override
            protected void onClick() {
                addTransition(EditTileComp.createNewTransition(cell), true);
            }
        };

        expand = new IconButton(Icons.get(Icons.EXPAND)) {
            @Override
            protected void onClick() {
                expand();
            }
        };

        fold = new IconButton(Icons.get(Icons.FOLD)) {
            @Override
            protected void onClick() {
                fold();
            }
        };


        if (cell >= 0) {
            add(remover);
            add(adder);
            add(expand);
            add(fold);
        }

        LevelTransition transition;
        if (cell < 0) {
            if (cell == CELL_DEFAULT_ENTRANCE)
                transition = levelScheme.getEntranceTransitionRegular();
            else transition = levelScheme.getExitTransitionRegular();
        } else transition = levelScheme.getLevel().transitions.get(cell);
        if (transition != null) {
            addTransition(transition, false);
            showTransitionEdit(transition.showDetailsInEditor);
        } else {
            remover.visible = remover.active = false;
            fold.visible = fold.active = false;
            expand.visible = expand.active = false;
        }


        if (saveForUndo) tileModify = new TileModify(transition, cell);
    }

    private void addTransition(LevelTransition transition, boolean updateOthers) {

        int terrainType;
        if (cell < 0)
            terrainType = cell == CELL_DEFAULT_ENTRANCE ? Terrain.ENTRANCE : Terrain.EXIT;
        else terrainType = levelScheme.getLevel().map[cell];

        if (transitionEditPart != null) {
            remove(transitionEditPart);
            transitionEditPart.destroy();
        }

        transitionEditPart = EditTileComp.addTransition(terrainType, transition, levelScheme, t -> {
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
        add(transitionEditPart);

        adder.visible = adder.active = false;
        remover.visible = remover.active = true;
        fold.visible = fold.active = true;
        expand.visible = expand.active = false;

        if (updateOthers) {
            layoutParent();
            if (levelScheme.getLevel() == EditorScene.customLevel())
                EditorScene.add(transition);
        }
    }

    private void removeTransition() {

        transitionEditPart.destroy();
        remove(transitionEditPart);
        transitionEditPart = null;

        LevelTransition t = levelScheme.getLevel().transitions.get(cell);
        levelScheme.getLevel().transitions.remove(cell);
        EditorScene.remove(t);

        remover.visible = remover.active = false;
        adder.visible = adder.active = true;
        fold.visible = fold.active = false;
        expand.visible = expand.active = false;

        layoutParent();
    }

    //Warning: these methods layout the parent, so don't call them while layouting!
    public void expand() {
        showTransitionEdit(true);
        layoutParent();
    }

    public void fold() {
        showTransitionEdit(false);
        layoutParent();
    }

    private void showTransitionEdit(boolean flag) {
        fold.visible = fold.active = flag;
        expand.visible = expand.active = !flag;
        transitionEditPart.visible = transitionEditPart.active = flag;
        transitionEditPart.transition.showDetailsInEditor = flag;
    }

    private static final int BUTTON_HEIGHT = 13;

    @Override
    protected void layout() {

        float pos = y;
        title.maxWidth((int) (width - (remover.visible || adder.visible ? BUTTON_HEIGHT + 3 : 0)));
        title.setPos(x, (BUTTON_HEIGHT - title.height()) * 0.5f + pos + 1);

        if (remover.visible)
            remover.setRect(width - BUTTON_HEIGHT - 3, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        else if (adder.visible)
            adder.setRect(width - BUTTON_HEIGHT - 3, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);

        if (fold.visible)
            fold.setRect(width - BUTTON_HEIGHT * 2 - 4, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        else if (expand.visible)
            expand.setRect(width - BUTTON_HEIGHT * 2 - 4, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);

        pos += BUTTON_HEIGHT + 2;

        if (transitionEditPart != null && transitionEditPart.visible) {
            transitionEditPart.setRect(x, pos, width, -1);
            pos = transitionEditPart.bottom();
        }
        height = pos - y + 1;

        line.size(width, 1);
        line.x = x;
        line.y = y + height;

    }

    protected void layoutParent() {
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if(tileModify != null){
            tileModify.finish();
            System.err.println(tileModify.hasContent());
            Undo.addActionPart(tileModify);
        }
    }
}