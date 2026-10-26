package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.HandViewAnimation;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_759.class)
public class MixinHandViewAnimation {
   @Unique
   private boolean vie$pushed;

   @ModifyVariable(method = "method_3228", at = @At("HEAD"), index = 5)
   private float onSwingProgress(
      float value,
      class_742 player,
      float tickDelta,
      float pitch,
      class_1268 hand,
      float swingProgress,
      class_1799 item,
      float equipProgress,
      class_4587 matrices,
      class_11659 queue,
      int light
   ) {
      VieClientMod var12 = VieClientMod.getInstance();
      if (var12 != null && var12.getModuleManager() != null) {
         HandViewAnimation var13 = (HandViewAnimation)var12.getModuleManager().getModule(HandViewAnimation.class);
         return var13 != null && var13.shouldAnimate(hand) ? 0.0F : value;
      } else {
         return value;
      }
   }

   @ModifyVariable(method = "method_3228", at = @At("HEAD"), index = 7)
   private float onEquipProgress(
      float value,
      class_742 player,
      float tickDelta,
      float pitch,
      class_1268 hand,
      float swingProgress,
      class_1799 item,
      float equipProgress,
      class_4587 matrices,
      class_11659 queue,
      int light
   ) {
      VieClientMod var12 = VieClientMod.getInstance();
      if (var12 != null && var12.getModuleManager() != null) {
         HandViewAnimation var13 = (HandViewAnimation)var12.getModuleManager().getModule(HandViewAnimation.class);
         if (var13 == null || !var13.shouldAnimate(hand)) {
            return value;
         } else if (player.method_6115()) {
            return value;
         } else {
            return !item.method_7960() && !item.method_57826(class_9334.field_49646) && !item.method_31574(class_1802.field_8399) ? 0.0F : value;
         }
      } else {
         return value;
      }
   }

   @Inject(
      method = "method_3228",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
         shift = Shift.BEFORE
      )
   )
   private void onBeforeItemRender(
      class_742 player,
      float tickDelta,
      float pitch,
      class_1268 hand,
      float swingProgress,
      class_1799 item,
      float equipProgress,
      class_4587 matrices,
      class_11659 queue,
      int light,
      CallbackInfo ci
   ) {
      this.vie$pushed = false;
      VieClientMod var12 = VieClientMod.getInstance();
      if (var12 != null && var12.getModuleManager() != null) {
         HandViewAnimation var13 = (HandViewAnimation)var12.getModuleManager().getModule(HandViewAnimation.class);
         if (var13 != null && var13.shouldAnimate(hand)) {
            if (!player.method_6115()) {
               if (!item.method_7960() && !item.method_57826(class_9334.field_49646) && !item.method_31574(class_1802.field_8399)) {
                  try {
                     matrices.method_22903();
                     this.vie$pushed = true;
                     var13.applyTransform(matrices);
                  } catch (Throwable var17) {
                     if (this.vie$pushed) {
                        try {
                           matrices.method_22909();
                        } catch (Throwable var16) {
                        }

                        this.vie$pushed = false;
                     }

                     VieClientMod.LOGGER.error("[VieClient] ItemSpin transform failed", var17);
                  }
               }
            }
         }
      }
   }

   @Inject(
      method = "method_3228",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
         shift = Shift.AFTER
      )
   )
   private void onAfterItemRender(
      class_742 player,
      float tickDelta,
      float pitch,
      class_1268 hand,
      float swingProgress,
      class_1799 item,
      float equipProgress,
      class_4587 matrices,
      class_11659 queue,
      int light,
      CallbackInfo ci
   ) {
      if (this.vie$pushed) {
         matrices.method_22909();
         this.vie$pushed = false;
      }
   }
}
