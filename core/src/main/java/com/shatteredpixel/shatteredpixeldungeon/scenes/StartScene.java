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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StartScene extends PixelScene {
	
	private static final int SLOT_WIDTH = 120;
	private static final int SLOT_HEIGHT = 30;
	
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
		
		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 9);
		title.hardlight(Window.TITLE_COLOR);
		title.setPos(
				(w - title.width()) / 2f,
				(20 - title.height()) / 2f
		);
		align(title);
		add(title);
		
		ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
		
		int slotCount = Math.min(GamesInProgress.MAX_SLOTS, games.size()+1);
		int slotGap = 10 - slotCount;
		int slotsHeight = slotCount*SLOT_HEIGHT + (slotCount-1)* slotGap;

		while (slotsHeight > (h-title.bottom()-2)){
			slotGap--;
			slotsHeight -= slotCount-1;
		}
		
		float yPos = (h - slotsHeight + title.bottom() + 2)/2f;
		
		for (GamesInProgress.Info game : games) {
			SaveSlotButton existingGame = new SaveSlotButton();
			existingGame.set(game.slot);
			existingGame.setRect((w - SLOT_WIDTH) / 2f, yPos, SLOT_WIDTH, SLOT_HEIGHT);
			yPos += SLOT_HEIGHT + slotGap;
			align(existingGame);
			add(existingGame);
			
		}
		
		if (games.size() < GamesInProgress.MAX_SLOTS){
			SaveSlotButton newGame = new SaveSlotButton();
			newGame.set(GamesInProgress.firstEmpty());
			newGame.setRect((w - SLOT_WIDTH) / 2f, yPos, SLOT_WIDTH, SLOT_HEIGHT);
			yPos += SLOT_HEIGHT + slotGap;
			align(newGame);
			add(newGame);
		}
		
		GamesInProgress.curSlot = 0;
		
		fadeIn();
		
	}
	
	@Override
	protected void onBackPressed() {
		SandboxPixelDungeon.switchNoFade( TitleScene.class );
	}
	
	private static class SaveSlotButton extends Button {
		
		private NinePatch bg;
		
		private Image hero;
		private RenderedTextBlock name;
		
		private Image steps;
		private BitmapText depth;
		private Image classIcon;
		private BitmapText level;
		
		private int slot;
		private boolean newGame;
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get(Chrome.Type.GEM);
			add( bg);
			
			name = PixelScene.renderTextBlock(9);
			add(name);
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
				
				depth.text(Integer.toString(info.depth));
				depth.measure();
				
				level.text(Integer.toString(info.level));
				level.measure();
				
				if (info.challenges > 0){
					name.hardlight(Window.TITLE_COLOR);
					depth.hardlight(Window.TITLE_COLOR);
					level.hardlight(Window.TITLE_COLOR);
				} else {
					name.resetColor();
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
						y + (height - name.height())/2f
				);
				align(name);
				
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
				} else showWndSelectDungeon(slot);
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
			SandboxPixelDungeon.scene().add(new WndOptions(Icons.get(Icons.WARNING),
					Messages.get(StartScene.class, "wnd_no_dungeon_title"),
					Messages.get(StartScene.class, "wnd_no_dungeon_body"),
					Messages.get(StartScene.class, "wnd_no_dungeon_create_new"),
					Messages.get(StartScene.class, "wnd_no_dungeon_play_default"),
					Messages.get(StartScene.class, "wnd_no_dungeon_cancel")) {
				@Override
				protected void onSelect(int index) {
					if (index == 0) {
						Game.scene().addToFront(new WndNewDungeon(EMPTY_HASHSET));
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
			SandboxPixelDungeon.scene().addToFront(new WndSelectDungeon(allInfos,false, featuredInfo){
				@Override
				protected void select(String customDungeonName) {
					try {
						Dungeon.customDungeon = CustomDungeonSaves.loadDungeon(customDungeonName);

						GamesInProgress.selectedClass = selectClass;
						GamesInProgress.curSlot = slot;
						SandboxPixelDungeon.switchScene(HeroSelectScene.class);

					} catch (IOException e) {
						e.printStackTrace();
						SandboxPixelDungeon.reportException(e);
					} catch (CustomDungeonSaves.RenameRequiredException e) {
						e.showExceptionWindow();
					}
				}
			});
		}
	}
}