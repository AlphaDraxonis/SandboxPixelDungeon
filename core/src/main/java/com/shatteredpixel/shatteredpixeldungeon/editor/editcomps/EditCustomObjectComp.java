package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Mobs;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.CustomObject;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaClass;
import com.shatteredpixel.shatteredpixeldungeon.editor.lua.LuaMob;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Reflection;

public class EditCustomObjectComp extends DefaultEditComp<CustomObject> {

    private final ItemSelector chooseType;

    public EditCustomObjectComp(CustomObject customObject) {
        super(customObject);

        chooseType = new ItemSelector(Messages.get(this, "type"), Item.class, null, ItemSelector.NullTypeSelector.DISABLED) {
            {
                selector = new AnyItemSelectorWnd(Item.class, false) {
                    @Override
                    public void onSelect(Item item) {
                        if (item == null) return;//if window is canceled
                        if (item instanceof EditorItem.NullItemClass) setSelectedItem(null);
                        else
                            setSelectedItem(item instanceof ItemItem ? ((ItemItem) item).item().getCopy() : item.getCopy());
                    }

                    @Override
                    public boolean acceptsNull() {
                        return false;
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        if (super.itemSelectable(item)) {
                            Object obj = item;
                            if (obj instanceof ItemItem) obj = ((ItemItem) obj).getObject();
                            else if (obj instanceof EditorItem) {
                                Object o = ((EditorItem<?>) obj).getObject();
                                return !(o instanceof LuaClass);
                            }
                            return true;
                        }
                        return false;
                    }
                };
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
                    Class<?> clazz = Reflection.forName(LuaMob.getLuaMobClassName(mob.getClass()));

                    obj.luaClass = clazz == null || !LuaClass.class.isAssignableFrom(clazz) ? null : (LuaClass) Reflection.newInstance(clazz);

                    if (obj.luaClass instanceof Mob)
                        ((Mob) obj.luaClass).pos = -1;
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