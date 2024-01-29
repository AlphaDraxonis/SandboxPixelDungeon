package com.shatteredpixel.shatteredpixeldungeon.editor.inv.other;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPartList;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.ParticleActionPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

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
                super.undo();
            }

            @Override
            public void redo() {
                Dungeon.customDungeon.particles.remove(particleID);
                Dungeon.level.particles.remove(particleID);
                super.redo();
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

    //TODO rename particles
    //TODO tzz show particles in editor tile desc
    //add editor in editcomp to edit the property values
    //add button to create/delete particles, and TEST deletion with undo interference!!
    //TODO tzz test what happens when custom tiles are deleted with undo (this is not related to particles) -> fix bug
    //don't forget storing it correctly in toolbar


}