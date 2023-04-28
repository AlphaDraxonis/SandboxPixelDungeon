package com.shatteredpixel.shatteredpixeldungeon.editor;

import java.util.Objects;

/**
 * WARNING! need to call update() AFTER changing the mapsize of floor in EditorScene<br>
 * x and y have values from 0 to width-1 bzw. height-1 (inclusive) (bzw. is a german shortcut)
 */
public class Koord {

    private int x;
    private int y;
    private int cell;

    public Koord(int cell) {
        setCell(cell);
    }

    public Koord(int x, int y) {
        setXY(x, y);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int cell() {
        return cell;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
        update();
    }

    public void setX(int x) {
        if (x < 0 || x >= EditorScene.floor.width())
            throw new IllegalArgumentException("Invalid x value: " + x + " (should be x>=0 and x<mapWidth (width=" + EditorScene.floor.width() + "))");
        this.x = x;
        cell += x;
    }

    public void setY(int y) {
        if (y < 0 || y >= EditorScene.floor.height())
            throw new IllegalArgumentException("Invalid y value: " + y + " (should be y>=0 and y<mapHeigth (height=" + EditorScene.floor.height() + "))");
        this.y = y;
        cell += y * EditorScene.floor.width();
    }

    public void setCell(int cell) {
        this.cell = cell;
        x = cell % EditorScene.floor.width();
        y = cell / EditorScene.floor.width();
        if (y >= EditorScene.floor.height())
            throw new IllegalArgumentException("y has been set to a value outside of the map: " + y);
    }

    /**
     * X and Y don't change, but cell is set to fit according to EditorScene.floor size
     */
    public void update() {
        cell = y * EditorScene.floor.width() + x;
        if (y >= EditorScene.floor.height())
            throw new IllegalArgumentException("y has been set to a value outside of the map: " + y);
    }

    @Override
    public String toString() {
        return "X: " + x() + ", Y: " + y();
    }

    /**
     * ONLY uses CELL as comparison, should also call update() before!
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Koord) return ((Koord) obj).cell() == cell();
        return false;
    }

    /**
     * ONLY uses CELL as comparison, should also call update() before!
     */
    @Override
    public int hashCode() {
        return cell();
    }
}
