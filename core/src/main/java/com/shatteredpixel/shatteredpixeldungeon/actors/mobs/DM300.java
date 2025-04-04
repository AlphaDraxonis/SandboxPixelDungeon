/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemsWithChanceDistrComp;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.WatabouRect;

import java.util.ArrayList;
import java.util.List;

public class DM300 extends DMMob implements MobBasedOnDepth {

	{
		spriteClass = DM300Sprite.class;

		HP = HT = 300;
		EXP = 30;
		defenseSkill = 15;
		attackSkill = 20;
		damageRollMin = 15;
		damageRollMax = 25;
		damageReductionMax = 10;

		lootChance = 1f;
		//special loot logic, see createActualLoot()

		pylonsNeeded = 2;

		properties.add(Property.BOSS);
		properties.add(Property.INORGANIC);
		properties.add(Property.LARGE);
	}

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 15, 25 );
//	}
//
//	@Override
//	public int attackSkill( Char target ) {
//		return 20;
//	}
//
//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 10);
//	}

	public int pylonsActivated = 0;
	public int pylonsNeeded;
	public boolean supercharged = false;
	public boolean chargeAnnounced = false;

	public boolean destroyWalls = true;

	private final int MIN_COOLDOWN = 5;
	private final int MAX_COOLDOWN = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 7 : 9;

	private int turnsSinceLastAbility = -1;
	private int abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

	private int lastAbility = 0;
	private static final int NONE = 0;
	private static final int GAS = 1;
	private static final int ROCKS = 2;

	private static final String PYLONS_ACTIVATED = "pylons_activated";
	private static final String PYLONS_NEEDED = "pylons_needed";
	private static final String SUPERCHARGED = "supercharged";
	private static final String CHARGE_ANNOUNCED = "charge_announced";
	private static final String DESTROY_WALLS = "destroy_walls";

	private static final String TURNS_SINCE_LAST_ABILITY = "turns_since_last_ability";
	private static final String ABILITY_COOLDOWN = "ability_cooldown";

	private static final String LAST_ABILITY = "last_ability";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(PYLONS_ACTIVATED, pylonsActivated);
		bundle.put(PYLONS_NEEDED, pylonsNeeded);
		bundle.put(SUPERCHARGED, supercharged);
		bundle.put(CHARGE_ANNOUNCED, chargeAnnounced);
		bundle.put(DESTROY_WALLS, destroyWalls);
		bundle.put(TURNS_SINCE_LAST_ABILITY, turnsSinceLastAbility);
		bundle.put(ABILITY_COOLDOWN, abilityCooldown);
		bundle.put(LAST_ABILITY, lastAbility);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		pylonsActivated = bundle.getInt(PYLONS_ACTIVATED);
		pylonsNeeded = bundle.getInt(PYLONS_NEEDED);
		supercharged = bundle.getBoolean(SUPERCHARGED);
		chargeAnnounced = bundle.getBoolean(CHARGE_ANNOUNCED);
		destroyWalls = bundle.getBoolean(DESTROY_WALLS);
		turnsSinceLastAbility = bundle.getInt(TURNS_SINCE_LAST_ABILITY);
		abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
		lastAbility = bundle.getInt(LAST_ABILITY);
	}

	@Override
	public void setLevel(int depth) {
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
			HT = (int) (HT * 4 / 3f);
			if (!hpSet) {
				HP = HT;
				hpSet = !CustomDungeon.isEditing();
			}
		}
	}

	@Override
	protected boolean act() {

		if (paralysed > 0){
			return super.act();
		}

		//ability logic only triggers if DM is not supercharged
		if (!supercharged){
			if (turnsSinceLastAbility >= 0) turnsSinceLastAbility++;

			//in case DM-300 hasn't been able to act yet
			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
				Dungeon.level.updateFieldOfView( this, fieldOfView );
			}

			//determine if DM can reach its enemy
			boolean canReach;
			if (enemy == null || !enemy.isAlive()){
				if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, Dungeon.hero.pos, Dungeon.level.openSpace, fieldOfView, true) != -1);
				}
			} else {
				if (Dungeon.level.adjacent(pos, enemy.pos)){
					canReach = true;
				} else {
					canReach = (Dungeon.findStep(this, enemy.pos, Dungeon.level.openSpace, fieldOfView, true) != -1);
				}
			}

			if (state != HUNTING){
				if (Dungeon.hero.invisible <= 0 && canReach){
					beckon(Dungeon.hero.pos);
				}
			} else {

				if ((enemy == null || !enemy.isAlive()) && Dungeon.hero.invisible <= 0) {
					enemy = Dungeon.hero;
				}

				//more aggressive ability usage when DM can't reach its target
				if (enemy != null && enemy.isAlive() && !canReach){

					//try to fire gas at an enemy we can't reach
					if (turnsSinceLastAbility >= MIN_COOLDOWN){
						//use a coneAOE to try and account for trickshotting angles
						ConeAOE aim = new ConeAOE(new Ballistica(pos, enemy.pos, Ballistica.WONT_STOP, null), Float.POSITIVE_INFINITY, 30, Ballistica.STOP_SOLID | Ballistica.STOP_BARRIER_PROJECTILES, null);
						if (aim.cells.contains(enemy.pos) && !Char.hasProp(enemy, Property.INORGANIC)) {
							lastAbility = GAS;
							turnsSinceLastAbility = 0;

							if (doRangedAttack()) {
								Sample.INSTANCE.play(Assets.Sounds.GAS);
								return true;
							} else {
								return false;
							}
						//if we can't gas, or if target is inorganic then drop rocks
						//unless enemy is already stunned, we don't want to stunlock them
						} else if (enemy.paralysed <= 0) {
							lastAbility = ROCKS;
							turnsSinceLastAbility = 0;
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								return DM300Sprite.slam(sprite, enemy.pos);
							} else {
								dropRocks(enemy);
								Sample.INSTANCE.play(Assets.Sounds.ROCKS);
								return true;
							}
						}

					}

				} else if (enemy != null && enemy.isAlive() && fieldOfView[enemy.pos]) {
					if (turnsSinceLastAbility > abilityCooldown) {

						if (lastAbility == NONE) {
							//50/50 either ability
							lastAbility = Random.Int(2) == 0 ? GAS : ROCKS;
						} else if (lastAbility == GAS) {
							//more likely to use rocks
							lastAbility = Random.Int(4) == 0 ? GAS : ROCKS;
						} else {
							//more likely to use gas
							lastAbility = Random.Int(4) != 0 ? GAS : ROCKS;
						}

						if (Char.hasProp(enemy, Property.INORGANIC)){
							lastAbility = ROCKS;
						}

						//doesn't spend a turn if enemy is at a distance
						if (Dungeon.level.adjacent(pos, enemy.pos)){
							spend(TICK);
						}

						turnsSinceLastAbility = 0;
						abilityCooldown = Random.NormalIntRange(MIN_COOLDOWN, MAX_COOLDOWN);

						if (lastAbility == GAS) {
							if (doRangedAttack()) {
								Sample.INSTANCE.play(Assets.Sounds.GAS);
								return true;
							} else {
								return false;
							}
						} else {
							if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
								return DM300Sprite.slam(sprite, enemy.pos);
							} else {
								dropRocks(enemy);
								Sample.INSTANCE.play(Assets.Sounds.ROCKS);
								return true;
							}
						}
					}
				}
			}
		} else {

			if (!chargeAnnounced){
				yell(Messages.get(this, "supercharged"));
				chargeAnnounced = true;
			}

			if (Dungeon.hero.invisible <= 0){
				beckon(Dungeon.hero.pos);
				state = HUNTING;
				enemy = Dungeon.hero;
			}

		}

		return super.act();
	}

	@Override
	public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
		if (enemy == Dungeon.hero && supercharged){
			Statistics.qualifiedForBossChallengesBadge[2] = false;
		}
		return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
	}

	@Override
	protected Char chooseEnemyImpl() {
		Char enemy = super.chooseEnemyImpl();
		if (supercharged && enemy == null && alignment == Alignment.ENEMY){
			enemy = Dungeon.hero;
		}
		return enemy;
	}

	@Override
	public void move(int step, boolean travelling) {
		super.move(step, travelling);

		if (travelling) PixelScene.shake( supercharged ? 3 : 1, 0.25f );

		if (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && state == HUNTING) {
			//WARNING: Make sure the shielding logic stays in DMMob after each update!
			//This if is to trick git into actually showing me the diff if something is changed here
		}
	}

	@Override
	public float speed() {
		return super.speed() * (supercharged ? 2 : 1);
	}

	@Override
	public void notice() {
		super.notice();
		if (playerAlignment != NORMAL_ALIGNMENT) return;

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			turnsSinceLastAbility = 0;
			if (playerAlignment == NORMAL_ALIGNMENT) {
				if (!(Dungeon.level instanceof CavesBossLevel)) {
					if (showBossBar) Dungeon.level.seal();
					playBossMusic(Assets.Music.CAVES_BOSS);
				}
				yell(Messages.get(this, "notice"));
				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayBoss(DM300.class);
					}
				}
			}
		}
	}

	@Override
	public void zap() {
		ventGas(enemy);
	}

	@Override
	public void playZapAnim(int target) {
		DM300Sprite.playZap(sprite.parent, sprite, target, this);
	}

	public void ventGas(Char target ){
		Dungeon.hero.interrupt();

		int gasVented = 0;

		Ballistica trajectory = new Ballistica(pos, target.pos, Ballistica.STOP_TARGET, null);

		int gasMulti = Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2 : 1;

		for (int i : trajectory.subPath(0, trajectory.dist)){
			GameScene.add(Blob.seed(i, 20*gasMulti, ToxicGas.class));
			gasVented += 20*gasMulti;
		}

		GameScene.add(Blob.seed(trajectory.collisionPos, 100*gasMulti, ToxicGas.class));

		if (gasVented < 250*gasMulti){
			int toVentAround = (int)Math.ceil(((250*gasMulti) - gasVented)/8f);
			for (int i : PathFinder.NEIGHBOURS8){
				GameScene.add(Blob.seed(pos+i, toVentAround, ToxicGas.class));
			}

		}

	}

	public void onSlamComplete(){
		dropRocks(enemy);
		next();
	}

	public void dropRocks( Char target ) {

		Dungeon.hero.interrupt();
		final int rockCenter;

		//knock back 2 tiles if adjacent
		if (Dungeon.level.adjacent(pos, target.pos)){
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT, target);
			WandOfBlastWave.throwChar(target, trajectory, 2, false, false, this);
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));

		//knock back 1 tile if there's 1 tile of space
		} else if (fieldOfView[target.pos] && Dungeon.level.distance(pos, target.pos) == 2) {
			int oppositeAdjacent = target.pos + (target.pos - pos);
			Ballistica trajectory = new Ballistica(target.pos, oppositeAdjacent, Ballistica.MAGIC_BOLT, target);
			WandOfBlastWave.throwChar(target, trajectory, 1, false, false, this);
			if (target == Dungeon.hero){
				Dungeon.hero.interrupt();
			}
			rockCenter = trajectory.path.get(Math.min(trajectory.dist, 1));

		//otherwise no knockback
		} else {
			rockCenter = target.pos;
		}

		int safeCell;
		do {
			safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)];
		} while (safeCell == pos
				|| (Dungeon.level.solid[safeCell] && Random.Int(2) == 0)
				|| (Blob.volumeAt(safeCell, CavesBossLevel.PylonEnergy.class) > 0 && Random.Int(2) == 0));

		ArrayList<Integer> rockCells = new ArrayList<>();

		int start = rockCenter - Dungeon.level.width() * 3 - 3;
		int pos;
		for (int y = 0; y < 7; y++) {
			pos = start + Dungeon.level.width() * y;
			for (int x = 0; x < 7; x++) {
				if (!Dungeon.level.insideMap(pos)) {
					pos++;
					continue;
				}
				//add rock cell to pos, if it is not solid, and isn't the safecell
				if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
					rockCells.add(pos);
				}
				pos++;
			}
		}
		for (int i : rockCells){
			sprite.parent.add(new TargetedCell(i, 0xFF0000));
		}
		//don't want to overly punish players with slow move or attack speed
		Buff.append(this, FallingRockBuff.class, GameMath.gate(TICK, (int)Math.ceil(target.cooldown()), 3*TICK)).setRockPositions(rockCells);

	}

	private boolean invulnWarned = false;

	@Override
	public void damage(int dmg, Object src) {
		if (!BossHealthBar.isAssigned(this)){
			notice();
		}

		int preHP = HP;
		super.damage(dmg, src);
		if (isInvulnerable(src.getClass())){
			return;
		}

		int dmgTaken = preHP - HP;
		if (dmgTaken > 0) {
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
				if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(dmgTaken/2f);
				else                                                    lock.addTime(dmgTaken);
			}
		}

		int pylonsToActivate = totalPylonsToActivate();
		int threshold = (int) Math.ceil(HT / ((float)(pylonsToActivate + 1)) * (pylonsToActivate - pylonsActivated));

		if (HP <= threshold && threshold > 0){
			HP = threshold;
			supercharge();
		}

	}

	public int totalPylonsToActivate(){
		if (Dungeon.level instanceof CavesBossLevel) {
			return Math.min(pylonsActivated + CavesBossLevel.getAvailablePylons(Dungeon.level, id()).size(),
					Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 3 : 2);
		}
		return pylonsNeeded;
	}

	@Override
	public boolean isInvulnerable(Class effect) {
		if (supercharged && !invulnWarned){
			invulnWarned = true;
			GLog.w(Messages.get(this, "charging_hint"));
		}
		return supercharged || super.isInvulnerable(effect);
	}

	public void supercharge(){
		supercharged = true;
		Pylon p;
		if (Dungeon.level instanceof CavesBossLevel) {
			p = ((CavesBossLevel)Dungeon.level).activatePylon(id());
		}
		else {
			Level level = Dungeon.level;
			p = CavesBossLevel.activatePylon(level, CavesBossLevel.getAvailablePylons(level, id()));

			for (int i = 0; i < level.length(); i++) {
				if (level.map[i] == Terrain.WATER
						|| CustomTileItem.findCustomTileAt(i, false) instanceof CavesBossLevel.TrapTile) {
					GameScene.add(Blob.seed(i, 1, CavesBossLevel.PylonEnergy.class));
				}
			}
		}
		if (p != null) p.dm300id = id();
		pylonsActivated++;

		spend(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2f : 3f);
		yell(Messages.get(this, "charging"));
		sprite.showStatus(CharSprite.POSITIVE, Messages.get(this, "invulnerable"));

		if (sprite.extraCode instanceof DM300Sprite.SuperchargeSparks)
			((DM300Sprite.SuperchargeSparks) sprite.extraCode).updateChargeState(sprite, true);

		if (sprite instanceof DM300Sprite)
			((DM300Sprite)sprite).charge();

		chargeAnnounced = false;

	}

	public boolean isSupercharged(){
		return supercharged;
	}

	public void loseSupercharge(){
		supercharged = false;
		if (sprite.extraCode instanceof DM300Sprite.SuperchargeSparks)
			((DM300Sprite.SuperchargeSparks) sprite.extraCode).updateChargeState(sprite, false);

		//adjust turns since last ability to prevent DM immediately using an ability when charge ends
		turnsSinceLastAbility = Math.max(turnsSinceLastAbility, MIN_COOLDOWN-3);

		if (pylonsActivated < totalPylonsToActivate()){
			yell(Messages.get(this, "charge_lost"));
		} else {
			yell(Messages.get(this, "pylons_destroyed"));
			bleeding = true;
			if (Dungeon.level instanceof CavesBossLevel) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						Music.INSTANCE.fadeOut(0.5f, new Callback() {
							@Override
							public void call() {
								Music.INSTANCE.play(Assets.Music.CAVES_BOSS_FINALE, true);
							}
						});
					}
				});
			} else {
				if (playerAlignment == Mob.NORMAL_ALIGNMENT) {
					if (bossMusic == null) Dungeon.level.playSpecialMusic(Assets.Music.CAVES_BOSS_FINALE, id());
				}
			}

		}
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || pylonsActivated < totalPylonsToActivate();
	}

	@Override
	public void die( Object cause ) {

		super.die( cause );

		if (showBossBar) GameScene.bossSlain();
		Dungeon.level.unseal();

		Badges.validateBossSlain(DM300.class);
		if (Statistics.qualifiedForBossChallengesBadge[2]){
			Badges.validateBossChallengeCompleted(DM300.class);
		}
		Statistics.bossScores[2] += 3000;

		yell( Messages.get(this, "defeated") );

		if (!(Dungeon.level instanceof CavesBossLevel)) {
			Dungeon.level.stopSpecialMusic(id());
		}
	}

	@Override
	public List<Item> createActualLoot() {
		if (loot == null) return convertLootToRandomItemData().generateLoot();
		else return super.createActualLoot();
	}

	@Override
	public ItemsWithChanceDistrComp.RandomItemData convertLootToRandomItemData() {
		ItemsWithChanceDistrComp.RandomItemData customLootInfo = new ItemsWithChanceDistrComp.RandomItemData();

		//60% chance of 2 shards, 30% chance of 3, 10% chance for 4. Average of 2.5

		ItemsWithChanceDistrComp.ItemWithCount itemWithCount = new ItemsWithChanceDistrComp.ItemWithCount();
		itemWithCount.items.add(new MetalShard().quantity(2));
		itemWithCount.setCount(6);
		customLootInfo.distrSlots.add(itemWithCount);

		itemWithCount = new ItemsWithChanceDistrComp.ItemWithCount();
		itemWithCount.items.add(new MetalShard().quantity(3));
		itemWithCount.setCount(3);
		customLootInfo.distrSlots.add(itemWithCount);

		itemWithCount = new ItemsWithChanceDistrComp.ItemWithCount();
		itemWithCount.items.add(new MetalShard().quantity(4));
		itemWithCount.setCount(1);
		customLootInfo.distrSlots.add(itemWithCount);

		return customLootInfo;
	}

	@Override
	protected boolean getCloser(int target) {
		if (super.getCloser(target)){
			return true;
		} else {
			boolean isCavesBossLevel = Dungeon.level instanceof CavesBossLevel;

			if (state != HUNTING || rooted || target == pos || Dungeon.level.adjacent(pos, target)
					|| ((!supercharged || !isCavesBossLevel) && (!destroyWalls || isCavesBossLevel))
					|| hasProp(this, Property.AQUATIC) && !Dungeon.level.water[target]) {
				return false;
			}

			int bestpos = pos;
			for (int i : PathFinder.NEIGHBOURS8){
				if (Actor.findChar(pos+i) == null &&
						Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
					bestpos = pos+i;
				}
			}
			if (bestpos != pos && (destroyWalls || isCavesBossLevel)) {

				Sample.INSTANCE.play( Assets.Sounds.ROCKS );

				WatabouRect gate = CavesBossLevel.gate;
				for (int i : PathFinder.NEIGHBOURS9){
					if (Dungeon.level.map[pos+i] == Terrain.WALL || Dungeon.level.map[pos+i] == Terrain.WALL_DECO){

						if (isCavesBossLevel) {
							Point p = Dungeon.level.cellToPoint(pos + i);
							if (p.y < gate.bottom && p.x >= gate.left-2 && p.x < gate.right+2){
								continue; //don't break the gate or walls around the gate
							}
							if (!CavesBossLevel.diggableArea.inside(p)) {
								continue; //Don't break any walls out of the boss arena
							}
						} else {
							if (!Dungeon.level.insideMap(pos + i))
								continue;
							if (!Zone.canDestroyWall(Dungeon.level, pos + i))
								continue;
						}
						Level.set(pos+i, Terrain.EMPTY_DECO);
						GameScene.updateMap(pos+i);
					}
					Dungeon.level.blobs.doOnEach(WallOfLight.LightWall.class, b -> b.clear(pos+i));
				}
				Dungeon.level.cleanWalls();
				Dungeon.observe();
				spend(Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 2f : 3f);

				bestpos = pos;
				for (int i : PathFinder.NEIGHBOURS8){
					if (Actor.findChar(pos+i) == null && Dungeon.level.openSpace[pos+i] &&
							Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(pos+i, target)){
						bestpos = pos+i;
					}
				}

				if (bestpos != pos) {
					move(bestpos);
				}
				PixelScene.shake( 5, 1f );

				return true;
			}

			return false;
		}
	}

	@Override
	public String desc() {
		String desc = super.desc();
		if (supercharged) {
			desc += "\n\n" + Messages.get(this, "desc_supercharged");
		}
		return desc;
	}

	{
		immunities.add(Sleep.class);

		resistances.add(Terror.class);
		resistances.add(Charm.class);
		resistances.add(Vertigo.class);
		resistances.add(Cripple.class);
		resistances.add(Chill.class);
		resistances.add(Frost.class);
		resistances.add(Roots.class);
		resistances.add(Slow.class);
	}

	public static class FallingRockBuff extends DelayedRockFall {

		@Override
		public void affectChar(Char ch) {
			if (!(ch instanceof DM300)){
				Buff.prolong(ch, Paralysis.class, Dungeon.isChallenged(Challenges.STRONGER_BOSSES) ? 5 : 3);
				if (ch == Dungeon.hero) {
					Statistics.bossScores[2] -= 100;
				}
			}
		}

	}
}
