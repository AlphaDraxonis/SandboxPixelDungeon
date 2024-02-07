package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class InspectObj extends Component {

    private final Reference reference;

    private final MultiWindowTabComp.OutsideSpSwitchTabs outsideSp;
    private IconTitle title;

    private final FieldsTab fieds;
    private final FieldsTab methods;
    private final Component[] tabs;


    public InspectObj(Reference reference) {
        this.reference = reference;

        tabs = new Component[]{
                fieds = new FieldsTab(),
                methods = new FieldsTab()
        };
        for (Component tab : tabs) {
            tab.visible = tab.active = false;
            add(tab);
        }

        outsideSp = new MultiWindowTabComp.OutsideSpSwitchTabs() {

            @Override
            protected void createChildren(Object... params) {
                tabs = new TabControlButton[2];
                tabs[0] = new MultiWindowTabComp.OutsideSpSwitchTabs.TabControlButton(0);
//                tabs[0].icon(createTabIcon(j));
                add(tabs[0]);
                tabs[1] = new MultiWindowTabComp.OutsideSpSwitchTabs.TabControlButton(1);
//                tabs[1].icon(createTabIcon(j));
                add(tabs[1]);

                super.createChildren(params);

                select(currentIndex);
            }

            @Override
            public void select(int index) {
                InspectObj.this.selectTab(index);
                super.select(index);
            }

            @Override
            public String getTabName(int index) {
                return InspectObj.getTabName(index);
            }
        };
    }

    public void selectTab(int index) {
        int currentIndex = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
        tabs[currentIndex].visible = tabs[currentIndex].active = false;
        tabs[index].visible = tabs[index].active = true;

        if (title != null) {

            title.icon(createTabIcon(index));
            title.label(getTabName(index));

            updateParentLayout();
        }
    }

    protected void updateParentLayout() {

    }

    @Override
    protected void layout() {
        for (Component tab : tabs) {
            if (tab.visible) {
                tab.setRect(x, y, width, -1);
                height = tab.height();
                break;
            }
        }
    }

    public Component createTitle() {
        int index = outsideSp == null ? 0 : outsideSp.getCurrentIndex();
        return title = new IconTitle(createTabIcon(index), Messages.titleCase(getTabName(index)));
    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    public static Image createTabIcon(int index) {
        return new ItemSprite();
    }

    public static String getTabName(int index) {
        return "Tab " + index;
    }

    private static final Component[] EMPTY_COMPONENT_ARRAY = new Component[0];

    private class FieldsTab extends Component {

        private final Component[] comps;
        private RenderedTextBlock info;

        public FieldsTab() {

            List<Component> temp = new ArrayList<>(6);
            temp.add(info);
            for (Field f : reference.getType().getFields()) {//TODO maybe also get inaccessible fields? need to use getDeclaredFields() and getSuperclass()
                if (Modifier.isStatic(f.getModifiers())) continue;
                FieldComp c = new FieldComp(f, reference.getValue());
                temp.add(c);
                add(c);
            }
            comps = temp.toArray(EMPTY_COMPONENT_ARRAY);
        }

        @Override
        protected void createChildren(Object... params) {
            info = PixelScene.renderTextBlock(Messages.get(InspectObj.class, "warning_change_fields"),6);
            add(info);
        }

        @Override
        protected void layout() {
            info.maxWidth((int) width);
            height = -1;
            height = EditorUtilies.layoutCompsLinear(2, 16, this, comps);
        }
    }
}