package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.ItemContainer;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Mobs;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Rooms;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.RoomItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general.FeelingSpinner;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseObjectComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.Room;
import com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard.BlacksmithRoom;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.HeroSelectScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.IconButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
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

    protected RenderedTextBlock title;

    protected ChooseObjectComp seed;
    protected FeelingSpinner feelingSpinner;

    protected ColorBlock line;

    protected SpawnSection<Item> sectionItems;
    protected SpawnSection<MobItem> sectionMobs;
    protected SpawnSectionMore<RoomItem> sectionRooms;

    public LevelGenComp(LevelScheme newLevelScheme) {
        super(newLevelScheme);
    }

    @Override
    protected void createChildren(Object... params) {

        super.createChildren(params);

        content = new Component();

        title = PixelScene.renderTextBlock(Messages.get(LevelGenComp.class, "title"), 8);
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
                        long s = DungeonSeed.convertFromText(text);
                        if (positive && s != -1) {
                            seed.selectObject(text);
                            newLevelScheme.setSeed(s);
                        } else {
                            seed.selectObject(null);
                            newLevelScheme.resetSeed();
                        }
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
        if (newLevelScheme.isSeedSet())
            seed.selectObject(DungeonSeed.convertToCode(newLevelScheme.getSeed()));
        content.add(seed);

        feelingSpinner = new FeelingSpinner(newLevelScheme.getFeeling(), 9, true);
        feelingSpinner.addChangeListener(() -> {
            newLevelScheme.setFeeling((Level.Feeling) feelingSpinner.getValue());
            EditorScene.updateDepthIcon();
        });
        content.add(feelingSpinner);

        line = new ColorBlock(1, 1, 0xFF222222);
        content.add(line);

        sectionItems = new SpawnSection<>(Messages.get(LevelGenComp.class, "items"), new ItemContainer<Item>(newLevelScheme.itemsToSpawn) {
            @Override
            protected void onSlotNumChange() {
                LevelGenComp.this.layout();
            }
        });
        content.add(sectionItems);

        List<MobItem> listMobs = new ArrayList<>();
        for (Mob mob : newLevelScheme.mobsToSpawn) listMobs.add(new MobItem(mob));
        sectionMobs = new SpawnSection<>(Messages.get(LevelGenComp.class, "mobs"), new ItemContainer<MobItem>(listMobs) {
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
                    for (Mob m : newLevelScheme.mobsToSpawn) {
                        if (m instanceof Blacksmith) numSmiths++;
                    }
                    int numSmithRooms = 0;
                    for (Room r : newLevelScheme.roomsToSpawn) {
                        if (r instanceof BlacksmithRoom) numSmithRooms++;
                    }
                    if (numSmithRooms <= numSmiths)
                        sectionRooms.container.addNewItem(new RoomItem(new BlacksmithRoom()));
                }
                super.doAddItem(item);
                newLevelScheme.mobsToSpawn.add(item.mob());
            }

            @Override
            protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                if (super.removeSlot(slot)) {
                    newLevelScheme.mobsToSpawn.remove(slot.item());
                    return true;
                }
                return false;
            }
        });
        content.add(sectionMobs);

        if (newLevelScheme.getName() == null) {
            List<RoomItem> listRooms = new ArrayList<>();
            for (Room room : newLevelScheme.roomsToSpawn) listRooms.add(new RoomItem(room));
            sectionRooms = new SpawnSectionMore<RoomItem>(Messages.get(LevelGenComp.class, "rooms"), new ItemContainer<RoomItem>(listRooms) {
                @Override
                protected void onSlotNumChange() {
                    LevelGenComp.this.layout();
                }

                @Override
                protected Class<? extends Bag> getPreferredBag() {
                    return Rooms.bag.getClass();
                }

                @Override
                protected void doAddItem(RoomItem item) {
                    super.doAddItem(item);
                    newLevelScheme.roomsToSpawn.add(item.room());
                }

                @Override
                protected boolean removeSlot(ItemContainer<RoomItem>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        newLevelScheme.roomsToSpawn.remove(slot.item());
                        return true;
                    }
                    return false;
                }
            }) {
                @Override
                protected void onAddClick() {
                    Window w = new WndRoomSettings(newLevelScheme.spawnStandartRooms, newLevelScheme.spawnSecretRooms, newLevelScheme.spawnSpecialRooms) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnStandartRooms = stand.checked();
                            newLevelScheme.spawnSecretRooms = sec.checked();
                            newLevelScheme.spawnSpecialRooms = spec.checked();
                        }
                    };
                    if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                    else Game.scene().addToFront(w);
                }
            };
            content.add(sectionRooms);
        }

        sp = new ScrollPane(content);
        add(sp);
    }

    @Override
    public void layout() {

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

        if (sectionItems != null) {
            sectionItems.setRect(MARGIN, pos, width - MARGIN * 2, -1);
            pos = sectionItems.bottom() + MARGIN;
        }

        if (sectionMobs != null) {
            sectionMobs.setRect(MARGIN, pos, width - MARGIN * 2, -1);
            pos = sectionMobs.bottom() + MARGIN;
        }

        if (sectionRooms != null) {
            sectionRooms.setRect(MARGIN, pos, width - MARGIN * 2, -1);
            pos = sectionRooms.bottom() + MARGIN;
        }


        content.setSize(width, pos);
        if (sp != null) {
            sp.setSize(width, height);
            sp.scrollToCurrentView();
        }
    }

    private class SpawnSection<T extends Item> extends FoldableComp {


        public SpawnSection(String label, ItemContainer<T> container) {
            super(label, container);
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

        public SpawnSectionMore(String label, ItemContainer<T> container) {
            super(label);

            remove(adder);
            adder.destroy();
            adder = new IconButton(Icons.get(Icons.MORE)) {
                @Override
                protected void onClick() {
                    onAddClick();
                }
            };
            add(adder);

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

            RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(LevelGenComp.class, "room_settings_title"), 10);
            title.hardlight(Window.TITLE_COLOR);
            add(title);

            stand = new CheckBox(Messages.get(LevelGenComp.class, "room_settings_standart"));
            stand.checked(standart);
            add(stand);

            sec = new CheckBox(Messages.get(LevelGenComp.class, "room_settings_secret"));
            sec.checked(secret);
            add(sec);

            spec = new CheckBox(Messages.get(LevelGenComp.class, "room_settings_special"));
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