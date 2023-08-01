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
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class WndChangeMapSize extends Window {

    private Spinner topSpinner, rightSpinner, bottomSpinner, leftSpinner;
    private RedButton cancel, save;

    private Component content;
    private ScrollPane scrollPane;

    private final int startTop, startBottom, startLeft, startRight;

    public WndChangeMapSize() {

        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(WndChangeMapSize.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);
        title.maxWidth(width);
        title.setPos((width - title.width()) * 0.5f, 3);

        content = new Component();

        RenderedTextBlock info = PixelScene.renderTextBlock(Messages.get(WndChangeMapSize.class, "info"), 6);
        content.add(info);
        info.maxWidth(width);
        info.setPos(0, 0);

        topSpinner = new Spinner(new SpinnerIntegerModel(1, 100, startTop = (int) Math.ceil((EditorScene.customLevel().height() - 3) * 0.5f), 1, false, null), Messages.get(WndChangeMapSize.class, "n") + " ", 9);
        content.add(topSpinner);
        topSpinner.setRect(0, info.bottom() + 4, width, 18);
        bottomSpinner = new Spinner(new SpinnerIntegerModel(1, 100, startBottom = (int) ((EditorScene.customLevel().height() - 3) * 0.5f), 1, false, null), Messages.get(WndChangeMapSize.class, "s") + " ", 9);
        content.add(bottomSpinner);
        bottomSpinner.setRect(0, topSpinner.bottom() + 4, width, 18);
        leftSpinner = new Spinner(new SpinnerIntegerModel(1, 100, startLeft = (int) Math.ceil((EditorScene.customLevel().width() - 3) * 0.5f), 1, false, null), Messages.get(WndChangeMapSize.class, "w") + " ", 9);
        content.add(leftSpinner);
        leftSpinner.setRect(0, bottomSpinner.bottom() + 4, width, 18);
        rightSpinner = new Spinner(new SpinnerIntegerModel(1, 100, startRight = (int) ((EditorScene.customLevel().width() - 3) * 0.5f), 1, false, null), Messages.get(WndChangeMapSize.class, "e") + " ", 9);
        content.add(rightSpinner);
        rightSpinner.setRect(0, leftSpinner.bottom() + 4, width, 18);


        cancel = new RedButton(Messages.get(WndChangeRegion.class, "cancel")) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        content.add(cancel);
        save = new RedButton(Messages.get(WndChangeRegion.class, "close")) {
            @Override
            protected void onClick() {
                save();
                hide();
            }
        };
        content.add(save);

        float posY = rightSpinner.bottom() + 5;
        float w = width / 3f;
        cancel.setRect(0, posY, w, GeneralTab.BUTTON_HEIGHT);
        save.setRect(cancel.right() + GeneralTab.GAP, posY, w * 2f - GeneralTab.GAP, GeneralTab.BUTTON_HEIGHT);

        scrollPane = new ScrollPane(content);
        add(scrollPane);

        int h = (int) Math.min((PixelScene.uiCamera.height * 0.8f), Math.ceil(title.height() + 10 + save.bottom()));
        resize(width, h);
        scrollPane.setRect(0, title.bottom() + 5, width, h - title.bottom() - 5);
    }

    private void save() {
        int top = (int) topSpinner.getValue();
        int bottom = (int) bottomSpinner.getValue();
        int left = (int) leftSpinner.getValue();
        int right = (int) rightSpinner.getValue();
        if (top == startTop && bottom == startBottom && left == startLeft && right == startRight)
            return;


        CustomLevel.increaseMapSize(EditorScene.customLevel(), left + right + 3, top + bottom + 3, top - startTop, left - startLeft);

        Undo.reset();
        SandboxPixelDungeon.switchNoFade(EditorScene.class);
//        Game.switchScene(EditorScene.class);
    }
}