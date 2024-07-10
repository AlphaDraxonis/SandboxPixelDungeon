package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.Checkpoint;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CheckpointItem;
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

    protected StyledSpinner saves, heals, satiates, debuffs, uncurses;

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

        heals = new StyledSpinner(new SpinnerIntegerModel(0, 1000, obj.totalHeals) {
            @Override
            public float getInputFieldWidth(float height) {
                return StyledSpinner.FILL;
            }

            @Override
            public void afterClick() {
                updateObj();
            }
        }, Messages.get(this, "heals"));
        RectF r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_HEALING);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            heals.icon(icon);
        }
        heals.addChangeListener(() -> {
            obj.totalHeals = (int) heals.getValue();
            if (obj.totalHeals % 20 == 0)
                updateObj();
        });
        add(heals);

        satiates = new StyledSpinner(new SpinnerIntegerModel(0, 1000, obj.totalSatiates) {
            @Override
            public float getInputFieldWidth(float height) {
                return StyledSpinner.FILL;
            }

            @Override
            public void afterClick() {
                updateObj();
            }
        }, Messages.get(this, "satiates"));
        satiates.icon(new ItemSprite(ItemSpriteSheet.RATION));
        satiates.addChangeListener(() -> {
            obj.totalSatiates = (int) satiates.getValue();
            if (obj.totalSatiates % 20 == 0)
                updateObj();
        });
        add(satiates);

        debuffs = new StyledSpinner(new SpinnerIntegerModel(0, 1000, obj.totalDebuffCuring) {
            @Override
            public float getInputFieldWidth(float height) {
                return StyledSpinner.FILL;
            }

            @Override
            public void afterClick() {
                updateObj();
            }
        }, Messages.get(this, "debuffs"));
        r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.POTION_CLEANSE);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            debuffs.icon(icon);
        }
        debuffs.addChangeListener(() -> {
            obj.totalDebuffCuring = (int) debuffs.getValue();
            if (obj.totalDebuffCuring % 20 == 0)
                updateObj();
        });
        add(debuffs);

        uncurses = new StyledSpinner(new SpinnerIntegerModel(0, 1000, obj.totalUncurse) {
            @Override
            public float getInputFieldWidth(float height) {
                return StyledSpinner.FILL;
            }

            @Override
            public void afterClick() {
                updateObj();
            }
        }, Messages.get(this, "uncurses"));
        r = ItemSpriteSheet.Icons.film.get(ItemSpriteSheet.Icons.SCROLL_REMCURSE);
        if (r != null) {
            Image icon = new Image(Assets.Sprites.ITEM_ICONS);
            icon.frame(r);
            icon.scale.set(10 / Math.max(icon.width(), icon.height()));
            uncurses.icon(icon);
        }
        uncurses.addChangeListener(() -> {
            obj.totalUncurse = (int) uncurses.getValue();
            if (obj.totalUncurse % 20 == 0)
                updateObj();
        });
        add(uncurses);

        comps = new Component[]{
                saves, heals, satiates, debuffs, uncurses
        };
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
    protected void updateObj() {

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
        if (a.totalHeals != b.totalHeals) return false;
        if (a.totalSatiates != b.totalSatiates) return false;
        if (a.totalDebuffCuring != b.totalDebuffCuring) return false;
        if (a.totalUncurse != b.totalUncurse) return false;
        return true;
    }
}