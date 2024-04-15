package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.editor.Copyable;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scrollofdebug.inspector.FieldLike;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WndSetValue extends Window {

	public static void show(FieldLike field, Object obj, Object curValue, Runnable finish) {
		Class<?> type = field.getType();

		if (type == boolean.class || type == Boolean.class) {
			doSetField(field, obj, curValue == null || !(Boolean) curValue, finish);
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
			return;
		}

		String title = Messages.get(WndSetValue.class, "title", type.getSimpleName(), field.getName());
		String body = Messages.get(WndSetValue.class, "body", field.getName(), type.getSimpleName());

		if (type == String.class) {
			enterString((String) curValue, title, body, 0, Integer.MAX_VALUE, val -> doSetField(field, obj, val, finish));
		} else if (type.isPrimitive() || Number.class.isAssignableFrom(type)) {
			if (type == int.class || type == Integer.class) {
				enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, val -> doSetField(field, obj, val, finish));
			} else if (type == float.class || type == Float.class) {
				enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 10, title, body, val -> doSetField(field, obj, val, finish));
			} else if (type == double.class || type == Double.class) {
				enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 20, title, body, val -> doSetField(field, obj, val, finish));
			} else if (type == long.class || type == Long.class) {
				enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, val -> doSetField(field, obj, val, finish));
			} else if (type == short.class || type == Short.class) {
				enterInteger(Short.MIN_VALUE, Short.MAX_VALUE, (Short) curValue, title, body, val -> doSetField(field, obj, val, finish));
			} else if (type == byte.class || type == Byte.class) {
				enterInteger(Byte.MIN_VALUE, Byte.MAX_VALUE, (Byte) curValue, title, body, val -> doSetField(field, obj, val, finish));
			}
		} else if (type == char.class || type == Character.class) {
			enterString(Character.toString((Character) curValue), title, body, 1, 1, val -> doSetField(field, obj, val.charAt(0), finish));
		} else if (type.isEnum()) {
			Enum<?>[] enumConstants = (Enum<?>[]) type.getEnumConstants();
			String[] names = new String[enumConstants.length];
			for (int i = 0; i < enumConstants.length; i++) {
				names[i] = enumConstants[i].name();
			}
			show(new WndOptions(title, body, names) {
				@Override
				protected void onSelect(int index) {
					doSetField(field, obj, enumConstants[index], finish);
				}
			});
		} else {
			//Can't be set via TextInput
			//show WndOptions: select from reference table
			//select from level
			//create new object

			List<Bag> b = new ArrayList<>(7);
			if (type == int.class || type == Integer.class || type == CustomTilemap.class) b.add(Tiles.bag);
			if (Char.class.isAssignableFrom(type) && type != Hero.class) b.add(Mobs.bag);
			if (Item.class.isAssignableFrom(type)) b.add(Items.bag);
			if (Trap.class.isAssignableFrom(type)) b.add(Traps.bag);
			if (Plant.class.isAssignableFrom(type)) b.add(Plants.bag);
			if (Buff.class.isAssignableFrom(type)) b.add(Buffs.bag);
			else b = null;//can't create new object
			List<Bag> bags = b == null ? null : Collections.unmodifiableList(b);

			show(new WndOptions(title, body, "Enter primitive", "From reference table", "from level", "create new object") {
				@Override
				protected void onSelect(int index) {
					switch (index) {
						case 0:
							show(new WndOptions("Choose type of primitve", body, "String", "Character", "Integer", "Float", "Boolean") {
								@Override
								protected void onSelect(int index) {
									switch (index) {
										case 0:
											enterString(String.valueOf(curValue), title, body, 0, Integer.MAX_VALUE, val -> doSetField(field, obj, val, finish));
											break;
										case 1:
											String initValChar = String.valueOf(curValue);
											if (initValChar.length() > 1) initValChar = initValChar.substring(0, 1);
											enterString(initValChar, title, body, 1, 1, val -> doSetField(field, obj, val.charAt(0), finish));
											break;
										case 2:
											int initValInt = 0;
											if (curValue instanceof String) {
												try {initValInt = Integer.parseInt((String) curValue);} catch (NumberFormatException ignored) {}
											}
											else if (curValue instanceof Number) initValInt = ((Number) curValue).intValue();
											enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, initValInt, title, body, val -> doSetField(field, obj, val, finish));
											break;
										case 3:
											float initValFloat = 0;
											if (curValue instanceof String) {
												try {initValFloat = Float.parseFloat((String) curValue);} catch (NumberFormatException ignored) {}
											}
											else if (curValue instanceof Number) initValFloat = ((Number) curValue).intValue();
											enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, initValFloat, 10, title, body, val -> doSetField(field, obj, val, finish));
											break;
										case 4:
											boolean initValBoolean = false;
											if (curValue instanceof String) initValBoolean = Boolean.parseBoolean((String) curValue);
											else if (curValue instanceof Number) initValBoolean = ((Number) curValue).intValue() != 0;
											initValBoolean = !initValBoolean;
											enterString(String.valueOf(initValBoolean), title, body, 0, 5, val -> doSetField(field, obj, Boolean.parseBoolean(val), finish));
											break;
									}
								}
							});
							break;
						case 1:
						case 2:
							if (bags == null && type != Heap.class) return;//TODO just don't include in the first place
						case 3:
							if (bags == null) return;//TODO just don't include in the first place

							WndEditorInv.chooseClass = true;
							EditorScene.selectItem(new WndBag.ItemSelector() {
								@Override
								public String textPrompt() {
									return null;
								}

								@Override
								public Class<? extends Bag> preferredBag() {
									return bags.get(0).getClass();
								}

								@Override
								public List<Bag> getBags() {
									return bags;
								}

								@Override
								public boolean itemSelectable(Item item) {
									Object obj;
									if (item instanceof EditorItem) obj = ((EditorItem<?>) item).getObject();
									else obj = item;
									return type.isAssignableFrom(obj.getClass());
								}

								@Override
								public void onSelect(Item item) {
									WndEditorInv.chooseClass = false;
									Object newVal;
									if (item instanceof EditorItem) newVal = ((EditorItem<?>) item).getObject();
									else newVal = item;
									if (newVal == null) return;
									if (newVal instanceof Copyable<?>) newVal = ((Copyable<?>) newVal).getCopy();
									else newVal = Reflection.newInstance(newVal.getClass());
									doSetField(field, obj, newVal, finish);
								}

								@Override
								public boolean acceptsNull() {
									return false;
								}
							});
					}
				}
			});
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

	private static void doSetField(FieldLike field, Object obj, Object newVal, Runnable finish) {
		try {
			field.set(obj, newVal);
			finish.run();
		} catch (Exception ignored) {
		}
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
				return txt.length() == 0 || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
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
					return txt.length() == 0 || !txt.contains(Character.toString(NUMBER_DECIMAL_SEPARATOR));
				}
				return txt.length() == 0 || textField.getCursorPosition() == 0 && !isVorzeichen(txt.charAt(0), min, max);
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
				StringBuilder b = new StringBuilder();
				boolean decimalPointUsed = false;
				for (int i = 0; i < cs.length; i++) {
					if (Character.isDigit(cs[i])
							|| i == 0 && isVorzeichen(cs[i], min, max)) b.append(cs[i]);
					else if (!decimalPointUsed) {
						if (cs[i] == NUMBER_DECIMAL_SEPARATOR) {
							b.append(cs[i]);
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

	private static void show(Window w) {
		if (Game.scene() instanceof EditorScene) EditorScene.show(w);
		else if (Game.scene() instanceof GameScene) GameScene.show(w);
		else Game.scene().addToFront(w);
	}

}