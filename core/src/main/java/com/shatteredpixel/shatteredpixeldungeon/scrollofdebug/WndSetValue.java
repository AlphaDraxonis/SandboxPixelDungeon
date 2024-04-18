package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.DungeonScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector.FieldLike;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.audio.Sample;

import java.util.List;

public class WndSetValue extends Window {

	public static void show(FieldLike field, Object curValue, Consumer<Object> finish) {
		Class<?> type = field.getType();

		if (type == boolean.class || type == Boolean.class) {
			finish.accept(curValue == null || !(Boolean) curValue);
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
			return;
		}

		String title = Messages.get(WndSetValue.class, "title", type.getSimpleName(), field.getName());
		String body = Messages.get(WndSetValue.class, "body", field.getName(), type.getSimpleName());

		if (type == String.class) {
			enterString((String) curValue, title, body, 0, Integer.MAX_VALUE, finish::accept);
		} else if (type.isPrimitive() || Number.class.isAssignableFrom(type)) {
			if (type == int.class || type == Integer.class) {
				enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, finish::accept);
			} else if (type == float.class || type == Float.class) {
				enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 6, title, body, finish::accept);
			} else if (type == double.class || type == Double.class) {
				enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 12, title, body, finish::accept);
			} else if (type == long.class || type == Long.class) {
				enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, finish::accept);
			} else if (type == short.class || type == Short.class) {
				enterInteger(Short.MIN_VALUE, Short.MAX_VALUE, (Short) curValue, title, body, finish::accept);
			} else if (type == byte.class || type == Byte.class) {
				enterInteger(Byte.MIN_VALUE, Byte.MAX_VALUE, (Byte) curValue, title, body, finish::accept);
			}
		} else if (type == char.class || type == Character.class) {
			enterString(Character.toString((Character) curValue), title, body, 1, 1, finish::accept);
		} else if (type.isEnum()) {
			selectEnum((Enum<?>[]) type.getEnumConstants(), title, body, finish::accept);
		} else {
			selectObject(type, curValue, title, body, finish::accept);
		}

	}

	public static boolean isPrimitiveLike(Class<?> clazz) {
		return clazz.isPrimitive() ||
				clazz == String.class ||
				(Number.class.isAssignableFrom(clazz) &&
						(clazz == Integer.class ||
						clazz == Float.class ||
						clazz == Double.class ||
						clazz == Long.class ||
						clazz == Short.class ||
						clazz == Byte.class)) ||
				clazz == Boolean.class ||
				clazz == Character.class ||
				clazz.isEnum();
	}

	public static void enterInteger(float min, float max, int initValue, String title, String body, Consumer<Integer> onSet) {
		WndTextInput w = new WndTextInput(
				title,
				body,
				String.valueOf(initValue), 12, false,
				Messages.get(WndSetValue.class, "set"),
				Messages.get(WndSetValue.class, "cancel")
		) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) {
					try {
						onSet.accept((int) Math.max(min, Integer.parseInt(text)));
					} catch (NumberFormatException ex) {
						//just ignore value
					}
				}
			}
		};
		w.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (super.acceptChar(textField, c)) return true;
				if (!isVorzeichen(c, min, max)) return false;
				String txt = textField.getText();
				return txt.isEmpty() || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
			}
		});

		w.getTextBox().convertStringToValidString = s -> {
			try {
				int val = Integer.parseInt(s);
//                if (val < min) return Integer.toString((int) min);
				if (val > max) return Integer.toString((int) max);
				return s;
			} catch (NumberFormatException ex) {
				char[] cs = s.toCharArray();
				if (cs.length == 0) return "";
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < cs.length; i++) {
					if (Character.isDigit(cs[i])
							|| i == 0 && isVorzeichen(cs[i], min, max)) b.append(cs[i]);
				}
				s = b.toString();
				if (s.length() == 1 && isVorzeichen(s.charAt(0), min, max)) return s;
				while (true) {
					try {
						int val = Integer.parseInt(s);
						if (val < min) return Integer.toString((int) min);
						if (val > max) return Integer.toString((int) max);
						return s;
					} catch (NumberFormatException ex2) {
						if (s.length() <= 1) return "";
						s = s.substring(0, s.length() - 1);
					}
				}
			}
		};

		show(w);
	}

	public static void enterFloat(float min, float max, float initValue, int precision, String title, String body, Consumer<Float> onSet) {
		final char NUMBER_DECIMAL_SEPARATOR = getNumberDecimalSeparator();
		final String precisionFormat = "%." + precision + "f";
		WndTextInput w = new WndTextInput(title, body,
				String.format(Languages.getCurrentLocale(), precisionFormat, initValue), 10 + precision, false,
				Messages.get(WndSetValue.class, "set"),
				Messages.get(WndSetValue.class, "cancel")
		) {
			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) {
					try {
						onSet.accept(Math.max(min, Float.parseFloat(text.replace(NUMBER_DECIMAL_SEPARATOR, '.'))));
					} catch (NumberFormatException ex) {
						//just ignore value
					}
				}
			}
		};
		w.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter() {
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if (super.acceptChar(textField, c)) return true;
				if (!isVorzeichen(c, min, max) && c != NUMBER_DECIMAL_SEPARATOR) return false;
				String txt = textField.getText();
				if (c == NUMBER_DECIMAL_SEPARATOR) {
					return txt.isEmpty() || !txt.contains(Character.toString(NUMBER_DECIMAL_SEPARATOR));
				}
				return txt.isEmpty() || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
			}
		});

		w.getTextBox().convertStringToValidString = s -> {
			try {
				float val = Float.parseFloat(s.replace(NUMBER_DECIMAL_SEPARATOR, '.'));
//                if (val < min) return String.format(Languages.getCurrentLocale(), precisionFormat, min);
				if (val > max) return String.format(Languages.getCurrentLocale(), precisionFormat, max);
				return s;
			} catch (NumberFormatException ex) {
				char[] cs = s.toCharArray();
				if (cs.length == 0) return "";
				if (cs.length == 1 && isVorzeichen(cs[0], min, max)) return s;
				boolean decimalPointUsed = false;
				for (int i = 0; i < cs.length; i++) {
					if (Character.isDigit(cs[i]) || i == 0 && isVorzeichen(cs[i], min, max));
					else if (!decimalPointUsed) {
						if (cs[i] == NUMBER_DECIMAL_SEPARATOR) {
							decimalPointUsed = true;
						}
					}
				}
				while (true) {
					try {
						float val = Float.parseFloat(s.replace(NUMBER_DECIMAL_SEPARATOR, '.'));
						if (val < min) return String.format(Languages.getCurrentLocale(), precisionFormat, min);
						if (val > max) return String.format(Languages.getCurrentLocale(), precisionFormat, max);
						return s;
					} catch (NumberFormatException ex2) {
						if (s.length() <= 1) return "";
						s = s.substring(0, s.length() - 1);
					}
				}
			}
		};

		show(w);
	}

	private static char getNumberDecimalSeparator() {
		return String.format(Languages.getCurrentLocale(), "%.1f", 1.1f).charAt(1);
	}

	public static boolean isVorzeichen(char c, float min, float max) {
		return c == '-' && min < 0 || c == '+' && max > 0;
	}

	public static void enterString(String initValue, String title, String body, int minLength, int maxLength, Consumer<String> onSet) {
		show(new WndTextInput(title, body, initValue, maxLength, false,
				Messages.get(WndSetValue.class, "set"),
				Messages.get(WndSetValue.class, "cancel")
		) {
			private boolean rejectHide = false;

			@Override
			public void onSelect(boolean positive, String text) {
				if (positive) {
					if (text == null || text.length() < minLength) rejectHide = true;
					else {
						try {
							onSet.accept(text);
						} catch (NumberFormatException ex) {
							//just ignore value
						}
					}
				}
			}

			@Override
			public void hide() {
				if (!rejectHide) super.hide();
				rejectHide = false;
			}
		});
	}

	public static void selectEnum(Enum<?>[] enumConstants, String title, String body, Consumer<Enum<?>> onSet) {
		String[] names = new String[enumConstants.length];
		for (int i = 0; i < enumConstants.length; i++) {
			names[i] = enumConstants[i].name();
		}
		show(new WndOptions(title, body, names) {
			@Override
			protected void onSelect(int index) {
				onSet.accept(enumConstants[index]);
			}
		});
	}

	public static void selectObject(Class<?> type, Object curValue, String title, String body, Consumer<Object> onSet) {
		List<Bag> bags = SelectObjectOption.getMatchingBagsForNewObject(type);
		SelectObjectOption[] options = SelectObjectOption.options(type, bags);

		String[] names = new String[options.length];
		for (int i = 0; i < options.length; i++) {
			names[i] = options[i].label();
		}

		show(new WndOptions(title, body, names) {
			@Override
			protected void onSelect(int index) {
				options[index].select(type, curValue, bags, title, body, onSet);
			}
		});
	}


	static void show(Window w) {
		DungeonScene.show(w);
	}

}