package com.github.steven23334.maid_seeds.client;

import com.github.steven23334.maid_seeds.client.gui.MaidModelSelectorScreen;

import java.util.List;

public final class ClientPacketHandler {
    public static void openMaidModelSelector(List<String> currentModelIds) {
        MaidModelSelectorScreen.open(currentModelIds);
    }
}