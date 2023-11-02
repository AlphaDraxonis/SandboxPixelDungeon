package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditBlobComp extends DefaultEditComp<Class<? extends Blob>> {

    private final VolumeSpinner volumeSpinner;
    private final ItemSelector sacrificialFirePrize;

    public EditBlobComp(Class<? extends Blob> item) {
        super(item);

        if (item == SacrificialFire.class) {
            volumeSpinner = new VolumeSpinner(Blob.volumeInInv.get(item));
            volumeSpinner.addChangeListener(() -> Blob.volumeInInv.put(item, (int) volumeSpinner.getValue()));
            add(volumeSpinner);
        } else volumeSpinner = null;

        if (item == SacrificialFire.class) {
            sacrificialFirePrize = new SacrificialFirePrize(SacrificialFire.prizeInInventory) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    SacrificialFire.prizeInInventory = selectedItem;
                }
            };
            add(sacrificialFirePrize);
        } else sacrificialFirePrize = null;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(volumeSpinner, sacrificialFirePrize);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    protected String createTitleText() {
        return BlobItem.createName(obj);
    }

    @Override
    protected String createDescription() {
        return Messages.get(obj, "desc");
    }

    @Override
    public Image getIcon() {
        return BlobItem.createIcon(obj);
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());
        super.updateObj();
    }

    public static class VolumeSpinner extends Spinner {

        public VolumeSpinner(Integer currentVolume) {
            super(new SpinnerIntegerModel(1, 100, currentVolume == null ? 1 : currentVolume, 1, false, null){
                {
                    setAbsoluteMinimum(1);
                }
            },Messages.get(VolumeSpinner.class, "label") + ":" ,10);
        }
    }

    public static abstract class SacrificialFirePrize extends ItemSelector {

        public SacrificialFirePrize(Item startItem) {
            super(Messages.get(SacrificialFirePrize.class, "label"), Item.class, startItem, ItemSelector.NullTypeSelector.RANDOM);
            setShowWhenNull(ItemSpriteSheet.SOMETHING);
        }

        @Override
        public void change() {
            EditorScene.selectItem(selector);
        }
    }
}