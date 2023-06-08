package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Piranha;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditMobComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
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
        Mob mob = (Mob) mob().getCopy();

        if (!validPlacement(mob, level, cell)) return;

        removeMob(level.getMobAtCell(cell));

        mob.pos = cell;
        EditorScene.add(mob);

        level.occupyCell(mob);
    }

    @Override
    public String name() {
        return mob().name();
    }

    public Mob mob() {
        return mob;
    }

    public static boolean validPlacement(Mob mob, CustomLevel level, int cell) {
        return level.passable[cell]
                && (!Char.hasProp(mob, Char.Property.LARGE) || level.openSpace[cell])
                && (!(mob instanceof Piranha) || level.water[cell])
                ;//&& level.map[cell] != Terrain.DOOR;//TODO make placement on doors possible
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