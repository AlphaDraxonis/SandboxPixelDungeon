package com.shatteredpixel.shatteredpixeldungeon.editor.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.TransitionEditPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelScheme implements Bundlable, Comparable<LevelScheme> {

    public static final LevelScheme SURFACE_LEVEL_SCHEME = new LevelScheme();//Placeholder for selecting levels
    public static final LevelScheme NO_LEVEL_SCHEME = new LevelScheme();//Placeholder for selecting levels

    static {
        SURFACE_LEVEL_SCHEME.name = Level.SURFACE;
        NO_LEVEL_SCHEME.name = Level.NONE;
    }


    private String name;
    CustomDungeon customDungeon;

    private int depth;
    private String chasm = null, passage;
    private LevelTransition entranceTransitionRegular, exitTransitionRegular;//only for type Regular

    private Class<? extends Level> type;
    private Level level;
    private int numInRegion = 1;
    private Level.Feeling feeling;
    private int shopPriceMultiplier = 1;

    private long seed;
    private boolean seedSet = false;
    public List<Integer> entranceCells, exitCells;


    public LevelScheme() {
    }

    //Wrapper for creating templates
    LevelScheme(Level level, int numInRegion) {
        this.name = Level.NONE;
        this.level = level;
        this.numInRegion = numInRegion;
    }

    public LevelScheme(String name, Class<? extends Level> levelType, Class<? extends Level> levelTemplate, Long seed, Level.Feeling feeling, int numInRegion, int depth) {
        this.name = name;
        type = levelType;
        this.feeling = feeling;
        this.numInRegion = numInRegion;
        this.depth = depth;
        exitCells = new ArrayList<>(3);
        entranceCells = new ArrayList<>(3);
        if (type == CustomLevel.class) {
            level = new CustomLevel(name, levelTemplate, feeling, seed, numInRegion, depth, this);
        } else {
            initExitEntranceCellsForRandomLevel();
        }
        shopPriceMultiplier = Dungeon.getSimulatedDepth(this) / 5 + 1;
        if (seed != null) setSeed(seed);
    }

    public LevelScheme(String name, int depth, CustomDungeon customDungeon) {
        this.name = name;
        this.depth = depth;
        this.customDungeon = customDungeon;
        if (depth < 26) setChasm(Integer.toString(depth + 1));

        switch (depth) {
            case 1:
            case 2:
            case 3:
            case 4:
                type = SewerLevel.class;
                break;
            case 5:
                type = SewerBossLevel.class;
                customDungeon.addRatKingLevel(name);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
                type = PrisonLevel.class;
                break;
            case 10:
                type = PrisonBossLevel.class;
                break;
            case 11:
            case 12:
            case 13:
            case 14:
                type = CavesLevel.class;
                break;
            case 15:
                type = CavesBossLevel.class;
                break;
            case 16:
            case 17:
            case 18:
            case 19:
                type = CityLevel.class;
                break;
            case 20:
                type = CityBossLevel.class;
                break;
            case 21:
            case 22:
            case 23:
            case 24:
                type = HallsLevel.class;
                break;
            case 25:
                type = HallsBossLevel.class;
                break;
            case 26:
                type = LastLevel.class;
                break;
            default:
                type = DeadEndLevel.class;
        }
        numInRegion = (depth - 1) % 5 + 1;
         if (depth > 1 && depth < 5) {
            customDungeon.addMaybeGhostSpawnLevel(name);
        }
       else if (depth > 6 && depth < 10) {
            customDungeon.addMaybeWandmakerSpawnLevel(name);
        }
       else if (depth > 11 && depth < 15) {
            customDungeon.addMaybeBlacksmithSpawnLevel(name);
        }
        else if (depth > 16 && depth < 20) {
            customDungeon.addMaybeImpSpawnLevel(name);
        }

        shopPriceMultiplier = depth / 5 + 1;
        passage = Integer.toString(((depth - 1) / 5) * 5 + 1);

        exitCells = new ArrayList<>(3);
        entranceCells = new ArrayList<>(3);
        initExitEntranceCellsForRandomLevel();
        if (depth > 1) {
            entranceTransitionRegular.destLevel = Integer.toString(depth - 1);
        }
        exitTransitionRegular.destLevel = Integer.toString(depth + 1);
    }

    private void initExitEntranceCellsForRandomLevel() {
        if (type != DeadEndLevel.class && type != LastLevel.class)
            exitCells.add(TransitionEditPart.DEFAULT);
        else exitCells.add(TransitionEditPart.NONE);
        entranceCells.add(TransitionEditPart.DEFAULT);
        entranceTransitionRegular = new LevelTransition(Level.SURFACE, -1);
        exitTransitionRegular = new LevelTransition(null, -1);
    }

    public String getName() {
        return name;
    }

    public int getDepth() {
        return depth;
    }

    public String getChasm() {
        return chasm;
    }

    public String getDefaultAbove() {
        if (entranceTransitionRegular == null) return Level.SURFACE;
        return entranceTransitionRegular.destLevel;
    }

    public String getDefaultBelow() {
        if (exitTransitionRegular == null) return null;
        return exitTransitionRegular.destLevel;
    }

    public LevelTransition getEntranceTransitionRegular() {
        return entranceTransitionRegular;
    }

    public LevelTransition getExitTransitionRegular() {
        return exitTransitionRegular;
    }

    public String getPassage() {//Passage entry
        if (passage == null) return Dungeon.customDungeon.getStart();
        return passage;
    }

    public Class<? extends Level> getType() {
        return type;
    }

    public Level getLevel() {
        return level;
    }

    public Level.Feeling getFeeling() {
        return feeling;
    }

    public boolean hasBoss() {
        return getBoss() != REGION_NONE;
    }

    public boolean hasShop() {
        int depth = Dungeon.getSimulatedDepth(this);
        return depth == 6 || depth == 11 || depth == 16;
    }

    public int getPriceMultiplier() {
        return shopPriceMultiplier;
    }

    public void setChasm(String chasm) {
        this.chasm = chasm;
    }

    public void setPassage(String passage) {
        this.passage = passage;
    }

    public void setEntranceTransitionRegular(LevelTransition entranceTransitionRegular) {
        this.entranceTransitionRegular = entranceTransitionRegular;
    }

    public void setExitTransitionRegular(LevelTransition exitTransitionRegular) {
        this.exitTransitionRegular = exitTransitionRegular;
    }

    public void setLevel(Level level) {
        this.level = level;
        type = level.getClass();
        level.levelScheme = this;
    }

    public void setFeeling(Level.Feeling feeling) {
        this.feeling = feeling;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setShopPriceMultiplier(int shopPriceMultiplier) {
        this.shopPriceMultiplier = shopPriceMultiplier;
    }

    public Level initLevel() {
        if (type != CustomLevel.class) {
            Random.pushGenerator(seed);
            Dungeon.levelName = name;
            level = Reflection.newInstance(type);
            Dungeon.level = level;
            level.name = name;
            level.levelScheme = this;
            level.feeling = feeling;
            level.create();
            Random.popGenerator();
        } else {
            if (level == null) {
                try {
                    level = CustomDungeonSaves.loadLevel(name);
                } catch (IOException e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            } else
                level = ((CustomLevel) level).createCopiedFloor();//make sure the levels are different objects? FIXME maybe not needed?? WICHTIG
            level.levelScheme = this;
            level.name = name;
            Dungeon.level = level;
            Dungeon.levelName = name;
        }
        return level;
    }


    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        seedSet = true;
    }

    public void initSeed() {
        if (!seedSet) {
            int lookAhead = depth;
            if (name.equals(String.valueOf(depth))) lookAhead += name.hashCode() % 100;

            for (int i = 0; i < lookAhead; i++) {
                Random.Long(); //we don't care about these values, just need to go through them
            }
            seed = Random.Long();
        }
    }


    private static final String NAME = "name";
    private static final String ENTRANCE_CELLS = "entrance_cells";
    private static final String EXIT_CELLS = "exit_cells";
    private static final String CHASM = "chasm";
    private static final String DEST_ENTRANCE_REGULAR = "dest_entrance_regular";
    private static final String DEST_EXIT_REGULAR = "dest_exit_regular";
    private static final String PASSAGE = "passage";
    private static final String DEPTH = "depth";
    private static final String TYPE = "type";
    private static final String NUM_IN_REGION = "num_in_region";
    private static final String FEELING = "feeling";
    private static final String SEED = "seed";
    private static final String SEED_SET = "seed_set";
    private static final String SHOP_PRICE_MULTIPLIER = "shop_price_multiplier";

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(NAME, name);
        bundle.put(CHASM, chasm);
        if (entranceTransitionRegular != null)
            bundle.put(DEST_ENTRANCE_REGULAR, entranceTransitionRegular);
        if (exitTransitionRegular != null) bundle.put(DEST_EXIT_REGULAR, exitTransitionRegular);
        bundle.put(PASSAGE, passage);
        bundle.put(DEPTH, depth);
        bundle.put(TYPE, type);
        bundle.put(NUM_IN_REGION, numInRegion);
        bundle.put(SEED, seed);
        bundle.put(SEED_SET, seedSet);
        bundle.put(FEELING, feeling);
        bundle.put(SHOP_PRICE_MULTIPLIER, shopPriceMultiplier);

        int[] entrances = new int[entranceCells.size()];
        int i = 0;
        for (int cell : entranceCells) {
            entrances[i] = cell;
            i++;
        }
        bundle.put(ENTRANCE_CELLS, entrances);
        int[] exits = new int[exitCells.size()];
        i = 0;
        for (int cell : exitCells) {
            exits[i] = cell;
            i++;
        }
        bundle.put(EXIT_CELLS, exits);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        name = bundle.getString(NAME);
        chasm = bundle.getString(CHASM);
        if (bundle.contains(DEST_ENTRANCE_REGULAR))
            entranceTransitionRegular = (LevelTransition) bundle.get(DEST_ENTRANCE_REGULAR);
        if (bundle.contains(DEST_EXIT_REGULAR))
            exitTransitionRegular = (LevelTransition) bundle.get(DEST_EXIT_REGULAR);
        passage = bundle.getString(PASSAGE);
        depth = bundle.getInt(DEPTH);
        if (bundle.contains(FEELING)) feeling = bundle.getEnum(FEELING, Level.Feeling.class);
        shopPriceMultiplier = bundle.getInt(SHOP_PRICE_MULTIPLIER);

        int[] entrances = bundle.getIntArray(ENTRANCE_CELLS);
        entranceCells = new ArrayList<>(entrances.length + 1);
        for (int entrance : entrances) entranceCells.add(entrance);

        int[] exits = bundle.getIntArray(EXIT_CELLS);
        exitCells = new ArrayList<>(exits.length + 1);
        for (int exit : exits) exitCells.add(exit);

        type = bundle.getClass(TYPE);
        numInRegion = bundle.getInt(NUM_IN_REGION);
        seed = bundle.getLong(SEED);
        seedSet = bundle.getBoolean(SEED_SET);
    }

    public Level loadLevel() {
        if (type == CustomLevel.class) {
            try {
                level = CustomDungeonSaves.loadLevel(name);
                level.levelScheme = this;
            } catch (IOException e) {
                ShatteredPixelDungeon.reportException(e);
            }
        }
        return level;
    }

    public void saveLevel() throws IOException {
        if (type == CustomLevel.class) CustomDungeonSaves.saveLevel(level);
    }

    public void unloadLevel() {
        level = null;
    }


    public static final int REGION_NONE = 0, REGION_SEWERS = 1, REGION_PRISON = 2, REGION_CAVES = 3, REGION_CITY = 4, REGION_HALLS = 5;

    public static int getRegion(Level level) {
        if (level instanceof CustomLevel) return ((CustomLevel) level).region;
        if (level instanceof SewerLevel) return REGION_SEWERS;
        if (level instanceof PrisonLevel || level instanceof PrisonBossLevel) return REGION_PRISON;
        if (level instanceof CavesLevel || level instanceof CavesBossLevel) return REGION_CAVES;
        if (level instanceof CityLevel || level instanceof CityBossLevel) return REGION_CITY;
        if (level instanceof HallsLevel || level instanceof HallsBossLevel || level instanceof LastLevel || level instanceof DeadEndLevel)
            return REGION_HALLS;
        return REGION_NONE;
    }

    public static int getRegion(Class<? extends Level> level) {
        if (level == SewerLevel.class || level == SewerBossLevel.class) return REGION_SEWERS;
        if (level == PrisonLevel.class || level == PrisonBossLevel.class) return REGION_PRISON;
        if (level == CavesLevel.class || level == CavesBossLevel.class) return REGION_CAVES;
        if (level == CityLevel.class || level == CityBossLevel.class) return REGION_CITY;
        if (level == HallsLevel.class || level == HallsBossLevel.class || level == LastLevel.class || level == DeadEndLevel.class)
            return REGION_HALLS;
        return REGION_NONE;
    }

    public static int getBoss(Class<? extends Level> level) {
        if (level == SewerBossLevel.class) return REGION_SEWERS;
        if (level == PrisonBossLevel.class) return REGION_PRISON;
        if (level == CavesBossLevel.class) return REGION_CAVES;
        if (level == CityBossLevel.class) return REGION_CITY;
        if (level == HallsBossLevel.class) return REGION_HALLS;
        return REGION_NONE;
    }

    public final int getRegion() {
        if (level == null) return getRegion(type);
        return getRegion(level);
    }

    public final int getBoss() {
//        if (level instanceof Floor) return ((Floor) level).region;
        return getBoss(level == null ? type : level.getClass());
    }

    public int getNumInRegion() {//if it is the first, second, 3rd etc floor of a region (only good for setting default playing)
        return numInRegion;
    }

    @Override
    public int compareTo(LevelScheme o) {
        if (getDepth() != o.getDepth())
            return (getDepth() - o.getDepth()) * 1000;//TODO might wanna use more complex sorting, like used for ItemTab
        return getName().compareTo(o.getName());
    }

    //Depth nur für Scaling, Region und numInRegionFür LevelGen
}