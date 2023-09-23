package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ChangeMapSize extends Component {

    private Spinner topSpinner, rightSpinner, bottomSpinner, leftSpinner;
    RenderedTextBlock info;

    private final Component outsideSp;

    private int startTop, startBottom, startLeft, startRight;

    public ChangeMapSize(Runnable onClose) {

        outsideSp = new Component() {

            private RedButton cancel, save;

            @Override
            protected void createChildren(Object... params) {
                cancel = new RedButton(Messages.get(ChangeRegion.class, "cancel")) {
                    @Override
                    protected void onClick() {
                        onClose.run();
                    }
                };
                save = new RedButton(Messages.get(ChangeRegion.class, "close")) {
                    @Override
                    protected void onClick() {
                        save();
                        onClose.run();
                    }
                };
                add(cancel);
                add(save);
            }

            @Override
            protected void layout() {
                float w = width / 3f;
                cancel.setRect(x, y, w, GeneralTab.BUTTON_HEIGHT);
                save.setRect(cancel.right() + GeneralTab.GAP, y, width - w - GeneralTab.GAP, GeneralTab.BUTTON_HEIGHT);
                height = GeneralTab.BUTTON_HEIGHT;
            }
        };
    }

    @Override
    protected void createChildren(Object... params) {
        info = PixelScene.renderTextBlock(Messages.get(ChangeMapSize.class, "info"), 6);
        add(info);

        topSpinner = new Spinner(new OwnSpinnerModel(1, 60, startTop = (int) Math.ceil((EditorScene.customLevel().height() - 1) * 0.5f), 1, false, null), Messages.get(ChangeMapSize.class, "n") + " ", 9);
        add(topSpinner);
        bottomSpinner = new Spinner(new OwnSpinnerModel(1, 60, startBottom = (int) ((EditorScene.customLevel().height() - 1) * 0.5f), 1, false, null), Messages.get(ChangeMapSize.class, "s") + " ", 9);
        add(bottomSpinner);
        leftSpinner = new Spinner(new OwnSpinnerModel(1, 60, startLeft = (int) Math.ceil((EditorScene.customLevel().width() - 1) * 0.5f), 1, false, null), Messages.get(ChangeMapSize.class, "w") + " ", 9);
        add(leftSpinner);
        rightSpinner = new Spinner(new OwnSpinnerModel(1, 60, startRight = (int) ((EditorScene.customLevel().width() - 1) * 0.5f), 1, false, null), Messages.get(ChangeMapSize.class, "e") + " ", 9);
        add(rightSpinner);
    }

    @Override
    protected void layout() {

        info.maxWidth((int) width);
        info.setPos(x, y);

        topSpinner.setRect(x, y + info.bottom() + 4, width, 18);

        bottomSpinner.setRect(x, y + topSpinner.bottom() + 4, width, 18);

        leftSpinner.setRect(x, y + bottomSpinner.bottom() + 4, width, 18);

        rightSpinner.setRect(x, y + leftSpinner.bottom() + 4, width, 18);

        height = rightSpinner.bottom() + 1;
    }

    private void save() {
        int top = (int) topSpinner.getValue();
        int bottom = (int) bottomSpinner.getValue();
        int left = (int) leftSpinner.getValue();
        int right = (int) rightSpinner.getValue();
        if (top == startTop && bottom == startBottom && left == startLeft && right == startRight)
            return;

        int nW = left + right + 1;
        int nH = top + bottom + 1;
        CustomLevel.changeMapSize(EditorScene.customLevel(), nW, nH, top - startTop, left - startLeft);

        Undo.reset();
        SandboxPixelDungeon.switchNoFade(EditorScene.class);
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangeMapSize.class, "title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    private static class OwnSpinnerModel extends SpinnerIntegerModel {
        public OwnSpinnerModel(Integer minimum, Integer maximum, Integer value, int stepSize, boolean cycle, String showWhenNull) {
            super(minimum, maximum, value, stepSize, cycle, showWhenNull);
        }

        @Override
        public void displayInputAnyNumberDialog() {
            displayInputAnyNumberDialog(1, 60);
        }
    }
}