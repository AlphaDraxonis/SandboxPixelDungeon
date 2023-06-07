package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions;

import static com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart.DEFAULT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart.NONE;

import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.List;

class DestCellSpinner extends Spinner {

    public DestCellSpinner(List<Integer> cells) {
        super(new DestCellModel(cells), Messages.get(TransitionEditPart.class, "dest_cell"), 8);
        setButtonWidth(13);
    }

    public void setData(List<Integer> cells, Integer select) {
        ((DestCellModel) getModel()).setData(cells, select);
    }

    private static class DestCellModel extends SpinnerTextModel {

        public DestCellModel(List<Integer> cells) {
            super(true, new Object[]{NONE});
            setData(cells, null);
        }

        public void setData(List<Integer> cells, Integer select) {
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
            return EditorUtilies.cellToString(val);
        }
    }
}