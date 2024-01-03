package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class Barrier implements Bundlable {

    public static final int NUM_BLOCK_TYPES = 5;

    public static final int BLOCK_NONE = 0;
    public static final int BLOCK_PLAYER = 1;
    public static final int BLOCK_MOBS = 2;
    public static final int BLOCK_ALLIES = 4;
    public static final int BLOCK_PROJECTILES = 8;
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

    public Barrier getCopy() {
        Barrier copy = new Barrier(pos, blocks);
        copy.visible = visible;
        return copy;
    }

    public String name() {
        return Messages.get(Barrier.class, "name");
    }

    public String desc() {
        String desc = Messages.get(Barrier.class, "desc");
        if (blocks == 0) desc += "\n" + Messages.get(Barrier.class, "block_none");
        else {
            for (int i = 0; i < NUM_BLOCK_TYPES; i++) {
                int bit = (int) Math.pow(2, i);
                if ((blocks & bit) != 0) desc += "\n" + Messages.get(Barrier.class, "block_" + getBlockKey(bit));
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

    public static boolean stopMobs(int cell, Char.Alignment alignment) {
        if (Dungeon.level.barriers.get(cell) != null) {
            if (alignment == Char.Alignment.ENEMY) {
                if (Dungeon.level.barriers.get(cell).blocksMobs()) return true;
            } if (Dungeon.level.barriers.get(cell).blocksAllies()) return true;
        }
        return false;
    }
}