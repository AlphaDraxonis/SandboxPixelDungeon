package com.shatteredpixel.shatteredpixeldungeon.editor;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.WndSwitchFloor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.DangerIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

//public class SwitchLevelIndicator extends Tag {
//
//
//    Image icon;
//
//    public SwitchLevelIndicator() {
//        super(Window.SHPX_COLOR);
//
//        setSize(SIZE, SIZE);
//
//        icon = Icons.get(Icons.STAIRS);
//        add(icon);
//
//        active = true;
//        bg.visible=true;
//
//        flip(true);
//
//    }
//
//    //    @Override
////    public GameAction keyAction() {
////        return SPDAction.TAG_ATTACK;
////    }
//    @Override
//    protected synchronized void layout() {
//        super.layout();
//
//        if (icon != null) {
//            if (!flipped) icon.x = x + (SIZE - icon.width()) / 2f + 1;
//            else icon.x = x + width - (SIZE + icon.width()) / 2f - 1;
//            icon.y = y + (height - icon.height()) / 2f;
//            PixelScene.align(icon);
//        }
//    }
//
//    @Override
//    public void flip(boolean value) {
//        flipped=true;
//        bg.flipHorizontal(true);
//        layout();
//    }
//
//    @Override
//    protected void onClick() {
//        CustomDungeonSaves.saveLevel(EditorScene.floor());
//        EditorScene.show(new WndSwitchFloor());
//    }
//
//    @Override
//    protected String hoverText() {
//        return "Switch level";
////        return Messages.titleCase(Messages.get(WndKeyBindings.class, "tag_attack"));
//    }
//}