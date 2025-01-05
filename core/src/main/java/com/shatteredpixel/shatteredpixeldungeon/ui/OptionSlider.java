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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;

public abstract class OptionSlider extends Component {
    
    private PointerArea pointerArea;
    
    private RenderedTextBlock title;
    private RenderedTextBlock minTxt;
    private RenderedTextBlock maxTxt;
    
    //values are expressed internally as ints, but they can easily be interpreted as something else externally.
    private int minVal;
    private int maxVal;
    private int selectedVal;
    
    private NinePatch sliderNode;
    private NinePatch BG;
    private ColorBlock sliderBG;
    private ColorBlock[] sliderTicks;
    private float tickDist;
    private final int maxNumSliderTicks;
    
    
    public OptionSlider(String title, String minTxt, String maxTxt, int minVal, int maxVal){
        this(title, minTxt, maxTxt, minVal, maxVal, Integer.MAX_VALUE);
    }
    
    public OptionSlider(String title, String minTxt, String maxTxt, int minVal, int maxVal, int maxNumSliderTicks){
        super();
        
        //shouldn't function if this happens.
        if (minVal > maxVal){
            minVal = maxVal;
            active = false;
        }
        
        this.title.text(title);
        this.minTxt.text(minTxt);
        this.maxTxt.text(maxTxt);
        
        this.minVal = minVal;
        this.maxVal = maxVal;
        
        this.maxNumSliderTicks = maxNumSliderTicks;
        
        sliderTicks = new ColorBlock[Math.min((maxVal - minVal) + 1, maxNumSliderTicks)];
        for (int i = 0; i < sliderTicks.length; i++){
            add(sliderTicks[i] = new ColorBlock(1, 9, ColorBlock.SEPARATOR_COLOR));
        }
        add(sliderNode);
    }
    
    protected abstract void onChange();
    
    protected void immediatelyOnChange(int currentVal) {
    }
    
    public int getSelectedValue(){
        return selectedVal;
    }
    
    public void setSelectedValue(int val) {
        this.selectedVal = val;
        sliderNode.x = (int)(x + tickDist*(selectedVal-minVal)) + 0.5f;
        sliderNode.y = sliderBG.y-4;
        PixelScene.align(sliderNode);
    }
    
    @Override
    protected void createChildren() {
        super.createChildren();
        
        add( BG = Chrome.get(Chrome.Type.RED_BUTTON));
        BG.alpha(0.5f);
        
        add(title = PixelScene.renderTextBlock(9));
        add(this.minTxt = PixelScene.renderTextBlock(6));
        add(this.maxTxt = PixelScene.renderTextBlock(6));
        
        add(sliderBG = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR));
        sliderNode = Chrome.get(Chrome.Type.RED_BUTTON);
        sliderNode.size(4, 7);
        
        pointerArea = new PointerArea(0, 0, 0, 0){
            boolean pressed = false;
            
            @Override
            protected void onPointerDown( PointerEvent event ) {
                pressed = true;
                PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                sliderNode.x = GameMath.gate(sliderBG.x-2, p.x - sliderNode.width()/2, sliderBG.x+sliderBG.width()-2);
                sliderNode.brightness(1.5f);
            }
            
            @Override
            protected void onPointerUp( PointerEvent event ) {
                if (pressed) {
                    PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                    sliderNode.x = GameMath.gate(sliderBG.x - 2, p.x - sliderNode.width()/2, sliderBG.x + sliderBG.width() - 2);
                    sliderNode.resetColor();
                    
                    //sets the selected value
                    selectedVal = minVal + Math.round((sliderNode.x - x) / tickDist);
                    sliderNode.x = x + tickDist * (selectedVal - minVal) + 0.5f;
                    PixelScene.align(sliderNode);
                    onChange();
                    pressed = false;
                }
            }
            
            @Override
            protected void onDrag( PointerEvent event ) {
                if (pressed) {
                    PointF p = camera().screenToCamera((int) event.current.x, (int) event.current.y);
                    sliderNode.x = GameMath.gate(sliderBG.x - 2, p.x - sliderNode.width()/2, sliderBG.x + sliderBG.width() - 2);
                    
                    immediatelyOnChange(minVal + Math.round((sliderNode.x - x) / tickDist));
                }
            }
        };
        add(pointerArea);
        
    }
    
    @Override
    protected void layout() {
        
        if (title.width() > 0.7f*width){
            String titleText = title.text;
            remove(title);
            title = PixelScene.renderTextBlock(6);
            add(title);
            title.text(titleText);
        }
        
        title.setPos(
                x + (width-title.width())/2,
                y+2
        );
        PixelScene.align(title);
        sliderBG.y = y + height() - 7;
        sliderBG.x = x+2;
        sliderBG.size(width-5, 1);
        tickDist = sliderBG.width()/(maxVal - minVal);
        int numValues = (maxVal - minVal) + 1;
        float distancePerSliderTick = sliderBG.width() / (sliderTicks.length-1);
        float posNextSliderTick = sliderBG.x;
        int indexNextSliderTick = 0;
        for (int i = 0; i < numValues; i++){
            float xPos = sliderBG.x + (tickDist*i);
            if (xPos >= posNextSliderTick) {
                sliderTicks[indexNextSliderTick].y = sliderBG.y-4;
                sliderTicks[indexNextSliderTick].x = xPos;
                PixelScene.align(sliderTicks[indexNextSliderTick]);
                indexNextSliderTick++;
                posNextSliderTick += distancePerSliderTick;
            }
        }
        sliderTicks[sliderTicks.length-1].y = sliderBG.y-4;
        sliderTicks[sliderTicks.length-1].x = sliderBG.x + (tickDist * (numValues-1) );
        PixelScene.align(sliderTicks[sliderTicks.length-1]);
        
        
        minTxt.setPos(
                x+1,
                sliderBG.y-5-minTxt.height()
        );
        maxTxt.setPos(
                x+width()-maxTxt.width()-1,
                sliderBG.y-5-minTxt.height()
        );
        
        sliderNode.x = x + tickDist*(selectedVal-minVal) + 0.5f;
        sliderNode.y = sliderBG.y-3;
        PixelScene.align(sliderNode);
        
        pointerArea.x = x;
        pointerArea.y = y;
        pointerArea.width = width();
        pointerArea.height = height();
        
        BG.size(width(), height());
        BG.x = x;
        BG.y = y;
        
    }
    
    @Override
    public void redirectPointerEvent(PointerEvent event) {
        super.redirectPointerEvent(event);
        if (pointerArea.onSignal(event)) event.handled = true;
    }
    
    @Override
    public void cancelClick() {
        pointerArea.reset();
        sliderNode.resetColor();
        
        //sets the selected value
        int curVal = getSelectedValue();
        selectedVal = minVal + Math.round((sliderNode.x - x) / tickDist);
        sliderNode.x = x + tickDist * (selectedVal - minVal) + 0.5f;
        PixelScene.align(sliderNode);
        if (curVal != selectedVal) onChange();
    }
}