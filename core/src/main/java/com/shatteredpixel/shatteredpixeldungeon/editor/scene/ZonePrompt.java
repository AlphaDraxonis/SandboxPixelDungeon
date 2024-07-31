package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndZones;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ToastWithButtons;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Collections;
import java.util.LinkedList;

public class ZonePrompt extends ToastWithButtons {

    private static Zone selectedZone;
    public static Mode mode = Mode.ADD;

    private static ZonePrompt instance;

    public enum Mode {
        ADD,
        REMOVE,
        EDIT;
    }

    private ColorBlock zoneColor;
    private SelectZoneButton selectZoneButton;

    public ZonePrompt() {

        instance = this;
        updateButtonColors(comps);

        zoneColor = new ColorBlock(1, 1, 0x8FD8D8D8) {
            @Override
            public void resetColor() {
                super.resetColor();
                if (selectedZone != null) hardlight(selectedZone.getColor());
            }
        };
        add(zoneColor);
        sendToBack(zoneColor);
        sendToBack(bg);

        zoneColor.resetColor();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

    @Override
    protected void createChildren() {
        super.createChildren();

        //Think before changing order!
        IconButton placeZone = new IconButton(Icons.PLUS.get()) {
            @Override
            protected void onClick() {
                mode = Mode.ADD;
                updateButtonColors(comps);
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateButtonColors(comps);
            }
        };
        add(placeZone);
        IconButton removeZone = new IconButton(Icons.CLOSE.get()) {
            @Override
            protected void onClick() {
                mode = Mode.REMOVE;
                updateButtonColors(comps);
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateButtonColors(comps);
            }
        };
        add(removeZone);
        IconButton editZone = new IconButton(Icons.EDIT.get()) {
            @Override
            protected void onClick() {
                mode = Mode.EDIT;
                updateButtonColors(comps);
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateButtonColors(comps);
            }
        };
        add(editZone);
        selectZoneButton = new SelectZoneButton();
        add(selectZoneButton);


        placeZone.setSize(16, 16);
        removeZone.setSize(16, 16);
        editZone.setSize(16, 16);
        selectZoneButton.setSize(16, 16);

        comps = new Component[]{placeZone, removeZone, editZone, selectZoneButton};
    }

    private static void updateButtonColors(Component[] comps) {

        if (mode == Mode.ADD) ((IconButton) comps[0]).icon().brightness(1.5f);
        else ((IconButton) comps[0]).icon().resetColor();

        if (mode == Mode.REMOVE) ((IconButton) comps[1]).icon().brightness(1.5f);
        else ((IconButton) comps[1]).icon().resetColor();

        if (mode == Mode.EDIT) ((IconButton) comps[2]).icon().brightness(1.5f);
        else ((IconButton) comps[2]).icon().resetColor();
    }

    public static void updateSelectedZoneColor() {
        if (instance != null && getSelectedZone() != null) {
            instance.zoneColor.hardlight(getSelectedZone().getColor());
        }
    }

    public static Zone getSelectedZone() {
        return selectedZone;
    }

    public static void setSelectedZone(Zone selectedZone) {
        ZonePrompt.selectedZone = selectedZone;
        if (instance != null) {
            ZonePrompt.updateSelectedZoneColor();
            instance.selectZoneButton.setSelectedZoneVisually(selectedZone);
            instance.layout();
            Toast.placeToastOnScreen(instance);
        }
    }

    public static Zone getFirstZoneAvailable(Level level) {
        LinkedList<Zone> zones = new LinkedList<>(level.zoneMap.values());
        if (!zones.isEmpty()) {
            Collections.sort(zones, (a, b) -> a.getName().compareTo(b.getName()));
            return zones.peekFirst();
        }
        return null;
    }

    @Override
    protected void layout() {
        super.layout();

        if (zoneColor != null) {
            zoneColor.size(width - bg.marginHor() + 4, height - bg.marginVer() + 4);
            zoneColor.x = x + bg.marginLeft() - 2;
            zoneColor.y = y + bg.marginTop() - 2;
            PixelScene.align(zoneColor);
        }
    }

    private static class SelectZoneButton extends Button {

        private RenderedTextBlock selectedZoneText;

        private IconButtonWithPublicMethods changeZone;

        private boolean extended;

        public SelectZoneButton() {

            super();
            setSelectedZoneVisually(getSelectedZone());
        }

        protected void setSelectedZoneVisually(Zone zone) {
            if (selectedZoneText != null) {
                selectedZoneText.text(zone == null ? Messages.get(Zone.class, "none_zone") : zone.getName());
            }
        }

        @Override
        protected void layout() {

            super.layout();

            selectedZoneText.maxWidth(100);

            width = 3 + selectedZoneText.width() + 1 + (height - 2) + 2;

            selectedZoneText.setPos(x + 3, y + 2 + (height - 4 - selectedZoneText.height()) * 0.5f);
            PixelScene.align(selectedZoneText);

            changeZone.setRect(selectedZoneText.right() + 1, y + 1, height - 2, height - 2);
            PixelScene.align(changeZone);
        }

        @Override
        protected void createChildren() {

            super.createChildren();

            changeZone = new IconButtonWithPublicMethods(Icons.FOLD.get()) {
                {
                    icon().originToCenter();
                }

                @Override
                public void onClick() {
                    extended = !extended;
                    EditorScene.show(new WndZones.WndSelectZone(ZonePrompt::setSelectedZone,
                            (int) (y - camera().height / 2f) - 15) {

                        @Override
                        public void hide() {
                            super.hide();
                            if (isExtending() != null) extended = isExtending();
                        }

                        @Override
                        public void hideImmediately() {
                            super.hideImmediately();
                            icon.angle = 0;
                        }

                        @Override
                        public void showImmediately() {
                            super.showImmediately();
                            icon.angle = 180;
                        }
                    });
                }

                @Override
                public void update() {
                    if (extended) {
                        icon.angle = Math.min(180, icon.angle + 180 * Game.elapsed / WndZones.WndSelectZone.TIME_TO_OPEN_WINDOW);
                    } else {
                        icon.angle = Math.max(0, icon.angle - 180 * Game.elapsed / WndZones.WndSelectZone.TIME_TO_OPEN_WINDOW);
                    }
                    super.update();
                }
            };
            add(changeZone);

            selectedZoneText = PixelScene.renderTextBlock(7);
            add(selectedZoneText);

        }

        @Override
        protected void onClick() {
            changeZone.onClick();
        }

        @Override
        protected void onPointerDown() {
            changeZone.onPointerDown();
        }

        @Override
        protected void onPointerUp() {
            changeZone.onPointerUp();
        }

        private static class IconButtonWithPublicMethods extends IconButton {

            public IconButtonWithPublicMethods(Image icon) {
                super(icon);
            }

            @Override
            public void onClick() {
                super.onClick();
            }

            @Override
            public void onPointerDown() {
                super.onPointerDown();
            }

            @Override
            public void onPointerUp() {
                super.onPointerUp();
            }
        }

    }

}