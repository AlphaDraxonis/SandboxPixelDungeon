package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.LevelColoring;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndColorPicker;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

public class ChangeLevelColoring extends Component {

    //title
    //info
    RenderedTextBlock info;

    protected Part floor, wall, water;

    protected final LevelScheme levelScheme;

    public ChangeLevelColoring(LevelScheme levelScheme) {
        this.levelScheme = levelScheme;

        info = PixelScene.renderTextBlock(Messages.get(ChangeLevelColoring.class, "info"), 6);
        add(info);

        floor = new Part(Messages.get(ChangeLevelColoring.class, "floor"), levelScheme.floorColor, levelScheme.floorAlpha) {
            @Override
            protected void setColor(int rgbColor) {
                super.setColor(rgbColor);
                LevelColoring.getFloor().setColor(rgbColor);
            }

            @Override
            protected void setAlpha(float alphaZeroToOne) {
                super.setAlpha(alphaZeroToOne);
                LevelColoring.getFloor().setAlpha(alphaZeroToOne);
            }
        };
        add(floor);

        wall = new Part(Messages.get(ChangeLevelColoring.class, "wall"), levelScheme.wallColor, levelScheme.wallAlpha) {
            @Override
            protected void setColor(int rgbColor) {
                super.setColor(rgbColor);
                LevelColoring.getWall(true).setColor(rgbColor);
            }

            @Override
            protected void setAlpha(float alphaZeroToOne) {
                super.setAlpha(alphaZeroToOne);
                LevelColoring.getWall(true).setAlpha(alphaZeroToOne);
            }
        };
        add(wall);

        water = new Part(Messages.get(ChangeLevelColoring.class, "water"), levelScheme.waterColor, levelScheme.waterAlpha) {
            @Override
            protected void setColor(int rgbColor) {
                super.setColor(rgbColor);
                LevelColoring.getWater().setColor(rgbColor);
            }

            @Override
            protected void setAlpha(float alphaZeroToOne) {
                super.setAlpha(alphaZeroToOne);
                LevelColoring.getWater().setAlpha(alphaZeroToOne);
            }
        };
        add(water);
    }

    @Override
    protected void layout() {

        info.maxWidth((int) width);
        info.setPos(x, y);

        floor.setRect(x, y + info.bottom() + 4, width, 30);

        wall.setRect(x, y + floor.bottom() + 4, width, 30);

        water.setRect(x, y + wall.bottom() + 4, width, 30);

        height = water.bottom() + 1;
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangeLevelColoring.class, "title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    //for each of three: text mit hotarea, white colorblock as bg, colored colorblock shows color, click opens color picker

    private static class Part extends Button {

        protected RenderedTextBlock label;
        protected ColorBlock bg;
        protected ColorBlock showColor;

        protected OptionSlider transparency;

        private int currentColor;

        public Part(String text, int color, float alpha) {

            super();

            label = PixelScene.renderTextBlock(text, 10);
            add(label);

            transparency = new OptionSlider(Messages.get(ChangeLevelColoring.class, "transparency"),
                    Messages.get(ChangeLevelColoring.class, "invisible"), Messages.get(ChangeLevelColoring.class, "visible"),
                    0, 100, 11) {
                @Override
                protected void immediatelyOnChange(int currentVal) {
                    setAlpha(currentVal / 100f);
                }

                @Override
                protected void onChange() {
                }
            };
            transparency.setSelectedValue((int) (alpha * 100));
            add(transparency);

            currentColor = color;
            showColor.hardlight(color);
            showColor.alpha(alpha);
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            bg = new ColorBlock(1, 1, 0xFFFFFFFF);
            add(bg);

            showColor = new ColorBlock(1, 1, 0xFFFFFFFF);
            add(showColor);
        }

        @Override
        protected void layout() {
            super.layout();

            label.maxWidth((int) (width * 2) / 5);
            label.setPos(x + 2, y + (height - label.height()) * 0.5f);

            transparency.setRect(label.right() + 5, y + 1, width - label.width() - 9, height - 2);

            hotArea.width = label.width() + 6;

            bg.size(width, height);
            bg.x = x;
            bg.y = y;

            showColor.size(width, height);
            showColor.x = x;
            showColor.y = y;
        }

        @Override
        protected void onClick() {
            EditorScene.show(new WndColorPicker(currentColor){
                @Override
                public void setSelectedColor(int color) {
                    super.setSelectedColor(color);
                    setColor(color);
                }
            });
        }

        protected void setColor(int rgbColor) {
            showColor.hardlight(rgbColor);
            currentColor = rgbColor;
        }

        protected void setAlpha(float alphaZeroToOne){
            showColor.alpha(alphaZeroToOne);
        }
    }
}