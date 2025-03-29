package com.shatteredpixel.shatteredpixeldungeon.editor.recipes;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.ChooseOneInCategoriesBody;
import com.shatteredpixel.shatteredpixeldungeon.editor.ui.WndChooseOneInCategories;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.PotionCocktail;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
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
                            if (r.recipeClass == null) {
                                if (value) Dungeon.customDungeon.blockedRecipeResults.add(r.item);
                                else Dungeon.customDungeon.blockedRecipeResults.remove(r.item);
                            } else {
                                if (value) Dungeon.customDungeon.blockedRecipes.add(r.recipeClass);
                                else Dungeon.customDungeon.blockedRecipes.remove(r.recipeClass);
                            }
                        }
                    };
                    if (r.recipeClass == null) {
                        ((CheckBox) btn).checked(Dungeon.customDungeon.blockedRecipeResults.contains(r.item));
                    } else {
                        ((CheckBox) btn).checked(Dungeon.customDungeon.blockedRecipes.contains(r.recipeClass));
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
        Class<? extends Recipe> recipeClass;
        String name, desc;
        Class<? extends Item> item;

        public RecipeInfo(int sampleOutputImage, Class<? extends Recipe> recipeClass, Class<? extends Item> item) {
            this.sampleOutputImage = sampleOutputImage;
            this.item = item;
            this.recipeClass = recipeClass;
            this.name = Messages.get(item, "name");
            this.desc = Messages.get(item, "desc");
            if (this.desc.equals(Messages.NO_TEXT_FOUND)) this.desc = "";
        }

        public RecipeInfo(int sampleOutputImage, String name, Class<? extends Recipe> recipeClass, Class<? extends Item> item) {
            this.sampleOutputImage = sampleOutputImage;
            this.item = item;
            this.recipeClass = recipeClass;
            this.name = name;
            this.desc = item == null ? "" : Messages.get(item, "desc");
        }
    }

    public static List<RecipeInfo> getRecipes(int index) {
        List<RecipeInfo> result = new ArrayList<>();
        switch (index) {
            case 0:
            default:
                result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, Messages.get(WndDisableRecipes.class, "potions"), Potion.SeedToPotion.class, null));
                result.add(new RecipeInfo(ItemSpriteSheet.STONE_HOLDER, Messages.get(WndDisableRecipes.class, "runestones"), Scroll.ScrollToStone.class, null));
                result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, Messages.get(WndDisableRecipes.class, "exotic_potions"), ExoticPotion.PotionToExotic.class, null));
                result.add(new RecipeInfo(ItemSpriteSheet.SCROLL_HOLDER, Messages.get(WndDisableRecipes.class, "exotic_scrolls"), ExoticScroll.ScrollToExotic.class, null));
                return result;
            case 1:
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, "1x " + Messages.get(StewedMeat.class, "name"), StewedMeat.oneMeat.class, StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, "2x " + Messages.get(StewedMeat.class, "name"), StewedMeat.twoMeat.class, StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.STEWED, "3x " + Messages.get(StewedMeat.class, "name"), StewedMeat.threeMeat.class, StewedMeat.class));
                result.add(new RecipeInfo(ItemSpriteSheet.MEAT_PIE, MeatPie.Recipe.class, MeatPie.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BLANDFRUIT, Blandfruit.CookFruit.class, Blandfruit.class));
                return result;
            case 2:
                result.add(new RecipeInfo(ItemSpriteSheet.BOMB, Bomb.EnhanceBomb.class, Bomb.class));
                result.add(new RecipeInfo(ItemSpriteSheet.LIQUID_METAL, LiquidMetal.Recipe.class, LiquidMetal.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ARCANE_RESIN, ArcaneResin.Recipe.class, ArcaneResin.class));
                return result;
            case 3:
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_CAUSTIC, CausticBrew.Recipe.class, CausticBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_BLIZZARD, BlizzardBrew.Recipe.class, BlizzardBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_INFERNAL, InfernalBrew.Recipe.class, InfernalBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_SHOCKING, ShockingBrew.Recipe.class, ShockingBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_UNSTABLE, UnstableBrew.Recipe.class, UnstableBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_COCKTAIL, PotionCocktail.Recipe.class, PotionCocktail.class));
                result.add(new RecipeInfo(ItemSpriteSheet.BREW_AQUA, AquaBrew.Recipe.class, AquaBrew.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_HONEY, ElixirOfHoneyedHealing.Recipe.class, ElixirOfHoneyedHealing.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_AQUA, ElixirOfAquaticRejuvenation.Recipe.class, ElixirOfAquaticRejuvenation.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_MIGHT, ElixirOfMight.Recipe.class, ElixirOfMight.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_DRAGON, ElixirOfDragonsBlood.Recipe.class, ElixirOfDragonsBlood.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_ICY, ElixirOfIcyTouch.Recipe.class, ElixirOfIcyTouch.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_TOXIC, ElixirOfToxicEssence.Recipe.class, ElixirOfToxicEssence.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_ARCANE, ElixirOfArcaneArmor.Recipe.class, ElixirOfArcaneArmor.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ELIXIR_FEATHER, ElixirOfFeatherFall.Recipe.class, ElixirOfFeatherFall.class));
                return result;
            case 4:
                result.add(new RecipeInfo(ItemSpriteSheet.UNSTABLE_SPELL, UnstableSpell.Recipe.class, UnstableSpell.class));
                result.add(new RecipeInfo(ItemSpriteSheet.TELE_GRAB, TelekineticGrab.Recipe.class, TelekineticGrab.class));
                result.add(new RecipeInfo(ItemSpriteSheet.PHASE_SHIFT, PhaseShift.Recipe.class, PhaseShift.class));
                result.add(new RecipeInfo(ItemSpriteSheet.WILD_ENERGY, WildEnergy.Recipe.class, WildEnergy.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RETURN_BEACON, BeaconOfReturning.Recipe.class, BeaconOfReturning.class));
                result.add(new RecipeInfo(ItemSpriteSheet.SUMMON_ELE, SummonElemental.Recipe.class, SummonElemental.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RECLAIM_TRAP, ReclaimTrap.Recipe.class, ReclaimTrap.class));
                result.add(new RecipeInfo(ItemSpriteSheet.ALCHEMIZE, Alchemize.Recipe.class, Alchemize.class));
                result.add(new RecipeInfo(ItemSpriteSheet.MAGIC_INFUSE, MagicalInfusion.Recipe.class, MagicalInfusion.class));
                result.add(new RecipeInfo(ItemSpriteSheet.CURSE_INFUSE, CurseInfusion.Recipe.class, CurseInfusion.class));
                result.add(new RecipeInfo(ItemSpriteSheet.RECYCLE, Recycle.Recipe.class, Recycle.class));
                result.add(new RecipeInfo(ItemSpriteSheet.WAND_YENDOR, WandOfYendor.Recipe.class, WandOfYendor.class));
                return result;

            case 5:
                for (Class c : Generator.Category.POTION.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, null, c));
                }
                return result;
            case 6:
                for (Class c : Generator.Category.STONE.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.STONE_HOLDER, null, c));
                }
                return result;
            case 7:
                for (Class c : Generator.Category.POTION.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.POTION_HOLDER, null, ExoticPotion.regToExo.get(c)));
                }
                return result;
            case 8:
                for (Class c : Generator.Category.SCROLL.classes){
                    result.add(new RecipeInfo(ItemSpriteSheet.SCROLL_HOLDER, null, ExoticScroll.regToExo.get(c)));
                }
                return result;
        }
    }
    
    public static Class<? extends Recipe> indexToRecipe(int index) {
        switch (index) {
            case 0: return LiquidMetal.Recipe.class;
            
            case 300: return Potion.SeedToPotion.class;
            case 100: return Scroll.ScrollToStone.class;
            case 101: return ExoticPotion.PotionToExotic.class;
            case 102: return ExoticScroll.ScrollToExotic.class;
            
            case 105: return StewedMeat.oneMeat.class;
            case 226: return StewedMeat.twoMeat.class;
            case 301: return StewedMeat.threeMeat.class;
            case 302: return MeatPie.Recipe.class;
            case 200: return Blandfruit.CookFruit.class;
            
            case 103: return ArcaneResin.Recipe.class;
            case 104: return Alchemize.Recipe.class;
            
            case 201: return Bomb.EnhanceBomb.class;
            case 202: return UnstableBrew.Recipe.class;
            case 203: return UnstableSpell.Recipe.class;
            case 204: return ElixirOfArcaneArmor.Recipe.class;
            case 205: return ElixirOfAquaticRejuvenation.Recipe.class;
            case 206: return ElixirOfDragonsBlood.Recipe.class;
            case 207: return ElixirOfIcyTouch.Recipe.class;
            case 208: return ElixirOfMight.Recipe.class;
            case 209: return ElixirOfHoneyedHealing.Recipe.class;
            case 210: return ElixirOfToxicEssence.Recipe.class;
            case 211: return CausticBrew.Recipe.class;
            case 212: return BlizzardBrew.Recipe.class;
            case 213: return InfernalBrew.Recipe.class;
            case 214: return ShockingBrew.Recipe.class;
            case 215: return AquaBrew.Recipe.class;
            case 218: return ElixirOfFeatherFall.Recipe.class;
            
            case 216: return BeaconOfReturning.Recipe.class;
            case 217: return CurseInfusion.Recipe.class;
            case 219: return MagicalInfusion.Recipe.class;
            case 220: return PhaseShift.Recipe.class;
            case 221: return ReclaimTrap.Recipe.class;
            case 222: return Recycle.Recipe.class;
            case 223: return WildEnergy.Recipe.class;
            case 224: return TelekineticGrab.Recipe.class;
            case 225: return SummonElemental.Recipe.class;
            
            case 303: return WandOfYendor.Recipe.class;
        }
        return Recipe.class;
    }
}
