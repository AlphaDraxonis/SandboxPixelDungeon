package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;


import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.gerneral.GeneralTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.ItemTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.mobs.EnemyTab;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//From WndJournal
public class WndEditorSettings extends WndTabbed {

    //    public static final int WIDTH_P = 126;
//    public static final int HEIGHT_P = 180;
//
//    public static final int WIDTH_L = 200;
//    public static final int HEIGHT_L = 130;
    public static int calclulateWidth() {
        return (int) Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
    }

    public static int calclulateHeight() {
        return (int) (PixelScene.uiCamera.height * 0.63);
    }

    public static final int ITEM_HEIGHT = 18;

    private final EnemyTab enemyTab;
    private final ItemTab itemTab = null;
    private final GeneralTab generalTab;
    private final TransitionTab transitionTab;
    private final TabComp[] ownTabs;

    public static int last_index = 0;

    public WndEditorSettings() {

//        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;
//        int height = PixelScene.landscape() ? HEIGHT_L : HEIGHT_P;
        int width = calclulateWidth();
        int height = calclulateHeight();

        resize(width, height);

        ownTabs = new TabComp[]{
                enemyTab = new EnemyTab(),
//                itemTab = new ItemTab(),
                transitionTab = new TransitionTab(),
                generalTab = new GeneralTab()};

        Tab[] tabs = new Tab[ownTabs.length];
        for (int i = 0; i < ownTabs.length; i++) {
            add(ownTabs[i]);
            ownTabs[i].setRect(0, 0, width, height);
            ownTabs[i].updateList();
            int index = i;
            tabs[i] = new IconTab(ownTabs[i].createIcon()) {
                protected void select(boolean value) {
                    super.select(value);
                    ownTabs[index].active = ownTabs[index].visible = value;
                    if (value) last_index = index;
                }
            };
            add(tabs[i]);
        }

        layoutTabs();
        select(last_index);
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        for (TabComp tab : ownTabs) {
            tab.layout();
        }
    }


    public static abstract class TabComp extends Component {

        protected void updateList() {
        }

        @Override
        public void layout() {
            super.layout();
        }

        protected abstract Image createIcon();
    }

}