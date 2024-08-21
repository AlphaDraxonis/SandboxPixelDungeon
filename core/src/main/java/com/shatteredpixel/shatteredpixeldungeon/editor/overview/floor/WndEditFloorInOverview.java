package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionCompRow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.TransitionTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

import java.io.IOException;
import java.util.*;

@NotAllowedInLua
public class WndEditFloorInOverview extends WndTabbed {


    protected final LevelScheme levelScheme;
    protected final LevelListPane.ListItem listItem;
    protected final LevelListPane listPane;
    private final Map<Integer, TransitionCompRow> transitionCompMap = new HashMap<>(5);

    private final WndEditorSettings.TabComp[] ownTabs;

    private static LevelListPane.ListItem lastListItem;
    private static int lastIndex;


    public WndEditFloorInOverview(LevelScheme levelScheme, LevelListPane.ListItem listItem, LevelListPane listPane) {
        this.levelScheme = levelScheme;
        this.listItem = listItem;
        this.listPane = listPane;
        if (listItem != lastListItem) lastIndex = 0;
        lastListItem = listItem;

        if (levelScheme.getType() == CustomLevel.class && levelScheme.getLevel() == null) {
            if (levelScheme.loadLevel() == null) {
                hide();
                ownTabs = null;
                return;
            }
        }

        resize(PixelScene.landscape() ? 210 : Math.min(155, (int) (PixelScene.uiCamera.width * 0.85)), (int) (PixelScene.uiCamera.height * 0.75f));

        ownTabs = new WndEditorSettings.TabComp[]{
                new General(), new LevelGenComp(levelScheme) {
            @Override
            protected void onFeelingChange() {
                super.onFeelingChange();
                listItem.updateLevel();
            }
        }, new LevelTab(null, levelScheme)
        };

        WndTabbed.Tab[] tabs = new WndTabbed.Tab[ownTabs.length];
        for (int i = 0; i < ownTabs.length; i++) {
            add(ownTabs[i]);
            ownTabs[i].setRect(0, 0, width, height);
            int index = i;
            tabs[i] = new WndTabbed.IconTab(ownTabs[i].createIcon()) {
                protected void select(boolean value) {
                    super.select(value);
                    ownTabs[index].active = ownTabs[index].visible = value;
                    if (value) lastIndex = index;
                }
            };
            add(tabs[i]);
        }

        layoutTabs();
        select(lastIndex);
    }

    @Override
    public void hide() {
        super.hide();
        if (Dungeon.level != levelScheme.getLevel()) {
            levelScheme.unloadLevel();
            EditorScene.updatePathfinder();
        }
    }

    private class General extends WndEditorSettings.TabComp {

        protected Component content;
        protected ScrollPane sp;
        protected ColorBlock line;

        protected RenderedTextBlock title;
        protected ChooseDestLevelComp passage, chasm;
        protected RedButton delete, open;

        protected IconButton rename, copy;

        public General() {
        }

        @Override
        protected void createChildren() {
            content = new Component();

            title = PixelScene.renderTextBlock(levelScheme.getName() + " (" + levelScheme.getType().getSimpleName() + ")", 9);
            title.hardlight(Window.TITLE_COLOR);
            title.setHighlighting(false);
            add(title);

            rename = new IconButton(Icons.get(Icons.RENAME_ON)) {
                @Override
                protected void onClick() {
                    Window w = new WndTextInput(Messages.get(WndSelectDungeon.class, "rename_title"),
                            "",
                            levelScheme.getName(),
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
                                for (String floorN : levelScheme.getCustomDungeon().floorNames()) {
                                    if (!floorN.equals(levelScheme.getName()) && floorN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                                        WndNewDungeon.showNameWarning();
                                        return;
                                    }
                                }
                                if (!text.equals(levelScheme.getName())) {
                                    try {
                                        levelScheme.getCustomDungeon().renameLevel(levelScheme, text);
                                        listPane.updateList();
                                        WndEditFloorInOverview.this.hide();
                                        EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, listPane));
                                    } catch (Exception ex) {
                                        EditorScene.catchError(ex);
                                    }
                                }
                            }
                        }
                    };
                    EditorScene.show(w);
                }

                @Override
                protected String hoverText() {
                    return Messages.get(WndSelectDungeon.class, "rename_yes");
                }
            };
            add(rename);

            copy = new IconButton(Icons.COPY.get()) {
                @Override
                protected void onClick() {
                    Window w = new WndTextInput(Messages.get(WndSelectDungeon.class, "copy_title"),
                            "",
                            levelScheme.getName() + " " + Messages.get(WndSelectDungeon.class, "copy_extension"),
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
                                for (String floorN : levelScheme.getCustomDungeon().floorNames()) {
                                    if (!floorN.equals(levelScheme.getName()) && floorN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                                        WndNewDungeon.showNameWarning();
                                        return;
                                    }
                                }
                                if (!text.equals(levelScheme.getName())) {
                                    LevelScheme newLevel = levelScheme.getCustomDungeon().copyLevel(levelScheme, text);
                                    if (newLevel == null) return;
                                    WndEditFloorInOverview.this.hide();

                                    if (newLevel.getType() == CustomLevel.class) {
                                        //open level
                                        WndSwitchFloor.selectLevelScheme(newLevel, listItem, listPane);
                                    } else {
                                        //show editing window
                                        listPane.updateList();
                                        EditorScene.show(new WndEditFloorInOverview(newLevel, listItem, listPane));
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

            //From TransitionTab
            passage = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "passage")) {
                @Override
                public void selectObject(Object object) {
                    super.selectObject(object);
                    if (object instanceof LevelScheme)
                        levelScheme.setPassage(EditorUtilities.getCodeName((LevelScheme) object));
                }
            };
            content.add(passage);

            chasm = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "chasm")) {
                @Override
                public void selectObject(Object object) {
                    super.selectObject(object);
                    if (object instanceof LevelScheme)
                        levelScheme.setChasm(EditorUtilities.getCodeName((LevelScheme) object), true);
                }

                @Override
                protected float getDisplayWidth() {
                    return passage.getDW();
                }

                @Override
                protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                    List<LevelSchemeLike> ret = super.filterLevels(levels);
                    ret.remove(levelScheme);//Cant choose same level
                    return ret;
                }
            };
            content.add(chasm);

            if (levelScheme.getPassage() != null)
                passage.selectObject(levelScheme.getPassage());
            if (levelScheme.getChasm() != null) chasm.selectObject(levelScheme.getChasm());

            line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
            content.add(line);

            delete = new RedButton(Messages.get(WndGameInProgress.class, "erase")) {
                @Override
                protected void onClick() {
                    super.onClick();

                    SandboxPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                            Messages.get(WndEditFloorInOverview.class, "erase_title"),
                            Messages.get(WndEditFloorInOverview.class, "erase_body"),
                            Messages.get(WndEditFloorInOverview.class, "erase_yes"),
                            Messages.get(WndGameInProgress.class, "erase_warn_no")) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                WndEditFloorInOverview.this.hide();//important to hide before deletion
                                try {
                                    Dungeon.customDungeon.delete(levelScheme);
                                } catch (IOException e) {
                                    SandboxPixelDungeon.reportException(e);
                                }
                                listPane.updateList();
                            }
                        }
                    });
                }
            };
            delete.icon(Icons.get(Icons.CLOSE));
            add(delete);
            if (levelScheme.getType() == CustomLevel.class && Dungeon.level != levelScheme.getLevel()) {
                open = new RedButton(Messages.get(WndEditFloorInOverview.class, "open")) {
                    @Override
                    protected void onClick() {
                        WndSwitchFloor.selectLevelScheme(levelScheme, listItem, listPane);
                    }
                };
                add(open);
            }

            sp = new ScrollPane(content);
            add(sp);
        }

        @Override
        public Image createIcon() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public String hoverText() {
            return null;
        }

        private static final int GAP_BETWEEN_BUTTON_AND_SP = 2;

        @Override
        public void layout() {

            float iconWidh = rename.icon().width + copy.icon().width + 2;

            title.maxWidth((int) (width - iconWidh - 2));
            title.setPos((title.maxWidth() - title.width()) * 0.5f, 3);

            rename.setRect(width - iconWidh, title.top() + (title.height() - rename.icon().height) * 0.5f, rename.icon().width, rename.icon().height);
            copy.setRect(rename.right() + 2, title.top() + (title.height() - copy.icon().height) * 0.5f, copy.icon().width, copy.icon().height);
            float titlePos = title.bottom() + 5;

            float pos = 0;
            passage.setRect(0, pos, width, WndEditorSettings.ITEM_HEIGHT);
            pos = passage.bottom() + 2;
            chasm.setRect(0, pos, width, WndEditorSettings.ITEM_HEIGHT);
            pos = chasm.bottom() + 2;
            line.size(width, 1);
            line.x = 0;
            line.y = pos;
            pos++;

            if (levelScheme.getType() != CustomLevel.class) {
                pos = layoutTransitionComps(Collections.singletonList(TransitionCompRow.CELL_DEFAULT_ENTRANCE), pos);
                pos = layoutTransitionComps(Collections.singletonList(TransitionCompRow.CELL_DEFAULT_EXIT), pos);
            } else {
                pos = layoutTransitionComps(levelScheme.entranceCells, pos);
                pos = layoutTransitionComps(levelScheme.exitCells, pos);
            }
            pos--;

            content.setSize(width, pos);

            float deleteW = open == null ? width : (width - 3) / 2f;
            delete.setRect(width - deleteW, height - WndEditorSettings.ITEM_HEIGHT, deleteW, WndEditorSettings.ITEM_HEIGHT);
            if (open != null)
                open.setRect(0, height - WndEditorSettings.ITEM_HEIGHT, deleteW, WndEditorSettings.ITEM_HEIGHT);

            sp.setRect(0, titlePos, width, height - titlePos - WndEditorSettings.ITEM_HEIGHT - GAP_BETWEEN_BUTTON_AND_SP);
            sp.scrollToCurrentView();

        }

        private float layoutTransitionComps(List<Integer> cells, float pos) {
            for (int cell : cells) {
                TransitionCompRow comp = transitionCompMap.get(cell);
                if (comp == null) {
                    comp = new TransitionCompRow(cell, levelScheme, false) {
                        @Override
                        protected void layoutParent() {
                            General.this.layout();
                        }
                    };
                    content.add(comp);
                    transitionCompMap.put(cell, comp);
                }
                comp.setRect(0, pos, width, -1);
                pos = comp.bottom() + 2;
            }
            return pos;
        }
    }

}