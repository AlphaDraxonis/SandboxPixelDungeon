package com.shatteredpixel.shatteredpixeldungeon.editor.ui;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public abstract class WndChooseOneInCategories extends Window {

    private final ChooseOneInCategoriesBody body;

    public WndChooseOneInCategories(String title, String desc,Object[][] categories, String[] categoryNames){
        this(PixelScene.renderTextBlock(Messages.titleCase( title),10),desc,categories,categoryNames);
    }
    public WndChooseOneInCategories(Component titleBar, String desc, Object[][] categories, String[] categoryNames) {
        super();
        int WIDTH = Math.min(160, (int) (PixelScene.uiCamera.width * 0.9));
        int HEIGHT = (int) (PixelScene.uiCamera.height * 0.8);
        resize(WIDTH, HEIGHT);

        body = new ChooseOneInCategoriesBody(titleBar, desc, categories, categoryNames) {

            @Override
            public void onCancel() {
                hide();
            }

            @Override
            protected BtnRow[] createCategoryRows(Object[] category) {
                return WndChooseOneInCategories.this.createCategoryRows(category);
            }
        };
        add(body);
        body.setSize(WIDTH, HEIGHT);
    }

    protected abstract ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category);

    protected void finish() {
        hide();
    }


    public final void scrollUp() {
        body.scrollUp();
    }

    public final void scrollDown() {
       body.scrollDown();
    }

    protected final void scrollTo(float yPos) {
        body.scrollTo(yPos);
    }

    protected final float getCurrentViewY() {
        return body.getCurrentViewY();
    }

}