package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollTrickster;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.Koord;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnemyTab extends WndEditorSettings.TabComp {

    private ScrollingListPane list;

    @Override
    protected void createChildren(Object... params) {
        list = new ScrollingListPane();
        add(list);
    }

    @Override
    public void layout() {
        super.layout();
        list.setRect(0, 0, width, height);
    }


    @Override
    protected void updateList() {

        list.clear();

        list.addTitle("Mob overview");

        List<Mob> mobsOnFloor = new ArrayList<>(EditorScene.floor().mobs);

//        for (Mob m : testMobs) {
//            m.pos = Random.Int(EditorScene.floor().length());
////                Buff.affectAnyBuffAndSetDuration(m,Burning.class,10);
//        }

        Collections.sort(mobsOnFloor, (m1, m2) -> m1.pos - m2.pos);

        for (Mob m : mobsOnFloor) {
            list.addItem(new MobCatalogItem(m));
        }
        list.setRect(x, y, width, height);
    }

    private static class MobCatalogItem extends AdvancedListPaneItem {
        private final Mob mob;

        public MobCatalogItem(Mob mob) {
            super(createSprite(mob), null, EditorScene.formatTitle(mob.name(), new Koord(mob.pos)));
            this.mob = mob;
        }

        @Override
        public void onClick() {
            Sample.INSTANCE.play(Assets.Sounds.CLICK);
            EditorScene.show(new EditCompWindow(mob,this));
        }

        @Override
        public void onUpdate() {
            if (mob == null) return;

            if (icon != null) remove(icon);
            icon = mob.sprite();
            addToBack(icon);
            remove(bg);
            addToBack(bg);

            super.onUpdate();
        }

        private static Image createSprite(Mob mob) {
            CharSprite sprite = mob.sprite();
            sprite.jumpToFrame((int) (Math.random() * sprite.idle.frames.length));//Shouldn't all be synchrony
            return sprite;
        }
    }

    @Override
    protected Image createIcon() {
        return new SkeletonSprite();
    }
}