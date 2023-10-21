package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel.INFINITY;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffIndicatorEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditBuffComp extends DefaultEditComp<Buff> {

    private final BuffIndicatorEditor buffIndicator;

    private final Component[] comps;

    private final RedButton removeBuff;
    private final Spinner changeDuration;

    public EditBuffComp(Buff buff, BuffIndicatorEditor buffIndicator) {
        super(buff);

        this.buffIndicator = buffIndicator;

        removeBuff = new RedButton(Messages.get(EditBuffComp.class, "remove")) {
            @Override
            protected void onClick() {
//                  EditorScene.show(new WndOptions((Image)null,"really remove?","msg","Yes","No"));
                buff.detach();
                buffIndicator.updateBuffs();
                EditorUtilies.getParentWindow(this).hide();
            }
        };

        add(removeBuff);


        SpinnerIntegerModel spinnerModel = new SpinnerIntegerModel(1, 500, (int) buff.visualcooldown(), 1, true, INFINITY) {
            @Override
            public float getInputFieldWith(float height) {
                return height * 1.4f;
            }
        };

        if (false) {//only apply permanent buffs for now
            changeDuration = new Spinner(spinnerModel, " " + Messages.get(EditBuffComp.class, "duration"), 10) {
                @Override
                protected void afterClick() {
                    onSpinnerValueChange(true);
                }
            };
            add(changeDuration);

            changeDuration.addChangeListener(() -> onSpinnerValueChange(false));
        } else changeDuration = null;


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
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return null;
    }

    @Override
    public void updateObj() {
//        if (title instanceof IconTitle) {
//        }
        desc.text(createDescription());
        super.updateObj();
    }

//    public static boolean areEqual(Buff a, Buff b) {
//        if (a == null || b == null) return false;
//        if (a.getClass() != b.getClass()) return false;
//        return true;
//    }
}