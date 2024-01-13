package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ui.Component;

import java.util.HashSet;

public class RandomItemDistrComp extends ItemsWithChanceDistrComp {

    private final RandomItem<?> randomItem;

    public RandomItemDistrComp(RandomItem<?> randomItem) {
        super(randomItem.getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI(), 1);
        this.randomItem = randomItem;
    }

    public Component createTitle() {
        return super.createTitle(Messages.get(RandomItemDistrComp.class, "title"));
    }

    @Override
    protected void showAddItemWnd() {
        WndBag.ItemSelector selector = createSelector(randomItem.getType(), false, Items.bag.getClass());
        if (randomItem.getType() != Item.class)
            ItemSelector.showSelectWindow(selector, ItemSelector.NullTypeSelector.NOTHING, randomItem.getType(),
                    Items.bag, new HashSet<>(0), false);
        else EditorScene.selectItem(selector);
    }

    @Override
    protected void updateParent() {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }
}