package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public abstract class DMMob extends Mob {

    @Override
    public void move(int step, boolean travelling) {
        super.move(step, travelling);

        if (state == HUNTING &&
                (Dungeon.level.map[step] == Terrain.INACTIVE_TRAP && Dungeon.level instanceof CavesBossLevel
                || CustomTileItem.findCustomTileAt(step) instanceof CavesBossLevel.TrapTile)){
            //don't gain energy from cells that are energized
            if (CavesBossLevel.PylonEnergy.volumeAt(pos, CavesBossLevel.PylonEnergy.class) > 0){
                return;
            }

            if (Dungeon.level.heroFOV[step]) {
                if (buff(Barrier.class) == null && this instanceof DM300) {
                    GLog.w(Messages.get(this, "shield"));
                }
                Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
                sprite.emitter().start(SparkParticle.STATIC, 0.05f, 20);
            }

            Buff.affect(this, Barrier.class).setShield( HT/10 + (HT - HP)/10);
        }
    }

}