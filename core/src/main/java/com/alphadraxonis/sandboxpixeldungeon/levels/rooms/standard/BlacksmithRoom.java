/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2023 Evan Debenham
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

package com.alphadraxonis.sandboxpixeldungeon.levels.rooms.standard;

import com.alphadraxonis.sandboxpixeldungeon.actors.Actor;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.alphadraxonis.sandboxpixeldungeon.items.Generator;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.Terrain;
import com.alphadraxonis.sandboxpixeldungeon.levels.painters.Painter;
import com.alphadraxonis.sandboxpixeldungeon.levels.traps.BurningTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class BlacksmithRoom extends StandardRoom {

    private boolean hasBlacksmith;

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 6);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 6);
    }

    public void paint(Level level) {

        Painter.fill(level, this, Terrain.WALL);
        Painter.fill(level, this, 1, Terrain.TRAP);
        Painter.fill(level, this, 2, Terrain.EMPTY_SP);

        for (int i = 0; i < 2; i++) {
            int pos;
            do {
                pos = level.pointToCell(random());
            } while (level.map[pos] != Terrain.EMPTY_SP);
            level.drop(
                    Generator.random(Random.oneOf(
                            Generator.Category.ARMOR,
                            Generator.Category.WEAPON,
                            Generator.Category.MISSILE
                    )), pos);
        }

        for (Door door : connected.values()) {
            door.set(Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 1, Terrain.EMPTY);
        }

        for (Point p : getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] == Terrain.TRAP) {
                level.setTrap(new BurningTrap().reveal(), cell);
            }
        }
    }

    public boolean placeBlacksmith(Blacksmith blacksmith, Level level) {
        if (hasBlacksmith) return false;
        do {
            blacksmith.pos = level.pointToCell(random(2));
        } while (Actor.findChar(blacksmith.pos) == null
                && blacksmith.pos != level.exit()
                && level.heaps.get(blacksmith.pos) != null);
        level.mobs.add(blacksmith);
        return hasBlacksmith = true;
    }
}