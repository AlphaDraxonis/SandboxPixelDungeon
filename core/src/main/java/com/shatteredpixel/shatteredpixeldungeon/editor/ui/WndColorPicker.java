package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class WndColorPicker extends Window {

    protected static final int MARGIN = 2, COMP_HEIGHT = 18;

    protected IconTitle title;
    protected Image icon;

    protected ColorSpinner[] colorSpinners;
    protected ColorField[] colorFields;

    protected int color;

    public WndColorPicker(int startColor) {

        this.color = startColor;

        icon = Icons.ZONE.get();
        icon.hardlight(color);
        title = new IconTitle(icon, Messages.get(WndColorPicker.class, "title"));
        add(title);

        colorSpinners = new ColorSpinner[3];

        colorSpinners[0] = new ColorSpinner() {

            {
                addChangeListener(() -> setRed((Integer) colorSpinners[0].getValue()));
            }

            @Override
            protected int getColorPart(int rgbaColor) {
                return getRed(rgbaColor);
            }
        };
        add(colorSpinners[0]);
        colorSpinners[1] = new ColorSpinner() {

            {
                addChangeListener(() -> setGreen((Integer) colorSpinners[1].getValue()));
            }

            @Override
            protected int getColorPart(int rgbaColor) {
                return getGreen(rgbaColor);
            }
        };
        add(colorSpinners[1]);
        colorSpinners[2] = new ColorSpinner() {

            {
                addChangeListener(() -> setBlue((Integer) colorSpinners[2].getValue()));
            }

            @Override
            protected int getColorPart(int rgbaColor) {
                return getBlue(rgbaColor);
            }
        };
        add(colorSpinners[2]);

        colorFields = new ColorField[15];

        //Red
        colorFields[0] = new ColorField(0xF53200);
        add(colorFields[0]);

        //Green
        colorFields[1] = new ColorField(0x96C832);
        add(colorFields[1]);

        //Blue
        colorFields[2] = new ColorField(0x78D2FF);
        add(colorFields[2]);

        //Dark red
        colorFields[3] = new ColorField(0xBE0000);
        add(colorFields[3]);

        //Dark green
        colorFields[4] = new ColorField(0x078C07);
        add(colorFields[4]);

        //Dark blue
        colorFields[5] = new ColorField(0x0078D2);
        add(colorFields[5]);

        //Orange
        colorFields[6] = new ColorField(0xFA961C);
        add(colorFields[6]);

        //Pink
        colorFields[7] = new ColorField(0xEB91D2);
        add(colorFields[7]);

        //Purple
        colorFields[8] = new ColorField(0xA743D1);
        add(colorFields[8]);

        //Yellow
        colorFields[9] = new ColorField(0xEBDC05);
        add(colorFields[9]);

        //Dark pink
        colorFields[10] = new ColorField(0xC80087);
        add(colorFields[10]);

        //Dark purple
        colorFields[11] = new ColorField(0x7D1478);
        add(colorFields[11]);

        //White
        colorFields[12] = new ColorField(0xF5F5F5);
        add(colorFields[12]);

        //Light gray
        colorFields[13] = new ColorField(0xA3A3A3);
        add(colorFields[13]);

        //Brown
        colorFields[14] = new ColorField(0x824328);
        add(colorFields[14]);


        resize(PixelScene.landscape() ? 210 : Math.min(155, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        title.setRect(MARGIN, MARGIN, width - MARGIN * 2, title.height());

        float widthOneThird = (width - MARGIN * 4) / 3f;

        float posX = MARGIN;
        float posY = title.bottom() + MARGIN * 2;
        for (ColorSpinner spinner : colorSpinners) {
            spinner.setRect(posX, posY, widthOneThird, COMP_HEIGHT);
            posX = spinner.right() + MARGIN;
            PixelScene.align(spinner);
        }
        posX = MARGIN;
        posY = colorSpinners[colorSpinners.length - 1].bottom() + MARGIN * 2;

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
        posY += MARGIN;

        resize(width, (int) Math.ceil(posY));

        setSelectedColor(color);
    }

    public void setSelectedColor(int color) {
        this.color = color;

        icon.hardlight(color);
        colorSpinners[0].updateColor(color);
        colorSpinners[1].updateColor(color);
        colorSpinners[2].updateColor(color);
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

    private static abstract class ColorSpinner extends Spinner {

        public ColorSpinner() {
            super(new SpinnerIntegerModel(0, 255, 0) {
                {
                    setAbsoluteMaximum(255f);
                }

                @Override
                public float getInputFieldWidth(float height) {
                    return FILL;
                }
            }, "", 9);
        }

        protected abstract int getColorPart(int rgbaColor);

        public void updateColor(int rgbaColor) {
            int c = getColorPart(rgbaColor);
            if ((Integer) getValue() != c) setValue(c);
        }

    }

    private class ColorField extends Button {

        public final int color;

        protected ColorBlock colorBlock;

        private ColorField(int color) {
            this.color = color;
            colorBlock.color(color);
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            colorBlock = new ColorBlock(1, 1, 0xFF000000);
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
            setSelectedColor(color);
        }
    }

}