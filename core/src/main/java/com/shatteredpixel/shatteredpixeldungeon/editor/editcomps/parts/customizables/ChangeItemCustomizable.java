package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables;

import com.badlogic.gdx.files.FileHandle;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ResourcePath;
import com.shatteredpixel.shatteredpixeldungeon.customobjects.ui.WndSelectResourceFile;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;

import java.util.Map;

public class ChangeItemCustomizable extends ChangeCustomizable<Item> {

    private ItemSelector customSprite;

    public ChangeItemCustomizable(SimpleWindow window, EditItemComp editItemComp) {
        super(window, editItemComp);

        customSprite = new ItemSelector(Messages.get(this, "sprite", Messages.get(obj.getClass(), "name")), Item.class, obj, ItemSelector.NullTypeSelector.NOTHING) {

            @Override
            public void change() {
                DungeonScene.show(new WndSelectResourceFile(Messages.get(ChangeItemCustomizable.class, "custom_sprite_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                        Messages.get(ChangeItemCustomizable.class, "no_custom_sprite"), false){
                    @Override
                    protected boolean acceptExtension(String extension) {
                        return ResourcePath.isImage(extension);
                    }
                    
                    @Override
                    protected void onSelect(Map.Entry<String, FileHandle> path) {
                        if (path == null) {
                            obj.customImage = null;
                        } else {
                            obj.customImage = path.getKey();
                        }
                        itemSlot.item(obj);
                        editItemComp.updateObj();
                    }
                });
            }

            @Override
            protected void onItemSlotClick() {
                change();
            }
        };
        add(customSprite);
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilities.layoutCompsLinear(2, this, customSprite, name, desc) + 2;
    }
}
