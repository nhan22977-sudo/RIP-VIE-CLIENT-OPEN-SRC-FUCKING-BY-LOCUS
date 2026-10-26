package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.SkyCustom;
import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_9975.class)
public class MixinSkyRendering {
   @Inject(method = "method_74926", at = @At("TAIL"))
   private void onUpdateRenderState(class_638 world, float tickDelta, class_4184 camera, class_12076 state, CallbackInfo ci) {
      VieClientMod var6 = VieClientMod.getInstance();
      if (var6 != null && var6.getModuleManager() != null) {
         SkyCustom var7 = (SkyCustom)var6.getModuleManager().getModule(SkyCustom.class);
         if (var7 != null && var7.isEnabled()) {
            double var8 = var7.isTimeIndependent() ? 1.0 : var7.getBlend();
            state.field_63097 = SkyCustom.lerpArgb(state.field_63097, var7.resolveTopColor(), var8);
            state.field_63095 = SkyCustom.lerpArgb(state.field_63095, var7.resolveHorizonColor(), var8);
         }
      }
   }
}
