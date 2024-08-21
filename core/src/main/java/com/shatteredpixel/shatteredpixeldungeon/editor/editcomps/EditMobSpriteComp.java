package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class EditMobSpriteComp extends DefaultEditComp<Class<? extends CharSprite>> {

    private Mob mob;

    public EditMobSpriteComp(Class<? extends CharSprite> sprite, Mob mob) {
        super(sprite);
        this.mob = mob;
        updateObj();
    }

    public EditMobSpriteComp(MobSpriteItem item) {
        this(item.getObject(), item.mob());
    }

    @Override
    protected String createTitleText() {
        if (mob == null) return "";
        return Messages.titleCase(mob.name());
    }

    @Override
    protected String createDescription() {
        if (mob == null) return "";
        return mob.desc();
    }

    @Override
    public Image getIcon() {
        return Reflection.newInstance(obj);
    }
}