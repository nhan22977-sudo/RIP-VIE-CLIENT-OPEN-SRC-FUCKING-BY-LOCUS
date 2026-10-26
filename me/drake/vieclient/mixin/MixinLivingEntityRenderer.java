package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.Accessories;
import me.drake.vieclient.modules.render.PlayerModels;
import net.minecraft.class_10042;
import net.minecraft.class_10055;
import net.minecraft.class_1058;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_1921;
import net.minecraft.class_3879;
import net.minecraft.class_4587;
import net.minecraft.class_922;
import net.minecraft.class_11683.class_11792;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_922.class)
public abstract class MixinLivingEntityRenderer {
   @Inject(method = "method_24302(Lnet/minecraft/class_10042;ZZZ)Lnet/minecraft/class_1921;", at = @At("HEAD"), cancellable = true)
   private void vie$suppressBaseSkinLayer(class_10042 state, boolean isVisible, boolean isInvisibleToPlayer, boolean hasOutline, CallbackInfoReturnable cir) {
      if (state instanceof class_10055 var6) {
         VieClientMod var7 = VieClientMod.getInstance();
         if (var7 == null || var7.getModuleManager() == null) {
            return;
         }

         PlayerModels var8 = (PlayerModels)var7.getModuleManager().getModule(PlayerModels.class);
         if (var8 != null && var8.isEnabled() && var8.shouldApplyTo(var6)) {
            cir.setReturnValue(null);
         }
      }
   }

   @Redirect(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11659;method_73490(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_1058;ILnet/minecraft/class_11683$class_11792;)V"
      )
   )
   private void vie$submitOrSuppressVanillaModel(
      class_11659 queue,
      class_3879 model,
      Object state,
      class_4587 matrices,
      class_1921 renderLayer,
      int light,
      int overlay,
      int color,
      class_1058 sprite,
      int glMode,
      class_11792 crumblingOverlayCommand
   ) {
      if (state instanceof class_10055 var12) {
         VieClientMod var13 = VieClientMod.getInstance();
         if (var13 != null && var13.getModuleManager() != null) {
            PlayerModels var14 = (PlayerModels)var13.getModuleManager().getModule(PlayerModels.class);
            if (var14 != null && var14.isEnabled() && var14.shouldApplyTo(var12)) {
               return;
            }
         }
      }

      queue.method_73490(model, state, matrices, renderLayer, light, overlay, color, sprite, glMode, crumblingOverlayCommand);
   }

   @Inject(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_922;method_62483(Lnet/minecraft/class_10042;)Z")
   )
   private void vie$renderCustomModelAndAccessories(class_10042 state, class_4587 matrices, class_11659 queue, class_12075 cameraState, CallbackInfo ci) {
      if (state instanceof class_10055 var6) {
         VieClientMod var7 = VieClientMod.getInstance();
         if (var7 == null || var7.getModuleManager() == null) {
            return;
         }

         PlayerModels var8 = (PlayerModels)var7.getModuleManager().getModule(PlayerModels.class);
         if (var8 != null && var8.isEnabled() && var8.shouldApplyTo(var6)) {
            var8.renderCharacter(var6, matrices, queue);
         }

         Accessories var9 = (Accessories)var7.getModuleManager().getModule(Accessories.class);
         if (var9 != null && var9.isEnabled() && var9.shouldApplyTo(var6)) {
            var9.renderAccessories(var6, matrices, queue);
         }
      }
   }

   @Inject(method = "method_62483(Lnet/minecraft/class_10042;)Z", at = @At("HEAD"), cancellable = true)
   private void vie$suppressFeaturesForCustomModel(class_10042 state, CallbackInfoReturnable cir) {
      if (state instanceof class_10055 var3) {
         VieClientMod var4 = VieClientMod.getInstance();
         if (var4 == null || var4.getModuleManager() == null) {
            return;
         }

         PlayerModels var5 = (PlayerModels)var4.getModuleManager().getModule(PlayerModels.class);
         if (var5 != null && var5.isEnabled() && var5.shouldApplyTo(var3)) {
            cir.setReturnValue(false);
         }
      }
   }
}
