package com.github.steven23334.maid_seeds.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModNetwork {

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                OpenMaidModelSelectorS2CPacket.TYPE,
                OpenMaidModelSelectorS2CPacket.STREAM_CODEC,
                OpenMaidModelSelectorS2CPacket::handle
        );
        registrar.playToServer(
                SetMaidModelSelectionC2SPacket.TYPE,
                SetMaidModelSelectionC2SPacket.STREAM_CODEC,
                SetMaidModelSelectionC2SPacket::handle
        );
    }
}