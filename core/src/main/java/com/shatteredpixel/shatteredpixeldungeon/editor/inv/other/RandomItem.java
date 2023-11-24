package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.AugumentationSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.stateditor.LootTableComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.List;

public interface RandomItem<T extends Item> {

    String INTERNAL_RANDOM_ITEM = "internal_random_item";
    String GENERATED_ITEM = "generated_item";

    T generateItem();

    LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI();

    Class<? extends Item> getType();

    //pls tell me if there is a better solution than copy paste:
    //I need random classes to extend classes like weapon or armor so I can still use these types everywhere else
    //ok maybe an alternative would be to create wrapper classes that extend each of these types and use a reference to the only real random class

    static void replaceRandomItemsInList(List<? extends Item> items) {
        replaceRandomItemsInList(items, false);
    }

    static <T extends Item> void replaceRandomItemsInList(List<T> items, boolean identifyOnlyEquipment) {
        for (Item i : items.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {//i is also of type T
            T item = (T) AugumentationSpinner.assignRandomAugmentation(i, identifyOnlyEquipment);//always returns <? extends T>
            if (item != i) {
                if (item == null) items.remove(i);
                else items.set(items.indexOf(i), item);
            }
        }
    }

    static RandomItem<? extends Item> getNewRandomItem(Class<? extends Item> itemClass) {
        if (itemClass == Weapon.class) return new RandomWeapon();
        if (itemClass == MeleeWeapon.class) return new RandomMeleeWeapon();
        if (itemClass == Armor.class) return new RandomArmor();
        if (itemClass == Ring.class) return new RandomRing();
        if (itemClass == Artifact.class) return new RandomArmor();
        if (itemClass == EquipableItem.class) return new RandomEqItem();
        if (itemClass == Wand.class) return new RandomWand();
        if (itemClass == Bag.class) return new RandomBag();
        return new RandomItemAny();
    }

    static String getName() {
        return Messages.get(RandomItem.class, "name");
    }

    static String getDesc() {
        return Messages.get(RandomItem.class, "desc");
    }

    class RandomItemAny extends Item implements RandomItem<Item> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Item generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Item) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomItemAny) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomItemAny) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Item generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Item.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }
    }


    class RandomWeapon extends Weapon implements RandomItem<Weapon> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Weapon generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Weapon) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomWeapon) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomWeapon) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Weapon generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Weapon) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Weapon.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }

        @Override
        public int STRReq(int lvl) {
            return 0;
        }

        @Override
        public int min(int lvl) {
            return 0;
        }

        @Override
        public int max(int lvl) {
            return 0;
        }
    }


    class RandomMeleeWeapon extends MeleeWeapon implements RandomItem<MeleeWeapon> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private MeleeWeapon generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (MeleeWeapon) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomMeleeWeapon) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomMeleeWeapon) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public MeleeWeapon generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (MeleeWeapon) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return MeleeWeapon.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }
    }


    class RandomArmor extends Armor implements RandomItem<Armor> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Armor generatedItem;

        public RandomArmor() {
            super(1);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Armor) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomArmor) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomArmor) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Armor generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Armor) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Armor.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }
    }


    class RandomRing extends Ring implements RandomItem<Ring> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Ring generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Ring) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomRing) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomRing) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Ring generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Ring) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Ring.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }

    }


    class RandomArtifact extends Artifact implements RandomItem<Artifact> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Artifact generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Artifact) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomArtifact) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomArtifact) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Artifact generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Artifact) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Artifact.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }

    }


    class RandomEqItem extends EquipableItem implements RandomItem<EquipableItem> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private EquipableItem generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (EquipableItem) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomEqItem) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomEqItem) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public EquipableItem generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (EquipableItem) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return EquipableItem.class;
        }

        @Override
        public boolean doEquip(Hero hero) {
            return false;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }
    }


    class RandomWand extends Wand implements RandomItem<Wand> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Wand generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Wand) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomWand) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomWeapon) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Wand generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Wand) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Wand.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }

        @Override
        public void onZap(Ballistica attack) {
        }

        @Override
        public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        }
    }


    class RandomBag extends Bag implements RandomItem<Bag> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private LootTableComp.CustomLootInfo internalRandomItem = new LootTableComp.CustomLootInfo();

        private Bag generatedItem;

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (LootTableComp.CustomLootInfo) bundle.get(INTERNAL_RANDOM_ITEM);
            generatedItem = (Bag) bundle.get(GENERATED_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
            bundle.put(GENERATED_ITEM, generatedItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomBag) obj).internalRandomItem)
                    && EditItemComp.areEqual(generatedItem, ((RandomBag) obj).generatedItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Bag generateItem() {
            if (generatedItem != null) return generatedItem;
            if (Random.Float() < internalRandomItem.lootChance()) return generatedItem = null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            generatedItem = (Bag) result.get(0);
            if (reservedQuickslot > 0) generatedItem.reservedQuickslot = reservedQuickslot;
            return generatedItem;
        }

        @Override
        public LootTableComp.CustomLootInfo getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<? extends Item> getType() {
            return Bag.class;
        }

        @Override
        public String name() {
            return RandomItem.getName();
        }

        @Override
        public String desc() {
            return RandomItem.getDesc();
        }
    }

}