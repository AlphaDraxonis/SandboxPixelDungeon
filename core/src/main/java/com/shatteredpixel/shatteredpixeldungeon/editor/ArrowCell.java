package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.Locale;

public class ArrowCell implements Bundlable, Copyable<ArrowCell>, PathFinder.ArrowCellInterface {

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
    
    
    public static final int NUM_AFFECT_TYPES = 3;
    
    public static final int AFFECT_NONE = 0;
    public static final int AFFECT_PLAYER = 1;
    public static final int AFFECT_MOBS = 2;
    public static final int AFFECT_ALLIES = 4;
    public static final int AFFECT_ALL = AFFECT_PLAYER | AFFECT_MOBS | AFFECT_ALLIES;


    public int pos;
    public int directionsLeaving;
    public EnterMode enterMode;
    public boolean allowsWaiting = true;
    public boolean visible;
    public int affects;


    public ArrowCell() {
    }

    public ArrowCell(int pos) {
        this(pos, ALL, EnterMode.ALWAYS_ALLOWED, AFFECT_ALL);
    }

    public ArrowCell(int pos, int directionsLeaving, EnterMode enterMode, int affects) {
        this.pos = pos;
        this.directionsLeaving = directionsLeaving;
        this.enterMode = enterMode;
        this.affects = affects;
    }

    private static final String POS = "pos";
    private static final String DIRECTIONS_LEAVING = "directions_leaving";
    private static final String ENTER_MODE = "enter_mode";
    private static final String ALLOWS_WAITING = "allows_waiting";
    private static final String VISIBLE = "visible";
    private static final String AFFECTS = "affects";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(DIRECTIONS_LEAVING, directionsLeaving);
        bundle.put(ENTER_MODE, enterMode);
        bundle.put(ALLOWS_WAITING, allowsWaiting);
        bundle.put(VISIBLE, visible);
        bundle.put(AFFECTS, affects);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        directionsLeaving = bundle.getInt(DIRECTIONS_LEAVING);
        enterMode = bundle.getEnum(ENTER_MODE, EnterMode.class);
        allowsWaiting = bundle.getBoolean(ALLOWS_WAITING);
        visible = bundle.getBoolean(VISIBLE);
        affects = bundle.getInt(AFFECTS);
    }

    @Override
    public ArrowCell getCopy() {
        ArrowCell copy = new ArrowCell(pos, directionsLeaving, enterMode, affects);
        copy.visible = visible;
        copy.allowsWaiting = allowsWaiting;
        return copy;
    }

    public Image getSprite() {
        return EditorUtilities.getArrowCellTexture(this);
    }

    public String name() {
        return Messages.get(this, "name");
    }

    public String desc() {
        String affectDesc = null;
        if (affects == 0) affectDesc = Messages.get(this, "affect_none");
        else {
            for (int i = 0; i < NUM_AFFECT_TYPES; i++) {
                int bit = (int) Math.pow(2, i);
                if ((affects & bit) == bit) {
                    if (affectDesc != null) {
                        affectDesc += ", ";
                    } else {
                        affectDesc = "";
                    }
                    affectDesc += Messages.get(this, "affect_" + getBlockKey(bit));
                }
            }
        }
        return Messages.get(this, "desc", affectDesc) + " " + Messages.get(EnterMode.class, enterMode.name().toLowerCase(Locale.ENGLISH) + "_desc");
    }
    
    public static String getBlockKey(int blockBit) {
        switch (blockBit) {
            case AFFECT_PLAYER:
                return "player";
            case AFFECT_MOBS:
                return "mobs";
            case AFFECT_ALLIES:
                return "allies";
        }
        return "none";
    }
    
    public boolean allowsWaiting(Char ch) {
        return !affectsChar(ch) || allowsWaiting;
    }
    
    public boolean affectsHero() {
        return (affects & AFFECT_PLAYER) == AFFECT_PLAYER;
    }
    
    public boolean affectsMobs() {
        return (affects & AFFECT_MOBS) == AFFECT_MOBS;
    }
    
    public boolean affectsAllies() {
        return (affects & AFFECT_ALLIES) == AFFECT_ALLIES;
    }
    
    public boolean affectsChar(Object ch) {
        if (ch instanceof Char) {
            if (ch instanceof Hero) return affectsHero();
            if (((Char) ch).alignment == Char.Alignment.ENEMY) return affectsMobs();
            return affectsAllies();
        }
        if (ch == null) {
            return true;
        }
        Game.reportException(new Exception("ch must be a Char or null! Did you pass the wrong argument?"));
        return true;
    }

    public boolean allowsDirectionLeaving(int pathfinderNeighboursValue, Object ch) {
        return !affectsChar(ch) || allowsDirection(pathfinderNeighboursValue, directionsLeaving);
    }

    public boolean allowsDirectionEnter(int pathfinderNeighboursValue, Object ch) {
        if (!affectsChar(ch)) {
            return true;
        }
        switch (enterMode) {
			case IF_NO_EXIT:
                return !allowsDirectionLeaving(pathfinderNeighboursValue, ch);
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
    public static boolean allowsStep(int from, int to, Char ch) {
       return PathFinder.ArrowCellInterface.allowsStep(from, to, ch);
    }
}