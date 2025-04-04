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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.TengusMask;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TenguSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Tengu extends Mob implements MobBasedOnDepth {
	
	{
		spriteClass = TenguSprite.class;
		
		HP = HT = 200;
		EXP = 20;
		defenseSkill = 15;
		attackSkill = 20;
		damageRollMin = 6;
		damageRollMax = 12;
		damageReductionMax = 5;

		loot = new TengusMask();
		lootChance = 1f;

		HUNTING = new Hunting();

		WANDERING = new Wandering();
		state = HUNTING;

		properties.add(Property.BOSS);
		
		viewDistance = 12;
	}

	public int phase = 1;//1 or 2, ONLY used for non PrisonBossLevels
	public int arenaRadius = 7;//ONLY used for non PrisonBossLevels
	private int stepsToDo;//only if state is Wandering
	private boolean attackedPlayer;

	public boolean zapForAbility = false;//differentiate between normal attacks and abilities in sprite

//	@Override
//	public int damageRoll() {
//		return Random.NormalIntRange( 6, 12 );
//	}

	@Override
	public int attackSkill( Char target ) {
		if (Dungeon.level.adjacent(pos, target.pos)){
			return attackSkill / 2;
		} else {
			return attackSkill;
		}
	}
	
	@Override
	public void setLevel(int depth) {
		if (!hpSet && Dungeon.level instanceof PrisonBossLevel) phase = 0;
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
			HT = (int) (HT * 1.25f);
			if (!hpSet) HP = HT;
		}
		hpSet = !CustomDungeon.isEditing();
	}

	//	@Override
//	public int drRoll() {
//		return super.drRoll() + Random.NormalIntRange(0, 5);
//	}

	boolean loading = false;

	//Tengu is immune to debuffs and damage when removed from the level
	@Override
	public boolean add(Buff buff) {
		if (Actor.chars().contains(this) || buff instanceof Doom || loading || CustomDungeon.knowsEverything()){
			return super.add(buff);
		}
		return false;
	}

	@Override
	public void damage(int dmg, Object src) {
		if (!Dungeon.level.mobs.contains(this)){
			return;
		}

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			if (playerAlignment == NORMAL_ALIGNMENT && !(Dungeon.level instanceof PrisonBossLevel)) {
				if (showBossBar) Dungeon.level.seal();
				playBossMusic(Assets.Music.PRISON_BOSS);
			}
		}

		PrisonBossLevel.State state;
		boolean normalFight = Dungeon.level instanceof PrisonBossLevel;
		if (normalFight) state = ((PrisonBossLevel) Dungeon.level).state();
		else state = phase == 1 ? PrisonBossLevel.State.FIGHT_START : PrisonBossLevel.State.FIGHT_ARENA;

		int hpBracket = Math.max(2, HT / 8);

		int curbracket = HP / hpBracket;
		int beforeHitHP = HP;
		super.damage(dmg, src);

		//cannot be hit through multiple brackets at a time
		if (HP <= (curbracket-1)*hpBracket){
			HP = (curbracket-1)*hpBracket + 1;
		}

		int newBracket =  HP / hpBracket;
		dmg = beforeHitHP - HP;

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null && !isImmune(src.getClass()) && !isInvulnerable(src.getClass())){
			if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES))   lock.addTime(2*dmg/3f);
			else                                                    lock.addTime(dmg);
		}

		//phase 2 of the fight is over
		if (HP == 0 && state == PrisonBossLevel.State.FIGHT_ARENA) {
			//let full attack action complete first
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					Actor.remove(this);
					if (normalFight) ((PrisonBossLevel) Dungeon.level).progress();
					else PrisonBossLevel.killTengu(Dungeon.level, Tengu.this);
					return true;
				}
			});
			return;
		}

		//phase 1 of the fight is over
		if (state == PrisonBossLevel.State.FIGHT_START && (HP <= HT/2 && normalFight || HP <= 0 && !normalFight)){
			if (normalFight) {
				HP = (HT / 2);
				yell(Messages.get(this, "interesting"));
				((PrisonBossLevel) Dungeon.level).progress();

				bleeding = true;

			} else PrisonBossLevel.killTengu(Dungeon.level, Tengu.this);

		//if tengu has lost a certain amount of hp, jump
		} else if (newBracket != curbracket) {
			//let full attack action complete first
			Actor.add(new Actor() {

				{
					actPriority = VFX_PRIO;
				}

				@Override
				protected boolean act() {
					Actor.remove(this);
					jump(-1, true);
					return true;
				}
			});

		}
		if (!normalFight && HP > 0 && HP <= HT / 2) {
			bleeding = true;
		}
	}
	
	@Override
	public boolean isAlive() {
		return super.isAlive() || Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
	}

	@Override
	public void die( Object cause ) {

		if (showBossBar) GameScene.bossSlain();
		super.die( cause );
		
		Badges.validateBossSlain(Tengu.class);
		if (Statistics.qualifiedForBossChallengesBadge[1]){
			Badges.validateBossChallengeCompleted(Tengu.class);
		}
		Statistics.bossScores[1] += 2000;

		yell( Messages.get(this, "defeated") );

		if (!(Dungeon.level instanceof PrisonBossLevel)) {
			int id = id();
			for (Heap h : Dungeon.level.heaps.valueList()) {
				for (Item item : h.items) {
					if (item instanceof TenguAbilityItem && ((TenguAbilityItem) item).throwerId == id
					) {
						h.remove(item);
					}
				}
			}
			Dungeon.level.stopSpecialMusic(id());
		}
	}
	
	@Override
	protected boolean canAttack( Char enemy ) {
		Ballistica b = new Ballistica(pos, enemy.pos, Ballistica.REAL_PROJECTILE, null);
		return b.collisionPos == enemy.pos && (Dungeon.level instanceof PrisonBossLevel || b.dist <= arenaRadius);
	}

	@Override
	protected boolean doAttack(Char enemy) {

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			if (playerAlignment == NORMAL_ALIGNMENT && !(Dungeon.level instanceof PrisonBossLevel)) {
				if (showBossBar) Dungeon.level.seal();
				playBossMusic(Assets.Music.PRISON_BOSS);
			}
		}

		if (sprite instanceof TenguSprite || sprite == null || !sprite.visible && !enemy.sprite.visible)
			return super.doAttack(enemy);

		TenguSprite.doRealAttack(sprite, enemy.pos);
		return false;
	}

	@Override
	public void playZapAnim(int target) {
		TenguSprite.playZap(sprite.parent, sprite, target, this);
	}

	@Override
	public void hitSound(float pitch) {
		if (sprite instanceof TenguSprite || Dungeon.level.adjacent(pos, target)) super.hitSound(pitch);
	}

	private void jump(int targetPos, boolean insideArena) {
		
		//in case tengu hasn't had a chance to act yet
		if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
			fieldOfView = new boolean[Dungeon.level.length()];
			Dungeon.level.updateFieldOfView( this, fieldOfView );
		}
		
		if (enemy == null) enemy = chooseEnemy();
		if (enemy == null && targetPos == -1) return;
		else if (enemy != null) targetPos = enemy.pos;
		
		int newPos;
		if (Dungeon.level instanceof PrisonBossLevel){
			PrisonBossLevel level = (PrisonBossLevel) Dungeon.level;
			
			//if we're in phase 1, want to warp around within the room
			if (level.state() == PrisonBossLevel.State.FIGHT_START) {
				
				level.cleanTenguCell();

				int tries = 100;
				do {
					newPos = ((PrisonBossLevel)Dungeon.level).randomTenguCellPos();
					tries--;
				} while ( tries > 0 && (level.trueDistance(newPos, targetPos) <= 3.5f
						|| level.trueDistance(newPos, Dungeon.hero.pos) <= 3.5f
						|| Actor.findChar(newPos) != null));

				if (tries <= 0) newPos = pos;

				if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				
				sprite.move( pos, newPos );
				move( newPos );
				
				if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );

				float fill = 0.9f - 0.5f*((HP-(HT/2f))/(HT/2f));
				level.placeTrapsInTenguCell(fill);
				
			//otherwise, jump in a larger possible area, as the room is bigger
			} else {

				int tries = 100;
				do {
					newPos = Random.Int(level.length());
					tries--;
				} while (  tries > 0 &&
						(level.solid[newPos] ||
								level.distance(newPos, targetPos) < 5 ||
								level.distance(newPos, targetPos) > 7 ||
								level.distance(newPos, Dungeon.hero.pos) < 5 ||
								level.distance(newPos, Dungeon.hero.pos) > 7 ||
								level.distance(newPos, pos) < 5 ||
								Actor.findChar(newPos) != null ||
								Dungeon.level.heaps.get(newPos) != null));

				if (tries <= 0) newPos = pos;

				if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
				
				sprite.move( pos, newPos );
				move( newPos );
				
				if (arenaJumps < 4) arenaJumps++;
				
				if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
				Sample.INSTANCE.play( Assets.Sounds.PUFF );
				
			}
			
		//if we're on another type of level
		} else {
			Level level = Dungeon.level;

			int tries = 300;
			do {
				newPos = Random.Int(level.length());
				tries--;
			} while (  tries > 0 &&
					(!Dungeon.hero.fieldOfView[newPos] ||
							!level.isPassable(newPos, this) ||
							level.distance(newPos, targetPos) > arenaRadius ||
							insideArena && level.distance(newPos, Dungeon.hero.pos) > arenaRadius ||
							insideArena && level.distance(newPos, pos) < arenaRadius - 1 ||
							Actor.findChar(newPos) != null ||
							level.heaps.get(newPos) != null));

			if (tries <= 0) newPos = pos;
			
			if (level.heroFOV[pos]) CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
			
			sprite.move( pos, newPos );
			move( newPos );

			if (arenaJumps < 6) arenaJumps++;

			if (level.heroFOV[newPos]) CellEmitter.get( newPos ).burst( Speck.factory( Speck.WOOL ), 6 );
			Sample.INSTANCE.play( Assets.Sounds.PUFF );
			
		}
		
	}
	
	@Override
	public void notice() {
		super.notice();
		if (playerAlignment != NORMAL_ALIGNMENT) return;

		if (!BossHealthBar.isAssigned(this)) {
			BossHealthBar.addBoss(this);
			if (HP <= HT/2) bleeding = true;
			if (showBossBar && !(Dungeon.level instanceof PrisonBossLevel)) Dungeon.level.seal();
			if (HP == HT) {
				yell(Messages.get(this, "notice_gotcha", Dungeon.hero.name()));
				for (Char ch : Actor.chars()) {
					if (ch instanceof DriedRose.GhostHero) {
						((DriedRose.GhostHero) ch).sayBoss(Tengu.class);
					}
				}
			} else {
				yell(Messages.get(this, "notice_have", Dungeon.hero.name()));
			}
		}

		if (playerAlignment == NORMAL_ALIGNMENT && !(Dungeon.level instanceof PrisonBossLevel)) {
			if (showBossBar) Dungeon.level.seal();
			Dungeon.level.playSpecialMusic(Assets.Music.PRISON_BOSS, id());
		}
	}
	
	{
		immunities.add( Roots.class );
		immunities.add( Blindness.class );
		immunities.add( Dread.class );
		immunities.add( Terror.class );
	}
	
	private static final String LAST_ABILITY     = "last_ability";
	private static final String ABILITIES_USED   = "abilities_used";
	private static final String ARENA_JUMPS      = "arena_jumps";
	private static final String ABILITY_COOLDOWN = "ability_cooldown";
	private static final String PHASE            = "phase";
	private static final String ARENA_RADIUS     = "arena_radius";
	private static final String INITIAL_POS      = "initial_pos";
	private static final String STEPS_TO_DO      = "steps_to_do";
	private static final String ATTACKED_PLAYER  = "attacked_player";


	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( LAST_ABILITY, lastAbility );
		bundle.put( ABILITIES_USED, abilitiesUsed );
		bundle.put( ARENA_JUMPS, arenaJumps );
		bundle.put( ABILITY_COOLDOWN, abilityCooldown );
		bundle.put(PHASE, phase);
		bundle.put(ARENA_RADIUS, arenaRadius);
		bundle.put(STEPS_TO_DO, stepsToDo);
		bundle.put(ATTACKED_PLAYER, attackedPlayer);
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		loading = true;
		super.restoreFromBundle(bundle);
		loading = false;
		lastAbility = bundle.getInt( LAST_ABILITY );
		abilitiesUsed = bundle.getInt( ABILITIES_USED );
		arenaJumps = bundle.getInt( ARENA_JUMPS );
		abilityCooldown = bundle.getInt( ABILITY_COOLDOWN );
		phase = bundle.getInt(PHASE);
		arenaRadius = bundle.getInt(ARENA_RADIUS);
		stepsToDo = bundle.getInt(STEPS_TO_DO);
		attackedPlayer = bundle.getBoolean(ATTACKED_PLAYER);
	}
	
	//don't bother bundling this, as its purely cosmetic
	private boolean yelledCoward = false;
	
	//tengu is always hunting in Shattered
	private class Hunting extends Mob.Hunting{
		
		@Override
		public boolean act(boolean enemyInFOV, boolean justAlerted) {
			
			enemySeen = enemyInFOV;
			if (enemyInFOV && !isCharmedBy( enemy ) && canAttack( enemy )) {

				if (enemy instanceof Hero) attackedPlayer = true;

				if (canUseAbility()){
					return useAbility();
				}

				recentlyAttackedBy.clear();
				target = enemy.pos;
				return doAttack( enemy );
				
			} else {
				
				if (enemyInFOV) {
					target = enemy.pos;
				} else {
					chooseEnemy();
					if (enemy == null){
						if (playerAlignment == NORMAL_ALIGNMENT) {
							//if nothing else can be targeted, target hero
							enemy = Dungeon.hero;
						} else {
							looseEnemy();
							spend(TICK);
							return true;
						}
					}
					target = enemy.pos;
				}
				
				//if not charmed, attempt to use an ability, even if the enemy can't be seen
				if (canUseAbility()){
					return useAbility();
				}
				
				spend( TICK );
				return true;
				
			}
		}
	}

	protected class Wandering extends Mob.Wandering {

		protected boolean continueWandering(){

			if (stepsToDo > 0) {
				stepsToDo--;
				spend(1 / speed());
				return true;
			}

			if (target == -1) {
				target = randomDestination();
				spend( TICK );
				return true;
			}

			enemySeen = false;

			int oldPos = pos;
			if (!ScrollOfTeleportation.teleportToLocation(Tengu.this, target, false, false)){
				int oldArenaJumps = arenaJumps;
				jump(target, false);
				spend(1 / speed());
				arenaJumps = oldArenaJumps;
				stepsToDo = new Ballistica(oldPos, pos, Ballistica.STOP_TARGET, Tengu.this).dist - 1;
				target = -1;
				return true;
			} else {
				stepsToDo = new Ballistica(oldPos, pos, Ballistica.STOP_TARGET, Tengu.this).dist - 1;
				spend(1 / speed());
				moveSprite( oldPos, pos );
				return true;
			}
		}

		@Override
		protected boolean noticeEnemy() {
			stepsToDo = 0;
			return super.noticeEnemy();
		}
	}

	//*****************************************************************************************
	//***** Tengu abilities. These are expressed in game logic as buffs, blobs, and items *****
	//*****************************************************************************************
	
	//so that mobs can also use this
	private static Char throwingChar;
	
	private int lastAbility = -1;
	private int abilitiesUsed = 0;
	private int arenaJumps = 0;
	
	//starts at 2, so one turn and then first ability
	private int abilityCooldown = 2;
	
	private static final int BOMB_ABILITY    = 0;
	private static final int FIRE_ABILITY    = 1;
	private static final int SHOCKER_ABILITY = 2;
	
	//expects to be called once per turn;
	public boolean canUseAbility(){
		
		if (HP > HT/2 && Dungeon.level instanceof PrisonBossLevel) return false;
		
		if (abilitiesUsed >= targetAbilityUses() || new Ballistica(pos, enemy.pos, Ballistica.REAL_PROJECTILE, null).collisionPos != enemy.pos) {
			return false;
		} else {
			
			abilityCooldown--;
			
			if (targetAbilityUses() - abilitiesUsed >= 4 && !Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
				//Very behind in ability uses, use one right away!
				//but not on bosses challenge, we already cast quickly then
				abilityCooldown = 0;
				
			} else if (targetAbilityUses() - abilitiesUsed >= 3){
				//moderately behind in uses, use one every other action.
				if (abilityCooldown == -1 || abilityCooldown > 1) abilityCooldown = 1;
				
			} else {
				//standard delay before ability use, 1-4 turns
				if (abilityCooldown == -1) abilityCooldown = Random.IntRange(1, 4);
			}
			
			if (abilityCooldown == 0){
				return true;
			} else {
				return false;
			}
		}
	}
	
	private int targetAbilityUses(){
		//1 base ability use, plus 2 uses per jump
		int targetAbilityUses = 1 + 2*arenaJumps;
		
		//and ane extra 2 use for jumps 3 and 4
		if (Dungeon.level instanceof PrisonBossLevel) {
			if (arenaJumps >= 3) targetAbilityUses++;
			if (arenaJumps >= 4) targetAbilityUses++;
		}
		
		return targetAbilityUses;
	}
	
	public boolean useAbility(){
		boolean abilityUsed = false;
		int abilityToUse = -1;
		
		while (!abilityUsed){
			
			if (abilitiesUsed == 0){
				abilityToUse = BOMB_ABILITY;
			} else if (abilitiesUsed == 1){
				abilityToUse = SHOCKER_ABILITY;
			} else if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)) {
				abilityToUse = Random.Int(2)*2; //0 or 2, can't roll fire ability with challenge
			} else {
				abilityToUse = Random.Int(3);
			}
			
			//If we roll the same ability as last time, 9/10 chance to reroll
			if (abilityToUse != lastAbility || Random.Int(10) == 0){
				switch (abilityToUse){
					case BOMB_ABILITY : default:
						abilityUsed = throwBomb(Tengu.this, enemy);
						//if Tengu cannot use his bomb ability first, use fire instead.
						if (abilitiesUsed == 0 && !abilityUsed){
							abilityToUse = FIRE_ABILITY;
							abilityUsed = throwFire(Tengu.this, enemy);
						}
						break;
					case FIRE_ABILITY:
						abilityUsed = throwFire(Tengu.this, enemy);
						break;
					case SHOCKER_ABILITY:
						abilityUsed = throwShocker(Tengu.this, enemy);
						//if Tengu cannot use his shocker ability second, use fire instead.
						if (abilitiesUsed == 1 && !abilityUsed){
							abilityToUse = FIRE_ABILITY;
							abilityUsed = throwFire(Tengu.this, enemy);
						}
						break;
				}
				//always use the fire ability with the bosses challenge
				if (abilityUsed && abilityToUse != FIRE_ABILITY && Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
					throwFire(Tengu.this, enemy);
				}
			}
			
		}
		
		//spend 1 less turn if seriously behind on ability uses
		if (Dungeon.isChallenged(Challenges.STRONGER_BOSSES)){
			if (targetAbilityUses() - abilitiesUsed >= 4) {
				//spend no time
			} else {
				spend(TICK);
			}
		} else {
			if (targetAbilityUses() - abilitiesUsed >= 4) {
				spend(TICK);
			} else {
				spend(2 * TICK);
			}
		}
		
		lastAbility = abilityToUse;
		abilitiesUsed++;
		return lastAbility == FIRE_ABILITY;
	}

	public static class TenguAbilityItem extends Item {

		int throwerId;

		private final static String THROWER_ID = "thrower_id";

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			throwerId = bundle.getInt(THROWER_ID);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(THROWER_ID, throwerId);
		}
	}

	//******************
	//***Bomb Ability***
	//******************
	
	//returns true if bomb was thrown
	public static boolean throwBomb(final Char thrower, final Char target){
		
		int targetCell = -1;
		
		//Targets closest cell which is adjacent to target and has no existing bombs
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = target.pos + i;
			boolean bombHere = false;
			for (BombAbility b : thrower.buffs(BombAbility.class)){
				if (b.bombPos == cell){
					bombHere = true;
				}
			}
			if (!bombHere && !Dungeon.level.solid[cell] &&
					(targetCell == -1 || Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos))){
				targetCell = cell;
			}
		}
		
		if (targetCell == -1){
			return false;
		}
		
		final int finalTargetCell = targetCell;
		throwingChar = thrower;
		final BombAbility.BombItem item = new BombAbility.BombItem();
		item.throwerId = thrower.id();
		if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = true;
		thrower.sprite.zap(finalTargetCell);
		if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = false;
		((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
				reset(thrower.sprite,
						finalTargetCell,
						item,
						new Callback() {
							@Override
							public void call() {
								item.onThrow(finalTargetCell);
								thrower.next();
							}
						});
		return true;
	}
	
	public static class BombAbility extends Buff {
		
		public int bombPos = -1;
		private int timer = 3;

		protected ArrayList<Emitter> smokeEmitters = new ArrayList<>();
		
		@Override
		public boolean act() {

			if (smokeEmitters.isEmpty()){
				fx(true);
			}

			if (!showTimer(bombPos, timer)){
				doExplode(bombPos, this::reduceBossScore);
				detach();
				return true;
			}

			timer--;
			spend(TICK);
			return true;
		}

		@Override
		public void fx(boolean on) {
			fxStatic(on && !alwaysHidesFx, bombPos, smokeEmitters);
		}

		public static void fxStatic(boolean on, int bombPos, ArrayList<Emitter> smokeEmitters) {
			if (on && bombPos != -1){
				PathFinder.buildDistanceMapForEnvironmentals( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
				for (int i = 0; i < PathFinder.distance.length; i++) {
					if (PathFinder.distance[i] < Integer.MAX_VALUE) {
						Emitter e = CellEmitter.get(i);
						e.pour( SmokeParticle.FACTORY, 0.25f );
						smokeEmitters.add(e);
					}
				}
			} else if (!on) {
				for (Emitter e : smokeEmitters){
					e.burst(BlastParticle.FACTORY, 2);
				}
			}
		}

		public static boolean showTimer(int bombPos, int timer) {
			PointF p = DungeonTilemap.raisedTileCenterToWorld(bombPos);
			if (timer == 3) {
				FloatingText.show(p.x, p.y, bombPos, "3...", CharSprite.WARNING);
			} else if (timer == 2){
				FloatingText.show(p.x, p.y, bombPos, "2...", CharSprite.WARNING);
			} else if (timer == 1) {
				FloatingText.show(p.x, p.y, bombPos, "1...", CharSprite.WARNING);
			}
			return timer > 0;
		}

		public static void doExplode(int bombPos, Runnable reduceHeroBossScore) {
			PathFinder.buildDistanceMapForEnvironmentals( bombPos, BArray.not( Dungeon.level.solid, null ), 2 );
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {

				if (PathFinder.distance[cell] < Integer.MAX_VALUE) {
					Char ch = Actor.findChar(cell);
					if (ch != null && !(ch instanceof Tengu)) {
						int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth() * 2);
						dmg -= ch.drRoll();

						if (dmg > 0) {
							ch.damage(dmg, Bomb.class);
						}

						if (ch == Dungeon.hero){
							if (reduceHeroBossScore != null)
								reduceHeroBossScore.run();

							if (!ch.isAlive()) {
								Dungeon.fail(Tengu.class);
							}
						}
					}
				}

			}

			Heap h = Dungeon.level.heaps.get(bombPos);
			if (h != null) {
				for (Item i : h.items.toArray(EditorUtilities.EMPTY_ITEM_ARRAY)) {
					if (i instanceof BombItem) {
						h.remove(i);
					}
				}
			}
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
		}

		protected void reduceBossScore(){
			Statistics.qualifiedForBossChallengesBadge[1] = false;
			Statistics.bossScores[1] -= 100;
		}

		private static final String BOMB_POS = "bomb_pos";
		private static final String TIMER = "timer";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BOMB_POS, bombPos );
			bundle.put( TIMER, timer );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			bombPos = bundle.getInt( BOMB_POS );
			timer = bundle.getInt( TIMER );
		}
		
		public static class BombItem extends TenguAbilityItem {
			
			{
				dropsDownHeap = true;
				unique = true;
				
				image = ItemSpriteSheet.TENGU_BOMB;
			}
			
			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}
			
			@Override
			protected void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, BombAbility.class).bombPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, BombAbility.class).bombPos = cell;
				}
			}
			
			@Override
			public Emitter emitter() {
				return staticEmitter();
			}

			public static Emitter staticEmitter(){
				Emitter emitter = new Emitter();
				emitter.pos(7.5f, 3.5f);
				emitter.fillTarget = false;
				emitter.pour(SmokeParticle.SPEW, 0.05f);
				return emitter;
			}
		}
	}
	
	//******************
	//***Fire Ability***
	//******************
	
	public static boolean throwFire(final Char thrower, final Char target){
		
		Ballistica aim = new Ballistica(thrower.pos, target.pos, Ballistica.WONT_STOP, null);
		
		for (int i = 0; i < PathFinder.CIRCLE8.length; i++){
			if (aim.sourcePos+PathFinder.CIRCLE8[i] == aim.path.get(1)){
				if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = true;
				thrower.sprite.zap(target.pos);
				if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = false;
				Buff.append(thrower, Tengu.FireAbility.class).direction = i;
				
				thrower.sprite.emitter().start(Speck.factory(Speck.STEAM), .03f, 10);
				return true;
			}
		}
		
		return false;
	}
	
	public static class FireAbility extends Buff {
		
		public int direction;
		private int[] curCells;
		
		HashSet<Integer> toCells = new HashSet<>();
		
		@Override
		public boolean act() {

			toCells.clear();

			if (curCells == null){
				curCells = new int[1];
				curCells[0] = target.pos;
				spreadFromCell( curCells[0] );

			} else {
				for (Integer c : curCells) {
					if (FireBlob.volumeAt(c, FireBlob.class) > 0) spreadFromCell(c);
				}
			}
			
			for (Integer c : curCells){
				toCells.remove(c);
			}
			
			if (toCells.isEmpty()){
				detach();
			} else {
				curCells = new int[toCells.size()];
				int i = 0;
				for (Integer c : toCells){
					GameScene.add(Blob.seed(c, 2, FireBlob.class));
					curCells[i] = c;
					i++;
				}
			}
			
			spend(TICK);
			return true;
		}
		
		private void spreadFromCell( int cell ){
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[left(direction)]]){
				toCells.add(cell + PathFinder.CIRCLE8[left(direction)]);
			}
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[direction]]){
				toCells.add(cell + PathFinder.CIRCLE8[direction]);
			}
			if (!Dungeon.level.solid[cell + PathFinder.CIRCLE8[right(direction)]]){
				toCells.add(cell + PathFinder.CIRCLE8[right(direction)]);
			}
		}
		
		private int left(int direction){
			return direction == 0 ? 7 : direction-1;
		}
		
		private int right(int direction){
			return direction == 7 ? 0 : direction+1;
		}
		
		private static final String DIRECTION = "direction";
		private static final String CUR_CELLS = "cur_cells";
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( DIRECTION, direction );
			if (curCells != null) bundle.put( CUR_CELLS, curCells );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			direction = bundle.getInt( DIRECTION );
			if (bundle.contains( CUR_CELLS )) curCells = bundle.getIntArray( CUR_CELLS );
		}
		
		public static class FireBlob extends Blob {
			
			{
				actPriority = BUFF_PRIO - 1;
				alwaysVisible = true;
			}
			
			@Override
			protected void evolve() {
				
				boolean observe = false;
				boolean burned = false;
				
				int cell;
				for (int i = area.left; i < area.right; i++){
					for (int j = area.top; j < area.bottom; j++){
						cell = i + j* Dungeon.level.width();
						off[cell] = (int)GameMath.gate(0, cur[cell] - 1, 1);
						
						if (off[cell] > 0) {
							volume += off[cell];
						}
						
						if (cur[cell] > 0 && off[cell] == 0){

							//similar to fire.burn(), but Tengu is immune, and hero loses score
							Char ch = Actor.findChar( cell );
							if (ch != null && !ch.isImmune(Fire.class) && !(ch instanceof Tengu)) {
								Buff.affect( ch, Burning.class ).reignite( ch );
							}
							if (ch == Dungeon.hero){
								Statistics.qualifiedForBossChallengesBadge[1] = false;
								Statistics.bossScores[1] -= 100;
							}

							Heap heap = Dungeon.level.heaps.get( cell );
							if (heap != null) {
								heap.burn();
							}

							Plant plant = Dungeon.level.plants.get( cell );
							if (plant != null){
								plant.wither();
							}
							
							if (Dungeon.level.isFlamable(cell)){
								Dungeon.level.destroy( cell );
								
								observe = true;
								GameScene.updateMap( cell );
							}
							
							burned = true;
							CellEmitter.get(cell).start(FlameParticle.FACTORY, 0.03f, 10);
						}
					}
				}
				
				if (observe) {
					Dungeon.observe();
				}
				
				if (burned){
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
				}
			}
			
			@Override
			public void use(BlobEmitter emitter) {
				super.use(emitter);
				
				emitter.pour( Speck.factory( Speck.STEAM ), 0.2f );
			}
			
			@Override
			public String tileDesc() {
				return Messages.get(this, "desc");
			}
		}
	}
	
	//*********************
	//***Shocker Ability***
	//*********************
	
	//returns true if shocker was thrown
	public static boolean throwShocker(final Char thrower, final Char target){
		
		int targetCell = -1;
		
		//Targets closest cell which is adjacent to target, and not adjacent to thrower or another shocker
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = target.pos + i;
			if (Dungeon.level.distance(cell, thrower.pos) >= 2 && !Dungeon.level.solid[cell]){
				boolean validTarget = true;
				for (ShockerAbility s : thrower.buffs(ShockerAbility.class)){
					if (Dungeon.level.distance(cell, s.shockerPos) < 2){
						validTarget = false;
						break;
					}
				}
				if (validTarget && Dungeon.level.trueDistance(cell, thrower.pos) < Dungeon.level.trueDistance(targetCell, thrower.pos)){
					targetCell = cell;
				}
			}
		}
		
		if (targetCell == -1){
			return false;
		}
		
		final int finalTargetCell = targetCell;
		throwingChar = thrower;
		final ShockerAbility.ShockerItem item = new ShockerAbility.ShockerItem();
		item.throwerId = thrower.id();
		if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = true;
		thrower.sprite.zap(finalTargetCell);
		if (thrower instanceof Tengu) ((Tengu) thrower).zapForAbility = false;
		((MissileSprite) thrower.sprite.parent.recycle(MissileSprite.class)).
				reset(thrower.sprite,
						finalTargetCell,
						item,
						new Callback() {
							@Override
							public void call() {
								item.onThrow(finalTargetCell);
								thrower.next();
							}
						});
		return true;
	}
	
	public static class ShockerAbility extends Buff {
	
		public int shockerPos, quantity = 1;
		private Boolean shockingOrdinals = null;
		
		@Override
		public boolean act() {
			
			if (shockingOrdinals == null){
				shockingOrdinals = Random.Int(2) == 1;
				
				spreadblob(quantity);
			} else if (shockingOrdinals){
				
				getGroupToAddVisuals().add(new Lightning(shockerPos - 1 - Dungeon.level.width(), shockerPos + 1 + Dungeon.level.width(), null));
				getGroupToAddVisuals().add(new Lightning(shockerPos - 1 + Dungeon.level.width(), shockerPos + 1 - Dungeon.level.width(), null));
				
				if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1){
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				}
				
				shockingOrdinals = false;
				spreadblob(quantity);
			} else {
				
				getGroupToAddVisuals().add(new Lightning(shockerPos - Dungeon.level.width(), shockerPos + Dungeon.level.width(), null));
				getGroupToAddVisuals().add(new Lightning(shockerPos - 1, shockerPos + 1, null));
				
				if (Dungeon.level.distance(Dungeon.hero.pos, shockerPos) <= 1){
					Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				}
				
				shockingOrdinals = true;
				spreadblob(quantity);
			}
			
			spend(TICK);
			return true;
		}
		
		protected void spreadblob(int quantity){
			GameScene.add(Blob.seed(shockerPos, quantity, getBlobClass()));
			for (int i = shockingOrdinals ? 0 : 1; i < PathFinder.CIRCLE8.length; i += 2){
				if (!Dungeon.level.solid[shockerPos+PathFinder.CIRCLE8[i]]) {
					GameScene.add(Blob.seed(shockerPos + PathFinder.CIRCLE8[i], quantity, getBlobClass()));
				}
			}
		}

		protected Class<? extends ShockerBlob> getBlobClass(){
			return ShockerBlob.class;
		}

		protected Group getGroupToAddVisuals() {
			return target.sprite.parent;
		}

		private static final String SHOCKER_POS = "shocker_pos";
		private static final String SHOCKING_ORDINALS = "shocking_ordinals";
		private static final String QUANTITY = "quantity";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( SHOCKER_POS, shockerPos );
			bundle.put( QUANTITY, quantity );
			if (shockingOrdinals != null) bundle.put( SHOCKING_ORDINALS, shockingOrdinals );
		}
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			shockerPos = bundle.getInt( SHOCKER_POS );
			quantity = Math.max(1, bundle.getInt( QUANTITY ));
			if (bundle.contains(SHOCKING_ORDINALS)) shockingOrdinals = bundle.getBoolean( SHOCKING_ORDINALS );
		}
		
		public static class ShockerBlob extends Blob {
			
			{
				actPriority = BUFF_PRIO - 1;
				alwaysVisible = true;
			}

			protected int[] cur2;

			@Override
			protected void evolve() {

				boolean shocked = false;
				
				int cell;
				for (int i = area.left; i < area.right; i++){
					for (int j = area.top; j < area.bottom; j++){
						cell = i + j* Dungeon.level.width();

						int dmg = (int) ((cur2[cell]+1)/2f);
						cur2[cell] = Math.max(0, cur[cell] - 1);
						off[cell] = cur2[cell] >= 1 ? 1 : 0;
						cur[cell] = 0;
						
						if (off[cell] > 0) {
							volume += off[cell];
						}
						
						if (dmg > 0){

							shocked = true;
							
							Char ch = Actor.findChar(cell);
							if (ch != null && !(ch instanceof Tengu)){
								ch.damage((2 + Dungeon.scalingDepth()) * dmg, new Electricity());
								
								if (ch == Dungeon.hero){
									reduceBossScore();
									if (!ch.isAlive()) {
										Dungeon.fail(Tengu.class);
										GLog.n(Messages.get(Electricity.class, "ondeath"));
									}
								}
							}
							
						}
					}
				}

				if (shocked) Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
				
			}

			public void seedNoCooldown(int cell, int amount) {
				seed(Dungeon.level, cell, 0);
				cur2[cell] += amount * 2;
				off[cell] = cur2[cell];
			}

			public void actAfterThrow() {
				spendConstant(-1f);
			}

			@Override
			public void use(BlobEmitter emitter) {
				super.use(emitter);
				
				emitter.pour( SparkParticle.STATIC, 0.10f );
			}
			
			@Override
			public String tileDesc() {
				return Messages.get(this, "desc");
			}

			protected void reduceBossScore() {
				Statistics.qualifiedForBossChallengesBadge[1] = false;
				Statistics.bossScores[1] -= 100;
			}

			private static final String CUR2	= "cur2";
			private static final String START2  = "start2";
			private static final String LENGTH2	= "length2";

			@Override
			public void storeInBundle( Bundle bundle ) {
				super.storeInBundle( bundle );
				if (volume > 0) {
					int start;
					for (start=0; start < Dungeon.level.length(); start++) {
						if (cur2[start] > 0) {
							break;
						}
					}
					int end;
					for (end=Dungeon.level.length()-1; end > start; end--) {
						if (cur[end] > 0) {
							break;
						}
					}
					bundle.put( START2, start );
					bundle.put( LENGTH2, cur.length );
					bundle.put( CUR2, trim( start, end + 1, cur2 ) );
				}
			}

			@Override
			public void restoreFromBundle( Bundle bundle ) {
				super.restoreFromBundle( bundle );
				if (bundle.contains( CUR2 )) {
					cur2 = new int[bundle.getInt(LENGTH2)];
					int[] data = bundle.getIntArray(CUR2);
					int start = bundle.getInt(START2);
					System.arraycopy(data, 0, cur2, start, data.length);
				}
			}

			public void setupArea(){
				super.setupArea();
				for (int cell=0; cell < cur2.length; cell++) {
					if (cur2[cell] != 0){
						area.union(cell%Dungeon.level.width(), cell/Dungeon.level.width());
					}
				}
			}
			public void seed( Level level, int cell, int amount ) {
				if (cur2 == null) cur2 = new int[level.length()];
				super.seed(level, cell, amount * 2);
			}

			public void clear( int cell ) {
				super.clear(cell);
				cur2[cell] = 0;
			}

			public void fullyClear(){
				super.fullyClear();
				cur2 = new int[Dungeon.level.length()];
			}
		}
		
		public static class ShockerItem extends TenguAbilityItem {
			
			{
				dropsDownHeap = true;
				unique = true;
				
				image = ItemSpriteSheet.TENGU_SHOCKER;
			}
			
			@Override
			public boolean doPickUp(Hero hero, int pos) {
				GLog.w( Messages.get(this, "cant_pickup") );
				return false;
			}
			
			@Override
			protected void onThrow(int cell) {
				super.onThrow(cell);
				if (throwingChar != null){
					Buff.append(throwingChar, ShockerAbility.class).shockerPos = cell;
					throwingChar = null;
				} else {
					Buff.append(curUser, ShockerAbility.class).shockerPos = cell;
				}
			}
			
			@Override
			public Emitter emitter() {
				Emitter emitter = new Emitter();
				emitter.pos(5, 5);
				emitter.fillTarget = false;
				emitter.pour(SparkParticle.FACTORY, 0.1f);
				return emitter;
			}
		}
		
	}
}
