package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.DungeonTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.List;

public class CustomRecipeList extends Component {

    private final Component outsideSp;

    protected List<RecipeListItem> recipeListItems;

    public CustomRecipeList() {

        outsideSp = new Component() {
            private RedButton btn;

            @Override
            protected void createChildren(Object... params) {
                btn = new RedButton(Messages.get(CustomRecipeList.class, "add_recipe")) {
                    @Override
                    protected void onClick() {
                        addRecipe();
                    }
                };
                add(btn);
            }

            @Override
            protected void layout() {
                btn.setRect(x + 1, y, width - 2, 18);
                height = 18;
            }
        };

    }

    public Component getOutsideSp() {
        return outsideSp;
    }

    protected void addRecipe() {
        CustomRecipe recipe = new CustomRecipe();
        Dungeon.customDungeon.recipes.add(recipe);
        RecipeListItem recipeListItem = new RecipeListItem(recipe);
        recipeListItems.add(recipeListItem);
        add(recipeListItem);
        DungeonTab.updateLayout();
    }

    @Override
    protected void createChildren(Object... params) {
        recipeListItems = new ArrayList<>(5);
        for (CustomRecipe recipe : Dungeon.customDungeon.recipes) {
            RecipeListItem c = new RecipeListItem(recipe);
            recipeListItems.add(c);
            add(c);
        }
    }

    @Override
    protected void layout() {
        float posY = y;
        for (Component c : recipeListItems) {
            c.setRect(x, posY, width, 18);
            posY += c.height();
        }
        height = posY - y;
    }

    public Component createTitle() {
        RenderedTextBlock titleTextBlock = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(CustomRecipeList.class, "title")), 12);
        titleTextBlock.hardlight(Window.TITLE_COLOR);
        return titleTextBlock;
    }

    public class RecipeListItem extends Component {

        protected IconButton remove;
        protected IngedientSlot[] ingredients;
        protected Image arrow;
        protected IngedientSlot output;
        protected Spinner cost;
        protected Image spinnerImage;

        protected ColorBlock line;

        protected final CustomRecipe recipe;

        public RecipeListItem(CustomRecipe recipe) {
            this.recipe = recipe;


            cost = new Spinner(new SpinnerIntegerModel(0, 100, recipe.getCost(), 1, false, null) {

                @Override
                public int getClicksPerSecondWhileHolding() {
                    return 15;
                }

                @Override
                public float getInputFieldWith(float height) {
                    return Spinner.FILL;
                }
            }, "", 8);
            cost.addChangeListener(() -> recipe.setCost((int) cost.getValue()));
            add(cost);

            updateState();
        }

        @Override
        protected void createChildren(Object... params) {
            super.createChildren(params);

            remove = new IconButton(Icons.CLOSE.get()) {
                @Override
                protected void onClick() {
                    Dungeon.customDungeon.recipes.remove(recipe);
                    recipeListItems.remove(RecipeListItem.this);
                    RecipeListItem.this.remove();
                    RecipeListItem.this.destroy();
                    RecipeListItem.this.killAndErase();
                    DungeonTab.updateLayout();
                }
            };
            add(remove);

            arrow = Icons.BACK.get();
            arrow.originToCenter();
            arrow.angle = 180;
            add(arrow);

            output = new IngedientSlot(null, 999);
            add(output);

            ingredients = new IngedientSlot[3];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = new IngedientSlot(null, i);
                add(ingredients[i]);
            }

            add(spinnerImage = Icons.ENERGY.get());

            line = new ColorBlock(1, 1, 0xFF222222);
            add(line);
        }

        @Override
        protected void layout() {

            super.layout();

            line.size(width, 1);
            line.x = x;
            line.y = y;
            PixelScene.align(line);

            float posX = x + 2;
            for (int i = 0; i < 3; i++) {
                if (ingredients[i].visible) {
                    ingredients[i].setRect(posX, y + (height - ItemSpriteSheet.SIZE) * 0.5f, ItemSpriteSheet.SIZE, ItemSpriteSheet.SIZE);
                    posX = ingredients[i].right() + 2;
                    PixelScene.align(ingredients[i]);
                }
            }

            remove.setRect(width - 2 - remove.icon().width(), y + (height - remove.icon().height()) * 0.5f, remove.icon().width(), remove.icon().height());
            PixelScene.align(remove);

            output.setRect(remove.left() - ItemSpriteSheet.SIZE - 3, y + (height - ItemSpriteSheet.SIZE) * 0.5f, ItemSpriteSheet.SIZE, ItemSpriteSheet.SIZE);

            cost.setRect(output.left() - 50, y + (height - ItemSpriteSheet.SIZE) * 0.5f, 50, ItemSpriteSheet.SIZE);
            PixelScene.align(cost);

            spinnerImage.x = cost.left() - spinnerImage.width() - 2;
            spinnerImage.y = y + (height - spinnerImage.height()) * 0.5f;
            PixelScene.align(spinnerImage);

            arrow.x = posX + (spinnerImage.x - posX - arrow.width()) * 0.5f;
            arrow.y = y + (height - arrow.height()) * 0.5f;
            PixelScene.align(arrow);
        }

        private void updateState() {
            output.item(recipe.itemOutput);
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i].item(recipe.itemInputs[i]);
//                ingredients[i].visible = ingredients[i].active = recipe.itemInputs[i] != null;
            }
        }

        private class IngedientSlot extends InventorySlot {

            private final int slot;

            public IngedientSlot(Item item, int slot) {
                super(item);
                this.slot = slot;
            }

            @Override
            protected void onClick() {
                EditorScene.selectItem(new WndBag.ItemSelectorInterface() {
                    @Override
                    public String textPrompt() {
                        return null;
                    }

                    @Override
                    public Class<? extends Bag> preferredBag() {
                        return Items.bag.getClass();
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return true;
                    }

                    @Override
                    public void onSelect(Item item) {
                        if (item == EditorItem.NULL_ITEM) item = null;
                        else if (item instanceof ItemItem) item = ((ItemItem) item).item();
                        item(Reflection.newInstance(item == null ? null : item.getClass()));
                        if (slot == 999) recipe.itemOutput = item;
                        else recipe.itemInputs[slot] = item;
                        recipe.updateQuantities();
                    }

                    @Override
                    public boolean addOtherTabs() {
                        return false;
                    }

                    @Override
                    public boolean acceptsNull() {
                        return true;
                    }
                });
            }

            @Override
            public void item(Item item) {
                if (item != null)
                    item.image = Dungeon.customDungeon.getItemSpriteOnSheet(item);
                super.item(item);
                bg.visible = true;
                enable(visible);
            }
        }

    }


}