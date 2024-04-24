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

package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndItemDistribution;
import com.shatteredpixel.shatteredpixeldungeon.editor.server.UploadDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector.FieldLike;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.AccessChainReference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StandardReference;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.TextInput;
import com.watabou.noosa.ui.Component;

public class WndStoreReference extends SimpleWindow {//tzz 3 strings missing!

	private TextInput nameInput;
	private StyledCheckBox asAccessChain, asValue;

	public WndStoreReference(Reference reference, FieldLike field, Object obj) {

		super();

		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.get(this, "title"), 10);
		title.hardlight(Window.TITLE_COLOR);

		Component content = new Component() {
			@Override
			protected void createChildren(Object... params) {
				nameInput = new TextInput(Chrome.get(Chrome.Type.TOAST_WHITE), false, PixelScene.uiCamera.zoom);
				nameInput.setMaxLength(100);
				add(nameInput);

				//TODO tzz dont allow as reference if parent reference is null (e.g. return value from a method)
				asAccessChain = new StyledCheckBox(Messages.get(WndStoreReference.class, "as_reference")) {
					@Override
					public void checked(boolean value) {
						super.checked(value);
						if (asValue != null && asValue.checked() == value) asValue.checked(!value);
					}
				};
				add(asAccessChain);

				asValue = new StyledCheckBox(Messages.get(WndStoreReference.class, "as_value")) {
					@Override
					public void checked(boolean value) {
						super.checked(value);
						if (asAccessChain != null && asAccessChain.checked() == value) asAccessChain.checked(!value);
					}
				};
				add(asValue);

				nameInput.setText(field.getName());
				asValue.checked(true);
			}

			@Override
			protected void layout() {
				height = 0;
				height = EditorUtilies.layoutCompsLinear(2, this, nameInput) + 3;
				height = EditorUtilies.layoutStyledCompsInRectangles(2, width,2, this, asValue, asAccessChain);
			}
		};

		Component outsideSp = new Component() {
			RedButton save, cancel;

			@Override
			protected void createChildren(Object... params) {

				save = new RedButton(Messages.get(WndItemDistribution.class, "save")) {
					@Override
					protected void onClick() {
						hide();
						Reference ref;
						if (asAccessChain.checked()) {
							ref = new AccessChainReference(field.getType(), nameInput.getText(), reference, field);
//							Object val;
//							try {
//								val = field.get(obj);
//							} catch (IllegalAccessException e) {
//								return;
//							}
//							ref = new PointerReference(field, val, nameInput.getText());
						} else {
							Object val;
							try {
								val = field.get(obj);
							} catch (IllegalAccessException e) {
								return;
							}
							ref = new StandardReference(val.getClass(), val, nameInput.getText(), reference, field);
						}
						WndScrollOfDebug.addReference(ref);

					}
				};
				cancel = new RedButton(Messages.get(UploadDungeon.class, "cancel")) {
					@Override
					protected void onClick() {
						hide();
					}
				};
				add(save);
				add(cancel);
			}

			@Override
			protected void layout() {
				float pos = y;
				float w = (width - LevelTab.GAP) / 2f;
				cancel.setRect(0, pos, w, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
				PixelScene.align(cancel);
				save.setRect(cancel.right() + LevelTab.GAP, pos, w, ChooseOneInCategoriesBody.BUTTON_HEIGHT);
				PixelScene.align(save);

				height = ChooseOneInCategoriesBody.BUTTON_HEIGHT;
			}
		};


		initComponents(title, content, outsideSp);

		float prefHeight = preferredHeight();
		if (prefHeight < height) resize(width, (int) Math.ceil(prefHeight));
	}

}