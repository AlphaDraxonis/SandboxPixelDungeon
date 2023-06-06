package com.alphadraxonis.sandboxpixeldungeon.editor.overview;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.audio.Sample;

import java.io.IOException;

public class WndSwitchFloor extends Window {

    private static final int MARGIN = 2;
    private static WndSwitchFloor instance;

    protected LevelListPane listPane;
    protected RedButton createFloor;

    public WndSwitchFloor() {
        instance = this;
        resize(PixelScene.landscape() ? 215 : PixelScene.uiCamera.width - 5, (int) (PixelScene.uiCamera.height * 0.8f));

        listPane = new LevelListPane(){

            @Override
            public void onSelect(LevelScheme levelScheme,LevelListPane.ListItem  listItem) {
                if (levelScheme.getType() == CustomLevel.class) {
                    hide();
                    selectLevelScheme(levelScheme,listItem,this);
                }
                else onEdit(levelScheme,listItem);
            }
        };
        add(listPane);

        createFloor = new RedButton(Messages.get(WndSwitchFloor.class,"new_floor")) {
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
    public void destroy() {
        super.destroy();
        instance = null;
    }

    public static void selectLevelScheme(LevelScheme levelScheme,LevelListPane.ListItem listItem,LevelListPane listPane){
        if (levelScheme.getType() == CustomLevel.class) {
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            CustomLevel f = (CustomLevel) levelScheme.getLevel();
            if (f == null) {
                try {
                    f = CustomDungeonSaves.loadLevel(levelScheme.getName());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
            }
            EditorScene.open(f);
        }else listPane.onEdit(levelScheme,listItem);
    }

}