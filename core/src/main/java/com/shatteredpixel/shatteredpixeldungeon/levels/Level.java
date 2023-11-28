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

package com.shatteredpixel.shatteredpixeldungeon.levels;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CAVES;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CITY;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_HALLS;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_PRISON;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_SEWERS;

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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobBasedOnDepth;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.NPC;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.editor.Sign;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.QuestLevels;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.Zone;
import com.shatteredpixel.shatteredpixeldungeon.editor.quests.BlacksmithQuest;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomTileLoader;
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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
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
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
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
import java.util.Map;
import java.util.Set;

public abstract class Level implements Bundlable {

	public static final String SURFACE = "surface", NONE = "none", ANY = "any" + (char) 30 + (char) 31;

	public static enum Feeling {
		NONE,
		CHASM,
		WATER,
		GRASS,
		DARK,
		LARGE,
		TRAPS,
		SECRETS
	}

	protected int width;
	protected int height;
	protected int length;

	public String name;

	protected static final float TIME_TO_RESPAWN	= 50;

	public int version;
	
	public int[] map;
	public boolean[] visited;
	public boolean[] mapped;
	public boolean[] discoverable;

	public int viewDistance = Dungeon.isChallenged( Challenges.DARKNESS ) ? 2 : 8;
	
	public boolean[] heroFOV;
	
	public boolean[] passable;
	public boolean[] losBlocking;
	private boolean[] flamable;
	public boolean[] secret;
	public boolean[] solid;
	public boolean[] avoid;
	public boolean[] water;
	public boolean[] pit;

	public boolean[] openSpace;

	/**
	 * <b>IMPORTANT: keep keys synchronised with levelScheme.zones!!!</b>
	 */
	public final Map<String, Zone> zoneMap = new HashMap<>(3);
	public Zone[] zone;
	
	public Feeling feeling = Feeling.NONE;

	public Map<Integer, LevelTransition> transitions;

	//when a boss level has become locked.
	public boolean locked = false;
	public int musicVariant = 0;//so bosses can play boss music while they are alive
	private final LinkedList<Integer> musicRequests = new LinkedList<>();//bosses can add their music request in notice(), and remove it in die(), always the last element is played
	
	public HashSet<Mob> mobs;
	public SparseArray<Heap> heaps;
	public BlobStoreMap blobs;
	public SparseArray<Plant> plants;
	public SparseArray<Trap> traps;
	public SparseArray<Sign> signs;
	public HashSet<CustomTilemap> customTiles;
	public HashSet<CustomTilemap> customWalls;
	
	protected ArrayList<Item> itemsToSpawn = new ArrayList<>();

	protected Group visuals;
	protected Group wallVisuals;

	public int color1 = 0x004400;
	public int color2 = 0x88CC44;

	public LevelScheme levelScheme;

	public static final int NO_BOSS_MOB = -2;//need to be -2 bc -1 is pos of mobs in inv
	public int bossmobAt = -2;//store as pos so we don't have problems with undo, only for CustomLevel
	public Mob bossMob;//after initForPlay
	public boolean bossFound;

	//For loading
	public static boolean bossFoundStatic;
	public static Mob bossMobStatic;

	private static final String VERSION     = "version";
	private static final String WIDTH       = "width";
	private static final String HEIGHT      = "height";
	private static final String NAME        = "name";
	private static final String MAP			= "map";
	private static final String VISITED		= "visited";
	private static final String MAPPED		= "mapped";
	private static final String TRANSITIONS	= "transitions";
	private static final String LOCKED      = "locked";
	private static final String HEAPS		= "heaps";
	private static final String PLANTS		= "plants";
	private static final String TRAPS       = "traps";
	private static final String SIGNS       = "signs";
	private static final String CUSTOM_TILES= "customTiles";
	private static final String CUSTOM_WALLS= "customWalls";
	private static final String MOBS		= "mobs";
	private static final String BLOBS		= "blobs";
	private static final String FEELING		= "feeling";
	private static final String VIEW_DISTANCE = "view_distance";
	private static final String BOSS_MOB_AT = "boss_mob_at";
	private static final String BOSS_FOUND  = "boss_found";
	private static final String ZONES       = "zones";
	private static final String MUSIC_VARIANT = "music_variant";
	private static final String MUSIC_REQUESTS = "music_requests";


	public void create() {

        Random.pushGenerator(Dungeon.seedForLevel(name,this instanceof MiningLevel ? 1 + ((MiningLevel)this).questId : 0 ));//TODO fixme tzz

		if (!Dungeon.bossLevel() && Dungeon.branch == 0) {

			if (levelScheme.spawnItems) {

				for (Item item : levelScheme.prizeItemsToSpawn) {
					addItemToSpawn(item);
					item.reset();//important for scroll runes being inited
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
						//add a second torch to help with the larger floor
						if (Dungeon.isChallenged(Challenges.DARKNESS)) {
							addItemToSpawn(new Torch());
						}
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
			plants = new SparseArray<>();
			traps = new SparseArray<>();
			signs = new SparseArray<>();
			customTiles = new HashSet<>();
			customWalls = new HashSet<>();
			
		} while (!build());
		
		buildFlagMaps();
		cleanWalls();
		
		createMobs();
		createItems();

		Random.popGenerator();
	}
	
	public void setSize(int w, int h){
		
		width = w;
		height = h;
		length = w * h;
		
		map = new int[length];
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
		for (Zone z : zoneMap.values()) {
			z.initTransitions(this);
		}
		for (Mob m : mobs) {
			if (m instanceof MobBasedOnDepth) ((MobBasedOnDepth) m).setLevel(Dungeon.depth);
			if (m instanceof Blacksmith) ((Blacksmith) m).registerQuest();
			else if (m instanceof Pylon && ((Pylon) m).alwaysActive && m.playerAlignment == Mob.NORMAL_ALIGNMENT) m.alignment = Char.Alignment.ENEMY;
			else if (m instanceof Tengu && m.playerAlignment == Mob.NORMAL_ALIGNMENT) playSpecialMusic(Level.MUSIC_BOSS);
			if (m.pos == bossmobAt) {
				bossMob = m;
				bossMob.isBossMob = !(m instanceof Goo || m instanceof DwarfKing);
				bossMob.addBossProperty();
			}
			if (m.playerAlignment == Mob.NEUTRAL_ALIGNMENT) {
				if (m.state == m.HUNTING) m.state = m.WANDERING;
				m.immunities.add(Amok.class);
			} else if (m.following && m.playerAlignment == Mob.FRIENDLY_ALIGNMENT) m.intelligentAlly = true;
		}
		for (Heap h : heaps.valueList()) {
			h.seen = false;
			for (Item i : h.items) {
				if (i.identifyOnStart && (i instanceof EquipableItem || i instanceof Wand)) i.identify();
				if (i.getClass() == Bomb.class && i.quantity() >= 2) i.image = ItemSpriteSheet.DBL_BOMB;
			}
		}
	}

	public void playLevelMusic(){
		//do nothing by default
	}

	private static final String[][] SPECIAL_MUSIC = {
			{Assets.Music.SEWERS_TENSE, Assets.Music.SEWERS_BOSS, Assets.Music.SEWERS_BOSS},
			{Assets.Music.PRISON_TENSE, Assets.Music.PRISON_BOSS, Assets.Music.PRISON_BOSS},
			{Assets.Music.CAVES_TENSE, Assets.Music.CAVES_BOSS, Assets.Music.CAVES_BOSS_FINALE},
			{Assets.Music.CITY_TENSE, Assets.Music.CITY_BOSS, Assets.Music.CITY_BOSS_FINALE},
			{Assets.Music.HALLS_TENSE, Assets.Music.HALLS_BOSS, Assets.Music.HALLS_BOSS_FINALE}
	};
	public static final int MUSIC_NORMAL = 0, MUSIC_TENSE = 1, MUSIC_BOSS = 2, MUSIC_BOSS_FINAL = 3;
	private int currentVariant;
	public void playLevelMusic(int region, int variant) {
		//variant = 0: normal
		//variant = 1: tense
		//variant = 2: boss normal
		//variant = 3: boss final
		//region = -1: theme final
		if (!musicRequests.isEmpty()) variant = musicRequests.getLast();
		currentVariant = variant;

		boolean hasTransitionToSurface = false;
		for (LevelTransition transition : transitions.values()) {
			if (transition.destLevel.equals(Level.SURFACE)) {
				hasTransitionToSurface = true;
				break;
			}
		}

		if (Statistics.amuletObtained && hasTransitionToSurface || region == -1) {
			Music.INSTANCE.play(Assets.Music.THEME_FINALE, true);
			return;
		}

		if (variant > 0 && variant <= 3) {
			Music.INSTANCE.play(SPECIAL_MUSIC[region-1][variant-1], true);
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

	public int playsMusicFromRegion() {
		return LevelScheme.getRegion(this);
	}

	public void playSpecialMusic(int variant) {
		musicRequests.add(variant);
		if (currentVariant != variant) {
			Music.INSTANCE.end();
			playLevelMusic();
		}
	}

	public void stopSpecialMusic(int variant) {
		musicRequests.removeLastOccurrence(variant);
		int nowPlaying = musicRequests.isEmpty() ? musicVariant : musicRequests.getLast();
		if (nowPlaying != variant) {
			Music.INSTANCE.end();
			playLevelMusic();
		}
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {

		version = bundle.getInt( VERSION );

		setSize( bundle.getInt(WIDTH), bundle.getInt(HEIGHT));
		
		mobs = new HashSet<>();
		heaps = new SparseArray<>();
		blobs = new BlobStoreMap();
		plants = new SparseArray<>();
		traps = new SparseArray<>();
		signs = new SparseArray<>();
		customTiles = new HashSet<>();
		customWalls = new HashSet<>();
		
		map		= bundle.getIntArray( MAP );

		visited	= bundle.getBooleanArray( VISITED );
		mapped	= bundle.getBooleanArray( MAPPED );
		name = bundle.getString( NAME );
		viewDistance = bundle.getInt( VIEW_DISTANCE );
		musicVariant = bundle.getInt( MUSIC_VARIANT );
		int[] intArray = bundle.getIntArray(MUSIC_REQUESTS);
		if (intArray != null) {
			for (int i : intArray)
				musicRequests.add(i);
		}

		Dungeon.customDungeon.getFloor(name).setLevel(this);

		if (bundle.contains(BOSS_MOB_AT)) {
			bossmobAt = bundle.getInt(BOSS_MOB_AT);
			bossFound = bundle.getBoolean(BOSS_FOUND);
		}

		transitions = new HashMap<>();
		for (Bundlable b : bundle.getCollection( TRANSITIONS )){
			transitions.put(((LevelTransition) b).departCell, (LevelTransition) b);
		}

		locked      = bundle.getBoolean( LOCKED );
		
		Collection<Bundlable> collection = bundle.getCollection( HEAPS );
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

		collection = bundle.getCollection( CUSTOM_TILES );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			if(!(vis instanceof CustomTileLoader.UserCustomTile) || ((CustomTileLoader.UserCustomTile) vis).identifier != null)
				customTiles.add(vis);
		}

		collection = bundle.getCollection( CUSTOM_WALLS );
		for (Bundlable p : collection) {
			CustomTilemap vis = (CustomTilemap)p;
			if(!(vis instanceof CustomTileLoader.UserCustomTile) || ((CustomTileLoader.UserCustomTile) vis).identifier != null)
				customWalls.add(vis);
		}
		
		collection = bundle.getCollection( MOBS );
		for (Bundlable m : collection) {
			Mob mob = (Mob)m;
			if (mob != null) {
				mobs.add( mob );
			}
		}
		bossMob = bossMobStatic;
		
		collection = bundle.getCollection( BLOBS );
		for (Bundlable b : collection) {
			Blob blob = (Blob)b;
			blobs.put( blob.getClass(), blob );
		}

		collection = bundle.getCollection( ZONES );
		for (Bundlable b : collection) {
			Zone zone = (Zone) b;
			zoneMap.put( zone.getName(), zone );
		}

		feeling = bundle.getEnum( FEELING, Feeling.class );

		if (bundle.contains( "mobs_to_spawn" )) {
			for (Class<? extends Mob> mob : bundle.getClassArray("mobs_to_spawn")) {
				if (mob != null) mobsToSpawn.add(mob);
			}
		}

		if (bundle.contains( "respawner" )){
			respawner = (Respawner) bundle.get("respawner");
		}

		buildFlagMaps();
		Zone.setupZoneArray(this);
		cleanWalls();

	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( VERSION, Game.versionCode );
		bundle.put( WIDTH, width );
		bundle.put( HEIGHT, height );
		bundle.put( MAP, map );
		bundle.put( NAME, name );
		bundle.put( VISITED, visited );
		bundle.put( MAPPED, mapped );
		bundle.put( TRANSITIONS, transitions.values() );
		bundle.put( LOCKED, locked );
		bundle.put( HEAPS, heaps.valueList() );
		bundle.put( PLANTS, plants.valueList() );
		bundle.put( TRAPS, traps.valueList() );
		bundle.put( SIGNS, signs.valueList() );
		bundle.put( CUSTOM_TILES, customTiles );
		bundle.put( CUSTOM_WALLS, customWalls );
		bundle.put( MOBS, mobs );
		bundle.put( BLOBS, blobs.values() );
		bundle.put( ZONES, zoneMap.values() );
		bundle.put( FEELING, feeling );
		bundle.put( BOSS_MOB_AT, bossmobAt );
		bundle.put( BOSS_FOUND, bossFound );
		bundle.put( "mobs_to_spawn", mobsToSpawn.toArray(new Class[0]));
		bundle.put( "respawner", respawner );
		bundle.put( VIEW_DISTANCE, viewDistance );
		bundle.put(MUSIC_VARIANT, musicVariant);
		int[] intArray = new int[musicRequests.size()];
		int index = 0;
		for (int i : musicRequests) {
			intArray[index] = i;
			index++;
		}
		bundle.put(MUSIC_REQUESTS, intArray);
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
	
	public String tilesTex() {
		return null;
	}
	
	public String waterTex() {
		return null;
	}
	
	abstract protected boolean build();
	
	private ArrayList<Class<?extends Mob>> mobsToSpawn = new ArrayList<>();
	
	public Mob createMob() {
		if (mobsToSpawn == null || mobsToSpawn.isEmpty()) {
			mobsToSpawn = getMobRotation();
			if (mobsToSpawn.isEmpty()) return null;
		}

		Mob m = Reflection.newInstance(mobsToSpawn.remove(0));
		ChampionEnemy.rollForChampion(m);
		return m;
	}

	public ArrayList<Class<? extends Mob>> getMobRotation() {
		return Bestiary.getMobRotation(Dungeon.getSimulatedDepth());
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
                    (transition.type == LevelTransition.Type.REGULAR_ENTRANCE || transition.type == LevelTransition.Type.SURFACE)) {
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
		if (locked){
			return false;
		}

		if (transition.destType == LevelTransition.Type.BRANCH_ENTRANCE) {

			if (transition.destBranch == QuestLevels.MINING.ID) transitionEnterBlacksmithMine(hero, transition);

			return false;
		}

		if (transition.type == LevelTransition.Type.SURFACE){
			if (hero.belongings.getItem( Amulet.class ) == null) {
				Game.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndMessage( Messages.get(hero, "leave") ) );
					}
				});
				return false;
			} else {
				Statistics.ascended = true;
				Badges.silentValidateHappyEnd();
				Dungeon.win( Amulet.class );
				Dungeon.deleteGame( GamesInProgress.curSlot, true );
				Game.switchScene( SurfaceScene.class );
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

		//iron stomach does not persist through chasm falling
		Talent.WarriorFoodImmunity foodImmune = Dungeon.hero.buff(Talent.WarriorFoodImmunity.class);
		if (foodImmune != null) foodImmune.detach();

		//spend the hero's partial turns,  so the hero cannot take partial turns between floors
		Dungeon.hero.spendToWhole();
		for (Char ch : Actor.chars()){
			//also adjust any mobs that are now ahead of the hero due to this
			if (ch.cooldown() < Dungeon.hero.cooldown()){
				ch.spendToWhole();
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
			PathFinder.buildDistanceMap(hero.pos, passable);
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

	public void seal(){
		if (!locked) {
			locked = true;
			Buff.affect(Dungeon.hero, LockedFloor.class);
		}
	}

	public void unseal(){
		if (locked) {
			locked = false;
			if (Dungeon.hero.buff(LockedFloor.class) != null){
				Dungeon.hero.buff(LockedFloor.class).detach();
			}
		}
	}

	public ArrayList<Item> getItemsToPreserveFromSealedResurrect(){
		ArrayList<Item> items = new ArrayList<>();
		for (Heap h : heaps.valueList()){
			if (h.type == Heap.Type.HEAP) items.addAll(h.items);
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

	private Respawner respawner;

	public Actor addRespawner() {
		if (respawner == null){
			respawner = new Respawner();
			Actor.addDelayed(respawner, respawnCooldown());
		} else {
			Actor.add(respawner);
			if (respawner.cooldown() > respawnCooldown()){
				respawner.resetCooldown();
			}
		}
		return respawner;
	}

	public static class Respawner extends Actor {
		{
			actPriority = BUFF_PRIO; //as if it were a buff.
		}

		@Override
		protected boolean act() {

			if (Dungeon.level.mobCount() < Dungeon.level.mobLimit()) {

				if (Dungeon.level.spawnMob(12)){
					spend(Dungeon.level.respawnCooldown());
				} else {
					//try again in 1 turn
					spend(TICK);
				}

			} else {
				spend(Dungeon.level.respawnCooldown());
			}

			return true;
		}

		protected void resetCooldown(){
			spend(-cooldown());
			spend(Dungeon.level.respawnCooldown());
		}
	}

	public float respawnCooldown(){
		if (Statistics.amuletObtained){
			if (Dungeon.getSimulatedDepth() == 1){
				//very fast spawns on floor 1! 0/2/4/6/8/10/12, etc.
				return (Dungeon.level.mobCount()) * (TIME_TO_RESPAWN / 25f);
			} else {
				//respawn time is 5/5/10/15/20/25/25, etc.
				return Math.round(GameMath.gate( TIME_TO_RESPAWN/10f, Dungeon.level.mobCount() * (TIME_TO_RESPAWN / 10f), TIME_TO_RESPAWN / 2f));
			}
		} else if (Dungeon.level.feeling == Feeling.DARK){
			return 2*TIME_TO_RESPAWN/3f;
		} else {
			return TIME_TO_RESPAWN;
		}
	}

	public boolean spawnMob(int disLimit){
		PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(passable, avoid, null));

		Mob mob = createMob();
		mob.state = mob.WANDERING;
		int tries = 30;
		do {
			mob.pos = randomRespawnCell(mob);
			tries--;
		} while ((mob.pos == -1 || PathFinder.distance[mob.pos] < disLimit || !Zone.canSpawnMobs(this, mob.pos)) && tries > 0);

		if (Dungeon.hero.isAlive() && mob.pos != -1 && PathFinder.distance[mob.pos] >= disLimit) {
			GameScene.add( mob );
			if (!mob.buffs(ChampionEnemy.class).isEmpty()){
				GLog.w(Messages.get(ChampionEnemy.class, "warn"));
			}
			return true;
		} else {
			return false;
		}
	}
	
	public int randomRespawnCell( Char ch ) {
		return randomRespawnCell(ch, false);
	}

	public int randomRespawnCell(Char ch, boolean guarantee) {
		boolean checkPath = Dungeon.hero.pos > 0;
		if (checkPath)
			PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(passable, avoid, null));

		//prefer spawning >>>in zones<<< where no mobs can spawn if at least one zone that can spawn mobs exists
		if (ch instanceof Hero) {
			Set<String> safeZonesToSpawn = new HashSet<>(2);
			boolean spawnHeroInSafePlace = false;
			for (Zone z : zoneMap.values()){
				if (z.canSpawnMobs) spawnHeroInSafePlace = true;
				else safeZonesToSpawn.add(z.getName());
			}
			if (spawnHeroInSafePlace && !safeZonesToSpawn.isEmpty()) {
				int cell = 0;
				int count = guarantee ? length() : 300;
				do {
					if (--count < 0) break;
					cell = Random.Int( length() );

				} while ((Dungeon.level == this && heroFOV[cell])
						|| !passable[cell]
						|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
						|| Actor.findChar( cell ) != null
						|| findMob(cell) != null
						|| (checkPath && PathFinder.distance[cell] == Integer.MAX_VALUE)
						|| zone[cell] == null
						|| !safeZonesToSpawn.contains(zone[cell].getName()));
				if (count < 0){
					if(!guarantee) return -1;
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
								&& passable[cell]
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

		} while ((Dungeon.level == this && heroFOV[cell])
				|| !passable[cell]
				|| (Char.hasProp(ch, Char.Property.LARGE) && !openSpace[cell])
				|| Actor.findChar( cell ) != null
				|| (ch instanceof Piranha && map[cell] != Terrain.WATER)
				|| findMob(cell) != null
				|| (checkPath && PathFinder.distance[cell] == Integer.MAX_VALUE)
				|| (!Zone.canSpawnMobs(this, cell) && !(ch instanceof Hero || ch instanceof NPC)));

		return cell;
	}
	
	public int randomDestination( Char ch ) {
		int cell;
		do {
			cell = Random.Int( length() );
		} while (!passable[cell]
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
		}

		for (Blob b : blobs.values()){
			b.onBuildFlagMaps(this);
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

	public boolean isFlamable(int cell) {
		return flamable[cell] && Zone.isFlamable(this, cell);
	}

	public boolean[] getFlamable() {
		return flamable;
	}

	public void setFlamable(boolean[] flamable) {
		this.flamable = flamable;
	}

	public void destroy(int pos ) {
		//if raw tile type is flammable or empty
		int terr = map[pos];
		if (terr == Terrain.EMPTY || terr == Terrain.EMPTY_DECO
				|| (Terrain.flags[map[pos]] & Terrain.FLAMABLE) != 0) {
			set(pos, Terrain.EMBERS);
		}
		blobs.doOnEach(Web.class, b -> b.clear(pos));
	}

	public void cleanWalls() {
		if (discoverable == null || discoverable.length != length) {
			discoverable = new boolean[length()];
		}

		for (int i=0; i < length(); i++) {
			
			boolean d = false;
			
			for (int j=0; j < PathFinder.NEIGHBOURS9.length; j++) {
				int n = i + PathFinder.NEIGHBOURS9[j];
				if (n >= 0 && n < length() && map[n] != Terrain.WALL && map[n] != Terrain.WALL_DECO) {
					d = true;
					break;
				}
			}
			
			discoverable[i] = d;
		}
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
		level.passable[cell]		= (flags & Terrain.PASSABLE) != 0;
		level.losBlocking[cell]	    = (flags & Terrain.LOS_BLOCKING) != 0;
		level.flamable[cell]		= (flags & Terrain.FLAMABLE) != 0;
		level.secret[cell]		    = (flags & Terrain.SECRET) != 0;
		level.solid[cell]			= (flags & Terrain.SOLID) != 0;
		level.avoid[cell]			= (flags & Terrain.AVOID) != 0;
		level.pit[cell]			    = (flags & Terrain.PIT) != 0;
		level.water[cell]			= terrain == Terrain.WATER;

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
			} while (!passable[n] && !avoid[n]);
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

	public void disarmTrap( int pos ) {
		set(pos, Terrain.INACTIVE_TRAP);
		GameScene.updateMap(pos);
	}

	public void discover( int cell ) {
		set( cell, Terrain.discover( map[cell] ) );
		Trap trap = traps.get( cell );
		if (trap != null)
			trap.reveal();
		GameScene.updateMap( cell );
	}

	public boolean setCellToWater( boolean includeTraps, int cell ){
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
		if (terr == Terrain.EMPTY || terr == Terrain.GRASS ||
				terr == Terrain.EMBERS || terr == Terrain.EMPTY_SP ||
				terr == Terrain.HIGH_GRASS || terr == Terrain.FURROWED_GRASS
				|| terr == Terrain.EMPTY_DECO){
			set(cell, Terrain.WATER);
			GameScene.updateMap(cell);
			return true;
		} else if (includeTraps && TileItem.isTrapTerrainCell(terr)){
			set(cell, Terrain.WATER);
			Dungeon.level.traps.remove(cell);
			GameScene.updateMap(cell);
			return true;
		}

		return false;
	}
	
	public int fallCell( boolean fallIntoPit, String destZone ) {
		Dungeon.hero.pos = -1;
		int result;
		do {
			result = randomRespawnCell( null );
			if (result == -1) return -1;
		} while ((destZone != null && (zone[result] == null || !zone[result].getName().equals(destZone)))
				|| traps.get(result) != null
				|| findMob(result) != null);
		return result;
	}
	
	public void occupyCell( Char ch ){
		if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos, Web.class) > 0){
			blobs.doOnEach(Web.class, b -> b.clear(ch.pos));
			Web.affectChar( ch );
		}

		if (!ch.flying){

			if ( (map[ch.pos] == Terrain.GRASS || map[ch.pos] == Terrain.EMBERS)
					&& ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS)
					&& ch.buff(Talent.RejuvenatingStepsCooldown.class) == null){

				if (!Regeneration.regenOn()){
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200) {
					set(ch.pos, Terrain.FURROWED_GRASS);
				} else {
					set(ch.pos, Terrain.HIGH_GRASS);
					Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
				}
				GameScene.updateMap(ch.pos);
				Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15f - 5f*Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
			}
			
			if (pit[ch.pos]){
				if (ch == Dungeon.hero) {
					Chasm.heroFall(ch.pos);
				} else if (ch instanceof Mob) {
					Chasm.mobFall( (Mob)ch );
				}
				return;
			}
			
			//characters which are not the hero or a sheep 'soft' press cells
			if (!CustomDungeon.isEditing())
				pressCell(ch.pos, ch instanceof Hero || ch instanceof Sheep);
		} else {
			if (map[ch.pos] == Terrain.DOOR){
				Door.enter( ch.pos );
			}
		}

		if (ch.isAlive() && ch instanceof Piranha && !water[ch.pos] && !CustomDungeon.isEditing()){
			((Piranha) ch).dieOnLand();
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
						&& c.buff( TimekeepersHourglass.timeStasis.class ) == null && c.isAlive();
		if (sighted) {
			boolean[] blocking = null;

			if (modifiableBlocking == null || modifiableBlocking.length != Dungeon.level.losBlocking.length){
				modifiableBlocking = new boolean[Dungeon.level.losBlocking.length];
			}
			
			if ((c instanceof Hero && ((Hero) c).subClass == HeroSubClass.WARDEN)
				|| c instanceof YogFist.SoiledFist) {
				if (blocking == null) {
					System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
					blocking = modifiableBlocking;
				}
				for (int i = 0; i < blocking.length; i++){
					if (blocking[i] && (Dungeon.level.map[i] == Terrain.HIGH_GRASS || Dungeon.level.map[i] == Terrain.FURROWED_GRASS)){
						blocking[i] = false;
					}
				}
			}

			if (c.alignment != Char.Alignment.ALLY
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
			
			int viewDist = c.viewDistance;
			if (c instanceof Hero){
				viewDist *= 1f + 0.25f*((Hero) c).pointsInTalent(Talent.FARSIGHT);
			}
			
			ShadowCaster.castShadow( cx, cy, fieldOfView, blocking, viewDist );
		} else {
			BArray.setFalse(fieldOfView);
		}
		
		int sense = 1;
		//Currently only the hero can get mind vision
		if (c.isAlive() && c == Dungeon.hero) {
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

		//Currently only the hero can get mind vision or awareness
		if (c.isAlive() && c == Dungeon.hero) {

			if (heroMindFov == null || heroMindFov.length != length()){
				heroMindFov = new boolean[length];
			} else {
				BArray.setFalse(heroMindFov);
			}

			Dungeon.hero.mindVisionEnemies.clear();
			if (c.buff( MindVision.class ) != null) {
				for (Mob mob : mobs) {
					for (int i : PathFinder.NEIGHBOURS9) {
						heroMindFov[mob.pos + i] = true;
					}
				}
			} else if (((Hero) c).hasTalent(Talent.HEIGHTENED_SENSES)) {
				Hero h = (Hero) c;
				int range = 1+h.pointsInTalent(Talent.HEIGHTENED_SENSES);
				for (Mob mob : mobs) {
					int p = mob.pos;
					if (!fieldOfView[p] && distance(c.pos, p) <= range) {
						for (int i : PathFinder.NEIGHBOURS9) {
							heroMindFov[mob.pos + i] = true;
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
						|| m instanceof SpiritHawk.HawkAlly){
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
				if (heroMindFov[mob.pos] && (!fieldOfView[mob.pos] || mob.invisible > 0)){
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
			case Terrain.DOOR:
				return Messages.get(Level.class, "closed_door_name");
			case Terrain.OPEN_DOOR:
				return Messages.get(Level.class, "open_door_name");
			case Terrain.ENTRANCE:
				return Messages.get(Level.class, "entrace_name");
			case Terrain.EXIT:
				return Messages.get(Level.class, "exit_name");
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_name");
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "furrowed_grass_name");
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
				return Messages.get(Level.class, "entrance_desc") + appendNoTransWarning(cell);
			case Terrain.EXIT:
			case Terrain.UNLOCKED_EXIT:
				return Messages.get(Level.class, "exit_desc") + appendNoTransWarning(cell);
			case Terrain.EMBERS:
				return Messages.get(Level.class, "embers_desc");
			case Terrain.HIGH_GRASS:
			case Terrain.FURROWED_GRASS:
				return Messages.get(Level.class, "high_grass_desc");
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
			default:
				return "";
		}
	}

	public String appendNoTransWarning(int cell) {
		return cell >= 0 && transitions.get(cell) == null ? "\n" + Messages.get(Hero.class, "no_trans_warning") : "";
	}
}