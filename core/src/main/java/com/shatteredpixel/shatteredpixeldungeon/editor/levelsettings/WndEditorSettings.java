package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings;


import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.DungeonTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level.LevelTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor.LevelGenComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.NotAllowedInLua;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//From WndJournal
@NotAllowedInLua
public class WndEditorSettings extends WndTabbed {

//    public static final int WIDTH_P = 126;
//    public static final int HEIGHT_P = 180;
//
//    public static final int WIDTH_L = 200;
//    public static final int HEIGHT_L = 130;
    public static boolean closingBecauseMapSizeChange = false;

    public static final int ITEM_HEIGHT = 18;

    private final LevelTab levelTab;
    private final DungeonTab dungeonTab;
    private final TransitionTab transitionTab;
    private final LevelGenComp levelGenTab;
    private final LuaOverviewTab luaOverviewTab;
    private final TabComp[] ownTabs;

    public static int last_index = 0;
    private static WndEditorSettings instance;

    public WndEditorSettings() {

        if (instance != null) {
            instance.hide();
        }
        instance = this;

        Undo.startAction();

        offset(0, EditorUtilities.getMaxWindowOffsetYForVisibleToolbar());
        resize(WindowSize.WIDTH_LARGE.get(), WindowSize.HEIGHT_LARGE.get() - tabHeight() - yOffset - tabHeight());

        ownTabs = new TabComp[]{
                levelTab = EditorScene.isEditingRoomLayout ? null : new LevelTab((CustomLevel) Dungeon.level, Dungeon.level.levelScheme),
                dungeonTab = new DungeonTab(),
                levelGenTab = new LevelGenComp(Dungeon.level.levelScheme) {
                    @Override
                    protected void onFeelingChange() {
                        super.onFeelingChange();
                        EditorScene.updateDepthIcon();
                    }
                },
                transitionTab = EditorScene.isEditingRoomLayout ? null : new TransitionTab(),
                luaOverviewTab = new LuaOverviewTab()
        };

        Tab[] tabs = new Tab[ownTabs.length];
        for (int i = 0; i < ownTabs.length; i++) {
            if (ownTabs[i] == null) continue;
            add(ownTabs[i]);
            ownTabs[i].setRect(0, 0, width, height);
            ownTabs[i].updateList();
            int index = i;
            tabs[i] = new IconTab(ownTabs[i].createIcon()) {
                @Override
                protected void select(boolean value) {
                    super.select(value);
                    ownTabs[index].setVisible(value);
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
        
        if (ownTabs != null) {
            for (TabComp tab : ownTabs) {
                tab.layout();
            }
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