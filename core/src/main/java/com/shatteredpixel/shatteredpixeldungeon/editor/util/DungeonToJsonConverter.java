package com.shatteredpixel.shatteredpixeldungeon.editor.util;

import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CAVES;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_CITY;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_HALLS;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_PRISON;
import static com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme.REGION_SEWERS;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DungeonToJsonConverter {


    public static String getAsJson(CustomDungeon dungeon) {
        Dungeon.customDungeon = dungeon;

        StringBuilder b = new StringBuilder();

        List<String> floors = new ArrayList<>(dungeon.floorNames());
        b.append('{');
        appendParam(b, "start", dungeon.getStart());
        if (dungeon.getStartGold() > 0) appendParam(b, "gold", dungeon.getStartGold());
        if (dungeon.getStartAlchemicalEnergy() > 0)
            appendParam(b, "energy", dungeon.getStartAlchemicalEnergy());
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
        if (dungeon.getMaybeGhostSpawnLevels().length > 0)
            appendArray(b, "ghost_spawn_levels", dungeon.getMaybeGhostSpawnLevels());
        else appendArray(b, "ghost_spawn_levels", new String[]{Level.SURFACE});
        if (dungeon.getMaybeWandmakerSpawnLevels().length > 0)
            appendArray(b, "wandmaker_spawn_levels", dungeon.getMaybeWandmakerSpawnLevels());
        else appendArray(b, "wandmaker_spawn_levels", new String[]{Level.SURFACE});
        if (dungeon.getMaybeBlacksmithSpawnLevels().length > 0)
            appendArray(b, "blacksmith_spawn_levels", dungeon.getMaybeBlacksmithSpawnLevels());
        else appendArray(b, "blacksmith_spawn_levels", new String[]{Level.SURFACE});
        if (dungeon.getMaybeImpSpawnLevels().length > 0)
            appendArray(b, "imp_spawn_levels", dungeon.getMaybeImpSpawnLevels());
        else appendArray(b, "imp_spawn_levels", new String[]{Level.SURFACE});

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

        appendListHead(b, l.getName());
        appendParam(b, "depth", l.getDepth());
        appendParam(b, "type", l.getType() == CustomLevel.class ? "custom" : "regular");

        List<Integer> entranceCells = null;
        List<Integer> exitCells = null;
        Map<Integer, LevelTransition> levelTransitions = null;
        CustomLevel f = null;
        if (l.getType() == CustomLevel.class) {
            appendListHead(b, "custom_layout");
            f = (CustomLevel) l.loadLevel();
            int width = f.width();
            int height = f.height();
            appendParam(b, "width", width);
            appendParam(b, "height", height);
            appendParam(b, "region", l.getRegion());//1 to 5   is value??!
            appendParam(b, "view_distance", f.viewDistance);
            appendMusic(b, f.getRegionValue());
            appendArray(b, "map", f.map);

            appendArrayHead(b, "entrances");
            entranceCells = l.entranceCells;
            for (int cell : entranceCells) {
                if (f.transitions.containsKey(cell)) {
                    appendCoord(b, cell, width, height);
                }
            }
            b.append("],\n");

            appendArrayHead(b, "exits");
            exitCells = l.exitCells;
            for (int cell : exitCells) {
                if (f.transitions.containsKey(cell)) {
                    appendCoord(b, cell, width, height);
                }
            }
            b.append("],\n");

            appendComplexArray(b, "items", f.heaps.valueList(), h -> h.peek() != null,
                    obj -> appendItemValues(b, obj), h -> h.pos, width, height);

            //TODO shuffle_items

            appendComplexArray(b, "mobs", f.mobs, m -> m != null,
                    obj -> appendMobValues(b, obj), m -> m.pos, width, height);

            appendComplexArray(b, "open_traps", f.traps.values(), t -> t.visible && t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width, height);

            appendComplexArray(b, "hidden_traps", f.traps.values(), t -> !t.visible && t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width, height);

            appendComplexArray(b, "disarmed_traps", f.traps.values(), t -> !t.active,
                    obj -> appendParamEnd(b, "type", obj.getClass().getSimpleName()), t -> t.pos, width, height);

            removeLastChars(b, 2);

            //TODO plants

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

        appendParam(b, "chasm", l.getChasm());
        if (l.getBoss() != LevelScheme.REGION_NONE) appendParam(b, "boss", true);
        appendParam(b, "passage", l.getPassage());
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
        appendParam(b, "price_multiplier", l.getPriceMultiplier());
        //TODO visibility
        //TODO trap_detection
        //TODO door_detection
//        if (f == null && l.getFeeling() != Level.Feeling.NONE) //TODO why is this "unknown key"?
//            appendParam(b, "feeling", l.getFeeling() == null ? "random" : l.getFeeling().toString().toLowerCase(Locale.ENGLISH));
        //TODO extra_items  (RNG manipultad item spawning)
        //TODO extra_mobs

        if (f == null) {
            Class[] rot = Bestiary.standardMobRotation(Dungeon.getSimulatedDepth(l)).toArray(new Class[0]);
            appendArray(b, "bestiary", rot);
        } else appendArray(b, "bestiary", f.getMobRotationVar().toArray(new Class[0]));

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
                                               Consumer<T> appendObject, IntFunction<T> getPos, int width, int height) {
        appendArrayHead(b, name);
        b.append("  ");
        for (T obj : list) {
            if (validityCheck.test(obj)) {
                b.append("{");
                appendCoordWithoutBrackets(b, getPos.get(obj), width, height);
                b.append(",");
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

    private static void appendCoord(StringBuilder b, int cell, int width, int height) {
        b.append("{");
        appendCoordWithoutBrackets(b, cell, width, height);
        b.append("}\n");
    }

    private static void appendCoordWithoutBrackets(StringBuilder b, int cell, int width, int height) {
        b.append("\"x\": ").append(cell % width).append(", \"y\": ").append(cell / height).append("\n");
    }

    private static final int PACKAGE_NAME_LENGTH = "com.alphadraxonis.sandboxpixeldungeon.".length();
    //TODO FIXME package name
    private static final int PACKAGE_NAME_ITEMS_LENGTH = PACKAGE_NAME_LENGTH + "items.".length();
    private static final int PACKAGE_NAME_MOBS_LENGTH = PACKAGE_NAME_LENGTH + "actors.mobs.".length();
    private static final int PACKAGE_NAME_WEAPON_LENGTH = PACKAGE_NAME_ITEMS_LENGTH + "weapon.".length();
    private static final int PACKAGE_NAME_ARMOR_LENGTH = PACKAGE_NAME_ITEMS_LENGTH + "armor.".length();

    private static void appendItemValues(StringBuilder b, Heap heap) {

        Item i = heap.peek();
        appendParam(b, "type", getShortenFullClassName(i, PACKAGE_NAME_ITEMS_LENGTH));
        //TODO category (instead of type)
        //TODO ignore_deck (only with category)
        appendParam(b, "heap_type", heap.type.toString());
        if (i.quantity() > 1) appendParam(b, "quantity", i.quantity());
        //TODO quantity_min and _max
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
        if (i instanceof Key) {
            appendParam(b, "level_name", ((Key) i).levelName);
        }
        removeLastChars(b, 2);
    }

    private static void appendMobValues(StringBuilder b, Mob m) {

        appendParam(b, "type", getShortenFullClassName(m, PACKAGE_NAME_MOBS_LENGTH));
        //TODO hp
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
        int[] probs = {1, 1, 1};//TODO does default 1:1:0.5 work?
        switch (region) {
            case REGION_SEWERS:
                tracks = new String[]{Assets.Music.SEWERS_1, Assets.Music.SEWERS_2, Assets.Music.SEWERS_2};
                break;
            case REGION_PRISON:
                tracks = new String[]{Assets.Music.PRISON_1, Assets.Music.PRISON_2, Assets.Music.PRISON_2};
                break;
            case REGION_CAVES:
                tracks = new String[]{Assets.Music.CAVES_1, Assets.Music.CAVES_2, Assets.Music.CAVES_2};
                break;
            case REGION_CITY:
                tracks = new String[]{Assets.Music.CITY_1, Assets.Music.CITY_2, Assets.Music.CITY_2};
                break;
            case REGION_HALLS:
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