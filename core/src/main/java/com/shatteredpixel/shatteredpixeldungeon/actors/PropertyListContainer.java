package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PropertyItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyListContainer extends ItemContainerWithLabel<PropertyItem> {

    private Char ch;

    public PropertyListContainer(Char ch, DefaultEditComp<?> editComp) {
        super(createPropertyItemList(ch), editComp, Messages.get(PropertyListContainer.class, "properties") + ":");
        this.ch = ch;
    }

    private static List<PropertyItem> createPropertyItemList(Char ch) {
        List<PropertyItem> asPropertyItems = new ArrayList<>();
        for (Char.Property p : ch.properties) {
            asPropertyItems.add(new PropertyItem(p));
        }
        return asPropertyItems;
    }


    @Override
    protected void showSelectWindow() {

        EditorScene.show(new WndChooseOneInCategories(
                Messages.get(EditMobComp.class, "add_property_title"), "",
                Char.Property.getAllProperties(getPropertiesToIgnore()), new String[]{Messages.get(this, "properties")}) {
            @Override
            protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
                ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
                for (int i = 0; i < ret.length; i++) {
                    Char.Property p = (Char.Property) category[i];
                    ret[i] = new ChooseOneInCategoriesBody.BtnRow(PropertyItem.getName(p), PropertyItem.getDesc(p), PropertyItem.getImage(p)) {
                        @Override
                        protected void onClick() {
                            finish();
                            addNewItem(new PropertyItem(p));
                        }
                    };
                    ret[i].setLeftJustify(true);
                }
                return ret;
            }
        });
    }

    @Override
    protected void doAddItem(PropertyItem item) {
        item.setObject(doAddProperty(item.getObject()));
        super.doAddItem(item);
    }

    @Override
    protected boolean removeSlot(ItemContainer<PropertyItem>.Slot slot) {
        if (super.removeSlot(slot)) {
            doRemoveProperty(((PropertyItem) slot.item()).getObject());
            return true;
        }
        return false;
    }

    protected Char.Property doAddProperty(Char.Property property) {
        ch.properties.add(property);
        return property;
    }

    protected void doRemoveProperty(Char.Property property) {
        ch.properties.remove(property);
    }

    protected Set<Char.Property> getPropertiesToIgnore() {
        Set<Char.Property> propertiesToIgnore = new HashSet<>();
        for (PropertyItem item : itemList) {
            propertiesToIgnore.add(item.getObject());
        }
        return propertiesToIgnore;
    }

    public void setProperties(Char ch) {
        while (!slots.isEmpty()) {
            removeSlot(slots.get(0));
        }
        for (Char.Property property : ch.properties) {
            addNewItem(new PropertyItem(property));
        }
    }
}