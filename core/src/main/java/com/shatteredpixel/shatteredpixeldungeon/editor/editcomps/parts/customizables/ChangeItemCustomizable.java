package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables;

import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.CustomDungeonSaves;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Image;

import java.util.List;

public class ChangeItemCustomizable extends ChangeCustomizable<Item> {

    private ItemSelector customSprite;

    public ChangeItemCustomizable(EditItemComp editItemComp) {
        super(editItemComp);

        customSprite = new ItemSelector(Messages.get(this, "sprite", Messages.get(obj.getClass(), "name")), Item.class, obj, ItemSelector.NullTypeSelector.NOTHING) {

            @Override
            public void change() {
                List<String> imgFiles = CustomDungeonSaves.findAllFilePaths("png");

				String[] options = new String[imgFiles.size() + 1];
                options[0] = Messages.get(ChangeItemCustomizable.class, "no_custom_sprite");
                int i = 1;
                for (String m : imgFiles) {
                    options[i++] = m;
                }
                EditorScene.show(new WndOptions(
                        Messages.get(ChangeItemCustomizable.class, "custom_sprite"),
                        Messages.get(ChangeItemCustomizable.class, "custom_sprite_info", CustomDungeonSaves.getAdditionalFilesDir().file().getAbsolutePath()),
                        options
                ) {
                    {
                        tfMessage.setHighlighting(false);
                    }

                    @Override
                    protected Image getIcon(int index) {
                        if (index == 0) {
                            return new ItemSprite(obj.image());
                        }
                        Image img = new Image(TextureCache.getFromCurrentSavePath(CustomDungeonSaves.getExternalFilePath(options[index])));
                        img.scale.set(ItemSpriteSheet.SIZE / Math.max(img.width, img.height));
                        return img;
                    }

                    @Override
                    protected void onSelect(int index) {
                        super.onSelect(index);
                        if (index == 0) {
                            obj.customImage = null;
                        } else {
                            obj.customImage = options[index];
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
        height = EditorUtilies.layoutCompsLinear(2, this, customSprite, name, desc) + 2;
    }
}