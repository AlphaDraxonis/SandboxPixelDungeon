package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SideControlPane extends Component {


    public static final int WIDTH = 16;
    private SideControlButton[] buttons;

    public SideControlPane(boolean editor) {

        if (editor) {
            buttons = new SideControlButton[4];
            buttons[0] = new StartBtn();
            buttons[1] = new PipetteBtn();
            buttons[2] = new FillBtn();
            if (!EditorScene.isEditingRoomLayout) buttons[3] = new ToggleZoneViewBtn();
        } else {
            buttons = new SideControlButton[9];
            buttons[0] = new ExitBtn();
            buttons[1] = new DamageBtn();
            buttons[2] = new KeyBtn();
            buttons[3] = new SpeedBtn();
            buttons[4] = new InvisBtn();
            buttons[5] = new MindVisionBtn();
            buttons[6] = new MappingBtn();
            buttons[7] = new TeleportToFloorBtn();
            buttons[8] = new DebugBtn();
        }

        for (Component button : buttons) {
            if (button != null)
                add(button);
        }
    }

    public void reduceHeight() {
        for (SideControlButton btn : buttons) {
            btn.icon.scale.set(btn.icon.scale.x * 0.97f);
            btn.bg.scale.set(btn.bg.scale.x * 0.97f);
            btn.setSize(btn.bg.width(), btn.bg.height());
        }
        layout();
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = y;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null && buttons[i].visible) {
                buttons[i].setPos(x, posY);
                PixelScene.align(buttons[i]);
                posY = buttons[i].bottom();
            }
        }
        width = WIDTH;
        height = posY - y;
    }

    public void setButtonEnabled(Class<? extends SideControlButton> button, boolean enabled) {
        for (SideControlButton b : buttons) {
            if (b.getClass() == button) b.enable(enabled);
        }
    }

    public static HeroClass lastSelectedClass;


    private static abstract class SideControlButton extends Button {
        private Image bg, icon;
        protected boolean btnEnabled;

        public SideControlButton(int num) {
            this(num, false);
        }

        public SideControlButton(int num, boolean large) {

            if (large) {
                icon = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS_LARGE, 0, 26 * num, 24, 26);
                icon.scale = new PointF(0.5f, 0.5f);
            } else {
                icon = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS, 0, 13 * num, 12, 13);
            }
            add(icon);

            width = bg.width;
            height = bg.height;
        }

        @Override
        protected void createChildren() {
            super.createChildren();

            bg = new Image(Assets.Interfaces.SIDE_CONTROL_PANE);
            add(bg);
        }

        @Override
        protected void layout() {
            super.layout();
            bg.x = x;
            bg.y = y;
            icon.x = x + 2;
            icon.y = y + 2;
        }

        @Override
        protected void onPointerDown() {
            icon.brightness(1.1f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK, 0.6f);
        }

        @Override
        protected void onPointerUp() {
            float alpha = icon.alpha();
            icon.resetColor();
            icon.alpha(alpha);
        }

        @Override
        protected void onClick() {
            enable(!isBtnEnabled());
        }

        protected void enable(boolean value) {
            icon.alpha((btnEnabled = value) ? 1f : 0.3f);
        }

        public boolean isBtnEnabled() {
            return btnEnabled;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(this, "label"));
        }
    }

    public static final class StartBtn extends SideControlButton {

        private StartBtn() {
            super(0);
            enable(true);
        }

        @Override
        protected void onClick() {
            try {
                if (Dungeon.hero == null) {
                    CustomDungeonSaves.saveLevel(EditorScene.getCustomLevel());
                    CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                } else
                    Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(Dungeon.customDungeon.getName());//restart

                EditorScene.setCameraZoomWhenOpen = Camera.main.zoom;

                GamesInProgress.selectedClass = lastSelectedClass;
                if (GamesInProgress.selectedClass == null)
                    GamesInProgress.selectedClass = HeroClass.WARRIOR;
                GamesInProgress.curSlot = GamesInProgress.TEST_SLOT;
                EditorScene.isEditing = false;
                SandboxPixelDungeon.switchScene(HeroSelectScene.class);
            } catch (IOException | CustomDungeonSaves.RenameRequiredException e) {
                SandboxPixelDungeon.reportException(e);
            }
        }
    }

    public static final class PipetteBtn extends SideControlButton {

        private PipetteBtn() {
            super(1);
            enable(true);
        }

        @Override
        protected void onClick() {
            EditorScene.selectCell(pickObjCellListener);
        }
    }

    public static final class FillBtn extends SideControlButton {

        private FillBtn() {
            super(1, true);
            enable(true);
        }

        @Override
        protected void onClick() {
            if (EditorScene.dragClickEnabled())
                EditorScene.selectCell(fillAllCellListener);
        }
    }

    public static final class ToggleZoneViewBtn extends SideControlButton {

        private ToggleZoneViewBtn() {
            super(2, false);
            enable(EditorScene.isDisplayZones());
        }

        @Override
        protected void onClick() {
            boolean value = !EditorScene.isDisplayZones();
            EditorScene.setDisplayZoneState(value);
            enable(value);
        }

        @Override
        protected String hoverText() {
            return Messages.get(this, "label_" + (btnEnabled ? "on" : "off"));
        }
    }

    public static final class ExitBtn extends SideControlButton {

        private ExitBtn() {
            super(3);
            enable(true);
        }

        @Override
        protected void onClick() {
//          GameScene.scene.destroy(); ???

            CustomObjectManager.loadUserContentFromFiles();

            EditorScene.start();
            EditorScene.openDifferentLevel = false;
            WndSelectDungeon.openDungeon(Dungeon.customDungeon.getName());
        }
    }

    public static final class DamageBtn extends SideControlButton {

        private static boolean shouldBeEnabled;

        private DamageBtn() {
            super(4);
            enable(shouldBeEnabled);
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.damageImmune = value;
        }
    }

    public static final class KeyBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        private KeyBtn() {
            super(5);
            enable(shouldBeEnabled);
        }


        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.permaKey = value;
        }
    }

    public static final class SpeedBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        private SpeedBtn() {
            super(6);
            enable(shouldBeEnabled);
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.extraSpeed = value;
        }
    }

    public static final class InvisBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        private InvisBtn() {
            super(7);
            enable(shouldBeEnabled);
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            if (Dungeon.customDungeon.permaInvis = value)
                Buff.affect(Dungeon.hero, Invisibility.class, 1);
            else {
                Buff b = Dungeon.hero.buff(Invisibility.class);
                if (b != null) b.detach();
                Dungeon.hero.invisible = 0;
            }
        }
    }

    public static final class SecretsBtn extends SideControlButton {

        private static boolean shouldBeEnabled;

        private SecretsBtn() {
            super(8);
            enable(shouldBeEnabled);
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.seeSecrets = value;
            GameScene.updateMap();
        }
    }

    public static final class MindVisionBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        private MindVisionBtn() {
            super(9);
            enable(shouldBeEnabled);
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            if (Dungeon.customDungeon.permaMindVision = value) {
                Buff.affect(Dungeon.hero, MindVision.class, 1);
                Dungeon.observe();
            } else {
                Buff b = Dungeon.hero.buff(MindVision.class);
                if (b != null) b.detach();
            }
        }
    }

    public static final class MappingBtn extends SideControlButton {

        private MappingBtn() {
            super(10);
            enable(true);
        }

        @Override
        protected void onClick() {
            new ScrollOfMagicMapping() {
                {
                    final Hero oldUser = curUser;
                    curUser = Dungeon.hero;
                    anonymize();
                    if (Dungeon.level.levelScheme.magicMappingDisabled) {
                        Dungeon.level.levelScheme.magicMappingDisabled = false;
                        doRead();
                        Dungeon.level.levelScheme.magicMappingDisabled = true;
                    }
                    else doRead();
                    curUser.spend(-TIME_TO_READ);
                    curUser = oldUser;
                }
            };
        }

        @Override
        protected String hoverText() {
            return super.hoverText()
                    + " " + Messages.titleCase(Messages.get(ScrollOfMagicMapping.class, "name"));
        }
    }

    public static final class TeleportToFloorBtn extends SideControlButton {

        private TeleportToFloorBtn() {
            super(11);
            enable(true);
        }

        @Override
        protected void onClick() {
            EditorScene.show(new WndSelectFloor() {
                @Override
                public boolean onSelect(LevelSchemeLike levelScheme) {
                    if (levelScheme instanceof LevelScheme) {
                        Level.beforeTransition();
                        InterlevelScene.curTransition = new LevelTransition(Dungeon.level, Dungeon.hero.pos, -1, ((LevelScheme) levelScheme).getName());
                        InterlevelScene.mode = ((LevelScheme) levelScheme).getDepth() >= Dungeon.depth
                                ? InterlevelScene.Mode.DESCEND
                                : InterlevelScene.Mode.ASCEND;

                        Game.switchScene(InterlevelScene.class);
                    }
                    return true;
                }

                @Override
                protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                    List<LevelSchemeLike> levelsList = new ArrayList<>();
                    for (LevelSchemeLike ls : super.filterLevels(levels)) {
                        levelsList.add(ls);
                    }
                    return levelsList;
                }
            });
        }
    }

    public static final class DebugBtn extends SideControlButton {

        private DebugBtn() {
            super(12);
            enable(true);
        }

        @Override
        protected void onClick() {
            GameScene.show(new WndScrollOfDebug());
        }
    }

    private static final CellSelector.Listener pickObjCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            EditorScene.putInQuickslot(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
                EditorScene.showEditCellWindow(cell);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SideControlPane.class, "pick_obj_prompt");
        }
    };
    private static final CellSelector.Listener fillAllCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            EditorScene.fillAllWithOneTerrain(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < Dungeon.level.length()) {
                EditorScene.showEditCellWindow(cell);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SideControlPane.class, "fill_all_prompt");
        }
    };

}