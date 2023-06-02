package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;


import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;


public class Spinner extends Component {

    public static final int GAP = 5;
    public static final int FILL = -1;//Constant for SpinnerModel#getWidth(int height)
    public static final int SQUARE = -2;//Constant for buttonWidth to always have width = height
    public static final float ALIGNMENT_LEFT = 0, ALIGNMENT_CENTER = 0.5f, ALIGNMENT_RIGHT = 1;

    private RedButton rightButton, leftButton;
    private Component inputField;
    private RenderedTextBlock title;

    private SpinnerModel model;//Determitesinput size
    private Runnable modelListener;

    private float alignmentSpinnerX = ALIGNMENT_RIGHT;
    private float buttonWidth = SQUARE;


    public Spinner(SpinnerModel model, String name, int textSize) {
        super(model, name, textSize);
    }

    @Override
    protected void createChildren(Object... params) {
        model = (SpinnerModel) params[0];
        rightButton = new RedButton(">") {
            @Override
            protected void onClick() {
                setValue(getNextValue());
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return model.getClicksPerSecondWhileHolding();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                Spinner.this.onPointerUp();
            }
        };
        leftButton = new RedButton("<") {
            @Override
            protected void onClick() {
                setValue(getPreviousValue());
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return model.getClicksPerSecondWhileHolding();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                Spinner.this.onPointerUp();
            }
        };
        inputField = model.createInputField((int) params[2]);

        model.setValue(model.getValue());

        title = PixelScene.renderTextBlock((String) params[1], (int) params[2]);
        add(rightButton);
        add(leftButton);
        add(title);
        addToBack(inputField);
        layout();
    }

    @Override
    protected void layout() {

        int gap = title.text().isEmpty() ? 0 : GAP;

        height = Math.max(Math.max(title.height(), 10), height);

        float bw = getButtonWidth();
        if (bw == SQUARE) bw = height;
        else bw = Math.max(bw, 6);

        float txtWidth = model.getInputFieldWith(height);
        if (txtWidth == FILL) txtWidth = width - bw * 2 - 1 - title.width() - gap;

        title.setRect(x, y + (height - title.height()) / 2, title.width(), height);
        PixelScene.align(title);

        float conW = bw * 2 + txtWidth - 1;
        float startX = Math.max(width - title.width() - conW - gap, 0) * getAlignmentSpinnerX() + gap + title.right();

        leftButton.setRect(startX, y, bw, height);
        PixelScene.align(leftButton);
        inputField.setRect(leftButton.right() - 1, y, txtWidth, height);
        PixelScene.align(inputField);
        rightButton.setRect(inputField.right() - 1, y, bw, height);
        PixelScene.align(rightButton);

        width = Math.max(width, conW + title.width() + gap);
    }

    public void enable( boolean value ) {
        active = value;
        rightButton.enable(value);
        leftButton.enable(value);
        getModel().enable(value);
        title.alpha( value ? 1.0f : 0.3f );
    }

    protected void onPointerUp() {
    }

    public float getAlignmentSpinnerX() {
        return alignmentSpinnerX;
    }

    public void setAlignmentSpinnerX(float alignmentSpinnerX) {
        if (alignmentSpinnerX > 1 || alignmentSpinnerX < 0)
            throw new IllegalArgumentException("invalid alignment: " + alignmentSpinnerX);
        this.alignmentSpinnerX = alignmentSpinnerX;
        layout();
    }

    public float getButtonWidth() {
        return buttonWidth;
    }

    public void setButtonWidth(float buttonWidth) {
        this.buttonWidth = buttonWidth;
        layout();
    }


    public SpinnerModel getModel() {
        return model;
    }

    public Object getValue() {
        return getModel().getValue();
    }

    public void setValue(Object value) {
        getModel().setValue(value);
    }


    public Object getNextValue() {
        return getModel().getNextValue();
    }

    public Object getPreviousValue() {
        return getModel().getPreviousValue();
    }


    public void addChangeListener(Runnable listener) {
        model.addChangeListener(listener);
    }

    public void removeChangeListener(Runnable listener) {
        model.removeChangeListener(listener);
    }

    public Runnable[] getChangeListeners() {
        return model.getChangeListeners();
    }

    public static class SpinnerTextBlock extends Component {

        protected RenderedTextBlock textBlock;
        protected NinePatch bg;

        protected float textOffsetX, textOffsetY;

        public SpinnerTextBlock(NinePatch bg, int fontSize) {
            super();
            this.bg = bg;
            add(bg);

            textBlock = PixelScene.renderTextBlock(fontSize);
            addToFront(textBlock);
        }

        public void setText(String text) {
            textBlock.text(text);
        }

        public String getText() {
            return textBlock.text();
        }


        @Override
        protected void layout() {

            float contX = x;
            float contY = y;
            float contW = width;
            float contH = height;

            if (bg != null) {
                bg.x = x;
                bg.y = y;
                bg.size(width, height);

                contX += bg.marginLeft();
                contY += bg.marginTop();
                contW -= bg.marginHor();
                contH -= bg.marginVer();
            }

            float w = textBlock.width();
            float h = textBlock.height();
            textBlock.setRect((contW - w) / 2 + contX + textOffsetX, (contH - h) / 2 + contY + textOffsetY, w, h);
        }

        public void setTextOffsetX(float textOffsetX) {
            this.textOffsetX = textOffsetX;
        }

        public void setTextOffsetY(float textOffsetY) {
            this.textOffsetY = textOffsetY;
        }

        public  void enable(boolean value){
            textBlock.alpha( value ? 1.0f : 0.3f );
            textBlock.visible=value;
            bg.alpha(value ? 1.0f : 0.3f);
        }

    }


}