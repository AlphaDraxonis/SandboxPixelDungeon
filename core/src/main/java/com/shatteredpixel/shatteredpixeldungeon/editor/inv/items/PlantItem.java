package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditPlantComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.PlantActionPart;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;

public class PlantItem extends EditorItem<Plant> {

    private static final TextureFilm TEXTURE_FILM = new TextureFilm(Assets.Environment.TERRAIN_FEATURES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);


    public PlantItem() {
    }

    public PlantItem(Plant plant) {
        this.obj = plant;
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditPlantComp(this);
    }

    @Override
    public String name() {
        return getObject().name();
    }

    @Override
    public Image getSprite() {
        return getObject().getSprite();
    }

    @Override
    public void setObject(Plant obj) {
        Plant copy = obj.getCopy();
        copy.pos = -1;
        super.setObject(copy);
    }

    @Override
    public void place(int cell) {
        Plant place = getObject().getCopy();
        Plant remove = Dungeon.level.plants.get(cell);

        if (!invalidPlacement(cell) && !EditPlantComp.areEqual(remove, place)) {
            Undo.addActionPart(remove(remove));
            Undo.addActionPart(place(place, cell));
        }
    }

    public static boolean invalidPlacement(int cell) {
        return !Dungeon.level.isPassable(cell) || !Dungeon.level.insideMap(cell);
//        return level.map[cell] == Terrain.EMPTY || TileItem.isGrassTerrainCell(level.map[cell]);
    }

    public static PlantActionPart remove(int cell) {
        return remove(Dungeon.level.plants.get(cell));
    }

    public static PlantActionPart.Remove remove(Plant plant) {
        if (plant != null) {
            return new PlantActionPart.Remove(plant);
        }
        return null;
    }

    public static PlantActionPart.Place place(Plant plant, int cell) {
        if (plant != null) {
            plant.pos = cell;
            return new PlantActionPart.Place(plant);
        }
        return null;
    }

    private static final String PLANT = "plant";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PLANT, obj);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        obj = (Plant) bundle.get(PLANT);
    }
}