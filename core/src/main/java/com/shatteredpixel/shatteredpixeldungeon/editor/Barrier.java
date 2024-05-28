package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class Barrier implements Bundlable, Copyable<Barrier> {

    public static final int NUM_BLOCK_TYPES = 5;

    public static final int BLOCK_NONE = 0;
    /**
     * A player cannot enters this tile in any way, similar to a wall.
     */
    public static final int BLOCK_PLAYER = 1;

    /**
     * All mobs with enemy alignment can't enters this tile in any way, similar to a wall.
     */
    public static final int BLOCK_MOBS = 2;

    /**
     * All allied mobs (no enemy alignment, only neutral and ally remaining) can't enters this tile in any way, similar to a wall.
     */
    public static final int BLOCK_ALLIES = 4;

    /**
     * For projectiles, this tile behaves exactly as a wall, so things like projecting missiles can still go through.
     */
    public static final int BLOCK_PROJECTILES = 8;

    /**
     * For blobs, this tile behaves exactly as a wall, so no blobs can exist on this tile unless placed in editor, and no blobs will spread on it.
     */
    public static final int BLOCK_BLOBS = 16;
    public static final int BLOCK_ALL = BLOCK_PLAYER | BLOCK_MOBS | BLOCK_ALLIES | BLOCK_PROJECTILES | BLOCK_BLOBS;

    public int pos;
    public int blocks;
    public boolean visible;


    public Barrier() {
    }

    public Barrier(int pos) {
        this(pos, BLOCK_ALL);
    }

    public Barrier(int pos, int blocks) {
        this.pos = pos;
        this.blocks = blocks;
    }

    private static final String POS = "pos";
    private static final String BLOCKS = "blocks";
    private static final String VISIBLE = "visible";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(BLOCKS, blocks);
        bundle.put(VISIBLE, visible);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        blocks = bundle.getInt(BLOCKS);
        visible = bundle.getBoolean(VISIBLE);
    }

    @Override
    public Barrier getCopy() {
        Barrier copy = new Barrier(pos, blocks);
        copy.visible = visible;
        return copy;
    }

    public Image getSprite() {
        return EditorUtilies.getBarrierTexture(visible ? 1 : 0);
    }

    public String name() {
        return Messages.get(this, "name");
    }

    public String desc() {
        String desc = Messages.get(this, "desc");
        if (blocks == 0) desc += "\n" + Messages.get(this, "block_none");
        else {
            for (int i = 0; i < NUM_BLOCK_TYPES; i++) {
                int bit = (int) Math.pow(2, i);
                if ((blocks & bit) != 0) desc += "\n" + Messages.get(this, "block_" + getBlockKey(bit));
            }
        }
        return desc;
    }

    public static String getBlockKey(int blockBit) {
        switch (blockBit) {
            case BLOCK_PLAYER:
                return "player";
            case BLOCK_MOBS:
                return "mobs";
            case BLOCK_ALLIES:
                return "allies";
            case BLOCK_PROJECTILES:
                return "projectiles";
            case BLOCK_BLOBS:
                return "blobs";
        }
        return "none";
    }

    public boolean blocksHero() {
        return (blocks & BLOCK_PLAYER) != 0;
    }

    public boolean blocksMobs() {
        return (blocks & BLOCK_MOBS) != 0;
    }

    public boolean blocksAllies() {
        return (blocks & BLOCK_ALLIES) != 0;
    }

    public boolean blocksProjectiles() {
        return (blocks & BLOCK_PROJECTILES) != 0;
    }

    public boolean blocksBlobs() {
        return (blocks & BLOCK_BLOBS) != 0;
    }

    public boolean blocksChar(Char ch) {
        if (ch instanceof Hero) return blocksHero();
        if (ch.alignment == Char.Alignment.ENEMY) return blocksMobs();
        return blocksAllies();
    }

    public static boolean stopHero(int cell, Level level) {
        return level.barriers.get(cell) != null && level.barriers.get(cell).blocksHero();
    }


    public static boolean stopChar(int cell, Char ch) {
        if (ch == null) return false;
        Barrier b = Dungeon.level.barriers.get(cell);
        return b != null && b.blocksChar(ch);
    }

    public static boolean canEnemyEnterCell(int cell, boolean enterAvoid) {
        return (Dungeon.level.isPassable(cell) || (enterAvoid && Dungeon.level.avoid[cell]))
                && ( Dungeon.level.barriers.get(cell) == null || !Dungeon.level.barriers.get(cell).blocksMobs())
                && Actor.findChar(cell) == null;
    }

//    public static boolean canEnterCell(int cell, Char ch, boolean enterAvoid) {
//        return (Dungeon.level.isPassable(cell) || (enterAvoid && Dungeon.level.avoid[cell])) && !Barrier.stopChar(cell, ch) && Actor.findChar(cell) == null;
//    }

    public static boolean canEnterCell(int cell, Char ch, boolean enterAvoid, boolean checkOtherActors) {
        return (Dungeon.level.isPassable(cell) || (enterAvoid && Dungeon.level.avoid[cell])) && !Barrier.stopChar(cell, ch)
                && (!checkOtherActors || Actor.findChar(cell) == null);
    }
}