package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.TextInput;

public class WndInfoBuffEditor extends WndTitledMessage {

    private final Buff buff;
    private final BuffIndicatorEditor buffIndicator;

    public WndInfoBuffEditor(Buff buff, BuffIndicatorEditor buffIndicator) {
        super(WndInfoBuff.createIconTitle(buff), () -> new Body(buff, 6));
        this.buff = buff;
        this.buffIndicator = buffIndicator;
        ((Body) body()).setWnd(this);
    }

    @Override
    public void hide() {
        buffIndicator.updateBuffs();
        super.hide();
    }

    private static class Body extends WndTitledMessage.Body {
        private final RenderedTextBlock text;
        private final RedButton removeBuff;
        private final Spinner changeDuration;
        private WndInfoBuffEditor wnd;

        public Body(Buff buff, int fontSize) {
            super();

            text = PixelScene.renderTextBlock(buff.desc(), fontSize);
            add(text);

            removeBuff = new RedButton("Remove") {
                @Override
                protected void onClick() {
//                  EditorScene.show(new WndOptions((Image)null,"really remove?","msg","Yes","No"));
                    buff.detach();
                    wnd.buffIndicator.updateBuffs();
                    wnd.hide();
                }
            };

            add(removeBuff);


            SpinnerIntegerModel spinnerModel = new SpinnerIntegerModel(1, 500, (int) buff.visualcooldown(), 1, true,true) {
                @Override
                public float getInputFieldWith(float height) {
                    return height * 1.4f;
                }
            };
            changeDuration = new Spinner(spinnerModel, " Duration:", 10) {
                @Override
                protected void onPointerUp() {
                    onSpinnerValueChange(true);
                }
            };
            add(changeDuration);

            changeDuration.addChangeListener(() -> onSpinnerValueChange(false));

            layout();
        }

        private void onSpinnerValueChange(boolean updateTextAlways) {
            int duration = changeDuration.getValue() == null ? 9999 : (int) changeDuration.getValue();
            wnd.buff.setDurationForBuff(duration);
            String updatedText = wnd.buff.desc();
            if (updateTextAlways||updatedText.length() < 500  || duration % 10 == 0)
                updateText(updatedText);//pretty expensive call for longer texts so it is better to call this less
        }

        @Override
        protected void layout() {
            text.setRect(x, y, text.width(), text.height());
            changeDuration.setRect(text.left(), text.bottom() + 2 * GAP, width, WndMenuEditor.BTN_HEIGHT);
            removeBuff.setRect(text.left(), changeDuration.bottom() +  GAP, width, WndMenuEditor.BTN_HEIGHT);
            height = removeBuff.bottom() - y;
        }

        @Override
        public void setMaxWith(int width) {
            text.maxWidth(width);
        }

        private void setWnd(WndInfoBuffEditor wnd) {
            this.wnd = wnd;
        }

        private void updateText(String txt) {
            text.text(txt);
            wnd.layout();
        }
    }
}
