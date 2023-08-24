package com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon;

import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon.WndNewDungeon.DEFAULT_DUNGEON;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.DungeonToJsonConverter;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.exotic.ExoticPotion;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndGameInProgress;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WndSelectDungeon extends Window {


    protected ScrollingListPane listPane;
    protected RedButton createNewDungeonBtn, openFileExplorer;

    private List<CustomDungeonSaves.Info> allInfos;
    private Set<String> dungeonNames;

    public WndSelectDungeon(List<CustomDungeonSaves.Info> allInfos, boolean showAddButton) {
        this.allInfos = allInfos;

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new ScrollingListPane();
        add(listPane);

        dungeonNames = new HashSet<>();
        for (CustomDungeonSaves.Info info : allInfos) dungeonNames.add(info.name);

        if (showAddButton) {
            createNewDungeonBtn = new RedButton(Messages.get(WndSelectDungeon.class, "new")) {
                @Override
                protected void onClick() {
                    Game.scene().addToFront(new WndNewDungeon(dungeonNames));
                }
            };
            add(createNewDungeonBtn);

            if(DeviceCompat.isDesktop() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)){
                createNewDungeonBtn.setRect(0, height - 18, (width*9/16f)-2, 18);
                openFileExplorer = new RedButton(Messages.get(WndSelectDungeon.class, "open_file_explorer")){
                    @Override
                    protected void onClick() {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.isSupported(Desktop.Action.OPEN);
                        File fileToOpen = FileUtils.getFileHandleWithDefaultPath(
                                FileUtils.getFileTypeForCustomDungeons(), CustomDungeonSaves.DUNGEON_FOLDER).file();
                        try {
                            desktop.open(fileToOpen);
                        } catch (IOException e) {
                            Game.runOnRenderThread(()->
                                    Game.scene().addToFront(new WndTitledMessage(Icons.get(Icons.WARNING), "ERROR",
                                            "Something went wrong:\n"+e.getMessage())));
                        }
                    }
                };
                add(openFileExplorer);
                openFileExplorer.setRect(createNewDungeonBtn.right()+2, height - 18, width - createNewDungeonBtn.width() - 2, 18);
            }else{
                createNewDungeonBtn.setRect(0, height - 18, width, 18);
            }
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

                resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), 100);

                RenderedTextBlock title = PixelScene.renderTextBlock(info.name, 10);
                title.hardlight(Window.TITLE_COLOR);
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

                        SandboxPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                                Messages.get(WndSelectDungeon.class, "erase_title"),
                                Messages.get(WndSelectDungeon.class, "erase_body"),
                                Messages.get(ExoticPotion.class, "yes"), Messages.get(ExoticPotion.class, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    CustomDungeon.deleteDungeon(info.name);
                                    allInfos.remove(info);
                                    dungeonNames.remove(info.name);
                                    updateList();
                                    WndInfoDungeon.this.hide();
                                }
                            }
                        });
                    }
                };

                RedButton export = new RedButton(Messages.get(WndSelectDungeon.class, "export_label")) {
                    @Override
                    protected void onClick() {
                        String exportedName = info.name.replace('_','-');
                        String fileName = "exports/" + info.name + ".json";
                        String destLocation = CustomDungeonSaves.getAbsolutePath(fileName).replace('_','-');
                        Window w = new WndOptions(
                                Messages.get(WndSelectDungeon.class, "export_title", exportedName),
                                Messages.get(WndSelectDungeon.class, "export_body", destLocation),
                                Messages.get(WndSelectDungeon.class, "export_yes"), Messages.get(WndSelectDungeon.class, "export_no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    try {
                                        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
                                        CustomDungeonSaves.writeClearText(fileName,
                                                DungeonToJsonConverter.getAsJson(CustomDungeonSaves.loadDungeon(info.name)));

                                        Window win = new WndOptions(
                                                Messages.get(WndSelectDungeon.class, "export_confirm_title", exportedName),
                                                Messages.get(WndSelectDungeon.class, "export_confirm_body", exportedName) +
                                                        (info.name.equals("dungeon") ? "" : Messages.get(WndSelectDungeon.class, "export_confirm_rename_hint")),
                                                Messages.get(WndSelectDungeon.class, "export_confirm_close"));
                                        if (Game.scene() instanceof EditorScene)
                                            EditorScene.show(win);
                                        else Game.scene().addToFront(win);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        SandboxPixelDungeon.reportException(e);
                                    }
                                }
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };
                export.enable(info.numLevels > 0);

                IconButton rename = new IconButton(Icons.get(Icons.RENAME_ON)){
                    @Override
                    protected void onClick() {
                        Window w = new WndTextInput(Messages.get(WndSelectDungeon.class, "rename_title"),
                                "",
                                info.name,
                                50,
                                false,
                                Messages.get(WndSelectDungeon.class, "rename_yes"),
                                Messages.get(WndSelectDungeon.class, "export_no")){
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive && !text.isEmpty()) {
                                    for (String dungeonN : dungeonNames) {
                                        if (!dungeonN.equals(info.name) && dungeonN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                                            WndNewDungeon.showNameWarnig();
                                            return;
                                        }
                                    }
                                    if (text.equals(DEFAULT_DUNGEON))
                                        WndNewDungeon.showNameWarnig();
                                    else if (!text.equals(info.name)) {
                                        CustomDungeon.renameDungeon(info.name, text);
                                        dungeonNames.remove(info.name);
                                        info.name = text;
                                        dungeonNames.add(info.name);
                                        updateList();
                                        WndInfoDungeon.this.hide();
                                        Window w = new WndInfoDungeon(info);
                                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                                        else Game.scene().addToFront(w);
                                    }
                                }
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }

                    @Override
                    protected String hoverText() {
                        return Messages.get(WndSelectDungeon.class, "rename_yes");
                    }
                };
                add(rename);
                float iconWidth = rename.icon().width;

                float pos = 2;
                title.maxWidth((int) (width - iconWidth - 2));
                title.setPos((title.maxWidth() - title.width()) * 0.5f, pos);

                rename.setRect(width - iconWidth, title.top() + (title.height() - rename.icon().height) * 0.5f, iconWidth, rename.icon().height);
                pos = title.bottom() + GAP;

                pos = statSlot(Messages.get(WndSelectDungeon.class, "num_floors"), Integer.toString(info.numLevels), pos) + GAP * 3;

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

    public static void openDungeon(String name) {
        try {
            Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(name);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
        String lastEditedFloor = Dungeon.customDungeon.getLastEditedFloor();
        LevelScheme l;
        if (Dungeon.customDungeon.getNumFloors() == 0 || lastEditedFloor == null || (l = Dungeon.customDungeon.getFloor(lastEditedFloor)) == null)
            SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
        else {
            if (l.getType() != CustomLevel.class)
                SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            else {
                l.loadLevel();
                EditorScene.open((CustomLevel) l.getLevel());
            }
        }
    }
}