package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.mobs;

import static com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel.INFINITY;

import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndInfoBuff;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;

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

            removeBuff = new RedButton(Messages.get(WndInfoBuffEditor.class, "remove")) {
                @Override
                protected void onClick() {
//                  EditorScene.show(new WndOptions((Image)null,"really remove?","msg","Yes","No"));
                    buff.detach();
                    wnd.buffIndicator.updateBuffs();
                    wnd.hide();
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
                changeDuration = new Spinner(spinnerModel, " " + Messages.get(WndInfoBuffEditor.class, "duration"), 10) {
                    @Override
                    protected void afterClick() {
                        onSpinnerValueChange(true);
                    }
                };
                add(changeDuration);

                changeDuration.addChangeListener(() -> onSpinnerValueChange(false));
            } else changeDuration = null;

            layout();
        }

        private void onSpinnerValueChange(boolean updateTextAlways) {
            int duration = changeDuration.getValue() == null ? 9999 : (int) changeDuration.getValue();
            wnd.buff.setDurationForBuff(duration);
            String updatedText = wnd.buff.desc();
            if (updateTextAlways || updatedText.length() < 500 || duration % 10 == 0)
                updateText(updatedText);//pretty expensive call for longer texts so it is better to call this less
        }

        @Override
        protected void layout() {
            text.setRect(x, y, text.width(), text.height());
            if (changeDuration != null)
                changeDuration.setRect(text.left(), text.bottom() + 2 * GAP, width, WndMenuEditor.BTN_HEIGHT);
            removeBuff.setRect(text.left(), changeDuration != null ? changeDuration.bottom() + GAP : text.bottom() + 2 * GAP, width, WndMenuEditor.BTN_HEIGHT);
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
            wnd.layout(-1);
        }
    }
}