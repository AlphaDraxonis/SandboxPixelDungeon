package com.shatteredpixel.shatteredpixeldungeon.editor;

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