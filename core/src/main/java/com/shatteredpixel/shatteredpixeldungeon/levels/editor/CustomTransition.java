package com.shatteredpixel.shatteredpixeldungeon.levels.editor;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Point;

public class CustomTransition extends LevelTransition {

    public CustomTransition(){
        super();
    }

    public CustomTransition(Level level, int cell, Type type, int destDepth, int destBranch, Type destType){
        super(level,cell,type,destDepth,destBranch,destType);
    }

    //gives default values for common transition types
    public CustomTransition(Level level, int cell, Type type){
        super(level,cell,type);
    }

}
