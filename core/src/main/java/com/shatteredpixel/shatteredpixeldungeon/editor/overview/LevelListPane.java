package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndEditFloorInOverview;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LevelListPane extends ScrollingListPane {


    public void updateList() {
        clear();
        List<LevelSchemeLike> levels = filterLevels(Dungeon.customDungeon.levelSchemes());
        if (levels.isEmpty()) ;//TODO show that its empty
        Collections.sort(levels, (a, b) -> {
            if (a instanceof LevelScheme && b instanceof LevelScheme) return ((LevelScheme) a).compareTo((LevelScheme) b);
            return 0;//there are only LevelSchemes!
        });
        for (LevelSchemeLike levelScheme : levels) {
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
                    return getLevelScheme() instanceof LevelScheme && onEdit((LevelScheme) getLevelScheme(), this);
                }
            });
        }
        scrollToCurrentView();
    }

    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
        return new ArrayList<>(levels);
    }

    protected abstract void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem);

    public boolean onEdit(LevelScheme levelScheme, LevelListPane.ListItem listItem) {
        if (SPDSettings.tutorialOpenRegularLevel()) {
            EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, this));
        } else {
            final long time = System.currentTimeMillis();
            EditorScene.show(new WndOptions(
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_title"),
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_body"),
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_close")){
                @Override
                public void hide() {
                    super.hide();
                    if (time + 1100 < System.currentTimeMillis()) SPDSettings.tutorialOpenRegularLevel(true);
                    EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, LevelListPane.this));
                }
            });
        }
        return true;
    }


    public static class ListItem extends ScrollingListPane.ListItem {

        private final LevelSchemeLike levelScheme;

        public ListItem(LevelSchemeLike levelScheme) {
            super(Icons.get(Icons.STAIRS), "");
            this.levelScheme = levelScheme;
            label.setHightlighting(false);
            updateLevel();
        }

        public LevelSchemeLike getLevelScheme() {
            return levelScheme;
        }

        public void updateLevel() {
            String name;
            if(levelScheme instanceof QuestLevels)
                name = ((QuestLevels) levelScheme).getName();
            else if (levelScheme == LevelScheme.NO_LEVEL_SCHEME)
                name = EditorUtilies.getDispayName(Level.NONE);
            else if (levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME)
                name = EditorUtilies.getDispayName(Level.SURFACE);
            else if (levelScheme == LevelScheme.ANY_LEVEL_SCHEME)
                name = EditorUtilies.getDispayName(Level.ANY);
            else{
                LevelScheme ls = (LevelScheme) levelScheme;
                name = ls.getName() + " (depth=" + ls.getDepth() + ", type=" + ls.getType().getSimpleName() + ")";
            }
            label.text(name);
        }
    }
}