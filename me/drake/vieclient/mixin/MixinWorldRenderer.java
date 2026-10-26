package me.drake.vieclient.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.modules.render.BreakingProgress;
import me.drake.vieclient.render.VieMeshRenderer;
import me.drake.vieclient.util.EntityCache;
import net.minecraft.class_11658;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import net.minecraft.class_4597.class_4598;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_761.class)
public class MixinWorldRenderer {
   @Inject(method = "method_22710", at = @At("HEAD"))
   private void onRenderHead(
      class_9922 allocator,
      class_9779 tickCounter,
      boolean renderBlockOutline,
      class_4184 camera,
      Matrix4f positionMatrix,
      Matrix4f projectionMatrix,
      Matrix4f frustumProjectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean spectator,
      CallbackInfo ci
   ) {
      EntityCache.updateFrustum(positionMatrix, frustumProjectionMatrix, camera);
      VieMeshRenderer.lastPositionMatrix.set(positionMatrix);
      VieMeshRenderer.lastProjectionMatrix.set(projectionMatrix);
      VieMeshRenderer.hasCapturedWorldMatrices = true;
   }

   @ModifyVariable(method = "method_22710", at = @At("HEAD"), ordinal = 1, argsOnly = true)
   private boolean vieclient$forceSpectatorForFreecam(boolean spectator) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         Freecam var3 = (Freecam)var2.getModuleManager().getModule(Freecam.class);
         if (var3 != null && var3.isActive()) {
            return true;
         }

         FreeLook var4 = (FreeLook)var2.getModuleManager().getModule(FreeLook.class);
         return var4 != null && var4.isRenderAllActive() ? true : spectator;
      } else {
         return spectator;
      }
   }

   @Inject(method = "method_62206", at = @At("HEAD"), cancellable = true)
   private void vieclient$onRenderBlockDamage(class_4587 matrices, class_4598 vertexConsumers, class_11658 renderState, CallbackInfo ci) {
      VieClientMod var5 = VieClientMod.getInstance();
      if (var5 != null && var5.getModuleManager() != null) {
         BreakingProgress var6 = (BreakingProgress)var5.getModuleManager().getModule(BreakingProgress.class);
         if (var6 != null && var6.isEnabled() && (Boolean)var6.getHideVanillaCracks().get()) {
            ci.cancel();
         }
      }
   }
}
