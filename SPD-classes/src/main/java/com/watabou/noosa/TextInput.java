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

package com.watabou.noosa;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Function;
import com.watabou.utils.Point;

import java.util.Objects;

//essentially contains a libGDX text input field, plus a PD-rendered background
public class TextInput extends Component {

	private Stage stage;
	private Container container;
	private TextField textField;

	private Skin skin;

	private NinePatch bg;

	public Function<String, String> convertStringToValidString;

	public TextInput( NinePatch bg, boolean multiline, int size ){
		super();
		this.bg = bg;
		add(bg);

		//use a custom viewport here to ensure stage camera matches game camera
		Viewport viewport = new Viewport() {};
		viewport.setWorldSize(Game.width, Game.height);
		viewport.setScreenBounds(0, Game.bottomInset, Game.width, Game.height);
		viewport.setCamera(new OrthographicCamera());
		stage = new Stage(viewport){
			@Override
			public boolean keyDown(int keycode) {
				if (!isActive() || keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
					return false; // don't consume the back button event
				}
				return super.keyDown(keycode); // Let other events be processed
			}

			@Override
			public boolean keyUp(int keycode) {
				if (isActive()) return super.keyUp(keycode);
				return false;
			}

			@Override
			public boolean keyTyped(char character) {
				if (isActive()) return super.keyTyped(character);
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (isActive()) return super.touchDown(screenX, screenY, pointer, button);
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (isActive()) return super.touchUp(screenX, screenY, pointer, button);
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if (isActive()) return super.touchDragged(screenX, screenY, pointer);
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				if (isActive()) return super.mouseMoved(screenX, screenY);
				return false;
			}

			@Override
			public boolean scrolled(float amountX, float amountY) {
				if (isActive()) return super.scrolled(amountX, amountY);
				return false;
			}
		};
		Game.inputHandler.addInputProcessor(stage);

		container = new Container<TextField>();
		stage.addActor(container);
		container.setTransform(true);

		skin = new Skin(FileUtils.getFileHandle(Files.FileType.Internal, "gdx/textfield.json"));

		TextField.TextFieldStyle style = skin.get(TextField.TextFieldStyle.class);
		style.font = Game.platform.getFont(size, "", false, false);
		style.background = null;
		textField = multiline ? new TextArea("", style) : new TextField("", style);
		textField.setProgrammaticChangeEvents(true);

		if (!multiline) textField.setAlignment(Align.center);

		textField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setText(textField.getText());

				BitmapFont f = Game.platform.getFont(size, textField.getText(), false, false);
				TextField.TextFieldStyle style = textField.getStyle();
				if (f != style.font){
					style.font = f;
					textField.setStyle(style);
				}
			}
		});

		if (!multiline){
			textField.setTextFieldListener(new TextField.TextFieldListener(){
				public void keyTyped (TextField textField, char c){
					if (c == '\r' || c == '\n'){
						enterPressed();
					}
				}

			});
		}

		textField.setOnscreenKeyboard(new TextField.OnscreenKeyboard() {
			@Override
			public void show(boolean visible) {
				Game.platform.setOnscreenKeyboardVisible(visible);
			}
		});

		container.setActor(textField);
		stage.setKeyboardFocus(textField);
		Game.platform.setOnscreenKeyboardVisible(true);
	}

	public void enterPressed(){
		//do nothing by default
	};

	public void setText(String text) {
		if (convertStringToValidString != null) {
			text = convertStringToValidString.apply(text);
			if (text == null) return;
		}
		if (!Objects.equals(text, textField.getText())) {
			textField.setText(text);
			textField.setCursorPosition(textField.getText().length());
		}
	}

	public void setMaxLength(int maxLength){
		textField.setMaxLength(maxLength);
	}

	public String getText(){
		return textField.getText();
	}

	public void copyToClipboard(){
		if (textField.getSelection().isEmpty()) {
			textField.selectAll();
		}

		textField.copy();
	}

	public void pasteFromClipboard(){
		String contents = Gdx.app.getClipboard().getContents();
		if (contents == null) return;

		if (!textField.getSelection().isEmpty()){
			//just use cut, but override clipboard
			textField.cut();
			Gdx.app.getClipboard().setContents(contents);
		}

		String existing = textField.getText();
		int cursorIdx = textField.getCursorPosition();

		textField.setText(existing.substring(0, cursorIdx) + contents + existing.substring(cursorIdx));
		textField.setCursorPosition(cursorIdx + contents.length());
	}

	public void selectAll() {
		textField.selectAll();
	}

	@Override
	protected void layout() {
		super.layout();

		float contX = x;
		float contY = y;
		float contW = width;
		float contH = height;

		if (bg != null){
			bg.x = x;
			bg.y = y;
			bg.size(width, height);

			contX += bg.marginLeft();
			contY += bg.marginTop();
			contW -= bg.marginHor();
			contH -= bg.marginVer();
		}

		float zoom = Camera.main.zoom;
		Camera c = camera();
		if (c != null){
			zoom = c.zoom;
			Point p = c.cameraToScreen(contX, contY);
			contX = p.x/zoom;
			contY = p.y/zoom;
		}

		container.align(Align.topLeft);
		container.setPosition(contX*zoom, (Game.height-(contY*zoom)));
		container.size(contW*zoom, contH*zoom);
	}

	@Override
	public void update() {
		super.update();
		stage.act(Game.elapsed);
	}

	@Override
	public void draw() {
		super.draw();
		Quad.releaseIndices();
		Script.unuse();
		Texture.clear();
		stage.draw();
		Quad.bindIndices();
		Blending.useDefault();
	}

	@Override
	public synchronized void destroy() {
		super.destroy();
		if (stage != null) {
			stage.dispose();
			skin.dispose();
			Game.inputHandler.removeInputProcessor(stage);
			Game.platform.setOnscreenKeyboardVisible(false);
			if (!DeviceCompat.isDesktop()) Game.platform.updateSystemUI();
		}
	}

	public void setTextFieldFilter(TextField.TextFieldFilter filter) {
		textField.setTextFieldFilter(filter);
	}

	@Null
	public TextField.TextFieldFilter getTextFieldFilter() {
		return textField.getTextFieldFilter();
	}

	public static final TextField.TextFieldFilter FILE_NAME_INPUT = new TextField.TextFieldFilter() {
		@Override
		public boolean acceptChar(TextField textField, char c) {
			return c != '/' && c != '\\' && c != ':' && c != '?' && c != '\"' && c != '<' && c != '>' && c != '|';
		}
	};
}