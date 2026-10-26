package me.drake.vieclient.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_761.class)
public interface WorldRendererAccessor {
   @Accessor("field_4058")
   Int2ObjectMap getBlockBreakingInfos();
}
