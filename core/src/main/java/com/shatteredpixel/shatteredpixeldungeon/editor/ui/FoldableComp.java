package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class FoldableComp extends Component {

    private static final float EXPAND_ANIMATION_DURATION = 0.2f;
    protected Image icon;
    protected RenderedTextBlock title;
    protected ColorBlock line;

    protected IconButton expandAndFold;

    protected Component body;
    
    protected boolean expanded;


    protected FoldableComp() {
    }

    public FoldableComp(String label) {
        title.text(label);
    }

    public FoldableComp(Component body) {
        setBody(body);
    }

    public FoldableComp(String label, Component body) {
        this(label);
        setBody(body);
    }

    @Override
    protected void createChildren() {

        line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
        add(line);

        title = PixelScene.renderTextBlock(titleFontSize());
        add(title);


        expandAndFold = new IconButton(Icons.EXPAND.get()) {
            
            private static final float HALF_CIRCLE_DEGREES = 180f;
            
            {
                icon.originToCenter();
            }
            
            private final float defaultAngle = icon.angle;
            private boolean animationEnabled;
            
            @Override
            protected void onClick() {
                animationEnabled = true;
                if (expanded) {
                    fold();
                } else {
                    expand();
                }
            }
            
            @Override
            public void update() {
                if (animationEnabled) {
                    if (expanded) {
                        float angle = icon.angle + HALF_CIRCLE_DEGREES * Game.elapsed / EXPAND_ANIMATION_DURATION;
                        if (angle >= HALF_CIRCLE_DEGREES) {
                            angle = HALF_CIRCLE_DEGREES;
                            animationEnabled = false;
                        }
                        icon.angle = defaultAngle + angle;
                    } else {
                        float angle = icon.angle - HALF_CIRCLE_DEGREES * Game.elapsed / EXPAND_ANIMATION_DURATION;
                        if (angle <= 0) {
                            angle = 0;
                            animationEnabled = false;
                        }
                        icon.angle = defaultAngle + angle;
                    }
                } else {
                    if (expanded) {
                        icon.angle = defaultAngle + HALF_CIRCLE_DEGREES;
                    } else {
                        icon.angle = defaultAngle;
                    }
                }
                super.update();
            }
        };
        add(expandAndFold);
    }

    protected int titleFontSize() {
        return 9;
    }


    //Warning: these methods layout the parent, so don't call them while layouting!
    public void expand() {
        showBody(true);
        layoutParent();
    }

    public void fold() {
        showBody(false);
        layoutParent();
    }

    protected void showBody(boolean flag) {
        expanded = flag;
        body.visible = body.active = flag;
    }

    public void setBody(Component body) {
        this.body = body;
        add(body);
        showBody(true);
    }

    protected static final int BUTTON_HEIGHT = 13, BUTTON_GAP = 1;

    @Override
    protected void layout() {

        float posY = y;

        float posX = x + width - 2;
        float iconWidth = (icon == null ? 0 : icon.width() + 4);
        float titleWidth = width - 2 - requiredWidthForControlButtons() - iconWidth;

        title.maxWidth((int) titleWidth);
        float titleHeight = Math.max(BUTTON_HEIGHT, Math.max(title.height(), icon == null ? 0 : icon.height()));

        layoutControlButtons(posX, posY, titleHeight);
        
        if (icon != null) {
            icon.x = x + 2;
            icon.y = posY + 1 + (titleHeight - icon.height()) * 0.5f;
            PixelScene.align(icon);
        }

        title.setPos(x + iconWidth, (titleHeight - title.height()) * 0.5f + posY + 1);

        posY += titleHeight + 2;

        if (body != null && body.visible) {
            body.setRect(x, posY, width, -1);
            posY = body.bottom();
        }
        height = posY - y + 1;

        line.size(width, 1);
        line.x = x;
        line.y = y + height;

    }

    protected float requiredWidthForControlButtons() {
        float w = 0;
        if (expandAndFold.visible) w += BUTTON_HEIGHT + BUTTON_GAP;
        return w;
    }

    //posX is from right to left
    protected float layoutControlButtons(float posX, float posY, float titleHeight) {
        if (expandAndFold != null && expandAndFold.visible) {
            expandAndFold.setRect(posX -= BUTTON_HEIGHT + BUTTON_GAP, posY + (titleHeight - expandAndFold.icon().height()) * 0.5f, BUTTON_HEIGHT, BUTTON_HEIGHT);
            PixelScene.align(expandAndFold);
        }

        return posX;

    }

    protected void layoutParent() {
    }
}