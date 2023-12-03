package net.jens.cookingmod.block.entity;

import net.jens.cookingmod.block.custom.CookingPotBlock;
import net.jens.cookingmod.recipe.RecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import java.util.stream.IntStream;

public class CookingPotBlockEntity extends BlockEntity  {
    private int level = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            updateLevel();
        }
    };

    public CookingPotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COOKING_POT_BE.get(), pPos, pBlockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.contains("Inventory") ? tag.getCompound("Inventory") : new CompoundTag());
        level = tag.getInt("Level");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("Level", level);
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }

    public void updateLevel() {
        level = (int) IntStream.range(0, itemHandler.getSlots())
                .filter(i -> !itemHandler.getStackInSlot(i).isEmpty())
                .count();
        if (level == 5) {
            produceDish();
        } else {
            updateBlockState();
        }
    }

    private void produceDish() {
        ItemStack result = RecipeManager.getMatchingRecipeOutput(itemHandler);
        if (!result.isEmpty()) {
            dropItem(result);

            resetCookingPot();
        }
    }

    private void dropItem(ItemStack itemStack) {
        Level world = getLevel();
        if (world != null && !world.isClientSide) {
            double x = this.worldPosition.getX() + 0.5;
            double y = this.worldPosition.getY() + 1;
            double z = this.worldPosition.getZ() + 0.5;

            ItemEntity itemEntity = new ItemEntity(world, x, y, z, itemStack);
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
    }

    private void resetCookingPot() {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        level = 0;
        updateBlockState();
    }

    private void updateBlockState() {
        Level world = getLevel();
        if (world != null && !world.isClientSide) {
            BlockState state = world.getBlockState(this.worldPosition);
            if (state.getBlock() instanceof CookingPotBlock) {
                world.setBlock(this.worldPosition, state.setValue(CookingPotBlock.LEVEL, level), 3);
            }
        }
        setChanged();
    }

    public int getStoredLevel() {
        return level;
    }
}