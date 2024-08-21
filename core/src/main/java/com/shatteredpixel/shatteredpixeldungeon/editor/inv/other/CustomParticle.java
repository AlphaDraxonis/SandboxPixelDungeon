package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.TileSprite;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.BlobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.*;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class CustomParticle extends Blob {

    public static final int WIND_PARTICLE = 1001;
    public static final int FLOW_PARTICLE = 1002;
    public static final int WATER_SPLASH_PARTICLE = 1003;
    public static final int FLAMES_PARTICLE = 1004;
    public static final int ETERNAL_FLAMES_PARTICLE = 1005;
    public static final int LIGHT_HALO = 2001;//must be >2000
    public static final int FLARE = 2002;//must be >2000


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

    public boolean alwaysEmitting() {
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

    @Override
    public void seed(Level level, int cell, int amount ) {
        if (cur == null || cur[cell] == 0) {
            super.seed(level, cell, amount);
        }
    }

    public static class ParticleProperty implements Bundlable, Copyable<ParticleProperty> {

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

        @Override
        public ParticleProperty getCopy() {
            Bundle bundle = new Bundle();
            bundle.put("PARTICLE", this);
            return (ParticleProperty) bundle.get("PARTICLE");
        }

        public Image getSprite() {
            if (type > 1000) {
                if (type == WATER_SPLASH_PARTICLE) return new TileSprite(Assets.Environment.WATER_SEWERS, Terrain.WATER);
                if (type == FLAMES_PARTICLE) return BlobItem.createIcon(PermaGas.PFire.class);
                if (type == ETERNAL_FLAMES_PARTICLE) return BlobItem.createIcon(MagicalFireRoom.EternalFire.class);
                //TODO flare and halo
                return new ItemSprite();
            }
            Speck icon = new Speck();
            icon.image(type);
            if (type == Speck.DISCOVER) icon.resetColor();
            icon.scale.set(1.5f);
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
                    case FLAMES_PARTICLE: return FlameParticle.FACTORY;
                    case ETERNAL_FLAMES_PARTICLE: return ElmoParticle.FACTORY;
                    default: return new GizmoFactory();
                }
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
    public static final int CELL_WILL_GO_INACTIVE = 1;
    public static final int CELL_ACTIVE = 2;
    public static final int HERO_JUST_ENTERED = 3;
    public static final int CHAR_JUST_ENTERED = 4;

    public static class ParticleEmitter extends BlobEmitter {

        private static final int MAX_NUMBER_OF_PARTICLES_PER_SECOND = 8000;
        private static final float AVERAGE_LIFESPAN = 1.5f;
        private static final float MAX_NUMBER_OF_PARTICLES_PER_SECOND__TIMES__AVERAGE_LIFESPAN
                = MAX_NUMBER_OF_PARTICLES_PER_SECOND * AVERAGE_LIFESPAN;

        private CellEmitter[] emitters;
        private Visual[] visualsOnTop;

        private CustomParticle particle;

        private float originalInterval;

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
            originalInterval = interval;
            super.start(factory, interval, quantity);
        }

        protected void updateCell(int cell) {
            if (cell < Dungeon.level.heroFOV.length
                    && (Dungeon.level.heroFOV[cell] || particle.alwaysVisible || CustomDungeon.isEditing())
                    && particle.cur[cell] > (particle.alwaysEmitting() || CustomDungeon.isEditing() ? CELL_INACTIVE : CELL_ACTIVE)) {

                boolean heroJustEnteredCell = particle.cur[cell] == HERO_JUST_ENTERED;
                boolean charJustEnteredCell = particle.cur[cell] >= HERO_JUST_ENTERED;
                int valueOnEmitterKill = particle.removeOnEnter() ? particle.cur[cell] - HERO_JUST_ENTERED - 1 : CELL_ACTIVE;

                if (heroJustEnteredCell) {
                    int setValue = particle.removeOnEnter() ? CELL_WILL_GO_INACTIVE : CELL_ACTIVE;
                    particle.volume += setValue - particle.cur[cell];
                    particle.cur[cell] = setValue;
                }

                if (particle.properties.type > 2000) {
                    Visual visual;
                    boolean newVisual = false;
                    if (visualsOnTop[cell] == null || !visualsOnTop[cell].alive) {
                        if (particle.properties.type == LIGHT_HALO) visual = visualsOnTop[cell] = new ParticleHalo();
                        else if (particle.properties.type == FLARE) visual = visualsOnTop[cell] = new ParticleFlare(Window.TITLE_COLOR);//tzz user set color!
                        else return;
                        newVisual = true;
                    } else {
                        if (charJustEnteredCell) {
                            visual = visualsOnTop[cell];
                        }
                        else return;
                    }
                    if (visual instanceof VisualAsParticle) {
                        VisualAsParticle f = (VisualAsParticle) visual;
                        f.setPos(cell);
                        f.setParticle(particle);
                        if (particle.cur[cell] == CELL_WILL_GO_INACTIVE) {
                            f.setValueOnKill(valueOnEmitterKill);
                            if (particle.alwaysEmitting()) f.sohw(this, cell, 0.2f);
                            else f.sohw(this, cell, quantity == 0 ? 1.6f : quantity);
                        } else {
                            if (!particle.alwaysEmitting() || newVisual) {
                                f.setValueOnKill(CELL_ACTIVE);
                                f.sohw(this, cell, charJustEnteredCell && quantity == 0 ? 1.6f : quantity);
                            }
                        }
                    }
                    return;
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
                    if (charJustEnteredCell) {
                        emitter = emitters[cell];
                    }
                    else return;
                }
                if (particle.cur[cell] == CELL_WILL_GO_INACTIVE) {
                    emitter.valueOnKill = valueOnEmitterKill;
                    if (particle.alwaysEmitting()) emitter.start(factory, 0.01f, 1);
                    else emitter.start(factory, interval, quantity == 0 ? Math.max(1, (int) (1.2f / interval)) : quantity);
                } else {
                    if (!particle.alwaysEmitting() || !emitter.on) {
                        emitter.valueOnKill = CELL_ACTIVE;
                        emitter.start(factory, interval, charJustEnteredCell && quantity == 0 ? Math.max(1, (int) (1.2f / interval)) : quantity);
                    }
                }
            }
        }

        @Override
        protected void emit(int index) {
            if (particle == null) {
                return;
            }

            if (visualsOnTop != null) {
                for (int i = 0; i < visualsOnTop.length; i++) {
                    if (visualsOnTop[i] != null && particle.cur[i] == 0) {
                        visualsOnTop[i].remove();
                        visualsOnTop[i].destroy();
                        visualsOnTop[i] = null;
                    }
                }
            }

            if (particle.volume <= 0) {
                return;
            }

            if (particle.area.isEmpty())
                particle.setupArea();

            if (emitters == null || emitters.length != Dungeon.level.length()) {
                emitters = new CellEmitter[Dungeon.level.length()];
            }

            if (visualsOnTop == null) {
                visualsOnTop = new Visual[Dungeon.level.length()];
            }
            else if (visualsOnTop.length != Dungeon.level.length()) {
                for (int i = 0; i < visualsOnTop.length; i++) {
                    visualsOnTop[i].destroy();
                }
                visualsOnTop = new Visual[Dungeon.level.length()];
            }

            int cellsWithEmitter = particle.volume / CELL_ACTIVE;//not perfect, but good enough since 99% of emitting cells are CELL_ACTIVE
            if (cellsWithEmitter > 0)
                interval = Math.max( cellsWithEmitter / MAX_NUMBER_OF_PARTICLES_PER_SECOND__TIMES__AVERAGE_LIFESPAN, originalInterval);
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
                    if (Dungeon.level.heroFOV[pos] || CustomDungeon.isEditing()) {
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
                if (particle.cur[pos] > CELL_INACTIVE) {
                    particle.volume += valueOnKill - particle.cur[pos];
                    particle.cur[pos] = valueOnKill;
                }
            }
        }

    }

    public static class GizmoEmitter extends BlobEmitter {

        private Visual[] visuals;

        private CustomParticle particle;

        public GizmoEmitter(CustomParticle particle) {
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
                    && (Dungeon.level.heroFOV[cell] || particle.alwaysVisible || CustomDungeon.isEditing())
                    && particle.cur[cell] > (particle.alwaysEmitting() || CustomDungeon.isEditing() ? CELL_INACTIVE : CELL_ACTIVE)) {

                boolean heroJustEnteredCell = particle.cur[cell] == HERO_JUST_ENTERED;
                boolean charJustEnteredCell = particle.cur[cell] >= HERO_JUST_ENTERED;
                int valueOnEmitterKill = particle.removeOnEnter() ? particle.cur[cell] - HERO_JUST_ENTERED - 1 : CELL_ACTIVE;

                if (heroJustEnteredCell) {
                    int setValue = particle.removeOnEnter() ? CELL_WILL_GO_INACTIVE : CELL_ACTIVE;
                    particle.volume += setValue - particle.cur[cell];
                    particle.cur[cell] = setValue;
                }

                if (particle.properties.type > 2000) {
                    Visual visual;
                    boolean newVisual = false;
                    if (visuals[cell] == null || !visuals[cell].alive) {
                        if (particle.properties.type == LIGHT_HALO) visual = visuals[cell] = new ParticleHalo();
                        else if (particle.properties.type == FLARE) visual = visuals[cell] = new ParticleFlare(Window.TITLE_COLOR);//tzz user set color!
                        else return;
                        newVisual = true;
                    } else {
                        if (charJustEnteredCell) {
                            visual = visuals[cell];
                        } else return;
                    }
                    if (visual instanceof VisualAsParticle) {
                        VisualAsParticle f = (VisualAsParticle) visual;
                        f.setPos(cell);
                        f.setParticle(particle);
                        if (particle.cur[cell] == CELL_WILL_GO_INACTIVE) {
                            f.setValueOnKill(valueOnEmitterKill);
                            if (particle.alwaysEmitting()) f.sohw(this, cell, 0.2f);
                            else f.sohw(this, cell, quantity == 0 ? 1.6f : quantity);
                        } else {
                            if (!particle.alwaysEmitting() || newVisual) {
                                f.setValueOnKill(CELL_ACTIVE);
                                f.sohw(this, cell, charJustEnteredCell && quantity == 0 ? 1.6f : quantity);
                            }
                        }
                    }
                    return;
                }
            }
        }

        @Override
        protected void emit(int index) {
            if (particle == null) {
                return;
            }

            if (visuals != null) {
                for (int i = 0; i < visuals.length; i++) {
                    if (visuals[i] != null && particle.cur[i] == 0) {
                        visuals[i].remove();
                        visuals[i].destroy();
                        visuals[i] = null;
                    }
                }
            }

            if (particle.volume <= 0) {
                return;
            }

            if (particle.area.isEmpty())
                particle.setupArea();

            if (visuals == null) {
                visuals = new Visual[Dungeon.level.length()];
            } else if (visuals.length != Dungeon.level.length()) {
                for (int i = 0; i < visuals.length; i++) {
                    visuals[i].destroy();
                }
                visuals = new Visual[Dungeon.level.length()];
            }

            for (int i = particle.area.left; i < particle.area.right; i++) {
                for (int j = particle.area.top; j < particle.area.bottom; j++) {
                    updateCell(i + j * Dungeon.level.width());
                }
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

    private static class GizmoFactory extends Emitter.Factory {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
        }
    }

    private static class ParticleFlare extends Flare implements VisualAsParticle {

        private int pos;
        private CustomParticle particle;
        int valueOnKill;

        public ParticleFlare(int color) {
            super(5, 16);
            angularSpeed = 90;
            color(color, true);
        }

        @Override
        public synchronized void kill() {
            super.kill();
            if (particle.cur[pos] > CELL_INACTIVE) {
                particle.volume += valueOnKill - particle.cur[pos];
                particle.cur[pos] = valueOnKill;
            }
            destroy();
        }

        @Override
        public void setPos(int pos) {
            this.pos = pos;
        }

        @Override
        public void setValueOnKill(int valueOnKill) {
            this.valueOnKill = valueOnKill;
        }

        @Override
        public void setParticle(CustomParticle particle) {
            this.particle = particle;
        }

        @Override
        public void sohw(Group group, int cell, float duration) {
            show(group, DungeonTilemap.tileCenterToWorld(cell), duration);
        }
    }

    private static class ParticleHalo extends Halo implements VisualAsParticle {

        private int pos;
        private CustomParticle particle;
        int valueOnKill;

        float duration, lifespan;

        public ParticleHalo() {
            super(12, 0xFFFFCC, 0.4f);
        }

        @Override
        public void update() {
            super.update();

            if (duration > 0) {
                if ((lifespan -= Game.elapsed) > 0) {

                    float p = 1 - lifespan / duration;	// 0 -> 1
                    p =  p < 0.25f ? p * 4 : (1 - p) * 1.333f;
                    alpha( p );

                } else {
                    killAndErase();
                }
            }
        }

        @Override
        public synchronized void kill() {
            super.kill();
            if (particle.cur[pos] > CELL_INACTIVE) {
                particle.volume += valueOnKill - particle.cur[pos];
                particle.cur[pos] = valueOnKill;
            }
            destroy();
        }

        @Override
        public void sohw( Group parent, int cell, float duration ) {
            point( DungeonTilemap.tileCenterToWorld(cell) );
            parent.add( this );

            lifespan = this.duration = duration;

            visible = true;
        }

        @Override
        public PointF point(PointF p) {
            p.x -= scale.x * RADIUS;
            p.y -= scale.y * RADIUS;
            return super.point(p);
        }

        @Override
        public void setPos(int pos) {
            this.pos = pos;
        }

        @Override
        public void setValueOnKill(int valueOnKill) {
            this.valueOnKill = valueOnKill;
        }

        @Override
        public void setParticle(CustomParticle particle) {
            this.particle = particle;
        }
    }

    public interface VisualAsParticle {
         void setPos(int pos);
         void setValueOnKill(int valueOnKill);
         void setParticle(CustomParticle particle);

         void sohw(Group group, int cell, float duration);

    }

}