package com.github.steven23334.maid_seeds.network;

import com.github.steven23334.maid_seeds.MaidSeedsMod;
import com.github.steven23334.maid_seeds.item.MaidModelSelectionData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SetMaidModelSelectionC2SPacket(List<String> modelIds) implements CustomPacketPayload {

    public static final Type<SetMaidModelSelectionC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MaidSeedsMod.MOD_ID, "set_maid_model_selection"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetMaidModelSelectionC2SPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(java.util.ArrayList::new, ByteBufCodecs.STRING_UTF8),
                    SetMaidModelSelectionC2SPacket::modelIds,
                    SetMaidModelSelectionC2SPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetMaidModelSelectionC2SPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            MaidModelSelectionData.get(player).setSelectedModels(player, packet.modelIds());
        });
    }
}