package me.drake.vieclient.mixin;

import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_636.class)
public interface ClientPlayerInteractionManagerAccessor {
   @Invoker("method_2911")
   void syncSlot();
}
