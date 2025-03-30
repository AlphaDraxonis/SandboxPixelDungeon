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

package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Point;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

public class RitualSiteRoom extends StandardRoom {

	{
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
		spawnItemsOnLevel.add(new CeremonialCandle());
	}

	private int ownRitualPos;
	
	@Override
	public int minWidth() {
		return Math.max(super.minWidth(), 9);
	}
	
	@Override
	public int minHeight() {
		return Math.max(super.minHeight(), 9);
	}

	public void paint( Level level ) {

		for (Door door : connected.values()) {
			door.set( Door.Type.REGULAR );
		}

		Painter.fill(level, this, Terrain.WALL);
		Painter.fill(level, this, 1, Terrain.EMPTY);

		RitualMarker vis = new RitualMarker();
		Point c = center();
		vis.pos(c.x, c.y);

		level.customTiles.add(vis);
		
		Painter.fill(level, c.x-1, c.y-1, 3, 3, Terrain.CUSTOM_DECO_EMPTY);

		ownRitualPos = c.x + (level.width() * c.y);
	}

	@Override
	public boolean canPlaceItem(Point p, Level l) {
		return super.canPlaceItem(p, l) && l.distance(ownRitualPos, l.pointToCell(p)) >= 2;
	}

	@Override
	public boolean canPlaceCharacter(Point p, Level l) {
		return super.canPlaceItem(p, l) && l.distance(ownRitualPos, l.pointToCell(p)) >= 2;
	}

	public static class RitualMarker extends CustomTilemap {

		protected Class<? extends Mob> defaultTemplateClass;
		public List<Mob> summons = new ArrayList<>();

		{
			texture = Assets.Environment.PRISON_QUEST;
			
			tileW = tileH = 3;

			offsetCenterX = offsetCenterY = 1;
		}

		{
			defaultTemplateClass = Elemental.NewbornFireElemental.class;
			Mob summon = Reflection.newInstance(defaultTemplateClass);
			summon.state = summon.HUNTING;
			summons.clear();
			summons.add(summon);
		}
		
		final int TEX_WIDTH = 64;


		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			v.map(mapSimpleImage(0, 0, TEX_WIDTH), 3);
			return v;
		}

		@Override
		public String name(int tileX, int tileY) {
			return Messages.get(this, "name");
		}

		@Override
		public String desc(int tileX, int tileY) {
			return Messages.get(this, "desc");
		}

		@Override
		public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
			return super.doOnAllGameObjects(whatToDo)
					| doOnAllGameObjectsList(summons, whatToDo);
		}

		private static final String SUMMONS = "summons";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SUMMONS, summons);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			if (bundle.getBoolean("used")) {
				summons.clear();
			} else {
				if (bundle.contains(SUMMONS)) {
					summons.clear();
					for (Bundlable mob : bundle.getCollection(SUMMONS))
						summons.add((Mob) mob);
				}
			}
		}

		public Mob getMobToSummon() {
			return canSummonMobs() ? summons.remove(0) : null;
		}

		public boolean canSummonMobs() {
			return !summons.isEmpty();
		}
	}

}
