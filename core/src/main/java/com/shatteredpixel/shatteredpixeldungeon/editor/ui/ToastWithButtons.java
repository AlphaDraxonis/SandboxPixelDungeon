package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toast;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;

public class ToastWithButtons extends Component {

    protected NinePatch bg;
    protected Component[] comps;

    private PointerArea blocker;

    public ToastWithButtons() {
        super();
        layout();
    }


    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        bg = Chrome.get(Chrome.Type.TOAST_TR);
        add(bg);

        blocker = new PointerArea(bg);
        add(blocker);
    }

    @Override
    protected void layout() {

        final float maxWidth = PixelScene.uiCamera.width * PixelScene.uiCamera.zoom;
        float minWidth = 0;
        float posX = x + Toast.MARGIN_HOR;
        float posY = y + Toast.MARGIN_VER;
        float posYnextRow = posY;

        for (Component c : comps) {
            if (c.visible) {
                c.setPos(posX, posY);
                PixelScene.align(c);
                posX = c.right() + Toast.MARGIN_HOR;
                posYnextRow = Math.max(posYnextRow, c.bottom() + Toast.MARGIN_VER);
                if (posX > maxWidth) {
                    posY = posYnextRow;
                    posX = x + Toast.MARGIN_HOR;
                    c.setPos(posX, posY);
                    PixelScene.align(c);
                    posX = c.right() + Toast.MARGIN_HOR;
                    posYnextRow = Math.max(posYnextRow, c.bottom() + Toast.MARGIN_VER);
                    minWidth = Math.max(minWidth, posX - x);
                } else minWidth = Math.max(minWidth, posX - x);
            }
        }

        width = minWidth;
        height = posYnextRow - y;

        bg.x = x;
        bg.y = y;
        bg.size(width, height);

        blocker.x = bg.x;
        blocker.y = bg.y;
        blocker.width = bg.width;
        blocker.height = bg.height;

    }

}