package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.MultiWindowTabComp;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.SearchBar;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.HashSet;
import java.util.Set;

public class ObjInspector extends Component {

    protected final Reference reference;

    protected MultiWindowTabComp.OutsideSpSwitchTabs outsideSp;

    private final Title title;
    private final FieldsTab fieldsTab;
    private final MethodsTab methodsTab;
    private DefaultEditComp<?> editComp;
    private final Component[] tabs;
    private final float[] scrollPos;


    public ObjInspector(Reference reference) {
        this.reference = reference;

        title = new Title();

        Object val = reference.getValue();
        editComp = EditCompWindow.createContent(val);

        if (editComp == null) {
            tabs = new Component[]{
                    fieldsTab = new FieldsTab(reference, WndScrollOfDebug.ACCESS_LEVEL_PRIVATE),
                    methodsTab = new MethodsTab(reference, WndScrollOfDebug.ACCESS_LEVEL_PUBLIC)
            };
        } else {
            editComp.setDoLayoutTitle(false);
            editComp.setOnUpdate(this::updateParentLayout);
            tabs = new Component[]{
                    editComp,
                    fieldsTab = new FieldsTab(reference, WndScrollOfDebug.ACCESS_LEVEL_PRIVATE),
                    methodsTab = new MethodsTab(reference, WndScrollOfDebug.ACCESS_LEVEL_PUBLIC)
            };
        }
        scrollPos = new float[tabs.length];
        for (Component tab : tabs) {
            tab.setVisible(false);
            add(tab);
        }

//        outsideSp = new MultiWindowTabComp.OutsideSpSwitchTabs() {
//
//            @Override
//            protected void createChildren(Object... params) {
//                tabs = new TabControlButton[ObjInspector.this.tabs.length];
//                for (int i = 0; i < tabs.length; i++) {
//                    tabs[i] = new MultiWindowTabComp.OutsideSpSwitchTabs.TabControlButton(i);
//                    add(tabs[i]);
//                }
//
//                super.createChildren(params);
//
//                select(currentIndex);
//            }
//
//            @Override
//            public void select(int index) {
//                ObjInspector.this.selectTab(index);
//                super.select(index);
//            }
//
//            @Override
//            public String getTabName(int index) {
//                return ObjInspector.getTabName(index);
//            }
//        };

        selectTab(0);
    }

    private void selectTab(int index) {
        int currentIndex = outsideSp == null ? 0 : outsideSp.getCurrentIndex();

        Camera camera = tabs[currentIndex].camera();
        if (camera != null) scrollPos[currentIndex] = camera.scroll.y;

        tabs[currentIndex].visible = tabs[currentIndex].active = false;
        tabs[index].visible = tabs[index].active = true;

        if (title != null) {
            if (index != 0 || editComp == null) {
                SearchBar searchBar = ((ObjInspectorTab) tabs[index]).getSearchBar();
                title.setContent(searchBar);
                searchBar.gainFocus();
                ((ObjInspectorTab) tabs[index]).updateValues();
            } else {
                editComp.remove();
                editComp.destroy();
                tabs[0] = editComp = EditCompWindow.createContent(editComp.getObj());
                add(editComp);
                editComp.setDoLayoutTitle(false);
                title.setContent(editComp.getTitleComponent());
                editComp.setOnUpdate(this::updateParentLayout);
            }
            updateParentLayout();
        }

        if (camera != null) scrollTo(0, scrollPos[index]);
    }

    public void selectFieldsTab() {
        outsideSp.select(editComp == null ? 0 : 1);
    }

    protected void updateParentLayout() {
    }

    protected void showDifferentInspectObj(Reference reference) {
    }

    protected void scrollTo(float x, float y) {
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
        if (tabs[index] != null) {
            if (index != 0 || editComp == null) title.setContent(((ObjInspectorTab) tabs[index]).getSearchBar());
            else title.setContent(editComp.getTitleComponent());
        }
        return title;
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

    private static class Title extends Component {

        private final Set<Component> contents = new HashSet<>(4);
        private Component current;

        public void setContent(Component content) {
            if (current != null) current.setVisible(false);
            if (!contents.contains(content)) {
                contents.add(content);
                add(content);
            }
            content.setVisible(true);
            current = content;
        }

        @Override
        protected void layout() {
            if (current != null) {
                current.setRect(x, y, width, height);
                x = current.bottom();
                y = current.top();
                width = current.width();
                height = current.height();
            }
        }
    }
}