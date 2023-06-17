package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.FloorOverviewScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.utils.DungeonSeed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

import java.io.IOException;

//a lot of code copied from WndTextInput because idk how to make TextInputs
public class WndNewFloor extends WndTabbed {


    protected static final int MARGIN = 1;
    protected static final int BUTTON_HEIGHT = 16;


    private final CustomDungeon owner;


    protected LevelGenComp levelGenComp;
    protected NewFloorComp newFloorComp;

    public WndNewFloor(CustomDungeon owner) {

        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)),(int) (PixelScene.uiCamera.height * 0.65));

        this.owner = owner;


        OwnTab[] tbs = {
                newFloorComp = new NewFloorComp(){
                    @Override
                    protected void create(boolean positive) {
                        WndNewFloor.this.create(positive);
                    }
                },
                levelGenComp = new LevelGenComp()
        };
        for(int i =0;i<tbs.length;i++){
            add(tbs[i]);
            tbs[i].setRect(0, 0, width, height);
            int index = i;
            add( new IconTab(new ItemSprite(ItemSpriteSheet.SOMETHING)) {
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

        hide();

        if (positive) {
            String name = newFloorComp.textBox.getText();

            if (owner.getFloor(name) != null || name.equals(Level.SURFACE) || name.equals(Level.NONE)) {
                WndNewDungeon.showNameWarnig();
                return;
            }

            Long seed;
            if (levelGenComp.seed.getObject() == null) seed = null;
            else {
                seed = DungeonSeed.convertFromText((String) levelGenComp.seed.getObject());
                if (seed == -1) seed = null;
            }
            LevelScheme levelScheme = new LevelScheme(name,
                    (Class<? extends Level>) newFloorComp.chooseType.getObject(),
                    (Class<? extends Level>) newFloorComp.chooseTemplate.getObject(),
                    seed,
                    (Level.Feeling) levelGenComp.feelingSpinner.getValue(),
                    (int) newFloorComp.numInRegion.getValue(),
                    (int) newFloorComp.depth.getValue(),
                    levelGenComp.spawnItemsList);
            if (owner.getNumFloors() == 0) owner.setStart(name);
            owner.addFloor(levelScheme);

            if (levelScheme.getType() == CustomLevel.class) {
                Dungeon.levelName = name;
                if (levelScheme.getLevel().width() == 0) levelScheme.getLevel().create();
                try {
                    CustomDungeonSaves.saveLevel(levelScheme.getLevel());
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
//                FloorOverviewScene.updateList();
                EditorScene.open((CustomLevel) levelScheme.getLevel());
            } else {
                WndSwitchFloor.updateList();
                FloorOverviewScene.updateList();
            }
            try {
                CustomDungeonSaves.saveDungeon(owner);
            } catch (IOException e) {
                SandboxPixelDungeon.reportException(e);
            }
        }

    }

    @Override
    public void onBackPressed() {
        //do nothing TODO maybe just do confirmation?
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (newFloorComp !=null && newFloorComp.textBox != null) {
            TextInput textBox = newFloorComp.textBox;
            textBox.setRect(textBox.left(), textBox.top(), textBox.width(), textBox.height());
        }
    }

    protected static class OwnTab extends Component{
    }
}