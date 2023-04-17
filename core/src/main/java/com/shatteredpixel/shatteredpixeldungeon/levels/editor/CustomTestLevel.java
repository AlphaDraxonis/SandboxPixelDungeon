package com.shatteredpixel.shatteredpixeldungeon.levels.editor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.*;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Point;

public class CustomTestLevel extends Level {

    private final int width = 10, height = 10;

    private final int[] terrains = {
            WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
            WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EXIT, EMPTY, WALL,
            WALL, WATER, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WATER, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ENTRANCE, EMPTY, WALL,
            WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EXIT, WALL,
            WALL, EXIT, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
            WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
    };


    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;
    }

    @Override
    public String tilesTex() {
        return Assets.Environment.TILES_SEWERS;
    }

    @Override
    public String waterTex() {
        return Assets.Environment.WATER_PRISON;
    }

    @Override
    protected boolean build() {

        setSize(width, height);
        terrains[54] = Dungeon.depth;


        for (int i = 0; i < terrains.length; i++) {
            map[i] = terrains[i];
        }

        addTransitios();

        return true;
    }

    protected void addTransitios() {
        int curBranch = Dungeon.branch;
        for (int i = 0; i < terrains.length; i++) {
            if (terrains[i] == ENTRANCE){
                LevelTransition transition = new LevelTransition(this, i, Dungeon.depth == 1 ? LevelTransition.Type.SURFACE : LevelTransition.Type.REGULAR_ENTRANCE);
//                transition.destBranch = parentBranch;
                transitions.add(transition);
            }
            else if (terrains[i] == EXIT) {
                LevelTransition transition = new LevelTransition(this, i, LevelTransition.Type.REGULAR_EXIT);
                transition.destBranch = curBranch;
                transitions.add(transition);
                curBranch++;
            }
        }
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
        Mob m1 = new Rat();
        m1.pos = 25;
        mobs.add(m1);

        Mob m2 = new Imp();
        m2.pos = 45;
        mobs.add(m2);
        Mob m4 = new Imp();
        m4.pos = 46;
        mobs.add(m4);

        Mob m3 = new Scorpio();
        m3.pos = 83;

        m3.paralysed = 5000;
        m3.invisible = 10;

//        mobs.add(m3);
    }


    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {

        drop(new Torch(), 21);

    }

    @Override
    public int randomRespawnCell(Char ch) {
        return entrance() - width();
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
