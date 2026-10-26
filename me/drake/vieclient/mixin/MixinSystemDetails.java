package me.drake.vieclient.mixin;

import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.class_6396;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_6396.class)
public abstract class MixinSystemDetails {
   @Shadow
   public abstract void method_37122(String var1, String var2);

   @Inject(method = "method_37123(Ljava/lang/String;Ljava/util/function/Supplier;)V", at = @At("HEAD"), cancellable = true)
   private void vieclient$hideRenderExtensions(String name, Supplier supplier, CallbackInfo ci) {
      if (name != null) {
         String var4 = name.toLowerCase(Locale.ROOT);
         if (var4.contains("render") && var4.contains("extension")) {
            this.method_37122(name, "N/A");
            ci.cancel();
         }
      }
   }
}
