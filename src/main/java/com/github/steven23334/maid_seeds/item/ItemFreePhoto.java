package com.github.steven23334.maid_seeds.item;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitDataComponent;
import com.github.tartaricacid.touhoulittlemaid.item.ItemPhoto;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemFreePhoto extends ItemPhoto {
    public ItemFreePhoto() {
        super();
    }

    @Override
    public @NotNull String getDescriptionId() {
        return "item.touhou_little_maid.photo";
    }

    /**
     * ⚠️ 关键修复：不要在 else 分支里调用 super.useOn(context)。
     * 因为 ItemPhoto.useOn() 内部会回调 spawnFromStore()，
     * 于是形成 useOn → spawnFromStore → useOn → spawnFromStore ... 无限递归，
     * 最终在 EntityMaid 构造时 StackOverflowError 崩溃。
     */
    @Override
    public @NotNull InteractionResult spawnFromStore(@NotNull UseOnContext context,
                                                     @NotNull Player player,
                                                     @NotNull Level worldIn,
                                                     @NotNull EntityMaid maid,
                                                     @NotNull Runnable runnable) {
        ItemStack stack = context.getItemInHand();
        CustomData compoundData = stack.get(InitDataComponent.MAID_INFO);
        if (compoundData != null) {
            CompoundTag maidCompound = compoundData.copyTag();

            var event = new MaidAndItemTransformEvent.ToMaid(maid, stack, maidCompound);
            NeoForge.EVENT_BUS.post(event);

            maid.load(maidCompound);
            maid.moveTo(context.getClickedPos().above(), 0, 0);
            if (worldIn instanceof ServerLevel) {
                worldIn.addFreshEntity(maid);
            }
            maid.spawnExplosionParticle();
            maid.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);
            runnable.run();
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        } else {
            if (worldIn.isClientSide) {
                player.sendSystemMessage(Component.translatable(
                        "message.touhou_little_maid.photo.have_no_nbt_data"));
            }
            // 修复：直接返回 FAIL，不要 super.useOn(context)
            return InteractionResult.FAIL;
        }
    }

    @Override
    public boolean hasCustomEntity(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(@NotNull Level level,
                                         @NotNull Entity location,
                                         @NotNull ItemStack stack) {
        var maid = new EntityMaid(level);
        CustomData compoundData = stack.get(InitDataComponent.MAID_INFO);
        if (compoundData == null) return maid;
        CompoundTag maidCompound = compoundData.copyTag();
        var event = new MaidAndItemTransformEvent.ToMaid(maid, stack, maidCompound);
        NeoForge.EVENT_BUS.post(event);
        maid.load(maidCompound);
        maid.setPos(location.position());
        return maid;
    }
}