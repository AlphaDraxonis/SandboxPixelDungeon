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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.BlobStoreMap;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.HeroMob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.editor.ArrowCell;
import com.shatteredpixel.shatteredpixeldungeon.editor.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.CoinDoor;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.annotations.KeepProguard;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.WandmakerQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfYendor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.*;

public abstract class Level implements Bundlable, Copyable<Level> {

	public static final String SURFACE = "surface", NONE = "none", ANY = "any" + (char) 30 + (char) 31;

	public static enum Feeling {
		NONE,
		CHASM,
		WATER,
		GRASS,
		DARK,
		LARGE,
		TRAPS,
		SECRETS;

		public String title(){
			return Messages.get(this, name()+"_title");
		}

		public String desc() {
			return Messages.get(this, name()+"_desc");
		}
	}

	protected int width;
	protected int height;
	protected int length;

	public String name;

	protected static final float TIME_TO_RESPAWN	= 50;

	public int version;
	
	public int[] map;
	public int[] visualMap;
	public int[] visualRegions;
	public boolean[] visited;
	public boolean[] mapped;
	public boolean[] discoverable;
	
	public byte[] tileVariance;

	public int viewDistance = Dungeon.isChallenged( Challenges.DARKNESS ) ? 2 : 8, originalViewDistance;
	
	public boolean[] heroFOV;
	
	private boolean[] passable;
	public boolean[] losBlocking;
	private boolean[] flamable;
	public boolean[] secret;
	public boolean[] solid;
	public boolean[] avoid;
	public boolean[] water;
	public boolean[] pit;

	public boolean[] openSpace;

	private boolean[] passableHero, passableMob, passableAlly;

	/**
	 * <b>IMPORTANT: keep keys synchronised with levelScheme.zones!!!</b>
	 */
	public final Map<String, Zone> zoneMap = new HashMap<>(3);
	public Zone[] zone;

	public Feeling feeling = Feeling.NONE;

	public Map<Integer, LevelTransition> transitions;

	//when a boss level has become locked.
	public int lockedCount = 0;//  <= 0 -> not locked
	protected final LinkedList<String> musicRequests = new LinkedList<>();//bosses can add their music request in notice(), and remove it in die(), always the last element is played
	private final LinkedList<Integer> musicRequestsMobIDs = new LinkedList<>();//keeps track which mobs have changed the music

	public HashSet<Mob> mobs;
	public SparseArray<Heap> heaps;
	public BlobStoreMap blobs;
	public SparseArray<CustomParticle> particles;//keep in mind: key is identifier, not position on level
	public SparseArray<Plant> plants;
	public SparseArray<Trap> traps;
	public SparseArray<Sign> signs;
	public SparseArray<Barrier> barriers;
	public SparseArray<ArrowCell> arrowCells;
	public SparseArray<Checkpoint> checkpoints;
	public HashSet<CustomTilemap> customTiles;
	public HashSet<CustomTilemap> customWalls;
	public SparseArray<CoinDoor> coinDoors;

	protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

	protected Group visuals;
	protected Group wallVisuals;

	public int color1 = 0x004400;
	public int color2 = 0x88CC44;

	public LevelScheme levelScheme;

	private boolean initForPlayCalled;

	public static final int NO_BOSS_MOB = -2;//need to be -2 bc -1 is pos of mobs in inv
	public int bossmobAt = -2;//store as pos so we don't have problems with undo, only for CustomLevel
	public String bossmobMusic = null;//boss music of bossMob
	public Mob bossMob;//after initForPlay

	//For loading
	public static Mob bossMobStatic;

	private static final String VERSION     = "version";
	private static final String WIDTH       = "width";
	private static final String HEIGHT      = "height";
	private static final String NAME        = "name";
	private static final String MAP			= "map";
	private static final String TILE_VARIANCE= "tile_variance";
	private static final String VISITED		= "visited";
	private static final String MAPPED		= "mapped";
	private static final String TRANSITIONS	= "transitions";
	private static final String LOCKED_COUNT= "locked_count";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String TRAPS       = "traps";
	private static final String SIGNS       = "signs";
	private static final String BARRIERS    = "barriers";
	private static final String ARROW_CELLS = "arrow_cells";
	private static final String CHECKPOINTS = "checkpoints";
	private static final String CUSTOM_TILES= "customTiles";
	private static final String CUSTOM_WALLS= "customWalls";
	private static final String COIN_DOORS = "coin_door";
	private static final String MOBS		= "mobs";
	private static final String BLOBS		= "blobs";
	private static final String PARTICLES	= "particles";
	private static final String FEELING		= "feeling";
	private static final String VIEW_DISTANCE = "view_distance";
	private static final String ORIGINAL_VIEW_DISTANCE = "original_view_distance";
	private static final String BOSS_MOB_MUSIC = "boss_mob_music";
	private static final String BOSS_MOB_AT = "boss_mob_at";
	private static final String ZONES       = "zones";
	private static final String MUSIC_REQUESTS = "full_music_requests";
	private static final String MUSIC_REQUESTS_MOB_IDS = "music_requests_mob_ids";
	private static final String INIT_FOR_PLAY_CALLED = "init_for_play_called";


	public final void setLevelScheme(LevelScheme levelScheme) {
		this.levelScheme = levelScheme;
		name = levelScheme.getName();
		initRegionColors();
	}

	public void create() {

        Random.pushGenerator(Dungeon.seedForLevel(name, this instanceof MiningLevel ? Dungeon.branch + ((MiningLevel)this).questId : 0 ));

		if (!Dungeon.bossLevel() && Dungeon.branch == 0) {

			if (levelScheme.spawnItems) {

				for (Item item : levelScheme.prizeItemsToSpawn) {
					addItemToSpawn(item);
				}

				addItemToSpawn(Generator.random(Generator.Category.FOOD));

				if (Dungeon.posNeeded()) {
					Dungeon.LimitedDrops.STRENGTH_POTIONS.count++;
					addItemToSpawn(new PotionOfStrength());
				}
				if (Dungeon.souNeeded()) {
					Dungeon.LimitedDrops.UPGRADE_SCROLLS.count++;
					//every 2nd scroll of upgrade is removed with forbidden runes challenge on
					//TODO while this does significantly reduce this challenge's levelgen impact, it doesn't quite remove it
					//for 0 levelgen impact, we need to do something like give the player all SOU, but nerf them
					//or give a random scroll (from a separate RNG) instead of every 2nd SOU
					if (!Dungeon.isChallenged(Challenges.NO_SCROLLS) || Dungeon.LimitedDrops.UPGRADE_SCROLLS.count % 2 != 0) {
						addItemToSpawn(new ScrollOfUpgrade());
					}
				}
				if (Dungeon.asNeeded()) {
					Dungeon.LimitedDrops.ARCANE_STYLI.count++;
					addItemToSpawn(new Stylus());
				}
				if (Dungeon.enchStoneNeeded()) {
					Dungeon.LimitedDrops.ENCH_STONE.drop();
					addItemToSpawn(new StoneOfEnchantment());
				}
				if (Dungeon.intStoneNeeded()) {
					Dungeon.LimitedDrops.INT_STONE.drop();
					addItemToSpawn(new StoneOfIntuition());
				}
				if (Dungeon.trinketCataNeeded()) {
					Dungeon.LimitedDrops.TRINKET_CATA.drop();
					addItemToSpawn(new TrinketCatalyst());
				}
			}
			
			if (Dungeon.depth > 1 && feeling == null) {
				//50% chance of getting a level feeling
				//~7.15% chance for each feeling
				switch (Random.Int( 14 )) {
					case 0:
						feeling = Feeling.CHASM;
						break;
					case 1:
						feeling = Feeling.WATER;
						break;
					case 2:
						feeling = Feeling.GRASS;
						break;
					case 3:
						feeling = Feeling.DARK;
						viewDistance = Math.round(5*viewDistance/8f);
						break;
					case 4:
						feeling = Feeling.LARGE;
						break;
					case 5:
						feeling = Feeling.TRAPS;
						break;
					case 6:
						feeling = Feeling.SECRETS;
						break;
					default:
						float mossyClumpChance = MossyClump.overrideNormalLevelChance();
						float trapMechanismChance = TrapMechanism.overrideNormalLevelChance();
						float largest = Math.max(mossyClumpChance, trapMechanismChance);
						if (Random.Float() < largest){
							if (mossyClumpChance == largest)
								feeling = MossyClump.getNextFeeling();
							else if (trapMechanismChance == largest)
								feeling = TrapMechanism.getNextFeeling();
							else
								feeling = Feeling.NONE;
						} else {
							feeling = Feeling.NONE;
						}
				}
			}
			if (feeling == Feeling.DARK) {
				if (!(this instanceof CustomLevel)) {
					if (levelScheme.spawnItems) addItemToSpawn(new Torch());
					viewDistance = Math.round(viewDistance / 2f);
				}
			} else if (feeling == Feeling.LARGE && levelScheme.spawnItems) {
					if (!(this instanceof CustomLevel)) {
						addItemToSpawn(Generator.random(Generator.Category.FOOD));
					}
				}
			if (feeling == null) feeling = Feeling.NONE;//this also includes default case

		}
		
        do {
			width = height = length = 0;

			transitions = new HashMap<>();

			mobs = new HashSet<>();
			heaps = new SparseArray<>();
			blobs = new BlobStoreMap();
			particles = new SparseArray<>();
			plants = new SparseArray<>();
			traps = new SparseArray<>();
			signs = new SparseArray<>();
			barriers = new SparseArray<>();
			arrowCells = new SparseArray<>();
			checkpoints = new SparseArray<>();
			customTiles = new HashSet<>();
			customWalls = new HashSet<>();
			coinDoors = new SparseArray<>();

		} while (!build());
		
		buildFlagMaps();
		cleanWalls();
		
		createMobs();
		createItems();

		Random.popGenerator();

		visualMap = map.clone();
		visualRegions = new int[map.length];
	}
	
	public void setSize(int w, int h){
		
		width = w;
		height = h;
		length = w * h;
		
		map = new int[length];
		visualMap = new int[length];
		visualRegions = new int[length];
		Arrays.fill( map, feeling == Level.Feeling.CHASM ? Terrain.CHASM : Terrain.WALL );
		
		visited     = new boolean[length];
		mapped      = new boolean[length];
		
		heroFOV     = new boolean[length];
		
		passable	= new boolean[length];
		losBlocking	= new boolean[length];
		flamable	= new boolean[length];
		secret		= new boolean[length];
		solid		= new boolean[length];
		avoid		= new boolean[length];
		water		= new boolean[length];
		pit			= new boolean[length];

		openSpace   = new boolean[length];

		zone 		= new Zone[length];

		passableHero=new boolean[length];
		passableMob =new boolean[length];
		passableAlly=new boolean[length];

		tileVariance = null;

		PathFinder.setMapSize(w, h);
	}
	
	public void reset() {
		
		for (Mob mob : mobs.toArray( new Mob[0] )) {
			if (!mob.reset()) {
				mobs.remove( mob );
			}
		}
		createMobs();
	}

	public void initForPlay() {
		initForPlayCalled = true;
		originalViewDistance = viewDistance;
		for (Zone z : zoneMap.values()) {
			z.initTransitions(this);
		}
		for (Mob m : mobs) {
//			if (m instanceof MobBasedOnDepth) ((MobBasedOnDepth) m).setLevel(Dungeon.depth);
			if (m.pos == bossmobAt) {
				bossMob = m;
				bossMob.isBossMob = !(m instanceof Goo || m instanceof DwarfKing);
				bossMob.addBossProperty();
			}
		}
		for (Heap h : heaps.valueList()) {
			h.seen = false;
			if (h.type == Heap.Type.FOR_SALE){
				Item i = h.items.getLast();
				if ((i instanceof EquipableItem || i instanceof Wand)) i.identify();
 			}
			for (Item i : h.items) {
				if (i instanceof Scroll || i instanceof Potion || i instanceof Ring)
					i.reset();//important for scroll runes being inited
				if (i instanceof Bomb) {
					if (i.getClass() == Bomb.class && i.quantity() >= 2) i.image = ItemSpriteSheet.DBL_BOMB;
					if (((Bomb) i).igniteOnDrop) {
						if (h.type == Heap.Type.HEAP || h.type == Heap.Type.FOR_SALE && h.items.getLast() != i)
							((Bomb) i).trigger(h.pos);
					}
				}
			}
		}
	}

	public void playLevelMusic(){
		if (Dungeon.hero != null && Zone.getMusic(this, Dungeon.hero.pos) != null) {
			zoneWithPlayedMusic = zone[Dungeon.hero.pos];
			currentMusic = Zone.getMusic(this, Dungeon.hero.pos);
			if (currentMusic.isEmpty()) Music.INSTANCE.end();
			else Music.INSTANCE.play(currentMusic, true);
		}
		else if (musicRequests.isEmpty() && levelScheme.musicFile != null) {
			if (levelScheme.musicFile.isEmpty()) Music.INSTANCE.end();
			else Music.INSTANCE.play(levelScheme.musicFile, true);
		}
		else {
			playLevelMusic(levelScheme.musicRegion == REGION_NONE ? getVisualRegionValue() : levelScheme.musicRegion);
		}
	}

	public static final String[][] SPECIAL_MUSIC = {
			{Assets.Music.SEWERS_TENSE, Assets.Music.SEWERS_BOSS, Assets.Music.SEWERS_BOSS},
			{Assets.Music.PRISON_TENSE, Assets.Music.PRISON_BOSS, Assets.Music.PRISON_BOSS},
			{Assets.Music.CAVES_TENSE, Assets.Music.CAVES_BOSS, Assets.Music.CAVES_BOSS_FINALE},
			{Assets.Music.CITY_TENSE, Assets.Music.CITY_BOSS, Assets.Music.CITY_BOSS_FINALE},
			{Assets.Music.HALLS_TENSE, Assets.Music.HALLS_BOSS, Assets.Music.HALLS_BOSS_FINALE}
	};

	//music variants
	public static final int MUSIC_NORMAL = 0, MUSIC_TENSE = 1, MUSIC_BOSS = 2, MUSIC_BOSS_FINAL = 3;
	protected String currentMusic;
	public void playLevelMusic(int region) {

		if (!musicRequests.isEmpty()) {
			currentMusic = musicRequests.getLast();
			if (currentMusic.isEmpty()) Music.INSTANCE.end();
			else Music.INSTANCE.play(currentMusic, true);
			return;
		}

		if (Statistics.amuletObtained) {
			for (LevelTransition transition : transitions.values()) {
				if (transition.destLevel.equals(Level.SURFACE)) {
					Music.INSTANCE.play(Assets.Music.THEME_FINALE, true);
					return;
				}
			}
		}

		boolean wandmakerQuestActive = WandmakerQuest.active();
		WandmakerQuest.setMusicPlaying(wandmakerQuestActive);
		if (wandmakerQuestActive){
			Music.INSTANCE.play(Assets.Music.PRISON_TENSE, true);
			return;
		}

		switch (region) {
			case REGION_SEWERS:
				SewerLevel.playSewersLevelMusic();
				break;
			case REGION_PRISON:
				PrisonLevel.playPrisonLevelMusic();
				break;
			case REGION_CAVES:
				CavesLevel.playCavesLevelMusic();
				break;
			case REGION_CITY:
				CityLevel.playCityLevelMusic();
				break;
			case REGION_HALLS:
				HallsLevel.playHallsLevelMusic();
				break;

			default:
				//do nothing
				break;
		}
	}

	public void playSpecialMusic(String music, int mobID) {
		int index = musicRequestsMobIDs.indexOf(mobID);
		if (index != -1) {
			musicRequestsMobIDs.remove(index);
			musicRequests.remove(index);
		}

		musicRequests.add(music);
		musicRequestsMobIDs.add(mobID);
		if (!music.equals(currentMusic)) {
			Music.INSTANCE.fadeOut(0.2f, this::playLevelMusic);
		}
	}

	public void stopSpecialMusic(int mobID) {
		int index = musicRequestsMobIDs.indexOf(mobID);
		if (index != -1) {
			musicRequests.remove(index);
			musicRequestsMobIDs.remove(index);
			String nowPlaying = musicRequests.isEmpty() ? null : musicRequests.getLast();
			if (!Objects.equals(currentMusic, nowPlaying)) {
				Music.INSTANCE.fadeOut(0.2f, this::playLevelMusic);
			}
		}
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {

		version = bundle.getInt( VERSION );
		
		//saves from before v1.4.3 are not supported
		if (version < SandboxPixelDungeon.v1_4_3){
			throw new RuntimeException("old save");
		}

		initForPlayCalled = !bundle.contains(INIT_FOR_PLAY_CALLED) || bundle.getBoolean( INIT_FOR_PLAY_CALLED );

		setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT));
		
		mobs = new HashSet<>();
		heaps = new SparseArray<>();
		blobs = new BlobStoreMap();
		particles = new SparseArray<>();
		plants = new SparseArray<>();
		traps = new SparseArray<>();
		signs = new SparseArray<>();
		barriers = new SparseArray<>();
		arrowCells = new SparseArray<>();
		checkpoints = new SparseArray<>();
		customTiles = new HashSet<>();
		customWalls = new HashSet<>();
		coinDoors = new SparseArray<>();

		map		= bundle.getIntArray( MAP );

		if (initForPlayCalled) {
			visited	= bundle.getBooleanArray( VISITED );
			mapped	= bundle.getBooleanArray( MAPPED );
		} else {
			visited = new boolean[map.length];
			mapped = new boolean[map.length];
		}
		name = bundle.getString( NAME );
		viewDistance = bundle.getInt( VIEW_DISTANCE );
		originalViewDistance = bundle.getInt( ORIGINAL_VIEW_DISTANCE );
		if (originalViewDistance == 0) originalViewDistance = viewDistance;

		if (bundle.contains( TILE_VARIANCE ))
			tileVariance = bundle.getByteArray( TILE_VARIANCE );

		if (bundle.contains("music_requests")) {
			int[] intArray = bundle.getIntArray("music_requests");
			int region = LevelScheme.getRegion(getClass());
			if (region > LevelScheme.REGION_NONE) {
				for (int i : intArray)
					musicRequests.add(SPECIAL_MUSIC[region - 1][i - 1]);
			}
		} else {
			String[] stringArray = bundle.getStringArray(MUSIC_REQUESTS);
			if (stringArray != null) musicRequests.addAll(Arrays.asList(stringArray));
		}

		int[] intArray = bundle.getIntArray(MUSIC_REQUESTS_MOB_IDS);
		if (intArray != null) {
			for (int i : intArray)
				musicRequestsMobIDs.add(i);
		}

		if (bundle.contains(BOSS_MOB_MUSIC)) {
			bossmobMusic = bundle.getString(BOSS_MOB_MUSIC);
		}

		if (!bundle.contains(DO_NOT_SET_AS_DUNGEON_FLOOR) || !bundle.getBoolean(DO_NOT_SET_AS_DUNGEON_FLOOR))
			Dungeon.customDungeon.getFloor(name).setLevel(this);

		if (bundle.contains(BOSS_MOB_AT)) {
			bossmobAt = bundle.getInt(BOSS_MOB_AT);
		}

		transitions = new HashMap<>();
		for (Bundlable b : bundle.getCollection( TRANSITIONS )){
			transitions.put(((LevelTransition) b).departCell, (LevelTransition) b);
		}

		lockedCount = bundle.getInt( LOCKED_COUNT );
		if (bundle.getBoolean("locked")) lockedCount++;

		Collection<Bundlable> collection = bundle.getCollection( ZONES );
		for (Bundlable b : collection) {
			Zone zone = (Zone) b;
			zoneMap.put( zone.getName(), zone );
		}
		Zone.setupZoneArray(this);
		
		collection = bundle.getCollection( HEAPS );
		for (Bundlable h : collection) {
			Heap heap = (Heap)h;
			if (!heap.isEmpty())
				heaps.put( heap.pos, heap );
		}
		
		collection = bundle.getCollection( PLANTS );
		for (Bundlable p : collection) {
			Plant plant = (Plant)p;
			plants.put( plant.pos, plant );
		}

		collection = bundle.getCollection( TRAPS );
		for (Bundlable p : collection) {
			Trap trap = (Trap)p;
			traps.put( trap.pos, trap );
		}

		collection = bundle.getCollection(SIGNS);
		for (Bundlable p : collection) {
			Sign sign = (Sign) p;
			signs.put(sign.pos, sign);
		}

		collection = bundle.getCollection(BARRIERS);
		for (Bundlable p : collection) {
			Barrier barrier = (Barrier) p;
			barriers.put(barrier.pos, barrier);
		}

		collection = bundle.getCollection(ARROW_CELLS);
		for (Bundlable p : collection) {
			ArrowCell arrowCell = (ArrowCell) p;
			arrowCells.put(arrowCell.pos, arrowCell);
		}

		collection = bundle.getCollection(CHECKPOINTS);
		for (Bundlable p : collection) {
			Checkpoint cp = (Checkpoint) p;
			checkpoints.put(cp.pos, cp);
		}

		visualMap = map.clone();
		visualRegions = new int[map.length];
		collection = bundle.getCollection( CUSTOM_TILES );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			if (vis instanceof CustomTileLoader.UserCustomTile) {
				if (((CustomTileLoader.UserCustomTile) vis).getIdentifier() != null) {
					if (vis instanceof CustomTileLoader.SimpleCustomTile) {
						int cell = vis.tileX + vis.tileY * width;
						visualMap[cell] = ((CustomTileLoader.SimpleCustomTile) vis).imageTerrain;
						visualRegions[cell] = ((CustomTileLoader.SimpleCustomTile) vis).region;
					}
					customTiles.add(vis);
				}
			} else customTiles.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_WALLS );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			if(!(vis instanceof CustomTileLoader.UserCustomTile) || ((CustomTileLoader.UserCustomTile) vis).getIdentifier() != null)
				customWalls.add(vis);
		}

		collection = bundle.getCollection(COIN_DOORS);
		for (Bundlable c : collection) {
			CoinDoor cost = (CoinDoor) c;
			coinDoors.put(cost.pos, cost);
		}

		collection = bundle.getCollection( MOBS );
		for (Bundlable m : collection) {
			Mob mob = (Mob)m;
			if (mob != null) {
				mobs.add( mob );
				mob.restoreCurrentZone(this);
			}
		}
		bossMob = bossMobStatic;

		collection = bundle.getCollection( BLOBS );
		for (Bundlable b : collection) {
			Blob blob = (Blob)b;
			blobs.put( blob.getClass(), blob );
		}

		collection = bundle.getCollection( PARTICLES );
		for (Bundlable b : collection) {
			CustomParticle p = (CustomParticle) b;
			particles.put(p.particleID, p);
		}

		feeling = bundle.getEnum( FEELING, Feeling.class );

		if (bundle.contains( "mobs_to_spawn" )) {
			mobsToSpawn.clear();
			for (Class<? extends Mob> mob : bundle.getClassArray("mobs_to_spawn")) {
				if (mob != null) mobsToSpawn.add(Reflection.newInstance(mob));
			}
		}

		if (bundle.contains( "respawner" )){
			respawner = (MobSpawner) bundle.get("respawner");
		}

		buildFlagMaps();
		cleanWalls();

	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( VERSION, Game.versionCode );
		bundle.put( WIDTH, width );
		bundle.put( HEIGHT, height );
		bundle.put( INIT_FOR_PLAY_CALLED, initForPlayCalled );
		bundle.put( MAP, map );
		bundle.put( NAME, name );
		if  (tileVariance != null) bundle.put( TILE_VARIANCE, tileVariance );
		if (initForPlayCalled) {
			bundle.put( VISITED, visited );
			bundle.put( MAPPED, mapped );
		}
		bundle.put( TRANSITIONS, transitions.values() );
		bundle.put( LOCKED_COUNT, lockedCount );
		bundle.put( HEAPS, heaps.valueList() );
		bundle.put( PLANTS, plants.valueList() );
		bundle.put( TRAPS, traps.valueList() );
		bundle.put( SIGNS, signs.valueList() );
		bundle.put( BARRIERS, barriers.valueList() );
		bundle.put( ARROW_CELLS, arrowCells.valueList() );
		bundle.put( CHECKPOINTS, checkpoints.valueList() );
		bundle.put( CUSTOM_TILES, customTiles );
		bundle.put( CUSTOM_WALLS, customWalls );
		bundle.put( COIN_DOORS, coinDoors.valueList() );
		bundle.put( MOBS, mobs );
		bundle.put( BLOBS, blobs.values() );
		bundle.put( PARTICLES, particles.valueList() );
		bundle.put( ZONES, zoneMap.values() );
		bundle.put( FEELING, feeling );
		bundle.put( BOSS_MOB_AT, bossmobAt );
		bundle.put( "mobs_to_spawn", mobsToSpawn.toArray(new Class[0]));
		bundle.put( "respawner", respawner );
		bundle.put( VIEW_DISTANCE, viewDistance );
		bundle.put( ORIGINAL_VIEW_DISTANCE, originalViewDistance );

		bundle.put(MUSIC_REQUESTS, musicRequests.toArray(EditorUtilities.EMPTY_STRING_ARRAY));
		int[] intArray = new int[musicRequestsMobIDs.size()];
		int index = 0;
		for (int i : musicRequestsMobIDs) {
			intArray[index] = i;
			index++;
		}
		bundle.put(MUSIC_REQUESTS_MOB_IDS, intArray);
		if (bossmobMusic != null) bundle.put(BOSS_MOB_MUSIC, bossmobMusic);
	}
	
	private static final String DO_NOT_SET_AS_DUNGEON_FLOOR = "do_not_set_as_dungeon_floor";
	@Override
	public Level getCopy() {
		Bundle bundle = new Bundle();
		bundle.put("LEVEL",this);
		bundle.getBundle("LEVEL").put(DO_NOT_SET_AS_DUNGEON_FLOOR, true);
		return (Level) bundle.get("LEVEL");
	}
	
	public int tunnelTile() {
		return feeling == Feeling.CHASM ? Terrain.EMPTY_SP : Terrain.EMPTY;
	}

	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public int length() {
		return length;
	}

	public int getRegionValue() {
		return levelScheme.getRegion();
	}

	public int getVisualRegionValue() {
		return levelScheme.getVisualRegion();
	}

	public final void initRegionColors() {
		switch (levelScheme.getVisualRegion()) {
			//region colors and music are hardcoded in their region level...; not gonna create a new level just to read the color values
			case REGION_PRISON:
				color1 = 0x6a723d;
				color2 = 0x88924c;
				break;
			case REGION_CAVES:
				color1 = 0x534f3e;
				color2 = 0xb9d661;
				break;
			case REGION_CITY:
				color1 = 0x4b6636;
				color2 = 0xf2f2f2;
				break;
			case REGION_HALLS:
				color1 = 0x801500;
				color2 = 0xa68521;
				break;

			default:
				color1 = 0x48763c;
				color2 = 0x59994a;
				break;
		}
	}

	public String tilesTex() {
		return levelScheme.customTilesTex != null
				? tilesTex(levelScheme)
				: tilesTex(getVisualRegionValue(), false);
	}

	public String waterTex() {
		return levelScheme.customWaterTex != null
				? tilesTex(levelScheme)
				: tilesTex(levelScheme.waterTexture == REGION_NONE ? getVisualRegionValue() : levelScheme.waterTexture, true);
	}
	
	public static String tilesTex(LevelScheme levelScheme) {
		return levelScheme.customTilesTex != null
				? TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getExternalFilePath(levelScheme.customTilesTex)
				: tilesTex(levelScheme.getVisualRegion(), false);
	}
	
	public static String waterTex(LevelScheme levelScheme) {
		return levelScheme.customWaterTex != null
				? TextureCache.EXTERNAL_ASSET_PREFIX + CustomDungeonSaves.getExternalFilePath(levelScheme.customWaterTex)
				: tilesTex(levelScheme.getVisualRegion(), true);
	}

	public static String tilesTex(int region, boolean water) {
		if (water) {
			switch (region) {
				case REGION_PRISON:
					return Assets.Environment.WATER_PRISON;
				case REGION_CAVES:
					return Assets.Environment.WATER_CAVES;
				case REGION_CITY:
					return Assets.Environment.WATER_CITY;
				case REGION_HALLS:
					return Assets.Environment.WATER_HALLS;

				default:
					return Assets.Environment.WATER_SEWERS;
			}
		}

		switch (region) {
			case REGION_PRISON:
				return Assets.Environment.TILES_PRISON;
			case REGION_CAVES:
				return Assets.Environment.TILES_CAVES;
			case REGION_CITY:
				return Assets.Environment.TILES_CITY;
			case REGION_HALLS:
				return Assets.Environment.TILES_HALLS;

			default:
				return Assets.Environment.TILES_SEWERS;
		}
	}
	
	abstract protected boolean build();
	
	private List<Mob> mobsToSpawn = new ArrayList<>();
	
	public Mob createMob() {
		return MobSpawner.createMob(mobsToSpawn, this::getMobRotation);
	}

	public List<? extends Mob> getMobRotation() {
		return MobSpawner.getMobRotation(Dungeon.getSimulatedDepth());
	}

	abstract protected void createMobs();

	abstract protected void createItems();

	public int entrance(){
		LevelTransition l = getTransition(null);
		if (l != null){
			return l.cell();
		}
		return 0;
	}

	public int exit(){
		LevelTransition l = getTransition(LevelTransition.Type.REGULAR_EXIT);
		if (l != null){
			return l.cell();
		}
		return 0;
	}

    public LevelTransition getTransition(int destCell) {
        LevelTransition t = transitions.get(destCell);
        if (t == null && Dungeon.level != null) {
            for (LevelTransition transition : transitions.values()) {
                if (transition != null && transition.inside(destCell)) {
                    t = transition;
                    break;
                }
            }
        }
        if (t == null && InterlevelScene.curTransition != null)
            t = getTransition(InterlevelScene.curTransition.destType);
        return t;
//        return (type == null && !transitions.isEmpty() ? transitions.get(0) : null);
    }

    //
    public LevelTransition getTransition(LevelTransition.Type type) {
        if (transitions.isEmpty()) {
            return null;
        }
        for (LevelTransition transition : transitions.values()) {
//            if (!destination.equals(transition.destLevel)) continue;
            //if we don't specify a type, prefer to return any entrance
            if (type == null &&
                    (transition.type == LevelTransition.Type.REGULAR_ENTRANCE
							|| transition.type == LevelTransition.Type.BRANCH_ENTRANCE
							|| transition.type == LevelTransition.Type.SURFACE)) {
                return transition;
            } else if (transition.type == type) {
                return transition;
            }
        }
        for (LevelTransition transition : transitions.values()) {
            return transition;
        }
        return null;
    }

	public LevelTransition getTransitionFromSurface() {
		List<LevelTransition> results = new ArrayList<>(5);
		for (LevelTransition transition : transitions.values()) {
			if (transition.type == LevelTransition.Type.SURFACE) {
				results.add(transition);
			}
		}
		if (!results.isEmpty()) {
			Random.pushGenerator(Dungeon.seedCurLevel());
			LevelTransition ret = Random.element(results);
			Random.popGenerator();
			return ret;
		}
		return getTransition(null);
	}
//
//    public LevelTransition getTransition(int cell) {
//        for (LevelTransition transition : transitions) {
//            if (transition.inside(cell)) {
//                return transition;
//            }
//        }
//        return null;
//    }

	//returns true if we immediately transition, false otherwise
	public boolean activateTransition(Hero hero, LevelTransition transition){
		if (lockedCount > 0){
			return false;
		}

		if (transition.destType == LevelTransition.Type.BRANCH_ENTRANCE) {

			if (transition.destBranch == QuestLevels.MINING.ID) transitionEnterBlacksmithMine(hero, transition);

			return false;
		}

		if (transition.type == LevelTransition.Type.SURFACE){
			Object winCondition = hero.belongings.getItem( WandOfYendor.class );
			if (winCondition == null) winCondition = hero.belongings.getItem( Amulet.class );
			if (winCondition == null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndMessage( Messages.get(hero, "leave") ) );
					}
				});
				return false;
			} else {
				Statistics.ascended = true;
				Object finalWinCondition = winCondition;
				Game.switchScene(SurfaceScene.class, new Game.SceneChangeCallback() {
					@Override
					public void beforeCreate() {
					
					}
					
					@Override
					public void afterCreate() {
						Badges.silentValidateHappyEnd();
						Dungeon.win(finalWinCondition);
						Dungeon.deleteGame( GamesInProgress.curSlot );
						Badges.saveGlobal();
					}
				});
				return true;
			}
		}

		if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE
				&& hero.belongings.getItem(Amulet.class) != null
				&& hero.buff(AscensionChallenge.class) == null) {

			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					GameScene.show( new WndOptions( new ItemSprite(ItemSpriteSheet.AMULET),
							Messages.get(Amulet.class, "ascent_title"),
							Messages.get(Amulet.class, "ascent_desc"),
							Messages.get(Amulet.class, "ascent_yes"),
							Messages.get(Amulet.class, "ascent_no")){
						@Override
						protected void onSelect(int index) {
							if (index == 0){
								Buff.affect(hero, AscensionChallenge.class);
								Statistics.highestAscent = Dungeon.depth;
								activateTransition(hero, transition);
							}
						}
					} );
				}
			});
			return false;
		}

		defaultActiveTransitionImpl(hero, transition);
		return true;
	}

	//some buff effects have special logic or are cancelled from the hero before transitioning levels
	public static void beforeTransition(){

		//time freeze effects need to resolve their pressed cells before transitioning
		TimekeepersHourglass.timeFreeze timeFreeze = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
		if (timeFreeze != null) timeFreeze.disarmPresses();
		Swiftthistle.TimeBubble timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
		if (timeBubble != null) timeBubble.disarmPresses();

		//iron stomach and challenge arena do not persist between floors
		Talent.WarriorFoodImmunity foodImmune = Dungeon.hero.buff(Talent.WarriorFoodImmunity.class);
		if (foodImmune != null) foodImmune.detach();
		ScrollOfChallenge.ChallengeArena arena = Dungeon.hero.buff(ScrollOfChallenge.ChallengeArena.class);
		if (arena != null) arena.detach();

		Char ally = Stasis.getStasisAlly();
		if (Char.hasProp(ally, Char.Property.IMMOVABLE)){
			Dungeon.hero.buff(Stasis.StasisBuff.class).act();
			GLog.w(Messages.get(Stasis.StasisBuff.class, "left_behind"));
		}

		//spend the hero's partial turns,  so the hero cannot take partial turns between floors
		Dungeon.hero.spendToWhole();
		for (Actor a : Actor.all()){
			//also adjust any other actors that are now ahead of the hero due to this
			if (a.cooldown() < Dungeon.hero.cooldown()){
				a.spendToWhole();
			}
		}
	}

	protected void defaultActiveTransitionImpl(Hero hero, LevelTransition transition) {
		beforeTransition();
		InterlevelScene.curTransition = transition;
		if (transition.type == LevelTransition.Type.REGULAR_EXIT
				|| transition.type == LevelTransition.Type.BRANCH_EXIT) {
			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		} else {
			InterlevelScene.mode = InterlevelScene.Mode.ASCEND;
		}
		Game.switchScene(InterlevelScene.class);
	}

	private void transitionEnterBlacksmithMine(Hero hero, LevelTransition transition) {
		boolean allOldQuestMineBlocked = true;
		boolean entranceBlocked = true;

		Set<Blacksmith> smiths = new HashSet<>(3);
		Set<Blacksmith> potentialQuestOwner = new HashSet<>(3);

		for (Mob m : mobs) {
			if (m instanceof Blacksmith && ((Blacksmith) m).quest != null) smiths.add((Blacksmith) m);
		}
		for (Blacksmith blacksmith : smiths) {
			BlacksmithQuest quest = blacksmith.quest;
			if (quest.type() > BlacksmithQuest.BLOOD){
				allOldQuestMineBlocked = false;
				if(!quest.started() && quest.given() && !quest.completed()) {
					potentialQuestOwner.add(blacksmith);
				}
			}
			if (quest.given() && !quest.completed()) entranceBlocked = false;
		}

		final Blacksmith questOwner;
		if (!potentialQuestOwner.isEmpty()) {
			//find closest one
			PathFinder.buildDistanceMap(hero.pos, passable, null);
			int minDist = Integer.MAX_VALUE;
			Blacksmith temQuestOwner = null;
			for(Blacksmith smith : potentialQuestOwner){
				if(PathFinder.distance[smith.pos] < minDist){
					minDist = PathFinder.distance[smith.pos];
					temQuestOwner = smith;
				}
			}
			questOwner = temQuestOwner;
		} else questOwner = null;

		Blacksmith smith = null;
		for (Char c : Actor.chars()) {
			if (c instanceof Blacksmith) {
				smith = (Blacksmith) c;
				break;
			}
		}

		if (allOldQuestMineBlocked) {
			GLog.w(Messages.get(Blacksmith.class, "you_need_smith_with_new_quest"));
		} else if (smith == null || entranceBlocked) {
			GLog.w(Messages.get(Blacksmith.class, "entrance_blocked"));
		} else if (questOwner != null) {
			final Pickaxe pick = hero.belongings.getItem(Pickaxe.class);
			Game.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					if (pick == null) {
						GameScene.show(new WndTitledMessage(new BlacksmithSprite(),
								Messages.titleCase(Messages.get(Blacksmith.class, "name")),
								Messages.get(Blacksmith.class, "lost_pick"))
						);
					} else {
						GameScene.show(new WndOptions(new BlacksmithSprite(),
								Messages.titleCase(Messages.get(Blacksmith.class, "name")),
								Messages.get(Blacksmith.class, "quest_start_prompt"),
								Messages.get(Blacksmith.class, "enter_yes"),
								Messages.get(Blacksmith.class, "enter_no")) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									MiningLevel.generateWithThisQuest = questOwner;
									questOwner.quest.actualStart();
									defaultActiveTransitionImpl(hero, transition);
								}
							}
						});
					}

				}
			});
		}
	}

	public final LevelTransition addRegularEntrance(int cell) {
		LevelTransition result = null;
		String dest = Dungeon.customDungeon.getFloor(Dungeon.levelName).getDefaultAbove();
		if (Level.SURFACE.equals(dest)){
			transitions.put(cell, result = new LevelTransition(this, cell, LevelTransition.Type.SURFACE));
		} else {
			LevelScheme destFloor = Dungeon.customDungeon.getFloor(dest);
			if (destFloor != null && !destFloor.exitCells.isEmpty())
				transitions.put(cell, result = new LevelTransition(this, cell, LevelTransition.Type.REGULAR_ENTRANCE));
		}
		return result;
	}

	public final LevelTransition addRegularExit(int cell) {
		LevelTransition result = null;
		String dest = Dungeon.customDungeon.getFloor(Dungeon.levelName).getDefaultBelow();
		if (Level.SURFACE.equals(dest)) {
			transitions.put(cell, result = new LevelTransition(this, cell, LevelTransition.Type.SURFACE));
		} else {
			LevelScheme destFloor = Dungeon.customDungeon.getFloor(dest);
			if (destFloor != null && !destFloor.entranceCells.isEmpty())
				transitions.put(cell, result = new LevelTransition(this, cell, LevelTransition.Type.REGULAR_EXIT));
		}
		return result;
	}

	public void seal(){
		lockedCount++;
		if (lockedCount >= 1) {
			if (Dungeon.hero.buff(LockedFloor.class) == null) {
				Buff.affect(Dungeon.hero, LockedFloor.class);
			}
		}
	}

	public void unseal(){
		lockedCount--;
		if (lockedCount <= 0) {
			lockedCount = 0;
			if (Dungeon.hero.buff(LockedFloor.class) != null){
				Dungeon.hero.buff(LockedFloor.class).detach();
			}
			
			for (CustomTilemap cust : customTiles) {
				if (cust instanceof CavesBossLevel.MetalGate) {
					((CavesBossLevel.MetalGate) cust).open();
				}
			}
		}
	}

	public boolean locked() {
		return lockedCount > 0;
	}

	public ArrayList<Item> getItemsToPreserveFromSealedResurrect(){
		ArrayList<Item> items = new ArrayList<>();
		for (Heap h : heaps.valueList()){
			if (h.type == Heap.Type.HEAP) {
				for (Item i : h.items){
					if (i instanceof Bomb){
						((Bomb) i).fuse = null;
					}
					items.add(i);
				}
			}
		}
		for (Mob m : mobs){
			for (PinCushion b : m.buffs(PinCushion.class)){
				items.addAll(b.getStuckItems());
			}
		}
		for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)){
			if (b.activeLevel().equals(Dungeon.levelName)) items.add(b.cancel());
		}
		return items;
	}

	public Group addVisuals() {
		if (visuals == null || visuals.parent == null){
			visuals = new Group();
		} else {
			visuals.clear();
			visuals.camera = null;
		}
		for (int i=0; i < length(); i++) {
			if (pit[i]) {
				visuals.add( new WindParticle.Wind( i ) );
				if (i >= width() && water[i-width()]) {
					visuals.add( new FlowParticle.Flow( i - width() ) );
				}
			}
		}
		
		SewerLevel  .addSewerVisuals( this, visuals);
		PrisonLevel .addPrisonVisuals(this, visuals);
		CavesLevel  .addCavesVisuals( this, visuals);
		CityLevel   .addCityVisuals(  this, visuals);
		HallsLevel  .addHallsVisuals( this, visuals);
		
		SewerBossLevel.addSewerBossVisuals(this, visuals);
		
		return visuals;
	}

	//for visual effects that should render above wall overhang tiles
	public Group addWallVisuals(){
		if (wallVisuals == null || wallVisuals.parent == null){
			wallVisuals = new Group();
		} else {
			wallVisuals.clear();
			wallVisuals.camera = null;
		}
		return wallVisuals;
	}


	public int mobLimit() {
		return 0;
	}

	public int mobCount(){
		float count = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
			if (mob.alignment == Char.Alignment.ENEMY && !mob.properties().contains(Char.Property.MINIBOSS)) {
				count += mob.spawningWeight();
			}
		}
		return Math.round(count);
	}

	public Mob findMob( int pos ){
		for (Mob mob : mobs){
			if (mob.pos == pos){
				return mob;
			}
		}
		return null;
	}

	private MobSpawner respawner;
	private ZoneRespawner[] zoneRespawner;

	public Actor addRespawner() {
		if (respawner == null){
			respawner = new MobSpawner();
			Actor.addDelayed(respawner, respawnCooldown());
		} else {
			Actor.add(respawner);
			if (respawner.cooldown() > respawnCooldown()){
				respawner.resetCooldown();
			}
		}
		return respawner;
	}

	public Actor[] addZoneRespawner() {
		if (zoneRespawner == null){
			Collection<Zone> zones = new HashSet<>(5);
			for (Zone z : zoneMap.values()) {
				if (z.ownMobRotationEnabled) zones.add(z);
			}
			zoneRespawner = new ZoneRespawner[zones.size()];
			int i = 0;
			for (Zone z : zones) {
				zoneRespawner[i] = new ZoneRespawner(z);
				Actor.addDelayed(zoneRespawner[i], zoneRespawner[i].respawnCooldown());
				i++;
			}
		} else {
			for (ZoneRespawner res : zoneRespawner) {
				Actor.add(res);
				if (res.cooldown() > res.respawnCooldown()) {
					res.resetCooldown();
				}
			}
		}
		return zoneRespawner;
	}

	public static class Respawner extends Actor {
		{
			actPriority = BUFF_PRIO; //as if it were a buff.
		}

		@Override
		protected boolean act() {

			if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {

				if (Dungeon.level.spawnMob(12)){
					spend(respawnCooldown());
				} else {
					//try again in 1 turn
					spend(TICK);
				}

			} else {
				spend(respawnCooldown());
			}

			return true;
		}

		protected void resetCooldown(){
			spend(-cooldown());
			spend(respawnCooldown());
		}

		protected float respawnCooldown() {
			return Dungeon.level.respawnCooldown();
		}
	}

	public static class ZoneRespawner extends Respawner {

		private final Zone zone;

		public ZoneRespawner(Zone zone) {
			this.zone = zone;
		}

		@Override
		protected boolean act() {
			spend(Dungeon.level.mobCount() < Dungeon.level.mobLimit()
					? Dungeon.level.spawnMob(12, zone) ? respawnCooldown() : TICK
					: respawnCooldown());
			return true;
		}

		@Override
		protected float respawnCooldown() {
			return zone.respawnCooldown;
		}
	}

	public float respawnCooldown(){
		float cooldown;
		if (Statistics.amuletObtained){
			if (Dungeon.getSimulatedDepth() == 1){
				//very fast spawns on floor 1! 0/2/4/6/8/10/12, etc.
				cooldown = (Dungeon.level.mobCount()) * (TIME_TO_RESPAWN / 25f);
			} else {
				//respawn time is 5/5/10/15/20/25/25, etc.
				cooldown = Math.round(GameMath.gate( TIME_TO_RESPAWN/10f, Dungeon.level.mobCount() * (TIME_TO_RESPAWN / 10f), TIME_TO_RESPAWN / 2f));
			}
		} else if (Dungeon.level.feeling == Feeling.DARK){
			cooldown = 2*TIME_TO_RESPAWN/3f;
		} else {
			cooldown = TIME_TO_RESPAWN;
		}
		return cooldown / DimensionalSundial.spawnMultiplierAtCurrentTime();
	}

	public boolean spawnMob(int disLimit){
		return spawnMob(disLimit, null);
	}

	public boolean spawnMob(int disLimit, Zone zone){

		Mob mob = zone == null ? createMob() : zone.createMob();
		if (mob == null) return false;
		mob.state = mob.WANDERING;

		return spawnMob(mob, disLimit, zone);
	}

	public boolean spawnMob(Mob mob, int disLimit, Zone zone) {
		PathFinder.buildDistanceMap(Dungeon.hero.pos, getPassableAndAvoidVar(mob), mob);

		int tries = 30;
		do {
			if (--tries <= 0) break;
			mob.pos = randomRespawnCell(mob);
		} while (mob.pos == -1 || PathFinder.distance[mob.pos] < disLimit
				|| (!Zone.canSpawnMobs(this, mob.pos) && zone == null || zone != null && this.zone[mob.pos] != zone));

		if (Dungeon.hero.isAlive() && tries > 0) {
			placeMob(mob);
			return true;
		} else {
			return false;
		}
	}

	public static void placeMob(Mob mob) {
		GameScene.add( mob );
		if (!mob.buffs(ChampionEnemy.class).isEmpty()){
			GLog.w(Messages.get(ChampionEnemy.class, "warn"));
		}
	}

	public int randomRespawnCell( Char ch ) {
		return randomRespawnCell(ch, false);
	}

	public int randomRespawnCell(Char ch, boolean guarantee) {
		boolean checkPath = Dungeon.hero.pos > 0;
		if (checkPath)
			PathFinder.buildDistanceMap(Dungeon.hero.pos, getPassableAndAvoidVar(ch), ch);

		//prefer spawning >>>in zones<<< where no mobs can spawn if at least one zone that can spawn mobs exists
		if (ch instanceof Hero) {
			Set<String> safeZonesToSpawn = new HashSet<>(2);
			for (Zone z : zoneMap.values()){
				if (!z.canSpawnMobs && !z.ownMobRotationEnabled) safeZonesToSpawn.add(z.getName());
			}
			if (!safeZonesToSpawn.isEmpty()) {
				int cell = 0;
				int count = guarantee ? length() : 300;
				do {
					if (--count < 0) break;
					cell = Random.Int( length() );

				} while ((Dungeon.level == this && heroFOV[cell])
						|| !isPassable(cell, ch)
						|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
						|| Actor.findChar( cell ) != null
						|| findMob(cell) != null
						|| (checkPath && PathFinder.distance[cell] == Integer.MAX_VALUE)
						|| zone[cell] == null
						|| !safeZonesToSpawn.contains(zone[cell].getName()));
				if (count < 0) {
					if (!guarantee) return -1;
				} else return cell;
			}
		}

		int cell;
		int count = guarantee ? length() : 300;
		do {

			if (--count < 0) {
				if (guarantee) {
					int l = length();
					for (cell = 0; cell < l; cell++) {
						if ((Dungeon.level == this && !heroFOV[cell])
								&& isPassable(cell, ch)
								&& (!Char.hasProp(ch, Char.Property.LARGE) || openSpace[cell])
								&& Actor.findChar(cell) == null
								&& (!(ch instanceof Piranha) || map[cell] == Terrain.WATER)
								&& findMob(cell) == null
								&& (!checkPath || PathFinder.distance[cell] != Integer.MAX_VALUE))
							return cell;//choose first valid cell
					}
				}
				return -1;//if there is no valid cell, return -1
			}

			cell = Random.Int( length() );

		} while (!isValidSpawnCell(ch, cell) || (checkPath && PathFinder.distance[cell] == Integer.MAX_VALUE));

		return cell;
	}

	protected boolean isValidSpawnCell(Char ch, int cell) {
		return !( (Dungeon.level == this && heroFOV[cell])
				|| !isPassable(cell, ch)
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar( cell ) != null
				|| !Piranha.canSurviveOnCell(ch, cell, this)
				|| (ch instanceof SentryRoom.Sentry && map[cell] != Terrain.PEDESTAL)
				|| findMob(cell) != null
				|| (!Zone.canSpawnMobs(this, cell) && !(ch == null || ch instanceof Hero || ch instanceof NPC)) );
	}

	public int randomDestination( Char ch ) {
		int cell;
		do {
			cell = Random.Int( length() );
		} while (!isPassable(cell, ch)
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell]));
		return cell;
	}
	
	public void addItemToSpawn( Item item ) {
		if (item != null) {
			itemsToSpawn.add( item );
		}
	}

	public Item findPrizeItem(){ return findPrizeItem(null); }

	public Item findPrizeItem(Class<?extends Item> match){
		if (itemsToSpawn.size() == 0)
			return null;

		if (match == null){
			//if we have a trinket catalyst, always return that first
			for (Item item : itemsToSpawn){
				if (item instanceof TrinketCatalyst){
					itemsToSpawn.remove(item);
					return item;
				}
			}

			Item item = Random.element(itemsToSpawn);
			itemsToSpawn.remove(item);
			return item;
		}

		for (Item item : itemsToSpawn){
			if (match.isInstance(item)){
				itemsToSpawn.remove( item );
				return item;
			}
		}

		return null;
	}

	public void buildFlagMaps() {
		
		for (int i=0; i < length(); i++) {
			int flags = Terrain.flags[map[i]];
			passable[i]		= (flags & Terrain.PASSABLE) != 0;
			losBlocking[i]	= (flags & Terrain.LOS_BLOCKING) != 0;
			flamable[i]		= (flags & Terrain.FLAMABLE) != 0;
			secret[i]		= (flags & Terrain.SECRET) != 0;
			solid[i]		= (flags & Terrain.SOLID) != 0;
			avoid[i]		= (flags & Terrain.AVOID) != 0;
			water[i]		= (flags & Terrain.LIQUID) != 0;
			pit[i]			= (flags & Terrain.PIT) != 0;

			passableHero[i] = passableMob[i] = passableAlly[i] = passable[i];
		}

		for (Blob b : blobs.values()){
			b.onBuildFlagMaps(this);
		}

		for (Checkpoint cp : checkpoints.values()) {
			passable[cp.pos] = false;
		}

		int lastRow = length() - width();
		for (int i=0; i < width(); i++) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[lastRow + i] = avoid[lastRow + i] = false;
			losBlocking[lastRow + i] = solid[lastRow + i] = true;
		}
		for (int i=width(); i < lastRow; i += width()) {
			passable[i] = avoid[i] = false;
			losBlocking[i] = solid[i] = true;
			passable[i + width()-1] = avoid[i + width()-1] = false;
			losBlocking[i + width()-1] = solid[i + width()-1] = true;
		}

		for (int i=0; i < length(); i++) {
			passableHero[i] = passableMob[i] = passableAlly[i] = passable[i];
		}

		for (Barrier barrier : barriers.values()) {
			if (barrier.blocksHero()) passableHero[barrier.pos] = false;
			if (barrier.blocksMobs()) passableMob[barrier.pos] = false;
			if (barrier.blocksAllies()) passableAlly[barrier.pos] = false;
		}

		for (Trap trap : traps.values()) {
			if (!trap.canBeSearchedByMagic && map[trap.pos] == Terrain.SECRET_TRAP) {
				secret[trap.pos] = false;
			}
		}

        //an open space is large enough to fit large mobs. A space is open when it is not solid
        // and there is an open corner with both adjacent cells opens
        int l = length();
        for (int i = 0; i < l; i++) {
            if (solid[i]) {
                openSpace[i] = false;
            } else {
                for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2) {
                    if (i + PathFinder.CIRCLE8[j] >= map.length || i + PathFinder.CIRCLE8[j] < 0 || solid[i + PathFinder.CIRCLE8[j]]) {
                        openSpace[i] = false;
                    } else {
                        int a = i + PathFinder.CIRCLE8[(j + 1) % 8];
                        int b = i + PathFinder.CIRCLE8[(j + 2) % 8];
                        if (a < map.length && b < map.length && a >= 0 && b >= 0
                                && !solid[i + PathFinder.CIRCLE8[(j + 1) % 8]]
                                && !solid[i + PathFinder.CIRCLE8[(j + 2) % 8]]) {
                            openSpace[i] = true;
                            break;
                        }
                    }
                }
            }
        }

	}

	public boolean isPassable(int cell) {
		return passable[cell];
	}

	public boolean isPassable(int cell, Char c) {
		if (c == null) return isPassable(cell);
		if (c instanceof Hero/* || c instanceof HeroMob*/) return isPassableHero(cell);
		if (c.alignment == Char.Alignment.ENEMY) return isPassableMob(cell);
		return isPassableAlly(cell);
	}

	public boolean isPassableHero(int cell) {
		return passableHero[cell];
	}

	public boolean isPassableMob(int cell) {
		return passableMob[cell];
	}

	public boolean isPassableAlly(int cell) {
		return passableAlly[cell];
	}

	public void setPassableLater(int cell, boolean flag) {
		passable[cell] = flag;
		if (flag) {
			Barrier barrier = barriers.get(cell);
			passableHero[cell] = passableMob[cell] = passableAlly[cell] = true;
			if (barrier != null) {
				if (barrier.blocksHero()) passableHero[barrier.pos] = false;
				if (barrier.blocksMobs()) passableMob[barrier.pos] = false;
				if (barrier.blocksAllies()) passableAlly[barrier.pos] = false;
			}
		} else {
			passableHero[cell] = passableMob[cell] = passableAlly[cell] = false;
		}
	}

	public boolean[] getPassableVar() {
		return passable;
	}

	public boolean[] getPassableVar(Char ch) {
		if (ch instanceof Hero/* || ch instanceof HeroMob*/) return getPassableHeroVar();
		if (ch.alignment == Char.Alignment.ENEMY) return getPassableMobVar();
		return passableAlly;
	}

	public boolean[] getPassableHeroVar() {
		return passableHero;
	}

	public boolean[] getPassableMobVar() {
		return passableMob;
	}

	public boolean[] getPassableAndAnyVarForBoth(Char a, Char b, boolean[] input) {
		return getPassableAndAnyVarForBoth(a, b, input, null);
	}

	public boolean[] getPassableAndAnyVarForBoth(Char a, Char b, boolean[] input, boolean[] putIntoResult) {
		boolean[] passableModifyable = BArray.or(passable, input, putIntoResult);
		for (Barrier barrier : barriers.values()) {
			if (Barrier.stopChar(barrier.pos, a) || Barrier.stopChar(barrier.pos, b))
				passableModifyable[barrier.pos] = false;
		}
		return passableModifyable;
	}

	public boolean[] getPassableAndAvoidVarForBoth(Char a, Char b) {
		return getPassableAndAnyVarForBoth(a, b, avoid);
	}

	public boolean[] getPassableAndAvoidVar(Char ch) {
		return getPassableAndAvoidVarForBoth(ch, null);
	}

	public boolean[] getPassableAndAvoidVar(Char ch, boolean[] putIntoResult) {
		return getPassableAndAvoidVarForBoth(ch, null, putIntoResult);
	}

	public boolean[] getPassableAndAvoidVarForBoth(Char a, Char b, boolean[] putIntoResult) {
		return getPassableAndAnyVarForBoth(a, b, avoid, putIntoResult);
	}

	public boolean isFlamable(int cell) {
		return flamable[cell] && Zone.isFlamable(this, cell);
	}

	public boolean[] getFlamable() {
		return flamable;
	}

	public void setFlamable(boolean[] flamable) {
		this.flamable = flamable;
	}
	
	public int getTileVarianceAt(int pos) {
		return tileVariance == null ? 0 : tileVariance[pos] >= 95 ? 3 : tileVariance[pos] >= 50 ? 2 : tileVariance[pos] > 0 ? 1 : 0;
	}
	
	public void setTileVarianceAt(int pos, int spinnerValue) {
		byte oldValue;
		if (tileVariance == null) {
			if (spinnerValue == 0) {
				return;
			}
			tileVariance = new byte[length];
			oldValue = 0;
		} else {
			oldValue = tileVariance[pos];
		}
		switch (spinnerValue) {
			case 1: tileVariance[pos] = 25; break;
			case 2: tileVariance[pos] = 75; break;
			case 3: tileVariance[pos] = 98; break;
			default:
			case 0: tileVariance[pos] = 0; break;
		}
		if (tileVariance[pos] > 0) DungeonTileSheet.tileVariance[pos] = tileVariance[pos];
		else if (tileVariance[pos] != oldValue) DungeonTileSheet.tileVariance[pos] = (byte) Random.Int(100);
	}

	public int getCoinDoorCost(int cell) {
		CoinDoor door = coinDoors.get(cell);
		return door == null ? (cell >= 0 ? CoinDoor.DEFAULT_COST : CoinDoor.costInInventory) : door.cost;
	}

	public void setCoinDoorCost(int cell, int cost) {
		CoinDoor c = coinDoors.get(cell);
		if (c == null) {
			c = new CoinDoor(cell, cost);
			coinDoors.put(cell, c);
		} else c.cost = cost;
	}

	public void destroy(int pos ) {
		//if raw tile type is flammable or empty
		int terr = map[pos];
		if (terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO
				|| (Terrain.flags[map[pos]] & Terrain.FLAMABLE) != 0) {
			removeSimpleCustomTile(pos);
			set(pos, Terrain.EMBERS);
		}
		blobs.doOnEach(Web.class, b -> b.clear(pos));
	}

	public void removeSimpleCustomTile(int cell) {
		visualRegions[cell] = LevelScheme.REGION_NONE;
		//expected to call GameScene.updateMapCell(cell) later!
		CustomTilemap remove = null;
		for (CustomTilemap ct : customTiles) {
			if (ct instanceof CustomTileLoader.SimpleCustomTile
					&& ct.tileX + ct.tileY * width() == cell) {
				remove = ct;
				break;
			}
		}
		if (remove != null) customTiles.remove(remove);
		remove = null;
		for (CustomTilemap ct : customWalls) {
			if (ct instanceof CustomTileLoader.SimpleCustomTile
					&& ct.tileX + ct.tileY * width() == cell) {
				remove = ct;
				break;
			}
		}
		if (remove != null) customWalls.remove(remove);
	}

	public void cleanWalls() {
		if (discoverable == null || discoverable.length != length) {
			discoverable = new boolean[length()];
		}

		for (int i=0; i < length(); i++) {
			
			cleanWallCell(i);
		}
	}

	public void cleanWallCell(int cell) {
		boolean d = false;

		for (int j=0; j < PathFinder.NEIGHBOURS9.length; j++) {
			int n = cell + PathFinder.NEIGHBOURS9[j];
			if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
				d = true;
				break;
			}
		}

		discoverable[cell] = d;
	}

	@KeepProguard
	public void setTerrain( int cell, int terrain ){//used for Lua
		set( cell, terrain, this );
	}

	public static void set( int cell, int terrain ){
		set( cell, terrain, Dungeon.level );
	}
	
	public static void set( int cell, int terrain, Level level ) {
		Painter.set( level, cell, terrain );

		if (terrain != Terrain.TRAP && terrain != Terrain.SECRET_TRAP && terrain != Terrain.INACTIVE_TRAP){
			level.traps.remove( cell );
		}

		int flags = Terrain.flags[terrain];
		level.setPassableLater(cell, (flags & Terrain.PASSABLE) != 0);
		level.losBlocking[cell]	    = (flags & Terrain.LOS_BLOCKING) != 0;
		level.flamable[cell]		= (flags & Terrain.FLAMABLE) != 0;
		level.secret[cell]		    = (flags & Terrain.SECRET) != 0;
		level.solid[cell]			= (flags & Terrain.SOLID) != 0;
		level.avoid[cell]			= (flags & Terrain.AVOID) != 0;
		level.pit[cell]			    = (flags & Terrain.PIT) != 0;
		level.water[cell]			= terrain == Terrain.WATER;

		if (!level.insideMap(cell)) {
			level.passable[cell] = level.passableHero[cell] = level.passableMob[cell] = level.passableAlly[cell] = level.avoid[cell] = false;
			level.losBlocking[cell] = level.solid[cell] = true;
		}

		Trap trap = level.traps.get(cell);
		if (trap != null && !trap.canBeSearchedByMagic && level.map[trap.pos] == Terrain.SECRET_TRAP) {
			level.secret[trap.pos] = false;
		}

        for (int i : PathFinder.NEIGHBOURS9) {
            i = cell + i;
            if (i < 0 || i >= level.solid.length) continue;
            if (level.solid[i]) {
                level.openSpace[i] = false;
            } else {
                for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2) {
                    if (i + PathFinder.CIRCLE8[j] >= level.solid.length
                            || i + PathFinder.CIRCLE8[j] < 0
                            || level.solid[i + PathFinder.CIRCLE8[j]]) {
                        level.openSpace[i] = false;
                    } else {
                        int index1 = i + PathFinder.CIRCLE8[(j + 1) % 8];
                        int index2 = i + PathFinder.CIRCLE8[(j + 2) % 8];
                        if (index1 >= 0 && index2 >= 0 && index1 <level.solid.length && index2 < level.solid.length
                                && !level.solid[index1] && !level.solid[index2]) {
                            level.openSpace[i] = true;
                            break;
                        }
                    }
                }
            }
        }

		if (level.visualRegions[cell] == REGION_NONE) {
			level.visualMap[cell] = terrain;
		}
    }

    public Heap drop(Item item, int cell) {

		if (item == null || Challenges.isItemBlocked(item)){

			//create a dummy heap, give it a dummy sprite, don't add it to the game, and return it.
			//effectively nullifies whatever the logic calling this wants to do, including dropping items.
			Heap heap = new Heap();
			ItemSprite sprite = heap.sprite = new ItemSprite();
			sprite.link(heap);
			return heap;

		}
		
		Heap heap = heaps.get( cell );
		if (heap == null) {
			
			heap = new Heap();
			heap.seen = Dungeon.level == this && heroFOV[cell] || CustomDungeon.isEditing();
			heap.pos = cell;
			heap.drop(item);
			if (map[cell] == Terrain.CHASM || (Dungeon.level != null && pit[cell])) {
				Dungeon.dropToChasm( item );
				GameScene.discard( heap );
			} else {
				heaps.put( cell, heap );
				GameScene.add( heap );
			}
			
		} else if (!CustomDungeon.isEditing() && (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST)) {
			
			int n;
			do {
				n = cell + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			} while (!isPassableHero(n) && !avoid[n]);
			return drop( item, n );
			
		} else {
			heap.drop(item);
		}
		
		if (Dungeon.level != null && SandboxPixelDungeon.scene() instanceof GameScene) {
			pressCell( cell );
		}
		
		return heap;
	}
	
	public Plant plant( Plant.Seed seed, int pos ) {

		Plant plant = plants.get( pos );
		if (plant != null) {
			plant.wither();
		}

		if (map[pos] == Terrain.HIGH_GRASS ||
				map[pos] == Terrain.FURROWED_GRASS ||
				map[pos] == Terrain.EMPTY ||
				map[pos] == Terrain.EMBERS ||
				map[pos] == Terrain.EMPTY_DECO) {
			set(pos, Terrain.GRASS, this);
			GameScene.updateMap(pos);
		}

		//we have to get this far as grass placement has RNG implications in levelgen
		if (Dungeon.isChallenged(Challenges.NO_HERBALISM)){
			return null;
		}
		
		plant = seed.couch( pos, this );
		plants.put( pos, plant );
		
		GameScene.plantSeed( pos );

		for (Char ch : Actor.chars()){
			if (ch instanceof WandOfRegrowth.Lotus
					&& ((WandOfRegrowth.Lotus) ch).inRange(pos)
					&& Actor.findChar(pos) != null){
				plant.trigger();
				return null;
			}
		}
		
		return plant;
	}
	
	public void uproot( int pos ) {
		plants.remove(pos);
		GameScene.updateMap( pos );
	}

	public Trap setTrap( Trap trap, int pos ){
		Trap existingTrap = traps.get(pos);
		if (existingTrap != null){
			traps.remove( pos );
		}
		trap.set( pos );
		traps.put( pos, trap );
		GameScene.updateMap( pos );
		return trap;
	}

	public void disarmTrap( int pos, boolean reveal ) {
		set(pos, reveal ? Terrain.INACTIVE_TRAP : Terrain.EMPTY);
		GameScene.updateMap(pos);
	}

	public void discover( int cell ) {
		removeSimpleCustomTile(cell);
		set( cell, Terrain.discover( map[cell] ) );
		Trap trap = traps.get( cell );
		if (trap != null)
			trap.reveal();
		GameScene.updateMap( cell );
	}

	public final boolean setCellToWater( boolean includeTraps, int cell ){
		if (canSetCellToWater(includeTraps, cell)) {
			if (includeTraps && TileItem.isTrapTerrainCell(map[cell])){
				Dungeon.level.traps.remove(cell);
			}
			set(cell, Terrain.WATER);
			GameScene.updateMap(cell);
			return true;
		}

		return false;
	}

	public boolean canSetCellToWater( boolean includeTraps, int cell ) {
		Point p = cellToPoint(cell);

		//if a custom tilemap is over that cell, don't put water there
		for (CustomTilemap cust : customTiles){
			Point custPoint = new Point(p);
			custPoint.x -= cust.tileX;
			custPoint.y -= cust.tileY;
			if (custPoint.x >= 0 && custPoint.y >= 0
					&& custPoint.x < cust.tileW && custPoint.y < cust.tileH){
				if (cust.image(custPoint.x, custPoint.y) != null){
					return false;
				}
			}
		}

		int terr = map[cell];
		return terr == Terrain.EMPTY || terr == Terrain.GRASS ||
				terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
				terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
				|| terr == Terrain.EMPTY_DECO
				|| includeTraps && TileItem.isTrapTerrainCell(terr);
	}
	
	public int fallCell( boolean fallIntoPit, String destZone ) {
		Dungeon.hero.pos = -1;
		int tries = length;
		int result;
		do {
			result = randomRespawnCell(null);
			if (tries-- < 0) {
				if (destZone != null) {

					for (int i = 0; i < length; i++) {
						if (zone[i] != null && destZone.equals(zone[i].getName()) && isValidSpawnCell(null, i))
							return i;
					}

					destZone = null;
					tries = length / 3;
				} else return -1;
			}
		} while (result == -1
				|| (destZone != null && (zone[result] == null || !zone[result].getName().equals(destZone)))
				|| traps.get(result) != null
				|| findMob(result) != null);
		return result;
	}
	
	public void occupyCell( Char ch ){
		if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos, Web.class) > 0){
			blobs.doOnEach(Web.class, b -> b.clear(ch.pos));
			Web.affectChar( ch );
		}

		if (!CustomDungeon.isEditing()) {
			applyZoneBuffs(ch);
			if (zone[ch.pos] != null) zone[ch.pos].onZoneEntered(ch);
		}

		if (!ch.isFlying() && pit[ch.pos]) {
			if (ch == Dungeon.hero) {
				Chasm.heroFall(ch.pos);
			} else if (ch instanceof Mob) {
				Chasm.mobFall((Mob) ch);
			}
			return;
		}

		int setValue = ch == Dungeon.hero ? CustomParticle.HERO_JUST_ENTERED : CustomParticle.CHAR_JUST_ENTERED;
		for (CustomParticle particle : Dungeon.level.particles.values()) {
			if (particle != null && particle.cur != null
					&& particle.cur[ch.pos] >= CustomParticle.CELL_ACTIVE) {
				particle.volume += setValue - particle.cur[ch.pos];
				particle.cur[ch.pos] = setValue;
			}
		}

		if (!ch.isFlying()) {

			//we call act here instead of detach in case the debuffs haven't managed to deal dmg once yet
			if (map[ch.pos] == Terrain.WATER){
				if (ch.buff(Burning.class) != null){
					ch.buff(Burning.class).act();
				}
				if (ch.buff(Ooze.class) != null){
					ch.buff(Ooze.class).act();
				}
			}

			if ( (map[ch.pos] == Terrain.GRASS || map[ch.pos] == Terrain.EMBERS)
					&& ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS)
					&& ch.buff(Talent.RejuvenatingStepsCooldown.class) == null) {

				if (!Regeneration.regenOn()) {
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200) {
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else {
					set(ch.pos, Terrain.HIGH_GRASS);
					Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
				}
				GameScene.updateMap(ch.pos);
				Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15f - 5f * Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
			}
			//characters which are not the hero or a sheep 'soft' press cells
			if (!CustomDungeon.isEditing())
				pressCell(ch.pos, ch instanceof Hero || ch instanceof Sheep);

		}

		if (map[ch.pos] == Terrain.DOOR){
			if (!CustomDungeon.isEditing()) Door.enter( ch.pos );
		}

		if (ch.isAlive() && !Piranha.canSurviveOnCell(ch, ch.pos, this) && !CustomDungeon.isEditing()){
			ch.dieOnLand();
		}

		if (ch == Dungeon.hero) {
			updateMusic();
		}
	}

	protected Zone zoneWithPlayedMusic = null;

	protected void updateMusic() {
		if (WandmakerQuest.updateMusic()) {
			return;
		}
		if (Objects.equals(Zone.getMusic(this, Dungeon.hero.pos), zoneWithPlayedMusic == null ? null : zoneWithPlayedMusic.music)) {
			return;
		}
		if (zoneWithPlayedMusic != zone[Dungeon.hero.pos]) {
			zoneWithPlayedMusic = zone[Dungeon.hero.pos];

			Game.runOnRenderThread(() -> Music.INSTANCE.fadeOut(1f, () -> {
				if (Dungeon.level != null) {
					Dungeon.level.playLevelMusic();
				}
			}));
		}
	}

	public void applyZoneBuffs(Char ch) {
		if (ch.currentZoneBuffs != null && ch.currentZoneBuffs != zone[ch.pos]) {
			ch.currentZoneBuffs.removeBuffs(ch);
			ch.currentZoneBuffs = null;
		}
		if (zone[ch.pos] != null) {
			ch.currentZoneBuffs = zone[ch.pos];
			ch.currentZoneBuffs.affectBuffs(ch);
		}
	}
	
	//public method for forcing the hard press of a cell. e.g. when an item lands on it
	public void pressCell( int cell ){
		pressCell( cell, true );
	}
	
	//a 'soft' press ignores hidden traps
	//a 'hard' press triggers all things
	private void pressCell( int cell, boolean hard ) {

		Trap trap = null;
		
		switch (map[cell]) {
		
		case Terrain.SECRET_TRAP:
			if (hard) {
				trap = traps.get( cell );
				GLog.i(Messages.get(Level.class, "hidden_trap", trap.name()));
			}
			break;
			
		case Terrain.TRAP:
			trap = traps.get( cell );
			break;
			
		case Terrain.HIGH_GRASS:
		case Terrain.FURROWED_GRASS:
			HighGrass.trample( this, cell);
			break;
			
		case Terrain.WELL:
			WellWater.affectCell( cell );
			break;
			
		case Terrain.DOOR:
			Door.enter( cell );
			break;
		}

		TimekeepersHourglass.timeFreeze timeFreeze =
				Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);

		Swiftthistle.TimeBubble bubble =
				Dungeon.hero.buff(Swiftthistle.TimeBubble.class);

		if (trap != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				discover(cell);
				bubble.setDelayedPress(cell);
				
			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAP);
				discover(cell);
				timeFreeze.setDelayedPress(cell);
				
			} else {
				if (Dungeon.hero.pos == cell) {
					Dungeon.hero.interrupt();
				}
				trap.trigger();

			}
		}
		
		Plant plant = plants.get( cell );
		if (plant != null) {
			if (bubble != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				bubble.setDelayedPress(cell);

			} else if (timeFreeze != null){
				Sample.INSTANCE.play(Assets.Sounds.TRAMPLE, 1, Random.Float( 0.96f, 1.05f ) );
				timeFreeze.setDelayedPress(cell);

			} else {
				plant.trigger();

			}
		}

		if (hard && Blob.volumeAt(cell, Web.class) > 0){
			blobs.doOnEach(Web.class, b -> b.clear(cell));
		}
	}

	private static boolean[] heroMindFov;

	private static boolean[] modifiableBlocking;

	public void updateFieldOfView( Char c, boolean[] fieldOfView ) {

		int cx = c.pos % width();
		int cy = c.pos / width();
		
		boolean sighted = c.buff( Blindness.class ) == null && c.buff( Shadows.class ) == null
						&& c.isAlive();
		if (sighted) {
			boolean[] blocking = null;

			if (modifiableBlocking == null || modifiableBlocking.length != Dungeon.level.losBlocking.length){
				modifiableBlocking = new boolean[Dungeon.level.losBlocking.length];
			}

			//grass is see-through by some specific entities, but not during the fungi quest
			if (!(Dungeon.level instanceof  MiningLevel) || ((MiningLevel) Dungeon.level).questType() != BlacksmithQuest.FUNGI){
				if ((c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN)
						|| c instanceof YogFist.SoiledFist || c instanceof GnollGeomancer) {
					if (blocking == null) {
						System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
						blocking = modifiableBlocking;
					}
					for (int i = 0; i < blocking.length; i++) {
						if (blocking[i] && (Dungeon.level.map[i] == Terrain.HIGH_GRASS || Dungeon.level.map[i] == Terrain.FURROWED_GRASS)) {
							blocking[i] = false;
						}
					}
				}
			}

			//allies and specific enemies can see through shrouding fog
			if ((c.alignment != Char.Alignment.ALLY && !(c instanceof GnollGeomancer))
					&& Dungeon.level.blobs.containsKey(SmokeScreen.class)
					&& Blob.totalVolume(SmokeScreen.class) > 0) {
				if (blocking == null) {
					System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
					blocking = modifiableBlocking;
				}
				for (Blob s : blobs.get(SmokeScreen.class)){
					for (int i = 0; i < blocking.length; i++){
						if (!blocking[i] && s.cur[i] > 0){
							blocking[i] = true;
						}
					}
				}
			}

			if (blocking == null){
				blocking = Dungeon.level.losBlocking;
			}

			float viewDist = c.viewDistance;
			if (c instanceof Hero){
				viewDist *= 1f + 0.25f*((Hero) c).pointsInTalent(Talent.FARSIGHT);
				viewDist *= EyeOfNewt.visionRangeMultiplier((Hero) c);
			}
			
			ShadowCaster.castShadow( cx, cy, width(), fieldOfView, blocking, Math.round(viewDist), c instanceof Hero );
		} else {
			BArray.setFalse(fieldOfView);
		}
		
		int sense = 1;
		if (c.isAlive()) {
			for (Buff b : c.buffs( MindVision.class )) {
				sense = Math.max( ((MindVision)b).distance, sense );
			}
			if (c.buff(MagicalSight.class) != null){
				sense = Math.max( MagicalSight.DISTANCE, sense );
			}
		}
		
		//uses rounding
		if (!sighted || sense > 1) {
			
			int[][] rounding = ShadowCaster.rounding;
			
			int left, right;
			int pos;
			for (int y = Math.max(0, cy - sense); y <= Math.min(height()-1, cy + sense); y++) {
				if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
					left = cx - rounding[sense][Math.abs(cy - y)];
				} else {
					left = sense;
					while (rounding[sense][left] < rounding[sense][Math.abs(cy - y)]){
						left--;
					}
					left = cx - left;
				}
				right = Math.min(width()-1, cx + cx - left);
				left = Math.max(0, left);
				pos = left + y * width();
				System.arraycopy(discoverable, pos, fieldOfView, pos, right - left + 1);
			}
		}

		if (c instanceof SpiritHawk.HawkAlly && Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) >= 3){
			int range = 1+(Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE)-2);
			for (Mob mob : mobs) {
				int p = mob.pos;
				if (!fieldOfView[p] && distance(c.pos, p) <= range) {
					for (int i : PathFinder.NEIGHBOURS9) {
						fieldOfView[mob.pos + i] = true;
					}
				}
			}
		}

		//Currently only a hero can get mind vision or awareness
		if (c.isAlive() && (c instanceof Hero || c instanceof HeroMob)   && c == Dungeon.hero) {
			Hero hero = c instanceof Hero ? (Hero) c : ((HeroMob) c).hero();

			if (heroMindFov == null || heroMindFov.length != length()){
				heroMindFov = new boolean[length];
			} else {
				BArray.setFalse(heroMindFov);
			}

			Dungeon.hero.mindVisionEnemies.clear();
			if (c.buff( MindVision.class ) != null) {
				for (Mob mob : mobs) {
					if (mob instanceof Mimic && mob.alignment == Char.Alignment.NEUTRAL && ((Mimic) mob).stealthy()){
						continue;
					}
					for (int i : PathFinder.NEIGHBOURS9) {
						heroMindFov[mob.pos + i] = true;
					}
				}
			} else {

				int mindVisRange = 0;
				if (hero.hasTalent(Talent.HEIGHTENED_SENSES)){
					mindVisRange = 1+hero.pointsInTalent(Talent.HEIGHTENED_SENSES);
				}
				if (c.buff(DivineSense.DivineSenseTracker.class) != null){
					if (((Hero) c).heroClass == HeroClass.CLERIC){
						mindVisRange = 4+4*((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					} else {
						mindVisRange = 1+2*((Hero) c).pointsInTalent(Talent.DIVINE_SENSE);
					}
				}
				mindVisRange = Math.max(mindVisRange, EyeOfNewt.mindVisionRange(hero));

				//power of many's life link spell allows allies to get divine sense
				Char ally = PowerOfMany.getPoweredAlly();
				if (ally != null && ally.buff(DivineSense.DivineSenseTracker.class) == null){
					ally = null;
				}

				if (mindVisRange >= 1) {
					for (Mob mob : mobs) {
						if (mob instanceof Mimic && mob.alignment == Char.Alignment.NEUTRAL && ((Mimic) mob).stealthy()){
							continue;
						}
						int p = mob.pos;
						if (!fieldOfView[p] && (distance(c.pos, p) <= mindVisRange || (ally != null && distance(ally.pos, p) <= mindVisRange))) {
							for (int i : PathFinder.NEIGHBOURS9) {
								int cell = mob.pos + i;
							if (cell >= 0 && cell < heroMindFov.length) heroMindFov[mob.pos + i] = true;}
						}
					}
				}
			}
			
			if (c.buff( Awareness.class ) != null) {
				for (Heap heap : heaps.valueList()) {
					int p = heap.pos;
					for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
				}
			}

			for (TalismanOfForesight.CharAwareness a : c.buffs(TalismanOfForesight.CharAwareness.class)){
				Char ch = (Char) Actor.findById(a.charID);
				if (ch == null || !ch.isAlive()) {
					continue;
				}
				int p = ch.pos;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[p+i] = true;
			}

			for (TalismanOfForesight.HeapAwareness h : c.buffs(TalismanOfForesight.HeapAwareness.class)){
				if (!Dungeon.levelName.equals(h.level) || Dungeon.branch != h.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[h.pos+i] = true;
			}

			for (Mob m : mobs){
				if (m instanceof WandOfWarding.Ward
						|| m instanceof WandOfRegrowth.Lotus
						|| m instanceof SpiritHawk.HawkAlly
						|| m.buff(PowerOfMany.PowerBuff.class) != null){
					if (m.fieldOfView == null || m.fieldOfView.length != length()){
						m.fieldOfView = new boolean[length()];
						Dungeon.level.updateFieldOfView( m, m.fieldOfView );
					}
					BArray.or(heroMindFov, m.fieldOfView, heroMindFov);
				}
			}

			for (RevealedArea a : c.buffs(RevealedArea.class)){
				if (!Dungeon.levelName.equals(a.level) || Dungeon.branch != a.branch) continue;
				for (int i : PathFinder.NEIGHBOURS9) heroMindFov[a.pos+i] = true;
			}

			//set mind vision chars
			for (Mob mob : mobs) {
				if (heroMindFov[mob.pos] && (!fieldOfView[mob.pos] || mob.invisible > 0) && Mimic.isLikeMob(mob)){
					Dungeon.hero.mindVisionEnemies.add(mob);
				}
			}

			BArray.or(heroMindFov, fieldOfView, fieldOfView);

		}

		if (c == Dungeon.hero) {
			for (Heap heap : heaps.valueList())
				if (!heap.seen && fieldOfView[heap.pos])
					heap.seen = true;
		}

	}

	public boolean isLevelExplored( String levelName ){
		return false;
	}
	
	public int distance( int a, int b ) {
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return Math.max( Math.abs( ax - bx ), Math.abs( ay - by ) );
	}
	
	public boolean adjacent( int a, int b ) {
		return distance( a, b ) == 1;
	}
	
	//uses pythagorean theorum for true distance, as if there was no movement grid
	public float trueDistance(int a, int b){
		int ax = a % width();
		int ay = a / width();
		int bx = b % width();
		int by = b / width();
		return (float)Math.sqrt(Math.pow(Math.abs( ax - bx ), 2) + Math.pow(Math.abs( ay - by ), 2));
	}

	//usually just if a cell is solid, but other cases exist too
	public boolean invalidHeroPos( int tile ){
		return !passable[tile] && !avoid[tile];
	}

	//returns true if the input is a valid tile within the level
	public boolean insideMap( int tile ){
				//top and bottom row and beyond
		return !((tile < width || tile >= length - width) ||
				//left and right column
				(tile % width == 0 || tile % width == width-1));
	}

	public Point cellToPoint( int cell ){
		return new Point(cell % width(), cell / width());
	}

	public int pointToCell( Point p ){
		return p.x + p.y*width();
	}
	
	public String tileName( int tile ) {
		
		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_name");
			case Terrain.EMPTY:
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
			case Terrain.CUSTOM_DECO_EMPTY:
			case Terrain.SECRET_TRAP:
				return Messages.get(Level.class, "floor_name");
			case Terrain.GRASS:
				return Messages.get(Level.class, "grass_name");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_name");
			case Terrain.WALL:
			case Terrain.WALL_DECO:
				return Messages.get(Level.class, "wall_name");
			case Terrain.SECRET_DOOR:
				return Messages.get(Level.class, CustomDungeon.showHiddenDoors() ? "hidden_wall_name" : "wall_name");
			case Terrain.SECRET_LOCKED_DOOR:
				return Messages.get(Level.class, CustomDungeon.showHiddenDoors() ? "hidden_locked_wall_name" : "wall_name");
			case Terrain.SECRET_CRYSTAL_DOOR:
				return Messages.get(Level.class, CustomDungeon.showHiddenDoors() ? "hidden_crystal_wall_name" : "wall_name");
			case Terrain.DOOR:
				return Messages.get(Level.class, "closed_door_name");
			case Terrain.MIMIC_DOOR:
				return Messages.get(Level.class, CustomDungeon.showHiddenDoors() ? "mimic_door_name" : "closed_door_name");
			case Terrain.OPEN_DOOR:
				return Messages.get(Level.class, "open_door_name");
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(Level.class, "entrance_name");
			case Terrain.EXIT:
				return Messages.get(Level.class, "exit_name");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_name");
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "furrowed_grass_name");
			case Terrain.COIN_DOOR:
				return Messages.get(Level.class, "coin_door_name");
			case Terrain.LOCKED_DOOR:
				return Messages.get(Level.class, "locked_door_name");
			case Terrain.CRYSTAL_DOOR:
				return Messages.get(Level.class, "crystal_door_name");
			case Terrain.PEDESTAL:
				return Messages.get(Level.class, "pedestal_name");
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_name");
			case Terrain.HIGH_GRASS:
				return Messages.get(Level.class, "high_grass_name");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_name");
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "unlocked_exit_name");
			case Terrain.SIGN:
			case Terrain.SIGN_SP:
				return Messages.get(Level.class, "sign_name");
			case Terrain.WELL:
				return Messages.get(Level.class, "well_name");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_name");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_name");
			case Terrain.INACTIVE_TRAP:
				return Messages.get(Level.class, "inactive_trap_name");
			case Terrain.BOOKSHELF:
				return Messages.get(Level.class, "bookshelf_name");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_name");
			case Terrain.MINE_CRYSTAL:
				return Messages.get(MiningLevel.class, "crystal_name");
			case Terrain.MINE_BOULDER:
				return Messages.get(MiningLevel.class, "boulder_name");
			default:
				return Messages.get(Level.class, "default_name");
		}
	}
	
	public String tileDesc( int tile, int cell ) {
		
		switch (tile) {
			case Terrain.CHASM:
				return Messages.get(Level.class, "chasm_desc");
			case Terrain.WATER:
				return Messages.get(Level.class, "water_desc");
			case Terrain.ENTRANCE:
			case Terrain.ENTRANCE_SP:
				return Messages.get(Level.class, "entrance_desc") + appendNoTransWarning(cell);
			case Terrain.EXIT:
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "exit_desc") + appendNoTransWarning(cell);
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_desc");
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "high_grass_desc");
			case Terrain.MIMIC_DOOR:
				if (CustomDungeon.showHiddenDoors()) return Messages.get(Level.class, "mimic_door_desc");
				else return "";
			case Terrain.COIN_DOOR:
				return Messages.get(Level.class, "coin_door_desc");
			case Terrain.LOCKED_DOOR:
				return Messages.get(Level.class, "locked_door_desc");
			case Terrain.CRYSTAL_DOOR:
				return Messages.get(Level.class, "crystal_door_desc");
			case Terrain.LOCKED_EXIT:
				return Messages.get(Level.class, "locked_exit_desc") + appendNoTransWarning(cell);
			case Terrain.BARRICADE:
				return Messages.get(Level.class, "barricade_desc");
			case Terrain.SIGN:
			case Terrain.SIGN_SP:
				return Messages.get(Level.class, "sign_desc");
			case Terrain.INACTIVE_TRAP:
				return Messages.get(Level.class, "inactive_trap_desc");
			case Terrain.STATUE:
			case Terrain.STATUE_SP:
				return Messages.get(Level.class, "statue_desc");
			case Terrain.ALCHEMY:
				return Messages.get(Level.class, "alchemy_desc");
			case Terrain.EMPTY_WELL:
				return Messages.get(Level.class, "empty_well_desc");
			case Terrain.MINE_CRYSTAL:
				return Messages.get(MiningLevel.class, "crystal_desc");
			case Terrain.MINE_BOULDER:
				return Messages.get(MiningLevel.class, "boulder_desc");
			default:
				return "";
		}
	}

	public String appendNoTransWarning(int cell) {
		return cell >= 0 && transitions.get(cell) == null ? "\n" + Messages.get(Hero.class, "no_trans_warning") : "";
	}

	public static String getMessageKey(int terrain, boolean desc) {
		switch (terrain) {//name and desc available
			case Terrain.CHASM: return "chasm";
			case Terrain.WATER: return "water";
			case Terrain.ENTRANCE: return "entrance";
			case Terrain.EXIT:
			case Terrain.UNLOCKED_EXIT: return "exit";
			case Terrain.EMBERS: return "embers";
			case Terrain.MIMIC_DOOR: return "mimic_door";
			case Terrain.COIN_DOOR: return "coin_door";
			case Terrain.CRYSTAL_DOOR: return "crystal_door";
			case Terrain.LOCKED_DOOR: return "locked_door";
			case Terrain.LOCKED_EXIT: return "locked_exit";
			case Terrain.BARRICADE: return "barricade";
			case Terrain.INACTIVE_TRAP: return "inactive_trap";
			case Terrain.SIGN:
			case Terrain.SIGN_SP: return "sign";
			case Terrain.STATUE:
			case Terrain.STATUE_SP: return "statue";
			case Terrain.ALCHEMY: return "alchemy";
			case Terrain.EMPTY_WELL: return "empty_well";
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS: return "high_grass";
			case Terrain.MINE_CRYSTAL: return "crystal";
			case Terrain.MINE_BOULDER: return "boulder";
		}

		if (desc) return "";

		switch (terrain) {//only name available
			case Terrain.EMPTY:
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
			case Terrain.CUSTOM_DECO_EMPTY:
			case Terrain.SECRET_TRAP: return "floor";
			case Terrain.GRASS: return "grass";
			case Terrain.WALL:
			case Terrain.WALL_DECO:
			case Terrain.SECRET_DOOR:
			case Terrain.SECRET_LOCKED_DOOR:
			case Terrain.SECRET_CRYSTAL_DOOR: return "wall";
			case Terrain.DOOR: return "closed_door";
			case Terrain.OPEN_DOOR: return "open_door";
			case Terrain.PEDESTAL: return "pedestal";
			case Terrain.WELL: return "well";
			case Terrain.BOOKSHELF: return "bookshelf";
			default: return "";
		}
	}

	public static String getFullMessageKey(int region, int terrain, boolean desc) {
		String level;
		switch (region) {
			case REGION_SEWERS: level = "sewerlevel"; break;
			case REGION_PRISON: level = "prisonlevel"; break;
			case REGION_CAVES: level = "caveslevel"; break;
			case REGION_CITY: level = "citylevel"; break;
			case REGION_HALLS: level = "hallslevel"; break;
			default: return null;
		}
		if (terrain == Terrain.MINE_CRYSTAL || terrain == Terrain.MINE_BOULDER)
			level = "mininglevel";

		String msgKey = getMessageKey(terrain, desc);
		String fullKey = "levels." + level + "." + msgKey + "_" + (desc ? "desc" : "name");
		if (Messages.NO_TEXT_FOUND.equals(Messages.get(fullKey))) {
			fullKey = "levels.level." + msgKey + "_" + (desc ? "desc" : "name");
			if (Messages.NO_TEXT_FOUND.equals(Messages.get(fullKey))) {
				return desc ? "windows.wndinfocell.nothing" : "levels.level.default_name";
			}
		}
		return fullKey;

	}

}
