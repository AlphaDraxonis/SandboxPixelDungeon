package com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner;

import com.watabou.noosa.ui.Component;

//is javax.swing.SpinnerModel
public interface SpinnerModel {

    float getInputFieldWith(float height);

    Component createInputField(int fontSize);

    int getClicksPerSecondWhileHolding();

    /**
     * The <i>current element</i> of the sequence.  This element is usually
     * displayed by the <code>editor</code> part of a <code>JSpinner</code>.
     *
     * @return the current spinner value.
     * @see #setValue
     */
    Object getValue();


    /**
     * Changes current value of the model, typically this value is displayed
     * by the <code>editor</code> part of a  <code>JSpinner</code>.
     * If the <code>SpinnerModel</code> implementation doesn't support
     * the specified value then an <code>IllegalArgumentException</code>
     * is thrown.  For example a <code>SpinnerModel</code> for numbers might
     * only support values that are integer multiples of ten. In
     * that case, <code>model.setValue(new Number(11))</code>
     * would throw an exception.
     *
     * @param value  new value for the spinner
     * @throws IllegalArgumentException if <code>value</code> isn't allowed
     * @see #getValue
     */
    void setValue(Object value);


    /**
     * Return the object in the sequence that comes after the object returned
     * by <code>getValue()</code>. If the end of the sequence has been reached
     * then return null.  Calling this method does not effect <code>value</code>.
     *
     * @return the next legal value or null if one doesn't exist
     * @see #getValue
     * @see #getPreviousValue
     */
    Object getNextValue();


    /**
     * Return the object in the sequence that comes before the object returned
     * by <code>getValue()</code>.  If the end of the sequence has been reached then
     * return null. Calling this method does not effect <code>value</code>.
     *
     * @return the previous legal value or null if one doesn't exist
     * @see #getValue
     * @see #getNextValue
     */
    Object getPreviousValue();


    public void addChangeListener(Runnable listener) ;
    public void removeChangeListener(Runnable listener);
    public Runnable[] getChangeListeners();

}
