package me.drake.vieclient.mixin;

import java.util.Optional;
import net.minecraft.class_1059;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_4618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_4618.class)
public class MixinOutlineVertexConsumerProvider {
   @Redirect(method = "method_73477", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1921;method_23289()Ljava/util/Optional;"))
   private Optional vie$provideItemOutlineLayer(class_1921 layer) {
      Optional var2 = layer.method_23289();
      if (var2.isPresent()) {
         return var2;
      } else {
         String var3 = layer.toString();
         if (var3.contains("item_entity_translucent_cull")) {
            return var3.contains("textures/atlas/blocks.png")
               ? Optional.of(class_12249.method_76018(class_1059.field_5275))
               : Optional.of(class_12249.method_76018(class_1059.field_64467));
         } else {
            return Optional.empty();
         }
      }
   }
}
