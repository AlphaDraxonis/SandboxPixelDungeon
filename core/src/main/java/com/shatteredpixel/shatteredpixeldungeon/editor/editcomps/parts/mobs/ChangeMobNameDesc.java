package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.DefaultStatsCache;
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
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.List;

public class ChangeMobNameDesc extends Component {

    protected StringInputComp name, desc;
    protected List<StringInputComp> dialogs = new ArrayList<>(5);
    protected RedButton addDialog;
    protected ItemSelector mobSprite;
    protected final Mob mob;

    MobSpriteItem currentSprite;

    public ChangeMobNameDesc(EditMobComp editMobComp) {
        this.mob = editMobComp.getObj();

        if (MobSpriteItem.canChangeSprite(mob)) {
            currentSprite = new MobSpriteItem(mob.spriteClass);
            Mob defaultMob = DefaultStatsCache.getDefaultObject(mob.getClass());
            mobSprite = new ItemSelector(Messages.get(this, "sprite", defaultMob == null ? mob.getClass().getSimpleName() : defaultMob.name()),
                    MobSpriteItem.class, currentSprite, ItemSelector.NullTypeSelector.DISABLED) {
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

                    if (mob.customName == null
                            || mob.customName.equals(Messages.getFullMessageKey(currentSprite.mob().getClass(), "name"))) {
                        name.setText(mob.customName = Messages.getFullMessageKey(i.mob().getClass(), "name"));
                    }
                    if (mob.customDesc == null
                            || mob.customDesc.equals(Messages.getFullMessageKey(currentSprite.mob().getClass(), "desc"))) {
                        desc.setText(mob.customDesc = Messages.getFullMessageKey(i.mob().getClass(), "desc"));
                    }

                    EditorScene.replaceMobSprite(mob, i.getObject());
                    currentSprite = i;
                    editMobComp.updateTitleIcon();
                    editMobComp.updateObj();

                    updateLayout();
                }
            };
            add(mobSprite);
        }

        name = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "name_label"), null, 100, false,
                mob.customName == null ? Messages.getFullMessageKey(mob.getClass(), "name") : mob.customName) {
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

        desc = new StringInputComp(Messages.get(Tiles.WndCreateCustomTile.class, "desc_label"), null, 500, true,
                mob.customDesc == null ? Messages.getFullMessageKey(mob.getClass(), "desc") : mob.customDesc) {
            @Override
            protected void onChange() {
                super.onChange();
                mob.customDesc = getText();
                if (mob.customDesc.trim().isEmpty()) mob.customDesc = null;
                updateLayout();
            }
        };
        add(desc);

        addDialog = new RedButton(Messages.get(ChangeMobNameDesc.class, "add_dialog")) {
            @Override
            protected void onClick() {
                mob.dialogs.add(null);
                addDialogField(mob.dialogs.size() - 1);
                updateLayout();
            }
        };
        add(addDialog);

        if (mob.dialogs.isEmpty()) mob.dialogs.add(null);
        addDialogField(0);
        for (int i = 1; i < mob.dialogs.size(); i++) {
            addDialogField(i);
        }
    }

    protected void updateLayout() {
        Window w = EditorUtilies.getParentWindow(this);
        if (w instanceof SimpleWindow) ((SimpleWindow) w).layout();
    }

    @Override
    protected void layout() {
        height = 0;
        height = EditorUtilies.layoutCompsLinear(2, this, mobSprite, name, desc) + 2;
        height = EditorUtilies.layoutCompsLinear(2, this, dialogs.toArray(new StringInputComp[0]));

        addDialog.setRect(x + width / 5, height + 4,width * 3 / 5, ItemSpriteSheet.SIZE);
        height = addDialog.bottom() + 1;
    }

    public static Component createTitle() {
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(ChangeMobNameDesc.class, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    @Override
    public synchronized void destroy() {
        super.destroy();
        for (int i = mob.dialogs.size() - 1; i >= 0; i--) {
            String dialog = mob.dialogs.get(i);
            if (dialog == null || dialog.trim().isEmpty()) mob.dialogs.remove(i);
        }
    }

    private void addDialogField(int index) {
        StringInputComp dialog = new StringInputComp(Messages.get(ChangeMobNameDesc.class, "dialog_label") + " " + (index + 1) + ":", null, 500, true,
                mob.dialogs.size() <= index || mob.dialogs.get(index) == null ? "" : mob.dialogs.get(index)) {
            @Override
            protected void onChange() {
                super.onChange();
                String txt = getText();
                if (txt.trim().isEmpty()) txt = null;
                mob.dialogs.set(index, txt);
                updateLayout();
            }
        };
        add(dialog);
        dialogs.add(dialog);
    }
}