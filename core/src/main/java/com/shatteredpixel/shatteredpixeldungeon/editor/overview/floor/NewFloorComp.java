package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
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
    protected IconButton infoNumInRegion;
    protected Spinner numInRegion;

    public NewFloorComp(LevelScheme newLevelScheme) {
        super(newLevelScheme);
        chooseType.selectObject(CustomLevel.class);
        chooseTemplate.selectObject(null);
    }

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);
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
                newLevelScheme.setType((Class<? extends Level>) object);
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
                newLevelScheme.setDepth(newDepth);
            }
        };
        depth.setButtonWidth(13);
        newLevelScheme.setDepth((Integer) depth.getValue());
        add(depth);

        infoNumInRegion = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                Window window = new WndTitledMessage(Icons.get(Icons.INFO), Messages.get(WndNewFloor.class, "num_region"),
                        Messages.get(WndNewFloor.class, "num_region_info"));
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }
        };
        add(infoNumInRegion);
        numInRegion = new Spinner(new SpinnerIntegerModel(1, 5, newLevelScheme.getNumInRegion(), 1, true, null) {
            @Override
            public float getInputFieldWidth(float height) {
                return height * 1.3f;
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 5;
            }

            @Override
            public void displayInputAnyNumberDialog() {
                //do nothing
            }
        }, Messages.get(WndNewFloor.class, "num_region") + ":", 8);
        numInRegion.setButtonWidth(13);
        numInRegion.addChangeListener(()->newLevelScheme.setNumInRegion((Integer) numInRegion.getValue()));
        add(numInRegion);
    }

    @Override
    public void layout() {

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

        infoNumInRegion.setRect(width - MARGIN - BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        PixelScene.align(infoNumInRegion);
        numInRegion.setRect(MARGIN, pos, infoNumInRegion.left() - MARGIN * 2, BUTTON_HEIGHT);
        PixelScene.align(numInRegion);
        pos += BUTTON_HEIGHT + MARGIN * 3;

        textBox.setRect(MARGIN, textBox.top(), width, inputHeight);

        create.setRect(MARGIN, pos, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
        cancel.setRect(create.right() + MARGIN * 2, pos, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
        pos += BUTTON_HEIGHT + 1;

        PointerEvent.clearKeyboardThisPress = false;

    }

    @Override
    public String hoverText() {
        return Messages.get(WndNewFloor.class, "title");
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