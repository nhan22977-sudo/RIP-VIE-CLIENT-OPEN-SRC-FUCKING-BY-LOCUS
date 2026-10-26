package me.drake.vieclient.mixin;

import me.drake.vieclient.gui.nvg.ui.CustomTitleScreen;
import me.drake.vieclient.gui.web.VieWebTitleScreen;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_442.class)
public class MixinTitleScreen {
   private static final Logger LOGGER = LoggerFactory.getLogger("vie/title");

   @Inject(method = "method_25426", at = @At("HEAD"), cancellable = true)
   private void onInit(CallbackInfo ci) {
      class_310 var2 = class_310.method_1551();
      if (var2 != null) {
         class_437 var3 = createCustomTitleScreen();
         var2.method_1507(var3);
         ci.cancel();
      }
   }

   private static class_437 createCustomTitleScreen() {
      try {
         return new VieWebTitleScreen();
      } catch (Throwable var1) {
         LOGGER.error("[vie/title] Failed to instantiate VieWebTitleScreen: {}", var1.getMessage(), var1);
         return new CustomTitleScreen();
      }
   }
}
