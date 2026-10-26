package io.github.kosmx.emotes.arch.mixin;

import io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity;
import net.minecraft.class_11890;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1657.class)
public abstract class PlayerMixin extends class_11890 {
   @Shadow
   public abstract boolean method_7340();

   protected PlayerMixin(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "method_7318()V", at = @At("TAIL"))
   private void updatePlayerPoseEvent(CallbackInfo ci) {
      if (this.method_7340()
         && this instanceof IPlayerEntity animator
         && (
            this.method_18376() == class_4050.field_18081
               || this.method_18376() == class_4050.field_18082
               || this.method_18376() == class_4050.field_18079
               || this.method_18376() == class_4050.field_18077
               || this.method_18376() == class_4050.field_18078
         )) {
         animator.emotecraft$playerEntersInvalidPose();
      }
   }
}
