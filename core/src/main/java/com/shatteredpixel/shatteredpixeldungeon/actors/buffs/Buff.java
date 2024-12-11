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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.HashSet;

public class Buff extends Actor {
	
	public Char target;

	//whether this buff was already extended by the mnemonic prayer spell
	public boolean mnemonicExtended = false;

	{
		actPriority = BUFF_PRIO; //low priority, towards the end of a turn
	}

	//determines how the buff is announced when it is shown.
	public enum buffType {POSITIVE, NEGATIVE, NEUTRAL}
	public buffType type = buffType.NEUTRAL;
	
	//whether or not the buff announces its name
	public boolean announced = false;

	//whether a buff should persist through revive effects or similar (e.g. transmogrify)
	public boolean revivePersists = false;
	
	protected HashSet<Class> resistances = new HashSet<>();
	
	public HashSet<Class> resistances() {
		return new HashSet<>(resistances);
	}
	
	protected HashSet<Class> immunities = new HashSet<>();
	
	public HashSet<Class> immunities() {
		return new HashSet<>(immunities);
	}

	public boolean permanent = false;
	public boolean zoneBuff = false;
	public boolean alwaysHidesFx = false;
	
	@Override
	public void initAsInventoryItem() {
		super.initAsInventoryItem();
		permanent = true; //for description
	}
	
	
	public boolean attachTo(Char target ) {

		if (target.isImmune( getClass() )) {
			return false;
		}
		
		this.target = target;

		if (target.add( this )){
			if (target.sprite != null) fx( true );
			return true;
		} else {
			this.target = null;
			return false;
		}
	}
	
	public void detach() {
		if (target.remove( this ) && target.sprite != null) fx( false );
	}
	
	@Override
	public boolean act() {
		if (permanent){
			spend(0.005f);
		} else {
			diactivate();
		}
		return true;
	}

	protected float timeWhenPaused;
	protected float cooldownWhenPaused;
	public void makePermanent(boolean flag) {
		if (permanent = flag) {
			timeWhenPaused = Actor.now();
			cooldownWhenPaused = cooldown();
		}
	}
	
	public int icon() {
		return BuffIndicator.NONE;
	}

	//some buffs may want to tint the base texture color of their icon
	public void tintIcon( Image icon ){
		//do nothing by default
	}

	//percent (0-1) to fade out out the buff icon, usually if buff is expiring
	public float iconFadePercent(){
		return 0;
	}

	//text to display on large buff icons in the desktop UI
	public String iconTextDisplay(){
		return "";
	}

	//visual effect usually attached to the sprite of the character the buff is attacked to
	public void fx(boolean on) {
		//do nothing by default
	}

	public String heroMessage(){
		String msg = Messages.get(this, "heromsg");
		if (msg.isEmpty()) {
			return null;
		} else {
			return msg;
		}
	}

	public String name() {
		return Messages.get(this, "name");
	}

    public String desc() {
        return Messages.get(this, "desc") + appendDescForPermanent();
    }

    protected String appendDescForPermanent() {
        if (zoneBuff) return "\n\n" + Messages.get(this, "zone_buff");
        return permanent ? "\n\n" + Messages.get(this, "permanent") : "";
    }

    //to handle the common case of showing how many turns are remaining in a buff description.
    protected String dispTurns(float input) {
        return permanent ? SpinnerIntegerModel.INFINITY : Messages.decimalFormat("#.##", input);
    }

	//buffs act after the hero, so it is often useful to use cooldown+1 when display buff time remaining
	public float visualcooldown(){
		return cooldown()+1f;
	}


    private static final String MNEMONIC_EXTENDED    = "mnemonic_extended";
	private static final String PERMANENT = "permanent";
	private static final String ZONE_BUFF = "zone_buff";
	private static final String ALWAYS_HIDES_FX = "always_hides_fx";
	private static final String TIME_WHEN_PAUSED = "time_when_paused";
	private static final String COOLDOWN_WHEN_PAUSED = "cooldown_when_paused";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (mnemonicExtended) bundle.put(MNEMONIC_EXTENDED, mnemonicExtended);
		bundle.put(PERMANENT, permanent);
		bundle.put(ZONE_BUFF, zoneBuff);
		bundle.put(ALWAYS_HIDES_FX, alwaysHidesFx);
		bundle.put(TIME_WHEN_PAUSED, timeWhenPaused);
		bundle.put(COOLDOWN_WHEN_PAUSED, cooldownWhenPaused);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(MNEMONIC_EXTENDED)) {
			mnemonicExtended = bundle.getBoolean(MNEMONIC_EXTENDED);
		}
		permanent = bundle.getBoolean(PERMANENT);
		zoneBuff = bundle.getBoolean(ZONE_BUFF);
		alwaysHidesFx = bundle.getBoolean(ALWAYS_HIDES_FX);
		timeWhenPaused = bundle.getFloat(TIME_WHEN_PAUSED);
		cooldownWhenPaused = bundle.getFloat(COOLDOWN_WHEN_PAUSED);
	}
	
	//creates a fresh instance of the buff and attaches that, this allows duplication.
    public static <T extends Buff> T append(Char target, Class<T> buffClass) {
        return append(target, Reflection.newInstance(buffClass));
    }

	public static <T extends Buff> T append(Char target, T buff) {
		buff = (T) buff.getCopy();
		buff.attachTo(target);
		return buff;
	}

    public static <T extends FlavourBuff> T append(Char target, Class<T> buffClass, float duration) {
        T buff = append(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return buff;
    }

	public static <T extends FlavourBuff> T append(Char target, T buff, float duration) {
		buff = append(target, buff);
		buff.spend(duration * target.resist(buff.getClass()));
		return buff;
	}


    //same as append, but prevents duplication.
    public static <T extends Buff> T affect(Char target, Class<T> buffClass) {
        T buff = target.buff(buffClass);
        if (buff != null) {
            return buff;
        } else {
            return append(target, buffClass);
        }
    }

	public static <T extends Buff> T affect(Char target, T buff) {
		T existingBuff = (T) target.buff(buff.getClass());
		if (existingBuff != null) {
			existingBuff.alwaysHidesFx |= buff.alwaysHidesFx;
			return existingBuff;
		} else {
			return append(target, buff);
		}
	}

    public static <T extends FlavourBuff> T affect(Char target, Class<T> buffClass, float duration) {
        T buff = affect(target, buffClass);
        buff.spend(duration * target.resist(buffClass));
        return buff;
    }

	public static <T extends FlavourBuff> T affect(Char target, T buff, float duration) {
		buff = affect(target, buff);
		buff.spend(duration * target.resist(buff.getClass()));
		return buff;
	}


	//postpones an already active buff, or creates & attaches a new buff and delays that.
	public static<T extends FlavourBuff> T prolong( Char target, Class<T> buffClass, float duration ) {
		T buff = affect( target, buffClass );
		buff.postpone( duration * target.resist(buffClass) );
		return buff;
	}


	public static<T extends CounterBuff> T count( Char target, Class<T> buffclass, float count ) {
		T buff = affect( target, buffclass );
		buff.countUp( count );
		return buff;
	}
	
	public static void detach( Char target, Class<? extends Buff> cl ) {
		for ( Buff b : target.buffs( cl )){
			b.detach();
		}
	}


    @Override
    public Actor getCopy() {
        Buff b = (Buff) super.getCopy();
        b.target = null;//Need to add later!
        return b;
    }

}