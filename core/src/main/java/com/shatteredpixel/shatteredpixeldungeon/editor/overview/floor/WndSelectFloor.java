package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class WndSelectFloor extends Window {

    protected LevelListPane listPane;

    public WndSelectFloor() {
        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new LevelListPane() {

            @Override
            public void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem) {
                if (WndSelectFloor.this.onSelect(levelScheme)) hide();
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                return WndSelectFloor.this.filterLevels(levels);
            }
        };
        add(listPane);

        listPane.setSize(width, height);
        PixelScene.align(listPane);

        listPane.updateList();

    }

    public abstract boolean onSelect(LevelSchemeLike levelScheme);

    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
        return new ArrayList<>(levels);
    }
}