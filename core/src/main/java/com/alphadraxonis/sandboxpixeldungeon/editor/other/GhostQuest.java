package com.alphadraxonis.sandboxpixeldungeon.editor.other;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.LevelScheme;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.LeatherArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.MailArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.PlateArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.ScaleArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.journal.Notes;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GhostQuest extends Quest {

    private static boolean completedOnce;


    public Weapon weapon;
    public Armor armor;
    public Weapon.Enchantment enchant;
    public Armor.Glyph glyph;

    public GhostQuest() {
    }

    public GhostQuest(int type, Weapon weapon, Armor armor, Weapon.Enchantment enchant, Armor.Glyph glyph) {
        this.type = type;
        this.weapon = weapon;
        this.armor = armor;
        this.enchant = enchant;
        this.glyph = glyph;
    }

    public static GhostQuest createRandom(LevelScheme levelScheme) {
        GhostQuest quest = new GhostQuest();

        quest.type = levelScheme.getGhostQuest();

        //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
        switch (Random.chances(new float[]{0, 0, 10, 6, 3, 1})) {
            default:
            case 2:
                quest.armor = new LeatherArmor();
                break;
            case 3:
                quest.armor = new MailArmor();
                break;
            case 4:
                quest.armor = new ScaleArmor();
                break;
            case 5:
                quest.armor = new PlateArmor();
                break;
        }
        //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
        int wepTier = Random.chances(new float[]{0, 0, 10, 6, 3, 1});
        quest.weapon = (Weapon) Generator.randomUsingDefaults(Generator.wepTiers[wepTier - 1]);

        //clear weapon's starting properties
        quest.weapon.level(0);
        quest.weapon.enchant(null);
        quest.weapon.cursed = false;

        //50%:+0, 30%:+1, 15%:+2, 5%:+3
        float itemLevelRoll = Random.Float();
        int itemLevel;
        if (itemLevelRoll < 0.5f) {
            itemLevel = 0;
        } else if (itemLevelRoll < 0.8f) {
            itemLevel = 1;
        } else if (itemLevelRoll < 0.95f) {
            itemLevel = 2;
        } else {
            itemLevel = 3;
        }
        quest.weapon.upgrade(itemLevel);
        quest.armor.upgrade(itemLevel);

        //10% to be enchanted. We store it separately so enchant status isn't revealed early
        if (Random.Int(10) == 0) {
            quest.enchant = Weapon.Enchantment.random();
            quest.glyph = Armor.Glyph.random();
        }

        return quest;
    }

    public void complete() {
        weapon = null;
        armor = null;
        completedOnce = true;

        Notes.remove(Notes.Landmark.GHOST);
    }

    @Override
    public boolean completed() {
        return processed() && weapon == null && armor == null;
    }

    public static boolean completedOnce() {
        return completedOnce;
    }

    public void process() {
        if (given && !processed()) {
            GLog.n(Messages.get(Ghost.class, "find_me"));
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
            processed = true;
            addScore(0,1000);
        }
    }


    private static final String NODE = "sad_ghost";
    private static final String COMPLETED_ONCE = "completed_once";

    private static final String WEAPON = "weapon";
    private static final String ARMOR = "armor";
    private static final String ENCHANT = "enchant";
    private static final String GLYPH = "glyph";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(WEAPON, weapon);
        bundle.put(ARMOR, armor);

        if (enchant != null) {
            bundle.put(ENCHANT, enchant);
            bundle.put(GLYPH, glyph);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

        weapon = (Weapon) bundle.get(WEAPON);
        armor = (Armor) bundle.get(ARMOR);

        if (bundle.contains(ENCHANT)) {
            enchant = (Weapon.Enchantment) bundle.get(ENCHANT);
            glyph = (Armor.Glyph) bundle.get(GLYPH);
        }
    }

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(COMPLETED_ONCE, completedOnce);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = (Bundle) bundle.getBundle(NODE);
        completedOnce = b.getBoolean(COMPLETED_ONCE);
    }

    public static void reset(){
        completedOnce = false;
    }

}