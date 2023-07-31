package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.RectF;

public class PlantItem extends EditorItem {

    private static final TextureFilm TEXTURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

    private final Plant plant;


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
        if (validPlacement(cell, EditorScene.customLevel()))
            Undo.addActionPart(place(plant().getCopy(), cell));
    }

    @Override
    public String name() {
        return plant().name();
    }

    public Plant plant() {
        return plant;
    }

    public static boolean validPlacement(int cell, CustomLevel level) {
        return level.passable[cell];
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
}