/*package net.jens.cookingmod.block.entity;

import net.jens.cookingmod.block.custom.CookingPotBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CookingPotBlockEntity extends BlockEntity {
    private List<Item> ingredients = new ArrayList<>();
    public CookingPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public void addIngredient(Item ingredient, Level world, BlockPos pos) {
        if (this.ingredients.size() < 5) {
            this.ingredients.add(ingredient);

            BlockState state = world.getBlockState(pos);
            int currentLevel = state.getValue(CookingPotBlock.LEVEL);
            if (currentLevel < 5) {
                world.setBlock(pos, state.setValue(CookingPotBlock.LEVEL, currentLevel + 1), 3);
            }
        }
    }
    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        // Serialize the ingredients list to NBT
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        // Deserialize the ingredients list from NBT
    }
}*/