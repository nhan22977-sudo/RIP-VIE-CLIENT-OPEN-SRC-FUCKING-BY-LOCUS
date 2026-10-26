package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.client.FakeRoles;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.util.ChatMessageQueue;
import net.minecraft.class_2561;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_338.class)
public class MixinChatHud {
   @ModifyVariable(
      method = "method_44811(Lnet/minecraft/class_2561;Lnet/minecraft/class_7469;Lnet/minecraft/class_7591;)V",
      at = @At("HEAD"),
      argsOnly = true,
      index = 1
   )
   private class_2561 vie$protectDisplayedChat(class_2561 message) {
      if (message == null) {
         return null;
      }

      ChatMessageQueue.push(message.getString());
      class_2561 var2 = NameProtect.staticReplaceText(message);
      return FakeRoles.replaceInText(var2);
   }
}
