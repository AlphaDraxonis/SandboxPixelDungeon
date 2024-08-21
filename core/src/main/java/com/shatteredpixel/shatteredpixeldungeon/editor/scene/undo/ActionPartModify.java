package com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public interface ActionPartModify extends ActionPart{

    void finish();


    public static ActionPartModify startNewModify(Object objToModify) {

        if (objToModify instanceof Mob) return new MobActionPart.Modify((Mob) objToModify);
        if (objToModify instanceof Heap) return new HeapActionPart.Modify((Heap) objToModify);
        if (objToModify instanceof Trap) return new TrapActionPart.Modify((Trap) objToModify);
        if (objToModify instanceof Plant) return new PlantActionPart.Modify((Plant) objToModify);

        if (objToModify instanceof Checkpoint) return new CheckpointActionPart.Modify((Checkpoint) objToModify);
        if (objToModify instanceof ArrowCell) return new ArrowCellActionPart.Modify((ArrowCell) objToModify);
        if (objToModify instanceof Barrier) return new BarrierActionPart.Modify((Barrier) objToModify);

        if (objToModify instanceof Sign) return new SignActionPart.Modify((Sign) objToModify);

        if (objToModify instanceof Room) return new RoomActionPart.Modify((Room) objToModify);
        if (objToModify instanceof Buff) return new BuffActionPart.Modify((Buff) objToModify);

        if (objToModify instanceof Item) return new ItemActionPart.Modify((Item) objToModify);

//        if (objToModify instanceof Zone) return new ZoneActionPart.Modify((Zone) objToModify);
//        if (objToModify instanceof Cell) return new PlaceCellActionPart.Modify((ArrowCell) objToModify);
//        if (objToModify instanceof Particl) return new ParticleActionPart.Modify((ArrowCell) objToModify);
//        if (objToModify instanceof new ) return new TileModify.Modify((ArrowCell) objToModify);
//        if (objToModify instanceof Blob) return new BlobActionPart.Modify((Blob) objToModify);
//        if (objToModify instanceof CustomTile) return new CustomTileActionPart.Modify((CustomTile) objToModify);

        return null;
    }

}