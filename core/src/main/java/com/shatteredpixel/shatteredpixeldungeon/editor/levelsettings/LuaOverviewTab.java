package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Sandbox Pixel Dungeon
 * Copyright (C) 2023-2024 AlphaDraxonis
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

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.DefaultListItemWithRemoveBtn;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.DungeonScript;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaCodeHolder;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaManager;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.NewInstanceButton;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.PopupMenu;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptionsCondensed;
import com.watabou.idewindowactions.LuaScript;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.Collections;
import java.util.List;

import static com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp.GAP;

public class LuaOverviewTab extends WndEditorSettings.TabComp {

	private MultiWindowTabComp.OutsideSpSwitchTabs outsideSp;

	protected ScrollingListPane scrollingListPane;
	protected RenderedTextBlock title;

	private int selectedTab;

	public LuaOverviewTab() {

		title = PixelScene.renderTextBlock(Messages.get(LuaOverviewTab.class, "scripts_title"), 12);
		title.hardlight(Window.TITLE_COLOR);
		add(title);

		scrollingListPane = new ScrollingListPane();
		add(scrollingListPane);

		outsideSp = new MultiWindowTabComp.OutsideSpSwitchTabs() {
			@Override
			protected void createChildren() {
				tabs = new TabControlButton[2];
				for (int j = 0; j < tabs.length; j++) {
					tabs[j] = new TabControlButton(j);
					add(tabs[j]);
				}
				tabs[0].text(getTabName(0));
				tabs[1].text(getTabName(1));

				super.createChildren();

				select(0);
			}

			@Override
			public void select(int index) {
				super.select(index);
				if (outsideSp != null) LuaOverviewTab.this.select(index);
			}

			@Override
			protected void layout() {
				float posY = y;
				float buttonWidth = width() / tabs.length;
				for (int i = 0; i < tabs.length; i++) {
					tabs[i].setRect(x + i * buttonWidth, posY, buttonWidth, ScrollingListPane.ITEM_HEIGHT);
					PixelScene.align(tabs[i]);
				}
				height = posY - y + ScrollingListPane.ITEM_HEIGHT;
			}

			@Override
			public String getTabName(int index) {
				switch (index) {
					case 0: return Messages.get(LuaOverviewTab.class, "scripts_title");
					case 1: return Messages.get(LuaOverviewTab.class, "dungeon_script_title");
				}
				return "null";
			}
		};
		add(outsideSp);
	}

	private void select(int index) {
		selectedTab = index;
		title.text(outsideSp.getTabName(index));
		layout();
	}

	@Override
	public void layout() {
		scrollingListPane.clear();

		title.maxWidth((int) width);
		title.setPos(x + (width - title.width()) * 0.5f, y + 2);

		float posY = title.bottom() + 4;

		float normalSpHeight;
		if (outsideSp != null) {
			outsideSp.setSize(width, -1);
			float outsideSpH = outsideSp.height();
			outsideSp.setPos(x, y + height - outsideSpH);
			normalSpHeight = height - posY - (outsideSpH == 0 ? 1 : outsideSpH + GAP);
		} else {
			normalSpHeight = height - posY - 1;
		}
		scrollingListPane.setRect(x, posY, width, normalSpHeight);

		updateList();
	}

	@Override
	public Image createIcon() {
		return Icons.RENAME_ON.get();
	}

	@Override
	public String hoverText() {
		return Messages.get(LuaOverviewTab.class, "scripts_title");
	}

	@Override
	public void updateList() {

		scrollingListPane.clear();

		if (selectedTab == 0) {
			List<LuaScript> scripts = CustomDungeonSaves.findScripts(null);
			Collections.sort(scripts);

			for (LuaScript script : scripts) {
				scrollingListPane.addItemNoLayouting(new ScriptItem(script));
			}
		} else {

			scrollingListPane.addItemNoLayouting(new RedButton(Messages.get(LuaOverviewTab.class, "open_dungeon_script")) {
				@Override
				protected void onClick() {
					if (Dungeon.dungeonScript.pathToScript == null) Dungeon.dungeonScript.pathToScript = "";
					IDEWindow.showWindow(Dungeon.dungeonScript);
				}
			});

		}

		scrollingListPane.nowLayout();
	}

	private class ScriptItem extends ScrollingListPane.ListItem {

		protected final LuaScript script;

		protected RenderedTextBlock description;
		protected IconButton delete;

		public ScriptItem(LuaScript script) {
			super(createIcon(script), script.pathFromRoot);
			title.setHighlighting(false);

			this.script = script;

			description = PixelScene.renderTextBlock(script.desc, 6);
			description.maxNumLines = 1;
			description.setHighlighting(false);
			add(description);

			StringBuilder builder = new StringBuilder();
			for (CustomObject obj : CustomObject.customObjects.values()) {
				if (script.pathFromRoot.equals(obj.pathToScript)) builder.append(obj.name).append('\n');
			}
			for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
				if (ls.luaScript != null && script.pathFromRoot.equals(ls.luaScript.pathToScript)) builder.append(ls.getName()).append('\n');
			}
			int l = builder.length();
			if (l != 0) builder.delete(l - 1, l);
			String usedIn = builder.toString();

			delete = new IconButton(Icons.TRASH.get()) {
				@Override
				protected void onClick() {
					EditorScene.show(new WndOptionsCondensed(Messages.get(LuaOverviewTab.class, "delete_confirm_title", script.pathFromRoot),
							Messages.get(LuaOverviewTab.class, "delete_confirm_body", script.pathFromRoot)
									+ (usedIn.isEmpty() ? "" : Messages.get(LuaOverviewTab.class, "delete_confirm_used_in", usedIn)),
							Messages.get(HeroSelectScene.class, "daily_yes"), Messages.get(HeroSelectScene.class, "daily_no")) {
						@Override
						protected void onSelect(int index) {
							if (index == 0) {
								if (CustomDungeonSaves.deleteScriptFile(script.pathFromRoot)) {
									updateList();
									if (Level.class.isAssignableFrom(script.type)) {
										for (LevelScheme ls : Dungeon.customDungeon.levelSchemes()) {
											if (ls.luaScript != null && script.pathFromRoot.equals(ls.luaScript.pathToScript))
												ls.luaScript = null;
										}
									}
								}
							}
						}
					});
				}

				@Override
				protected String hoverText() {
					return Messages.get(DefaultListItemWithRemoveBtn.class, "delete");
				}
			};
			add(delete);
		}

		@Override
		protected void onClick() {
			IDEWindow.showWindow(new LuaCodeHolder(script));
		}

		@Override
		protected void layout() {
			super.layout();

			icon.y = y + 2;
			iconLabel.y = icon.y + (icon.height - iconLabel.height()) / 2f + 0.5f;
			PixelScene.align(iconLabel);

			label.setPos(label.left(), y + 4);
			PixelScene.align(label);

			description.maxWidth(label.maxWidth());

			description.setPos(label.left(), label.bottom() + 2);

			height = Math.max(Math.max(4 + icon.height(), ScrollingListPane.ITEM_HEIGHT), description.bottom() - y + 3);

			if (delete != null) layoutIconButtonOnRight(delete);
		}

		@Override
		protected int getLabelMaxWidth() {
			return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
		}
	}

	private static Image createIcon(LuaScript script) {
		if (Level.class.isAssignableFrom(script.type)) return Icons.STAIRS.get();
		if (DungeonScript.class.isAssignableFrom(script.type)) return Icons.NEWS.get();
		return Reference.objectToImage(Reflection.newInstance(script.type));
	}


	private class VarItem extends Component {

		protected ColorBlock line;
		protected TextInput nameInput;
		protected TextInput valueInput;
		protected IconButton delete;
		protected RedButton more;

		private final int index;

		public VarItem(String initString, int index) {
			super();

			this.index = index;

			line = new ColorBlock(1, 1, 0xFF222222);
			add(line);

			nameInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, 7, PixelScene.uiCamera.zoom) {
				@Override
				protected void looseFocus() {
					super.looseFocus();
					//TODO tzz check if valid name!
				}
			};
			if (!initString.isEmpty()) nameInput.setText(initString.substring(initString.indexOf(' ')));
			add(nameInput);

			valueInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, 7, PixelScene.uiCamera.zoom) {
				@Override
				protected void looseFocus() {
					super.looseFocus();
					//TODO tzz check if valid name!
				}
			};
			if (!initString.isEmpty()) valueInput.setText(initString.substring(initString.indexOf('=') + 2));
			add(valueInput);

			delete = new IconButton(Icons.TRASH.get()) {
				@Override
				protected void onClick() {
//					CustomObject.globalVarsDefaults.remove(index);
					updateList();
				}

				@Override
				protected String hoverText() {
					return Messages.get(DefaultListItemWithRemoveBtn.class, "delete");
				}

				@Override
				protected void layout() {
					super.layout();
					hotArea.width -= 3;
					hotArea.height -= 3;
					hotArea.x += 1.5f;
					hotArea.y += 1.5f;
				}
			};
			add(delete);

			more = new RedButton("") {

				@Override
				protected void onClick() {
					PopupMenu popupMenu = new PopupMenu() {
						{
							Runnable hide = this::hideImmediately;
							RedButton[] buttons = {
									new NewInstanceButton(this) {
										@Override
										protected void onSelect(String insertText) {
											TextInput textInput = TextInput.getWithFocus();
											if (textInput != null) textInput.insert(insertText);
										}
									},
									new RedButton("TODO select cell") {
										@Override
										protected void onClick() {
											EditorScene.hideWindowsTemporarily();
											EditorScene.selectCell(new CellSelector.Listener() {
												@Override
												public void onSelect(Integer cell) {
													if (cell != null) {

														hide.run();
														if (cell >= 0 && cell < Dungeon.level.length()) {
															TextInput textInput = TextInput.getWithFocus();
															if (textInput != null) textInput.insert(Integer.toString(cell));
														}
														EditorScene.reshowWindows();
													}
												}

												@Override
												public String prompt() {
													return "TODO";
//													return Messages.get(EditTrapComp.class, "gateway_trap_prompt");
												}
											});
										}
									}
							};
							finishInstantiation(buttons,
									(int) ((more.camera().width / 2f - 10)),
									(int) (camera().height / 2f) - 70, 200, Orientation.TOP_TO_BOTTOM);
						}
					};
					DungeonScene.show(popupMenu);
				}
			};
			more.icon(Icons.MENU.get());
			add(more);
		}

		@Override
		protected void layout() {
			super.layout();

			line.size(width, 1);
			line.x = x;
			line.y = y;

			nameInput.setRect(x + 1, y + 2, width * 3 / 10, height - 4);

			delete.setRect(width - delete.icon().width - 2, y + (height - delete.icon().height) * 0.5f, delete.icon().width(), delete.icon().height());
			more.setRect(delete.left() - more.icon().width() - 3,  y + (height - more.icon().height) * 0.5f, more.icon().width(), more.icon().height());

			float posX = nameInput.right() + 2;
			valueInput.setRect(posX, y + 2, more.left() - 4 - posX, height - 4);
		}

		@Override
		public synchronized void destroy() {

			String nameText;
			nameText = nameInput.getText().isEmpty() ? "unnamed" : nameInput.getText();

			String valueText;
			valueText = valueInput.getText().isEmpty() ? "nil" : valueInput.getText();

			if (LuaManager.compile("return " + valueText) != null) valueText = "nil --[[    " + valueText + " ]]";

//			if (nameInput.getText().isEmpty() && valueInput.getText().isEmpty()) CustomObject.globalVarsDefaults.set(index, "");
//			else CustomObject.globalVarsDefaults.set(index, nameText + " = " + valueText);

			super.destroy();

		}
	}
}