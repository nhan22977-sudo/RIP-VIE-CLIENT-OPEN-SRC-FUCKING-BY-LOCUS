package me.drake.vieclient.modules.client.hud;

import java.nio.charset.StandardCharsets;

public final class HudEditorGeometry {
   public static final float MIN_SCALE = 0.5F;
   public static final float MAX_SCALE = 3.0F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private HudEditorGeometry() {
   }

   public static float clampScale(float value) {
      return !Float.isFinite(value) ? 1.0F : Math.max(0.5F, Math.min(3.0F, value));
   }

   public static HudEditorGeometry.Corner hitCorner(HudEditorGeometry.Rect rect, double px, double py, float radius) {
      HudEditorGeometry.Corner var6 = null;
      double var7 = Double.MAX_VALUE;

      for (HudEditorGeometry.Corner var12 : HudEditorGeometry.Corner.values()) {
         float var13 = switch (var12) {
            case TOP_LEFT, BOTTOM_LEFT -> rect.x();
            case TOP_RIGHT, BOTTOM_RIGHT -> rect.right();
         };

         float var14 = switch (var12) {
            case TOP_LEFT, TOP_RIGHT -> rect.y();
            case BOTTOM_LEFT, BOTTOM_RIGHT -> rect.bottom();
         };
         double var15 = px - var13;
         double var17 = py - var14;
         if (!(Math.abs(var15) > radius) && !(Math.abs(var17) > radius)) {
            double var19 = var15 * var15 + var17 * var17;
            if (var19 < var7) {
               var6 = var12;
               var7 = var19;
            }
         }
      }

      return var6;
   }

   public static HudEditorGeometry.ResizeResult resize(
      HudEditorGeometry.Rect start, float startScale, HudEditorGeometry.Corner corner, double pointerX, double pointerY, float screenWidth, float screenHeight
   ) {
      float var9 = clampScale(startScale);
      float var10 = start.width() / var9;
      float var11 = start.height() / var9;
      if (!(var10 <= 0.0F) && !(var11 <= 0.0F)) {
         float var12 = switch (corner) {
            case TOP_LEFT, BOTTOM_LEFT -> start.right();
            case TOP_RIGHT, BOTTOM_RIGHT -> start.x();
         };

         float var13 = switch (corner) {
            case TOP_LEFT, TOP_RIGHT -> start.bottom();
            case BOTTOM_LEFT, BOTTOM_RIGHT -> start.y();
         };

         float var14 = switch (corner) {
            case TOP_LEFT, BOTTOM_LEFT -> start.x();
            case TOP_RIGHT, BOTTOM_RIGHT -> start.right();
         };

         float var15 = switch (corner) {
            case TOP_LEFT, TOP_RIGHT -> start.y();
            case BOTTOM_LEFT, BOTTOM_RIGHT -> start.bottom();
         };
         double var16 = Math.hypot(var14 - var12, var15 - var13);
         double var18 = Math.hypot(pointerX - var12, pointerY - var13);
         if (var16 <= 0.001) {
            return new HudEditorGeometry.ResizeResult(start, var9);
         }

         float var20 = clampScale((float)(var9 * (var18 / var16)));
         float var21 = var10 * var20;
         float var22 = var11 * var20;

         float var23 = switch (corner) {
            case TOP_LEFT, BOTTOM_LEFT -> var12 - var21;
            case TOP_RIGHT, BOTTOM_RIGHT -> var12;
         };

         float var24 = switch (corner) {
            case TOP_LEFT, TOP_RIGHT -> var13 - var22;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> var13;
         };
         float var25 = 1.0F;
         float var26 = 1.0F;
         float var27 = Math.max(var25, screenWidth - var21 - 1.0F);
         float var28 = Math.max(var26, screenHeight - var22 - 1.0F);
         float var29 = Math.max(var25, Math.min(var27, var23));
         float var30 = Math.max(var26, Math.min(var28, var24));
         return new HudEditorGeometry.ResizeResult(new HudEditorGeometry.Rect(var29, var30, var21, var22), var20);
      } else {
         return new HudEditorGeometry.ResizeResult(start, var9);
      }
   }

   public static HudEditorGeometry.Rect clampToScreen(HudEditorGeometry.Rect rect, float screenWidth, float screenHeight) {
      float var3 = 1.0F;
      float var4 = 1.0F;
      float var5 = Math.max(var3, screenWidth - rect.width() - 1.0F);
      float var6 = Math.max(var4, screenHeight - rect.height() - 1.0F);
      float var7 = Math.max(var3, Math.min(var5, rect.x()));
      float var8 = Math.max(var4, Math.min(var6, rect.y()));
      return new HudEditorGeometry.Rect(var7, var8, rect.width(), rect.height());
   }

   public enum Corner {
      TOP_LEFT,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_RIGHT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HudEditorGeometry.Corner[] $values() {
         HudEditorGeometry.Corner[] var10000 = new HudEditorGeometry.Corner[4];
         var10000[0] = TOP_LEFT;
         var10000[1] = TOP_RIGHT;
         var10000[2] = BOTTOM_LEFT;
         var10000[3] = BOTTOM_RIGHT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-123);
         var10002[5] = (byte)(22);
         var10002[6] = (byte)(-94);
         var10002[7] = (byte)(104);
         TOP_LEFT = new HudEditorGeometry.Corner();
         var10002 = new byte[9];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-101);
         var10002[5] = (byte)(26);
         var10002[6] = (byte)(-93);
         var10002[7] = (byte)(116);
         var10002[8] = (byte)(3);
         TOP_RIGHT = new HudEditorGeometry.Corner();
         var10002 = new byte[11];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-122);
         var10002[5] = (byte)(30);
         var10002[6] = (byte)(-69);
         var10002[7] = (byte)(112);
         var10002[8] = (byte)(18);
         var10002[9] = (byte)(-28);
         var10002[10] = (byte)(-80);
         BOTTOM_LEFT = new HudEditorGeometry.Corner();
         var10002 = new byte[12];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-122);
         var10002[5] = (byte)(30);
         var10002[6] = (byte)(-69);
         var10002[7] = (byte)(110);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-27);
         var10002[10] = (byte)(-84);
         var10002[11] = (byte)(-94);
         BOTTOM_RIGHT = new HudEditorGeometry.Corner();
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
   }

   public record Rect(float x, float y, float width, float height) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public float right() {
         return this.x + this.width;
      }

      public float bottom() {
         return this.y + this.height;
      }

      public boolean contains(double px, double py) {
         return px >= this.x && px <= this.right() && py >= this.y && py <= this.bottom();
      }
   }

   public record ResizeResult(HudEditorGeometry.Rect rect, float scale) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
