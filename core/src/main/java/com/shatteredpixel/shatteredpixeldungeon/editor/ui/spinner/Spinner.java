package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

//inspired by javax.swing.JSpi/**

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

/**
 * A single line input field that lets the user select a
 * number or an object value from an ordered sequence. Spinners typically
 * provide a pair of tiny arrow buttons for stepping through the elements
 * of the sequence. The keyboard up/down arrow keys also cycle through the
 * elements. The user may also be allowed to type a (legal) value directly
 * into the spinner. Although combo boxes provide similar functionality,
 * spinners are sometimes preferred because they don't require a drop down list
 * that can obscure important data.
 * <p>
 * A <code>JSpinner</code>'s sequence value is defined by its
 * <code>SpinnerModel</code>.
 * The <code>model</code> can be specified as a constructor argument and
 * changed with the <code>model</code> property.  <code>SpinnerModel</code>
 * classes for some common types are provided: <code>SpinnerListModel</code>,
 * <code>SpinnerNumberModel</code>, and <code>SpinnerDateModel</code>.
 * <p>
 * A <code>JSpinner</code> has a single child component that's
 * responsible for displaying
 * and potentially changing the current element or <i>value</i> of
 * the model, which is called the <code>editor</code>.  The editor is created
 * by the <code>JSpinner</code>'s constructor and can be changed with the
 * <code>editor</code> property.  The <code>JSpinner</code>'s editor stays
 * in sync with the model by listening for <code>ChangeEvent</code>s. If the
 * user has changed the value displayed by the <code>editor</code> it is
 * possible for the <code>model</code>'s value to differ from that of
 * the <code>editor</code>. To make sure the <code>model</code> has the same
 * value as the editor use the <code>commitEdit</code> method, eg:
 * <pre>
 *   try {
 *       spinner.commitEdit();
 *   }
 *   catch (ParseException pe) {
 *       // Edited value is invalid, spinner.getValue() will return
 *       // the last valid value, you could revert the spinner to show that:
 *       JComponent editor = spinner.getEditor();
 *       if (editor instanceof DefaultEditor) {
 *           ((DefaultEditor)editor).getTextField().setValue(spinner.getValue());
 *       }
 *       // reset the value to some known value:
 *       spinner.setValue(fallbackValue);
 *       // or treat the last valid value as the current, in which
 *       // case you don't need to do anything.
 *   }
 *   return spinner.getValue();
 * </pre>
 * <p>
 * For information and examples of using spinner see
 * <a href="https://docs.oracle.com/javase/tutorial/uiswing/components/spinner.html">How to Use Spinners</a>,
 * a section in <em>The Java Tutorial.</em>
 * <p>
 * <strong>Warning:</strong> Swing is not thread safe. For more
 * information see <a
 * href="package-summary.html#threading">Swing's Threading
 * Policy</a>.
 *
 * @author Hans Muller
 * @see SpinnerModel
 * @see AbstractSpinnerModel
 * @see SpinnerListModel
 * @see SpinnerNumberModel
 * @since 1.4
 */


public class Spinner extends Component {

    public static final int GAP = 6;
    public static final int FILL = -1;//Constant for SpinnerModel#getWidth(int height)

    private RedButton rightButton, leftButton;
    private Component inputField;
    private RenderedTextBlock title;

    private SpinnerModel model;//Determitesinput size
    private Runnable modelListener;


    public Spinner(SpinnerModel model, String name, int nameSize) {
        super(model, name, nameSize);
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

        height = Math.max(Math.max(title.height(), 10), height);

        float txtWidth = model.getInputFieldWith(height);
        if (txtWidth == FILL) txtWidth = width - height * 2 + 2 - title.width() - GAP;


        //Allignment tilte left
        title.setRect(x, y + (height - title.height()) / 2, title.width(), height);

        //Alignment spinner center (alsomove buttons to midle by 1)
//        float startX = Math.max(width - title.width() - height * 2 - txtWidth + 2, 0) / 2 + x + title.width();

        //Alignment spinner right
        float startX = Math.max(width - height * 2 - txtWidth + 2, 0) + x;
        if (startX + GAP < title.width()+x) startX = GAP + title.width()+x;

        leftButton.setRect(startX + 0, y, height, height);
        inputField.setRect(leftButton.right() - 1, y, txtWidth, height);
        rightButton.setRect(inputField.right() - 1, y, height, height);

        width = Math.max(width, height * 2 + txtWidth - 2 + title.width() + GAP);
    }

    protected void onPointerUp() {
    }


    //------------------- Swing methods -----------------------


    /**
     * Returns the <code>SpinnerModel</code> that defines
     * this spinners sequence of values.
     *
     * @return the value of the model property
     */
    public SpinnerModel getModel() {
        return model;
    }

    /**
     * Returns the current value of the model, typically
     * this value is displayed by the <code>editor</code>. If the
     * user has changed the value displayed by the <code>editor</code> it is
     * possible for the <code>model</code>'s value to differ from that of
     * the <code>editor</code>, refer to the class level javadoc for examples
     * of how to deal with this.
     * <p>
     * This method simply delegates to the <code>model</code>.
     * It is equivalent to:
     * <pre>
     * getModel().getValue()
     * </pre>
     *
     * @return the current value of the model
     * @see #setValue
     * @see SpinnerModel#getValue
     */
    public Object getValue() {
        return getModel().getValue();
    }

    /**
     * Changes current value of the model, typically
     * this value is displayed by the <code>editor</code>.
     * If the <code>SpinnerModel</code> implementation
     * doesn't support the specified value then an
     * <code>IllegalArgumentException</code> is thrown.
     * <p>
     * This method simply delegates to the <code>model</code>.
     * It is equivalent to:
     * <pre>
     * getModel().setValue(value)
     * </pre>
     *
     * @param value new value for the spinner
     * @throws IllegalArgumentException if <code>value</code> isn't allowed
     * @see #getValue
     * @see SpinnerModel#setValue
     */
    public void setValue(Object value) {
        getModel().setValue(value);
    }

    /**
     * Returns the object in the sequence that comes after the object returned
     * by <code>getValue()</code>. If the end of the sequence has been reached
     * then return <code>null</code>.
     * Calling this method does not effect <code>value</code>.
     * <p>
     * This method simply delegates to the <code>model</code>.
     * It is equivalent to:
     * <pre>
     * getModel().getNextValue()
     * </pre>
     *
     * @return the next legal value or <code>null</code> if one doesn't exist
     * @see #getValue
     * @see #getPreviousValue
     * @see SpinnerModel#getNextValue
     */
    public Object getNextValue() {
        return getModel().getNextValue();
    }

    /**
     * Returns the object in the sequence that comes
     * before the object returned by <code>getValue()</code>.
     * If the end of the sequence has been reached then
     * return <code>null</code>. Calling this method does
     * not effect <code>value</code>.
     * <p>
     * This method simply delegates to the <code>model</code>.
     * It is equivalent to:
     * <pre>
     * getModel().getPreviousValue()
     * </pre>
     *
     * @return the previous legal value or <code>null</code>
     * if one doesn't exist
     * @see #getValue
     * @see #getNextValue
     * @see SpinnerModel#getPreviousValue
     */
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

        private RenderedTextBlock textBlock;
        private NinePatch bg;

        private float textOffsetX, textOffsetY;

        public SpinnerTextBlock(NinePatch bg, int fontSize) {
            super();
            this.bg = bg;
            add(bg);

            textBlock = PixelScene.renderTextBlock(fontSize);
            addToFront(textBlock);
            //TODO von group direkt referenz auf buttons von scrollpane uas?
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
    }


}
