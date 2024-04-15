package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class BossLevelRetexture extends Component {

    RenderedTextBlock info;

    protected CheckBox none, caves, city;

    @Override
    protected void createChildren(Object... params) {
        info = PixelScene.renderTextBlock(Messages.get(BossLevelRetexture.class, "info"), 6);
        add(info);

        none = new CheckBox(Messages.get(BossLevelRetexture.class, "none")){
            @Override
            public void checked(boolean value) {
                CustomLevel level = EditorScene.getCustomLevel();
                if (value || level.bossGroundVisuals != null) super.checked(value);
                if (level.bossGroundVisuals != null && value) {
                    assignVisuals(null, null);
                }
                super.checked(value);
            }
        };
        add(none);

        caves = new CheckBox(Messages.get(BossLevelRetexture.class, "caves")) {
            @Override
            public void checked(boolean value) {
                CustomLevel level = EditorScene.getCustomLevel();
                if (value || !(level.bossGroundVisuals instanceof CavesBossLevel.ArenaVisuals)) super.checked(value);
                if (!(level.bossGroundVisuals instanceof CavesBossLevel.ArenaVisuals) && value) {
                    assignVisuals(new CavesBossLevel.ArenaVisuals(), new CavesBossLevel.EntranceOverhang());
                }
                super.checked(value);
            }
        };
        add(caves);

        city = new CheckBox(Messages.get(BossLevelRetexture.class, "city")){
            @Override
            public void checked(boolean value) {
                CustomLevel level = EditorScene.getCustomLevel();
                if (value || !(level.bossGroundVisuals instanceof CityBossLevel.CustomGroundVisuals)) super.checked(value);
                if (!(level.bossGroundVisuals instanceof CityBossLevel.CustomGroundVisuals) && value) {
                    assignVisuals(new CityBossLevel.CustomGroundVisuals(), new CityBossLevel.CustomWallVisuals());
                }
                super.checked(value);
            }
        };
        add(city);

        CustomLevel level = EditorScene.getCustomLevel();
        none.checked(level.bossGroundVisuals == null);
        caves.checked(level.bossGroundVisuals instanceof CavesBossLevel.ArenaVisuals);
        city.checked(level.bossGroundVisuals instanceof CityBossLevel.CustomGroundVisuals);
    }

    private void assignVisuals(CustomTilemap groundVisual, CustomTilemap wallsVisual) {
        CustomLevel level = EditorScene.getCustomLevel();
        level.customTiles.remove(level.bossGroundVisuals);
        level.customWalls.remove(level.bossWallsVisuals);
        level.bossGroundVisuals = groundVisual;
        if (groundVisual != null) {
            groundVisual.tileW = level.width();
            groundVisual.tileH = level.height();
            level.customTiles.add(groundVisual);
        }
        level.bossWallsVisuals = wallsVisual;
        if (wallsVisual != null) {
            wallsVisual.tileW = level.width();
            wallsVisual.tileH = level.height();
            level.customWalls.add(wallsVisual);
        }
        none.checked(false);
        caves.checked(false);
        city.checked(false);

        EditorScene.revalidateCustomTiles();
    }

    @Override
    protected void layout() {

        info.maxWidth((int) width);
        info.setPos(x, y);

        none.setRect(x, y + info.bottom() + 4, width, 18);

        caves.setRect(x, y + none.bottom() + 4, width, 18);

        city.setRect(x, y + caves.bottom() + 4, width, 18);

        height = city.bottom() + 1;
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(BossLevelRetexture.class, "title")), 12);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

}