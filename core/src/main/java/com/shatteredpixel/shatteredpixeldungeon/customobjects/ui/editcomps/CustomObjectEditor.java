/*
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

package com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.editcomps;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.CustomObjectManager;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.LuaCustomObject;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.CustomObjSelector;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndSelectResourceFile;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.luaeditor.IDEWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;

import java.io.IOException;
import java.util.Map;

public class CustomObjectEditor<T extends CustomObject> extends Component {

	protected CustomObjSelector<String> luaScriptPath;//only if needed

	protected Component[] linearComps, rectComps;

	protected final T obj;

	private final Runnable onUpdateObj;

	public CustomObjectEditor(Runnable onUpdateObj, T obj) {
		this.onUpdateObj = onUpdateObj;
		this.obj = obj;

		createChildren(obj);

		updateObj();
	}

	protected void createChildren(T obj) {

		if (obj instanceof LuaCustomObject) {
			LuaCustomObject lco = (LuaCustomObject) obj;
			luaScriptPath = new CustomObjSelector<String>(Messages.get(CustomObjectEditor.class, "script"), new CustomObjSelector.Selector<String>() {

				@Override
				public String getCurrentValue() {
					return lco.getLuaScriptPath();
				}

				@Override
				public void onSelect(String path) {
					lco.setLuaScriptPath(path);
					CustomObjectManager.loadScript(lco);
					updateObj();
					obj.reloadSprite();
				}

				@Override
				public void onItemSlotClick() {
					IDEWindow.showWindow(lco, luaScriptPath, lco.getLuaTargetClass());
				}

				@Override
				public void onChangeClick() {
					DungeonScene.show(new WndSelectResourceFile() {
						@Override
						protected boolean acceptExtension(String extension) {
							return ResourcePath.isLua(extension);
						}

						@Override
						protected void onSelect(Map.Entry<String, FileHandle> path) {
							luaScriptPath.setValue(path.getKey());
						}
					});
				}
			}) {
				@Override
				protected void onChangeClick() {
					IDEWindow.showSelectScriptWindow(lco.getLuaTargetClass(), script -> {
						if (script != null) {
							luaScriptPath.setValue(script.getPath());
						}
					});
				}
			};
			luaScriptPath.enableChanging(true);
			luaScriptPath.enableDetaching(true);
			add(luaScriptPath);
		}

		linearComps = null;
		rectComps = new Component[] {
				luaScriptPath
		};
	}

	@Override
	protected void layout() {
		super.layout();
		layoutCompsLinear(linearComps);
		layoutCompsInRectangles(rectComps);
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (obj.getIdentifier() != 0) {
			try {
				CustomDungeonSaves.storeCustomObject(obj);
			} catch (IOException e) {
				Game.runOnRenderThread(() -> DungeonScene.show(new WndError(e)));
			}
		}
	}

	public void updateObj() {
		if (onUpdateObj != null) onUpdateObj.run();
	}


	protected final void layoutCompsLinear(Component... comps) {
		if (height > 0) height += WndTitledMessage.GAP;
		float newHeight = EditorUtilities.layoutCompsLinear(WndTitledMessage.GAP, this, comps);
		if (newHeight == height && height > WndTitledMessage.GAP) height -= WndTitledMessage.GAP;
		else height = newHeight;
	}

	protected final void layoutCompsInRectangles(Component... comps) {
		layoutCompsInRectangles(WndTitledMessage.GAP, comps);
	}

	protected final void layoutCompsInRectangles(int gap, Component... comps) {
		if (height > 0) height += gap;
		float newHeight = EditorUtilities.layoutStyledCompsInRectangles(WndTitledMessage.GAP, width, this, comps);
		if (newHeight == height) height -= gap;
		else height = newHeight;
	}
}