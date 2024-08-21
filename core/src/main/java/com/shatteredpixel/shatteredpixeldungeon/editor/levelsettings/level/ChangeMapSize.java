package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

@NotAllowedInLua
public class ChangeMapSize extends Component {

    private Spinner topSpinner, rightSpinner, bottomSpinner, leftSpinner;
    RenderedTextBlock info;

    private final Component outsideSp;

    private int startTop, startBottom, startLeft, startRight;

    public ChangeMapSize(Runnable onClose) {

        outsideSp = new Component() {

            private RedButton cancel, save;

            @Override
            protected void createChildren() {
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
                cancel.setRect(x, y, w, LevelTab.BUTTON_HEIGHT);
                save.setRect(cancel.right() + LevelTab.GAP, y, width - w - LevelTab.GAP, LevelTab.BUTTON_HEIGHT);
                height = LevelTab.BUTTON_HEIGHT;
            }
        };
    }

    @Override
    protected void createChildren() {
        info = PixelScene.renderTextBlock(Messages.get(ChangeMapSize.class, "info"), 6);
        add(info);

        topSpinner = new Spinner(new OwnSpinnerModel(1, 65, startTop = (int) Math.ceil((Dungeon.level.height() - 1) * 0.5f)), Messages.get(ChangeMapSize.class, "n") + " ", 9);
        add(topSpinner);
        bottomSpinner = new Spinner(new OwnSpinnerModel(1, 65, startBottom = (int) ((Dungeon.level.height() - 1) * 0.5f)), Messages.get(ChangeMapSize.class, "s") + " ", 9);
        add(bottomSpinner);
        leftSpinner = new Spinner(new OwnSpinnerModel(1, 65, startLeft = (int) Math.ceil((Dungeon.level.width() - 1) * 0.5f)), Messages.get(ChangeMapSize.class, "w") + " ", 9);
        add(leftSpinner);
        rightSpinner = new Spinner(new OwnSpinnerModel(1, 65, startRight = (int) ((Dungeon.level.width() - 1) * 0.5f)), Messages.get(ChangeMapSize.class, "e") + " ", 9);
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

        try {
            CustomLevel.changeMapSize(Dungeon.level, nW, nH, top - startTop, left - startLeft);
        } catch (Exception ex) {
            EditorScene.catchError(ex);
            return;
        }

        Undo.reset();
        WndEditorSettings.closingBecauseMapSizeChange = true;
        SandboxPixelDungeon.switchNoFade(EditorScene.class, new Game.SceneChangeCallback() {
            @Override
            public void beforeCreate() {
            }

            @Override
            public void afterCreate() {
                WndEditorSettings.closingBecauseMapSizeChange = false;
            }
        });
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
        public OwnSpinnerModel(Integer minimum, Integer maximum, Integer value) {
            super(minimum, maximum, value);

            setAbsoluteMinAndMax(1f, 60f);
        }
    }
}