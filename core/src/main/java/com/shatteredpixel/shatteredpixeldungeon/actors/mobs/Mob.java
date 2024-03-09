/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.Customizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PropertyItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.BiPredicate;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.IntFunction;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.GlyphArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.EnchantmentWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.LooseItemsTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public abstract class Mob extends Char implements Customizable {

	{
		actPriority = MOB_PRIO;
		
		alignment = Alignment.ENEMY;
	}

	public AiState SLEEPING     = new Sleeping();
	public AiState HUNTING		= new Hunting();
	public AiState WANDERING	= new Wandering();
	public AiState FLEEING		= new Fleeing();
	public AiState PASSIVE		= new Passive();
	public AiState state = SLEEPING;
	
	public Class<? extends CharSprite> spriteClass;
	
	protected int target = -1;
	public boolean following;

	public int defenseSkill = 0;//evasion
	public int attackSkill = 0;//accuracy
	public int damageRollMin = 0, damageRollMax = 0;
	public int specialDamageRollMin = 0, specialDamageRollMax = 0;
	public float statsScale = 1f;//only used in subclasses!
	public int tilesBeforeWakingUp = 100;

	public GlyphArmor glyphArmor = new GlyphArmor();
	public EnchantmentWeapon enchantWeapon = new EnchantmentWeapon();


	protected String customName, customDesc;
	public List<String> dialogs = new ArrayList<>(5);
	public boolean isBossMob;//only real value while playing, use level.bossmobAt instead!, not meant for shattered bosses except goo!
	public boolean showBossBar = true;
	public boolean bleeding;
	public String bossMusic;

	//Normal, Neutral, or Friedly; Neutral enemies behave similar as RatKing, cannot have boss bars
	public static final int NORMAL_ALIGNMENT   = 0;
	public static final int NEUTRAL_ALIGNMENT  = 1;
	public static final int FRIENDLY_ALIGNMENT = 2;
	public int playerAlignment;
	
	public int EXP = 1;
	public int maxLvl = Hero.MAX_LEVEL-1;
	
	protected Char enemy;
	protected int enemyID = -1; //used for save/restore
	protected boolean enemySeen;
	protected boolean alerted = false;

	protected static final float TIME_TO_WAKE_UP = 1f;

	protected boolean firstAdded = true;
	protected boolean hpSet = false;

	protected void onAdd(){
		if (firstAdded) {
			//modify health for ascension challenge if applicable, only on first add
			float percent = HP / (float) HT;
			HT = Math.round(HT * AscensionChallenge.statModifier(this));
			HP = Math.round(HT * percent);
			firstAdded = false;
		}
	}

	public boolean onDeleteLevelScheme(String name) {
		boolean changedSth = false;

		if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
			for (ItemsWithChanceDistrComp.ItemWithCount itemsWithCount : ((ItemsWithChanceDistrComp.RandomItemData) loot).distrSlots) {
				if (CustomDungeon.removeInvalidKeys(itemsWithCount.items, name)) {
					changedSth = true;
				}
			}
		}
		return changedSth;
	}

	public boolean onRenameLevelScheme(String oldName, String newName) {
		boolean changedSth = false;

		if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
			for (ItemsWithChanceDistrComp.ItemWithCount itemsWithCount : ((ItemsWithChanceDistrComp.RandomItemData) loot).distrSlots) {
				if (CustomDungeon.renameInvalidKeys(itemsWithCount.items, oldName, newName)) {
					changedSth = true;
				}
			}
		}
		return changedSth;
	}

	public void onMapSizeChange(IntFunction<Integer> newPosition, BiPredicate<Integer, Integer> isPositionValid) {
		if (turnToCell != -1) {
			int nTurn = newPosition.get(turnToCell);
			turnToCell = isPositionValid.test(turnToCell, nTurn) ? nTurn : -1;
		}
	}

	private static final String STATE	= "state";
	private static final String SEEN	= "seen";
	private static final String TARGET	= "target";
	private static final String MAX_LVL	= "max_lvl";
	private static final String DEFENSE_SKILL = "defense_skill";
	private static final String ATTACK_SKILL = "attack_skill";
	private static final String DAMAGE_ROLL_MIN = "damage_roll_min";
	private static final String DAMAGE_ROLL_MAX = "damage_roll_max";
	private static final String SPECIAL_DAMAGE_ROLL_MIN = "special_damage_roll_min";
	private static final String SPECIAL_DAMAGE_ROLL_MAX = "special_damage_roll_max";
	private static final String TILES_BEFORE_WAKING_UP = "tiles_before_waking_up";
	private static final String GLYPH_ARMOR = "glyph_armor";
	private static final String ENCHANT_WEAPON = "enchant_weapon";
	private static final String XP = "xp";
	private static final String STATS_SCALE = "stats_scale";
	private static final String IS_BOSS_MOB = "is_boss_mob";
	private static final String SHOW_BOSS_BAR = "show_boss_bar";
	private static final String BLEEDING = "bleeding";
	private static final String BOSS_MUSIC = "boss_music";
	private static final String PLAYER_ALIGNMENT = "player_alignment";
	private static final String FOLLOWING = "following";
	private static final String LOOT = "loot";
	private static final String CUSTOM_NAME = "custom_name";
	private static final String CUSTOM_DESC = "custom_desc";
	private static final String DIALOGS = "dialogs";
	private static final String NEXT_DIALOG = "next_dialog";
	public static final String SPRITE = "sprite";
	public static final String HP_SET = "hp_set";

	private static final String ENEMY_ID	= "enemy_id";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		
		super.storeInBundle( bundle );

		if (state == SLEEPING) {
			bundle.put( STATE, Sleeping.TAG );
		} else if (state == WANDERING) {
			bundle.put( STATE, Wandering.TAG );
		} else if (state == HUNTING) {
			bundle.put( STATE, Hunting.TAG );
		} else if (state == FLEEING) {
			bundle.put( STATE, Fleeing.TAG );
		} else if (state == PASSIVE) {
			bundle.put( STATE, Passive.TAG );
		}
		bundle.put( SEEN, enemySeen );
		bundle.put( TARGET, target );
		bundle.put( MAX_LVL, maxLvl );
		bundle.put( FOLLOWING, following );

        Mob defaultMob = DefaultStatsCache.getDefaultObject(getClass());
        if (defaultMob != null) {
            if (defaultMob.defenseSkill != defenseSkill) bundle.put(DEFENSE_SKILL, defenseSkill);
            if (defaultMob.attackSkill != attackSkill) bundle.put(ATTACK_SKILL, attackSkill);
            if (defaultMob.damageRollMin != damageRollMin) bundle.put(DAMAGE_ROLL_MIN, damageRollMin);
            if (defaultMob.damageRollMax != damageRollMax) bundle.put(DAMAGE_ROLL_MAX, damageRollMax);
            if (defaultMob.specialDamageRollMin != specialDamageRollMin) bundle.put(SPECIAL_DAMAGE_ROLL_MIN, specialDamageRollMin);
            if (defaultMob.specialDamageRollMax != specialDamageRollMax) bundle.put(SPECIAL_DAMAGE_ROLL_MAX, specialDamageRollMax);
            if (defaultMob.tilesBeforeWakingUp != tilesBeforeWakingUp) bundle.put(TILES_BEFORE_WAKING_UP, tilesBeforeWakingUp);
            if (defaultMob.EXP != EXP) bundle.put(XP, EXP);
            if (defaultMob.statsScale != statsScale) bundle.put(STATS_SCALE, statsScale);

            if (defaultMob.spriteClass != spriteClass) bundle.put(SPRITE, spriteClass);
        } else if (MobSpriteItem.canChangeSprite(this)) {
			if (Reflection.newInstance(getClass()).spriteClass != spriteClass) bundle.put(SPRITE, spriteClass);
		}
		if (hpSet) bundle.put(HP_SET, hpSet);

        bundle.put(GLYPH_ARMOR, glyphArmor);
        bundle.put(ENCHANT_WEAPON, enchantWeapon);
        bundle.put(IS_BOSS_MOB, isBossMob);
        bundle.put(SHOW_BOSS_BAR, showBossBar);
        bundle.put(BLEEDING, bleeding);
        if (bossMusic != null) bundle.put(BOSS_MUSIC, bossMusic);
        bundle.put(PLAYER_ALIGNMENT, playerAlignment);

        if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) bundle.put(LOOT, (Bundlable) loot);

		if (customName != null) bundle.put(CUSTOM_NAME, customName);
		if (customDesc != null) bundle.put(CUSTOM_DESC, customDesc);
		bundle.put(DIALOGS, dialogs.toArray(EditorUtilies.EMPTY_STRING_ARRAY));
		bundle.put(NEXT_DIALOG, nextDialog);

        if (enemy != null) {
            bundle.put(ENEMY_ID, enemy.id());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {

        super.restoreFromBundle(bundle);

		String state = bundle.getString( STATE );
		if (state.equals( Sleeping.TAG )) {
			this.state = SLEEPING;
		} else if (state.equals( Wandering.TAG )) {
			this.state = WANDERING;
		} else if (state.equals( Hunting.TAG )) {
			this.state = HUNTING;
		} else if (state.equals( Fleeing.TAG )) {
			this.state = FLEEING;
		} else if (state.equals( Passive.TAG )) {
			this.state = PASSIVE;
		}

		enemySeen = bundle.getBoolean( SEEN );

		target = bundle.getInt( TARGET );
		following = bundle.getBoolean( FOLLOWING );

		if (bundle.contains(MAX_LVL)) maxLvl = bundle.getInt(MAX_LVL);

		if (bundle.contains(ENEMY_ID)) {
			enemyID = bundle.getInt(ENEMY_ID);
		}

		//no need to actually save this, must be false
		firstAdded = false;

		if (bundle.contains(DEFENSE_SKILL)) defenseSkill = bundle.getInt(DEFENSE_SKILL);
		if (bundle.contains(ATTACK_SKILL)) attackSkill = bundle.getInt(ATTACK_SKILL);
		if (bundle.contains(DAMAGE_ROLL_MIN)) damageRollMin = bundle.getInt(DAMAGE_ROLL_MIN);
		if (bundle.contains(DAMAGE_ROLL_MAX)) damageRollMax = bundle.getInt(DAMAGE_ROLL_MAX);
		if (bundle.contains(SPECIAL_DAMAGE_ROLL_MIN)) specialDamageRollMin = bundle.getInt(SPECIAL_DAMAGE_ROLL_MIN);
		if (bundle.contains(SPECIAL_DAMAGE_ROLL_MAX)) specialDamageRollMax = bundle.getInt(SPECIAL_DAMAGE_ROLL_MAX);
		if (bundle.contains(TILES_BEFORE_WAKING_UP)) tilesBeforeWakingUp = bundle.getInt(TILES_BEFORE_WAKING_UP);
		if (bundle.contains(XP)) EXP = bundle.getInt(XP);
		if (bundle.contains(STATS_SCALE)) statsScale = bundle.getFloat(STATS_SCALE);
		hpSet = bundle.getBoolean(HP_SET);

		if (bundle.contains(SPRITE)) spriteClass = bundle.getClass(SPRITE);

		if (bundle.contains(CUSTOM_NAME)) customName = bundle.getString(CUSTOM_NAME);
		if (bundle.contains(CUSTOM_DESC)) customDesc = bundle.getString(CUSTOM_DESC);
		if (bundle.contains("dialog")) dialogs.add(bundle.getString("dialog"));
		else if (bundle.contains(DIALOGS)) dialogs.addAll(Arrays.asList(bundle.getStringArray(DIALOGS)));
		nextDialog = bundle.getInt(NEXT_DIALOG);

		glyphArmor = (GlyphArmor) bundle.get(GLYPH_ARMOR);
		enchantWeapon = (EnchantmentWeapon) bundle.get(ENCHANT_WEAPON);
		if (glyphArmor == null) glyphArmor = new GlyphArmor();
		if (enchantWeapon == null) enchantWeapon = new EnchantmentWeapon();

		if (bundle.contains(SHOW_BOSS_BAR)) showBossBar = bundle.getBoolean(SHOW_BOSS_BAR);
		if (bundle.contains(BOSS_MUSIC)) bossMusic = bundle.getString(BOSS_MUSIC);
		bleeding = bundle.getBoolean(BLEEDING);

		if (bundle.contains(IS_BOSS_MOB)) isBossMob = bundle.getBoolean(IS_BOSS_MOB);

		if (bundle.contains(LOOT)) loot = bundle.get(LOOT);
		if (bundle.contains("neutral_enemy")) playerAlignment = bundle.getBoolean("neutral_enemy") ? NEUTRAL_ALIGNMENT : NORMAL_ALIGNMENT;
		else playerAlignment = bundle.getInt( PLAYER_ALIGNMENT );
		setPlayerAlignment(playerAlignment);
	}

	public void setPlayerAlignment(int playerAlignment) {
		this.playerAlignment = playerAlignment;
		if (playerAlignment == NEUTRAL_ALIGNMENT) alignment = Alignment.NEUTRAL;
		else if (playerAlignment == FRIENDLY_ALIGNMENT) alignment = Alignment.ALLY;
		else if (alignment == Alignment.NEUTRAL || alignment == Alignment.ALLY)
			alignment = Reflection.newInstance(getClass()).alignment;//only works if alignment can't be changed otherwise
	}

	//mobs need to remember their targets after every actor is added
	public void restoreEnemy(){
		if (enemyID != -1 && enemy == null) enemy = (Char)Actor.findById(enemyID);
	}
	
	public CharSprite sprite() {
		return Reflection.newInstance(spriteClass);
	}
	
	@Override
	protected boolean act() {

		if (isBossMob && HP*2 > HT) {
			bleeding = false;
//            ((GooSprite)sprite).spray(false);
		}

		super.act();
		
		boolean justAlerted = alerted;
		alerted = false;
		
		if (justAlerted){
			sprite.showAlert();
		} else {
			sprite.hideAlert();
			sprite.hideLost();
		}
		
		if (paralysed > 0) {
			enemySeen = false;
			spend( TICK );
			return true;
		}

		if (buff(Terror.class) != null || buff(Dread.class) != null ){
			state = FLEEING;
		}
		
		enemy = chooseEnemy();
		
		boolean enemyInFOV = enemy != null && enemy.isAlive() && (fieldOfView[enemy.pos] && enemy.invisible <= 0 || following || buff(MindVision.class) != null);
		if (enemyInFOV && target == -1 && !justAlerted) justAlerted = true;

		//prevents action, but still updates enemy seen status
		if (buff(Feint.AfterImage.FeintConfusion.class) != null){
			enemySeen = enemyInFOV;
			spend( TICK );
			return true;
		}

		return state.act( enemyInFOV, justAlerted );
	}

	@Override
	public boolean interact(Char c) {
		if (playerAlignment == NEUTRAL_ALIGNMENT) {
			sprite.turnTo(pos, c.pos);
			if (c == Dungeon.hero) {
				if (state == SLEEPING) {
					notice();
					yell(Messages.get(RatKing.class, "not_sleeping"));
					state = WANDERING;
				} else {
					tellDialog();
				}
			}
		}
		if (playerAlignment != NEUTRAL_ALIGNMENT || c != Dungeon.hero) {
			return super.interact(c);
		}
		return true;
	}

	public int nextDialog = 0;
	protected void tellDialog() {
		if (dialogs.isEmpty()) {
			yell(Messages.get(this, "what_is_it"));
			return;
		}
		String dialog = dialogs.get(nextDialog);
		nextDialog = Math.min(nextDialog+1, dialogs.size()-1);
		String tell = Messages.get(dialog);
		if (tell == Messages.NO_TEXT_FOUND) tell = dialog;
		final String finalTell = tell;
		Game.runOnRenderThread(() -> GameScene.show(new WndQuest(this, finalTell)));
	}

	//FIXME this is sort of a band-aid correction for allies needing more intelligent behaviour
	public boolean intelligentAlly = false;
	
	protected Char chooseEnemy() {

		Dread dread = buff( Dread.class );
		if (dread != null) {
			Char source = (Char)Actor.findById( dread.object );
			if (source != null) {
				return source;
			}
		}

		Terror terror = buff( Terror.class );
		if (terror != null) {
			Char source = (Char)Actor.findById( terror.object );
			if (source != null) {
				return source;
			}
		}

		if (following) {
			if (playerAlignment == NORMAL_ALIGNMENT && !(this instanceof NPC)) return Dungeon.hero;
		}

		//if we are an alert enemy, auto-hunt a target that is affected by aggression, even another enemy
		if ((alignment == Alignment.ENEMY || buff(Amok.class) != null ) && state != PASSIVE && state != SLEEPING) {
			if (enemy != null && enemy.buff(StoneOfAggression.Aggression.class) != null){
				state = HUNTING;
				return enemy;
			}
			for (Char ch : Actor.chars()) {
				if (ch != this && fieldOfView[ch.pos] &&
						ch.buff(StoneOfAggression.Aggression.class) != null) {
					state = HUNTING;
					return ch;
				}
			}
		}

		//find a new enemy if..
		boolean newEnemy = false;
		//we have no enemy, or the current one is dead/missing
		if ( enemy == null || !enemy.isAlive() || !Actor.chars().contains(enemy) || state == WANDERING) {
			newEnemy = true;
		//We are amoked and current enemy is the hero
		} else if (buff( Amok.class ) != null && enemy == Dungeon.hero) {
			newEnemy = true;
		//We are charmed and current enemy is what charmed us
		} else if (buff(Charm.class) != null && buff(Charm.class).object == enemy.id()) {
			newEnemy = true;
		}

		//additionally, if we are an ally, find a new enemy if...
		if (!newEnemy && alignment == Alignment.ALLY){
			//current enemy is also an ally
			if (enemy.alignment == Alignment.ALLY){
				newEnemy = true;
			//current enemy is invulnerable
			} else if (enemy.isInvulnerable(getClass())){
				newEnemy = true;
			}
		}

		if ( newEnemy ) {

			HashSet<Char> enemies = new HashSet<>();
			boolean hasMindVision = buff(MindVision.class) != null;

			//if we are amoked...
			if ( buff(Amok.class) != null) {
				//try to find an enemy mob to attack first.
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY && mob != this
							&& (fieldOfView[mob.pos] && mob.invisible <= 0 || hasMindVision)) {
						enemies.add(mob);
					}
				
				if (enemies.isEmpty()) {
					//try to find ally mobs to attack second.
					for (Mob mob : Dungeon.level.mobs)
						if (mob.alignment == Alignment.ALLY && mob != this
								&& (fieldOfView[mob.pos] && mob.invisible <= 0 || hasMindVision)) {
							enemies.add(mob);
						}
					
					if (enemies.isEmpty()) {
						//try to find the hero third
						if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0 || hasMindVision) {
							enemies.add(Dungeon.hero);
						}
					}
				}
				
			//if we are an ally...
			} else if ( alignment == Alignment.ALLY ) {
				//look for hostile mobs to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ENEMY && (fieldOfView[mob.pos] && mob.invisible <= 0 || hasMindVision)
							&& !mob.isInvulnerable(getClass()))
						//do not target passive mobs
						//intelligent allies also don't target mobs which are wandering or asleep
						if (mob.state != mob.PASSIVE &&
								(!intelligentAlly || (mob.state != mob.SLEEPING && mob.state != mob.WANDERING))) {
							enemies.add(mob);
						}
				
			//if we are an enemy...
			} else if (alignment == Alignment.ENEMY) {
				//look for ally mobs to attack
				for (Mob mob : Dungeon.level.mobs)
					if (mob.alignment == Alignment.ALLY && (fieldOfView[mob.pos] && mob.invisible <= 0 || hasMindVision))
						enemies.add(mob);

				//and look for the hero
				if (fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0 || hasMindVision) {
					enemies.add(Dungeon.hero);
				}
				
			}

			//do not target anything that's charming us
			Charm charm = buff( Charm.class );
			if (charm != null){
				Char source = (Char)Actor.findById( charm.object );
				if (source != null && enemies.contains(source) && enemies.size() > 1){
					enemies.remove(source);
				}
			}

			//neutral characters in particular do not choose enemies.
			if (enemies.isEmpty()){
				return null;
			} else {
				//go after the closest potential enemy, preferring enemies that can be reached/attacked, and the hero if two are equidistant
				PathFinder.buildDistanceMap(pos, Dungeon.findPassable(this, Dungeon.level.getPassableVar(this), hasMindVision ? Dungeon.level.getPassableVar(this) : fieldOfView, true));
				Char closest = null;

				for (Char curr : enemies){
					if (closest == null){
						closest = curr;
					} else if (canAttack(closest) && !canAttack(curr)){
						continue;
					} else if ((canAttack(curr) && !canAttack(closest))
							|| (PathFinder.distance[curr.pos] < PathFinder.distance[closest.pos])){
						closest = curr;
					} else if ( curr == Dungeon.hero &&
							(PathFinder.distance[curr.pos] == PathFinder.distance[closest.pos]) || (canAttack(curr) && canAttack(closest))){
						closest = curr;
					}
				}
				//if we were going to target the hero, but an afterimage exists, target that instead
				if (closest == Dungeon.hero){
					for (Char ch : enemies){
						if (ch instanceof Feint.AfterImage){
							closest = ch;
							break;
						}
					}
				}

				return closest;
			}

		} else
			return enemy;
	}
	
	@Override
	public boolean add( Buff buff ) {
		if (super.add( buff )) {
			if (buff instanceof Amok || buff instanceof AllyBuff) {
				state = HUNTING;
			} else if (buff instanceof Terror || buff instanceof Dread) {
				state = FLEEING;
			} else if (buff instanceof Sleep) {
				state = SLEEPING;
				postpone(Sleep.SWS);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove( Buff buff ) {
		if (super.remove( buff )) {
			if ((buff instanceof Terror && buff(Dread.class) == null)
					|| (buff instanceof Dread && buff(Terror.class) == null)) {
				if (enemySeen) {
					sprite.showStatus(CharSprite.WARNING, Messages.get(this, "rage"));
					state = HUNTING;
				} else {
					state = WANDERING;
				}
			}
			return true;
		}
		return false;
	}
	
	protected boolean canAttack( Char enemy ) {
		if (Dungeon.level.adjacent( pos, enemy.pos )){
			return true;
		}
		for (ChampionEnemy buff : buffs(ChampionEnemy.class)){
			if (buff.canAttackWithExtraReach( enemy )){
				return true;
			}
		}
		if (enchantWeapon != null) {
			return enchantWeapon.canReach(this, enemy.pos);
		}
		return false;
	}

	protected boolean cellIsPathable( int cell ){
		if (!Barrier.canEnterCell(cell, this, isFlying() || buff(Amok.class) != null, true)){
			return false;
		}
		if (Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]){
			return false;
		}

		return true;
	}

	protected boolean getCloser( int target ) {
		
		if (rooted || target == pos) {
			return false;
		}

		int step = -1;

		if (Dungeon.level.adjacent( pos, target )) {

			path = null;

			if (cellIsPathable(target)) {
				step = target;
			}

		} else {

			boolean newPath = false;
			//scrap the current path if it's empty, no longer connects to the current location
			//or if it's extremely inefficient and checking again may result in a much better path
			if (path == null || path.isEmpty()
					|| !Dungeon.level.adjacent(pos, path.getFirst())
					|| path.size() > 2*Dungeon.level.distance(pos, target))
				newPath = true;
			else if (path.getLast() != target) {
				//if the new target is adjacent to the end of the path, adjust for that
				//rather than scrapping the whole path.
				if (Dungeon.level.adjacent(target, path.getLast())) {
					int last = path.removeLast();

					if (path.isEmpty()) {

						//shorten for a closer one
						if (Dungeon.level.adjacent(target, pos)) {
							path.add(target);
						//extend the path for a further target
						} else {
							path.add(last);
							path.add(target);
						}

					} else {
						//if the new target is simply 1 earlier in the path shorten the path
						if (path.getLast() == target) {

						//if the new target is closer/same, need to modify end of path
						} else if (Dungeon.level.adjacent(target, path.getLast())) {
							path.add(target);

						//if the new target is further away, need to extend the path
						} else {
							path.add(last);
							path.add(target);
						}
					}

				} else {
					newPath = true;
				}

			}

			//checks if the next cell along the current path can be stepped into
			if (!newPath) {
				int nextCell = path.removeFirst();
				if (!cellIsPathable(nextCell)) {

					newPath = true;
					//If the next cell on the path can't be moved into, see if there is another cell that could replace it
					if (!path.isEmpty()) {
						for (int i : PathFinder.NEIGHBOURS8) {
							if (Dungeon.level.adjacent(pos, nextCell + i) && Dungeon.level.adjacent(nextCell + i, path.getFirst())) {
								if (cellIsPathable(nextCell+i)){
									path.addFirst(nextCell+i);
									newPath = false;
									break;
								}
							}
						}
					}
				} else {
					path.addFirst(nextCell);
				}
			}

			//generate a new path
			if (newPath) {
				//If we aren't hunting, always take a full path
				PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.getPassableVar(this), fieldOfView, true);
				if (state != HUNTING){
					path = full;
				} else {
					//otherwise, check if other characters are forcing us to take a very slow route
					// and don't try to go around them yet in response, basically assume their blockage is temporary
					PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.getPassableVar(this), fieldOfView, false);
					if (ignoreChars != null && (full == null || full.size() > 2*ignoreChars.size())){
						//check if first cell of shorter path is valid. If it is, use new shorter path. Otherwise do nothing and wait.
						path = ignoreChars;
						if (!cellIsPathable(ignoreChars.getFirst())) {
							return false;
						}
					} else {
						path = full;
					}
				}
			}

			if (path != null) {
				step = path.removeFirst();
			} else {
				return false;
			}
		}
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}
	
	protected boolean getFurther( int target ) {
		if (rooted || target == pos) {
			return false;
		}
		
		int step = Dungeon.flee( this, target, Dungeon.level.getPassableVar(this), fieldOfView, true );
		if (step != -1) {
			move( step );
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void updateSpriteState() {
		super.updateSpriteState();
		if (Dungeon.hero != null
				&& (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null
				|| Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null))
			sprite.add( CharSprite.State.PARALYSED );
	}
	
	public float attackDelay() {
		float delay = 1f;
		if ( buff(Adrenaline.class) != null) delay /= 1.5f;
		return delay;
	}
	
	protected boolean doAttack( Char enemy ) {
		
		if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
			sprite.attack( enemy.pos );
			return false;
			
		} else {
			attack( enemy );
			Invisibility.dispel(this);
			spend( attackDelay() );
			return true;
		}
	}
	
	@Override
	public void onAttackComplete() {
		attack( enemy );
		Invisibility.dispel(this);
		spend( attackDelay() );
		super.onAttackComplete();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		if (playerAlignment == NEUTRAL_ALIGNMENT) return INFINITE_EVASION;
		if ( !surprisedBy(enemy)
				&& paralysed == 0
				&& !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
			return glyphArmor == null ? this.defenseSkill : Math.round(glyphArmor.evasionFactor(this, this.defenseSkill));
		} else {
			return 0;
		}
	}

	@Override
	public int attackSkill(Char target) {
		return attackSkill;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange(damageRollMin, damageRollMax);
	}

	@Override
	public int defenseProc( Char enemy, int damage ) {

		damage = glyphArmor.proc(enemy, this, damage);

		if (enemy instanceof Hero
				&& ((Hero) enemy).belongings.attackingWeapon() instanceof MissileWeapon){
			Statistics.thrownAttacks++;
			Badges.validateHuntressUnlock();
		}
		
		if (surprisedBy(enemy)) {
			Statistics.sneakAttacks++;
			Badges.validateRogueUnlock();
			//TODO this is somewhat messy, it would be nicer to not have to manually handle delays here
			// playing the strong hit sound might work best as another property of weapon?
			if (Dungeon.hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow
				|| Dungeon.hero.belongings.attackingWeapon() instanceof Dart){
				Sample.INSTANCE.playDelayed(Assets.Sounds.HIT_STRONG, 0.125f);
			} else {
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}
			if (enemy.buff(Preparation.class) != null) {
				Wound.hit(this);
			} else {
				Surprise.hit(this);
			}
		}

		//if attacked by something else than current target, and that thing is closer, switch targets
		if (this.enemy == null
				|| (enemy != this.enemy && (Dungeon.level.distance(pos, enemy.pos) < Dungeon.level.distance(pos, this.enemy.pos)))) {
			aggro(enemy);
			target = enemy.pos;
		}

		if (buff(SoulMark.class) != null) {
			int restoration = Math.min(damage, HP+shielding());
			
			//physical damage that doesn't come from the hero is less effective
			if (enemy != Dungeon.hero){
				restoration = Math.round(restoration * 0.4f*Dungeon.hero.pointsInTalent(Talent.SOUL_SIPHON)/3f);
			}
			if (restoration > 0) {
				Buff.affect(Dungeon.hero, Hunger.class).affectHunger(restoration*Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)/3f);

				if (Dungeon.hero.HP < Dungeon.hero.HT) {
					int heal = (int)Math.ceil(restoration * 0.4f);
					Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + heal);
					Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(heal), FloatingText.HEALING);
				}
			}
		}

		return super.defenseProc(enemy, damage);
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		if (enchantWeapon != null) {
			damage = enchantWeapon.proc( this, enemy, damage );
		}
		return damage;
	}

	@Override
	public boolean isImmune(Class effect) {
		if (effect == Burning.class
				&& glyphArmor != null
				&& glyphArmor.hasGlyph(Brimstone.class, this)){
			return true;
		}
		return super.isImmune(effect);
	}

	@Override
	public float speed() {
		float s = super.speed() * AscensionChallenge.enemySpeedModifier(this);
		return glyphArmor == null ? s : glyphArmor.speedFactor(this, s);
	}

	@Override
	public float stealth() {
		float sth = super.stealth();
		return glyphArmor == null ? sth : glyphArmor.stealthFactor(this, sth);
	}

	public final boolean surprisedBy(Char enemy ){
		return surprisedBy( enemy, true);
	}

	public boolean surprisedBy( Char enemy, boolean attacking ){
		return enemy == Dungeon.hero
				&& (enemy.invisible > 0 || !enemySeen || (fieldOfView != null && fieldOfView.length == Dungeon.level.length() && !fieldOfView[enemy.pos]))
				&& (!attacking || enemy.canSurpriseAttack());
	}

	//whether the hero should interact with the mob (true) or attack it (false)
	public boolean heroShouldInteract(){
		return alignment != Alignment.ENEMY && buff(Amok.class) == null;
	}

	public void aggro( Char ch ) {
		enemy = ch;
		if (state != PASSIVE){
			state = HUNTING;
		}
	}

	public void clearEnemy(){
		enemy = null;
		enemySeen = false;
		if (state == HUNTING) state = WANDERING;
	}
	
	public boolean isTargeting( Char ch){
		return enemy == ch;
	}

	@Override
	public void damage( int dmg, Object src ) {

		//TODO improve this when I have proper damage source logic
		if (glyphArmor != null && glyphArmor.hasGlyph(AntiMagic.class, this)
				&& AntiMagic.RESISTS.contains(src.getClass())){
			dmg -= AntiMagic.drRoll(this, glyphArmor.buffedLvl());
		}

		boolean bleedingCheck;
		if (isBossMob && !BossHealthBar.isAssigned(this)){
			BossHealthBar.addBoss( this );
			bleedingCheck = (HP*2 <= HT);
			if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
				Dungeon.level.seal();
				playBossMusic(Level.SPECIAL_MUSIC[Dungeon.level.levelScheme.getRegion()-1][Level.MUSIC_BOSS-1]);
			}
		} else bleedingCheck = false;

		if (!isInvulnerable(src.getClass())) {
			if (state == SLEEPING) {
				state = WANDERING;
			}
			if (state != HUNTING && !(src instanceof Corruption)) {
				alerted = true;
			}
		}
		
		super.damage( dmg, src );

		if (isBossMob) {
			if ((HP * 2 <= HT) && !bleedingCheck) {
				bleeding = true;
				sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
//                ((GooSprite) sprite).spray(true);
			}
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) {
				if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) lock.addTime(dmg);
				else lock.addTime(dmg * 1.5f);
			}
		}
	}
	
	
	@Override
	public void destroy() {
		
		super.destroy();
		
		Dungeon.level.mobs.remove( this );

		if (CustomDungeon.isEditing()) return;

		if (Dungeon.hero.buff(MindVision.class) != null){
			Dungeon.observe();
			GameScene.updateFog(pos, 2);
		}

		if (Dungeon.hero.isAlive()) {
			
			if (alignment == Alignment.ENEMY) {
				Statistics.enemiesSlain++;
				Badges.validateMonstersSlain();
				Statistics.qualifiedForNoKilling = false;

				AscensionChallenge.processEnemyKill(this);
				
				int exp = Dungeon.hero.lvl <= maxLvl ? EXP : 0;

				//during ascent, under-levelled enemies grant 10 xp each until level 30
				// after this enemy kills which reduce the amulet curse still grant 10 effective xp
				// for the purposes of on-exp effects, see AscensionChallenge.processEnemyKill
				if (Dungeon.hero.buff(AscensionChallenge.class) != null &&
						exp == 0 && maxLvl > 0 && EXP > 0 && Dungeon.hero.lvl < Hero.MAX_LEVEL){
					exp = Math.round(10 * spawningWeight());
				}

				if (exp > 0) {
					Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(exp), FloatingText.EXPERIENCE);
				}
				Dungeon.hero.earnExp(exp, getClass());

				if (Dungeon.hero.subClass == HeroSubClass.MONK){
					Buff.affect(Dungeon.hero, MonkEnergy.class).gainEnergy(this);
				}
			}
		}
	}
	
	@Override
	public void die( Object cause ) {

		if (cause == Chasm.class){
			//50% chance to round up, 50% to round down
			if (EXP % 2 == 1) EXP += Random.Int(2);
			EXP /= 2;
		}

		if (alignment == Alignment.ENEMY){
			rollToDropLoot();

			if (cause == Dungeon.hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment){
				if (Dungeon.hero.hasTalent(Talent.LETHAL_MOMENTUM)
						&& Random.Float() < 0.34f + 0.33f* Dungeon.hero.pointsInTalent(Talent.LETHAL_MOMENTUM)){
					Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 0f);
				}
				if (Dungeon.hero.heroClass != HeroClass.DUELIST
						&& Dungeon.hero.hasTalent(Talent.LETHAL_HASTE)
						&& Dungeon.hero.buff(Talent.LethalHasteCooldown.class) == null){
					Buff.affect(Dungeon.hero, Talent.LethalHasteCooldown.class, 100f);
					Buff.affect(Dungeon.hero, Haste.class, 1.67f + Dungeon.hero.pointsInTalent(Talent.LETHAL_HASTE));
				}
			}

		}

		if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
			GLog.i( Messages.get(this, "died") );
		}

		boolean soulMarked = buff(SoulMark.class) != null;

		super.die( cause );

		if (!(this instanceof Wraith)
				&& soulMarked
				&& Random.Float() < (0.4f*Dungeon.hero.pointsInTalent(Talent.NECROMANCERS_MINIONS)/3f)){
			Wraith w = Wraith.spawnAt(pos, Wraith.class);
			if (w != null) {
				Buff.affect(w, Corruption.class);
				if (Dungeon.level.heroFOV[pos]) {
					CellEmitter.get(pos).burst(ShadowParticle.CURSE, 6);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}
			}
		}

		Dungeon.level.stopSpecialMusic(id());
		if (isBossMob && playerAlignment == Mob.NORMAL_ALIGNMENT) {
			Dungeon.level.unseal();
			GameScene.bossSlain();
		}

		//enemy heroes can gain xp for the kill
		if (alignment == Alignment.ALLY) {
			for (Mob m : Dungeon.level.mobs) {
				if (m instanceof HeroMob && m.alignment == Alignment.ENEMY && m.isAlive()) {
					HeroMob heroMob = (HeroMob) m;
					if (heroMob.killedMob(cause)) {
						((HeroMob) m).earnExp(EXP, getClass());
					}
				}
			}
		}
	}

	public float lootChance(){
		float lootChance = this.lootChance;

		float dropBonus = RingOfWealth.dropChanceMultiplier( Dungeon.hero );

		Talent.BountyHunterTracker bhTracker = Dungeon.hero.buff(Talent.BountyHunterTracker.class);
		if (bhTracker != null){
			Preparation prep = Dungeon.hero.buff(Preparation.class);
			if (prep != null){
				// 2/4/8/16% per prep level, multiplied by talent points
				float bhBonus = 0.02f * (float)Math.pow(2, prep.attackLevel()-1);
				bhBonus *= Dungeon.hero.pointsInTalent(Talent.BOUNTY_HUNTER);
				dropBonus += bhBonus;
			}
		}

		return lootChance * dropBonus;
	}

	public static final int DROP_LOOT_IF_ABOVE_MAX_LVL = 2;
	public void rollToDropLoot(){
		if (Dungeon.hero.lvl > maxLvl + DROP_LOOT_IF_ABOVE_MAX_LVL && !(loot instanceof ItemsWithChanceDistrComp.RandomItemData)) return;

		if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
			this.lootChance = ((ItemsWithChanceDistrComp.RandomItemData) loot).lootChance();
		}

		MasterThievesArmband.StolenTracker stolen = buff(MasterThievesArmband.StolenTracker.class);
		if (stolen == null || !stolen.itemWasStolen()) {
			if (Random.Float() < lootChance()) {
				List<Item> loot = createActualLoot();
				RandomItem.replaceRandomItemsInList(loot);
				for (Item l : loot) {
					if (l == null) continue;
					increaseLimitedDropCount(l);
					doDropLoot(l);
				}
			}
		}
		
		//ring of wealth logic
		if (Ring.getBuffedBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
			int rolls = 1;
			if (properties.contains(Property.BOSS)) rolls = 15;
			else if (properties.contains(Property.MINIBOSS)) rolls = 5;
			ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
			if (bonus != null && !bonus.isEmpty()) {
				for (Item b : bonus) Dungeon.level.drop(b, pos).sprite.drop();
				RingOfWealth.showFlareForBonusDrop(sprite);
			}
		}
		
		//lucky enchant logic
		if (buff(Lucky.LuckProc.class) != null){
			Dungeon.level.drop(buff(Lucky.LuckProc.class).genLoot(), pos).sprite.drop();
			Lucky.showFlare(sprite);
		}

		//soul eater talent
		if (buff(SoulMark.class) != null &&
				Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)){
			Talent.onFoodEaten(Dungeon.hero, 0, null);
		}

	}

	protected void doDropLoot(Item item) {
		if (!item.spreadIfLoot || !LooseItemsTrap.dropAround(item, this, PathFinder.NEIGHBOURS8)) {
			Dungeon.level.drop(item, pos).sprite.drop();
		}
	}

	public Object loot = null;
	protected float lootChance = 0;

	public List<Item> createActualLoot() {
		if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) return ((ItemsWithChanceDistrComp.RandomItemData) loot).generateLoot();
		else return Arrays.asList(createLoot());
	}

	@SuppressWarnings("unchecked")
	protected Item createLoot() {
		Item item;
		if (loot instanceof Generator.Category) {

			item = Generator.randomUsingDefaults( (Generator.Category)loot );

		} else if (loot instanceof Class<?>) {

			item = Generator.random( (Class<? extends Item>)loot );

		} else {

			item = (Item)loot;

		}
		return item;
	}

	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = new ItemsWithChanceDistrComp.RandomItemData();
		if (loot instanceof Item || loot == Gold.class || loot instanceof Class<?>) {
			customLootInfo.addItem(createLoot(), 1);
			int noLoot = (int) (1f / lootChance - 1);
			if (noLoot > 0) customLootInfo.setLootChance(noLoot);
		}
		return customLootInfo;
	}

	public void increaseLimitedDropCount(Item generatedLoot) {

	}

	public float spawningWeight_NOT_SAVED_IN_BUNDLE = 1;
	//how many mobs this one should count as when determining spawning totals
	public float spawningWeight(){
		return spawningWeight_NOT_SAVED_IN_BUNDLE;
	}
	
	public boolean reset() {
		return false;
	}
	
	public void beckon( int cell ) {
		
		notice();
		
		if (state != HUNTING && state != FLEEING) {
			state = WANDERING;
		}
		target = cell;
	}

	@Override
	public String name() {
		String msg;
		return customName == null ? super.name() : Messages.NO_TEXT_FOUND.equals(msg = Messages.get(customName)) ? customName : msg;
	}

	public String description() {
		String msg;
		return customDesc == null ? Messages.get(this, "desc") : Messages.NO_TEXT_FOUND.equals(msg = Messages.get(customDesc)) ? customDesc : msg;
	}

	public String info(){
		StringBuilder desc = new StringBuilder(description());

		for (Buff b : buffs(ChampionEnemy.class)){
			desc.append("\n\n_").append(Messages.titleCase(b.name())).append("_\n").append(b.desc());
		}

		if (playerAlignment == NEUTRAL_ALIGNMENT) {
			desc.append("\n\n").append(Messages.get(this, "neutral_desc"));
		} else if (playerAlignment == FRIENDLY_ALIGNMENT) {
			desc.append("\n\n").append(Messages.get(this, "friendly_desc"));
		}
		if (following) {
			if (playerAlignment == NORMAL_ALIGNMENT) desc.append('\n');
			desc.append('\n').append(Messages.get(this, "following_desc"));
		}

        Mob defaultStats = DefaultStatsCache.getDefaultObject(getClass());
        if (defaultStats != null) {

			HashSet<Char.Property> defProps = defaultStats.properties;
			HashSet<Char.Property> props = properties;

            if (DefaultStatsCache.useStatsScale(this)) {
                if (defaultStats.baseSpeed != baseSpeed || defaultStats.statsScale != statsScale
						|| defaultStats.viewDistance != viewDistance
						|| defaultStats.tilesBeforeWakingUp != tilesBeforeWakingUp
						|| !defProps.equals(props)
                        || this instanceof Brute && (
                        defaultStats.HT != HT || defaultStats.damageReductionMax != damageReductionMax
                                || defaultStats.attackSkill != attackSkill || defaultStats.defenseSkill != defenseSkill
                                || defaultStats.EXP != EXP
                                || loot instanceof ItemsWithChanceDistrComp.RandomItemData
								|| glyphArmor.hasGlyphs() || enchantWeapon.hasEnchantments()
                )) {
                    desc.append("\n\n").append(Messages.get(Mob.class, "base_stats_changed"));
                    if (defaultStats.statsScale != statsScale)
                        desc.append('\n').append(Messages.get(Mob.class, "stats_scale")).append(": ").append(defaultStats.statsScale).append(" -> _").append(statsScale).append('_');
                    if (defaultStats.baseSpeed != baseSpeed)
                        desc.append('\n').append(Messages.get(StoneOfAugmentation.WndAugment.class, "speed")).append(": ").append(defaultStats.baseSpeed).append(" -> _").append(baseSpeed).append('_');
                    if (defaultStats.viewDistance != viewDistance)
                        desc.append('\n').append(Messages.get(Mob.class, "view_distance")).append(": ").append(defaultStats.viewDistance).append(" -> _").append(viewDistance).append('_');
                    if (this instanceof Brute) {
                        desc.append(infoStatsChangedHPAccuracyEvasionArmor(defaultStats));
                    }

					if (defaultStats.tilesBeforeWakingUp != tilesBeforeWakingUp)
						desc.append('\n').append(Messages.get(Mob.class, "tiles_before_waking_up")).append(": ").append(defaultStats.tilesBeforeWakingUp).append(" -> _").append(tilesBeforeWakingUp).append('_');
					if (defaultStats.EXP != EXP && !(this instanceof HeroMob))
                        desc.append('\n').append(Messages.get(Mob.class, "xp")).append(": ").append(defaultStats.EXP).append(" -> _").append(EXP).append('_');
                    if (defaultStats.maxLvl != maxLvl)
                        desc.append('\n').append(Messages.get(Mob.class, "max_lvl")).append(": ").append(defaultStats.maxLvl).append(" -> _").append(maxLvl).append('_');
                }
            } else {

                if (!DefaultStatsCache.areStatsEqual(defaultStats, this)
                        || loot instanceof ItemsWithChanceDistrComp.RandomItemData
					|| glyphArmor.hasGlyphs() || enchantWeapon.hasEnchantments()) {
					desc.append("\n\n").append(Messages.get(Mob.class, "base_stats_changed"));

                    if (defaultStats.baseSpeed != baseSpeed)
						desc.append('\n').append(Messages.get(StoneOfAugmentation.WndAugment.class, "speed")).append(": ").append(defaultStats.baseSpeed).append(" -> _").append(baseSpeed).append('_');
					if (defaultStats.viewDistance != viewDistance)
						desc.append('\n').append(Messages.get(Mob.class, "view_distance")).append(": ").append(defaultStats.viewDistance).append(" -> _").append(viewDistance).append('_');
					desc.append(infoStatsChangedHPAccuracyEvasionArmor(defaultStats));
                    if (defaultStats.damageRollMin != damageRollMin)
                        desc.append('\n').append(Messages.get(Mob.class, "dmg_min")).append(": ").append(defaultStats.damageRollMin).append(" -> _").append(damageRollMin).append('_');
                    if (defaultStats.damageRollMax != damageRollMax)
                        desc.append('\n').append(Messages.get(Mob.class, "dmg_max")).append(": ").append(defaultStats.damageRollMax).append(" -> _").append(damageRollMax).append('_');
                    if (defaultStats.specialDamageRollMin != specialDamageRollMin)
                        desc.append('\n').append(Messages.get(Mob.class, "special_dmg_min")).append(": ").append(defaultStats.specialDamageRollMin).append(" -> _").append(specialDamageRollMin).append('_');
                    if (defaultStats.specialDamageRollMax != specialDamageRollMax)
						desc.append('\n').append(Messages.get(Mob.class, "special_dmg_max")).append(": ").append(defaultStats.specialDamageRollMax).append(" -> _").append(specialDamageRollMax).append('_');

					if (defaultStats.tilesBeforeWakingUp != tilesBeforeWakingUp)
                        desc.append('\n').append(Messages.get(Mob.class, "tiles_before_waking_up")).append(": ").append(defaultStats.tilesBeforeWakingUp).append(" -> _").append(tilesBeforeWakingUp).append('_');
                    if (defaultStats.EXP != EXP)
                        desc.append('\n').append(Messages.get(Mob.class, "xp")).append(": ").append(defaultStats.EXP).append(" -> _").append(EXP).append('_');
					if (defaultStats.maxLvl != maxLvl)
						desc.append('\n').append(Messages.get(Mob.class, "max_lvl")).append(": ").append(defaultStats.maxLvl + 2).append(" -> _").append(maxLvl + 2).append('_');
                }

            }

			if (glyphArmor.hasGlyphs() || enchantWeapon.hasEnchantments()) {
				desc.append("\n_").append(Messages.get(Mob.class, "enchantments")).append("_: ");
				if (glyphArmor.hasGlyphs() && enchantWeapon.hasEnchantments()) {
					desc.append(enchantWeapon.info()).append(", ").append(glyphArmor.info());
				} else if (glyphArmor.hasGlyphs()) {
					desc.append(glyphArmor.info());
				} else {
					desc.append(enchantWeapon.info());
				}
			}
            if (loot instanceof ItemsWithChanceDistrComp.RandomItemData) {
				desc.append('\n').append(Messages.get(Mob.class, "loot"));
			}
			if (!defProps.equals(props)) {
				desc.append("\n_").append(Messages.get(Mob.class, "properties")).append("_: ");
				for (Property p : props) {
					desc.append(PropertyItem.getName(p)).append(", ");
				}
				if (props.size() > 0) desc.delete(desc.length() - 2, desc.length());
				else desc.append(Messages.get(Barrier.class, "block_none"));
			}
        }

        return desc.toString();
    }

    private String infoStatsChangedHPAccuracyEvasionArmor(Mob defaultStats) {
        String ret = "";
        if (defaultStats.HT != HT)
            ret += "\n" + Messages.get(Mob.class, "hp") + ": " + defaultStats.HT + " -> _" + HT + "_";
        if (defaultStats.attackSkill != attackSkill)
            ret += "\n" + Messages.get(Mob.class, "accuracy") + ": " + defaultStats.attackSkill + " -> _" + attackSkill + "_";
        if (defaultStats.defenseSkill != defenseSkill)
            ret += "\n" + Messages.get(StoneOfAugmentation.WndAugment.class, "evasion") + ": " + defaultStats.defenseSkill + " -> _" + defenseSkill + "_";
        if (defaultStats.damageReductionMax != damageReductionMax)
            ret += "\n" + Messages.get(Mob.class, "armor") + ": " + defaultStats.damageReductionMax + " -> _" + damageReductionMax + "_";
        return ret;
    }

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(String name) {
		customName = name;
	}

	@Override
	public String getCustomDesc() {
		return customDesc;
	}

	@Override
	public void setCustomDesc(String desc) {
		customDesc = desc;
	}

    public void addBossProperty() {
        if (isBossMob) properties.add(Property.BOSS);
    }

    public void notice() {
        sprite.showAlert();
		if (playerAlignment != NORMAL_ALIGNMENT) return;

		if (isBossMob) {
            if (!BossHealthBar.isAssigned(this)) {
                BossHealthBar.addBoss(this);
				if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
					Dungeon.level.seal();
					playBossMusic(Level.SPECIAL_MUSIC[Dungeon.level.levelScheme.getRegion()-1][Level.MUSIC_BOSS-1]);
				}
//                yell(Messages.get(this, "notice"));
//                for (Char ch : Actor.chars()) {
//                    if (ch instanceof DriedRose.GhostHero) {
//                        ((DriedRose.GhostHero) ch).sayBoss();
//                    }
//                }
            }
        }
    }

    public void yell(String str) {
        GLog.newLine();
        GLog.n("%s: \"%s\" ", Messages.titleCase(name()), str);
    }

	protected void playBossMusic(String defaultMusic) {
		String play = bossMusic == null ? defaultMusic : bossMusic;
		if (!play.equals("/"))
			Dungeon.level.playSpecialMusic(play, id());
	}

	public interface AiState {
		boolean act( boolean enemyInFOV, boolean justAlerted );
	}

	protected class Sleeping implements AiState {

		public static final String TAG	= "SLEEPING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {

			//debuffs cause mobs to wake as well
			for (Buff b : buffs()){
				if (b.type == Buff.buffType.NEGATIVE){
					awaken(enemyInFOV);
					if (state == SLEEPING){
						spend(TICK); //wait if we can't wake up for some reason
					}
					return true;
				}
			}

			if (enemyInFOV) {

				float enemyStealth = enemy.stealth();

				if (enemy instanceof Hero && ((Hero) enemy).hasTalent(Talent.SILENT_STEPS)){
					if (Dungeon.level.distance(pos, enemy.pos) >= 4 - ((Hero) enemy).pointsInTalent(Talent.SILENT_STEPS)) {
						enemyStealth = Float.POSITIVE_INFINITY;
					}
				}
				if (distance( enemy) > tilesBeforeWakingUp ) enemyStealth = Float.POSITIVE_INFINITY;

				if (Random.Float( distance( enemy ) + enemyStealth ) < 1) {
					awaken(enemyInFOV);
					if (state == SLEEPING){
						spend(TICK); //wait if we can't wake up for some reason
					}
					return true;
				}

			}

			enemySeen = false;
			spend( TICK );

			return true;
		}

		protected void awaken( boolean enemyInFOV ){
			if (enemyInFOV) {
				enemySeen = true;
				notice();
				state = HUNTING;
				target = enemy.pos;
			} else {
				notice();
				state = WANDERING;
				target = randomDestination();
			}

			if (alignment == Alignment.ENEMY && Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.paralysed <= 0
							&& Dungeon.level.distance(pos, mob.pos) <= 8
							&& mob.state != mob.HUNTING) {
						mob.beckon(target);
					}
				}
			}
			spend(TIME_TO_WAKE_UP);
		}
	}

	protected class Wandering implements AiState {

		public static final String TAG	= "WANDERING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV && (justAlerted || distance( enemy ) <= tilesBeforeWakingUp && Random.Float( distance( enemy ) / 2f + enemy.stealth() ) < 1)) {

				return noticeEnemy();

			} else {

				if (following) target = Dungeon.hero.pos;
				return continueWandering();

			}
		}
		
		protected boolean noticeEnemy(){
			enemySeen = true;
			
			notice();
			alerted = true;
			state = HUNTING;
			target = enemy.pos;
			
			if (alignment == Alignment.ENEMY && Dungeon.isChallenged( Challenges.SWARM_INTELLIGENCE )) {
				for (Mob mob : Dungeon.level.mobs) {
					if (mob.paralysed <= 0
							&& Dungeon.level.distance(pos, mob.pos) <= 8
							&& mob.state != mob.HUNTING) {
						mob.beckon( target );
					}
				}
			}
			
			return true;
		}
		
		protected boolean continueWandering(){
			enemySeen = false;
			
			int oldPos = pos;
			if (target != -1 && getCloser( target )) {
				spend( 1 / speed() );
				return moveSprite( oldPos, pos );
			} else {
				target = randomDestination();
				spend( TICK );
			}
			
			return true;
		}

		protected int randomDestination(){
			return Mob.this.randomDestination();
		}

	}

	protected int randomDestination(){
		return following ? Dungeon.hero.pos : Dungeon.level.randomDestination( Mob.this );
	}

	protected class Hunting implements AiState {

		public static final String TAG	= "HUNTING";

		//prevents rare infinite loop cases
		private boolean recursing = false;

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				target = enemy.pos;

				if (enemy.invisible > 0) {
					spend( TICK );
					return true;
				}

				return doAttack( enemy );

			} else {

				if (enemyInFOV) {
					target = enemy.pos;
				} else if (enemy == null) {
					looseEnemy();
					spend( TICK );
					return true;
				}
				
				int oldPos = pos;
				if (target != -1 && getCloser( target )) {
					
					spend( 1 / speed() );
					return moveSprite( oldPos,  pos );

				} else {

					//if moving towards an enemy isn't possible, try to switch targets to another enemy that is closer
					//unless we have already done that and still can't move toward them, then move on.
					if (!recursing) {
						Char oldEnemy = enemy;
						enemy = null;
						enemy = chooseEnemy();
						if (enemy != null && enemy != oldEnemy) {
							recursing = true;
							boolean result = act(enemyInFOV, justAlerted);
							recursing = false;
							return result;
						}
					}

					spend( TICK );
					if (!enemyInFOV) {
						looseEnemy();
					}
					return true;
				}
			}
		}

		protected void looseEnemy(){
			sprite.showLost();
			state = WANDERING;
			target = following ? Dungeon.hero.pos : ((Mob.Wandering)WANDERING).randomDestination();
		}
	}

	protected class Fleeing implements AiState {

		public static final String TAG	= "FLEEING";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			//triggers escape logic when 0-dist rolls a 6 or greater.
			if (enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(pos, target)) >= 6){
				escaped();
				if (state != FLEEING){
					spend( TICK );
					return true;
				}
			
			//if enemy isn't in FOV, keep running from their previous position.
			} else if (enemyInFOV) {
				target = enemy.pos;
			}

			int oldPos = pos;
			if (target != -1 && getFurther( target )) {

				spend( 1 / speed() );
				return moveSprite( oldPos, pos );

			} else {

				spend( TICK );
				nowhereToRun();

				return true;
			}
		}

		protected void escaped(){
			//does nothing by default, some enemies have special logic for this
		}

		//enemies will turn and fight if they have nowhere to run and aren't affect by terror
		protected void nowhereToRun() {
			if (buff( Terror.class ) == null
					&& buffs( AllyBuff.class ).isEmpty()
					&& buff( Dread.class ) == null) {
				if (enemySeen) {
					sprite.showStatus(CharSprite.WARNING, Messages.get(Mob.class, "rage"));
					state = HUNTING;
				} else {
					state = WANDERING;
				}
			}
		}
	}

	protected class Passive implements AiState {

		public static final String TAG	= "PASSIVE";

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			enemySeen = enemyInFOV;
			spend( TICK );
			return true;
		}
	}
	
	
	private static ArrayList<Mob> heldAllies = new ArrayList<>();

	public static void holdAllies( Level level ){
		holdAllies(level, Dungeon.hero.pos);
	}

	public static void holdAllies( Level level, int holdFromPos ){
		heldAllies.clear();
		for (Mob mob : level.mobs.toArray( new Mob[0] )) {
			//preserve directable allies no matter where they are
			if (mob instanceof DirectableAlly) {
				((DirectableAlly) mob).clearDefensingPos();
				level.mobs.remove( mob );
				heldAllies.add(mob);
				
			//preserve intelligent allies if they are near the hero
			} else if (mob.alignment == Alignment.ALLY
					&& mob.intelligentAlly
					&& Dungeon.level.distance(holdFromPos, mob.pos) <= 5){
				level.mobs.remove( mob );
				heldAllies.add(mob);
			}
		}
	}

	public static void restoreAllies( Level level, int pos ){
		restoreAllies(level, pos, -1);
	}

	public static void restoreAllies( Level level, int pos, int gravitatePos ){
		if (!heldAllies.isEmpty()){
			
			ArrayList<Integer> candidatePositions = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS8) {
				if (!Dungeon.level.solid[i+pos] && !Dungeon.level.avoid[i+pos] && level.findMob(i+pos) == null){
					candidatePositions.add(i+pos);
				}
			}

			//gravitate pos sets a preferred location for allies to be closer to
			if (gravitatePos == -1) {
				Collections.shuffle(candidatePositions);
			} else {
				Collections.sort(candidatePositions, new Comparator<Integer>() {
					@Override
					public int compare(Integer t1, Integer t2) {
						return Dungeon.level.distance(gravitatePos, t1) -
								Dungeon.level.distance(gravitatePos, t2);
					}
				});
			}
			
			for (Mob ally : heldAllies) {
				level.mobs.add(ally);
				ally.state = ally.WANDERING;
				
				if (!candidatePositions.isEmpty()){
					ally.pos = candidatePositions.remove(0);
				} else {
					ally.pos = pos;
				}
				if (ally.sprite != null) ally.sprite.place(ally.pos);

				if (ally.fieldOfView == null || ally.fieldOfView.length != level.length()){
					ally.fieldOfView = new boolean[level.length()];
				}
				Dungeon.level.updateFieldOfView( ally, ally.fieldOfView );
				
			}
		}
		heldAllies.clear();
	}
	
	public static void clearHeldAllies(){
		heldAllies.clear();
	}
}