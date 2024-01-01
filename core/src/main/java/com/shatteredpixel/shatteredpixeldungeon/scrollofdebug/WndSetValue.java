package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;

import java.lang.reflect.Field;

public class WndSetValue extends Window {

    public static void show(Field field, Object obj, Object curValue, Runnable finish) {
        Class<?> type = field.getType();

        if (type == boolean.class || type == Boolean.class) {
            doSetField(field, obj, !(boolean) curValue, finish);
            return;
        }

        String title = Messages.get(WndSetValue.class, "title", type.getSimpleName(), field.getName());
        String body = Messages.get(WndSetValue.class, "body", field.getName(), type.getSimpleName());
        if (type == int.class || type == Integer.class) {
            enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == float.class || type == Float.class) {
            enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 10, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == String.class) {
            enterString((String) curValue, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == double.class || type == Double.class) {
            enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, (Float) curValue, 20, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == long.class || type == Long.class) {
            enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, (Integer) curValue, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == short.class || type == Short.class) {
            enterInteger(Short.MIN_VALUE, Short.MAX_VALUE, (Short) curValue, title, body, val -> doSetField(field, obj, val, finish));
        }
        else if (type == byte.class || type == Byte.class) {
            enterInteger(Byte.MIN_VALUE, Byte.MAX_VALUE, (Byte) curValue, title, body, val -> doSetField(field, obj, val, finish));
        }

    }

    private static void doSetField(Field field, Object obj, Object newVal, Runnable finish) {
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

    public static void enterString(String initValue, String title, String body, Consumer<String> onSet) {
        show(new WndTextInput(title, body, initValue, Integer.MAX_VALUE, false,
                Messages.get(WndSetValue.class, "set"),
                Messages.get(WndSetValue.class, "cancel")
        ) {
            @Override
            public void onSelect(boolean positive, String text) {
                if (positive) {
                    try {
                        onSet.accept(text);
                    } catch (NumberFormatException ex) {
                        //just ignore value
                    }
                }
            }
        });
    }

    private static void show(Window w) {
        if (Game.scene() instanceof EditorScene) EditorScene.show(w);
        else if (Game.scene() instanceof GameScene) GameScene.show(w);
        else Game.scene().addToFront(w);
    }

}