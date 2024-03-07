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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class BlacksmithSprite extends MobSprite {

    private Emitter emitter;

    public BlacksmithSprite() {
        super();

        texture(Assets.Sprites.TROLL);

        TextureFilm frames = new TextureFilm(texture, 13, 16);

        idle = new Animation(15, true);
        idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 3);

        run = new Animation(20, true);
        run.frames(frames, 0);

        die = new Animation(20, false);
        die.frames(frames, 0);

        attack = new Animation(15, false);
        attack.frames(frames,  2, 3, 3);

        play(idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);

        emitter = new Emitter();
        emitter.autoKill = false;
        emitter.pos(x + 7, y + 12);
        parent.add(emitter);
    }

    @Override
    public void update() {
        super.update();

        if (emitter != null) {
            emitter.visible = visible;
        }
    }

    @Override
    public void onComplete(Animation anim) {
        super.onComplete(anim);

        if (visible && emitter != null && anim == idle) {
            emitter.burst(Speck.factory(Speck.FORGE), 3);
            float volume = CustomDungeon.isEditing() ? 0.01f : 0.2f / (Dungeon.level.distance(ch.pos, Dungeon.hero.pos)); //FIXME add setting to change/disable volume
            Sample.INSTANCE.play(Assets.Sounds.EVOKE, volume, 0.8f);
        }
    }

}