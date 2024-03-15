package com.shatteredpixel.shatteredpixeldungeon.editor.scene;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.utils.SparseArray;

public class LevelColoring extends Group {

    private static LevelColoring floor, wall, water;

    public static LevelColoring getFloor() {
        return floor == null ?
                floor = new LevelColoring(Dungeon.level.levelScheme.floorColor, Dungeon.level.levelScheme.floorAlpha) {
                    @Override
                    protected void updateColor(ColorBlock img, int cell) {
                        if (!Dungeon.level.solid[cell]) super.updateColor(img, cell);
//                        else if (Dungeon.hero != null) {
//                            img.hardlight(Dungeon.level.levelScheme.wallColor);
//                            img.alpha(Dungeon.level.levelScheme.wallAlpha);
//                        }
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

    private static final int RIGHT = 1, LEFT = 2, BOTTOM = 4, TOP = 8, BOTTOM_RIGHT = 16, BOTTOM_LEFT = 32;
    private static final float TILE_SCALE = DungeonTilemap.SIZE / 16f;

    public static LevelColoring getWall(boolean view2d) {
        if (wall != null) return wall;
        wall = new LevelColoring(Dungeon.level.levelScheme.wallColor, Dungeon.level.levelScheme.wallAlpha) {

            @Override
            protected void updateColor(ColorBlock img, int cell) {
                if (CustomDungeon.isEditing() || Dungeon.customDungeon.view2d) {
                    if (Dungeon.level.solid[cell] && DungeonTileSheet.wallStitcheable(Dungeon.level.map[cell])) super.updateColor(img, cell);
                    else img.alpha(0f);
                } else {
                    int mapWidth = Dungeon.level.width();
                    int[] map = Dungeon.level.map;
                    int result = 0;
                    if (DungeonTileSheet.wallStitcheable((cell + 1) % mapWidth != 0 ? map[cell + 1] : -1)) result += RIGHT;//right
                    if (DungeonTileSheet.wallStitcheable(cell % mapWidth != 0 ? map[cell - 1] : -1)) result += LEFT;//left
                    if (DungeonTileSheet.wallStitcheable(cell + mapWidth < map.length ? map[cell + mapWidth] : -1)) result += BOTTOM;//bottom
                    if (DungeonTileSheet.wallStitcheable(cell - mapWidth >= 0 ? map[cell - mapWidth] : -1)) result += TOP;//top
                    if (DungeonTileSheet.wallStitcheable((cell + 1) % mapWidth != 0 && cell + mapWidth < map.length ? map[cell + 1 + mapWidth] : -1))//bottom right
                        result += BOTTOM_RIGHT;
                    if (DungeonTileSheet.wallStitcheable(cell % mapWidth != 0 && cell + mapWidth < map.length ? map[cell - 1 + mapWidth] : -1))//bottom left
                        result += BOTTOM_LEFT;

                    if ((result & BOTTOM) == 0 && DungeonTileSheet.wallStitcheable(map[cell])) {
                        super.updateColor(img, cell);
                    } else {
                        img.alpha(0f);
                    }

                }
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
        };
        if (!view2d) {
            wall.secondColorLevel = new LevelColoring(Dungeon.level.levelScheme.wallColor, Dungeon.level.levelScheme.wallAlpha) {
                private SparseArray<ColorBlock> altComps1, altComps2, altComps3;

                private int verticalWallWidth, horizontalWallHeight, horizontalDoorWidth;

                @Override
                protected void makeExistent() {
                    if (altComps1 == null) {
                        altComps1 = new SparseArray<>();
                        altComps2 = new SparseArray<>();
                        altComps3 = new SparseArray<>();

                        switch (LevelScheme.getRegion(Dungeon.level)) {
                            default:
                            case LevelScheme.REGION_SEWERS:
                                verticalWallWidth = 5;
                                horizontalWallHeight = 5;
                                horizontalDoorWidth = 2;
                                break;
                            case LevelScheme.REGION_PRISON:
                                verticalWallWidth = 5;
                                horizontalWallHeight = 5;
                                horizontalDoorWidth = 1;
                                break;
                            case LevelScheme.REGION_CAVES:
                                verticalWallWidth = 5;
                                horizontalWallHeight = 4;
                                horizontalDoorWidth = 1;
                                break;
                            case LevelScheme.REGION_CITY:
                                verticalWallWidth = 4;
                                horizontalWallHeight = 4;
                                horizontalDoorWidth = 1;
                                break;
                            case LevelScheme.REGION_HALLS:
                                verticalWallWidth = 4;
                                horizontalWallHeight = 7;
                                horizontalWallHeight = 4;
                                horizontalDoorWidth = 2;
                                break;
                        }
                    }
                    super.makeExistent();
                }

                @Override
                protected void updateColor(ColorBlock img, int cell) {
                    int mapWidth = Dungeon.level.width();
                    int[] map = Dungeon.level.map;

                    img.size(DungeonTilemap.SIZE, DungeonTilemap.SIZE);
                    img.origin.set(0, 0);

                    boolean destroyAlt1 = true, destroyAlt2 = true, destroyAlt3 = true;

                    int result = 0;
                    if (DungeonTileSheet.wallStitcheable((cell + 1) % mapWidth != 0 ? map[cell + 1] : -1)) result += RIGHT;//right
                    if (DungeonTileSheet.wallStitcheable(cell % mapWidth != 0 ? map[cell - 1] : -1)) result += LEFT;//left
                    if (DungeonTileSheet.wallStitcheable(cell + mapWidth < map.length ? map[cell + mapWidth] : -1)) result += BOTTOM;//bottom
                    if (DungeonTileSheet.wallStitcheable(cell - mapWidth >= 0 ? map[cell - mapWidth] : -1)) result += TOP;//top
                    if (DungeonTileSheet.wallStitcheable((cell + 1) % mapWidth != 0 && cell + mapWidth < map.length ? map[cell + 1 + mapWidth] : -1))//bottom right
                        result += BOTTOM_RIGHT;
                    if (DungeonTileSheet.wallStitcheable(cell % mapWidth != 0 && cell + mapWidth < map.length ? map[cell - 1 + mapWidth] : -1))//bottom left
                        result += BOTTOM_LEFT;

                    if ((result & BOTTOM) == 0 && DungeonTileSheet.wallStitcheable(map[cell])) {

                        if (cell + mapWidth < map.length && TileItem.isDoor(map[cell + mapWidth]) && map[cell + mapWidth] != Terrain.OPEN_DOOR) {
                            super.updateColor(img, cell);
                            img.size(img.width(), 7 * TILE_SCALE);
                            ColorBlock alt = altComps1.get(cell);
                            if (alt == null) {
                                alt = initNewColorblock(cell, false);
                                altComps1.put(cell, alt);
                            }
                            super.updateColor(alt, cell);
                            alt.size(5 * TILE_SCALE, 9 * TILE_SCALE);
                            alt.origin.set(0, img.height());
                            destroyAlt1 = false;

                            alt = altComps2.get(cell);
                            if (alt == null) {
                                alt = initNewColorblock(cell, false);
                                altComps2.put(cell, alt);
                            }
                            super.updateColor(alt, cell);
                            alt.size(5 * TILE_SCALE, 9 * TILE_SCALE);
                            alt.origin.set((16 - 5) * TILE_SCALE, img.height());
                            destroyAlt2 = false;
                        } else img.alpha(0f);


                    } else if (((result & BOTTOM) == BOTTOM || (result & TOP) == TOP) && DungeonTileSheet.wallStitcheable(map[cell])) {
                        super.updateColor(img, cell);

                        if ((result & BOTTOM_RIGHT) == 0 || (result & BOTTOM_LEFT) == 0) {
                            img.size(verticalWallWidth * TILE_SCALE, img.height());
                        } else {
                            img.size(verticalWallWidth * TILE_SCALE, (8 + horizontalWallHeight) * TILE_SCALE);
                        }
                        if ((result & LEFT) == LEFT && (result & BOTTOM_LEFT) == BOTTOM_LEFT) {
                            img.origin.set((16 - verticalWallWidth) * TILE_SCALE, 0);
                        } else if ((result & RIGHT) != RIGHT || (result & BOTTOM_RIGHT) != BOTTOM_RIGHT) {
                            img.size(verticalWallWidth * TILE_SCALE, (result & BOTTOM_LEFT) == 0 ? 16 * TILE_SCALE : (8 + horizontalWallHeight) * TILE_SCALE);
                            ColorBlock alt = altComps1.get(cell);
                            if (alt == null) {
                                alt = initNewColorblock(cell, false);
                                altComps1.put(cell, alt);
                            }
                            super.updateColor(alt, cell);
                            alt.size(img.width(), (result & BOTTOM_RIGHT) == 0 ? 16 * TILE_SCALE : (8 + horizontalWallHeight) * TILE_SCALE);
                            alt.origin.set((16 - verticalWallWidth) * TILE_SCALE, 0);
                            destroyAlt1 = false;
                        }


                    } else if (((result & BOTTOM) == BOTTOM) && !DungeonTileSheet.wallStitcheable(map[cell])) {
                        super.updateColor(img, cell);

                        img.size(img.width(), horizontalWallHeight * TILE_SCALE);
                        img.origin.set(0, 8 * TILE_SCALE);

                        if ((result & BOTTOM_RIGHT) == 0 || (result & BOTTOM_LEFT) == 0) {
                            ColorBlock alt = altComps1.get(cell);
                            if (alt == null) {
                                alt = initNewColorblock(cell, false);
                                altComps1.put(cell, alt);
                            }
                            super.updateColor(alt, cell);
                            alt.size(verticalWallWidth * TILE_SCALE, horizontalWallHeight * TILE_SCALE);
                            alt.origin.set((result & BOTTOM_LEFT) == 0 ? 0 : (16 - verticalWallWidth) * TILE_SCALE, (8 + horizontalWallHeight) * TILE_SCALE);
                            destroyAlt1 = false;

                            if ((result & BOTTOM_RIGHT) == 0 && (result & BOTTOM_LEFT) == 0) {
                                alt = altComps2.get(cell);
                                if (alt == null) {
                                    alt = initNewColorblock(cell, false);
                                    altComps2.put(cell, alt);
                                }
                                super.updateColor(alt, cell);
                                alt.size(verticalWallWidth * TILE_SCALE, horizontalWallHeight * TILE_SCALE);
                                alt.origin.set((16 - verticalWallWidth) * TILE_SCALE, (8 + horizontalWallHeight) * TILE_SCALE);
                                destroyAlt2 = false;
                            }

                            if (TileItem.isDoor(map[cell])) {
                                alt = altComps3.get(cell);
                                if (alt == null) {
                                    alt = initNewColorblock(cell, false);
                                    altComps3.put(cell, alt);
                                }
                                super.updateColor(alt, cell);
                                alt.size(6 * TILE_SCALE, 3 * TILE_SCALE);
                                alt.origin.set(5 * TILE_SCALE, 5 * TILE_SCALE);
                                destroyAlt3 = false;
                            }
                        }

                    } else if (TileItem.isDoor(map[cell]) && (result & TOP) == 0) {
                        super.updateColor(img, cell);

                        img.size(horizontalDoorWidth * TILE_SCALE, (8 + horizontalWallHeight) * TILE_SCALE);
                        ColorBlock alt = altComps1.get(cell);
                        if (alt == null) {
                            alt = initNewColorblock(cell, false);
                            altComps1.put(cell, alt);
                        }
                        super.updateColor(alt, cell);
                        alt.size(img.width(), img.height());
                        alt.origin.set(16 * TILE_SCALE - img.width(), img.origin.y);
                        destroyAlt1 = false;

                    } else if (cell + mapWidth < map.length && TileItem.isDoor(map[cell + mapWidth])) {
                        super.updateColor(img, cell);

                        img.size(horizontalDoorWidth * TILE_SCALE, 4 * TILE_SCALE);
                        img.origin.set(0, (8 + horizontalWallHeight) * TILE_SCALE);
                        ColorBlock alt = altComps1.get(cell);
                        if (alt == null) {
                            alt = initNewColorblock(cell, false);
                            altComps1.put(cell, alt);
                        }
                        super.updateColor(alt, cell);
                        alt.size(img.width(), img.height());
                        alt.origin.set((8 + horizontalWallHeight) * TILE_SCALE - img.width(), img.origin.y);
                        destroyAlt1 = false;

                    } else {
                        img.alpha(0f);
                    }

                    if (destroyAlt1) {
                        makeNonExistent(cell, altComps1);
                    }
                    if (destroyAlt2) {
                        makeNonExistent(cell, altComps2);
                    }
                    if (destroyAlt3) {
                        makeNonExistent(cell, altComps3);
                    }
                }

                @Override
                protected void placeComp(ColorBlock img, int cell, float x, float y) {
                    super.placeComp(img, cell, x, y);
                    ColorBlock alt = altComps1.get(cell);
                    if (alt != null) super.placeComp(alt, cell, x, y);
                    alt = altComps2.get(cell);
                    if (alt != null) super.placeComp(alt, cell, x, y);
                    alt = altComps3.get(cell);
                    if (alt != null) super.placeComp(alt, cell, x, y);
                }

                @Override
                public ColorBlock updateMapCell(int cell) {
                    ColorBlock comp = super.updateMapCell(cell);
                    if (comp != null) {
                        placeComp(comp, cell,
                                PixelScene.align(Camera.main, (cell % Dungeon.level.width()) * DungeonTilemap.SIZE),
                                PixelScene.align(Camera.main, (cell / Dungeon.level.width()) * DungeonTilemap.SIZE));
                    }
                    return comp;
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
            };
        }
        return wall;
    }

    public static LevelColoring getWater() {
        return water == null ?
                water = new LevelColoring(Dungeon.level.levelScheme.waterColor, Dungeon.level.levelScheme.waterAlpha) {
//                    @Override
//                    protected void updateColor(ColorBlock img, int cell) {
//                        if (Dungeon.level.water[cell]) super.updateColor(img, cell);
//                        else img.alpha(0f);
//                    }

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

    private LevelColoring secondColorLevel;

    private LevelColoring(int color, float alpha) {
        this.color = color;
        this.alpha = alpha;

        comps = new SparseArray<>();

        if (alpha > 0f) makeExistent();
    }

    public LevelColoring getSecondColorLevel() {
        return secondColorLevel;
    }

    protected void makeExistent() {
        if (comps.isEmpty()) {
            int posX = 0, posY = 0;
            for (int i = 0; i < Dungeon.level.length(); i++) {
                ColorBlock img = initNewColorblock(i, true);
                comps.put(i, img);

                placeComp(img, i,
                        PixelScene.align(Camera.main, posX * DungeonTilemap.SIZE),
                        PixelScene.align(Camera.main, posY * DungeonTilemap.SIZE));

                posX++;
                if (posX == Dungeon.level.width()) {
                    posX = 0;
                    posY++;
                }
            }
        }
    }

    protected void placeComp(ColorBlock img, int cell, float x, float y) {
        img.x = x + img.origin.x;
        img.y = y + img.origin.y;
        img.origin.set(0, 0);
    }

    protected ColorBlock initNewColorblock(int cell, boolean updateColor) {
        ColorBlock img = new ColorBlock(DungeonTilemap.SIZE, DungeonTilemap.SIZE, 0xFFFFFFFF);
        if (updateColor) updateColor(img, cell);
        add(img);
        return img;
    }

    protected void makeNonExistent(int cell, SparseArray<ColorBlock> comps) {
        ColorBlock c = comps.get(cell);
        if (c != null) {
            c.killAndErase();
            comps.remove(cell);
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
        } else if (alpha <= 0f) {
            for (int i = 0; i < Dungeon.level.length(); i++) {
                makeNonExistent(i, comps);
            }
        }
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

    public ColorBlock updateMapCell(int cell) {
        ColorBlock comp = comps.get(cell);
        if (comp != null) updateColor(comp, cell);
        return comp;
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