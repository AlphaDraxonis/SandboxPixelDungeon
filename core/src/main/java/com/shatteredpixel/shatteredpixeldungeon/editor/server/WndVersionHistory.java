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

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.server.DungeonPreview;
import com.shatteredpixel.shatteredpixeldungeon.services.server.ServerCommunication;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.shatteredpixel.shatteredpixeldungeon.services.server.ServerConstants.KEYWORD_SUCCESS;

@NotAllowedInLua
public final class WndVersionHistory extends Component {
    
    private final SimpleWindow simpleWindow;
    
    private final DungeonPreview preview;
    private DungeonPreview.DungeonVersionsHistoryEntry[] historyEntries;
    
    private SingleEntryComp[] comps;

    private WndVersionHistory(SimpleWindow simpleWindow, DungeonPreview preview, DungeonPreview.DungeonVersionsHistoryEntry[] historyEntries) {
		this.simpleWindow = simpleWindow;
		this.preview = preview;
        this.historyEntries = historyEntries;
        
        comps = new SingleEntryComp[historyEntries.length];
        for (int i = 0; i < historyEntries.length; i++) {
            comps[i] = new SingleEntryComp(historyEntries[i], i, historyEntries.length > 1);
            add(comps[i]);
        }
        
    }
    
    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilities.layoutCompsLinear(2, this, comps);
    }
    
    private void removeEntry(DungeonPreview.DungeonVersionsHistoryEntry entry) {
        DungeonPreview.DungeonVersionsHistoryEntry[] newArray = new DungeonPreview.DungeonVersionsHistoryEntry[historyEntries.length-1];
        int j = 0;
		for (DungeonPreview.DungeonVersionsHistoryEntry existingEntry : historyEntries) {
			if (existingEntry != entry) {
				newArray[j] = existingEntry;
				comps[j].setEntry(existingEntry, j, newArray.length > 1);
				j++;
			}
		}
        
        for (; j < historyEntries.length; j++) {
            comps[j].remove();
            comps[j].destroy();
            comps[j].killAndErase();
            comps[j] = null;
        }
        
        historyEntries = newArray;
        
        simpleWindow.resize(simpleWindow.width(), Math.min(Window.WindowSize.HEIGHT_MEDIUM.get(), (int) Math.ceil(simpleWindow.preferredHeight())) );
        simpleWindow.layout();
    }
    
    protected final class SingleEntryComp extends Component {
        
        private DungeonPreview.DungeonVersionsHistoryEntry entry;
        
        
        protected RenderedTextBlock name, uploadTime;
        protected IconButton btnDownload, btnDelete;
        
        protected ColorBlock line;
        
        private SingleEntryComp(DungeonPreview.DungeonVersionsHistoryEntry entry, int index, boolean deletable) {
            super();
            
            name = PixelScene.renderTextBlock(7);
            name.setHighlighting(false);
            name.hardlight(Window.TITLE_COLOR);
            add(name);
            
            uploadTime = PixelScene.renderTextBlock(6);
            add(uploadTime);
            
            btnDownload = new IconButton(Icons.BTN_DOWNLOAD.get()) {
                @Override
                protected void onClick() {
                    WndPreview.userRequestsDownload(preview, entry.getVersionID());
                }
                
                @Override
                protected String hoverText() {
                    return Messages.get(WndPreview.class, "download");
                }
            };
            btnDownload.setSize(btnDownload.icon().width(), btnDownload.icon().height());
            add(btnDownload);
            
            btnDelete = new IconButton(Icons.TRASH.get()) {
                @Override
                protected void onClick() {
                    userRequestDeleteVersion(preview, name.text(), entry);
                }
                
                @Override
                protected String hoverText() {
                    return Messages.get(WndPreview.class, "delete");
                }
            };
            btnDelete.setSize(btnDelete.icon().width(), btnDelete.icon().height());
            add(btnDelete);
            
            line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
            add(line);
            
            setEntry(entry, index, deletable);
        }
        
        public void setEntry(DungeonPreview.DungeonVersionsHistoryEntry entry, int index, boolean deletable) {
            this.entry = entry;
            
            if (entry.getName() == null || entry.getName().isEmpty()) {
                name.text(Messages.get(WndVersionHistory.class, "entry_default_name", index+1));
            } else {
                name.text(entry.getName());
            }
            
            Date uploadDate = new Date(entry.getUploadTime());
            Locale locale = Languages.getCurrentLocale();
            long diff = System.currentTimeMillis() - uploadDate.getTime();
            if (diff >= 86400000) {
                //more than 24h ago, display only the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", locale);
                uploadTime.text(dateFormat.format(uploadDate));
            } else {
                DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
                uploadTime.text(timeFormat.format(uploadDate));
            }
            
            btnDelete.setVisible(deletable);
        }
        
        private final int MARGIN = 2;
        
        @Override
        protected void layout() {
            
            float rightEdge = x + width;
            
            if (btnDelete.visible) {
                btnDelete.setPos(rightEdge - MARGIN - btnDelete.width(), y + 2 + (height - btnDelete.height()) * 0.5f);
                rightEdge = btnDelete.left();
            }
            if (btnDownload.visible) {
                btnDownload.setPos(rightEdge - MARGIN - btnDownload.width(), y + 2 + (height - btnDownload.height()) * 0.5f);
                rightEdge = btnDownload.left();
            }
            if (btnDelete.visible || btnDownload.visible) {
                rightEdge -= MARGIN*3;
            }
            
            name.maxWidth((int) (rightEdge - x));
            uploadTime.maxWidth(name.maxWidth());
            
            name.setPos(x + 1, y + 3);
            uploadTime.setPos(name.left() + 3, name.bottom() + 4);
            
            height = uploadTime.bottom() - y + 1;
            
            line.size(width, 1);
            line.x = x;
            line.y = y;
            
            super.layout();
        }
        
    }
    
    
    
    public Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndVersionHistory.class, "title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
    
    public Component getOutsideSp() {
        return null;
    }
    
    
    public static Window show(DungeonPreview preview, DungeonPreview.DungeonVersionsHistoryEntry[] historyEntries) {
        SimpleWindow w = new SimpleWindow(Window.WindowSize.WIDTH_MEDIUM.get(), 0);
        
        WndVersionHistory versionHistory = new WndVersionHistory(w, preview, historyEntries);
        w.initComponents(versionHistory.createTitle(), versionHistory, versionHistory.getOutsideSp(), 0f, 0.5f);
        
        w.resize(w.width(), Math.min(Window.WindowSize.HEIGHT_MEDIUM.get(), (int) Math.ceil(w.preferredHeight())) );
        
        Game.scene().addToFront(w);
        
        return w;
    }
    
    
    
    private void userRequestDeleteVersion(DungeonPreview preview, String displayedVersionName, DungeonPreview.DungeonVersionsHistoryEntry entry) {
        WndPreview.checkOwnership(preview.dungeonID, () -> {
            Game.scene().addToFront(new WndOptions(Icons.get(Icons.WARNING),
                    Messages.get(WndPreview.class, "delete_version_title", displayedVersionName),
                    Messages.get(WndPreview.class, "delete_version_body", displayedVersionName),
                    Messages.get(WndGameInProgress.class, "erase_warn_yes"),
                    Messages.get(WndGameInProgress.class, "erase_warn_no")) {
                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        ServerCommunication.deleteVersion(preview.dungeonID, entry.getVersionID(), new ServerCommunication.UploadCallback() {
                            @Override
                            protected void onSuccessful(String dungeonID) {
                                String jsonResponse = dungeonID;//yeah, this is NOT dungeonID; ik it’s very bad code…
                                Bundle bundle = null;
                                try {
                                    bundle = Bundle.class.getConstructor(String.class).newInstance(jsonResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//						        Bundle bundle = new com.watabou.utils.Bundle(jsonResponse);
                                String message = bundle.getString("message");
                                if (!message.equals(KEYWORD_SUCCESS)) {
                                    Game.runOnRenderThread(() -> failed(new Exception(message)));
                                    return;
                                }
                                //in case we deleted the newest version
                                preview.mostRecentDungeon = bundle.getString("most_recent_version");
                                
                                Game.runOnRenderThread(() -> {
                                    removeEntry(entry);
                                    Game.scene().addToFront(new WndMessage(Messages.get(WndPreview.class, "delete_version_successful", displayedVersionName)));
                                });
                            }
                        });
                    }
                }
            });
        });
    }
    
}
