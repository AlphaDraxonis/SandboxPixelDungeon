package com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon.DEFAULT_DUNGEON;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.DungeonToJsonConverter;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.ExportDungeonWrapper;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Consumer;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

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

            try {
                if (SandboxPixelDungeon.platform.supportsOpenFileExplorer()
                        || (DeviceCompat.isAndroid() && SandboxPixelDungeon.platform.canReadExternalFilesIfUserGrantsPermission())) {
                    createNewDungeonBtn.setRect(0, height - 18, (width * 9 / 16f) - 2, 18);
                    openFileExplorer = new RedButton(DeviceCompat.isDesktop()
                            ? Messages.get(WndSelectDungeon.class, "open_file_explorer")
                            : Messages.get(WndSelectDungeon.class, "import")) {
                        @Override
                        protected void onClick() {
                            if (DeviceCompat.isDesktop()) {
                                SandboxPixelDungeon.platform.openFileExplorer(FileUtils.getFileHandleWithDefaultPath(
                                        FileUtils.getFileTypeForCustomDungeons(), CustomDungeonSaves.DUNGEON_FOLDER));
                            } else {
                                SandboxPixelDungeon.platform.selectFile(new Consumer<FileHandle>() {
                                    @Override
                                    public void accept(FileHandle fileHandle) {
                                        if (fileHandle != null) {
                                            CustomDungeonSaves.Info info = ExportDungeonWrapper.doImport(fileHandle);
                                            if (info != null) {
                                                allInfos.add(info);
                                                dungeonNames.add(info.name);
                                                updateList();
                                                Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                                            } else {
                                                Sample.INSTANCE.play(Assets.Sounds.CURSED);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                    add(openFileExplorer);
                    openFileExplorer.setRect(createNewDungeonBtn.right() + 2, height - 18, width - createNewDungeonBtn.width() - 2, 18);
                } else {
                    createNewDungeonBtn.setRect(0, height - 18, width, 18);
                }
            } catch (Exception ignored) {
                createNewDungeonBtn.setRect(0, height - 18, width, 18);
            }
        }

        listPane.setSize(width, createNewDungeonBtn == null ? height : createNewDungeonBtn.top());

        updateList();
    }

    private void updateList() {
        listPane.clear();
        for (CustomDungeonSaves.Info info : allInfos) {
            if (createNewDungeonBtn != null || info.numLevels > 0)
                listPane.addItem(new ListItem(info));
        }
        listPane.scrollToCurrentView();
    }

    protected void select(String customDungeonName) {
        EditorScene.openDifferentLevel = true;
        openDungeon(customDungeonName);
    }


    private class ListItem extends ScrollingListPane.ListItem {

        private final CustomDungeonSaves.Info info;

        protected RenderedTextBlock folderName;

        public ListItem(CustomDungeonSaves.Info info) {
            super(Icons.get(Icons.STAIRS), info.name);
            for (String n : dungeonNames) {
                if (info.name != n && info.name.trim().equals(n.trim())) {
                    folderName = PixelScene.renderTextBlock("(" + info.name + ")", 5);
                    add(folderName);
                    break;
                }
            }
            this.info = info;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);
            label.setHightlighting(false);
        }

        @Override
        protected void layout() {
            super.layout();
            if (folderName != null) {
                label.setPos(label.left(), label.top() - 1);
                folderName.setPos(label.left(), label.bottom() + 2);
            }
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

                RedButton exportJson = new RedButton(Messages.get(WndSelectDungeon.class, "export_json_label")) {
                    @Override
                    protected void onClick() {
                        String exportedName = info.name.replace('_', '-');
                        String fileName = "exports/" + info.name + ".json";
                        String destLocation = CustomDungeonSaves.getAbsolutePath(fileName).replace('_', '-');
                        Window w = new WndOptions(
                                Messages.get(WndSelectDungeon.class, "export_json_title", exportedName),
                                Messages.get(WndSelectDungeon.class, "export_json_body", destLocation),
                                Messages.get(WndSelectDungeon.class, "export_yes"), Messages.get(WndSelectDungeon.class, "export_no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    try {
                                        FileUtils.setDefaultFileType(FileUtils.getFileTypeForCustomDungeons());
                                        CustomDungeonSaves.writeClearText(fileName,
                                                DungeonToJsonConverter.getAsJson(CustomDungeonSaves.loadDungeon(info.name)));

                                        Window win = new WndOptions(
                                                Messages.get(WndSelectDungeon.class, "export_json_confirm_title", exportedName),
                                                Messages.get(WndSelectDungeon.class, "export_json_confirm_body", exportedName) +
                                                        (info.name.equals("dungeon") ? "" : Messages.get(WndSelectDungeon.class, "export_json_confirm_rename_hint")),
                                                Messages.get(WndSelectDungeon.class, "export_confirm_close"));
                                        if (Game.scene() instanceof EditorScene)
                                            EditorScene.show(win);
                                        else Game.scene().addToFront(win);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        SandboxPixelDungeon.reportException(e);
                                    } catch (CustomDungeonSaves.RenameRequiredException e) {
                                        e.showExceptionWindow();
                                    }
                                }
                            }
                        };
                        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                        else Game.scene().addToFront(w);
                    }
                };
                exportJson.enable(info.numLevels > 0);

                RedButton exportDun = new RedButton(Messages.get(WndSelectDungeon.class, "export_dun_label")) {
                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndExportDungeon(info));
                    }
                };
                exportDun.enable(info.numLevels > 0);

                IconButton rename = new IconButton(Icons.get(Icons.RENAME_ON)) {
                    @Override
                    protected void onClick() {
                        Window w = new WndTextInput(Messages.get(WndSelectDungeon.class, "rename_title"),
                                "",
                                info.name,
                                50,
                                false,
                                Messages.get(WndSelectDungeon.class, "rename_yes"),
                                Messages.get(WndSelectDungeon.class, "export_no")) {
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive && !text.isEmpty()) {
                                    for (String dungeonN : dungeonNames) {
                                        if (!dungeonN.equals(info.name) && dungeonN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                                            WndNewDungeon.showNameWarning();
                                            return;
                                        }
                                    }
                                    if (text.equals(DEFAULT_DUNGEON))
                                        WndNewDungeon.showNameWarning();
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

                IconButton copy = new IconButton(Icons.PASTE.get()) {
                    @Override
                    protected void onClick() {
                        Window w = new WndTextInput(Messages.get(WndSelectDungeon.class, "copy_title"),
                                "",
                                info.name + " " + Messages.get(WndSelectDungeon.class, "copy_extension"),
                                50,
                                false,
                                Messages.get(WndSelectDungeon.class, "copy_yes"),
                                Messages.get(WndSelectDungeon.class, "export_no")) {
                            {
                                textBox.selectAll();
                            }
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive && !text.isEmpty()) {
                                    for (String dungeonN : dungeonNames) {
                                        if (!dungeonN.equals(info.name) && dungeonN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                                            WndNewDungeon.showNameWarning();
                                            return;
                                        }
                                    }
                                    if (text.equals(DEFAULT_DUNGEON))
                                        WndNewDungeon.showNameWarning();
                                    else if (!text.equals(info.name)) {
                                        CustomDungeonSaves.Info newInfo = CustomDungeon.copyDungeon(info.name, text);
                                        if (newInfo != null) {
                                            dungeonNames.add(newInfo.name);
                                            allInfos.add(newInfo);
                                            updateList();
                                            WndInfoDungeon.this.hide();
                                            EditorScene.show(new WndInfoDungeon(newInfo));
                                        }
                                    }
                                }
                            }
                        };
                        EditorScene.show(w);
                    }

                    @Override
                    protected String hoverText() {
                        return Messages.get(WndSelectDungeon.class, "copy_yes");
                    }
                };
                add(copy);

                float iconWidth = rename.icon().width + copy.icon().width + 2;

                float pos = 2;
                title.maxWidth((int) (width - iconWidth - 2));
                title.setPos((title.maxWidth() - title.width()) * 0.5f, pos);

                rename.setRect(width - iconWidth, title.top() + (title.height() - rename.icon().height) * 0.5f, rename.icon().width, rename.icon().height);
                copy.setRect(rename.right() + 2, title.top() + (title.height() - rename.icon().height) * 0.5f, copy.icon().width, copy.icon().height);
                pos = title.bottom() + GAP;

                pos = statSlot(Messages.get(WndSelectDungeon.class, "num_floors"), Integer.toString(info.numLevels), pos) + GAP * 3;

//                pos += statSlot(Messages.get(WndSelectDungeon.class, "hashcode"), Integer.toHexString(info.hashcode), pos);

                cont.icon(Icons.get(Icons.ENTER));
                cont.setRect(0, pos, width / 2 - 1, 20);
                add(cont);

                erase.icon(Icons.get(Icons.CLOSE));
                erase.setRect(width / 2 + 1, pos, width / 2 - 1, 20);
                add(erase);

                pos = erase.bottom() + 3;

                exportJson.setRect(0, pos, width, 20);
                add(exportJson);
                pos = exportJson.bottom() + 2;

                exportDun.setRect(0, pos, width, 20);
                add(exportDun);

                resize(width, (int) exportDun.bottom() + 1);
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
        } catch (CustomDungeonSaves.RenameRequiredException e) {
            e.showExceptionWindow();
            return;
        }
        CustomTileLoader.loadTiles(EditorScene.openDifferentLevel);
        String lastEditedFloor = Dungeon.customDungeon.getLastEditedFloor();
        LevelScheme l;
        if (Dungeon.customDungeon.getNumFloors() == 0 || lastEditedFloor == null || (l = Dungeon.customDungeon.getFloor(lastEditedFloor)) == null)
            SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
        else {
            if (l.getType() != CustomLevel.class)
                SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
            else {
                l.loadLevel();
                if (l.getLevel() == null)
                    SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
                else EditorScene.open((CustomLevel) l.getLevel());
            }
        }
    }

    private static class WndExportDungeon extends WndOptions {

        private final CustomDungeonSaves.Info info;
        private String destLocation;
        private final String fileName;

        public WndExportDungeon(CustomDungeonSaves.Info info) {
            super( Messages.get(WndSelectDungeon.class, "export_dun_title", info.name),
                    getMessage(info),
                    Messages.get(WndSelectDungeon.class, "export_yes"),
                    Messages.get(WndSelectDungeon.class, "export_" +
                             (DeviceCompat.isDesktop() ? "open_location" : DeviceCompat.isAndroid() ? "to_downloads" : "yes")),
                    Messages.get(WndSelectDungeon.class, "export_no"));
            this.info = info;
            fileName = info.name.replace('_', '-') + CustomDungeonSaves.EXPORT_FILE_EXTENSION;
            destLocation = CustomDungeonSaves.getAbsolutePath("exports/" + fileName).replace('_', '-');
        }

        @Override
        protected void onSelect(int index) {
            try {
                if (index == 0) {
                    CustomDungeonSaves.exportDungeon(info.name, "exports/");
                    showSuccessful();
                } else if (index == 1) {
                    if (DeviceCompat.isDesktop()) {
                        if (SandboxPixelDungeon.platform.supportsOpenFileExplorer())
                            SandboxPixelDungeon.platform.openFileExplorer(FileUtils.getFileHandleWithDefaultPath(
                                    FileUtils.getFileTypeForCustomDungeons(), "exports/"));
                    } else if (DeviceCompat.isAndroid()) {
                        if (!SandboxPixelDungeon.platform.canReadExternalFiles()) {
                            Game.scene().addToFront(new WndExportDungeon(info));
                            Sample.INSTANCE.play(Assets.Sounds.CURSED);
                            return;
                        }
                        FileHandle dir = SandboxPixelDungeon.platform.getDownloadDirectory(fileName);
                        destLocation = dir.file().getAbsolutePath();
                        CustomDungeonSaves.exportDungeon(info.name, dir);
                        showSuccessful();
                    } else {
                        onSelect(0);
                    }
                }

            } catch (IOException e) {
                Sample.INSTANCE.play(Assets.Sounds.CURSED);
                e.printStackTrace();
                SandboxPixelDungeon.reportException(e);
                Game.scene().addToFront(new WndError(e.getMessage()));
            }
        }

        private void showSuccessful() {
            Sample.INSTANCE.play(Assets.Sounds.EVOKE);
            EditorScene.show(new WndOptions(
                    Messages.get(WndSelectDungeon.class, "export_dun_confirm_title", info.name),
                    Messages.get(WndSelectDungeon.class, "export_dun_confirm_body", destLocation)
                            + (ExportDungeonWrapper.hasCustomTiles(info.name) ? Messages.get(WndSelectDungeon.class, "export_dun_confirm_custom_tile_hint") : ""),
                    Messages.get(WndSelectDungeon.class, "export_confirm_close")));
        }

        private static String getMessage(CustomDungeonSaves.Info info) {
            String fileName = info.name.replace('_', '-') + CustomDungeonSaves.EXPORT_FILE_EXTENSION;
            String destLocation = CustomDungeonSaves.getAbsolutePath("exports/" + fileName).replace('_', '-');
            return Messages.get(WndSelectDungeon.class, "export_dun_body", destLocation);
        }
    }
}