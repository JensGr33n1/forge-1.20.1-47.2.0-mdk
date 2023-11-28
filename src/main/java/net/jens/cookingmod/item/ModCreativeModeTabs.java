package net.jens.cookingmod.item;

import net.jens.cookingmod.CookingMod;
import net.jens.cookingmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CookingMod.MOD_ID);


    public static final RegistryObject<CreativeModeTab> COOKING_TAB = CREATIVE_MODE_TABS.register("cooking_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SALTED_BUTTER.get()))
                    .title(Component.translatable("creativetab.cooking_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SALTED_BUTTER.get());
                        pOutput.accept(ModBlocks.COOKING_POT.get());
                    })
                    .build());

    public static void register(IEventBus eventBus)
    {CREATIVE_MODE_TABS.register(eventBus);}
}
