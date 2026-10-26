package me.drake.vieclient.mixin;

import java.util.List;
import me.drake.vieclient.modules.render.EdgeGlow;
import me.drake.vieclient.modules.render.FillGlow;
import me.drake.vieclient.render.post.EdgeGlowPass;
import net.minecraft.class_11687;
import net.minecraft.class_11788;
import net.minecraft.class_4587;
import net.minecraft.class_4618;
import net.minecraft.class_811;
import net.minecraft.class_918;
import net.minecraft.class_10444.class_10445;
import net.minecraft.class_11661.class_11668;
import net.minecraft.class_4597.class_4598;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_11687.class)
public abstract class MixinItemCommandRenderer {
   @Shadow
   @Final
   private class_4587 field_61844;

   @Inject(
      method = "method_73010(Lnet/minecraft/class_11788;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4618;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void vie$renderWithEdgeGlow(class_11788 queue, class_4598 vertexConsumers, class_4618 outlineVertexConsumers, CallbackInfo ci) {
      EdgeGlow var5 = EdgeGlow.get();
      FillGlow var6 = FillGlow.get();
      boolean var7 = var6 != null && var6.shouldGlowItems();
      boolean var8 = var5 != null && var5.shouldGlowItems();
      if (var7 || var8) {
         List var9 = queue.method_73522();
         if (var9 != null && !var9.isEmpty()) {
            boolean var10 = false;

            for (class_11668 var12 : var9) {
               if (var12.comp_4638() == class_811.field_4318) {
                  var10 = true;
                  break;
               }
            }

            if (var10) {
               ci.cancel();
               vertexConsumers.method_22993();
               boolean var15 = false;

               for (class_11668 var13 : var9) {
                  if (var13.comp_4638() != class_811.field_4318) {
                     var15 = true;
                     this.field_61844.method_22903();
                     this.field_61844.method_23760().method_66521(var13.comp_4498());
                     class_918.method_62476(
                        var13.comp_4638(),
                        this.field_61844,
                        vertexConsumers,
                        var13.comp_4500(),
                        var13.comp_4501(),
                        var13.comp_4639(),
                        var13.comp_4640(),
                        var13.comp_4641(),
                        var13.comp_4642()
                     );
                     if (var13.comp_4661() != 0) {
                        outlineVertexConsumers.method_23286(var13.comp_4661());
                        class_918.method_62476(
                           var13.comp_4638(),
                           this.field_61844,
                           outlineVertexConsumers,
                           var13.comp_4500(),
                           var13.comp_4501(),
                           var13.comp_4639(),
                           var13.comp_4640(),
                           var13.comp_4641(),
                           class_10445.field_55341
                        );
                     }

                     this.field_61844.method_22909();
                  }
               }

               if (var15) {
                  vertexConsumers.method_22993();
               }

               boolean var17 = EdgeGlowPass.beginCapture(true);

               for (class_11668 var14 : var9) {
                  if (var14.comp_4638() == class_811.field_4318) {
                     this.field_61844.method_22903();
                     this.field_61844.method_23760().method_66521(var14.comp_4498());
                     class_918.method_62476(
                        var14.comp_4638(),
                        this.field_61844,
                        vertexConsumers,
                        var14.comp_4500(),
                        var14.comp_4501(),
                        var14.comp_4639(),
                        var14.comp_4640(),
                        var14.comp_4641(),
                        var14.comp_4642()
                     );
                     if (var14.comp_4661() != 0) {
                        outlineVertexConsumers.method_23286(var14.comp_4661());
                        class_918.method_62476(
                           var14.comp_4638(),
                           this.field_61844,
                           outlineVertexConsumers,
                           var14.comp_4500(),
                           var14.comp_4501(),
                           var14.comp_4639(),
                           var14.comp_4640(),
                           var14.comp_4641(),
                           class_10445.field_55341
                        );
                     }

                     this.field_61844.method_22909();
                  }
               }

               vertexConsumers.method_22993();
               if (var17) {
                  EdgeGlowPass.endCapture();
                  if (var7) {
                     var6.composePass();
                  } else {
                     var5.composePass();
                  }
               }
            }
         }
      }
   }
}
