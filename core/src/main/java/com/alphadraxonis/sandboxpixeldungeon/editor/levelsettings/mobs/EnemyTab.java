package com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.mobs;

import com.alphadraxonis.sandboxpixeldungeon.Assets;
import com.alphadraxonis.sandboxpixeldungeon.actors.mobs.Mob;
import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.Koord;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.alphadraxonis.sandboxpixeldungeon.editor.levelsettings.editcomps.EditCompWindow;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;
import com.alphadraxonis.sandboxpixeldungeon.sprites.CharSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.SkeletonSprite;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        list.addTitle(Messages.get(EnemyTab.class,"title"));

        List<Mob> mobsOnFloor = new ArrayList<>(EditorScene.customLevel().mobs);

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