package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.NameTags;
import me.drake.vieclient.modules.render.NonLa;
import me.drake.vieclient.modules.render.PlayerESP;
import me.drake.vieclient.modules.render.PlayerModels;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_11890;
import net.minecraft.class_12075;
import net.minecraft.class_1799;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1007.class)
public abstract class MixinPlayerEntityRenderer {
   @Inject(
      method = "method_4213(Lnet/minecraft/class_10055;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void vie$hideVanillaPlayerLabels(class_10055 state, class_4587 matrices, class_11659 queue, class_12075 cameraState, CallbackInfo ci) {
      VieClientMod var6 = VieClientMod.getInstance();
      if (var6 != null && var6.getModuleManager() != null) {
         NameTags var7 = (NameTags)var6.getModuleManager().getModule(NameTags.class);
         PlayerESP var8 = (PlayerESP)var6.getModuleManager().getModule(PlayerESP.class);
         boolean var9 = var7 != null && var7.isEnabled();
         boolean var10 = var8 != null && var8.isEnabled();
         if (var9 || var10) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_4212(Lnet/minecraft/class_10055;Lnet/minecraft/class_4587;FF)V", at = @At("RETURN"))
   private void vie$captureBodyTransform(class_10055 state, class_4587 matrices, float bodyYaw, float scale, CallbackInfo ci) {
      NonLa.captureBodyTransform(state.field_53528, matrices.method_23760().method_23761());
   }

   @Inject(method = "method_62606(Lnet/minecraft/class_10055;)Z", at = @At("HEAD"), cancellable = true)
   private void vie$suppressFeaturesForCustomModel(class_10055 state, CallbackInfoReturnable cir) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         PlayerModels var4 = (PlayerModels)var3.getModuleManager().getModule(PlayerModels.class);
         if (var4 != null && var4.isEnabled() && var4.shouldApplyTo(state)) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(method = "method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V", at = @At("RETURN"))
   private void vie$hideArmorWhenCustomModelEnabled(class_11890 player, class_10055 state, float tickDelta, CallbackInfo ci) {
      VieClientMod var5 = VieClientMod.getInstance();
      if (var5 != null && var5.getModuleManager() != null) {
         PlayerModels var6 = (PlayerModels)var5.getModuleManager().getModule(PlayerModels.class);
         if (var6 != null && var6.isEnabled() && var6.shouldApplyTo(state)) {
            state.field_55309 = class_1799.field_8037;
            state.field_53418 = class_1799.field_8037;
            state.field_53419 = class_1799.field_8037;
            state.field_53420 = class_1799.field_8037;
         }
      }
   }

   @Inject(method = "method_4220(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Z)V", at = @At("HEAD"), cancellable = true)
   private void vie$hideFirstPersonRightArm(class_4587 matrices, class_11659 queue, int light, class_2960 playerTexture, boolean hasGlint, CallbackInfo ci) {
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getModuleManager() != null) {
         PlayerModels var8 = (PlayerModels)var7.getModuleManager().getModule(PlayerModels.class);
         if (var8 != null && var8.isEnabled()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_4221(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;Z)V", at = @At("HEAD"), cancellable = true)
   private void vie$hideFirstPersonLeftArm(class_4587 matrices, class_11659 queue, int light, class_2960 playerTexture, boolean hasGlint, CallbackInfo ci) {
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getModuleManager() != null) {
         PlayerModels var8 = (PlayerModels)var7.getModuleManager().getModule(PlayerModels.class);
         if (var8 != null && var8.isEnabled()) {
            ci.cancel();
         }
      }
   }
}
