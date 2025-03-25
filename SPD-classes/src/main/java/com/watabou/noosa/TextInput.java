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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.watabou.glscripts.Script;
import com.watabou.glwrap.Blending;
import com.watabou.glwrap.Quad;
import com.watabou.glwrap.Texture;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Function;
import com.watabou.utils.Point;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

//essentially contains a libGDX text input field, plus a PD-rendered background
public class TextInput extends Component {
	
	private static final Set<TextInput> activeTextInputs = new HashSet<>();
	
	private boolean hasFocus;
	
	private Stage stage;
	private Container container;
	protected TextField textField;
	private final boolean multiline;
	
	private Skin skin;
	private TextField.TextFieldStyle style;
	private int normalCursorColor;
	
	private NinePatch bg;
	private PointerArea catchClicks;
	
	public Function<String, String> convertStringToValidString;
	
	public TextInput( NinePatch bg, boolean multiline, float pixelScene_uiCamera_zoom ){
		this(bg, multiline, multiline ? 6 : 9, pixelScene_uiCamera_zoom);
	}
	
	public TextInput( NinePatch bg, boolean multiline, int fontSize, float pixelScene_uiCamera_zoom ){
		super();
		this.multiline = multiline;
		this.bg = bg;
		add(bg);
		
		catchClicks = new PointerArea(bg) {
			
			private boolean clickStarted = false;
			
			@Override
			protected void onPointerUp(PointerEvent event) {
				super.onPointerUp(event);
				if (clickStarted) {
					gainFocus();
					//set the cursor at where it was clicked
					stage.touchDown((int) event.current.x, (int) event.current.y, event.id, event.button);
					stage.touchUp((int) event.current.x, (int) event.current.y, event.id, event.button);
				}
				clickStarted = false;
			}
			
			@Override
			protected void onClick(PointerEvent event) {
				super.onClick(event);
				Gdx.app.postRunnable(() -> Game.platform.setOnscreenKeyboardVisible(true));
			}
			
			@Override
			protected void onPointerDown(PointerEvent event) {
				super.onPointerDown(event);
				clickStarted = true;
			}
			
			@Override
			public void reset() {
				super.reset();
				clickStarted = false;
			}
		};
		catchClicks.blockLevel = PointerArea.NEVER_BLOCK;
		add(catchClicks);
		
		activeTextInputs.add(this);
		
		int size = (int) pixelScene_uiCamera_zoom * fontSize;
		
		//use a custom viewport here to ensure stage camera matches game camera
		Viewport viewport = new Viewport() {};
		viewport.setWorldSize(Game.width, Game.height);
		viewport.setScreenBounds(0, Game.bottomInset, Game.width, Game.height);
		viewport.setCamera(new OrthographicCamera());
		stage = new Stage(viewport){
			@Override
			public boolean keyDown(int keycode) {
				if (!isActive() || !hasFocus() || keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE || !isInTopWindow()) {
					return false; // don't consume the back button event
				}
				return super.keyDown(keycode); // Let other events be processed
			}
			
			@Override
			public boolean keyUp(int keycode) {
				if (isActive() && hasFocus() && isInTopWindow())
					return super.keyUp(keycode);
				return false;
			}
			
			@Override
			public boolean keyTyped(char character) {
				if (isActive() && hasFocus() && isInTopWindow())
					return super.keyTyped(character);
				return false;
			}
			
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				if (isActive() && hasFocus() && isInTopWindow() && catchClicks.overlapsScreenPoint(screenX, screenY))
					return super.touchDown(screenX, screenY, pointer, button);
				return false;
			}
			
			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				if (isActive() && hasFocus() && isInTopWindow() /*&& catchClicks.overlapsScreenPoint(screenX, screenY)*/)
					return super.touchUp(screenX, screenY, pointer, button);
				return false;
			}
			
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if (isActive() && hasFocus() && isInTopWindow() /*&& catchClicks.overlapsScreenPoint(screenX, screenY)*/)
					return super.touchDragged(screenX, screenY, pointer);
				return false;
			}
			
			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				if (isActive() && hasFocus() && isInTopWindow() && catchClicks.overlapsScreenPoint(screenX, screenY))
					return super.mouseMoved(screenX, screenY);
				return false;
			}
			
			@Override
			public boolean scrolled(float amountX, float amountY) {
				if (isActive() && hasFocus() && isInTopWindow())
					return super.scrolled(amountX, amountY);
				return false;
			}
		};
		Game.inputHandler.addInputProcessor(stage);
		
		container = new Container<TextField>();
		stage.addActor(container);
		container.setTransform(true);
		
		skin = new Skin(FileUtils.getFileHandle(Files.FileType.Internal, "gdx/textfield.json"));
		
		style = skin.get(TextField.TextFieldStyle.class);
		style.font = Game.platform.getFont(size, "", false, false);
		style.background = null;
		if (multiline){
			textField = new TextArea("", style){
				@Override
				public void cut() {
					super.cut();
					onClipBoardUpdate();
				}
				
				@Override
				public void copy() {
					super.copy();
					onClipBoardUpdate();
				}
			};
		} else {
			textField = new TextField("", style){
				@Override
				public void cut() {
					super.cut();
					onClipBoardUpdate();
				}
				
				@Override
				public void copy() {
					super.copy();
					onClipBoardUpdate();
				}
			};
		}
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
				onChanged();
			}
		});
		
		textField.setTextFieldListener((textField, c) -> onKeyTyped(c));
		
		textField.setOnscreenKeyboard(visible -> Game.platform.setOnscreenKeyboardVisible(visible));
		
		container.setActor(textField);
		stage.setKeyboardFocus(textField);
		
		normalCursorColor = ((NinePatchDrawable) style.cursor).getPatch().getColor().toIntBits();
		gainFocus();
	}
	
	public synchronized void gainFocus() {
		if (!hasFocus) {
			for (TextInput textInput : activeTextInputs.toArray(new TextInput[0])) {
				if (textInput.isActive() && textInput != this) {
					textInput.looseFocus();
				}
			}
			((NinePatchDrawable) style.cursor).getPatch().getColor().set(normalCursorColor == 0 ? 0xFFFFFFFF : normalCursorColor);
			hasFocus = true;
		}
	}
	
	protected void looseFocus() {
		textField.clearSelection();
		Color cursorColor = ((NinePatchDrawable) style.cursor).getPatch().getColor();
		normalCursorColor = cursorColor.toIntBits();
		cursorColor.set(0);
		hasFocus = false;
	}
	
	protected static void focusToFirstVisible() {
		for (TextInput textInput : activeTextInputs.toArray(new TextInput[0])) {
			if (textInput.isVisible() && textInput.isActive()) {
				textInput.gainFocus();
				break;
			}
		}
	}
	
	private boolean isInTopWindow() {
		return ScrollArea.isInTopWindow(this);
	}
	
	public static TextInput getWithFocus() {
		for (TextInput c : activeTextInputs) {
			if (c.hasFocus() && c.isActive() && c.isInTopWindow()) return c;
		}
		return null;
	}
	
	public boolean hasFocus() {
		return hasFocus;
	}
	
	public boolean isVisibleOnScreen() {//FIXME not very relying
		Point posOnScreen = catchClicks.camera().cameraToScreen(catchClicks.x, catchClicks.y);
		return catchClicks.overlapsScreenPoint(posOnScreen.x, posOnScreen.y);
	}
	
	protected void onKeyTyped(char c) {
		if (multiline) {
			if (c == '\t'){
				stage.keyTyped(' ');
				stage.keyTyped(' ');
				stage.keyTyped(' ');
				stage.keyTyped(' ');
			}
		} else {
			if (c == '\r' || c == '\n'){
				enterPressed();
			}
		}
	}
	
	public void enterPressed(){
		//fires any time enter is pressed, do nothing by default
	};
	
	public void onChanged(){
		//fires any time the text box is changed, do nothing by default
	}
	
	public void onClipBoardUpdate(){
		//fires any time the clipboard is updated via cut or copy, do nothing by default
	}
	
	public void setText(String text){
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
		
		insert(contents);
	}
	
	public void selectAll() {
		textField.selectAll();
	}
	
	public void insert(String s) {
		String existing = textField.getText();
		int cursorIdx = textField.getCursorPosition();
		
		textField.setText(existing.substring(0, cursorIdx) + s + existing.substring(cursorIdx));
		textField.setCursorPosition(cursorIdx + s.length());
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		if (bg != null){
			bg.x = x;
			bg.y = y;
			bg.size(width, height);
		}
		if (catchClicks != null){
			catchClicks.x = x;
			catchClicks.y = y;
			catchClicks.width = width;
			catchClicks.height = height;
		}
		
		layoutContainer(true);
	}
	
	private static final float SCROLL_NOT_SET = Float.NaN;
	private float lastScrollX = SCROLL_NOT_SET, lastScrollY = SCROLL_NOT_SET;
	
	private void layoutContainer(boolean force) {
		
		Camera c = camera();
		
		if (!force && c != null && c.scroll.y == lastScrollY && c.scroll.x == lastScrollX) {
			return;
		}
		
		float contX = x;
		float contY = y;
		float contW = width;
		float contH = height;
		
		if (bg != null){
			contX += bg.marginLeft();
			contY += bg.marginTop();
			contW -= bg.marginHor();
			contH -= bg.marginVer();
		}
		
		float zoom = Camera.main.zoom;
		if (c != null){
			
			zoom = c.zoom;
			Point p = c.cameraToScreen(contX, contY);
			contX = p.x/zoom;
			contY = p.y/zoom;
			
			if (!force) {
				lastScrollX = c.scroll.x;
				lastScrollY = c.scroll.y;
			}
			
			float cameraBottom = c.y + c.scroll.y + c.height;
			if (cameraBottom > 1 && cameraBottom <= contY + contH ) {
				contH = Math.max(0, cameraBottom - contY);
			}
			
		} else {
			lastScrollX = lastScrollY = SCROLL_NOT_SET;
		}
		
		container.align(Align.topLeft);
		container.setPosition(contX*zoom, (Game.height-(contY*zoom)));
		container.size(contW*zoom, contH*zoom);
	}
	
	@Override
	public void cancelClick() {
		if (catchClicks != null) catchClicks.reset();
	}
	
	@Override
	public void redirectPointerEvent(PointerEvent event) {
		if (catchClicks != null) catchClicks.onSignal(event);
	}
	
	@Override
	public void update() {
		super.update();
		stage.act(Game.elapsed);
		layoutContainer(false);
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
		activeTextInputs.remove(this);
		if (hasFocus) {
			focusToFirstVisible();
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
