package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticReference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StaticValueReference;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class WndScrollOfDebug extends Window {

    static final List<Reference> references;

    static {
        references = new LinkedList<>();

        references.add(new StaticReference(Dungeon.class, "Dungeon"));

        references.add(new StaticValueReference<CustomDungeon>(CustomDungeon.class, "customDungeon") {
            @Override
            public CustomDungeon getValue() {
                return Dungeon.customDungeon;
            }
        });
        references.add(new StaticValueReference<Level>(Level.class, "level") {
            @Override
            public Level getValue() {
                return Dungeon.level;
            }
        });

        references.add(new StaticValueReference<Hero>(Hero.class, "hero") {
            @Override
            public Hero getValue() {
                return Dungeon.hero;
            }
        });

        references.add(new StaticReference(Statistics.class, "Statistics"));
    }

    public static void addReference(Reference reference) {
        references.add(reference);
        if (instance != null) instance.getReferenceTable().addReferenceToUI(reference);
    }

    private final ReferenceTable referenceTable;
    private static WndScrollOfDebug instance;

    public WndScrollOfDebug() {

        CustomDungeon.knowsEverything = true;

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        if (instance != null) {
            instance.hide();
        }
        instance = this;

        Undo.startAction();

        offset(0, EditorUtilies.getMaxWindowOffsetYForVisibleToolbar());

        referenceTable = new ReferenceTable();
        add(referenceTable);

        referenceTable.setSize(width, height);
    }

    public ReferenceTable getReferenceTable() {
        return referenceTable;
    }

    @Override
    public void hide() {
        CustomDungeon.knowsEverything = false;
        super.hide();
        instance = null;
    }

    public static WndScrollOfDebug getInstance() {
        return instance;
    }


    public static final int ACCESS_LEVEL_PRIVATE = 0, ACCESS_LEVEL_PCKGE_PRIVATE = 1, ACCESS_LEVEL_PROTECTED = 2, ACCESS_LEVEL_PUBLIC = 3;

    public static boolean canAccess(int modifiers, int minAccessLevel) {
        int accessLevel;
        if (Modifier.isPublic(modifiers)) accessLevel = ACCESS_LEVEL_PUBLIC;
        else if (Modifier.isPrivate(modifiers)) accessLevel = ACCESS_LEVEL_PRIVATE;
        else if (Modifier.isProtected(modifiers)) accessLevel = ACCESS_LEVEL_PROTECTED;
        else accessLevel = ACCESS_LEVEL_PCKGE_PRIVATE;

        return minAccessLevel <= accessLevel;
    }
}