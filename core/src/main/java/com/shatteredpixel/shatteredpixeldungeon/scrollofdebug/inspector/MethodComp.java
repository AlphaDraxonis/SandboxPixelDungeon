package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StandardReference;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;

import java.lang.reflect.Method;
import java.util.Locale;

public class MethodComp extends ObjInspectorTabComp {

    private final Method method;

    private Spinner.SpinnerTextBlock value;

    private IconButton take;

    private Button editValue;
    private Button inspectType;

    public MethodComp(Method method, Object obj) {
        super(obj);
        this.method = method;

        modifiersTxt.text(WndScrollOfDebug.modifiersToString(method.getModifiers()));
        typeTxt.text("_" + method.getReturnType().getSimpleName() + "_");
        nameTxt.text(" " + method.getName());

//        value.enable(!Modifier.isFinal(mod) && !WndScrollOfDebug.unassigneableClasses.contains(field.getType()));
        updateValueText();

        if (!method.getReturnType().isPrimitive() && method.getReturnType() != String.class) {
            inspectType = new Button() {
                @Override
                protected void onClick() {
                    openDifferentInspectWnd(new StandardReference(method.getReturnType(), null, method.getName(), null, null));
                }
            };
            add(inspectType);
        }
//
//        if (method.getType().isPrimitive() && method instanceof FieldLike.RealField) {
//            editValue = new Button() {
//                @Override
//                protected void onClick() {
//                    WndSetValue.show(((FieldLike.RealField) method).getField(), obj, fieldValue(method, obj), () -> updateValueText());
//                }
//            };
//            add(editValue);
//        }
    }

    @Override
    protected void createChildren(Object... params) {
        super.createChildren(params);

        value = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 7);
        add(value);

        take = new IconButton(Icons.EDIT.get());
        add(take);
    }

    @Override
    protected void layout() {
        super.layout();

        typeTxt.maxWidth(Integer.MAX_VALUE);
        nameTxt.maxWidth(Integer.MAX_VALUE);

        int w = (int) (width / 2);
        float posX = x;
        float posY = y;

        modifiersTxt.maxWidth(w);
        modifiersTxt.setPos(posX, posY);
        posX = modifiersTxt.right();

        int widthType = (int) (w - posX + x - 2);
        if (typeTxt.width() > widthType) {
            typeTxt.maxWidth(widthType);
            posX = x;
            posY = modifiersTxt.bottom() + 2;
        }

        typeTxt.setPos(posX, posY);
        posX = typeTxt.right();

        int widthName = (int) (w - posX + x - 2);
        if (nameTxt.width() > widthName) {
            nameTxt.maxWidth(widthName);
            posX = x;
            posY = typeTxt.bottom() + 2;
        }

        nameTxt.setPos(posX, posY);

        float totalHeight = nameTxt.bottom() - modifiersTxt.top();

        if (totalHeight > height) height = totalHeight;
        else {
            float addY = (height - totalHeight) * 0.5f;
            modifiersTxt.setPos(modifiersTxt.left(), modifiersTxt.top() + addY);
            typeTxt.setPos(typeTxt.left(), typeTxt.top() + addY);
            nameTxt.setPos(nameTxt.left(), nameTxt.top() + addY);
        }

        take.setRect(x + width - take.icon().width() - 2, y + (height - take.icon().height()) * 0.5f, take.icon().width(), take.icon().height());

        float valueFieldWidth = Math.min(100, Math.max(20, take.left() - nameTxt.right()));
        value.setRect(x + width - valueFieldWidth, y, valueFieldWidth, height);

        if (inspectType != null) {
            inspectType.setRect(typeTxt.left() - 2, typeTxt.top() - 2, typeTxt.width() + 4, typeTxt.height() + 4);
        }

        if (editValue != null) {
            editValue.setRect(value.left() - 2, value.top() - 2, value.width() + 4, value.height() + 4);
        }

//        hotArea.x = value.left();
//        hotArea.width = valueFieldWidth - 4 - take.icon().width();
    }

    @Override
    protected boolean matchesSearch(String searchTerm) {
        return method.getName().contains(searchTerm)
                || method.getName().toLowerCase(Locale.ENGLISH).contains(searchTerm.toLowerCase(Locale.ENGLISH));
    }

    public void updateValueText() {
        StringBuilder b = new StringBuilder("( ");
        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            b.append(params[i].getSimpleName());
            if (i+1 < params.length) b.append(", ");
            else b.append(' ');
        }
        b.append(')');
        value.showValue(b.toString());
    }


}