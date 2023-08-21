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
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.HeroSelectScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.io.IOException;

public class SideControlPane extends Component {


    public static final int WIDTH = 16;

    private SideControlButton[] buttons;

    public SideControlPane(int indexFirstButton, int numButtons) {

        buttons = new SideControlButton[numButtons];

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new SideControlButton(indexFirstButton + i);
            buttons[i].updateState();
            add(buttons[i]);
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

    protected void onClick(int index, SideControlButton button) {
        switch (index) {
            case 0:
                try {
                    if (Dungeon.hero == null) {
                        CustomDungeonSaves.saveLevel(EditorScene.customLevel());
                        CustomDungeonSaves.saveDungeon(Dungeon.customDungeon);
                    } else
                        Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(Dungeon.customDungeon.getName());//restart

                    GamesInProgress.selectedClass = lastSelectedClass;
                    if (GamesInProgress.selectedClass == null)
                        GamesInProgress.selectedClass = HeroClass.WARRIOR;
                    GamesInProgress.curSlot = GamesInProgress.TEST_SLOT;
                    SandboxPixelDungeon.switchScene(HeroSelectScene.class);
                } catch (IOException e) {
                    SandboxPixelDungeon.reportException(e);
                }
                return;
            case 1:
                EditorScene.start();
                EditorScene.openDifferentLevel = false;
                WndSelectDungeon.openDungeon(Dungeon.customDungeon.getName());
                return;
            case 2:
                button.enable(Dungeon.customDungeon.damageImmune = !button.isEnabled());
                return;
            case 3:
                button.enable(Dungeon.customDungeon.seeSecrets = !button.isEnabled());
                GameScene.updateMap();
                return;
            case 4:
                button.enable(Dungeon.customDungeon.permaMindVision = !button.isEnabled());
                if (Dungeon.customDungeon.permaMindVision) {
                    Buff.affect(Dungeon.hero, MindVision.class, 1);
                    Dungeon.observe();
                } else {
                    Buff b = Dungeon.hero.buff(MindVision.class);
                    if (b != null) b.detach();
                }
                return;
            case 5:
                new ScrollOfMagicMapping() {
                    {
                        final Hero oldUser = curUser;
                        curUser = Dungeon.hero;
                        anonymize();
                        doRead();
                        curUser = oldUser;
                    }
                };
                return;
            case 6:
                button.enable(Dungeon.customDungeon.permaKey = !button.isEnabled());
                return;
            case 7:
                boolean active = !button.isEnabled();
                if (active) Dungeon.hero.baseSpeed *= 10f;
                else Dungeon.hero.baseSpeed /= 10f;
                button.enable(active);
                return;
            case 8:
                button.enable(Dungeon.customDungeon.permaInvis = !button.isEnabled());
                if (Dungeon.customDungeon.permaInvis)
                    Buff.affect(Dungeon.hero, Invisibility.class, 1);
                else {
                    Buff b = Dungeon.hero.buff(Invisibility.class);
                    if (b != null) b.detach();
                }
                return;
        }

    }

    protected String createHoverText(int index) {
        switch (index) {
            case 0:
                return Messages.titleCase(Messages.get(SideControlPane.class, "play"));
            case 1:
                return Messages.titleCase(Messages.get(SideControlPane.class, "exit"));
            case 2:
                return Messages.titleCase(Messages.get(SideControlPane.class, "damage"));
            case 3:
                return Messages.titleCase(Messages.get(SideControlPane.class, "secrets"));
            case 4:
                return Messages.titleCase(Messages.get(SideControlPane.class, "mind_vision"));
            case 5:
                return Messages.titleCase(Messages.get(SideControlPane.class, "magic_mapping")
                        + " " + Messages.get(ScrollOfMagicMapping.class, "name"));
            case 6:
                return Messages.titleCase(Messages.get(SideControlPane.class, "key"));
            case 7:
                return Messages.titleCase(Messages.get(SideControlPane.class, "speed"));
            case 8:
                return Messages.titleCase(Messages.get(SideControlPane.class, "invis"));
        }
        return null;
    }


    private class SideControlButton extends Button {
        private Image bg, enabled, disabled;
        private final int num;

        public SideControlButton(int num) {
            this.num = num;

            enabled = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS, 0, 13 * num, 12, 13);
            add(enabled);

            disabled = new Image(Assets.Interfaces.SIDE_CONTROL_BUTTONS, 12, 13 * num, 12, 13);
            add(disabled);

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
            enabled.x = disabled.x = x + 2;
            enabled.y = disabled.y = y + 2;
        }

        protected void updateState() {
            enable(false);
        }

        @Override
        protected void onPointerDown() {
            enabled.brightness(1.1f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK, 0.6f);
        }

        @Override
        protected void onPointerUp() {
            enabled.resetColor();
            disabled.resetColor();
        }

        protected void enable(boolean value) {
            if (num == 0 || num == 1 || num == 5) value = true;
            enabled.visible = value;
            disabled.visible = !value;
        }

        protected boolean isEnabled() {
            return enabled.visible;
        }

        @Override
        protected String hoverText() {
            String text = createHoverText(num);
            if (text == null) return null;
            return Messages.titleCase(text);
        }

        @Override
        protected void onClick() {
            SideControlPane.this.onClick(num, this);
        }
    }
}