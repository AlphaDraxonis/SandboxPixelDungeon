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

package com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.rogue;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.Char;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.AllyBuff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Amok;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Blindness;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Buff;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Charm;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Dread;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Haste;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Invisibility;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Sleep;
import com.alphadraxonis.sandboxpixeldungeon.actors.buffs.Terror;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Talent;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.NPC;
import com.alphadraxonis.sandboxpixeldungeon.effects.CellEmitter;
import com.alphadraxonis.sandboxpixeldungeon.effects.Speck;
import com.alphadraxonis.sandboxpixeldungeon.items.Item;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.ClassArmor;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.GameScene;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.MobSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.HeroIcon;
import com.alphadraxonis.sandboxpixeldungeon.utils.BArray;
import com.alphadraxonis.sandboxpixeldungeon.utils.GLog;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class SmokeBomb extends ArmorAbility {

	{
		baseChargeUse = 50;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public boolean useTargeting() {
		return false;
	}

	@Override
	public float chargeUse(Hero hero) {
		if (!hero.hasTalent(Talent.SHADOW_STEP) || hero.invisible <= 0){
			return super.chargeUse(hero);
		} else {
			//reduced charge use by 16%/30%/41%/50%
			return (float)(super.chargeUse(hero) * Math.pow(0.84, hero.pointsInTalent(Talent.SHADOW_STEP)));
		}
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target != null) {

			if (target != hero.pos && hero.rooted){
				PixelScene.shake( 1, 1f );
				return;
			}

			PathFinder.buildDistanceMap(hero.pos, BArray.not(Dungeon.level.solid,null), 6);

			if ( PathFinder.distance[target] == Integer.MAX_VALUE ||
					!Dungeon.level.heroFOV[target] ||
					(target != hero.pos && Actor.findChar( target ) != null)) {

				GLog.w( Messages.get(this, "fov") );
				return;
			}

			armor.charge -= chargeUse(hero);
			Item.updateQuickslot();

			boolean shadowStepping = hero.invisible > 0 && hero.hasTalent(Talent.SHADOW_STEP);

			if (!shadowStepping) {
				for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
					if (Dungeon.level.adjacent(mob.pos, hero.pos) && mob.alignment != Char.Alignment.ALLY) {
						Buff.prolong(mob, Blindness.class, Blindness.DURATION / 2f);
						if (mob.state == mob.HUNTING) mob.state = mob.WANDERING;
						mob.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 4);
					}
				}

				if (hero.hasTalent(Talent.BODY_REPLACEMENT)) {
					for (Char ch : Actor.chars()){
						if (ch instanceof NinjaLog){
							ch.die(null);
						}
					}

					NinjaLog n = new NinjaLog();
					n.pos = hero.pos;
					GameScene.add(n);
				}

				if (hero.hasTalent(Talent.HASTY_RETREAT)){
					//effectively 1/2/3/4 turns
					float duration = 0.67f + hero.pointsInTalent(Talent.HASTY_RETREAT);
					Buff.affect(hero, Haste.class, duration);
					Buff.affect(hero, Invisibility.class, duration);
				}
			}

			CellEmitter.get( hero.pos ).burst( Speck.factory( Speck.WOOL ), 10 );
			ScrollOfTeleportation.appear( hero, target );
			Sample.INSTANCE.play( Assets.Sounds.PUFF );
			Dungeon.level.occupyCell( hero );
			Dungeon.observe();
			GameScene.updateFog();

			if (!shadowStepping) {
				hero.spendAndNext(Actor.TICK);
			} else {
				hero.next();
			}
		}
	}

	@Override
	public int icon() {
		return HeroIcon.SMOKE_BOMB;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HASTY_RETREAT, Talent.BODY_REPLACEMENT, Talent.SHADOW_STEP, Talent.HEROIC_ENERGY};
	}

	public static class NinjaLog extends NPC {

		{
			spriteClass = NinjaLogSprite.class;
			defenseSkill = 0;

			properties.add(Property.INORGANIC); //wood is organic, but this is accurate for game logic

			alignment = Alignment.ALLY;

			HP = HT = 20*Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT);
		}

		@Override
		public int drRoll() {
			int dr = super.drRoll();

			dr += Random.NormalIntRange(Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT),
					3*Dungeon.hero.pointsInTalent(Talent.BODY_REPLACEMENT));

			return dr;
		}

		{
			immunities.add( Dread.class );
			immunities.add( Terror.class );
			immunities.add( Amok.class );
			immunities.add( Charm.class );
			immunities.add( Sleep.class );
			immunities.add( AllyBuff.class );
		}

	}

	public static class NinjaLogSprite extends MobSprite {

		public NinjaLogSprite(){
			super();

			texture( Assets.Sprites.NINJA_LOG );

			TextureFilm frames = new TextureFilm( texture, 11, 12 );

			idle = new Animation( 0, true );
			idle.frames( frames, 0 );

			run = idle.clone();
			attack = idle.clone();
			zap = attack.clone();

			die = new Animation( 12, false );
			die.frames( frames, 1, 2, 3, 4 );

			play( idle );

		}

		@Override
		public void showAlert() {
			//do nothing
		}

		@Override
		public int blood() {
			return 0xFF966400;
		}

	}
}