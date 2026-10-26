package me.drake.vieclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.Render3DEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.modules.donut.ChunkDebug;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.modules.render.ColorCorrection;
import me.drake.vieclient.modules.render.CustomFOV;
import me.drake.vieclient.modules.render.MotionBlur;
import me.drake.vieclient.modules.render.TrueStretched;
import me.drake.vieclient.render.VieMeshRenderer;
import me.drake.vieclient.render.ViePipelines;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_757.class)
public abstract class MixinGameRenderer {
   private static final Matrix4f REUSABLE_VIEW_MATRIX = new Matrix4f();
   @Shadow
   @Final
   private class_4184 field_18765;

   @Shadow
   protected abstract float method_3196(class_4184 var1, float var2, boolean var3);

   @Shadow
   public abstract Matrix4f method_22973(float var1);

   @Inject(method = "method_22973", at = @At("RETURN"), cancellable = true)
   private void vieclient$applyTrueStretched(float fov, CallbackInfoReturnable cir) {
      float var3 = TrueStretched.getStretchFactor();
      if (!(Math.abs(var3 - 1.0F) < 1.0E-4F)) {
         Matrix4f var4 = (Matrix4f)cir.getReturnValue();
         if (var4 != null) {
            Matrix4f var5 = new Matrix4f(var4);
            var5.scale(var3, 1.0F, 1.0F);
            cir.setReturnValue(var5);
         }
      }
   }

   @Inject(method = "method_32796", at = @At("RETURN"), cancellable = true)
   private void extendChunkDebugFarPlane(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      class_310 var3 = class_310.method_1551();
      if (var2 != null && var3.field_1724 != null) {
         ChunkDebug var4 = (ChunkDebug)var2.getModuleManager().getModule(ChunkDebug.class);
         float var5 = (Float)cir.getReturnValue();
         if (var4 != null && var4.isEnabled()) {
            var4.setVanillaFarPlaneDistance(var5);
            if (var4.hasAlertedChunks()) {
               var5 = var4.requiredFarPlaneDistance(var3.field_1724.method_23317(), var3.field_1724.method_23321(), var5);
            }
         }

         FreeLook var6 = (FreeLook)var2.getModuleManager().getModule(FreeLook.class);
         if (var6 != null && var6.isEnabled()) {
            float var7 = var6.getCameraDistance();
            if (var7 > 16.0F) {
               var5 = Math.max(var5, var7 + 512.0F);
            }
         }

         cir.setReturnValue(var5);
      }
   }

   @Inject(method = "method_3196", at = @At("RETURN"), cancellable = true)
   private void onGetFov(class_4184 camera, float tickDelta, boolean changingFov, CallbackInfoReturnable cir) {
      VieClientMod var5 = VieClientMod.getInstance();
      if (var5 != null && var5.getModuleManager() != null) {
         CustomFOV var6 = (CustomFOV)var5.getModuleManager().getModule(CustomFOV.class);
         if (var6 != null && var6.isEnabled()) {
            cir.setReturnValue(var6.computeFov((Float)cir.getReturnValue(), tickDelta, changingFov));
         }

         FreeLook var7 = (FreeLook)var5.getModuleManager().getModule(FreeLook.class);
         if (var7 != null && var7.isEnabled()) {
            float var8 = var7.getFovModifier();
            if (var8 != 0.0F) {
               cir.setReturnValue((Float)cir.getReturnValue() + var8);
            }
         }
      }
   }

   @Inject(method = "method_3188", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3695;method_15405(Ljava/lang/String;)V", ordinal = 2))
   private void onRenderWorld(class_9779 tickCounter, CallbackInfo ci) {
      if (!ViePipelines.isPrecompiled) {
         ViePipelines.precompile();
      }

      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null) {
         boolean var4 = var3.getEventBus().hasSubscribers(RenderEvent.class);
         boolean var5 = var3.getEventBus().hasSubscribers(Render3DEvent.class);
         if (var4 || var5) {
            float var6 = tickCounter.method_60637(true);
            float var7 = class_3532.method_15363(tickCounter.method_60636() / 20.0F, 5.0E-4F, 0.05F);

            try {
               if (VieMeshRenderer.hasCapturedWorldMatrices) {
                  VieMeshRenderer.projection.set(VieMeshRenderer.lastProjectionMatrix);
               } else {
                  VieMeshRenderer.projection.set(this.method_22973(this.method_3196(this.field_18765, var6, true)));
               }

               VieMeshRenderer.flipFrame();
               if (VieMeshRenderer.hasCapturedWorldMatrices) {
                  REUSABLE_VIEW_MATRIX.set(VieMeshRenderer.lastPositionMatrix);
               } else {
                  REUSABLE_VIEW_MATRIX.identity();
               }

               RenderSystem.getModelViewStack().pushMatrix();
               RenderSystem.getModelViewStack().identity();
               RenderSystem.getModelViewStack().mul(REUSABLE_VIEW_MATRIX);

               try {
                  RenderUtil.begin();
                  class_4587 var8 = null;
                  if (var5) {
                     var8 = new class_4587();
                     var3.getEventBus().post(new Render3DEvent(var6, var8));
                  }

                  if (var4) {
                     var3.getEventBus().post(new RenderEvent(RenderEvent.Stage.WORLD_3D, var6, var7));
                  }

                  RenderUtil.render(var8);
               } finally {
                  RenderSystem.getModelViewStack().popMatrix();
               }
            } catch (Exception var13) {
               VieClientMod.LOGGER.error("[VieClient] Render error", var13);
            }
         }
      }
   }

   @Inject(method = "method_3188", at = @At("TAIL"))
   private void onRenderWorldTail(class_9779 tickCounter, CallbackInfo ci) {
      ColorCorrection.renderAfterWorld(true);
      MotionBlur.renderAfterWorld(true);
   }
}
