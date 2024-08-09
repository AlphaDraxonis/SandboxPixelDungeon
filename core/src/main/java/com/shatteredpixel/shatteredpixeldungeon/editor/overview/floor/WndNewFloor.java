package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.FloorOverviewScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.utils.PathFinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

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

                @Override
                protected String hoverText() {
                    return tbs[index].hoverText();
                }
            });
        }

        layoutTabs();
        select(0);

    }

    public void create(boolean positive) {

        if (positive) {
            String name = CustomDungeon.maybeFixIncorrectNameEnding(newFloorComp.textBox.getText());

            Collection<String> floorNames = owner.floorNames();
            for (String floors : floorNames) {
                if (floors.replace(' ', '_').equals(name.replace(' ', '_'))) {
                    WndNewDungeon.showNameWarning();
                    return;
                }
            }
            if (name.equals(Level.SURFACE) || name.equals(Level.NONE) || name.equals(Level.ANY) ||  name.contains("\"")) {
                WndNewDungeon.showNameWarning();
                return;
            }

            for (Room r : newLevelScheme.roomsToSpawn) {
                r.doOnAllGameObjects(obj -> {
                    if (obj instanceof Key && !floorNames.contains(((Key) obj).name()))
                         return obj.onRenameLevelScheme(((Key) obj).name(), newLevelScheme.getName());
                    return GameObject.ModifyResult.noChange();
                });
            }

            // Create an ExecutorService with a single thread
            ExecutorService executor = Executors.newSingleThreadExecutor();

            // Create a Future object to track the generation task
            // SET BREAKPOINT IN LINE 115 (2 lines below, this is line 113) IF GENERATING IS NOT WORKING PROPERLY ggv
            Future<Boolean> generator = executor.submit(() -> {
                newLevelScheme.initNewLevelScheme(name,
                        (Class<? extends Level>) newFloorComp.chooseTemplate.getObject());
                return true;
            });

            // Wait for 10 seconds for the level generation to complete
			Exception exception;
            try {
				generator.get(10, TimeUnit.SECONDS);
				exception = null;
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
				Game.reportException(ex);
                ex.printStackTrace();
                exception = ex;
            }
            if (exception != null) {
                Level curLevel = EditorScene.getCustomLevel();
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
                if (exception instanceof TimeoutException) {
                    EditorScene.show(new WndError(Messages.get(InterlevelScene.class, "could_not_generate", Dungeon.seed)) {
                        @Override
                        public void onBackPressed() {
                            super.onBackPressed();
                            if (EditorScene.getCustomLevel() != null) {
                                PathFinder.setMapSize(Dungeon.level.width(), Dungeon.level.height());
                                EditorScene.revalidateHeaps();
                            }
                        }
                    });
                } else {
                    EditorScene.show(new WndError(exception));
                }
                return;
            }

            executor.shutdownNow();


            hide();

            if (owner.getNumFloors() == 0) owner.setStart(name);
            owner.addFloor(newLevelScheme);

            if (newLevelScheme.getType() == CustomLevel.class) {
                Dungeon.levelName = name;
                if (newLevelScheme.getLevel().width() == 0) newLevelScheme.getLevel().create();
                owner.initExitsFromPreviousFloor(newLevelScheme);
                PathFinder.setMapSize(newLevelScheme.getLevel().width(), newLevelScheme.getLevel().height());
                try {
                    CustomDungeonSaves.saveLevel(newLevelScheme.getLevel());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
//                FloorOverviewScene.updateList();
                EditorScene.open((CustomLevel) newLevelScheme.getLevel());
            } else {
                newLevelScheme.initExitEntranceCellsForRandomLevel();
                owner.initExitsFromPreviousFloor(newLevelScheme);
                EditorScene.updatePathfinder();
                EditorScene.updateTransitionIndicators();
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

    protected static abstract class OwnTab extends WndEditorSettings.TabComp {

        protected LevelScheme newLevelScheme;

        public OwnTab(LevelScheme newLevelScheme) {
            super();
            this.newLevelScheme = newLevelScheme;
        }

        @Override
        public Image createIcon() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }
    }
}