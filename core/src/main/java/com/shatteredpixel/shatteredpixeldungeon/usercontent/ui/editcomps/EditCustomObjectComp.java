package com.shatteredpixel.shatteredpixeldungeon.usercontent.ui.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.CustomObject;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

//this class is only needed as a wrapper, the true editing is done via CustomObjectEditor and its subclasses
public class EditCustomObjectComp<T extends CustomObject> extends DefaultEditComp<T> {

	protected Component[] rectComps;

    public EditCustomObjectComp(T obj) {
        super(obj);
		initializeCompsForCustomObjectClass();
    }

	@Override
	protected void layout() {
		super.layout();
		layoutCompsInRectangles(rectComps);

		layoutCustomObjectEditor();
	}

	protected String createTitleText() {
        return obj.getName();
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return obj.getSprite(this::updateObj);
    }
}