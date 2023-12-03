package net.jens.cookingmod.block.entity;

import net.jens.cookingmod.CookingMod;
import net.jens.cookingmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CookingMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<CookingPotBlockEntity>> COOKING_POT_BE =
            BLOCK_ENTITIES.register("cooking_pot_be", () ->
                    BlockEntityType.Builder.of(CookingPotBlockEntity::new,
                            ModBlocks.COOKING_POT.get()).build(null));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
