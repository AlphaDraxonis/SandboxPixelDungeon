package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.util.List;

public class UploadDungeon extends Component implements MultiWindowTabComp.BackPressImplemented {

    //select dungeon
    //descr
    //username
    //password + infp and infobtn

    protected final SimpleWindow window;

    private final ServerCommunication.UploadType type;

    protected Component outsideSp;

    protected ChooseObjectComp selectDungeon;
    protected StringInputComp description;
    protected StringInputComp userName;


    protected RenderedTextBlock info, legalInfo;
    private final MultiWindowTabComp.BackPressImplemented onBackPressed;
    private final DungeonPreview preview;


    public UploadDungeon(SimpleWindow window, ServerCommunication.UploadType type, String desc, DungeonPreview preview, Runnable onClose, MultiWindowTabComp.BackPressImplemented onBackPressed) {

        this.window = window;
        this.type = type;
        this.onBackPressed = onBackPressed;
        this.preview = preview;

        outsideSp = new Component() {
            private RedButton cancel, upload;

            @Override
            protected void createChildren(Object... params) {
                cancel = new RedButton(Messages.get(UploadDungeon.class, "cancel")) {
                    @Override
                    protected void onClick() {
                        onClose.run();
                    }
                };
                upload = new RedButton(Messages.get(UploadDungeon.class, type.id() + "_title")) {
                    @Override
                    protected void onClick() {
                        uploadDungeon(onClose);
                    }
                };
                add(cancel);
                add(upload);
            }

            @Override
            protected void layout() {
                float w = width / 2f;
                cancel.setRect(x, y, w, LevelTab.BUTTON_HEIGHT);
                upload.setRect(cancel.right() + LevelTab.GAP, y, width - w - LevelTab.GAP, LevelTab.BUTTON_HEIGHT);
                height = LevelTab.BUTTON_HEIGHT;
            }
        };

        if (type == ServerCommunication.UploadType.REPORT_BUG) {
            info = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "report_bug_info"), 6);
            add(info);
        }

        selectDungeon = new ChooseObjectComp(Messages.get(UploadDungeon.class, "dungeon"
                + (type == ServerCommunication.UploadType.REPORT_BUG ? "_bug" : "")) + ":") {
            @Override
            protected void doChange() {
                List<CustomDungeonSaves.Info> allInfos;
                allInfos = CustomDungeonSaves.getAllInfos();
                if (allInfos == null) return;
                for (CustomDungeonSaves.Info info : allInfos.toArray(new CustomDungeonSaves.Info[0])) {
                    if (info.downloaded) allInfos.remove(info);
                }
                Game.scene().addToFront(new WndSelectDungeon(allInfos, false) {
                    @Override
                    protected void select(String customDungeonName) {
                        selectDungeon.selectObject(customDungeonName);
                        hide();
                    }
                });
            }
        };
        add(selectDungeon);

        description = new StringInputComp(Messages.get(UploadDungeon.class, type.id() + "_desc_label"), desc, 500, true, null) {
            @Override
            protected void onChange() {
                if (preview != null) ServerDungeonList.updateLayout();
                if (window != null) window.layout();
            }
        };
        add(description);

        if (type == ServerCommunication.UploadType.UPLOAD) {

            userName = new StringInputComp(Messages.get(UploadDungeon.class, "username_label"), null, 50, false, null);
            add(userName);
        }

        if (type == ServerCommunication.UploadType.UPLOAD) {
            legalInfo = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "legal_info"), 6);
            add(legalInfo);
        }
    }

    @Override
    protected void layout() {
        height = 0;
        if (info != null) {
            info.maxWidth((int) width);
            height -= 4;
        }
        if (legalInfo != null) legalInfo.maxWidth((int) width);
        height = EditorUtilies.layoutCompsLinear(4, this, info, selectDungeon, description, userName, legalInfo) + 1;
    }

    public Component createTitle() {
        if (type == ServerCommunication.UploadType.REPORT_BUG) {
            return new IconTitle(Icons.BUG.get(), Messages.get(UploadDungeon.class, type.id() + "_title"));
        }
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(UploadDungeon.class, type.id() + "_title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }


    private void uploadDungeon(Runnable onSuccessful) {
        if (type == ServerCommunication.UploadType.UPLOAD) {

            if (!SPDSettings.canUploadedToServer()) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "wait_upload")));
                return;
            }

            String dungeon = (String) selectDungeon.getObject();
            if (dungeon == null) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "no_dungeon")));
                return;
            }
            String uploader = userName.getText();
            if (uploader == null || uploader.equals(userName.defaultValue)) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "no_username")));
                return;
            }

            ServerCommunication.uploadDungeon(dungeon, description.getText(), uploader, new ServerCommunication.UploadCallback() {
                @Override
                protected void onSuccessful(String dungeonFileID) {
                    SPDSettings.increaseUploadTimer();
                    onSuccessful.run();
                    Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "upload_successful")));
                }
            });
        } else if (type == ServerCommunication.UploadType.CHANGE) {

            if (!SPDSettings.canUpdatedToServer()) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "wait_update")));
                return;
            }

            String dungeon = (String) selectDungeon.getObject();
            ServerCommunication.updateDungeon(preview, dungeon, description.getText(), new ServerCommunication.UploadCallback() {
                @Override
                protected void onSuccessful(String dungeonFileID) {
                    SPDSettings.increaseUpdateTimer();
                    if (dungeon != null) preview.title = dungeon;
                    preview.dungeonFileID = dungeonFileID;
                    preview.uploadTime = System.currentTimeMillis();
                    preview.description = description.getText();
                    preview.version = Game.version;
                    ServerDungeonList.updatePage();
                    onSuccessful.run();
                    Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "update_successful")));
                }
            });
        } else if (type == ServerCommunication.UploadType.REPORT_BUG) {
            String desc = description.getText();
            if (desc == null || desc.trim().isEmpty()) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "no_bug_desc")));
                return;
            }
            ServerCommunication.reportBug((String) selectDungeon.getObject(), desc, new ServerCommunication.UploadCallback() {
                @Override
                protected void onSuccessful(String dungeonFileID) {
                    onSuccessful.run();
                    Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "bug_report_successful")));
                }
            });
        }
    }

    @Override
    public boolean onBackPressed() {
        if (onBackPressed != null) return onBackPressed.onBackPressed();
        return false;
    }

    public static void showUploadWindow(ServerCommunication.UploadType type, String preselectDungeon) {
        if (type != ServerCommunication.UploadType.CHANGE) {
            if (!EditorUtilies.shouldConnectToInternet(() -> forceShowWindow(type, preselectDungeon))) return;
        }
        forceShowWindow(type, preselectDungeon);
    }

    private static void forceShowWindow(ServerCommunication.UploadType type, String preselectDungeon) {
        SimpleWindow w = new SimpleWindow(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f)) {
            @Override
            public void onBackPressed() {
            }
        };
        UploadDungeon uploadDungeon = new UploadDungeon(w, type, null, null, w::hide, null);
        uploadDungeon.selectDungeon.selectObject(preselectDungeon);
        w.initComponents(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
        Game.scene().addToFront(w);
    }
}