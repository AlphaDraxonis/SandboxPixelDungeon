package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FieldComp extends Button {

    private final Field field;
    private final Object obj;

    private RenderedTextBlock modifiers;
    private RenderedTextBlock type;
    private RenderedTextBlock name;

    private Spinner.SpinnerTextBlock value;

    private IconButton take;

    private Button editValue;
    private Button inspectType;

    public FieldComp(Field field, Object obj) {
        this.field = field;
        this.obj = obj;

        int mod = field.getModifiers();
        modifiers.text((mod == 0) ? "" : (Modifier.toString(mod).replace("public ", "") + " "));
        type.text("_" + field.getType().getSimpleName() + "_");
        name.text(" " + field.getName());

        value.enable(!Modifier.isFinal(mod) && !WndScrollOfDebug.unassigneableClasses.contains(field.getType()));
        updateValueText();
    }

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        modifiers = PixelScene.renderTextBlock(6);
        add(modifiers);
        type = PixelScene.renderTextBlock(6);
        add(type);
        name = PixelScene.renderTextBlock(6);
        name.setHighlighting(false);
        add(name);

        value = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 7);
        add(value);

        take = new IconButton(Icons.EDIT.get());
        add(take);
    }

    @Override
    protected void layout() {
        super.layout();

        modifiers.setPos(x, y + 2);
        type.setPos(modifiers.right(), y + 2);
        name.setPos(type.right(), y + 2);

        take.setRect(x + width - take.icon().width() - 2, y + (height - take.icon().height()) * 0.5f, take.icon().width(), take.icon().height());

        float valueFieldWidth = Math.min(100, Math.max(20, take.left() - name.right()));
        value.setRect(x + width - valueFieldWidth, y, valueFieldWidth, height);

        hotArea.x = value.left();
        hotArea.width = valueFieldWidth - 4 - take.icon().width();
    }

    public void updateValueText() {
        value.setText(fieldValueAsString(field, obj));
    }

    @Override
    protected void onClick() {
        Object value;
        try {
            value = field.get(obj);
        } catch (Exception e) {
            value = null;
        }
        WndSetValue.show(field, obj, value, this::updateValueText);
    }

    public static String fieldValueAsString(Field field, Object obj) {
        Object value;
        try {
            value = field.get(obj);
        } catch (Exception e) {
            value = null;
        }
        return valueAsString(field.getType(), value);
    }

    private static String valueAsString(Class<?> type, Object value) {
        if (value == null) return "null";
        if (value instanceof Collection) {
            StringBuilder b = new StringBuilder();
            b.append('[');
            for (Object o : ((Collection<?>) value).toArray()) {
                b.append(valueAsString(o.getClass(), o));
                b.append(',');
            }
            int length = b.length();
            if (length > 1) b.delete(length - 1, length);
            b.append(']');
            return b.toString();
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Set<?> keys = map.keySet();
            StringBuilder b = new StringBuilder();
            b.append('[');
            for (Object key : keys) {
                b.append(valueAsString(key.getClass(), key));
                b.append('=');
                Object o = map.get(key);
                b.append(valueAsString(o.getClass(), o));
                b.append(',');
            }
            int length = b.length();
            if (length > 1) b.delete(length - 1, length);
            b.append(']');
            return b.toString();
        }
        String toString = value.toString();
        if (toString.startsWith(value.getClass().getName())) return type.getSimpleName();
        return toString.replaceAll("com.shatteredpixel.shatteredpixeldungeon.", "");
    }


}