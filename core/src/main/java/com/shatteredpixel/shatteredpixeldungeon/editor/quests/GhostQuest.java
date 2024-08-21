package com.shatteredpixel.shatteredpixeldungeon.editor.quests;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.*;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GreatCrabSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GhostQuest extends Quest {

    public static final int RAT = 0, GNOLL = 1, CRAB = 2;

    private static boolean completedOnce;
    private static List<String> questsActive = new ArrayList<>(3);


    public Weapon weapon;
    public Armor armor;
    public Weapon.Enchantment enchant;
    public Armor.Glyph glyph;


    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        return super.doOnAllGameObjects(whatToDo)
                | doOnSingleObject(weapon, whatToDo, newValue -> weapon = newValue)
                | doOnSingleObject(armor, whatToDo, newValue -> armor = newValue);
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
            boolean doEnchant = Random.Float() < 0.2f * ParchmentScrap.enchantChanceMultiplier();//20% to be enchanted. We store it separately so enchant status isn't revealed early

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
            } else {
                GameObject.doOnSingleObject(armor, GameObject::initRandoms, newValue -> armor = newValue);
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
            } else {
                GameObject.doOnSingleObject(weapon, GameObject::initRandoms, newValue -> weapon = newValue);
            }
        } else {
            GameObject.doOnSingleObject(armor, GameObject::initRandoms, newValue -> armor = newValue);
            GameObject.doOnSingleObject(weapon, GameObject::initRandoms, newValue -> weapon = newValue);
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

    public void process(Char questGiver) {
        if (given() && !completed()) {
            GLog.n(Messages.get(Ghost.class, "find_me"));
            Sample.INSTANCE.play(Assets.Sounds.GHOST);
            super.complete();
            addScore(0, 1000);
            questsActive.remove(Dungeon.levelName);

            Dungeon.level.stopSpecialMusic(questGiver.id());
        }
    }

    @Override
    public void start() {
        super.start();
        Notes.add(Notes.Landmark.GHOST);
        questsActive.add(Dungeon.levelName);
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
    private static final String QUESTS_ACTIVE_LIST = "quests_active_list";

    public static void storeStatics(Bundle bundle) {
        Bundle node = new Bundle();
        node.put(COMPLETED_ONCE, completedOnce);
        node.put(QUESTS_ACTIVE_LIST, questsActive.toArray(EditorUtilities.EMPTY_STRING_ARRAY));
        bundle.put(NODE, node);
    }

    public static void restoreStatics(Bundle bundle) {
        Bundle b = bundle.getBundle(NODE);
        completedOnce = b.getBoolean(COMPLETED_ONCE);
        questsActive.clear();
        if (bundle.contains(QUESTS_ACTIVE_LIST)) {
            questsActive.addAll(Arrays.asList(b.getStringArray(QUESTS_ACTIVE_LIST)));
        }
    }

    public static boolean areQuestsActive() {
        return questsActive.contains(Dungeon.levelName);
    }

    public static void reset() {
        completedOnce = false;
        questsActive.clear();
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