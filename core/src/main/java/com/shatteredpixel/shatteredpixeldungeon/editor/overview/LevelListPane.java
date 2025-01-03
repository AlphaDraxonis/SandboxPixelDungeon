package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
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
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.BadgeBanner;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
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
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@NotAllowedInLua
public class LevelListPane extends ScrollPane {

    public final Selector selector;

    public LevelListPane(Selector selector) {
		super(new Content(selector));
        this.selector = selector;
        selector.listPane = this;
	}

    public void updateList() {
        ((Content) content).updateContent();
        layout();
        scrollToCurrentView();
    }

    @Override
    protected void layout() {
        content.setSize(width, 0);
        layout(true);
    }

    public static abstract class Selector {

        private LevelListPane listPane;

		protected abstract void onSelect(LevelSchemeLike levelScheme, ListItem listItem);

        protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
            return new ArrayList<>(levels);
        }

        public boolean onEdit(LevelScheme levelScheme, ListItem listItem) {
            if (listPane == null) {
                return false;
            }

            if (SPDSettings.tutorialOpenRegularLevel() || levelScheme.getType() == CustomLevel.class) {
                EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, listPane));
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
                        EditorScene.show(new WndEditFloorInOverview(levelScheme, listItem, listPane));
                    }
                });
            }
            return true;
        }

    }

    public static class Content extends Component {

        private final Selector selector;

        private Component[] children;

		public Content(Selector selector) {
			this.selector = selector;
		}

		public void updateContent() {
            clear();
            List<LevelSchemeLike> levels = selector.filterLevels(Dungeon.customDungeon.levelSchemes());
            if (levels.isEmpty()) ;//TODO show that its empty
            Collections.sort(levels, (a, b) -> {
                if (a instanceof LevelScheme && b instanceof LevelScheme) return ((LevelScheme) a).compareTo((LevelScheme) b);
                return 0;//there are only LevelSchemes!
            });
            children = new Component[levels.size()];
            int i = 0;
            for (LevelSchemeLike levelScheme : levels) {
                children[i] = new ListItem(levelScheme) {
                    @Override
                    protected void onClick() {
                        selector.onSelect(getLevelScheme(), this);
                    }

                    @Override
                    protected void onRightClick() {
                        if (!onLongClick()) {
                            onClick();
                        }
                    }

                    @Override
                    protected boolean onLongClick() {
                        return getLevelScheme() instanceof LevelScheme && selector.onEdit((LevelScheme) getLevelScheme(), this);
                    }
                };
                add(children[i]);
                i++;
            }
        }

        @Override
        protected void layout() {
            super.layout();

            height = 0;
            height = EditorUtilities.layoutStyledCompsInRectangles(2, width, this, children);
        }

        @Override
        public synchronized void clear() {
            super.clear();
            if (children != null) {
                for (Component c : children) {
                    c.remove();
                    c.destroy();
                }
                children = null;
            }
        }
    }

    public static class ListItem extends StyledButtonWithIconAndText {

        private final LevelSchemeLike levelScheme;
        protected Image foregroundImage;
        protected RenderedTextBlock depthText;
        protected Image depthIcon;

        private ListItem(LevelSchemeLike levelScheme) {
            super(Chrome.Type.GREY_BUTTON_TR, "");
            this.levelScheme = levelScheme;
            text.setHighlighting(false);

            depthText = PixelScene.renderTextBlock(textSize());
            add(depthText);

            updateLevel();
        }

        public LevelSchemeLike getLevelScheme() {
            return levelScheme;
        }

        public void updateLevel() {
            String name;
            int region = LevelScheme.REGION_NONE;
            int depth = -1;
            Level.Feeling feeling = null;
            
            Image newForegroundImg = icon == null
                    ? createLevelForegroundImage(levelScheme)
                    : createLevelForegroundImage(levelScheme, icon.width(), icon.height());

            if (levelScheme instanceof QuestLevels) {
                name = levelScheme.getName();
                region = levelScheme.getRegion();
            } else if (levelScheme == LevelScheme.NO_LEVEL_SCHEME) {
                name = EditorUtilities.getDispayName(Level.NONE);
            } else if (levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME) {
                name = EditorUtilities.getDispayName(Level.SURFACE);
            } else if (levelScheme == LevelScheme.ANY_LEVEL_SCHEME) {
                name = EditorUtilities.getDispayName(Level.ANY);
            } else {
                LevelScheme ls = (LevelScheme) levelScheme;
                name = ls.getName();
                depth = ls.getDepth();
                region = ls.getRegion();
                feeling = ls.getFeeling();
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
                depthIcon = Icons.getWithNoOffset(feeling);
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
    
    public static Image createLevelForegroundImage(LevelSchemeLike levelScheme) {
        return createLevelForegroundImage(levelScheme, ItemSpriteSheet.SIZE, ItemSpriteSheet.SIZE);
    }
    
    public static Image createLevelForegroundImage(LevelSchemeLike levelScheme, float width, float height) {
        Image result = null;
        
        if (levelScheme instanceof QuestLevels) {
            result = ((QuestLevels) levelScheme).createForegroundIcon();
        } else if (levelScheme == LevelScheme.NO_LEVEL_SCHEME) {
            result = Icons.CLOSE.get();
        } else if (levelScheme == LevelScheme.SURFACE_LEVEL_SCHEME) {
            result = BadgeBanner.image(Badges.Badge.HAPPY_END.image);
        } else if (levelScheme == LevelScheme.ANY_LEVEL_SCHEME) {
            result = new ItemSprite();
        } else {
            LevelScheme ls = (LevelScheme) levelScheme;
            Class<? extends Level> type = ls.getType();
            if (type == CustomLevel.class) result = Icons.TALENT.get();
            else if (type == MiningLevel.CrystalMiningLevel.class) result = new TileSprite(CustomLevel.tilesTex(ls.getRegion(), false), Terrain.MINE_CRYSTAL);
            else if (type == MiningLevel.GnollMiningLevel.class) result = new GnollGuardSprite();
            else if (type == MiningLevel.FungiMiningLevel.class) result = new FungalSentrySprite();
            else if (type == LastLevel.class) result = new ItemSprite(ItemSpriteSheet.AMULET);
            else {
                switch (ls.getBoss()) {
                    default:
                    case LevelScheme.REGION_NONE:
                        break;
                    case LevelScheme.REGION_SEWERS:
                        result = new GooSprite();
                        break;
                    case LevelScheme.REGION_PRISON:
                        result = new TenguSprite();
                        break;
                    case LevelScheme.REGION_CAVES:
                        result = new DM300Sprite();
                        break;
                    case LevelScheme.REGION_CITY:
                        result = new KingSprite();
                        break;
                    case LevelScheme.REGION_HALLS:
                        result = new YogSprite();
                        break;
                }
            }
        }
        
        if (result != null) {
            float bgSize = Math.min(width, height);
            result.scale.set(bgSize / Math.min(result.width(), result.height()) * 0.8f);
        }
        return result;
    }
}