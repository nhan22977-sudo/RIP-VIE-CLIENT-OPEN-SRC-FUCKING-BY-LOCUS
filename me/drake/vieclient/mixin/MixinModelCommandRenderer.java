package me.drake.vieclient.mixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.drake.vieclient.modules.render.EdgeGlow;
import me.drake.vieclient.modules.render.FillGlow;
import me.drake.vieclient.render.post.EdgeGlowPass;
import net.minecraft.class_10055;
import net.minecraft.class_11683;
import net.minecraft.class_11788;
import net.minecraft.class_1921;
import net.minecraft.class_4618;
import net.minecraft.class_11661.class_11670;
import net.minecraft.class_11661.class_11744;
import net.minecraft.class_11683.class_12048;
import net.minecraft.class_4597.class_4598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_11683.class)
public abstract class MixinModelCommandRenderer {
   @Shadow
   protected abstract void method_73249(class_4598 var1, class_4618 var2, Map var3, class_4598 var4);

   @Shadow
   protected abstract void method_73250(class_4598 var1, class_4618 var2, List var3, class_4598 var4);

   @Inject(
      method = "method_73001(Lnet/minecraft/class_11788;Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4618;Lnet/minecraft/class_4597$class_4598;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void vie$renderWithPlayerGlow(
      class_11788 queue, class_4598 vertexConsumers, class_4618 outlineVertexConsumers, class_4598 crumblingOverlayVertexConsumers, CallbackInfo ci
   ) {
      EdgeGlow var6 = EdgeGlow.get();
      FillGlow var7 = FillGlow.get();
      boolean var8 = var7 != null && var7.shouldGlowPlayers();
      boolean var9 = var6 != null && var6.shouldGlowPlayers();
      if (var8 || var9) {
         class_12048 var10 = queue.method_74771();
         if (var10 != null) {
            ModelCommandsAccessor var11 = (ModelCommandsAccessor)var10;
            Map var12 = var11.vie$getOpaqueModelCommands();
            List var13 = var11.vie$getBlendedModelCommands();
            boolean var14 = false;
            if (var12 != null) {
               for (List var16 : var12.values()) {
                  if (var16 != null) {
                     for (class_11670 var18 : var16) {
                        if (var18 != null && var18.comp_4506() instanceof class_10055) {
                           var14 = true;
                           break;
                        }
                     }
                  }

                  if (var14) {
                     break;
                  }
               }
            }

            if (!var14 && var13 != null) {
               for (class_11744 var25 : var13) {
                  if (var25 != null && var25.comp_4615() != null && var25.comp_4615().comp_4506() instanceof class_10055) {
                     var14 = true;
                     break;
                  }
               }
            }

            if (var14) {
               ci.cancel();
               vertexConsumers.method_22993();
               LinkedHashMap var24 = new LinkedHashMap();
               LinkedHashMap var26 = new LinkedHashMap();
               if (var12 != null) {
                  for (Entry var29 : var12.entrySet()) {
                     class_1921 var19 = (class_1921)var29.getKey();
                     List var20 = (List)var29.getValue();
                     if (var20 != null && !var20.isEmpty()) {
                        for (class_11670 var22 : var20) {
                           if (var22 != null && var22.comp_4506() instanceof class_10055) {
                              var26.computeIfAbsent(var19, k -> new ArrayList<>()).add(var22);
                           } else if (var22 != null) {
                              var24.computeIfAbsent(var19, k -> new ArrayList<>()).add(var22);
                           }
                        }
                     }
                  }
               }

               ArrayList var28 = new ArrayList();
               ArrayList var30 = new ArrayList();
               if (var13 != null) {
                  for (class_11744 var33 : var13) {
                     if (var33 != null && var33.comp_4615() != null && var33.comp_4615().comp_4506() instanceof class_10055) {
                        var30.add(var33);
                     } else if (var33 != null) {
                        var28.add(var33);
                     }
                  }
               }

               boolean var32 = false;
               if (!var24.isEmpty()) {
                  var32 = true;
                  this.method_73249(vertexConsumers, outlineVertexConsumers, var24, crumblingOverlayVertexConsumers);
               }

               if (!var28.isEmpty()) {
                  var32 = true;
                  var28.sort(Comparator.comparingDouble(cmd -> -cmd.comp_4618().lengthSquared()));
                  this.method_73250(vertexConsumers, outlineVertexConsumers, var28, crumblingOverlayVertexConsumers);
               }

               if (var32) {
                  vertexConsumers.method_22993();
               }

               boolean var34 = EdgeGlowPass.beginCapture(true);
               if (!var26.isEmpty()) {
                  this.method_73249(vertexConsumers, outlineVertexConsumers, var26, crumblingOverlayVertexConsumers);
               }

               if (!var30.isEmpty()) {
                  var30.sort(Comparator.comparingDouble(cmd -> -cmd.comp_4618().lengthSquared()));
                  this.method_73250(vertexConsumers, outlineVertexConsumers, var30, crumblingOverlayVertexConsumers);
               }

               vertexConsumers.method_22993();
               if (var34) {
                  EdgeGlowPass.endCapture();
                  if (var8) {
                     var7.composePass();
                  } else {
                     var6.composePass();
                  }
               }
            }
         }
      }
   }
}
