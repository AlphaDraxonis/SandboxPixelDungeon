package com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndOptions;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class WndNewDungeon extends WndTextInput {


    public static final String DEFAULT_DUNGEON = "default_dungeon";


    private final Set<String> dungeonNames;
    public WndNewDungeon(Set<String> dungeonNames) {

        super(
                Messages.titleCase(Messages.get(WndNewDungeon.class,"title")),
                Messages.get(WndNewDungeon.class,"body"),
                "",
                50,
                false,
                Messages.get(WndNewDungeon.class,"yes"),
                Messages.get(WndNewDungeon.class,"no")
        );

        this.dungeonNames = dungeonNames;
    }

    @Override
    public void onSelect(boolean positive, String text) {
        if (positive && !text.isEmpty()) {
            if (dungeonNames.contains(text) || text.equals(DEFAULT_DUNGEON)) showNameWarnig();
            else createAndOpenNewCustomDungeon(text);
        }
    }

    public static void createAndOpenNewCustomDungeon(String name) {
        Dungeon.customDungeon = new CustomDungeon(name);
        try {
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            SandboxPixelDungeon.reportException(e);
        }
        EditorScene.start();
        SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
    }

    public static void showNameWarnig() {
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                Game.scene().addToFront(
                        new WndOptions(Icons.get(Icons.WARNING),
                                Messages.get(WndNewDungeon.class,"dup_name_title"),
                                Messages.get(WndNewDungeon.class,"dup_name_body", Level.SURFACE,Level.NONE),
                                Messages.get(WndNewDungeon.class,"dup_name_close")
                        )
                );
            }
        });
    }

    public static boolean doAddDefaultLevelsToDungeon(CustomDungeon dungeon) {
        Collection<String> names = dungeon.floorNames();
        for (int i = 1; i <= 26; i++) {
            if (names.contains(Integer.toString(i))) {
                return false;
            }
        }

        Window w = new WndOptions(Icons.get(Icons.WARNING),
                Messages.get(WndNewDungeon.class,"add_default_title"),
                Messages.get(WndNewDungeon.class,"add_default_body"),
                Messages.get(WndNewDungeon.class,"add_default_yes"),
                Messages.get(WndNewDungeon.class,"add_default_no")
        ) {
            @Override
            protected void onSelect(int index) {
                if (index == 0) {
                    dungeon.initDefault();
                    WndSwitchFloor.updateList();
                    FloorOverviewScene.updateList();
                    try {
                        CustomDungeonSaves.saveDungeon(dungeon);
                    } catch (IOException e) {
                        SandboxPixelDungeon.reportException(e);
                    }
                }
            }
        };
        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);

        return true;
    }

}