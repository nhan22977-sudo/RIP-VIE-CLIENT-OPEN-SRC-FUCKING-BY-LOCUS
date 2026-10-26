package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_1972;
import net.minecraft.class_2246;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_4482;
import net.minecraft.class_6880;

public final class SusGrowthScan {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SusGrowthScan() {
   }

   public static boolean evaluate(
      class_2818 chunk,
      boolean checkBeeNest,
      boolean checkVine,
      boolean checkCaveVine,
      boolean checkKelp,
      boolean checkBamboo,
      boolean checkCacao,
      boolean checkDripstone,
      boolean checkSeagrass
   ) {
      return evaluateChunk(chunk, checkBeeNest, checkVine, checkCaveVine, checkKelp, checkBamboo, checkCacao, checkDripstone, checkSeagrass).isSus();
   }

   public static SusGrowthScan.GrowthEvaluation evaluateChunk(
      class_2818 chunk,
      boolean checkBeeNest,
      boolean checkVine,
      boolean checkCaveVine,
      boolean checkKelp,
      boolean checkBamboo,
      boolean checkCacao,
      boolean checkDripstone,
      boolean checkSeagrass
   ) {
      if (chunk == null) {
         return new SusGrowthScan.GrowthEvaluation(false, 0, "");
      }

      int var9 = 0;
      boolean var10 = false;
      String var11 = "";
      boolean var12 = false;
      boolean var13 = false;
      boolean var14 = false;

      try {
         class_6880 var15 = chunk.method_16359(2, 4, 2);
         if (var15 != null) {
            var12 = var15.method_40225(class_1972.field_9423)
               || var15.method_40225(class_1972.field_9446)
               || var15.method_40225(class_1972.field_9408)
               || var15.method_40225(class_1972.field_9441)
               || var15.method_40225(class_1972.field_9439)
               || var15.method_40225(class_1972.field_9467)
               || var15.method_40225(class_1972.field_9470)
               || var15.method_40225(class_1972.field_9435)
               || var15.method_40225(class_1972.field_9418);
            var13 = var15.method_40225(class_1972.field_9417) || var15.method_40225(class_1972.field_35118) || var15.method_40225(class_1972.field_9440);
            var14 = var15.method_40225(class_1972.field_29218);
         }
      } catch (Throwable var45) {
      }

      if (checkBeeNest) {
         SusGrowthScan.BeeEvaluation var46 = evaluateBees(chunk);
         if (var46.score() > 0) {
            var9 += var46.score();
            if (var46.isDefiniteSus()) {
               var10 = true;
               var11 = var46.signal();
            }
         }
      }

      class_2826[] var47 = chunk.method_12006();
      if (var47 != null && var47.length != 0) {
         int var16 = chunk.method_31607();
         int var17 = 0;
         int var18 = 0;
         int var19 = 0;
         int var20 = 0;
         int var21 = 0;
         int var22 = 0;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         int var26 = 0;
         int var27 = 0;

         for (int var28 = 0; var28 < var47.length; var28++) {
            class_2826 var29 = var47[var28];
            if (var29 != null && !var29.method_38292()) {
               boolean[] var30 = new boolean[7];
               boolean var31 = var29.method_19523(state -> {
                  boolean var9x = false;
                  if (checkVine && state.method_27852(class_2246.field_10597)) {
                     var30[0] = true;
                     var9x = true;
                  }

                  if (checkCaveVine && (state.method_27852(class_2246.field_28675) || state.method_27852(class_2246.field_28676))) {
                     var30[1] = true;
                     var9x = true;
                  }

                  if (checkKelp && state.method_27852(class_2246.field_9993)) {
                     var30[2] = true;
                     var9x = true;
                  }

                  if (checkBamboo && state.method_27852(class_2246.field_10211)) {
                     var30[3] = true;
                     var9x = true;
                  }

                  if (checkCacao && state.method_27852(class_2246.field_10302)) {
                     var30[4] = true;
                     var9x = true;
                  }

                  if (checkDripstone && state.method_27852(class_2246.field_28048)) {
                     var30[5] = true;
                     var9x = true;
                  }

                  if (checkSeagrass && state.method_27852(class_2246.field_10238)) {
                     var30[6] = true;
                     var9x = true;
                  }

                  return var9x;
               });
               if (var31) {
                  boolean var32 = var30[0];
                  boolean var33 = var30[1];
                  boolean var34 = var30[2];
                  boolean var35 = var30[3];
                  boolean var36 = var30[4];
                  boolean var37 = var30[5];
                  boolean var38 = var30[6];
                  int var39 = var16 + (var28 << 4);

                  for (int var40 = 0; var40 < 16; var40++) {
                     int var41 = var39 + var40;

                     for (int var42 = 0; var42 < 16; var42++) {
                        for (int var43 = 0; var43 < 16; var43++) {
                           class_2680 var44 = var29.method_12254(var43, var40, var42);
                           if (var32 && var44.method_27852(class_2246.field_10597)) {
                              if (var41 <= 45) {
                                 var23++;
                              } else if (!var13) {
                                 var24++;
                              }
                           }

                           if (var33
                              && (var44.method_27852(class_2246.field_28675) || var44.method_27852(class_2246.field_28676))
                              && var44.method_28498(class_2741.field_28716)
                              && (Boolean)var44.method_11654(class_2741.field_28716)) {
                              var25++;
                           }

                           if (var34
                              && var44.method_27852(class_2246.field_9993)
                              && var44.method_28498(class_2741.field_12517)
                              && (Integer)var44.method_11654(class_2741.field_12517) >= 24) {
                              if (var41 <= 25) {
                                 var21++;
                              } else if (!var12) {
                                 var22++;
                              }
                           }

                           if (var35 && var44.method_27852(class_2246.field_10211)) {
                              if (var41 <= 55) {
                                 var19++;
                              } else if (!var13) {
                                 var20++;
                              }
                           }

                           if (var36
                              && var44.method_27852(class_2246.field_10302)
                              && var44.method_28498(class_2741.field_12556)
                              && (Integer)var44.method_11654(class_2741.field_12556) == 2) {
                              if (var41 <= 55) {
                                 var17++;
                              } else if (!var13) {
                                 var18++;
                              }
                           }

                           if (var37 && var44.method_27852(class_2246.field_28048)) {
                              var26++;
                           }

                           if (var38 && var44.method_27852(class_2246.field_10238) && !var12) {
                              var27++;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (var17 > 0) {
            var9 += Math.min(80, 40 + var17 * 5);
            if (var17 >= 4) {
               var10 = true;
               if (var11.isEmpty()) {
                  var11 = "Underground Cocoa Farm";
               }
            }
         }

         if (var19 >= 5) {
            var9 += Math.min(80, 30 + var19 * 2);
            if (var19 >= 10) {
               var10 = true;
               if (var11.isEmpty()) {
                  var11 = "Underground Bamboo Farm";
               }
            }
         }

         if (var21 >= 3) {
            var9 += Math.min(75, 30 + var21 * 3);
            if (var21 >= 8) {
               var10 = true;
               if (var11.isEmpty()) {
                  var11 = "Underground Kelp Elevator";
               }
            }
         }

         if (var23 >= 15) {
            var9 += Math.min(40, var23);
            if (var23 >= 30) {
               var10 = true;
               if (var11.isEmpty()) {
                  var11 = "Underground Vines";
               }
            }
         }

         if (var25 >= (var14 ? 14 : 4)) {
            var9 += Math.min(25, var25 * 2);
            if (var11.isEmpty()) {
               var11 = "Cave Vine Berries";
            }
         }

         if (!var13 && var18 >= 8) {
            var9 += Math.min(15, var18);
         }

         if (!var13 && var20 >= 30) {
            var9 += Math.min(15, var20 / 5);
         }

         if (!var12 && var22 >= 10) {
            var9 += Math.min(10, var22 / 2);
         }

         if (!var13 && var24 >= 35) {
            var9 += Math.min(15, var24 / 4);
         }

         if (var26 >= 30) {
            var9 += Math.min(15, var26 / 6);
            if (var11.isEmpty()) {
               var11 = "Pointed Dripstone";
            }
         }

         if (!var12 && var27 >= 15) {
            var9 += Math.min(10, var27 / 2);
            if (var11.isEmpty()) {
               var11 = "Tall Seagrass";
            }
         }

         if (var9 >= 100) {
            var10 = true;
         }

         return new SusGrowthScan.GrowthEvaluation(var10, var9, var11);
      } else {
         return new SusGrowthScan.GrowthEvaluation(var10, var9, var11);
      }
   }

   private static SusGrowthScan.BeeEvaluation evaluateBees(class_2818 chunk) {
      int var1 = 0;

      for (class_2586 var3 : chunk.method_12214().values()) {
         if (var3 instanceof class_4482) {
            class_2680 var4 = var3.method_11010();
            if (var4 != null && var4.method_28498(class_2741.field_20432) && (Integer)var4.method_11654(class_2741.field_20432) > 0) {
               var1++;
            }
         }
      }

      if (var1 >= (2)) {
         int var5 = 90;
         byte[] var6 = new byte[17];
         var6[0] = (byte)(127);
         var6[1] = (byte)(-17);
         var6[2] = (byte)(112);
         var6[3] = (byte)(-86);
         var6[4] = (byte)(-7);
         var6[5] = (byte)(81);
         var6[6] = 0;
         var6[7] = (byte)(-53);
         var6[8] = (byte)(41);
         var6[9] = (byte)(-42);
         var6[10] = (byte)(-60);
         var6[11] = (byte)(-73);
         var6[12] = (byte)(-72);
         var6[13] = (byte)(-51);
         var6[14] = (byte)(7);
         var6[15] = (byte)(-92);
         var6[16] = (byte)(27);
         return new SusGrowthScan.BeeEvaluation(true, var5, $$v_s(var6, -8769174601194024937L));
      } else if (var1 == 1) {
         int var10003 = 15;
         byte[] var10004 = new byte[21];
         var10004[0] = (byte)(97);
         var10004[1] = (byte)(-17);
         var10004[2] = (byte)(110);
         var10004[3] = (byte)(-72);
         var10004[4] = (byte)(-15);
         var10004[5] = (byte)(31);
         var10004[6] = (byte)(43);
         var10004[7] = (byte)(-114);
         var10004[8] = (byte)(13);
         var10004[9] = (byte)(-53);
         var10004[10] = (byte)(-120);
         var10004[11] = (byte)(-110);
         var10004[12] = (byte)(-24);
         var10004[13] = (byte)(-26);
         var10004[14] = (byte)(3);
         var10004[15] = (byte)(-77);
         var10004[16] = (byte)(66);
         var10004[17] = (byte)(4);
         var10004[18] = (byte)(-87);
         var10004[19] = (byte)(-35);
         var10004[20] = (byte)(84);
         return new SusGrowthScan.BeeEvaluation(false, var10003, $$v_s(var10004, -8769174601194024937L));
      } else {
         return new SusGrowthScan.BeeEvaluation(false, 0, "");
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   private record BeeEvaluation(boolean isDefiniteSus, int score, String signal) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record GrowthEvaluation(boolean isSus, int score, String primarySignal) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
