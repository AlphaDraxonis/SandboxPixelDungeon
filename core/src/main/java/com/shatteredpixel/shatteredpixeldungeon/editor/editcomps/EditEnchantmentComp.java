package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EnchantmentItem;
import com.shatteredpixel.shatteredpixeldungeon.items.EnchantmentLike;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditEnchantmentComp extends DefaultEditComp<EnchantmentLike> {

    private Component[] comps;

    public EditEnchantmentComp(EnchantmentLike ench) {
        super(ench);
        initComps();
    }

    public EditEnchantmentComp(EnchantmentItem enchItem) {
        super(enchItem.getObject());
        initComps();
    }

    private void initComps() {
        comps = new Component[0];
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(comps);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(obj.name());
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return EnchantmentItem.getImage(obj);
    }


    public static boolean areEqual(EnchantmentLike a, EnchantmentLike b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        return true;
    }
}