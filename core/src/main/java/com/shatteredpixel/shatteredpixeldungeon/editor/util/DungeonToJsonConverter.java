package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.RandomItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.watabou.utils.IntFunction;

import java.util.*;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.*;

public class DungeonToJsonConverter {


    public static String getAsJson(CustomDungeon dungeon) {
        Dungeon.customDungeon = dungeon;

        StringBuilder b = new StringBuilder();

        List<String> floors = new ArrayList<>(dungeon.floorNames());
        b.append('{');
        appendParam(b, "start", dungeon.getStart());
        //TODO bones (if bones can spawn)


        appendListHead(b, "dungeon");

        for (String f : floors) {
            appendFloor(b, dungeon.getFloor(f));
        }
        if (!floors.isEmpty()) {
            removeLastChars(b, 2);
        }
        b.append("},\n");

        String ratKingLevel = dungeon.getAnyRatKingLevel();
        if (ratKingLevel != null) appendParam(b, "rat_king_level", ratKingLevel);
        appendArray(b, "ghost_spawn_levels", new String[]{Level.SURFACE});
        appendArray(b, "wandmaker_spawn_levels", new String[]{Level.SURFACE});
        appendArray(b, "blacksmith_spawn_levels", new String[]{Level.SURFACE});
        appendArray(b, "imp_spawn_levels", new String[]{Level.SURFACE});

        boolean countsAsDefault = true;
        Collection<String> floorNames = dungeon.floorNames();
        for (int i = 1; i <= 24; i++) {
            if (!floorNames.contains(Integer.toString(i))) {
                countsAsDefault = false;
                break;
            }
        }
        if (countsAsDefault) {
            appendDefaultDistribution(b);
        } else {
            appendEmptyArray(b, "pos_distribution");
            appendEmptyArray(b, "sou_distribution");
            appendEmptyArray(b, "as_distribution");
            removeLastChars(b, 2);
        }


        b.append("}");

        return b.toString();
    }

    private static void appendFloor(StringBuilder b, LevelScheme l) {

        CustomLevel f = null;
        if (l.getType() == CustomLevel.class){
            f = (CustomLevel) l.loadLevel();
            if (f == null) return;
        }

        appendListHead(b, l.getName());
        appendParam(b, "depth", l.getDepth());
        appendParam(b, "type", l.getType() == CustomLevel.class ? "custom" : "regular");

        List<Integer> entranceCells = null;
        List<Integer> exitCells = null;
        Map<Integer, LevelTransition> levelTransitions = null;
        if (l.getType() == CustomLevel.class) {
            appendListHead(b, "custom_layout");
            int width = f.width();
            int height = f.height();
            appendParam(b, "width", width);
            appendParam(b, "height", height);
            appendParam(b, "region", l.getRegion());//1 to 5   is value??!
            appendParam(b, "view_distance", f.viewDistance);
            appendMusic(b, f.getVisualRegionValue());
            appendArrayReplace(b, "map", f.map, Terrain.SIGN_SP, Terrain.SIGN);

            appendArrayHead(b, "entrances");
            entranceCells = l.entranceCells;
            Iterator<Integer> transitionCellIterator = entranceCells.listIterator();
            while (transitionCellIterator.hasNext()) {
                int cell = transitionCellIterator.next();
                if (f.transitions.containsKey(cell)) {
                    appendCoord(b, cell, width);
                    if (transitionCellIterator.hasNext()) b.append(',');
                }
            }
            b.append("],\n");

            appendArrayHead(b, "exits");
            exitCells = l.exitCells;
            transitionCellIterator = exitCells.listIterator();
            while (transitionCellIterator.hasNext()) {
                int cell = transitionCellIterator.next();
                if (f.transitions.containsKey(cell)) {
                    appendCoord(b, cell, width);
                    if (transitionCellIterator.hasNext()) b.append(',');
                }
            }
            b.append("],\n");

            appendComplexArray(b, "items", f.heaps.valueList(), h -> h.peek() != null,
                    obj -> appendItemValues(b, obj, width), null, 0);

            //TODO shuffle_items

            appendComplexArray(b, "mobs", f.mobs, m -> m != null,
                    obj -> appendMobValues(b, obj), m -> m.pos, width);

            appendComplexArray(b, "open_traps", f.traps.values(), t -> t.visible && t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width);

            appendComplexArray(b, "hidden_traps", f.traps.values(), t -> !t.visible && t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width);

            appendComplexArray(b, "disarmed_traps", f.traps.values(), t -> !t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width);

            appendComplexArray(b, "plants", f.plants.values(), p -> p != null,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), p -> p.pos, width);

            removeLastChars(b, 2);


            b.append("},");

            levelTransitions = f.transitions;
        } else {
            appendParam(b, "layout", l.getType().getSimpleName());
        }

        appendArrayHead(b, "entrances");
        if (l.getType() != CustomLevel.class) {
            String dest = l.getDefaultAbove();
            if (dest != null && !dest.isEmpty()) b.append("\"").append(dest).append("\"");
        } else {
            b.append(" ");//so we don't accidentally remove important chars
            for (int cell : entranceCells) {
                LevelTransition t = levelTransitions.get(cell);
                if (t != null) b.append("\"").append(t.destLevel).append("\",");
            }
            removeLastChars(b, 1);
        }
        b.append("],\n");

        appendArrayHead(b, "exits");
        if (l.getType() != CustomLevel.class) {
            String dest = l.getDefaultBelow();
            if (dest != null && !dest.isEmpty()) b.append("\"").append(dest).append("\"");
            else b.append("\"").append(l.getName()).append("\"");
        } else {
            b.append(" ");//so we don't accidentally remove important chars
            for (int cell : exitCells) {
                LevelTransition t = levelTransitions.get(cell);
                if (t != null) b.append("\"").append(t.destLevel).append("\",");
            }
            removeLastChars(b, 1);
        }
        b.append("],\n");

        String chasm = l.getChasm();
        if (chasm == null || chasm.isEmpty()) chasm = l.getDefaultBelow();
        appendParam(b, "chasm", chasm);
        if (l.getBoss() != LevelScheme.REGION_NONE) appendParam(b, "boss", true);
        String passage = l.getPassage();
        if (passage == null || passage.isEmpty()) passage = l.getDefaultAbove();
        appendParam(b, "passage", passage);
//        appendParam(b, "locked", "false");//TODO
        //spawner_cooldown
        if (f == null) {
            for (Room r : l.roomsToSpawn) {
                if (r instanceof ShopRoom) {
                    appendParam(b, "shop", true);
                    break;
                }
            }
        }
        appendParam(b, "price_multiplier", (int) Math.min(1, l.shopPriceMultiplier));
        //TODO visibility
        //TODO trap_detection
        //TODO door_detection
        if (f == null && l.getFeeling() != Level.Feeling.NONE && l.getFeeling() != null) //TODO why is this "unknown key"?
            appendParam(b, "level_feeling", l.getFeeling().toString().toLowerCase(Locale.ENGLISH));

        appendComplexArray(b, "extra_items", l.itemsToSpawn, i -> i != null,
                obj -> appendValueOneItem(b, obj, Heap.Type.HEAP, -1, 0));

        appendComplexArray(b, "extra_mobs", l.mobsToSpawn, m -> m != null,
                obj -> appendMobValues(b, obj));

        if (f == null) {
            Class[] rot = MobSpawner.standardMobRotation(Dungeon.getSimulatedDepth(l)).toArray(new Class[0]);
            appendArray(b, "bestiary", rot);
        }
        //else appendArray(b, "bestiary", f.getMobRotationVar().toArray(new Class[0]));

        if (f == null) {
            if (Dungeon.getSimulatedDepth(l) == 4) appendParam(b, "rare_mob", "Thief");
            if (Dungeon.getSimulatedDepth(l) == 9) appendParam(b, "rare_mob", "Bat");
            if (Dungeon.getSimulatedDepth(l) == 14) appendParam(b, "rare_mob", "Ghoul");
            if (Dungeon.getSimulatedDepth(l) == 19) appendParam(b, "rare_mob", "Succubus");
        }

        removeLastChars(b, 2);


        b.append("},\n");

    }

    private static void appendParam(StringBuilder b, String paramName, int value) {
        b.append("\"").append(paramName).append("\": ").append(value).append(",\n");
    }

    private static void appendParam(StringBuilder b, String paramName, boolean value) {
        b.append("\"").append(paramName).append("\": ").append(value).append(",\n");
    }

    private static void appendParam(StringBuilder b, String paramName, String value) {
        b.append("\"").append(paramName).append("\": ").append("\"").append(value).append("\"").append(",\n");
    }

    private static void appendParamEnd(StringBuilder b, String paramName, String value) {
        b.append("\"").append(paramName).append("\": ").append("\"").append(value).append("\"");
    }

    private static void appendArray(StringBuilder b, String name, int[] value) {
        appendArrayHead(b, name);
        for (int i : value) {
            b.append(i).append(", ");
        }
        if (value.length > 0) {
            removeLastChars(b, 2);
        }
        b.append("],\n");
    }

    private static void appendArrayReplace(StringBuilder b, String name, int[] value, int oldValue, int newValue) {
        appendArrayHead(b, name);
        for (int i : value) {
            if (i == oldValue) i = newValue;
            b.append(i).append(", ");
        }
        if (value.length > 0) {
            removeLastChars(b, 2);
        }
        b.append("],\n");
    }

    private static void appendArray(StringBuilder b, String name, String[] value) {
        appendArrayHead(b, name);
        for (String i : value) {
            b.append("\"").append(i).append("\"").append(", ");
        }
        if (value.length > 0) {
            removeLastChars(b, 2);
        }
        b.append("],\n");
    }

    private static <T> void appendComplexArray(StringBuilder b, String name, Iterable<T> list, Predicate<T> validityCheck,
                                               Consumer<T> appendObject, IntFunction<T> getPos, int width) {
        appendArrayHead(b, name);
        b.append("  ");
        for (T obj : list) {
            if (validityCheck.test(obj)) {
                b.append("{");
                if (getPos != null) {
                    appendCoordWithoutBrackets(b, getPos.apply(obj), width);
                    b.append(",");
                }
                appendObject.accept(obj);
                b.append("},\n");
            }
        }
        removeLastChars(b, 2);
        b.append("],\n");
    }

    private static <T> void appendComplexArray(StringBuilder b, String name, Iterable<T> list, Predicate<T> validityCheck,
                                               Consumer<T> appendObject) {
        appendArrayHead(b, name);
        b.append("  ");
        for (T obj : list) {
            if (validityCheck.test(obj)) {
                b.append("{");
                appendObject.accept(obj);
                b.append("},\n");
            }
        }
        removeLastChars(b, 2);
        b.append("],\n");
    }

    private static void appendArray(StringBuilder b, String name, Class[] value) {
        String[] asString = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            asString[i] = value[i].getSimpleName();
        }
        appendArray(b, name, asString);
    }

    private static void appendListHead(StringBuilder b, String name) {
        b.append("\"").append(name).append("\": {\n");
    }

    private static void appendArrayHead(StringBuilder b, String name) {
        b.append("\"").append(name).append("\": [\n");
    }

    private static void appendEmptyArray(StringBuilder b, String name) {
        b.append("\"").append(name).append("\": [],\n");
    }

    private static void appendCoord(StringBuilder b, int cell, int width) {
        b.append("{");
        appendCoordWithoutBrackets(b, cell, width);
        b.append("}\n");
    }

    private static void appendCoordWithoutBrackets(StringBuilder b, int cell, int width) {
        b.append("\"x\": ").append(cell % width).append(", \"y\": ").append(cell / width).append("\n");
    }

    private static final int PACKAGE_NAME_ITEMS_LENGTH = Messages.PACKAGE_NAME_LENGTH + "items.".length();
    private static final int PACKAGE_NAME_SEEDS_LENGTH = Messages.PACKAGE_NAME_LENGTH /*+ "plants.".length()*/;
    private static final int PACKAGE_NAME_MOBS_LENGTH = Messages.PACKAGE_NAME_LENGTH + "actors.mobs.".length();
    private static final int PACKAGE_NAME_WEAPON_LENGTH = PACKAGE_NAME_ITEMS_LENGTH + "weapon.".length();
    private static final int PACKAGE_NAME_ARMOR_LENGTH = PACKAGE_NAME_ITEMS_LENGTH + "armor.".length();

    private static void appendItemValues(StringBuilder b, Heap heap, int width) {
        boolean isFirst = true;
        for (Item i : heap.items) {
            if (!isFirst) b.append("},\n{");
            if (i != null) appendValueOneItem(b, i, heap.type, heap.pos, width);
            isFirst = false;
        }
    }

    private static void appendValueOneItem(StringBuilder b, Item i, Heap.Type heapType, int pos, int width) {
        if (i instanceof RandomItem || i instanceof BeaconOfReturning || i instanceof LloydsBeacon) return;

        appendParam(b, "type", getShortenFullClassName(i,
                (i instanceof Plant.Seed ? PACKAGE_NAME_SEEDS_LENGTH : PACKAGE_NAME_ITEMS_LENGTH)));
        //TODO category (then instead of type)
        //TODO ignore_deck (only with category)
        appendParam(b, "heap_type", heapType.toString());
        if (pos >= 0) {
            appendCoordWithoutBrackets(b, pos, width);
            b.append(',');
        }
        if (i.quantity() > 1) appendParam(b, "quantity", i.quantity());
        if (i.randQuantMin >= 0) appendParam(b, "quantity_min", i.randQuantMin);
        if (i.randQuantMax >= 0) appendParam(b, "quantity_max", i.randQuantMax);
        if (i.level() > 0) appendParam(b, "level", i.level());
        if (i instanceof Weapon) {
            if (((Weapon) i).enchantment != null) {
                appendParam(b, "enchantment", getShortenFullClassName(((Weapon) i).enchantment, PACKAGE_NAME_WEAPON_LENGTH));
            }
        } else if (i instanceof Armor) {
            if (((Armor) i).glyph != null) {
                appendParam(b, "enchantment", getShortenFullClassName(((Armor) i).glyph, PACKAGE_NAME_ARMOR_LENGTH));
            }
        }
        //TODO identified
        if (i.cursed)
            appendParam(b, "cursed", true);//TODO can override randomly generated curses if set to false
        if (i instanceof Key && !Level.ANY.equals(((Key) i).levelName)) {//maybe also look inside mob containers?
            appendParam(b, "level_name", ((Key) i).levelName);
        }
        removeLastChars(b, 2);
    }

    private static void appendMobValues(StringBuilder b, Mob m) {

        Mob defaultValue = DefaultStatsCache.getDefaultObject(m.getClass());

        appendParam(b, "type", getShortenFullClassName(m, PACKAGE_NAME_MOBS_LENGTH));
       if(defaultValue!=null&& defaultValue.HT != m.HT) appendParam(b, "hp", m.HT);
        //TODO alignment
        appendParam(b, "ai_state", m.state.getClass().getSimpleName().toLowerCase(Locale.ENGLISH));

        for (Buff buff : m.buffs()) {
            if (buff instanceof ChampionEnemy) {
                appendParam(b, "champion", buff.getClass().getSimpleName());
                break;
            }
        }
        removeLastChars(b, 2);
    }

    private static void appendDefaultDistribution(StringBuilder b) {

        //TODO actual item distribution

        b.append("\"pos_distribution\": [\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"1\",\n" +
                "                    \"2\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"3\",\n" +
                "                    \"4\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"6\",\n" +
                "                    \"7\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"8\",\n" +
                "                    \"9\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"11\",\n" +
                "                    \"12\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"13\",\n" +
                "                    \"14\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"16\",\n" +
                "                    \"17\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"18\",\n" +
                "                    \"19\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"21\",\n" +
                "                    \"22\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"23\",\n" +
                "                    \"24\"\n" +
                "      ]\n" +
                "        }\n" +
                "  ],\n" +
                "        \"sou_distribution\": [\n" +
                "        {\n" +
                "            \"quantity\": 3,\n" +
                "                \"levels\": [\n" +
                "            \"1\",\n" +
                "                    \"2\",\n" +
                "                    \"3\",\n" +
                "                    \"4\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 3,\n" +
                "                \"levels\": [\n" +
                "            \"6\",\n" +
                "                    \"7\",\n" +
                "                    \"8\",\n" +
                "                    \"9\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 3,\n" +
                "                \"levels\": [\n" +
                "            \"11\",\n" +
                "                    \"12\",\n" +
                "                    \"13\",\n" +
                "                    \"14\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 3,\n" +
                "                \"levels\": [\n" +
                "            \"16\",\n" +
                "                    \"17\",\n" +
                "                    \"18\",\n" +
                "                    \"19\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 3,\n" +
                "                \"levels\": [\n" +
                "            \"21\",\n" +
                "                    \"22\",\n" +
                "                    \"23\",\n" +
                "                    \"24\"\n" +
                "      ]\n" +
                "        }\n" +
                "  ],\n" +
                "        \"as_distribution\": [\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"1\",\n" +
                "                    \"2\",\n" +
                "                    \"3\",\n" +
                "                    \"4\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"6\",\n" +
                "                    \"7\",\n" +
                "                    \"8\",\n" +
                "                    \"9\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"11\",\n" +
                "                    \"12\",\n" +
                "                    \"13\",\n" +
                "                    \"14\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"16\",\n" +
                "                    \"17\",\n" +
                "                    \"18\",\n" +
                "                    \"19\"\n" +
                "      ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"quantity\": 1,\n" +
                "                \"levels\": [\n" +
                "            \"21\",\n" +
                "                    \"22\",\n" +
                "                    \"23\",\n" +
                "                    \"24\"\n" +
                "      ]\n" +
                "        }\n" +
                "  ]");

    }

    private static String getShortenFullClassName(Object obj, int startIndex) {
        return obj.getClass().getName().substring(startIndex);
    }

    private static void removeLastChars(StringBuilder b, int num) {
        int length = b.length();
        b.delete(length - num, length);
    }


    private static void appendMusic(StringBuilder b, int region) {
        String[] tracks;
        int[] probs = {1, 1, 1};
        switch (region) {//don't add 2.2.0 music yet
            case REGION_SEWERS:
//                tracks = SewerLevel.SEWER_TRACK_LIST;
                tracks = new String[]{Assets.Music.SEWERS_1, Assets.Music.SEWERS_2, Assets.Music.SEWERS_2};
                break;
            case REGION_PRISON:
//                tracks = PrisonLevel.PRISON_TRACK_LIST;
                tracks = new String[]{Assets.Music.PRISON_1, Assets.Music.PRISON_2, Assets.Music.PRISON_2};
                break;
            case REGION_CAVES:
//                tracks = CavesLevel.CAVES_TRACK_LIST;
                tracks = new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2};
                break;
            case REGION_CITY:
//                tracks = CityLevel.CITY_TRACK_LIST;
                tracks = new String[]{Assets.Music.CITY_1, Assets.Music.CITY_2, Assets.Music.CITY_2};
                break;
            case REGION_HALLS:
//                tracks = HallsLevel.HALLS_TRACK_LIST;
                tracks = new String[]{Assets.Music.HALLS_1, Assets.Music.HALLS_2, Assets.Music.HALLS_2};
                break;

            default:
                tracks = new String[]{"music/theme_2.ogg"};
                probs = new int[]{1};
                break;
        }
        appendListHead(b, "music");
        appendArray(b, "tracks", tracks);
        appendArray(b, "probs", probs);
        removeLastChars(b, 2);
        b.append("},\n");
    }

}