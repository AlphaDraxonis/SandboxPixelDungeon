package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

public class CategoryScroller extends AbstractCategoryScroller<RedButton> {

    private static final int MIN_WIDTH_PER_BUTTON = 20;
    
    public CategoryScroller(Category[] categories, EditorInventoryWindow window) {
        this(categories, window, categories.length > 1);
    }
    
    public CategoryScroller(Category[] categories, EditorInventoryWindow window, boolean createCategoryComps) {
        super(categories, new RedButton[categories.length], window, createCategoryComps);
    }

    @Override
    protected ScrollPane createSp() {
        return new ScrollingListPane();
    }

    @Override
    protected RedButton createCategoryComp(int index, Category category) {
        RedButton result = new RedButton("") {
            @Override
            protected void onClick() {
                if (selectedVisIndex != index) {
                    selectCategory(index);
                }
            }

            @Override
            protected String hoverText() {
                return category.getName();
            }
        };
        Image icon = category.getImage();
        if (icon != null) result.icon(icon);
        else result.text("-");
        add(result);
        return result;
    }

    @Override
    protected boolean processKey() {
        if (super.processKey()) {
            return true;
        }

        int numRows = numVisibleRows();
        if (numRows > 1 && (curAction == SPDAction.S || curAction == SPDAction.N)) {

            float ownLeft = categoryComps[indicesWithNonEmptyCats[selectedVisIndex]].left();
            float ownRight = categoryComps[indicesWithNonEmptyCats[selectedVisIndex]].right();

            int compsPerRow = (int) Math.ceil((float) indicesWithNonEmptyCats.length / numRows);
            int currentRow = (int) Math.ceil((float) (selectedVisIndex + 1) / compsPerRow);

            int indexInTargetRow;
            Component c;

            if (curAction == SPDAction.S) {
                //suche den ersten btn der nächsten Zeile, dessen right() >= eigenes right() ist

                indexInTargetRow = currentRow * compsPerRow;//first index
                if (indexInTargetRow >= indicesWithNonEmptyCats.length) {
                    //go from last row to first row
                    indexInTargetRow = compsPerRow;
                    do {
                        indexInTargetRow--;
                        c = categoryComps[indicesWithNonEmptyCats[indexInTargetRow]];
                    } while (c.right() > ownRight);
                    if (c.left() < ownLeft) indexInTargetRow++;
                } else {
                    indexInTargetRow--;
                    do {
                        indexInTargetRow++;
                        c = categoryComps[indicesWithNonEmptyCats[indexInTargetRow]];
                    } while (c.right() < ownRight);
                    if (c.left() > ownLeft) indexInTargetRow--;
                }
                selectVisibleCategory(indexInTargetRow);
                return true;
            }
            if (curAction == SPDAction.N) {

                indexInTargetRow = (currentRow - 2) * compsPerRow;//first index

                if (indexInTargetRow < 0) {
                    //go from first row to last row
                    indexInTargetRow = indicesWithNonEmptyCats.length;
                    do {
                        indexInTargetRow--;
                        c = categoryComps[indicesWithNonEmptyCats[indexInTargetRow]];
                    } while (c.left() > ownLeft);
                    if (c.right() < ownRight) indexInTargetRow++;
                } else {
                    indexInTargetRow--;
                    do {
                        indexInTargetRow++;
                        c = categoryComps[indicesWithNonEmptyCats[indexInTargetRow]];
                    } while (c.left() < ownLeft);
                    if (c.right() > ownRight) indexInTargetRow--;
                }
                selectVisibleCategory(indexInTargetRow);
                return true;
            }
		}
        return false;
    }

    @Override
    protected void layout() {

        if (categoryComps != null) {
            int numRows = numVisibleRows();
            int compsPerRow = (int) Math.ceil((float) indicesWithNonEmptyCats.length / numRows);

            float compWidth = width() / compsPerRow;
            float y = this.y;
            float x = this.x;
            int i = 0;
            int indexRow = 0;
			while (i < indicesWithNonEmptyCats.length) {
				int catIndex = indicesWithNonEmptyCats[i];
				categoryComps[catIndex].setRect(x, y, compWidth, ITEM_HEIGHT);
				PixelScene.align(categoryComps[catIndex]);
                i++;
				if (i % compsPerRow == 0) {
                    indexRow++;
					y += ITEM_HEIGHT;
					x = this.x;
                    if (indexRow == numRows-1) {
                        //last row
                        compWidth = width() / (indicesWithNonEmptyCats.length - i);
                    }
				} else {
                    x += compWidth;
                }
			}
        }

        selectVisibleCategory(selectedVisIndex);
    }

    private int numVisibleRows() {
        return (int) Math.ceil(indicesWithNonEmptyCats.length * MIN_WIDTH_PER_BUTTON / width());
    }

    @Override
    protected void doSelectCategory(int selectedVisIndex, int selectedCatIndex) {

        if (sp instanceof ScrollingListPane)
            sp.clear();

        if (categoryComps != null) {
            for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                int catIndex = indicesWithNonEmptyCats[i];
                if (catIndex == selectedCatIndex) categoryComps[catIndex].icon().color(Window.TITLE_COLOR);
                else categoryComps[catIndex].icon().resetColor();
            }
        }

        sp.scrollTo(0, 0);


        updateList(selectedCatIndex);
        sp.givePointerPriority();

        float bottom = categoryComps == null ? y : categoryComps[indicesWithNonEmptyCats[indicesWithNonEmptyCats.length - 1]].bottom() + 1;
        sp.setRect(x, bottom, width,height - bottom + y);
    }

    @Override
    public void updateItems() {
        for (Component i : ((ScrollingListPane) sp).getItems()) {
            if (i instanceof AdvancedListPaneItem) ((AdvancedListPaneItem) i).onUpdate();
        }
    }

    protected void updateList(int selectedCatIndex) {
        for (Object o : categories[selectedCatIndex].items(false, true)) {
            //TODO maybe some titles here as well?
            if (o instanceof ScrollingListPane.ListButton) {
                if (((ScrollingListPane.ListButton) o).isDestroyed()) o = Reflection.newInstance(o.getClass());
            }
            ((ScrollingListPane) sp).addItem(categories[selectedCatIndex].createListItem(o, window));
        }
    }

}
