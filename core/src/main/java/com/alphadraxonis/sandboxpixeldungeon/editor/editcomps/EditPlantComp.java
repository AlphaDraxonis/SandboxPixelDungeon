package com.alphadraxonis.sandboxpixeldungeon.editor.editcomps;

import com.alphadraxonis.sandboxpixeldungeon.editor.EditorScene;
import com.alphadraxonis.sandboxpixeldungeon.editor.inv.items.PlantItem;
import com.alphadraxonis.sandboxpixeldungeon.plants.Plant;
import com.alphadraxonis.sandboxpixeldungeon.ui.ItemSlot;
import com.alphadraxonis.sandboxpixeldungeon.ui.QuickSlotButton;
import com.alphadraxonis.sandboxpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class EditPlantComp extends DefaultEditComp<Plant> {

    private final PlantItem plantItem;//used for linking the item with the sprite in the toolbar

//    private Component[] comps;

    public EditPlantComp(Plant item) {
        super(item);
//        initComps();
        plantItem = null;
    }

    public EditPlantComp(PlantItem plantItem) {
        super(plantItem.plant());
//        initComps();
        this.plantItem = plantItem;
    }

//    private void initComps() {
//        comps = new Component[]{};
//    }

//    @Override
//    protected void layout() {
//        super.layout();
//        layoutCompsLinear(comps);
//    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), PlantItem.createTitle(obj));
    }

    @Override
    protected String createDescription() {
        return obj.desc();
    }

    @Override
    public Image getIcon() {
        return PlantItem.getPlantImage(obj);
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(PlantItem.createTitle(obj));
            ((IconTitle) title).icon(PlantItem.getPlantImage(obj));
        }
        desc.text(createDescription());

        if (plantItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(plantItem);
            if (slot != null) slot.item(plantItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Plant a, Plant b) {
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        return true;
    }
}