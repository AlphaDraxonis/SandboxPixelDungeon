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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.ImpQuest;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.Game;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.WatabouRect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CityBossLevel extends Level {

	{
		color1 = 0x4b6636;
		color2 = 0xf2f2f2;
	}

	private static int WIDTH = 15;
	private static int HEIGHT = 48;

	private static final WatabouRect entry = new WatabouRect(1, 37, 14, 48);
	private static final WatabouRect arena = new WatabouRect(1, 25, 14, 38);
	private static final WatabouRect end = new WatabouRect(0, 0, 15, 22);

	private static final int bottomDoor = 7 + (arena.bottom-1)*15;
	private static final int topDoor = 7 + arena.top*15;

	public static final int throne;
	private static final int[] pedestals = new int[4];

	static {
		Point c = arena.center();
		throne = c.x + (c.y) * WIDTH;
		pedestals[0] = c.x-3 + (c.y-3) * WIDTH;
		pedestals[1] = c.x+3 + (c.y-3) * WIDTH;
		pedestals[2] = c.x+3 + (c.y+3) * WIDTH;
		pedestals[3] = c.x-3 + (c.y+3) * WIDTH;
	}

	private ImpShopRoom impShop;

	private int entranceCell, exitCell;

	@Override
	public void playLevelMusic() {
		if (locked()){
			if (BossHealthBar.bleedingActive()){
				Music.INSTANCE.play(Assets.Music.CITY_BOSS_FINALE, true);
			} else {
				Music.INSTANCE.play(Assets.Music.CITY_BOSS, true);
			}
		//if top door isn't unlocked
		} else if (map[topDoor] == Terrain.LOCKED_DOOR){
			Music.INSTANCE.end();
		} else {
			Music.INSTANCE.playTracks(CityLevel.CITY_TRACK_LIST, CityLevel.CITY_TRACK_CHANCES, false);
		}
	}

	@Override
	public int entrance() {
		int entr = super.entrance();
		return entr == 0 ? entranceCell : entr;
	}

	@Override
	public int exit() {
		int exit = super.exit();
		return exit == 0 ? exitCell : exit;
	}

	private static final String IMP_SHOP = "imp_shop";
	private static final String ENTRANCE_CELL = "entrance_cell";
	private static final String EXIT_CELL = "exit_cell";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(ENTRANCE_CELL, entranceCell);
		bundle.put(EXIT_CELL, exitCell);
		bundle.put( IMP_SHOP, impShop );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		entranceCell = bundle.getInt(ENTRANCE_CELL);
		exitCell = bundle.getInt(EXIT_CELL);
		impShop = (ImpShopRoom) bundle.get( IMP_SHOP );
		if (map[topDoor] != Terrain.LOCKED_DOOR && ImpQuest.completedOnce() && !impShop.shopSpawned()){
			spawnShop();
		}
	}

	@Override
	protected boolean build() {

		setSize(WIDTH, HEIGHT);

		//entrance room
		Painter.fill(this, entry, Terrain.WALL);
		Painter.fill(this, entry, 1, Terrain.BOOKSHELF);
		Painter.fill(this, entry, 2, Terrain.EMPTY);

		Painter.fill(this, entry.left+3, entry.top+3, 1, 5, Terrain.BOOKSHELF);
		Painter.fill(this, entry.right-4, entry.top+3, 1, 5, Terrain.BOOKSHELF);

		Point c = entry.center();

		Painter.fill(this, c.x-1, c.y-2, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x-1, c.y, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x-1, c.y+2, 3, 1, Terrain.STATUE);
		Painter.fill(this, c.x, entry.top+1, 1, 6, Terrain.EMPTY_SP);

		Painter.set(this, c.x, entry.top, Terrain.DOOR);

		entranceCell = c.x + (c.y+2)*width();
		Painter.set(this, entranceCell, Terrain.ENTRANCE);
		addRegularEntrance(entranceCell);

		//DK's throne room
		Painter.fillDiamond(this, arena, 1, Terrain.EMPTY);

		Painter.fill(this, arena, 5, Terrain.EMPTY_SP);
		Painter.fill(this, arena, 6, Terrain.CUSTOM_DECO);

		c = arena.center();
		Painter.set(this, c.x-3, c.y, Terrain.STATUE);
		Painter.set(this, c.x-4, c.y, Terrain.STATUE);
		Painter.set(this, c.x+3, c.y, Terrain.STATUE);
		Painter.set(this, c.x+4, c.y, Terrain.STATUE);

		Painter.set(this, pedestals[0], Terrain.PEDESTAL);
		Painter.set(this, pedestals[1], Terrain.PEDESTAL);
		Painter.set(this, pedestals[2], Terrain.PEDESTAL);
		Painter.set(this, pedestals[3], Terrain.PEDESTAL);

		Painter.set(this, c.x, arena.top, Terrain.LOCKED_DOOR);

		//exit hallway
		Painter.fill(this, end, Terrain.CHASM);
		Painter.fill(this, end.left+4, end.top+5, 7, 18, Terrain.EMPTY);
		Painter.fill(this, end.left+4, end.top+5, 7, 4, Terrain.EXIT);

		exitCell = end.left+7 + (end.top+8)*width();
		LevelTransition exit = addRegularExit(exitCell);
		if (exit != null) {
			exit.set(end.left + 4, end.top + 4, end.left + 4 + 6, end.top + 4 + 4);
		}

		impShop = new ImpShopRoom();
		impShop.set(end.left+3, end.top+12, end.left+11, end.top+20);
		Painter.set(this, impShop.center(), Terrain.PEDESTAL);

		Painter.set(this, impShop.left+2, impShop.top, Terrain.STATUE);
		Painter.set(this, impShop.left+6, impShop.top, Terrain.STATUE);

		Painter.fill(this, end.left+5, end.bottom+1, 5, 1, Terrain.EMPTY);
		Painter.fill(this, end.left+6, end.bottom+2, 3, 1, Terrain.EMPTY);

		impShop.paint(this);
		new CityPainter().paint(this, null);

		//pillars last, no deco on these
		Painter.fill(this, end.left+1, end.top+2, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+7, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+12, 2, 2, Terrain.WALL);
		Painter.fill(this, end.left+1, end.top+17, 2, 2, Terrain.WALL);

		Painter.fill(this, end.right-3, end.top+2, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+7, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+12, 2, 2, Terrain.WALL);
		Painter.fill(this, end.right-3, end.top+17, 2, 2, Terrain.WALL);

		CustomTilemap customVisuals = new CustomGroundVisuals();
		customVisuals.setRect(0, 0, width(), height());
		customTiles.add(customVisuals);

		customVisuals = new CustomWallVisuals();
		customVisuals.setRect(0, 0, width(), height());
		customWalls.add(customVisuals);

		return true;
	}

	//returns a random pedestal that doesn't already have a summon inbound on it
	public int getSummoningPos(){
		Mob king = getKing();
		HashSet<DwarfKing.Summoning> summons = king.buffs(DwarfKing.Summoning.class);
		ArrayList<Integer> positions = new ArrayList<>();
		for (int pedestal : pedestals) {
			boolean clear = true;
			for (DwarfKing.Summoning s : summons) {
				if (s.getPos() == pedestal) {
					clear = false;
					break;
				}
			}
			if (clear) {
				positions.add(pedestal);
			}
		}
		if (positions.isEmpty()){
			return -1;
		} else {
			return Random.element(positions);
		}
	}

	private Mob getKing(){
		for (Mob m : mobs){
			if (m instanceof DwarfKing) return m;
		}
		return null;
	}

	@Override
	protected void createMobs() {
	}

	@Override
	public Actor addRespawner() {
		return null;
	}

	@Override
	protected void createItems() {
		Random.pushGenerator(Random.Long());
			ArrayList<Item> bonesItems = Bones.get();
			if (bonesItems != null) {
				int pos;
				do {
					pos = randomRespawnCell(null);
				} while (pos == entrance());
				for (Item i : bonesItems) {
					drop(i, pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
				}
			}
		Random.popGenerator();
	}

	@Override
	public int randomRespawnCell( Char ch ) {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int i : PathFinder.NEIGHBOURS8){
			int cell = entrance() + i;
			if (isPassable(cell, ch)
					&& Actor.findChar(cell) == null
					&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])){
				candidates.add(cell);
			}
		}

		if (candidates.isEmpty()){
			return -1;
		} else {
			return Random.element(candidates);
		}
	}

	@Override
	public boolean invalidHeroPos(int tile) {
		//hero cannot be above top door if it is locked
		if (map[topDoor] == Terrain.LOCKED_DOOR && tile <= topDoor){
			return true;
		}
		return super.invalidHeroPos(tile);
	}

	@Override
	public void occupyCell( Char ch ) {
		if (map[bottomDoor] != Terrain.LOCKED_DOOR && map[topDoor] == Terrain.LOCKED_DOOR
				&& ch.pos < bottomDoor && ch == Dungeon.hero) {
			seal();
		}

		super.occupyCell( ch );
	}

	@Override
	public void seal() {
		super.seal();

		//moves intelligent allies with the hero, preferring closer pos to entrance door
		int doorPos = pointToCell(new Point(arena.left + arena.width()/2, arena.bottom));
		Mob.holdAllies(this, doorPos);
		Mob.restoreAllies(this, Dungeon.hero.pos, doorPos);

		DwarfKing boss = new DwarfKing();
		boss.setLevel(Dungeon.depth);
		boss.state = boss.WANDERING;
		boss.pos = pointToCell(arena.center());
		GameScene.add( boss );
		boss.beckon(Dungeon.hero.pos);

		if (heroFOV[boss.pos]) {
			boss.notice();
			boss.sprite.alpha( 0 );
			boss.sprite.parent.add( new AlphaTweener( boss.sprite, 1, 0.1f ) );
		}

		set( bottomDoor, Terrain.LOCKED_DOOR );
		GameScene.updateMap( bottomDoor );
		Dungeon.observe();

		Game.runOnRenderThread(new Callback() {
			@Override
			public void call() {
				Music.INSTANCE.play(Assets.Music.CITY_BOSS, true);
			}
		});
	}

	@Override
	public void unseal() {
		super.unseal();

		if (!locked()) {
			set(bottomDoor, Terrain.DOOR);
			GameScene.updateMap(bottomDoor);

			set(topDoor, Terrain.DOOR);
			GameScene.updateMap(topDoor);

			if (ImpQuest.completedOnce()) {
				spawnShop();
			}
			Dungeon.observe();

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Music.INSTANCE.fadeOut(5f, new Callback() {
						@Override
						public void call() {
							Music.INSTANCE.end();
						}
					});
				}
			});
		}
	}

	private void spawnShop(){
		while (impShop.spacesNeeded() >= 7*(impShop.height()-2)){
			impShop.bottom++;
		}
		impShop.spawnShop(this);
	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(CityLevel.class, "water_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(CityLevel.class, "high_grass_name");
			default:
				return super.tileName( tile );
		}
	}

	@Override
	public String tileDesc(int tile, int cell) {
		switch (tile) {
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(CityLevel.class, "entrance_desc") + appendNoTransWarning(cell);
			case Terrain.EXIT:
				return Messages.get(CityLevel.class, "exit_desc") + appendNoTransWarning(cell);
			case Terrain.WALL_DECO:
			case Terrain.EMPTY_DECO:
				return Messages.get(CityLevel.class, "deco_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(CityLevel.class, "sp_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(CityLevel.class, "statue_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(CityLevel.class, "bookshelf_desc");
			default:
				return super.tileDesc( tile, cell );
		}
	}

	public static class CustomGroundVisuals extends CustomTilemap implements CustomTilemap.BossLevelVisuals {

		{
			texture = Assets.Environment.CITY_BOSS;
			tileW = 15;
			tileH = 48;
		}

		private static final int STAIR_ROWS = 7;

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			updateState();
			return v;
		}

		@Override
		public void updateState() {
			if (vis != null) {
				int[] data = new int[tileW*tileH];

				int[] map = Dungeon.level.map;

				int stairsTop = -1;
				int stairsWidth = 0;

				int indexStitchingWithCityTiles = -1;

				//upper part of the level, mostly demon halls tiles
				for (int i = tileW; i < tileW*Math.min(22, tileH); i++){

					if (indexStitchingWithCityTiles >= 0)
						indexStitchingWithCityTiles = (indexStitchingWithCityTiles + 1) % 7;

					if (map[i] == Terrain.EXIT && stairsTop == -1){
						stairsTop = i;
					}
                    if (stairsWidth <= 0 && stairsTop != -1) {
                        if (map[i] == Terrain.EXIT && i / tileW == (i + 1) / tileW) stairsWidth--;
                        else stairsWidth = -stairsWidth;
                    }

					//pillars
					if (map[i] == Terrain.WALL && map[i-tileW] == Terrain.CHASM){
						data[i] = 13*8 + 6;
						if (++i < data.length) data[i] = 13*8 + 7;
					} else if (map[i] == Terrain.WALL && map[i-tileW] == Terrain.WALL){
						data[i] = 14*8 + 6;
						if (++i < data.length) data[i] = 14*8 + 7;
					} else if (i > tileW && map[i] == Terrain.CHASM && map[i-tileW] == Terrain.WALL) {
						data[i] = 15*8 + 6;
						if (++i < data.length) data[i] = 15*8 + 7;

						//imp's pedestal
					} else if (map[i] == Terrain.PEDESTAL) {
						data[i] = 12*8 + 5;

						//skull piles
					} else if (map[i] == Terrain.STATUE) {
						data[i] = 15*8 + 5;

						//ground tiles
					} else if (map[i] == Terrain.EMPTY || map[i] == Terrain.EMPTY_DECO
							|| map[i] == Terrain.EMBERS || map[i] == Terrain.GRASS
							|| map[i] == Terrain.HIGH_GRASS || map[i] == Terrain.FURROWED_GRASS){

						//final ground stiching with city tiles
						if (i/tileW == 21){
							if(indexStitchingWithCityTiles == -1) indexStitchingWithCityTiles = 0;
							data[i] = 11*8 + indexStitchingWithCityTiles;
						} else {

							//regular ground tiles
							if (map[i - 1] == Terrain.CHASM) {
								data[i] = 12 * 8 + 1;
							} else if (i + 1 >= map.length || map[i + 1] == Terrain.CHASM) {
								data[i] = 12 * 8 + 3;
							} else if (map[i] == Terrain.EMPTY_DECO) {
								data[i] = 12 * 8 + 4;
							} else {
								data[i] = 12 * 8 + 2;
							}
						}

						//otherwise no tile here
					} else {
						data[i] = -1;
					}
				}

				//custom for stairs
				for (int i = 0; i < STAIR_ROWS; i++){
					for (int j = 0; j < stairsWidth; j++){
						if (stairsTop + j < data.length) data[stairsTop+j] = (i+4)*8 + j;
					}
					stairsTop += tileW;
				}

				//lower part: statues, pedestals, and carpets
				for (int i = tileW*Math.min(22, tileH); i < tileW * tileH; i++){

					//pedestal spawners
					if (map[i] == Terrain.PEDESTAL){
						data[i] = 13*8 + 4;

						//statues that should face left instead of right
					} else if (map[i] == Terrain.STATUE && i%tileW > tileW/2) {
						data[i] = 15 * 8 + 4;

						//carpet tiles
					} else if (map[i] == Terrain.EMPTY_SP) {
						if (i + 1 < map.length) {
							//top row of DK's throne
							if (map[i + 1] == Terrain.EMPTY_SP && i + tileW < map.length && map[i + tileW] == Terrain.EMPTY_SP) {
								data[i] = 13 * 8 + 1;
								data[++i] = 13 * 8 + 2;
								if (++i < data.length) data[i] = 13 * 8 + 3;

								//mid row of DK's throne
							} else if (map[i + 1] == Terrain.CUSTOM_DECO) {
								data[i] = 14 * 8 + 1;
								data[++i] = 14 * 8 + 2;
								if (++i < data.length) data[i] = 14 * 8 + 3;

								//bottom row of DK's throne
							} else if (map[i + 1] == Terrain.EMPTY_SP && map[i - tileW] == Terrain.EMPTY_SP) {
								data[i] = 15 * 8 + 1;
								data[++i] = 15 * 8 + 2;
								if (++i < data.length) data[i] = 15 * 8 + 3;
							}
							//otherwise entrance carpet
							else if (map[i-tileW] != Terrain.EMPTY_SP){
								data[i] = 13*8 + 0;
							} else if (i+tileW < map.length && map[i+tileW] != Terrain.EMPTY_SP){
								data[i] = 15*8 + 0;
							} else {
								data[i] = 14*8 + 0;
							}

						}
						//otherwise entrance carpet
						else if (map[i-tileW] != Terrain.EMPTY_SP){
							data[i] = 13*8 + 0;
						} else if (i+tileW < map.length && map[i+tileW] != Terrain.EMPTY_SP){
							data[i] = 15*8 + 0;
						} else {
							data[i] = 14*8 + 0;
						}

						//otherwise no tile here
					} else {
						data[i] = -1;
					}
				}

				vis.map( data, tileW );
			}
		}

		@Override
		public String name(int tileX, int tileY) {
			int cell = (this.tileX + tileX) + (this.tileY + tileY)*tileW;

			//demon halls tiles
			if (cell < Dungeon.level.width*22){
				if (Dungeon.level.map[cell] == Terrain.STATUE){
					return Messages.get(HallsLevel.class, "statue_name");
				}

				//DK arena tiles
			} else {
				if (Dungeon.level.map[cell] == Terrain.CUSTOM_DECO){
					return Messages.get(CityBossLevel.class, "throne_name");
				} else if (Dungeon.level.map[cell] == Terrain.PEDESTAL){
					return Messages.get(CityBossLevel.class, "summoning_name");
				}
			}

			return super.name(tileX, tileY);
		}

		@Override
		public String desc(int tileX, int tileY) {
			int cell = (this.tileX + tileX) + (this.tileY + tileY)*tileW;

			//demon halls tiles
			if (cell < Dungeon.level.width*22){
				if (Dungeon.level.map[cell] == Terrain.EXIT){
					return Messages.get(HallsLevel.class, "exit_desc");
				} else if (Dungeon.level.map[cell] == Terrain.STATUE){
					return Messages.get(HallsLevel.class, "statue_desc");
				} else if (Dungeon.level.map[cell] == Terrain.EMPTY_DECO){
					return "";
				}

			//DK arena tiles
			} else {
				if (Dungeon.level.map[cell] == Terrain.CUSTOM_DECO){
					return Messages.get(CityBossLevel.class, "throne_desc");
				} else if (Dungeon.level.map[cell] == Terrain.PEDESTAL){
					return Messages.get(CityBossLevel.class, "summoning_desc");
				}
			}

			return super.desc(tileX, tileY);
		}
	}

	public static class CustomWallVisuals extends CustomTilemap implements CustomTilemap.BossLevelVisuals {
		{
			texture = Assets.Environment.CITY_BOSS;
			tileW = 15;
			tileH = 48;

			wallVisual = true;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			updateState();
			return v;
		}

		@Override
		public void updateState() {
			if (vis != null) {
				int[] data = new int[tileW * tileH];
				Arrays.fill(data, -1);

				int[] map = Dungeon.level.map;

				int shadowTop = -1;
				int shadowWidth = 0;
                int cutShadow = 0;

				//upper part of the level, mostly demon halls tiles
				int length = Math.min(tileW * 21, map.length);
				for (int i = tileW; i < length; i++) {

					if (map[i] == Terrain.EXIT && shadowTop == -1) {
                        cutShadow = Math.max(0, 4 - i / tileW);
						shadowTop = i - tileW * (4-cutShadow);
					}
                    if (shadowWidth <= 0 && shadowTop != -1) {
                        if (map[i] == Terrain.EXIT && i / tileW == (i + 1) / tileW) shadowWidth--;
                        else shadowWidth = -shadowWidth;
                    }

					//pillars
					if (i + tileW < map.length && map[i] == Terrain.CHASM && map[i + tileW] == Terrain.WALL) {
						data[i] = 12 * 8 + 6;
						data[++i] = 12 * 8 + 7;
					} else if (map[i] == Terrain.WALL && map[i - tileW] == Terrain.CHASM) {
						data[i] = 13 * 8 + 6;
						data[++i] = 13 * 8 + 7;

					//skull tops
					} else if (i + tileW < map.length && map[i + tileW] == Terrain.STATUE) {
						data[i] = 14 * 8 + 5;

					//otherwise no tile here
					} else {
						data[i] = -1;
					}
				}

				//custom shadow  for stairs
				if (shadowTop != -1) {
					for (int i = cutShadow; i < 8; i++) {
						if (i < 4 - cutShadow) {
							data[shadowTop] = i * 8 + 0;
							for (int j = 1; j < shadowWidth - 1; j++) {
								if (shadowTop + j < data.length) data[shadowTop + j] = i * 8 + 1;
							}
							if (shadowTop + shadowWidth < data.length) data[shadowTop + shadowWidth] = i * 8 + 2;
						} else {
							int j = i - 4;
							data[shadowTop] = j * 8 + 3;
							for (int k = 1; k < shadowWidth - 1; k++) {
								if (shadowTop + k < data.length) data[shadowTop + k] = j * 8 + 4;
							}
							if (shadowTop + shadowWidth < data.length) data[shadowTop + shadowWidth] = j * 8 + 5;
						}

						shadowTop += tileW;
						if (shadowTop >= data.length) break;
					}
				}

				//lower part. Statues and DK's throne
				for (int i = tileW * 21; i < tileW * tileH; i++) {

					//Statues that need to face left instead of right
					if (map[i] == Terrain.STATUE && i % tileW > 7) {
						data[i - tileW] = 14 * 8 + 4;
					} else if (map[i] == Terrain.CUSTOM_DECO) {
						data[i - tileW] = 13 * 8 + 5;
					}

					//always no tile here (as the above statements are modifying previous tiles)
					data[i] = -1;
				}

				vis.map(data, tileW);
			}
		}
	}

	public static class KingsThrone extends CustomTilemap {

		{
			texture = Assets.Environment.CITY_BOSS;

			terrain = Terrain.EMPTY_SP;

			tileW = tileH = 3;
			offsetCenterX = offsetCenterY = 1;
		}

		@Override
		public Tilemap create() {
			Tilemap v = super.create();
			int[] data = new int[tileW*tileH];
			for (int i = 0; i < data.length; i++) {
				data[i] = (13 + i/3) * 8 + 1 + i%3;
			}
			v.map( data, tileW );
			return v;
		}

		@Override
		public String name(int tileX, int tileY) {
			return Messages.get(CityBossLevel.class, "throne_name");
		}

		@Override
		public String desc(int tileX, int tileY) {
			return Messages.get(CityBossLevel.class, "throne_desc");
		}

	}
}
