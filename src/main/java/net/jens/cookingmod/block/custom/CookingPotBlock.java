package net.jens.cookingmod.block.custom;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.jens.cookingmod.block.entity.CookingPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
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

import java.util.Optional;

public class CookingPotBlock extends BaseEntityBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 5); // Define levels for your cooking pot
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 12, 14);
    public static final Object2FloatMap<ItemLike> INGREDIENTS = new Object2FloatOpenHashMap<>(); // Define ingredients and their properties

    static {
        INGREDIENTS.put(Items.CARROT, 1.0F);
        INGREDIENTS.put(Items.POTATO, 1.0F);
        INGREDIENTS.put(Items.WHEAT, 1.0F);
        INGREDIENTS.put(Items.PUMPKIN, 1.0F);
        INGREDIENTS.put(Items.SUGAR, 1.0F);
        INGREDIENTS.put(Items.BROWN_MUSHROOM, 1.0F);
        INGREDIENTS.put(Items.EGG, 1.0F);
        INGREDIENTS.put(Items.SALMON, 1.0F);
        INGREDIENTS.put(Items.BEEF, 1.0F);
        INGREDIENTS.put(Items.HONEY_BOTTLE, 1.0F);
        INGREDIENTS.put(Items.MILK_BUCKET, 1.0F);
        INGREDIENTS.put(Items.APPLE, 1.0F);
    }
    public CookingPotBlock(Properties pProperties) {

        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
    }
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Early return if the item in hand is not a valid ingredient
        if (!INGREDIENTS.containsKey(itemStack.getItem())) {
            return InteractionResult.PASS;
        }

        return processInteraction(world, pos, player, itemStack, state);
    }

    private InteractionResult processInteraction(Level world, BlockPos pos, Player player, ItemStack itemStack, BlockState state) {
        Optional<CookingPotBlockEntity> maybePot = getPotEntity(world, pos);
        if (maybePot.isPresent()) {
            if (!world.isClientSide) {
                return handleServerInteraction(maybePot.get(), player, itemStack, world, pos, state);
            } else {
                playParticleEffect(world, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private Optional<CookingPotBlockEntity> getPotEntity(Level world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof CookingPotBlockEntity ? Optional.of((CookingPotBlockEntity) blockEntity) : Optional.empty();
    }

    private InteractionResult handleServerInteraction(CookingPotBlockEntity cookingPot, Player player, ItemStack itemStack, Level world, BlockPos pos, BlockState state) {
        ItemStackHandler inventory = cookingPot.getInventory();

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                inventory.setStackInSlot(i, new ItemStack(itemStack.getItem(), 1));
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                updateCookingPotState(cookingPot, world, pos, state);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    private void updateCookingPotState(CookingPotBlockEntity cookingPot, Level world, BlockPos pos, BlockState state) {
        cookingPot.updateLevel();
        int newLevel = cookingPot.getStoredLevel();
        world.setBlock(pos, state.setValue(LEVEL, newLevel), 3);
        playSoundEffect(world, pos, newLevel);
        cookingPot.setChanged();
    }
    private void playParticleEffect(Level world, BlockPos pos) {
        double d0 = 0.5D + 0.03125D; // Adjust the height as needed for your block
        RandomSource randomsource = world.getRandom();

        for (int i = 0; i < 5; ++i) {
            double d3 = randomsource.nextGaussian() * 0.02D;
            double d4 = randomsource.nextGaussian() * 0.02D;
            double d5 = randomsource.nextGaussian() * 0.02D;
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.13125D + 0.7375D * randomsource.nextFloat(), pos.getY() + d0 + randomsource.nextFloat() * (1.0D - d0), pos.getZ() + 0.13125D + 0.7375D * randomsource.nextFloat(), d3, d4, d5);
        }
        world.addParticle(ParticleTypes.COMPOSTER, pos.getX() + 0.13125D + 0.7375D * randomsource.nextFloat(), pos.getY() + d0 + randomsource.nextFloat() * (1.0D - d0), pos.getZ() + 0.13125D + 0.7375D * randomsource.nextFloat(), 0.02, 0.02, 0.02);
    }
    private void playSoundEffect(Level world, BlockPos pos, int currentLevel) {
        float pitch = 1.0F + 0.5F * currentLevel;
        world.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 3.0F, pitch);
        world.playSound(null, pos, SoundEvents.BUCKET_EMPTY_TADPOLE, SoundSource.BLOCKS, 1.0F, pitch);
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
