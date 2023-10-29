package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
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

    public TransitionEditPart(LevelTransition transition, LevelScheme suggestion, boolean showEntrances, int targetDepth) {
        super();
        this.transition = transition;
        this.showEntrances = showEntrances;
        this.targetDepth = targetDepth;

        destLevel = new ChooseDestLevelComp(Messages.get(TransitionEditPart.class, "dest_level")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                if(object instanceof QuestLevels) transition.destBranch = ((QuestLevels) object).ID;
                else transition.destBranch = 0;
                updateTransition();
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                levels = super.filterLevels(levels);
                ArrayList<LevelSchemeLike> ret = new ArrayList<>(levels);
                for (LevelSchemeLike lvl : levels) {
                    if (lvl == LevelScheme.NO_LEVEL_SCHEME) continue;
                    if(lvl instanceof LevelScheme) {
                            int depth = ((LevelScheme) lvl).getDepth();
                        if (showEntrances) {
                            if (depth < targetDepth) ret.remove(lvl);
                        } else if (depth > targetDepth) ret.remove(lvl);
                    }
                }
                if (!showEntrances) ret.add(1, LevelScheme.SURFACE_LEVEL_SCHEME);
                if (showEntrances)  ret.add(1, QuestLevels.MINING);
                return ret;
            }
        };
        add(destLevel);

        destCell = new DestCellSpinner(new ArrayList<>(), -1);
        destCell.addChangeListener(() -> transition.destCell = (int) destCell.getValue());
        if (transition.destBranch == 0||true) destCell.setValue(transition.destCell);
        add(destCell);

        if (suggestion != null) {
            if (transition.destBranch == 0)
                destLevel.selectObject(suggestion);//already includes updateTransition()
            else destLevel.selectObject(QuestLevels.get(transition.destBranch));
        } else updateTransition();
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
        if (transition.destBranch != 0) {
            destCell.enable(false);
            EditorScene.updateTransitionIndicator(transition);
            return;
        }
        LevelScheme destL = (LevelScheme) destLevel.getObject();
        if (destL == null) {
            transition.destLevel = null;
            destCell.enable(false);
            destCell.setData(new ArrayList<>(2), -1, null);
            return;
        }
        String destN = EditorUtilies.getCodeName(destL);
        transition.destLevel = destN;
        if (destN == null || destN.isEmpty() || destN.equals(Level.SURFACE) || Dungeon.customDungeon.getFloor(destN) == null) {
            destCell.enable(false);
            destCell.setData(new ArrayList<>(2), -1, null);
        } else {
            destCell.enable(true);
            if (showEntrances) {
                destCell.setData(destL.entranceCells, destL.getSizeIfUnloaded().x, transition.destCell);
            } else
                destCell.setData(destL.exitCells, destL.getSizeIfUnloaded().x, transition.destCell);
        }
        EditorScene.updateTransitionIndicator(transition);
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (transition.destBranch != 0) {
            transition.type = LevelTransition.Type.BRANCH_EXIT;
            transition.destType = LevelTransition.Type.BRANCH_ENTRANCE;
            transition.destLevel = transition.departLevel;
            return;
        }
        if (transition.destLevel == null || transition.destLevel.equals("") || (transition.destCell == NONE && !transition.destLevel.equals(Level.SURFACE))) {
            deleteTransition(transition);
        } else {
            if (transition.destLevel.equals(Level.SURFACE))
                transition.type = LevelTransition.Type.SURFACE;
            else if (Dungeon.customDungeon.getFloor(transition.destLevel).getDepth() > targetDepth)
                transition.type = LevelTransition.Type.REGULAR_EXIT;
            else if (Dungeon.customDungeon.getFloor(transition.destLevel).getDepth() < targetDepth)
                transition.type = LevelTransition.Type.REGULAR_ENTRANCE;
            else {
                LevelScheme departLevel = Dungeon.customDungeon.getFloor(transition.departLevel);
                if (departLevel != null && departLevel.getType() == CustomLevel.class) {
                    if (departLevel.exitCells.contains(transition.departCell))
                        transition.type = LevelTransition.Type.REGULAR_EXIT;
                    else transition.type = LevelTransition.Type.REGULAR_ENTRANCE;
                } else transition.type = LevelTransition.Type.REGULAR_EXIT;
            }
        }
    }

    protected abstract void deleteTransition(LevelTransition transition);

    public static boolean areEqual(LevelTransition a, LevelTransition b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.destCell != b.destCell) return false;
        if (a.destBranch != b.destBranch) return false;
        return a.destLevel.equals(b.destLevel);
    }
}