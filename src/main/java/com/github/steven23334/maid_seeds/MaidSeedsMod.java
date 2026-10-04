package com.github.steven23334.maid_seeds;

import com.github.steven23334.maid_seeds.block.ModBlocks;
import com.github.steven23334.maid_seeds.item.ModItems;
import com.github.steven23334.maid_seeds.network.ModNetwork;
import com.github.steven23334.steven_mod_api.ApiTabContributors;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(MaidSeedsMod.MOD_ID)
public class MaidSeedsMod {
    public static final String MOD_ID = "maid_seeds";

    public MaidSeedsMod(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);

        // 手动把网络包注册到 mod event bus 上
        modEventBus.addListener(ModNetwork::register);

        // 只把女仆种子 / 模型选择器贡献给 Steven's Mod API 标签页
        ApiTabContributors.register(() -> List.of(
                new ItemStack(ModItems.MAID_SEED.get()),
                new ItemStack(ModItems.MAID_MODEL_SELECTOR.get())
        ));
    }
}