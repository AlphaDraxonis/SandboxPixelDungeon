package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndSelectFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitionTab extends WndEditorSettings.TabComp {


    private RenderedTextBlock title, titleLevelTransitons;
    private ChooseDestinationLevelComp startfloor, passage, chasm;

    private ScrollPane sp;
    protected Component content;
    protected ColorBlock line;
    protected Map<Integer, TransitionComp> transitionCompMap;

    @Override
    protected void createChildren(Object... params) {

        title = PixelScene.renderTextBlock(Messages.get(TransitionTab.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);

        content = new Component();

        startfloor = new ChooseDestinationLevelComp(Messages.get(TransitionTab.class, "startfloor")) {
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

        passage = new ChooseDestinationLevelComp(Messages.get(TransitionTab.class, "passage")) {
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

        chasm = new ChooseDestinationLevelComp(Messages.get(TransitionTab.class, "chasm")) {
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

        titleLevelTransitons = PixelScene.renderTextBlock(Messages.get(TransitionTab.class, "trans_for_lvl"), 9);
        titleLevelTransitons.hardlight(Window.TITLE_COLOR);
        content.add(titleLevelTransitons);

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

        titleLevelTransitons.maxWidth((int) width);
        titleLevelTransitons.setPos(x + (width - titleLevelTransitons.width()) * 0.5f, pos + (WndEditorSettings.ITEM_HEIGHT - 3 - titleLevelTransitons.height()) * 0.5f);
        pos += WndEditorSettings.ITEM_HEIGHT - 3;

        pos = layoutTransitionComps(EditorScene.customLevel().levelScheme.entranceCells, pos);
        pos = layoutTransitionComps(EditorScene.customLevel().levelScheme.exitCells, pos);

        content.setSize(width, pos - 1);
        sp.setPos(sp.left(), sp.top());

        sp.scrollTo(sp.content().camera().scroll.x, sp.content().camera().scroll.y);
    }

    private float layoutTransitionComps(List<Integer> cells, float pos) {
        for (int cell : cells) {
            TransitionComp comp = transitionCompMap.get(cell);
            if (comp == null) {
                comp = new TransitionComp(cell, EditorScene.customLevel().levelScheme) {
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

    public static class ChooseDestinationLevelComp extends ChooseObjectComp {

        public ChooseDestinationLevelComp(String label) {
            super(label);
        }

        @Override
        public void selectObject(Object object) {
            if (object.equals(Level.NONE)) super.selectObject(null);
            else super.selectObject(object);
        }

        @Override
        protected void doChange() {
            Window w = new WndSelectFloor() {
                @Override
                public boolean onSelect(LevelScheme levelScheme) {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    selectObject(levelScheme.getName());
                    return true;
                }

                @Override
                protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                    return ChooseDestinationLevelComp.this.filterLevels(levels);
                }
            };
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
        }

        public float getDW() {
            return display.width();
        }

        protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
            ArrayList<LevelScheme> ret = new ArrayList<>(levels);
            ret.add(0, LevelScheme.NO_LEVEL_SCHEME);
            return ret;
        }
    }

    public static class TransitionComp extends Component {

        public static final int CELL_DEFAULT_ENTRANCE = -3, CELL_DEFAULT_EXIT = -4;
        private final int cell;
        private final LevelScheme levelScheme;
        protected TransitionEditPart transitionEditPart;
        protected ColorBlock line;
        protected RenderedTextBlock title;
        protected IconButton remover, adder;
        //TODO might wanna have a button for expanding/hiding transition settings


        public TransitionComp(int cell, LevelScheme levelScheme) {
            super();
            this.cell = cell;
            this.levelScheme = levelScheme;

            line = new ColorBlock(1, 1, 0xFF222222);
            add(line);

            title = PixelScene.renderTextBlock(9);
            if (cell < 0)
                title.text(Messages.get(TransitionTab.class, cell == CELL_DEFAULT_ENTRANCE ? "entrance" : "exit"));
            else
                title.text(Messages.get(TransitionTab.class, (levelScheme.getLevel().map[cell] == Terrain.ENTRANCE ? "entrance" : "exit"))
                        + " " + new Koord(cell, levelScheme.getLevel()));
            add(title);

            remover = new IconButton(Icons.get(Icons.CLOSE)) {
                @Override
                protected void onClick() {
                    removeTransition();
                }
            };
            if (cell >= 0) add(remover);

            adder = new IconButton(Icons.get(Icons.PLUS)) {
                @Override
                protected void onClick() {
                    addTransition(EditTileComp.createNewTransition(cell), true);
                }
            };
            if (cell >= 0) add(adder);

            LevelTransition transition;
            if (cell < 0) {
                if (cell == CELL_DEFAULT_ENTRANCE)
                    transition = levelScheme.getEntranceTransitionRegular();
                else transition = levelScheme.getExitTransitionRegular();
            } else transition = levelScheme.getLevel().transitions.get(cell);
            if (transition != null) {
                addTransition(transition, false);
            } else remover.visible = false;
        }

        private void addTransition(LevelTransition transition, boolean updateOthers) {
            int terrainType;
            if (cell < 0)
                terrainType = cell == CELL_DEFAULT_ENTRANCE ? Terrain.ENTRANCE : Terrain.EXIT;
            else terrainType = levelScheme.getLevel().map[cell];
            if (transitionEditPart != null) {
                remove(transitionEditPart);
                transitionEditPart.destroy();
            }
            transitionEditPart = EditTileComp.addTransition(terrainType, transition, levelScheme, t -> {
                if (cell < 0) {
                    if (cell == CELL_DEFAULT_ENTRANCE) {
                        t.destLevel = Level.SURFACE;
                    } else {
                        t.destLevel = null;
                    }
                    t.destCell = -1;
                } else {
                    levelScheme.getLevel().transitions.remove(transition.cell());
                    if (levelScheme.getLevel() == EditorScene.customLevel())
                        EditorScene.remove(transition);
                }
            });
            add(transitionEditPart);
            adder.visible = false;
            remover.visible = true;
            if (updateOthers) {
                layoutParent();
                if (levelScheme.getLevel() == EditorScene.customLevel())
                    EditorScene.add(transition);
            }
        }

        private void removeTransition() {
            transitionEditPart.destroy();
            remove(transitionEditPart);
            transitionEditPart = null;
            LevelTransition t = levelScheme.getLevel().transitions.get(cell);
            levelScheme.getLevel().transitions.remove(cell);
            EditorScene.remove(t);
            remover.visible = false;
            adder.visible = true;
            layoutParent();
        }

        private static final int BUTTON_HEIGHT = 15;

        @Override
        protected void layout() {

            float pos = y;
            title.maxWidth((int) (width - (remover.visible || adder.visible ? BUTTON_HEIGHT + 3 : 0)));
            title.setPos(x, (BUTTON_HEIGHT - title.height()) * 0.5f + pos + 1);

            if (remover.visible)
                remover.setRect(width - BUTTON_HEIGHT - 3, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);
            else if (adder.visible)
                adder.setRect(width - BUTTON_HEIGHT - 3, pos, BUTTON_HEIGHT, BUTTON_HEIGHT);

            pos += BUTTON_HEIGHT + 1;

            if (transitionEditPart != null && transitionEditPart.visible) {
                transitionEditPart.setRect(x, pos, width, -1);
                pos = transitionEditPart.bottom();
            }
            height = pos - y + 1;

            line.size(width, 1);
            line.x = x;
            line.y = y + height;

        }

        protected void layoutParent() {
        }
    }

    @Override
    protected Image createIcon() {
        return Icons.get(Icons.STAIRS);
    }
}