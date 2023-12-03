package net.jens.cookingmod.block.custom;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
//import net.jens.cookingmod.block.entity.CookingPotBlockEntity;
import net.jens.cookingmod.block.entity.CookingPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CookingPotBlock extends BaseEntityBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5); // Define levels for your cooking pot
    public static final Object2FloatMap<ItemLike> INGREDIENTS = new Object2FloatOpenHashMap<>(); // Define ingredients and their properties

    public CookingPotBlock(Properties pProperties) {

        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
        INGREDIENTS.put(Items.CARROT, 1.0F); // Assuming each carrot adds one layer
        INGREDIENTS.put(Items.POTATO, 1.0F); // Assuming each carrot adds one layer
    }
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check if the item in the player's hand is a valid ingredient
        if (INGREDIENTS.containsKey(itemStack.getItem())) {
            int currentLevel = state.getValue(LEVEL);
            BlockEntity be = world.getBlockEntity(pos);

            // Check if the cooking pot is not already full (assuming a max level of 5)
            if (currentLevel < 5) {
                if (!world.isClientSide) {
                    // Retrieve our cooking pot block entity
                    if (be instanceof CookingPotBlockEntity) {
                        CookingPotBlockEntity cookingPot = (CookingPotBlockEntity) be;
                        int level = cookingPot.getStoredLevel(); // Access the level
                        ItemStackHandler items = cookingPot.getInventory();

                        // Find an empty slot in the cooking pot's inventory
                        for (int i = 0; i < items.getSlots(); i++) {
                            if (items.getStackInSlot(i).isEmpty()) {
                                // Add the item to the cooking pot's inventory
                                items.setStackInSlot(i, new ItemStack(itemStack.getItem(), 1));

                                // Shrink the player's item stack by one
                                if (!player.getAbilities().instabuild) {
                                    itemStack.shrink(1);
                                }

                                // Update the block state to reflect the new level
                                world.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), 3);

                                // Play sound effect and any other effects
                                playSoundEffect(world, pos, currentLevel);

                                // Mark the block entity as changed
                                cookingPot.setChanged();

                                // Return success
                                return InteractionResult.sidedSuccess(world.isClientSide);
                            }
                        }
                    }
                } else {
                    // Client-side particle effect and hand animation
                    playComposterParticleEffect(world, pos);
                }
            }
        }
        return InteractionResult.PASS;
    }

    private void playComposterParticleEffect(Level world, BlockPos pos) {
        double d0 = 0.5D + 0.03125D; // Adjust the height as needed for your block
        RandomSource randomsource = world.getRandom();

        for (int i = 0; i < 5; ++i) {
            double d3 = randomsource.nextGaussian() * 0.02D;
            double d4 = randomsource.nextGaussian() * 0.02D;
            double d5 = randomsource.nextGaussian() * 0.02D;
            world.addParticle(ParticleTypes.COMPOSTER, pos.getX() + 0.13125D + 0.7375D * randomsource.nextFloat(), pos.getY() + d0 + randomsource.nextFloat() * (1.0D - d0), pos.getZ() + 0.13125D + 0.7375D * randomsource.nextFloat(), d3, d4, d5);
        }
    }
    private void playSoundEffect(Level world, BlockPos pos, int currentLevel) {
        float pitch = 1.0F + 0.5F * currentLevel;
        world.playSound(null, pos, SoundEvents.SNIFFER_EGG_PLOP, SoundSource.BLOCKS, 1.0F, pitch);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LEVEL);
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CookingPotBlockEntity(pPos, pState);
    }
    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CookingPotBlockEntity) {
                ItemStackHandler itemHandler = ((CookingPotBlockEntity) blockEntity).getInventory();
                for (int i = 0; i < itemHandler.getSlots(); ++i) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        popResource(world, pos, stack); // Drop the item in the world
                    }
                }
                world.updateNeighbourForOutputSignal(pos, this); // Update redstone signal
            }
            super.onRemove(oldState, world, pos, newState, isMoving);
        }
    }
}
