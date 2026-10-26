package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.TargetMarginEvent;
import me.drake.vieclient.core.event.events.TargetPoseEvent;
import me.drake.vieclient.gui.nvg.ui.EmoteWheel;
import me.drake.vieclient.modules.combat.AutoSwordBotting;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import net.minecraft.class_1297;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1297.class)
public abstract class MixinEntity {
   @Inject(method = "method_5872", at = @At("HEAD"), cancellable = true)
   private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
      if (this instanceof class_746) {
         if (EmoteWheel.consumesCameraInput()) {
            EmoteWheel.applyMouseDelta(cursorDeltaX, cursorDeltaY);
            ci.cancel();
         } else {
            VieClientMod var6 = VieClientMod.getInstance();
            if (var6 != null) {
               Freecam var7 = (Freecam)var6.getModuleManager().getModule(Freecam.class);
               if (var7 != null && var7.isEnabled()) {
                  var7.applyMouseDelta((float)cursorDeltaX, (float)cursorDeltaY);
                  ci.cancel();
               } else {
                  FreeLook var8 = (FreeLook)var6.getModuleManager().getModule(FreeLook.class);
                  if (var8 != null && var8.isEnabled()) {
                     AutoSwordBotting var9 = (AutoSwordBotting)var6.getModuleManager().getModule(AutoSwordBotting.class);
                     boolean var10 = var9 != null && var9.isEnabled();
                     if (var10 || var8.cameraMode()) {
                        var8.applyMouseDelta((float)cursorDeltaX, (float)cursorDeltaY);
                        ci.cancel();
                        return;
                     }

                     if (var8.playerMode()) {
                        return;
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "method_18376", at = @At("HEAD"), cancellable = true)
   private void onGetPose(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getEventBus() != null) {
         var2.getEventBus().post(new TargetPoseEvent((class_1297)this, cir));
      }
   }

   @Inject(method = "method_5871", at = @At("HEAD"), cancellable = true)
   private void onGetTargetingMargin(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getEventBus() != null) {
         var2.getEventBus().post(new TargetMarginEvent((class_1297)this, cir));
      }
   }
}
