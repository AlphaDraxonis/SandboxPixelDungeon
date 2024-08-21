package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables.ChangeCustomizable;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.PlantItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.UserContentManager;
import com.shatteredpixel.shatteredpixeldungeon.usercontent.interfaces.CustomGameObjectClass;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.Objects;

public class EditPlantComp extends DefaultEditComp<Plant> {

    protected StyledCheckBox activateOnTrigger;
    protected StyledItemSelector dropItem;

    private final Component[] comps;

    public EditPlantComp(PlantItem plantItem) {
        this(plantItem.getObject());
    }

    public EditPlantComp(Plant plant) {
        super(plant);

        rename.setVisible(!(plant instanceof CustomGameObjectClass) || ((CustomGameObjectClass) plant).getInheritStats());

        activateOnTrigger = new StyledCheckBox(Messages.get(this, "activate_on_trigger"));
        activateOnTrigger.icon(EditorUtilities.createSubIcon(ItemSpriteSheet.Icons.RING_ACCURACY));
        activateOnTrigger.icon().scale.set(ItemSpriteSheet.SIZE / activateOnTrigger.icon().width());
        activateOnTrigger.checked(obj.activateOnTrigger);
        activateOnTrigger.addChangeListener(v -> {
            obj.activateOnTrigger = v;
            updateObj();
        });
        add(activateOnTrigger);

        dropItem = new StyledItemSelector(Messages.get(this, "drop_item"), Item.class, plant.dropItem, ItemSelector.NullTypeSelector.NOTHING) {
            @Override
            public void setSelectedItem(Item selectedItem) {
                super.setSelectedItem(selectedItem);
                plant.dropItem = selectedItem;
            }

            @Override
            public void change() {
                EditorScene.selectItem(selector);
            }
        };
        add(dropItem);

        comps = new Component[] {
                activateOnTrigger, dropItem
        };

        initializeCompsForCustomObjectClass();
    }

    @Override
    protected void updateStates() {
        super.updateStates();
        if (dropItem != null) dropItem.setSelectedItem(obj.dropItem);
        if (activateOnTrigger != null) activateOnTrigger.checked(obj.activateOnTrigger);
    }

    @Override
    protected void onInheritStatsClicked(boolean flag, boolean initializing) {
        if (flag && !initializing) {
            obj.copyStats((Plant) UserContentManager.getLuaClass(((CustomGameObjectClass) obj).getIdentifier()));
        }

        for (Component c : comps) {
            if (c != null) c.visible = c.active = !flag;
        }

        if (rename != null) rename.setVisible(!flag);

        ((CustomGameObjectClass) obj).setInheritStats(flag);
//        if (viewScript != null) viewScript.visible = viewScript.active = true;
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);

        layoutCustomObjectEditor();
    }

    @Override
    protected void onRenameClicked() {
        ChangeCustomizable.showAsWindow(this, w -> new ChangeCustomizable<>(w, this));
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
	public void updateObj() {

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        super.updateObj();
    }


    public static boolean areEqual(Plant a, Plant b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;

        if (a.activateOnTrigger != b.activateOnTrigger) return false;

        if (!Objects.equals(a.getCustomName(), b.getCustomName())) return false;
        if (!Objects.equals(a.getCustomDesc(), b.getCustomDesc())) return false;

        if (!EditItemComp.areEqual(a.dropItem, b.dropItem)) return false;

        return true;
    }
}