package me.drake.vieclient.modules.donut.autobase.navigation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class StandPositionFinder {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static class_2338 findBestStandPosition(class_1937 world, class_746 player, class_2338 targetPos, class_2350 targetFace, double maxReach) {
      List var6 = findCandidateStandPositions(world, player, targetPos, targetFace, maxReach);
      return var6.isEmpty() ? null : (class_2338)var6.get(0);
   }

   public static List findCandidateStandPositions(class_1937 world, class_746 player, class_2338 targetPos, class_2350 targetFace, double maxReach) {
      return findCandidateStandPositions(world, player, targetPos, targetFace, null, maxReach);
   }

   public static List findCandidateStandPositions(
      class_1937 world, class_746 player, class_2338 targetPos, class_2350 targetFace, class_2350 requiredPlayerFacing, double maxReach
   ) {
      ArrayList var7 = new ArrayList();
      if (world != null && targetPos != null) {
         class_243 var8 = targetFace != null
            ? class_243.method_24953(targetPos).method_1019(class_243.method_24954(targetFace.method_62675()).method_1021(0.48))
            : class_243.method_24953(targetPos);
         int var9 = (int)Math.ceil(maxReach);
         class_243 var10 = player != null ? new class_243(player.method_23317(), player.method_23318(), player.method_23321()) : var8;

         for (int var11 = -var9; var11 <= var9; var11++) {
            for (int var12 = -3; var12 <= 3; var12++) {
               for (int var13 = -var9; var13 <= var9; var13++) {
                  class_2338 var14 = targetPos.method_10069(var11, var12, var13);
                  if (!var14.equals(targetPos)) {
                     class_2338 var15 = var14.method_10074();
                     class_2680 var16 = world.method_8320(var15);
                     if (!var16.method_26220(world, var15).method_1110()
                        && var16.method_26204() != class_2246.field_10164
                        && var16.method_26204() != class_2246.field_10036) {
                        class_2680 var17 = world.method_8320(var14);
                        class_2680 var18 = world.method_8320(var14.method_10084());
                        if (var17.method_26194(world, var14, class_3726.method_16194()).method_1110()
                           && var18.method_26194(world, var14.method_10084(), class_3726.method_16194()).method_1110()) {
                           class_243 var19 = new class_243(var14.method_10263() + 0.5, var14.method_10264() + 1.27, var14.method_10260() + 0.5);
                           double var20 = var19.method_1022(var8);
                           if (!(var20 > maxReach) && !(var20 < 0.6)) {
                              class_3965 var22 = world.method_17742(new class_3959(var19, var8, class_3960.field_17559, class_242.field_1348, player));
                              if (var22.method_17783() != class_240.field_1332
                                 || var22.method_17777().equals(targetPos)
                                 || targetFace != null && var22.method_17777().equals(targetPos.method_10093(targetFace.method_10153()))
                                 || world.method_8320(var22.method_17777()).method_26220(world, var22.method_17777()).method_1110()) {
                                 var7.add(var14);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         var7.sort(Comparator.comparingDouble(p -> {
            double var4 = p.method_19770(var10);
            if (requiredPlayerFacing != null) {
               double var6 = targetPos.method_10263() + 0.5 - (p.method_10263() + 0.5);
               double var8x = targetPos.method_10260() + 0.5 - (p.method_10260() + 0.5);
               float var10x = (float)Math.toDegrees(Math.atan2(var8x, var6)) - 90.0F;
               class_2350 var11x = yawToDirection(var10x);
               if (var11x == requiredPlayerFacing) {
                  var4 -= 10000.0;
               }
            }

            return var4;
         }));
         return var7;
      } else {
         return var7;
      }
   }

   private static class_2350 yawToDirection(float yaw) {
      float var1 = class_3532.method_15393(yaw);
      if (var1 >= -45.0F && var1 < 45.0F) {
         return class_2350.field_11035;
      } else if (var1 >= 45.0F && var1 < 135.0F) {
         return class_2350.field_11039;
      } else {
         return var1 >= -135.0F && var1 < -45.0F ? class_2350.field_11034 : class_2350.field_11043;
      }
   }
}
