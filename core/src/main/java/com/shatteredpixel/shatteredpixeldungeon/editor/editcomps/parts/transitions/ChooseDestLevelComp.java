package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.transitions;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.LevelScheme;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.WndSelectFloor;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseObjectComp;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChooseDestLevelComp extends ChooseObjectComp {

        public ChooseDestLevelComp(String label) {
            super(label);
        }

        @Override
        public void selectObject(Object object) {
            if (object.equals(Level.NONE)) super.selectObject(null);
            else super.selectObject(object);
        }

        @Override
        protected void doChange() {
            Window w = new WndSelectFloor() {
                @Override
                public boolean onSelect(LevelScheme levelScheme) {
                    Sample.INSTANCE.play(Assets.Sounds.CLICK);
                    selectObject(levelScheme.getName());
                    return true;
                }

                @Override
                protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
                    return ChooseDestLevelComp.this.filterLevels(levels);
                }
            };
            if (Game.scene() instanceof EditorScene) EditorScene.show(w);
            else Game.scene().addToFront(w);
        }

        public float getDW() {
            return display.width();
        }

        protected List<LevelScheme> filterLevels(Collection<LevelScheme> levels) {
            ArrayList<LevelScheme> ret = new ArrayList<>(levels);
            ret.add(0, LevelScheme.NO_LEVEL_SCHEME);
            return ret;
        }
    }