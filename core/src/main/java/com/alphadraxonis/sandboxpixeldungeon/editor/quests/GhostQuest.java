package com.alphadraxonis.sandboxpixeldungeon.editor.quests;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Ghost;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.parts.items.AugumentationSpinner;
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
import com.alphadraxonis.sandboxpixeldungeon.sprites.FetidRatSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GnollTricksterSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GreatCrabSprite;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class GhostQuest extends Quest {

    public static final int RAT = 0, GNOLL = 1, CRAB = 2;

    private static boolean completedOnce;
    private static int questsActive;


    public Weapon weapon;
    public Armor armor;
    public Weapon.Enchantment enchant;
    public Armor.Glyph glyph;

    public GhostQuest() {
    }

    @Override
    public void initRandom(LevelScheme levelScheme) {
        if (type == BASED_ON_DEPTH) type = levelScheme.generateGhostQuestNotRandom();
        else if (type == RANDOM) type = Random.Int(3);

        if (armor == null || weapon == null) {
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
            boolean doEnchant = Random.Int(10) == 0;//10% to be enchanted. We store it separately so enchant status isn't revealed early

            if (armor == null) {
                //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
                switch (Random.chances(new float[]{0, 0, 10, 6, 3, 1})) {
                    default:
                    case 2:
                        armor = new LeatherArmor();
                        break;
                    case 3:
                        armor = new MailArmor();
                        break;
                    case 4:
                        armor = new ScaleArmor();
                        break;
                    case 5:
                        armor = new PlateArmor();
                        break;
                }
                armor.upgrade(itemLevel);
                if (doEnchant) glyph = Armor.Glyph.random();
            } else{
                if (armor.identifyOnStart) armor.identify();
                AugumentationSpinner.assignRandomAugumentation(armor);
            }
            if (weapon == null) {
                //50%:tier2, 30%:tier3, 15%:tier4, 5%:tier5
                int wepTier = Random.chances(new float[]{0, 0, 10, 6, 3, 1});
                weapon = (Weapon) Generator.randomUsingDefaults(Generator.wepTiers[wepTier - 1]);

                //clear weapon's starting properties
                weapon.level(0);
                weapon.enchant(null);
                weapon.cursed = false;
                weapon.upgrade(itemLevel);
                if (doEnchant) enchant = Weapon.Enchantment.random();
            } else{
                if (weapon.identifyOnStart) weapon.identify();
                AugumentationSpinner.assignRandomAugumentation(weapon);
            }
        }

    }

    public void complete() {
        weapon = null;
        armor = null;
        completedOnce = true;

        Notes.remove(Notes.Landmark.GHOST);
    }

    public static boolean completedOnce() {
        return completedOnce;
    }

    public void process() {
        if (given() && !completed()) {
            GLog.n(Messages.get(Ghost.class, "find_me"));
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
            super.complete();
            addScore(0, 1000);
            questsActive--;

            if (Dungeon.level.playsMusicFromRegion() == LevelScheme.REGION_SEWERS) {
                Game.runOnRenderThread(new Callback() {
                    @Override
                    public void call() {
                        Music.INSTANCE.fadeOut(1f, new Callback() {
                            @Override
                            public void call() {
                                if (Dungeon.level != null) {
                                    Dungeon.level.playLevelMusic();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.GHOST);
        questsActive++;
    }

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


    private static final String NODE = "sad_ghost";
    private static final String COMPLETED_ONCE = "completed_once";
    private static final String QUESTS_ACTIVE = "quests_active";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(COMPLETED_ONCE, completedOnce);
        node.put(QUESTS_ACTIVE, questsActive);
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        completedOnce = b.getBoolean(COMPLETED_ONCE);
        questsActive = b.getInt(QUESTS_ACTIVE);
    }

    public static boolean areQuestsActive() {
        return questsActive > 0;
    }

    public static void reset() {
        completedOnce = false;
        questsActive = 0;
    }

    @Override
    public int getNumQuests() {
        return 3;
    }
@Override
    public Image getIcon(){
        switch (type) {
            case RAT:
                return new FetidRatSprite();
            case GNOLL:
                return new GnollTricksterSprite();
            case CRAB:
                return new GreatCrabSprite();
        }
        return null;
    }

    @Override
    public String getMessageString() {
        return getMessageString(type);
    }

    @Override
    public String getMessageString(int type) {
        switch (type) {
            case RAT:
                return "rat";
            case GNOLL:
                return "gnoll";
            case CRAB:
                return "crab";
        }
        return null;
    }
}