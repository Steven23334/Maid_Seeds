package com.github.steven23334.maid_seeds.block;

import com.github.steven23334.maid_seeds.MaidSeedsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, MaidSeedsMod.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MaidSeedsMod.MOD_ID);

    public static final DeferredHolder<Block, MaidCropBlock> MAID_CROP_BLOCK =
            BLOCKS.register("maid_crop", r -> new MaidCropBlock());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MaidCropBlockEntity>> MAID_CROP_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("maid_crop", r ->
                    BlockEntityType.Builder.of(MaidCropBlockEntity::new, MAID_CROP_BLOCK.get()).build(null));
}