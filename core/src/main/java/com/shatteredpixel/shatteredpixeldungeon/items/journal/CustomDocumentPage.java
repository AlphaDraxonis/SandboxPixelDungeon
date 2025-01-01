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

package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.Arrays;
import java.util.List;

public class CustomDocumentPage extends Item {

	public static final List<String> types = Arrays.asList(/*"guide", "alchemy",*/ "sewers", "prison", "caves", "city", "halls");
	
	{
		type = 2;
		image = ItemSpriteSheet.SEWER_PAGE;
	}
	
	public int type;
	public String text, title;

	private boolean read = false;

	public void setType(int type, Heap heap) {
		this.type = type;
		image = getImage(type);
		if (heap != null) EditorScene.updateHeapImage(heap);
	}

	@Override
	public final boolean doPickUp(Hero hero, int pos) {
		GameScene.pickUpJournal(this, pos);

		boolean alreadyFound = false;
		for (CustomDocumentPage page : Dungeon.customDungeon.foundPages) {
			if (isContentIdentical(page)) {
				page.merge(this);
				alreadyFound = true;
				break;
			}
		}
		if (!alreadyFound) {
			GameScene.flashForDocument(document(), "");
			Dungeon.customDungeon.foundPages.add(this);
			WndJournal.last_index = WndJournal.CatalogTab.LORE_IDX;
		}
		Sample.INSTANCE.play( Assets.Sounds.ITEM );
		hero.spendAndNext( TIME_TO_PICK_UP );
		return true;
	}

	private boolean isContentIdentical(CustomDocumentPage other) {
		return (other.title == null ? "" : other.title).equals(title == null ? "" : title)
				&& (other.text == null ? "" : other.text).equals(text == null ? "" : text);
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public String name() {
		return Messages.get(messageKey() + ".name");
	}

	@Override
	public String desc() {
		return Messages.get(messageKey() + ".desc");
	}

	private String messageKey() {
		switch (type) {
//			case 0: return Messages.trimPackageName(GuidePage.class.getName());
//			case 1: return Messages.trimPackageName(AlchemyPage.class.getName());
			default:
				return Messages.trimPackageName(RegionLorePage.class.getName()) + "$" + types.get(type);
		}
	}

	private Document document() {
		switch (type) {
			case 0: return Document.SEWERS_GUARD;
			case 1: return Document.PRISON_WARDEN;
			case 2: return Document.CAVES_EXPLORER;
			case 3: return Document.CITY_WARLOCK;
			case 4: return Document.HALLS_KING;
		}
		return null;
	}

	private static final String TYPE = "type";
	private static final String TEXT = "text";
	private static final String TITLE = "title";
	private static final String READ = "read";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( TYPE, type );
		bundle.put( TEXT, text );
		bundle.put( TITLE, title);
		bundle.put( READ, read );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type = bundle.getInt( TYPE );
		text = bundle.getString( TEXT );
		title = bundle.getString( TITLE );
		read = bundle.getBoolean( READ );
		image = getImage(type);
	}

	public static int getImage(int type) {
		return ItemSpriteSheet.SEWER_PAGE + type;
	}

	public String pageTitle() {
		if (title == null) return "";
		String t = Messages.get(title);
		return Messages.titleCase(t == Messages.NO_TEXT_FOUND ? title : t);
	}

	public String pageBody() {
		if (text == null) return "";
		String t = Messages.get(text);
		return t == Messages.NO_TEXT_FOUND ? text : t;
	}

	public boolean isPageRead() {
		return read;
	}

	public void readPage() {
		read = true;
	}
}