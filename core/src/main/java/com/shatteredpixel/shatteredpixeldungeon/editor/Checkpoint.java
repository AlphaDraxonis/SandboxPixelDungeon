/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import com.watabou.utils.PointF;

import java.io.IOException;

public class Checkpoint implements Bundlable, Copyable<Checkpoint> {

	private int timesUsed;
	public CheckpointSprite sprite;

	public int pos;
	public int totalSaves = 1000;
	public boolean healingAvailable;
	public boolean satiationAvailable;
	public boolean debuffCuringAvailable;
	public boolean uncursesAvailable;

	public String name() {
		return Messages.get(this, "name");
	}

	public String desc() {
		int usesAvailable = usesAvailable();
		if (usesAvailable <= 0) {
			return Messages.get(this, "inactive_desc");
		} else {
			String desc = Messages.get(this, "desc");
			if (usesAvailable == 1) desc += "\n\n" + Messages.get(this, "save_once");
			else if (usesAvailable <= 50 ) desc += "\n\n" + Messages.get(this, "save_often", usesAvailable);
			else desc += "\n\n" + Messages.get(this, "save_very_often");
			return desc;
		}
	}

	public CheckpointSprite getSprite() {
		return new CheckpointSprite(this);
	}

	public int usesAvailable() {
		return totalSaves - timesUsed;
	}

	public int healingsAvailable() {
		return healingAvailable ? usesAvailable() : 0;
	}

	public int satiationsAvailable() {
		return satiationAvailable ? usesAvailable() : 0;
	}

	public int debuffCuringAvailable() {
		return debuffCuringAvailable ? usesAvailable() : 0;
	}

	public int uncursesAvailable() {
		return uncursesAvailable ? usesAvailable() : 0;
	}

	public void use() {

		timesUsed++;

		if (usesAvailable() <= 0) {
			FileUtils.getFileHandle(GamesInProgress.checkpointFolder(GamesInProgress.curSlot)).deleteDirectory();
			Dungeon.reachedCheckpoint = null;
		} else {
			doSave();
		}


		if (healingAvailable) {
			Buff.affect(Dungeon.hero, Healing.class).setHeal((int) (0.8f * Dungeon.hero.HT + 14), 0.25f, 0);
			GLog.p( Messages.get(PotionOfHealing.class, "heal") );
		}

		if (satiationAvailable) {
			Buff.affect(Dungeon.hero, Hunger.class).satisfy(10000);
		}

		if (debuffCuringAvailable) {
			PotionOfCleansing.cleanse(Dungeon.hero, 0);
		}

		if (uncursesAvailable) {
			Dungeon.hero.belongings.uncurseEquipped();
		}

		DungeonScene.updateAllCheckpointSprites();
	}

	public boolean isReached() {
		return Dungeon.reachedCheckpoint != null
				&& pos == Dungeon.reachedCheckpoint.cell
				&& Dungeon.branch == Dungeon.reachedCheckpoint.branch
				&& Dungeon.reachedCheckpoint.level.equals(Dungeon.levelName);
	}

	public boolean canReach() {
		return usesAvailable() > 0 && !isReached();
	}

	public void reachCheckpoint() {
		if (!canReach()) {
			return;
		}

		Actor.addDelayed(new Actor() {
			{
				actPriority = HERO_PRIO + 1;//right before hero
			}
			@Override
			protected boolean act() {
				Sample.INSTANCE.play(Assets.Sounds.DEWDROP, 1.6f);

				Dungeon.reachedCheckpoint = new ReachedCheckpoint();
				Dungeon.reachedCheckpoint.set(pos);

				DungeonScene.updateAllCheckpointSprites();
				doSave();

				Actor.remove(this);
				return true;
			}
		}, 1);
	}

	public static void doSave() {

		FileHandle gameFolder = FileUtils.getFileHandle(GamesInProgress.gameFolder(GamesInProgress.curSlot));
		FileHandle checkpointFolder = FileUtils.getFileHandle(GamesInProgress.checkpointFolder(GamesInProgress.curSlot));

		if (checkpointFolder.exists()) {
			checkpointFolder.deleteDirectory();
		}
		checkpointFolder.mkdirs();

		try {
			Dungeon.saveAll();
		} catch (IOException e) {
			Dungeon.reachedCheckpoint = null;
			SandboxPixelDungeon.reportException(e);
			Game.runOnRenderThread(() -> GameScene.show(new WndError("Error while saving: " + e.getMessage())));
			return;
		}

		for (FileHandle file : gameFolder.list(".dat")) {
			FileHandle dest = checkpointFolder.child(file.name());
			file.copyTo(dest);
		}
	}

	public static class ReachedCheckpoint implements Bundlable {

		public String level;
		public int branch;
		public int cell;

		public void set(int cell) {
			level = Dungeon.levelName;
			branch = Dungeon.branch;
			this.cell = cell;
		}

		private static final String LEVEL = "level";
		private static final String BRANCH = "branch";
		private static final String CELL = "cell";

		@Override
		public void restoreFromBundle(Bundle bundle) {
			level = bundle.getString(LEVEL);
			branch = bundle.getInt(BRANCH);
			cell = bundle.getInt(CELL);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			bundle.put(LEVEL, level);
			bundle.put(BRANCH, branch);
			bundle.put(CELL, cell);
		}
	}

	private static final String POS = "pos";
	private static final String TOTAL_SAVES = "total_saves";
	private static final String HEALING_AVAILABLE = "healing_available";
	private static final String SATIATION_AVAILABLE = "satiation_available";
	private static final String DEBUFF_CURING_AVAILABLE = "debuff_curing_available";
	private static final String UNCURSES_AVAILABLE = "uncurses_available";

	private static final String TIMES_USED = "times_used";

	@Override
	public void restoreFromBundle(Bundle bundle) {
		pos = bundle.getInt(POS);

		totalSaves = bundle.getInt(TOTAL_SAVES);

		if (bundle.contains("total_heals")) {
			healingAvailable = bundle.getInt("total_heals") > 0;
			satiationAvailable = bundle.getInt("total_satiates") > 0;
			debuffCuringAvailable = bundle.getInt("total_debuff_curing") > 0;
			uncursesAvailable = bundle.getInt("total_uncurse") > 0;
		} else {
			healingAvailable = bundle.getBoolean(HEALING_AVAILABLE);
			satiationAvailable = bundle.getBoolean(SATIATION_AVAILABLE);
			debuffCuringAvailable = bundle.getBoolean(DEBUFF_CURING_AVAILABLE);
			uncursesAvailable = bundle.getBoolean(UNCURSES_AVAILABLE);
		}

		timesUsed = bundle.getInt(TIMES_USED);
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		bundle.put(POS, pos);

		bundle.put(TOTAL_SAVES, totalSaves);

		bundle.put(HEALING_AVAILABLE, healingAvailable);
		bundle.put(SATIATION_AVAILABLE, satiationAvailable);
		bundle.put(DEBUFF_CURING_AVAILABLE, debuffCuringAvailable);
		bundle.put(UNCURSES_AVAILABLE, uncursesAvailable);

		bundle.put(TIMES_USED, timesUsed);
	}

	@Override
	public Checkpoint getCopy() {
		Bundle bundle = new Bundle();
		bundle.put("CHECKPOINT", this);
		return (Checkpoint) bundle.get("CHECKPOINT");
	}


	public static class CheckpointSprite extends MovieClip {

		public Checkpoint cp;

		protected MovieClip.Animation active;
		protected MovieClip.Animation inactive;

		protected Emitter save;
		protected Emitter heal;
		protected Emitter satiate;
		protected Emitter cleanse;
		protected Emitter uncurse;

		public CheckpointSprite(Checkpoint checkpoint) {

			texture( Assets.Sprites.CHECKPOINT );

			TextureFilm frames = new TextureFilm( texture, 10, 12 );

			Integer[] animation = new Integer[30];
			for (int i = 0; i < animation.length; i++)
				animation[i] = i;
			active = new Animation( animation.length, true );
			active.frames( frames, (Object[]) animation);

			inactive = new Animation( 0, true );
			inactive.frames( frames, 30 );

			updateSprite(checkpoint);
		}

		public void link( Checkpoint cp ) {

			this.cp = cp;
			cp.sprite = this;

			place( cp.pos );
		}

		public void updateSprite(Checkpoint cp) {
			if (cp.usesAvailable() > 0) {
				play(active);

				if (cp.pos != -1) {

					if (cp.isReached()) {
						if (save != null) save.on = false;
						save = emitter();
						save.pour(ShaftParticle.FACTORY, 1f * (cp.usesAvailable() > 1 ? 1.5f : 1f));
					} else if (save != null) {
						save.on = false;
						save = null;
					}

					if (cp.healingsAvailable() > 0) {
						if (heal != null) heal.on = false;
						heal = emitter();
						heal.pour(Speck.factory(Speck.HEALING), 1f * (cp.healingsAvailable() > 1 ? 1.5f : 1f));
					} else if (heal != null) {
						heal.on = false;
						heal = null;
					}

					if (cp.satiationsAvailable() > 0) {
						if (satiate != null) satiate.on = false;
						satiate = emitter();
						satiate.pour(Speck.factory(Speck.RED_LIGHT), 1f * (cp.satiationsAvailable() > 1 ? 1.5f : 1f));
					} else if (satiate != null) {
						satiate.on = false;
						satiate = null;
					}

					if (cp.debuffCuringAvailable() > 0) {
						if (cleanse != null )cleanse.on = false;
						cleanse = emitter();
						cleanse.pour(Speck.factory(Speck.HEART), 1f * (cp.debuffCuringAvailable() > 1 ? 1.5f : 1f));
					} else if (cleanse != null) {
						cleanse.on = false;
						cleanse = null;
					}

					if (cp.uncursesAvailable() > 0) {
						if (uncurse != null) uncurse.on = false;
						uncurse = emitter();
						uncurse.pour(Speck.factory(Speck.LIGHT), 1f * (cp.uncursesAvailable() > 1 ? 1.5f : 1f));
					} else if (uncurse != null) {
						uncurse.on = false;
						uncurse = null;
					}
				}

			} else {
				play(inactive);

				disableAllEmitters();

			}


		}

		private void disableAllEmitters() {
			if (save != null) {
				save.on = false;
				save = null;
			}
			if (heal != null) {
				heal.on = false;
				heal = null;
			}
			if (satiate != null) {
				satiate.on = false;
				satiate = null;
			}
			if (cleanse != null) {
				cleanse.on = false;
				cleanse = null;
			}
			if (uncurse != null) {
				uncurse.on = false;
				uncurse = null;
			}
		}

		public Emitter emitter() {
			Emitter emitter = GameScene.emitter();
			if (emitter != null) emitter.pos( this );
			return emitter;
		}

		public PointF worldToCamera(int cell ) {

			final int csize = DungeonTilemap.SIZE;

			return new PointF(
					PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - width() * 0.5f),
					PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - height() - csize * 6/16f)
			);
		}

		public void place( int cell ) {
			point( worldToCamera( cell ) );
		}

		@Override
		public void destroy() {
			super.destroy();
			disableAllEmitters();
		}
	}

}