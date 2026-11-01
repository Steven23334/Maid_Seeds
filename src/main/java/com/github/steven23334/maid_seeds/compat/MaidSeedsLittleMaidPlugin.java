package com.github.steven23334.maid_seeds.compat;

import com.github.steven23334.maid_seeds.block.ModBlocks;
import com.github.steven23334.maid_seeds.item.ModItems;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.LittleMaidExtension;
import com.github.tartaricacid.touhoulittlemaid.entity.task.crop.SpecialCropManager;

/**
 * TLM 通过 {@link LittleMaidExtension} 注解扫描附属模组（AnnotatedInstanceUtil），
 * 不是 ServiceLoader，因此这个注解不能省，否则 registerSpecialCropHandler 永远不会被调用。
 */
@LittleMaidExtension
public class MaidSeedsLittleMaidPlugin implements ILittleMaid {

    @Override
    public void registerSpecialCropHandler(SpecialCropManager manager) {
        manager.add(ModItems.MAID_SEED.get(), ModBlocks.MAID_CROP_BLOCK.get(), new MaidCropHandler());
    }
}
