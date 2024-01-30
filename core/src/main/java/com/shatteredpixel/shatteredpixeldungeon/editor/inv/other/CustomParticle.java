package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.RectF;

public class CustomParticle extends Blob {

    public int particleID;
    private ParticleProperty properties;

    public CustomParticle() {
    }

    public CustomParticle(int particleID) {
        this.particleID = particleID;
        properties = Dungeon.customDungeon.particles.get(particleID);
    }

    @Override
    protected void evolve() {
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        if (properties != null) emitter.start(Speck.factory(properties.type), properties.interval, properties.quantity);
    }

    public boolean removeOnEnter() {
        return properties == null || properties.removeOnEnter;
    }

    private static final String PARTICLE_ID = "particle_id";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTICLE_ID, particleID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        particleID = bundle.getInt(PARTICLE_ID);
        properties = Dungeon.customDungeon.particles.get(particleID);
    }

    public static class ParticleProperty implements Bundlable {

        public String name;

        public int type = Speck.HEALING;
        public float interval = 0.5f;
        public int quantity = 0;

        public boolean removeOnEnter = true;

        private int id = 0;

        private static final String NAME = "name";
        private static final String TYPE = "type";
        private static final String INTERVAL = "interval";
        private static final String QUANTITY = "quantity";
        private static final String REMOVE_ON_ENTER = "remove_on_enter";
        private static final String ID = "id";


        @Override
        public void restoreFromBundle(Bundle bundle) {

            name = bundle.getString(NAME);
            id = bundle.getInt(ID);

            type = bundle.getInt(TYPE);
            interval = bundle.getFloat(INTERVAL);
            quantity = bundle.getInt(QUANTITY);
            removeOnEnter = bundle.getBoolean(REMOVE_ON_ENTER);
        }

        @Override
        public void storeInBundle(Bundle bundle) {

            bundle.put(NAME, name);
            bundle.put(ID, id);

            bundle.put(TYPE, type);
            bundle.put(INTERVAL, interval);
            bundle.put(QUANTITY, quantity);
            bundle.put(REMOVE_ON_ENTER, removeOnEnter);
        }

        public int particleID() {
            return id;
        }

        private ParticleProperty getCopy() {
            Bundle bundle = new Bundle();
            bundle.put("PARTICLE", this);
            return (ParticleProperty) bundle.get("PARTICLE");
        }

        public Image getSprite() {
            RectF r = Speck.getFilm().get(type);
            if (r == null) return new ItemSprite();
            Image icon = new Image(Assets.Effects.SPECKS);
            icon.frame(r);
            icon.scale.set(1.5f);//16/7=2.28
            return icon;
        }

    }

    public static ParticleProperty createNewParticle(ParticleProperty template) {
        ParticleProperty result = template.getCopy();
        result.id = Dungeon.customDungeon.nextParticleID;
        Dungeon.customDungeon.particles.put(result.particleID(), result);
        Dungeon.customDungeon.updateNextParticleID();
        return result;
    }

    public static ParticleProperty deleteParticle(int particleID) {

        ParticleProperty del = Dungeon.customDungeon.particles.get(particleID);

        Undo.startAction();

        CustomParticle particleOnLevel = Dungeon.level.particles.get(particleID);

        ActionPartList actionPart = new ActionPartList() {
            @Override
            public void undo() {
                Dungeon.customDungeon.particles.put(particleID, del);
                Dungeon.level.particles.put(particleID, particleOnLevel);
                Tiles.updateParticlesInInv();
                super.undo();
            }

            @Override
            public void redo() {
                Dungeon.customDungeon.particles.remove(particleID);
                Tiles.updateParticlesInInv();
                super.redo();
                Dungeon.level.particles.remove(particleID);
            }

            @Override
            public boolean hasContent() {
                return true;
            }
        };

        if (particleOnLevel != null && particleOnLevel.cur != null) {
            int length = Dungeon.level.length();
            for (int cell = 0; cell < length; cell++) {
                ParticleActionPart.Modify modify = new ParticleActionPart.Modify(cell);
                particleOnLevel.volume -= particleOnLevel.cur[cell];
                particleOnLevel.cur[cell] = 0;
                modify.finish();
                actionPart.addActionPart(modify);
            }
        }

        Undo.addActionPart(actionPart);

        Undo.endAction();

        actionPart.redo();

        return del;
    }


//    public void updateEmitterAtCell(int cell) {
//        ParticleEmitter emitter = (ParticleEmitter) this.emitter;
//        emitter.updateCell(cell, true);
//    }
//
//    public static class ParticleEmitter extends BlobEmitter {
//
//        private EmitterAtMultipleCells[] uniqueEmitters = new EmitterAtMultipleCells[50];
//
////        private int[] cellsWithEmitter;
//
//        private CustomParticle particle;
//
//        public ParticleEmitter(CustomParticle particle) {
//            super(particle);
//            this.particle = particle;
//            particle.use(this);
//        }
//
//        @Override
//        public void start(Factory factory, float interval, int quantity) {
//            this.factory = factory;
//            this.interval = interval;
//            this.quantity = quantity;
//            super.start(factory, interval, quantity);
//        }
//
//        protected void updateCell(int cell, boolean isEditing) {
//            if (cell < Dungeon.level.heroFOV.length
//                    && (Dungeon.level.heroFOV[cell] || particle.alwaysVisible || isEditing)
//                    && particle.cur[cell] > 0) {
//                int useEmitter = Random.Int(uniqueEmitters.length);
//                EmitterAtMultipleCells emitter;
//                if (uniqueEmitters[useEmitter] == null) {
//                    emitter = uniqueEmitters[useEmitter] = new EmitterAtMultipleCells();
//                    emitter.particle = particle;
//                    emitter.start(factory, interval, quantity);
//                    add(emitter);
//                } else emitter = uniqueEmitters[useEmitter];
//                if (emitter.nextFreeIndex == emitter.cells.length) {
//                    emitter.increaseSize();
//                }
//                emitter.cells[emitter.nextFreeIndex] = cell;
//                emitter.updateNextFreeIndex();
//            }
//        }
//
//        @Override
//        protected void emit(int index) {
//            if (particle == null) {
//                return;
//            }
//
//            if (particle.volume <= 0) {
//                return;
//            }
//
//            if (particle.area.isEmpty())
//                particle.setupArea();
//
////            if (cellsWithEmitter == null || cellsWithEmitter.length != Dungeon.level.length()) {
////                cellsWithEmitter = new int[Dungeon.level.length()];
////                for (EmitterAtMultipleCells emitter : uniqueEmitters) {
////                    if (emitter != null) Arrays.fill(emitter.cells, -1);
////                }
////            }
//
//            boolean isEditing = CustomDungeon.isEditing();
//
//            for (int i = particle.area.left; i < particle.area.right; i++) {
//                for (int j = particle.area.top; j < particle.area.bottom; j++) {
//                    updateCell(i + j*Dungeon.level.width(), isEditing);
//                }
//            }
//        }
//
//        private static class EmitterAtMultipleCells extends Emitter {
//            int[] cells = new int[50];
//            int nextFreeIndex = 0;
//            {
//                Arrays.fill(cells, -1);
//            }
//            CustomParticle particle;
//
//            @Override
//            protected void emit(int index) {
//                boolean isEditing = CustomDungeon.isEditing();
//                int w = Dungeon.level.width();
//                for (int i = 0; i < cells.length; i++) {
//                    int cell = cells[i];
//                    if (cell > -1) {
//                        if (cell >= particle.cur.length || particle.cur[cell] == 0) {
//                            cells[i] = -1;
//                            nextFreeIndex = i;
//                        }
//                        else {
//                            if (Dungeon.level.heroFOV[cell] || isEditing) {
//                                float x = (cell%w + Random.Float(BlobEmitter.bound.left, BlobEmitter.bound.right)) * DungeonTilemap.SIZE;
//                                float y = (cell/w + Random.Float(BlobEmitter.bound.top, BlobEmitter.bound.bottom)) * DungeonTilemap.SIZE;
//                                factory.emit(this, index, x, y);
//                            }
//                        }
//                    }
//                }
//            }
//
//            private void updateNextFreeIndex() {
//                if (nextFreeIndex != cells.length) {
//                    while (cells[nextFreeIndex] > -1 && ++nextFreeIndex < cells.length);
//                }
//            }
//
//            private void increaseSize() {
//                int[] newCells = new int[cells.length + 50];
//                System.arraycopy(cells, 0, newCells, 0, cells.length);
//                cells = newCells;
//            }
//        }
//
//    }

}