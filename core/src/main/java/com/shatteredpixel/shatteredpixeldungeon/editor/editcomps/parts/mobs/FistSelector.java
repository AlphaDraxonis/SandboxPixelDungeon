package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelectorList;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class FistSelector extends ItemSelectorList<MobItem> {


    protected List<YogFist> fists;

    public FistSelector(List<YogFist> fists, String label, int fontSize) {
        super(createMobItems(fists), label, fontSize);
        this.fists = fists;
    }

    public static List<MobItem> createMobItems(List<? extends Mob> mobs) {
        List<MobItem> ret = new ArrayList<>(5);
        for (Mob m : mobs) ret.add(new MobItem(m));
        return ret;
    }

    @Override
    public void change(int index) {
        ItemSelector.showSelectWindow(new WndBag.ItemSelector() {
            @Override
            public String textPrompt() {
                return null;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof MobItem && ((MobItem) item).mob() instanceof YogFist;
            }

            @Override
            public void onSelect(Item item) {
                if (!(item instanceof MobItem)) return;
                item = item.getCopy();
                list.set(index, (MobItem) item);
                fists.set(index, (YogFist) ((MobItem) item).mob());
                updateItem(index);
            }

            @Override
            public List<Bag> getBags() {
                return Collections.singletonList(Mobs.bag());
            }
        }, ItemSelector.NullTypeSelector.DISABLED, YogFist.class, Mobs.bag(), new HashSet<>());
    }
}