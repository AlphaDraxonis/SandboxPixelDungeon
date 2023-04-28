package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.levels.editor.Floor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Point;

public class EditorScene extends PixelScene {

    private static  EditorScene scene;


    protected static Floor floor=new Floor();

    private MenuPane menu;


    @Override
    public void create() {

        scene=this;

        int uiSize = SPDSettings.interfaceSize();

        menu = new MenuPane();
        menu.camera = uiCamera;
        menu.setPos(uiCamera.width - MenuPane.WIDTH, uiSize > 0 ? 0 : 1);
        add(menu);


    }

    public static void show( Window wnd ) {
        if (scene != null) {
            cancel();

            //If a window is already present (or was just present)
            // then inherit the offset it had
//            if (scene.inventory != null && scene.inventory.visible){
//                Point offsetToInherit = null;
//                for (Gizmo g : scene.members){
//                    if (g instanceof Window) offsetToInherit = ((Window) g).getOffset();
//                }
//                if (lastOffset != null) {
//                    offsetToInherit = lastOffset;
//                }
//                if (offsetToInherit != null) {
//                    wnd.offset(offsetToInherit);
//                    wnd.boundOffsetWithMargin(3);
//                }
//            }

            scene.addToFront(wnd);
        }
    }
    public static void cancel() {
//        cellSelector.resetKeyHold();
//        if (Dungeon.hero != null && (Dungeon.hero.curAction != null || Dungeon.hero.resting)) {
//
//            Dungeon.hero.curAction = null;
//            Dungeon.hero.resting = false;
//            return true;
//
//        } else {
//
//            return cancelCellSelector();
//
//        }
    }
    public void destroy() {

        //tell the actor thread to finish, then wait for it to complete any actions it may be doing.
//        if (!waitForActorThread( 4500, true )){
//            Throwable t = new Throwable();
//            t.setStackTrace(actorThread.getStackTrace());
//            throw new RuntimeException("timeout waiting for actor thread! ", t);
//        }
//
//        Emitter.freezeEmitters = false;

        scene = null;
//        Badges.saveGlobal();
//        Journal.saveGlobal();

        super.destroy();
    }

    public static Floor floor() {
        return floor;
    }

    public static String formatTitle(String name, Koord koord) {
        return Messages.titleCase(name) + ": " + koord;
    }
    public static String formatTitle(Floor.ItemWithPos item) {
        return formatTitle(item.item().title(),new Koord(item.pos()));
    }

}
