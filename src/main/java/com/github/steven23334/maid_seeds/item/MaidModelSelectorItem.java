package com.github.steven23334.maid_seeds.item;

import com.github.steven23334.maid_seeds.network.OpenMaidModelSelectorS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public final class MaidModelSelectorItem extends Item {

    public MaidModelSelectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level,
                                                           @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            var data = MaidModelSelectionData.get(serverPlayer);
            PacketDistributor.sendToPlayer(serverPlayer,
                    new OpenMaidModelSelectorS2CPacket(data.getSelectedModels(serverPlayer)));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}