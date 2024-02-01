package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.TileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class CustomParticle extends Blob {

    public static final int WIND_PARTICLE = 1001;
    public static final int FLOW_PARTICLE = 1002;
    public static final int WATER_SPLASH_PARTICLE = 1003;


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
        if (properties != null) emitter.start(properties.createFactory(), properties.interval, properties.quantity);
    }

    public boolean removeOnEnter() {
        return properties == null || properties.removeOnEnter;
    }

    private boolean alwaysEmitting() {
        return properties == null || properties.alwaysEmitting;
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

        public boolean removeOnEnter = false;
        public boolean alwaysEmitting = true;

        private int id = 0;

        private static final String NAME = "name";
        private static final String TYPE = "type";
        private static final String INTERVAL = "interval";
        private static final String QUANTITY = "quantity";
        private static final String REMOVE_ON_ENTER = "remove_on_enter";
        private static final String ALWAYS_EMITTING = "always_emitting";
        private static final String ID = "id";


        @Override
        public void restoreFromBundle(Bundle bundle) {

            name = bundle.getString(NAME);
            id = bundle.getInt(ID);

            type = bundle.getInt(TYPE);
            interval = bundle.getFloat(INTERVAL);
            quantity = bundle.getInt(QUANTITY);
            removeOnEnter = bundle.getBoolean(REMOVE_ON_ENTER);
            alwaysEmitting = bundle.getBoolean(ALWAYS_EMITTING);
        }

        @Override
        public void storeInBundle(Bundle bundle) {

            bundle.put(NAME, name);
            bundle.put(ID, id);

            bundle.put(TYPE, type);
            bundle.put(INTERVAL, interval);
            bundle.put(QUANTITY, quantity);
            bundle.put(REMOVE_ON_ENTER, removeOnEnter);
            bundle.put(ALWAYS_EMITTING, alwaysEmitting);
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
            if (type > 1000) {
                if (type == WATER_SPLASH_PARTICLE) return new ItemSprite(Assets.Environment.WATER_SEWERS, new TileItem(Terrain.WATER, -1));
                return new ItemSprite();
            }
            Speck icon = new Speck();
            icon.image(type);
            if (type == Speck.DISCOVER) icon.resetColor();
            icon.scale.set(1.5f);//16/7=2.28
            return icon;
        }

        public void setPredefinedInterval(int type) {
            if (type == WIND_PARTICLE) {
//                interval = WindParticle.INTERVAL;
            }
        }

        public Emitter.Factory createFactory() {
            if (type > 1000) {
                switch (type) {
                    case WIND_PARTICLE: return WindParticle.FACTORY;
                    case FLOW_PARTICLE: return FlowParticle.FACTORY;
                    case WATER_SPLASH_PARTICLE: return SPLASH_FACTORY;
                }
                return Speck.factory(0);
            } else return Speck.factory(type);
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


    //this order is important!
    public static final int CELL_INACTIVE = 0;
    public static final int CELL_ACTIVE = 1;
    public static final int HERO_JUST_ENTERED = 2;
    public static final int CHAR_JUST_ENTERED = 3;

    public static class ParticleEmitter extends BlobEmitter {

        private CellEmitter[] emitters;

        private CustomParticle particle;

        public ParticleEmitter(CustomParticle particle) {
            super(particle);
            this.particle = particle;
            particle.use(this);
        }

        @Override
        public void start(Factory factory, float interval, int quantity) {
            this.factory = factory;
            this.interval = interval;
            this.quantity = quantity;
            super.start(factory, interval, quantity);
        }

        protected void updateCell(int cell) {
            if (cell < Dungeon.level.heroFOV.length
                    && (Dungeon.level.heroFOV[cell] || particle.alwaysVisible || Dungeon.hero == null)
                    && particle.cur[cell] > (particle.alwaysEmitting() || Dungeon.hero == null ? 0 : 1)) {

                boolean heroJustEnteredCell = particle.cur[cell] == HERO_JUST_ENTERED;
                boolean charJustEnteredCell = particle.cur[cell] >= HERO_JUST_ENTERED;
                int valueOnEmitterKill = particle.removeOnEnter() ? particle.cur[cell] - HERO_JUST_ENTERED : CELL_ACTIVE;

                if (heroJustEnteredCell) {
                    particle.volume += CELL_ACTIVE - particle.cur[cell];
                    particle.cur[cell] = CELL_ACTIVE;
                }

                CellEmitter emitter;
                if (emitters[cell] == null) {
                    emitter = emitters[cell] = new CellEmitter();
                    emitter.setPos(cell);
                    emitter.particle = particle;
                    add(emitter);
                } else if (!emitters[cell].alive) {
                    emitter = emitters[cell];
                    emitter.revive();
                } else {
                    if (particle.removeOnEnter() && heroJustEnteredCell) {
                        emitter = emitters[cell];
                    }
                    else return;
                }
                if (particle.removeOnEnter() && heroJustEnteredCell) {
                    emitter.valueOnKill = valueOnEmitterKill;
                    emitter.start(factory, interval, quantity == 0 ? Math.max(1, (int) (1.2f / interval)) : quantity);
                } else {
                    emitter.valueOnKill = CELL_ACTIVE;
                    emitter.start(factory, interval, charJustEnteredCell && quantity == 0 ? Math.max(1, (int) (1.2f / interval)) : quantity);
                }
            }
        }

        @Override
        protected void emit(int index) {
            if (particle == null) {
                return;
            }

            if (particle.volume <= 0) {
                return;
            }

            if (particle.area.isEmpty())
                particle.setupArea();

            if (emitters == null || emitters.length != Dungeon.level.length()) {
                emitters = new CellEmitter[Dungeon.level.length()];
            }

            for (int i = particle.area.left; i < particle.area.right; i++) {
                for (int j = particle.area.top; j < particle.area.bottom; j++) {
                    updateCell(i + j*Dungeon.level.width());
                }
            }
        }

        private static class CellEmitter extends Emitter {

            private int pos;
            private CustomParticle particle;

            public void setPos(int cell) {
                pos = cell;
                PointF p = DungeonTilemap.tileToWorld( pos );
                pos(p.x, p.y, DungeonTilemap.SIZE, DungeonTilemap.SIZE);
            }

            @Override
            protected void emit(int index) {
                if (pos < particle.cur.length && particle.cur[pos] > CELL_INACTIVE) {
                    if (Dungeon.level.heroFOV[pos] || Dungeon.hero == null) {
                        float x = Random.Float(BlobEmitter.bound.left, BlobEmitter.bound.right) * DungeonTilemap.SIZE;
                        float y = Random.Float(BlobEmitter.bound.top, BlobEmitter.bound.bottom) * DungeonTilemap.SIZE;
                        factory.emit(this, index, x + this.x, y + this.y);
                    }
                } else on = false;
            }

            int valueOnKill;
            @Override
            public synchronized void kill() {
                super.kill();
                particle.volume += valueOnKill - particle.cur[pos];
                particle.cur[pos] = valueOnKill;
            }
        }

    }

    private static final Emitter.Factory SPLASH_FACTORY = new Emitter.Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            int cell = DungeonTilemap.pointToTile(x, y);
            GameScene.ripple(cell);
        }
    };

}