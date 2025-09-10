package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.TabResourceFiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.FileUtils;

import java.io.IOException;
import java.util.List;

@NotAllowedInLua
public class WndPreview extends Component {

    private final DungeonPreview preview;
    private final ServerDungeonList serverDungeonList;

    protected RenderedTextBlock desc, difficulty, creator, time, version;

    protected Component outsideSp;
    protected RedButton btnDownload;
    protected RedButton btnOpenPopupMenu;
    
    protected TabResourceFiles.FilePathListItem[] images;

    public WndPreview(DungeonPreview preview, ServerDungeonList dungeonList) {
        this.preview = preview;
        this.serverDungeonList = dungeonList;

        desc = PixelScene.renderTextBlock(preview.description, 7);
        add(desc);

        creator = PixelScene.renderTextBlock(Messages.get(WndPreview.class, "creator") + ": _" + preview.uploader + "_", 6);
        add(creator);

        difficulty = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "difficulty") + ": "
                + DungeonPreview.displayDifficulty(preview.difficulty), 6);
        add(difficulty);

        String displayTime;
//        displayTime = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Languages.getCurrentLocale()).format(new Date(preview.uploadTime));//absolute time
        displayTime = EditorUtilities.convertTimeDifferenceToString(System.currentTimeMillis() - preview.uploadTime);//relative time
        time = PixelScene.renderTextBlock(Messages.get(WndPreview.class, "time") + ": "
                + displayTime, 6);
        add(time);

        version = PixelScene.renderTextBlock(Messages.get(WndPreview.class, "version") + " " + preview.version, 6);
        add(version);
        
        if (preview.previewImageFileIDs != null) {
            images = new TabResourceFiles.FilePathListItem[preview.previewImageFileIDs.length];
            for (int i = 0; i < images.length; i++) {
                final int index = i;
                images[index] = new TabResourceFiles.FilePathListItem("Lädt…", null) {
                    @Override
                    protected void onClick() {
                        if (file != null) TabResourceFiles.viewResource(path, file);
                    }
                };
                preview.getPreviewImage(index, file -> {
                    Game.runOnRenderThread(() -> {
                        if (!images[index].isDestroyed()) images[index].set(Messages.format("Vorschaubild %d", index+1), file);
                    });
                });
                add(images[index]);
            }
        }
        

        btnDownload = new RedButton(Messages.get(WndPreview.class, "download")) {
            @Override
            protected void onClick() {
                userRequestsDownload(preview, preview.mostRecentDungeon);
            }
        };
        btnDownload.icon(Icons.DOWNLOAD.get());
        
        btnOpenPopupMenu = new RedButton("") {
            
            @Override
            protected void onClick() {
                DungeonScene.show(new OutsideSpMenuPopup(
                        (int) ((x + btnOpenPopupMenu.width() + 2 - camera().width / 2f)),
                        (int) (y - camera().height / 2f) - 3));
            }
            
            @Override
            protected String hoverText() {
                return Messages.get(WndPreview.class, "manage");
            }
        };
        btnOpenPopupMenu.icon(Icons.MENU.get());
        
        outsideSp = new Component() {
            {
                add(btnDownload);
                add(btnOpenPopupMenu);
            }
            
            private static final int HEIGHT = 16;
            
            @Override
            protected void layout() {
                
                if (PixelScene.landscape()) {
                    btnDownload.setRect(x + width / 5, y, width * 3 / 5, HEIGHT);
                    btnOpenPopupMenu.setRect(x + width - HEIGHT, y, HEIGHT, HEIGHT);
                } else {
                    btnDownload.setRect(x, y, width * 3 / 5, HEIGHT);
                    btnOpenPopupMenu.setRect(x + width - btnOpenPopupMenu.icon().width(), y + (HEIGHT - btnOpenPopupMenu.icon().height()) * 0.5f,
                            btnOpenPopupMenu.icon().width(), btnOpenPopupMenu.icon().height());
                }
                
                height = HEIGHT;
            }
        };
    }

    private void updatePreview() {
        serverDungeonList.closeCurrentSubMenu();
        WndPreview newWndPreview = new WndPreview(preview, serverDungeonList);
        serverDungeonList.changeContent(newWndPreview.createTitle(), newWndPreview, newWndPreview.getOutsideSp());
    }

    @Override
    protected void layout() {
        desc.maxWidth((int) width);
        height = 2;
        height = EditorUtilities.layoutCompsLinear(4, this, desc) + 6;
        height = EditorUtilities.layoutCompsLinear(4, this, creator, difficulty, time, version);
        
        if (images != null && images.length > 0) {
            height += 5;
            height = EditorUtilities.layoutCompsLinear(2, 18, this, images);
        }
        height += 2;
    }

    public Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(preview.title, 12);
        title.hardlight(Window.TITLE_COLOR);
        title.setHighlighting(false);
        return title;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    public static void checkOwnership(String dungeonID, Runnable onCorrect) {
        ServerCommunication.isCreator(dungeonID, new ServerCommunication.OwnershipCheckerCallback() {
            @Override
            protected void onSuccessful(Boolean value) {
                if (value != null && value) onCorrect.run();
                else {
                    if (value != null) {
                        Game.scene().addToFront(new WndError(Messages.get(WndPreview.class, "no_ownership")));
                    }
                }
            }
        });
    }
    
    public static void userRequestsDownload(DungeonPreview preview, String versionID) {
        List<CustomDungeonSaves.Info> allInfos = CustomDungeonSaves.getAllInfos();
        if (allInfos == null) return;
        
        String name = preview.title;
        if (CustomDungeon.illegalNameEnding(name)) name += " ";
        name = name.replace(' ', '_');
        for (CustomDungeonSaves.Info info : allInfos) {
            if (name.equals(info.name.replace(' ', '_'))) {
                confirmOverride(preview, versionID);
                return;
            }
        }
        
        actuallyStartDownload(preview, versionID);
    }

    private static void confirmOverride(DungeonPreview preview, String versionID) {
        Game.scene().addToFront(new WndOptions(Icons.WARNING.get(),
                Messages.get(WndPreview.class, "override_title"),
                Messages.get(WndPreview.class, "override_body"),
                Messages.get(WndPreview.class, "override_confirm"),
                Messages.get(WndChooseSubclass.class, "no")) {
            private float timer;

            @Override
            public void hide() {
               if (timer > 0.6f) super.hide();
            }

            @Override
            protected void onSelect(int index) {
                if (index == 0 && timer > 0.6f) {
                    actuallyStartDownload(preview, versionID);
                }
            }

            @Override
            public synchronized void update() {
                super.update();
                timer += Game.elapsed;
            }
        });
    }

    private static void actuallyStartDownload(DungeonPreview preview, String versionID) {
        ServerCommunication.downloadDungeon(preview.title, versionID, new ServerCommunication.OnDungeonReceive() {
            @Override
            protected void onSuccessful(CustomDungeonSaves.Info info) {
                Game.scene().addToFront(new WndOptions(
                        Messages.get(WndPreview.class, "successful_title"),
                        Messages.get(WndPreview.class, "successful_body", info.name), false,
                        Messages.get(WndPreview.class, "successful_play"),
                        Messages.get(WndSupportPrompt.class, "close")
                ) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            try {
                                Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(info.name);

                                FileUtils.resetDefaultFileType();
                                GamesInProgress.curSlot = GamesInProgress.firstEmpty();
                                if (GamesInProgress.curSlot == -1) {
                                    StartScene.skipDungeonSelection = true;
                                    SandboxPixelDungeon.switchNoFade(StartScene.class);
                                } else {
                                    SandboxPixelDungeon.switchScene(HeroSelectScene.class);
                                }

                            } catch (IOException | CustomDungeonSaves.RenameRequiredException e) {
                                SandboxPixelDungeon.reportException(e);
                            }

                        }
                    }
                });
            }
        });
    }
    
    
    private class OutsideSpMenuPopup extends PopupMenu {
        
        public OutsideSpMenuPopup(int posX, int posY) {
            
            finishInstantiation(new RedButton[] {
                    new RedButton(Messages.get(WndPreview.class, "show_versions") + " ") {
                        {
                            icon(Icons.CATALOG.get());
                            leftJustify = true;
                        }
                        
                        private Window waitWindow;
                        private boolean canceled;

                        @Override
                        protected void onClick() {
                            if (!preview.isVersionHistoryAvailable()) {
                                showLoadingWindow();
                            }
                            preview.getVersionHistory(historyEntries -> {
                                Game.runOnRenderThread(() -> {
                                    if (waitWindow != null) {
                                        waitWindow.hide();
                                        waitWindow = null;
                                    }
                                    
                                    if (canceled) {
                                        canceled = false;
                                        return;
                                    }
                                    
                                    if (historyEntries == null) {
                                        Game.scene().addToFront(new WndError(Messages.get(ServerCommunication.class, "error")) {
                                            {
                                                setHighlightingEnabled(false);
                                            }
                                        });
                                        return;
                                    }
                                    OutsideSpMenuPopup.this.hideImmediately();
                                    
                                    WndVersionHistory.show(preview, historyEntries);
                                });
                            });
                        }
                        
                        public void showLoadingWindow() {
                            waitWindow = new WndOptions(Messages.get(ServerCommunication.class, "wait_title"),
                                    Messages.get(ServerCommunication.class, "wait_body"),
                                    Messages.get(ServerCommunication.class, "wait_cancel")) {
                                {
                                    tfMessage.setHighlighting(false);
                                }
                                @Override
                                public void onBackPressed() {
                                }
                                
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        canceled = true;
                                        //don’t do anything else, we just don’t cancel the request
                                    }
                                }
                            };
                            Game.scene().addToFront(waitWindow);
                        }
                    },
                    new RedButton(Messages.get(WndPreview.class, "edit") + " ") {
                        {
                            icon(Icons.EDIT.get());
                            leftJustify = true;
                        }
                        
                        @Override
                        protected void onClick() {
                            checkOwnership(preview.dungeonID, () -> {
                                OutsideSpMenuPopup.this.hideImmediately();
                                serverDungeonList.closeCurrentSubMenu();
                                UploadDungeon uploadDungeon = new UploadDungeon(null, ServerCommunication.UploadType.CHANGE, preview.description, preview, WndPreview.this::updatePreview, () -> {
                                    updatePreview();
                                    return true;
                                }, UploadedDungeonRegistry.getAssociatedCoreID(preview.dungeonID));
                                serverDungeonList.changeContent(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
                            });
                        }
                    },
                    new RedButton(Messages.get(WndPreview.class, "delete") + " ") {
                        {
                            icon(Icons.TRASH.get());
                            leftJustify = true;
                        }
                        
                        @Override
                        protected void onClick() {
                            checkOwnership(preview.dungeonID, () -> {
                                OutsideSpMenuPopup.this.hideImmediately();
                                Game.scene().addToFront(new WndOptions(Icons.get(Icons.WARNING),
                                        Messages.get(WndPreview.class, "delete_title"),
                                        Messages.get(WndPreview.class, "delete_body"),
                                        Messages.get(WndGameInProgress.class, "erase_warn_yes"),
                                        Messages.get(WndGameInProgress.class, "erase_warn_no")) {
                                    @Override
                                    protected void onSelect(int index) {
                                        if (index == 0) {
                                            ServerCommunication.deleteDungeon(preview.dungeonID, preview.title, new ServerCommunication.UploadCallback() {
                                                @Override
                                                protected void onSuccessful(String dungeonID) {
                                                    preview.description = preview.uploader = preview.title = Messages.get(ServerCommunication.class, "deleted");
                                                    preview.version = "n/a";
                                                    preview.dungeonID = "";
                                                    preview.uploadTime = 0;
                                                    preview.mostRecentDungeon = "";
                                                    preview.versionFileID = "";
                                                    preview.previewImageFileIDs = null;
                                                    Game.runOnRenderThread(() -> {
                                                        ServerDungeonList.updatePage();
                                                        serverDungeonList.closeCurrentSubMenu();
                                                        Game.scene().addToFront(new WndMessage(Messages.get(WndPreview.class, "delete_successful")));
                                                    });
                                                }
                                            });
                                        }
                                    }
                                });
                            });
                        }
                    },
                
            }, posX, posY, 200);
        }
    }
    
}
