package com.github.steven23334.maid_seeds.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class MaidModelSelectionData extends SavedData {
    public static final String DATA_NAME = "maid_seeds_model_selection";

    private final Map<UUID, List<String>> selections = new HashMap<>();

    public static MaidModelSelectionData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MaidModelSelectionData::new,
                        MaidModelSelectionData::load, null),
                DATA_NAME);
    }

    public static MaidModelSelectionData get(Player player) {
        return get(player.getServer());
    }

    public static MaidModelSelectionData load(CompoundTag tag, HolderLookup.Provider provider) {
        MaidModelSelectionData data = new MaidModelSelectionData();
        CompoundTag map = tag.getCompound("Selections");
        for (String key : map.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                ListTag list = map.getList(key, Tag.TAG_STRING);
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    ids.add(list.getString(i));
                }
                if (!ids.isEmpty()) {
                    data.selections.put(uuid, ids);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        CompoundTag map = new CompoundTag();
        selections.forEach((uuid, ids) -> {
            ListTag list = new ListTag();
            ids.forEach(id -> list.add(StringTag.valueOf(id)));
            map.put(uuid.toString(), list);
        });
        tag.put("Selections", map);
        return tag;
    }

    /** 返回该玩家选中的所有模型 ID，可能为空列表 */
    public @NotNull List<String> getSelectedModels(Player player) {
        return List.copyOf(selections.getOrDefault(player.getUUID(), List.of()));
    }

    @Nullable
    public String getSelectedModel(Player player) {
        List<String> ids = selections.get(player.getUUID());
        return (ids == null || ids.isEmpty()) ? null : ids.getFirst();
    }

    public void setSelectedModels(Player player, List<String> modelIds) {
        Set<String> distinct = new LinkedHashSet<>(modelIds);
        if (distinct.isEmpty()) {
            selections.remove(player.getUUID());
        } else {
            selections.put(player.getUUID(), List.copyOf(distinct));
        }
        setDirty();
    }
}