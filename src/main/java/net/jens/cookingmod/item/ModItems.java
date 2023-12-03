package net.jens.cookingmod.item;

import net.jens.cookingmod.CookingMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CookingMod.MOD_ID);

    public static final RegistryObject<Item> SALTED_BUTTER = ITEMS.register( "salted_butter",
            () -> new Item(new Item.Properties().food(ModFoods.SALTED_BUTTER)));
    public static final RegistryObject<Item> GOLDEN_HARVEST_LOAF = ITEMS.register( "golden_harvest_loaf",
            () -> new Item(new Item.Properties().food(ModFoods.GOLDEN_HARVEST_LOAF)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
