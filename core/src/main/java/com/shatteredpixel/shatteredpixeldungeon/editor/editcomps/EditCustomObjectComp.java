package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.luamobs.Mob_lua;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class EditCustomObjectComp extends DefaultEditComp<CustomObject> {

    private final ItemSelector chooseType;

    public EditCustomObjectComp(CustomObject customObject) {
        super(customObject);

        chooseType = new ItemSelector("TYPEtzz", Item.class, null, ItemSelector.NullTypeSelector.DISABLED) {
            {
                selector.preferredBag = Mobs.bag.getClass();
            }
            @Override
            public void change() {
                EditorScene.selectItem(selector);
            }

            @Override
            public void setSelectedItem(Item selectedItem) {
                super.setSelectedItem(selectedItem);
                if (selectedItem instanceof MobItem) {
                    Mob mob = ((MobItem) selectedItem).getObject();
                    Class<?> clazz = Reflection.forName(mob.getClass().getName() + "_lua");//TODO tzz does this actually work?
                    if (clazz == null) clazz = Mob_lua.class;//tzz remove
                    obj.luaClass = clazz == null || !LuaClass.class.isAssignableFrom(clazz) ? null : (LuaClass) Reflection.newInstance(clazz);
                }
                updateObj();
            }
        };
        add(chooseType);

        updateObj();
    }

    @Override
    protected void layout() {
        super.layout();
        layoutCompsLinear(chooseType);
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

    public static class WndNewCustomObject extends NewCompWindow<CustomObject> {

        public WndNewCustomObject() {
            super(new CustomObject());
        }

        @Override
        protected Image getIcon() {
            return obj.getSprite();
        }

        @Override
        protected void create(String name) {
            if (name != null) {
                if (obj.luaClass == null) return;
                obj.name = name;
                CustomObject.assignNewID(obj);
                Mobs.updateCustomMobsInInv();//tzz more flexible!
                WndEditorInv.updateCurrentTab();
            }
            super.create(name);
        }

        @Override
        protected DefaultEditComp<CustomObject> createEditComp() {
            return new EditCustomObjectComp(obj) {
                @Override
                protected void updateObj() {
                    WndNewCustomObject.this.title.icon(obj.getSprite());
                    super.updateObj();
                }
            };
        }
    }
}