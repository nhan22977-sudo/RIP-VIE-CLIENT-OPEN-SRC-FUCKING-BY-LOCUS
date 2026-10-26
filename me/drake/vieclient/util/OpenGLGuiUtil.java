package me.drake.vieclient.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import me.drake.vieclient.render.ImmediateGuiRenderer;
import net.minecraft.class_332;

public final class OpenGLGuiUtil {
   private static final int MAX_CACHED_RADIUS = 64;
   private static final int[][] CACHED_CORNER_INSETS = new int[65][];
   private static final int[][] CACHED_CORNER_AA = new int[65][];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private OpenGLGuiUtil() {
   }

   public static void fillRoundedRect(class_332 ctx, int x, int y, int w, int h, int radius, int color) {
      fillRoundedRectVarying(ctx, x, y, w, h, radius, radius, radius, radius, color);
   }

   public static void fillRoundedRect(class_332 ctx, int x, int y, int w, int h, float radius, int color) {
      fillRoundedRect(ctx, x, y, w, h, Math.round(radius), color);
   }

   public static void fillRoundedRectVarying(class_332 ctx, int x, int y, int w, int h, int tl, int tr, int br, int bl, int color) {
      fillRoundedRectVarying(ctx, (RenderPipeline)null, x, y, w, h, tl, tr, br, bl, color);
   }

   public static void fillRoundedRectVarying(class_332 ctx, int x, int y, int w, int h, float tl, float tr, float br, float bl, int color) {
      fillRoundedRectVarying(ctx, x, y, w, h, Math.round(tl), Math.round(tr), Math.round(br), Math.round(bl), color);
   }

   public static void fillRoundedRectVarying(class_332 ctx, RenderPipeline pipeline, int x, int y, int w, int h, int tl, int tr, int br, int bl, int color) {
      if (w > 0 && h > 0) {
         int[] var11 = normalizeRadii(w, h, tl, tr, br, bl);
         tl = var11[0];
         tr = var11[1];
         br = var11[2];
         bl = var11[3];
         if ((tl | tr | br | bl) == 0) {
            ImmediateGuiRenderer.fillRect(ctx, x, y, x + w, y + h, color);
         } else {
            int var12 = color >>> (24) & (255);
            int var13 = color & (16777215);

            for (int var14 = 0; var14 < h; var14++) {
               int var15 = insetForRow(var14, h, tl, bl);
               int var16 = insetForRow(var14, h, tr, br);
               int var17 = x + var15;
               int var18 = x + w - var16;
               if (var18 > var17) {
                  ImmediateGuiRenderer.fillRect(ctx, var17, y + var14, var18, y + var14 + 1, color);
               }

               if (var12 > 0) {
                  int var19 = aaWeightForRow(var14, h, tl, bl);
                  if (var19 > 0 && var15 > 0) {
                     int var20 = edgeAaAlpha(var12, var19);
                     if (var20 > 1) {
                        int var21 = var20 << (24) | var13;
                        int var22 = var17 - 1;
                        ImmediateGuiRenderer.fillRect(ctx, var22, y + var14, var22 + 1, y + var14 + 1, var21);
                     }
                  }

                  int var28 = aaWeightForRow(var14, h, tr, br);
                  if (var28 > 0 && var16 > 0) {
                     int var29 = edgeAaAlpha(var12, var28);
                     if (var29 > 1) {
                        int var30 = var29 << (24) | var13;
                        int var23 = var18;
                        ImmediateGuiRenderer.fillRect(ctx, var23, y + var14, var23 + 1, y + var14 + 1, var30);
                     }
                  }
               }
            }
         }
      }
   }

   public static void fillRoundedRectSmooth(class_332 ctx, int x, int y, int w, int h, int radius, int color) {
      fillRoundedRectSmooth(ctx, (RenderPipeline)null, x, y, w, h, radius, color, 2);
   }

   public static void fillRoundedRectSmooth(class_332 ctx, int x, int y, int w, int h, int radius, int color, int softness) {
      fillRoundedRectSmooth(ctx, (RenderPipeline)null, x, y, w, h, radius, color, softness);
   }

   public static void fillRoundedRectSmooth(class_332 ctx, RenderPipeline pipeline, int x, int y, int w, int h, int radius, int color, int softness) {
      if (w > 0 && h > 0) {
         fillRoundedRectVarying(ctx, pipeline, x, y, w, h, radius, radius, radius, radius, color);
         int var9 = color >>> (24) & (255);
         if (var9 > 0 && softness > 0) {
            int var10 = color & (16777215);
            int var11 = Math.max(0, Math.min(3, softness));
            int var12 = Math.min(w, h);
            if (var12 <= (10)) {
               var11 = Math.min(var11, 1);
            }

            if (var12 <= (6) || radius <= 1) {
               var11 = 0;
            }

            if (var9 < (56)) {
               var11 = Math.min(var11, 1);
            }

            if (var9 < (26)) {
               var11 = 0;
            }

            for (int var13 = 1; var13 <= var11; var13++) {
               int var14 = Math.round(var9 * (0.15F / (var13 + 0.45F)));
               if (var14 < (2)) {
                  break;
               }

               int var15 = var14 << (24) | var10;
               fillRoundedRectVarying(
                  ctx,
                  pipeline,
                  x - var13,
                  y - var13,
                  w + var13 * (2),
                  h + var13 * (2),
                  radius + var13,
                  radius + var13,
                  radius + var13,
                  radius + var13,
                  var15
               );
            }
         }
      }
   }

   public static void strokeRoundedRect(class_332 ctx, int x, int y, int w, int h, int radius, int stroke, int color) {
      strokeRoundedRect(ctx, (RenderPipeline)null, x, y, w, h, radius, stroke, color);
   }

   public static void strokeRoundedRect(class_332 ctx, RenderPipeline pipeline, int x, int y, int w, int h, int radius, int stroke, int color) {
      if (w > 0 && h > 0 && stroke > 0) {
         int var9 = Math.max(1, stroke);

         for (int var10 = 0; var10 < var9; var10++) {
            int var11 = x + var10;
            int var12 = y + var10;
            int var13 = w - var10 * 2;
            int var14 = h - var10 * 2;
            if (var13 <= 0 || var14 <= 0) {
               break;
            }

            int var15 = Math.max(0, radius - var10);
            int[] var16 = normalizeRadii(var13, var14, var15, var15, var15, var15);
            int var17 = var16[0];
            int var18 = var16[1];
            int var19 = var16[2];
            int var20 = var16[3];
            if ((var17 | var18 | var19 | var20) == 0) {
               if (var13 != 1 && var14 != 1) {
                  ImmediateGuiRenderer.fillRect(ctx, var11, var12, var11 + var13, var12 + 1, color);
                  ImmediateGuiRenderer.fillRect(ctx, var11, var12 + var14 - 1, var11 + var13, var12 + var14, color);
                  ImmediateGuiRenderer.fillRect(ctx, var11, var12 + 1, var11 + 1, var12 + var14 - 1, color);
                  ImmediateGuiRenderer.fillRect(ctx, var11 + var13 - 1, var12 + 1, var11 + var13, var12 + var14 - 1, color);
               } else {
                  ImmediateGuiRenderer.fillRect(ctx, var11, var12, var11 + var13, var12 + var14, color);
               }
            } else {
               for (int var21 = 0; var21 < var14; var21++) {
                  int var22 = insetForRow(var21, var14, var17, var20);
                  int var23 = insetForRow(var21, var14, var18, var19);
                  int var24 = var11 + var22;
                  int var25 = var11 + var13 - var23;
                  if (var25 > var24) {
                     int var26 = var12 + var21;
                     if (var21 != 0 && var21 != var14 - 1) {
                        ImmediateGuiRenderer.fillRect(ctx, var24, var26, var24 + 1, var26 + 1, color);
                        if (var25 - var24 > 1) {
                           ImmediateGuiRenderer.fillRect(ctx, var25 - 1, var26, var25, var26 + 1, color);
                        }
                     } else {
                        ImmediateGuiRenderer.fillRect(ctx, var24, var26, var25, var26 + 1, color);
                     }
                  }
               }
            }
         }
      }
   }

   private static int[] normalizeRadii(int w, int h, int tl, int tr, int br, int bl) {
      int var6 = Math.min(w, h) / (2);
      tl = Math.max(0, Math.min(tl, var6));
      tr = Math.max(0, Math.min(tr, var6));
      br = Math.max(0, Math.min(br, var6));
      bl = Math.max(0, Math.min(bl, var6));
      float var7 = 1.0F;
      int var8 = tl + tr;
      if (var8 > w && var8 > 0) {
         var7 = Math.min(var7, (float)w / var8);
      }

      int var9 = bl + br;
      if (var9 > w && var9 > 0) {
         var7 = Math.min(var7, (float)w / var9);
      }

      int var10 = tl + bl;
      if (var10 > h && var10 > 0) {
         var7 = Math.min(var7, (float)h / var10);
      }

      int var11 = tr + br;
      if (var11 > h && var11 > 0) {
         var7 = Math.min(var7, (float)h / var11);
      }

      if (var7 < 1.0F) {
         tl = (int)Math.floor(tl * var7);
         tr = (int)Math.floor(tr * var7);
         br = (int)Math.floor(br * var7);
         bl = (int)Math.floor(bl * var7);
      }

      tl = Math.min(tl, Math.max(0, w - tr));
      tr = Math.min(tr, Math.max(0, w - tl));
      bl = Math.min(bl, Math.max(0, w - br));
      br = Math.min(br, Math.max(0, w - bl));
      tl = Math.min(tl, Math.max(0, h - bl));
      bl = Math.min(bl, Math.max(0, h - tl));
      tr = Math.min(tr, Math.max(0, h - br));
      br = Math.min(br, Math.max(0, h - tr));
      int[] var10000 = new int[4];
      var10000[0] = tl;
      var10000[1] = tr;
      var10000[2] = br;
      var10000[3] = bl;
      return var10000;
   }

   private static int insetForRow(int row, int h, int topRadius, int bottomRadius) {
      if (topRadius > 0 && row < topRadius) {
         return getCornerInsets(topRadius)[row];
      } else if (bottomRadius > 0 && row >= h - bottomRadius) {
         int var4 = h - 1 - row;
         return getCornerInsets(bottomRadius)[var4];
      } else {
         return 0;
      }
   }

   private static int aaWeightForRow(int row, int h, int topRadius, int bottomRadius) {
      if (topRadius > 0 && row < topRadius) {
         return getCornerAaWeights(topRadius)[row];
      } else if (bottomRadius > 0 && row >= h - bottomRadius) {
         int var4 = h - 1 - row;
         return getCornerAaWeights(bottomRadius)[var4];
      } else {
         return 0;
      }
   }

   private static int edgeAaAlpha(int baseAlpha, int weight) {
      if (baseAlpha > 0 && weight > 0) {
         float var2 = weight / 255.0F * 0.9F;
         int var3 = Math.round(baseAlpha * var2);
         if (var3 >= baseAlpha) {
            var3 = baseAlpha - 1;
         }

         return Math.max(0, var3);
      } else {
         return 0;
      }
   }

   private static int[] getCornerInsets(int radius) {
      if (radius <= 0) {
         return new int[0];
      }

      if (radius > (64)) {
         return buildCornerInsets(radius);
      }

      int[] var1 = CACHED_CORNER_INSETS[radius];
      if (var1 != null) {
         return var1;
      }

      synchronized (CACHED_CORNER_INSETS) {
         var1 = CACHED_CORNER_INSETS[radius];
         if (var1 == null) {
            CACHED_CORNER_INSETS[radius] = buildCornerInsets(radius);
            CACHED_CORNER_AA[radius] = buildCornerAaWeights(radius, CACHED_CORNER_INSETS[radius]);
            var1 = CACHED_CORNER_INSETS[radius];
         }

         return var1;
      }
   }

   private static int[] getCornerAaWeights(int radius) {
      if (radius <= 0) {
         return new int[0];
      }

      if (radius > (64)) {
         int[] var6 = buildCornerInsets(radius);
         return buildCornerAaWeights(radius, var6);
      }

      int[] var1 = CACHED_CORNER_AA[radius];
      if (var1 != null) {
         return var1;
      }

      synchronized (CACHED_CORNER_INSETS) {
         var1 = CACHED_CORNER_AA[radius];
         if (var1 == null) {
            CACHED_CORNER_INSETS[radius] = buildCornerInsets(radius);
            CACHED_CORNER_AA[radius] = buildCornerAaWeights(radius, CACHED_CORNER_INSETS[radius]);
            var1 = CACHED_CORNER_AA[radius];
         }

         return var1;
      }
   }

   private static int[] buildCornerInsets(int radius) {
      int[] var1 = new int[radius];
      double var2 = (double)radius * radius;

      for (int var4 = 0; var4 < radius; var4++) {
         double var5 = radius - var4 - 0.5;
         double var7 = radius - Math.sqrt(Math.max(0.0, var2 - var5 * var5));
         int var9 = (int)Math.ceil(var7 - 0.5);
         if (var9 < 0) {
            var9 = 0;
         }

         if (var9 > radius) {
            var9 = radius;
         }

         var1[var4] = var9;
      }

      return var1;
   }

   private static int[] buildCornerAaWeights(int radius, int[] insets) {
      int[] var2 = new int[radius];
      double var3 = (double)radius * radius;

      for (int var5 = 0; var5 < radius; var5++) {
         double var6 = radius - var5 - 0.5;
         double var8 = radius - Math.sqrt(Math.max(0.0, var3 - var6 * var6));
         double var10 = Math.max(0.0, Math.min(1.0, insets[var5] + 0.5 - var8));
         var2[var5] = (int)Math.round(var10 * 255.0);
      }

      return var2;
   }
}
