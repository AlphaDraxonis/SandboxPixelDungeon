package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ParticleItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.other.CustomParticle;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndSelectDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StyledCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerFloatModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Random;

public class EditParticleComp extends DefaultEditComp<CustomParticle.ParticleProperty> {

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

        Integer[] data = createTypeData();
        type = new StyledSpinner(new SpinnerTextModel(true, findIndex(obj.type, data), (Object[]) data) {
            @Override
            protected String getAsString(Object value) {
                return Messages.get(Speck.class, String.valueOf(value));
            }

            @Override
            public Component createInputField(int fontSize) {
                return super.createInputField(fontSize - 1);
            }
        }, Messages.get(this, "type"), 8);
        type.setButtonWidth(9f);
        type.addChangeListener(()-> {
            obj.type = (int) type.getValue();
            obj.setPredefinedInterval(obj.type);
            interval.setValue(SpinnerFloatModel.convertToInt(obj.interval, 2));
            updateObj();
        });
        add(type);

        interval = new StyledSpinner(new SpinnerFloatModel(0.01f, 10f, particle.interval, 2, 0.1f, false) {
            {
                setAbsoluteMaximum(100f);
            }
        }, Messages.get(this, "interval"), 9);
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

        removeOnEnter = new StyledCheckBox(Messages.get(this, "remove_on_enter"));
        removeOnEnter.checked(particle.removeOnEnter);
        removeOnEnter.addChangeListener(v -> particle.removeOnEnter = v);
        add(removeOnEnter);

        rename.visible = true;

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

    protected String createTitleText() {
        return obj.name;
    }

    @Override
    protected String createDescription() {
        return null;
    }

    @Override
    public Image getIcon() {
        return obj.getSprite();
    }

    @Override
    protected void updateObj() {

        if (emitter != null) {
            emitter.start(obj.createFactory(), obj.interval, obj.quantity);
            EditorScene.updateParticle(obj.particleID());
        }

        super.updateObj();
    }

    private static String lastEnteredText;
    @Override
    protected void onRenameClicked() {
        EditorScene.show(new WndTextInput(Messages.get(EditParticleComp.class, "rename_title"),
                "",
                lastEnteredText == null ? obj.name : lastEnteredText,
                100,
                false,
                Messages.get(WndSelectDungeon.class, "rename_yes"),
                Messages.get(WndSelectDungeon.class, "export_no")) {
            @Override
            public void onSelect(boolean positive, String text) {
                lastEnteredText = text;
                if (positive && !text.isEmpty()) {
                    for (CustomParticle.ParticleProperty particle : Dungeon.customDungeon.particles.values()) {
                        if (text.equals(particle.name)) {
                            onRenameClicked();
                            EditorUtilies.showDuplicateNameWarning();
                            return;
                        }
                    }
                    if (!text.equals(obj.name)) {
                        obj.name = text;
                        updateObj();
                    }
                }
                lastEnteredText = null;
            }
        });
    }

    private static int findIndex(int obj, Integer[] data) {
        for (int i = 0; i < data.length; i++) {
            if (obj == data[i]) return i;
        }
        return 0;
    }

    private static Integer[] createTypeData() {
        return new Integer[] {

                Speck.LIGHT,
                Speck.RED_LIGHT,
                Speck.DISCOVER,

                Speck.STAR,
                Speck.EVOKE,
                Speck.MASK,
                Speck.CROWN,
                Speck.FORGE,

                Speck.HEALING,
                Speck.QUESTION,
                Speck.UP,
                Speck.CHANGE,
                Speck.HEART,
                Speck.NOTE,
                Speck.SCREAM,
                Speck.CALM,

                Speck.RATTLE,
                Speck.BONE,

                CustomParticle.WIND_PARTICLE,
                Speck.WOOL,
                Speck.ROCK,
                Speck.BUBBLE,
                Speck.COIN,

                Speck.TOXIC,
                Speck.CORROSION,
                Speck.PARALYSIS,
                Speck.STENCH,
                Speck.CONFUSION,
                Speck.SMOKE,
                Speck.STORM,
                Speck.INFERNO,
                Speck.BLIZZARD,
                Speck.STEAM,
                Speck.DUST,
                Speck.JET,
        };
    }

    public static class WndNewParticle extends NewCompWindow<CustomParticle.ParticleProperty> {

        public WndNewParticle() {
            super(new CustomParticle.ParticleProperty());
        }

        @Override
        protected Image getIcon() {
            return obj.getSprite();
        }

        @Override
        protected void create(String name) {
            if (name != null) {
                for (CustomParticle.ParticleProperty particle : Dungeon.customDungeon.particles.values()) {
                    if (name.equals(particle.name)) {
                        EditorUtilies.showDuplicateNameWarning();
                        return;
                    }
                }
                CustomParticle.ParticleProperty particle = CustomParticle.createNewParticle(obj);
                particle.name = name;
                Tiles.particleBag.items.add(new ParticleItem(particle));
                WndEditorInv.updateCurrentTab();
            }
            super.create(name);
        }

        @Override
        protected DefaultEditComp<CustomParticle.ParticleProperty> createEditComp() {
            return new EditParticleComp(obj) {
                @Override
                protected void updateObj() {
                    WndNewParticle.this.title.icon(obj.getSprite());
                    super.updateObj();
                }
            };
        }
    }
}