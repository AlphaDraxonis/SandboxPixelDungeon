package com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts;

import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.ActionPart;

public class RemoveMobActionPart implements ActionPart {

    private Mob mob, copyForUndo;

    public RemoveMobActionPart(Mob mob) {
        this.mob = mob;
        copyForUndo = (Mob) mob.getCopy();
    }

    @Override
    public void undo() {
        EditorScene.add(copyForUndo);
        Dungeon.level.occupyCell(copyForUndo);
        mob = copyForUndo;
        copyForUndo = (Mob) mob.getCopy();
    }

    @Override
    public void redo() {
        MobItem.removeMob(mob);
    }
}