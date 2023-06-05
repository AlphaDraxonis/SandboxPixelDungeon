package com.alphadraxonis.sandboxpixeldungeon.editor.ui;

import static com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings.ITEM_HEIGHT;

import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.List;

public class CategoryScroller extends Component {

    public final ScrollingListPane list;
    private final Category[] categories;
    private final RedButton[] categoryButtons;

    private int selectedIndex;

    public CategoryScroller(Category[] categories) {
        super();

        this.categories = categories;
        categoryButtons = new RedButton[categories.length];

        for (int i = 0; i < categoryButtons.length; i++) {
            final int idx = i;
            categoryButtons[i] = new RedButton("") {
                @Override
                protected void onClick() {
                    if (selectedIndex != idx) {
                        selectCategory(idx);
                    }
                }

                @Override
                protected String hoverText() {
                    return categories[idx].getName();
                }
            };
            Image icon = categories[i].getImage();
            if (icon != null) categoryButtons[i].icon(icon);
            else categoryButtons[i].text("-");
            add(categoryButtons[i]);
        }

        list = new ScrollingListPane();
        add(list);
    }

    @Override
    protected void layout() {

        if (PixelScene.landscape() || categoryButtons.length <= 7) {
            float buttonWidth = width() / categoryButtons.length;
            for (int i = 0; i < categoryButtons.length; i++) {
                categoryButtons[i].setRect(i * buttonWidth, 0, buttonWidth, ITEM_HEIGHT);
                PixelScene.align(categoryButtons[i]);
            }
        } else {
            //for first row
            float buttonWidth = (float) (width() / Math.ceil(categoryButtons.length / 2f));
            float y = 0;
            float x = 0;
            for (int i = 0; i < categoryButtons.length; i++) {
                categoryButtons[i].setRect(x, y, buttonWidth, ITEM_HEIGHT);
                PixelScene.align(categoryButtons[i]);
                x += buttonWidth;
                if (i == Math.ceil(categoryButtons.length / 2f) - 1) {
                    y += ITEM_HEIGHT;
                    x = 0;
                    buttonWidth = (float) (width() / Math.ceil(categoryButtons.length / 2f));
                }
            }
        }

        selectCategory(selectedIndex);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void selectCategory(int index) {

        selectedIndex = index;

        list.clear();

        for (int i = 0; i < categoryButtons.length; i++) {
            if (i == selectedIndex) categoryButtons[i].icon().color(Window.TITLE_COLOR);
            else categoryButtons[i].icon().resetColor();
        }

        list.scrollTo(0, 0);


        for (Object o : categories[selectedIndex].getItems()) {
            //TODO maybe some titles here aswell?
            list.addItem(categories[selectedIndex].createListItem(o));
        }

        list.setRect(x, categoryButtons[categoryButtons.length - 1].bottom() + 1, width,
                height - categoryButtons[categoryButtons.length - 1].bottom() - 1);
    }

    public Category[] getCategories() {
        return categories;
    }

    public Component[] getObjectComps() {
        return list.getItems();
    }

    public void updateItems() {
        for (Component i : getObjectComps()) {
            if (i instanceof AdvancedListPaneItem) ((AdvancedListPaneItem) i).onUpdate();
        }
    }

    public float getCurrentViewY() {
        return  list.content().camera.scroll.y;
    }

    public abstract static class Category {

        public Category() {
        }

        protected abstract List<?> getItems();

        protected abstract ScrollingListPane.ListItem createListItem(Object object);

        protected Image getImage() {
            return new ItemSprite(ItemSpriteSheet.SOMETHING);
        }

        protected String getName() {
            return "";
        }

    }

}