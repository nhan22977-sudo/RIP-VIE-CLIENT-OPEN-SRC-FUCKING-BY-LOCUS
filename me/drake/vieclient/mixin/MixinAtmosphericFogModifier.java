package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.modules.render.SkyCustom;
import net.minecraft.class_11398;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_11398.class)
public class MixinAtmosphericFogModifier {
   @Inject(method = "method_71654", at = @At("RETURN"), cancellable = true)
   private void onGetFogColor(class_638 world, class_4184 camera, int light, float tickDelta, CallbackInfoReturnable cir) {
      VieClientMod var6 = VieClientMod.getInstance();
      if (var6 != null && var6.getModuleManager() != null) {
         SkyCustom var7 = (SkyCustom)var6.getModuleManager().getModule(SkyCustom.class);
         if (var7 != null && var7.isEnabled() && var7.isAffectFog()) {
            double var8 = var7.isTimeIndependent() ? 1.0 : var7.getBlend();
            cir.setReturnValue(SkyCustom.lerpArgb((Integer)cir.getReturnValue(), var7.resolveHorizonColor(), var8));
         }
      }
   }

   @Inject(method = "method_42591", at = @At("RETURN"))
   private void onApplyStartEndModifier(class_7285 fogData, class_4184 camera, class_638 world, float viewDistance, class_9779 tickCounter, CallbackInfo ci) {
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getModuleManager() != null) {
         FreeLook var8 = (FreeLook)var7.getModuleManager().getModule(FreeLook.class);
         if (var8 != null && var8.isEnabled() && var8.getCameraDistance() > 16.0F) {
            float var9 = var8.getCameraDistance();
            fogData.field_60582 = Math.max(fogData.field_60582, var9 + 64.0F);
            fogData.field_60584 = Math.max(fogData.field_60584, var9 + 512.0F);
            fogData.field_60583 = Math.max(fogData.field_60583, var9 + 64.0F);
            fogData.field_60585 = Math.max(fogData.field_60585, var9 + 512.0F);
         }
      }
   }
}
