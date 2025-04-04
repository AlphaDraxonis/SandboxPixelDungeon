package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs.BuffListContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ChangeRegion;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.WndSelectMusic;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ZoneMobSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndZones;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndEditFloorInOverview;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndColorPicker;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerEnumModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerLikeButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EditZoneComp extends DefaultEditComp<Zone> {

    protected Component[] comps;

    protected StyledButton addTransition;
    protected StyledSpinner chasmDest;
    protected TransitionEditPart transitionEdit;
    protected BuffListContainer heroBuffs, mobBuffs;

    public EditZoneComp(Zone zone) {
        super(zone);

        StyledButton pickColor = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditZoneComp.class, "color"), 9) {
            @Override
            protected void onClick() {
                showColorPickerDialog();
            }
        };
        pickColor.icon(Icons.COLORS.get());
        pickColor.multiline = true;

        StyledCheckBox flamable = new StyledCheckBox(Messages.get(EditZoneComp.class, "flamable"));
        flamable.checked(zone.flamable);
        flamable.addChangeListener(v -> zone.flamable = v);
        flamable.icon(BlobItem.createIcon(PermaGas.PFire.class));

        StyledCheckBox spawnMobs = new StyledCheckBox(Messages.get(EditZoneComp.class, "spawn_mobs"));
        spawnMobs.checked(zone.canSpawnMobs);
        spawnMobs.addChangeListener(v -> zone.canSpawnMobs = v);
        spawnMobs.icon(new GnollSprite());

        StyledCheckBox spawnItems = new StyledCheckBox(Messages.get(EditZoneComp.class, "spawn_items"));
        spawnItems.checked(zone.canSpawnItems);
        spawnItems.addChangeListener(v -> zone.canSpawnItems = v);
        spawnItems.icon(new ItemSprite(ItemSpriteSheet.CHEST));

        StyledCheckBox teleportTo = new StyledCheckBox(Messages.get(EditZoneComp.class, "teleport_to"));
        teleportTo.checked(zone.canTeleportTo);
        teleportTo.addChangeListener(v -> zone.canTeleportTo = v);
        Image teleIcon = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_TELEPORT);
        teleIcon.scale.set(ItemSpriteSheet.SIZE / Math.max(teleIcon.width(), teleIcon.height()));
        teleportTo.icon(teleIcon);

        StyledCheckBox destroyWalls = new StyledCheckBox(Messages.get(EditZoneComp.class, "destroy_walls"));
        destroyWalls.checked(zone.canDestroyWalls);
        destroyWalls.addChangeListener(v -> zone.canDestroyWalls = v);
        destroyWalls.icon(new TileSprite(Terrain.WALL));

        StyledCheckBox blocksVision = new StyledCheckBox(Messages.get(EditZoneComp.class, "blocks_vision"));
        blocksVision.checked(zone.blocksVision);
        blocksVision.addChangeListener(v -> zone.blocksVision = v);
        blocksVision.icon(new BuffIcon(BuffIndicator.BLINDNESS, true));
        
        SpinnerLikeButton grassVisuals = new SpinnerLikeButton(new SpinnerEnumModel<Zone.GrassType>(Zone.GrassType.class, zone.grassType, v -> {zone.grassType = v; updateObj();}) {
            @Override
            protected Image displayIcon(Object value) {
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
            protected String displayString(Object value) {
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
        }, Messages.get(EditZoneComp.class, "grass_label"), 9);

        StyledButton mobRotation = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditZoneComp.class, "custom_mob_cycle"), 9) {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
            }
            @Override
            protected void onClick() {
                SimpleWindow w = new SimpleWindow(Window.WindowSize.WIDTH_LARGE.get(), Window.WindowSize.HEIGHT_LARGE.get());
                ZoneMobSettings ms = new ZoneMobSettings(zone);
                w.initComponents(ms.createTitle(), ms, ms.getOutsideSp(), 0f, 0.5f);
                EditorScene.show(w);
            }
        };
        mobRotation.multiline = true;

        Image icon = EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.SCROLL_LULLABY);
        icon.scale.set(1.8f);
        String musicLabel = Messages.get(ChangeRegion.class, "music");
        StyledButton music = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, musicLabel) {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
                text.setHighlighting(false);
            }
            @Override
            protected void onClick() {
                EditorScene.show(new WndSelectMusic(WndSelectMusic.TypeOfFirstCategory.FOR_ZONES) {
                    @Override
                    protected void onSelect(Object music) {
                        super.onSelect(music);

                        if (music instanceof Integer) zone.music = null;
                        if (music instanceof String) zone.music = (String) music;

                        text(musicLabel + "\n" + (zone.music == null ? Messages.get(EditZoneComp.class, "no_change") : WndSelectMusic.getDisplayName(zone.music)));
                    }
                });
            }
        };
        music.text(musicLabel + "\n" + (zone.music == null ? Messages.get(EditZoneComp.class, "no_change") : WndSelectMusic.getDisplayName(zone.music)));
        music.icon(icon);
        add(music);

        LevelScheme chasm = Dungeon.customDungeon.getFloor(Dungeon.level.levelScheme.getChasm());
        Object[] data;
        int index = 0;
        if (chasm != null) {
            List<String> zones = new ArrayList<>(chasm.zones);
            if (!zones.isEmpty()) Collections.sort(zones, (a, b) -> a.compareTo(b));
            zones.add(0, null);
            data = zones.toArray(EditorUtilities.EMPTY_STRING_ARRAY);
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
            protected String displayString(Object value) {
                if (value == null) return Messages.get(Zone.class, "none_zone");
                return super.displayString(value);
            }
        }, Messages.get(EditZoneComp.class, "chasm_dest") + ":", 9, new TileSprite(Terrain.CHASM));
        chasmDest.addChangeListener(() -> zone.chasmDestZone = (String) chasmDest.getValue());
        chasmDest.enable(chasm != null);

        addTransition = new StyledButtonWithIconAndText(Chrome.Type.GREY_BUTTON_TR, Messages.get(EditTileComp.class, "add_transition"), 9) {
            {
                text.align(RenderedTextBlock.CENTER_ALIGN);
            }
            @Override
            protected void onClick() {
                addTransition(new LevelTransition(Dungeon.level, TransitionEditPart.NONE, TransitionEditPart.DEFAULT, null));
            }
        };
        addTransition.multiline = true;

        comps = new Component[]{pickColor, flamable, spawnMobs, spawnItems, teleportTo, destroyWalls, blocksVision, grassVisuals, music, mobRotation, addTransition};

        if (zone.zoneTransition != null) {
            addTransition(zone.zoneTransition);
        }

        List<BuffItem> asBuffItems = new ArrayList<>();
        for (Buff buff : zone.heroBuffs.values()) {
            if (buff.icon() != BuffIndicator.NONE) {
                buff.zoneBuff = buff.permanent = true;
                asBuffItems.add(new BuffItem(buff));
             }
        }
        heroBuffs = new BuffListContainer(asBuffItems, EditZoneComp.this, Messages.get(EditZoneComp.class, "hero_buffs")) {
            @Override
            protected Set<Class<? extends Buff>> getBuffsToIgnore() {
                Set<Class<? extends Buff>> buffsToIgnore = super.getBuffsToIgnore();
                buffsToIgnore.add(Amok.class);
                buffsToIgnore.add(Terror.class);
                buffsToIgnore.add(Dread.class);
                buffsToIgnore.add(SoulMark.class);
                return buffsToIgnore;
            }

            @Override
            protected Buff doAddBuff(Buff buff) {
                zone.heroBuffs.put(buff.getClass(), buff);
                updateObj();
                buff.zoneBuff = buff.permanent = true;
                return buff;
            }

            @Override
            protected void doRemoveBuff(Buff buff) {
                zone.heroBuffs.remove(buff.getClass());
                updateObj();
            }
        };
        add(heroBuffs);

        asBuffItems = new ArrayList<>();
        for (Buff buff : zone.mobBuffs.values()) {
            if (buff.icon() != BuffIndicator.NONE) {
                buff.zoneBuff = buff.permanent = true;
                asBuffItems.add(new BuffItem(buff));
            }
        }
        mobBuffs = new BuffListContainer(asBuffItems, EditZoneComp.this, Messages.get(EditZoneComp.class, "mob_buffs")) {
            @Override
            protected Set<Class<? extends Buff>> getBuffsToIgnore() {
                Set<Class<? extends Buff>> buffsToIgnore = super.getBuffsToIgnore();
                buffsToIgnore.add(MagicalSight.class);
                buffsToIgnore.add(Foresight.class);
                buffsToIgnore.add(Light.class);
                buffsToIgnore.add(Blindness.class);
                return buffsToIgnore;
            }

            @Override
            protected Buff doAddBuff(Buff buff) {
                zone.mobBuffs.put(buff.getClass(), buff);
                updateObj();
                buff.zoneBuff = buff.permanent = true;
                return buff;
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

        rename.setVisible(true);
        delete.setVisible(true);
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
        layoutCompsLinear(transitionEdit, chasmDest, heroBuffs, mobBuffs);
    }

    private void addTransition(LevelTransition transition) {
        transitionEdit = EditTileComp.addTransition(-12345, transition, Dungeon.level.levelScheme, t -> obj.zoneTransition = null, this::updateObj);
        add(transitionEdit);
        obj.zoneTransition = transition;
        addTransition.setVisible(false);
        layout();
        updateObj();//for resize
    }

    @Override
    protected String createTitleText() {
        return obj.getName();
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


    @Override
    protected void onRenameClicked() {
        EditorScene.show(new WndTextInput(Messages.get(EditZoneComp.class, "rename_title"),
                "",
                obj.getName(),
                100,
                false,
                Messages.get(WndSelectDungeon.class, "rename_yes"),
                Messages.get(WndSelectDungeon.class, "export_no")) {
            @Override
            public void onSelect(boolean positive, String text) {
                if (positive && !text.isEmpty()) {
                    for (String floorN : Dungeon.level.levelScheme.zones) {
                        if (floorN.equals(text)) {
                            EditorUtilities.showDuplicateNameWarning();
                            return;
                        }
                    }
                    if (!text.equals(obj.getName())) {
                        Dungeon.customDungeon.renameZone(obj, text);
                        WndZones.WndSelectZone.updateList();
                        Window oldW = EditorUtilities.getParentWindow(rename);
                        if (oldW != null) {
                            oldW.hide();
                            EditorScene.show(new EditCompWindow(obj));
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onDeleteClicked() {
        EditorScene.show(new WndOptions(Icons.get(Icons.WARNING),
                Messages.get(EditZoneComp.class, "erase_title"),
                Messages.get(EditZoneComp.class, "erase_body"),
                Messages.get(WndEditFloorInOverview.class, "erase_yes"),
                Messages.get(WndGameInProgress.class, "erase_warn_no")) {
            @Override
            protected void onSelect(int index) {
                if (index == 0) {
                    Window oldW = EditorUtilities.getParentWindow(delete);
                    if (oldW != null) {
                        oldW.hide();//important to hide before deletion
                    }
                    try {
                        Dungeon.customDungeon.deleteZone(obj);
                    } catch (IOException e) {
                        SandboxPixelDungeon.reportException(e);
                    }
                    WndZones.WndSelectZone.updateList();
                }
            }
        });
    }

}
