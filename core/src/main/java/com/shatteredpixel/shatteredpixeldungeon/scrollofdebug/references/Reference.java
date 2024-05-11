package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

import java.lang.reflect.Type;
import java.util.Collection;

public abstract class Reference {

    private final String name;

    private final Class<?> type;
    private Type[] actualTypeArguments;
    private final Object value;

    public Reference(Class<?> type, Object value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    public abstract Object valueViaParent() throws ReferenceNotFoundException;

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public void setActualTypeArguments(Type[] actualTypeArguments) {
        this.actualTypeArguments = actualTypeArguments;
    }

    public Type[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    public Image createIcon() {
        Object iconValue = getValue() == null ? Reflection.newInstance(type) : getValue();

        Image img = objectToImage(iconValue);
        if (img != null) return img;

        if (Collection.class.isAssignableFrom(getType())) {
            //TODO tzz
        }

        if (type == int.class || type == Integer.class) {
            switch (name) {
                case "pos":
                case "cell":
                case "target":
                case "position": new ItemSprite();//return Icons.POSITION.get();//TODO tzz
                case "gold": return new ItemSprite(ItemSpriteSheet.GOLD);
                case "energy": return new ItemSprite(ItemSpriteSheet.ENERGY);
                case "depth": return Icons.DEPTH.get();
            }
            if (name.contains("gold")) return new ItemSprite(ItemSpriteSheet.GOLD);
        }

        return new ItemSprite();
    }

    public static Image objectToImage(Object obj) {
        if (obj instanceof Item) return Dungeon.customDungeon.getItemImage((Item) obj);
        if (obj instanceof Mob) return ((Mob) obj).sprite();
        if (obj instanceof Trap) return ((Trap) obj).getSprite();
        if (obj instanceof Plant) return ((Plant) obj).getSprite();
        if (obj instanceof Heap) return new ItemSprite((Heap) obj);
        if (obj instanceof Image) return (Image) obj;
        return null;
    }


}