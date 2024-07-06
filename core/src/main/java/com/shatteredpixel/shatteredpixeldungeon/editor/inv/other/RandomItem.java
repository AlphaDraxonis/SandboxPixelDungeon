package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

import java.util.List;

public interface RandomItem<T extends GameObject> {

    String INTERNAL_RANDOM_ITEM = "internal_random_item";

    T[] generateItems();

    ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI();

    Class<T> getType();

    default int getMaxLoottableSize() {
        return 1;
    }

    //pls tell me if there is a better solution than copy paste:
    //I need random classes to extend classes like weapon or armor so I can still use these types everywhere else
    //ok maybe an alternative would be to create wrapper classes that extend each of these types and use a reference to the only real random class

    static RandomItem<? extends Item> getNewRandomItem(Class<? extends Item> itemClass) {
        if (itemClass == Weapon.class) return new RandomWeapon();
        if (itemClass == MeleeWeapon.class) return new RandomMeleeWeapon();
        if (itemClass == Armor.class) return new RandomArmor();
        if (itemClass == Ring.class) return new RandomRing();
        if (itemClass == Artifact.class) return new RandomArtifact();
        if (itemClass == KindofMisc.class) return new RandomEqMiscItem();
        if (itemClass == Wand.class) return new RandomWand();
        if (itemClass == Bag.class) return new RandomBag();
        if (itemClass == Trinket.class) return new RandomTrinket();
        return new RandomItemAny();
    }

    static String getName() {
        return Messages.get(RandomItem.class, "name");
    }

    static String getDesc() {
        return Messages.get(RandomItem.class, "desc");
    }

    static boolean doOnAllGameObjects(RandomItem obj, Function<GameObject, GameObject.ModifyResult> whatToDo) {
        boolean removedSth = false;
        for (ItemsWithChanceDistrComp.ItemWithCount items : obj.getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI().distrSlots) {
            if (GameObject.doOnAllGameObjectsList(items.items, whatToDo)) removedSth = true;
        }
        return removedSth;
    }

    static void fillArrayAsGoodAsPossibleUsingRandomItems(Item[] items) {
        Item[][] generatedItems = new Item[items.length][];
        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof RandomItem) {
                items[i] = null;
                generatedItems[i] = new Item[]{items[i]};
                Item[] generated = GameObject.doOnAllGameObjectsArray(generatedItems[i], GameObject::initRandoms);
                if (generated != null && generated.length > 0) items[i] = generated[0];
            }
        }
        int indexOne = 0;
        int indexTwo = 1;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                while (indexTwo < items.length) {
                    while (indexOne < generatedItems.length
                            && (generatedItems[indexOne] == null || generatedItems[indexOne].length <= indexTwo)) indexOne++;
                    if (indexOne >= generatedItems.length) {
                        indexOne = 0;
                        indexTwo++;
                        continue;
                    }
                    items[i] = generatedItems[indexOne][indexTwo];
                    indexOne++;
                    break;
                }

            }
        }
    }

    class RandomItemAny extends Item implements RandomItem<Item> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomItemAny) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Item[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Item[] array = result.toArray(EditorUtilies.EMPTY_ITEM_ARRAY);
            if (reservedQuickslot > 0 && array[0].defaultAction() != null && !(array[0] instanceof Key))
                array[0].reservedQuickslot = reservedQuickslot;
            return array;
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Item> getType() {
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

        @Override
        public int getMaxLoottableSize() {
            return 1_000_000;
        }
    }


    class RandomWeapon extends Weapon implements RandomItem<Weapon> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomWeapon) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Weapon[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Weapon item = (Weapon) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Weapon[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Weapon> getType() {
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomMeleeWeapon) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public MeleeWeapon[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            MeleeWeapon item = (MeleeWeapon) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new MeleeWeapon[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<MeleeWeapon> getType() {
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        public RandomArmor() {
            super(1);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomArmor) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Armor[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Armor item = (Armor) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Armor[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Armor> getType() {
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomRing) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Ring[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Ring item = (Ring) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Ring[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Ring> getType() {
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomArtifact) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Artifact[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Artifact item = (Artifact) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Artifact[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Artifact> getType() {
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


    class RandomEqMiscItem extends KindofMisc implements RandomItem<KindofMisc> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomEqMiscItem) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public KindofMisc[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            KindofMisc item = (KindofMisc) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new KindofMisc[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<KindofMisc> getType() {
            return KindofMisc.class;
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomWand) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Wand[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Wand item = (Wand) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Wand[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Wand> getType() {
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
        public String info() {
            return desc();
        }

        @Override
        public String status() {
            return null;
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
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomBag) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Bag[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Bag item = (Bag) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Bag[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Bag> getType() {
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


    class RandomTrinket extends Trinket implements RandomItem<Trinket> {

        {
            image = ItemSpriteSheet.SOMETHING;
        }

        //can only have one item per slot, and no RandomItem
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomTrinket) obj).internalRandomItem);
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public Trinket[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Trinket item = (Trinket) result.get(0);
            if (reservedQuickslot > 0) item.reservedQuickslot = reservedQuickslot;
            return new Trinket[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Trinket> getType() {
            return Trinket.class;
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
        protected int upgradeEnergyCost() {
            return 0;
        }
    }



    class RandomTrap extends Trap implements RandomItem<Trap> {

        {
            color = 15;
            shape = WAVES;
        }

        //can only have one item per slot, and no RandomTrap
        private ItemsWithChanceDistrComp.RandomItemData internalRandomItem = new ItemsWithChanceDistrComp.RandomItemData();

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            internalRandomItem = (ItemsWithChanceDistrComp.RandomItemData) bundle.get(INTERNAL_RANDOM_ITEM);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INTERNAL_RANDOM_ITEM, internalRandomItem);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return internalRandomItem.equals(((RandomTrap) obj).internalRandomItem);
        }

        @Override
        public Trap[] generateItems() {
            if (Random.Float() >= internalRandomItem.lootChance()) return null;
            List<Item> result = internalRandomItem.generateLoot();
            if (result == null || result.isEmpty()) return null;
            Trap item = ((TrapItem) result.get(0)).getObject();
            return new Trap[]{item};
        }

        @Override
        public ItemsWithChanceDistrComp.RandomItemData getInternalRandomItem_ACCESS_ONLY_FOR_EDITING_UI() {
            return internalRandomItem;
        }

        @Override
        public Class<Trap> getType() {
            return Trap.class;
        }

        @Override
        public void activate() {
        }

        @Override
        public String name() {
            return Messages.get(RandomTrap.class, "name");
        }

        @Override
        public String desc() {
            return Messages.get(RandomTrap.class, "desc");
        }

        @Override
        public ModifyResult initRandoms() {
            //super already assigns the values
            return overrideResult(super.initRandoms(), trap -> {
                Trap t = (Trap) trap;
                t.pos = pos;
                Dungeon.level.setTrap(t, t.pos);
                Dungeon.level.map[t.pos] = t.visible ? Terrain.TRAP : Terrain.SECRET_TRAP;
                Dungeon.level.secret[t.pos] = !t.visible;
                return true;
            });
        }
    }
}