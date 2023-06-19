package com.alphadraxonis.sandboxpixeldungeon.editor.overview;

import com.alphadraxonis.sandboxpixeldungeon.Chrome;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.EditorItemBag;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.ExitButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;

import java.io.IOException;

public class FloorOverviewScene extends PixelScene {

    private static FloorOverviewScene instance;

    protected LevelListPane listPane;
    protected RedButton createFloor;
    private NinePatch bg;
    private ExitButton btnExit;
    private static final int MARGIN = 2;

    @Override
    public void create() {
        super.create();

        instance = this;

        bg = Chrome.get(Chrome.Type.WINDOW);
        bg.size(camera().width + 10, camera().height + 10);
        bg.x = bg.y = -5;

        add(bg);

        listPane = new LevelListPane() {
            @Override
            public void onSelect(LevelScheme levelScheme,LevelListPane.ListItem listItem) {
                WndSwitchFloor.selectLevelScheme(levelScheme,listItem,listPane);
            }
        };
        add(listPane);
        createFloor = new RedButton(Messages.get(WndSwitchFloor.class,"new_floor")) {
            @Override
            protected void onClick() {
                FloorOverviewScene.this.addToFront(new WndNewFloor(Dungeon.customDungeon));
            }

            @Override
            protected boolean onLongClick() {
                return WndNewDungeon.doAddDefaultLevelsToDungeon(Dungeon.customDungeon);
            }
        };
        add(createFloor);

        createFloor.setRect(MARGIN, (camera().height - MARGIN * 2 - 18), camera().width - MARGIN * 2, 18);
        PixelScene.align(createFloor);

        listPane.setRect(MARGIN, MARGIN, camera().width - MARGIN * 2, createFloor.top() - MARGIN);
        PixelScene.align(listPane);

        btnExit = new ExitButton() {
            @Override
            protected void onClick() {
                try {
                    CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
                super.onClick();
            }
        };
        btnExit.setPos(Camera.main.width - btnExit.width(), 0);
        add(btnExit);

        updateList();

        EditorItemBag.callStaticInitializers();;
    }

    public static void updateList() {
        if (instance == null) return;
        instance.listPane.updateList();

        //Idk why bringToFront doesnt work...
        instance.remove(instance.btnExit);
        instance.btnExit.destroy();
        instance.btnExit = new ExitButton() {
            @Override
            protected void onClick() {
                try {
                    CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
                super.onClick();
            }
        };
        instance. btnExit.setPos(Camera.main.width - instance.btnExit.width(), 0);
        instance.add(instance.btnExit);
//        instance.bringToFront(instance.btnExit);
    }

    @Override
    public void destroy() {
        try {
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
        super.destroy();
        instance = null;
    }
}