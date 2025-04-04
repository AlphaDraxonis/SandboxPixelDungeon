package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.*;
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

    protected RedButton download;
    protected IconButton delete, edit;

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

        download = new RedButton(Messages.get(WndPreview.class, "download")) {
            @Override
            protected void onClick() {
                List<CustomDungeonSaves.Info> allInfos = CustomDungeonSaves.getAllInfos();
                if (allInfos == null) return;

                String name = preview.title;
                if (CustomDungeon.illegalNameEnding(name)) name += " ";
                name = name.replace(' ', '_');
                for (CustomDungeonSaves.Info info : allInfos) {
                    if (name.equals(info.name.replace(' ', '_'))) {
                        confirmOverride();
                        return;
                    }
                }

                startDownload();
            }
        };
        download.icon(Icons.DOWNLOAD.get());
        add(download);

        edit = new IconButton(Icons.EDIT.get()) {
            @Override
            protected void onClick() {
                checkOwnership(() -> {
                    serverDungeonList.closeCurrentSubMenu();
                    UploadDungeon uploadDungeon = new UploadDungeon(null, ServerCommunication.UploadType.CHANGE, preview.description, preview, () -> updatePreview(), () -> {
                        updatePreview();
                        return true;
                    });
                    serverDungeonList.changeContent(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
                });
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndPreview.class, "edit");
            }
        };
        add(edit);

        delete = new IconButton(Icons.TRASH.get()) {
            @Override
            protected void onClick() {
                checkOwnership(() -> Game.scene().addToFront(new WndOptions(Icons.get(Icons.WARNING),
                        Messages.get(WndPreview.class, "delete_title"),
                        Messages.get(WndPreview.class, "delete_body"),
                        Messages.get(WndGameInProgress.class, "erase_warn_yes"),
                        Messages.get(WndGameInProgress.class, "erase_warn_no")) {
                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            ServerCommunication.deleteDungeon(preview.dungeonFileID, preview.title, new ServerCommunication.UploadCallback() {
                                @Override
                                protected void onSuccessful(String dungeonFileID) {
                                    preview.description =
                                            preview.uploader =
                                                    preview.title = Messages.get(ServerCommunication.class, "deleted");
                                    preview.version = "n/a";
                                    preview.dungeonFileID = "";
                                    preview.uploadTime = 0;
                                    Game.runOnRenderThread(() -> {
                                        ServerDungeonList.updatePage();
                                        serverDungeonList.closeCurrentSubMenu();
                                        Game.scene().addToFront(new WndMessage(Messages.get(WndPreview.class, "delete_successful")));
                                    });
                                }
                            });
                        }
                    }
                }));
            }

            @Override
            protected String hoverText() {
                return Messages.get(WndPreview.class, "delete");
            }
        };
        add(delete);
    }

    private void updatePreview() {
        serverDungeonList.closeCurrentSubMenu();
        WndPreview newWndPreview = new WndPreview(preview, serverDungeonList);
        serverDungeonList.changeContent(newWndPreview.createTitle(), newWndPreview, newWndPreview.getOutsideSp());
    }

    @Override
    protected void layout() {
        desc.maxWidth((int) width);
        height = 0;
        float posY = y + EditorUtilities.layoutCompsLinear(4, this, desc, creator, difficulty, time, version) + 5;
        download.setRect(x + width / 5, posY, width * 3 / 5, 16);
        height = posY - y + 16 + 2;

        if (PixelScene.landscape()) {
            delete.setRect(x + width - 16, posY, 16, 16);
            edit.setRect(delete.left() - 16, posY, 16, 16);
        } else {
            delete.setRect(x + width - delete.icon().width(), posY + (16 - delete.icon().width()) * 0.5f, delete.icon().width(), delete.icon().width());
            edit.setRect(delete.left() - edit.icon().width() - 1, posY + (16 - edit.icon().width()) * 0.5f, edit.icon().width(), edit.icon().width());
        }
    }

    public Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(preview.title, 12);
        title.hardlight(Window.TITLE_COLOR);
        title.setHighlighting(false);
        return title;
    }

    public Component getOutsideSp() {
        return null;
    }

    private void checkOwnership(Runnable onCorrect) {
        ServerCommunication.isCreator(preview.dungeonFileID, new ServerCommunication.OwnershipCheckerCallback() {
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

    private void confirmOverride() {
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
                    startDownload();
                }
            }

            @Override
            public synchronized void update() {
                super.update();
                timer += Game.elapsed;
            }
        });
    }

    private void startDownload() {
        ServerCommunication.downloadDungeon(preview.title, preview.dungeonFileID, new ServerCommunication.OnDungeonReceive() {
            @Override
            protected void onSuccessful(CustomDungeonSaves.Info info) {
                Game.scene().addToFront(new WndOptions(
                        Messages.get(WndPreview.class, "successful_title"),
                        Messages.get(WndPreview.class, "successful_body", info.name),
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
}