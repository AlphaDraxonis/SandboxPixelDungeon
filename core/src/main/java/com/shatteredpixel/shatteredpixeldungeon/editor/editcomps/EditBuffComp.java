package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditBuffComp extends DefaultEditComp<Buff> {

    private final Component[] comps;

    private RedButton removeBuff;
    private Spinner changeDuration;

    public EditBuffComp(Buff buff, DefaultEditComp<?> editComp) {
        super(buff);

        if (buff.target != null) {
            removeBuff = new RedButton(Messages.get(this, "remove")) {
                @Override
                protected void onClick() {
                    buff.detach();
                    if (editComp != null) {
                        if (editComp instanceof EditMobComp) ((EditMobComp) editComp).buffs.removeBuffFromUI(buff.getClass());
                        editComp.updateObj();
                    }
                    EditorUtilies.getParentWindow(this).hide();
                }
            };
            add(removeBuff);
        }


//        SpinnerIntegerModel spinnerModel = new SpinnerIntegerModel(1, 500, (int) buff.visualcooldown(), 1, true, INFINITY) {
//            @Override
//            public float getInputFieldWidth(float height) {
//                return height * 1.4f;
//            }
//        };

//        if (false) {//only apply permanent buffs for now
//            changeDuration = new Spinner(spinnerModel, " " + Messages.get(EditBuffComp.class, "duration"), 10) {
//                @Override
//                protected void afterClick() {
//                    onSpinnerValueChange(true);
//                }
//            };
//            add(changeDuration);
//
//            changeDuration.addChangeListener(() -> onSpinnerValueChange(false));
//        } else changeDuration = null;


        comps = new Component[]{changeDuration, removeBuff};
    }

    private void onSpinnerValueChange(boolean updateTextAlways) {
        int duration = changeDuration.getValue() == null ? 9999 : (int) changeDuration.getValue();
        obj.setDurationForBuff(duration);
        String updatedText = obj.desc();
        if (updateTextAlways || updatedText.length() < 500 || duration % 10 == 0)
            updateObj();//pretty expensive call for longer texts so it is better to call this less
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
    }

    @Override
    protected Component createTitle() {
        return WndInfoBuff.createIconTitle(obj);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(obj.name());
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return null;
    }
}