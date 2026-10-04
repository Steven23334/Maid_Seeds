package com.github.steven23334.maid_seeds.network;

import com.github.steven23334.maid_seeds.MaidSeedsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record OpenMaidModelSelectorS2CPacket(List<String> currentModelIds) implements CustomPacketPayload {

    public static final Type<OpenMaidModelSelectorS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MaidSeedsMod.MOD_ID, "open_maid_model_selector"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenMaidModelSelectorS2CPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.collection(java.util.ArrayList::new, ByteBufCodecs.STRING_UTF8),
                    OpenMaidModelSelectorS2CPacket::currentModelIds,
                    OpenMaidModelSelectorS2CPacket::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenMaidModelSelectorS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() ->
                com.github.steven23334.maid_seeds.client.ClientPacketHandler
                        .openMaidModelSelector(packet.currentModelIds()));
    }
}