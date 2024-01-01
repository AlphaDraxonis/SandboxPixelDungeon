package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PlantItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TrapItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class Reference {

    private final String name;

    private final Class<?> type;
    private final Object value;

    public Reference(Class<?> type, Object value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Image createIcon() {
        Object iconValue = getValue() == null ? Reflection.newInstance(type) : getValue();

        if (value instanceof Item) return Dungeon.customDungeon.getItemImage((Item) iconValue);
        if (value instanceof Mob) return ((Mob) iconValue).sprite();
        if (value instanceof Trap) return TrapItem.getTrapImage((Trap) iconValue);
        if (value instanceof Plant) return PlantItem.getPlantImage((Plant) iconValue);

        return new ItemSprite();
    }


}