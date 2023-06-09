package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class UndoPane extends Component {

    private Image bg;

    private UndoButton btnUndo;
    private RedoButton btnRedo;

    public static final int WIDTH = 48;//1.5 scale of menu pane


    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        bg = new Image(Assets.Interfaces.UNDO);
        add(bg);

        btnUndo = new UndoButton();
        add(btnUndo);

        btnRedo = new RedoButton();
        add(btnRedo);

        updateStates();
    }

    @Override
    protected void layout() {
        super.layout();

        bg.x = x;
        bg.y = y;

        btnUndo.setPos(x + 2, y + 2);
        PixelScene.align(btnUndo);

        btnRedo.setPos(btnUndo.right() + 2, y + 2);
        PixelScene.align(btnRedo);
    }

    public void updateStates() {
        btnUndo.enable(Undo.canUndo());
        btnRedo.enable(Undo.canRedo());
    }

    private static abstract class Btn extends Button {

        protected Image enabled, disabled;//Both need to have same size!

        public Btn() {
            width = enabled.width;
            height = disabled.height;
        }

        @Override
        protected void layout() {
            enabled.x = disabled.x = x;
            enabled.y = disabled.y = y;
            hotArea.x = x - 2;
            hotArea.y = y - 2;
            hotArea.width = width + 3;
            hotArea.height = height + 4;
        }

        @Override
        protected void onPointerDown() {
            if (enabled.visible) {
                enabled.brightness(1.1f);
                Sample.INSTANCE.play(Assets.Sounds.CLICK, 0.6f);
            }//else Sample.INSTANCE.play(Assets.Sounds.CLICK, 0.6f); TODO maybe play another sound?
        }

        @Override
        protected void onPointerUp() {
            enabled.resetColor();
            disabled.resetColor();
        }

        protected void enable(boolean value) {
            enabled.visible = value;
            disabled.visible = !value;
        }

        protected abstract void onClick();
    }

    private static class UndoButton extends Btn {


        @Override
        public GameAction keyAction() {
            return SPDAction.UNDO;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            enabled = new Image(Assets.Interfaces.UNDO_BTN, 2, 2, 20, 14);
            add(enabled);

            disabled = new Image(Assets.Interfaces.UNDO_BTN, 50, 2, 20, 14);
            add(disabled);
        }


        @Override
        protected void onClick() {
            if (Undo.canUndo()) Undo.undo();
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "undo"));
        }

    }

    private static class RedoButton extends Btn {

        @Override
        public GameAction keyAction() {
            return SPDAction.REDO;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            enabled = new Image(Assets.Interfaces.UNDO_BTN, 24, 2, 21, 14);
            add(enabled);

            disabled = new Image(Assets.Interfaces.UNDO_BTN, 72, 2, 21, 14);
            add(disabled);
        }

        @Override
        protected void onClick() {
            if (Undo.canRedo()) Undo.redo();
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "redo"));
        }

    }

}