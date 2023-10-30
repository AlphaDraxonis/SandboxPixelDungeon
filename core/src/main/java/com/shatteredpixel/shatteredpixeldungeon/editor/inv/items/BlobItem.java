package com.shatteredpixel.shatteredpixeldungeon.editor.inv.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.DefaultEditComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditBlobComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.DefaultListItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.EditorInventoryWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.gases.PermaGas;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomLevel;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.ActionPart;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.Undo;
import com.shatteredpixel.shatteredpixeldungeon.editor.scene.undo.parts.BlobEditPart;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollingListPane;
import com.watabou.noosa.Image;
import com.watabou.utils.RectF;

public class BlobItem extends EditorItem {

    private final Class<? extends Blob> blob;


    public BlobItem(Class<? extends Blob> blob) {
        this.blob = blob;
    }

    @Override
    public ScrollingListPane.ListItem createListItem(EditorInventoryWindow window) {
        return new DefaultListItem(this, window, name(), getSprite()) {
            @Override
            public void onUpdate() {
                super.onUpdate();
            }
        };
    }

    @Override
    public DefaultEditComp<?> createEditComponent() {
        return new EditBlobComp(blob());//Edit duration and ?reward?
    }

    @Override
    public Image getSprite() {
        return createIcon(blob());
    }

    @Override
    public void place(int cell) {

        CustomLevel level = EditorScene.customLevel();

        if (invalidPlacement(cell, level)) return;

        Undo.addActionPart(place(blob(), cell));
    }

    @Override
    public Object getObject() {
        return blob();
    }

    public Class<? extends Blob> blob() {
        return blob;
    }

    @Override
    public String name() {
        return createName(blob());
    }

    public static String createName(Class<? extends Blob> blob) {
        return Messages.get(BlobItem.class, blob.getSimpleName());
    }

    public static Image createIcon(Class<? extends Blob> blob) {
        if (blob == MagicalFireRoom.EternalFire.class) {
            Image icon = Icons.ETERNAL_FIRE.get();
            icon.scale.set(2.28f);//16/7=2.28
            return icon;
        } else if (blob == SacrificialFire.class) {
            Image icon = Icons.SACRIFICIAL_FIRE.get();
            icon.scale.set(2.28f);//16/7=2.28
            return icon;
        } else if (PermaGas.class.isAssignableFrom(blob)) {
            RectF r = Speck.getFilm().get(Speck.STEAM);
            if (r == null) return null;

            int color;
            if (blob == PermaGas.PToxicGas.class) color = 0x50FF60;
            else if (blob == PermaGas.PCorrosiveGas.class) color = 0xDC8C32;
            else if (blob == PermaGas.PConfusionGas.class) color = 0xA882A0;
            else if (blob == PermaGas.PParalyticGas.class) color = 0xDCE150;
            else if (blob == PermaGas.PStenchGas.class) color = 0x003300;
            else return new ItemSprite();
            Image icon = new Image(Assets.Effects.SPECKS) {
                @Override
                public void resetColor() {
                    super.resetColor();
                    hardlight(color);
                }

                @Override
                public void brightness(float value) {
                    rm += value - 1f;
                    gm += value - 1f;
                    bm += value - 1f;
                }

                @Override
                public void lightness(float value) {
                    if (value < 0.5f) {
                        rm = value * 2f * rm;
                        gm = value * 2f * gm;
                        bm = value * 2f * bm;
                        ra = ga = ba = 0;
                    } else {
                        if (blob == PermaGas.PToxicGas.class) {
                            rm = 3f * rm - value * 3f * rm;
                            gm = 3f * gm - value * 3f * gm;
                            bm = 3f * bm - value * 3f * bm;
                        } else {
                            rm = 2f * rm - value * 2f * rm;
                            gm = 2f * gm - value * 2f * gm;
                            bm = 2f * bm - value * 2f * bm;
                        }
                        ra = ga = ba = value * 2f - 1f;
                    }
                }
            };
            icon.frame(r);
            icon.hardlight(color);
            icon.scale.set(2.28f);//16/7=2.28
            return icon;
        }
        return new ItemSprite();
    }

    public static boolean invalidPlacement(int cell, CustomLevel level) {
//        return level.passable[cell];
        return level.solid[cell] || !level.insideMap(cell);
    }

    public static ActionPart remove(int cell) {
        BlobEditPart.Modify part = new BlobEditPart.Modify(cell);
        BlobEditPart.clearNormalAtCell(cell);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }

    public static ActionPart place(Class<? extends Blob> blob, int cell) {
        BlobEditPart.Modify part = new BlobEditPart.Modify(cell);
        BlobEditPart.place(cell, blob, 1);
        part.finish();
        if (part.hasContent()) return part;
        return null;
    }
}