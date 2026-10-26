package me.drake.vieclient.render;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.class_5595;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

public final class ImmediateGuiRenderer {
   private static final VieMeshBuilder QUAD = new VieMeshBuilder(ViePipelines.HUD_COLOR);
   private static final VieMeshBuilder QUAD_TEX = new VieMeshBuilder(ViePipelines.HUD_TEX);
   private static final Vector2f POINT_0 = new Vector2f();
   private static final Vector2f POINT_1 = new Vector2f();
   private static final Vector2f POINT_2 = new Vector2f();
   private static final Vector2f POINT_3 = new Vector2f();
   private static boolean disabled;
   private static boolean warned;
   private static boolean warnedTex;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ImmediateGuiRenderer() {
   }

   public static void reset() {
      disabled = false;
      warned = false;
      warnedTex = false;
   }

   public static void fillRect(class_332 ctx, int x1, int y1, int x2, int y2, int argb) {
      if (ctx != null && !disabled) {
         if ((argb >>> (24) & (255)) != 0) {
            int var6 = Math.min(x1, x2);
            int var7 = Math.min(y1, y2);
            int var8 = Math.max(x1, x2);
            int var9 = Math.max(y1, y2);
            if (var8 > var6 && var9 > var7) {
               drawQuad(ctx, var6, var7, var8, var9, argb, argb, argb, argb);
            }
         }
      }
   }

   public static void fillGradientRect(class_332 ctx, int x, int y, int w, int h, int startColor, int endColor, boolean vertical) {
      if (ctx != null && !disabled) {
         if (w > 0 && h > 0) {
            int var8 = x + w;
            int var9 = y + h;
            if (vertical) {
               drawQuad(ctx, x, y, var8, var9, startColor, startColor, endColor, endColor);
            } else {
               drawQuad(ctx, x, y, var8, var9, startColor, endColor, endColor, startColor);
            }
         }
      }
   }

   public static void drawLine(class_332 ctx, float x1, float y1, float x2, float y2, float stroke, int argb) {
      if (ctx != null && !disabled) {
         if ((argb >>> (24) & (255)) != 0) {
            float var7 = x2 - x1;
            float var8 = y2 - y1;
            float var9 = (float)Math.sqrt(var7 * var7 + var8 * var8);
            float var10 = Math.max(0.5F, stroke * 0.5F);
            if (var9 < 1.0E-4F) {
               fillRect(ctx, Math.round(x1 - var10), Math.round(y1 - var10), Math.round(x1 + var10), Math.round(y1 + var10), argb);
            } else {
               float var11 = -var8 / var9 * var10;
               float var12 = var7 / var9 * var10;
               float var13 = x1 + var11;
               float var14 = y1 + var12;
               float var15 = x2 + var11;
               float var16 = y2 + var12;
               float var17 = x2 - var11;
               float var18 = y2 - var12;
               float var19 = x1 - var11;
               float var20 = y1 - var12;
               drawQuad(ctx, var13, var14, var15, var16, var17, var18, var19, var20, argb, argb, argb, argb);
            }
         }
      }
   }

   public static void strokeRect(class_332 ctx, int x, int y, int w, int h, int stroke, int argb) {
      if (ctx != null && !disabled && w > 0 && h > 0) {
         int var7 = Math.max(1, stroke);
         fillRect(ctx, x, y, x + w, y + var7, argb);
         fillRect(ctx, x, y + h - var7, x + w, y + h, argb);
         fillRect(ctx, x, y + var7, x + var7, y + h - var7, argb);
         fillRect(ctx, x + w - var7, y + var7, x + w, y + h - var7, argb);
      }
   }

   private static void drawQuad(class_332 ctx, float x1, float y1, float x2, float y2, int cTopLeft, int cTopRight, int cBottomRight, int cBottomLeft) {
      drawQuad(ctx, x1, y1, x2, y1, x2, y2, x1, y2, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
   }

   private static void drawQuad(class_332 ctx, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int c1, int c2, int c3, int c4) {
      try {
         int var13 = ctx.method_51421();
         int var14 = ctx.method_51443();
         if (var13 <= 0 || var14 <= 0) {
            return;
         }

         Matrix3x2fStack var15 = ctx.method_51448();
         Vector2f var16 = var15.transformPosition(x1, y1, POINT_0);
         Vector2f var17 = var15.transformPosition(x2, y2, POINT_1);
         Vector2f var18 = var15.transformPosition(x3, y3, POINT_2);
         Vector2f var19 = var15.transformPosition(x4, y4, POINT_3);
         QUAD.beginNoCamera();
         int var20 = QUAD.vec3(toNdcX(var16.x, var13), toNdcY(var16.y, var14), 0.0).color(c1).next();
         int var21 = QUAD.vec3(toNdcX(var17.x, var13), toNdcY(var17.y, var14), 0.0).color(c2).next();
         int var22 = QUAD.vec3(toNdcX(var18.x, var13), toNdcY(var18.y, var14), 0.0).color(c3).next();
         int var23 = QUAD.vec3(toNdcX(var19.x, var13), toNdcY(var19.y, var14), 0.0).color(c4).next();
         QUAD.quad(var20, var21, var22, var23);
         QUAD.end();
         if (!QUAD.hasData()) {
            return;
         }

         class_310 var24 = class_310.method_1551();
         if (var24 == null) {
            return;
         }

         class_276 var25 = var24.method_1522();
         VieMeshBuilder.GpuBufferPair var26 = QUAD.getBuffers();
         RenderPass var27 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
            byte[] var10000 = new byte[13];
            var10000[0] = (byte)(103);
            var10000[1] = (byte)(-13);
            var10000[2] = (byte)(121);
            var10000[3] = (byte)(-2);
            var10000[4] = (byte)(118);
            var10000[5] = (byte)(-101);
            var10000[6] = (byte)(-45);
            var10000[7] = (byte)(27);
            var10000[8] = (byte)(26);
            var10000[9] = (byte)(-51);
            var10000[10] = (byte)(-120);
            var10000[11] = (byte)(-103);
            var10000[12] = (byte)(20);
            return $$v_s(var10000, 1410770756529541140L);
         }, var25.method_71639(), OptionalInt.empty());
         RenderPass var28 = var27;

         try {
            var27.setPipeline(ViePipelines.HUD_COLOR);
            var27.setVertexBuffer(0, var26.vertexBuffer);
            var27.setIndexBuffer(var26.indexBuffer, class_5595.field_27373);
            var27.drawIndexed(0, 0, QUAD.getIndicesCount(), 1);
         } catch (Throwable var32) {
            if (var28 != null) {
               try {
                  var28.close();
               } catch (Throwable var31) {
                  var32.addSuppressed(var31);
               }
            }

            throw var32;
         }

         if (var28 != null) {
            var28.close();
         }
      } catch (Throwable var33) {
         disabled = true;
         if (!warned) {
            warned = true;
            VieClientMod.LOGGER
               .warn(
                  "[VieClient] Immediate GUI color renderer disabled.",
                  var33
               );
         }
      }
   }

   private static float toNdcX(float x, int screenW) {
      return x / screenW * 2.0F - 1.0F;
   }

   private static float toNdcY(float y, int screenH) {
      return 1.0F - y / screenH * 2.0F;
   }

   public static void drawTriangle(class_332 ctx, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
      if (ctx != null && !disabled) {
         if ((color >>> 24 & 0xFF) != 0) {
            int var8 = ctx.method_51421();
            int var9 = ctx.method_51443();
            if (var8 > 0 && var9 > 0) {
               try {
                  Matrix3x2fStack var10 = ctx.method_51448();
                  Vector2f var11 = var10.transformPosition(x1, y1, POINT_0);
                  Vector2f var12 = var10.transformPosition(x2, y2, POINT_1);
                  Vector2f var13 = var10.transformPosition(x3, y3, POINT_2);
                  QUAD.beginNoCamera();
                  int var14 = QUAD.vec3(toNdcX(var11.x, var8), toNdcY(var11.y, var9), 0.0).color(color).next();
                  int var15 = QUAD.vec3(toNdcX(var12.x, var8), toNdcY(var12.y, var9), 0.0).color(color).next();
                  int var16 = QUAD.vec3(toNdcX(var13.x, var8), toNdcY(var13.y, var9), 0.0).color(color).next();
                  QUAD.quad(var14, var15, var16, var16);
                  QUAD.end();
                  if (!QUAD.hasData()) {
                     return;
                  }

                  class_310 var17 = class_310.method_1551();
                  if (var17 == null) {
                     return;
                  }

                  VieMeshBuilder.GpuBufferPair var18 = QUAD.getBuffers();
                  RenderPass var19 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                     byte[] var10000 = new byte[12];
                     var10000[0] = (byte)(103);
                     var10000[1] = (byte)(-13);
                     var10000[2] = (byte)(121);
                     var10000[3] = (byte)(-2);
                     var10000[4] = (byte)(106);
                     var10000[5] = (byte)(-68);
                     var10000[6] = (byte)(-2);
                     var10000[7] = (byte)(90);
                     var10000[8] = (byte)(55);
                     var10000[9] = (byte)(-59);
                     var10000[10] = (byte)(-120);
                     var10000[11] = (byte)(-109);
                     return $$v_s(var10000, 1410770756529541140L);
                  }, var17.method_1522().method_71639(), OptionalInt.empty());
                  RenderPass var20 = var19;

                  try {
                     var19.setPipeline(ViePipelines.HUD_COLOR);
                     var19.setVertexBuffer(0, var18.vertexBuffer);
                     var19.setIndexBuffer(var18.indexBuffer, class_5595.field_27373);
                     var19.drawIndexed(0, 0, QUAD.getIndicesCount(), 1);
                  } catch (Throwable var24) {
                     if (var20 != null) {
                        try {
                           var20.close();
                        } catch (Throwable var23) {
                           var24.addSuppressed(var23);
                        }
                     }

                     throw var24;
                  }

                  if (var20 != null) {
                     var20.close();
                  }
               } catch (Throwable var25) {
                  disabled = true;
                  if (!warned) {
                     warned = true;
                     VieClientMod.LOGGER
                        .warn(
                           "[VieClient] Immediate GUI renderer disabled.",
                           var25
                        );
                  }
               }
            }
         }
      }
   }

   public static void drawTexturedRect(class_332 ctx, class_1043 tex, int x, int y, int w, int h, int argb) {
      if (ctx != null && !disabled && tex != null && w > 0 && h > 0) {
         int var7 = argb >>> 24 & 0xFF;
         if (var7 != 0) {
            int var8 = ctx.method_51421();
            int var9 = ctx.method_51443();
            if (var8 > 0 && var9 > 0) {
               try {
                  Matrix3x2fStack var10 = ctx.method_51448();
                  Vector2f var11 = var10.transformPosition(x, y, POINT_0);
                  Vector2f var12 = var10.transformPosition(x + w, y, POINT_1);
                  Vector2f var13 = var10.transformPosition(x + w, y + h, POINT_2);
                  Vector2f var14 = var10.transformPosition(x, y + h, POINT_3);
                  int var15 = argb >> 16 & 0xFF;
                  int var16 = argb >> 8 & 0xFF;
                  int var17 = argb & 0xFF;
                  QUAD_TEX.beginNoCamera();
                  QUAD_TEX.vec3(toNdcX(var11.x, var8), toNdcY(var11.y, var9), 0.0).uv(0.0F, 0.0F).color(var15, var16, var17, var7).next();
                  QUAD_TEX.vec3(toNdcX(var12.x, var8), toNdcY(var12.y, var9), 0.0).uv(1.0F, 0.0F).color(var15, var16, var17, var7).next();
                  QUAD_TEX.vec3(toNdcX(var13.x, var8), toNdcY(var13.y, var9), 0.0).uv(1.0F, 1.0F).color(var15, var16, var17, var7).next();
                  QUAD_TEX.vec3(toNdcX(var14.x, var8), toNdcY(var14.y, var9), 0.0).uv(0.0F, 1.0F).color(var15, var16, var17, var7).next();
                  QUAD_TEX.quad(0, 1, 2, 3);
                  QUAD_TEX.end();
                  if (!QUAD_TEX.hasData()) {
                     return;
                  }

                  class_310 var18 = class_310.method_1551();
                  if (var18 == null) {
                     return;
                  }

                  VieMeshBuilder.GpuBufferPair var19 = QUAD_TEX.getBuffers();
                  RenderPass var20 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                     byte[] var10000 = new byte[7];
                     var10000[0] = (byte)(103);
                     var10000[1] = (byte)(-13);
                     var10000[2] = (byte)(121);
                     var10000[3] = (byte)(-2);
                     var10000[4] = (byte)(106);
                     var10000[5] = (byte)(-85);
                     var10000[6] = (byte)(-17);
                     return $$v_s(var10000, 1410770756529541140L);
                  }, var18.method_1522().method_71639(), OptionalInt.empty());
                  RenderPass var21 = var20;

                  try {
                     var20.setPipeline(ViePipelines.HUD_TEX);
                     var20.bindTexture("Sampler0", tex.method_71659(), tex.method_75484());
                     var20.setVertexBuffer(0, var19.vertexBuffer);
                     var20.setIndexBuffer(var19.indexBuffer, class_5595.field_27373);
                     var20.drawIndexed(0, 0, QUAD_TEX.getIndicesCount(), 1);
                  } catch (Throwable var25) {
                     if (var21 != null) {
                        try {
                           var21.close();
                        } catch (Throwable var24) {
                           var25.addSuppressed(var24);
                        }
                     }

                     throw var25;
                  }

                  if (var21 != null) {
                     var21.close();
                  }
               } catch (Throwable var26) {
                  disabled = true;
                  if (!warnedTex) {
                     warnedTex = true;
                     VieClientMod.LOGGER
                        .warn(
                           "[VieClient] Immediate GUI tex renderer disabled.",
                           var26
                        );
                  }
               }
            }
         }
      }
   }

   public static void drawTexturedRect(class_332 ctx, class_2960 textureId, int x, int y, int w, int h, float u1, float v1, float u2, float v2, int argb) {
      if (ctx != null && !disabled && textureId != null && w > 0 && h > 0) {
         int var11 = argb >>> 24 & 0xFF;
         if (var11 != 0) {
            int var12 = ctx.method_51421();
            int var13 = ctx.method_51443();
            if (var12 > 0 && var13 > 0) {
               try {
                  Matrix3x2fStack var14 = ctx.method_51448();
                  Vector2f var15 = var14.transformPosition(x, y, POINT_0);
                  Vector2f var16 = var14.transformPosition(x + w, y, POINT_1);
                  Vector2f var17 = var14.transformPosition(x + w, y + h, POINT_2);
                  Vector2f var18 = var14.transformPosition(x, y + h, POINT_3);
                  int var19 = argb >> 16 & 0xFF;
                  int var20 = argb >> 8 & 0xFF;
                  int var21 = argb & 0xFF;
                  QUAD_TEX.beginNoCamera();
                  QUAD_TEX.vec3(toNdcX(var15.x, var12), toNdcY(var15.y, var13), 0.0).uv(u1, v1).color(var19, var20, var21, var11).next();
                  QUAD_TEX.vec3(toNdcX(var16.x, var12), toNdcY(var16.y, var13), 0.0).uv(u2, v1).color(var19, var20, var21, var11).next();
                  QUAD_TEX.vec3(toNdcX(var17.x, var12), toNdcY(var17.y, var13), 0.0).uv(u2, v2).color(var19, var20, var21, var11).next();
                  QUAD_TEX.vec3(toNdcX(var18.x, var12), toNdcY(var18.y, var13), 0.0).uv(u1, v2).color(var19, var20, var21, var11).next();
                  QUAD_TEX.quad(0, 1, 2, 3);
                  QUAD_TEX.end();
                  if (!QUAD_TEX.hasData()) {
                     return;
                  }

                  class_310 var22 = class_310.method_1551();
                  if (var22 == null || var22.method_1531() == null) {
                     return;
                  }

                  class_1044 var23 = var22.method_1531().method_4619(textureId);
                  if (var23 == null) {
                     return;
                  }

                  VieMeshBuilder.GpuBufferPair var24 = QUAD_TEX.getBuffers();
                  RenderPass var25 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                     byte[] var10000 = new byte[10];
                     var10000[0] = (byte)(103);
                     var10000[1] = (byte)(-13);
                     var10000[2] = (byte)('끻' ^ '뀂');
                     var10000[3] = (byte)(-2);
                     var10000[4] = (byte)(106);
                     var10000[5] = (byte)(-85);
                     var10000[6] = (byte)(-17);
                     var10000[7] = (byte)(27);
                     var10000[8] = (byte)(16);
                     var10000[9] = (byte)(-58);
                     return $$v_s(var10000, 1410770756529541140L);
                  }, var22.method_1522().method_71639(), OptionalInt.empty());
                  RenderPass var26 = var25;

                  try {
                     var25.setPipeline(ViePipelines.HUD_TEX);
                     var25.bindTexture("Sampler0", var23.method_71659(), var23.method_75484());
                     var25.setVertexBuffer(0, var24.vertexBuffer);
                     var25.setIndexBuffer(var24.indexBuffer, class_5595.field_27373);
                     var25.drawIndexed(0, 0, QUAD_TEX.getIndicesCount(), 1);
                  } catch (Throwable var30) {
                     if (var26 != null) {
                        try {
                           var26.close();
                        } catch (Throwable var29) {
                           var30.addSuppressed(var29);
                        }
                     }

                     throw var30;
                  }

                  if (var26 != null) {
                     var26.close();
                  }
               } catch (Throwable var31) {
                  disabled = true;
                  if (!warnedTex) {
                     warnedTex = true;
                     VieClientMod.LOGGER
                        .warn(
                           "[VieClient] Immediate GUI tex renderer disabled.",
                           var31
                        );
                  }
               }
            }
         }
      }
   }

   public static void drawTexturedRoundedRect(class_332 ctx, class_1043 tex, int x, int y, int w, int h, int radius, int argb) {
      drawTexturedRect(ctx, tex, x, y, w, h, argb);
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
