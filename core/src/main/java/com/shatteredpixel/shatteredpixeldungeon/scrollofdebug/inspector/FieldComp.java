package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndScrollOfDebug;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndSetValue;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.WndStoreReference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.Reference;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.ReferenceNotFoundException;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.references.StandardReference;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.SparseArray;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FieldComp extends ObjInspectorTabComp {

	private final Reference reference;
	private final FieldLike field;

	private Spinner.SpinnerTextBlock value;

	private IconButton take, setToNull;

	private Button valueBoxBtn;
	private Button inspectType;

	public FieldComp(Reference reference, FieldLike field, Object obj) {
		super(obj);
		this.reference = reference;
		this.field = field;

		if (!WndSetValue.isPrimitiveLike(field.getType())) {
			setToNull = new IconButton(Icons.CLOSE.get()) {
				@Override
				protected void onClick() {
					setToNull();
				}
			};
			add(setToNull);
		}

		modifiersTxt.text(WndScrollOfDebug.modifiersToString(field.getModifiers()));
		typeTxt.text("_" + field.getType().getSimpleName() + "_");
		nameTxt.text(" " + field.getName());

		updateValueText();

		try {
			field.get(obj);
		} catch (Exception e) {
			value.setVisible(valueBoxBtn.active = false);
		}

		if (obj == null && !Modifier.isStatic(field.getModifiers())) {
			value.setVisible(false);
			take.setVisible(false);
		}
	}

	@Override
	protected void createChildren(Object... params) {
		super.createChildren(params);

		value = new Spinner.SpinnerTextBlock(Chrome.get(Chrome.Type.TOAST_WHITE), 7);
		add(value);

		take = new IconButton(Icons.BOOKMARK.get()) {
			@Override
			protected void onClick() {
				onTake();
			}
		};
		add(take);

		valueBoxBtn = new Button() {
			@Override
			protected void onClick() {
				Object value = fieldValueCatchException(field, obj);
				if (value == null || WndSetValue.isPrimitiveLike(value.getClass())) onChangeValue();
				else onInspectType();
			}
		};
		add(valueBoxBtn);

		inspectType = new Button() {
			@Override
			protected void onClick() {
				onInspectType();
			}
		};
		add(inspectType);
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
			typeTxt.maxWidth(w);
			posX = x;
			posY = modifiersTxt.bottom() + 2;
		}

		typeTxt.setPos(posX, posY);
		posX = typeTxt.right();

		int widthName = (int) (w - posX + x - 2);
		if (nameTxt.width() > widthName) {
			nameTxt.maxWidth(w);
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

		float widthForButtons = 0;
		take.setRect(x + width - take.icon().width() - 2, y + (height - take.icon().height()) * 0.5f, take.icon().width(), take.icon().height());
		widthForButtons = take.width() + 2;
		if (setToNull != null) {
			setToNull.setRect(take.left() - setToNull.icon().width() - 1, y + (height - setToNull.icon().height()) * 0.5f, setToNull.icon().width(), setToNull.icon().height());
			widthForButtons += setToNull.width() + 1;
		}

		float valueFieldWidth = Math.min(100, Math.max(20, take.right() - nameTxt.right()));
		value.setRect(x + width - valueFieldWidth, y, valueFieldWidth, height);

		if (inspectType != null) {
			inspectType.setRect(typeTxt.left() - 2, typeTxt.top() - 2, typeTxt.width() + 4, typeTxt.height() + 4);
		}

		if (valueBoxBtn != null) {
			valueBoxBtn.setRect(value.left() - 2, value.top() - 2, take.right() - widthForButtons - value.left(), value.height() + 4);
		}
	}

	protected void setToNull() {
		if (WndSetValue.isPrimitiveLike(field.getType())) return;

		DungeonScene.show(new WndOptions(Icons.WARNING.get(), Messages.get(this, "set_to_null_title"), Messages.get(this, "set_to_null_body"),
				Messages.get(HeroSelectScene.class, "daily_yes"), Messages.get(HeroSelectScene.class, "daily_no")) {
			@Override
			protected void onSelect(int index) {
				if (index == 0) {
					try {
						field.set(obj, null);
						updateValueText();
					} catch (Exception ignored) {
					}
				}
			}
		});
	}

	protected void onInspectType() { //clicked on the type name or value box
		if (field.getType().isPrimitive() || field.getType() == String.class) return;

		Reference ref = new StandardReference(field.getType(), fieldValueCatchException(field, obj), field.getName(), reference, field);
		ref.setActualTypeArguments(field.getActualTypeArguments());
		openDifferentInspectWnd(ref);
	}

	protected void onTake() { //clicked on take btn
		GameScene.show(new WndStoreReference(reference, field, obj));
	}

	protected void onChangeValue() { //clicked on change value
		if (!Modifier.isFinal(field.getModifiers()) && value.active) {
			WndSetValue.show(field, fieldValueCatchException(field, obj), newVal -> {
				try {
					field.set(obj, newVal);
					updateValueText();
				} catch (Exception ignored) {
				}
			});
		}
	}

	@Override
	protected boolean matchesSearch(String searchTerm) {
		return field.getName().contains(searchTerm)
				|| field.getName().toLowerCase(Locale.ENGLISH).contains(searchTerm.toLowerCase(Locale.ENGLISH));
	}

	public void updateValueText() {
		value.setText(fieldValueAsString(field, obj));
		if (setToNull != null) setToNull.enable(fieldValueCatchException(field, obj) != null);
	}

	public static Object fieldValueCatchException(FieldLike field, Object obj) {
		try {
			return field.get(obj);
		} catch (Exception e) {
			return null;
		}
	}

	public static String fieldValueAsString(FieldLike field, Object obj) {
		if (obj instanceof ReferenceNotFoundException.ReturnPlaceholder)
			return obj.toString();
		try {
			return valueAsString(field.get(obj));
		} catch (Exception e) {
			return "<Inaccessible>";
		}
	}

	public static String valueAsString(Object value) {
		if (value == null) return "null";
		if (value instanceof String) return "\"" + value + "\"";
		if (value instanceof Character) return "'" + value + "'";
		if (value instanceof Collection) {
			if (((Collection<?>) value).size() > 2) return ((Collection<?>) value).size() + " elements";
			StringBuilder b = new StringBuilder();
			b.append('[');
			for (Object o : ((Collection<?>) value).toArray()) {
				b.append(valueAsString(o));
				b.append(',');
			}
			int length = b.length();
			if (length > 1) b.delete(length - 1, length);
			b.append(']');
			return b.toString();
		}
		if (value instanceof Map) {
			if (((Map<?, ?>) value).size() > 2) return ((Map<?, ?>) value).size() + " elements";
			Map<?, ?> map = (Map<?, ?>) value;
			Set<?> keys = map.keySet();
			StringBuilder b = new StringBuilder();
			b.append('[');
			for (Object key : keys) {
				b.append(valueAsString(key));
				b.append('=');
				Object o = map.get(key);
				b.append(valueAsString(o));
				b.append(',');
			}
			int length = b.length();
			if (length > 1) b.delete(length - 1, length);
			b.append(']');
			return b.toString();
		}
		if (value instanceof SparseArray)
			if (((SparseArray<?>) value).size > 2) return ((SparseArray<?>) value).size + " elements";

		String toString = value.toString();

		if (value.getClass().isArray()) {
			toString = value.getClass().getSimpleName();
			return toString.substring(0, toString.length() - 1) + " " + Array.getLength(value) + " ]";
		}
		if (toString.startsWith(value.getClass().getName()))
			return value.getClass().getSimpleName() + " @" + Integer.toHexString(System.identityHashCode(value));
		return toString.replaceAll(Messages.MAIN_PACKAGE_NAME, "");
	}


}