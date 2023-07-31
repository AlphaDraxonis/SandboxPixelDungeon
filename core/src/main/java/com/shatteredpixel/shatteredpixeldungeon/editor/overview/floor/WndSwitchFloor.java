package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;

import java.io.IOException;

public class WndSwitchFloor extends Window {

    private static final int MARGIN = 2;
    private static WndSwitchFloor instance;

    protected LevelListPane listPane;
    protected RedButton createFloor;

    public WndSwitchFloor() {
        instance = this;
        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new LevelListPane() {

            @Override
            public void onSelect(LevelScheme levelScheme, LevelListPane.ListItem listItem) {
                if (levelScheme.getType() == CustomLevel.class) {
                    hide();
                    selectLevelScheme(levelScheme, listItem, this);
                } else onEdit(levelScheme, listItem);
            }
        };
        add(listPane);

        createFloor = new RedButton(Messages.get(WndSwitchFloor.class, "new_floor")) {
            @Override
            protected void onClick() {
                EditorScene.show(new WndNewFloor(Dungeon.customDungeon));
            }

            @Override
            protected boolean onLongClick() {
                return WndNewDungeon.doAddDefaultLevelsToDungeon(Dungeon.customDungeon);
            }
        };
        add(createFloor);


        createFloor.setRect(MARGIN, height - MARGIN - 18, width - MARGIN * 2, 18);
        PixelScene.align(createFloor);

        listPane.setSize(width, createFloor.top() - MARGIN);
        PixelScene.align(listPane);

        updateList();

    }

    public static void updateList() {
        if (instance == null) return;
        instance.listPane.updateList();
    }

    @Override
    public boolean onSignal(KeyEvent event) {
        if (event.pressed && KeyBindings.getActionForKey(event) == SPDAction.WAIT_OR_PICKUP) {//TODO change hotkey!
            onBackPressed();
            return true;
        } else {
            return super.onSignal(event);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        instance = null;
    }

    public static void selectLevelScheme(LevelScheme levelScheme, LevelListPane.ListItem listItem, LevelListPane listPane) {
        if (levelScheme.getType() == CustomLevel.class) {
            CustomLevel f = (CustomLevel) levelScheme.getLevel();
            if (f == null) {
                try {
                    f = CustomDungeonSaves.loadLevel(levelScheme.getName());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
            }
            EditorScene.open(f);
        } else listPane.onEdit(levelScheme, listItem);
    }

}