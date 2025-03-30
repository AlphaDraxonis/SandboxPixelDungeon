/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class RenderedTextBlock extends Component {

	private int maxWidth = Integer.MAX_VALUE;
	public int nLines;

	private static final RenderedText SPACE = new RenderedText();
	private static final RenderedText NEWLINE = new RenderedText();
	
	protected String text;
	protected String[] tokens = null;
	protected ArrayList<RenderedText> words = new ArrayList<>();
	protected boolean multiline = false;

	private int size;
	private float zoom;
	private int color = -1;
	private float alpha = 1f;
	private boolean colorsInverted;
	
	private int hightlightColor = Window.TITLE_COLOR;
	private boolean highlightingEnabled = true;
	public static final String MARKER = " ";//specifically yellow
	public static final String COLOR_MARKERS = "           ";//U+2000 to U+200A
	private int currentMarkingColor = -1;
	public static final int[] COLORS = {
			0xFF0000,//red  
			0x00FF00,//green  
			0xFFFF44,//yellow  
			0xFF8800,//orange  
			0x3399FF,//blue  
			0x661F66,//purple  
			0xFFCCDD,//pink  
			0xBB0000,//dark red  
			0xB3B3B3,//light gray  
			0x000000,//black  
			0x884306 //brown  
	};

	public static final int LEFT_ALIGN = 1;
	public static final int CENTER_ALIGN = 2;
	public static final int RIGHT_ALIGN = 3;
	private int alignment = LEFT_ALIGN;
	
	public RenderedTextBlock(int size){
		this.size = size;
	}

	public RenderedTextBlock(String text, int size){
		this.size = size;
		text(text);
	}

	public void text(String text){
		this.text = text;

		if (text != null && !text.equals("")) {
			
			tokens = Game.platform.splitforTextBlock(text, multiline);
			
			build();
		}
	}

	//for manual text block splitting, a space between each word is assumed
	public void tokens(String... words){
		StringBuilder fullText = new StringBuilder();
		for (String word : words) {
			fullText.append(word);
		}
		text = fullText.toString();

		tokens = words;
		build();
	}

	public void text(String text, int maxWidth){
		this.maxWidth = maxWidth;
		multiline = true;
		text(text);
	}

	public String text(){
		return text;
	}

	public void maxWidth(int maxWidth){
		if (this.maxWidth != maxWidth){
			this.maxWidth = maxWidth;
			multiline = true;
			text(text);
		}
	}

	public int maxWidth(){
		return maxWidth;
	}

	private synchronized void build(){
		if (tokens == null) return;
		
		clear();
		words = new ArrayList<>();
		boolean highlighting = false;
		currentMarkingColor = -1;
		for (String str : tokens){

			if (str.isEmpty()) continue;

			int forceColorChange = COLOR_MARKERS.indexOf(str.charAt(0));

			//if highlighting is enabled, '_' or '**' is used to toggle highlighting on or off
			// the actual symbols are not rendered
			if ((str.equals("_") || str.equals("**")) && highlightingEnabled){
				highlighting = !highlighting;
			} else if (forceColorChange != -1) {
				currentMarkingColor = COLORS[forceColorChange] == currentMarkingColor ? -1 : COLORS[forceColorChange];
			} else if (str.equals("\n")){
				words.add(NEWLINE);
			} else if (str.equals(" ")){
				words.add(SPACE);
			} else {
				RenderedText word = new RenderedText(str, size);
				
				if (highlighting) word.hardlight(hightlightColor);
				else if (currentMarkingColor != -1) word.hardlight(currentMarkingColor);
				else if (color != -1) word.hardlight(color);
				word.scale.set(zoom);
				word.alpha(alpha);
				
				words.add(word);
				add(word);
				
				if (height < word.height()) height = word.height();
			}
		}
		if (colorsInverted) {
			invert();
			colorsInverted = true;
		}
		layout();
	}

	public synchronized void zoom(float zoom){
		this.zoom = zoom;
		for (RenderedText word : words) {
			if (word != null) word.scale.set(zoom);
		}
		layout();
	}

	public synchronized void hardlight(int color){
		this.color = color;
		for (RenderedText word : words) {
			if (word != null) word.hardlight( color );
		}
	}
	
	public synchronized void resetColor(){
		this.color = -1;
		this.alpha = 1f;
		this.colorsInverted = false;
		for (RenderedText word : words) {
			if (word != null) word.resetColor();
		}
	}
	
	public synchronized void alpha(float value){
		this.alpha = value;
		for (RenderedText word : words) {
			if (word != null) word.alpha( value );
		}
	}
	
	public synchronized void setHighlighting(boolean enabled){
		setHighlighting(enabled, Window.TITLE_COLOR);
	}
	
	public synchronized void setHighlighting(boolean enabled, int color){
		if (enabled != highlightingEnabled || color != hightlightColor) {
			hightlightColor = color;
			highlightingEnabled = enabled;
			build();
		}
	}

	public synchronized void invert(){
		colorsInverted = !colorsInverted;
		if (words != null) {
			for (RenderedText word : words) {
				if (word != null) {
					word.ra = 0.77f;
					word.ga = 0.73f;
					word.ba = 0.62f;
					word.rm = -0.77f;
					word.gm = -0.73f;
					word.bm = -0.62f;
				}
			}
		}
	}

	public synchronized void align(int align){
		alignment = align;
		layout();
	}

	public int maxNumLines = Integer.MAX_VALUE;

	@Override
	protected synchronized void layout() {
		super.layout();
		float x = this.x;
		float y = this.y;
		float height = 0;
		nLines = 1;

		ArrayList<ArrayList<RenderedText>> lines = new ArrayList<>();
		ArrayList<RenderedText> curLine = new ArrayList<>();
		lines.add(curLine);

		width = 0;
		for (int i = 0; i < words.size(); i++){
			RenderedText word = words.get(i);

			if (!(word.visible = word.active = nLines <= maxNumLines)) continue;

			if (word == SPACE){
				x += 1.667f;
			} else if (word == NEWLINE) {
				//newline
				if (nLines < maxNumLines) y += height+2f;
				x = this.x;
				nLines++;
				curLine = new ArrayList<>();
				lines.add(curLine);
			} else {
				if (word.height() > height) height = word.height();

				float fullWidth = word.width();
				int j = i+1;

				if (!"/".equals(word.text()) && !"\\".equals(word.text())) {
					//this is so that words split only by highlighting are still grouped in layout
					//Chinese/Japanese always render every character separately without spaces however
					while (Messages.lang() != Languages.CHINESE && Messages.lang() != Languages.JAPANESE
							&& j < words.size() && words.get(j) != SPACE && words.get(j) != NEWLINE
							&& !words.get(j).text().equals("/") && !words.get(j).text().equals("\\")) {
						fullWidth += words.get(j).width() - 0.667f;
						j++;
					}
				}

				if ((x - this.x) + fullWidth - 0.001f > maxWidth && !curLine.isEmpty()){
					nLines++;
					if (!(word.visible = word.active = nLines <= maxNumLines)) continue;
					y += height+2f;
					x = this.x;
					curLine = new ArrayList<>();
					lines.add(curLine);
				}

				word.x = x;
				word.y = y;
				PixelScene.align(word);
				x += word.width();
				curLine.add(word);

				if ((x - this.x) > width) width = (x - this.x);

				//Note that spacing currently doesn't factor in halfwidth and fullwidth characters
				//(e.g. Ideographic full stop)
				x -= 0.667f;

			}
		}
		this.height = (y - this.y) + height;

		if (alignment != LEFT_ALIGN){
			for (ArrayList<RenderedText> line : lines){
				if (line.size() == 0) continue;
				float lineWidth = line.get(line.size()-1).width() + line.get(line.size()-1).x - this.x;
				if (alignment == CENTER_ALIGN){
					for (RenderedText text : line){
						text.x += (width() - lineWidth)/2f;
						PixelScene.align(text);
					}
				} else if (alignment == RIGHT_ALIGN) {
					for (RenderedText text : line){
						text.x += width() - lineWidth;
						PixelScene.align(text);
					}
				}
			}
		}
	}
}
