package com.shatteredpixel.shatteredpixeldungeon.editor.overview;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.ItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelSchemeLike;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndNewFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.FoldableComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NotAllowedInLua
public class WndEditItemDistribution<T extends Item> extends Window {

    private static final int GAP = 1;
    private RedButton positive, negative, addLevel;
    private ScrollPane scrollPane;
    private Component content;
    private FoldableComp objectsToDistribute;
    private ItemContainer<T> container;
    private RenderedTextBlock titleLevels;
    private List<LevelComp> levelList;

    private ItemDistribution<T> itemDistribution;

    private List<String> prevLevels;
    private List<T> prevObjsToDistr;
    private List<LevelScheme> selectedLevelSchemes;

    public WndEditItemDistribution(ItemDistribution<T> itemDistribution, String approveText) {
        this.itemDistribution = itemDistribution;
        prevLevels = new ArrayList<>(itemDistribution.getLevels());
        prevObjsToDistr = new ArrayList<>(itemDistribution.getObjectsToDistribute());
        selectedLevelSchemes = new ArrayList<>();

        resize((int) (WindowSize.WIDTH_LARGE.get() * 0.9f), WindowSize.HEIGHT_VERY_SMALL.get());

        content = new Component();

        container = new ItemContainer<T>(itemDistribution.getObjectsToDistribute()) {
            @Override
            protected void onSlotNumChange() {
                if (objectsToDistribute != null) updateLayout();
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return itemDistribution.getPreferredBag();
            }
        };
        objectsToDistribute = new FoldableComp(itemDistribution.getDistributionLabel(), container) {
            @Override
            protected void layoutParent() {
                updateLayout();
            }
        };

        titleLevels = PixelScene.renderTextBlock(Messages.get(this,"in_levels"), 10);
        content.add(titleLevels);

        container.setSize(width, -1);
        content.add(objectsToDistribute);

        levelList = new ArrayList<>(6);

        for (String name : itemDistribution.getLevels()) {
            LevelComp comp = new LevelComp(name);
            content.add(comp);
            levelList.add(comp);
            selectedLevelSchemes.add(Dungeon.customDungeon.getFloor(name));
        }

        addLevel = new RedButton(Messages.get(this,"add_level")) {
            @Override
            protected void onClick() {
                Window w = new WndSelectFloor() {
                    @Override
                    public boolean onSelect(LevelSchemeLike levelScheme) {
                        if(levelScheme instanceof LevelScheme) {
                            addLevel((LevelScheme) levelScheme);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    protected List<LevelSchemeLike> filterLevels(Collection<? extends LevelSchemeLike> levels) {
                        List<LevelSchemeLike> levelsList = super.filterLevels(levels);
                        levelsList.removeAll(selectedLevelSchemes);
                        return levelsList;
                    }
                };
                if (Game.scene() instanceof EditorScene) EditorScene.show(w);
                else Game.scene().addToFront(w);
            }
        };
        content.add(addLevel);

        positive = new RedButton(approveText) {
            @Override
            protected void onClick() {
                hide();
                doAfterPositive();
            }
        };
        add(positive);
        negative = new RedButton(Messages.get(WndNewFloor.class, "cancel_label")) {
            @Override
            protected void onClick() {
                cancel();
            }
        };
        add(negative);

        scrollPane = new ScrollPane(content);
        add(scrollPane);

        updateLayout();

    }

    protected void doAfterPositive() {
    }

    @Override
    public void onBackPressed() {
    }

    public void cancel() {
        itemDistribution.getLevels().clear();
        itemDistribution.getObjectsToDistribute().clear();
        itemDistribution.getLevels().addAll(prevLevels);
        itemDistribution.getObjectsToDistribute().addAll(prevObjsToDistr);
        hide();
    }

    private void updateLayout() {

        titleLevels.maxWidth(width);

        objectsToDistribute.setRect(GAP, 0, width - GAP * 2, -1);
        float posY = objectsToDistribute.bottom() + 7;
        titleLevels.setPos((width - titleLevels.width() - GAP * 2) * 0.5f + GAP, posY);
        posY = titleLevels.bottom() + 3;
        for (LevelComp comp : levelList) {
            comp.setRect(GAP, posY, width - GAP * 2, 13);
            posY = comp.bottom() + 2;
        }
        float addLevelW = (width - GAP * 2) * 3 / 5f;
        addLevel.setRect(GAP + addLevelW / 3f, posY + 2, addLevelW, 14);
        content.setRect(0, 0, width, addLevel.bottom());

        float buttonW = (width - GAP * 3 - 1) / 2f;
        negative.setRect(GAP, height - 18, buttonW, 18);
        positive.setRect(GAP * 2 + buttonW + 1, height - 18, buttonW, 18);
        scrollPane.setRect(0, 0, width, height - 18 - GAP * 2);
    }

    private void addLevel(LevelScheme level) {
        LevelComp comp = new LevelComp(level.getName());
        levelList.add(comp);
        content.add(comp);
        selectedLevelSchemes.add(level);
        itemDistribution.getLevels().add(level.getName());
        updateLayout();
    }

    private void removeLevel(LevelComp levelComp) {
        levelList.remove(levelComp);
        levelComp.destroy();
        selectedLevelSchemes.remove(Dungeon.customDungeon.getFloor(levelComp.name));
        itemDistribution.getLevels().remove(levelComp.name);
        updateLayout();
        scrollPane.scrollToCurrentView();
    }

    private class LevelComp extends ScrollingListPane.ListItem {

        private IconButton remove;
        private final String name;

        public LevelComp(String levelName) {
            super(new Image(), levelName);
            this.name = levelName;
            remove = new IconButton(Icons.get(Icons.CLOSE)) {
                @Override
                protected void onClick() {
                    removeLevel(LevelComp.this);
                }
            };
            add(remove);
        }

        protected void layout() {
            super.layout();
            label.setPos(label.left(), label.top() + 1);
            remove.setRect(x + width - 16, y, 16, 16);
            hotArea.width = width - remove.left() - 2 - x;
        }

        @Override
        protected int getLabelMaxWidth() {
            return super.getLabelMaxWidth() - 16 - 4;
        }
    }
}