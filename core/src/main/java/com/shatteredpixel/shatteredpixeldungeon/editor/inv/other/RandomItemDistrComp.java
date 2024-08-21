package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;

import java.util.HashSet;

public class RandomItemDistrComp extends ItemsWithChanceDistrComp {

    private final RandomItem<?> randomItem;

    public RandomItemDistrComp(RandomItem<?> randomItem) {
        super(randomItem.getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI(), 0, randomItem.getMaxLoottableSize());
        this.randomItem = randomItem;
    }

    public Component createTitle() {
        return super.createTitle(Messages.get(RandomItemDistrComp.class, "title"));
    }

    @Override
    protected void showAddItemWnd(Consumer<Item> onSelect) {
        WndBag.ItemSelector selector = createSelector(onSelect);
        if (randomItem.getType() != Item.class)
            ItemSelector.showSelectWindow(selector, ItemSelector.NullTypeSelector.NOTHING, randomItem.getType(),
                    Items.bag(), new HashSet<>(0), false);
        else EditorScene.selectItem(selector);
    }

    @Override
    protected WndBag.ItemSelector createSelector(Consumer<Item> onSelect) {
        return createSelector((Class<? extends Item>) randomItem.getType(), false, Items.bag().getClass(), onSelect);
    }

    @Override
    protected void updateParent() {
        Window w = EditorUtilities.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }
}