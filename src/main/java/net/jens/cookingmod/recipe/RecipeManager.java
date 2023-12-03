package net.jens.cookingmod.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jens.cookingmod.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;

public class RecipeManager {
    private static List<Recipe> recipes = new ArrayList<>();

    static {
        // Initialize recipes here
        recipes.add(new Recipe(Map.of(Items.EGG, 2, Items.WHEAT, 2, Items.MILK_BUCKET, 1), new ItemStack(Items.CAKE)));
        recipes.add(new Recipe(Map.of(Items.WHEAT, 5), new ItemStack(ModItems.GOLDEN_HARVEST_LOAF.get())));
        recipes.add(new Recipe(Map.of(Items.EGG, 5), new ItemStack(Items.SNIFFER_EGG)));
        // Add more recipes as needed
    }

    public static ItemStack getMatchingRecipeOutput(ItemStackHandler inventory) {
        for (Recipe recipe : recipes) {
            if (matches(recipe, inventory)) {
                return recipe.getResult();
            }
        }
        return ItemStack.EMPTY;
    }

    private static boolean matches(Recipe recipe, ItemStackHandler inventory) {
        Map<Item, Integer> ingredientsNeeded = new HashMap<>(recipe.getIngredients());

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            ingredientsNeeded.computeIfPresent(stack.getItem(), (item, count) -> count - stack.getCount());
        }

        return ingredientsNeeded.values().stream().allMatch(count -> count <= 0);
    }
}