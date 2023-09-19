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

package com.alphadraxonis.sandboxpixeldungeon.items;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.Dungeon;
import com.alphadraxonis.sandboxpixeldungeon.actors.hero.Hero;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Wraith;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.alphadraxonis.sandboxpixeldungeon.editor.editcomps.EditItemComp;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.IconTitleWithSubIcon;
import com.alphadraxonis.sandboxpixeldungeon.editor.util.EditorUtilies;
import com.alphadraxonis.sandboxpixeldungeon.effects.CellEmitter;
import com.alphadraxonis.sandboxpixeldungeon.effects.Speck;
import com.alphadraxonis.sandboxpixeldungeon.effects.particles.ElmoParticle;
import com.alphadraxonis.sandboxpixeldungeon.effects.particles.ShadowParticle;
import com.alphadraxonis.sandboxpixeldungeon.items.armor.Armor;
import com.alphadraxonis.sandboxpixeldungeon.items.artifacts.Artifact;
import com.alphadraxonis.sandboxpixeldungeon.items.bombs.Bomb;
import com.alphadraxonis.sandboxpixeldungeon.items.food.ChargrilledMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.food.FrozenCarpaccio;
import com.alphadraxonis.sandboxpixeldungeon.items.food.MysteryMeat;
import com.alphadraxonis.sandboxpixeldungeon.items.journal.DocumentPage;
import com.alphadraxonis.sandboxpixeldungeon.items.journal.Guidebook;
import com.alphadraxonis.sandboxpixeldungeon.items.keys.Key;
import com.alphadraxonis.sandboxpixeldungeon.items.potions.Potion;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.Ring;
import com.alphadraxonis.sandboxpixeldungeon.items.rings.RingOfWealth;
import com.alphadraxonis.sandboxpixeldungeon.items.scrolls.Scroll;
import com.alphadraxonis.sandboxpixeldungeon.items.wands.Wand;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.Weapon;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.Dart;
import com.alphadraxonis.sandboxpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.alphadraxonis.sandboxpixeldungeon.journal.Document;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Heap implements Bundlable {

    public enum Type {
        HEAP,
        FOR_SALE,
        CHEST,
        LOCKED_CHEST,
        CRYSTAL_CHEST,
        TOMB,
        SKELETON,
        REMAINS
    }

    public Type type = Type.HEAP;

    public int pos = 0;

    public ItemSprite sprite;
    public boolean seen = false;
    public boolean haunted = false;
    public boolean autoExplored = false; //used to determine if this heap should count for exploration bonus

    public LinkedList<Item> items = new LinkedList<>();

    public void open(Hero hero) {
        switch (type) {
            case TOMB:
                Wraith.spawnAround(hero.pos, true);
                break;
            case REMAINS:
            case SKELETON:
                CellEmitter.center(pos).start(Speck.factory(Speck.RATTLE), 0.1f, 3);
                break;
            default:
        }

        if (haunted) {
            if (Wraith.spawnAt(pos, true) == null) {
                hero.sprite.emitter().burst(ShadowParticle.CURSE, 6);
                hero.damage(hero.HP / 2, this);
            }
            Sample.INSTANCE.play(Assets.Sounds.CURSED);
        }

        type = Type.HEAP;
        ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(hero, 1);
        if (bonus != null && !bonus.isEmpty()) {
            items.addAll(0, bonus);
            RingOfWealth.showFlareForBonusDrop(sprite);
        }
        linkSprite();
        sprite.drop();
    }

    public Heap setHauntedIfCursed() {
        for (Item item : items) {
            if (item.cursed) {
                haunted = true;
                item.setCursedKnown(true);
                break;
            }
        }
        return this;
    }

    public int size() {
        return items.size();
    }

    public Item pickUp() {

        if (items.isEmpty()) {
            destroy();
            return null;
        }
        Item item = items.removeFirst();
        updateAfterRemovalOfItems();

        return item;
    }

    public Item peek() {
        return items.peek();
    }

    public void drop(Item item) {

        if (item.stackable && type != Type.FOR_SALE) {

            for (Item i : items) {
                if (i.isSimilar(item)) {
                    item = i.merge(item);
                    break;
                }
            }
            items.remove(item);

        }

        //lost backpack must always be on top of a heap
        if ((item.dropsDownHeap && type != Type.FOR_SALE) || peek() instanceof LostBackpack) {
            items.add(item);
        } else {
            items.addFirst(item);
        }

        if (sprite != null) {
            sprite.view(this).place(pos);
            updateSubicon();
        }

        if (TippedDart.lostDarts > 0) {
            Dart d = new Dart();
            d.quantity(TippedDart.lostDarts);
            TippedDart.lostDarts = 0;
            drop(d);
        }
    }

    public void replace(Item a, Item b) {
        int index = items.indexOf(a);
        if (index != -1) {
            items.remove(index);
            for (Item i : items) {
                if (i.isSimilar(b)) {
                    i.merge(b);
                    return;
                }
            }
            items.add(index, b);
        }
    }

    public void remove(Item a) {
        items.remove(a);
        updateAfterRemovalOfItems();
    }

    public void remove(Item item, int quantity) {

        if (item.stackable && type != Type.FOR_SALE) {

            for (Item i : items) {
                if (i.isSimilar(item)) {
                    i.quantity -= quantity;
                    if (i.quantity <= 0) {
                        remove(i);
                        updateAfterRemovalOfItems();
                        return;
                    } else {
                        updateSubicon();
                        return;
                    }
                }
            }
        }
        for (Item i : items) {
            if (EditItemComp.areEqual(i, item)) {
                remove(i);
                break;
            }
        }
    }
    private void updateAfterRemovalOfItems(){
        if (items.isEmpty()) {
            destroy();
        } else if (sprite != null) {
            sprite.view(this).place(pos);
            updateSubicon();
        }
    }

    public void burn() {

        if (type != Type.HEAP) {
            return;
        }

        boolean burnt = false;
        boolean evaporated = false;

        for (Item item : items.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {
            if (item instanceof Scroll && !item.unique) {
                items.remove(item);
                burnt = true;
            } else if (item instanceof Dewdrop) {
                items.remove(item);
                evaporated = true;
            } else if (item instanceof MysteryMeat || item instanceof FrozenCarpaccio) {
                replace(item, ChargrilledMeat.cook(item.quantity));
                burnt = true;
            } else if (item instanceof Bomb) {
                items.remove(item);
                ((Bomb) item).explode(pos);
                if (((Bomb) item).explodesDestructively()) {
                    //stop processing the burning, it will be replaced by the explosion.
                    return;
                } else {
                    burnt = true;
                }
            }
        }

        if (burnt || evaporated) {

            if (Dungeon.level.heroFOV[pos]) {
                if (burnt) {
                    burnFX(pos);
                } else {
                    evaporateFX(pos);
                }
            }

            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(this).place(pos);
            }

        }
    }

    //Note: should not be called to initiate an explosion, but rather by an explosion that is happening.
    public void explode() {

        //breaks open most standard containers, mimics die.
        if (type == Type.CHEST || type == Type.SKELETON) {
            type = Type.HEAP;
            linkSprite();
            sprite.drop();
            return;
        }

        if (type != Type.HEAP) {

            return;

        } else {

            for (Item item : items.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {

                //unique items aren't affect by explosions
                if (item.unique || (item instanceof Armor && ((Armor) item).checkSeal() != null)) {
                    continue;
                }

                if (item instanceof Potion) {
                    items.remove(item);
                    ((Potion) item).shatter(pos);

                } else if (item instanceof Honeypot.ShatteredPot) {
                    items.remove(item);
                    ((Honeypot.ShatteredPot) item).destroyPot(pos);

                } else if (item instanceof Bomb) {
                    items.remove(item);
                    ((Bomb) item).explode(pos);
                    if (((Bomb) item).explodesDestructively()) {
                        //stop processing current explosion, it will be replaced by the new one.
                        return;
                    }

                    //upgraded items can endure the blast
                } else if (item.level() <= 0) {
                    items.remove(item);
                }

            }

            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(this).place(pos);
            }
        }
    }

    public void freeze() {

        if (type != Type.HEAP) {
            return;
        }

        boolean frozen = false;
        for (Item item : items.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {
            if (item instanceof MysteryMeat) {
                replace(item, FrozenCarpaccio.cook((MysteryMeat) item));
                frozen = true;
            } else if (item instanceof Potion && !item.unique) {
                items.remove(item);
                ((Potion) item).shatter(pos);
                frozen = true;
            } else if (item instanceof Bomb) {
                ((Bomb) item).fuse = null;
                frozen = true;
            }
        }

        if (frozen) {
            if (isEmpty()) {
                destroy();
            } else if (sprite != null) {
                sprite.view(this).place(pos);
            }
        }
    }

    public static void burnFX(int pos) {
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
    }

    public static void evaporateFX(int pos) {
        CellEmitter.get(pos).burst(Speck.factory(Speck.STEAM), 5);
    }

    public boolean isEmpty() {
        return items == null || items.size() == 0;
    }

    public void destroy() {
        Dungeon.level.heaps.remove(this.pos);
        destroyImages();
        items.clear();
    }
    public void destroyImages(){
        if (sprite != null) sprite.kill();
        killSubicons();
    }

    public String title() {
        switch (type) {
            case FOR_SALE:
                Item i = peek();
                if (size() == 1) {
                    return Messages.get(this, "for_sale", Shopkeeper.sellPrice(i), i.title());
                } else {
                    return i.title();
                }
            case CHEST:
                return Messages.get(this, "chest");
            case LOCKED_CHEST:
                return Messages.get(this, "locked_chest");
            case CRYSTAL_CHEST:
                return Messages.get(this, "crystal_chest");
            case TOMB:
                return Messages.get(this, "tomb");
            case SKELETON:
                return Messages.get(this, "skeleton");
            case REMAINS:
                return Messages.get(this, "remains");
            default:
                return peek().title();
        }
    }

    public String info() {
        switch (type) {
            case CHEST:
                return Messages.get(this, "chest_desc");
            case LOCKED_CHEST:
                return Messages.get(this, "locked_chest_desc");
            case CRYSTAL_CHEST:
                if (peek() instanceof Artifact)
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "artifact"));
                else if (peek() instanceof Wand)
                    return Messages.get(this, "crystal_chest_desc", Messages.get(this, "wand"));
                else if (peek()instanceof Ring) return Messages.get(this, "crystal_chest_desc", Messages.get(this, "ring"));
                return Messages.get(this, "locked_chest_desc");
            case TOMB:
                return Messages.get(this, "tomb_desc");
            case SKELETON:
                return Messages.get(this, "skeleton_desc");
            case REMAINS:
                return Messages.get(this, "remains_desc");
            default:
                return peek().info();
        }
    }

    private static final String POS = "pos";
    private static final String SEEN = "seen";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String HAUNTED = "haunted";
    private static final String AUTO_EXPLORED = "auto_explored";

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        seen = bundle.getBoolean(SEEN);
        type = Type.valueOf(bundle.getString(TYPE));

        items = new LinkedList<>((Collection<Item>) ((Collection<?>) bundle.getCollection(ITEMS)));
        items.removeAll(Collections.singleton(null));

        //remove any document pages that either don't exist anymore or that the player already has
        for (Item item : items.toArray(EditorUtilies.EMPTY_ITEM_ARRAY)) {
            if (item instanceof DocumentPage
                    && (!((DocumentPage) item).document().pageNames().contains(((DocumentPage) item).page())
                    || ((DocumentPage) item).document().isPageFound(((DocumentPage) item).page()))) {
                items.remove(item);
            }
            if (item instanceof Guidebook && Document.ADVENTURERS_GUIDE.isPageRead(0)) {
                items.remove(item);
            }
        }

        haunted = bundle.getBoolean(HAUNTED);
        autoExplored = bundle.getBoolean(AUTO_EXPLORED);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(SEEN, seen);
        bundle.put(TYPE, type);
        bundle.put(ITEMS, items);
        bundle.put(HAUNTED, haunted);
        bundle.put(AUTO_EXPLORED, autoExplored);
    }

    public Heap getCopy(){
        Bundle bundle = new Bundle();
        bundle.put("HEAP",this);
        return  (Heap) bundle.get("HEAP");
    }


    public Image subicon, forSaleIndicator;
    public BitmapText quantityDisplay, heapSize, itemLvl, keyLevel;

    {
        initSubicons();
    }

    public void initSubicons() {
        subicon = new Image();
        quantityDisplay = new BitmapText(PixelScene.pixelFont);
        heapSize = new BitmapText(PixelScene.pixelFont);
        itemLvl = new BitmapText(PixelScene.pixelFont);
        keyLevel = new BitmapText(PixelScene.pixelFont);
        forSaleIndicator = new Image();
        forSaleIndicator.copy(new ItemSprite(ItemSpriteSheet.GOLD));
    }

    private void killSubicons() {
        subicon.kill();
        quantityDisplay.kill();
        heapSize.kill();
        itemLvl.kill();
        keyLevel.kill();
        forSaleIndicator.kill();
    }

    public void destroySubicons() {
        subicon.destroy();
        quantityDisplay.destroy();
        heapSize.destroy();
        itemLvl.destroy();
        keyLevel.destroy();
        forSaleIndicator.destroy();
    }

    public void addHeapComponents(Group addTo) {
        addTo.add(sprite);
        addTo.add(subicon);
        addTo.add(quantityDisplay);
        addTo.add(heapSize);
        addTo.add(itemLvl);
        addTo.add(keyLevel);
        addTo.add(forSaleIndicator);
    }

    public void linkSprite() {
        sprite.link(this);
    }

    public void linkSprite(Heap heap) {
        sprite.link(heap);

        if (CustomDungeon.isEditing()) updateSubicon();
    }

    public void updateSubicon() {
        Item i = items.peek();

        if (i != null) {
            Image copy = IconTitleWithSubIcon.createSubIcon(i);
            if (copy != null && isNotContainerType()) {
                subicon.copy(copy);
                subicon.visible = true;
                subicon.scale.set(0.8f);

                subicon.point(sprite.point());

                if (i instanceof Potion) {
                    subicon.x += sprite.width() / 2 + subicon.width() / 4;
                    subicon.y -= subicon.height() / 6;
                } else if (i instanceof Ring) {
                    subicon.x += sprite.width() - subicon.width() * 7 / 12f;
                    subicon.y -= subicon.height() / 5 * 2;
                } else {
                    subicon.x += sprite.width() - subicon.width();
                    subicon.y -= subicon.height() / 6;
                }

                PixelScene.align(subicon);

            } else subicon.visible = false;

            if (i.quantity() > 1 && isNotContainerType()) {

                quantityDisplay.visible = true;
                quantityDisplay.scale.set(0.65f);


                quantityDisplay.text("x" + i.quantity());
                quantityDisplay.measure();

                quantityDisplay.point(sprite.point());

                PixelScene.align(quantityDisplay);


            } else {
                quantityDisplay.text(null);
                quantityDisplay.visible = false;
            }

            if (items.size() > 1) {
                heapSize.visible = true;
                heapSize.scale.set(0.65f);

                heapSize.text((items.size() - 1) + "+");
                heapSize.measure();

                heapSize.point(sprite.point());

                heapSize.y += sprite.height() - heapSize.height() * 0.5f;

                PixelScene.align(heapSize);

            } else {
                heapSize.text(null);
                heapSize.visible = false;
            }

            //Code from ItemSlot
            int trueLvl = i.trueLevel();
            int buffedLvl = i.buffedLvl();
            if ((trueLvl != 0 || buffedLvl != 0) && isNotContainerType()) {

                itemLvl.visible = true;
                itemLvl.scale.set(0.65f);

                itemLvl.text(Messages.format(ItemSlot.TXT_LEVEL, buffedLvl));
                itemLvl.measure();
                if (trueLvl == buffedLvl || buffedLvl <= 0) {
                    if (buffedLvl > 0) {
                        if ((i instanceof Weapon && ((Weapon) i).curseInfusionBonus)
                                || (i instanceof Armor && ((Armor) i).curseInfusionBonus)
                                || (i instanceof Wand && ((Wand) i).curseInfusionBonus)) {
                            itemLvl.hardlight(ItemSlot.CURSE_INFUSED);
                        } else {
                            itemLvl.hardlight(ItemSlot.UPGRADED);
                        }
                    } else {
                        itemLvl.hardlight(ItemSlot.DEGRADED);
                    }
                } else {
                    itemLvl.hardlight(buffedLvl > trueLvl ? ItemSlot.ENHANCED : ItemSlot.WARNING);
                }

                itemLvl.point(sprite.point());

                itemLvl.x += sprite.width() - itemLvl.width() * 0.9f;
                itemLvl.y += sprite.height() * 0.77f;

                PixelScene.align(itemLvl);

            } else {
                itemLvl.text(null);
                itemLvl.visible = false;
            }

            if (i instanceof Key && !((Key) i).levelName.equals(Dungeon.level.name)) {

                keyLevel.visible = true;
                keyLevel.scale.set(0.45f);

                keyLevel.hardlight(Window.TITLE_COLOR);
                keyLevel.text(EditorUtilies.getDispayName(((Key) i).levelName));
                keyLevel.measure();

                keyLevel.point(sprite.point());

                keyLevel.x += (sprite.width() - keyLevel.width()) * 0.5f;
                keyLevel.y += sprite.height() * 0.65f;

                PixelScene.align(keyLevel);

            } else {
                keyLevel.text(null);
                keyLevel.visible = false;
            }


            if (type == Type.FOR_SALE) {

                forSaleIndicator.visible = true;
                forSaleIndicator.scale.set(0.4f);

                forSaleIndicator.point(sprite.point());

                forSaleIndicator.x += (sprite.width() - forSaleIndicator.width()) * 0.5f;
                forSaleIndicator.y += (sprite.height() - forSaleIndicator.height()) * 0.5f;

                PixelScene.align(forSaleIndicator);

            } else forSaleIndicator.visible = false;

        } else {
            subicon.visible = quantityDisplay.visible = heapSize.visible = itemLvl.visible = forSaleIndicator.visible = false;
        }
    }

    private boolean isNotContainerType() {
        return type == Type.HEAP || type == Type.FOR_SALE;
    }
}