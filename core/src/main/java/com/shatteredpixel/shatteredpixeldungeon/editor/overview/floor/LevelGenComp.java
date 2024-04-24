package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableCompWithAdd;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.MARGIN;

//If you wanna see trash, then this file is the perfect place (FIXME)
public class LevelGenComp extends WndNewFloor.OwnTab {

    private ScrollPane sp;
    protected Component content;

    protected RenderedTextBlock title;

    protected StyledButton seed;
    protected FeelingSpinner feelingSpinner;
    private String currentSeed;

    protected FoldableComp challengeSettings;

    protected Component sectionItems;
    protected Component sectionMobs;
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

        if (newLevelScheme.isSeedSet()) currentSeed = DungeonSeed.convertToCode(newLevelScheme.getSeed());
        seed = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, "") {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
            }
            @Override
            protected void onClick() {
                EditorScene.show( new WndTextInput(Messages.get(HeroSelectScene.class, "custom_seed_title"),
                        Messages.get(LevelGenComp.class, "enter_seed_prompt"),
                        currentSeed == null ? "" : currentSeed,
                        20,
                        false,
                        Messages.get(HeroSelectScene.class, "custom_seed_set"),
                        Messages.get(HeroSelectScene.class, "custom_seed_clear")) {
                    @Override
                    public void onSelect(boolean positive, String text) {
                        text = DungeonSeed.formatText(text);
                        long s = DungeonSeed.convertFromText(text);
                        if (positive && s != -1) {
                            currentSeed = text;
                            newLevelScheme.setSeed(s);
                        } else {
                            currentSeed = null;
                            newLevelScheme.resetSeed();
                        }
                        updateSeedText();
                    }
                } );
            }
        };
        seed.icon(new ItemSprite(ItemSpriteSheet.SEED_SUNGRASS));
        updateSeedText();
        content.add(seed);

        feelingSpinner = new FeelingSpinner(newLevelScheme.getFeeling(), 9, true);
        feelingSpinner.addChangeListener(() -> {
            newLevelScheme.setFeeling((Level.Feeling) feelingSpinner.getValue());
            onFeelingChange();
        });
        content.add(feelingSpinner);

        challengeSettings = new FoldableComp(Messages.get(LevelGenComp.class, "challenge_settings_title")) {
            @Override
            protected void layoutParent() {
                LevelGenComp.this.layout();
            }
        };
        challengeSettings.setBody(new Component() {

            private RenderedTextBlock info;
            private RenderedTextBlock[] challengeTitles;
            private StyledCheckBox[][] checkBoxes;

            {
                info = PixelScene.renderTextBlock(Messages.get(LevelGenComp.class, "challenge_settings_info"), 6);
                add(info);

                challengeTitles = new RenderedTextBlock[newLevelScheme.getName() != null && newLevelScheme.getType() == CustomLevel.class ? 3 : 2];
                checkBoxes = new StyledCheckBox[challengeTitles.length][];

                int i = 0;
                challengeTitles[i++] = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(Challenges.class, "darkness")) + ":", 9);
                challengeTitles[i++] = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(Challenges.class, "no_scrolls")) + ":", 9);
                if (i < challengeTitles.length)
                    challengeTitles[i++] = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(Challenges.class, "champion_enemies")) + ":", 9);

                for (RenderedTextBlock chTitle : challengeTitles) {
                    add(chTitle);
                }

                i = 0;
                checkBoxes[i++] = new StyledCheckBox[] {
                        new StyledCheckBox(Messages.titleCase(Messages.get(LevelGenComp.class, "spawn_torch"))) {
                            {
                                text.align(RenderedTextBlock.CENTER_ALIGN);
                                super.checked(newLevelScheme.spawnTorchIfDarkness);
                            }

                            @Override
                            public void checked(boolean value) {
                                super.checked(value);
                                newLevelScheme.spawnTorchIfDarkness = value;
                            }
                        },
                        new StyledCheckBox(Messages.get(LevelGenComp.class, "reduce_view")) {
                            {
                                text.align(RenderedTextBlock.CENTER_ALIGN);
                                super.checked(newLevelScheme.reduceViewDistanceIfDarkness);
                            }

                            @Override
                            public void checked(boolean value) {
                                super.checked(value);
                                newLevelScheme.reduceViewDistanceIfDarkness = value;
                            }
                        }};
                checkBoxes[i++] = new StyledCheckBox[] {
                        new StyledCheckBox(Messages.get(LevelGenComp.class, "remove_scrolls")) {
                            {
                                text.align(RenderedTextBlock.CENTER_ALIGN);
                                super.checked(newLevelScheme.affectedByNoScrolls);
                            }

                            @Override
                            public void checked(boolean value) {
                                super.checked(value);
                                newLevelScheme.affectedByNoScrolls = value;
                            }
                        }
                };
                if (i < checkBoxes.length) {
                    checkBoxes[i++] = new StyledCheckBox[] {
                            new StyledCheckBox(Messages.get(LevelGenComp.class, "spawn_champs")) {
                                {
                                    text.align(RenderedTextBlock.CENTER_ALIGN);
                                    super.checked(newLevelScheme.rollForChampionIfChampionChallenge);
                                }

                                @Override
                                public void checked(boolean value) {
                                    super.checked(value);
                                    newLevelScheme.rollForChampionIfChampionChallenge = value;
                                }
                            }};
                }

                for (StyledCheckBox[] cbs : checkBoxes)
                    for (StyledCheckBox cb : cbs)
                        add(cb);
            }

            @Override
            protected void layout() {
                info.maxWidth((int) width);
                info.setPos(x, y);

                height = info.height();

                for (int i = 0; i < challengeTitles.length; i++) {
                    height += 5;
                    height = EditorUtilies.layoutCompsLinear(2, this, challengeTitles[i]) + 3;
                    height = EditorUtilies.layoutStyledCompsInRectangles(
                            2, width, Math.min(checkBoxes[i].length, PixelScene.landscape() ? 3 : 2), this, checkBoxes[i]);
                }

            }
        });
        challengeSettings.fold();
        content.add(challengeSettings);

        if (newLevelScheme.getName() == null || newLevelScheme.getType() != CustomLevel.class) {
            sectionItems = new SpawnSectionMore<Item>("items", new ItemContainer<Item>(newLevelScheme.itemsToSpawn) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionItems != null)
                        ((UpdateTitle) sectionItems).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

            }) {
                @Override
                protected void onAddClick() {
                    EditorScene.show( new WndItemSettings(newLevelScheme.spawnItems) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnItems = cb.checked();
                        }
                    } );
                }
            };

            List<MobItem> listMobs = new ArrayList<>();
            for (Mob mob : newLevelScheme.mobsToSpawn) listMobs.add(new MobItem(mob));
            sectionMobs = new SpawnSectionMore<MobItem>("mobs", new ItemContainer<MobItem>(listMobs) {

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
                   EditorScene.show( new WndMobSettings(newLevelScheme.spawnMobs) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnMobs = cb.checked();
                        }
                    } );
                }
            };
        } else {
            sectionItems = new SpawnSection<>("items", new ItemContainer<Item>(newLevelScheme.itemsToSpawn) {

                @Override
                protected void onSlotNumChange() {
                    if (sectionItems != null)
                        ((UpdateTitle) sectionItems).updateTitle(getNumSlots());
                    LevelGenComp.this.layout();
                }

            });

            List<MobItem> listMobs = new ArrayList<>();
            for (Mob mob : newLevelScheme.mobsToSpawn) listMobs.add(new MobItem(mob));
            sectionMobs = new SpawnSection<>("mobs", new ItemContainer<MobItem>(listMobs) {

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
            sectionRooms = new SpawnSectionMore<RoomItem>("rooms", new ItemContainer<RoomItem>(listRooms) {
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
                    EditorScene.show( new WndRoomSettings(newLevelScheme.spawnStandartRooms, newLevelScheme.spawnSecretRooms, newLevelScheme.spawnSpecialRooms) {
                        @Override
                        protected void finish() {
                            newLevelScheme.spawnStandartRooms = stand.checked();
                            newLevelScheme.spawnSecretRooms = sec.checked();
                            newLevelScheme.spawnSpecialRooms = spec.checked();
                        }
                    } );
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

        content.setSize(width, title.bottom() + 4 * MARGIN);

        content.setSize(width, EditorUtilies.layoutStyledCompsInRectangles(MARGIN * 2, width, 2, content, seed, feelingSpinner) + 2);

        content.setSize(width, EditorUtilies.layoutCompsLinear(MARGIN * 2, content, challengeSettings, sectionItems, sectionMobs, sectionRooms));

        if (sp != null) {
            sp.setSize(width, height);
            sp.scrollToCurrentView();
        }
    }

    protected void updateSeedText() {
        seed.text( Messages.get(this, "seed") + "\n" + (currentSeed == null ? Messages.get(this, "no_seed") : currentSeed) );
    }

    protected void onFeelingChange() {}

    @Override
    public String hoverText() {
        return Messages.get(LevelGenComp.class, "title");
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

            stand = new CheckBox(Messages.get(LevelGenComp.class, "room_settings_standard"));
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