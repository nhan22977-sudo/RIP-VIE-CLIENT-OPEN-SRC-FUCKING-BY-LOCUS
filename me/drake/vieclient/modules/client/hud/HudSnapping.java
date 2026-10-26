package me.drake.vieclient.modules.client.hud;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class HudSnapping {
   public static final float DEFAULT_THRESHOLD = 6.0F;
   public static final float STANDARD_GAP = 6.0F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private HudSnapping() {
   }

   public static HudSnapping.Result snap(float rawX, float rawY, float width, float height, float screenWidth, float screenHeight, Map otherWidgets) {
      return snap(rawX, rawY, width, height, screenWidth, screenHeight, otherWidgets, 6.0F);
   }

   public static HudSnapping.Result snap(
      float rawX, float rawY, float width, float height, float screenWidth, float screenHeight, Map otherWidgets, float threshold
   ) {
      float var8 = rawX;
      float var9 = threshold + 0.001F;
      HudSnapping.Guide var10 = null;
      float var11 = rawY;
      float var12 = threshold + 0.001F;
      HudSnapping.Guide var13 = null;
      float var14 = screenWidth / 2.0F;
      float var15 = rawX + width / 2.0F;
      float var16 = Math.abs(var15 - var14);
      if (var16 <= threshold && var16 < var9) {
         var8 = var14 - width / 2.0F;
         var9 = var16;
         var10 = new HudSnapping.Guide(
            true, var14, true, "Screen Center X"
         );
      }

      float var17 = screenHeight / 2.0F;
      float var18 = rawY + height / 2.0F;
      float var19 = Math.abs(var18 - var17);
      if (var19 <= threshold && var19 < var12) {
         var11 = var17 - height / 2.0F;
         var12 = var19;
         var13 = new HudSnapping.Guide(
            false, var17, true, "Screen Center Y"
         );
      }

      float var20 = Math.abs(rawX - var14);
      if (var20 <= threshold && var20 < var9) {
         var8 = var14;
         var9 = var20;
         var10 = new HudSnapping.Guide(
            true,
            var14,
            true,
            "Screen Center X (Left Edge)"
         );
      }

      float var21 = Math.abs(rawX + width - var14);
      if (var21 <= threshold && var21 < var9) {
         var8 = var14 - width;
         var9 = var21;
         var10 = new HudSnapping.Guide(
            true,
            var14,
            true,
            "Screen Center X (Right Edge)"
         );
      }

      float var22 = Math.abs(rawY - var17);
      if (var22 <= threshold && var22 < var12) {
         var11 = var17;
         var12 = var22;
         var13 = new HudSnapping.Guide(
            false,
            var17,
            true,
            "Screen Center Y (Top Edge)"
         );
      }

      float var23 = Math.abs(rawY + height - var17);
      if (var23 <= threshold && var23 < var12) {
         var11 = var17 - height;
         var12 = var23;
         var13 = new HudSnapping.Guide(
            false,
            var17,
            true,
            "Screen Center Y (Bottom Edge)"
         );
      }

      if (otherWidgets != null) {
         for (Entry var25 : otherWidgets.entrySet()) {
            float[] var26 = (float[])var25.getValue();
            if (var26 != null && var26.length >= 4) {
               float var27 = var26[0];
               float var28 = var26[1];
               float var29 = var26[2];
               float var30 = var26[3];
               if (!(var29 <= 0.0F) && !(var30 <= 0.0F)) {
                  String var31 = (String)var25.getKey();
                  float var32 = var27 + var29 / 2.0F;
                  float var33 = var32 - width / 2.0F;
                  float var34 = Math.abs(rawX - var33);
                  if (var34 <= threshold && var34 < var9) {
                     var8 = var33;
                     var9 = var34;
                     var10 = new HudSnapping.Guide(true, var32, true, var31);
                  }

                  float var35 = Math.abs(rawX - var27);
                  if (var35 <= threshold && var35 < var9) {
                     var8 = var27;
                     var9 = var35;
                     var10 = new HudSnapping.Guide(true, var27, false, var31);
                  }

                  float var36 = var27 + var29 - width;
                  float var37 = Math.abs(rawX - var36);
                  if (var37 <= threshold && var37 < var9) {
                     var8 = var36;
                     var9 = var37;
                     var10 = new HudSnapping.Guide(true, var27 + var29, false, var31);
                  }

                  float var38 = var27 + var29;
                  float var39 = Math.abs(rawX - var38);
                  if (var39 <= threshold && var39 < var9) {
                     var8 = var38;
                     var9 = var39;
                     var10 = new HudSnapping.Guide(true, var27 + var29, false, var31);
                  }

                  float var40 = var27 - width;
                  float var41 = Math.abs(rawX - var40);
                  if (var41 <= threshold && var41 < var9) {
                     var8 = var40;
                     var9 = var41;
                     var10 = new HudSnapping.Guide(true, var27, false, var31);
                  }

                  float var42 = var27 + var29 + 6.0F;
                  float var43 = Math.abs(rawX - var42);
                  if (var43 <= threshold && var43 < var9) {
                     var8 = var42;
                     var9 = var43;
                     var10 = new HudSnapping.Guide(true, var42, false, var31);
                  }

                  float var44 = var27 - width - 6.0F;
                  float var45 = Math.abs(rawX - var44);
                  if (var45 <= threshold && var45 < var9) {
                     var8 = var44;
                     var9 = var45;
                     var10 = new HudSnapping.Guide(true, var27 - 6.0F, false, var31);
                  }

                  float var46 = var28 + var30 / 2.0F;
                  float var47 = var46 - height / 2.0F;
                  float var48 = Math.abs(rawY - var47);
                  if (var48 <= threshold && var48 < var12) {
                     var11 = var47;
                     var12 = var48;
                     var13 = new HudSnapping.Guide(false, var46, true, var31);
                  }

                  float var49 = Math.abs(rawY - var28);
                  if (var49 <= threshold && var49 < var12) {
                     var11 = var28;
                     var12 = var49;
                     var13 = new HudSnapping.Guide(false, var28, false, var31);
                  }

                  float var50 = var28 + var30 - height;
                  float var51 = Math.abs(rawY - var50);
                  if (var51 <= threshold && var51 < var12) {
                     var11 = var50;
                     var12 = var51;
                     var13 = new HudSnapping.Guide(false, var28 + var30, false, var31);
                  }

                  float var52 = var28 + var30;
                  float var53 = Math.abs(rawY - var52);
                  if (var53 <= threshold && var53 < var12) {
                     var11 = var52;
                     var12 = var53;
                     var13 = new HudSnapping.Guide(false, var28 + var30, false, var31);
                  }

                  float var54 = var28 - height;
                  float var55 = Math.abs(rawY - var54);
                  if (var55 <= threshold && var55 < var12) {
                     var11 = var54;
                     var12 = var55;
                     var13 = new HudSnapping.Guide(false, var28, false, var31);
                  }

                  float var56 = var28 + var30 + 6.0F;
                  float var57 = Math.abs(rawY - var56);
                  if (var57 <= threshold && var57 < var12) {
                     var11 = var56;
                     var12 = var57;
                     var13 = new HudSnapping.Guide(false, var56, false, var31);
                  }

                  float var58 = var28 - height - 6.0F;
                  float var59 = Math.abs(rawY - var58);
                  if (var59 <= threshold && var59 < var12) {
                     var11 = var58;
                     var12 = var59;
                     var13 = new HudSnapping.Guide(false, var28 - 6.0F, false, var31);
                  }
               }
            }
         }
      }

      ArrayList var60 = new ArrayList(2);
      if (var10 != null) {
         var60.add(var10);
      }

      if (var13 != null) {
         var60.add(var13);
      }

      return new HudSnapping.Result(var8, var11, var60);
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

   public record Guide(boolean vertical, float position, boolean center, String reference) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public Guide(boolean vertical, float position, boolean center) {
         this(verticalx, positionx, centerx, null);
      }
   }

   public record Result(float x, float y, List<HudSnapping.Guide> guides) {
      public static final HudSnapping.Result NONE = new HudSnapping.Result(0.0F, 0.0F, Collections.emptyList());
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
