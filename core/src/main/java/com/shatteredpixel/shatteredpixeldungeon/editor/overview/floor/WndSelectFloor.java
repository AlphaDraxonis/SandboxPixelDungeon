package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NotAllowedInLua
public abstract class WndSelectFloor extends Window {

    protected LevelListPane listPane;

    public WndSelectFloor() {
        resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_MEDIUM.get());

        listPane = new LevelListPane(new LevelListPane.Selector() {
            @Override
            public void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem) {
                if (WndSelectFloor.this.onSelect(levelScheme)) hide();
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                return WndSelectFloor.this.filterLevels(levels);
            }
        });
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