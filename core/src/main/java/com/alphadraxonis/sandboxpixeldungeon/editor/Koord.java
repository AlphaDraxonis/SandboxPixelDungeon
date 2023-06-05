package com.alphadraxonis.sandboxpixeldungeon.editor;

import com.alphadraxonis.sandboxpixeldungeon.levels.Level;

/**
 * WARNING! need to call update() AFTER changing the mapsize of floor in EditorScene<br>
 * x and y have values from 0 to width-1 bzw. height-1 (inclusive) (bzw. is a german shortcut)
 */
public class Koord {

    private int x;
    private int y;
    private int cell;
    private Level level;

    public Koord(int cell){
        this(cell,EditorScene.customLevel());
    }
    public Koord(int cell,Level level) {
        this.level=level;
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
        if (x < 0 || x >= level.width())
            throw new IllegalArgumentException("Invalid x value: " + x + " (should be x>=0 and x<mapWidth (width=" + level.width() + "))");
        this.x = x;
        cell += x;
    }

    public void setY(int y) {
        if (y < 0 || y >=level.height())
            throw new IllegalArgumentException("Invalid y value: " + y + " (should be y>=0 and y<mapHeigth (height=" + level.height() + "))");
        this.y = y;
        cell += y * level.width();
    }

    public void setCell(int cell) {
        this.cell = cell;
        x = cell % level.width();
        y = cell / level.width();
        if (y >= level.height())
            throw new IllegalArgumentException("y has been set to a value outside of the map: " + y);
    }

    /**
     * X and Y don't change, but cell is set to fit according to EditorScene.floor size
     */
    public void update() {
        cell = y * level.width() + x;
        if (y >= level.height())
            throw new IllegalArgumentException("y has been set to a value outside of the map: " + y);
    }

    @Override
    public String toString() {
        return "( "+(x()+1) +" | "+(y()+1)+" )";//Add 1 to each because they start at 0, but the first pos should be (1|1)
//        return "X: " + (x()+1) + ", Y: " + (y()+1);
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