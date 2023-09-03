package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.general;

import com.alphadraxonis.sandboxpixeldungeon.actors.hero.HeroClass;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.ItemContainer;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ItemSelector;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.KindofMisc;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.bags.Bag;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.ui.CheckBox;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.List;

public class HeroSettings extends Component {

    private static final int[] scrollPos = new int[HeroClass.values().length + 1];

    private static int currentIndex = 0;

    private Component outsideSp;//TODO key listener for switching tabs

    private HeroTab[] heroTabs;


    public HeroSettings() {

        heroTabs = new HeroTab[scrollPos.length];

        int i = 0;
        heroTabs[i] = new HeroTab(null, i);
        i++;
        for (HeroClass hero : HeroClass.values()) {
            heroTabs[i] = new HeroTab(hero, i);
            i++;
        }
    }


    public static Component createTitle() {
        return WndTitledMessage.createTitleNoIcon(Messages.titleCase(Messages.get(HeroSettings.class, "title")));
    }

    public Component getOutsideSp() {
        return outsideSp;
    }


    private static class HeroTab extends Component {

        private final HeroClass hero;
        private final int index;

        private CheckBox heroEnabled;
        private ItemContainer<Item> startItems;//TODO extract gold and energy, own spinner for each  and exclude bags
        private ItemContainer<Bag> startBags;
        private ItemSelector startWeapon, startArmor, startRing, startArti, startMisc;

        public HeroTab(HeroClass hero, int index) {
            this.hero = hero;
            this.index = index;
        }

    }

    public static class HeroStartItemsData implements Bundlable {
        public Weapon weapon;
        public Armor armor;
        public Ring ring;
        public Artifact artifact;
        public KindofMisc misc;
        public List<Bag> bags = new ArrayList<>(2);
        public List<Item> items = new ArrayList<>(3);
        public int gold, energy;

        private static final String WEAPON = "weapon";
        private static final String ARMOR = "armor";
        private static final String RING = "ring";
        private static final String ARTIFACT = "artifact";
        private static final String MISC = "misc";
        private static final String GOLD = "gold";
        private static final String ENERGY = "energy";
        private static final String BAGS = "bags";
        private static final String ITEMS = "items";

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(WEAPON, weapon);
            bundle.put(ARMOR, armor);
            bundle.put(RING, ring);
            bundle.put(ARTIFACT, artifact);
            bundle.put(MISC, misc);
            bundle.put(GOLD, gold);
            bundle.put(ENERGY, energy);

            bundle.put(BAGS, bags);
            bundle.put(ITEMS, items);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            weapon = (Weapon) bundle.get(WEAPON);
            armor = (Armor) bundle.get(ARMOR);
            ring = (Ring) bundle.get(RING);
            artifact = (Artifact) bundle.get(ARTIFACT);
            misc = (KindofMisc) bundle.get(MISC);
            gold = bundle.getInt(GOLD);
            energy = bundle.getInt(ENERGY);

            for (Bundlable b : bundle.getCollection(BAGS)) {
                bags.add((Bag) b);
            }
            for (Bundlable b : bundle.getCollection(ITEMS)) {
                items.add((Item) b);
            }
        }

        public HeroStartItemsData getCopy() {
            Bundle bundle = new Bundle();
            bundle.put("DATA", this);
            return (HeroStartItemsData) bundle.get("DATA");
        }
    }
}