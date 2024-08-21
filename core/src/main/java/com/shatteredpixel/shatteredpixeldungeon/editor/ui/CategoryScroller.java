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

    public CategoryScroller(Category[] categories, EditorInventoryWindow window) {
        super(categories, new RedButton[categories.length], window);
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

        if (indicesWithNonEmptyCats.length > maxButtonsPerRow()) {
            if (curAction == SPDAction.S) {
                int index = (int) (selectedVisIndex + Math.ceil(indicesWithNonEmptyCats.length / 2f)) % indicesWithNonEmptyCats.length;
                selectVisibleCategory(index);
                return true;
            }
            if (curAction == SPDAction.N) {
                int index = (int) (selectedVisIndex - Math.ceil(indicesWithNonEmptyCats.length / 2f)) % indicesWithNonEmptyCats.length;
                if (index < 0) index += Math.ceil(indicesWithNonEmptyCats.length / 2f) * 2;
                selectVisibleCategory(index);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void layout() {

        if (categoryComps != null) {
            if (indicesWithNonEmptyCats.length <= maxButtonsPerRow()) {
                float buttonWidth = width() / indicesWithNonEmptyCats.length;
                for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                    int catIndex = indicesWithNonEmptyCats[i];
                    categoryComps[catIndex].setRect(i * buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryComps[catIndex]);
                }
            } else {
                //for first row
                float buttonWidth = (float) (width() / Math.ceil(indicesWithNonEmptyCats.length / 2f));
                float y = 0;
                float x = 0;
                for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                    int catIndex = indicesWithNonEmptyCats[i];
                    categoryComps[catIndex].setRect(x, y, buttonWidth, ITEM_HEIGHT);
                    PixelScene.align(categoryComps[catIndex]);
                    x += buttonWidth;
                    if (i == Math.ceil(indicesWithNonEmptyCats.length / 2f) - 1) {
                        y += ITEM_HEIGHT;
                        x = 0;
                    }
                }
            }
        }

        selectVisibleCategory(selectedVisIndex);
    }

    private static int maxButtonsPerRow() {
        return PixelScene.landscape() ? 9 : 7;
    }

    @Override
    protected void doSelectCategory(int selectedVisIndex, int selectedCatIndex) {
        sp.clear();

        if (categoryComps != null) {
            for (int i = 0; i < indicesWithNonEmptyCats.length; i++) {
                int catIndex = indicesWithNonEmptyCats[i];
                if (catIndex == selectedCatIndex) categoryComps[catIndex].icon().color(Window.TITLE_COLOR);
                else categoryComps[catIndex].icon().resetColor();
            }
        }

        sp.scrollTo(0, 0);


        for (Object o : categories[selectedCatIndex].items(false, true)) {
            //TODO maybe some titles here as well?
            if (o instanceof ScrollingListPane.ListButton) {
                if (((ScrollingListPane.ListButton) o).isDestroyed()) o = Reflection.newInstance(o.getClass());
            }
            ((ScrollingListPane) sp).addItem(categories[selectedCatIndex].createListItem(o, window));
        }

        float bottom = categoryComps == null ? 0 : categoryComps[indicesWithNonEmptyCats[indicesWithNonEmptyCats.length - 1]].bottom() + 1;
        sp.setRect(x, bottom, width,height - bottom);
    }

    @Override
    public void updateItems() {
        for (Component i : ((ScrollingListPane) sp).getItems()) {
            if (i instanceof AdvancedListPaneItem) ((AdvancedListPaneItem) i).onUpdate();
        }
    }
}