package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.PlayerMoveEvent;
import me.drake.vieclient.core.event.events.PlayerMovementTickEvent;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_746.class)
public class MixinClientPlayerEntity {
   @Inject(method = "method_3136", at = @At("RETURN"))
   private void afterMovementPackets(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         var2.getEventBus().post(PlayerMovementTickEvent.INSTANCE);
      }
   }

   @Inject(method = "method_3136", at = @At("HEAD"))
   private void onSendMovementPackets(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         class_746 var3 = (class_746)this;
         PlayerMoveEvent var4 = new PlayerMoveEvent(
            var3.method_23317(), var3.method_23318(), var3.method_23321(), var3.method_36454(), var3.method_36455(), var3.method_24828()
         );
         var2.getEventBus().post(var4);
      }
   }
}
