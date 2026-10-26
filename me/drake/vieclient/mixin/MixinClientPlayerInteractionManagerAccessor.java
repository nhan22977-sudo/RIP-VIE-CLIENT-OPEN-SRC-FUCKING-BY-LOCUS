package me.drake.vieclient.mixin;

import net.minecraft.class_2338;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_636.class)
public interface MixinClientPlayerInteractionManagerAccessor {
   @Accessor("field_3716")
   int getBlockBreakingCooldown();

   @Accessor("field_3716")
   void setBlockBreakingCooldown(int var1);

   @Accessor("field_3717")
   boolean getBreakingBlock();

   @Accessor("field_3714")
   class_2338 getCurrentBreakingPos();

   @Accessor("field_3715")
   float getCurrentBreakingProgress();

   @Invoker("method_2911")
   void invokeSyncSlot();
}
