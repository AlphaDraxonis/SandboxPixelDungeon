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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ShadowClone extends ArmorAbility {

	@Override
	public String targetingPrompt() {
		if (getShadowAlly() == null) {
			return super.targetingPrompt();
		} else {
			return Messages.get(this, "prompt");
		}
	}

	@Override
	public boolean useTargeting(){
		return false;
	}

	{
		baseChargeUse = 35f;
	}

	@Override
	public float chargeUse(Hero hero) {
		if (getShadowAlly() == null) {
			return super.chargeUse(hero);
		} else {
			return 0;
		}
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		ShadowAlly ally = getShadowAlly();

		if (ally != null){
			if (target == null){
				return;
			} else {
				ally.directableAlly.directTocell(target);
			}
		} else {
			ArrayList<Integer> spawnPoints = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && Dungeon.level.isPassableAlly(p)) {
					spawnPoints.add(p);
				}
			}

			if (!spawnPoints.isEmpty()){
				armor.charge -= chargeUse(hero);
				armor.updateQuickslot();

				ally = new ShadowAlly(hero.lvl);
				ally.pos = Random.element(spawnPoints);
				GameScene.add(ally);

				ShadowAlly.appear(ally, ally.pos);

				Invisibility.dispel();
				hero.spendAndNext(Actor.TICK);

			} else {
				GLog.w(Messages.get(SpiritHawk.class, "no_space"));
			}
		}

	}

	@Override
	public int icon() {
		return HeroIcon.SHADOW_CLONE;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.SHADOW_BLADE, Talent.CLONED_ARMOR, Talent.PERFECT_COPY, Talent.HEROIC_ENERGY};
	}

	private static ShadowAlly getShadowAlly(){
		for (Char ch : Actor.chars()){
			if (ch instanceof ShadowAlly){
				return (ShadowAlly) ch;
			}
		}
		return null;
	}

	public static class ShadowAlly extends NPC {

		private DirectableAlly directableAlly;

		{
			alignment = Alignment.ALLY;
			directableAlly = new ShadowDirectableAlly(this);
			state = WANDERING;

			spriteClass = ShadowSprite.class;

			HP = HT = 80;

			immunities.add(AllyBuff.class);

			properties.add(Property.INORGANIC);
		}

		public ShadowAlly(){
			super();
		}

		public ShadowAlly( int heroLevel ){
			super();
			int hpBonus = 15 + 5*heroLevel;
			hpBonus = Math.round(0.1f * Dungeon.hero.pointsInTalent(Talent.PERFECT_COPY) * hpBonus);
			if (hpBonus > 0){
				HT += hpBonus;
				HP += hpBonus;
			}
			defenseSkill = heroLevel + 5; //equal to base hero defense skill
		}

		@Override
		protected boolean act() {
			int oldPos = pos;
			boolean result = super.act();
			//partially simulates how the hero switches to idle animation
			if ((pos == target || oldPos == pos) && sprite.looping()){
				sprite.idle();
			}
			return result;
		}

		public static class ShadowDirectableAlly extends DirectableAlly {

			public ShadowDirectableAlly(Mob mob) {
				super(mob);
			}

			@Override
			public void defendPos(int cell) {
				GLog.i(Messages.get(mob.getClass(), "direct_defend"));
				super.defendPos(cell);
			}

			@Override
			public void followHero() {
				GLog.i(Messages.get(mob.getClass(), "direct_follow"));
				super.followHero();
			}

			@Override
			public void targetChar(Char ch) {
				GLog.i(Messages.get(mob.getClass(), "direct_attack"));
				super.targetChar(ch);
			}
		}

		@Override
		public void aggro(Char ch) {
			directableAlly.aggroOverride(ch);
		}

		@Override
		public DirectableAlly getDirectableAlly() {
			return directableAlly;
		}

		@Override
		public int attackSkill(Char target) {
			return defenseSkill+5; //equal to base hero attack skill
		}

		@Override
		public int damageRoll() {
			int damage = Char.combatRoll(10, 20);
			int heroDamage = Dungeon.hero.damageRoll();
			heroDamage /= Dungeon.hero.attackDelay(); //normalize hero damage based on atk speed
			heroDamage = Math.round(0.08f * Dungeon.hero.pointsInTalent(Talent.SHADOW_BLADE) * heroDamage);
			if (heroDamage > 0){
				damage += heroDamage;
			}
			return damage;
		}

		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );
			if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.SHADOW_BLADE)
					&& Dungeon.hero.belongings.weapon() != null){
				return Dungeon.hero.belongings.weapon().proc( this, enemy, damage );
			} else {
				return damage;
			}
		}

		@Override
		public int drRoll() {
			int dr = super.drRoll();
			int heroRoll = Dungeon.hero.drRoll();
			heroRoll = Math.round(0.12f * Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR) * heroRoll);
			if (heroRoll > 0){
				dr += heroRoll;
			}
			return dr;
		}

		@Override
		public boolean isImmune(Class effect) {
			if (effect == Burning.class
					&& Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR)
					&& Dungeon.hero.belongings.armor() != null
					&& Dungeon.hero.belongings.armor().hasGlyph(Brimstone.class, this)){
				return true;
			}
			return super.isImmune(effect);
		}

		@Override
		public int defenseProc(Char enemy, int damage) {
			damage = super.defenseProc(enemy, damage);
			if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR)
					&& Dungeon.hero.belongings.armor() != null){
				return Dungeon.hero.belongings.armor().proc( enemy, this, damage );
			} else {
				return damage;
			}
		}

		@Override
		public void damage(int dmg, Object src) {

			//TODO improve this when I have proper damage source logic
			if (Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.CLONED_ARMOR)
					&& Dungeon.hero.belongings.armor() != null
					&& Dungeon.hero.belongings.armor().hasGlyph(AntiMagic.class, this)
					&& AntiMagic.RESISTS.contains(src.getClass())){
				dmg -= AntiMagic.drRoll(Dungeon.hero, Dungeon.hero.belongings.armor().buffedLvl());
				dmg = Math.max(dmg, 0);
			}

			super.damage(dmg, src);
		}

		@Override
		public float speed() {
			float speed = super.speed();

			//moves 2 tiles at a time when returning to the hero
			if (state == WANDERING
					&& directableAlly.defendingPos == -1
					&& Dungeon.level.distance(pos, Dungeon.hero.pos) > 1){
				speed *= 2;
			}

			return speed;
		}

		@Override
		public boolean canInteract(Char c) {
			if (super.canInteract(c)){
				return true;
			} else if (Dungeon.level.distance(pos, c.pos) <= Dungeon.hero.pointsInTalent(Talent.PERFECT_COPY)) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public boolean interact(Char c) {
			if (!Dungeon.hero.hasTalent(Talent.PERFECT_COPY)){
				return super.interact(c);
			}

			//some checks from super.interact
			if (Dungeon.level.pit[pos] && !c.isFlying()){
				return true;
			}
			if (!Dungeon.level.isPassable(pos, c) || !Dungeon.level.isPassable(c.pos, this)) {
				return true;
			}

			if (properties().contains(Property.LARGE) && !Dungeon.level.openSpace[c.pos]
					|| c.properties().contains(Property.LARGE) && !Dungeon.level.openSpace[pos]){
				return true;
			}

			int curPos = pos;

			PathFinder.buildDistanceMap(c.pos, Dungeon.level.getPassableAndAvoidVarForBoth(c, this));
			if (PathFinder.distance[pos] == Integer.MAX_VALUE){
				return true;
			}
			appear(this, Dungeon.hero.pos);
			appear(Dungeon.hero, curPos);
			Dungeon.observe();
			GameScene.updateFog();
			return true;
		}

		private static void appear( Char ch, int pos ) {

			ch.sprite.interruptMotion();

			if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[ch.pos]){
				Sample.INSTANCE.play(Assets.Sounds.PUFF);
			}

			ch.move( pos );
			if (ch.pos == pos) ch.sprite.place( pos );

			if (Dungeon.level.heroFOV[pos] || ch == Dungeon.hero ) {
				ch.sprite.emitter().burst(SmokeParticle.FACTORY, 10);
			}
		}

		private static final String DEF_SKILL = "def_skill";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(DEF_SKILL, defenseSkill);
			directableAlly.store(bundle);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			defenseSkill = bundle.getInt(DEF_SKILL);
			directableAlly.restore(bundle);
		}
	}

	public static class ShadowSprite extends MobSprite {

		private Emitter smoke;

		public ShadowSprite() {
			super();

			texture( HeroClass.ROGUE.spritesheet() );

			TextureFilm film = new TextureFilm( HeroSprite.tiers(), 6, 12, 15 );

			idle = new Animation( 1, true );
			idle.frames( film, 0, 0, 0, 1, 0, 0, 1, 1 );

			run = new Animation( 20, true );
			run.frames( film, 2, 3, 4, 5, 6, 7 );

			die = new Animation( 20, false );
			die.frames( film, 0 );

			attack = new Animation( 15, false );
			attack.frames( film, 13, 14, 15, 0 );

			idle();
			resetColor();
		}

		@Override
		public void onComplete(Tweener tweener) {
			super.onComplete(tweener);
		}

		@Override
		public void resetColor() {
			super.resetColor();
			alpha(0.8f);
			brightness(0.0f);
		}

		@Override
		public void link( Char ch ) {
			super.link( ch );
			renderShadow = false;

			if (smoke == null) {
				smoke = emitter();
				smoke.pour( CityLevel.Smoke.factory, 0.2f );
			}
		}

		@Override
		public void update() {

			super.update();

			if (smoke != null) {
				smoke.visible = visible;
			}
		}

		@Override
		public void kill() {
			super.kill();

			if (smoke != null) {
				smoke.on = false;
			}
		}
	}
}