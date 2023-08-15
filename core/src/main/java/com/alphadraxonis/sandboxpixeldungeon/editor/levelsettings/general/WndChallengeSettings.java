package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.Challenges;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor.WndNewFloor;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class WndChallengeSettings extends Window {

    private Component content;
    private ScrollPane scrollPane;

    private final LevelScheme newLevelScheme;

    public WndChallengeSettings(LevelScheme newLevelScheme) {
        this.newLevelScheme = newLevelScheme;

        resize(PixelScene.landscape() ? 215 : Math.min(160, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(WndChallengeSettings.class, "title"), 10);
        title.hardlight(Window.TITLE_COLOR);
        add(title);
        title.maxWidth(width);
        title.setPos((width - title.width()) * 0.5f, 3);

        content = new Component();

        RenderedTextBlock info = PixelScene.renderTextBlock(Messages.get(WndChallengeSettings.class, "info"), 6);
        content.add(info);
        info.maxWidth(width);
        info.setPos(0, 0);

        float posY = info.bottom() + 7;
        posY = addChallengeSettings(posY, Challenges.DARKNESS, content) + 2;
        posY = addChallengeSettings(posY, Challenges.NO_SCROLLS, content) + 2;
        if (newLevelScheme.getName() != null && newLevelScheme.getType() == CustomLevel.class)
            posY = addChallengeSettings(posY, Challenges.CHAMPION_ENEMIES, content) + 2;

        scrollPane = new ScrollPane(content);
        add(scrollPane);

        int h = (int) Math.min((PixelScene.uiCamera.height * 0.8f), Math.ceil(title.height() + posY + 6));
        resize(width, h);
        scrollPane.setRect(0, title.bottom() + 5, width, h - title.bottom() - 5);
    }

    private float addChallengeSettings(float posY, int challenge, Component content) {

        //TODO labels don't fit on the buttons!

        String titleString;
        if (challenge == Challenges.DARKNESS) titleString = "darkness";
        else if (challenge == Challenges.NO_SCROLLS) titleString = "no_scrolls";
        else if (challenge == Challenges.CHAMPION_ENEMIES) titleString = "champion_enemies";
        else return posY;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(Challenges.class, titleString)), 9);
        content.add(title);

        title.setPos(0, posY);
        posY = title.bottom() + 4;

        if (challenge == Challenges.DARKNESS) {
            posY = addCheckbox(new CheckBox(Messages.get(WndChallengeSettings.class, "spawn_torch")) {
                {
                    super.checked(newLevelScheme.spawnTorchIfDarkness);
                }

                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    newLevelScheme.spawnTorchIfDarkness = value;
                }
            }, content, posY);
            if (newLevelScheme.getName() != null && newLevelScheme.getType() == CustomLevel.class) {
                posY = addCheckbox(new CheckBox(Messages.get(WndChallengeSettings.class, "reduce_view")) {
                    {
                        super.checked(newLevelScheme.reduceViewDistanceIfDarkness);
                    }

                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        newLevelScheme.reduceViewDistanceIfDarkness = value;
                    }
                }, content, posY);
            }
        } else if (challenge == Challenges.NO_SCROLLS) {
            posY = addCheckbox(new CheckBox(Messages.get(WndChallengeSettings.class, "sou")) {
                {
                    super.checked(newLevelScheme.affectedByNoScrolls);
                }

                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    newLevelScheme.affectedByNoScrolls = value;
                }
            }, content, posY);
        } else if (challenge == Challenges.CHAMPION_ENEMIES) {
            posY = addCheckbox(new CheckBox(Messages.get(WndChallengeSettings.class, "spawn_champs")) {
                {
                    super.checked(newLevelScheme.rollForChampionIfChampionChallenge);
                }

                @Override
                public void checked(boolean value) {
                    super.checked(value);
                    newLevelScheme.rollForChampionIfChampionChallenge = value;
                }
            }, content, posY);
        }

        return posY;
    }

    private float addCheckbox(CheckBox cb, Component content, float posY) {
        cb.setRect(0, posY, width, WndNewFloor.BUTTON_HEIGHT);
        content.add(cb);
        return cb.bottom() + 2;
    }

}