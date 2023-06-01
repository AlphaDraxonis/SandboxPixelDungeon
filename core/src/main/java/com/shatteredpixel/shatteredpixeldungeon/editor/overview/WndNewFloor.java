package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.gerneral.FeelingSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextInput;

import java.io.IOException;

//a lot of code copied from WndTextInput because idk how to make TextInputs
public class WndNewFloor extends Window {


    private static final int MARGIN = 1;
    private static final int BUTTON_HEIGHT = 16;


    protected TextInput textBox;

    protected RedButton btnCopy;
    protected RedButton btnPaste;

    protected ChooseLevel chooseType, chooseTemplate;
    protected ChooseObjectComp seed;
    protected FeelingSpinner feelingSpinner;
    protected Spinner numInRegion, depth;
    protected IconButton infoNumInRegion, infoDepth;

    public WndNewFloor(CustomDungeon owner) {

        super(PixelScene.landscape() ? 215 : PixelScene.uiCamera.width - 5, 200);

        //need to offset to give space for the soft keyboard
        if (PixelScene.landscape()) {
            offset(0, -30);
        } else {
            offset(0, -30);
        }

        final RenderedTextBlock txtTitle = PixelScene.renderTextBlock("Create floor", 10);
        txtTitle.maxWidth(width);
        txtTitle.hardlight(Window.TITLE_COLOR);
        txtTitle.setPos((width - txtTitle.width()) / 2, 2);
        add(txtTitle);

        float pos = txtTitle.bottom() + 4 * MARGIN;

//        final RenderedTextBlock txtBody = PixelScene.renderTextBlock(
//                "Note that the Shattered Pixel Dungeon TextInput and its input event system is really bugged...", 3);
//        txtBody.maxWidth(width);
//        txtBody.setPos(0, pos);
//        add(txtBody);
//
//        pos = txtBody.bottom() + 2 * MARGIN;

        int textSize = (int) PixelScene.uiCamera.zoom * 9;
        textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, textSize) {
            @Override
            public void enterPressed() {
                if (!getText().isEmpty()) {
                    //triggers positive action on enter pressed, only with non-multiline though.
                    onSelect(true, getText());
                    hide();
                }
            }
        };
        textBox.setMaxLength(50);

        final float inputHeight = 16;

        float textBoxWidth = width - 3 * MARGIN - BUTTON_HEIGHT;

        add(textBox);
        textBox.setRect(MARGIN, pos, textBoxWidth, inputHeight);

        btnCopy = new RedButton("") {
            @Override
            protected void onPointerDown() {
                super.onPointerDown();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onClick() {
                super.onClick();
                textBox.copyToClipboard();
            }
        };
        btnCopy.icon(Icons.COPY.get());
        add(btnCopy);

        btnPaste = new RedButton("") {
            @Override
            protected void onPointerDown() {
                super.onPointerDown();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                PointerEvent.clearKeyboardThisPress = false;
            }

            @Override
            protected void onClick() {
                super.onClick();
                textBox.pasteFromClipboard();
            }

        };
        btnPaste.icon(Icons.PASTE.get());
        add(btnPaste);

        btnCopy.setRect(textBoxWidth + 2 * MARGIN, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        btnPaste.setRect(textBoxWidth + 2 * MARGIN, btnCopy.bottom() + MARGIN, BUTTON_HEIGHT, BUTTON_HEIGHT);

        pos += inputHeight + MARGIN;

        final RedButton positiveBtn = new RedButton("Create") {
            @Override
            protected void onClick() {
                if (!textBox.getText().isEmpty()) {
                    onSelect(true, textBox.getText());
                    hide();
                }
            }
        };

        final RedButton negativeBtn;
        if (false) {
            negativeBtn = new RedButton("Cancel") {
                @Override
                protected void onClick() {
                    onSelect(false, textBox.getText());
                    hide();
                }
            };
        } else {
            negativeBtn = null;
        }

        if (false) {
            positiveBtn.setRect(MARGIN, pos, (textBoxWidth - MARGIN) / 2, BUTTON_HEIGHT);
            add(positiveBtn);
            negativeBtn.setRect(positiveBtn.right() + MARGIN, pos, (textBoxWidth - MARGIN) / 2, BUTTON_HEIGHT);
            add(negativeBtn);
        } else {
            positiveBtn.setRect(MARGIN, pos, textBoxWidth, BUTTON_HEIGHT);
            add(positiveBtn);
        }

        pos += BUTTON_HEIGHT + MARGIN * 3;

        chooseType = new ChooseLevel("Level type:", false) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                chooseTemplate.enable(object == CustomLevel.class);
            }
        };
        add(chooseType);
        chooseType.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN;

        chooseTemplate = new ChooseLevel("Template:", true) {
            @Override
            protected float getDisplayWidth() {
                return chooseType.getDW();
            }
        };
        add(chooseTemplate);
        chooseTemplate.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN * 2;

        seed = new ChooseObjectComp("Seed:") {
            @Override
            protected void doChange() {
                Window window = new WndTextInput(Messages.get(HeroSelectScene.class, "custom_seed_title"),
                        "Enter seed for the template or generated level type",
                        seed.getObject() == null ? "" : seed.getObject().toString(),
                        20,
                        false,
                        Messages.get(HeroSelectScene.class, "custom_seed_set"),
                        Messages.get(HeroSelectScene.class, "custom_seed_clear")) {
                    @Override
                    public void onSelect(boolean positive, String text) {
                        text = DungeonSeed.formatText(text);
                        if (positive) {
                            if (DungeonSeed.convertFromText(text) != -1) seed.selectObject(text);
                            else seed.selectObject(null);
                        }
                    }
                };
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }

            @Override
            protected float getDisplayWidth() {
                return chooseType.getDW();
            }
        };
        add(seed);
        seed.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN * 2;

        feelingSpinner = new FeelingSpinner(null, 9, true);
        add(feelingSpinner);
        feelingSpinner.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos = feelingSpinner.bottom() + MARGIN * 2;


        infoNumInRegion = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                Window window = new WndTitledMessage(Icons.get(Icons.INFO), "NumInRegion",
                        "NumInRegion is used for the level generation. Together with the region of the template," +
                                "the depth for generation the level is calculated. This parameter influences generated things like rooms, mobs or items.");
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }
        };
        add(infoNumInRegion);
        infoNumInRegion.setRect(width - MARGIN - BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        PixelScene.align(infoNumInRegion);

        numInRegion = new Spinner(new SpinnerIntegerModel(1, 5, 1, 1, true, null) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.2f;// Should align with depth spinner!
            }

            @Override
            public int getClicksPerSecondWhileHolding() {
                return 5;
            }
        }, "NumInRegion:", 8);
        numInRegion.setButtonWidth(13);
        add(numInRegion);
        numInRegion.setRect(MARGIN, pos, infoNumInRegion.left() - MARGIN * 2, BUTTON_HEIGHT);
        PixelScene.align(numInRegion);
        pos += BUTTON_HEIGHT + MARGIN;

        infoDepth = new IconButton(Icons.get(Icons.INFO)) {
            @Override
            protected void onClick() {
                Window window = new WndTitledMessage(Icons.get(Icons.INFO), "Depth",
                        "The depth is used for the level difficulty. " +
                                "It does not change the level layout or other generated things, but is used for scaling mobs like piranhas.\n" +
                                "Note that in Custom PD by QuasiStellar, it does affect item generation.");
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }
        };
        add(infoDepth);
        infoDepth.setRect(width - MARGIN - BUTTON_HEIGHT, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
        PixelScene.align(infoDepth);

        depth = new DepthSpinner(1, 8) {
            @Override
            protected void onChange(int newDepth) {
            }
        };
        depth.setButtonWidth(13);
        add(depth);
        depth.setRect(MARGIN, pos, infoDepth.left() - MARGIN * 2, BUTTON_HEIGHT);
        PixelScene.align(depth);
        pos += BUTTON_HEIGHT;


        resize(width, (int) pos);

        textBox.setRect(MARGIN, textBox.top(), textBoxWidth, inputHeight);

        PointerEvent.clearKeyboardThisPress = false;

        chooseType.selectObject(CustomLevel.class);
        chooseTemplate.selectObject(null);
    }

    private class ChooseLevel extends ChooseObjectComp {
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
            if (object == null) return "None";
            if (object == CustomLevel.class) return "Custom";
            return super.objectToString(object);
        }
    }

    @Override
    public synchronized void update() {
        super.update();
        btnCopy.enable(!textBox.getText().isEmpty());
        btnPaste.enable(Gdx.app.getClipboard().hasContents());
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (textBox != null) {
            textBox.setRect(textBox.left(), textBox.top(), textBox.width(), textBox.height());
        }
    }

    public void onSelect(boolean positive, String name) {

        if (positive) {

            if (Dungeon.customDungeon.getFloor(name) != null || name.equals(Level.SURFACE) || name.equals(Level.NONE)) {
                WndNewDungeon.showNameWarnig();
                return;
            }

            Long seed;
            if (this.seed.getObject() == null) seed = null;
            else {
                seed = DungeonSeed.convertFromText((String) this.seed.getObject());
                if (seed == -1) seed = null;
            }
            LevelScheme levelScheme = new LevelScheme(name,
                    (Class<? extends Level>) chooseType.getObject(),
                    (Class<? extends Level>) chooseTemplate.getObject(),
                    seed, (Level.Feeling) feelingSpinner.getValue(), (int) numInRegion.getValue(), (int) depth.getValue());
            if (Dungeon.customDungeon.getNumFloors() == 0) Dungeon.customDungeon.setStart(name);
            Dungeon.customDungeon.addFloor(levelScheme);

            if (levelScheme.getType() == CustomLevel.class) {
                Dungeon.levelName = name;
                if (levelScheme.getLevel().width() == 0) levelScheme.getLevel().create();
                try {
                    CustomDungeonSaves.saveLevel(levelScheme.getLevel());
                } catch (IOException e) {
                    ShatteredPixelDungeon.reportException(e);
                }
//                FloorOverviewScene.updateList();
                EditorScene.open((CustomLevel) levelScheme.getLevel());
            } else {
                WndSwitchFloor.updateList();
                FloorOverviewScene.updateList();
            }
            try {
                CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
            } catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }

    }

}