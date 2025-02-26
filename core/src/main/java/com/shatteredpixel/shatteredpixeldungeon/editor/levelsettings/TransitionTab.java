package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.ChooseDestLevelComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionCompRow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NotAllowedInLua
public class TransitionTab extends WndEditorSettings.TabComp {


    private RenderedTextBlock title, titleLevelTransitions;
    private ChooseDestLevelComp startfloor, passage, chasm;

    private ScrollPane sp;
    protected Component content;
    protected ColorBlock line;
    protected Map<Integer, TransitionCompRow> transitionCompMap;

    @Override
    protected void createChildren() {
        
        LevelScheme levelScheme = Dungeon.level.levelScheme;

        title = PixelScene.renderTextBlock(Messages.get(TransitionTab.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        content = new Component();

        startfloor = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "startfloor")) {
            @Override
            public void selectObject(LevelSchemeLike object) {
                super.selectObject(object);
                if (object instanceof LevelScheme) {
                    Dungeon.customDungeon.setStart(EditorUtilities.getCodeName((LevelScheme) object));
                    if (passage != null) {
                        passage.selectObject(levelScheme.getPassage());
                    }
                }
                if (parent != null) {
                    TransitionTab.this.layout();
                }
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                List<LevelSchemeLike> ret = super.filterLevels(levels);
                ret.remove(LevelScheme.NO_LEVEL_SCHEME);
                return ret;
            }
        };
        startfloor.selectObject(Dungeon.customDungeon.getStart());
        content.add(startfloor);

        content.add(passage = createChooseDestLevelCompForPassage(levelScheme, TransitionTab.this::layout));

        content.add(chasm = createChooseDestLevelCompForChasm(levelScheme, TransitionTab.this::layout));


        line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
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
        if (sp == null) {
            return;
        }
        
        super.layout();

        float pos = y;
        title.maxWidth((int) width);
        title.setPos((width - title.width()) * 0.5f, pos + (WndEditorSettings.ITEM_HEIGHT - title.height()) * 0.5f);
        pos += WndEditorSettings.ITEM_HEIGHT + 2;

        sp.setRect(x, pos, width, height - pos);

        pos = 0;
        content.setSize(width, 0);
        content.setSize(width, pos = EditorUtilities.layoutStyledCompsInRectangles(2, width, content, startfloor, passage, chasm) + 2);
        
        line.size(width, 1);
        line.x = x;
        line.y = pos;
        pos++;

        titleLevelTransitions.maxWidth((int) width);
        titleLevelTransitions.setPos(x + (width - titleLevelTransitions.width()) * 0.5f, pos + (WndEditorSettings.ITEM_HEIGHT - 3 - titleLevelTransitions.height()) * 0.5f);
        pos += WndEditorSettings.ITEM_HEIGHT - 3;

        pos = layoutTransitionComps(Dungeon.level.levelScheme.entranceCells, pos);
        pos = layoutTransitionComps(Dungeon.level.levelScheme.exitCells, pos);

        content.setSize(width, pos - 1);
        sp.setPos(sp.left(), sp.top());

        sp.scrollToCurrentView();
    }

    private float layoutTransitionComps(List<Integer> cells, float pos) {
        for (int cell : cells) {
            TransitionCompRow comp = transitionCompMap.get(cell);
            if (comp == null) {
                comp = new TransitionCompRow(cell, Dungeon.level.levelScheme, true) {
                    @Override
                    protected void layoutParent() {
                        if (parent != null) {
                            TransitionTab.this.layout();
                        }
                    }
                };
                content.add(comp);
                transitionCompMap.put(cell, comp);
            }
            comp.setRect(x, pos, width, 0);
            pos = comp.bottom() + 2;
        }
        return pos;
    }

    @Override
    public void updateList() {
        layout();
    }

    @Override
    public Image createIcon() {
        return Icons.get(Icons.STAIRS);
    }

    @Override
    public String hoverText() {
        return Messages.get(TransitionTab.class, "title");
    }
    
    
    public static ChooseDestLevelComp createChooseDestLevelCompForPassage(LevelScheme levelScheme, Runnable layoutParent) {
        ChooseDestLevelComp result = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "passage")) {
            @Override
            public void selectObject(LevelSchemeLike object) {
                if (object == null) {
                    selectObject(Dungeon.customDungeon.getStart());
                    return;
                }
                super.selectObject(object);
                if (object instanceof LevelScheme)
                    levelScheme.setPassage(EditorUtilities.getCodeName((LevelScheme) object));
                if (parent != null) {
                    layoutParent.run();
                }
            }
        };
        result.selectObject(levelScheme.getPassage());
        return result;
    }
    
    public static ChooseDestLevelComp createChooseDestLevelCompForChasm(LevelScheme levelScheme, Runnable layoutParent) {
        ChooseDestLevelComp result = new ChooseDestLevelComp(Messages.get(TransitionTab.class, "chasm")) {
            @Override
            public void selectObject(LevelSchemeLike object) {
                super.selectObject(object);
                if (object instanceof LevelScheme)
                    Dungeon.level.levelScheme.setChasm(EditorUtilities.getCodeName((LevelScheme) object), true);
                if (parent != null) {
                    layoutParent.run();
                }
            }
            
            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                List<LevelSchemeLike> ret = super.filterLevels(levels);
//                ret.remove(Dungeon.level.levelScheme);//Can't choose same level
                ret.remove(LevelScheme.NO_LEVEL_SCHEME);
                return ret;
            }
        };
        String chasm = levelScheme.getChasm();
        if (chasm != null) {
            result.selectObject(chasm);
        } else {
            result.selectObject(LevelScheme.NO_LEVEL_SCHEME);
        }
        
        return result;
    }
}