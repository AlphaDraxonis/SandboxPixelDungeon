/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.input.PointerEvent;
import com.watabou.input.ScrollEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ScrollArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

public class ScrollPane extends Component {

    protected static final int THUMB_COLOR = 0xFF7b8073;
    protected static final float THUMB_ALPHA = 0.5f;

    protected PointerController controller;
    protected Signal.Listener<KeyEvent> keyListener;
    protected Component content;
    protected ColorBlock thumb;

    private float keyScroll = 0;

    public ScrollPane(Component content) {
        super();

        this.content = content;
        addToBack(content);

        width = content.width();
        height = content.height();

        content.camera = new Camera(0, 0, 1, 1, PixelScene.defaultZoom);
        Camera.add(content.camera);

        KeyEvent.addKeyListener(keyListener = new Signal.Listener<KeyEvent>() {
            @Override
            public boolean onSignal(KeyEvent keyEvent) {
                GameAction action = KeyBindings.getActionForKey(keyEvent);
                if (action == SPDAction.ZOOM_IN) {
                    if (keyEvent.pressed) {
                        keyScroll += 1;
                    } else {
                        keyScroll -= 1;
                    }
                    keyScroll = GameMath.gate(-1f, keyScroll, +1f);
                    return true;
                } else if (action == SPDAction.ZOOM_OUT) {
                    if (keyEvent.pressed) {
                        keyScroll -= 1;
                    } else {
                        keyScroll += 1;
                    }
                    keyScroll = GameMath.gate(-1f, keyScroll, +1f);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        Camera.remove(content.camera);
        KeyEvent.removeKeyListener(keyListener);
    }

    public void scrollTo(float x, float y) {
        Camera c = content.camera;
        c.scroll.set(x, y);
        if (c.scroll.x + width > content.width()) {
            c.scroll.x = content.width() - width;
        }
        if (c.scroll.x < 0) {
            c.scroll.x = 0;
        }
        if (c.scroll.y + height > content.height()) {
            c.scroll.y = content.height() - height;
        }
        if (c.scroll.y < 0) {
            c.scroll.y = 0;
        }
        thumb.y = this.y + height * c.scroll.y / content.height();
    }
    public void scrollToCurrentView() {// if the size has changed, this method will move the camera to a valid position
        scrollTo(content().camera.scroll.x, content().camera.scroll.y);
    }

    @Override
    public synchronized void update() {
        super.update();
        if (keyScroll != 0) {
            scrollTo(content.camera.scroll.x, content.camera.scroll.y + (keyScroll * 150 * Game.elapsed));
        }
    }

    @Override
    protected void createChildren(Object... params) {
        controller = new PointerController();
        add(controller);

        thumb = new ColorBlock(1, 1, THUMB_COLOR);
        thumb.am = THUMB_ALPHA;
        add(thumb);
    }

    @Override
    protected void layout() {
        layout(true);
    }

    protected void layout(boolean modifyContentCameraPosition) {

        content.setPos(0, 0);
        controller.x = x;
        controller.y = y;
        controller.width = width;
        controller.height = height;

        Camera cs = content.camera;
        if (modifyContentCameraPosition) {
            Point p = camera().cameraToScreen(x, y);
            cs.x = p.x;
            cs.y = p.y;
        }
        cs.resize((int) width, (int) height);

        thumb.visible = height < content.height();
        if (thumb.visible) {
            thumb.scale.set(2, height * height / content.height());
            thumb.x = right() - thumb.width();
            thumb.y = y + height * content.camera.scroll.y / content.height();
        }
    }

    public Component content() {
        return content;
    }

    public void onClick(float x, float y) {
    }

    public void onMiddleClick(float x, float y) {
    }

    public void onRightClick(float x, float y) {
    }

    @Override
    public void cancelClick() {
        super.cancelClick();

        controller.dragging = false;//from controller.onPointerUp()
        thumb.am = THUMB_ALPHA;

        controller.resetCurrentEvent();
    }

    public void givePointerPriority(){//call this after new content members were added (like revalidate() in Swing)
        controller.givePointerPriority();
    }

    public void setControllerSize(float width, float height) {
        controller.width = width;
        controller.height = height;
    }

    public class PointerController extends ScrollArea {

        private float dragThreshold;

        public PointerController() {
            super(0, 0, 0, 0);
            dragThreshold = PixelScene.defaultZoom * 8;
            doNotHover = true;
        }

        @Override
        protected void onScroll(ScrollEvent event) {
            PointF newPt = new PointF(lastPos);
            newPt.y -= event.amount * content.camera.zoom * 10;
            scroll(newPt);
            dragging = false;
        }

        @Override
        protected void onPointerDown(PointerEvent event) {
            super.onPointerDown(event);
            if (event != null) {
                content.redirectPointerEvent(event);
            }
        }

        @Override
        protected void onHoverStart(PointerEvent event) {
            super.onHoverStart(event);
            if (event != null) {
                content.redirectPointerEvent(event);
            }
        }

        @Override
        protected void onHoverEnd(PointerEvent event) {
            super.onHoverEnd(event);
            if (event != null) {
                content.redirectPointerEvent(event);
            }
        }

        @Override
        protected void onPointerUp(PointerEvent event) {

            if (dragging) {

                dragging = false;
                thumb.am = THUMB_ALPHA;

            } else {

                PointF p = content.camera.screenToCamera((int) event.current.x, (int) event.current.y);

                switch (event.button) {
                    case PointerEvent.LEFT:
                    default:
                        ScrollPane.this.onClick(p.x, p.y);
                        break;
                    case PointerEvent.RIGHT:
                        ScrollPane.this.onRightClick(p.x, p.y);
                        break;
                    case PointerEvent.MIDDLE:
                        ScrollPane.this.onMiddleClick(p.x, p.y);
                        break;
                }

                content.redirectPointerEvent(event);
            }
        }

        private boolean dragging = false;
        private PointF lastPos = new PointF();

        @Override
        protected void onDrag(PointerEvent event) {
            if (dragging) {

                scroll(event.current);

            } else if (PointF.distance(event.current, event.start) > dragThreshold) {

                dragging = true;
                lastPos.set(event.current);
                thumb.am = 1;
                content.cancelClick();

            } else {
                //redirect to button?
            }
        }

        private void scroll(PointF current) {
            Camera c = content.camera;

            c.shift(PointF.diff(lastPos, current).invScale(c.zoom));
            if (c.scroll.x + width > content.width()) {
                c.scroll.x = content.width() - width;
            }
            if (c.scroll.x < 0) {
                c.scroll.x = 0;
            }
            if (c.scroll.y + height > content.height()) {
                c.scroll.y = content.height() - height;
            }
            if (c.scroll.y < 0) {
                c.scroll.y = 0;
            }

            thumb.y = y + height * c.scroll.y / content.height();

            lastPos.set(current);

        }

        private void resetCurrentEvent() {
            controller.curEvent = null;
        }

    }
}