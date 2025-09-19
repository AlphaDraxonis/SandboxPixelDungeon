package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.EditorInventory;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Camera;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.RectF;

import java.io.IOException;

@NotAllowedInLua
public class FloorOverviewScene extends PixelScene {

    private static FloorOverviewScene instance;

    protected LevelListPane listPane;
    protected RedButton createFloor,openItemDistribution;
    private NinePatch bg;
    private ExitButton btnExit;
    private static final int MARGIN = 2;

    @Override
    public void create() {

        EditorScene.isEditing = true;

        super.create();

        instance = this;
        
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = getCommonInsets();

        bg = Chrome.get(Chrome.Type.WINDOW);
        bg.size(w + 10, h + 10);
        bg.x = bg.y = -5;
        add(bg);
        
        insets.top += MARGIN;
        insets.left += MARGIN;
        insets.bottom += MARGIN;
        insets.right += MARGIN;
        
        w -= insets.left + insets.right;
        h -= insets.top + insets.bottom;

        listPane = new LevelListPane(new LevelListPane.Selector() {
            @Override
            public void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem) {
                if(levelScheme instanceof LevelScheme)
                    WndSwitchFloor.selectLevelScheme((LevelScheme) levelScheme,listItem,listPane);
            }
        });
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

        openItemDistribution = new RedButton(""){
            @Override
            protected void onClick() {
                WndItemDistribution.showWindow();
            }
        };
        openItemDistribution.icon(Icons.get(Icons.BACKPACK));
        add(openItemDistribution);


        createFloor.setRect(insets.left, insets.top + h - 18, w - MARGIN - 18, 18);
        PixelScene.align(createFloor);

        openItemDistribution.setRect(insets.left + w - 18, insets.top + h - 18, 18, 18);
        PixelScene.align(openItemDistribution);

        listPane.setRect(insets.left, insets.top, w, h - 18 - MARGIN);
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
        btnExit.setPos( insets.left + w + MARGIN - btnExit.width(), insets.top - MARGIN);
        add(btnExit);

        updateList();

        EditorInventory.callStaticInitializers();
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
        instance.btnExit.givePointerPriority();
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
