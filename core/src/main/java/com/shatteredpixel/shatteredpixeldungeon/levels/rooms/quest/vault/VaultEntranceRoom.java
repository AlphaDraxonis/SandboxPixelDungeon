package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;

public class VaultEntranceRoom extends StandardRoom {

	@Override
	public float[] sizeCatProbs() {
		return new float[]{0, 1, 0};
	}

	@Override
	public void paint(Level level) {
		Painter.fill( level, this, Terrain.WALL);
		Painter.fill( level, this, 1, Terrain.EMPTY );

		for (Room.Door door : connected.values()) {
			door.set( Room.Door.Type.REGULAR );
		}

		int entrance;
		do {
			entrance = level.pointToCell(random(2));
		} while (level.findMob(entrance) != null);
		
		int destCell =  level instanceof VaultLevel ? ((VaultLevel) level).destCell : -1;
		if (destCell >= 0) {
			level.transitions.put(entrance, new LevelTransition(level,
					entrance,
					LevelTransition.Type.BRANCH_ENTRANCE, destCell, 0));
		} else {
			level.addRegularEntrance(entrance);
		}
	}

	@Override
	public boolean isEntrance() {
		return true;
	}

	@Override
	public int maxConnections(int direction) {
		//only up and right right now
		if (direction == LEFT || direction == BOTTOM) return 0;
		return super.maxConnections(direction);
	}

	@Override
	public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
		return false;
	}
}
