package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class WndChooseIntOnScala extends Window {
    
    protected static final int MARGIN = 2, COMP_HEIGHT = 24;
    private static final int SPINNER_WIDTH = 48;

    protected RenderedTextBlock title;
    
    protected Component content;
    protected ScrollPane sp;
    
    private final InputEntry[] entries;
    private final InputEntryComp[] entryComps;
    
    public WndChooseIntOnScala(String titleText, String label, int min, int max, int current, Consumer<Integer> onChange) {
        this(titleText, new InputEntry(label, min, max, current, onChange));
    }
    
    public WndChooseIntOnScala(String titleText, InputEntry... entries) {
        super(WindowSize.WIDTH_MEDIUM.get(), 100);

        this.entries = entries;
        this.entryComps = new InputEntryComp[entries.length];

        title = PixelScene.renderTextBlock(titleText, 9);
        title.hardlight(Window.TITLE_COLOR);
        add(title);
        
        content = new Component() {
            {
                for (int i = 0; i < entries.length; i++) {
                    add(entryComps[i] = new InputEntryComp(entries[i]));
                }
            }
            
            @Override
            protected void layout() {
                height = 0;
                height = EditorUtilities.layoutCompsLinear(MARGIN, this, entryComps);
            }
        };
        
        sp = new ScrollPane(content);
        add(sp);
        
        layout(width);
        
        sp.givePointerPriority();
    }
    
    private void layout(float width) {
        
        title.maxWidth((int) width);
        title.setPos((width - title.width()) * 0.5f, MARGIN);
        sp.setPos(0, title.bottom() + MARGIN*2);
        
        content.setSize(width, 0);
        
        float spaceForTitle = MARGIN + title.height() + MARGIN*2;
        float spHeight = Math.min(WindowSize.HEIGHT_MEDIUM.get() - spaceForTitle - MARGIN, content.height());
        int windowHeight = (int) Math.ceil(spaceForTitle + spHeight + MARGIN);
        
        resize((int) Math.ceil(width), windowHeight);
        sp.setSize(width, spHeight);
    }
    
    
    public static class InputEntry {
        private final String label;
        private final int min, max;
        
        private int current;
        private final Consumer<Integer> onChange;
        
        public InputEntry(String label, int min, int max, int current) {
            this(label, min, max, current, null);
        }
        
        public InputEntry(String label, int min, int max, int current, Consumer<Integer> onChange) {
            this.label = label;
            this.min = min;
            this.max = max;
            this.current = current;
            this.onChange = onChange;
        }
        
        public int getCurrent() {
            return current;
        }
    }
    
    protected static class InputEntryComp extends Component {
        
        protected final InputEntry entry;
        
        protected OptionSlider slider;
        protected Spinner spinner;
        
        public InputEntryComp(InputEntry entry) {
            super();
            
            this.entry = entry;
            
            slider = new OptionSlider(entry.label, String.valueOf(entry.min), String.valueOf(entry.max), entry.min, entry.max, 11) {
                @Override
                protected void onChange() {
                }
                @Override
                protected void immediatelyOnChange(int currentVal) {
                    spinner.setValue(currentVal);
                }
            };
            slider.setSelectedValue(entry.current);
            add(slider);
            
            spinner = new Spinner(new SpinnerIntegerModel(entry.min, entry.max, entry.current) {
                {
                    setAbsoluteMinimum(entry.min);
                    setAbsoluteMaximum(entry.max);
                }
                
                @Override
                public float getInputFieldWidth(float height) {
                    return Spinner.FILL;
                }
            }, "", 9);
            spinner.addChangeListener(() -> {
                entry.current = (Integer) spinner.getValue();
                slider.setSelectedValue(entry.current);
                if (entry.onChange != null) entry.onChange.accept(entry.current);
            });
            add(spinner);
        }
        
        @Override
        protected void layout() {
            slider.setRect(x, y, width - MARGIN*3 - SPINNER_WIDTH, COMP_HEIGHT);
            spinner.setRect(slider.right() + MARGIN, y, SPINNER_WIDTH, COMP_HEIGHT);
            height = COMP_HEIGHT;
        }
    }
}
