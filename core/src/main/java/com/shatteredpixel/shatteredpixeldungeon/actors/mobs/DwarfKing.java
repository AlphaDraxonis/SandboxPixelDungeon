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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DwarfKing extends Mob implements MobBasedOnDepth {

	{
		spriteClass = KingSprite.class;

		HP = HT = 300;
		EXP = 40;
		defenseSkill = 22;
		attackSkill = 26;
		damageRollMin = 15;
		damageRollMax = 25;
		damageReductionMax = 10;

		loot = new KingsCrown();
		lootChance = 1f;

		properties.add(Property.BOSS);
		properties.add(Property.UNDEAD);
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 15, 25 );
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 26;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 10);
//	}

	private int phase = 1;
	private int summonsMade = 0;

	private float summonCooldown = 0;
	private float abilityCooldown = 0;
	private final int MIN_COOLDOWN = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 8 : 10;
	private final int MAX_COOLDOWN = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 10 : 14;

	private int lastAbility = 0;
	private static final int NONE = 0;
	private static final int LINK = 1;
	private static final int TELE = 2;

	private int initialThrone = -1;
	private boolean yelledWavePhase2;

	private static final String PHASE = "phase";
	private static final String SUMMONS_MADE = "summons_made";

	private static final String SUMMON_CD = "summon_cd";
	private static final String ABILITY_CD = "ability_cd";
	private static final String LAST_ABILITY = "last_ability";
	private static final String YELLED = "yelled";
	private static final String INITIAL_THRONE = "initial_throne";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( PHASE, phase );
		bundle.put( SUMMONS_MADE, summonsMade );
		bundle.put( SUMMON_CD, summonCooldown );
		bundle.put( ABILITY_CD, abilityCooldown );
		bundle.put( LAST_ABILITY, lastAbility );
		bundle.put( YELLED, yelledWavePhase2 );
		bundle.put( INITIAL_THRONE, initialThrone );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		phase = bundle.getInt( PHASE );
		summonsMade = bundle.getInt( SUMMONS_MADE );
		summonCooldown = bundle.getFloat( SUMMON_CD );
		abilityCooldown = bundle.getFloat( ABILITY_CD );
		lastAbility = bundle.getInt( LAST_ABILITY );
		yelledWavePhase2 = bundle.getBoolean( YELLED );
		initialThrone = bundle.getInt( INITIAL_THRONE );

		if (phase == 2) properties.add(Property.IMMOVABLE);
	}

	@Override
	public void setLevel(int depth) {
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
			HT = (int) (HT * 1.5f);
			if (!hpSet) {
				HP = HT;
				hpSet = !CustomDungeon.isEditing();
			}
		}
	}

	@Override
	protected void onAdd() {
		super.onAdd();
		if (initialThrone <= 0) {
			initialThrone = pos;
			if (!(Dungeon.level instanceof CityBossLevel)) {
				Dungeon.level.setPassableLater(initialThrone,false);
				phase = 0;
			}
		}
	}

	@Override
	protected boolean act() {

		if (pos == thronePosition()){
			throwItems();
		}

		if (phase == 1) {

			if (summonCooldown <= 0 && summonSubject(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2 : 3)){
				summonsMade++;
				summonCooldown += Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);
			} else if (summonCooldown > 0){
				summonCooldown--;
			}

			if (paralysed > 0){
				spend(TICK);
				return true;
			}

			if (abilityCooldown <= 0){

				if (lastAbility == NONE) {
					//50/50 either ability
					lastAbility = Random.Int(2) == 0 ? LINK : TELE;
				} else if (lastAbility == LINK) {
					//more likely to use tele
					lastAbility = Random.Int(8) == 0 ? LINK : TELE;
				} else {
					//more likely to use link
					lastAbility = Random.Int(8) != 0 ? LINK : TELE;
				}

				if (lastAbility == LINK && lifeLinkSubject()){
					abilityCooldown += Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);
					spend(TICK);
					return true;
				} else if (teleportSubject()) {
					lastAbility = TELE;
					abilityCooldown += Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);
					spend(TICK);
					return true;
				}

			} else {
				abilityCooldown--;
			}

		} else if (phase == 2){

			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
				//challenge logic
				if (summonsMade < 6){
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_1"));
					}
					if (summonSubject(3, DKGhoul.class)) summonsMade++;
					if (summonsMade < 6)
						if (summonSubject(3, DKGhoul.class)) summonsMade++;
					spend(3 * TICK);
					if (summonsMade == 6) yelledWavePhase2 = false;
					return true;
				} else if (shielding() <= 300 && summonsMade < 12){
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_2"));
					}
					if (summonsMade == 6 || summonsMade == 9) {
						if (summonSubject(3, DKGhoul.class)) summonsMade++;
					}
					if (summonsMade == 7 || summonsMade == 10) {
						if (summonSubject(3, DKGhoul.class)) summonsMade++;
					}
					if (summonsMade == 8) {
						if (summonSubject(3, DKMonk.class)) summonsMade++;
					} else if (summonsMade == 11) {
						if (summonSubject(3, DKWarlock.class)) summonsMade++;
					}
					if (summonsMade == 12) yelledWavePhase2 = false;
					spend(3*TICK);
					return true;
				} else if (shielding() <= 150 && summonsMade < 18) {
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_3"));
					}
					if (summonsMade < 16) {
						if (summonsMade == 12)
							if (summonSubject(3, DKWarlock.class)) summonsMade++;
						if (summonsMade == 13)
							if (summonSubject(3, DKMonk.class)) summonsMade++;
						if (summonsMade == 14)
							if (summonSubject(3, DKGhoul.class)) summonsMade++;
						if (summonsMade == 15)
							if (summonSubject(3, DKGhoul.class)) summonsMade++;
						spend(3 * TICK);
					} else {
						if (summonsMade == 16)
							if (summonSubject(3, DKGhoul.class)) summonsMade++;
						if (summonsMade == 17)
							if (summonSubject(3, DKGhoul.class)) summonsMade++;
						if (summonsMade == 18) yelledWavePhase2 = false;
						spend(TICK);
					}
					return true;
				} else {
					spend(TICK);
					return true;
				}
			} else {
				//non-challenge logic
				if (summonsMade < 4) {
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_1"));
					}
					if (summonSubject(3, DKGhoul.class)) summonsMade++;
					if (summonsMade == 4) yelledWavePhase2 = false;
					spend(3 * TICK);
					return true;
				} else if (shielding() <= 200 && summonsMade < 8) {
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_2"));
					}
					if (summonsMade == 7) {
						if (summonSubject(3, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class)) summonsMade++;
					} else {
						if (summonSubject(3, DKGhoul.class)) summonsMade++;
					}
					if (summonsMade == 8) yelledWavePhase2 = false;
					spend(TICK);
					return true;
				} else if (shielding() <= 100 && summonsMade < 12) {
					if (!yelledWavePhase2) {
						yelledWavePhase2 = true;
						sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.4f, 2);
						Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
						yell(Messages.get(this, "wave_3"));
					}
					if (summonsMade == 8)
						if (summonSubject(4, DKWarlock.class)) summonsMade++;
					if (summonsMade == 9)
						if (summonSubject(4, DKMonk.class)) summonsMade++;
					if (summonsMade == 10)
						if (summonSubject(4, DKGhoul.class)) summonsMade++;
					if (summonsMade == 11)
						if (summonSubject(4, DKGhoul.class)) summonsMade++;
					if (summonsMade == 12) yelledWavePhase2 = false;

					spend(TICK);
					return true;
				} else {
					spend(TICK);
					return true;
				}
			}
		} else if (phase == 3 && buffs(Summoning.class).size() < 4){
			if (summonSubject(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2 : 3)) summonsMade++;
		}

		return super.act();
	}

	private boolean summonSubject( int delay ){
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
			//every 3rd summon is always a monk or warlock, otherwise ghoul
			//except every 9th summon, which is a golem!
			if (summonsMade % 3 == 2) {
				if (summonsMade % 9 == 8){
					return summonSubject(delay, DKGolem.class);
				} else {
					return summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
				}
			} else {
				return summonSubject(delay, DKGhoul.class);
			}

		} else {
			//every 4th summon is always a monk or warlock, otherwise ghoul
			if (summonsMade % 4 == 3) {
				return summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
			} else {
				return summonSubject(delay, DKGhoul.class);
			}
		}
	}

	private boolean summonSubject( int delay, Class<?extends DwarfKingMob> type ){
		Summoning s = new Summoning(id(), playerAlignment);
		s.pos = getSummoningPos();
		if (s.pos == -1) {
			return false;
		}
		s.summon = type;
		s.delay = delay;
		s.attachTo(this);
		return true;
	}

	private List<Integer> getPedestalsInRange(int position){//range=3; add pos to the values!
		if (Dungeon.level instanceof CityBossLevel) return null;
		int width = Dungeon.level.width();
		int width2 = width * 2;
		int width3 = width * 3;
		int[] possiblePos = {
				-3 - width3,-2 - width3, -1 - width3, -width3, 1 - width3, 2 - width3, 3 - width3,
				-3 - width2,-2 - width2, -1 - width2, -width2, 1 - width2, 2 - width2, 3 - width2,
				-3 - width , -2 - width, 								   2 - width,  3 - width,
				-3         , -2        , 								   2,		   3,
				-3 + width , -2 + width, 								   2 + width,  3 + width,
				-3 + width2,-2 + width2, -1 + width2,  width2, 1 + width2, 2 + width2, 3 + width2,
				-3 + width3,-2 + width3, -1 + width3,  width3, 1 + width3, 2 + width3, 3 + width3,
		};
		List<Integer> actualPedestals = new ArrayList<>();
		for (int i : possiblePos) {
			int cell = i + position;
			if (cell >= 0 && cell < Dungeon.level.map.length
					&& Dungeon.level.map[cell] == Terrain.PEDESTAL) actualPedestals.add(i);
		}
		return actualPedestals;
	}

	private int getSummoningPos(){
		if (Dungeon.level instanceof CityBossLevel) return ((CityBossLevel) Dungeon.level).getSummoningPos();

		List<Integer> actualPedestals = getPedestalsInRange(pos);
//		if (actualPedestals.isEmpty()) {
//			for (int i : possiblePos) actualPedestals.add(i);
//		}

		Set<Summoning> summons = new HashSet<>();
		for (Mob m : Dungeon.level.mobs) {
			if (m instanceof DwarfKing) {
				summons.addAll(m.buffs(DwarfKing.Summoning.class));
			}
		}
		ArrayList<Integer> positions = new ArrayList<>();
		for (int i : actualPedestals) {
			boolean clear = true;
			int pedestal = i + pos;
			for (DwarfKing.Summoning s : summons) {
				if (s.getPos() == pedestal) {
					clear = false;
					break;
				}
			}
			if (clear && Dungeon.level.isPassable(pedestal, this) && Actor.findChar(pedestal) == null) {
				positions.add(pedestal);
			}
		}
		if (positions.isEmpty()) return -1;
		else return Random.element(positions);
	}

	private boolean skipPhase2(){
		List<Integer> actualPedestals = getPedestalsInRange(initialThrone);
		return actualPedestals != null && actualPedestals.isEmpty();
	}

	private int thronePosition(){
		return Dungeon.level instanceof CityBossLevel ? CityBossLevel.throne : initialThrone;
	}

	public HashSet<Mob> getSubjects(){
		HashSet<Mob> subjects = new HashSet<>();
		for (Mob m : Dungeon.level.mobs){
			if (m.alignment == alignment
					//&& (m instanceof Ghoul || m instanceof Monk || m instanceof Warlock || m instanceof Golem)
					&& m instanceof DwarfKingMob && ((DwarfKingMob) m).getKingId() == id()) {
				subjects.add(m);
			}
		}
		return subjects;
	}

	private boolean lifeLinkSubject(){
		Mob furthest = null;

		for (Mob m : getSubjects()){
			boolean alreadyLinked = false;
			for (LifeLink l : m.buffs(LifeLink.class)){
				if (l.object == id()) alreadyLinked = true;
			}
			if (!alreadyLinked) {
				if (furthest == null || Dungeon.level.distance(pos, furthest.pos) < Dungeon.level.distance(pos, m.pos)){
					furthest = m;
				}
			}
		}

		if (furthest != null) {
			Buff.append(furthest, LifeLink.class, 100f).object = id();
			Buff.append(this, LifeLink.class, 100f).object = furthest.id();
			yell(Messages.get(this, "lifelink_" + Random.IntRange(1, 2)));
			sprite.parent.add(new Beam.HealthRay(sprite.destinationCenter(), furthest.sprite.destinationCenter()));
			return true;

		}
		return false;
	}

	private boolean teleportSubject(){
		if (enemy == null) return false;

		Mob furthest = null;

		for (Mob m : getSubjects()){
			if (furthest == null || Dungeon.level.distance(pos, furthest.pos) < Dungeon.level.distance(pos, m.pos)){
				furthest = m;
			}
		}

		if (furthest != null){

			float bestDist;
			int bestPos = pos;

			Ballistica trajectory = new Ballistica(enemy.pos, pos, Ballistica.STOP_TARGET, null);
			int targetCell = trajectory.path.get(trajectory.dist+1);
			//if the position opposite the direction of the hero is open, go there
			if (Actor.findChar(targetCell) == null && !Dungeon.level.solid[targetCell]){
				bestPos = targetCell;

			//Otherwise go to the neighbour cell that's open and is furthest
			} else {
				bestDist = Dungeon.level.trueDistance(pos, enemy.pos);

				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null
							&& !Dungeon.level.solid[pos+i]
							&& Dungeon.level.trueDistance(pos+i, enemy.pos) > bestDist){
						bestPos = pos+i;
						bestDist = Dungeon.level.trueDistance(pos+i, enemy.pos);
					}
				}
			}

			Actor.add(new Pushing(this, pos, bestPos));
			pos = bestPos;

			//find closest cell that's adjacent to enemy, place subject there
			bestDist = Dungeon.level.trueDistance(enemy.pos, pos);
			bestPos = enemy.pos;
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(enemy.pos+i) == null
						&& !Dungeon.level.solid[enemy.pos+i]
						&& Dungeon.level.trueDistance(enemy.pos+i, pos) < bestDist){
					bestPos = enemy.pos+i;
					bestDist = Dungeon.level.trueDistance(enemy.pos+i, pos);
				}
			}

			if (bestPos != enemy.pos) ScrollOfTeleportation.appear(furthest, bestPos);
			yell(Messages.get(this, "teleport_" + Random.IntRange(1, 2)));
			return true;
		}
		return false;
	}

	@Override
	public void notice() {
		super.notice();
		phase = Math.max(phase, 1);
		if (playerAlignment != NORMAL_ALIGNMENT) return;

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
				if (!(Dungeon.level instanceof CityBossLevel)) {
					if (showBossBar) Dungeon.level.seal();
					playBossMusic(Assets.Music.CITY_BOSS);
				}
				yell(Messages.get(this, "notice"));
				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayBoss(DwarfKing.class);
					}
				}
			}
		}
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		if (effect == KingDamager.class){
			return false;
		} else {
			return phase == 2 || super.isInvulnerable(effect);
		}
	}

	@Override
	public void damage(int dmg, Object src) {

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			if (showBossBar && playerAlignment == NORMAL_ALIGNMENT && !(Dungeon.level instanceof PrisonBossLevel)) {
				Dungeon.level.seal();
				playBossMusic(Assets.Music.CITY_BOSS);
			}
		}

		phase = Math.max(phase, 1);

		//hero counts as unarmed if they aren't attacking with a weapon and aren't benefiting from force
		if (src == Dungeon.hero && (!RingOfForce.fightingUnarmed(Dungeon.hero) || Dungeon.hero.buff(RingOfForce.Force.class) != null)){
			Statistics.qualifiedForBossChallengesBadge[3] = false;
		//Corrosion, corruption, and regrowth do no direct damage and so have their own custom logic
		//Transfusion damages DK and so doesn't need custom logic
		//Lightning has custom logic so that chaining it doesn't DQ for the badge
		} else if (src instanceof Wand && !(src instanceof WandOfLightning)){
			Statistics.qualifiedForBossChallengesBadge[3] = false;
		}

		if (isInvulnerable(src.getClass())){
			super.damage(dmg, src);
			return;
		} else if (phase == 3 && !(src instanceof Viscosity.DeferedDamage)){
			if (dmg >= 0) {
				Viscosity.DeferedDamage deferred = Buff.affect( this, Viscosity.DeferedDamage.class );
				deferred.extend( dmg );

				sprite.showStatus( CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", dmg) );
			}
			return;
		}
		int preHP = HP;
		super.damage(dmg, src);

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmg/5f);
			else                                                    lock.addTime(dmg/3f);
		}

		if (phase == 1) {
			int dmgTaken = preHP - HP;
			abilityCooldown -= dmgTaken/8f;
			summonCooldown -= dmgTaken/8f;
			if (HP <= (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? HT/4.5f : HT/6f)) {
				HP = (int) (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? HT/4.5f : HT/6f);
				phase = skipPhase2() ? 3 : 2;
				summonsMade = 0;
				sprite.idle();
				if (phase == 2) {
					sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));
					ScrollOfTeleportation.appear(this, thronePosition());
					properties.add(Property.IMMOVABLE);
					Buff.affect(this, DKBarrior.class).setShield(HT);
					for (Summoning s : buffs(Summoning.class)) {
						s.detach();
					}
				}
				Bestiary.skipCountingEncounters = true;
				for (Mob m : getSubjects()) {
					m.die(null);
				}
				Bestiary.skipCountingEncounters = false;
				for (Buff b: buffs()){
					if (b instanceof LifeLink){
						b.detach();
					}
				}
			}
		} else if (phase == 2 && shielding() == 0) {
			properties.remove(Property.IMMOVABLE);
			phase = 3;
			summonsMade = 1; //monk/warlock on 3rd summon
			sprite.centerEmitter().start( Speck.factory( Speck.SCREAM ), 0.4f, 2 );
			Sample.INSTANCE.play( Assets.Sounds.CHALLENGE );
			yell(  Messages.get(this, "enraged", Dungeon.hero.name()) );
			bleeding = true;
			if (Dungeon.level instanceof CityBossLevel) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.fadeOut(0.5f, new Callback() {
							@Override
							public void call() {
								Music.INSTANCE.play(Assets.Music.CITY_BOSS_FINALE, true);
							}
						});
					}
				});
			} else if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
				if (bossMusic == null) Dungeon.level.playSpecialMusic(Assets.Music.CITY_BOSS_FINALE, id());
			}
		} else if (phase == 3 && preHP > 20 && HP < 20 && isAlive()){
			yell( Messages.get(this, "losing") );
		}
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || phase != 3;
	}

	@Override
	public void die(Object cause) {

		if (showBossBar) GameScene.bossSlain();

		super.die( cause );

		if (!(Dungeon.level instanceof CityBossLevel)) Dungeon.level.setPassableLater(initialThrone, true);

		Heap h = Dungeon.level.heaps.get(thronePosition());
		if (h != null) {
			for (Item i : h.items) {
				Dungeon.level.drop(i, thronePosition() + Dungeon.level.width());
			}
			h.destroy();
		}

		if (pos == CityBossLevel.throne){
			Dungeon.level.drop(new KingsCrown(), pos + Dungeon.level.width()).sprite.drop(pos);
		} else {
			Dungeon.level.drop(new KingsCrown(), pos).sprite.drop();
		}

		Badges.validateBossSlain(DwarfKing.class);
		if (Statistics.qualifiedForBossChallengesBadge[3]){
			Badges.validateBossChallengeCompleted(DwarfKing.class);
		}
		Statistics.bossScores[3] += 4000;

		Dungeon.level.unseal();

		Bestiary.skipCountingEncounters = true;
		for (Mob m : getSubjects()){
			m.die(null);
		}
		Bestiary.skipCountingEncounters = false;

		yell( Messages.get(this, "defeated") );

		Dungeon.level.stopSpecialMusic(id());
	}

	@Override
	protected void doDropLoot(Item item) {
		if (item.spreadIfLoot) super.doDropLoot(item);
		if (Dungeon.level.solid[pos]) {
			Dungeon.level.drop(item, pos + Dungeon.level.width()).sprite.drop(pos);
		} else {
			Dungeon.level.drop(item, pos).sprite.drop();
		}
	}

	@Override
	public boolean isImmune(Class effect) {
		//immune to damage amplification from doomed in 2nd phase or later, but it can still be applied
		if (phase > 1 && effect == Doom.class && buff(Doom.class) != null ){
			return true;
		}
		return super.isImmune(effect);
	}

	protected static final String KING_ID = "king_id";

	protected static DwarfKing findKingById(int id) {
		if (id == 0) {
			for (Mob m : Dungeon.level.mobs) if (m instanceof DwarfKing) return (DwarfKing) m;
		}
		Actor c = Actor.findById(id);
		if (c instanceof DwarfKing) return (DwarfKing) c;
		return null;
	}

	protected interface DwarfKingMob{
		void setKingId(int id);
		int getKingId();
	}

	public static class DKGhoul extends Ghoul implements DwarfKingMob{
		{
			properties.add(Property.BOSS_MINION);
			state = HUNTING;
		}

		@Override
		protected boolean act() {
			partnerID = -2; //no partners
			return super.act();
		}

		private int kingID;

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(KING_ID, kingID);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			kingID = bundle.getInt(KING_ID);
		}

		@Override
		public void setKingId(int id) {
			kingID = id;
		}

		@Override
		public int getKingId() {
			return kingID;
		}
	}

	public static class DKMonk extends Monk implements DwarfKingMob {
		{
			properties.add(Property.BOSS_MINION);
			state = HUNTING;
		}

		private int kingID;

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(KING_ID, kingID);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			kingID = bundle.getInt(KING_ID);
		}

		@Override
		public void setKingId(int id) {
			kingID = id;
		}

		@Override
		public int getKingId() {
			return kingID;
		}
	}

	public static class DKWarlock extends Warlock implements DwarfKingMob {
		{
			properties.add(Property.BOSS_MINION);
			state = HUNTING;
		}

		@Override
		public void zap() {
			if (enemy == Dungeon.hero){
				Statistics.bossScores[3] -= 400;
			}
			super.zap();
		}

		private int kingID;

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(KING_ID, kingID);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			kingID = bundle.getInt(KING_ID);
		}

		@Override
		public void setKingId(int id) {
			kingID = id;
		}

		@Override
		public int getKingId() {
			return kingID;
		}
	}

	public static class DKGolem extends Golem implements DwarfKingMob {
		{
			properties.add(Property.BOSS_MINION);
			state = HUNTING;
		}

		private int kingID;

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(KING_ID, kingID);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			kingID = bundle.getInt(KING_ID);
		}

		@Override
		public void setKingId(int id) {
			kingID = id;
		}

		@Override
		public int getKingId() {
			return kingID;
		}
	}

	public static class Summoning extends Buff {

		private int delay;
		private int pos;
		private Class<?extends DwarfKingMob> summon;

		private Emitter particles;

		private int kingID;
		private int playerAlignment;

		public Summoning(){
			kingID = -1;
		}
		public Summoning(int kingID, int playerAlignment){
			this.kingID = kingID;
			this.playerAlignment = playerAlignment;
		}

		public int getPos() {
			return pos;
		}

		@Override
		public boolean act() {
			delay--;

			if (delay <= 0){

				if (summon == DKGolem.class){
					particles.burst(SparkParticle.FACTORY, 10);
					Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
				} else if (summon == DKWarlock.class){
					particles.burst(ShadowParticle.CURSE, 10);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				} else if (summon == DKMonk.class){
					particles.burst(ElmoParticle.FACTORY, 10);
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				} else {
					particles.burst(Speck.factory(Speck.BONE), 10);
					Sample.INSTANCE.play(Assets.Sounds.BONES);
				}
				particles = null;

				if (Actor.findChar(pos) != null){
					ArrayList<Integer> candidates = new ArrayList<>();
					for (int i : PathFinder.NEIGHBOURS8){
						if (Dungeon.level.isPassable(pos+i, (Char) Actor.findById(kingID)) && Actor.findChar(pos+i) == null){
							candidates.add(pos+i);
						}
					}
					if (!candidates.isEmpty()){
						pos = Random.element(candidates);
					}
				}

				//kill sheep that are right on top of the spawner instead of failing to spawn
				if (Actor.findChar(pos) instanceof Sheep){
					Actor.findChar(pos).die(null);
				}

				if (Actor.findChar(pos) == null) {
					Mob m = (Mob) Reflection.newInstance(summon);
					((DwarfKingMob) m).setKingId(kingID);
					m.setPlayerAlignment(playerAlignment);
					m.pos = pos;
					m.maxLvl = -2;
					GameScene.add(m);
					Dungeon.level.occupyCell(m);
					m.state = m.HUNTING;
					if (((DwarfKing)target).phase == 2){
						Buff.affect(m, KingDamager.class).kingID = kingID;
					}
				} else {
					Char ch = Actor.findChar(pos);
					ch.damage(Random.NormalIntRange(20, 40), this);
					if (((DwarfKing)target).phase == 2){
						target.damage((int) Math.ceil(target.HT/(Dungeon.isChallenged(Challenges.STRONGER_BOSSES)? 18d : 12d)),
								new KingDamager(kingID));
					}
					if (!ch.isAlive() && ch == Dungeon.hero) {
						Dungeon.fail(DwarfKing.class);
						GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", Messages.get(DwarfKing.class, "name"))));
					}
				}

				detach();
			}

			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			if (on && !alwaysHidesFx && (particles == null || particles.parent == null)) {
				particles = CellEmitter.get(pos);

				if (summon == DKGolem.class){
					particles.pour(SparkParticle.STATIC, 0.05f);
				} else if (summon == DKWarlock.class){
					particles.pour(ShadowParticle.UP, 0.1f);
				} else if (summon == DKMonk.class){
					particles.pour(ElmoParticle.FACTORY, 0.1f);
				} else {
					particles.pour(Speck.factory(Speck.RATTLE), 0.1f);
				}

			} else if (!on && particles != null) {
				particles.on = false;
			}
		}

		private static final String DELAY = "delay";
		private static final String POS = "pos";
		private static final String SUMMON = "summon";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DELAY, delay);
			bundle.put(POS, pos);
			bundle.put(SUMMON, summon);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			delay = bundle.getInt(DELAY);
			pos = bundle.getInt(POS);
			summon = bundle.getClass(SUMMON);
			if (target != null) {
				kingID = target.id();
				playerAlignment = ((DwarfKing)target).playerAlignment;
			}
		}
	}

	public static class KingDamager extends Buff {

		{
			revivePersists = true;
		}

		private int kingID;

		public KingDamager(){
			kingID = -1;
		}
		public KingDamager(int kingId){
			this.kingID = kingId;
		}

		@Override
		public boolean act() {
			if (target.alignment != Alignment.ENEMY){
				detach();
			}
			spend( TICK );
			return true;
		}

		@Override
		public void detach() {
			super.detach();
			DwarfKing king = findKingById(kingID);
			if (king != null) {
				int damage = (int) Math.ceil(king.HT / (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 18d : 12d));
				king.damage(damage, this);
			}
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(KING_ID, kingID);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			kingID = bundle.getInt(KING_ID);
		}
	}

	public static class DKBarrior extends Barrier {

		@Override
		public boolean act() {
			incShield();
			return super.act();
		}

		@Override
		public int icon() {
			return BuffIndicator.NONE;
		}
	}

}