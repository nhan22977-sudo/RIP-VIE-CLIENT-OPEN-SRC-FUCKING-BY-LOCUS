package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.movement.TridentBoost;
import net.minecraft.class_1657;
import net.minecraft.class_1835;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_1835.class)
public abstract class MixinTridentItem {
   @Redirect(method = "method_7836", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1657;method_5721()Z"))
   private boolean vie$allowOutOfWaterUse(class_1657 player) {
      return player.method_5721() || TridentBoost.allowOutOfWater();
   }

   @Redirect(method = "method_7840", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1657;method_5721()Z"))
   private boolean vie$allowOutOfWaterRelease(class_1657 player) {
      return player.method_5721() || TridentBoost.allowOutOfWater();
   }

   @ModifyConstant(method = "method_7840", constant = @Constant(intValue = 10))
   private int vie$scaleMinCharge(int original) {
      return TridentBoost.minChargeTicks(original);
   }

   @Redirect(method = "method_7840", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1657;method_5762(DDD)V"))
   private void vie$boostRiptideVelocity(class_1657 player, double x, double y, double z) {
      double var8 = TridentBoost.riptideMultiplier();
      player.method_5762(x * var8, y * var8, z * var8);
   }
}
