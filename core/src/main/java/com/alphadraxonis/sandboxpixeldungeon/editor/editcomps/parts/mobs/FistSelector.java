package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.mobs;

import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.YogFist;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.categories.Mobs;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.MobItem;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelectorList;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndBag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FistSelector extends ItemSelectorList<MobItem> {


    protected List<YogFist> fists;

    public FistSelector(List<YogFist> fists, String label, int fontSize) {
        super(createMobItems(fists), label, fontSize);
        this.fists = fists;
    }

    private static List<MobItem> createMobItems(List<? extends Mob> fists) {
        List<MobItem> ret = new ArrayList<>(5);
        for (Mob m : fists) ret.add(new MobItem(m));
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
            public boolean addOtherTabs() {
                return false;
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return Mobs.bag.getClass();
            }
        }, ItemSelector.NullTypeSelector.NONE, YogFist.class, Mobs.bag, new HashSet<>());
    }
}