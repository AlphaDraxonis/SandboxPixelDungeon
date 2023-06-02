package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class WndNewDungeon extends WndTextInput {

    private final Set<String> otherNames;

    public WndNewDungeon(Set<String> otherNames) {

        super(
                Messages.titleCase("Create new dungeon"),
                "Enter name for new dungeon:",
                "",
                50,
                false,
                "Create",
                "Cancel"
        );

        this.otherNames = otherNames;
    }

    @Override
    public void onSelect(boolean positive, String text) {
        if (positive && !text.isEmpty()) {
            if (otherNames.contains(text)) showNameWarnig();
            else createAndOpenNewCustomDungeon(text);
        }
    }

    public static void createAndOpenNewCustomDungeon(String name) {
        Dungeon.customDungeon = new CustomDungeon(name);
        try {
            CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
        } catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
        EditorScene.start();
        ShatteredPixelDungeon.switchNoFade(FloorOverviewScene.class);
    }

    public static void showNameWarnig() {
        Game.runOnRenderThread(new Callback() {
            @Override
            public void call() {
                Game.scene().addToFront(
                        new WndOptions(Icons.get(Icons.WARNING),
                                "ERROR - duplicate names",
                                "You cannot create a new dungeon or floor because one already exists with that name. " +
                                        "Note that the names \"" + Level.SURFACE + "\" and \"" + Level.NONE + "\" are also reserved names for floors.",
                                "Close"
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
                "Add default floors",
                "Do you really want to add the 26 default levels and set the settings to the default dungeon settings? You will need about 1KB disc space.",
                "Yes", "No"
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
                        ShatteredPixelDungeon.reportException(e);
                    }
                }
            }
        };
        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else Game.scene().addToFront(w);

        return true;
    }

}