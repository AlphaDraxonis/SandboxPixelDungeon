package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.WndEditorSettings;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class WndScrollOfDebug extends WndTabbed {

    public static final Map<String, StaticReference<?>> superGlobalReferences;
    public static final Set<Class<?>> unassigneableClasses;//fields with this type or a subclass cannot be reassigned
    //also cannot change final fields

    static {
        Map<String, StaticReference<?>> tempMap = new LinkedHashMap<>(10);
        tempMap.put("customDungeon", new StaticReference<CustomDungeon>(CustomDungeon.class, "customDungeon") {
            @Override
            public CustomDungeon getValue() {
                return Dungeon.customDungeon;
            }
        });
        tempMap.put("level", new StaticReference<Level>(Level.class, "level") {
            @Override
            public Level getValue() {
                return Dungeon.level;
            }
        });
        tempMap.put("hero", new StaticReference<Hero>(Hero.class, "hero") {
            @Override
            public Hero getValue() {
                return Dungeon.hero;
            }
        });
        tempMap.put("depth", new StaticReference<Integer>(Integer.class, "depth") {
            @Override
            public Integer getValue() {
                return Dungeon.depth;
            }
        });
        tempMap.put("branch", new StaticReference<Integer>(Integer.class, "branch") {
            @Override
            public Integer getValue() {
                return Dungeon.branch;
            }
        });
        superGlobalReferences = Collections.unmodifiableMap(tempMap);

        Set<Class<?>> tempSet = new LinkedHashSet<>(5);
        tempSet.add(CustomDungeon.class);
        tempSet.add(Level.class);
        tempSet.add(Hero.class);
        unassigneableClasses = Collections.unmodifiableSet(tempSet);
    }

    public static final Map<String, Reference> globalReferences = new LinkedHashMap<>();

    {
    }


    //Reference Table: name of variables: Only latin characters, numerical only not on first position, start with lowercase
    //Has add button: either call any custom code, choose a constructor from any item available in inv excluding tiles, use a return value of a macro, or define a new primitive/String
    //   + name, and for first 3 options: choose type (either its class or a superclass)

    //Macro List: List of named macros, that are basically self-coded methods
    //Macro List contains build-in macros like adding mobs to a level
    //Macros cannot access

    //When calling methods: do not allow custom code to call methods that throw exceptions, and do not allow CustomDungeonSaves methods
    //Do not allow to assign new values to: Level, CustomDungeon, LevelScheme, Hero
    //super globals cannot be changed in any way

    //Warning: Reassigning non-primitive fields is dangerous as the same object is likely also stored somewhere else, and it wouldn't be changed there

    //Build.in macros:
    {
        EditorUtilies.cellToString(1);//and reverse
        //add mob to level: takes mob, returns mob
    }

    //    private final LevelTab levelTab;
    private final ReferenceTable referenceTable;
    private final WndEditorSettings.TabComp[] ownTabs;

    public static int last_index = 0;
    private static WndScrollOfDebug instance;

    public WndScrollOfDebug() {

        if (instance != null) {
            instance.hide();
        }
        instance = this;

        Undo.startAction();

        offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar());
        resize(WndEditorSettings.calclulateWidth(), WndEditorSettings.calclulateHeight() - 50 - yOffset);

        ownTabs = new WndEditorSettings.TabComp[]{
                referenceTable = new ReferenceTable(),
//                dungeonTab = new DungeonTab()
        };

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

//    public LevelTab getLevelTab() {
//        return levelTab;
//    }
//

    public ReferenceTable getReferenceTable() {
        return referenceTable;
    }

    @Override
    public void offset(int xOffset, int yOffset) {
        super.offset(xOffset, yOffset);
        if (ownTabs == null) return;
        for (WndEditorSettings.TabComp tab : ownTabs) {
            tab.layout();
        }
    }

    @Override
    public void hide() {
        super.hide();
        instance = null;
    }

    public static WndScrollOfDebug getInstance() {
        return instance;
    }
}