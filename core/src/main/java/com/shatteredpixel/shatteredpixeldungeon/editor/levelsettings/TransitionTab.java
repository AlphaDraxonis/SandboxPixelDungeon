package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionCompRow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
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
                if (object instanceof LevelScheme)
                    Dungeon.customDungeon.setStart(EditorUtilies.getCodeName((LevelScheme) object));
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                List<LevelSchemeLike> ret = super.filterLevels(levels);
                ret.remove(LevelScheme.NO_LEVEL_SCHEME);
                return ret;
            }
        };
        content.add(startfloor);

        passage = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "passage")) {
            @Override
            public void selectObject(Object object) {
                super.selectObject(object);
                if (object instanceof LevelScheme)
                    EditorScene.customLevel().levelScheme.setPassage(EditorUtilies.getCodeName((LevelScheme) object));
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
                if (object instanceof LevelScheme)
                    EditorScene.customLevel().levelScheme.setChasm(EditorUtilies.getCodeName((LevelScheme) object));
            }

            @Override
            protected float getDisplayWidth() {
                return startfloor.getDW();
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                List<LevelSchemeLike> ret = super.filterLevels(levels);
//                ret.remove(EditorScene.customLevel().levelScheme);//Cant choose same level
                return ret;
            }
        };
        content.add(chasm);

        LevelScheme levelScheme = EditorScene.customLevel().levelScheme;
        startfloor.selectObject(Dungeon.customDungeon.getStart());
        passage.selectObject(levelScheme.getPassage() != null && !levelScheme.getPassage().equals(Dungeon.customDungeon.getStart())
                ? levelScheme.getPassage() : null);
        chasm.selectObject(levelScheme.getChasm());


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