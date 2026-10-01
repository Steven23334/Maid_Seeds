package com.github.steven23334.maid_seeds.block;

import com.github.steven23334.maid_seeds.item.ModItems;
import com.github.tartaricacid.touhoulittlemaid.entity.info.ServerCustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;

public class MaidCropBlock extends CropBlock implements EntityBlock {
    public MaidCropBlock() {
        super(Properties.ofFullCopy(Blocks.WHEAT));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new MaidCropBlockEntity(blockPos, blockState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.MAID_SEED.get();
    }

    // 成熟时直接生成女仆，不再掉落照片
    @Override
    public void playerDestroy(@NotNull Level level, @NotNull Player player, @NotNull BlockPos pos,
                              @NotNull BlockState state, @Nullable BlockEntity blockEntity, @NotNull ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (level.isClientSide) return;
        if (state.getValue(CropBlock.AGE) != getMaxAge()) return;

        String modelId;
        if (blockEntity instanceof MaidCropBlockEntity cropEntity && cropEntity.getModelID() != null) {
            modelId = cropEntity.getModelID();
        } else {
            modelId = randomID(level);
        }

        EntityMaid maid = new EntityMaid(level);
        maid.setModelId(modelId);
        maid.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        level.addFreshEntity(maid);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (level.getRawBrightness(pos, 0) < 9) return;
        int age = getAge(state);
        if (age >= getMaxAge()) return;
        if (random.nextInt((int) (25.0F / getGrowthSpeed(state, level, pos)) + 1) != 0) return;

        BlockState next = getStateForAge(age + 1).setValue(FACING, state.getValue(FACING));
        level.setBlock(pos, next, 2);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                           @NotNull BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide
                && state.getBlock() != oldState.getBlock()
                && !movedByPiston
                && level.getBlockEntity(pos) instanceof MaidCropBlockEntity blockEntity) {
            blockEntity.setModelID(randomID(level));
        }
    }

    public static String randomID(@Nullable Level level) {
        if (level == null) return MaidCropBlockEntity.DEFAULT_MODEL_ID;
        var count = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelSize();
        if (count <= 0) return MaidCropBlockEntity.DEFAULT_MODEL_ID;
        int skipRandom = level.getRandom().nextInt(count);
        Optional<String> modelId = ServerCustomPackLoader.SERVER_MAID_MODELS.getModelIdSet().stream()
                .skip(skipRandom).findFirst();
        return modelId.orElse(MaidCropBlockEntity.DEFAULT_MODEL_ID);
    }

    @Override
    public void growCrops(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        if (i > j) i = j;
        level.setBlock(pos, this.getStateForAge(i).setValue(FACING, state.getValue(FACING)), 2);
    }
}