package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class UndoPane extends Component {

    private Image bg;

    private Button btnUndo, btnRedo;

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

    private static class UndoButton extends Button {

        private Image enabled, disabled;//Need to have same size!

        public UndoButton() {
            super();

            width = enabled.width;
            height = disabled.height;

            enable(true);
        }

//        @Override
//        public GameAction keyAction() {
//            return SPDAction.JOURNAL;
//        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            enabled = new Image(Assets.Interfaces.UNDO_BTN, 2, 2, 20, 14);
            add(enabled);

            disabled = new Image(Assets.Interfaces.UNDO_BTN, 50, 2, 20, 14);
            add(disabled);
        }

        @Override
        protected void layout() {
            super.layout();

            enabled.x = disabled.x = x;
            enabled.y = disabled.y = y;
        }

        @Override
        protected void onPointerDown() {
            if (enabled.visible) enabled.brightness(1.3f);
            else disabled.brightness(1.3f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
        }

        @Override
        protected void onPointerUp() {
            enabled.resetColor();
            disabled.resetColor();
        }

        @Override
        protected void onClick() {
            enable(!enabled.visible);
        }

        //        @Override
//        protected String hoverText() {
//            return Messages.titleCase(Messages.get(WndKeyBindings.class, "journal"));
//        }
        private void enable(boolean value) {
            enabled.visible = value;
            disabled.visible = !value;
        }

    }

    private static class RedoButton extends Button {

        private Image enabled, disabled;//Need to have same size!

        public RedoButton() {
            super();

            width = enabled.width;
            height = disabled.height;

            enable(true);
        }

//        @Override
//        public GameAction keyAction() {
//            return SPDAction.JOURNAL;
//        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            enabled = new Image(Assets.Interfaces.UNDO_BTN, 24, 2, 21, 14);
            add(enabled);

            disabled = new Image(Assets.Interfaces.UNDO_BTN, 72, 2, 21, 14);
            add(disabled);
        }

        @Override
        protected void layout() {
            super.layout();

            enabled.x = disabled.x = x;
            enabled.y = disabled.y = y;
        }

        @Override
        protected void onPointerDown() {
            if (enabled.visible) enabled.brightness(1.2f);
            else disabled.brightness(0.8f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
        }

        @Override
        protected void onPointerUp() {
            enabled.resetColor();
            disabled.resetColor();
        }

        @Override
        protected void onClick() {
            enable(!enabled.visible);
        }

        //        @Override
//        protected String hoverText() {
//            return Messages.titleCase(Messages.get(WndKeyBindings.class, "journal"));
//        }
        private void enable(boolean value) {
            enabled.visible = value;
            disabled.visible = !value;
        }

    }

}