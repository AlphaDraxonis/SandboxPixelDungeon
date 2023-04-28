package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.TextInput;

public class TW2 extends Window {

    private static final int WIDTH = 135;
    private static final int W_LAND_EXTRA = 220; //extra width is sometimes used in landscape
    private static final int MARGIN = 1;
    private static final int BUTTON_HEIGHT = 16;

    protected TextInput textBox;

    public TW2() {
        super();

        final String title = "Title", body = "Body", initialValue = "val";
        final int maxLength = 5;
        final boolean multiLine = false;
        final String posTxt = "Ja", negTxt = "nein";


        //need to offset to give space for the soft keyboard
        if (PixelScene.landscape()) {
            offset(0, -45);
        } else {
            offset(0, multiLine ? -60 : -45);
        }

        final int width;
        if (PixelScene.landscape() && (multiLine || body != null)) {
            width = W_LAND_EXTRA; //more space for landscape users
        } else {
            width = WIDTH;
        }

        float pos = 2;

        int textSize = (int) PixelScene.uiCamera.zoom * (multiLine ? 6 : 9);
        textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), multiLine, textSize) {
            @Override
            public void enterPressed() {
                //triggers positive action on enter pressed, only with non-multiline though.
                onSelect(true, getText());
                hide();
            }
        };
        if (initialValue != null) textBox.setText(initialValue);
        textBox.setMaxLength(maxLength);

        //sets different height depending on whether this is a single or multi line input.
        final float inputHeight;
        if (multiLine) {
            inputHeight = 64; //~8 lines of text
        } else {
            inputHeight = 16;
        }

        float textBoxWidth = width - 3 * MARGIN - BUTTON_HEIGHT;

        add(textBox);
        textBox.setRect(MARGIN, pos, textBoxWidth, inputHeight);


        pos += inputHeight + MARGIN;

        pos += BUTTON_HEIGHT;

        //need to resize first before laying out the text box, as it depends on the window's camera
        resize(width, (int) pos);

        textBox.setRect(MARGIN, textBox.top(), textBoxWidth, inputHeight);

        PointerEvent.clearKeyboardThisPress = false;

    }

    @Override
    public synchronized void update() {
        super.update();
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (textBox != null) {
            textBox.setRect(textBox.left(), textBox.top(), textBox.width(), textBox.height());
        }
    }

    public void onSelect(boolean positive, String text) {
    }

    @Override
    public void onBackPressed() {
        //Do nothing, prevents accidentally losing writing
    }

}
