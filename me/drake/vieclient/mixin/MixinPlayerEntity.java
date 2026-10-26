package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.modules.client.FakeRoles;
import me.drake.vieclient.modules.client.NameProtect;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1657.class)
public class MixinPlayerEntity {
   @Inject(method = "method_7324", at = @At("HEAD"), cancellable = true)
   private void onAttack(class_1297 target, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null) {
         AttackEvent var4 = new AttackEvent(target);
         var3.getEventBus().post(var4);
         if (var4.isCancelled()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_5477", at = @At("RETURN"), cancellable = true)
   private void onGetName(CallbackInfoReturnable cir) {
      class_2561 var2 = (class_2561)cir.getReturnValue();
      if (var2 != null) {
         cir.setReturnValue(NameProtect.staticReplaceNametagText(var2));
      }
   }

   @Inject(method = "method_5476", at = @At("RETURN"), cancellable = true)
   private void onGetDisplayName(CallbackInfoReturnable cir) {
      class_2561 var2 = (class_2561)cir.getReturnValue();
      if (var2 != null) {
         if (class_310.method_1551().field_1724 != null && class_310.method_1551().field_1724 == this) {
            var2 = FakeRoles.replaceInText(var2);
         }

         cir.setReturnValue(NameProtect.staticReplaceNametagText(var2));
      }
   }
}
