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

package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.WatabouRect;

public class LevelTransition extends WatabouRect implements Bundlable {

    public enum Type {
        SURFACE,
        REGULAR_ENTRANCE,
        REGULAR_EXIT,
        BRANCH_ENTRANCE,
        BRANCH_EXIT;
    }

    public Type type;
    public String departLevel;
    public int departCell;
    public int destCell;
    public int destBranch;
    public String destLevel;
    public Type destType;

    public int centerCell;

    public boolean showDetailsInEditor = true;

    //for bundling
    public LevelTransition() {
        super();
    }

    public LevelTransition(Level level, int cell, Type type) {
        this(level, cell, type, -1);
    }

    //gives default values for common transition types
    public LevelTransition(Level level, int cell, Type type, int destCell) {
        this(level, cell, type, destCell, 0);
    }

    public LevelTransition(Level level, int cell, Type type, int destCell, int destBranch) {
        centerCell = cell;
        departCell = cell;
        Point p = level.cellToPoint(cell);
        set(p.x, p.y, p.x, p.y);
        this.type = type;
        this.destCell = destCell;
        departLevel = CustomLevel.tempDungeonNameForKey == null ? Dungeon.levelName : CustomLevel.tempDungeonNameForKey;
        LevelScheme levelScheme = Dungeon.customDungeon.getFloor(Dungeon.levelName);
        switch (type) {
            default:
                destLevel = levelScheme.getDefaultAbove();
                destType = Type.REGULAR_EXIT;
                this.destBranch = destBranch;
                if (destCell == -1 && levelScheme.getEntranceTransitionRegular() != null)
                    this.destCell = levelScheme.getEntranceTransitionRegular().destCell;
                break;
            case REGULAR_EXIT:
                destLevel = levelScheme.getDefaultBelow();
                destType = Type.REGULAR_ENTRANCE;
                this.destBranch = destBranch;
                if (destCell == -1 && levelScheme.getExitTransitionRegular() != null)
                    this.destCell = levelScheme.getExitTransitionRegular().destCell;
                break;
            case BRANCH_ENTRANCE:
                destLevel = level.name;//same levelscheme, different branch
                destType = Type.BRANCH_EXIT;
                this.destBranch = destBranch;
//                if (destCell == -1 && levelScheme.getEntranceTransitionRegular() != null)
//                    this.destCell = levelScheme.getEntranceTransitionRegular().destCell;
                break;
            case BRANCH_EXIT:
                destLevel = level.name;//same levelscheme, different branch
                destType = Type.BRANCH_ENTRANCE;
                this.destBranch = destBranch;
//                if (destCell == -1 && levelScheme.getExitTransitionRegular() != null)
//                    this.destCell = levelScheme.getExitTransitionRegular().destCell;
                break;
            case SURFACE:
                destLevel = Level.SURFACE;
                destType = null;
                this.destBranch = 0;
        }
    }

    public LevelTransition(Level level, int cell, int destCell, String destLevel) {
        centerCell = cell;
        departCell = cell;
        if (cell >= 0) {
            Point p = level.cellToPoint(cell);
            set(p.x, p.y, p.x, p.y);
        }
        this.destCell = destCell;
        departLevel = level.name;
        this.destLevel = destLevel;
    }

    public LevelTransition(String destLevel, int destCell) {//As a wrapper for generated levels that are no templates
        this.destLevel = destLevel;
        this.destCell = destCell;
    }

    //note that the center cell isn't always the actual center.
    // It is important when game logic needs to pick a specific cell for some action
    // e.g. where to place the hero
    public int cell() {
        return centerCell;
    }

    //Transitions are inclusive to their right and bottom sides
    @Override
    public int width() {
        return super.width() + 1;
    }

    @Override
    public int height() {
        return super.height() + 1;
    }

    @Override
    public boolean inside(Point p) {
        return p.x >= left && p.x <= right && p.y >= top && p.y <= bottom;
    }

    public boolean inside(int cell) {
        return inside(new Point(Dungeon.level.cellToPoint(cell)));
    }

    public Point center() {
        return new Point(
                (left + right) / 2 + (((right - left) % 2) == 1 ? Random.Int(2) : 0),
                (top + bottom) / 2 + (((bottom - top) % 2) == 1 ? Random.Int(2) : 0));
    }

    public static final String TYPE = "type";
    public static final String DEPART_LEVEL = "depart_level";
    public static final String DEST_LEVEL = "dest_level";
    public static final String DEST_TYPE = "dest_type";
    public static final String DEST_CELL = "dest_cell";
    public static final String DEPART_CELL = "depart_cell";
    public static final String DEST_BRANCH = "dest_branch";
    public static final String DETAILS_IN_EDITOR = "details_in_editor";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("left", left);
        bundle.put("top", top);
        bundle.put("right", right);
        bundle.put("bottom", bottom);

        bundle.put("center", centerCell);

        bundle.put(TYPE, type);
        bundle.put(DEPART_LEVEL, departLevel);
        bundle.put(DEST_LEVEL, destLevel);
        bundle.put(DEST_TYPE, destType);
        bundle.put(DEPART_CELL, departCell);
        bundle.put(DEST_CELL, destCell);
        bundle.put(DEST_BRANCH, destBranch);

        bundle.put(DETAILS_IN_EDITOR, showDetailsInEditor);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        left = bundle.getInt("left");
        top = bundle.getInt("top");
        right = bundle.getInt("right");
        bottom = bundle.getInt("bottom");

        centerCell = bundle.getInt("center");

        if (bundle.contains(TYPE)) type = bundle.getEnum(TYPE, Type.class);
        departLevel = bundle.getString(DEPART_LEVEL);
        destLevel = bundle.getString(DEST_LEVEL);
        if (destLevel.equals("")) destLevel = null;
        if (bundle.contains(DEST_TYPE)) destType = bundle.getEnum(DEST_TYPE, Type.class);
        destCell = bundle.getInt(DEST_CELL);
        departCell = bundle.getInt(DEPART_CELL);
        destBranch = bundle.getInt(DEST_BRANCH);

        showDetailsInEditor = bundle.getBoolean(DETAILS_IN_EDITOR);
    }

    public LevelTransition getCopy(){
        Bundle bundle = new Bundle();
        bundle.put("TRANSITION",this);
        return  (LevelTransition) bundle.get("TRANSITION");
    }
}
