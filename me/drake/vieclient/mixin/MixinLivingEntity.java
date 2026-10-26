package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.player.SwingSpeed;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1309.class)
public class MixinLivingEntity {
   @Inject(method = "method_6028", at = @At("RETURN"), cancellable = true)
   private void onGetHandSwingDuration(CallbackInfoReturnable cir) {
      class_1309 var2 = (class_1309)this;
      class_310 var3 = class_310.method_1551();
      if (var2 == var3.field_1724 || var2.method_5628() == -6969) {
         VieClientMod var4 = VieClientMod.getInstance();
         if (var4 != null && var4.getModuleManager() != null) {
            SwingSpeed var5 = (SwingSpeed)var4.getModuleManager().getModule(SwingSpeed.class);
            if (var5 != null && var5.isEnabled()) {
               cir.setReturnValue(var5.getSwingDuration());
            }
         }
      }
   }

   @Inject(method = "method_5810", at = @At("RETURN"), cancellable = true)
   private void onIsPushable(CallbackInfoReturnable cir) {
      class_1309 var2 = (class_1309)this;
      if (var2.method_5628() == -6969) {
         VieClientMod var3 = VieClientMod.getInstance();
         if (var3 != null && var3.getModuleManager() != null) {
            Freecam var4 = (Freecam)var3.getModuleManager().getModule(Freecam.class);
            if (var4 != null && var4.isActive()) {
               cir.setReturnValue(false);
            }
         }
      }
   }
}
