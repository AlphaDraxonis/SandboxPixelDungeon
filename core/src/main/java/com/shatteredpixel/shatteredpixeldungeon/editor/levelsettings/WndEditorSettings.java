package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;


import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.DungeonTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.items.ItemTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.LevelGenComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//From WndJournal
public class WndEditorSettings extends WndTabbed {

    //    public static final int WIDTH_P = 126;
//    public static final int HEIGHT_P = 180;
//
//    public static final int WIDTH_L = 200;
//    public static final int HEIGHT_L = 130;
    public static boolean closingBecauseMapSizeChange = false;

    public static int calclulateWidth() {
        return (int) Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9));
    }

    public static int calclulateHeight() {
        return (int) (PixelScene.uiCamera.height * 0.9);
    }

    public static final int ITEM_HEIGHT = 18;

    private final ItemTab itemTab = null;
    private final LevelTab levelTab;
    private final DungeonTab dungeonTab;
    private final TransitionTab transitionTab;
    private final LevelGenComp levelGenTab;
    private final TabComp[] ownTabs;

    public static int last_index = 0;
    private static WndEditorSettings instance;

    public WndEditorSettings() {

        if (instance != null) {
            instance.hide();
        }
        instance = this;

        Undo.startAction();

        offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar());
        resize(calclulateWidth(), calclulateHeight() - 50 - yOffset);

        ownTabs = new TabComp[]{
                levelTab = new LevelTab(),
                dungeonTab = new DungeonTab(),
                levelGenTab = new LevelGenComp(EditorScene.customLevel().levelScheme) {
                    @Override
                    protected void onFeelingChange() {
                        super.onFeelingChange();
                        EditorScene.updateDepthIcon();
                    }
                },
                transitionTab = new TransitionTab()};

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

                @Override
                protected String hoverText() {
                    return ownTabs[index].hoverText();
                }
            };
            add(tabs[i]);
        }

        layoutTabs();
        select(last_index);
    }

    public LevelTab getLevelTab() {
        return levelTab;
    }

    public DungeonTab getDungeonTab() {
        return dungeonTab;
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (ownTabs == null) return;
        for (TabComp tab : ownTabs) {
            tab.layout();
        }
    }

    @Override
    public void hide() {
        super.hide();
        instance = null;
    }

    public static WndEditorSettings getInstance() {
        return instance;
    }

    public static abstract class TabComp extends Component {

        public TabComp() {
        }

        public TabComp(Object... params) {
            super(params);
        }

        public void updateList() {
        }

        @Override
        public void layout() {
            super.layout();
        }

        public abstract Image createIcon();

        public abstract String hoverText();
    }

    @Override
    public void destroy() {
        super.destroy();
        Undo.endAction();
    }
}