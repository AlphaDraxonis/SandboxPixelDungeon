package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ParticleItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextIconModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class EditParticleComp extends DefaultEditComp<CustomParticle.ParticleProperty> {

    //TODO tzz Strings
    //And show links to open such window directly from EditTileComp

    protected Emitter emitter;

    protected StyledSpinner type;
    protected StyledSpinner interval;
    protected StyledSpinner quantity;
    protected StyledCheckBox removeOnEnter;

    public EditParticleComp(CustomParticle.ParticleProperty particle) {
        super(particle);

        emitter = new Emitter() {
            @Override
            protected void emit(int index) {
                //From BlobEmitter
                float x = emitter.x + Random.Float(BlobEmitter.bound.left, BlobEmitter.bound.right) * DungeonTilemap.SIZE;
                float y = emitter.y + Random.Float(BlobEmitter.bound.top, BlobEmitter.bound.bottom) * DungeonTilemap.SIZE;
                factory.emit(this, index, x, y);
            }
        };
        add(emitter);

        type = new StyledSpinner(new SpinnerTextIconModel() {//TODO tzz
            @Override
            protected Image getIcon(Object value) {
                return null;
            }

            @Override
            public Component createInputField(int fontSize) {
                return super.createInputField(fontSize - 1);
            }
        }, "TYPE tzz", 8);
        type.setButtonWidth(9f);
        add(type);

        interval = new StyledSpinner(new SpinnerFloatModel(0.01f, 10f, particle.interval, 2, 0.1f, false),
                "INTERVAL tzz", 9);
        interval.addChangeListener(() -> {
            particle.interval = SpinnerFloatModel.convertToFloat((Integer) interval.getValue(), 2);
            updateObj();
        });
        add(interval);

        //Should not be able to set quantity
//        quantity = new StyledSpinner(new SpinnerIntegerModel(0, 100, particle.quantity, 1, false, null),
//                "QUANTITY", 9);
//        quantity.addChangeListener(() -> {
//            particle.quantity = (int) quantity.getValue();
//            updateObj();
//        });
//        add(quantity);

        removeOnEnter = new StyledCheckBox("REMOVE_ON_ENTER tzz");
        removeOnEnter.checked(particle.removeOnEnter);
        removeOnEnter.addChangeListener(v -> particle.removeOnEnter = v);
        add(removeOnEnter);

        updateObj();
    }

    @Override
    protected void layout() {
        super.layout();
        emitter.x = x + width * 0.5f;
        emitter.y = height() + y + DungeonTilemap.SIZE/2;
        height += DungeonTilemap.SIZE*2;
        layoutCompsInRectangles(type, interval, quantity, removeOnEnter);
    }

    @Override
    protected Component createTitle() {
        return new IconTitle(getIcon(), createTitleText());
    }

    protected String createTitleText() {
        return obj.name;
    }

    @Override
    protected String createDescription() {
        return null;
    }

    @Override
    public Image getIcon() {
        return ParticleItem.createIcon(obj);
    }

    @Override
    protected void updateObj() {
        if (title instanceof IconTitle) {
            ((IconTitle) title).label(createTitleText());
            ((IconTitle) title).icon(getIcon());
        }
        desc.text(createDescription());

        if (emitter != null) {
            emitter.start(Speck.factory(obj.type), obj.interval, obj.quantity);
            EditorScene.updateParticle(obj.particleID());
        }

        super.updateObj();
    }
}