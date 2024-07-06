package com.shatteredpixel.shatteredpixeldungeon.scrollofdebug;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.editor.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.WndEditorInv;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.*;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.CustomTileItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.util.Consumer;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public enum SelectObjectOption {
	PRIMITIVE, //or string
	FROM_REFERENCE,
	FROM_LEVEL,
	NEW_OBJECT;

	public enum SelectPrimitiveOption {
		STRING,
		CHARACTER,
		INTEGER,
		FLOAT,
		BOOLEAN;

		private final SelectPrimitiveOption[] singletonArray;

		SelectPrimitiveOption() {
			this.singletonArray = new SelectPrimitiveOption[] {this};
		}

		public static String title() {
			return Messages.get(SelectObjectOption.SelectPrimitiveOption.class, "title");
		}

		public static SelectPrimitiveOption[] options(Class<?> type) {

			if (type == Object.class) {
				return SelectPrimitiveOption.values();
			}

			if (type == String.class) return STRING.singletonArray;
			if (type == char.class || type == Character.class) return CHARACTER.singletonArray;
			if (Number.class.isAssignableFrom(type)
					|| type == int.class || type == float.class || type == double.class
					|| type == short.class || type == long.class || type == byte.class) {
				if (type == float.class || type == double.class || type == Float.class || type == Double.class)
					return new SelectPrimitiveOption[]{INTEGER, FLOAT};
			}
			if (type == boolean.class || type == Boolean.class) return BOOLEAN.singletonArray;

			return new SelectPrimitiveOption[0];
		}

		public void select(Object curValue, String title, String body, Consumer<Object> onSelect) {
			switch (this) {
				case STRING:
					WndSetValue.enterString(String.valueOf(curValue), title, body, 0, Integer.MAX_VALUE, onSelect::accept);
					break;
				case CHARACTER:
					String initValChar = String.valueOf(curValue);
					if (initValChar.length() > 1) initValChar = initValChar.substring(0, 1);
					WndSetValue.enterString(initValChar, title, body, 1, 1, val -> onSelect.accept(val.charAt(0)));
					break;
				case INTEGER:
					int initValInt = 0;
					if (curValue instanceof String) {
						try {initValInt = Integer.parseInt((String) curValue);} catch (NumberFormatException ignored) {}
					}
					else if (curValue instanceof Number) initValInt = ((Number) curValue).intValue();
					WndSetValue.enterInteger(Integer.MIN_VALUE, Integer.MAX_VALUE, initValInt, title, body, onSelect::accept);
					break;
				case FLOAT:
					float initValFloat = 0;
					if (curValue instanceof String) {
						try {initValFloat = Float.parseFloat((String) curValue);} catch (NumberFormatException ignored) {}
					}
					else if (curValue instanceof Number) initValFloat = ((Number) curValue).intValue();
					WndSetValue.enterFloat(Float.MIN_VALUE, Float.MAX_VALUE, initValFloat, 6, title, body, onSelect::accept);
					break;
				case BOOLEAN:
					boolean initValBoolean = false;
					if (curValue instanceof String) initValBoolean = Boolean.parseBoolean((String) curValue);
					else if (curValue instanceof Number) initValBoolean = ((Number) curValue).intValue() != 0;
					initValBoolean = !initValBoolean;
					WndSetValue.enterString(String.valueOf(initValBoolean), title, body, 0, 5, val -> onSelect.accept(Boolean.parseBoolean(val)));
					break;
			}
		}

		public String label() {
			return Messages.get(this, name().toLowerCase(Locale.ENGLISH) + "_label");
		}
	}

	public static SelectObjectOption[] options(Class<?> type, List<Bag> returnValueOf_getMatchingBagsForNewObject) {

		List<SelectObjectOption> result = new ArrayList<>(6);

//		result.add(FROM_REFERENCE);//always possible

		if (WndSetValue.isPrimitiveLike(type) || type == Object.class) {
			result.add(PRIMITIVE);
		}

		if (returnValueOf_getMatchingBagsForNewObject != null
				|| type == Heap.class || type == int.class || type == Integer.class || type == Sign.class) {
			result.add(FROM_LEVEL);
		}

		if (returnValueOf_getMatchingBagsForNewObject != null) {
			result.add(NEW_OBJECT);
		}

		return result.toArray(new SelectObjectOption[0]);

	}

	public static List<Bag> getMatchingBagsForNewObject(Class<?> type) {
		List<EditorItemBag> result = new ArrayList<>(7);
		if (type == int.class || type == Integer.class || type == CustomTilemap.class || type == Object.class) result.add(Tiles.bag);
		if (Char.class.isAssignableFrom(type) && type != Hero.class || type.isAssignableFrom(Char.class)) result.add(Mobs.bag);
		if (Item.class.isAssignableFrom(type) || type.isAssignableFrom(Item.class)) result.add(Items.bag);
		if (Trap.class.isAssignableFrom(type) || type.isAssignableFrom(Trap.class)) result.add(Traps.bag);
		if (Plant.class.isAssignableFrom(type) || type.isAssignableFrom(Plant.class)) result.add(Plants.bag);
		if (Buff.class.isAssignableFrom(type) || type.isAssignableFrom(Buff.class)) result.add(Buffs.bag);
		else result = null;//can't create new object
		return result == null ? null : Collections.unmodifiableList(result);
	}

	public void select(Class<?> type, Object curValue, List<Bag> bags, String title, String body, Consumer<Object> onSelect) {
		switch (this) {
			case PRIMITIVE:
				SelectPrimitiveOption[] options = SelectPrimitiveOption.options(type);
				if (options.length == 1) {
					options[0].select(curValue, title, body, onSelect);
					break;
				}
				String[] names = new String[options.length];
				for (int i = 0; i < options.length; i++) {
					names[i] = options[i].label();
				}
				WndSetValue.show(new WndOptions(SelectObjectOption.SelectPrimitiveOption.title(), body, names) {
					@Override
					protected void onSelect(int index) {
						options[index].select(curValue, title, body, onSelect);
					}
				});
				break;
			case FROM_REFERENCE:
				break;
			case FROM_LEVEL:
				GameScene.hideWindowsTemporarily();
				GameScene.selectCell(new CellSelector.Listener() {
					@Override
					public void onSelect(Integer cell) {
						if (cell != null && cell >= 0 && cell <= Dungeon.level.length()) {
							if (type == Integer.class) onSelect.accept(cell);
							else if (Mob.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.findMob(cell));
							else if (Heap.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.heaps.get(cell));
							else if (Item.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.heaps.get(cell) == null ? null : Dungeon.level.heaps.get(cell).peek());
							else if (Trap.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.traps.get(cell));
							else if (Plant.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.plants.get(cell));
							else if (Barrier.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.barriers.get(cell));
							else if (ArrowCell.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.arrowCells.get(cell));
							else if (Sign.class.isAssignableFrom(type)) onSelect.accept(Dungeon.level.signs.get(cell));
							else if (Buff.class.isAssignableFrom(type)) {
								Char ch = Actor.findChar(cell);
								if (ch == null || ch.buffs(((Class<? extends Buff>) type)).isEmpty()) onSelect.accept(null);
								else onSelect.accept(ch.buffs(((Class<? extends Buff>) type)).iterator().next());
							} else if (CustomTilemap.class.isAssignableFrom(type)) {
								onSelect.accept(CustomTileItem.findAnyCustomTileAt(cell));
							}
						}
						GameScene.reshowWindows();
					}

					@Override
					public String prompt() {
						return "propmt tzz";
					}
				});
				break;
			case NEW_OBJECT:

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
						onSelect.accept(newVal);
					}

					@Override
					public boolean acceptsNull() {
						return false;
					}
				});
				break;
		}
	}

	public String label() {
		return Messages.get(this, name().toLowerCase(Locale.ENGLISH) + "_label");
	}
}