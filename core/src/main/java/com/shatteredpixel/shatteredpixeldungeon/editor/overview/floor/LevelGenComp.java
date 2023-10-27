package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Rooms;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.RoomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.FeelingSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.WndChallengeSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

//If you wanna see trash, then this file is the perfect place (FIXME)
public class LevelGenComp extends WndNewFloor.OwnTab {

    private ScrollPane sp;
    protected Component content;

    protected RenderedTextBlock title;

    protected ChooseObjectComp seed;
    protected FeelingSpinner feelingSpinner;

    protected ColorBlock line;

    protected Component sectionItems;
    protected Component sectionMobs;
    protected SpawnSectionMore<RoomItem> sectionRooms;
    protected RedButton challengeSettings;

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

        if (newLevelScheme.getName() == null || newLevelScheme.getType() != CustomLevel.class) {
            sectionItems = new SpawnSectionMore<Item>("items", new ItemContainer<Item>(newLevelScheme.itemsToSpawn, null, true) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionItems != null)
                        ((UpdateTitle) sectionItems).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

            }) {
                @Override
                protected void onAddClick() {
                    Window w = new WndItemSettings(newLevelScheme.spawnItems) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnItems = cb.checked();
                        }
                    };
                    if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                    else Game.scene().addToFront(w);
                }
            };

            List<MobItem> listMobs = new ArrayList<>();
            for (Mob mob : newLevelScheme.mobsToSpawn) listMobs.add(new MobItem(mob));
            sectionMobs = new SpawnSectionMore<MobItem>("mobs", new ItemContainer<MobItem>(listMobs, null, true) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionMobs != null) ((UpdateTitle) sectionMobs).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

                @Override
                public Class<? extends Bag> preferredBag() {
                    return Mobs.bag.getClass();
                }

                @Override
                protected void doAddItem(MobItem item) {
                    newLevelScheme.mobsToSpawn.add(item.mob());
                    if (item.mob() instanceof Blacksmith) {
                        int numSmiths = 0;
                        for (Mob m : newLevelScheme.mobsToSpawn) {
                            if (m instanceof Blacksmith) numSmiths++;
                        }
                        int numSmithRooms = 0;
                        for (Room r : newLevelScheme.roomsToSpawn) {
                            if (r instanceof BlacksmithRoom) numSmithRooms++;
                        }
                        if (numSmithRooms < numSmiths)
                            sectionRooms.container.addNewItem(new RoomItem(new BlacksmithRoom()));
                    }
                    super.doAddItem(item);
                }

                @Override
                protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        newLevelScheme.mobsToSpawn.remove(((MobItem) slot.item()).mob());
                        return true;
                    }
                    return false;
                }

            }) {
                @Override
                protected void onAddClick() {
                    Window w = new WndMobSettings(newLevelScheme.spawnMobs) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnMobs = cb.checked();
                        }
                    };
                    if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                    else Game.scene().addToFront(w);
                }
            };
        } else {
            sectionItems = new SpawnSection<>("items", new ItemContainer<Item>(newLevelScheme.itemsToSpawn, null, true) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionItems != null)
                        ((UpdateTitle) sectionItems).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

            });

            List<MobItem> listMobs = new ArrayList<>();
            for (Mob mob : newLevelScheme.mobsToSpawn) listMobs.add(new MobItem(mob));
            sectionMobs = new SpawnSection<>("mobs", new ItemContainer<MobItem>(listMobs, null, true) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionMobs != null) ((UpdateTitle) sectionMobs).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

                @Override
                public Class<? extends Bag> preferredBag() {
                    return Mobs.bag.getClass();
                }

                @Override
                protected void doAddItem(MobItem item) {
                    newLevelScheme.mobsToSpawn.add(item.mob());
                    if (item.mob() instanceof Blacksmith) {
                        int numSmiths = 0;
                        for (Mob m : newLevelScheme.mobsToSpawn) {
                            if (m instanceof Blacksmith) numSmiths++;
                        }
                        int numSmithRooms = 0;
                        for (Room r : newLevelScheme.roomsToSpawn) {
                            if (r instanceof BlacksmithRoom) numSmithRooms++;
                        }
                        if (numSmithRooms < numSmiths)
                            sectionRooms.container.addNewItem(new RoomItem(new BlacksmithRoom()));
                    }
                    super.doAddItem(item);
                }

                @Override
                protected boolean removeSlot(ItemContainer<MobItem>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        newLevelScheme.mobsToSpawn.remove(((MobItem) slot.item()).mob());
                        return true;
                    }
                    return false;
                }

            });
        }

        content.add(sectionItems);
        content.add(sectionMobs);

        if (newLevelScheme.getName() == null || newLevelScheme.getType() != CustomLevel.class) {
            List<RoomItem> listRooms = new ArrayList<>();
            for (Room room : newLevelScheme.roomsToSpawn) listRooms.add(new RoomItem(room));
            sectionRooms = new SpawnSectionMore<RoomItem>("rooms", new ItemContainer<RoomItem>(listRooms, null, true) {
                @Override
                protected void onSlotNumChange() {
                    if (sectionRooms != null) sectionRooms.updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

                @Override
                public Class<? extends Bag> preferredBag() {
                    return Rooms.bag.getClass();
                }

                @Override
                protected void doAddItem(RoomItem item) {
                    newLevelScheme.roomsToSpawn.add(item.room());
                    if (item.room() instanceof BlacksmithRoom) {
                        int numSmiths = 0;
                        for (Mob m : newLevelScheme.mobsToSpawn) {
                            if (m instanceof Blacksmith) numSmiths++;
                        }
                        int numSmithRooms = 0;
                        for (Room r : newLevelScheme.roomsToSpawn) {
                            if (r instanceof BlacksmithRoom) numSmithRooms++;
                        }
                        if (numSmiths < numSmithRooms)
                            ((SpawnSectionMore<MobItem>) sectionMobs).container.addNewItem(new MobItem(new Blacksmith(new BlacksmithQuest())));
                    }
                    super.doAddItem(item);
                }

                @Override
                protected boolean removeSlot(ItemContainer<RoomItem>.Slot slot) {
                    if (super.removeSlot(slot)) {
                        newLevelScheme.roomsToSpawn.remove(((RoomItem) slot.item()).room());
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

        challengeSettings = new RedButton(Messages.get(WndChallengeSettings.class, "title")) {

            @Override
            protected void onClick() {
                Window w = new WndChallengeSettings(newLevelScheme);
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
            }
        };
        content.add(challengeSettings);

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
        pos = feelingSpinner.bottom() + MARGIN * 2;

        if (challengeSettings != null) {
            challengeSettings.setRect(MARGIN, pos, width - MARGIN * 2, BUTTON_HEIGHT);
            pos = challengeSettings.bottom() + MARGIN * 4;
        }

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

    private interface UpdateTitle {
        void updateTitle(int numSlots);
    }

    private class SpawnSection<T extends Item> extends FoldableComp implements UpdateTitle {

        private final String key;

        public SpawnSection(String key, ItemContainer<T> container) {
            super(container);
            this.key = key;
            container.setSize(LevelGenComp.this.width, -1);
            showBody(false);
            updateTitle(container.getNumSlots());
        }

        @Override
        protected void layoutParent() {
            LevelGenComp.this.layout();
        }

        public void updateTitle(int numSlots) {
            title.text(Messages.get(LevelGenComp.class, key) + " (" + numSlots + ")");
        }

    }

    private abstract class SpawnSectionMore<T extends Item> extends FoldableCompWithAdd implements UpdateTitle {

        private ItemContainer<T> container;
        private final String key;

        public SpawnSectionMore(String key, ItemContainer<T> container) {
            super();
            this.key = key;

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

            updateTitle(container.getNumSlots());
        }

        @Override
        protected void layoutParent() {
            LevelGenComp.this.layout();
        }

        @Override
        protected final Component createBody(Object param) {
            return null;
        }

        public void updateTitle(int numSlots) {
            title.text(Messages.get(LevelGenComp.class, key) + " (" + numSlots + ")");
        }
    }


    //TODO copy pasta this entire file here is so trash...
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

    private static abstract class WndMobSettings extends Window {

        protected CheckBox cb;

        public WndMobSettings(boolean spawn) {

            resize(PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : (int) (PixelScene.uiCamera.width * 0.85f), 100);

            RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(LevelGenComp.class, "mob_settings_title"), 10);
            title.hardlight(Window.TITLE_COLOR);
            add(title);

            cb = new CheckBox(Messages.get(LevelGenComp.class, "spawn_mobs"));
            cb.checked(spawn);
            add(cb);

            title.setPos((width - title.width()) * 0.5f, 2);
            cb.setRect(0, title.bottom() + 5, width, 16);

            resize(width, (int) (Math.ceil(cb.bottom()) + 3));
        }

        @Override
        public void hide() {
            super.hide();
            finish();
        }

        protected abstract void finish();

    }

    private static abstract class WndItemSettings extends Window {

        protected CheckBox cb;

        public WndItemSettings(boolean spawn) {

            resize(PixelScene.landscape() ? WndTitledMessage.WIDTH_MAX : (int) (PixelScene.uiCamera.width * 0.85f), 100);

            RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(LevelGenComp.class, "item_settings_title"), 10);
            title.hardlight(Window.TITLE_COLOR);
            add(title);

            cb = new CheckBox(Messages.get(LevelGenComp.class, "spawn_items"));
            cb.checked(spawn);
            add(cb);

            title.setPos((width - title.width()) * 0.5f, 2);
            cb.setRect(0, title.bottom() + 5, width, 16);

            resize(width, (int) (Math.ceil(cb.bottom()) + 3));
        }

        @Override
        public void hide() {
            super.hide();
            finish();
        }

        protected abstract void finish();

    }
}