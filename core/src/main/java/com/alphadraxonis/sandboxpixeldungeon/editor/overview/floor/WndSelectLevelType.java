package com.alphadraxonis.sandboxpixeldungeon.editor.overview.floor;

import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomLevel;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.alphadraxonis.sandboxpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.alphadraxonis.sandboxpixeldungeon.levels.CavesBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.CavesLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.CityBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.CityLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.DeadEndLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.HallsBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.HallsLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.LastLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.Level;
import com.alphadraxonis.sandboxpixeldungeon.levels.PrisonBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.PrisonLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.SewerBossLevel;
import com.alphadraxonis.sandboxpixeldungeon.levels.SewerLevel;
import com.alphadraxonis.sandboxpixeldungeon.messages.Messages;

public abstract class WndSelectLevelType extends WndChooseOneInCategories {

    public WndSelectLevelType(boolean flag) {
        super(Messages.get(WndSelectLevelType.class,"title"), Messages.get(WndSelectLevelType.class,"desc"),
                createCategories(flag),
                new String[]{
                        Messages.get(WndSelectLevelType.class,"type_custom"),
                        Messages.get(WndSelectLevelType.class,"type_regular"),
                        Messages.get(WndSelectLevelType.class,"type_boss")});
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
        ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
        for (int i = 0; i < category.length; i++) {
            final Class<? extends Level> clazz = (Class<? extends Level>) category[i];
            if (clazz == null) ret[i] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndSelectLevelType.class,"type_none"),
                    Messages.get(WndSelectLevelType.class,"none_info")) {
                @Override
                protected void onClick() {
                    click(null);
                }
            };
            else if (clazz == CustomLevel.class) ret[i] = new ChooseOneInCategoriesBody.BtnRow(Messages.get(WndSelectLevelType.class,"type_custom"),
                    Messages.get(WndSelectLevelType.class,"custom_info")){
                @Override
                protected void onClick() {
                    click(clazz);
                }
            };
            else ret[i] = new ChooseOneInCategoriesBody.BtnRow(clazz.getSimpleName(),Messages.get(WndSelectLevelType.class,"new_msg",clazz.getSimpleName())){
                    @Override
                    protected void onClick() {
                        click(clazz);
                    }
                };
        }
        return ret;
    }
    private void click(Class<? extends Level> clazz){
        onSelect(clazz);
        finish();
    }

    private static Object[][] createCategories(boolean includeNull) {
        if (includeNull) {
            return new Object[][]{
                    {null, LastLevel.class, DeadEndLevel.class},
                    {SewerLevel.class, PrisonLevel.class, CavesLevel.class, CityLevel.class, HallsLevel.class},
//                    {SewerBossLevel.class, PrisonBossLevel.class, CavesBossLevel.class, CityBossLevel.class, HallsBossLevel.class}
            };
        }
        return new Object[][]{
                {CustomLevel.class, LastLevel.class, DeadEndLevel.class},
                {SewerLevel.class, PrisonLevel.class, CavesLevel.class, CityLevel.class, HallsLevel.class},
                {SewerBossLevel.class, PrisonBossLevel.class, CavesBossLevel.class, CityBossLevel.class, HallsBossLevel.class}
        };
    }

    protected abstract void onSelect(Class<? extends Level> clazz);
}