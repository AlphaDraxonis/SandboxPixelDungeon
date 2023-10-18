package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions;

import static com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart.DEFAULT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart.NONE;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.List;

public class DestCellSpinner extends Spinner {

    public DestCellSpinner(List<Integer> cells, int levelWidth) {
        super(new DestCellModel(cells, levelWidth), Messages.get(TransitionEditPart.class, "dest_cell"), 8);
        setButtonWidth(13);
    }

    public void setData(List<Integer> cells, int levelWidth, Integer select) {
        ((DestCellModel) getModel()).setData(cells, levelWidth, select);
    }

    private static class DestCellModel extends SpinnerTextModel {
        private int levelWidth;

        public DestCellModel(List<Integer> cells, int levelWidth) {
            super(true, new Object[]{NONE});
            setData(cells, levelWidth, null);
        }

        public void setData(List<Integer> cells, int levelWidth, Integer select) {
            if (levelWidth != -1) this.levelWidth = levelWidth;
            cells = new ArrayList<>(cells);
            if (cells.isEmpty()) cells.add(NONE);
            Object[] data = cells.toArray();
            setData(data);
            if (cells.contains(DEFAULT)) setValue(DEFAULT);
            else {
                if (select == null || !cells.contains(select)) {
                    setValue(data[0]);
                } else setValue(select);
            }
        }

        @Override
        protected String getAsString(Object value) {
            int val = (int) value;
            if (val == NONE) return Messages.get(DestCellSpinner.class, "none");
            if (val == DEFAULT) return Messages.get(DestCellSpinner.class, "default");
            if (levelWidth <= 0) return "ERROR";
            return EditorUtilies.cellToString(val, levelWidth);
        }
    }
}