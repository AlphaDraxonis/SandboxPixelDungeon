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

package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class Notes {
	
	public static abstract class Record implements /*Comparable<Record>,*/ Bundlable {

		//TODO currently notes can only relate to branch = 0, add branch support here if that changes
		protected String levelName;

		public String levelName(){
			return levelName;
		}
		
		public abstract String desc();
		
		@Override
		public abstract boolean equals(Object obj);

		private static final String LEVEL_NAME = "levelName";



		@Override
		public void restoreFromBundle( Bundle bundle ) {
			levelName = bundle.getString(LEVEL_NAME);
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
			bundle.put(LEVEL_NAME, levelName );
		}
	}
	
	public enum Landmark {
		WELL_OF_HEALTH,
		WELL_OF_AWARENESS,
		WELL_OF_TRANSMUTATION,
		ALCHEMY,
		GARDEN,
		STATUE,
		SACRIFICIAL_FIRE,
		SHOP,
		
		GHOST,
		WANDMAKER,
		TROLL,
		IMP,

		DEMON_SPAWNER;
		
		public String desc() {
			return Messages.get(this, name());
		}
	}
	
	public static class LandmarkRecord extends Record {
		
		protected Landmark landmark;
		
		public LandmarkRecord() {}
		
		public LandmarkRecord(Landmark landmark, String levelName ) {
			this.landmark = landmark;
			this.levelName = levelName;
		}
		
		@Override
		public String desc() {
			return landmark.desc();
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof LandmarkRecord)
					&& landmark == ((LandmarkRecord) obj).landmark
					&& levelName().equals(((LandmarkRecord) obj).levelName());
		}
		
		private static final String LANDMARK	= "landmark";
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			landmark = Landmark.valueOf(bundle.getString(LANDMARK));
		}
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( LANDMARK, landmark.name() );
		}
	}
	
	public static class KeyRecord extends Record {
		
		protected Key key;
		
		public KeyRecord() {}
		
		public KeyRecord( Key key ){
			this.key = key;
		}
		
		@Override
		public String levelName() {
			return key.levelName;
		}

		public int keyCell() {
			return key.cell;
		}

		@Override
		public String desc() {
			return key.title();
		}
		
		public Class<? extends Key> type(){
			return key.getClass();
		}
		
		public int quantity(){
			return key.quantity();
		}
		
		public void quantity(int num){
			key.quantity(num);
		}
		
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof KeyRecord)
					&& key.isSimilar(((KeyRecord) obj).key);
		}
		
		private static final String KEY	= "key";
		
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			key = (Key) bundle.get(KEY);
		}
		
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( KEY, key );
		}
	}
	
	private static ArrayList<Record> records;
	
	public static void reset() {
		records = new ArrayList<>();
	}
	
	private static final String RECORDS	= "records";
	
	public static void storeInBundle( Bundle bundle ) {
		bundle.put( RECORDS, records );
	}
	
	public static void restoreFromBundle( Bundle bundle ) {
		records = new ArrayList<>();
		for (Bundlable rec : bundle.getCollection( RECORDS ) ) {
			records.add( (Record) rec );
		}
	}
	
	public static boolean add( Landmark landmark ) {
		LandmarkRecord l = new LandmarkRecord( landmark, Dungeon.levelName );
		if (!records.contains(l)) {
			boolean result = records.add(new LandmarkRecord(landmark, Dungeon.levelName));
//			Collections.sort(records);
			return result;
		}
		return false;
	}
	
	public static boolean remove( Landmark landmark ) {
		return records.remove( new LandmarkRecord(landmark, Dungeon.levelName) );
	}
	
	public static boolean add( Key key ){
		KeyRecord k = new KeyRecord(key);
		if (!records.contains(k)){
			boolean result = records.add(k);
//			Collections.sort(records);
			return result;
		} else {
			k = (KeyRecord) records.get(records.indexOf(k));
			k.quantity(k.quantity() + key.quantity());
			return true;
		}
	}
	
	public static boolean remove( Key key ){
		int keyQuantityToRemove = key.quantity();

		for (int i = 0; i < keyQuantityToRemove; i++) {
			if (searchForKeyAndRemoveIt(key.levelName, key.cell, key.getClass())) continue;
			if (key.cell != -1) {
				if (searchForKeyAndRemoveIt(Level.ANY, key.cell, key.getClass())) continue;
				if (searchForKeyAndRemoveIt(key.levelName, -1, key.getClass())) continue;
			}
			if (searchForKeyAndRemoveIt(Level.ANY, -1, key.getClass())) continue;
			return Dungeon.customDungeon.permaKey;
		}
		return true;
	}

	private static boolean searchForKeyAndRemoveIt(String compareName, int compareCell, Class<? extends Key> compareClass){
		for (KeyRecord record : getRecords(KeyRecord.class)) {
			if (record.keyCell() == compareCell && record.levelName().equals(compareName)
					&& record.key.getClass() == compareClass) {
				record.quantity(record.quantity() - 1);
				if (record.quantity() <= 0) {
					records.remove(record);
				}
				return true;
			}
		}
		return compareClass != SkeletonKey.class && compareCell != -1 && searchForKeyAndRemoveIt(compareName, compareCell, SkeletonKey.class);
	}
	
	public static int keyCount( Key key ){
		if (Dungeon.customDungeon.permaKey) return Integer.MAX_VALUE;

		int quantity = 0;
		for (KeyRecord record : getRecords(KeyRecord.class)) {
			if (record.levelName().equals(Level.ANY) || record.levelName().equals(key.levelName)
					&& (record.keyCell() == -1 || record.keyCell() == key.cell)
					&& ((record.key.getClass() == SkeletonKey.class && record.keyCell() != -1) || key.getClass() == record.key.getClass())) {
				quantity += record.quantity();
			}
		}
		return quantity;
	}
	
	public static ArrayList<Record> getRecords(){
		return getRecords(Record.class);
	}
	
	public static <T extends Record> ArrayList<T> getRecords( Class<T> recordType ){
		ArrayList<T> filtered = new ArrayList<>();
		for (Record rec : records){
			if (recordType.isInstance(rec)){
				filtered.add((T)rec);
			}
		}
		return filtered;
	}
	
	public static void remove( Record rec ){
		records.remove(rec);
	}
	
}