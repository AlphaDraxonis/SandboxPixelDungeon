package com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.parts.items.LevelSpinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerTextModel;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.StyledSpinner;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.GlyphArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlyphSpinner extends StyledSpinner {

    public GlyphLevelSpinner glyphLevelSpinner;

    public GlyphSpinner(Mob mob) {
        super(new GlyphSpinnerModel(mob.glyphArmor == null ? null : mob.glyphArmor.glyph), Messages.get(Mob.class, "glyph"), 9, new ItemSprite(ItemSpriteSheet.STYLUS));
        setButtonWidth(9f);

        addChangeListener(() -> {
            if (glyphLevelSpinner == null) return;
            Class<?> val = (Class<?>) getValue();
            if (val == null) {
                mob.glyphArmor = null;
                return;
            }
            if (mob.glyphArmor == null) {
                mob.glyphArmor = new GlyphArmor();
                mob.glyphArmor.level((int) glyphLevelSpinner.getValue());
            }
            mob.glyphArmor.glyph = (Armor.Glyph) Reflection.newInstance(val);
        });
    }

    private static class GlyphSpinnerModel extends SpinnerTextModel {

        public GlyphSpinnerModel(Armor.Glyph glyph) {
            super(true, findIndex(glyph), GLYPHS_ARRAY);
        }

        @Override
        protected String displayString(Object value) {
            if (value == null) return Messages.get(GlyphSpinner.class, "none");
            return Messages.titleCase(Messages.get((Class<?>) value, "pure_name"));
        }

        @Override
        public Component createInputField(int fontSize) {
            return super.createInputField(fontSize - 2);
        }

        private static final Object[] GLYPHS_ARRAY;

        static {
            List<Class<?>> dataList = new ArrayList<>();
            dataList.add(null);
            dataList.addAll(Arrays.asList(Armor.Glyph.common));
            dataList.addAll(Arrays.asList(Armor.Glyph.uncommon));
            dataList.addAll(Arrays.asList(Armor.Glyph.rare));
            dataList.addAll(Arrays.asList(Armor.Glyph.curses));
            GLYPHS_ARRAY = dataList.toArray();
        }

        private static int findIndex(Armor.Glyph glyph) {
            Class<?> clazz = glyph == null ? null : glyph.getClass();
            for (int i = 0; i < GLYPHS_ARRAY.length; i++) {
                if (clazz == GLYPHS_ARRAY[i]) return i;
            }
            return -1;
        }
    }

    public static class GlyphLevelSpinner extends LevelSpinner {

        public GlyphLevelSpinner(Mob mob) {
            super(mob.glyphArmor == null ? new Armor(0) : mob.glyphArmor);
            label.text(Messages.get(this, "label"));

            ((SpinnerIntegerModel) getModel()).setAbsoluteMinimum(0f);
            ((SpinnerIntegerModel) getModel()).setMinimum(0);

            removeChangeListener(getChangeListeners()[0]);
            addChangeListener(() -> {
                if (mob.glyphArmor != null) mob.glyphArmor.level((int) getValue());
            });
        }
    }
}