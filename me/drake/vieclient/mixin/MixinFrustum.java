package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import net.minecraft.class_238;
import net.minecraft.class_4604;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_4604.class)
public class MixinFrustum {
   @Inject(method = "method_23093", at = @At("HEAD"), cancellable = true)
   private void vieclient$forceVisible(class_238 box, CallbackInfoReturnable cir) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         Freecam var4 = (Freecam)var3.getModuleManager().getModule(Freecam.class);
         if (var4 != null && var4.isRenderAllActive()) {
            cir.setReturnValue(true);
         } else {
            FreeLook var5 = (FreeLook)var3.getModuleManager().getModule(FreeLook.class);
            if (var5 != null && var5.isRenderAllActive()) {
               cir.setReturnValue(true);
            }
         }
      }
   }
}
