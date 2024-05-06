package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.watabou.noosa.Group;
import com.watabou.utils.SparseArray;

public class ArrowCellTilemap extends Group {
    private static ArrowCellTilemap instance;

    protected SparseArray<ArrowCell> arrowCells;

    private ArrowCellTilemapPart diagonalOuterArrows;
    private ArrowCellTilemapPart straightOuterArrows;
    private ArrowCellTilemapPart center;

    public ArrowCellTilemap(SparseArray<ArrowCell> arrowCells) {

        this.arrowCells = arrowCells;

        diagonalOuterArrows = new ArrowCellTilemapPart() {
            @Override
            protected int getTileVisual(int pos, int tile, boolean flat) {
                ArrowCell arrowCell = arrowCells.get(pos);
                if (arrowCell != null
                    && (arrowCell.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing())) {
                    return imgCodeDiagonalOuterArrows(arrowCell.directionsEnter, arrowCell.visible);
                }
                return -1;
            }
        };
        add(diagonalOuterArrows);

        straightOuterArrows = new ArrowCellTilemapPart() {
            @Override
            protected int getTileVisual(int pos, int tile, boolean flat) {
                ArrowCell arrowCell = arrowCells.get(pos);
                if (arrowCell != null
                        && (arrowCell.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing())) {
                    return imgCodeStraightOuterArrows(arrowCell.directionsEnter, arrowCell.visible);
                }
                return -1;
            }
        };
        add(straightOuterArrows);

        center = new ArrowCellTilemapPart() {
            @Override
            protected int getTileVisual(int pos, int tile, boolean flat) {
                ArrowCell arrowCell = arrowCells.get(pos);
                if (arrowCell != null
                        && (arrowCell.visible || Dungeon.customDungeon.seeSecrets || CustomDungeon.isEditing())) {
                    return imgCodeCenter(arrowCell.directionsEnter, arrowCell.visible);
                }
                return -1;
            }
        };
        add(center);

        instance = this;
    }

    public void updateMap() {
        diagonalOuterArrows.updateMap();
        straightOuterArrows.updateMap();
        center.updateMap();
    }

    public void updateMapCell(int cell) {
        diagonalOuterArrows.updateMapCell(cell);
        straightOuterArrows.updateMapCell(cell);
        center.updateMapCell(cell);
    }

    public void map(int[] data, int cols) {
        diagonalOuterArrows.map(data, cols);
        straightOuterArrows.map(data, cols);
        center.map(data, cols);
    }

    private static abstract class ArrowCellTilemapPart extends DungeonTilemap {

        public ArrowCellTilemapPart() {
            super(Assets.Environment.ARROW_CELL);
            map( CustomDungeon.isEditing() ? Dungeon.level.map : Dungeon.level.visualMap, Dungeon.level.width() );
        }
    }

    public static int imgCodeDiagonalOuterArrows(int directions, boolean visible) {
        int visual = 0;
        if ((directions & ArrowCell.TOP_LEFT) != 0) visual += 1;
        if ((directions & ArrowCell.TOP_RIGHT) != 0) visual += 2;
        if ((directions & ArrowCell.BOTTOM_LEFT) != 0) visual += 4;
        if ((directions & ArrowCell.BOTTOM_RIGHT) != 0) visual += 8;
        if (!visible) visual += 64;
        return visual;
    }

    public static int imgCodeStraightOuterArrows(int directions, boolean visible) {
        int visual = 16;
        if ((directions & ArrowCell.TOP) != 0) visual += 1;
        if ((directions & ArrowCell.RIGHT) != 0) visual += 2;
        if ((directions & ArrowCell.LEFT) != 0) visual += 4;
        if ((directions & ArrowCell.BOTTOM) != 0) visual += 8;
        if (!visible) visual += 64;
        return visual;
    }

    public static int imgCodeCenter(int directions, boolean visible) {
        int visual = 32;
        if ((directions & ArrowCell.TOP_LEFT) != 0
                || (directions & ArrowCell.BOTTOM_RIGHT) != 0) visual += 1;
        if ((directions & ArrowCell.TOP_RIGHT) != 0
                || (directions & ArrowCell.BOTTOM_LEFT) != 0) visual += 2;
        if ((directions & ArrowCell.TOP) != 0
                || (directions & ArrowCell.BOTTOM) != 0) visual += 4;
        if ((directions & ArrowCell.RIGHT) != 0
                || (directions & ArrowCell.LEFT) != 0) visual += 8;
//                    if (!arrowCell.allowsWaiting) visual += 16;
        if (!visible) visual += 64;
        return visual;
    }

}