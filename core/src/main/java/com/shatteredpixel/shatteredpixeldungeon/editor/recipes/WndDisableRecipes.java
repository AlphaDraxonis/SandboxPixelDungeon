package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.*;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfYendor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSupportPrompt;

import java.util.ArrayList;
import java.util.List;


public class WndDisableRecipes extends WndChooseOneInCategories {


    public WndDisableRecipes() {
        super(Messages.titleCase(Messages.get(WndDisableRecipes.class, "title")),
                Messages.get(WndDisableRecipes.class, "desc"),
                new Object[][]{{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}}, new String[]{
                        Messages.get(WndDisableRecipes.class, "cat_1"),
                        Messages.get(WndDisableRecipes.class, "cat_2"),
                        Messages.get(WndDisableRecipes.class, "cat_3"),
                        Messages.get(WndDisableRecipes.class, "cat_4"),
                        Messages.get(WndDisableRecipes.class, "cat_5"),
                        Messages.get(WndDisableRecipes.class, "specific_potions"),
                        Messages.get(WndDisableRecipes.class, "specific_runestones"),
                        Messages.get(WndDisableRecipes.class, "specific_exotic_potions"),
                        Messages.get(WndDisableRecipes.class, "specific_exotic_scrolls"),
                });
        body.setCancelText(Messages.get(WndSupportPrompt.class, "close"));
    }

    @Override
    protected ChooseOneInCategoriesBody.BtnRow[] createCategoryRows(Object[] category) {
        List<RecipeInfo> recipes = getRecipes((int) category[0]);

        ChooseOneInCategoriesBody.BtnRow[] ret = new ChooseOneInCategoriesBody.BtnRow[recipes.size()];
        int index = 0;
        for (RecipeInfo r : recipes) {
            ret[index] = new ChooseOneInCategoriesBody.BtnRow(r.name, r.desc, new ItemSprite(r.sampleOutputImage)) {
                {
                    btn.remove();
                    btn.destroy();
                    btn.killAndErase();

                    btn = new CheckBox(Messages.titleCase(r.name)) {
                        @Override
                        public void checked(boolean value) {
                            super.checked(value);
                            if (r.index == -1) {
                                if (value) Dungeon.customDungeon.blockedRecipeResults.add(r.item);
                                else Dungeon.customDungeon.blockedRecipeResults.remove(r.item);
                            } else {
                                if (value) Dungeon.customDungeon.blockedRecipes.add(r.index);
                                else Dungeon.customDungeon.blockedRecipes.remove(r.index);
                            }
                        }
                    };
                    if (r.index == -1) {
                        ((CheckBox) btn).checked(Dungeon.customDungeon.blockedRecipeResults.contains(r.item));
                    } else {
                        ((CheckBox) btn).checked(Dungeon.customDungeon.blockedRecipes.contains(r.index));
                    }
                    add(btn);
                }
            };
            index++;
        }

        return ret;
    }

    private static class RecipeInfo {

        int sampleOutputImage;
        int index;
        String name, desc;
        Class<? extends Item> item;

        public RecipeInfo(int sampleOutputImage, int index, Class<? extends Item> item) {
            this.sampleOutputImage = sampleOutputImage;
            this.item = item;
            this.index = index;
            this.name = Messages.get(item, "name");
            this.desc = Messages.get(item, "desc");
            if (this.desc.equals(Messages.NO_TEXT_FOUND)) this.desc = "";
        }

        public RecipeInfo(int sampleOutputImage, int index, String name, Class<? extends Item> item) {
            this.sampleOutputImage = sampleOutputImage;
            this.item = item;
            this.index = index;
            this.name = name;
            this.desc = item == null ? "" : Messages.get(item, "desc");
        }
    }

    public static List<RecipeInfo> getRecipes(int index) {
        List<RecipeInfo> result = new ArrayList<>();
        switch (index) {
            case 0:
            default:
                result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, 300, Messages.get(WndDisableRecipes.class, "potions"), null));
                result.add(new RecipeInfo(ItemSpriteSheet.STONE_HOLDER, 100, Messages.get(WndDisableRecipes.class, "runestones"), null));
                result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, 101, Messages.get(WndDisableRecipes.class, "exotic_potions"), null));
                result.add(new RecipeInfo(ItemSpriteSheet.SCROLL_HOLDER, 102, Messages.get(WndDisableRecipes.class, "exotic_scrolls"), null));
                return result;
            case 1:
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, 105, "1x " + Messages.get(StewedMeat.class, "name"), StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, 226, "2x " + Messages.get(StewedMeat.class, "name"), StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, 301, "3x " + Messages.get(StewedMeat.class, "name"), StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.MEAT_PIE, 302, MeatPie.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BLANDFRUIT, 200, Blandfruit.class));
                return result;
            case 2:
                result.add(new RecipeInfo(ItemSpriteSheet.BOMB, 201, Bomb.class));
                result.add(new RecipeInfo(ItemSpriteSheet.LIQUID_METAL, 0, LiquidMetal.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ARCANE_RESIN, 103, ArcaneResin.class));
                return result;
            case 3:
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_CAUSTIC, 211, CausticBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_BLIZZARD, 212, BlizzardBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_INFERNAL, 213, InfernalBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_SHOCKING, 214, ShockingBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_UNSTABLE, 202, UnstableBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_AQUA, 215, UnstableBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_HONEY, 209, ElixirOfHoneyedHealing.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_AQUA, 205, ElixirOfAquaticRejuvenation.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_MIGHT, 208, ElixirOfMight.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_DRAGON, 206, ElixirOfDragonsBlood.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_ICY, 207, ElixirOfIcyTouch.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_TOXIC, 210, ElixirOfToxicEssence.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_ARCANE, 204, ElixirOfArcaneArmor.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_FEATHER, 218, ElixirOfFeatherFall.class));
                return result;
            case 4:
                result.add(new RecipeInfo(ItemSpriteSheet.UNSTABLE_SPELL, 203, UnstableSpell.class));
                result.add(new RecipeInfo(ItemSpriteSheet.TELE_GRAB, 224, TelekineticGrab.class));
                result.add(new RecipeInfo(ItemSpriteSheet.PHASE_SHIFT, 220, PhaseShift.class));
                result.add(new RecipeInfo(ItemSpriteSheet.WILD_ENERGY, 223, WildEnergy.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RETURN_BEACON, 216, BeaconOfReturning.class));
                result.add(new RecipeInfo(ItemSpriteSheet.SUMMON_ELE, 225, SummonElemental.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RECLAIM_TRAP, 221, ReclaimTrap.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ALCHEMIZE, 104, Alchemize.class));
                result.add(new RecipeInfo(ItemSpriteSheet.MAGIC_INFUSE, 219, MagicalInfusion.class));
                result.add(new RecipeInfo(ItemSpriteSheet.CURSE_INFUSE, 217, CurseInfusion.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RECYCLE, 222, Recycle.class));
                result.add(new RecipeInfo(ItemSpriteSheet.WAND_YENDOR, 303, WandOfYendor.class));
                return result;

            case 5:
                for (Class c : Generator.Category.POTION.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, -1, c));
                }
                return result;
            case 6:
                for (Class c : Generator.Category.STONE.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.STONE_HOLDER, -1, c));
                }
                return result;
            case 7:
                for (Class c : Generator.Category.POTION.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, -1, ExoticPotion.regToExo.get(c)));
                }
                return result;
            case 8:
                for (Class c : Generator.Category.SCROLL.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.SCROLL_HOLDER, -1, ExoticScroll.regToExo.get(c)));
                }
                return result;
        }
    }
}