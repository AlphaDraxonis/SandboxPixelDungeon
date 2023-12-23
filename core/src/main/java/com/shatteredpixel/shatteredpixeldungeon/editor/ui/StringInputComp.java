package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.ui.Component;

import java.util.Objects;

public class StringInputComp extends Component {

    protected RenderedTextBlock label, text;
    protected IconButton change;

    public String defaultValue;

    protected TextField.TextFieldFilter textFilter;

    public StringInputComp(String label, String text, int maxLength, boolean multiline) {
        this(label, text, maxLength, multiline, null);
    }

    public StringInputComp(String label, String text, int maxLength, boolean multiline, String defaultValue) {
        this(label, text, maxLength, multiline, defaultValue, null);
    }

    public StringInputComp(String label, String text, int maxLength, boolean multiline, String defaultValue, String changeBodyText) {
        this.defaultValue = defaultValue == null ? "???" : defaultValue;

        this.label = PixelScene.renderTextBlock(8);
        if (label != null) this.label.text(label);
        add(this.label);

        this.text = PixelScene.renderTextBlock(7);
        if (text != null) this.text.text(text);
        else this.text.text(defaultValue);
        add(this.text);

        change = new IconButton(Icons.CHANGES.get()) {

            @Override
            protected void onClick() {

                EditorScene.show(new WndTextInput(
                        StringInputComp.this.label.text(),
                        changeBodyText,
                        StringInputComp.this.text.text(),
                        maxLength, multiline, Messages.get(HeroSelectScene.class, "custom_seed_set"),
                        Messages.get(WndNewFloor.class, "cancel_label")
                ) {
                    {
                        setTextFieldFilter(textFilter);
                        if (Objects.equals(getText(), defaultValue)) {
                            textBox.selectAll();
                        }
                    }

                    @Override
                    public void onSelect(boolean positive, String text) {
                        if (positive && text != null && !text.equals(StringInputComp.this.text.text())) {
                            StringInputComp.this.text.text("".equals(text) ? " " : text);
                            onChange();
                        }
                    }
                });
            }
        };
        add(change);
    }

    @Override
    protected void layout() {

        label.maxWidth((int) (width - change.icon().width() - 2));
        text.maxWidth((int) (width - change.icon().width() - 2));

        label.setPos(x, y);
        text.setPos(x + 2, label.bottom() + 2);

        height = Math.max(change.icon().height(), text.bottom() - y);

        change.setRect(x + width - change.icon().width(), y + (height - change.icon().height()) * 0.5f, change.icon().width(), change.icon().height());
    }

    public void setHighlightingEnabled(boolean enabled) {
        text.setHighlighting(enabled);
    }

    public String getText() {
        return text.text();
    }

    public void setText(String text) {
        this.text.text(text);
    }

    protected void onChange() {
    }
}