/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndMenuEditor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.ui.MenuPane;

public class EditorMenuPane extends MenuPane {
    
    @Override
    protected void onDepthButtonClicked() {
        if (EditorScene.isEditingRoomLayout) {
            Dungeon.customDungeon.removeFloor(EditorScene.getCustomLevel().levelScheme);
            EditorScene.open((CustomLevel) EditorScene.customLevelBeforeRoomLayout.loadLevel());
        }
        else {
            EditorScene.show(new WndSwitchFloor());
        }
    }
    
    @Override
    protected void onJournalButtonClicked() {
        EditorScene.show(new WndEditorSettings());
    }
    
    @Override
    protected void onMenuButtonClicked() {
        EditorScene.show(new WndMenuEditor());
    }

}
