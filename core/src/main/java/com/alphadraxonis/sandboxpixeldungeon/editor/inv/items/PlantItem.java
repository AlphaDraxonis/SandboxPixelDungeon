package com.alphadraxonis.sandboxpixeldungeon.editor.inv.items;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.DefaultEditComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditPlantComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.DefaultListItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.EditorInventoryWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.Undo;
import com.alphadraxonis.sandboxpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.tiles.DungeonTilemap;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.RectF;

public class PlantItem extends EditorItem {

    private static final TextureFilm TEXTURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

    private Plant plant;


    public PlantItem() {
    }

    public PlantItem(Plant plant) {
        this.plant = plant;
    }

    private static int imgCode(Plant plant) {
        if (plant != null) return plant.image + 7 * 16;
        else return -1;
    }

    @Override
    public Image getSprite() {
        return getPlantImage(imgCode(plant()));
    }

    public static Image getPlantImage(int imgCode) {
        RectF frame = TEXTURE_FILM.get(imgCode);
        if (frame != null) {
            Image img = new Image(Assets.Environment.TERRAIN_FEATURES);
            img.frame(frame);
            return img;
        }
        return new Image();
    }

    public static Image getPlantImage(Plant plant) {
        return getPlantImage(imgCode(plant));
    }

    public static String createTitle(Plant plant) {
        return Messages.titleCase(plant.name());
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, createTitle(plant()), getSprite()) {
            @Override
            public void onUpdate() {
                if (item == null || ((PlantItem) item).plant() == null) return;
                Plant t = ((PlantItem) item).plant();
                label.text(PlantItem.createTitle(t));

                if (icon != null) remove(icon);
                icon = PlantItem.getPlantImage(t);
                addToBack(icon);
                remove(bg);
                addToBack(bg);

                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditPlantComp(this);
    }

    @Override
    public void place(int cell) {
        if (!invalidPlacement(cell, EditorScene.customLevel()))
            Undo.addActionPart(place(plant().getCopy(), cell));
    }

    @Override
    public String name() {
        return plant().name();
    }

    public Plant plant() {
        return plant;
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
        return !level.passable[cell] || !level.insideMap(cell);
//        return level.map[cell] == Terrain.EMPTY || TileItem.isGrassTerrainCell(level.map[cell]);
    }

    public static PlantActionPart remove(int cell, CustomLevel level) {
        return remove(level.plants.get(cell));
    }

    public static PlantActionPart.Remove remove(Plant plant) {
        if (plant != null) {
            return new PlantActionPart.Remove(plant);
        }
        return null;
    }

    public static PlantActionPart.Place place(Plant plant) {
        if (plant != null && !EditPlantComp.areEqual(Dungeon.level.plants.get(plant.pos), plant))
            return new PlantActionPart.Place(plant);
        return null;
    }

    public static PlantActionPart.Place place(Plant plant, int cell) {
        if (plant != null && !EditPlantComp.areEqual(Dungeon.level.plants.get(cell), plant)) {
            plant.pos = cell;
            return new PlantActionPart.Place(plant);
        }
        return null;
    }

    private static final String PLANT = "plant";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PLANT, plant);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        plant = (Plant) bundle.get(PLANT);
    }
}