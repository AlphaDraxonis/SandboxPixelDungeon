package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

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
        Sample.INSTANCE.play(Assets.Sounds.CLICK);
        if (Game.scene() instanceof EditorScene)
            EditorScene.show(new WndEditLevelInOverview(levelScheme, listItem, this));
        else Game.scene().addToFront(new WndEditLevelInOverview(levelScheme, listItem, this));
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
            label.text(levelScheme.getName()
                    + (levelScheme == LevelScheme.NO_LEVEL_SCHEME || levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME ?
                    "" : " (depth=" + levelScheme.getDepth() + ", type=" + levelScheme.getType().getSimpleName() + ")"));
        }
    }
}