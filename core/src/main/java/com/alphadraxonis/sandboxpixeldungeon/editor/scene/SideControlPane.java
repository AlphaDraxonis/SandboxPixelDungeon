package com.alphadraxonis.sandboxpixeldungeon.editor.scene;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.GamesInProgress;
import com.alphadraxonis.sandboxpixeldungeon.SandboxPixelDungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Invisibility;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.MindVision;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroClass;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.CustomDungeonSaves;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.CellSelector;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.HeroSelectScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.PointF;

import java.io.IOException;

public class SideControlPane extends Component {


    public static final int WIDTH = 16;
    private Component[] buttons;

    public SideControlPane(boolean editor) {

        if (editor) {
            buttons = new Component[3];
            buttons[0] = new StartBtn();
            buttons[1] = new PipetteBtn();
            buttons[2] = new FillBtn();
        } else {
            buttons = new Component[8];
            buttons[0] = new ExitBtn();
            buttons[1] = new DamageBtn();
            buttons[2] = new SecretsBtn();
            buttons[3] = new MindVisionBtn();
            buttons[4] = new MappingBtn();
            buttons[5] = new KeyBtn();
            buttons[6] = new SpeedBtn();
            buttons[7] = new InvisBtn();
        }

        for (Component button : buttons) {
            add(button);
        }
    }

    @Override
    protected void layout() {
        super.layout();

        float posY = y;
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPos(x, posY);
            PixelScene.align(buttons[i]);
            posY = buttons[i].bottom();
        }
        width = WIDTH;
        height = posY - y;
    }

    public static HeroClass lastSelectedClass;


    private static abstract class SideControlButton extends Button {
        private Image bg, icon;
        protected boolean btnEnabled;

        public SideControlButton(int num) {
            this(num, false);
        }

        public SideControlButton(int num, boolean large) {

            if(large){
                icon = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS_LARGE, 0, 26 * num, 24,26);
                icon.scale = new PointF(0.5f, 0.5f);
            }else {
                icon = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS, 0, 13 * num, 12,13);
            }
            add(icon);

            width = bg.width;
            height = bg.height;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

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
    }

    private static class StartBtn extends SideControlButton {

        public StartBtn() {
            super(0);
            enable(true);
        }

        @Override
        protected void onClick() {
            try {
                if (Dungeon.hero == null) {
                    CustomDungeonSaves.saveLevel(EditorScene.customLevel());
                    CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                } else
                    Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(Dungeon.customDungeon.getName());//restart

                EditorScene.setCameraZoomWhenOpen = Camera.main.zoom;

                GamesInProgress.selectedClass = lastSelectedClass;
                if (GamesInProgress.selectedClass == null)
                    GamesInProgress.selectedClass = HeroClass.WARRIOR;
                GamesInProgress.curSlot = GamesInProgress.TEST_SLOT;
                SandboxPixelDungeon.switchScene(HeroSelectScene.class);
            } catch (IOException | CustomDungeonSaves.RenameRequiredException e) {
                SandboxPixelDungeon.reportException(e);
            }
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "play"));
        }
    }

    private static class PipetteBtn extends SideControlButton {

        public PipetteBtn() {
            super(1);
            enable(true);
        }

        @Override
        protected void onClick() {
            EditorScene.selectCell(pickObjCellListener);
        }

        @Override
        protected String hoverText() {
            return null;
        }
    }

    private static class FillBtn extends SideControlButton {

        public FillBtn() {
            super(1, true);
            enable(true);
        }

        @Override
        protected void onClick() {
            EditorScene.selectCell(fillAllCellListener);
        }

        @Override
        protected String hoverText() {
            return null;
        }
    }

    private static class ExitBtn extends SideControlButton {

        public ExitBtn() {
            super(2);
            enable(true);
        }

        @Override
        protected void onClick() {
//          GameScene.scene.destroy(); ???
            EditorScene.start();
            EditorScene.openDifferentLevel = false;
            WndSelectDungeon.openDungeon(Dungeon.customDungeon.getName());
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "exit"));
        }
    }

    private static class DamageBtn extends SideControlButton {

        private static boolean shouldBeEnabled;

        public DamageBtn() {
            super(3);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "damage"));
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.damageImmune = value;
        }
    }

    private static class SecretsBtn extends SideControlButton {

        private static boolean shouldBeEnabled;

        public SecretsBtn() {
            super(4);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "secrets"));
        }

        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.seeSecrets = value;
            GameScene.updateMap();
        }
    }

    private static class MindVisionBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        public MindVisionBtn() {
            super(5);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "mind_vision"));
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

    private static class MappingBtn extends SideControlButton {

        public MappingBtn() {
            super(6);
            enable(true);
        }

        @Override
        protected void onClick() {
            new ScrollOfMagicMapping() {
                {
                    final Hero oldUser = curUser;
                    curUser = Dungeon.hero;
                    anonymize();
                    doRead();
                    curUser = oldUser;
                }
            };
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "magic_mapping")
                    + " " + Messages.get(ScrollOfMagicMapping.class, "name"));
        }
    }

    private static class KeyBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        public KeyBtn() {
            super(7);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "play"));
        }


        @Override
        protected void enable(boolean value) {
            super.enable(value);
            shouldBeEnabled = value;
            Dungeon.customDungeon.permaKey = value;
        }
    }

    private static class SpeedBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        public SpeedBtn() {
            super(8);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "speed"));
        }

        @Override
        protected void enable(boolean value) {
            if (value != isBtnEnabled()) {
                if (value) Dungeon.hero.baseSpeed *= 10;
                else Dungeon.hero.baseSpeed /= 10;
            }
            super.enable(value);
            shouldBeEnabled = value;
        }
    }

    private static class InvisBtn extends SideControlButton {
        private static boolean shouldBeEnabled;

        public InvisBtn() {
            super(9);
            enable(shouldBeEnabled);
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(SideControlPane.class, "invis"));
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
            }
        }
    }

    private static final CellSelector.Listener pickObjCellListener = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer cell) {
            EditorScene.putInQuickslot(cell);
        }

        @Override
        public void onRightClick(Integer cell) {
            if (cell != null && cell >= 0 && cell < EditorScene.customLevel().length()) {
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
            if (cell != null && cell >= 0 && cell < EditorScene.customLevel().length()) {
                EditorScene.showEditCellWindow(cell);
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SideControlPane.class, "fill_all_prompt");
        }
    };

}