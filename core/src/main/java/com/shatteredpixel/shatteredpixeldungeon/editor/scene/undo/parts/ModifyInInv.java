/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2024 Evan Debenham
 *  *
 *  * Sandbox Pixel Dungeon
 *  * Copyright (C) 2023-2024 AlphaDraxonis
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.FindInBag;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartModify;

public final class ModifyInInv implements ActionPartModify {
        
        private final GameObject before;
        private GameObject after;
        
        public ModifyInInv(GameObject obj) {
            before = obj.getCopy();
            after = obj;
        }
        
        @Override
        public void undo() {
            GameObject realObj = (GameObject) new FindInBag(after).getAsInBag().getObject();
            if (realObj != null) {
                realObj.copyStats(before);
            }
        }
        
        @Override
        public void redo() {
            GameObject realObj = (GameObject) new FindInBag(after).getAsInBag().getObject();
            if (realObj != null) {
                realObj.copyStats(after);
            }
        }
        
        @Override
        public boolean hasContent() {
            return !GameObject.areEqual(before, after);
        }
        
        @Override
        public void finish() {
            after = after.getCopy();
        }
    }
