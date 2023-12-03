package net.jens.cookingmod.recipe;

import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Recipe {
    private Map<Item, Integer> ingredients;
    private ItemStack result;

    public Recipe(Map<Item, Integer> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public Map<Item, Integer> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }
}