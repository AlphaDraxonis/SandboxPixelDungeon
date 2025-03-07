package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.TabResourceFiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NotAllowedInLua
public class UploadDungeon extends Component implements MultiWindowTabComp.BackPressImplemented {

    protected final SimpleWindow window;

    private final ServerCommunication.UploadType type;

    protected Component outsideSp;

    protected ChooseObjectComp selectDungeon;
    protected StringInputComp description;
    protected StringInputComp userName;
    protected PreviewImageSelector previewImageSelector;
    protected Spinner difficulty;


    protected RenderedTextBlock info, legalInfo;
    private final MultiWindowTabComp.BackPressImplemented onBackPressed;
    private final DungeonPreview preview;

    private boolean scrolledToBottom;


    public UploadDungeon(SimpleWindow window, ServerCommunication.UploadType type, String desc, DungeonPreview preview, Runnable onClose, MultiWindowTabComp.BackPressImplemented onBackPressed) {

        this.window = window;
        this.type = type;
        this.onBackPressed = onBackPressed;
        this.preview = preview;

        scrolledToBottom = type != ServerCommunication.UploadType.UPLOAD;

        outsideSp = new Component() {
            private RedButton cancel, upload;

            @Override
            protected void createChildren() {
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
            
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                if (previewImageSelector != null) {
                    previewImageSelector.reloadAll();
                }
            }
        };
        add(selectDungeon);

        description = new StringInputComp(Messages.get(UploadDungeon.class, type.id() + "_desc_label") +":", desc, 500, true, null) {
            @Override
            protected void onChange() {
                if (preview != null) ServerDungeonList.updateLayout();
                if (window != null) window.layout();
            }
        };
        add(description);

        if (type == ServerCommunication.UploadType.UPLOAD) {
            userName = new StringInputComp(Messages.get(UploadDungeon.class, "username_label") +":", null, 50, false, null);
            add(userName);
        }
        
        if (type == ServerCommunication.UploadType.UPLOAD) {
//            previewImageSelector = new PreviewImageSelector();
//            add(previewImageSelector);
        }

        if (type != ServerCommunication.UploadType.REPORT_BUG) {
            difficulty = new Spinner(new SpinnerTextModel(true, preview == null ? DungeonPreview.HARD : preview.difficulty,
                    new Object[]{DungeonPreview.EASY,
                            DungeonPreview.MEDIUM,
                            DungeonPreview.HARD,
                            DungeonPreview.EXPERT,
                            DungeonPreview.INSANE}) {
                @Override
                protected String displayString(Object value) {
                    if (value instanceof Integer) return DungeonPreview.displayDifficulty((int) value);
                    return super.displayString(value);
                }
            }, Messages.get(UploadDungeon.class, "difficulty"), 9);
            add(difficulty);
        }

        if (type == ServerCommunication.UploadType.UPLOAD) {
            legalInfo = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "legal_info"), 6);
            add(legalInfo);
        }
    }

    @Override
    protected void layout() {
        height = 0;
        if (info != null) info.maxWidth((int) width);
        if (legalInfo != null) legalInfo.maxWidth((int) width);
        height = EditorUtilities.layoutCompsLinear(4, this, info, selectDungeon, description, userName, previewImageSelector, difficulty, legalInfo) + 1;
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
    
    protected class PreviewImageSelector extends ItemContainerWithLabel<ImageFileItem> {
        
        private static final int MAX_NUM_SLOTS = 5;
        
        private FileHandle[] knownPreviewImages;
        
        public PreviewImageSelector() {
            super(new ArrayList<>(), null, Messages.get(PreviewImageSelector.class, "label"), false, 0, MAX_NUM_SLOTS);
		}
        
        @Override
        protected void showSelectWindow() {
            String dungeon = (String) selectDungeon.getObject();
            if (dungeon != null) {
                FileHandle destPath = getDir(dungeon);
                if (DeviceCompat.isDesktop()) {
                    String[] option = SandboxPixelDungeon.platform.supportsOpenFileExplorer()
                            ? new String[]{ Messages.get(WndSelectDungeon.class, "open_file_explorer") }
                            : new String[]{ };
                    DungeonScene.show(new WndOptions(Messages.get(this, "import_preview_image_title"),
                            Messages.get(this, "import_preview_image_info") + "\n\n" + Messages.get(this, "import_preview_image_location", destPath.path()), option) {
                        {
                            tfMessage.setHighlighting(false);
                        }
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                SandboxPixelDungeon.platform.openFileExplorer(getDir(dungeon));
                            }
                        }
                    });
                } else {
                    SandboxPixelDungeon.platform.selectImageFile(new Consumer<Object>() {
                        @Override
                        public void accept(Object obj) {
                            if (obj instanceof Exception) {
                                DungeonScene.show(new WndError((Throwable) obj));
                            } else {
                                byte[] data = (byte[]) obj;
                                int nextFreeIndex = MAX_NUM_SLOTS;
                                for (int i = 0; i < knownPreviewImages.length; i++) {
                                    if (knownPreviewImages[i] == null) {
                                        nextFreeIndex = i;
                                        break;
                                    }
                                }
                                FileHandle dest = destPath.child(Messages.format("preview%d.png", (nextFreeIndex+1)));
                                dest.writeBytes(data, false);
                                previewImageSelector.syncPreviewImageFiles();
                            }
                        }
                    });
                }
            } else {
                DungeonScene.show(new WndMessage(Messages.get(this, "select_dungeon_first")));
            }
        }
        
        @Override
        protected void showWndEditItemComp(ItemContainer<ImageFileItem>.Slot slot, Item item) {
            TabResourceFiles.viewResource(((ImageFileItem) item).getObject().path(), ((ImageFileItem) item).getObject());
        }
        
        @Override
        protected boolean removeSlot(ItemContainer<ImageFileItem>.Slot slot) {
            DungeonScene.show(new WndOptionsCondensed(Messages.get(this, "delete_preview_image_title"),
                    Messages.get(this, "delete_preview_image_desc"),
                    Messages.get(HeroSelectScene.class, "daily_yes"), Messages.get(HeroSelectScene.class, "daily_no")) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        PreviewImageSelector.super.removeSlot(slot);
                        ((ImageFileItem) slot.item()).getObject().delete();
                    }
                }
            });
            return true;
        }
        
        @Override
        protected void onSlotNumChange() {
            if (window != null) window.layout();
        }
        
        private float lastSynced;
        
        @Override
        public synchronized void update() {
            super.update();
            lastSynced += Game.elapsed;
            if (lastSynced >= 2) {
                syncPreviewImageFiles();
            }
        }
        
        public void syncPreviewImageFiles() {
            lastSynced = 0;
            
            String dungeon = (String) selectDungeon.getObject();
            if (dungeon != null) {
                FileHandle[] newPreviewImages = new FileHandle[knownPreviewImages.length];
                
                FileHandle dungeonDir = getDir(dungeon);
                FileHandle[] files = dungeonDir.list(".png");
                
                for (int i = 0; i < files.length; i++) {
                    String name = files[i].name();
                    if (name.length() == 12 && name.startsWith("preview")) {
                        char num = name.charAt(7);
                        if (Character.isDigit(num)) {
                            int index = Integer.parseInt(Character.toString(num)) - 1;
                            newPreviewImages[index] = files[i];
                        }
                    }
                }
                
                for (int i = 0; i < knownPreviewImages.length; i++) {
                    String prev = knownPreviewImages[i] == null ? null : knownPreviewImages[i].path();
                    String now = newPreviewImages[i] == null ? null : newPreviewImages[i].path();
                    
                    if (!Objects.equals(prev, now)) {
                        //update
                        knownPreviewImages = newPreviewImages;
                        for (Slot slot : slots) {
                            super.removeSlot(slot);
                        }
                        for (FileHandle knownPreviewImage : knownPreviewImages) {
                            if (knownPreviewImage != null) {
                                previewImageSelector.addNewItem(new ImageFileItem(knownPreviewImage));
                            }
                        }
                        return;
                    }
                    
                }
            }
            
        }
        
        public void reloadAll() {
            knownPreviewImages = new FileHandle[MAX_NUM_SLOTS];
            syncPreviewImageFiles();
        }
        
        private FileHandle getDir(String dungeonName) {
            return FileUtils.getFileHandleWithDefaultPath(FileUtils.getFileTypeForCustomDungeons(), CustomDungeonSaves.DUNGEON_FOLDER + dungeonName.replace(' ', '_') + "/");
        }
    }


    private void uploadDungeon(Runnable onSuccessful) {
        if (type == ServerCommunication.UploadType.UPLOAD) {

            if (!scrolledToBottom) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "scroll_to_bottom")));
                return;
            }

            if (!SPDSettings.canUploadToServer() && !DeviceCompat.isDebug()) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "wait_upload")));
                return;
            }

            String dungeon = (String) selectDungeon.getObject();
            if (dungeon == null) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "no_dungeon")));
                return;
            }
            String uploader = userName.getText();
            if (uploader == null || uploader.equals(userName.defaultValue) || ("AlphaDraxonis".equals(uploader) && !DeviceCompat.isDebug())) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "no_username")));
                return;
            }

            ServerCommunication.uploadDungeon(dungeon, description.getText(), uploader, (int) difficulty.getValue(), new ServerCommunication.UploadCallback() {
                @Override
                protected void onSuccessful(String dungeonFileID) {
                    SPDSettings.increaseUploadTimer();
                    onSuccessful.run();
                    Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "upload_successful")));
                }
            });
        } else if (type == ServerCommunication.UploadType.CHANGE) {

            if (!SPDSettings.canUpdateToServer() && !DeviceCompat.isDebug()) {
                Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, "wait_update")));
                return;
            }

            String dungeon = (String) selectDungeon.getObject();
            ServerCommunication.updateDungeon(preview, dungeon, description.getText(), (int) difficulty.getValue(), new ServerCommunication.UploadCallback() {
                @Override
                protected void onSuccessful(String dungeonFileID) {
                    SPDSettings.increaseUpdateTimer();
                    if (dungeon != null) preview.title = dungeon;
                    preview.dungeonFileID = dungeonFileID;
                    preview.uploadTime = System.currentTimeMillis();
                    preview.description = description.getText();
                    preview.version = Game.version;
                    preview.difficulty = (int) difficulty.getValue();
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
            if (!EditorUtilities.shouldConnectToInternet(() -> forceShowWindow(type, preselectDungeon))) return;
        }
        forceShowWindow(type, preselectDungeon);
    }

    private static void forceShowWindow(ServerCommunication.UploadType type, String preselectDungeon) {
        SimpleWindow w = new SimpleWindow() {
            private UploadDungeon uploadDungeon;
            {
                uploadDungeon = new UploadDungeon(this, type, null, null, this::hide, null);
                uploadDungeon.selectDungeon.selectObject(preselectDungeon);
                initComponents(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
            }
            @Override
            public void onBackPressed() {
            }

            @Override
            protected void onScroll(ScrollPane sp) {
                super.onScroll(sp);
                if (uploadDungeon != null && sp.content().camera.scroll.y >= sp.content().height() - sp.height() - 2) {
                    uploadDungeon.scrolledToBottom = true;
                }
            }
        };
        Game.scene().addToFront(w);
    }
}
