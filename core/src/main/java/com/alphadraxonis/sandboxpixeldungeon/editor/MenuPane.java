package com.alphadraxonis.sandboxpixeldungeon.editor;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.SPDAction;
import com.alphadraxonis.sandboxpixeldungeon.SPDSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.alphadraxonis.sandboxpixeldungeon.journal.Document;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.Button;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.StatusPane;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndKeyBindings;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

//from ui.MenuPane
public class MenuPane extends Component {

    private Image bg;

    private Image depthIcon;
    private BitmapText depthText;
    private Button depthButton;

    private JournalButton btnJournal;
    private MenuButton btnMenu;

    private BitmapText version;

    public static final int WIDTH = 32;

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        bg = new Image(Assets.Interfaces.MENU);
        add(bg);

        if (EditorScene.customLevel().feeling != null) {
            depthIcon = Icons.get(EditorScene.customLevel().feeling);
            add(depthIcon);
        }

        depthText = new BitmapText(EditorScene.customLevel().name, PixelScene.pixelFont);
        depthText.hardlight(0xCACFC2);
        depthText.measure();
        add(depthText);

        depthButton = new Button() {
            @Override
            protected String hoverText() {
                switch (EditorScene.customLevel().feeling) {
                    case CHASM:
                        return Messages.get(GameScene.class, "chasm");
                    case WATER:
                        return Messages.get(GameScene.class, "water");
                    case GRASS:
                        return Messages.get(GameScene.class, "grass");
                    case DARK:
                        return Messages.get(GameScene.class, "dark");
                    case LARGE:
                        return Messages.get(GameScene.class, "large");
                    case TRAPS:
                        return Messages.get(GameScene.class, "traps");
                    case SECRETS:
                        return Messages.get(GameScene.class, "secrets");
                }
                return null;
            }

            @Override
            protected void onClick() {
                super.onClick();

//                //just open journal for now, maybe have it open landmarks after expanding that page?
//                GameScene.show( new WndJournal() );
            }
        };
        add(depthButton);

        btnJournal = new JournalButton();
        add(btnJournal);

        btnMenu = new MenuButton();
        add(btnMenu);

        version = new BitmapText("v" + Game.version, PixelScene.pixelFont);
        version.alpha(0.5f);
        add(version);
    }

    @Override
    protected void layout() {
        super.layout();

        bg.x = x;
        bg.y = y;

        btnMenu.setPos(x + WIDTH - btnMenu.width(), y);

        btnJournal.setPos(btnMenu.left() - btnJournal.width() + 2, y);

        depthIcon.x = btnJournal.left() - 7 + (7 - depthIcon.width()) / 2f - 0.1f;
        depthIcon.y = y + 1;
        if (SPDSettings.interfaceSize() == 0) depthIcon.y++;
        PixelScene.align(depthIcon);

        depthText.scale.set(PixelScene.align(0.67f));
        depthText.x = depthIcon.x + depthIcon.width() - depthText.width();
        depthText.y = depthIcon.y + depthIcon.height();
        PixelScene.align(depthText);

        depthButton.setRect(depthIcon.x, depthIcon.y, depthIcon.width(), depthIcon.height() + depthText.height());

        version.scale.set(PixelScene.align(0.5f));
        version.measure();
        version.x = x + WIDTH - version.width();
        version.y = y + bg.height() + (3 - version.baseLine());
        PixelScene.align(version);
    }

    public void updateDepthIcon(){
        remove(depthIcon);
        depthIcon.destroy();
        depthIcon = Icons.get(EditorScene.customLevel().feeling);
        add(depthIcon);
        layout();
    }

    private static class JournalButton extends Button {

        private Image bg;
        private Image journalIcon;

        private Document flashingDoc = null;
        private String flashingPage = null;

        public JournalButton() {
            super();

            width = bg.width + 4;
            height = bg.height + 4;
        }

        @Override
        public GameAction keyAction() {
            return SPDAction.JOURNAL;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            bg = new Image(Assets.Interfaces.MENU_BTN, 2, 2, 13, 11);
            add(bg);

            journalIcon = new Image(Assets.Interfaces.MENU_BTN, 31, 0, 11, 7);
            add(journalIcon);
        }

        @Override
        protected void layout() {
            super.layout();

            bg.x = x + 2;
            bg.y = y + 2;

            journalIcon.x = bg.x + (bg.width() - journalIcon.width()) / 2f;
            journalIcon.y = bg.y + (bg.height() - journalIcon.height()) / 2f;
            PixelScene.align(journalIcon);
        }

        private float time;

        @Override
        public void update() {
            super.update();

            if (flashingPage != null) {
                journalIcon.am = (float) Math.abs(Math.cos(StatusPane.FLASH_RATE * (time += Game.elapsed)));
                bg.brightness(0.5f + journalIcon.am);
                if (time >= Math.PI / StatusPane.FLASH_RATE) {
                    time = 0;
                }
            }
        }

        @Override
        protected void onPointerDown() {
            bg.brightness(1.5f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
        }

        @Override
        protected void onPointerUp() {
            bg.resetColor();
        }

        @Override
        protected void onClick() {
            time = 0;
            if (flashingPage != null) {
                EditorScene.show(new WndMenuEditor());
                flashingPage = null;
            } else {
                EditorScene.show(new WndEditorSettings());
            }
        }
        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "journal"));
        }
    }
    private static class MenuButton extends Button {
        private Image image;

        public MenuButton() {
            super();

            width = image.width + 4;
            height = image.height + 4;
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            image = new Image(Assets.Interfaces.MENU_BTN, 17, 2, 12, 11);
            add(image);
        }

        @Override
        protected void layout() {
            super.layout();

            image.x = x + 2;
            image.y = y + 2;
        }

        @Override
        protected void onPointerDown() {
            image.brightness(1.5f);
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
        }

        @Override
        protected void onPointerUp() {
            image.resetColor();
        }

        @Override
        protected void onClick() {
            EditorScene.show(new WndMenuEditor());
        }

        @Override
        public GameAction keyAction() {
            return GameAction.BACK;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "menu"));
        }
    }

}