package com.shatteredpixel.shatteredpixeldungeon.editor.inv;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.overview.dungeon.WndNewDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.AdvancedListPaneItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.EditorUtilies;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class DefaultListItem extends AdvancedListPaneItem {

    protected final Item item;
    protected EditorInventoryWindow window;

    protected IconButton editButton;

    public DefaultListItem(Item item, EditorInventoryWindow window, String title, Image image) {
        super(image,
                (item instanceof EditorItem) ? ((EditorItem<?>) item).getSubIcon() : EditorUtilies.createSubIcon(item),
                Messages.titleCase(title));
        this.item = item;
        this.window = window;
        label.setHighlighting(false);

        if (item instanceof EditorItem) {
            editButton = new IconButton(Icons.EDIT.get()) {
                @Override
                protected void onClick() {
                    if (WndEditorInv.chooseClass) {
                        Class<?> clazz = ((EditorItem<?>) item).getObject().getClass();
                        while (clazz.getEnclosingClass() != null)
                            clazz = clazz.getEnclosingClass();
                        String className = clazz.getName();
                        EditorScene.show(new WndOptions(
                                Messages.get(DefaultListItem.class, "open_github_title"),
                                Messages.get(DefaultListItem.class, "open_github_body", clazz.getSimpleName()),
                                Messages.get(WndNewDungeon.class, "add_default_yes"),
                                Messages.get(WndChooseSubclass.class, "no")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    Game.platform.openURI(
                                            "https://github.com/AlphaDraxonis/SandboxPixelDungeon/blob/master/"
                                            + (className.startsWith("com.watabou") ? "SPD-classes" : "core")
                                            + "/src/main/java/" + className.replace('.', '/') + ".java");
                                }
                            }
                        });
                    }
                    else openEditWindow();
                }

                @Override
                protected String hoverText() {
                    if (WndEditorInv.chooseClass) return Messages.get(DefaultListItem.class, "open_github");
                    return Messages.get(DefaultListItem.class, "edit");
                }
            };
            if (WndEditorInv.chooseClass) {
                editButton.icon(Icons.MORE.get());
//                editButton.icon(Icons.GITHUB.get);
            }
            add(editButton);
        }

        onUpdate();
    }

    @Override
    protected void layout() {
        super.layout();

        if (editButton != null) {
            editButton.setRect(width - 3 - editButton.icon().width(), y + (height - editButton.icon().height()) * 0.5f, editButton.icon().width(), editButton.icon().height());
            hotArea.width = editButton.left() - 1;
        }

    }

    @Override
    public void onUpdate() {
        if (item == null) return;

        label.text(Messages.titleCase(item.name()));

        if (item instanceof EditorItem) {
            updateImage(((EditorItem<?>) item).getSprite());
        }

        QuickSlotButton.refresh();
        super.onUpdate();
    }

    public void updateImage(Image newImg) {
        if (icon != null) remove(icon);
        icon = newImg;
        addToBack(icon);
        remove(bg);
        addToBack(bg);
    }

    @Override
    protected int getLabelMaxWidth() {
        return (int) (width - ICON_WIDTH - 1 - 4 - ICON_WIDTH);
    }

    @Override
    protected void onClick() {
        Sample.INSTANCE.play(Assets.Sounds.CLICK);
        if (window == null) {
            openEditWindow();
        } else {
            if (window.selector() != null) {
                window.hide();
                window.selector().onSelect(item);
            } else {
                window.hide();
                QuickSlotButton.set(item);
            }
        }
    }

    @Override
    protected void onRightClick() {
        Sample.INSTANCE.play(Assets.Sounds.CLICK);
        onLongClick();
    }

    @Override
    protected boolean onLongClick() {

        if (openEditWindow()) {
            return true;
        }

        if (window != null) {
            window.hide();
            QuickSlotButton.set(item);
            return true;
        }
        return false;
    }

    protected boolean openEditWindow() {
        if (item instanceof EditorItem) {
            EditorScene.show(new EditCompWindow(item, this));
            return true;
        }
        return false;
    }
}