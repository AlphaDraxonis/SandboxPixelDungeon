package com.shatteredpixel.shatteredpixeldungeon.ui;


import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.Point;

//This Window slowly moves into the screen from a given starting point, making it ideal as a popup-window
//It can only either change width or height

/**
 * When preparing the layout for the components inside the window, it is important that all your components rely on <b>endWidth</b> and <b>endHeight</b>!
 */
public class SlowExtendWindow extends Window {

    public float speed = 4f;

    // true = size increases
    // false = size decreases
    // null = size stays the same
    private Boolean extending = true;

    protected final int endWidth;
    protected int endHeight;
    protected final Orientation orientation;
    protected final Point startPos;

    protected Camera spCamera;
    protected ScrollPane scrollPane;

    /**
     * @param endWidth    width the window has after the animation, dividable by 2
     * @param endHeight   height the window has after the animation, dividable by 2
     * @param orientation how it appears, defines if starting width or starting height is 0 or already the endSize
     * @param startPos    fix position on screen it cannot change, kinda like offset, a border ALWAYS touches this point in the center of the border
     */
    public SlowExtendWindow(int endWidth, int endHeight, Orientation orientation, Point startPos) {
        super();

        if (endWidth % 2 != 0) endWidth++;
        if (endHeight % 2 != 0) endHeight++;

        this.endWidth = endWidth;
        this.endHeight = endHeight;
        this.orientation = orientation;
        this.startPos = startPos;

        chrome.camera = camera;
        camera = new Camera(0, 0,
                (int) chrome.width - chrome.marginLeft(),
                (int) chrome.height - chrome.marginTop(),
                PixelScene.defaultZoom);
        camera.x = chrome.camera.x - chrome.marginLeft();
        camera.y = chrome.camera.y - chrome.marginTop();
        camera.scroll.set(chrome.camera.scroll);
        Camera.add(camera);

        if (orientation.offsetX == 0) resize(endWidth, 0);
        else resize(0, endHeight);

        offset(startPos);
    }

    @Override
    public void resize(int w, int h) {

        Camera ownCamera = camera;
        camera = chrome.camera;
        super.resize(w, h);
        camera = ownCamera;

        camera.resize((int) chrome.width - chrome.marginLeft(), (int) chrome.height - chrome.marginTop());

        camera.x = (int) (Game.width - camera.screenWidth()) / 2;
        camera.x += xOffset * camera.zoom - chrome.marginLeft();

        camera.y = (int) (Game.height - camera.screenHeight()) / 2;
        camera.y += yOffset * camera.zoom - chrome.marginTop();

        if (scrollPane != null) {
            if (orientation.changesWidth) {
                spCamera.resize((int) Math.min(scrollPane.width(), camera.x + camera.width + scrollPane.left() + chrome.marginRight() - spCamera.x), spCamera.height);
            } else {
                spCamera.resize(spCamera.width, (int) Math.min(scrollPane.height(), camera.y + camera.height + scrollPane.top() + chrome.marginBottom() - spCamera.y));
            }
        }
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        int oldX = this.xOffset;
        int oldY = this.yOffset;
        super.offset(xOffset, yOffset);
        this.xOffset = oldX;
        this.yOffset = oldY;

        Camera ownCamera = camera;
        camera = chrome.camera;
        super.offset(xOffset, yOffset);
        camera = ownCamera;

        if (scrollPane != null) scrollPane.layout(orientation.offsetY != 0 || orientation.offsetX != 0);
    }

    @Override
    public synchronized void update() {
        changeSize();
        super.update();
    }

    public Boolean isExtending() {
        return extending;
    }

    @Override
    public void destroy() {
        super.destroy();
        Camera.remove(chrome.camera);
    }

    private float unusedSizeChange;//should always be  0 <= |this| < 2

    protected void changeSize() {
        if (extending == null) return;
        float factor = speed * Game.elapsed * 100f * (extending ? 1 : -1);
        unusedSizeChange += factor;
        int resizeUnits = (int) (unusedSizeChange / 2);
        resizeUnits *= 2;//needs to be dividable by 2
        unusedSizeChange -= resizeUnits;

        int changeWidth = Math.min(orientation.changesWidth ? resizeUnits : 0, endWidth - width);
        int changeHeight = Math.min(orientation.changesWidth ? 0 : resizeUnits, endHeight - height);

        offset(getOffset().x + changeWidth * orientation.offsetX / 2, getOffset().y + changeHeight * orientation.offsetY / 2);
        resize(width + changeWidth, height + changeHeight);

        if (height >= endHeight && width >= endWidth && extending) {
            showImmediately();
        } else if (width < 0 || height < 0) {
            hideImmediately();
        }
    }

    @Override
    public void hide() {
        if (Boolean.TRUE.equals(extending)) {
            showImmediately();
        } else if (Boolean.FALSE.equals(extending)) {
            hideImmediately();
        } else extending = false;
    }

    public void hideImmediately() {
        extending = null;
        super.hide();
    }

    public void showImmediately() {
        resize(endWidth, endHeight);
        offset((int) (startPos.x + Math.ceil(endWidth / 2f) * orientation.offsetX), (int) (startPos.y + Math.ceil(endHeight / 2f) * orientation.offsetY));
        extending = null;
    }

    public enum Orientation {
        TOP_TO_BOTTOM(0, 1),
        RIGHT_TO_LEFT(-1, 0),
        BOTTOM_TO_TOP(0, -1),
        LEFT_TO_RIGHT(1, 0);

        public final int offsetX, offsetY;
        public final boolean changesWidth;

        Orientation(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            changesWidth = offsetX != 0;
        }
    }

}