package com.alphadraxonis.sandboxpixeldungeon.editor.overview;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.CustomDungeon;
import com.alphadraxonis.sandboxpixeldungeon.editor.levels.ItemDistribution;
import com.alphadraxonis.sandboxpixeldungeon.scenes.PixelScene;
import com.alphadraxonis.sandboxpixeldungeon.sprites.GnollSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSprite;
import com.alphadraxonis.sandboxpixeldungeon.sprites.ItemSpriteSheet;
import com.alphadraxonis.sandboxpixeldungeon.ui.Icons;
import com.alphadraxonis.sandboxpixeldungeon.ui.RedButton;
import com.alphadraxonis.sandboxpixeldungeon.ui.RenderedTextBlock;
import com.alphadraxonis.sandboxpixeldungeon.ui.ScrollingListPane;
import com.alphadraxonis.sandboxpixeldungeon.ui.Window;
import com.alphadraxonis.sandboxpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class WndItemDistribution extends Window {


    private ScrollingListPane distributions;

    public WndItemDistribution() {

        resize(Math.min(WndTitledMessage.WIDTH_MAX, (int) (PixelScene.uiCamera.width * 0.9)), (int) (PixelScene.uiCamera.height * 0.8f));

        RenderedTextBlock title = PixelScene.renderTextBlock("Item Distribution", 11);
        title.hardlight(Window.TITLE_COLOR);
        title.maxWidth(width);
        title.setPos((width - title.width()) * 0.5f, 3);
        add(title);

        RedButton[] addDistr = new RedButton[2];
        addDistr[0] = new RedButton("+Items");
        addDistr[0].icon(Icons.get(Icons.SEED_POUCH));

        addDistr[1] = new RedButton("+Mobs");
        addDistr[1].icon(new GnollSprite());

        float widthPerBtn = (width - addDistr.length * 2 - 6 + 2) / (float) addDistr.length;
        for (int i = 0; i < addDistr.length; i++) {
            addDistr[i].setRect(3 + (widthPerBtn + 2) * i, height - 21, widthPerBtn, 18);
            add(addDistr[i]);
        }

        distributions = new ScrollingListPane();
        add(distributions);

        for(ItemDistribution<?> distr : CustomDungeon.getDungeon().getItemDistributions()){
            distributions.addItem(new DistributionCompInList(distr));
        }

        distributions.setRect(0, title.bottom() + 4, width, height - 18 - 3 - title.bottom() - 4);
        add(distributions);
    }

    public static void showWindow() {
        if (Game.scene() instanceof EditorScene) EditorScene.show(new WndItemDistribution());
        else Game.scene().addToFront(new WndItemDistribution());
    }


    private static class DistributionCompInList extends ScrollingListPane.ListItem {

        private static final int ICON_SIZE = 15;
        private final ItemDistribution distribution;

        public DistributionCompInList(ItemDistribution distribution) {
            super(new Image(),"Something" );
            this.distribution = distribution;
            updateUI();
        }

        //FIXME open edit window on click

        public void updateUI() {
            if (icon != null) {
                remove(icon);
                icon.destroy();
            }
            if (distribution.getObjectsToDistribute().isEmpty())
                icon = new ItemSprite(ItemSpriteSheet.SOMETHING);
            else {
                if (distribution instanceof ItemDistribution.Items)
                    icon=CustomDungeon.getDungeon().getItemImage(((ItemDistribution.Items) distribution).getObjectsToDistribute().get(0));
                else if (distribution instanceof ItemDistribution.Mobs)
                    icon = ((ItemDistribution.Mobs) distribution).getObjectsToDistribute().get(0).sprite();
                else icon = new ItemSprite(ItemSpriteSheet.SOMETHING);
            }
            add(icon);
            icon.x = Math.max((15 - icon.width) * 0.5f, 0);
            icon.y = Math.max((15 - icon.height) * 0.5f, 0);
            layout();
        }
    }
}