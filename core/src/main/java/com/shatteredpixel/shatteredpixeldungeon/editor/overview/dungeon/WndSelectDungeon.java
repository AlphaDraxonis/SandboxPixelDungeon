package com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.UploadDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon.DEFAULT_DUNGEON;

@NotAllowedInLua
public class WndSelectDungeon extends Window {

    private enum SortMode {
        ALPHABETICALLY,
        LAST_MODIFIED;

        public SortMode nextMode() {
            switch (this) {
				case ALPHABETICALLY: return LAST_MODIFIED;
				case LAST_MODIFIED: return ALPHABETICALLY;
			}
            return LAST_MODIFIED;
        }
    }

    protected ScrollingListPane listPane;
    protected RedButton createNewDungeonBtn, openFileExplorer;
    protected IconButton sort;

    private SortMode sortMode = SortMode.LAST_MODIFIED;

    private List<CustomDungeonSaves.Info> allInfos;
    private CustomDungeonSaves.Info featuredInfo;
    private Set<String> dungeonNames;

    public WndSelectDungeon(List<CustomDungeonSaves.Info> allInfos, boolean showAddButton) {
        this(allInfos, showAddButton, null);
    }

    public WndSelectDungeon(List<CustomDungeonSaves.Info> allInfos, boolean showAddButton, CustomDungeonSaves.Info featuredInfo) {
        this.allInfos = allInfos;
        this.featuredInfo = featuredInfo;

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new ScrollingListPane() {
            @Override
            protected void layout() {
                layout(true);

                content.setSize(width, 0);

                Component[] comps = getItems();
                for (int i = 0; i < comps.length; i++) {
                    if (comps[i] instanceof PlayAgain) {
                        content.setSize(width, EditorUtilities.layoutCompsLinear(2, content, comps[i]) + 2);
                        comps[i] = null;
                    }
                }
                content.setSize(width, EditorUtilities.layoutStyledCompsInRectangles(2, width, content, comps));
            }
        };
        add(listPane);

        dungeonNames = new HashSet<>();
        for (CustomDungeonSaves.Info info : allInfos) dungeonNames.add(info.name);

        if (dungeonNames.size() > 3) {
            sort = new IconButton(Icons.SORT.get()) {
                @Override
                protected void onClick() {
                    sortMode = sortMode.nextMode();
                    updateList();
                }

                @Override
                protected void layout() {
                    super.layout();
                    hotArea.x--;
                    hotArea.y--;
                    hotArea.width += 2;
                    hotArea.height += 2;
                }
            };
            add(sort);
            sort.setRect(width - 1 - sort.icon().width(), 1, sort.icon().width(), sort.icon().height());
        }

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

        listPane.setSize(width, createNewDungeonBtn == null ? height : createNewDungeonBtn.top() - 1);

        updateList();
    }

    private void updateList() {
        listPane.clear();
        Collections.sort(allInfos, (o1, o2) -> {
			switch (sortMode) {
				case ALPHABETICALLY: return o1.name.compareTo(o2.name);
				case LAST_MODIFIED: return Long.compare(o2.lastModified, o1.lastModified);
			}
			return 0;
		});
        if (featuredInfo != null) {
            listPane.addItemNoLayouting(new PlayAgain(featuredInfo));
        }
        for (CustomDungeonSaves.Info info : allInfos) {
            if (createNewDungeonBtn != null || info.numLevels > 0)
                listPane.addItemNoLayouting(new ListItem(info));
        }
        listPane.nowLayout();
        listPane.scrollToCurrentView();
        listPane.givePointerPriority();
        if (sort != null) sort.givePointerPriority();
    }

    protected void select(String customDungeonName) {
        EditorScene.openDifferentLevel = true;
        openDungeon(customDungeonName);
    }


    @NotAllowedInLua
    private class ListItem extends StyledButton {

        private final CustomDungeonSaves.Info info;

        protected RenderedTextBlock folderName;

        protected RenderedTextBlock depthText;
        protected Image depthIcon;

        protected RenderedTextBlock lastModified;

        public ListItem(CustomDungeonSaves.Info info) {
            super(Chrome.Type.GREY_BUTTON_TR, info.name, 9);

            depthIcon = Icons.get(Icons.DEFAULT_DEPTH);
            depthIcon.scale.set(1.2f);
            add(depthIcon);
            depthText = PixelScene.renderTextBlock(Integer.toString(info.numLevels), 7);
            add(depthText);

            if (info.lastModified > 0) {
                long timeDiff = System.currentTimeMillis() - info.lastModified;
                String t;
                if (timeDiff < 86_400_000L) {
                    if (timeDiff < 0) t = null;
                    else {
                        t = EditorUtilities.convertTimeDifferenceToString(timeDiff);
                    }
                } else {
                    t = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(new Date(info.lastModified));
                }
                if (t != null) {
                    lastModified = PixelScene.renderTextBlock(t, 6);
                    lastModified.align(RenderedTextBlock.RIGHT_ALIGN);
                    add(lastModified);
                }
            }

            text.setHighlighting(false);
            text.hardlight(Window.TITLE_COLOR);
            text.align(RenderedTextBlock.CENTER_ALIGN);

            for (String n : dungeonNames) {
                if (info.name != n && info.name.trim().equals(n.trim())) {
                    folderName = PixelScene.renderTextBlock(Messages.get(WndSelectDungeon.class, "folder_name", info.name), 5);
                    folderName.align(RenderedTextBlock.CENTER_ALIGN);
                    folderName.setHighlighting(false);
                    add(folderName);
                    break;
                }
            }

            this.info = info;
        }

        @Override
        protected void layout() {

            float neededHeight = bg.marginVer() + 4;

            float componentWidth = 0;

            if (icon != null) componentWidth += icon.width() + 2;

            text.maxWidth( (int)(width - componentWidth - bg.marginHor() - 2) );
            neededHeight += text.height();

            if (folderName != null) {
                folderName.maxWidth(text.maxWidth());
                neededHeight += folderName.height() + 2;
            }

            if (depthIcon != null) {
                neededHeight += 3 + depthIcon.height();
            }

            float excessHeight = height - neededHeight;
            if (excessHeight < 0) {
                height = neededHeight;
                excessHeight = 0;
            }

            super.layout();

            depthIcon.x = x + 2;
            depthIcon.y = y + 2;
            depthText.setPos(depthIcon.x + depthIcon.width(), depthIcon.y + (depthIcon.height() - depthText.height()) * 0.5f);
            PixelScene.align(depthIcon);
            PixelScene.align(depthText);

            text.setPos(
                    x + (width() + componentWidth + text.width() + 2)/2f - text.width() - 1,
                    depthIcon.y + depthIcon.height() + excessHeight/2
            );

            if (folderName != null) {
                folderName.setPos(
                        x + (width() + componentWidth + folderName.width() + 2)/2f - folderName.width() - 1,
                        text.bottom() + 3
                );
            }

            if (lastModified != null) {
                lastModified.setPos(
                        x + width - bg.marginHor()/2f - lastModified.width(),
                        y + height - bg.marginVer()/2f - lastModified.height()
                );
            }

        }

        @Override
        protected boolean onLongClick() {
            EditorScene.show(new WndInfoDungeon(info));
            return true;
        }

        @Override
        protected void onClick() {
            select(info.name);
        }

        @NotAllowedInLua
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
                                                Messages.get(WndSupportPrompt.class, "close"));
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
                        EditorScene.show(w);
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

                RedButton upload = new RedButton(Messages.get(WndSelectDungeon.class, "upload_label")) {
                    @Override
                    protected void onClick() {
                        UploadDungeon.showUploadWindow(ServerCommunication.UploadType.UPLOAD, info.name);
                    }
                };
                upload.enable(!info.downloaded && info.numLevels > 0);
                upload.icon(Icons.UPLOAD.get());

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
                            {
                                setTextFieldFilter(TextInput.FILE_NAME_INPUT);
                            }
                            @Override
                            public void onSelect(boolean positive, String text) {
                                if (positive && !text.isEmpty()) {
                                    text = CustomDungeon.maybeFixIncorrectNameEnding(text);
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

                IconButton copy = new IconButton(Icons.COPY.get()) {
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
                pos = exportDun.bottom() + 2;

                upload.setRect(0, pos, width, 20);
                add(upload);

                resize(width, (int) upload.bottom() + 1);
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

                return txt.bottom();
            }

        }

    }

    private class PlayAgain extends Component {

        protected ListItem listItem;
        protected RenderedTextBlock playAgain;
        protected ColorBlock separator;

        public PlayAgain(CustomDungeonSaves.Info info) {
            listItem = new ListItem(info);
            add(listItem);

            playAgain = PixelScene.renderTextBlock(Messages.get(this, "label"), 10);
            playAgain.align(RenderedTextBlock.RIGHT_ALIGN);
            add(playAgain);

            separator = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
            add(separator);
        }

        @Override
        protected void layout() {

            float widthListItem = (width-2) / (PixelScene.landscape() ? 3 : 2);

            playAgain.maxWidth((int) (width - widthListItem - width/4));

            listItem.setRect(x + 0.9f*width - widthListItem, y + 10, widthListItem, Math.max(40, playAgain.height()));
            height = listItem.height() + 20;

            playAgain.setPos(listItem.left() - playAgain.width() - 0.05f*width, y + (height - playAgain.height()) * 0.5f);

            separator.x = x;
            separator.y = height - 2 + y;
            separator.size(width, 1);
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
                    Messages.get(WndSelectDungeon.class, "export_dun_confirm_body", destLocation),
                    Messages.get(WndSupportPrompt.class, "close")));
        }

        private static String getMessage(CustomDungeonSaves.Info info) {
            String fileName = info.name.replace('_', '-') + CustomDungeonSaves.EXPORT_FILE_EXTENSION;
            String destLocation = CustomDungeonSaves.getAbsolutePath("exports/" + fileName).replace('_', '-');
            return Messages.get(WndSelectDungeon.class, "export_dun_body", destLocation);
        }
    }
}