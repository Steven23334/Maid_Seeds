package com.github.steven23334.maid_seeds.item;

import com.github.steven23334.maid_seeds.MaidSeedsMod;
import com.github.steven23334.maid_seeds.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, MaidSeedsMod.MOD_ID);

    public static final Supplier<Item> MAID_SEED = ITEMS.register("maid_seed",
            () -> new ItemNameBlockItem(
                    ModBlocks.MAID_CROP_BLOCK.get(),
                    new Item.Properties().rarity(Rarity.RARE)
            ));

    public static final Supplier<Item> FREE_PHOTO = ITEMS.register("free_photo",
            ItemFreePhoto::new);

    public static final Supplier<Item> MAID_MODEL_SELECTOR = ITEMS.register("maid_model_selector",
            () -> new MaidModelSelectorItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
}