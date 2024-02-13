package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.ChangePlantNameDesc;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PlantItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.Objects;

public class EditPlantComp extends DefaultEditComp<Plant> {

    protected StyledCheckBox activateOnTrigger;

    public EditPlantComp(PlantItem plantItem) {
        this(plantItem.getObject());
    }

    public EditPlantComp(Plant item) {
        super(item);

        rename.visible = rename.active = true;

        activateOnTrigger = new StyledCheckBox(Messages.get(this, "activate_on_trigger"));
        activateOnTrigger.icon(EditorUtilies.createSubIcon(ItemSpriteSheet.Icons.RING_ACCURACY));
        activateOnTrigger.icon().scale.set(ItemSpriteSheet.SIZE / activateOnTrigger.icon().width());
        activateOnTrigger.checked(obj.activateOnTrigger);
        activateOnTrigger.addChangeListener(v -> {
            obj.activateOnTrigger = v;
            updateObj();
        });
        add(activateOnTrigger);
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(activateOnTrigger);
    }

    @Override
    protected void onRenameClicked() {
        Window parent = EditorUtilies.getParentWindow(this);
        SimpleWindow w = new SimpleWindow(parent.camera().width - 10, parent.camera().height - 10) {
            @Override
            public void hide() {
                super.hide();
                updateObj();
            }
        };
        w.initComponents(ChangePlantNameDesc.createTitle(), new ChangePlantNameDesc(EditPlantComp.this), null, 0f, 0.5f);
        EditorScene.show(w);
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
        return obj.getSprite();
    }

    @Override
    protected void updateObj() {

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Plant a, Plant b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;

        if (a.activateOnTrigger != b.activateOnTrigger) return false;

        if (!Objects.equals(a.customName, b.customName)) return false;
        if (!Objects.equals(a.customDesc, b.customDesc)) return false;

        return true;
    }
}