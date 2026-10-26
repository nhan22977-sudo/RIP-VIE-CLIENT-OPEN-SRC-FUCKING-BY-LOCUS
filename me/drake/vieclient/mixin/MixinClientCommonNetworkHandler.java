package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.donut.profiler.ActiveProbeCoordinator;
import me.drake.vieclient.modules.donut.profiler.DonutProfilerService;
import net.minecraft.class_8673;
import net.minecraft.class_9812;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_8673.class)
public class MixinClientCommonNetworkHandler {
   @Inject(method = "method_10839", at = @At("HEAD"))
   private void vie$endProfilerSession(class_9812 info, CallbackInfo ci) {
      ActiveProbeCoordinator.get().cancelAll();
      DonutProfilerService.get().endSession();
   }
}
