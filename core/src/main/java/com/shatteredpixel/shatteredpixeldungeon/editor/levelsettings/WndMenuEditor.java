/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;

import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;

//from WndGame
@NotAllowedInLua
public class WndMenuEditor extends Window {

    private static final int WIDTH = 120;
    public static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;

    private int pos;

    public WndMenuEditor() {

        super();

        //settings
        RedButton curBtn;
        addButton(curBtn = new RedButton(Messages.get(WndGame.class, "settings")) {
            @Override
            protected void onClick() {
                hide();
                EditorScene.show(new WndSettings());
            }
        });
        curBtn.icon(Icons.get(Icons.PREFS));

        // Main menu
        addButton(curBtn = new RedButton(Messages.get(WndGame.class, "menu")) {
            @Override
            protected void onClick() {
                if (GamesInProgress.curSlot == GamesInProgress.TEST_SLOT) {
                    GamesInProgress.curSlot = GamesInProgress.NO_SLOT;
                }
                //no need to sve here bc EditorScene autosaves anyway
                Game.switchScene(TitleScene.class);
            }
        });
        curBtn.icon(Icons.get(Icons.DISPLAY));
        if (SPDSettings.intro()) curBtn.enable(false);

        resize(WIDTH, pos);
    }

    private void addButton(RedButton btn) {
        add(btn);
        btn.setRect(0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT);
        pos += BTN_HEIGHT;
    }

    private void addButtons(RedButton btn1, RedButton btn2) {
        add(btn1);
        btn1.setRect(0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT);
        add(btn2);
        btn2.setRect(btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT);
        pos += BTN_HEIGHT;
    }
}