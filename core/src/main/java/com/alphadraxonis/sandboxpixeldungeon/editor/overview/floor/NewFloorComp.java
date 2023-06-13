package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseObjectComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextInput;

public class NewFloorComp extends WndNewFloor.OwnTab {

    protected TextInput textBox;

    protected RedButton cancel, create;

    protected RenderedTextBlock title;
    protected ChooseLevel chooseType, chooseTemplate;
    protected Spinner depth;
    protected IconButton infoDepth;


    public NewFloorComp() {
        chooseType.selectObject(CustomLevel.class);
        chooseTemplate.selectObject(null);
    }

    @Override
    protected void createChildren(Object... params) {
        title = PixelScene.renderTextBlock(Messages.get(WndNewFloor.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        int textSize = (int) PixelScene.uiCamera.zoom * 9;
        textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, textSize) {
            @Override
            public void enterPressed() {
                if (!getText().isEmpty()) {
                    create(true);
                }
            }
        };
        textBox.setMaxLength(50);
        add(textBox);
        Game.platform.setOnscreenKeyboardVisible(false);

        create = new RedButton(Messages.get(WndNewFloor.class, "create_label")) {
            @Override
            protected void onClick() {
                if (!textBox.getText().isEmpty()) {
                    create(true);
                }
            }
        };
        add(create);
        cancel = new RedButton(Messages.get(WndNewFloor.class, "cancel_label")) {
            @Override
            protected void onClick() {
                create(false);
            }
        };
        add(cancel);

        chooseType = new ChooseLevel(Messages.get(WndNewFloor.class, "type"), false) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                chooseTemplate.enable(object == CustomLevel.class);
            }
        };
        add(chooseType);

        chooseTemplate = new ChooseLevel(Messages.get(WndNewFloor.class, "template"), true) {
            @Override
            protected float getDisplayWidth() {
                return chooseType.getDW();
            }
        };
        add(chooseTemplate);

        infoDepth = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                Window window = new WndTitledMessage(Icons.get(Icons.INFO), Messages.get(WndNewFloor.class, "depth"),
                        Messages.get(WndNewFloor.class, "depth_info"));
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }
        };
        add(infoDepth);
        depth = new DepthSpinner(1, 8) {
            @Override
            protected void onChange(int newDepth) {
            }
        };
        depth.setButtonWidth(13);
        add(depth);
    }

    @Override
    protected void layout() {

        title.maxWidth((int) width);
        title.setPos((width - title.width()) / 2, MARGIN * 2);

        float pos = title.bottom() + 4 * MARGIN;

        final float inputHeight = 16;

        textBox.setRect(MARGIN, pos, width, inputHeight);

        pos += inputHeight + MARGIN;

        chooseType.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN;

        chooseTemplate.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN * 2;

        infoDepth.setRect(width - MARGIN - BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        PixelScene.align(infoDepth);
        depth.setRect(MARGIN, pos, infoDepth.left() - MARGIN * 2, BUTTON_HEIGHT);
        PixelScene.align(depth);
        pos += BUTTON_HEIGHT + MARGIN * 3;
        textBox.setRect(MARGIN, textBox.top(), width, inputHeight);

        create.setRect(MARGIN, pos, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
        cancel.setRect(create.right() + MARGIN * 2, pos, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
        pos += BUTTON_HEIGHT + 1;

        PointerEvent.clearKeyboardThisPress = false;

    }

    protected void create(boolean positive) {
    }

    class ChooseLevel extends ChooseObjectComp {
        private final boolean flag;

        public ChooseLevel(String label, boolean flag) {
            super(label);
            this.flag = flag;
        }

        protected float getDW() {
            return display.width();
        }

        @Override
        protected void doChange() {
            textBox.active = false;
            Window window = new WndSelectLevelType(flag) {
                @Override
                protected void onSelect(Class<? extends Level> clazz) {
                    selectObject(clazz);
                }

                @Override
                public void hide() {
                    textBox.active = true;
                    super.hide();
                }
            };
            if (Game.scene() instanceof EditorScene) EditorScene.show(window);
            else Game.scene().addToFront(window);
        }

        @Override
        protected String objectToString(Object object) {
            if (object == null) return Messages.get(WndSelectLevelType.class, "type_none");
            if (object == CustomLevel.class)
                return Messages.get(WndSelectLevelType.class, "type_custom");
            return super.objectToString(object);
        }

    }
}