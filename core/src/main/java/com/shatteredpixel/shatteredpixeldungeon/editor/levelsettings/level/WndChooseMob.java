package com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.level;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Reflection;

import java.util.HashSet;
import java.util.Set;

public class WndChooseMob extends WndChooseOneInCategories {

    private static float lastView;

    public WndChooseMob(String desc) {
        this(desc,new HashSet<>());
    }
    public WndChooseMob(String desc, Set<Class<? extends  Mob>> mobsToIgnore) {
        super(Messages.get(WndChooseMob.class,"title"),
                desc, Mobs.getAllMobs(mobsToIgnore), Mobs.getAllNames());
        scrollTo(lastView);
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
        ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[category.length];
        for (int i = 0; i < ret.length; i++) {
            Mob m = (Mob) Reflection.newInstance((Class<?>)category[i]);
            ret[i] = new ChooseOneInCategoriesBody.BtnRow(m.name(), m.description(), m.sprite()) {
                @Override
                protected void onClick() {
                    onSelect(m);
                    finish();
                }
            };
        }
        return ret;
    }


    protected void onSelect(Mob mob) {
    }

    @Override
    public void hide() {
        lastView = getCurrentViewY();
        super.hide();
    }

    public static void resetLastView() {
        lastView = 0;
    }
}