package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class ArrowCell implements Bundlable, PathFinder.ArrowCellInterface {

    public enum EnterMode {
        IF_NO_EXIT,
        IF_EXIT_ON_OPPOSITE,
        ALWAYS_ALLOWED
    }

    // Order is important!!!
    public static final int NONE = 0;
    public static final int TOP_LEFT = 1;
    public static final int TOP = 2;
    public static final int TOP_RIGHT = 4;
    public static final int LEFT = 8;
    public static final int RIGHT = 16;
    public static final int BOTTOM_LEFT = 32;
    public static final int BOTTOM = 64;
    public static final int BOTTOM_RIGHT = 128;
    public static final int ALL = 255;


    public int pos;
    public int directionsLeaving;
    public EnterMode enterMode;
    public boolean allowsWaiting = true;
    public boolean visible;


    public ArrowCell() {
    }

    public ArrowCell(int pos) {
        this(pos, ALL, EnterMode.ALWAYS_ALLOWED);
    }

    public ArrowCell(int pos, int directionsLeaving, EnterMode enterMode) {
        this.pos = pos;
        this.directionsLeaving = directionsLeaving;
        this.enterMode = enterMode;
    }

    private static final String POS = "pos";
    private static final String DIRECTIONS_LEAVING = "directions_leaving";
    private static final String ENTER_MODE = "enter_mode";
    private static final String ALLOWS_WAITING = "allows_waiting";
    private static final String VISIBLE = "visible";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(DIRECTIONS_LEAVING, directionsLeaving);
        bundle.put(ENTER_MODE, enterMode);
        bundle.put(ALLOWS_WAITING, allowsWaiting);
        bundle.put(VISIBLE, visible);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        directionsLeaving = bundle.getInt(DIRECTIONS_LEAVING);
        enterMode = bundle.getEnum(ENTER_MODE, EnterMode.class);
        allowsWaiting = bundle.getBoolean(ALLOWS_WAITING);
        visible = bundle.getBoolean(VISIBLE);
    }

    public ArrowCell getCopy() {
        ArrowCell copy = new ArrowCell(pos, directionsLeaving, enterMode);
        copy.visible = visible;
        copy.allowsWaiting = allowsWaiting;
        return copy;
    }

    public Image getSprite() {
        return EditorUtilies.getArrowCellTexture(this);
    }

    public String name() {
        return Messages.get(this, "name");
    }

    public String desc() {
        return Messages.get(this, "desc");
    }

    public boolean allowsDirectionLeaving(int pathfinderNeighboursValue) {
        return allowsDirection(pathfinderNeighboursValue, directionsLeaving);
    }

    public boolean allowsDirectionEnter(int pathfinderNeighboursValue) {
        switch (enterMode) {
			case IF_NO_EXIT:
                return !allowsDirectionLeaving(pathfinderNeighboursValue);
			case IF_EXIT_ON_OPPOSITE:
                return allowsDirection(-pathfinderNeighboursValue, directionsLeaving);
			case ALWAYS_ALLOWED:
                return true;
		}
        return false;
    }

    public static boolean allowsDirection(int pathfinderNeighboursValue, int directionsAllowed) {
        return (directionsAllowed & findDirectionBit(pathfinderNeighboursValue)) != 0;
    }

    private static int findDirectionBit(int pathfinderNeighboursValue) {
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            if (PathFinder.NEIGHBOURS8[i] == pathfinderNeighboursValue) return 1 << i;
        }
        return NONE;
    }

    /**
     * Assumes from and to are <b><u>adjacent</u></b>!
     */
    public static boolean allowsStep(int from, int to) {
       return PathFinder.ArrowCellInterface.allowsStep(from, to);
    }
}