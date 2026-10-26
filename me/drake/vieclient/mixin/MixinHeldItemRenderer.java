package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.EdgeGlow;
import me.drake.vieclient.modules.render.FillGlow;
import me.drake.vieclient.modules.render.ItemReplacement;
import me.drake.vieclient.modules.render.SwingAnimation;
import me.drake.vieclient.render.post.EdgeGlowPass;
import net.minecraft.class_11659;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_759.class)
public class MixinHeldItemRenderer {
   @Inject(method = "method_65816(FLnet/minecraft/class_4587;ILnet/minecraft/class_1306;)V", at = @At("HEAD"), cancellable = true)
   private void vie$applyCustomSwing(float swingProgress, class_4587 matrices, int side, class_1306 arm, CallbackInfo ci) {
      VieClientMod var6 = VieClientMod.getInstance();
      if (var6 != null && var6.getModuleManager() != null) {
         SwingAnimation var7 = (SwingAnimation)var6.getModuleManager().getModule(SwingAnimation.class);
         if (var7 != null && var7.isEnabled() && var7.applySwing(matrices, arm, swingProgress)) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_3224(Lnet/minecraft/class_4587;Lnet/minecraft/class_1306;F)V", at = @At("HEAD"), cancellable = true)
   private void vie$preventEquipDip(class_4587 matrices, class_1306 arm, float equipProgress, CallbackInfo ci) {
      VieClientMod var5 = VieClientMod.getInstance();
      if (var5 != null && var5.getModuleManager() != null) {
         SwingAnimation var6 = (SwingAnimation)var5.getModuleManager().getModule(SwingAnimation.class);
         if (var6 != null && var6.isEnabled() && var6.isNoEquipDip()) {
            int var7 = arm == class_1306.field_6183 ? 1 : -1;
            matrices.method_46416(var7 * 0.56F, -0.52F, -0.72F);
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V", at = @At("HEAD"))
   private void vie$startHandGlow(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
      if (!FillGlow.beginCaptureHand()) {
         EdgeGlow.beginCaptureHand();
      }
   }

   @Inject(method = "method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V", at = @At("RETURN"))
   private void vie$endHandGlow(float tickDelta, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
      if (EdgeGlowPass.isCapturing()) {
         class_310.method_1551().method_22940().method_23000().method_22993();
         FillGlow var7 = FillGlow.get();
         if (var7 != null && var7.shouldGlowHand()) {
            FillGlow.endCaptureHand(true);
         } else {
            EdgeGlow.endCaptureHand(true);
         }

         if (EdgeGlowPass.isCapturing()) {
            EdgeGlowPass.endCapture();
         }
      }
   }

   @ModifyVariable(method = "method_3228", at = @At("HEAD"), argsOnly = true)
   private class_1799 vie$modifyFirstPersonItem(class_1799 item) {
      return ItemReplacement.getReplaced(item);
   }

   @ModifyVariable(
      method = "method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At("HEAD"),
      argsOnly = true
   )
   private class_1799 vie$modifyHeldRenderItem(class_1799 item) {
      return ItemReplacement.getReplaced(item);
   }
}
