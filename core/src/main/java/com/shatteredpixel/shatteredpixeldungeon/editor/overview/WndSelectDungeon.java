package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.badlogic.gdx.Files;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.DungeonToJsonConverter;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WndSelectDungeon extends Window {


    protected ScrollingListPane listPane;
    protected RedButton createNewDungeonBtn;

    private List<CustomDungeonSaves.Info> allInfos;

    public WndSelectDungeon(List<CustomDungeonSaves.Info> allInfos, boolean showAddButton) {
        this.allInfos = allInfos;

        resize(PixelScene.landscape() ? 220 : 120, (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new ScrollingListPane();
        add(listPane);

        Set<String> otherDungeonNames = new HashSet<>();
        for (CustomDungeonSaves.Info info : allInfos) otherDungeonNames.add(info.name);

        if (showAddButton) {
            createNewDungeonBtn = new RedButton("New Dungeon") {
                @Override
                protected void onClick() {
                    Game.scene().addToFront(new WndNewDungeon(otherDungeonNames));
                }
            };
            add(createNewDungeonBtn);
            createNewDungeonBtn.setRect(0, height - 18, width, 18);
        }

        listPane.setSize(width, createNewDungeonBtn == null ? height : createNewDungeonBtn.top());

        updateList();
    }

    private void updateList() {
        listPane.clear();
        for (CustomDungeonSaves.Info info : allInfos) {
            listPane.addItem(new ListItem(info));
        }
    }

    protected void select(String customDungeonName) {
        openDungeon(customDungeonName);
    }


    private class ListItem extends ScrollingListPane.ListItem {

        private final CustomDungeonSaves.Info info;

        public ListItem(CustomDungeonSaves.Info info) {
            super(Icons.get(Icons.STAIRS), info.name);
            this.info = info;
        }

        @Override
        protected boolean onLongClick() {
            Window w = new WndInfoDungeon(info);
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
            return true;
        }

        @Override
        protected void onClick() {
            select(info.name);
        }

        private class WndInfoDungeon extends Window {

            private static final int GAP = 6;

            public WndInfoDungeon(CustomDungeonSaves.Info info) {

                resize(PixelScene.landscape() ? 215 : PixelScene.uiCamera.width - 5, 100);

                RenderedTextBlock title = PixelScene.renderTextBlock(info.name, 10);
                title.hardlight(Window.TITLE_COLOR);
                title.maxWidth(width);
                add(title);

                RedButton cont = new RedButton(Messages.get(WndGameInProgress.class, "continue")) {
                    @Override
                    protected void onClick() {
                        hide();
                        select(info.name);
                    }
                };
                add(cont);

                RedButton erase = new RedButton(Messages.get(WndGameInProgress.class, "erase")) {
                    @Override
                    protected void onClick() {
                        super.onClick();

                        ShatteredPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                                "Do you really want to delete this dungeon?",
                                "All floors will be deleted, but you can still continue games that were started with that dungeon.",
                                "Yes", "No") {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    CustomDungeon.deleteDungeon(info.name);
                                    allInfos.remove(info);
                                    updateList();
                                    WndInfoDungeon.this.hide();
                                }
                            }
                        });
                    }
                };

                RedButton export = new RedButton("Export as Json") {
                    @Override
                    protected void onClick() {
                        Window w = new WndOptions(
                                "Export \""+info.name+"\" as a Json file?",
                                "It will be exported to Android/data/com.alphadraxonis.sandboxpd/files/exports/"+info.name+".json\n"+//TODO add platform differences!
                                "If the file already exists, it will be overriden.\n"+
                                "The Json file can be used for \"Custom Pixel Dungeon\" by QuasiStellar, but not all features are supported.",
                                "Export", "Cancel") {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    try {
                                        CustomDungeonSaves.setFileType(Files.FileType.External);
                                        CustomDungeonSaves.writeClearText("exports/"+info.name+".json",
                                                DungeonToJsonConverter.getAsJson(CustomDungeonSaves.loadDungeon(info.name)));

                                        Window win = new WndOptions(
                                                "Successfully exported \""+info.name+"\" as a Json file",
                                                "\""+info.name+"\" was successfully exported!\nCheck it out to see which settings were not exported."+
                                                        (info.name.equals("dungeon")?"":"\nDon't forget to rename it to dungeon.json if you want to use it in Custom PD."),
                                                "Close");
                                        if (Game.scene() instanceof EditorScene) EditorScene.show(win);
                                        else Game.scene().addToFront(win);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ShatteredPixelDungeon.reportException(e);
                                    }
                                }
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };


                float pos = 0;
                title.setPos((width - title.width()) * 0.5f, pos);
                pos = title.bottom() + GAP;

                pos = statSlot("Num floors", Integer.toString( info.numLevels), pos) + GAP * 2;

                cont.icon(Icons.get(Icons.ENTER));
                cont.setRect(0, pos, width / 2 - 1, 20);
                add(cont);

                erase.icon(Icons.get(Icons.CLOSE));
                erase.setRect(width / 2 + 1, pos, width / 2 - 1, 20);
                add(erase);

                pos = erase.bottom() + 3;

                export.setRect(0, pos, width, 20);
                add(export);

                resize(width, (int) export.bottom() + 1);
            }

            private float statSlot(String label, String value, float pos) {

                RenderedTextBlock txt = PixelScene.renderTextBlock(label, 8);
                txt.setPos(0, pos);
                add(txt);

                int size = 8;
                if (value.length() >= 14) size -= 2;
                if (value.length() >= 18) size -= 1;
                txt = PixelScene.renderTextBlock(value, size);
                txt.setPos(width * 0.55f, pos + (6 - txt.height()) / 2);
                PixelScene.align(txt);
                add(txt);

                return GAP + txt.height();
            }

        }

    }

    private static void openDungeon(String name) {
        try {
            Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(name);
        } catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
        String lastEditedFloor = Dungeon.customDungeon.getLastEditedFloor();
        if (Dungeon.customDungeon.getNumFloors() == 0 || lastEditedFloor == null)
            ShatteredPixelDungeon.switchNoFade(FloorOverviewScene.class);
        else {
            LevelScheme l = Dungeon.customDungeon.getFloor(lastEditedFloor);
            if (l.getType() != CustomLevel.class)
                ShatteredPixelDungeon.switchNoFade(FloorOverviewScene.class);
            else {
                l.loadLevel();
                EditorScene.open((CustomLevel) l.getLevel());
            }
        }
    }
}