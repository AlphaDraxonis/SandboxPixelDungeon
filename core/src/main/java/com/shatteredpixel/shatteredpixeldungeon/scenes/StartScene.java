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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SandboxPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.OpenDungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NotAllowedInLua
public class StartScene extends PixelScene {
	
	private static final int SLOT_WIDTH = 120;
	private static final int SLOT_HEIGHT = 22;
	
	@Override
	public void create() {
		super.create();
		
		Badges.loadGlobal();
		Journal.loadGlobal();
		
		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		ExitButton btnExit = new ExitButton() {
			@Override
			protected void onClick() {
				super.onClick();
				skipDungeonSelection = false;
			}
		};
		btnExit.setPos( w - btnExit.width(), 0 );
		add( btnExit );
		
		
		
		ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
		
		LinkedHashMap<String, List<GamesInProgress.Info>> runsInEachDungeon = new LinkedHashMap<>();
		
		for (GamesInProgress.Info game : games) {
			List<GamesInProgress.Info> runs = runsInEachDungeon.get(game.dungeonName);
			if (runs == null) {
				runs = new ArrayList<>();
				runsInEachDungeon.put(game.dungeonName, runs);
			}
			runs.add(game);
		}
		
		MultiWindowTabComp dungeonSelection = new MultiWindowTabComp() {
			{
				title = new IconTitle( Icons.ENTER.get(), Messages.get(StartScene.this, "title"));
				add(title);
				
				mainWindowComps = new Component[runsInEachDungeon.size() + 1];
				int i = 0;
				for (Map.Entry<String, List<GamesInProgress.Info>> entry : runsInEachDungeon.entrySet()) {
					content.add(
							mainWindowComps[i++] = new DungeonWithRuns(entry.getKey(), entry.getValue()) {
								@Override
								protected void onClick() {
									changeContent(
											PixelScene.renderTextBlock(entry.getKey(), 9),
											getIDKHowToName(entry.getValue(), sp.height()),
											getOutsideSp(entry.getKey()),
											0.5f, 0.5f);
								}
							}
					);
				}
				SaveSlotButton newGame = new SaveSlotButton(null) {
					@Override
					protected void layout() {
						height = Math.max(height, SLOT_HEIGHT);
						super.layout();
					}
				};
				newGame.set(GamesInProgress.firstEmpty());
				content.add(newGame);
				mainWindowComps[i++] = newGame;
			}
			@Override
			public Image createIcon() {
				return null;
			}
			
			@Override
			public String hoverText() {
				return "";
			}
			
			@Override
			public void layout() {
				float oldY = y;
				title.setSize(200, 0);
				y = (20 - title.height()) / 2f;
				height = oldY - y + height;
				super.layout();
			}
			
			@Override
			protected void layoutOwnContent() {
				title.setPos(
						(width - ((IconTitle) title).reqWidth()) / 2f,
						title.top()
				);
				align(title);
				
				content.setSize(width, 0);
				content.setSize(width, EditorUtilities.layoutStyledCompsInRectangles(5, width, content, mainWindowComps));
			}
		};
		add(dungeonSelection);
		
		dungeonSelection.setRect(10, 0, w - 20, h - 20);
		
		GamesInProgress.curSlot = GamesInProgress.NO_SLOT;
		
		fadeIn();
		
	}
	
	private static Component getOutsideSp(String dungeon) {
		return new Component() {
			private SaveSlotButton newGame;
			{
				newGame = new SaveSlotButton(dungeon);
				newGame.set(GamesInProgress.firstEmpty());
				add(newGame);
			}
			
			@Override
			protected void layout() {
				newGame.setRect(x + (width - SLOT_WIDTH) / 2f, y, SLOT_WIDTH, SLOT_HEIGHT);
				align(newGame);
				height = SLOT_HEIGHT;
			}
		};
	}
	
	private static Component getIDKHowToName(List<GamesInProgress.Info> games, float visibleHeight) {
		
		int slotCount = Math.min(GamesInProgress.MAX_SLOTS, games.size()+1);
		int slotGap = 10 - slotCount;
		int slotsHeight = slotCount*SLOT_HEIGHT + (slotCount-1)* slotGap;
		slotsHeight += 14;

		while (slotGap >= 2 && slotsHeight > visibleHeight && slotGap > 5){
			slotGap--;
			slotsHeight -= slotCount-1;
		}
		
		final int finalSlotGap = slotGap;
		return new Component() {
			private SaveSlotButton[] btns;
			{
				btns = new SaveSlotButton[games.size()];
				int i = 0;
				for (GamesInProgress.Info game : games) {
					SaveSlotButton existingGame = new SaveSlotButton(game.dungeonName);
					existingGame.set(game.slot);
					add(existingGame);
					btns[i++] = existingGame;
				}
			}
			
			@Override
			protected void layout() {
				
				float yPos = y;
				
				for (SaveSlotButton btn : btns) {
					btn.setRect(x + (width - SLOT_WIDTH) / 2f, yPos, SLOT_WIDTH, SLOT_HEIGHT);
					yPos += SLOT_HEIGHT + finalSlotGap;
					align(btn);
				}
				height = yPos - y - finalSlotGap;
			}
		};
	}

	@Override
	protected void onBackPressed() {
		SandboxPixelDungeon.switchNoFade( TitleScene.class );
	}
	
	private static class DungeonWithRuns extends StyledButton {
		
		private Image stairIcon;
		private RenderedTextBlock numRuns;
		
		public DungeonWithRuns(String dungeonName, List<GamesInProgress.Info> runs) {
			super(Chrome.Type.GEM, dungeonName);
			
			multiline = true;
			
			numRuns = PixelScene.renderTextBlock(Integer.toString(runs.size()), textSize());
			add(numRuns);
			stairIcon = Icons.getWithNoOffset(Level.Feeling.NONE);
			add(stairIcon);
		}
		
		@Override
		protected void layout() {
			height = Math.max(height, getMinimumHeight(width));
			super.layout();
			stairIcon.x = x + bg.marginLeft() * 0.5f + 1;
			stairIcon.y = y + bg.marginTop() * 0.5f + 1;
			numRuns.setPos(stairIcon.x + stairIcon.width(), stairIcon.y + (stairIcon.height() - numRuns.height()) * 0.5f);
			PixelScene.align(stairIcon);
			PixelScene.align(numRuns);
		}
		
		@Override
		public float getMinimumHeight(float width) {
			return Math.max(20, super.getMinimumHeight(width));
		}
	}
	
	
	private static class SaveSlotButton extends Button {
		
		private NinePatch bg;
		
		private Image hero;
		private RenderedTextBlock name;
		private RenderedTextBlock lastPlayed;
		
		private Image steps;
		private BitmapText depth;
		private Image classIcon;
		private BitmapText level;
		
		private int slot;
		private boolean newGame;
		
		private final String dungeon;
		
		public SaveSlotButton(String dungeon) {
			this.dungeon = dungeon;
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get(Chrome.Type.TOAST_TR);
			add( bg );
			
			name = PixelScene.renderTextBlock(9);
			add(name);

			lastPlayed = PixelScene.renderTextBlock(6);
			add(lastPlayed);
		}
		
		public void set( int slot ){
			this.slot = slot;
			GamesInProgress.Info info = GamesInProgress.check(slot);
			newGame = info == null;
			if (newGame){
				name.text( Messages.get(StartScene.class, "new"));
				
				if (hero != null){
					remove(hero);
					hero = null;
					remove(steps);
					steps = null;
					remove(depth);
					depth = null;
					remove(classIcon);
					classIcon = null;
					remove(level);
					level = null;
				}
			} else {
				
				if (info.subClass != HeroSubClass.NONE){
					name.text(Messages.titleCase(info.subClass.title()));
				} else {
					name.text(Messages.titleCase(info.heroClass.title()));
				}
				
				if (hero == null){
					hero = new Image(info.heroClass.spritesheet(), 0, 15*info.armorTier, 12, 15);
					add(hero);
					
					steps = new Image(Icons.get(Icons.STAIRS));
					add(steps);
					depth = new BitmapText(PixelScene.pixelFont);
					add(depth);
					
					classIcon = new Image(Icons.get(info.heroClass));
					add(classIcon);
					level = new BitmapText(PixelScene.pixelFont);
					add(level);
				} else {
					hero.copy(new Image(info.heroClass.spritesheet(), 0, 15*info.armorTier, 12, 15));
					
					classIcon.copy(Icons.get(info.heroClass));
				}

				long diff = Game.realTime - info.lastPlayed;
				if (diff > 99L * 30 * 24 * 60 * 60_000){
					lastPlayed.text(" "); //show no text for >99 months ago
				} else if (diff < 60_000){
					lastPlayed.text(Messages.get(StartScene.class, "one_minute_ago"));
				} else if (diff < 2 * 60 * 60_000){
					lastPlayed.text(Messages.get(StartScene.class, "minutes_ago", diff / 60_000));
				} else if (diff < 2 * 24 * 60 * 60_000){
					lastPlayed.text(Messages.get(StartScene.class, "hours_ago", diff / (60 * 60_000)));
				} else if (diff < 2L * 30 * 24 * 60 * 60_000){
					lastPlayed.text(Messages.get(StartScene.class, "days_ago", diff / (24 * 60 * 60_000)));
				} else {
					lastPlayed.text(Messages.get(StartScene.class, "months_ago", diff / (30L * 24 * 60 * 60_000)));
				}
				
				depth.text(Integer.toString(info.depth));
				depth.measure();
				
				level.text(Integer.toString(info.level));
				level.measure();
				
				if (info.challenges > 0){
					name.hardlight(Window.TITLE_COLOR);
					lastPlayed.hardlight(Window.TITLE_COLOR);
					depth.hardlight(Window.TITLE_COLOR);
					level.hardlight(Window.TITLE_COLOR);
				} else {
					name.resetColor();
					lastPlayed.resetColor();
					depth.resetColor();
					level.resetColor();
				}

				if (info.daily){
					if (info.dailyReplay){
						steps.hardlight(1f, 0.5f, 2f);
					} else {
						steps.hardlight(0.5f, 1f, 2f);
					}
				} else if (!info.customSeed.isEmpty()){
					steps.hardlight(1f, 1.5f, 0.67f);
				}
				
			}
			
			layout();
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			if (hero != null){
				hero.x = x+8;
				hero.y = y + (height - hero.height())/2f;
				align(hero);
				
				name.setPos(
						hero.x + hero.width() + 6,
						y + (height - name.height() - lastPlayed.height() - 2)/2f
				);
				align(name);

				lastPlayed.setPos(
						hero.x + hero.width() + 6,
						name.bottom()+2
				);
				
				classIcon.x = x + width - 24 + (16 - classIcon.width())/2f;
				classIcon.y = y + (height - classIcon.height())/2f;
				align(classIcon);
				
				level.x = classIcon.x + (classIcon.width() - level.width()) / 2f;
				level.y = classIcon.y + (classIcon.height() - level.height()) / 2f + 1;
				align(level);
				
				steps.x = x + width - 40 + (16 - steps.width())/2f;
				steps.y = y + (height - steps.height())/2f;
				align(steps);
				
				depth.x = steps.x + (steps.width() - depth.width()) / 2f;
				depth.y = steps.y + (steps.height() - depth.height()) / 2f + 1;
				align(depth);
				
			} else {
				name.setPos(
						x + (width - name.width())/2f,
						y + (height - name.height())/2f
				);
				align(name);
			}
			
			
		}

		@Override
		protected void onClick() {
			if (newGame) {
				if (skipDungeonSelection) {
					skipDungeonSelection = false;
					GamesInProgress.selectedClass = null;
					GamesInProgress.curSlot = slot;
					SandboxPixelDungeon.switchScene(HeroSelectScene.class);
				} else {
					showWndSelectDungeon(slot, null, dungeon);
				}
			} else {
				SandboxPixelDungeon.scene().add( new WndGameInProgress(slot));
			}
		}
	}

	private static final Set<String> EMPTY_HASHSET = new HashSet<>(0);
	public static boolean skipDungeonSelection = false;

	public static void showWndSelectDungeon(int slot) {
		showWndSelectDungeon(slot, null);
	}

	public static void showWndSelectDungeon(int slot, HeroClass selectClass) {
		showWndSelectDungeon(slot, selectClass, null);
	}

	public static void showWndSelectDungeon(int slot, HeroClass selectClass, String featuredDungeon) {

		EditorScene.close();
		List<CustomDungeonSaves.Info> allInfos = CustomDungeonSaves.getAllInfos();
		if (allInfos == null) return;
		if (allInfos.isEmpty()) {
			DungeonScene.show(new WndOptions(Icons.get(Icons.WARNING),
					Messages.get(StartScene.class, "wnd_no_dungeon_title"),
					Messages.get(StartScene.class, "wnd_no_dungeon_body"),
					Messages.get(StartScene.class, "wnd_no_dungeon_create_new"),
					Messages.get(StartScene.class, "wnd_no_dungeon_play_default"),
					Messages.get(StartScene.class, "wnd_no_dungeon_cancel")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						DungeonScene.show(new WndNewDungeon(EMPTY_HASHSET));
					} else if(index == 1) {
						Dungeon.customDungeon = new CustomDungeon(WndNewDungeon.DEFAULT_DUNGEON);
						Dungeon.customDungeon.initDefault();

						GamesInProgress.selectedClass = selectClass;
						GamesInProgress.curSlot = slot;
						SandboxPixelDungeon.switchScene(HeroSelectScene.class);
					}
				}
			});
		} else {
			CustomDungeonSaves.Info featuredInfo = null;
			if (featuredDungeon != null) {
				for (CustomDungeonSaves.Info i : allInfos) {
					if (i.name.equals(featuredDungeon)) {
						featuredInfo = i;
						break;
					}
				}
			}
			if (featuredInfo != null) allInfos.remove(featuredInfo);
			DungeonScene.show(new WndSelectDungeon(allInfos,false, featuredInfo) {
				@Override
				protected void select(String customDungeonName) {
					GamesInProgress.selectedClass = selectClass;
					GamesInProgress.curSlot = slot;
					
					OpenDungeonScene.openDungeon(customDungeonName, OpenDungeonScene.Mode.GAME_LOAD);
				}
			});
		}
	}
}