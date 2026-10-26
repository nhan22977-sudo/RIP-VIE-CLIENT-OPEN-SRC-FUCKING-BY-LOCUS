package me.drake.vieclient.mixin;

import me.drake.vieclient.util.WindowIconHelper;
import net.minecraft.class_1041;
import net.minecraft.class_3262;
import net.minecraft.class_8518;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1041.class)
public abstract class MixinWindow {
   @Shadow
   public abstract long method_4490();

   @Inject(method = "method_4491", at = @At("TAIL"))
   private void onSetIcon(class_3262 resourcePack, class_8518 icons, CallbackInfo ci) {
      WindowIconHelper.apply(this.method_4490());
   }
}
