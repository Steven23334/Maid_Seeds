package com.github.steven23334.maid_seeds.client;

import com.github.steven23334.maid_seeds.MaidSeedsMod;
import com.github.steven23334.maid_seeds.block.ModBlocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MaidSeedsMod.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlocks.MAID_CROP_BLOCK_ENTITY.get(),
                MaidCropBlockRenderer::new
        );
    }
}