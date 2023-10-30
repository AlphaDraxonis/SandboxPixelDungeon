/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class YogDzewa extends Mob {

	{
		spriteClass = YogSprite.class;

		HP = HT = 1000;

		EXP = 50;

		//so that allies can attack it. States are never actually used.
		state = HUNTING;

		viewDistance = 12;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
	}

	public int spawnersAlive = CustomDungeon.isEditing() || Game.scene() instanceof TitleScene ? -1 : Statistics.spawnersAlive;

	private int phase = 0;

	private float abilityCooldown;
	private static final int MIN_ABILITY_CD = 10;
	private static final int MAX_ABILITY_CD = 15;

	private float summonCooldown;
	private static final int MIN_SUMMON_CD = 10;
	private static final int MAX_SUMMON_CD = 15;

	private static YogFist getPairedFist(YogFist fist){
		if (fist instanceof YogFist.BurningFist) return new YogFist.SoiledFist();
		if (fist instanceof YogFist.SoiledFist) return new YogFist.BurningFist();
		if (fist instanceof YogFist.RottingFist) return new YogFist.RustedFist();
		if (fist instanceof YogFist.RustedFist) return new YogFist.RottingFist();
		if (fist instanceof YogFist.BrightFist) return new YogFist.DarkFist();
		if (fist instanceof YogFist.DarkFist) return new YogFist.BrightFist();
		return null;
	}

	public ArrayList<YogFist> fistSummons = new ArrayList<>();
	public ArrayList<YogFist> challengeSummons = new ArrayList<>();
	{
		//offset seed slightly to avoid output patterns
		Random.pushGenerator(Dungeon.level == null || Dungeon.levelName == null ? Random.Long() : Dungeon.seedCurLevel()+1);
			fistSummons.add(Random.Int(2) == 0 ? new YogFist.BurningFist() : new YogFist.SoiledFist());
			fistSummons.add(Random.Int(2) == 0 ? new YogFist.RottingFist() : new YogFist.RustedFist());
			fistSummons.add(Random.Int(2) == 0 ? new YogFist.BrightFist()  : new YogFist.DarkFist());
			Random.shuffle(fistSummons);
			//randomly place challenge summons so that two fists of a pair can never spawn together
			if (Random.Int(2) == 0){
				challengeSummons.add(getPairedFist(fistSummons.get(1)));
				challengeSummons.add(getPairedFist(fistSummons.get(2)));
				challengeSummons.add(getPairedFist(fistSummons.get(0)));
			} else {
				challengeSummons.add(getPairedFist(fistSummons.get(2)));
				challengeSummons.add(getPairedFist(fistSummons.get(0)));
				challengeSummons.add(getPairedFist(fistSummons.get(1)));
			}
		Random.popGenerator();
	}

	private ArrayList<Class> regularSummons = new ArrayList<>();
	{
		if (!CustomDungeon.isEditing()) initRegularSummons();
	}

	private void initRegularSummons(){
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			for (int i = 0; i < 6; i++){
				if (i >= 4){
					regularSummons.add(YogRipper.class);
				} else if (i >= spawnersAlive){
					regularSummons.add(Larva.class);
				} else {
					regularSummons.add( i % 2 == 0 ? YogEye.class : YogScorpio.class);
				}
			}
		} else {
			for (int i = 0; i < 4; i++){
				if (i >= spawnersAlive){
					regularSummons.add(Larva.class);
				} else {
					regularSummons.add(YogRipper.class);
				}
			}
		}
		Random.shuffle(regularSummons);
	}

	private ArrayList<Integer> targetedCells = new ArrayList<>();

	@Override
	public int attackSkill(Char target) {
		return INFINITE_ACCURACY;
	}

	@Override
	protected boolean act() {
		//char logic
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
		}
		Dungeon.level.updateFieldOfView( this, fieldOfView );

		throwItems();

		sprite.hideAlert();
		sprite.hideLost();

		//mob logic
		enemy = chooseEnemy();

		enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
		//end of char/mob logic

		if (phase == 0){
			if (Dungeon.hero.viewDistance >= Dungeon.level.distance(pos, Dungeon.hero.pos)) {
				Dungeon.observe();
			}
			if (Dungeon.level.heroFOV[pos]) {
				notice();
			}
		}

		if (phase == 4 && findFist() == null){
			yell(Messages.get(this, "hope"));
			summonCooldown = -15; //summon a burst of minions!
			phase = 5;
			bleeding = true;
			if (Dungeon.level instanceof HallsBossLevel) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.fadeOut(0.5f, new Callback() {
							@Override
							public void call() {
								Music.INSTANCE.play(Assets.Music.HALLS_BOSS_FINALE, true);
							}
						});
					}
				});
			} else if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
				Dungeon.level.stopSpecialMusic(Level.MUSIC_BOSS);
				Dungeon.level.playSpecialMusic(Level.MUSIC_BOSS_FINAL);
			}
		}

		if (phase == 0){
			spend(TICK);
			return true;
		} else {

			boolean terrainAffected = false;
			HashSet<Char> affected = new HashSet<>();
			//delay fire on a rooted hero
			if (!Dungeon.hero.rooted) {
				for (int i : targetedCells) {
					Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP, null);
					//shoot beams
					sprite.parent.add(new Beam.DeathRay(sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
					for (int p : b.path) {
						Char ch = Actor.findChar(p);
						if (ch != null && (ch.alignment != alignment || ch instanceof Bee)) {
							affected.add(ch);
						}
						if (Dungeon.level.isFlamable(p)) {
							Dungeon.level.destroy(p);
							GameScene.updateMap(p);
							terrainAffected = true;
						}
					}
				}
				if (terrainAffected) {
					Dungeon.observe();
				}
				Invisibility.dispel(this);
				for (Char ch : affected) {

					if (ch == Dungeon.hero) {
						Statistics.bossScores[4] -= 500;
					}

					if (hit( this, ch, true )) {
						if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
							ch.damage(Random.NormalIntRange(30, 50), new Eye.DeathGaze());
						} else {
							ch.damage(Random.NormalIntRange(20, 30), new Eye.DeathGaze());
						}
						if (Dungeon.level.heroFOV[pos]) {
							ch.sprite.flash();
							CellEmitter.center(pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
						}
						if (!ch.isAlive() && ch == Dungeon.hero) {
							Badges.validateDeathFromEnemyMagic();
							Dungeon.fail(this);
							GLog.n(Messages.get(Char.class, "kill", name()));
						}
					} else {
						ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
					}
				}
				targetedCells.clear();
			}

			if (abilityCooldown <= 0){

				int beams = 1 + (int) ((HT - HP) * 0.0025f);
				HashSet<Integer> affectedCells = new HashSet<>();
				for (int i = 0; i < beams; i++){

					int targetPos = Dungeon.hero.pos;
					if (i != 0){
						do {
							targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
						} while (Dungeon.level.trueDistance(pos, Dungeon.hero.pos)
								> Dungeon.level.trueDistance(pos, targetPos));
					}
					targetedCells.add(targetPos);
					Ballistica b = new Ballistica(pos, targetPos, Ballistica.WONT_STOP, null);
					affectedCells.addAll(b.path);
				}

				//remove one beam if multiple shots would cause every cell next to the hero to be targeted
				boolean allAdjTargeted = true;
				for (int i : PathFinder.NEIGHBOURS9){
					if (!affectedCells.contains(Dungeon.hero.pos + i) && Dungeon.level.isPassableHero(Dungeon.hero.pos + i)){
						allAdjTargeted = false;
						break;
					}
				}
				if (allAdjTargeted){
					targetedCells.remove(targetedCells.size()-1);
				}
				for (int i : targetedCells){
					Ballistica b = new Ballistica(pos, i, Ballistica.WONT_STOP, null);
					for (int p : b.path){
						sprite.parent.add(new TargetedCell(p, 0xFF0000));
						affectedCells.add(p);
					}
				}

				//don't want to overly punish players with slow move or attack speed
				spend(GameMath.gate(TICK, (int)Math.ceil(Dungeon.hero.cooldown()), 3*TICK));
				Dungeon.hero.interrupt();

				abilityCooldown += Random.NormalFloat(MIN_ABILITY_CD, MAX_ABILITY_CD);
				abilityCooldown -= (phase - 1);

			} else {
				spend(TICK);
			}

			while (summonCooldown <= 0){

				Class<?extends Mob> cls = regularSummons.remove(0);
				Mob summon = Reflection.newInstance(cls);
				((YogDzewaMob) summon).setId(id());
				regularSummons.add(cls);

				int spawnPos = -1;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null){
						if (spawnPos == -1 || Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, pos+i)){
							spawnPos = pos + i;
						}
					}
				}

				//if no other valid spawn spots exist, try to kill an adjacent sheep to spawn anyway
				if (spawnPos == -1){
					for (int i : PathFinder.NEIGHBOURS8){
						if (Actor.findChar(pos+i) instanceof Sheep){
							if (spawnPos == -1 || Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, pos+i)){
								spawnPos = pos + i;
							}
						}
					}
					if (spawnPos != -1){
						Actor.findChar(spawnPos).die(null);
					}
				}

				if (spawnPos != -1) {
					summon.pos = spawnPos;
					GameScene.add( summon );
					Actor.add( new Pushing( summon, pos, summon.pos ) );
					summon.beckon(Dungeon.hero.pos);
					Dungeon.level.occupyCell(summon);

					summonCooldown += Random.NormalFloat(MIN_SUMMON_CD, MAX_SUMMON_CD);
					summonCooldown -= (phase - 1);
					if (findFist() != null){
						summonCooldown += MIN_SUMMON_CD - (phase - 1);
					}
				} else {
					break;
				}
			}

		}

		if (summonCooldown > 0) summonCooldown--;
		if (abilityCooldown > 0) abilityCooldown--;

		//extra fast abilities and summons at the final 100 HP
		if (phase == 5 && abilityCooldown > 2){
			abilityCooldown = 2;
		}
		if (phase == 5 && summonCooldown > 3){
			summonCooldown = 3;
		}

		return true;
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || phase != 5;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		return phase == 0 || findFist() != null || super.isInvulnerable(effect);
	}

	@Override
	public void damage( int dmg, Object src ) {

		int preHP = HP;
		super.damage( dmg, src );

		if (phase == 0 || findFist() != null) return;

		int tenPercentOfMaxHP = HT / 10;

		if (phase < 4) {
			HP = Math.max(HP, HT - tenPercentOfMaxHP * 3 * phase);
		} else if (phase == 4) {
			HP = Math.max(HP, tenPercentOfMaxHP);
		}
		int dmgTaken = preHP - HP;

		if (dmgTaken > 0) {
			abilityCooldown -= dmgTaken / 10f;
			summonCooldown -= dmgTaken / 10f;
		}

		if (phase < 4 && HP <= HT - tenPercentOfMaxHP*3*phase){

			phase++;

			updateVisibility(Dungeon.level);
			GLog.n(Messages.get(this, "darkness"));
			sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));

			int fistPos = addFist(fistSummons.remove(0));

			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
				addFist(challengeSummons.remove(0));
			}

			CellEmitter.get(fistPos-1).burst(ShadowParticle.UP, 25);
			CellEmitter.get(fistPos).burst(ShadowParticle.UP, 100);
			CellEmitter.get(fistPos+1).burst(ShadowParticle.UP, 25);

			if (abilityCooldown < 5) abilityCooldown = 5;
			if (summonCooldown < 5) summonCooldown = 5;

		}

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/3f);
			else                                                    lock.addTime(dmgTaken/2f);
		}

	}

	public int addFist(YogFist fist){
		fist.yogDzewaId = id();
		boolean normalFight = Dungeon.level instanceof HallsBossLevel;
		if (normalFight) fist.pos = Dungeon.level.exit();
		else {
			int width = Dungeon.level.width();
			int width2 = width * 2;
			int width3 = width * 3;
			int[] possiblePos = {
					-width3, 1 - width3, 2 - width3, 3 - width3,
					3 - width2, 3 - width, 3, 3 + width, 3 + width2, 3 + width3, 2 + width3, 1 + width3,
					width3, -1 + width3, -2 + width3, -3 + width3, -3 + width2, -3 + width,
					-3, -3 - width, -3 - width2, -3 - width3, -2 - width3, -1 - width3,

					-width2, 1 - width2, 2 - width2,
					2 - width, 2, 2 + width, 2 + width2, 1 + width2, width2, -1 + width2, -2 + width2,
					-2 + width, -2, -2 - width, -2 - width2, -1 - width2,

					-width, -width + 1, +1, +width + 1, +width, +width - 1, -1, -width - 1
			};
			fist.pos = -1;
			for (int i : possiblePos) {
				int cell = i + pos;
				boolean valid = true;
				for (int j = -1; j <= 1; j++) {
					Actor ch;
					if (!Dungeon.level.isPassable(cell + j, fist)
							|| !Dungeon.level.openSpace[cell + j]
							|| !((ch = Actor.findChar(cell)) instanceof Sheep || ch == null)
							|| Dungeon.level.findMob(cell + j) != null) {
						valid = false;
						break;
					}
				}
				if (valid) {
					fist.pos = cell;
					break;
				}
			}
			if (fist.pos == -1) {
				fist.pos = EditorUtilies.getRandomCellGuaranteed(Dungeon.level, fist);
			}
			fist.pos -= width;
		}

		CellEmitter.get(fist.pos-1 ).burst(ShadowParticle.UP, 25);
		CellEmitter.get(fist.pos).burst(ShadowParticle.UP, 100);
		CellEmitter.get(fist.pos+1).burst(ShadowParticle.UP, 25);

		if (abilityCooldown < 5) abilityCooldown = 5;
		if (summonCooldown < 5) summonCooldown = 5;

		int targetPos = fist.pos + Dungeon.level.width();

		if (!Dungeon.isChallenged(Challenges.STRONGER_BOSSES)
				&& (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep)){
			fist.pos = targetPos;
		} else if (Actor.findChar(targetPos-1) == null || Actor.findChar(targetPos-1) instanceof Sheep){
			fist.pos = targetPos-1;
		} else if (Actor.findChar(targetPos+1) == null || Actor.findChar(targetPos+1) instanceof Sheep){
			fist.pos = targetPos+1;
		} else if (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep){
			fist.pos = targetPos;
		}

		if (Actor.findChar(fist.pos) instanceof Sheep){
			Actor.findChar(fist.pos).die(null);
		}

		GameScene.add(fist, normalFight ? 4 : 1);
		Actor.add( new Pushing( fist, Dungeon.level.exit(), fist.pos ) );
		Dungeon.level.occupyCell(fist);

		return fist.pos;
	}

	public void updateVisibility( Level level ){
		if (phase > 1 && isAlive()){
			level.viewDistance = 4 - (phase-1);
		} else {
			level.viewDistance = 4;
		}
		level.viewDistance = Math.max(1, level.viewDistance);
		if (Dungeon.hero != null) {
			if (Dungeon.hero.buff(Light.class) == null) {
				Dungeon.hero.viewDistance = level.viewDistance;
			}
			Dungeon.observe();
		}
	}

	private YogFist findFist(){
		int id = id();
		for (Char c : Actor.chars()) {
			if (c instanceof YogFist && ((YogFist) c).yogDzewaId == id) {
				return (YogFist) c;
			}
		}
		return null;
	}

	@Override
	public void beckon( int cell ) {
	}

	@Override
	public void clearEnemy() {
		//do nothing
	}

	@Override
	public void aggro(Char ch) {
		int id = id();
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (Dungeon.level.distance(pos, mob.pos) <= 4 &&
					mob instanceof YogDzewaMob && ((YogDzewaMob) mob).getId() == id) {
				mob.aggro(ch);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

		int id = id();
		for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()) {
			if (mob instanceof YogDzewaMob && ((YogDzewaMob) mob).getId() == id) {
				mob.die( cause );
			}
		}

		updateVisibility(Dungeon.level);

		if (showBossBar) GameScene.bossSlain();

		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES) && spawnersAlive == 4){
			Badges.validateBossChallengeCompleted(YogDzewa.class);
		} else {
			Statistics.qualifiedForBossChallengeBadge = false;
		}
		Statistics.bossScores[4] += 5000 + 1250*spawnersAlive;

		Dungeon.level.unseal();
		super.die( cause );

		yell( Messages.get(this, "defeated") );

		if (playerAlignment == Mob.NORMAL_ALIGNMENT && !(Dungeon.level instanceof HallsBossLevel)) {
			Dungeon.level.stopSpecialMusic(Level.MUSIC_BOSS_FINAL);
		}
	}

	@Override
	public void notice() {
		if (playerAlignment != NORMAL_ALIGNMENT) return;

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			yell(Messages.get(this, "notice"));
			for (Char ch : Actor.chars()){
				if (ch instanceof DriedRose.GhostHero){
					((DriedRose.GhostHero) ch).sayBoss();
				}
			}
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.play(Assets.Music.HALLS_BOSS, true);
				}
			});
			if (phase == 0) {
				phase = 1;
				summonCooldown = Random.NormalFloat(MIN_SUMMON_CD, MAX_SUMMON_CD);
				abilityCooldown = Random.NormalFloat(MIN_ABILITY_CD, MAX_ABILITY_CD);
			}
		}
		if (playerAlignment == Mob.NORMAL_ALIGNMENT && !(Dungeon.level instanceof HallsBossLevel)) {
			Dungeon.level.seal();
			Dungeon.level.playSpecialMusic(Level.MUSIC_BOSS);
		}
	}

	@Override
	public String description() {
		String desc = super.description();

		if (Statistics.spawnersAlive > 0){
			desc += "\n\n" + Messages.get(this, "desc_spawners");
		}

		return desc;
	}

	{
		immunities.add( Dread.class );
		immunities.add( Terror.class );
		immunities.add( Amok.class );
		immunities.add( Charm.class );
		immunities.add( Sleep.class );
		immunities.add( Vertigo.class );
		immunities.add( Frost.class );
		immunities.add( Paralysis.class );
	}

	private static final String PHASE = "phase";
	private static final String SPAWNERS_ALIVE = "spawners_alive";

	private static final String ABILITY_CD = "ability_cd";
	private static final String SUMMON_CD = "summon_cd";

	private static final String FIST_SUMMONS = "fist_summons";
	private static final String REGULAR_SUMMONS = "regular_summons";
	private static final String CHALLENGE_SUMMONS = "challenges_summons";

	private static final String TARGETED_CELLS = "targeted_cells";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PHASE, phase);
		bundle.put(SPAWNERS_ALIVE, spawnersAlive);

		bundle.put(ABILITY_CD, abilityCooldown);
		bundle.put(SUMMON_CD, summonCooldown);

		bundle.put(FIST_SUMMONS, fistSummons);
		bundle.put(CHALLENGE_SUMMONS, challengeSummons);
		if (!CustomDungeon.isEditing()) bundle.put(REGULAR_SUMMONS, regularSummons.toArray(new Class[0]));

		int[] bundleArr = new int[targetedCells.size()];
		for (int i = 0; i < targetedCells.size(); i++){
			bundleArr[i] = targetedCells.get(i);
		}
		bundle.put(TARGETED_CELLS, bundleArr);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		phase = bundle.getInt(PHASE);
		spawnersAlive = bundle.getInt(SPAWNERS_ALIVE);
		if (spawnersAlive == -1 && !CustomDungeon.isEditing() && !(Game.scene() instanceof TitleScene)) spawnersAlive = Statistics.spawnersAlive;

		abilityCooldown = bundle.getFloat(ABILITY_CD);
		summonCooldown = bundle.getFloat(SUMMON_CD);

		fistSummons.clear();
		for (Bundlable b : bundle.getCollection(FIST_SUMMONS))
			fistSummons.add((YogFist) b);
		challengeSummons.clear();
		for (Bundlable b : bundle.getCollection(CHALLENGE_SUMMONS))
			challengeSummons.add((YogFist) b);
		if (bundle.contains(REGULAR_SUMMONS)) {
			regularSummons.clear();
			Collections.addAll(regularSummons, bundle.getClassArray(REGULAR_SUMMONS));
		}

		for (int i : bundle.getIntArray(TARGETED_CELLS)){
			targetedCells.add(i);
		}
	}

	interface YogDzewaMob {
		void setId(int id);

		int getId();
	}

	public static class Larva extends Mob implements YogDzewaMob {

		{
			spriteClass = LarvaSprite.class;

			HP = HT = 20;
			defenseSkill = 12;
			attackSkill = 30;
			damageRollMin = 15;
			damageRollMax = 25;
			damageReductionMax = 4;
			viewDistance = Light.DISTANCE;

			EXP = 5;
			maxLvl = -2;

			properties.add(Property.DEMONIC);
		}

		private int yogDzewaId;
		public static final String YOG_DZEWA_ID = "yog_dzewa_id";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(YOG_DZEWA_ID, yogDzewaId);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			yogDzewaId = bundle.getInt(YOG_DZEWA_ID);
			if (yogDzewaId > 0) properties.add(Property.BOSS_MINION);
		}

		@Override
		public void setId(int id) {
			yogDzewaId = id;
			properties.add(Property.BOSS_MINION);
		}

		@Override
		public int getId() {
			return yogDzewaId;
		}

//		@Override
//		public int attackSkill( Char target ) {
//			return 30;
//		}
//
//		@Override
//		public int damageRoll() {
//			return Random.NormalIntRange( 15, 25 );
//		}
//
//		@Override
//		public int drRoll() {
//			return super.drRoll() + Random.NormalIntRange(0, 4);
//		}

	}

	//used so death to yog's ripper demons have their own rankings description
	public static class YogRipper extends RipperDemon implements YogDzewaMob {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}

		private int yogDzewaId;
		public static final String YOG_DZEWA_ID = "yog_dzewa_id";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(YOG_DZEWA_ID, yogDzewaId);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			yogDzewaId = bundle.getInt(YOG_DZEWA_ID);
		}

		@Override
		public void setId(int id) {
			yogDzewaId = id;
		}

		@Override
		public int getId() {
			return yogDzewaId;
		}
	}
	public static class YogEye extends Eye implements YogDzewaMob {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}

		private int yogDzewaId;
		public static final String YOG_DZEWA_ID = "yog_dzewa_id";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(YOG_DZEWA_ID, yogDzewaId);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			yogDzewaId = bundle.getInt(YOG_DZEWA_ID);
		}

		@Override
		public void setId(int id) {
			yogDzewaId = id;
		}

		@Override
		public int getId() {
			return yogDzewaId;
		}
	}
	public static class YogScorpio extends Scorpio implements YogDzewaMob {
		{
			maxLvl = -2;
			properties.add(Property.BOSS_MINION);
		}

		private int yogDzewaId;
		public static final String YOG_DZEWA_ID = "yog_dzewa_id";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(YOG_DZEWA_ID, yogDzewaId);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			yogDzewaId = bundle.getInt(YOG_DZEWA_ID);
		}

		@Override
		public void setId(int id) {
			yogDzewaId = id;
		}

		@Override
		public int getId() {
			return yogDzewaId;
		}
	}
}