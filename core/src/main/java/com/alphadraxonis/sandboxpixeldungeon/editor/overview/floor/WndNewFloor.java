package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.InterlevelScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndError;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.utils.PathFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WndNewFloor extends WndTabbed {


    protected static final int MARGIN = 1;
    public static final int BUTTON_HEIGHT = 16;


    private final CustomDungeon owner;


    protected LevelGenComp levelGenComp;
    protected NewFloorComp newFloorComp;

    protected LevelScheme newLevelScheme = new LevelScheme();

    public WndNewFloor(CustomDungeon owner) {

        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.65));

        this.owner = owner;

        newLevelScheme.itemsToSpawn = new ArrayList<>(3);
        newLevelScheme.roomsToSpawn = new ArrayList<>(3);
        newLevelScheme.mobsToSpawn = new ArrayList<>(3);
        newLevelScheme.prizeItemsToSpawn = new ArrayList<>(3);


        OwnTab[] tbs = {
                newFloorComp = new NewFloorComp(newLevelScheme) {
                    @Override
                    protected void create(boolean positive) {
                        WndNewFloor.this.create(positive);
                    }
                },
                levelGenComp = new LevelGenComp(newLevelScheme)
        };
        for (int i = 0; i < tbs.length; i++) {
            add(tbs[i]);
            tbs[i].setRect(0, 0, width, height);
            int index = i;
            add(new IconTab(tbs[i].createIcon()) {
                protected void select(boolean value) {
                    super.select(value);
                    tbs[index].active = tbs[index].visible = value;
                }
            });
        }

        layoutTabs();
        select(0);

    }

    public void create(boolean positive) {

        if (positive) {
            String name = newFloorComp.textBox.getText();

            for (String floors : owner.floorNames()) {
                if (floors.replace(' ', '_').equals(name.replace(' ', '_'))) {
                    WndNewDungeon.showNameWarnig();
                    return;
                }
            }
            if (name.equals(Level.SURFACE) || name.equals(Level.NONE) || name.equals(Level.ANY)) {
                WndNewDungeon.showNameWarnig();
                return;
            }

            // Create an ExecutorService with a single thread
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Create a Future object to track the generation task
            Future<Boolean> generator = executor.submit(() -> {
                newLevelScheme.initNewLevelScheme(name,
                        (Class<? extends Level>) newFloorComp.chooseTemplate.getObject());
                return true;
            });

            // Wait for 10 seconds for the level generation to complete
            Boolean generated;
            try {
                generated = generator.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                generated = null;
                ex.printStackTrace();
            }
            if (generated == null) {
                Level curLevel = EditorScene.customLevel();
                if (curLevel != null) {
                    Dungeon.levelName = curLevel.name;
                    Dungeon.level = curLevel;
                } else {
                    Dungeon.levelName = null;
                    Dungeon.level = null;
                }
                for (String floor : Dungeon.customDungeon.floorNames()) {
                    LevelScheme l = Dungeon.customDungeon.getFloor(floor);
                    if (Level.NONE.equals(l.getName())) {
                        Dungeon.customDungeon.removeFloor(l);
                        break;
                    }
                }

                executor.shutdownNow();
                if (Game.scene() instanceof EditorScene)
                    EditorScene.show(new WndError(Messages.get(InterlevelScene.class, "could_not_generate", Dungeon.seed)){
                        @Override
                        public void onBackPressed() {
                            super.onBackPressed();
                            if (EditorScene.customLevel() != null) {
                                PathFinder.setMapSize(EditorScene.customLevel().width(), EditorScene.customLevel().height());
                                EditorScene.revalidateHeaps();
                            }
                        }
                    });
                else
                    Game.scene().addToFront(new WndError(Messages.get(InterlevelScene.class, "could_not_generate", Dungeon.seed)));
                return;
            }

            executor.shutdownNow();


            hide();

            if (owner.getNumFloors() == 0) owner.setStart(name);
            owner.addFloor(newLevelScheme);

            if (newLevelScheme.getType() == CustomLevel.class) {
                Dungeon.levelName = name;
                if (newLevelScheme.getLevel().width() == 0) newLevelScheme.getLevel().create();
                try {
                    CustomDungeonSaves.saveLevel(newLevelScheme.getLevel());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
//                FloorOverviewScene.updateList();
                EditorScene.open((CustomLevel) newLevelScheme.getLevel());
            } else {
                WndSwitchFloor.updateList();
                FloorOverviewScene.updateList();
            }
            try {
                CustomDungeonSaves.saveDungeon(owner);
            } catch (IOException e) {
                SandboxPixelDungeon.reportException(e);
            }
        } else hide();

    }


    @Override
    public void onBackPressed() {
        //do nothing TODO maybe just do confirmation?
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (newFloorComp != null && newFloorComp.textBox != null) {
            TextInput textBox = newFloorComp.textBox;
            textBox.setRect(textBox.left(), textBox.top(), textBox.width(), textBox.height());
        }
    }

    protected static class OwnTab extends WndEditorSettings.TabComp {

        protected LevelScheme newLevelScheme;

        public OwnTab(LevelScheme newLevelScheme) {
            super(newLevelScheme);
        }

        @Override
        protected void createChildren(Object... params) {
            newLevelScheme = (LevelScheme) params[0];
        }

        @Override
        public Image createIcon() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }
    }
}