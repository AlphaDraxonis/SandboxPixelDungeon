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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AbstractCategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.CategoryScroller;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.CustomDocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesGrid;
import com.shatteredpixel.shatteredpixeldungeon.ui.BadgesList;
import com.shatteredpixel.shatteredpixeldungeon.ui.CustomNoteButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickRecipe;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingGridPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WndJournal extends WndTabbed {
	
	public static final int WIDTH_P     = 126;
	public static final int HEIGHT_P    = 180;
	
	public static final int WIDTH_L     = 216;
	public static final int HEIGHT_L    = 130;
	
	private static final int ITEM_HEIGHT	= 18;
	
	private GuideTab guideTab;
	private AlchemyTab alchemyTab;
	private NotesTab notesTab;
	private CatalogTab catalogTab;
	private BadgesTab badgesTab;
	
	public static int last_index = 0;

	private static WndJournal INSTANCE = null;
	
	public WndJournal(){

		if (INSTANCE != null){
			INSTANCE.hide();
		}
		
		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
		int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
		
		resize(width, height);
		
		guideTab = new GuideTab();
		add(guideTab);
		guideTab.setRect(0, 0, width, height);
		guideTab.updateList();
		
		alchemyTab = new AlchemyTab();
		add(alchemyTab);
		alchemyTab.setRect(0, 0, width, height);
		
		notesTab = new NotesTab();
		add(notesTab);
		notesTab.setRect(0, 0, width, height);
		notesTab.updateList();
		
		catalogTab = new CatalogTab();
		add(catalogTab);
		catalogTab.setRect(0, 0, width, height);
		catalogTab.updateList();

		badgesTab = new BadgesTab();
		add(badgesTab);
		badgesTab.setRect(0, 0, width, height);
		badgesTab.updateList();
		
		Tab[] tabs = {
				new IconTab( Icons.JOURNAL.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						notesTab.active = notesTab.visible = value;
						if (value) last_index = 0;
					}

					@Override
					protected String hoverText() {
						return Messages.get(notesTab, "title");
					}
				},
				new IconTab( new ItemSprite(ItemSpriteSheet.MASTERY, null) ) {
					protected void select( boolean value ) {
						super.select( value );
						guideTab.active = guideTab.visible = value;
						if (value) last_index = 1;
					}

					@Override
					protected String hoverText() {
						return Messages.get(guideTab, "title");
					}
				},
				new IconTab( Icons.ALCHEMY.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						alchemyTab.active = alchemyTab.visible = value;
						if (value) last_index = 2;
					}

					@Override
					protected String hoverText() {
						return Messages.get(alchemyTab, "title");
					}
				},
				new IconTab( Icons.CATALOG.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						catalogTab.active = catalogTab.visible = value;
						if (value) last_index = 3;
					}

					@Override
					protected String hoverText() {
						return Messages.get(catalogTab, "title");
					}
				},
				new IconTab( Icons.BADGES.get() ) {
					protected void select( boolean value ) {
						super.select( value );
						badgesTab.active = badgesTab.visible = value;
						if (value) last_index = 4;
					}

					@Override
					protected String hoverText() {
						return Messages.get(badgesTab, "title");
					}
				}
		};

		for (Tab tab : tabs) {
			add( tab );
		}
		
		layoutTabs();
		
		select(last_index);

		INSTANCE = this;
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.JOURNAL) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		guideTab.layout();
		alchemyTab.layout();
		notesTab.layout();
		catalogTab.layout();
	}
	
	public static class GuideTab extends Component {

		private ScrollingListPane list;
		
		@Override
		protected void createChildren() {
			list = new ScrollingListPane();
			add( list );
		}
		
		@Override
		protected void layout() {
			super.layout();
			list.setRect( x, y, width, height);
		}
		
		public void updateList(){
			list.addTitle(Document.ADVENTURERS_GUIDE.title());

			for (String page : Document.ADVENTURERS_GUIDE.pageNames()){
				boolean found = Document.ADVENTURERS_GUIDE.isPageFound(page);
				ScrollingListPane.ListItem item = new ScrollingListPane.ListItem(
						Document.ADVENTURERS_GUIDE.pageSprite(page),
						null,
						found ? Messages.titleCase(Document.ADVENTURERS_GUIDE.pageTitle(page)) : Messages.titleCase(Messages.get( this, "missing" ))
				){
					@Override
					public boolean onClick(float x, float y) {
						if (inside( x, y ) && found) {
							DungeonScene.show( new WndStory( Document.ADVENTURERS_GUIDE.pageSprite(page),
									Document.ADVENTURERS_GUIDE.pageTitle(page),
									Document.ADVENTURERS_GUIDE.pageBody(page) ));
							Document.ADVENTURERS_GUIDE.readPage(page);
							return true;
						} else {
							return false;
						}
					}
				};
				if (!found){
					item.hardlight(0x999999);
					item.hardlightIcon(0x999999);
				}
				list.addItem(item);
			}

			list.setRect(x, y, width, height);
		}

	}
	
	public static class AlchemyTab extends Component {

		private static final int[] sprites = {
				ItemSpriteSheet.SEED_HOLDER,
				ItemSpriteSheet.STONE_HOLDER,
				ItemSpriteSheet.FOOD_HOLDER,
				ItemSpriteSheet.POTION_HOLDER,
				ItemSpriteSheet.SCROLL_HOLDER,
				ItemSpriteSheet.BOMB_HOLDER,
				ItemSpriteSheet.MISSILE_HOLDER,
				ItemSpriteSheet.ELIXIR_HOLDER,
				ItemSpriteSheet.SPELL_HOLDER,
				ItemSpriteSheet.SOMETHING
		};
		
		public static int currentPageIdx   = 0;

		protected AbstractCategoryScroller<?> categoryScroller;

		private IconTitle title;
		private RenderedTextBlock body;

		private ArrayList<QuickRecipe> recipes;

		private boolean skipPageLayouting;

		@Override
		protected void createChildren() {

			recipes = new ArrayList<>();

			title = new IconTitle();
			title.icon( new ItemSprite(ItemSpriteSheet.ALCH_PAGE));
			title.visible = false;

			body = PixelScene.renderTextBlock(6);


			AbstractCategoryScroller.Category[] cats = new AbstractCategoryScroller.Category[10];
			for (int i = 0; i < cats.length; i++) {
				final int idx = i;
				cats[idx] = new AbstractCategoryScroller.Category() {
					@Override
					public List<?> createItems(boolean required) {
						return QuickRecipe.getRecipes(idx);
					}

					@Override
					public Image getImage() {
						return new ItemSprite(sprites[idx], null);
					}

					@Override
					public String getName() {
						return Document.ALCHEMY_GUIDE.pageTitle(idx);
					}
				};
			}

			categoryScroller = new CategoryScroller(cats, null) {

				@Override
				protected ScrollPane createSp() {
					return new ScrollPane(new Component());
				}

				@Override
				protected RedButton createCategoryComp(int index, Category category) {
					RedButton btn = super.createCategoryComp(index, category);

					if (!Document.ALCHEMY_GUIDE.isPageFound(index)) {
						btn.icon(new ItemSprite(ItemSpriteSheet.SOMETHING, null));
						btn.enable(false);
					}

					return btn;
				}

				@Override
				protected void updateList(int selectedCatIndex) {

					if (skipPageLayouting) return;

					currentPageIdx = selectedCatIndex;

					if (currentPageIdx != -1 && !Document.ALCHEMY_GUIDE.isPageFound(currentPageIdx)){
						currentPageIdx = -1;
					}

					if (currentPageIdx == -1){
						return;
					}

					for (QuickRecipe r : recipes){
						if (r != null) {
							r.killAndErase();
							r.destroy();
						}
					}
					recipes.clear();

					Component content = sp.content();

					content.clear();

					title.visible = true;
					title.label(cats[currentPageIdx].getName());
					title.setRect(0, 0, width(), 10);
					content.add(title);

					body.maxWidth((int)width());
					body.text(Document.ALCHEMY_GUIDE.pageBody(currentPageIdx));
					body.setPos(0, title.bottom());
					content.add(body);

					Document.ALCHEMY_GUIDE.readPage(currentPageIdx);

					ArrayList<QuickRecipe> toAdd = (ArrayList<QuickRecipe>) cats[currentPageIdx].createItems(true);

					float left;
					float top = body.bottom()+2;
					int w;
					ArrayList<QuickRecipe> toAddThisRow = new ArrayList<>();
					while (!toAdd.isEmpty()){
						if (toAdd.get(0) == null){
							toAdd.remove(0);
							top += 6;
						}

						w = 0;
						while(!toAdd.isEmpty() && toAdd.get(0) != null
								&& w + toAdd.get(0).width() <= width()){
							toAddThisRow.add(toAdd.remove(0));
							w += toAddThisRow.get(0).width();
						}

						float spacing = (width() - w)/(toAddThisRow.size() + 1);
						left = spacing;
						while (!toAddThisRow.isEmpty()){
							QuickRecipe r = toAddThisRow.remove(0);
							r.setPos(left, top);
							left += r.width() + spacing;
							if (!toAddThisRow.isEmpty()) {
								ColorBlock spacer = new ColorBlock(1, 16, ColorBlock.SEPARATOR_COLOR);
								spacer.y = top;
								spacer.x = left - spacing / 2 - 0.5f;
								PixelScene.align(spacer);
								content.add(spacer);
							}
							recipes.add(r);
							content.add(r);
						}

						if (!toAdd.isEmpty() && toAdd.get(0) == null){
							toAdd.remove(0);
						}

						if (!toAdd.isEmpty() && toAdd.get(0) != null) {
							ColorBlock spacer = new ColorBlock(width(), 1, ColorBlock.SEPARATOR_COLOR);
							spacer.y = top + 16;
							spacer.x = 0;
							content.add(spacer);
						}
						top += 17;
						toAddThisRow.clear();
					}
					top -= 1;
					content.setSize(width(), top);
					sp.givePointerPriority();
				}
			};
			add(categoryScroller);

		}

		@Override
		protected void layout() {
			super.layout();
			skipPageLayouting = true;
			categoryScroller.setRect(x, y, width, height);
			skipPageLayouting = false;
			updateList();
		}

		public void updateList() {
			categoryScroller.selectCategory(currentPageIdx);
		}
	}
	
	private static class NotesTab extends Component {
		
		private ScrollingGridPane grid;
		private CustomNoteButton custom;
		
		@Override
		protected void createChildren() {
			grid = new ScrollingGridPane();
			add(grid);
		}
		
		@Override
		protected void layout() {
			super.layout();
			grid.setRect( x, y, width, height);
		}
		
		private void updateList(){

			grid.addHeader("_" + Messages.get(this, "title") + "_", 9, true);

			grid.addHeader(Messages.get(this, "desc"), 6, true);

			ArrayList<Notes.CustomRecord> customRecs = Notes.getRecords(Notes.CustomRecord.class);

			if (!customRecs.isEmpty()){
				grid.addHeader("_" + Messages.get(this, "custom_notes") + "_ (" + customRecs.size() + "/" + Notes.customRecordLimit() + ")");

				for (Notes.CustomRecord rec : customRecs){
					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new CustomNoteButton.CustomNoteWindow(rec));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			java.util.List<LevelScheme> sortedVisitedFloors = new ArrayList<>();
			oneFloor:
			for (String levelName : Dungeon.customDungeon.floorNames()) {
				for (String visited : Dungeon.visited) {
					if (visited.equals(levelName)) {
						sortedVisitedFloors.add(Dungeon.customDungeon.getFloor(levelName));
						continue oneFloor;
					}
				}
			}
			Collections.sort(sortedVisitedFloors, new Comparator<LevelScheme>() {
				@Override
				public int compare(LevelScheme o1, LevelScheme o2) {
					return Integer.compare(o2.getDepth(), o1.getDepth());
				}
			});

			for (LevelScheme ls : sortedVisitedFloors) {
				String name = ls.getName();

				ArrayList<Notes.Record> recs = Notes.getRecords(name);

				if (name.equals(Dungeon.levelName)) {
					grid.addHeader("_" + Messages.get(this, "floor_header", name) + "_");
				} else {
					grid.addHeader(Messages.get(this, "floor_header", name));
				}
				for( Notes.Record rec : recs){

					ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(rec.icon()){
						@Override
						public boolean onClick(float x, float y) {
							if (inside(x, y)) {
								GameScene.show(new WndJournalItem(rec.icon(),
										Messages.titleCase(rec.title()),
										rec.desc()));
								return true;
							} else {
								return false;
							}
						}
					};

					Visual secondIcon = rec.secondIcon();
					if (secondIcon != null){
						gridItem.addSecondIcon( secondIcon );
					}

					grid.addItem(gridItem);
				}
			}

			custom = new CustomNoteButton();
			grid.content().add(custom);
			custom.setPos(width-custom.width()-1, 0);

			grid.setRect(x, y, width, height);

		}
		
	}

	public static class CatalogTab extends Component {

		private RedButton[] itemButtons;
		private static final int MAX_NUM_BUTTONS = 4;

		public static int currentItemIdx   = 0;
		private static float[] scrollPositions = new float[MAX_NUM_BUTTONS];

		//sprite locations
		public static final int EQUIP_IDX = 0;
		public static final int CONSUM_IDX = 1;
		public static final int BESTIARY_IDX = 2;
		public static final int LORE_IDX = 3;

		private int numButtons;

		private ScrollingGridPane grid;
		
		@Override
		protected void createChildren() {

			numButtons = MAX_NUM_BUTTONS;

			if (Dungeon.customDungeon == null || Dungeon.customDungeon.foundPages.isEmpty())
				numButtons--;

			itemButtons = new RedButton[numButtons];
			for (int i = 0; i < itemButtons.length; i++){
				final int idx = i;
				itemButtons[i] = new RedButton( "" ){
					@Override
					protected void onClick() {
						currentItemIdx = idx;
						updateList();
					}
				};
				add( itemButtons[i] );
			}
			itemButtons[EQUIP_IDX].icon(new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER));
			itemButtons[CONSUM_IDX].icon(new ItemSprite(ItemSpriteSheet.POTION_HOLDER));
			itemButtons[BESTIARY_IDX].icon(new ItemSprite(ItemSpriteSheet.MOB_HOLDER));
			if (itemButtons.length > LORE_IDX) itemButtons[LORE_IDX].icon(new ItemSprite(ItemSpriteSheet.DOCUMENT_HOLDER));

			grid = new ScrollingGridPane(){
				@Override
				public synchronized void update() {
					super.update();
					scrollPositions[currentItemIdx] = content.camera.scroll.y;
				}
			};
			add( grid );
		}
		
		@Override
		protected void layout() {
			super.layout();

			int perRow = itemButtons.length;
			float buttonWidth = width()/perRow;

			for (int i = 0; i < itemButtons.length; i++) {
				itemButtons[i].setRect(x +(i%perRow) * (buttonWidth),
						y + (i/perRow) * (ITEM_HEIGHT ),
						buttonWidth, ITEM_HEIGHT);
				PixelScene.align(itemButtons[i]);
			}
			
			grid.setRect(x,
					itemButtons[itemButtons.length -1].bottom() + 1,
					width,
					height - itemButtons[itemButtons.length -1].height() - 1);
		}
		
		public void updateList() {
			
			grid.clear();

			for (int i = 0; i < itemButtons.length; i++){
				if (i == currentItemIdx){
					itemButtons[i].icon().color(TITLE_COLOR);
				} else {
					itemButtons[i].icon().resetColor();
				}
			}
			
			grid.scrollTo( 0, 0 );

			if (currentItemIdx == EQUIP_IDX) {
				int totalItems = 0;
				int totalSeen = 0;
				for (Catalog catalog : Catalog.equipmentCatalogs){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_equipment") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Catalog catalog : Catalog.equipmentCatalogs){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == CONSUM_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (Catalog catalog : Catalog.consumableCatalogs){
					totalItems += catalog.totalItems();
					totalSeen += catalog.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_consumables") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Catalog catalog : Catalog.consumableCatalogs){
					grid.addHeader("_" + Messages.titleCase(catalog.title()) + "_ (" + catalog.totalSeen() + "/" + catalog.totalItems() + "):");
					addGridItems(grid, catalog.items());
				}

			} else if (currentItemIdx == BESTIARY_IDX){
				int totalItems = 0;
				int totalSeen = 0;
				for (Bestiary bestiary : Bestiary.values()){
					totalItems += bestiary.totalEntities();
					totalSeen += bestiary.totalSeen();
				}
				grid.addHeader("_" + Messages.get(this, "title_bestiary") + "_ (" + totalSeen + "/" + totalItems + ")", 9, true);

				for (Bestiary bestiary : Bestiary.values()){
					grid.addHeader("_" + Messages.titleCase(bestiary.title()) + "_ (" + bestiary.totalSeen() + "/" + bestiary.totalEntities() + "):");
					addGridEntities(grid, bestiary.entities());
				}

			} else {
				grid.addHeader(Messages.get(this, "title_lore"), 9, true);

				if (Dungeon.customDungeon != null) {
					addGridDocuments(grid, Dungeon.customDungeon.foundPages);
				}
			}

			grid.setRect(x, itemButtons[numButtons -1].bottom() + 1, width,
					height - itemButtons[numButtons -1].height() - 1);

			grid.scrollTo(0, scrollPositions[currentItemIdx]);
		}
		
	}

	//also includes item-like things such as enchantments, glyphs, curses.
	private static void addGridItems( ScrollingGridPane grid, Collection<Class<?>> classes) {
		for (Class<?> itemClass : classes) {

			boolean seen = Catalog.isSeen(itemClass);;
			ItemSprite sprite = null;
			Image secondIcon = null;
			String title = "";
			String desc = "";

			if (Item.class.isAssignableFrom(itemClass)) {

				Item item = (Item) Reflection.newInstance(itemClass);

				if (seen) {
					if (item instanceof Ring) {
						((Ring) item).anonymize();
					} else if (item instanceof Potion) {
						((Potion) item).anonymize();
					} else if (item instanceof Scroll) {
						((Scroll) item).anonymize();
					}
				}

				sprite = new ItemSprite(item.image, seen ? item.glowing() : null);
				if (!seen)  {
					sprite.lightness(0);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_item");
				} else {
					title = Messages.titleCase( item.name() );
					//some items don't include direct stats, generally when they're not applicable
					if (item instanceof ClassArmor || item instanceof SpiritBow){
						desc += item.desc();
					} else {
						desc += item.info();
					}

					if (Catalog.useCount(itemClass) > 1) {
						if (item.isUpgradable() || item instanceof Artifact) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "upgrade_count", Catalog.useCount(itemClass));
						} else if (item instanceof Trinket) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "trinket_count", Catalog.useCount(itemClass));
						} else if (item instanceof Gold) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "gold_count", Catalog.useCount(itemClass));
						} else if (item instanceof EnergyCrystal) {
							desc += "\n\n" + Messages.get(CatalogTab.class, "energy_count", Catalog.useCount(itemClass));
						} else {
							desc += "\n\n" + Messages.get(CatalogTab.class, "use_count", Catalog.useCount(itemClass));
						}
					}

					//mage's staff normally has 2 pixels extra at the top for particle effects, we chop that off here
					if (item instanceof MagesStaff){
						RectF frame = sprite.frame();
						frame.top += frame.height()/8f;
						sprite.frame(frame);
					}

					if (item.icon != -1) {
						secondIcon = new Image(Assets.Sprites.ITEM_ICONS);
						secondIcon.frame(ItemSpriteSheet.Icons.film.get(item.icon));
					}
				}

			} else if (Weapon.Enchantment.class.isAssignableFrom(itemClass)){

				Weapon.Enchantment ench = (Weapon.Enchantment) Reflection.newInstance(itemClass);

				if (seen){
					sprite = new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD, ench.glowing());
					title = Messages.titleCase(ench.name());
					desc = ench.desc();
				} else {
					sprite = new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD);
					sprite.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_enchantment");
				}

			} else if (Armor.Glyph.class.isAssignableFrom(itemClass)){

				Armor.Glyph glyph = (Armor.Glyph) Reflection.newInstance(itemClass);

				if (seen){
					sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH, glyph.glowing());
					title = Messages.titleCase(glyph.name());
					desc = glyph.desc();
				} else {
					sprite = new ItemSprite(ItemSpriteSheet.ARMOR_CLOTH);
					sprite.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_glyph");
				}

			}

			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image sprite = new ItemSprite();
						sprite.copy(icon);
						DungeonScene.show(new WndJournalItem(sprite, finalTitle, finalDesc));
						return true;
					} else {
						return false;
					}
				}
			};
			if (secondIcon != null){
				gridItem.addSecondIcon(secondIcon);
			}
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	}

	private static void addGridEntities(ScrollingGridPane grid, Collection<Class<?>> classes) {
		for (Class<?> entityCls : classes){

			boolean seen = Bestiary.isSeen(entityCls)||true;
			Mob mob = null;
			Image icon = null;
			String title = null;
			String desc = null;

			if (Mob.class.isAssignableFrom(entityCls)) {

				mob = (Mob) Reflection.newInstance(entityCls);

				if (mob instanceof Mimic || mob instanceof Pylon || mob instanceof CrystalSpire) {
					mob.alignment = Char.Alignment.ENEMY;
				}
				if (mob instanceof WandOfWarding.Ward){
					if (mob instanceof WandOfWarding.Ward.WardSentry){
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
						((WandOfWarding.Ward) mob).upgrade(3);
					} else {
						((WandOfWarding.Ward) mob).upgrade(0);
					}
				}

				CharSprite sprite = mob.createSprite();
				sprite.idle();

				icon = new Image(sprite);
				if (seen) {
					title = Messages.titleCase(mob.name());
					desc = mob.desc();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "enemy_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					desc = mob.alignment == Char.Alignment.ENEMY ? Messages.get(CatalogTab.class, "not_seen_enemy") : Messages.get(CatalogTab.class, "not_seen_ally");
				}

				//we have to clip the bounds of the sprite if it's too large
				if (icon.width() >= 17 || icon.height() >= 17) {
					RectF frame = icon.frame();

					float wShrink = frame.width() * (1f - 17f / icon.width());
					if (wShrink > 0) {
						frame.left += wShrink / 2f;
						frame.right -= wShrink / 2f;
					}
					float hShrink = frame.height() * (1f - 17f / icon.height());
					if (hShrink > 0) {
						frame.top += hShrink / 2f;
						frame.bottom -= hShrink / 2f;
					}
					icon.frame(frame);
				}
			} else if (Trap.class.isAssignableFrom(entityCls)){

				Trap trap = (Trap) Reflection.newInstance(entityCls);
				icon = TerrainFeaturesTilemap.getTrapVisual(trap);

				if (seen) {
					title = Messages.titleCase(trap.name());
					desc = trap.desc();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "trap_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_trap");
				}

			} else if (Plant.class.isAssignableFrom(entityCls)){

				Plant plant = (Plant) Reflection.newInstance(entityCls);
				icon = TerrainFeaturesTilemap.getPlantVisual(plant);

				if (seen) {
					title = Messages.titleCase(plant.name());
					desc = plant.desc();
					if (Bestiary.encounterCount(entityCls) > 1){
						desc += "\n\n" + Messages.get(CatalogTab.class, "plant_count", Bestiary.encounterCount(entityCls));
					}
				} else {
					icon.lightness(0f);
					title = "???";
					desc = Messages.get(CatalogTab.class, "not_seen_plant");
				}

			}

			Mob finalMob = mob;
			String finalTitle = title;
			String finalDesc = desc;
			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(icon) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image image;
						if (seen && finalMob != null){
							image = finalMob.createSprite();
						} else {
							image = new Image(icon);
						}

						DungeonScene.show(new WndJournalItem(image, finalTitle, finalDesc));

						return true;
					} else {
						return false;
					}
				}
			};
			if (!seen) {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	};

	private static void addGridDocuments( ScrollingGridPane grid, List<CustomDocumentPage> docs ){
		for (CustomDocumentPage doc : docs){

			Image sprite = new ItemSprite(doc.image());

			boolean read = doc.isPageRead();
			boolean seen = true;

			if (!seen){
				sprite.lightness(0f);
			}

			ScrollingGridPane.GridItem gridItem = new ScrollingGridPane.GridItem(sprite) {
				@Override
				public boolean onClick(float x, float y) {
					if (inside(x, y)) {
						Image sprite = new Image(icon);
						if (seen) {
							DungeonScene.show(new WndStory(sprite, doc.pageTitle(), doc.pageBody()));

							doc.readPage();
							hardLightBG(1, 1, 1);
						} else {
							DungeonScene.show(new WndJournalItem(sprite, "???", Messages.get(CatalogTab.class, "not_seen_lore")));

						}
						return true;
					} else {
						return false;
					}
				}
			};

			if (seen && doc.quantity() != 1){
				BitmapText text = new BitmapText(Integer.toString(doc.quantity()), PixelScene.pixelFont);
				text.measure();
				gridItem.addSecondIcon( text );
				if (!read) {
					gridItem.hardLightBG(1f, 1f, 2f);
				}
			} else {
				gridItem.hardLightBG(2f, 1f, 2f);
			}
			grid.addItem(gridItem);
		}
	}

	public static class BadgesTab extends Component {

		private RedButton btnLocal;
		private RedButton btnGlobal;

		private RenderedTextBlock title;

		private Component badgesLocal;
		private Component badgesGlobal;

		public static boolean global = false;

		@Override
		protected void createChildren() {

			if (Dungeon.hero != null) {
				btnLocal = new RedButton(Messages.get(this, "this_run")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = false;
						updateList();
					}
				};
				btnLocal.icon(Icons.BADGES.get());
				add(btnLocal);

				btnGlobal = new RedButton(Messages.get(this, "overall")) {
					@Override
					protected void onClick() {
						super.onClick();
						global = true;
						updateList();
					}
				};
				btnGlobal.icon(Icons.BADGES.get());
				add(btnGlobal);

				if (Badges.filterReplacedBadges(false).size() <= 8){
					badgesLocal = new BadgesList(false);
				} else {
					badgesLocal = new BadgesGrid(false);
				}
				add( badgesLocal );
			} else {
				title = PixelScene.renderTextBlock(Messages.get(this, "title_main_menu"), 9);
				title.hardlight(Window.TITLE_COLOR);
				add(title);
			}

			badgesGlobal = new BadgesGrid(true);
			add( badgesGlobal );
		}

		@Override
		protected void layout() {
			super.layout();

			if (btnLocal != null) {
				btnLocal.setRect(x, y, width / 2, 18);
				btnGlobal.setRect(x + width / 2, y, width / 2, 18);

				badgesLocal.setRect(x, y + 20, width, height-20);
				badgesGlobal.setRect( x, y + 20, width, height-20);
			} else {
				title.setPos( x + (width - title.width())/2, y + (12-title.height())/2);

				badgesGlobal.setRect( x, y + 14, width, height-14);
			}
		}

		private void updateList(){
			if (btnLocal != null) {
				badgesLocal.visible = badgesLocal.active = !global;
				badgesGlobal.visible = badgesGlobal.active = global;

				btnLocal.textColor(global ? Window.WHITE : Window.TITLE_COLOR);
				btnGlobal.textColor(global ? Window.TITLE_COLOR : Window.WHITE);
			} else {
				badgesGlobal.visible = badgesGlobal.active = true;
			}
		}

	}

}