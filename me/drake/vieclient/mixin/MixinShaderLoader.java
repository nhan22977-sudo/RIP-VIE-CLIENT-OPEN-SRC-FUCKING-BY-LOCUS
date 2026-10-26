package me.drake.vieclient.mixin;

import com.mojang.blaze3d.shaders.ShaderType;
import me.drake.vieclient.render.ViePipelines;
import me.drake.vieclient.render.VieShaders;
import net.minecraft.class_10151;
import net.minecraft.class_2960;
import net.minecraft.class_3300;
import net.minecraft.class_3695;
import net.minecraft.class_10151.class_10153;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_10151.class)
public abstract class MixinShaderLoader {
   @Inject(method = "method_68496(Lnet/minecraft/class_2960;Lcom/mojang/blaze3d/shaders/ShaderType;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
   private void vieclient$provideCustomShaderSource(class_2960 id, ShaderType type, CallbackInfoReturnable cir) {
      if (id != null && "vie_client".equals(id.method_12836())) {
         String var4 = VieShaders.getShaderSource(id, type);
         if (var4 != null) {
            cir.setReturnValue(var4);
         }
      }
   }

   @Inject(method = "method_62945(Lnet/minecraft/class_10151$class_10153;Lnet/minecraft/class_3300;Lnet/minecraft/class_3695;)V", at = @At("TAIL"))
   private void vieclient$recompilePipelines(class_10153 definitions, class_3300 resourceManager, class_3695 profiler, CallbackInfo ci) {
      ViePipelines.isPrecompiled = false;
      ViePipelines.precompile();
   }
}
