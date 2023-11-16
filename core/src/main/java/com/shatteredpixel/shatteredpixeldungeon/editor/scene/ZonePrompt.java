package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndZones;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ToastWithButtons;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

import java.util.Collections;
import java.util.LinkedList;

public class ZonePrompt extends ToastWithButtons {

    private static Zone selectedZone;//TODO tzz autoselect first available
    public static Mode mode = Mode.ADD;

    private static ZonePrompt instance;

    public enum Mode {
        ADD,
        REMOVE,
        EDIT;
    }

    public ZonePrompt() {
        super(createComponents());
        instance = this;
        updateColors();
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        instance = null;
    }

    private static Component[] createComponents() {

        //Think before changing order!
        IconButton placeZone = new IconButton(Icons.PLUS.get()) {
            @Override
            protected void onClick() {
                mode = Mode.ADD;
                updateColors();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateColors();
            }
        };
        IconButton removeZone = new IconButton(Icons.CLOSE.get()) {
            @Override
            protected void onClick() {
                mode = Mode.REMOVE;
                updateColors();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateColors();
            }
        };
        IconButton editZone = new IconButton(Icons.PLUS.get()) {//tzz
            @Override
            protected void onClick() {
                mode = Mode.EDIT;
                updateColors();
            }

            @Override
            protected void onPointerUp() {
                super.onPointerUp();
                updateColors();
            }
        };
        SelectZoneButton selectZoneButton = new SelectZoneButton();


        placeZone.setSize(16, 16);
        removeZone.setSize(16, 16);
        editZone.setSize(16, 16);
        selectZoneButton.setSize(16, 16);

        return new Component[]{placeZone, removeZone, editZone, selectZoneButton};
    }

    private static void updateColors(){

        Component[] comps = instance.comps;
        if (mode == Mode.ADD) ((IconButton) comps[0]).icon().brightness( 1.5f );
        else ((IconButton) comps[0]).icon().resetColor();

        if (mode == Mode.REMOVE) ((IconButton) comps[1]).icon().brightness( 1.5f );
        else ((IconButton) comps[1]).icon().resetColor();

        if (mode == Mode.EDIT) ((IconButton) comps[2]).icon().brightness( 1.5f );
        else ((IconButton) comps[2]).icon().resetColor();
    }

    public static Zone getSelectedZone() {
        return selectedZone;
    }

    public static void setSelectedZone(Zone selectedZone) {
        //auto select
        if (selectedZone == null) {
            selectedZone = getFirstZoneAvailable(Dungeon.level);
        }

        if(ZonePrompt.selectedZone != selectedZone){
            ZonePrompt.selectedZone = selectedZone;
            if (instance != null) {
                instance.destroy();
                EditorScene.promptStatic(new ZonePrompt());//changing coordinates didn't work...
            }
        }
    }

    public static Zone getFirstZoneAvailable(Level level){
        LinkedList<Zone> zones = new LinkedList<>(level.zoneMap.values());
        if (!zones.isEmpty()){
            Collections.sort(zones, (a, b) -> a.getName().compareTo(b.getName()));
            return zones.peekFirst();
        }
        return null;
    }

    private static class SelectZoneButton extends Button {

        private NinePatch bg;
        private ColorBlock zoneColor;
        private RenderedTextBlock selectedZoneText;

        private IconButtonWithPublicMethods changeZone;

        private boolean extended;

        public SelectZoneButton() {

            super();
            setSelectedZoneVisually(getSelectedZone());

//            zoneColor.resetColor();
        }

        protected void setSelectedZoneVisually(Zone zone) {
            if (selectedZoneText != null) {
                selectedZoneText.text(zone == null ? "NONEtzz" : zone.getName());
                if (zone != null)
                    selectedZoneText.hardlight(zone.color);
            }
        }

        @Override
        protected void layout() {

            super.layout();

            selectedZoneText.maxWidth(100);

            width = 3 + selectedZoneText.width() + 1 + (height - 2) + 2;

            bg.x = x;
            bg.y = y;
            bg.size(width, height);
            PixelScene.align(bg);

            zoneColor.size(selectedZoneText.width() + 2, height - 2);
            zoneColor.x = x + 1;
            zoneColor.y = x + 1;
            PixelScene.align(zoneColor);

            selectedZoneText.setPos(x + 3, y + 2 + (height - 4 - selectedZoneText.height()) * 0.5f);
            PixelScene.align(selectedZoneText);

            changeZone.setRect(selectedZoneText.right() + 1, y + 1, height - 2, height - 2);
            PixelScene.align(changeZone);
        }

        @Override
        protected void createChildren(Object... params) {

            super.createChildren(params);

            bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
//            add(bg);

            zoneColor = new ColorBlock(1, 1, Window.TITLE_COLOR) {
                @Override
                public void resetColor() {
                    super.resetColor();
                    if (selectedZone != null)
                        hardlight(0x777700);
                }
            };
            add(zoneColor);

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