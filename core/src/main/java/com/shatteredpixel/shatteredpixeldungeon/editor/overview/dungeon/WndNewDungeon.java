package com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class WndNewDungeon extends WndTextInput {


    public static final String DEFAULT_DUNGEON = "default-dungeon";


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
        if (positive && !text.isEmpty() && !text.contains("\"")) {
            for (String dungeonN : dungeonNames) {
                if (dungeonN.replace(' ', '_').equals(text.replace(' ', '_'))) {
                    WndNewDungeon.showNameWarning();
                    return;
                }
            }
            if (text.equals(DEFAULT_DUNGEON)) showNameWarning();
            else createAndOpenNewCustomDungeon(text);
        }
    }

    public static void createAndOpenNewCustomDungeon(String name) {
        Dungeon.customDungeon = new CustomDungeon(name);
        try {
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            Game.scene().addToFront(new WndError(e.getClass().getSimpleName()
                    + (e.getCause() == null ? "" : " caused by " + e.getCause().getClass().getSimpleName())
                    + "\n" + e.getMessage()));
            SandboxPixelDungeon.reportException(e);
            return;
        }
        EditorScene.start();
        CustomTileLoader.loadTiles(true);
        SandboxPixelDungeon.switchNoFade(FloorOverviewScene.class);
    }

    public static void showNameWarning() {
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                Game.scene().addToFront(
                        new WndOptions(Icons.get(Icons.WARNING),
                                Messages.get(WndNewDungeon.class,"dup_name_title"),
                                Messages.get(WndNewDungeon.class,"dup_name_body", Level.SURFACE, Level.NONE),
                                Messages.get(WndSupportPrompt.class,"close")
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