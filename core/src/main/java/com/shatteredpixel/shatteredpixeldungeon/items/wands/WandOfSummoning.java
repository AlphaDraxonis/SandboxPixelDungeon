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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WandOfSummoning extends Wand {

	{
		image = ItemSpriteSheet.WAND_SUMMONING;

		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.STOP_BARRIER_PROJECTILES;
	}

	//see SpawnerMob
	protected Class<? extends Mob> defaultTemplateClass;
	public List<Mob> summonTemplate = new ArrayList<>();

	private Set<Mob> summonRotation;


	public Mob nextSummon;

	protected Set<Char> currentSummons = new HashSet<>();
	private int[] loadedIDs;

	{
		defaultTemplateClass = Wraith.class;
		Mob summon = Reflection.newInstance(defaultTemplateClass);
		summon.state = summon.WANDERING;
		summonTemplate.clear();
		summonTemplate.add(summon);
	}
	
	@Override
	public boolean tryToZap(Hero owner, int target) {

		removeDeadSummons();

		final Ballistica wandShot = new Ballistica(owner.pos, target, collisionProperties(target), null);

		int cell = wandShot.collisionPos;

		Char ch = Actor.findChar(cell);
		if (ch != null && !currentSummons.contains(ch)){
			if (owner == Dungeon.hero) GLog.w(Messages.get(this, "bad_location"));
			return false;
		}

		if (owner != Dungeon.hero) {
			if (nextSummon == null) chooseNextSummonMob();
			if (nextSummon == null
					|| !Barrier.canEnterCell(cell, nextSummon, nextSummon.isFlying() || nextSummon.buff(Amok.class) != null, true)
					|| Char.hasProp(nextSummon, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) {
				return false;
			}
		}

		return super.tryToZap(owner, target);
	}
	
	@Override
	public void onZap(Ballistica bolt) {

		removeDeadSummons();

		int target = bolt.collisionPos;
		Char ch = Actor.findChar(target);
		if (ch != null && !currentSummons.contains(ch)){
			if (bolt.dist > 1) target = bolt.path.get(bolt.dist-1);

			ch = Actor.findChar(target);
			if (ch != null && !currentSummons.contains(ch)){
				GLog.w( Messages.get(this, "bad_location"));
				return;
			}
		}

		int chargesPerCast = chargesPerCast();

		if (ch != null) {
			if (currentSummons.contains(ch)) {

				float healingPerCharge = healingPerCharge(buffedLvl());
				int before = ch.HP;
				int maxHeal = (int) (ch.HT * healingPerCharge * chargesPerCast);
				ch.HP = Math.min(ch.HT, ch.HP + maxHeal);
				float notHealedPercentage = (float) (maxHeal - ch.HP + before) / ch.HT;
				curCharges += (int) (notHealedPercentage / healingPerCharge);
				if (Dungeon.level.heroFOV[ch.pos]) {
					ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(ch.HP - before), FloatingText.HEALING);
				}
			} else {
				GLog.w(Messages.get(this, "bad_location"));
			}

		} else {
			if (nextSummon == null) chooseNextSummonMob();
			if (nextSummon == null
				|| !Barrier.canEnterCell(target, nextSummon, nextSummon.isFlying() || nextSummon.buff(Amok.class) != null, true)
				|| Char.hasProp(nextSummon, Char.Property.LARGE) && !Dungeon.level.openSpace[target]){
				GLog.w(Messages.get(this, "bad_location"));
			} else {

				if (!(nextSummon instanceof Wraith)) currentSummons.add(nextSummon);

				Buff.affect(nextSummon, Corruption.class);

				if (!(nextSummon instanceof Wraith)) {
					nextSummon.HP = (int) Math.min(nextSummon.HT, nextSummon.HP * healingPerCharge(buffedLvl()) * chargesPerCast);
				}

				nextSummon.pos = target;
				nextSummon.EXP = 0;
				nextSummon.loot = null;

				GameScene.add(nextSummon, 1f);

				Dungeon.level.occupyCell(nextSummon);

				Wraith.showSpawnParticle(nextSummon);

				chooseNextSummonMob();

			}
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar( curUser.sprite.parent,
				MagicMissile.SHADOW,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play( Assets.Sounds.ZAP );
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		int level = Math.max( 0, staff.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 - 33%
		// lvl 2 - 43%
		float procChance = (level+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {

			float powerMulti = Math.max(1f, procChance) * 0.5f;

			removeDeadSummons();

			if (!currentSummons.isEmpty()) {
				Char heal = Random.element(currentSummons);
				int before = heal.HP;
				heal.HP = (int) Math.min(heal.HT, before + heal.HT * healingPerCharge(buffedLvl()) * powerMulti);
				int healed = heal.HP - before;
				if (healed > 0 && Dungeon.level.heroFOV[heal.pos]) {
					heal.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healed), FloatingText.HEALING);
				}
			}
		}
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color( 0 );
		particle.am = 0.6f;
		particle.setLifespan(2f);
		particle.speed.set(0, 5);
		particle.setSize( 0.5f, 2f);
		particle.shuffleXY(1f);
	}

	@Override
	public String statsDesc() {
		if (levelKnown()) {
			int useCharges = chargesPerCast();
			float healPercent = healingPerCharge(level());
			float totalHealPercent = useCharges * healPercent;
			if (nextSummon == null) chooseNextSummonMob();
			if (nextSummon instanceof Wraith) {
				return Messages.get(this, "stats_desc_wraith", nextSummon.name());
			}
			else return Messages.get(this, "stats_desc", nextSummon.name(), ((int) (totalHealPercent * 100)) + "%", useCharges);
		} else {
			int useCharges = chargesPerCast();
			float healPercent = healingPerCharge(0);
			float totalHealPercent = useCharges * healPercent;
			if (nextSummon == null) chooseNextSummonMob();
			if (nextSummon instanceof Wraith) {
				return Messages.get(this, "stats_desc_wraith", nextSummon.name());
			}
			else return Messages.get(this, "stats_desc", nextSummon.name(), totalHealPercent, useCharges);

		}
	}

	@Override
	protected int chargesPerCast() {
		if (cursed || charger != null && charger.target.buff(WildMagic.WildMagicTracker.class) != null){
			return 1;
		}

		if (nextSummon == null) chooseNextSummonMob();
		if (nextSummon instanceof Wraith) {
			return 1;
		}

		//consumes up to 10 charges at once (unused charges are later readded)
		return Math.min(curCharges, 10);
	}


//	@Override
//	public String description() {
//		if (customDesc != null) return customDesc;
//		String desc = super.description();
//		int size = summonTemplate.size();
//		if (size == 0)
//			desc += "\n\n" + Messages.get(this, "summon_none", name());
//		else if (size > 1 && summonTemplate.get(0).getClass() != defaultTemplateClass) {
//			desc += "\n\n" + Messages.get(this, "summon", name());
//			size--;
//			for (int i = 0; i < size; i++) {
//				desc += " _" + summonTemplate.get(i).name() + "_,";
//			}
//			desc += " _" + summonTemplate.get(size).name() + "_";
//		}
//		return desc;
//	}

	private static float healingPerCharge(int level) {
		//10% base, 3% for each level
		return 0.1f + 0.03f * level;
	}

	protected Mob chooseNextSummonMob() {
		if (summonRotation == null) summonRotation = new HashSet<>(5);
		if (summonRotation.isEmpty()) summonRotation.addAll(summonTemplate);
		Mob m = Random.element(summonRotation);
		summonRotation.remove(m);
		nextSummon = (Mob) m.getCopy();
		return nextSummon;
	}

	@Override
	public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
		return super.doOnAllGameObjects(whatToDo)
				| doOnAllGameObjectsList(summonTemplate, whatToDo);
	}

	private static final String SUMMONING_TEMPLATE = "summoning_templates";
	private static final String SUMMON_ROTATION = "summon_rotation";

	private static final String NEXT_SUMMON = "next_summon";
	private static final String CURRENT_SUMMONS = "current_summons";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);

		bundle.put(SUMMONING_TEMPLATE, summonTemplate);
		if (summonRotation != null && !CustomDungeon.isEditing()) bundle.put(SUMMON_ROTATION, summonRotation);

		if (!CustomDungeon.isEditing())
			bundle.put(NEXT_SUMMON, nextSummon);

		if (!currentSummons.isEmpty()){
			int[] intArray = new int[currentSummons.size()];
			int i = 0;
			for (Char ch : currentSummons) {
				if (ch != null) intArray[i++] = ch.id();
			}
			bundle.put( CURRENT_SUMMONS, intArray );
		} else if (loadedIDs != null) {
			bundle.put(CURRENT_SUMMONS, loadedIDs);
		}
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);

		summonTemplate.clear();
		for (Bundlable b : bundle.getCollection(SUMMONING_TEMPLATE))
				summonTemplate.add((Mob) b);
		if (bundle.contains(SUMMON_ROTATION)) {
			summonRotation = new HashSet<>(5);
			for (Bundlable b : bundle.getCollection(SUMMON_ROTATION))
				summonRotation.add((Mob) b);
		}

		if (!CustomDungeon.isEditing()) {
			nextSummon = (Mob) bundle.get(NEXT_SUMMON);
		}

		currentSummons.clear();
		if (bundle.contains( CURRENT_SUMMONS )){
			loadedIDs = bundle.getIntArray(CURRENT_SUMMONS);
		}
	}

	private void removeDeadSummons() {
		if (loadedIDs != null) {
			for (int i = 0; i < loadedIDs.length; i++) {
				currentSummons.add((Char) Actor.findById(loadedIDs[i]));
			}
			loadedIDs = null;
		}
		for (Char ch : currentSummons.toArray(new Mob[0])) {
			if (!ch.isAlive()) {
				currentSummons.remove(ch);
			}
		}
	}
}