package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.ItemContainer;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general.FeelingSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseObjectComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableComp;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.HeroSelectScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.utils.DungeonSeed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

public class LevelGenComp extends WndNewFloor.OwnTab {

    private ScrollPane sp;
    protected Component content;

    protected RenderedTextBlock title, note;

    protected ChooseObjectComp seed;
    protected FeelingSpinner feelingSpinner;

    protected ColorBlock line;

    //items
    //mobs
    //rooms
    protected ItemContainer spawnItems;
    protected SpawnSection sectionItems;

    List<Item> spawnItemsList;

    public LevelGenComp() {

    }

    @Override
    protected void createChildren(Object... params) {
        content = new Component();

        title = PixelScene.renderTextBlock("LevelGenSettings", 10);
        title.hardlight(Window.TITLE_COLOR);
        content.add(title);

        seed = new ChooseObjectComp(Messages.get(WndNewFloor.class, "seed")) {
            @Override
            protected void doChange() {
                Window window = new WndTextInput(Messages.get(HeroSelectScene.class, "custom_seed_title"),
                        "Enter seed for the template or generated level type",//FIXME unhardcode easy
                        seed.getObject() == null ? "" : seed.getObject().toString(),
                        20,
                        false,
                        Messages.get(HeroSelectScene.class, "custom_seed_set"),
                        Messages.get(HeroSelectScene.class, "custom_seed_clear")) {
                    @Override
                    public void onSelect(boolean positive, String text) {
                        text = DungeonSeed.formatText(text);
                        if (positive && DungeonSeed.convertFromText(text) != -1)
                            seed.selectObject(text);
                        else seed.selectObject(null);
                    }
                };
                if (Game.scene() instanceof EditorScene) EditorScene.show(window);
                else Game.scene().addToFront(window);
            }

//            @Override
//            protected float getDisplayWidth() {
//                return chooseType.getDW();
//            }
        };
        content.add(seed);

        feelingSpinner = new FeelingSpinner(null, 9, true);
        content.add(feelingSpinner);

        line = new ColorBlock(1, 1, 0xFF222222);
        content.add(line);


        note = PixelScene.renderTextBlock("Note that following settings are only applied when generating the floor", 6);
        content.add(note);

        sectionItems = new SpawnSection("Items", spawnItems = new ItemContainer(spawnItemsList = new ArrayList<>()){
            @Override
            protected void onSlotNumChange() {
                LevelGenComp.this.layout();
            }
        });
        content.add(sectionItems);

        sp = new ScrollPane(content);
        add(sp);
    }

    @Override
    protected void layout() {

        float pos = MARGIN * 2;
        title.setPos((width - title.width()) / 2, pos);
        pos = title.bottom() + 4 * MARGIN;

        seed.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos += BUTTON_HEIGHT + MARGIN * 2;

        feelingSpinner.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
        pos = feelingSpinner.bottom() + MARGIN * 4;

        line.size(width, 1);
        line.x = 0;
        line.y = pos;
        PixelScene.align(line);
        pos += MARGIN * 4;

        note.maxWidth((int) width);
        note.setPos(0, pos);
        pos = note.bottom() + MARGIN * 3;

        sectionItems.setRect(MARGIN, pos, width - MARGIN * 2, -1);
        pos = sectionItems.bottom() + MARGIN;


        content.setSize(width, pos);
        sp.setSize(width, height);
    }

    //typ
    //template
    //tiefe


    //seed
    //feeling

    //items
    //mobs (inkl questnpcsm deren Quest durch bearbeiten verändert werden kann)
    //rooms


    private class SpawnSection extends FoldableComp {

        public SpawnSection(String label, ItemContainer container) {
            super(label, container);
            container.setSize(LevelGenComp.this.width, -1);
        }

        @Override
        protected void layoutParent() {
            LevelGenComp.this.layout();
        }

    }

}