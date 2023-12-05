package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public abstract class SpawnerMob extends Mob {

    //Inheritors only need to initialise these 2 variables correctly.
    //When spawning mobs, it is important that summonTemplate.getCopy() is called!

    protected Class<? extends Mob> defaultTemplateClass;
    public Mob summonTemplate;


    @Override
    public String description() {
        if (customDesc != null) return customDesc;
        String desc = super.description();
        if (summonTemplate == null)
            desc += "\n\n" + Messages.get(this, "summon_none", name());
        else if (summonTemplate.getClass() != defaultTemplateClass)
            desc += "\n\n" + Messages.get(this, "summon", name(), summonTemplate.name());
        return desc;
    }


    private static final String SUMMONING_TEMPLATE = "summoning_template";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( SUMMONING_TEMPLATE, summonTemplate );
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summonTemplate = (Mob) bundle.get(SUMMONING_TEMPLATE);
    }
}