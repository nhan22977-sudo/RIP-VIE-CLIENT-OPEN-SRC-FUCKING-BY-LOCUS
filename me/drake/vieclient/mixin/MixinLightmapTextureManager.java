package me.drake.vieclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.ModuleManager;
import me.drake.vieclient.modules.render.FullBright;
import net.minecraft.class_765;
import net.minecraft.class_9848;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_765.class)
public abstract class MixinLightmapTextureManager {
   @Shadow
   @Final
   private GpuTexture field_57927;

   @Inject(method = "method_3313", at = @At("HEAD"), cancellable = true)
   private void vieGammaFullBright(float tickDelta, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null) {
         ModuleManager var4 = var3.getModuleManager();
         if (var4 != null) {
            FullBright var5 = (FullBright)var4.getModule(FullBright.class);
            if (var5 != null && var5.isGammaMode()) {
               RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.field_57927, class_9848.method_61324(255, 255, 255, 255));
               ci.cancel();
            }
         }
      }
   }
}
