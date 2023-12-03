package net.jens.cookingmod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import javax.management.openmbean.CompositeData;

public class CookingPotBlockEntity extends BlockEntity {
    private int level = 0;  // Level field to keep track of items added
    private final ItemStackHandler itemHandler = new ItemStackHandler(5){ // 5 slots
    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        updateLevel(); // Update level when contents change
    }
};
    public CookingPotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.COOKING_POT_BE.get(), pPos, pBlockState);
    }
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
        level = tag.getInt("Level"); // Load level from NBT
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("Level", level); // Save level to NBT
    }

    public ItemStackHandler getInventory() {
        return itemHandler;
    }
    private void updateLevel() {
        level = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                level++; // Increase level for each non-empty slot
            }
        }
        setChanged(); // Notify that the block entity's data has changed
    }

    public int getStoredLevel() {
        return level;
    }
}