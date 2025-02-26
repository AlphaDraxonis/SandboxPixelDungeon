package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.EditorScene;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditCompWindow;
import com.shatteredpixel.shatteredpixeldungeon.editor.editcomps.EditItemComp;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.categories.Items;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.EditorItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.inv.items.ItemItem;
import com.shatteredpixel.shatteredpixeldungeon.editor.levels.CustomDungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.levelsettings.dungeon.DungeonTab;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.spinner.SpinnerIntegerModel;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomRecipeList extends Component {

    private final Component outsideSp;

    private List<RecipeListItem> recipeListItems;

    public CustomRecipeList() {

        outsideSp = new Component() {
            private RedButton addBtn;
            private IconButton disableRecipes;

            @Override
            protected void createChildren() {
                addBtn = new RedButton(Messages.get(CustomRecipeList.class, "add_recipe")) {
                    @Override
                    protected void onClick() {
                        addRecipe();
                    }
                };
                add(addBtn);

                disableRecipes = new IconButton(Icons.MORE.get()) {
                    @Override
                    protected void onClick() {
                        EditorScene.show(new WndDisableRecipes());
                    }

                    @Override
                    protected String hoverText() {
                        return Messages.get(WndDisableRecipes.class, "title");
                    }
                };
                add(disableRecipes);
            }

            @Override
            protected void layout() {
                addBtn.setRect(x + 1, y, width - 4 - disableRecipes.icon().width(), 18);
                disableRecipes.setRect(addBtn.right() + 2, y, disableRecipes.icon().width(), 18);
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
    protected void createChildren() {
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
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(CustomRecipeList.class, "title")), 11);
        title.hardlight(Window.TITLE_COLOR);
        return title;
    }

    private class RecipeListItem extends Component {

        private static final int INDEX_OUTPUT = 9999;

        protected IconButton remove;
        protected IngredientSlot[] ingredients;
        protected Image arrow;
        protected IngredientSlot output;
        protected EnergySpinner cost;

        protected ColorBlock line;

        protected final CustomRecipe recipe;

        public RecipeListItem(CustomRecipe recipe) {
            this.recipe = recipe;

            cost = new EnergySpinner(recipe);
            add(cost);
            
            remove = new IconButton(Icons.TRASH.get()) {
                @Override
                protected void onClick() {
                    Dungeon.customDungeon.recipes.remove(recipe);
                    recipeListItems.remove(RecipeListItem.this);
                    RecipeListItem.this.killAndErase();
                    RecipeListItem.this.destroy();
                    DungeonTab.updateLayout();
                }
            };
            add(remove);
            
            arrow = Icons.BACK.get();
            arrow.originToCenter();
            arrow.angle = 180;
            add(arrow);
            
            output = new IngredientSlot(null, INDEX_OUTPUT);
            add(output);
            
            ingredients = new IngredientSlot[3];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = new IngredientSlot(null, i);
                add(ingredients[i]);
            }
            
            line = new ColorBlock(1, 1, ColorBlock.SEPARATOR_COLOR);
            add(line);

            updateState();
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

            cost.setRect( posX + 5, y + (height - ItemSpriteSheet.SIZE) * 0.5f, 50, ItemSpriteSheet.SIZE);
            PixelScene.align(cost);
            posX = cost.right() + 2;

            if (width >= posX + remove.icon().width() + 2 + ItemSpriteSheet.SIZE + 3 + arrow.width()) {
                remove.setRect(width - 2 - remove.icon().width(), y + (height - remove.icon().height()) * 0.5f, remove.icon().width(), remove.icon().height());
                PixelScene.align(remove);

                output.setRect(remove.left() - ItemSpriteSheet.SIZE - 3, y + (height - ItemSpriteSheet.SIZE) * 0.5f, ItemSpriteSheet.SIZE, ItemSpriteSheet.SIZE);

                arrow.x = posX + (output.left() - posX - arrow.width()) * 0.5f;
                arrow.y = y + (height - arrow.height()) * 0.5f;
                PixelScene.align(arrow);
            } else {
                remove.setRect(width - 2 - remove.icon().width(), y + height + (height - remove.icon().height()) * 0.5f, remove.icon().width(), remove.icon().height());
                PixelScene.align(remove);

                float outputPlusArrowWidth = ItemSpriteSheet.SIZE + arrow.width() + 6;

                arrow.x = (width - outputPlusArrowWidth)*0.5f;
                arrow.y = y + height + (height - arrow.height()) * 0.5f;
                PixelScene.align(arrow);

                output.setRect(arrow.x + arrow.width() + 6, y + height + (height - ItemSpriteSheet.SIZE) * 0.5f, ItemSpriteSheet.SIZE, ItemSpriteSheet.SIZE);

                height *= 2;
            }
        }

        private void updateState() {
            output.item(recipe.itemOutput);
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i].item(recipe.itemInputs[i]);
//                ingredients[i].visible = ingredients[i].active = recipe.itemInputs[i] != null;
            }
        }

        private class IngredientSlot extends InventorySlot {

            private final int slot;

            public IngredientSlot(Item item, int slot) {
                super(item);
                this.slot = slot;
            }

            @Override
            protected void onClick() {
                if (item != null) {
                    boolean wasStackable = item.stackable;
                    if (slot != INDEX_OUTPUT) item.stackable = false;
                    EditItemComp.showOnlyCheckType = true;
                    EditorScene.show(new EditCompWindow(item) {
                        {
                            item.stackable = wasStackable;//don't show quantity spinner
                        }

                        @Override
                        protected void onUpdate() {
                            super.onUpdate();
                            item(item);
                        }
                    });
                } else onLongClick();
            }

            @Override
            protected boolean onLongClick() {
                EditorScene.selectItem(new WndBag.ItemSelectorInterface() {
                    @Override
                    public String textPrompt() {
                        return null;
                    }

                    @Override
                    public Class<? extends Bag> preferredBag() {
                        return Items.bag().getClass();
                    }

                    @Override
                    public List<Bag> getBags() {
                        return Collections.singletonList(Items.bag());
                    }

                    @Override
                    public boolean itemSelectable(Item item) {
                        return true;
                    }

                    @Override
                    public void onSelect(Item item) {
                        if (item == EditorItem.NULL_ITEM) item = null;
                        else if (item instanceof ItemItem) item = ((ItemItem) item).item();
                        if (item != null) item = item.getCopy();
                        if (slot == INDEX_OUTPUT) recipe.itemOutput = item;
                        else recipe.setInput(slot, item);
                        item(item);
                    }

                    @Override
                    public boolean acceptsNull() {
                        return true;
                    }
                });
                return true;
            }

            @Override
            public void item(Item item) {
                if (item != null)
                    item.image = CustomDungeon.getItemSpriteOnSheet(item);
                super.item(item);

                sprite.visible = sprite.active = item != null;

                bg.visible = true;
                enable(visible);
            }
        }

    }
    
    private static class EnergySpinner extends Spinner {
        
        public EnergySpinner(CustomRecipe recipe) {
            super(new EnergySpinnerModel(recipe), "", 8);
            
            addChangeListener(() -> recipe.setCost((int) getValue()));
            setButtonWidth(10f);
        }
    }
    
    private static class EnergySpinnerModel extends SpinnerIntegerModel {
        
        public EnergySpinnerModel(CustomRecipe recipe) {
            super(0, 100, recipe.cost(null));
        }
        
        @Override
        public int getClicksPerSecondWhileHolding() {
            return super.getClicksPerSecondWhileHolding() / 2;
        }
        
        @Override
        public float getInputFieldWidth(float height) {
            return height + 5;
        }
        
        @Override
        public Component createInputField(int fontSize) {
            return (Component) (valueDisplay = new ValueDisplayItemSlot());
        }
        
        private class ValueDisplayItemSlot extends ItemSlot implements ValueDisplay {
            
            public ValueDisplayItemSlot() {
                super(new EnergyCrystal() {
                    @Override
                    public String status() {
                        return Integer.toString( quantity );
                    }
                });
            }
            
            @Override
            protected void onClick() {
                EnergySpinnerModel.this.onClick();
            }
            
            @Override
            public void showValue(Object value) {
                item.quantity( Integer.parseInt(value.toString()) );
                updateCurrentItem();
            }
            
            @Override
            public void enableValueField(boolean flag) {
                alpha(flag ? 1.0f : 0.3f);
            }
            
        }
        
    }


}