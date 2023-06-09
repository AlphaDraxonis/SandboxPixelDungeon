package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.transitions.TransitionCompRow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitionTab extends WndEditorSettings.TabComp {


    private RenderedTextBlock title, titleLevelTransitions;
    private ChooseDestLevelComp startfloor, passage, chasm;

    private ScrollPane sp;
    protected Component content;
    protected ColorBlock line;
    protected Map<Integer, TransitionCompRow> transitionCompMap;

    @Override
    protected void createChildren(Object... params) {

        title = PixelScene.renderTextBlock(Messages.get(TransitionTab.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        content = new Component();

        startfloor = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "startfloor")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                Dungeon.customDungeon.setStart((String) object);
            }

            @Override
            protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                List<LevelScheme> ret = super.filterLevels(levels);
                ret.remove(LevelScheme.NO_LEVEL_SCHEME);
                return ret;
            }
        };
        content.add(startfloor);

        passage = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "passage")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                EditorScene.customLevel().levelScheme.setPassage((String) object);
            }

            @Override
            protected float getDisplayWidth() {
                return startfloor.getDW();
            }
        };
        content.add(passage);

        chasm = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "chasm")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                EditorScene.customLevel().levelScheme.setChasm((String) object);
            }

            @Override
            protected float getDisplayWidth() {
                return startfloor.getDW();
            }

            @Override
            protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                List<LevelScheme> ret = super.filterLevels(levels);
                ret.remove(EditorScene.customLevel().levelScheme);//Cant choose same level
                return ret;
            }
        };
        content.add(chasm);

        LevelScheme levelScheme = EditorScene.customLevel().levelScheme;
        if (Dungeon.customDungeon.getStart() != null)
            startfloor.selectObject(Dungeon.customDungeon.getStart());
        if (levelScheme.getPassage() != null && !levelScheme.getPassage().equals(Dungeon.customDungeon.getStart()))
            passage.selectObject(levelScheme.getPassage());
        if (levelScheme.getChasm() != null) chasm.selectObject(levelScheme.getChasm());


        line = new ColorBlock(1, 1, 0xFF222222);
        content.add(line);

        titleLevelTransitions = PixelScene.renderTextBlock(Messages.get(TransitionTab.class, "trans_for_lvl"), 9);
        titleLevelTransitions.hardlight(Window.TITLE_COLOR);
        content.add(titleLevelTransitions);

        transitionCompMap = new HashMap<>();

        sp = new ScrollPane(content);
        add(sp);
    }

    @Override
    public void layout() {
        super.layout();

        float pos = y;
        title.maxWidth((int) width);
        title.setPos((width - title.width()) * 0.5f, pos + (WndEditorSettings.ITEM_HEIGHT - title.height()) * 0.5f);
        pos += WndEditorSettings.ITEM_HEIGHT + 2;

        sp.setRect(x, pos, width, height - pos);

        pos = 0;
        startfloor.setRect(x, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = startfloor.bottom() + 2;
        passage.setRect(x, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = passage.bottom() + 2;
        chasm.setRect(x, pos, width, WndEditorSettings.ITEM_HEIGHT);
        pos = chasm.bottom() + 2;
        line.size(width, 1);
        line.x = x;
        line.y = pos;
        pos++;

        titleLevelTransitions.maxWidth((int) width);
        titleLevelTransitions.setPos(x + (width - titleLevelTransitions.width()) * 0.5f, pos + (WndEditorSettings.ITEM_HEIGHT - 3 - titleLevelTransitions.height()) * 0.5f);
        pos += WndEditorSettings.ITEM_HEIGHT - 3;

        pos = layoutTransitionComps(EditorScene.customLevel().levelScheme.entranceCells, pos);
        pos = layoutTransitionComps(EditorScene.customLevel().levelScheme.exitCells, pos);

        content.setSize(width, pos - 1);
        sp.setPos(sp.left(), sp.top());

        sp.scrollToCurrentView();
    }

    private float layoutTransitionComps(List<Integer> cells, float pos) {
        for (int cell : cells) {
            TransitionCompRow comp = transitionCompMap.get(cell);
            if (comp == null) {
                comp = new TransitionCompRow(cell, EditorScene.customLevel().levelScheme, true) {
                    @Override
                    protected void layoutParent() {
                        TransitionTab.this.layout();
                    }
                };
                content.add(comp);
                transitionCompMap.put(cell, comp);
            }
            comp.setRect(x, pos, width, -1);
            pos = comp.bottom() + 2;
        }
        return pos;
    }

    @Override
    protected void updateList() {
        layout();
    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.STAIRS);
    }
}