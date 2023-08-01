package com.alphadraxonis.sandboxpixeldungeon.editor.overview;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndEditFloorInOverview;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LevelListPane extends ScrollingListPane {


    public void updateList() {
        clear();
        List<LevelScheme> levels = filterLevels(Dungeon.customDungeon.levelSchemes());
        if (levels.isEmpty()) ;//TODO show that its empty
        Collections.sort(levels);
        //TODO sort
        for (LevelScheme levelScheme : levels) {
            addItem(new ListItem(levelScheme) {
                @Override
                protected void onClick() {
                    onSelect(getLevelScheme(), this);
                }

                @Override
                protected void onRightClick() {
                    if (!onLongClick()) {
                        onClick();
                    }
                }

                @Override
                protected boolean onLongClick() {
                    return onEdit(getLevelScheme(), this);
                }
            });
        }
    }

    protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
        return new ArrayList<>(levels);
    }

    protected abstract void onSelect(LevelScheme levelScheme, LevelListPane.ListItem listItem);

    public boolean onEdit(LevelScheme levelScheme, LevelListPane.ListItem listItem) {
        if (Game.scene() instanceof EditorScene)
            EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, this));
        else Game.scene().addToFront(new WndEditFloorInOverview(levelScheme, listItem, this));
        return true;
    }


    public static class ListItem extends ScrollingListPane.ListItem {

        private final LevelScheme levelScheme;

        public ListItem(LevelScheme levelScheme) {
            super(Icons.get(Icons.STAIRS), "");
            this.levelScheme = levelScheme;
            updateLevel();
        }

        public LevelScheme getLevelScheme() {
            return levelScheme;
        }

        public void updateLevel() {
            String name;
            if (levelScheme == LevelScheme.NO_LEVEL_SCHEME)
                name = Level.NONE;//name = Messages.get(TransitionEditPart.class,"none_level");
            else if (levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME)
                name = Level.SURFACE;//name = Messages.get(TransitionEditPart.class,"surface_level");
            else
                name = levelScheme.getName() + " (depth=" + levelScheme.getDepth() + ", type=" + levelScheme.getType().getSimpleName() + ")";
            label.text(name);
        }
    }
}