package com.shatteredpixel.shatteredpixeldungeon.editor.overview.floor;

import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.DeadEndLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

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