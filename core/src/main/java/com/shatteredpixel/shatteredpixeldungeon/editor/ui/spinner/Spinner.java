package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;


import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;


public class Spinner extends Component {

    public static final int GAP = 0;
    public static final int FILL = -1;//Constant for SpinnerModel#getWidth(int height)
    public static final int SQUARE = -2;//Constant for buttonWidth to always have width = height
    public static final float ALIGNMENT_LEFT = 0, ALIGNMENT_CENTER = 0.5f, ALIGNMENT_RIGHT = 1;

    protected RedButton rightButton, leftButton;
    protected Component inputField;
    protected RenderedTextBlock label;

    private SpinnerModel model;//Determines input size

    private float alignmentSpinnerX = ALIGNMENT_RIGHT;
    private float buttonWidth = 12f;


    public Spinner(SpinnerModel model, String name, int textSize) {
        super();

        this.model = model;

        rightButton = new RedButton(">") {
            @Override
            protected void onClick() {
                setValue(getNextValue());
                if (!isClickHolding()) model.afterClick();
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return model.getClicksPerSecondWhileHolding();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                model.afterClick();
            }
        };
        leftButton = new RedButton("<") {
            @Override
            protected void onClick() {
                setValue(getPreviousValue());
                if (!isClickHolding()) model.afterClick();
            }

            @Override
            protected int getClicksPerSecondWhenHolding() {
                return model.getClicksPerSecondWhileHolding();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                model.afterClick();
            }
        };
        inputField = model.createInputField(textSize);

        model.setValue(model.getValue());

        label = PixelScene.renderTextBlock(name, textSize);
        add(rightButton);
        add(leftButton);
        add(label);
        addToBack(inputField);
        layout();
    }

    @Override
    protected void layout() {

        int gap = label.text().isEmpty() ? 0 : GAP;

        height = Math.max(Math.max(label.height(), 10), height);

        float bw = getButtonWidth();
        if (bw == SQUARE) bw = height;
        else bw = Math.max(bw, 6);

        float txtWidth = model.getInputFieldWidth(height);
        if (txtWidth == FILL) txtWidth = width - bw * 2 - 1 - label.width() - gap;

        label.setRect(x, y + (height - label.height()) / 2, label.width(), height);
        PixelScene.align(label);

        float conW = bw * 2 + txtWidth - 1;
        float startX = Math.max(width - label.width() - conW - gap, 0) * getAlignmentSpinnerX() + gap + label.right();

        leftButton.setRect(startX, y, bw, height);
        PixelScene.align(leftButton);
        inputField.setRect(leftButton.right() - 1, y, txtWidth, height);
        PixelScene.align(inputField);
        rightButton.setRect(inputField.right() - 1, y, bw, height);
        PixelScene.align(rightButton);

        width = Math.max(width, conW + label.width() + gap);
    }

    public void enable(boolean value) {
        active = value;
        rightButton.enable(value);
        leftButton.enable(value);
        getModel().enable(value);
        label.alpha(value ? 1.0f : 0.3f);
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

    public float getCurrentInputFieldWith(){
        return inputField.width();
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

    public static class SpinnerTextBlock extends Component implements AbstractSpinnerModel.ValueDisplay {

        protected RenderedTextBlock textBlock;
        protected NinePatch bg;

        protected float textOffsetX, textOffsetY;

        public SpinnerTextBlock(NinePatch bg, int fontSize) {
            super();
            this.bg = bg;
            add(bg);

            textBlock = PixelScene.renderTextBlock(fontSize);
            textBlock.setHighlighting(false);
            addToFront(textBlock);
        }

        @Override
        public void showValue(Object value) {
            textBlock.text(value == null ? "<null>" : value.toString());
            layout();
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
            float plusOffsetY = textBlock.text().equals(SpinnerIntegerModel.INFINITY) ? -1 : 0;
            textBlock.setRect((contW - w) / 2 + contX + textOffsetX, (contH - h) / 2 + contY + textOffsetY + plusOffsetY, w, h);
        }

        public void setTextOffsetX(float textOffsetX) {
            this.textOffsetX = textOffsetX;
        }

        public void setTextOffsetY(float textOffsetY) {
            this.textOffsetY = textOffsetY;
        }

        @Override
        public void enableValueField(boolean value) {
            textBlock.alpha(value ? 1.0f : 0.3f);
            textBlock.visible = value;
            bg.alpha(value ? 1.0f : 0.3f);
        }
    }


}