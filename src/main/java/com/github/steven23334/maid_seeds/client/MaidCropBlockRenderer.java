package com.github.steven23334.maid_seeds.client;

import com.github.steven23334.maid_seeds.block.MaidCropBlockEntity;
import com.github.tartaricacid.touhoulittlemaid.api.client.render.MaidRenderState;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

import static com.github.tartaricacid.touhoulittlemaid.util.EntityCacheUtil.clearMaidDataResidue;

public class MaidCropBlockRenderer implements BlockEntityRenderer<MaidCropBlockEntity> {

    public MaidCropBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull MaidCropBlockEntity blockEntity, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        renderEntity(poseStack, buffer, blockEntity, packedLight);
    }

    private void renderEntity(PoseStack poseStack, MultiBufferSource buffer,
                              MaidCropBlockEntity blockEntity, int light) {

        String modelId = blockEntity.getModelID();
        if (modelId == null || modelId.isEmpty()) return;

        var level = blockEntity.getLevel();
        if (level == null) return;

        var state = blockEntity.getBlockState();
        if (!(state.getBlock() instanceof CropBlock crop)) return;

        int age = crop.getAge(state);
        int maxAge = crop.getMaxAge();
        float progress = maxAge <= 0 ? 1f : age / (float) maxAge;

        long posId = blockEntity.getBlockPos().asLong();
        EntityMaid maid;
        try {
            maid = EntityCacheUtil.STATUE_CACHE.get(posId, () -> new EntityMaid(level));
        } catch (ExecutionException e) {
            return;
        }

        // 每帧重置数据，保证姿势永远是默认的
        clearMaidDataResidue(maid, true);
        maid.renderState = MaidRenderState.GARAGE_KIT;
        //maid.tickCount = 0;
        maid.setModelId(modelId);

        poseStack.pushPose();

        poseStack.translate(0.5, 0, 0.5);
        poseStack.translate(0, -1.2 + progress * 1.1, 0);

        switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
            case EAST  -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            case WEST  -> poseStack.mulPose(Axis.YP.rotationDegrees(270));
            case NORTH -> poseStack.mulPose(Axis.YP.rotationDegrees(180));
            case SOUTH -> { }
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        boolean oldHitbox = dispatcher.shouldRenderHitBoxes();
        dispatcher.setRenderHitBoxes(false);

        dispatcher.render(maid, 0, 0, 0, 0, 0,
                poseStack, buffer, light);

        dispatcher.setRenderHitBoxes(oldHitbox);
        poseStack.popPose();
    }
}