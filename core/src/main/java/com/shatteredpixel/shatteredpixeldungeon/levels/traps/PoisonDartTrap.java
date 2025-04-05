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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;

public class PoisonDartTrap extends DartTrap {

	{
		color = GREEN;
		shape = CROSSHAIR;
	}
	
	protected int poisonAmount(){
		return 8 + Math.round(2*scalingDepth() / 3f);
	}
	
	@Override
	protected void onHitEffect(Char target) {
		if (target == Dungeon.hero && Dungeon.level instanceof PrisonBossLevel){
			Statistics.qualifiedForBossChallengesBadge[1] = false;
			Statistics.bossScores[1] -= 100;
		}
		Buff.affect( target, Poison.class ).set( poisonAmount() );
	}
}
