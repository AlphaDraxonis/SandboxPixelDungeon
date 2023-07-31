package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class TransitionEditPart extends Component {


    public static final int NONE = -2, DEFAULT = -1;

    protected final LevelTransition transition;
    protected ChooseDestLevelComp destLevel;
    protected DestCellSpinner destCell;

    protected boolean showEntrances; //else show exits,  showEntrances is true when departCell is exit

    private final int targetDepth;

    public TransitionEditPart(LevelTransition transition, String suggestion, boolean showEntrances, int targetDepth) {
        super();
        this.transition = transition;
        this.showEntrances = showEntrances;
        this.targetDepth = targetDepth;

        destLevel = new ChooseDestLevelComp(Messages.get(TransitionEditPart.class, "dest_level")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                updateTransition();
            }

            @Override
            protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                levels = super.filterLevels(levels);
                ArrayList<LevelScheme> ret = new ArrayList<>(levels);
                for (LevelScheme lvl : levels) {
                    if (lvl == LevelScheme.NO_LEVEL_SCHEME) continue;
                    if (showEntrances) {
                        if (lvl.getDepth() < targetDepth) ret.remove(lvl);
                    } else if (lvl.getDepth() > targetDepth) ret.remove(lvl);
                }
                if (!showEntrances) ret.add(1, LevelScheme.SURFACE_LEVEL_SCHEME);
                return ret;
            }
        };
        add(destLevel);

        destCell = new DestCellSpinner(new ArrayList<>());
        destCell.addChangeListener(() -> transition.destCell = (int) destCell.getValue());
        destCell.setValue(transition.destCell);
        add(destCell);

        if (suggestion != null && !suggestion.isEmpty())
            destLevel.selectObject(suggestion);//already includes updateTransition()
        else updateTransition();
    }

    @Override
    protected void layout() {

        float pos = y;

        destLevel.setRect(x, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = destLevel.bottom() + 2;

        destCell.setRect(x, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = destCell.bottom() + 2;

        height = pos - y - 2;
    }

    protected void updateTransition() {
        String destL = (String) destLevel.getObject();
        transition.destLevel = destL;
        if (destL == null || destL.isEmpty() || destL.equals(Level.SURFACE) || Dungeon.customDungeon.getFloor(destL) == null) {
            destCell.enable(false);
            destCell.setData(new ArrayList<>(2), null);
        } else {
            destCell.enable(true);
//            List<Integer> data = new ArrayList<>(Dungeon.customDungeon.getFloor(destL).entranceCells);
//            data.addAll(Dungeon.customDungeon.getFloor(destL).exitCells);
//            destCell.setData(data);
            if (showEntrances) {
                destCell.setData(Dungeon.customDungeon.getFloor(destL).entranceCells, transition.destCell);
            } else
                destCell.setData(Dungeon.customDungeon.getFloor(destL).exitCells, transition.destCell);
        }
        EditorScene.updateTransitionIndicator(transition);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (transition.destLevel == null || transition.destLevel.equals("") || (transition.destCell == NONE && !transition.destLevel.equals(Level.SURFACE))) {
            deleteTransition(transition);
        } else {
            if (transition.destLevel.equals(Level.SURFACE))
                transition.type = LevelTransition.Type.SURFACE;
            else if (Dungeon.customDungeon.getFloor(transition.destLevel).getDepth() >= targetDepth)
                transition.type = LevelTransition.Type.REGULAR_EXIT;
            else transition.type = LevelTransition.Type.REGULAR_ENTRANCE;
        }
    }

    protected abstract void deleteTransition(LevelTransition transition);

    public static boolean areEqual(LevelTransition a, LevelTransition b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.destCell != b.destCell) return false;
        return a.destLevel.equals(b.destLevel);
    }
}