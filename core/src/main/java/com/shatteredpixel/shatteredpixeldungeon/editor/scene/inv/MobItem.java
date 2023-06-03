package com.shatteredpixel.shatteredpixeldungeon.editor.scene.inv;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;

public class MobItem extends EditorItem {


    private final Mob mob;


    public MobItem(Mob mob) {
        this.mob = mob;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, mob.name(), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null) return;

                if (icon != null) remove(icon);
                icon = mob.sprite();
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditMobComp(mob());
    }

    @Override
    public Image getSprite() {
        return mob().sprite();
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();
        Mob mob;
        try {
            mob = (Mob) mob().clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }

        if (!validPlacement(mob, level, cell)) return;

        removeMob(level.getMobAtCell(cell));

        mob.state = mob instanceof NPC ? mob.PASSIVE : mob.SLEEPING;
        mob.pos = cell;
        EditorScene.add(mob);

        level.occupyCell(mob);
    }

    public Mob mob() {
        return mob;
    }

    public static boolean validPlacement(Mob mob, CustomLevel level, int cell) {
        return level.passable[cell]
                && (!Char.hasProp(mob, Char.Property.LARGE) || level.openSpace[cell])
                && (!(mob instanceof Piranha) || level.water[cell]);
    }

    public static boolean removeMob(Mob mob) {
        if (mob != null) {
            mob.destroy();
            mob.sprite.hideEmo();
            mob.sprite.killAndErase();
            return true;
        }
        return false;
    }
}