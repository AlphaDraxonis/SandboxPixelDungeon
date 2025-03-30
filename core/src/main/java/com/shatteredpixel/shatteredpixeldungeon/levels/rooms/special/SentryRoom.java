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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSpriteExtraEmitter;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EyeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.LinkedHashMap;

public class SentryRoom extends SpecialRoom {

	{
		spawnItemsOnLevel.add(new PotionOfHaste());
	}

	@Override
	public int minWidth() { return 7; }
	public int minHeight() { return 7; }

	@Override
	public void paint(Level level) {

		Painter.fill( level, this, Terrain.WALL );
		Painter.fill( level, this, 1, Terrain.EMPTY_SP );

		Door entrance = entrance();

		Point center;
		do {
			center = center();
		} while (center.x == entrance.x || center.y == entrance.y);

		Point sentryPos = new Point();
		Point treasurePos = new Point();

		//length of dangerous path from entrance to treasure and back
		int dangerDist = 0;
		int sentryRange = 0;

		//determine position of sentry, treasure, and paint safe tiles / statues
		if (entrance.x == left){
			sentryPos.set(right-1, center.y);
			Painter.fill(level, left+1, top+1, 1, height()-2, Terrain.EMPTY);
			if (entrance.y > center.y){
				treasurePos.set(left+1, (top + 1 + center.y)/2);
				Painter.fill(level, left+1, top+1, 2, center.y-top-1, Terrain.EMPTY);
			} else {
				treasurePos.set(left+1, (bottom + center.y)/2);
				Painter.fill(level, left+1, center.y+1, 2, bottom-center.y-1, Terrain.EMPTY);
			}
			for (int x = right-3; x > left; x--){
				if (level.map[x + (center.y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, x, center.y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, x, center.y, Terrain.STATUE);
				}
				sentryRange++;
			}
			dangerDist = 2*(width()-5);
		} else if (entrance.x == right){
			sentryPos.set(left+1, center.y);
			Painter.fill(level, right-1, top+1, 1, height()-2, Terrain.EMPTY);
			if (entrance.y > center.y){
				treasurePos.set(right-1, (top + 1 + center.y)/2);
				Painter.fill(level, right-2, top+1, 2, center.y-top-1, Terrain.EMPTY);
			} else {
				treasurePos.set(right-1, (bottom + 1 + center.y)/2);
				Painter.fill(level, right-2, center.y+1, 2, bottom-center.y-1, Terrain.EMPTY);
			}
			for (int x = left+3; x < right; x++){
				if (level.map[x + (center.y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, x, center.y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, x, center.y, Terrain.STATUE);
				}
				sentryRange++;
			}
			dangerDist = 2*(width()-5);
		} else if (entrance.y == top){
			sentryPos.set(center.x, bottom-1);
			Painter.fill(level, left+1, top+1, width()-2, 1, Terrain.EMPTY);
			if (entrance.x > center.x){
				treasurePos.set((left + 1 + center.x)/2, top+1);
				Painter.fill(level, left+1, top+1, center.x-left-1, 2, Terrain.EMPTY);
			} else {
				treasurePos.set((right + center.x)/2, top+1);
				Painter.fill(level, center.x+1, top+1, right - center.x-1, 2, Terrain.EMPTY);
			}
			for (int y = bottom-3; y > top; y--){
				if (level.map[center.x + (y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, center.x, y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, center.x, y, Terrain.STATUE);
				}
				sentryRange++;
			}
			dangerDist = 2*(height()-5);
 		} else  if (entrance.y == bottom){
			sentryPos.set(center.x, top+1);
			Painter.fill(level, left+1, bottom-1, width()-2, 1, Terrain.EMPTY);
			if (entrance.x > center.x){
				treasurePos.set((left + 1 + center.x)/2, bottom-1);
				Painter.fill(level, left+1, bottom-2, center.x-left-1, 2, Terrain.EMPTY);
			} else {
				treasurePos.set((right + center.x)/2, bottom-1);
				Painter.fill(level, center.x+1, bottom-2, right - center.x-1, 2, Terrain.EMPTY);
			}
			for (int y = top+3; y < bottom; y++){
				if (level.map[center.x + (y * level.width())] == Terrain.EMPTY_SP){
					Painter.set(level, center.x, y, Terrain.STATUE_SP);
				} else {
					Painter.set(level, center.x, y, Terrain.STATUE);
				}
				sentryRange++;
			}
			dangerDist = 2*(height()-5);
		}

		Painter.set(level, sentryPos, Terrain.PEDESTAL);
		Sentry sentry = new Sentry();
		sentry.pos = level.pointToCell(sentryPos);
		sentry.room = new EmptyRoom();
		sentry.room.set(this);
		sentry.range = sentryRange;
		sentry.setInitialChargeDelay(Math.max(1, dangerDist / 3f + 0.1f));
		level.mobs.add( sentry );

		Painter.set(level, treasurePos, Terrain.PEDESTAL);

		if (!itemsGenerated) generateItems(level);

		int pos = level.pointToCell(treasurePos);
		for (Item i : spawnItemsInRoom) {
			level.drop( i, pos ).type = Heap.Type.CHEST;
		}
		spawnItemsInRoom.clear();

		entrance.set( Door.Type.REGULAR );
	}

	@Override
	public void generateItems(Level level) {
		super.generateItems(level);

		spawnItemsInRoom.add(prize(level));
	}

	private static Item prize(Level level ) {

		Item prize;

		//50% chance for prize item
		if (Random.Int(2) == 0){
			prize = level.findPrizeItem();
			if (prize != null)
				return prize;
		}

		//1 floor set higher in probability, never cursed
		//1 floor set higher in probability, never cursed
		if (Random.Int(2) == 0) {
			prize = Generator.randomWeapon(Dungeon.level.levelScheme.getRegion());
			if (((Weapon)prize).hasCurseEnchant()){
				((Weapon) prize).enchant(null);
			}
		} else {
			prize = Generator.randomArmor(Dungeon.level.levelScheme.getRegion());
			if (((Armor)prize).hasCurseGlyph()){
				((Armor) prize).inscribe(null);
			}
		}
		prize.cursed = false;
		prize.setCursedKnown(true);

		//33% chance for an extra update.
		if (Random.Int(3) == 0){
			prize.upgrade();
		}

		return prize;
	}

	@Override
	public boolean canConnect(Point p) {
		if (!super.canConnect(p)){
			return false;
		}
		//don't place door in the exact center, if that exists
		if (width() % 2 == 1 && p.x == center().x){
			return false;
		}
		if (height() % 2 == 1 && p.y == center().y){
			return false;
		}
		return true;
	}

	public static class Sentry extends NPC {

		{
			spriteClass = SentrySprite.class;

			properties.add(Property.IMMOVABLE);
		}

		private float initialChargeDelay = 1f;
		private float curChargeDelay = 1f;

		public EmptyRoom room;
		public int range = 5;//only room OR range is used

		private boolean charging;

		@Override
		protected boolean act() {
			if (Dungeon.level.heroFOV[pos]){
				Bestiary.setSeen(getClass());
			}

			if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
				fieldOfView = new boolean[Dungeon.level.length()];
			}
			Dungeon.level.updateFieldOfView( this, fieldOfView );

			if (properties().contains(Property.IMMOVABLE)){
				throwItems();
			}

			if (Dungeon.hero != null){
				if (fieldOfView[Dungeon.hero.pos]
						&& Dungeon.level.map[Dungeon.hero.pos] == Terrain.EMPTY_SP
						&& isInRange(Dungeon.hero.pos)
						&& !Dungeon.hero.belongings.lostInventory()){

					if (curChargeDelay > 0.001f){ //helps prevent rounding errors
						if (curChargeDelay == initialChargeDelay) {
							charging = true;
							SentrySprite.charge(sprite, Dungeon.hero.pos);
						}
						curChargeDelay -= Dungeon.hero.cooldown();
						//pity mechanic so mistaps don't get people instakilled
						if (Dungeon.hero.cooldown() >= 0.34f){
							Dungeon.hero.interrupt();
						}
					}

					if (curChargeDelay <= .001f){
						curChargeDelay = 1f;
						sprite.zap(Dungeon.hero.pos);
						if (sprite.instantZapDamage()) zap();
						charging = true;
						SentrySprite.charge(sprite, Dungeon.hero.pos);
					}

					spend(Dungeon.hero.cooldown());
					return true;

				} else {
					curChargeDelay = initialChargeDelay;
					charging = false;
					sprite.idle();
				}

				spend(Dungeon.hero.cooldown());
			} else {
				spend(1f);
			}
			return true;
		}

		@Override
		public void playZapAnim(int target) {
			SentrySprite.playZap(sprite.parent, sprite, target, this);
		}

		@Override
		public void onZapComplete(){
			if (!sprite.instantZapDamage()) //else zap was called in the beginning
				zap();
		}

		@Override
		public void zap() {
			if (hit(this, Dungeon.hero, true)) {
				Dungeon.hero.damage((int) (Random.NormalIntRange(2 + Dungeon.depth / 2, 4 + Dungeon.depth) * statsScale), new Eye.DeathGaze());
				if (!Dungeon.hero.isAlive()) {
					Badges.validateDeathFromEnemyMagic();
					Dungeon.fail(this);
					GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", name())));
				}
			} else {
				Dungeon.hero.sprite.showStatus( CharSprite.NEUTRAL,  Dungeon.hero.defenseVerb() );
			}
		}

		private boolean isInRange(int pos) {
			if (room == null) {
				Ballistica b = new Ballistica(this.pos, pos, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.STOP_BARRIER_PROJECTILES, null);
				return b.collisionPos == pos && b.dist <= range;
			}
			return room.inside(Dungeon.level.cellToPoint(pos));
		}

		public void setInitialChargeDelay(float initialChargeDelay) {
			this.initialChargeDelay = curChargeDelay = initialChargeDelay;
		}

		public float getInitialChargeDelay() {
			return initialChargeDelay;
		}

		@Override
		public int attackSkill(Char target) {
			return (int) ((attackSkill = 20 + Dungeon.depth * 2) * statsScale);
		}

		@Override
		public int defenseSkill( Char enemy ) {
			return INFINITE_EVASION;
		}

		@Override
		public void damage( int dmg, Object src ) {
			//do nothing
		}

		@Override
		public boolean add( Buff buff ) {
			return false;
		}

		@Override
		public boolean reset() {
			return true;
		}

		@Override
		public boolean interact(Char c) {
			return true;
		}

		@Override
		public String desc() {
			return super.desc() + (CustomDungeon.isEditing() ? Messages.get(this, "desc_add") : "");
		}

		private static final String INITIAL_DELAY = "initial_delay";
		private static final String CUR_DELAY = "cur_delay";
		private static final String ROOM = "room";
		private static final String RANGE = "range";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(INITIAL_DELAY, initialChargeDelay);
			bundle.put(CUR_DELAY, curChargeDelay);
			bundle.put(ROOM, room);
			bundle.put(RANGE, range);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			initialChargeDelay = bundle.getFloat(INITIAL_DELAY);
			curChargeDelay = bundle.getFloat(CUR_DELAY);
			room = (EmptyRoom) bundle.get(ROOM);
			range = bundle.getInt(RANGE);
		}
	}

	public static class SentrySprite extends MobSprite {

		private Animation charging;

		@Override
		public LinkedHashMap<String, Animation> getAnimations() {
			LinkedHashMap<String, Animation> result = super.getAnimations();
			result.put("charging", charging);
			return result;
		}

		public SentrySprite(){
			texture( Assets.Sprites.RED_SENTRY );

			idle = new Animation(1, true);
			idle.frames(texture.uvRect(0, 0, 8, 15));

			run = idle.clone();
			run.delay = 1f / 5;

			attack = run.clone();
			charging = idle.clone();
			die = idle.clone();
			zap = attack.clone();

			play( idle );
		}

		@Override
		public boolean hasOwnZapAnimation() {
			return true;
		}

		@Override
		protected void playZapAnim(int cell) {
			playZap(parent, this, cell, ch);
		}

		public static void playZap(Group parent, CharSprite sprite, int cell, Char ch) {
			sprite.idle();
			sprite.flash();
			sprite.emitter().burst(MagicMissile.WardParticle.UP, 2);
			EyeSprite.playZap(parent, sprite, cell, ch);
		}

		@Override
		public void link(Char ch) {
			super.link(ch);

			if (ch instanceof Sentry && ((Sentry)ch).curChargeDelay != ((Sentry) ch).initialChargeDelay){
				play(charging);
			}
		}

		public static void charge( CharSprite sprite, int pos ){
			EyeSprite.charge(sprite, pos, sprite instanceof SentrySprite ? ((SentrySprite) sprite).charging : null);
		}

		public static class ChargeParticles extends CharSpriteExtraEmitter {

			private Char ch;
			@Override
			public void onLink(CharSprite sprite, Char ch) {
				this.ch = ch;
				emitter = sprite.centerEmitter();
				emitter.autoKill = false;
				emitter.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
				emitter.on = false;
			}

			@Override
			public void onUpdate(CharSprite sprite) {
				if (emitter != null){
					emitter.pos( sprite.center() );
					emitter.visible = sprite.visible;
				}
			}

			@Override
			public void onPlayAnimation(CharSprite sprite, Animation anim) {
				if (ch instanceof Sentry) {
					emitter.on = ((Sentry) ch).charging;
				}
			}
		}

		private float baseY = Float.NaN;

		@Override
		public void place(int cell) {
			super.place(cell);
			baseY = y;
		}

		@Override
		public void turnTo(int from, int to) {
			//do nothing
		}

		@Override
		public void update() {
			super.update();

			if (!paused){
				if (Float.isNaN(baseY) || Math.abs(baseY - y) > 2) baseY = y;
				y = baseY + (float) Math.sin(Game.timeTotal);
				shadowOffset = 0.25f - 0.8f*(float) Math.sin(Game.timeTotal);
			}
		}

	}

}
