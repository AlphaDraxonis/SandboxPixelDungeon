package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.badlogic.gdx.files.FileHandle;
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
public class BugReportUploadDungeon extends Component implements MultiWindowTabComp.BackPressImplemented {
    
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
    
    
    public BugReportUploadDungeon(SimpleWindow window, String desc, DungeonPreview preview, Runnable onClose, MultiWindowTabComp.BackPressImplemented onBackPressed) {
        
        this.window = window;
        this.type = ServerCommunication.UploadType.REPORT_BUG;
        this.onBackPressed = onBackPressed;
        this.preview = preview;
        
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
		
		info = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "report_bug_info"), 6);
		add(info);
		
		selectDungeon = new ChooseObjectComp(Messages.get(UploadDungeon.class, "dungeon_bug") + ":") {
            @Override
            protected void doChange() {
                List<CustomDungeonSaves.Info> allInfos;
                allInfos = CustomDungeonSaves.getAllInfos();
                if (allInfos == null) return;
                Game.scene().addToFront(new WndSelectDungeon(allInfos, false, false) {
                    @Override
                    protected void select(CustomDungeonSaves.Info dungeonInfo) {
                        selectDungeon.selectObject(dungeonInfo.name);
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
    }
    
    @Override
    protected void layout() {
        height = 0;
        if (info != null) info.maxWidth((int) width);
        if (legalInfo != null) legalInfo.maxWidth((int) width);
        height = EditorUtilities.layoutCompsLinear(4, this, info, selectDungeon, description, userName, previewImageSelector, difficulty, legalInfo) + 1;
    }
    
    public Component createTitle() {
        return new IconTitle(Icons.BUG.get(), Messages.get(UploadDungeon.class, type.id() + "_title"));
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
    
    @Override
    public boolean onBackPressed() {
        if (onBackPressed != null) return onBackPressed.onBackPressed();
        return false;
    }
    
    public static void showUploadWindow() {
        if (!EditorUtilities.shouldConnectToInternet(() -> forceShowWindow())) {
            return;
        }
        forceShowWindow();
    }
    
    private static void forceShowWindow() {
        SimpleWindow w = new SimpleWindow() {
            private BugReportUploadDungeon uploadDungeon;
            {
                uploadDungeon = new BugReportUploadDungeon(this, null, null, this::hide, null);
                uploadDungeon.selectDungeon.selectObject(null);
                initComponents(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
            }
            @Override
            public void onBackPressed() {
            }
        };
        Game.scene().addToFront(w);
    }
}
