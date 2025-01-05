package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.Locale;

public class WndColorPicker extends WndTabbed {
    
    //Tab 1: general colors, tab 2: rgb slider (+ textinput) and hex code
    
    private static final int
            RED = 0xF53200,
            DARK_RED = 0xBE0000,
            GREEN = 0x96C832,
            DARK_GREEN = 0x078C07,
            BLUE = 0x78D2FF,
            DARK_BLUE = 0x0078D2,
            ORANGE = 0xFA961C,
            PINK = 0xEB91D2,
            DARK_PINK = 0xC80087,
            PURPLE = 0xA743D1,
            DARK_PURPLE = 0x7D1478,
            YELLOW = 0xEBDC05,
            WHITE = 0xF5F5F5,
            LIGHT_GRAY = 0xA3A3A3,
            BROWN = 0x824328;
    

    protected static final int MARGIN = 2, COMP_HEIGHT = 18;

    protected IconTitle title;
    protected Image icon;
    
    protected Component content;
    protected ScrollPane sp;
    
    protected TabComp[] tabs;

    protected int color;

    public WndColorPicker(int startColor) {
        resize(WindowSize.WIDTH_LARGE_S.get(), 100);

        this.color = startColor;

        icon = Icons.ZONE.get();
        icon.hardlight(color);
        title = new IconTitle(icon, Messages.get(WndColorPicker.class, "title"));
        add(title);
        
        content = new Component();
        
        tabs = new TabComp[]{
                new ColorPaletteTab(),
                new RGBPickerTab()
        };
        
        for (int i = 0; i < tabs.length; i++) {
            content.add(tabs[i]);
            tabs[i].setRect(0, 0, width, height);
            int index = i;
            add(new LabeledTab(tabs[i].createLabel()) {
                protected void select(boolean value) {
                    super.select(value);
                    tabs[index].active = tabs[index].visible = value;
                }
            });
        }
        
        sp = new ScrollPane(content);
        add(sp);
        
        select(0);
        
        title.setRect(MARGIN, MARGIN, width - MARGIN * 2, title.height());
        sp.setPos(0, title.bottom() + MARGIN*2);
        
        content.setSize(width, changeHeight(width));
        
        layoutTabs();
        
        sp.givePointerPriority();
        
        setSelectedColor(color);
    }
    
    private float changeHeight(float width) {
        float h = 0;
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setSize(width, 0);
            h = Math.max(h, tabs[i].height());
        }
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setRect(0, 0, width, h);
        }
        
        float spaceForTitle = MARGIN + title.height() + MARGIN*2;
        float spHeight = Math.min(WindowSize.HEIGHT_MEDIUM.get() - spaceForTitle - MARGIN - tabHeight(), h);
        int windowHeight = (int) Math.ceil(spaceForTitle + spHeight + MARGIN);
        
        resize((int) Math.ceil(width), windowHeight);
        sp.setSize(width, spHeight);
        
        return h;
    }
    
    @Override
    protected void select(Tab tab) {
        int index = super.tabs.indexOf(tab);
        content.setSize(content.width(), tabs[index].height());
        sp.setSize(sp.width(), sp.height());
        sp.scrollToCurrentView();
        super.select(tab);
    }
    
    public void setSelectedColor(int color) {
        this.color = color;

        icon.hardlight(color);
        
        for (int i = 0; i < tabs.length; i++) {
            tabs[i].setSelectedColor(color);
        }
    }
    
    protected class ColorPaletteTab extends TabComp {
        
        protected ColorField[] colorFields;
        
        public ColorPaletteTab() {
            
            int i = 0;
            colorFields = new ColorField[15];
            add(colorFields[i++] = new ColorField(RED));
            add(colorFields[i++] = new ColorField(GREEN));
            add(colorFields[i++] = new ColorField(BLUE));
            add(colorFields[i++] = new ColorField(DARK_RED));
            add(colorFields[i++] = new ColorField(DARK_GREEN));
            add(colorFields[i++] = new ColorField(DARK_BLUE));
            add(colorFields[i++] = new ColorField(ORANGE));
            add(colorFields[i++] = new ColorField(PINK));
            add(colorFields[i++] = new ColorField(PURPLE));
            add(colorFields[i++] = new ColorField(YELLOW));
            add(colorFields[i++] = new ColorField(DARK_PINK));
            add(colorFields[i++] = new ColorField(DARK_PURPLE));
            add(colorFields[i++] = new ColorField(WHITE));
            add(colorFields[i++] = new ColorField(LIGHT_GRAY));
            add(colorFields[i++] = new ColorField(BROWN));
        }
        
        @Override
        protected void layout() {
            float widthOneThird = (width - MARGIN * 4) / 3f;
            
            float posX = x + MARGIN;
            float posY = y;
            
            int column = 0;
            for (ColorField cf : colorFields) {
                cf.setRect(posX, posY, widthOneThird, COMP_HEIGHT);
                posX = cf.right() + MARGIN;
                PixelScene.align(cf);
                column++;
                if (column == 3) {
                    column = 0;
                    posX = MARGIN;
                    posY = cf.bottom() + MARGIN;
                }
            }
            height = posY - MARGIN;
        }
        
        @Override
        public void setSelectedColor(int color) {
            //we don't need to do anything here
        }
        
        protected final class ColorField extends Button {
            
            public final int color;
            
            private final ColorBlock colorBlock;
            
            private ColorField(int color) {
                this.color = color;
                
                colorBlock = new ColorBlock(1, 1, 0xFF000000);
                colorBlock.color(color);
                add(colorBlock);
                
            }
            
            @Override
            protected void layout() {
                super.layout();
                
                colorBlock.size(width, height);
                colorBlock.x = x;
                colorBlock.y = y;
                PixelScene.align(colorBlock);
            }
            
            @Override
            protected void onPointerDown() {
                colorBlock.brightness(1.2f);
                Sample.INSTANCE.play(Assets.Sounds.CLICK);
            }
            
            @Override
            protected void onPointerUp() {
                colorBlock.resetColor();
                colorBlock.color(color);
            }
            
            @Override
            protected void onClick() {
                WndColorPicker.this.setSelectedColor(color);
            }
        }
    }
    
    protected class RGBPickerTab extends TabComp {
        
        protected TextInput hexInput;
        protected ColorSpinner[] colorSpinners;
        protected OptionSlider[] colorSliders;
        
        public RGBPickerTab() {
            
            hexInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, 9, PixelScene.uiCamera.zoom) {
                @Override
                public void setText(String text) {
                    super.setText(text.toUpperCase(Locale.ENGLISH));
                    int l = text.length();
                    if (l == 6 || l == 7) {
                        try {
                            int newColor = l == 7
                                    ? Integer.parseInt(text.substring(1), 16)
                                    : Integer.parseInt(text, 16);
                            
							if (newColor != color) {
								WndColorPicker.this.setSelectedColor(newColor);
							}
						} catch (NumberFormatException e) {
                            //probably more than one #
                        }
                    }
                }
            };
            hexInput.setTextFieldFilter(new TextField.TextFieldFilter() {
                @Override
                public boolean acceptChar(TextField textField, char c) {
                    return Character.digit(c, 16) != -1 || c == '#';
                }
            });
            hexInput.setMaxLength(7);
            add(hexInput);
            
            
            colorSpinners = new ColorSpinner[3];
            
            add( colorSpinners[0] = new ColorSpinner(() -> setRed((Integer) colorSpinners[0].getValue())) {
                @Override
                protected int getColorPart(int rgbaColor) {
                    return getRed(rgbaColor);
                }
            } );
            add( colorSpinners[1] = new ColorSpinner(() -> setGreen((Integer) colorSpinners[1].getValue())) {
                @Override
                protected int getColorPart(int rgbaColor) {
                    return getGreen(rgbaColor);
                }
            } );
            add( colorSpinners[2] = new ColorSpinner(() -> setBlue((Integer) colorSpinners[2].getValue())) {
                @Override
                protected int getColorPart(int rgbaColor) {
                    return getBlue(rgbaColor);
                }
            } );
            
            colorSliders = new OptionSlider[3];
            
            add( colorSliders[0] = new OptionSlider(Messages.get(WndColorPicker.class, "red"), "0", "255", 0, 255, 11) {
                @Override
                protected void onChange() {
                }
                @Override
                protected void immediatelyOnChange(int currentVal) {
                    setRed(currentVal);
                }
            } );
            add( colorSliders[1] = new OptionSlider(Messages.get(WndColorPicker.class, "green"), "0", "255", 0, 255, 11) {
                @Override
                protected void onChange() {
                }
                @Override
                protected void immediatelyOnChange(int currentVal) {
                    setGreen(currentVal);
                }
            } );
            add( colorSliders[2] = new OptionSlider(Messages.get(WndColorPicker.class, "blue"), "0", "255", 0, 255, 11) {
                @Override
                protected void onChange() {
                }
                @Override
                protected void immediatelyOnChange(int currentVal) {
                    setBlue(currentVal);
                }
            } );
            
        }
        private static final int HEX_INPUT_HEIGHT = 18;
        private static final int SLIDER_MIN_HEIGHT = 22;
        private static final int SLIDER_MAX_HEIGHT = 28;
        private static final int SPINNER_WIDTH = 48;
        
        @Override
        protected void layout() {
            height = Math.max(minimumHeight(), height);
            
            float posX = x + MARGIN;
            float posY = y;
            
            hexInput.setRect(posX + MARGIN, posY, width - MARGIN*4, HEX_INPUT_HEIGHT);
            posY = hexInput.bottom() + MARGIN*2;
            
            float remainingHeight = height - posY + y;
            float heightPerRow = Math.min((remainingHeight - MARGIN*2) / 3, SLIDER_MAX_HEIGHT);
            for (int i = 0; i < 3; i++) {
                colorSliders[i].setRect(posX, posY, width - MARGIN*3 - SPINNER_WIDTH, heightPerRow);
                colorSpinners[i].setRect(colorSliders[i].right() + MARGIN, posY, SPINNER_WIDTH, heightPerRow);
                posY += heightPerRow + MARGIN;
            }
            height = posY - y - MARGIN;
        }
        
        private float minimumHeight() {
            return HEX_INPUT_HEIGHT + MARGIN +  (SLIDER_MIN_HEIGHT+MARGIN)*3 - MARGIN;
        }
        
        @Override
        public void setSelectedColor(int color) {
            hexInput.setText(toHexValue(color));
            for (int i = 0; i < 3; i++) {
                colorSpinners[i].updateColor(color);
                colorSliders[i].setSelectedValue(colorSpinners[i].getColorPart(color));
            }
        }
    }

    public static int getRed(int rgbaColor) {
        return (rgbaColor >> 16) & 0xFF;
    }

    public static int getGreen(int rgbaColor) {
        return (rgbaColor >> 8) & 0xFF;
    }

    public static int getBlue(int rgbaColor) {
        return rgbaColor & 0xFF;
    }

    public static int getAlpha(int rgbaColor) {
        return (rgbaColor >> 24) & 0xFF;
    }

    public void setRed(int red) {
        setSelectedColor((color & 0xFF00FFFF) | ((red & 0xFF) << 16));
    }

    public void setGreen(int green) {
        setSelectedColor((color & 0xFFFF00FF) | ((green & 0xFF) << 8));
    }

    public void setBlue(int blue) {
        setSelectedColor((color & 0xFFFFFF00) | (blue & 0xFF));
    }

    public void setAlpha(int alpha) {
        setSelectedColor((color & 0x00FFFFFF) | ((alpha & 0xFF) << 24));
    }
    
    private static String toHexValue(int val) {
        String s = Integer.toHexString(val);
        while (s.length() < 6) {
            s = "0" + s;
        }
        return "#" + s;
    }
    
    protected static abstract class ColorSpinner extends Spinner {
        
        public ColorSpinner(Runnable changeListener) {
            super(new SpinnerIntegerModel(0, 255, 0) {
                {
                    setAbsoluteMaximum(255f);
                }
                
                @Override
                public float getInputFieldWidth(float height) {
                    return FILL;
                }
            }, "", 9);
            addChangeListener(changeListener);
        }
        
        protected abstract int getColorPart(int rgbaColor);
        
        public void updateColor(int rgbaColor) {
            int c = getColorPart(rgbaColor);
            if ((Integer) getValue() != c) setValue(c);
        }
        
    }
    
    protected static abstract class TabComp extends Component {
        
        public TabComp() {
        }
        
        public abstract void setSelectedColor(int color);
        
        public String createLabel() {
            return Messages.get(this, "label");
        }
    }

}