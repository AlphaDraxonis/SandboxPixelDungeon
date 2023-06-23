package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.ItemContainer;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Mobs;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Rooms;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.RoomItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general.FeelingSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseObjectComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.HeroSelectScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.utils.DungeonSeed;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTextInput;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
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

    protected SpawnSection<Item> sectionItems;
    protected SpawnSection<MobItem> sectionMobs;
    protected SpawnSectionMore<RoomItem> sectionRooms;
    protected boolean spawnStandartRooms = true, spawnSecretRooms = true, spawnSpecialRooms = true;

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


        note = PixelScene.renderTextBlock("Note that some settings are only applied when generating the floor", 6);
        content.add(note);

        List<Item> listItems = new ArrayList<>();
        sectionItems = new SpawnSection<>("Items", new ItemContainer<Item>(listItems) {
            @Override
            protected void onSlotNumChange() {
                LevelGenComp.this.layout();
            }
        }, listItems);
        content.add(sectionItems);

        List<MobItem> listMobs = new ArrayList<>();
        sectionMobs = new SpawnSection<>("Mobs", new ItemContainer<MobItem>(listMobs) {
            @Override
            protected void onSlotNumChange() {
                LevelGenComp.this.layout();
            }

            @Override
            protected Class<? extends Bag> getPreferredBag() {
                return Mobs.bag.getClass();
            }

            @Override
            protected void doAddItem(MobItem item) {
                if (item.mob() instanceof Blacksmith) {
                    int numSmiths = 0;
                    for (MobItem m : sectionMobs.list) {
                        if (m.mob() instanceof Blacksmith) numSmiths++;
                    }
                    int numSmithRooms = 0;
                    for (RoomItem r : sectionRooms.list) {
                        if (r.room() instanceof BlacksmithRoom) numSmithRooms++;
                    }
                    if (numSmithRooms <= numSmiths)
                        sectionRooms.container.addNewItem(new RoomItem(new BlacksmithRoom()));
                }
                super.doAddItem(item);
            }
        }, listMobs);
        content.add(sectionMobs);

        List<RoomItem> listRooms = new ArrayList<>();
        sectionRooms = new SpawnSectionMore<RoomItem>("Rooms", new ItemContainer<RoomItem>(listRooms) {
            @Override
            protected void onSlotNumChange() {
                LevelGenComp.this.layout();
            }

            @Override
            protected Class<? extends Bag> getPreferredBag() {
                return Rooms.bag.getClass();
            }
        }, listRooms) {
            @Override
            protected void onAddClick() {
                Window w = new WndRoomSettings(spawnStandartRooms, spawnSecretRooms, spawnSpecialRooms) {
                    @Override
                    protected void finish() {
                        spawnStandartRooms = stand.checked();
                        spawnSecretRooms = sec.checked();
                        spawnSpecialRooms = spec.checked();
                    }
                };
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
            }
        };
        content.add(sectionRooms);

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

        sectionMobs.setRect(MARGIN, pos, width - MARGIN * 2, -1);
        pos = sectionMobs.bottom() + MARGIN;

        sectionRooms.setRect(MARGIN, pos, width - MARGIN * 2, -1);
        pos = sectionRooms.bottom() + MARGIN;


        content.setSize(width, pos);
        sp.setSize(width, height);

        sp.scrollToCurrentView();
    }


    public List<Item> getSpawnItemsList() {
        return sectionItems.list;
    }

    public List<MobItem> getSpawnMobsList() {
        return sectionMobs.list;
    }

    public List<RoomItem> getSpawnRoomsList() {
        return sectionRooms.list;
    }

    private class SpawnSection<T extends Item> extends FoldableComp {

        private ItemContainer<T> container;
        private List<T> list;

        public SpawnSection(String label, ItemContainer<T> container, List<T> list) {
            super(label, container);
            this.list = list;
            this.container = container;
            container.setSize(LevelGenComp.this.width, -1);
            showBody(false);
        }

        @Override
        protected void layoutParent() {
            LevelGenComp.this.layout();
        }

    }

    private abstract class SpawnSectionMore<T extends Item> extends FoldableCompWithAdd {

        private ItemContainer<T> container;
        private List<T> list;

        public SpawnSectionMore(String label, ItemContainer<T> container, List<T> list) {
            super(label);
            this.list = list;
            setBody(this.container = container);
            container.setSize(LevelGenComp.this.width, -1);
            showBody(false);
            setReverseBtnOrder(true);
        }

        @Override
        protected void layoutParent() {
            LevelGenComp.this.layout();
        }

        @Override
        protected final Component createBody(Object param) {
            return null;
        }
    }


    private static abstract class WndRoomSettings extends Window {

        protected CheckBox stand, sec, spec;

        public WndRoomSettings(boolean standart, boolean secret, boolean special) {

            resize(PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : (int) (PixelScene.uiCamera.width * 0.85f), 100);

            RenderedTextBlock title = PixelScene.renderTextBlock("RoomSettings", 10);
            title.hardlight(Window.TITLE_COLOR);
            add(title);

            stand = new CheckBox("Spawn StandartRooms");
            stand.checked(standart);
            add(stand);

            sec = new CheckBox("Spawn SecretRooms");
            sec.checked(secret);
            add(sec);

            spec = new CheckBox("Spawn SpecialRooms");
            spec.checked(special);
            add(spec);

            title.setPos((width - title.width()) * 0.5f, 2);
            stand.setRect(0, title.bottom() + 5, width, 16);
            sec.setRect(0, stand.bottom() + 2, width, 16);
            spec.setRect(0, sec.bottom() + 2, width, 16);

            resize(width, (int) (Math.ceil(spec.bottom()) + 3));
        }

        @Override
        public void hide() {
            super.hide();
            finish();
        }

        protected abstract void finish();

    }
}