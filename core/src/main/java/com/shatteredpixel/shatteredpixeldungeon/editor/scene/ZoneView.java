package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.SparseArray;

public class ZoneView extends Group {

    private static final TextureFilm textureFilm = new TextureFilm(Assets.Environment.ZONES, DungeonTilemap.SIZE, DungeonTilemap.SIZE);

    private SparseArray<ZoneComp> comps;

    public ZoneView() {
        comps = new SparseArray<>();
    }

    private class ZoneComp extends Image {

        private final int cell;

        public ZoneComp(int cell) {
            super(Assets.Environment.ZONES);

            this.cell = cell;

            PointF pos = new PointF(
                    PixelScene.align(Camera.main, ((cell % Dungeon.level.width())) * DungeonTilemap.SIZE),
                    PixelScene.align(Camera.main, ((cell / Dungeon.level.width())) * DungeonTilemap.SIZE));
            point(pos);
        }

        private void updateImage(int cell, Zone zone, int levelLength, boolean updateNeighbours) {
            frame(textureFilm.get(ZoneView.this.updateImage(cell, zone, levelLength, updateNeighbours)));
            resetColor();
        }

        @Override
        public void resetColor() {
            super.resetColor();
            if (Dungeon.level.zone[cell] != null) hardlight(Dungeon.level.zone[cell].getColor());
            else if (cell != 0) {
                remove();
                killAndErase();
                destroy();
                comps.remove(cell);
            }
        }
    }

    public void updateZoneColors() {
        for (ZoneComp comp : comps.valueList()) {
            comp.resetColor();
        }
    }

    private int updateImage(int cell, Zone zone, int levelLength, boolean updateNeighbours) {
        int t, r, b, l;
        if ((t = cell + PathFinder.CIRCLE4[0]) >= levelLength || t < 0 || Dungeon.level.zone[t] != zone) t = -1;
        if ((r = cell + PathFinder.CIRCLE4[1]) >= levelLength || r < 0 || Dungeon.level.zone[r] != zone) r = -1;
        if ((b = cell + PathFinder.CIRCLE4[2]) >= levelLength || b < 0 || Dungeon.level.zone[b] != zone) b = -1;
        if ((l = cell + PathFinder.CIRCLE4[3]) >= levelLength || l < 0 || Dungeon.level.zone[l] != zone) l = -1;

        int img = stitchZoneTile(t >= 0, r >= 0, b >= 0, l >= 0);

        if (img > 0 && updateNeighbours) {
            ZoneComp comp;
            if (t >= 0 && (comp = comps.get(t)) != null) comp.updateImage(t, zone, levelLength, false);
            if (r >= 0 && (comp = comps.get(r)) != null) comp.updateImage(r, zone, levelLength, false);
            if (b >= 0 && (comp = comps.get(b)) != null) comp.updateImage(b, zone, levelLength, false);
            if (l >= 0 && (comp = comps.get(l)) != null) comp.updateImage(l, zone, levelLength, false);
        }

        return img;
    }

    public void updateCell(int cell, Zone zoneBefore) {
        Zone zone = Dungeon.level.zone[cell];
        if (zone == zoneBefore) return;

        if (zone != null) {
            ZoneComp zoneComp = comps.get(cell);
            if (zoneComp == null) {
                zoneComp = new ZoneComp(cell);
                comps.put(cell, zoneComp);
                add(zoneComp);
            }
            zoneComp.updateImage(cell, zone, Dungeon.level.map.length, true);
            if (zoneBefore != null) updateImage(cell, zoneBefore, Dungeon.level.map.length, true);
        } else {
            ZoneComp destroy = comps.remove(cell);
            if (destroy != null) {
                destroy.remove();
                destroy.killAndErase();
                destroy.destroy();
                updateImage(cell, zoneBefore, Dungeon.level.map.length, true);
            }
        }
    }

    //+1 for zone above, +2 for zone right, +4 for zone below, +8 for zone left.
    public static int stitchZoneTile(boolean top, boolean right, boolean bottom, boolean left) {
        int result = 0;
        if (top) result += 1;
        if (right) result += 2;
        if (bottom) result += 4;
        if (left) result += 8;
        return result;
    }
}