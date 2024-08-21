package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.GameObject;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Function;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SpawnerMob extends Mob {

    //Inheritors only need to initialize these 2 variables correctly.
    //When spawning mobs, it is important that summonTemplate.getCopy() is called!

    protected Class<? extends Mob> defaultTemplateClass;
    public List<Mob> summonTemplate = new ArrayList<>();

    private Set<Mob> summonRotation;


    @Override
    public String desc() {
        if (customDesc != null) return super.desc();
        String desc = super.desc();
        int size = summonTemplate.size();
        if (size == 0)
            desc += "\n\n" + Messages.get(this, "summon_none", name());
        else if (size > 1 && summonTemplate.get(0).getClass() != defaultTemplateClass) {
            desc += "\n\n" + Messages.get(this, "summon", name());
            size--;
            for (int i = 0; i < size; i++) {
                desc += " _" + summonTemplate.get(i).name() + "_,";
            }
            desc += " _" + summonTemplate.get(size).name() + "_";
        }
        return desc;
    }

    protected Mob createSummonedMob() {
        if (summonRotation == null) summonRotation = new HashSet<>(5);
        if (summonRotation.isEmpty()) summonRotation.addAll(summonTemplate);
        Mob m = Random.element(summonRotation);
        summonRotation.remove(m);
        return (Mob) m.getCopy();
    }


    @Override
    public boolean doOnAllGameObjects(Function<GameObject, ModifyResult> whatToDo) {
        return super.doOnAllGameObjects(whatToDo)
                | doOnAllGameObjectsList(summonTemplate, whatToDo);
    }

    private static final String SUMMONING_TEMPLATE = "summoning_templates";
    private static final String SUMMON_ROTATION = "summon_rotation";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SUMMONING_TEMPLATE, summonTemplate);
        if (summonRotation != null) bundle.put(SUMMON_ROTATION, summonRotation);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summonTemplate.clear();
        if (bundle.contains("summoning_template")) summonTemplate.add((Mob) bundle.get("summoning_template"));
        else {
            for (Bundlable b : bundle.getCollection(SUMMONING_TEMPLATE))
                summonTemplate.add((Mob) b);
        }
        if (bundle.contains(SUMMON_ROTATION)) {
            summonRotation = new HashSet<>(5);
            for (Bundlable b : bundle.getCollection(SUMMON_ROTATION))
                summonRotation.add((Mob) b);
        }
    }
}