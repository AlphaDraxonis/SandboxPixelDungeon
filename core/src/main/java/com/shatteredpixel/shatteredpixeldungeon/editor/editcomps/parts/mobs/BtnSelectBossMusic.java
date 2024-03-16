/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.ChangeRegion;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.WndSelectMusic;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;

public class BtnSelectBossMusic extends StyledButtonWithIconAndText {

    public BtnSelectBossMusic(String music) {
        super(Chrome.Type.GREY_BUTTON_TR, "");

        text.align(RenderedTextBlock.CENTER_ALIGN);
        text.setHighlighting(false);

        icon(EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.SCROLL_LULLABY));
        icon().scale.set(1.3f);

        updateLabel(music);
    }

    @Override
    protected void onClick() {
        EditorScene.show(new WndSelectMusic(WndSelectMusic.TypeOfFirstCategory.FOR_BOSSES) {
            @Override
            protected void onSelect(Object music) {
                super.onSelect(music);

                if (music instanceof Integer) {
                    setBossMusic(((int) music) == -3 ? "/" : null);
                }
                else if (music instanceof String) {
                    setBossMusic((String) music);
                }
            }
        });
    }

    protected void setBossMusic(String music) {
        updateLabel(music);
    }

    protected void updateLabel(String music) {
        text(Messages.get(ChangeRegion.class, "music") + "\n" + (music == null ? Messages.get(WndSelectMusic.class, "default_music") : WndSelectMusic.getDisplayName(music)));
    }
}