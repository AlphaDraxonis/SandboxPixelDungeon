package com.alphadraxonis.sandboxpixeldungeon.editor.scene.inv;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps.EditTrapComp;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class TrapItem extends EditorItem {

    private static final TextureFilm TEXTURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

    private final Trap trap;


    public TrapItem(Trap trap) {
        this.trap = trap;
    }

    private static int imgCode(Trap trap) {
        if (trap != null)
            return (trap.active ? trap.color : Trap.BLACK) + (trap.shape * 16) + (trap.visible ? 0 : 128);
        else return -1;
    }

    @Override
    public Image getSprite() {
        return getTrapImage(imgCode(trap()));
    }

    public static Image getTrapImage(int imgCode) {
        RectF frame = TEXTURE_FILM.get(imgCode);
        if (frame != null) {
            Image img = new Image(Assets.Environment.TERRAIN_FEATURES);
            img.frame(frame);
            return img;
        }
        return new Image();
    }

    public static Image getTrapImage(Trap trap) {
        return getTrapImage(imgCode(trap));
    }

    public static String createTitle(Trap trap) {
        return Messages.titleCase((trap.visible ?trap.name() : Messages.get(TrapItem.class,"title_hidden",trap.name())));
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, createTitle(trap()), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null || ((TrapItem) item).trap() == null) return;
                Trap t = ((TrapItem) item).trap();
                label.text(TrapItem.createTitle(t));

                if (icon != null) remove(icon);
                icon = TrapItem.getTrapImage(t);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditTrapComp(this);
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        Trap t = trap().getCopy();
        level.map[cell] = t.visible ? (t.active ? Terrain.TRAP : Terrain.INACTIVE_TRAP) : Terrain.SECRET_TRAP;
        level.setTrap(t, cell);
        EditorScene.updateMap(cell);
    }

    public static boolean removeTrap(int cell, CustomLevel level) {
        Trap t = level.traps.get(cell);
        if (t != null) {
            level.traps.remove(cell);
            level.map[cell] = Terrain.EMPTY;
            EditorScene.updateMap(cell);
            return true;
        }
        return false;
    }

    public Trap trap() {
        return trap;
    }
}