package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndEditFloorInOverview;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledButtonWithIconAndText;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalSentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGuardSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GooSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class LevelListPane extends ScrollingListPane {

    @Override
    protected boolean validClick() {
        return false;
    }

    public void updateList() {
        clear();
        List<LevelSchemeLike> levels = filterLevels(Dungeon.customDungeon.levelSchemes());
        if (levels.isEmpty()) ;//TODO show that its empty
        Collections.sort(levels, (a, b) -> {
            if (a instanceof LevelScheme && b instanceof LevelScheme) return ((LevelScheme) a).compareTo((LevelScheme) b);
            return 0;//there are only LevelSchemes!
        });
        for (LevelSchemeLike levelScheme : levels) {
            addItem(new ListItem(levelScheme) {
                @Override
                protected void onClick() {
                    onSelect(getLevelScheme(), this);
                }

                @Override
                protected void onRightClick() {
                    if (!onLongClick()) {
                        onClick();
                    }
                }

                @Override
                protected boolean onLongClick() {
                    return getLevelScheme() instanceof LevelScheme && onEdit((LevelScheme) getLevelScheme(), this);
                }
            });
        }
        scrollToCurrentView();
    }

    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
        return new ArrayList<>(levels);
    }

    protected abstract void onSelect(LevelSchemeLike levelScheme, LevelListPane.ListItem listItem);

    public boolean onEdit(LevelScheme levelScheme, LevelListPane.ListItem listItem) {
        if (SPDSettings.tutorialOpenRegularLevel() || levelScheme.getType() == CustomLevel.class) {
            EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, this));
        } else {
            final long time = System.currentTimeMillis();
            EditorScene.show(new WndOptions(
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_title"),
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_body"),
                    Messages.get(WndSwitchFloor.class, "tutorial_cant_open_regular_close")){
                @Override
                public void hide() {
                    super.hide();
                    if (time + 1100 < System.currentTimeMillis()) SPDSettings.tutorialOpenRegularLevel(true);
                    EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, LevelListPane.this));
                }
            });
        }
        return true;
    }

    @Override
    protected void layout() {
        layout(true);

        content.setSize(width, 0);
        float pos = EditorUtilies.layoutStyledCompsInRectangles(2, width, content, getItems());
        content.setSize(width, pos);
    }

    public static class ListItem extends StyledButtonWithIconAndText {

        private final LevelSchemeLike levelScheme;
        protected Image foregroundImage;
        protected RenderedTextBlock depthText;
        protected Image depthIcon;

        public ListItem(LevelSchemeLike levelScheme) {
            super(Chrome.Type.GREY_BUTTON_TR, "");
            this.levelScheme = levelScheme;
            text.setHighlighting(false);
            updateLevel();
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            depthText = PixelScene.renderTextBlock(textSize());
            add(depthText);
        }

        public LevelSchemeLike getLevelScheme() {
            return levelScheme;
        }

        public void updateLevel() {
            String name;
            int region = LevelScheme.REGION_NONE;
            int depth = -1;
            Image newForegroundImg = null;
            Level.Feeling feeling = null;

            if (levelScheme instanceof QuestLevels) {
                name = ((QuestLevels) levelScheme).getName();
                region = ((QuestLevels) levelScheme).getRegion();
                newForegroundImg = ((QuestLevels) levelScheme).createForegroundIcon();
            } else if (levelScheme == LevelScheme.NO_LEVEL_SCHEME) {
                name = EditorUtilies.getDispayName(Level.NONE);
                newForegroundImg = Icons.CLOSE.get();
            } else if (levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME) {
                name = EditorUtilies.getDispayName(Level.SURFACE);
                newForegroundImg = null;//Clouds tzz
            } else if (levelScheme == LevelScheme.ANY_LEVEL_SCHEME) {
                name = EditorUtilies.getDispayName(Level.ANY);
                newForegroundImg = new ItemSprite();
            } else {
                LevelScheme ls = (LevelScheme) levelScheme;
                name = ls.getName();
                depth = ls.getDepth();
                region = ls.getRegion();
                feeling = ls.getFeeling();
                Class<? extends Level> type = ls.getType();
                if (type == CustomLevel.class) newForegroundImg = Icons.TALENT.get();
                else if (type == MiningLevel.CrystalMiningLevel.class) newForegroundImg = new TileSprite(CustomLevel.tilesTex(region, false), Terrain.MINE_CRYSTAL);
                else if (type == MiningLevel.GnollMiningLevel.class) newForegroundImg = new GnollGuardSprite();
                else if (type == MiningLevel.FungiMiningLevel.class) newForegroundImg = new FungalSentrySprite();
                else {
                    switch (ls.getBoss()) {
                        default:
                        case LevelScheme.REGION_NONE: break;
                        case LevelScheme.REGION_SEWERS:
                            newForegroundImg = new GooSprite();
                            break;
                        case LevelScheme.REGION_PRISON:
                            newForegroundImg = new TenguSprite();
                            break;
                        case LevelScheme.REGION_CAVES:
                            newForegroundImg = new DM300Sprite();
                            break;
                        case LevelScheme.REGION_CITY:
                            newForegroundImg = new KingSprite();
                            break;
                        case LevelScheme.REGION_HALLS:
                            newForegroundImg = new YogSprite();
                            break;
                    }
                }
            }

            if (newForegroundImg != null) {
                float bgSize = icon == null ? ItemSpriteSheet.SIZE : Math.min(icon.width(), icon.height());
                newForegroundImg.scale.set(bgSize / Math.min(newForegroundImg.width(), newForegroundImg.height()) * 0.8f);
            }

            text.text(name);

            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            if (region > LevelScheme.REGION_NONE) {
                add(icon = new TileSprite(CustomLevel.tilesTex(region, false), Terrain.WALL));
            } else if (newForegroundImg != null) {//use foregroundImg as icon so it can be layouted by the superclass
                add(icon = newForegroundImg);
                newForegroundImg = null;
            }

            if (foregroundImage != null) {
                remove(foregroundImage);
                foregroundImage.destroy();
                foregroundImage = null;
            }

            if (newForegroundImg != null) {
                add(foregroundImage = newForegroundImg);
            }

            if (depthIcon != null) {
                remove(depthIcon);
                depthIcon.destroy();
            }

            if (depth == -1) depthText.visible = false;
            else {
                depthText.text(Integer.toString(depth));
                depthIcon = Icons.get(feeling);
                depthIcon.scale.set(1.5f);
                add(depthIcon);
                bringToFront(depthText);
            }

            layout();
        }

        @Override
        protected void layout() {
            super.layout();

            if (foregroundImage != null && icon != null) {
                foregroundImage.x = icon.x + (icon.width() - foregroundImage.width()) * 0.5f;
                foregroundImage.y = icon.y + (icon.height() - foregroundImage.height()) * 0.5f;
                PixelScene.align(foregroundImage);
            }

            if (depthIcon != null) {
                depthIcon.x = x + 2;
                depthIcon.y = y + 2;
                depthText.setPos(depthIcon.x + depthIcon.width(), depthIcon.y + (depthIcon.height() - depthText.height()) * 0.5f);
                PixelScene.align(depthIcon);
                PixelScene.align(depthText);
            }
        }
    }
}