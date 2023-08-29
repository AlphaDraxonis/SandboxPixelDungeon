package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditTrapComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.TrapActionPart;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.Trap;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.RectF;

public class TrapItem extends EditorItem {

    private static final TextureFilm TEXTURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

    private Trap trap;


    public TrapItem(){}
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
        return Messages.titleCase((trap.visible ? trap.name() : Messages.get(TrapItem.class, "title_hidden", trap.name())));
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
        if (validPlacement(cell, EditorScene.customLevel()))
            Undo.addActionPart(place(trap().getCopy(), cell));
    }

    public static boolean validPlacement(int cell, CustomLevel level) {
        return level.insideMap(cell);
    }

    @Override
    public String name() {
        return trap().name();
    }

    @Override
    public Object getObject() {
        return trap();
    }

    public Trap trap() {
        return trap;
    }

    public static int getTerrain(Trap trap) {
        return trap.visible ? (trap.active ? Terrain.TRAP : Terrain.INACTIVE_TRAP) : Terrain.SECRET_TRAP;
    }


    public static TrapActionPart.Remove remove(Trap trap) {
        if (trap != null) {
            return new TrapActionPart.Remove(trap);
        }
        return null;
    }

    public static TrapActionPart.Place place(Trap trap) {
        if (trap != null && !EditTrapComp.areEqual(Dungeon.level.traps.get(trap.pos), trap))
            return new TrapActionPart.Place(trap);
        return null;
    }

    public static TrapActionPart.Place place(Trap trap, int cell) {
        if (trap != null && !EditTrapComp.areEqual(Dungeon.level.traps.get(cell), trap)) {
            trap.pos = cell;
            return new TrapActionPart.Place(trap);
        }
        return null;
    }

    private static final String TRAP = "trap";
    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRAP,trap);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        trap = (Trap) bundle.get(TRAP);
    }
}