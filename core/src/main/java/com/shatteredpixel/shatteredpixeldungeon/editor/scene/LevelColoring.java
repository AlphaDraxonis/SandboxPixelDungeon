package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.utils.PointF;
import com.watabou.utils.SparseArray;

public class LevelColoring extends Group {

    private static LevelColoring floor, wall, water;

    public static LevelColoring getFloor() {
        return floor == null ?
                floor = new LevelColoring(Dungeon.level.levelScheme.floorColor, Dungeon.level.levelScheme.floorAlpha) {
                    @Override
                    protected void updateColor(ColorBlock img, int cell) {
                        if (!Dungeon.level.solid[cell]) super.updateColor(img, cell);
                        else img.alpha(0f);
                    }

                    @Override
                    public void setColor(int color) {
                        Dungeon.level.levelScheme.floorColor = color;
                        super.setColor(color);
                    }

                    @Override
                    public void setAlpha(float alpha) {
                        Dungeon.level.levelScheme.floorAlpha = alpha;
                        super.setAlpha(alpha);
                    }
                } : floor;
    }

    public static LevelColoring getWall() {
        return wall == null ?
                wall = new LevelColoring(Dungeon.level.levelScheme.wallColor, Dungeon.level.levelScheme.wallAlpha) {
                    @Override
                    protected void updateColor(ColorBlock img, int cell) {
                        if (Dungeon.level.solid[cell]) super.updateColor(img, cell);
                        else img.alpha(0f);
                    }

                    @Override
                    public void setColor(int color) {
                        Dungeon.level.levelScheme.wallColor = color;
                        super.setColor(color);
                    }

                    @Override
                    public void setAlpha(float alpha) {
                        Dungeon.level.levelScheme.wallAlpha = alpha;
                        super.setAlpha(alpha);
                    }
                } : wall;
    }

    public static LevelColoring getWater() {
        return water == null ?
                water = new LevelColoring(Dungeon.level.levelScheme.waterColor, Dungeon.level.levelScheme.waterAlpha) {
                    @Override
                    protected void updateColor(ColorBlock img, int cell) {
                        if (Dungeon.level.water[cell]) super.updateColor(img, cell);
                        else img.alpha(0f);
                    }

                    @Override
                    public void setColor(int color) {
                        Dungeon.level.levelScheme.waterColor = color;
                        super.setColor(color);
                    }

                    @Override
                    public void setAlpha(float alpha) {
                        Dungeon.level.levelScheme.waterAlpha = alpha;
                        super.setAlpha(alpha);
                    }
                } : water;
    }

    private int color;//0 to 255 for each rgba
    private float alpha;//small value -> more transparent
    private final SparseArray<ColorBlock> comps;

    private LevelColoring(int color, float alpha) {
        this.color = color;
        this.alpha = alpha;

        comps = new SparseArray<>();

        if (alpha > 0f) makeExistent();
    }

    private void makeExistent() {
        if (comps.isEmpty()) {
            int posX = 0, posY = 0;
            for (int i = 0; i < Dungeon.level.length(); i++) {
                ColorBlock img = new ColorBlock(DungeonTilemap.SIZE, DungeonTilemap.SIZE, 0xFFFFFFFF);
                updateColor(img, i);
                comps.put(i, img);
                add(img);

                PointF pos = new PointF(
                        PixelScene.align(Camera.main, posX * DungeonTilemap.SIZE),
                        PixelScene.align(Camera.main, posY * DungeonTilemap.SIZE));
                img.point(pos);

                posX++;
                if (posX == Dungeon.level.width()) {
                    posX = 0;
                    posY++;
                }
            }
        }
    }

    private void makeNonExistent() {
        for (int i = 0; i < Dungeon.level.length(); i++) {
            ColorBlock c = comps.get(i);
            if (c != null) {
                c.remove();
                c.destroy();
                c.killAndErase();
                comps.remove(i);
            }
        }
    }

    protected void updateColor(ColorBlock img, int cell) {
        img.hardlight(color);
        img.alpha(alpha);
    }

    public int getColor() {
        return color;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setColor(int color) {
        this.color = color;
        updateMap();
    }

    public void setAlpha(float alpha) {
        float oldAlpha = this.alpha;
        this.alpha = alpha;
        if (oldAlpha <= 0f) {
            if (alpha > 0f) makeExistent();
        } else if (alpha <= 0f) makeNonExistent();
        updateMap();
    }

    public static void allUpdateMapCell(int cell) {
        if (floor != null) floor.updateMapCell(cell);
        if (wall != null) wall.updateMapCell(cell);
        if (water != null) water.updateMapCell(cell);
    }

    public static void allUpdateMap() {
        for (int cell = 0; cell < Dungeon.level.length(); cell++) {
            allUpdateMapCell(cell);
        }
    }

    public void updateMapCell(int cell) {
        ColorBlock comp = comps.get(cell);
        if (comp != null) updateColor(comp, cell);
    }

    public void updateMap() {
        if (alpha > 0f) {
            for (int cell = 0; cell < Dungeon.level.length(); cell++) {
                updateMapCell(cell);
            }
        }
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        if (this == floor) floor = null;
        else if (this == wall) wall = null;
        else if (this == water) water = null;
    }
}