package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.ItemContainer;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.WndChooseEnchant;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EnchantmentItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemContainerWithLabel;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentListContainer extends ItemContainerWithLabel<EnchantmentItem> {

    private final Mob mob;

    public EnchantmentListContainer(Mob mob, DefaultEditComp<?> editComp) {
        super(createEnchantmentItemList(mob), editComp, Messages.get(Mob.class, "enchantments") + ":");
        this.mob = mob;
    }

    private static List<EnchantmentItem> createEnchantmentItemList(Mob mob) {
        List<EnchantmentItem> asEnchantItems = new ArrayList<>();
        for (Weapon.Enchantment ench : mob.enchantWeapon.enchantments) {
            asEnchantItems.add(new EnchantmentItem(ench));
        }
        for (Armor.Glyph ench : mob.glyphArmor.glyphs.values()) {
            asEnchantItems.add(new EnchantmentItem(ench));
        }
        return asEnchantItems;
    }


    @Override
    protected void showSelectWindow() {
        EditorScene.show(new WndChooseEnchant() {

            @Override
            protected void onSelect(EnchantmentLike enchantment) {
                addNewItem(new EnchantmentItem(enchantment));
                super.onSelect(enchantment);
            }
        });
    }

    @Override
    protected void doAddItem(EnchantmentItem item) {
        item.setObject(doAddEnchantment(item.getObject()));
        super.doAddItem(item);
    }

    @Override
    protected boolean removeSlot(ItemContainer<EnchantmentItem>.Slot slot) {
        if (super.removeSlot(slot)) {
            doRemoveEnchantment(((EnchantmentItem) slot.item()).getObject());
            return true;
        }
        return false;
    }

    protected EnchantmentLike doAddEnchantment(EnchantmentLike ench) {
        if (ench instanceof Weapon.Enchantment)
            mob.enchantWeapon.addEnchantment((Weapon.Enchantment) ench);
        else if (ench instanceof Armor.Glyph)
            mob.glyphArmor.addGlyph((Armor.Glyph) ench);

        return ench;
    }

    protected void doRemoveEnchantment(EnchantmentLike ench) {
        if (ench instanceof Weapon.Enchantment)
            mob.enchantWeapon.removeEnchantment((Weapon.Enchantment) ench);
        else if (ench instanceof Armor.Glyph)
            mob.glyphArmor.removeGlyph(((Armor.Glyph) ench).getClass());
    }

    public void setProperties(Mob mob) {
        while (!slots.isEmpty()) {
            removeSlot(slots.get(0));
        }
        for (EnchantmentItem item : createEnchantmentItemList(mob)) {
            addNewItem(item);
        }
    }
}