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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.customobjects.interfaces.CustomObjectClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ItemStatusHandler<T extends Item> {

	private Class<? extends T>[] items;
	private LinkedHashMap<Class<? extends T>, String> itemLabels;//stores a string for each item used as key in labelImages
	private LinkedHashMap<String, Integer> labelImages;//stores general string->image information
	private LinkedHashSet<Class<? extends T>> known;

	public ItemStatusHandler( Class<? extends T>[] items, HashMap<String, Integer> labelImages ) {

		this.items = items;

        this.labelImages = new LinkedHashMap<>(labelImages);
        known = new LinkedHashSet<>();

        this.itemLabels = assignLabels(new LinkedHashSet<>(Arrays.asList(items)), new ArrayList<>(labelImages.keySet()), new LinkedHashMap<>());
    }

    //default implementation: random; but can be overridden
    protected LinkedHashMap<Class<? extends T>, String> assignLabels(Set<Class<? extends T>> items, List<String> labelsLeft, LinkedHashMap<Class<? extends T>, String> itemLabels) {

        for (Class<? extends T> item : items) {

			int index = Random.Int( labelsLeft.size() );

			itemLabels.put( item, labelsLeft.get( index ) );
			labelsLeft.remove( index );

        }
        return itemLabels;
    }

	public ItemStatusHandler( Class<? extends T>[] items, HashMap<String, Integer> labelImages, Bundle bundle ) {

		this.items = items;

		this.itemLabels = new LinkedHashMap<>();
		this.labelImages = new LinkedHashMap<>(labelImages);
		known = new LinkedHashSet<>();

		ArrayList<String> allLabels = new ArrayList<>(labelImages.keySet());

		restore(bundle, allLabels);
	}

	private static final String PFX_LABEL	= "_label";
	private static final String PFX_KNOWN	= "_known";
	
	public void save( Bundle bundle ) {
		for (int i=0; i < items.length; i++) {
			String itemName = items[i].getSimpleName();
			bundle.put( itemName + PFX_LABEL, itemLabels.get( items[i] ) );
			bundle.put( itemName + PFX_KNOWN, known.contains( items[i] ) );
		}
	}

	public void saveSelectively( Bundle bundle, ArrayList<Item> itemsToSave ){
		List<Class<? extends T>> items = Arrays.asList(this.items);
		for (Item item : itemsToSave){
			if (items.contains(item.getClass())){
				Class<? extends T> cls = items.get(items.indexOf(item.getClass()));
				String itemName = cls.getSimpleName();
				bundle.put( itemName + PFX_LABEL, itemLabels.get( cls ) );
				bundle.put( itemName + PFX_KNOWN, known.contains( cls ) );
			}
		}
	}
	
	public void saveClassesSelectively( Bundle bundle, ArrayList<Class<?extends Item>> clsToSave ){
		List<Class<? extends T>> items = Arrays.asList(this.items);
		for (Class<?extends Item> cls : clsToSave){
			if (items.contains(cls)){
				Class<? extends T> toSave = items.get(items.indexOf(cls));
				String itemName = toSave.getSimpleName();
				bundle.put( itemName + PFX_LABEL, itemLabels.get( toSave ) );
				bundle.put( itemName + PFX_KNOWN, known.contains( toSave ) );
			}
		}
	}

	private void restore( Bundle bundle, ArrayList<String> labelsLeft  ) {

		ArrayList<Class<? extends T>> unlabelled = new ArrayList<>();

		for (int i=0; i < items.length; i++) {

			Class<? extends T> item = items[i];
			String itemName = item.getSimpleName();

			if (bundle.contains( itemName + PFX_LABEL )) {

				String label = bundle.getString( itemName + PFX_LABEL );
				itemLabels.put( item, label );
				labelsLeft.remove( label );

				if (bundle.getBoolean( itemName + PFX_KNOWN )) {
					known.add( item );
				}

			} else {

				unlabelled.add(items[i]);

			}
		}

		for (Class<? extends T> item : unlabelled){

			String itemName = item.getSimpleName();

			int index = Random.Int( labelsLeft.size() );

			itemLabels.put( item, labelsLeft.get( index ) );
			labelsLeft.remove( index );

			if (bundle.contains( itemName + PFX_KNOWN ) && bundle.getBoolean( itemName + PFX_KNOWN )) {
				known.add( item );
			}
		}
	}
	
	private Class<? extends T> getKey(Class<?> itemCls) {
		Class<?> result = itemCls;
		while (CustomObjectClass.class.isAssignableFrom(result)) {
			result = result.getSuperclass();
		}
		return (Class<? extends T>) result;
	}
	
	public boolean contains( T item ){
		return contains(getKey(item.getClass()));
	}
	
	public boolean contains( Class<?extends T> itemCls ){
		Class<?> cl = getKey(itemCls);
		for (Class<?extends Item> i : items){
			if (cl.equals(i)){
				return true;
			}
		}
		return false;
	}
	
	public int image( T item ) {
		return labelImages.get(label(item));
	}
	
	public int image( Class<?extends T> itemCls ) {
		return labelImages.get(label(itemCls));
	}
	
	public String label( T item ) {
		return itemLabels.get(getKey(item.getClass()));
	}
	
	public String label( Class<?extends T> itemCls ){
		return itemLabels.get( getKey(itemCls) );
	}
	
	public boolean isKnown( T item ) {
		return known.contains( getKey(item.getClass()) ) || CustomDungeon.isEditing();
	}
	
	public boolean isKnown( Class<?extends T> itemCls ){
		return known.contains( getKey(itemCls) ) || CustomDungeon.isEditing();
	}
	
	public void know( T item ) {
		known.add( getKey(item.getClass() ));
	}
	
	public void know( Class<?extends T> itemCls ){
		known.add( getKey(itemCls) );
	}
	
	public HashSet<Class<? extends T>> known() {
		return known;
	}
	
	public HashSet<Class<? extends T>> unknown() {
		LinkedHashSet<Class<? extends T>> result = new LinkedHashSet<>();
		for (Class<? extends T> i : items) {
			if (!known.contains( i )) {
				result.add( i );
			}
		}
		return result;
	}
}
