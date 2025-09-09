/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.editor.server;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.services.server.UpdateDungeonAction;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication.UploadType.CHANGE;
import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication.UploadType.UPLOAD;

@NotAllowedInLua
public class UploadDungeon extends Component implements MultiWindowTabComp.BackPressImplemented {

    protected final SimpleWindow window;

    private ServerCommunication.UploadType type;
	private boolean typeSwitchingEnabled;

    protected Component outsideSp;

    protected RenderedTextBlock dungeonTitleLabel;
    protected TextInput dungeonTitleInput;
    protected StringInputComp description;
    protected StringInputComp userName;
    protected StyledSpinner difficulty;
    
    protected ColorBlock versionSeparator;
    
    protected RenderedTextBlock versionSegmentLabel;
    protected ChooseObjectComp selectDungeon;
    protected RenderedTextBlock versionTitleLabel, versionTitleOptionalHint;
    protected TextInput versionTitleInput;
    
    
    protected PreviewImageSelector previewImageSelector;


    private final MultiWindowTabComp.BackPressImplemented onBackPressed;
    private DungeonPreview preview;
    
    protected final String coreID;

    public UploadDungeon(SimpleWindow window, ServerCommunication.UploadType type, String desc, DungeonPreview preview, Runnable onClose, MultiWindowTabComp.BackPressImplemented onBackPressed, String coreID) {

        this.window = window;
        this.onBackPressed = onBackPressed;
        this.preview = preview;
		this.coreID = coreID;
		
		typeSwitchingEnabled = preview == null;
		
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
                        if (canUpload()) {
                            showLegalConfirmWindow(() -> uploadDungeon(onClose));
                        }
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
        
        dungeonTitleLabel = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "dungeon_title") +":", 8);
        add(dungeonTitleLabel);
        dungeonTitleInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom);
        dungeonTitleInput.setMaxLength(50);
        add(dungeonTitleInput);
        
        versionTitleLabel = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "version_title") +":", 8);
        add(versionTitleLabel);
        versionTitleOptionalHint = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "optional_hint"), 6);
        add(versionTitleOptionalHint);
        versionTitleInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom);
        versionTitleInput.setMaxLength(50);
        add(versionTitleInput);
        
        description = new StringInputComp(Messages.get(UploadDungeon.class, type.id() + "_desc_label") +":", desc, 600, true, null) {
            @Override
            protected void onChange() {
                refreshLayout();
            }
        };
        add(description);
		
		userName = new StringInputComp(Messages.get(UploadDungeon.class, "username_label") +":", null, 50, false, null);
		add(userName);
		
		previewImageSelector = new PreviewImageSelector();
		add(previewImageSelector);
		
		difficulty = new StyledSpinner(new SpinnerTextModel(true, preview == null ? DungeonPreview.HARD : preview.difficulty,
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
		
		versionSeparator = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
		add(versionSeparator);
		
		versionSegmentLabel = PixelScene.renderTextBlock(Messages.get(UploadDungeon.class, "version_segment"), 10);
		versionSegmentLabel.hardlight(Window.TITLE_COLOR);
		add(versionSegmentLabel);
		
		selectDungeon = new ChooseObjectComp(Messages.get(UploadDungeon.class, "dungeon") + ":") {
            @Override
            protected void doChange() {
                List<CustomDungeonSaves.Info> allInfos = CustomDungeonSaves.getAllInfos();
				
				
				for (CustomDungeonSaves.Info info : allInfos.toArray(new CustomDungeonSaves.Info[0])) {
					
					//only dungeons that haven't been downloaded can be uploaded
					if (info.downloaded) allInfos.remove(info);
					
					//if a fixed coreID is set, the dungeon must have the same coreID
					if (!coreID.isEmpty() && !coreID.equals(info.coreID)) allInfos.remove(info);
					
				}
				
				if (type == CHANGE && preview != null) {
					//if we have no dungeon that is linked to the serverDungeonID that we want to update
					if (!UploadedDungeonRegistry.isServerIDAssociated(preview.dungeonID)) {
						//in that case, all dungeons that are not uploaded as a different dungeon (and therefore have a serverDungeonID) are selectable
						for (CustomDungeonSaves.Info info : allInfos.toArray(new CustomDungeonSaves.Info[0])) {
							if (UploadedDungeonRegistry.hasDungeonBeenUploaded(info.coreID)) allInfos.remove(info);
						}
					}
					else {
						for (CustomDungeonSaves.Info info : allInfos.toArray(new CustomDungeonSaves.Info[0])) {
							//only dungeons that have the same coreID as associated with the serverDungeonID are selectable (must be at least one: see previous condition)
							if (!preview.dungeonID.equals(UploadedDungeonRegistry.getAssociatedServerID(info.coreID))) allInfos.remove(info);
						}
						//if the original dungeon was deleted, it can happen that there is no dungeon selectable anymore
						if (allInfos.isEmpty()) {
							//in that case, the serverDungeonID must be treated like it’s unknown
							//serverDungeonID is the ID whose key in this mapping does no longer exist on the device, and therefore the mapping is invalid.
							UploadedDungeonRegistry.dungeonDeletedFromServer(preview.dungeonID);
							//now try again recursively (we will not reach this a second time with the same preview.dungeonID)
							doChange();
							return;
						}
					}
					
				}
				
                Game.scene().addToFront(new WndSelectDungeon(allInfos, false, false) {
                    @Override
                    protected void select(CustomDungeonSaves.Info dungeonInfo) {
						DungeonPreview existingUpload = UploadedDungeonRegistry.getAssociatedPreview(dungeonInfo.coreID);
						
						if (typeSwitchingEnabled) {
							if (type == UPLOAD && existingUpload != null) {
								setType(CHANGE);
								
								if (dungeonTitleInput.getText() == null || dungeonTitleInput.getText().isEmpty() || dungeonTitleInput.getText().equals(selectDungeon.getObject()))
									dungeonTitleInput.setText(existingUpload.title);
								if (description.getText() == null || description.getText().isEmpty()) description.setText(existingUpload.description);
								difficulty.setValue(existingUpload.difficulty);
								UploadDungeon.this.preview = existingUpload;
								
								refreshLayout();
							}
							else if (type == CHANGE && existingUpload == null) {
								setType(UPLOAD);
								
								refreshLayout();
							}
						}
						
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
                if (dungeonTitleInput.getText().isEmpty()) {
                    dungeonTitleInput.setText((String) object);
                }
            }
        };
        add(selectDungeon);
        
        if (preview != null) {
            dungeonTitleInput.setText(preview.title);
            description.setText(preview.description);
        }
        
        dungeonTitleInput.gainFocus();
		
		setType(type);
    }
	
	private void setType(ServerCommunication.UploadType type) {
		this.type = type;
		
		if (window.getTitleComp() != null) ((RenderedTextBlock) window.getTitleComp()).text(Messages.titleCase(Messages.get(UploadDungeon.class, type.id() + "_title")));
		
		userName.setVisible(type == UPLOAD);
		
		versionSeparator.setVisible(type == CHANGE);
		versionSegmentLabel.setVisible(type == CHANGE);
	}
	
	private void refreshLayout() {
		if (window != null) window.layout();
		else ServerDungeonList.updateLayout();
	}
	
	@Override
    protected void layout() {
        height = 0;
        
        float posX = x, posY = y;
        
        if (type == UPLOAD) {
            
            //shared layout: version specific parts are mixed together with dungeon-wide stuff
            
            dungeonTitleLabel.maxWidth((int) (width/2));
            versionTitleLabel.maxWidth((int) (width/2));
            versionTitleOptionalHint.maxWidth((int) (width/2));
            float indent = Math.max(dungeonTitleLabel.width(), Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width())) + 4;
            //dungeon title and version title input fields are indented by the same amount
            
            dungeonTitleInput.setRect(posX + indent, posY, width - indent, Math.max(18, dungeonTitleLabel.height()));
            dungeonTitleLabel.setPos(posX, (float) (posY + (dungeonTitleInput.height() - dungeonTitleLabel.height()) * 0.5));
            posY = dungeonTitleInput.bottom() + 4;
            
            
            float spaceForVersionTitle = Math.max(18, versionTitleLabel.height() + versionTitleOptionalHint.height() + 1);
            
            versionTitleInput.setRect(posX + indent, posY, width - indent, spaceForVersionTitle);
            versionTitleLabel.setPos(posX + (Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width()) - versionTitleLabel.width()) * 0.5f, (float) (posY + (versionTitleInput.height() - (versionTitleLabel.height() + versionTitleOptionalHint.height() + 1)) * 0.5));
            versionTitleOptionalHint.setPos(posX + (Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width()) - versionTitleOptionalHint.width()) * 0.5f,  versionTitleLabel.bottom() + 1);
            posY = versionTitleInput.bottom() + 4;
            
            selectDungeon.setRect(posX, posY, width, 18);
            posY = selectDungeon.bottom() + 6;
            
            height = posY - y + 1;
            posY = EditorUtilities.layoutCompsLinear(3, this, description, userName, difficulty, previewImageSelector);
        }
        
        else if (type == CHANGE) {
            
            //has segment “new version” at the top:
            
            versionSegmentLabel.maxWidth((int) width);
            versionSegmentLabel.setPos(posX, posY + 2);
            posY = versionSegmentLabel.bottom() + 4;
            
            final float versionSegmentIndentation = 6;//for better visibility: indents the things that are version-specific by 6
            posX += versionSegmentIndentation;
            
                versionTitleLabel.maxWidth((int) ((width-versionSegmentIndentation)/2));
                versionTitleOptionalHint.maxWidth((int) ((width-versionSegmentIndentation)/2));
                float indent2 = Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width()) + 4;
                
                float spaceForVersionTitle = Math.max(18, versionTitleLabel.height() + versionTitleOptionalHint.height() + 1);
                versionTitleInput.setRect(posX + indent2, posY, width - versionSegmentIndentation - indent2, spaceForVersionTitle);
                versionTitleLabel.setPos(posX + (Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width()) - versionTitleLabel.width()) * 0.5f, (float) (posY + (versionTitleInput.height() - (versionTitleLabel.height() + versionTitleOptionalHint.height() + 1)) * 0.5));
                versionTitleOptionalHint.setPos(posX + (Math.max(versionTitleLabel.width(), versionTitleOptionalHint.width()) - versionTitleOptionalHint.width()) * 0.5f,  versionTitleLabel.bottom() + 1);
                posY = versionTitleInput.bottom() + 4;
                
                selectDungeon.setRect(posX, posY, width - versionSegmentIndentation, 18);
                posY = selectDungeon.bottom() + 4;
            
            posX -= versionSegmentIndentation;
            
            versionSeparator.size(width - 4, 1);
            versionSeparator.x = posX + 2;
            versionSeparator.y = posY;
            posY += 6;
            
            
            dungeonTitleLabel.maxWidth((int) (width/2));
            float indent1 = dungeonTitleLabel.width() + 4;
            dungeonTitleInput.setRect(posX + indent1, posY, width - indent1, Math.max(18, dungeonTitleLabel.height()));
            dungeonTitleLabel.setPos(posX, (float) (posY + (dungeonTitleInput.height() - dungeonTitleLabel.height()) * 0.5));
            posY = dungeonTitleInput.bottom() + 4;
            
            height = posY - y + 1;
            posY = EditorUtilities.layoutCompsLinear(3, this, description, userName, difficulty, previewImageSelector);
            
        }
        
        else {
            throw new IllegalArgumentException("Upload Type not supported by this window!");
        }
        
        height = posY - y + 1;
    }

    public Component createTitle() {
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
                            Messages.get(this, "import_preview_image_info") + "\n\n" + Messages.get(this, "import_preview_image_location", destPath.file().getAbsolutePath()), option) {
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
            refreshLayout();
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


    private boolean canUpload() {
        
        String dungeon = (String) selectDungeon.getObject();
        String uploader = userName.getText();
        String desc = description.getText();
        String title = dungeonTitleInput.getText();
        String versionTitle = versionTitleInput.getText();//versionTitle can stay empty
        
        String errorMessage = null;
        if (type == UPLOAD) {
            
            if (!SPDSettings.canUploadToServer() && !DeviceCompat.isDebug()) {
                errorMessage = Messages.get(UploadDungeon.class, "wait_upload");
            }
            else if (dungeon == null) {
                errorMessage = Messages.get(UploadDungeon.class, "no_dungeon");
            }
            else if (uploader == null || uploader.equals(userName.defaultValue) || ("AlphaDraxonis".equals(uploader) && !DeviceCompat.isDebug())) {
                errorMessage = Messages.get(UploadDungeon.class, "no_username");
            }
            else if (title == null || title.trim().length() < 2) {
                errorMessage = Messages.get(UploadDungeon.class, "no_title");
            }
            else if (desc == null || desc.trim().length() < 3) {
                errorMessage = Messages.get(UploadDungeon.class, "no_desc");
            }
            
        } else if (type == CHANGE) {
            
            if (!SPDSettings.canUpdateToServer() && !DeviceCompat.isDebug()) {
                errorMessage = Messages.get(UploadDungeon.class, "wait_update");
            }
            else if (title == null || title.trim().length() < 2) {
                errorMessage = Messages.get(UploadDungeon.class, "no_title");
            }
            else if (desc == null || desc.trim().length() < 3) {
                errorMessage = Messages.get(UploadDungeon.class, "no_desc");
            }
            
        }
		
        if (errorMessage != null) {
            Game.scene().addToFront(new WndMessage(errorMessage));
            return false;
        };
        
        return true;
    }

    private void uploadDungeon(Runnable onSuccessful) {
        
        String title = dungeonTitleInput.getText();
        String dungeon = (String) selectDungeon.getObject();
        String uploader = userName.getText();
        String desc = description.getText();
        int difficulty = (int) this.difficulty.getValue();
        String versionTitle = versionTitleInput.getText();
		
		ServerCommunication.UploadCallback callback = new ServerCommunication.UploadCallback() {
			@Override
			protected void onSuccessful(String dungeonID) {
				SPDSettings.increaseUploadTimer(type);
				UploadedDungeonRegistry.newDungeonUploaded(dungeon, dungeonID, title, desc, uploader, difficulty);
				
				onSuccessful.run();
				Game.scene().addToFront(new WndMessage(Messages.get(UploadDungeon.class, (type == UPLOAD ? "upload" : "update") +"_successful")));
			}
			
			@Override
			public void failed(Throwable t) {
				super.failed(t);
				if (type == CHANGE && t instanceof UpdateDungeonAction.InvalidDungeonIDException) {
					
					UploadedDungeonRegistry.dungeonDeletedFromServer(preview.dungeonID);//preview.dungeonID doesn’t seem to exist on the server, so we delete it and all associated local dungeons can be uploaded as (one) new dungeon.
					
					Game.scene().addToFront(new WndError(Messages.get(UploadDungeon.class, "dungeon_id_not_found")));
					
					setType(UPLOAD);
					
					refreshLayout();
				}
			}
		};
		
		
        if (type == UPLOAD) {
            ServerCommunication.uploadDungeon(dungeon, title, desc, uploader, difficulty, versionTitle, callback);
			return;
        }
		if (type == CHANGE) {
            ServerCommunication.updateDungeon(preview, dungeon, title, desc, difficulty, versionTitle, callback);
			return;
        }
    }

    @Override
    public boolean onBackPressed() {
        if (onBackPressed != null) return onBackPressed.onBackPressed();
        return false;
    }

    public static void showUploadWindow(ServerCommunication.UploadType type, String preselectDungeon, String coreID) {
        if (type != CHANGE) {
            if (!EditorUtilities.shouldConnectToInternet(() -> forceShowWindow(type, preselectDungeon, coreID))) return;
        }
        forceShowWindow(type, preselectDungeon, coreID);
    }

    private static UploadDungeon forceShowWindow(ServerCommunication.UploadType type, String preselectDungeon, String coreID) {
        SimpleWindow w = new SimpleWindow() {
            @Override
            public void onBackPressed() {
            }
        };
        UploadDungeon uploadDungeon = new UploadDungeon(w, type, null, null, w::hide, null, coreID);
        uploadDungeon.selectDungeon.selectObject(preselectDungeon);
        w.initComponents(uploadDungeon.createTitle(), uploadDungeon, uploadDungeon.getOutsideSp());
        Game.scene().addToFront(w);
        
        return uploadDungeon;
    }
    
    private void showLegalConfirmWindow(Runnable onConfirm) {
        Game.scene().addToFront(new WndOptionsCondensed(
                Messages.get(UploadDungeon.class, "legal_info_title"),
                Messages.get(UploadDungeon.class, "legal_info"),
                Messages.get(Toolbar.class, "item_cancel"),
                Messages.get(UploadDungeon.class, "legal_info_confirm")){
            @Override
            protected void onSelect(int index) {
                if (index == 1) {
                    onConfirm.run();
                }
            }
        });
    };
}
