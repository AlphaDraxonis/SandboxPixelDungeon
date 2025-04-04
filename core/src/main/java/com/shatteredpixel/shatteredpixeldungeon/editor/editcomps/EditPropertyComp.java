package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PropertyItem;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;

public class EditPropertyComp extends DefaultEditComp<Char.Property> {

    public EditPropertyComp(Char.Property property) {
        super(property);
        initComps();
    }

    public EditPropertyComp(PropertyItem propertyItem) {
        super(propertyItem.getObject());
        initComps();
    }

    private void initComps() {
//        linearComps = new Component[0];
    }

    @Override
    protected void layout() {
        super.layout();
//        layoutCompsLinear(linearComps);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase(PropertyItem.getName(obj));
    }

    @Override
    protected String createDescription() {
        return PropertyItem.getDesc(obj);
    }

    @Override
    public Image getIcon() {
        return PropertyItem.getImage(obj);
    }


    public static boolean areEqual(Char.Property a, Char.Property b) {
        return a == b;
    }
}