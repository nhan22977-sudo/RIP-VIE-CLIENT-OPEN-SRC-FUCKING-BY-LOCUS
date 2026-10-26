package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.command.CommandManager;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_634.class)
public class MixinHandledScreen {
   @Inject(method = "method_45729", at = @At("HEAD"), cancellable = true)
   private void onSendChatMessage(String message, CallbackInfo ci) {
      if (message != null && message.startsWith(".")) {
         VieClientMod var3 = VieClientMod.getInstance();
         if (var3 != null) {
            CommandManager var4 = var3.getCommandManager();
            if (var4 != null) {
               boolean var5 = var4.dispatch(message);
               if (var5) {
                  ci.cancel();
               }
            }
         }
      }
   }
}
