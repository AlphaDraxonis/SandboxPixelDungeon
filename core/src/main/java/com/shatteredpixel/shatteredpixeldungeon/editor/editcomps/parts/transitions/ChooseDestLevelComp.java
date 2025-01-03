package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.LevelListPane;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.watabou.noosa.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChooseDestLevelComp extends StyledButton {
    
    protected RenderedTextBlock valueText;
    protected Image foregroundImage;
    
    private LevelSchemeLike selectedObject;

    public ChooseDestLevelComp(String label) {
        super(Chrome.Type.GREY_BUTTON_TR, label);
        
        multiline = true;
        
        valueText = PixelScene.renderTextBlock(textSize() + 1);
        add(valueText);
    }

    @Override
    protected void onClick() {
        DungeonScene.show( new WndSelectFloor() {
            @Override
            public boolean onSelect(LevelSchemeLike levelScheme) {
                selectObject(levelScheme);
                return true;
            }

            @Override
            protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                return ChooseDestLevelComp.this.filterLevels(levels);
            }
        } );
    }
    
    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
        ArrayList<LevelSchemeLike> ret = new ArrayList<>(levels);
        ret.add(0, LevelScheme.NO_LEVEL_SCHEME);
        return ret;
    }
    
    public LevelSchemeLike getObject() {
        return selectedObject;
    }
    
    public void selectObject(String levelName) {
        selectObject(Dungeon.customDungeon.getFloor(levelName));
    }
    
    public void selectObject(LevelSchemeLike levelScheme) {
        this.selectedObject = levelScheme;
        
        Image newForegroundImg;
        
        if (levelScheme == null) {
            valueText.text("");
            
            newForegroundImg = null;
            
            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            
        } else {
            valueText.text(EditorUtilities.getDispayName(levelScheme.getName()));
            int region = levelScheme.getRegion();
            
            newForegroundImg = LevelListPane.createLevelForegroundImage(levelScheme);
            
            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            
            if (region > LevelScheme.REGION_NONE) {
                add(icon = new TileSprite(CustomLevel.tilesTex(region, false), Terrain.WALL));
            } else if (newForegroundImg != null) {
                add(icon = newForegroundImg);
                newForegroundImg = null;
            }
        }
        
        
        if (foregroundImage != null) {
            remove(foregroundImage);
            foregroundImage.destroy();
            foregroundImage = null;
        }
        
        if (newForegroundImg != null) {
            add(foregroundImage = newForegroundImg);
        }
    }
    
    
    @Override
    protected void layout() {
        
        height = Math.max(getMinimumHeight(width()), height());
        
        super.layout();
        
        float contentHeight = height();
        
        float valueHeight = (icon == null ? valueText.height() : Math.max(valueText.height(), icon.height())) + 3;
        
        boolean layoutLabel = text != null && !text.text().equals("");
        if (layoutLabel) {
            if (multiline) text.maxWidth((int) width() - bg.marginHor());
            text.setPos(
                    x + (width() + text.width()) / 2f - text.width(),
                    (y + (contentHeight - text.height() - valueHeight) / 2f)
            );
            PixelScene.align(text);
            
        }
        
        float valueWidth = valueText.width() + (icon == null ? 0 : icon.width()+4);
        
        if (valueText != null && !valueText.text().equals("")) {
            if (multiline) valueText.maxWidth((int) (width - bg.marginHor() - (icon == null ? 0 : icon.width()+4)));
            valueText.setPos(
                    x + (width() - valueWidth) / 2f + (icon == null ? 0 : icon.width()+4),
                    y + (layoutLabel
                            ? text.bottom() - y + 2 + (valueHeight - valueText.height()) / 2f
                            : (contentHeight - valueText.height()) / 2f)
            );
            PixelScene.align(valueText);
            
        }
        
        if (icon != null) {
            icon.x = x + (width() - valueWidth) / 2f + 1;
            icon.y = layoutLabel
                    ? text.bottom() + 4
                    : y + (contentHeight - icon.height()) / 2f;
            PixelScene.align(icon);
        }
        
        if (foregroundImage != null && icon != null) {
            foregroundImage.x = icon.x + (icon.width() - foregroundImage.width()) * 0.5f;
            foregroundImage.y = icon.y + (icon.height() - foregroundImage.height()) * 0.5f;
            PixelScene.align(foregroundImage);
        }
        
        if (leftJustify) throw new IllegalArgumentException("leftJustify not supported!");
    }
    
    @Override
    public float getMinimumHeight(float width) {
        if (multiline) {
            text.maxWidth((int) width - bg.marginHor());
            valueText.maxWidth((int) (width - bg.marginHor() - (icon == null ? 0 : icon.width()+4)));
        }
        if (icon == null) {
            return text.height() + 4 + bg.marginVer() + 4 + valueText.height();
        } else {
            return text.height() + 4 + bg.marginVer() + 4 + Math.max( icon.height(), valueText.height() );
        }
    }
}