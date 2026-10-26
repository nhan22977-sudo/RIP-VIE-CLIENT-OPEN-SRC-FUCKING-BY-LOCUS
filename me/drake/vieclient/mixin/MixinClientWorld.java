package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.EntitySpawnEvent;
import net.minecraft.class_1297;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_638.class)
public class MixinClientWorld {
   @Inject(method = "method_53875", at = @At("HEAD"), cancellable = true)
   private void onAddEntity(class_1297 entity, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus() != null) {
         EntitySpawnEvent var4 = new EntitySpawnEvent(entity);
         var3.getEventBus().post(var4);
         if (var4.isCancelled()) {
            ci.cancel();
         }
      }
   }
}
