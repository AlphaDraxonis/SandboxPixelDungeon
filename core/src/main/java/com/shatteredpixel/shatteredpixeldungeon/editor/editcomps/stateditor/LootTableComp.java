package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Consumer;
import com.watabou.utils.Reflection;

public class LootTableComp extends ItemsWithChanceDistrComp {


    protected final Mob mob;

    public LootTableComp(Mob mob) {
        super(createRandomItemData(mob), 0, Integer.MAX_VALUE);
        this.mob = mob;
    }

    private static RandomItemData createRandomItemData(Mob mob) {
        if (mob.loot instanceof RandomItemData) return (RandomItemData) mob.loot;
        RandomItemData randomItemData = mob.convertLootToRandomItemData();
        mob.loot = randomItemData;
        return randomItemData;
    }

    protected Component createTitle() {
        return super.createTitle(Messages.get(LootTableComp.class, "title_loot"));
    }

    @Override
    protected Component createRestoreButton() {
        return new RedButton(Messages.get(LootTableComp.class, "reset_loot")) {
            @Override
            protected void onClick() {
                mob.loot = Reflection.newInstance(mob.getClass()).loot;
                WndEditStats wndEditStats = findWndEditStats();
                wndEditStats.closeCurrentSubMenu();
            }
        };
    }

    @Override
    protected WndBag.ItemSelector createSelector(Consumer<Item> onSelect) {
        return createSelector(Item.class, true, Items.bag().getClass(), onSelect);
    }

    @Override
    protected void updateParent() {
        WndEditStats wndEditStats = findWndEditStats();
        if (wndEditStats != null) wndEditStats.layout();
    }
}