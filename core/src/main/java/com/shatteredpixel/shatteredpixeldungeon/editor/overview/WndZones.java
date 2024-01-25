package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import static com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor.BUTTON_HEIGHT;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditTileComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffListContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ZoneMobSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndEditFloorInOverview;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.ZonePrompt;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndColorPicker;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.SlowExtendWindow;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Point;
import com.watabou.utils.Reflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public final class WndZones {

    private WndZones() {
    }

    private static final int MARGIN = 2;

    public static abstract class ZoneListPane extends ScrollingListPane {

        public void updateList() {
            clear();
            List<Zone> zones = new ArrayList<>(Dungeon.level.zoneMap.values());
            if (zones.isEmpty()) ;//TODO show that its empty
            Collections.sort(zones, (a, b) -> a.getName().compareTo(b.getName()));

            for (Zone zone : zones) {
                addItem(new ListItem(zone, this) {
                    @Override
                    protected void onClick() {
                        onSelect(zone, this);
                    }

                    @Override
                    protected void onRightClick() {
                        if (!onLongClick()) {
                            onClick();
                        }
                    }

                    @Override
                    protected boolean onLongClick() {
                        return onEdit(zone, this);
                    }
                });
            }
            scrollToCurrentView();
        }

        protected abstract void onSelect(Zone zone, ListItem listItem);

        public boolean onEdit(Zone zone, ListItem listItem) {
            EditorScene.show(new EditCompWindow(zone, listItem));
            return true;
        }

        public Camera getContentCamera() {
            return content.camera();
        }

        public static class ListItem extends AdvancedListPaneItem {

            private final Zone zone;
            private final ZoneListPane listPane;

            protected IconButton editButton;


            public ListItem(Zone zone, ZoneListPane listPane) {
                super(Icons.get(Icons.ZONE), null, zone.getName());
                this.zone = zone;
                this.listPane = listPane;
                label.setHighlighting(false);

                onUpdate();
            }

            @Override
            protected void createChildren(Object... params) {
                super.createChildren(params);

                editButton = new IconButton(Icons.get(Icons.RENAME_ON)) {
                    @Override
                    protected void onClick() {
                        openEditWindow();
                    }
                };
                add(editButton);
            }

            public void openEditWindow() {
                EditorScene.show(new EditCompWindow(zone, this));
            }

            @Override
            protected void layout() {
                super.layout();

                if (editButton != null) {
                    editButton.setRect(width - 3 - editButton.icon().width(), y + (height - editButton.icon().height()) * 0.5f, editButton.icon().width(), editButton.icon().height());
                    hotArea.width = editButton.left() - 1;
                }

            }

            @Override
            protected int getLabelMaxWidth() {
                return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
            }

            @Override
            public void onUpdate() {
                if (zone != null) {
                    icon.hardlight(zone.getColor());
                    label.text(zone.getName());
                }
                super.onUpdate();
            }
        }
    }


    public static class WndSelectZone extends SlowExtendWindow {

        public static final float TIME_TO_OPEN_WINDOW = 0.2f;//in seconds

        private static WndSelectZone instance;

        protected RenderedTextBlock desc;
        protected ZoneListPane listPane;
        protected RedButton createZone;

        public WndSelectZone(Consumer<Zone> onSelect, int posY) {

            super(
                    PixelScene.landscape() ? 200 : Math.min(150, (int) (PixelScene.uiCamera.width * 0.8)),
                    (int) ((PixelScene.uiCamera.height / 2) * 0.85f + posY),
                    Orientation.BOTTOM_TO_TOP,
                    new Point(0, posY));

            speed = endHeight / (TIME_TO_OPEN_WINDOW * 100);

            instance = this;

            desc = PixelScene.renderTextBlock(Messages.get(WndSelectZone.class, "desc"), 6);
            add(desc);

            listPane = new ZoneListPane() {

                @Override
                protected void onSelect(Zone zone, ListItem listItem) {
                    onSelect.accept(zone);
                    hide();
                }
            };
            add(listPane);

            createZone = new RedButton(Messages.get(this, "new_zone")) {
                @Override
                protected void onClick() {
                    EditorScene.show(new WndNewZone());
                }
            };
            add(createZone);

            desc.maxWidth(endWidth - MARGIN * 2);
            desc.setPos(MARGIN, MARGIN);

            createZone.setRect(MARGIN, endHeight - MARGIN - 18, endWidth - MARGIN, 18);
            PixelScene.align(createZone);

            listPane.setRect(0, desc.bottom() + MARGIN * 2, endWidth, createZone.top() - MARGIN * 4 - desc.height());
            PixelScene.align(listPane);

            listPane.updateList();

            spCamera = listPane.getContentCamera();
            scrollPane = listPane;

        }

        public static void updateList() {
            if (instance == null) return;
            instance.listPane.updateList();
        }

        @Override
        public void destroy() {
            super.destroy();
            instance = null;
        }

    }

    public static class WndNewZone extends Window {

        protected Component spContent;
        private final ScrollPane sp;
        protected IconTitle title;
        protected Image titleIcon;
        private EditZoneCompBase editZoneComp;

        protected RedButton create, cancel;

        protected TextInput textBox;

        public WndNewZone() {
            resize(PixelScene.landscape() ? 210 : Math.min(155, (int) (PixelScene.uiCamera.width * 0.88)), 100);
            Zone zone = new Zone();

            titleIcon = Icons.ZONE.get();
            titleIcon.hardlight(zone.getColor());
            title = new IconTitle(titleIcon, Messages.get(this, "title"));
            add(title);

            int textSize = (int) PixelScene.uiCamera.zoom * 9;
            textBox = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, textSize) {
                @Override
                public void enterPressed() {
                    if (!getText().isEmpty()) {
                        zone.name = textBox.getText();
                        create(zone);
                    }
                }
            };
            textBox.setMaxLength(50);
            add(textBox);
            Game.platform.setOnscreenKeyboardVisible(false);

            create = new RedButton(Messages.get(WndNewDungeon.class, "yes")) {
                @Override
                protected void onClick() {
                    if (!textBox.getText().isEmpty()) {
                        zone.name = textBox.getText();
                        create(zone);
                    }
                }
            };
            add(create);
            cancel = new RedButton(Messages.get(WndNewDungeon.class, "no")) {
                @Override
                protected void onClick() {
                    create(null);
                }
            };
            add(cancel);

            spContent = new Component() {
                @Override
                protected void layout() {
                    editZoneComp.setRect(0, 0, width, -1);
                    height = editZoneComp.height() + 1;
                }
            };

            editZoneComp = new EditZoneCompBase(zone) {

                {
                    title.visible = title.active = false;
                    desc.visible = desc.active = false;
                }

                @Override
                protected void layout() {
                    //no title or desc
                    height = -1;
                    layoutCompsInRectangles(comps);
                    layoutCompsLinear(transitionEdit, chasmDest, heroBuffs, mobBuffs);
                }

                @Override
                public void updateObj() {
                    titleIcon.hardlight(obj.getColor());
                    super.updateObj();
                    spContent.setPos(0,0);
                }

                @Override
                protected void showColorPickerDialog() {
                    textBox.active = false;
                    EditorScene.show(new WndColorPicker(zone.getColor()) {
                        @Override
                        public void setSelectedColor(int color) {
                            super.setSelectedColor(color);
                            zone.setColor(color);
                            updateObj();
                        }

                        @Override
                        public void hide() {
                            super.hide();
                            textBox.active = true;
                        }
                    });
                }
            };

            spContent.add(editZoneComp);

            sp = new ScrollPane(spContent);
            add(sp);

            float posY = MARGIN;
            title.setRect(MARGIN, posY, width, title.height());
            posY = title.bottom() + MARGIN * 2;

            final float textBoxPos = posY;
            posY = textBoxPos + 16 + MARGIN;

            spContent.setSize(width, -1);
            final float spPos = posY;
            final float spHeight = Math.min((int) (PixelScene.uiCamera.height * 0.8f) - posY - BUTTON_HEIGHT - 1, spContent.height());
            posY += spHeight + MARGIN * 2;

            create.setRect(MARGIN, posY, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
            cancel.setRect(create.right() + MARGIN * 2, posY, (width - MARGIN * 2) / 2, BUTTON_HEIGHT + 1);
            posY = create.bottom() + MARGIN;

            resize(width, (int) Math.ceil(posY));

            textBox.setRect(MARGIN, textBoxPos, width - MARGIN * 2, 16);
            sp.setRect(0, spPos, width, spHeight);
        }

        @Override
        public void onBackPressed() {
        }

        public void create(Zone zone) {
            if (zone != null) {

                if (Dungeon.level.zoneMap.containsKey(zone.getName())) {
                    showDuplicateNameWarning();
                    return;
                }
                Dungeon.level.zoneMap.put(zone.getName(), zone);
                Dungeon.level.levelScheme.zones.add(zone.getName());
                WndSelectZone.updateList();
                ZonePrompt.setSelectedZone(zone);
            }
            hide();
        }

    }

    private static void showDuplicateNameWarning() {
        EditorScene.show(
                new WndOptions(Icons.get(Icons.WARNING),
                        Messages.get(WndNewDungeon.class, "dup_name_title"),
                        Messages.get(WndNewZone.class, "dup_name_body"),
                        Messages.get(WndSupportPrompt.class, "close")
                )
        );
    }

    public static class EditZoneCompBase extends DefaultEditComp<Zone> {

        protected Component[] comps;

        protected StyledButton addTransition;
        protected StyledSpinner chasmDest;
        protected TransitionEditPart transitionEdit;
        protected BuffListContainer heroBuffs, mobBuffs;

        public EditZoneCompBase(Zone zone) {
            super(zone);

            StyledButton pickColor = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditZoneComp.class, "color"), 9) {
                @Override
                protected void onClick() {
                    showColorPickerDialog();
                }
            };
            pickColor.icon(Icons.COLORS.get());
            pickColor.multiline = true;

            StyledCheckBox flamable = new StyledCheckBox(Messages.get(EditZoneComp.class, "flamable")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.flamable = value;
                }
            };
            flamable.checked(zone.flamable);
            flamable.icon(BlobItem.createIcon(PermaGas.PFire.class));

            StyledCheckBox spawnMobs = new StyledCheckBox(Messages.get(EditZoneComp.class, "spawn_mobs")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.canSpawnMobs = value;
                }
            };
            spawnMobs.checked(zone.canSpawnMobs);
            spawnMobs.icon(new GnollSprite());

            StyledCheckBox spawnItems = new StyledCheckBox(Messages.get(EditZoneComp.class, "spawn_items")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.canSpawnItems = value;
                }
            };
            spawnItems.checked(zone.canSpawnItems);
            spawnItems.icon(new ItemSprite(ItemSpriteSheet.CHEST));

            StyledCheckBox teleportTo = new StyledCheckBox(Messages.get(EditZoneComp.class, "teleport_to")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.canTeleportTo = value;
                }
            };
            teleportTo.checked(zone.canTeleportTo);
            Image teleIcon = IconTitleWithSubIcon.createSubIcon(ItemSpriteSheet.Icons.SCROLL_TELEPORT);
            teleIcon.scale.set(ItemSpriteSheet.SIZE / Math.max(teleIcon.width(), teleIcon.height()));
            teleportTo.icon(teleIcon);

            StyledCheckBox destroyWalls = new StyledCheckBox(Messages.get(EditZoneComp.class, "destroy_walls")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.canDestroyWalls = value;
                }
            };
            destroyWalls.checked(zone.canDestroyWalls);
            destroyWalls.icon(new ItemSprite(EditorScene.customLevel().tilesTex(), new TileItem(Terrain.WALL, -1)));

            StyledCheckBox blocksVision = new StyledCheckBox(Messages.get(EditZoneComp.class, "blocks_vision")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    zone.blocksVision = value;
                }
            };
            blocksVision.checked(zone.blocksVision);
            blocksVision.icon(new BuffIcon(BuffIndicator.BLINDNESS, true));

            StyledSpinner grassVisuals = new StyledSpinner(new SpinnerTextIconModel(true, zone.grassType.index, (Object[]) Zone.GrassType.values()) {
                @Override
                protected Image getIcon(Object value) {
                    switch ((Zone.GrassType) value) {
                        case NONE:
                            return new Image();
                        case GRASS:
                        case HIGH_GRASS:
                        case FURROWED_GRASS:
                            return new TileItem(((Zone.GrassType) value).terrain, -1).getSprite();
                    }
                    return new Image();
                }

                @Override
                protected String getAsString(Object value) {
                    switch ((Zone.GrassType) value) {
                        case NONE:
                            return Messages.get(EditZoneComp.class, "no_grass");
                        case GRASS:
                        case HIGH_GRASS:
                        case FURROWED_GRASS:
                            return TileItem.getName(((Zone.GrassType) value).terrain, -1);
                    }
                    return Messages.NO_TEXT_FOUND;
                }
            }, Messages.get(EditZoneComp.class, "grass_label"), 9,
                    new ItemSprite(EditorScene.customLevel().tilesTex(), new TileItem(Terrain.HIGH_GRASS, -1)));
            grassVisuals.addChangeListener(() -> zone.setGrassType((Zone.GrassType) grassVisuals.getValue()));

            StyledButton mobRotation = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditTileComp.class, "mob_rotation"), 9) {
                @Override
                protected void onClick() {
                    SimpleWindow w = new SimpleWindow(WndEditorSettings.calclulateWidth(), WndEditorSettings.calclulateHeight());
                    ZoneMobSettings ms = new ZoneMobSettings(zone);
                    w.initComponents(ms.createTitle(), ms, ms.getOutsideSp(), 0f, 0.5f);
                    EditorScene.show(w);
                }
            };
            mobRotation.multiline = true;

            LevelScheme chasm = Dungeon.customDungeon.getFloor(Dungeon.level.levelScheme.getChasm());
            Object[] data;
            int index = 0;
            if (chasm != null) {
                List<String> zones = new ArrayList<>(chasm.zones);
                if (!zones.isEmpty()) Collections.sort(zones, (a, b) -> a.compareTo(b));
                zones.add(0, null);
                data = zones.toArray(EditorUtilies.EMPTY_STRING_ARRAY);
                if (zone.chasmDestZone != null) {
                    index++;
                    for (; index < data.length; index++) {
                        if (zone.chasmDestZone.equals(data[index])) break;
                    }
                    if (index == data.length) {
                        zone.chasmDestZone = null;
                        index = 0;
                    }
                }
            } else data = new Object[]{null};
            chasmDest = new StyledSpinner(new SpinnerTextModel(true, index, data) {
                @Override
                protected String getAsString(Object value) {
                    if (value == null) return Messages.get(Zone.class, "none_zone");
                    return super.getAsString(value);
                }
            }, Messages.get(EditZoneComp.class, "chasm_dest") + ":", 9, new ItemSprite(EditorScene.customLevel().tilesTex(), new TileItem(Terrain.CHASM, -1)));
            chasmDest.addChangeListener(() -> zone.chasmDestZone = (String) chasmDest.getValue());
            chasmDest.enable(chasm != null);

            addTransition = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditTileComp.class, "add_transition"), 9) {
                @Override
                protected void onClick() {
                    addTransition(new LevelTransition(EditorScene.customLevel(), TransitionEditPart.NONE, TransitionEditPart.DEFAULT, null));
                }
            };
            addTransition.multiline = true;

            comps = new Component[]{pickColor, flamable, spawnMobs, spawnItems, teleportTo, destroyWalls, blocksVision, grassVisuals, mobRotation, addTransition};

            if (zone.zoneTransition != null) {
                addTransition(zone.zoneTransition);
            }

            List<BuffItem> asBuffItems = new ArrayList<>();
            for (Class<? extends Buff> b : zone.heroBuffs) {
                Buff buff = Reflection.newInstance(b);
                buff.zoneBuff = buff.permanent = true;
                asBuffItems.add(new BuffItem(buff));
            }
            heroBuffs = new BuffListContainer(asBuffItems, EditZoneCompBase.this, Messages.get(EditZoneComp.class, "hero_buffs")) {
                @Override
                protected Set<Class<? extends Buff>> getBuffsToIgnore() {
                    Set<Class<? extends Buff>> buffsToIgnore = super.getBuffsToIgnore();
                    for (Class<? extends Buff> c : ChampionEnemy.CLASSES) {
                        buffsToIgnore.add(c);
                    }
                    buffsToIgnore.add(Amok.class);
                    buffsToIgnore.add(Terror.class);
                    buffsToIgnore.add(Dread.class);
                    return buffsToIgnore;
                }
                @Override
                protected Buff doAddBuff(Class<? extends Buff> buff) {
                    zone.heroBuffs.add(buff);
                    updateObj();
                    Buff b = Reflection.newInstance(buff);
                    b.zoneBuff = b.permanent = true;
                    return b;
                }

                @Override
                protected void doRemoveBuff(Buff buff) {
                    zone.heroBuffs.remove(buff.getClass());
                    updateObj();
                }
            };
            add(heroBuffs);

            asBuffItems = new ArrayList<>();
            for (Class<? extends Buff> b : zone.mobBuffs) {
                Buff buff = Reflection.newInstance(b);
                buff.zoneBuff = buff.permanent = true;
                asBuffItems.add(new BuffItem(buff));
            }
            mobBuffs = new BuffListContainer(asBuffItems, EditZoneCompBase.this, Messages.get(EditZoneComp.class, "mob_buffs")) {
                @Override
                protected Set<Class<? extends Buff>> getBuffsToIgnore() {
                    Set<Class<? extends Buff>> buffsToIgnore = super.getBuffsToIgnore();
                    buffsToIgnore.add(MagicalSight.class);
                    buffsToIgnore.add(Foresight.class);
                    return buffsToIgnore;
                }

                @Override
                protected Buff doAddBuff(Class<? extends Buff> buff) {
                    zone.mobBuffs.add(buff);
                    updateObj();
                    Buff b = Reflection.newInstance(buff);
                    b.zoneBuff = b.permanent = true;
                    return b;
                }

                @Override
                protected void doRemoveBuff(Buff buff) {
                    zone.mobBuffs.remove(buff.getClass());
                    updateObj();
                }
            };
            add(mobBuffs);

            for (Component c : comps) {
                if (c != null) add(c);
            }
            add(chasmDest);

        }

        @Override
        protected void layout() {
            super.layout();

            title.setRect(x, y, width, title.height());
            desc.setRect(x, title.bottom() + WndTitledMessage.GAP * 2, desc.width(), desc.height());

            height = desc.bottom() + 1;
            layoutCompsInRectangles(comps);
            layoutCompsLinear(transitionEdit, chasmDest, heroBuffs, mobBuffs);
        }

        private void addTransition(LevelTransition transition) {
            transitionEdit = EditTileComp.addTransition(-12345, transition, EditorScene.customLevel().levelScheme, t -> obj.zoneTransition = null);
            add(transitionEdit);
            obj.zoneTransition = transition;
            addTransition.visible = addTransition.active = false;
            layout();
            updateObj();//for resize
        }

        @Override
        protected Component createTitle() {
            return new IconTitle(getIcon(), obj.getName());
        }

        @Override
        protected String createDescription() {
            return null;
        }

        @Override
        public Image getIcon() {
            Image icon = Icons.ZONE.get();
            icon.hardlight(obj.getColor());
            return icon;
        }

        @Override
        public void updateObj() {
            if (title instanceof IconTitle) {
                ((IconTitle) title).icon(getIcon());
                ((IconTitle) title).label(obj.getName());
            }

            super.updateObj();
        }

        protected void showColorPickerDialog() {
            EditorScene.show(new WndColorPicker(obj.getColor()) {
                @Override
                public void setSelectedColor(int color) {
                    super.setSelectedColor(color);
                    obj.setColor(color);
                    updateObj();
                }
            });
        }

    }

    public static class EditZoneComp extends EditZoneCompBase {

        protected IconButton rename, delete;

        public EditZoneComp(Zone zone) {
            super(zone);

            rename = new IconButton(Icons.get(Icons.RENAME_ON)) {
                @Override
                protected void onClick() {
                    Window w = new WndTextInput(Messages.get(EditZoneComp.class, "rename_title"),
                            "",
                            zone.getName(),
                            100,
                            false,
                            Messages.get(WndSelectDungeon.class, "rename_yes"),
                            Messages.get(WndSelectDungeon.class, "export_no")) {
                        @Override
                        public void onSelect(boolean positive, String text) {
                            if (positive && !text.isEmpty()) {
                                for (String floorN : Dungeon.level.levelScheme.zones) {
                                    if (floorN.equals(text)) {
                                        showDuplicateNameWarning();
                                        return;
                                    }
                                }
                                if (!text.equals(zone.getName())) {
                                    Dungeon.customDungeon.renameZone(zone, text);
                                    WndSelectZone.updateList();
                                    Window oldW = EditorUtilies.getParentWindow(rename);
                                    if (oldW != null) {
                                        oldW.hide();
                                        EditorScene.show(new EditCompWindow(zone));
                                    }
                                }
                            }
                        }
                    };
                    EditorScene.show(w);
                }

                @Override
                protected String hoverText() {
                    return Messages.get(WndSelectDungeon.class, "rename_yes");
                }
            };
            add(rename);

            delete = new IconButton(Icons.CLOSE.get()) {
                @Override
                protected void onClick() {
                    super.onClick();

                    EditorScene.show(new WndOptions(Icons.get(Icons.WARNING),
                            Messages.get(EditZoneComp.class, "erase_title"),
                            Messages.get(EditZoneComp.class, "erase_body"),
                            Messages.get(WndEditFloorInOverview.class, "erase_yes"),
                            Messages.get(WndGameInProgress.class, "erase_warn_no")) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Window oldW = EditorUtilies.getParentWindow(delete);
                                if (oldW != null) {
                                    oldW.hide();//important to hide before deletion
                                }
                                try {
                                    Dungeon.customDungeon.deleteZone(zone);
                                } catch (IOException e) {
                                    SandboxPixelDungeon.reportException(e);
                                }
                                WndSelectZone.updateList();
                            }
                        }
                    });
                }

                @Override
                protected String hoverText() {
                    return Messages.get(WndGameInProgress.class, "erase");
                }
            };
            add(delete);
        }

        @Override
        protected void layout() {
            super.layout();

            if (rename == null) return;

            float renameDeleteWidth = rename.icon().width + 2 + delete.icon().width;

            title.setRect(x, y, width - renameDeleteWidth - 2, title.height());
            desc.setRect(x, title.bottom() + WndTitledMessage.GAP * 2, desc.width(), desc.height());

            height = desc.bottom() + 1;

            rename.setRect(width - renameDeleteWidth, title.top() + (title.height() - rename.icon().height) * 0.5f, rename.icon().width, rename.icon().height);
            delete.setRect(rename.right() + 2, title.top() + (title.height() - delete.icon().height) * 0.5f, delete.icon().width, delete.icon().height);

            layoutCompsInRectangles(comps);
            layoutCompsLinear(transitionEdit, chasmDest, heroBuffs, mobBuffs);
        }

    }

}