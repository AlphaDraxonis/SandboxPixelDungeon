package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.customizables;

import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.MobSprites;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilities;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

import java.util.ArrayList;
import java.util.List;

public class ChangeMobCustomizable extends ChangeCustomizable<Mob> {
    protected List<StringInputComp> dialogs = new ArrayList<>(5);
    protected RedButton addDialog;
    protected ItemSelector mobSprite;

    MobSpriteItem currentSprite;

    public ChangeMobCustomizable(SimpleWindow window, EditMobComp editMobComp) {
        super(window, editMobComp);

        if (MobSpriteItem.canChangeSprite(obj)) {
            currentSprite = new MobSpriteItem(obj.spriteClass);
            Mob defaultMob = DefaultStatsCache.getDefaultObject(obj.getClass());
            mobSprite = new ItemSelector(Messages.get(this, "sprite", defaultMob == null ? obj.getClass().getSimpleName() : defaultMob.name()),
                    MobSpriteItem.class, currentSprite, ItemSelector.NullTypeSelector.DISABLED) {
                {
                    selector.preferredBag = MobSprites.bag().getClass();
                }
                @Override
                public void change() {
                    EditorScene.selectItem(selector);
                }

                @Override
                public void setSelectedItem(Item selectedItem) {
                    super.setSelectedItem(selectedItem);

                    if (selectedItem == currentSprite) return;

                    MobSpriteItem i = (MobSpriteItem) selectedItem;

                    if (obj.getCustomName() == null
                            || obj.getCustomName().equals(Messages.getFullMessageKey(currentSprite.mob().getClass(), "name"))) {
                        obj.setCustomName(Messages.getFullMessageKey(i.mob().getClass(), "name"));
                        name.setText(obj.getCustomName());
                    }
                    if (obj.getCustomDesc() == null
                            || obj.getCustomDesc().equals(Messages.getFullMessageKey(currentSprite.mob().getClass(), "desc"))) {
                        obj.setCustomDesc(Messages.getFullMessageKey(i.mob().getClass(), "desc"));
                        desc.setText(obj.getCustomDesc());
                    }

                    if (obj.pos != -1) {
                        EditorScene.replaceMobSprite(obj, i.getObject());
                    } else {
                        obj.spriteClass = i.getObject();
                    }
                    currentSprite = i;
                    editMobComp.updateTitleIcon();
                    editMobComp.updateObj();

                    updateLayout();
                }
            };
            add(mobSprite);
        }

        addDialog = new RedButton(Messages.get(ChangeMobCustomizable.class, "add_dialog")) {
            @Override
            protected void onClick() {
                obj.dialogs.add(null);
                addDialogField(obj.dialogs.size() - 1);
                updateLayout();
            }
        };
        add(addDialog);

        if (obj.dialogs.isEmpty()) obj.dialogs.add(null);
        addDialogField(0);
        for (int i = 1; i < obj.dialogs.size(); i++) {
            addDialogField(i);
        }
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilities.layoutCompsLinear(2, this, mobSprite, name, desc) + 2;
        height = EditorUtilities.layoutCompsLinear(2, this, dialogs.toArray(new StringInputComp[0]));

        addDialog.setRect(x + width / 5, height + 4,width * 3 / 5, ItemSpriteSheet.SIZE);
        height = addDialog.bottom() + 1;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        for (int i = obj.dialogs.size() - 1; i >= 0; i--) {
            String dialog = obj.dialogs.get(i);
            if (dialog == null || dialog.trim().isEmpty()) obj.dialogs.remove(i);
        }
    }

    private void addDialogField(int index) {
        StringInputComp dialog = new StringInputComp(Messages.get(ChangeMobCustomizable.class, "dialog_label") + " " + (index + 1) + ":", null, 500, true,
                obj.dialogs.size() <= index || obj.dialogs.get(index) == null ? "" : obj.dialogs.get(index)) {
            @Override
            protected void onChange() {
                super.onChange();
                String txt = getText();
                if (txt.trim().isEmpty()) txt = null;
                obj.dialogs.set(index, txt);
                updateLayout();
            }
        };
        add(dialog);
        dialogs.add(dialog);
    }
}
