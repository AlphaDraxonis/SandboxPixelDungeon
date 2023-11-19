package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;

import java.io.IOException;

public class WndSwitchFloor extends Window {

    private static final int MARGIN = 2;
    private static WndSwitchFloor instance;

    protected LevelListPane listPane;
    protected RedButton createFloor, openItemDistribution;

    public WndSwitchFloor() {
        instance = this;
        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new LevelListPane() {

            @Override
            public void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem) {
                if (levelScheme instanceof LevelScheme) {
                    LevelScheme ls = (LevelScheme) levelScheme;
                    if (ls.getType() == CustomLevel.class) {
                        hide();
                        selectLevelScheme(ls, listItem, this);
                    } else onEdit(ls, listItem);
                }
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

        openItemDistribution = new RedButton(""){
            @Override
            protected void onClick() {
                WndItemDistribution.showWindow();
            }
        };
        openItemDistribution.icon(Icons.get(Icons.BACKPACK));
        add(openItemDistribution);


        createFloor.setRect(MARGIN, height - MARGIN - 18, width - MARGIN * 3 - 18, 18);
        PixelScene.align(createFloor);

        openItemDistribution.setRect(width -18-MARGIN, height - MARGIN - 18, 18, 18);
        PixelScene.align(openItemDistribution);

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
            if (f == null) f = (CustomLevel) levelScheme.loadLevel();
            if (f == null) {
                if (levelScheme.levelLoadingException != null){
                    String errorMsg = levelScheme.levelLoadingException.getClass().getSimpleName()
                            + (levelScheme.levelLoadingException.getMessage() == null ? ""
                            : ": _" + levelScheme.levelLoadingException.getMessage().replace('_', '-') + "_");
                            EditorScene.show(new WndOptions(Icons.WARNING.get(), Messages.get(WndSwitchFloor.class, "loading_error_title"),
                                    Messages.get(WndSwitchFloor.class, "loading_error_body", errorMsg),
                                    Messages.get(WndGameInProgress.class, "erase"),
                                    Messages.get(WndNewDungeon.class, "no")) {
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        try {
                                            levelScheme.getCustomDungeon().delete(levelScheme);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
                }
                return;
            }
            EditorScene.open(f);
        } else listPane.onEdit(levelScheme, listItem);
    }

}