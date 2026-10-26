package me.drake.vieclient.render;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.class_5595;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import net.minecraft.class_1043;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

public final class TessellatorRenderer {
   private static final boolean ONE_LAYER_UI = false;
   private static class_332 ctx;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TessellatorRenderer() {
   }

   private static float softenedRadius(float radius, int w, int h) {
      float var3 = Math.max(0.0F, radius);
      float var4 = Math.max(1.0F, Math.min(w, h) * 0.5F);
      float var5 = Math.min(var4, 7.25F);
      return Math.min(var3, var5);
   }

   private static int softenedRadiusInt(int radius, int w, int h) {
      return Math.max(0, Math.round(softenedRadius(radius, w, h)));
   }

   public static void setContext(class_332 c) {
      ctx = c;
   }

   public static boolean isContextValid() {
      return true;
   }

   public static boolean isInFrame() {
      return true;
   }

   public static boolean beginFrame(float scale, int w, int h) {
      return true;
   }

   public static void endFrame() {
   }

   public static void scissor(int x, int y, int w, int h) {
   }

   public static void scissor(class_332 c, int x, int y, int w, int h) {
   }

   public static void resetScissor() {
   }

   public static void fillRect(class_332 c, int x1, int y1, int x2, int y2, int argb) {
      ImmediateGuiRenderer.fillRect(c, x1, y1, x2, y2, argb);
   }

   public static void fillRect(int x1, int y1, int x2, int y2, int argb) {
      ImmediateGuiRenderer.fillRect(ctx, x1, y1, x2, y2, argb);
   }

   public static void fillRectSharp(class_332 c, int x, int y, int w, int h, int argb) {
      fillRect(c, x, y, x + w, y + h, argb);
   }

   public static void fillRectSharp(int x, int y, int w, int h, int argb) {
      fillRect(x, y, x + w, y + h, argb);
   }

   public static void fillRoundedRect(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         ImmediateGuiRenderer.fillRect(var7, x, y, x + w, y + h, argb);
      }
   }

   public static void fillRoundedRect(class_332 c, int x, int y, int w, int h, float radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         ImmediateGuiRenderer.fillRect(var7, x, y, x + w, y + h, argb);
      }
   }

   public static void fillRoundedRect(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRect(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRect(int x, int y, int w, int h, float radius, int argb) {
      fillRoundedRect(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectSmooth(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRect(c, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectSmooth(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRect(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectSmooth(class_332 c, int x, int y, int w, int h, float radius, int argb) {
      fillRoundedRectSmooth(c, x, y, w, h, (int)radius, argb);
   }

   public static void fillRoundedRectSmooth(int x, int y, int w, int h, float radius, int argb) {
      fillRoundedRectSmooth(x, y, w, h, (int)radius, argb);
   }

   public static void fillRoundedRectVarying(class_332 c, int x, int y, int w, int h, float tl, float tr, float br, float bl, int argb) {
      class_332 var10 = c != null ? c : ctx;
      if (var10 != null && w > 0 && h > 0) {
         ImmediateGuiRenderer.fillRect(var10, x, y, x + w, y + h, argb);
      }
   }

   public static void fillRoundedRectVarying(int x, int y, int w, int h, float tl, float tr, float br, float bl, int argb) {
      fillRoundedRectVarying(ctx, x, y, w, h, tl, tr, br, bl, argb);
   }

   public static void fillRoundedRectTop(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = softenedRadiusInt(radius, w, h);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x, y + var8, x + w, y + h, argb);
            fillRect(var7, x + var8, y, x + w - var8, y + var8, argb);
            fillRect(var7, x, y, x + var8, y + var8, argb);
            fillRect(var7, x + w - var8, y, x + w, y + var8, argb);
         }
      }
   }

   public static void fillRoundedRectTop(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectTop(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectBottom(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = softenedRadiusInt(radius, w, h);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x, y, x + w, y + h - var8, argb);
            fillRect(var7, x + var8, y + h - var8, x + w - var8, y + h, argb);
            fillRect(var7, x, y + h - var8, x + var8, y + h, argb);
            fillRect(var7, x + w - var8, y + h - var8, x + w, y + h, argb);
         }
      }
   }

   public static void fillRoundedRectBottom(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectBottom(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectLeft(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = softenedRadiusInt(radius, w, h);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x + var8, y, x + w, y + h, argb);
            fillRect(var7, x, y + var8, x + var8, y + h - var8, argb);
            fillRect(var7, x, y, x + var8, y + var8, argb);
            fillRect(var7, x, y + h - var8, x + var8, y + h, argb);
         }
      }
   }

   public static void fillRoundedRectLeft(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectLeft(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectLeftOnly(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = softenedRadiusInt(radius, w, h);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x + var8, y, x + w, y + h, argb);
            fillRect(var7, x, y + var8, x + var8, y + h - var8, argb);
            fillRect(var7, x, y, x + var8, y + var8, argb);
            fillRect(var7, x, y + h - var8, x + var8, y + h, argb);
         }
      }
   }

   public static void fillRoundedRectRightOnly(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = softenedRadiusInt(radius, w, h);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x, y, x + w - var8, y + h, argb);
            fillRect(var7, x + w - var8, y, x + w, y + var8, argb);
            fillRect(var7, x + w - var8, y + h - var8, x + w, y + h, argb);
            fillRect(var7, x, y + var8, x + w - var8, y + h - var8, argb);
         }
      }
   }

   public static void fillRoundedRectLeftOnly(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectLeftOnly(ctx, x, y, w, h, radius, argb);
   }

   public static void fillRoundedRectRightOnly(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectRightOnly(ctx, x, y, w, h, radius, argb);
   }

   public static void fillGradientRect(class_332 c, int x, int y, int w, int h, int startColor, int endColor, boolean vertical) {
      if (c != null && w > 0 && h > 0) {
         ImmediateGuiRenderer.fillGradientRect(c, x, y, w, h, startColor, endColor, vertical);
      }
   }

   public static void fillGradientRect(int x, int y, int w, int h, int startColor, int endColor, boolean vertical) {
      fillGradientRect(ctx, x, y, w, h, startColor, endColor, vertical);
   }

   private static void fillRoundedRectGradient0(class_332 c, int x, int y, int w, int h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRect(c, x, y, w, h, radius, startColor);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, int w, int h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, int w, int h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, int w, float h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, int w, float h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, float h, int w, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, float h, int w, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, float h, float w, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, (int)w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(class_332 c, int x, int y, float h, float w, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(c, x, y, (int)w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, int w, int h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, int w, int h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, int w, float h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, int w, float h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, float h, int w, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, float h, int w, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, float h, float w, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, (int)w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, int y, float h, float w, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, y, (int)w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, int w, int h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, w, h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, int w, int h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, w, h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, int w, float h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, int w, float h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, float w, int h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, (int)w, h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, float w, int h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, (int)w, h, (int)radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, float w, float h, int radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, (int)w, (int)h, radius, startColor, endColor, vertical);
   }

   public static void fillRoundedRectGradient(int x, float y, float w, float h, float radius, int startColor, int endColor, boolean vertical) {
      fillRoundedRectGradient0(ctx, x, (int)y, (int)w, (int)h, (int)radius, startColor, endColor, vertical);
   }

   private static void strokeRoundedRect0(class_332 c, float x, float y, float w, float h, float radius, float stroke, int argb) {
      if (c != null && !(w <= 0.0F) && !(h <= 0.0F)) {
         float var8 = Math.max(1.0F, stroke);
         float var9 = var8 * 0.5F;
         float var10 = x;
         float var11 = y;
         float var12 = x + w;
         float var13 = y + h;
         ImmediateGuiRenderer.drawLine(c, var10, var11 + var9, var12, var11 + var9, var8, argb);
         ImmediateGuiRenderer.drawLine(c, var10, var13 - var9, var12, var13 - var9, var8, argb);
         ImmediateGuiRenderer.drawLine(c, var10 + var9, var11, var10 + var9, var13, var8, argb);
         ImmediateGuiRenderer.drawLine(c, var12 - var9, var11, var12 - var9, var13, var8, argb);
      }
   }

   public static void strokeRoundedRect(class_332 c, int x, int y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, int y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, int y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, int y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, float y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, float y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, float y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, int x, float y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, int y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, int y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, int y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, int y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, float y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, float y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, float y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(class_332 c, float x, float y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(c, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, int y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, int y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, int y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, int y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, float y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, float y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, float y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(int x, float y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, int y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, int y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, int y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, int y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, float y, int w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, float y, int w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, float y, float w, int h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRoundedRect(float x, float y, float w, float h, float radius, float stroke, int argb) {
      strokeRoundedRect0(ctx, x, y, w, h, radius, stroke, argb);
   }

   public static void strokeRect(int x, int y, int w, int h, float stroke, int argb) {
      if (w > 0 && h > 0) {
         float var6 = Math.max(1.0F, stroke) * 0.5F;
         float var7 = x + var6;
         float var8 = x + w - var6;
         float var9 = y + var6;
         float var10 = y + h - var6;
         ImmediateGuiRenderer.drawLine(ctx, var7, var9, var8, var9, stroke, argb);
         ImmediateGuiRenderer.drawLine(ctx, var7, var10, var8, var10, stroke, argb);
         ImmediateGuiRenderer.drawLine(ctx, var7, var9, var7, var10, stroke, argb);
         ImmediateGuiRenderer.drawLine(ctx, var8, var9, var8, var10, stroke, argb);
      }
   }

   public static void drawCircle(class_332 c, float cx, float cy, float radius, int argb) {
      int var5 = (int)radius;
      fillRoundedRect(c, (int)(cx - radius), (int)(cy - radius), var5 * (2), var5 * (2), var5, argb);
   }

   public static void drawCircle(float cx, float cy, float radius, int argb) {
      drawCircle(ctx, cx, cy, radius, argb);
   }

   public static void drawLine(class_332 c, float x1, float y1, float x2, float y2, float stroke, int argb) {
      ImmediateGuiRenderer.drawLine(c, x1, y1, x2, y2, stroke, argb);
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float stroke, int argb) {
      drawLine(ctx, x1, y1, x2, y2, stroke, argb);
   }

   public static void drawTextWithShadow(class_332 c, class_327 tr, String text, int x, int y, int argb) {
      if (text != null && !text.isEmpty()) {
         int var6 = argb >>> (24) & (255);
         if (var6 != 0) {
            class_332 var7 = c != null ? c : ctx;
            if (var7 != null) {
               class_327 var8 = tr != null ? tr : (class_310.method_1551() != null ? class_310.method_1551().field_1772 : null);
               if (var8 != null) {
                  drawText0(var7, var8, text, x, y, argb, false);
               }
            }
         }
      }
   }

   public static void drawTextWithShadow(class_332 c, String text, float x, float y, float size, int argb) {
      if (text != null && !text.isEmpty()) {
         int var6 = argb >>> (24) & (255);
         if (var6 != 0) {
            class_332 var7 = c != null ? c : ctx;
            if (var7 != null) {
               class_310 var8 = class_310.method_1551();
               if (var8 != null && var8.field_1772 != null) {
                  drawText0(var7, var8.field_1772, text, Math.round(x), Math.round(y), argb, false);
               }
            }
         }
      }
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb) {
      drawTextWithShadow(ctx, text, x, y, size, argb);
   }

   public static int measureTextWidth(class_327 tr, String text) {
      if (text != null && !text.isEmpty()) {
         return tr != null ? tr.method_1727(text) : 0;
      } else {
         return 0;
      }
   }

   public static int measureTextWidth(String text, float size) {
      if (text != null && !text.isEmpty()) {
         class_310 var2 = class_310.method_1551();
         return var2 != null && var2.field_1772 != null ? var2.field_1772.method_1727(text) : 0;
      } else {
         return 0;
      }
   }

   public static int measureTextHeight(class_327 tr) {
      return tr != null ? 9 : 9;
   }

   public static String trimToWidth(class_327 tr, String text, int maxWidth) {
      if (text != null && !text.isEmpty() && maxWidth > 0) {
         if (measureTextWidth(tr, text) <= maxWidth) {
            return text;
         }

         int var3 = 0;
         int var4 = text.length();

         while (var3 < var4) {
            int var5 = var3 + var4 + 1 >>> 1;
            String var6 = text.substring(0, var5);
            if (measureTextWidth(tr, var6) <= maxWidth) {
               var3 = var5;
            } else {
               var4 = var5 - 1;
            }
         }

         return var3 <= 0 ? "" : text.substring(0, var3);
      } else {
         return "";
      }
   }

   private static void drawText0(class_332 target, class_327 tr, String text, int x, int y, int color, boolean shadow) {
      if (target != null && tr != null && text != null && !text.isEmpty()) {
         if (shadow) {
            target.method_51433(tr, text, x, y, color, true);
         } else {
            int var7 = color >>> (24) & (255);
            int var8 = color >>> (16) & (255);
            int var9 = color >>> (8) & (255);
            int var10 = color & (255);
            target.method_51433(tr, text, x, y, var7 << (24) | var8 << (16) | var9 << (8) | var10, false);
         }
      }
   }

   public static void drawText(class_332 ctx, class_327 tr, String text, int x, int y, int color) {
      drawText0(ctxx, tr, text, x, y, color, true);
   }

   public static void drawText(class_332 ctx, class_327 tr, String text, int x, int y, int color, boolean shadow) {
      drawText0(ctxx, tr, text, x, y, color, shadow);
   }

   public static void fillLine(class_332 c, float x1, float y1, float x2, float y2, float thickness, int color) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null) {
         float var8 = x2 - x1;
         float var9 = y2 - y1;
         float var10 = (float)Math.sqrt(var8 * var8 + var9 * var9);
         if (!(var10 < 1.0F)) {
            int var11 = Math.max(1, (int)var10);

            for (int var12 = 0; var12 < var11; var12++) {
               float var13 = (float)var12 / var11;
               float var14 = x1 + var8 * var13;
               float var15 = y1 + var9 * var13;
               int var16 = Math.round(var14);
               int var17 = Math.round(var15);
               int var18 = Math.max(1, (int)thickness);
               fillRectSharp(var7, var16, var17, var18, var18, color);
            }
         }
      }
   }

   public static void fillRoundedRectRight(class_332 c, int x, int y, int w, int h, int radius, int argb) {
      class_332 var7 = c != null ? c : ctx;
      if (var7 != null && w > 0 && h > 0) {
         int var8 = Math.max(0, radius);
         if (var8 <= 0) {
            fillRect(var7, x, y, x + w, y + h, argb);
         } else {
            fillRect(var7, x, y, x + w - var8, y + h, argb);
            fillRect(var7, x + w - var8, y, x + w, y + var8, argb);
            fillRect(var7, x + w - var8, y + h - var8, x + w, y + h, argb);
         }
      }
   }

   public static void fillRoundedRectRight(int x, int y, int w, int h, int radius, int argb) {
      fillRoundedRectRight(ctx, x, y, w, h, radius, argb);
   }

   public static void fillTriangle(class_332 c, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
      class_332 var8 = c != null ? c : ctx;
      if (var8 != null) {
         try {
            class_310 var9 = class_310.method_1551();
            if (var9 != null && var9.field_1773 != null) {
               fillTriangleViaMesh(var8, x1, y1, x2, y2, x3, y3, color);
            }
         } catch (Throwable var10) {
         }
      }
   }

   private static void fillTriangleViaMesh(class_332 target, float x1, float y1, float x2, float y2, float x3, float y3, int color) {
      if (target != null && (color >>> 24 & 0xFF) != 0) {
         int var8 = target.method_51421();
         int var9 = target.method_51443();
         if (var8 > 0 && var9 > 0) {
            try {
               Matrix3x2fStack var10 = target.method_51448();
               Vector2f var11 = var10.transformPosition(x1, y1, new Vector2f());
               Vector2f var12 = var10.transformPosition(x2, y2, new Vector2f());
               Vector2f var13 = var10.transformPosition(x3, y3, new Vector2f());
               float var14 = toNdcX(var11.x, var8);
               float var15 = toNdcY(var11.y, var9);
               float var16 = toNdcX(var12.x, var8);
               float var17 = toNdcY(var12.y, var9);
               float var18 = toNdcX(var13.x, var8);
               float var19 = toNdcY(var13.y, var9);
               int var20 = color >> 16 & 0xFF;
               int var21 = color >> 8 & 0xFF;
               int var22 = color & 0xFF;
               int var23 = color >> 24 & 0xFF;
               VieMeshBuilder var24 = new VieMeshBuilder(ViePipelines.HUD_COLOR);
               var24.beginNoCamera();
               var24.vec3(var14, var15, 0.0).color(var20, var21, var22, var23).next();
               var24.vec3(var16, var17, 0.0).color(var20, var21, var22, var23).next();
               var24.vec3(var18, var19, 0.0).color(var20, var21, var22, var23).next();
               var24.vec3(var18, var19, 0.0).color(var20, var21, var22, var23).next();
               var24.quad(0, 1, 2, 3);
               var24.end();
               if (!var24.hasData()) {
                  return;
               }

               class_310 var25 = class_310.method_1551();
               if (var25 == null || var25.method_1522() == null) {
                  return;
               }

               RenderPass var26 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                  byte[] var10000 = new byte[12];
                  var10000[0] = (byte)(110);
                  var10000[1] = (byte)(-13);
                  var10000[2] = (byte)(121);
                  var10000[3] = (byte)(-2);
                  var10000[4] = (byte)(14);
                  var10000[5] = (byte)(40);
                  var10000[6] = (byte)(46);
                  var10000[7] = (byte)(82);
                  var10000[8] = (byte)(62);
                  var10000[9] = (byte)(-59);
                  var10000[10] = (byte)(-120);
                  var10000[11] = (byte)(-109);
                  return $$v_s(var10000, 1964841449181925405L);
               }, var25.method_1522().method_71639(), OptionalInt.empty());

               try {
                  var26.setPipeline(ViePipelines.HUD_COLOR);
                  var26.setVertexBuffer(0, var24.getVertexBuffer());
                  var26.setIndexBuffer(var24.getIndexBuffer(), class_5595.field_27373);
                  var26.drawIndexed(0, 0, var24.getIndicesCount(), 1);
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
            }
         }
      }
   }

   public static void drawTexturedRect(class_332 c, class_1043 tex, int x, int y, int w, int h, int argb) {
      if (c != null && tex != null && w > 0 && h > 0) {
         int var7 = argb >>> 24 & 0xFF;
         if (var7 != 0) {
            int var8 = c.method_51421();
            int var9 = c.method_51443();
            if (var8 > 0 && var9 > 0) {
               try {
                  Matrix3x2fStack var10 = c.method_51448();
                  Vector2f var11 = var10.transformPosition(x, y, new Vector2f());
                  Vector2f var12 = var10.transformPosition(x + w, y, new Vector2f());
                  Vector2f var13 = var10.transformPosition(x + w, y + h, new Vector2f());
                  Vector2f var14 = var10.transformPosition(x, y + h, new Vector2f());
                  int var15 = argb >> 16 & 0xFF;
                  int var16 = argb >> 8 & 0xFF;
                  int var17 = argb & 0xFF;
                  VieMeshBuilder var18 = new VieMeshBuilder(ViePipelines.HUD_TEX);
                  var18.beginNoCamera();
                  var18.vec3(toNdcX(var11.x, var8), toNdcY(var11.y, var9), 0.0).uv(0.0F, 0.0F).color(var15, var16, var17, var7).next();
                  var18.vec3(toNdcX(var12.x, var8), toNdcY(var12.y, var9), 0.0).uv(1.0F, 0.0F).color(var15, var16, var17, var7).next();
                  var18.vec3(toNdcX(var13.x, var8), toNdcY(var13.y, var9), 0.0).uv(1.0F, 1.0F).color(var15, var16, var17, var7).next();
                  var18.vec3(toNdcX(var14.x, var8), toNdcY(var14.y, var9), 0.0).uv(0.0F, 1.0F).color(var15, var16, var17, var7).next();
                  var18.quad(0, 1, 2, 3);
                  var18.end();
                  if (!var18.hasData()) {
                     return;
                  }

                  class_310 var19 = class_310.method_1551();
                  if (var19 == null || var19.method_1522() == null) {
                     return;
                  }

                  RenderPass var20 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                     byte[] var10000 = new byte[12];
                     var10000[0] = (byte)(110);
                     var10000[1] = (byte)(-13);
                     var10000[2] = (byte)(121);
                     var10000[3] = (byte)(-2);
                     var10000[4] = (byte)(27);
                     var10000[5] = (byte)(54);
                     var10000[6] = (byte)(37);
                     var10000[7] = (byte)(70);
                     var10000[8] = (byte)(61);
                     var10000[9] = (byte)(-29);
                     var10000[10] = (byte)(-106);
                     var10000[11] = (byte)(-126);
                     return $$v_s(var10000, 1964841449181925405L);
                  }, var19.method_1522().method_71639(), OptionalInt.empty());

                  try {
                     var20.setPipeline(ViePipelines.HUD_TEX);
                     var20.bindTexture("Sampler0", tex.method_71659(), tex.method_75484());
                     var20.setVertexBuffer(0, var18.getVertexBuffer());
                     var20.setIndexBuffer(var18.getIndexBuffer(), class_5595.field_27373);
                     var20.drawIndexed(0, 0, var18.getIndicesCount(), 1);
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
               }
            }
         }
      }
   }

   private static float toNdcX(float x, int screenW) {
      return x / screenW * 2.0F - 1.0F;
   }

   private static float toNdcY(float y, int screenH) {
      return 1.0F - y / screenH * 2.0F;
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
