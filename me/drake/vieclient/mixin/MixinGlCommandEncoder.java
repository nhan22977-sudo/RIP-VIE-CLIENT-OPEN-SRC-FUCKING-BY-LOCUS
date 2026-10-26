package me.drake.vieclient.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.drake.vieclient.render.ViePipelines;
import net.minecraft.class_10860;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_10860.class)
public abstract class MixinGlCommandEncoder {
   @Inject(method = "method_68356", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_polygonMode(II)V"))
   private void vieclient$applyLineSmoothing(RenderPipeline pipeline, CallbackInfo ci) {
      if (pipeline != ViePipelines.WORLD_LINES && pipeline != ViePipelines.WORLD_LINES_DEPTH) {
         GL11.glDisable(2848);
         GL11.glEnable(32925);
      } else {
         GL11.glEnable(2848);
         GL11.glHint(3154, 4354);
         GL11.glLineWidth(1.0F);
         GL11.glDisable(32925);
      }
   }
}
