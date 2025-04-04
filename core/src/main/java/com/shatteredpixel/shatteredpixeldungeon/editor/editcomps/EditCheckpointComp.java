package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CheckpointItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class EditCheckpointComp extends DefaultEditComp<Checkpoint> {

    protected StyledSpinner saves;
    protected StyledCheckBox heals, satiates, debuffs, uncurses;

    private final CheckpointItem checkpointItem;//used for linking the item with the sprite in the toolbar

    private Component[] comps;

    public EditCheckpointComp(Checkpoint item) {
        super(item);
        initComps();
        checkpointItem = null;
    }

    public EditCheckpointComp(CheckpointItem checkpointItem) {
        super(checkpointItem.getObject());
        initComps();
        this.checkpointItem = checkpointItem;
    }

    private void initComps() {

        saves = new StyledSpinner(new SpinnerIntegerModel(0, 1000, obj.totalSaves) {
            @Override
            public float getInputFieldWidth(float height) {
                return StyledSpinner.FILL;
            }

            @Override
            public void afterClick() {
                updateObj();
            }
        }, Messages.get(this, "saves"));
        saves.icon(new ItemSprite(ItemSpriteSheet.ANKH, new ItemSprite.Glowing( 0xFFFFCC )));
        saves.addChangeListener(() -> {
            obj.totalSaves = (int) saves.getValue();
            if (obj.totalSaves % 20 == 0)
                updateObj();//pretty expensive call for longer texts, so it is better to call this less
        });
        add(saves);

        heals = new StyledCheckBox(Messages.get(this, "heals"));
        heals.checked(obj.healingAvailable);
        heals.addChangeListener(v -> obj.healingAvailable = v);
        RectF r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            heals.icon(icon);
        }
        add(heals);

        satiates = new StyledCheckBox(Messages.get(this, "satiates"));
        satiates.checked(obj.satiationAvailable);
        satiates.addChangeListener(v -> obj.satiationAvailable = v);
        satiates.icon(new ItemSprite(ItemSpriteSheet.RATION));
        add(satiates);

        debuffs = new StyledCheckBox(Messages.get(this, "debuffs"));
        debuffs.checked(obj.debuffCuringAvailable);
        debuffs.addChangeListener(v -> obj.debuffCuringAvailable = v);
        r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_CLEANSE);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            debuffs.icon(icon);
        }
        add(debuffs);

        uncurses = new StyledCheckBox(Messages.get(this, "uncurses"));
        uncurses.checked(obj.uncursesAvailable);
        uncurses.addChangeListener(v -> obj.uncursesAvailable = v);
        r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_REMCURSE);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            uncurses.icon(icon);
        }
        add(uncurses);

        comps = new Component[]{
                saves, heals, satiates, debuffs, uncurses
        };
    }

    @Override
    protected void updateStates() {
        super.updateStates();
        saves.setValue(obj.totalSaves);
        heals.checked(obj.healingAvailable);
        satiates.checked(obj.satiationAvailable);
        debuffs.checked(obj.debuffCuringAvailable);
        uncurses.checked(obj.uncursesAvailable);
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsInRectangles(comps);
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

        if (checkpointItem != null) {
            ItemSlot slot = QuickSlotButton.containsItem(checkpointItem);
            if (slot != null) slot.item(checkpointItem);
        }

        if (obj.pos != -1) EditorScene.updateMap(obj.pos);

        if (obj.sprite != null) obj.sprite.updateSprite(obj);

        super.updateObj();
    }


    public static boolean areEqual(Checkpoint a, Checkpoint b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.getClass() != b.getClass()) return false;
        if (a.pos != b.pos) return false;
        if (a.totalSaves != b.totalSaves) return false;
        if (a.healingAvailable != b.healingAvailable) return false;
        if (a.satiationAvailable != b.satiationAvailable) return false;
        if (a.debuffCuringAvailable != b.debuffCuringAvailable) return false;
        if (a.uncursesAvailable != b.uncursesAvailable) return false;
        return true;
    }
}