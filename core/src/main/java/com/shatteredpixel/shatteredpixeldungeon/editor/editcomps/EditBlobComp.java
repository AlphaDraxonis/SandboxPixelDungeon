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
import com.watabou.noosa.Image;

public class EditBlobComp extends DefaultEditComp<Class<? extends Blob>> {

    protected VolumeSpinner volumeSpinner;
    protected ItemSelector sacrificialFirePrize;

    public EditBlobComp(Class<? extends Blob> item) {
        super(item);

        if (item == SacrificialFire.class) {
            volumeSpinner = new VolumeSpinner(Blob.volumeInInv.get(item));
            volumeSpinner.addChangeListener(() -> Blob.volumeInInv.put(item, (int) volumeSpinner.getValue()));
            add(volumeSpinner);
        }

        if (item == SacrificialFire.class) {
            sacrificialFirePrize = new SacrificialFirePrize(SacrificialFire.prizeInInventory) {
                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);
                    SacrificialFire.prizeInInventory = selectedItem;
                }
            };
            add(sacrificialFirePrize);
        }
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(volumeSpinner, sacrificialFirePrize);
    }

    @Override
    protected String createTitleText() {
        return Messages.titleCase( BlobItem.createName(obj) );
    }

    @Override
    protected String createDescription() {
        return Messages.get(obj, "desc");
    }

    @Override
    public Image getIcon() {
        return BlobItem.createIcon(obj);
    }

    public static class VolumeSpinner extends Spinner {

        public VolumeSpinner(Integer currentVolume) {
            super(new SpinnerIntegerModel(1, 100, currentVolume == null ? 1 : currentVolume){
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