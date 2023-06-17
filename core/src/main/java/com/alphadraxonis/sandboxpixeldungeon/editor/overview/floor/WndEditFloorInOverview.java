package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionCompRow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.TransitionTab;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.LevelListPane;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.Spinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.spinner.impls.DepthSpinner;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndGameInProgress;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WndEditFloorInOverview extends Window {

    protected Component content;
    protected ScrollPane sp;
    protected ColorBlock line;

    protected RenderedTextBlock title;
    protected Spinner depth;
    protected ChooseDestLevelComp passage, chasm;
    protected RedButton delete, open;

    protected final LevelScheme levelScheme;
    private final Map<Integer, TransitionCompRow> transitionCompMap = new HashMap<>(5);


    public WndEditFloorInOverview(LevelScheme levelScheme, LevelListPane.ListItem listItem, LevelListPane listPane) {
        this.levelScheme = levelScheme;

        if (levelScheme.getType() == CustomLevel.class && levelScheme.getLevel() == null)
            levelScheme.loadLevel();

        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), 200);

        content = new Component();

        title = PixelScene.renderTextBlock(levelScheme.getName() + " (" + levelScheme.getType().getSimpleName() + ")", 9);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        depth = new DepthSpinner(levelScheme.getDepth(), 8) {
            @Override
            protected void onChange(int newDepth) {
                levelScheme.setDepth(newDepth);
                listItem.updateLevel();
            }
        };
        depth.setButtonWidth(15);
        depth.setAlignmentSpinnerX(Spinner.ALIGNMENT_CENTER);
        content.add(depth);

        //From TransitionTab
        passage = new ChooseDestLevelComp(Messages.get(TransitionTab.class,"passage")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                levelScheme.setPassage((String) object);
            }
        };
        content.add(passage);

        chasm = new ChooseDestLevelComp(Messages.get(TransitionTab.class,"chasm")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                levelScheme.setChasm((String) object);
            }

            @Override
            protected float getDisplayWidth() {
                return passage.getDW();
            }

            @Override
            protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                List<LevelScheme> ret = super.filterLevels(levels);
                ret.remove(levelScheme);//Cant choose same level
                return ret;
            }
        };
        content.add(chasm);

        if (levelScheme.getPassage() != null)
            passage.selectObject(levelScheme.getPassage());
        if (levelScheme.getChasm() != null) chasm.selectObject(levelScheme.getChasm());

        line = new ColorBlock(1, 1, 0xFF222222);
        content.add(line);

        delete = new RedButton(Messages.get(WndGameInProgress.class, "erase")) {
            @Override
            protected void onClick() {
                super.onClick();

                SandboxPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
                        Messages.get(WndEditFloorInOverview.class,"erase_title"),
                        Messages.get(WndEditFloorInOverview.class,"erase_body"),
                        Messages.get(WndEditFloorInOverview.class,"erase_yes"),
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
        if (levelScheme.getType() == CustomLevel.class && EditorScene.customLevel()!=levelScheme.getLevel()) {
            open = new RedButton(Messages.get(WndEditFloorInOverview.class,"open")){
                @Override
                protected void onClick() {
                    WndSwitchFloor.selectLevelScheme(levelScheme, listItem,listPane);
                }
            };
            add(open);
        }

        sp = new ScrollPane(content);
        add(sp);

        layout();
    }

    private static final int gapBetweenButtonAndSp = 2;

    private void layout() {

        title.maxWidth(width);
        title.setPos((width - title.width()) * 0.5f, 2);
        float titlePos = title.bottom() + 4;

        float pos = 0;
        depth.setRect(0, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = depth.bottom() + 2;
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
        resize(width, (int) Math.ceil(Math.min(PixelScene.uiCamera.height * 0.85f, pos + titlePos+WndEditorSettings.ITEM_HEIGHT+gapBetweenButtonAndSp)));


        float deleteW = open == null ? width : (width - 3) / 2f;
        delete.setRect(width - deleteW, height - WndEditorSettings.ITEM_HEIGHT, deleteW, WndEditorSettings.ITEM_HEIGHT);
        if(open!=null)open.setRect(0,height - WndEditorSettings.ITEM_HEIGHT,deleteW,WndEditorSettings.ITEM_HEIGHT);

        sp.setRect(0, titlePos, width, height - titlePos - WndEditorSettings.ITEM_HEIGHT - gapBetweenButtonAndSp);
        sp.scrollToCurrentView();

    }

    private float layoutTransitionComps(List<Integer> cells, float pos) {
        for (int cell : cells) {
            TransitionCompRow comp = transitionCompMap.get(cell);
            if (comp == null) {
                comp = new TransitionCompRow(cell, levelScheme, false) {
                    @Override
                    protected void layoutParent() {
                        WndEditFloorInOverview.this.layout();
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

    @Override
    public void hide() {
        super.hide();
        if (EditorScene.customLevel() != levelScheme.getLevel()) levelScheme.unloadLevel();
    }
}