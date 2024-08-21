package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditZoneComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.NewCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndColorPicker;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.utils.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NotAllowedInLua
public final class WndZones {

    private WndZones() {
    }

    private static final int MARGIN = 2;

    public static abstract class ZoneListPane extends ScrollingListPane {

        public void updateList() {
            clear();
            List<Zone> zones = new ArrayList<>(Dungeon.level.zoneMap.values());
            if (zones.isEmpty()) ;//TODO show that its empty
            Collections.sort(zones, (a, b) -> a.getName().compareTo(b.getName()));

            for (Zone zone : zones) {
                addItem(new ListItem(zone, this) {
                    @Override
                    protected void onClick() {
                        onSelect(zone, this);
                    }

                    @Override
                    protected void onRightClick() {
                        if (!onLongClick()) {
                            onClick();
                        }
                    }

                    @Override
                    protected boolean onLongClick() {
                        return onEdit(zone, this);
                    }
                });
            }
            scrollToCurrentView();
        }

        protected abstract void onSelect(Zone zone, ListItem listItem);

        public boolean onEdit(Zone zone, ListItem listItem) {
            EditorScene.show(new EditCompWindow(zone, listItem));
            return true;
        }

        public Camera getContentCamera() {
            return content.camera();
        }

        public static class ListItem extends AdvancedListPaneItem {

            private final Zone zone;
            protected IconButton editButton;

            public ListItem(Zone zone, ZoneListPane listPane) {
                super(Icons.get(Icons.ZONE), null, zone.getName());

                editButton = new IconButton(Icons.get(Icons.RENAME_ON)) {
                    @Override
                    protected void onClick() {
                        openEditWindow();
                    }
                };
                add(editButton);

                this.zone = zone;
                label.setHighlighting(false);

                onUpdate();
            }

            public void openEditWindow() {
                EditorScene.show(new EditCompWindow(zone, this));
            }

            @Override
            protected void layout() {
                super.layout();

                if (editButton != null) {
                    editButton.setRect(width - 3 - editButton.icon().width(), y + (height - editButton.icon().height()) * 0.5f, editButton.icon().width(), editButton.icon().height());
                    hotArea.width = editButton.left() - 1;
                }

            }

            @Override
            protected int getLabelMaxWidth() {
                return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
            }

            @Override
            public void onUpdate() {
                if (zone != null) {
                    icon.hardlight(zone.getColor());
                    label.text(zone.getName());
                }
                super.onUpdate();
            }
        }
    }


    public static class WndSelectZone extends SlowExtendWindow {

        public static final float TIME_TO_OPEN_WINDOW = 0.2f;//in seconds

        private static WndSelectZone instance;

        protected RenderedTextBlock desc;
        protected ZoneListPane listPane;
        protected RedButton createZone;

        public WndSelectZone(Consumer<Zone> onSelect, int posY) {

            super(
                    PixelScene.landscape() ? 200 : Math.min(150, (int) (PixelScene.uiCamera.width * 0.8)),
                    (int) ((PixelScene.uiCamera.height / 2) * 0.85f + posY),
                    Orientation.BOTTOM_TO_TOP,
                    new Point(0, posY));

            speed = endHeight / (TIME_TO_OPEN_WINDOW * 100);

            instance = this;

            desc = PixelScene.renderTextBlock(Messages.get(WndSelectZone.class, "desc"), 6);
            add(desc);

            listPane = new ZoneListPane() {

                @Override
                protected void onSelect(Zone zone, ListItem listItem) {
                    onSelect.accept(zone);
                    hide();
                }
            };
            add(listPane);

            createZone = new RedButton(Messages.get(this, "new_zone")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new WndNewZone());
                }
            };
            add(createZone);

            desc.maxWidth(endWidth - MARGIN * 2);
            desc.setPos(MARGIN, MARGIN);

            createZone.setRect(-2 + MARGIN*0.5f, endHeight - MARGIN - 18, endWidth - MARGIN, 18);
            PixelScene.align(createZone);

            listPane.setRect(-2, desc.bottom() + MARGIN * 2, endWidth, createZone.top() - MARGIN * 4 - desc.height());
            PixelScene.align(listPane);

            listPane.updateList();

            spCamera = listPane.getContentCamera();
            scrollPane = listPane;

        }

        public static void updateList() {
            if (instance == null) return;
            instance.listPane.updateList();
        }

        @Override
        public void destroy() {
            super.destroy();
            instance = null;
        }
    }

    public static class WndNewZone extends NewCompWindow<Zone> {

        private Image titleIcon;

        public WndNewZone() {
            super(new Zone());
        }

        @Override
        protected void create(String name) {
            if (name != null) {

                if (Dungeon.level.zoneMap.containsKey(name)) {
                    EditorUtilities.showDuplicateNameWarning();
                    return;
                }
                obj.name = name;

                Dungeon.level.zoneMap.put(obj.getName(), obj);
                Dungeon.level.levelScheme.zones.add(obj.getName());
                WndSelectZone.updateList();
                ZonePrompt.setSelectedZone(obj);
            }
            super.create(name);
        }

        @Override
        protected Image getIcon() {
            titleIcon = Icons.ZONE.get();
            titleIcon.hardlight(obj.getColor());
            return titleIcon;
        }

        @Override
        protected DefaultEditComp<Zone> createEditComp() {
            return new EditZoneComp(obj) {

                @Override
                public void updateObj() {
                    titleIcon.hardlight(obj.getColor());
                    super.updateObj();
                    spContent.setPos(0,0);
                }

                @Override
                protected void showColorPickerDialog() {
                    textBox.active = false;
                    EditorScene.show(new WndColorPicker(obj.getColor()) {
                        @Override
                        public void setSelectedColor(int color) {
                            super.setSelectedColor(color);
                            obj.setColor(color);
                            updateObj();
                        }

                        @Override
                        public void hide() {
                            super.hide();
                            textBox.active = true;
                        }
                    });
                }
            };
        }
    }

}