package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditMobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.MobSprites;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Tiles;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.MobSpriteItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ItemSelector;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.SimpleWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.StringInputComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

public class ChangeMobNameDesc extends Component {

    protected StringInputComp name, desc, dialog;
    protected ItemSelector mobSprite;
    protected final Mob mob;

    MobSpriteItem currentSprite;

    public ChangeMobNameDesc(EditMobComp editMobComp) {
        this.mob = editMobComp.getObj();

        if (MobSpriteItem.canChangeSprite(mob)) {
            currentSprite = new MobSpriteItem(mob.spriteClass);
            mobSprite = new ItemSelector(Messages.get(this, "sprite"), MobSpriteItem.class, currentSprite, ItemSelector.NullTypeSelector.NONE) {
                {
                    selector.preferredBag = MobSprites.bag.getClass();
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

                    String msgKey = i.mob().getMessageKey();
                    String msgKeyOld = currentSprite.mob().getMessageKey();
                    if (mob.customName == null || mob.customName.equals(msgKeyOld + "name")) {
                        mob.customName = msgKey + "name";
                        name.setText(mob.name());
                    }
                    if (mob.customDesc == null || mob.customDesc.equals(msgKeyOld + "desc")) {
                        mob.customDesc = msgKey + "desc";
                        desc.setText(mob.description());
                    }

                    EditorScene.replaceMobSprite(mob, i.getObject());
                    currentSprite = i;
                    editMobComp.updateTitleIcon();
                    editMobComp.updateObj();
                }
            };
            add(mobSprite);
        }

        name = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "name_label"), null, 100, false,
                mob.customName == null ? Messages.titleCase(mob.name()) : mob.name()) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.customName = getText();
                if (mob.customName.trim().isEmpty()) mob.customName = null;
                updateLayout();
            }
        };
        name.setHighlightingEnabled(false);
        add(name);

        desc = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "desc_label"), null, 500, true, mob.customDesc == null ? mob.description() : mob.customDesc) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.customDesc = getText();
                if (mob.customDesc.trim().isEmpty()) mob.customDesc = null;
                updateLayout();
            }
        };
        add(desc);

        dialog = new StringInputComp(Messages.get(ChangeMobNameDesc.class, "dialog_label"), null, 500, true, mob.dialog) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.dialog = getText();
                if (mob.dialog.trim().isEmpty()) mob.dialog = null;
                updateLayout();
            }
        };
        add(dialog);
    }

    protected void updateLayout() {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilies.layoutCompsLinear(2, this, mobSprite, name, desc, dialog);
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangeMobNameDesc.class, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }
}