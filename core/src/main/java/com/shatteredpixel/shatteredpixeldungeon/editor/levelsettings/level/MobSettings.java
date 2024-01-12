package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab.BUTTON_HEIGHT;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab.GAP;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectLevelType;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.MobActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class MobSettings extends Component implements LevelTab.BackPressImplemented {


    private final Component outsideSp;
    private Component outsideSpExtraBtn;

    private Component title;
    private RenderedTextBlock titleText;
    private IconButton buttonInTitle;

    private MobSpawningComp mobSpawning;
    private ChangeMobRotation mobRotation;
    private MobOverview mobOverview;

    private int currentSelected;
    private boolean mobCycleOpen;

    public MobSettings() {

        title = new Component() {
            @Override
            public synchronized void clear() {
                for (Gizmo c : members) c.destroy();
                super.clear();
            }

            @Override
            public float width() {
                return titleText.width() + (buttonInTitle == null ? 0 : GAP + buttonInTitle.width());
            }

            @Override
            public float height() {
                return titleText.height();
            }

            @Override
            public float bottom() {
                return super.bottom() - LevelTab.GAP * 3;
            }

            @Override
            protected void layout() {
                titleText.maxWidth((int) (width - GAP - (buttonInTitle == null ? 0 : buttonInTitle.height())));
                titleText.setPos(x, y);
                if (buttonInTitle != null)
                    buttonInTitle.setPos(title.right() - GAP - buttonInTitle.width(), (height - buttonInTitle.height()) * 0.5f);
            }
        };

        outsideSp = new MultiWindowTabComp.OutsideSpSwitchTabs() {
            @Override
            protected void createChildren(Object... params) {
                tabs = new TabControlButton[2];
                for (int j = 0; j < tabs.length; j++) {
                    tabs[j] = new TabControlButton(j);
                    add(tabs[j]);
                }
                Image image0 = new ItemSprite(ItemSpriteSheet.ANKH, new ItemSprite.Glowing( 0xFFFFCC ));
                image0.scale.set(0.8f);
                tabs[0].icon(image0);
                tabs[1].icon(new SkeletonSprite());

                super.createChildren(params);

                select(0);
            }

            @Override
            protected void layout() {
                float posY = y;
                if (outsideSpExtraBtn != null && outsideSpExtraBtn.isVisible()) {
                    outsideSpExtraBtn.setRect(x, posY, width, BUTTON_HEIGHT);
                    PixelScene.align(outsideSpExtraBtn);
                    posY += outsideSpExtraBtn.height() + GAP;
                }
                float buttonWidth = width() / tabs.length;
                for (int i = 0; i < tabs.length; i++) {
                    tabs[i].setRect(x + i * buttonWidth, posY, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(tabs[i]);
                }
                height = posY - y + ITEM_HEIGHT;
            }

            @Override
            public void select(int index) {
                if (mobCycleOpen && index == 0) {
                    MobSettings.this.select(2);
                    super.select(0);
                    currentSelected = 2;
                } else {
                    MobSettings.this.select(index);
                    super.select(index);
                }
            }

            @Override
            public String getTabName(int index) {
                switch (index) {
                    case 0:
                        return Messages.get(MobSettings.class, "title");
                    case 1:
                        return Messages.get(MobSettings.class, "overview");
                    case 2:
                        return Messages.get(MobSettings.class, "mob_rot");
                }
                return "null";
            }
        };
    }

    public Component createTitle() {
        return title;
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    protected void select(int identifier) {
        currentSelected = identifier;
        if (mobCycleOpen) {
            if (identifier == 0) mobCycleOpen = false;
        } else if (identifier == 2) mobCycleOpen = true;
        title.clear();
        if (titleText != null) titleText.destroy();
        if (buttonInTitle != null) {
            buttonInTitle.remove();
            buttonInTitle.destroy();
            buttonInTitle = null;
        }
        switch (identifier) {
            case 0:
                titleText = PixelScene.renderTextBlock(Messages.get(MobSettings.class, "title"), 11);
                titleText.hardlight(Window.TITLE_COLOR);
                title.add(titleText);
                if (mobSpawning == null) {
                    mobSpawning = new MobSpawningComp();
                    add(mobSpawning);
                }
                select(mobSpawning);
                WndEditorSettings.getInstance().getLevelTab().setAlignmentOther(0.5f);
                break;
            case 1:
                titleText = PixelScene.renderTextBlock(Messages.get(MobSettings.class, "overview"), 11);
                titleText.hardlight(Window.TITLE_COLOR);
                title.add(titleText);
                if (mobOverview == null) {
                    mobOverview = new MobOverview();
                    add(mobOverview);
                }
                select(mobOverview);

                WndEditorSettings.getInstance().getLevelTab().setAlignmentOther(0f);
                break;
            case 2:
                titleText = PixelScene.renderTextBlock(Messages.get(MobSettings.class, "mob_rot"), 11);
                titleText.hardlight(Window.TITLE_COLOR);
                title.add(titleText);
                if (mobRotation == null) {
                    mobRotation = new ChangeMobRotation();
                    add(mobRotation);
                }
                select(mobRotation);

                buttonInTitle = new IconButton(Icons.get(Icons.INFO)) {
                    {
                        height = icon.height();
                        width = icon.width();
                    }

                    @Override
                    protected void onClick() {
                        showMobRotationInfo();
                    }
                };
                title.add(buttonInTitle);

                WndEditorSettings.getInstance().getLevelTab().setAlignmentOther(0f);
                break;
        }
        LevelTab.updateLayout();
    }

    private void select(Component comp) {
        if (mobSpawning != null) mobSpawning.visible = mobSpawning.active = comp == mobSpawning;
        if (mobRotation != null) {
            mobRotation.visible = mobRotation.active = comp == mobRotation;
            if (comp != mobRotation) WndChooseMob.resetLastView();
            if (outsideSpExtraBtn != null) outsideSpExtraBtn.destroy();
            outsideSpExtraBtn = mobRotation.getOutsideSp();
            outsideSp.add(outsideSpExtraBtn);
        }
        if (mobOverview != null) mobOverview.visible = mobOverview.active = comp == mobOverview;
        if (outsideSpExtraBtn != null) outsideSpExtraBtn.visible = outsideSpExtraBtn.active = comp == mobRotation;
    }

    @Override
    protected void layout() {
        if (mobSpawning != null && mobSpawning.isVisible()) {
            mobSpawning.setRect(x, y, width, -1);
            height = mobSpawning.height();
        } else if (mobRotation != null && mobRotation.isVisible()) {
            mobRotation.setRect(x, y, width, -1);
            height = mobRotation.height();
        } else if (mobOverview != null && mobOverview.isVisible()) {
            mobOverview.setRect(x, y, width, -1);
            height = mobOverview.height();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (currentSelected == 2) {
            select(0);
            return true;
        }
        return false;
    }

    private void showMobRotationInfo() {
        EditorScene.show(new WndTitledMessage(Icons.get(Icons.INFO),
                Messages.titleCase(Messages.get(MobSettings.class, "mob_rot")),
                Messages.get(MobSettings.class, "mob_rot_info")));
    }

    private class MobSpawningComp extends Component {

        private final Spinner moblimit, respawnTime;
        private final RedButton openMobCycle;
        private final CheckBox disableSpawning;

        private ItemSelector boss;

        public MobSpawningComp() {
            CustomLevel f = EditorScene.customLevel();

            moblimit = new Spinner(new SpinnerIntegerModel(0, 100, f.mobLimit(), 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return height * 1.1f;
                }
            }, " " + Messages.get(MobSettings.class, "limit") + ":", 9);
            moblimit.addChangeListener(() -> f.setMobLimit((int) moblimit.getValue()));
            add(moblimit);
            respawnTime = new Spinner(new SpinnerIntegerModel(1, 100, (int) f.respawnCooldown(), 1, false, null) {
                @Override
                public float getInputFieldWidth(float height) {
                    return height * 1.1f;
                }

                @Override
                public void displayInputAnyNumberDialog() {
                    displayInputAnyNumberDialog(1, Integer.MAX_VALUE);
                }
            }, " " + Messages.get(MobSettings.class, "respawn_time") + ":", 9);
            ((SpinnerIntegerModel) respawnTime.getModel()).setAbsoluteMinimum(1f);
            respawnTime.addChangeListener(() -> f.setRespawnCooldown((int) respawnTime.getValue()));
            add(respawnTime);

            openMobCycle = new RedButton(Messages.get(MobSettings.class, "edit_cycle")) {
                @Override
                protected void onClick() {
                    select(2);
                }
            };
            add(openMobCycle);

            disableSpawning = new CheckBox(Messages.get(MobSettings.class, "enable_respawn")) {
                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    openMobCycle.enable(value);
                    moblimit.enable(value);
                    respawnTime.enable(value);
                    f.enableRespawning(value);
                }
            };
            disableSpawning.checked(f.isRespawEnabled());
            add(disableSpawning);

            Mob bossMob = EditorScene.customLevel().findMob(EditorScene.customLevel().bossmobAt);
            MobActionPart.Modify modify = bossMob == null ? null : new MobActionPart.Modify(bossMob);
            boss = new ItemSelector(getUpdatedLabelForBossChooser(bossMob), MobItem.class, bossMob == null ? null : new MobItem(bossMob), ItemSelector.NullTypeSelector.NOTHING) {
                @Override
                public void change() {
                    selectBoss(mob -> {
                        renderedTextBlock.text(getUpdatedLabelForBossChooser(mob));
                        boss.setSelectedItem(mob == null ? null : new MobItem(mob));
                    });
                }

                @Override
                public synchronized void destroy() {
                    super.destroy();
                    if (modify != null) {
                        modify.finish();
                        Undo.addActionPart(modify);
                    }
                }

            };
            add(boss);
        }

        @Override
        protected void layout() {
            float posY = y;

            disableSpawning.setRect(x, posY, width, BUTTON_HEIGHT);
            PixelScene.align(disableSpawning);
            posY = disableSpawning.bottom() + LevelTab.GAP;

            respawnTime.setRect(x, posY, width, BUTTON_HEIGHT);
            PixelScene.align(respawnTime);
            posY = respawnTime.bottom() + LevelTab.GAP;

            moblimit.setRect(x, posY, width, BUTTON_HEIGHT);
            PixelScene.align(moblimit);
            posY = moblimit.bottom() + LevelTab.GAP;

            openMobCycle.setRect(x, posY, width, BUTTON_HEIGHT);
            PixelScene.align(openMobCycle);
            posY = openMobCycle.bottom() + LevelTab.GAP * 3;

            boss.setRect(x, posY, width, BUTTON_HEIGHT);
            PixelScene.align(boss);
            posY = boss.bottom() + LevelTab.GAP;

            height = (int) (posY - LevelTab.GAP);
        }

        private void selectBoss(Consumer<Mob> callBack) {
            Window w = EditorUtilies.getParentWindow(this);
            w.active = false;
            if (w instanceof WndTabbed) ((WndTabbed) w).setBlockLevelForTabs(PointerArea.NEVER_BLOCK);
            Game.scene().remove(w);

            EditorScene.selectCell(new CellSelector.Listener() {
                @Override
                public void onSelect(Integer cell) {
                    if (cell != null) {
                        CustomLevel l = EditorScene.customLevel();
                        if (cell >= 0 && cell < l.length()) {
                            final int oldBoss = l.bossmobAt;
                            if (l.findMob(cell) == null) cell = Level.NO_BOSS_MOB;
                            final int c = cell;
                            ActionPart part = new ActionPart() {
                                @Override
                                public void undo() {
                                    l.bossmobAt = oldBoss;
                                }

                                @Override
                                public void redo() {
                                    l.bossmobAt = c;
                                }

                                @Override
                                public boolean hasContent() {
                                    return oldBoss != c;
                                }
                            };
                            Undo.addActionPart(part);
                            part.redo();

                            callBack.accept(l.findMob(cell));
                        } else callBack.accept(null);

                        w.active = true;
                        if (w instanceof WndTabbed) ((WndTabbed) w).setBlockLevelForTabs(PointerArea.ALWAYS_BLOCK);
                        EditorScene.show(w);
                    }
                }

                @Override
                public String prompt() {
                    return Messages.get(MobSettings.class, "select_boss_prompt");
                }
            });
        }
    }

    private static String getUpdatedLabelForBossChooser(Mob boss) {
        return "_"+Messages.get(WndSelectLevelType.class, "type_boss") + ":_"
                + (boss == null ? "" : (PixelScene.landscape() ? " " + Messages.titleCase(boss.name()) : "") + EditorUtilies.appendCellToString(boss.pos));
    }


    // ≙ ≈ =
    static class ChangeMobRotation extends ItemsWithChanceDistrComp {

        private static final Set<Class<? extends Mob>> NOT_AVAILABLE = new HashSet<>();

        {
            for (Class<?> m : Mobs.NPC.classes()) {
                NOT_AVAILABLE.add((Class<? extends Mob>) m);
            }
        }

        public ChangeMobRotation() {
            super(EditorScene.customLevel().getMobRotationVar(), 1);
            hasNullInLoot = true;//so no "no mob" can be added
        }

        @Override
        protected void showAddItemWnd() {
            EditorScene.selectItem(createSelector(MobItem.class, false, Mobs.bag.getClass()));
        }

        @Override
        protected boolean acceptItem(Item item) {
            return super.acceptItem(item) && item instanceof MobItem && !NOT_AVAILABLE.contains(((MobItem) item).getObject().getClass());
        }
    }

    private static class MobOverview extends Component {

        @Override
        public synchronized void clear() {
            for (Gizmo g : members) g.destroy();
            super.clear();
        }

        @Override
        public void layout() {
            super.layout();
            updateList();
        }

        protected void updateList() {

            clear();

            List<Mob> mobsOnFloor = new ArrayList<>(EditorScene.customLevel().mobs);

//        for (Mob m : testMobs) {
//            m.pos = Random.Int(EditorScene.floor().length());
////                Buff.affectAnyBuffAndSetDuration(m,Burning.class,10);
//        }

            Collections.sort(mobsOnFloor, (m1, m2) -> m1.pos - m2.pos);

            float posY = y;
            for (Mob m : mobsOnFloor) {
                MobCatalogItem i = new MobCatalogItem(m);
                i.setRect(x, posY, width, 18);
                posY += 18;
                add(i);
            }

            height = posY - y;

        }

    }

    private static class MobCatalogItem extends AdvancedListPaneItem {
        private final Mob mob;

        public MobCatalogItem(Mob mob) {
            super(createSprite(mob), null, EditorUtilies.formatTitle(mob.name(), mob.pos));
            this.mob = mob;
        }

        @Override
        public void onClick() {
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            MobActionPart.Modify mobActionPart = new MobActionPart.Modify(mob);
            EditorScene.show(new EditCompWindow(mob, this) {
                @Override
                public void hide() {
                    super.hide();
                    mobActionPart.finish();
                    Undo.addActionPart(mobActionPart);
                }
            });
        }

        @Override
        public void onUpdate() {
            if (mob == null) return;

            if (icon != null) remove(icon);
            icon = mob.sprite();
            addToBack(icon);
            remove(bg);
            addToBack(bg);

            super.onUpdate();
        }

        private static Image createSprite(Mob mob) {
            CharSprite sprite = mob.sprite();
            sprite.jumpToFrame((int) (Math.random() * sprite.idle.frames.length));//Shouldn't all be synchrony
            return sprite;
        }
    }
}