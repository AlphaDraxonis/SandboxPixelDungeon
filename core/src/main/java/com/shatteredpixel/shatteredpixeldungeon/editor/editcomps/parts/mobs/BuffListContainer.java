package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Buffs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BuffItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.watabou.utils.Reflection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BuffListContainer extends ItemContainerWithLabel<BuffItem> {


    public BuffListContainer(List<BuffItem> itemList, String label) {
        super(itemList, label);
    }

    public BuffListContainer(List<BuffItem> itemList, DefaultEditComp<?> editComp, String label) {
        super(itemList, editComp, label);
    }

    public BuffListContainer(List<BuffItem> itemList, DefaultEditComp<?> editComp, boolean reverseUiOrder, String label) {
        super(itemList, editComp, reverseUiOrder, label);
    }

    @Override
    protected void showSelectWindow() {

        EditorScene.show(new WndChooseOneInCategories(
                Messages.get(EditMobComp.class, "add_buff_title"), "",
                Buffs.getAllBuffs(getBuffsToIgnore()), Buffs.getCatNames()) {
            @Override
            protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
                ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
                for (int i = 0; i < ret.length; i++) {
                    Buff b = Reflection.newInstance((Class<? extends Buff>) category[i]);
                    ret[i] = new ChooseOneInCategoriesBody.BtnRow(b.name(), b.desc(), new BuffIcon(b, true)) {
                        @Override
                        protected void onClick() {
                            finish();
                            addNewItem(new BuffItem(b));
                        }
                    };
                    ret[i].setLeftJustify(true);
                }
                return ret;
            }
        });
    }

    @Override
    protected void doAddItem(BuffItem item) {
        item.setObject(doAddBuff(item.getObject().getClass()));
        super.doAddItem(item);
    }

    @Override
    protected boolean removeSlot(ItemContainer<BuffItem>.Slot slot) {
        if (super.removeSlot(slot)) {
            doRemoveBuff(((BuffItem) slot.item()).getObject());
            return true;
        }
        return false;
    }

    protected abstract Buff doAddBuff(Class<? extends Buff> buff);

    protected abstract void doRemoveBuff(Buff buff);

    protected Set<Class<? extends Buff>> getBuffsToIgnore() {
        Set<Class<? extends Buff>> buffsToIgnore = new HashSet<>();
        for (BuffItem item : itemList) {
            buffsToIgnore.add(item.getObject().getClass());
        }
        return buffsToIgnore;
    }

    public void removeBuffFromUI(Class<? extends Buff> buff) {
        ItemContainer<BuffItem>.Slot remove = null;
        for (ItemContainer<BuffItem>.Slot slot : slots) {
            Buff b = ((BuffItem) slot.item()).getObject();
            if (b.getClass() == buff) {
                remove = slot;
                break;
            }
        }
        if (remove != null) super.removeSlot(remove);
    }
}