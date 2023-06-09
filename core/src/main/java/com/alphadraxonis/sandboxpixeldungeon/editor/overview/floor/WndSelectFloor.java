package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.LevelListPane;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class WndSelectFloor extends Window {

    protected LevelListPane listPane;

    public WndSelectFloor() {
        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new LevelListPane() {

            @Override
            public void onSelect(LevelScheme levelScheme,LevelListPane.ListItem listItem) {
                if (WndSelectFloor.this.onSelect(levelScheme)) hide();
            }

            @Override
            protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                return WndSelectFloor.this.filterLevels(levels);
            }
        };
        add(listPane);

        listPane.setSize(width, height);
        PixelScene.align(listPane);

        listPane.updateList();

    }

    public abstract boolean onSelect(LevelScheme levelScheme);

    protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
        return new ArrayList<>(levels);
    }
}