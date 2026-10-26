package me.drake.vieclient.mixin;

import java.util.EnumSet;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import net.minecraft.class_2350;
import net.minecraft.class_852;
import net.minecraft.class_854;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_852.class)
public class MixinChunkOcclusionDataBuilder {
   @Inject(method = "method_3679", at = @At("HEAD"), cancellable = true)
   private void onBuild(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         Freecam var3 = (Freecam)var2.getModuleManager().getModule(Freecam.class);
         if (var3 != null && var3.isActive()) {
            class_854 var6 = new class_854();
            var6.method_3693(EnumSet.allOf(class_2350.class));
            cir.setReturnValue(var6);
         } else {
            FreeLook var4 = (FreeLook)var2.getModuleManager().getModule(FreeLook.class);
            if (var4 != null && var4.isRenderAllActive()) {
               class_854 var5 = new class_854();
               var5.method_3693(EnumSet.allOf(class_2350.class));
               cir.setReturnValue(var5);
            }
         }
      }
   }
}
