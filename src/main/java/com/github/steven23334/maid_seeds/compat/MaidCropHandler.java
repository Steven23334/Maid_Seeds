package com.github.steven23334.maid_seeds.compat;

import com.github.steven23334.maid_seeds.block.MaidCropBlock;
import com.github.steven23334.maid_seeds.block.MaidCropBlockEntity;
import com.github.steven23334.maid_seeds.item.MaidModelSelectionData;
import com.github.tartaricacid.touhoulittlemaid.api.task.ISpecialCropHandler;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 让女仆的农田任务能够种植和收获 {@link MaidCropBlock}。
 * <p>
 * 方法签名必须与 {@link ISpecialCropHandler} 保持一致：所有坐标都是作物所在的方块位置，
 * 种植时传入的 basePos 则是作物下方的耕地位置。
 */
public class MaidCropHandler implements ISpecialCropHandler {

    @Override
    public boolean canHarvest(EntityMaid maid, BlockPos cropPos, BlockState cropState) {
        return cropState.getBlock() instanceof MaidCropBlock crop && crop.isMaxAge(cropState);
    }

    @Override
    public void harvest(EntityMaid maid, BlockPos cropPos, BlockState cropState, boolean isDestroyMode) {
        if (!(maid.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!(cropState.getBlock() instanceof MaidCropBlock)) {
            return;
        }

        // 先取出作物上记录的模型 ID，方块被破坏后方块实体就没了
        BlockEntity blockEntity = cropState.hasBlockEntity() ? serverLevel.getBlockEntity(cropPos) : null;
        String modelId = blockEntity instanceof MaidCropBlockEntity cropEntity ? cropEntity.getModelID() : null;

        // 不区分是否手持锄头：直接破坏作物。
        // destroyBlock 会按原战利品表把种子等掉落物塞进女仆背包（放不下才掉在原地）
        if (!maid.destroyBlock(cropPos)) {
            return;
        }

        // 收获成熟作物后生成女仆
        spawnMaid(serverLevel, cropPos, modelId);
    }

    @Override
    public boolean canPlant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        if (!(seed.getItem() instanceof ItemNameBlockItem blockItem)) {
            return false;
        }
        if (!(blockItem.getBlock() instanceof MaidCropBlock)) {
            return false;
        }

        Level level = maid.level();
        BlockPos cropPos = basePos.above();
        BlockState aboveState = level.getBlockState(cropPos);
        if (!aboveState.canBeReplaced() || !aboveState.getFluidState().isEmpty()) {
            return false;
        }
        // 使用作物自身的生存条件判断（耕地、光照等）
        return blockItem.getBlock().defaultBlockState().canSurvive(level, cropPos);
    }

    @Override
    public ItemStack plant(EntityMaid maid, BlockPos basePos, BlockState baseState, ItemStack seed) {
        if (!canPlant(maid, basePos, baseState, seed)) {
            return seed;
        }
        if (!(seed.getItem() instanceof ItemNameBlockItem)) {
            return seed;
        }

        Level level = maid.level();
        BlockPos cropPos = basePos.above();

        // 走 BlockItem 的标准放置流程（会触发 getStateForPlacement / onPlace），
        // 放置成功后 BlockItem.place 会自动消耗一颗种子。
        if (!maid.placeItemBlock(cropPos, seed)) {
            return seed;
        }

        // 女仆种下的作物，模型跟随女仆主人（玩家）在模型选择器中的选择
        if (level.getBlockEntity(cropPos) instanceof MaidCropBlockEntity cropEntity) {
            cropEntity.setModelID(pickPlayerModelId(maid, level));
        }
        return seed;
    }

    /**
     * 从女仆主人的模型选择池里随机取一个；没有主人/没有选择时退回全服随机模型。
     */
    private static String pickPlayerModelId(EntityMaid maid, Level level) {
        if (maid.getOwner() instanceof ServerPlayer owner) {
            List<String> pool = MaidModelSelectionData.get(owner).getSelectedModels(owner);
            if (!pool.isEmpty()) {
                return pool.get(level.getRandom().nextInt(pool.size()));
            }
        }
        return MaidCropBlock.randomID(level);
    }

    private static void spawnMaid(ServerLevel level, BlockPos pos, @Nullable String modelId) {
        EntityMaid maid = new EntityMaid(level);
        if (modelId == null || modelId.isEmpty()) {
            modelId = MaidCropBlock.randomID(level);
        }
        maid.setModelId(modelId);
        maid.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
        level.addFreshEntity(maid);
    }
}
