package me.drake.vieclient.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.render.PlayerESP;
import me.drake.vieclient.modules.render.Tracers;
import me.drake.vieclient.render.VieMeshBuilder;
import me.drake.vieclient.render.VieMeshRenderer;
import me.drake.vieclient.render.ViePipelines;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class RenderUtil {
   public static final int FACE_WEST = 1;
   public static final int FACE_EAST = 2;
   public static final int FACE_NORTH = 4;
   public static final int FACE_SOUTH = 8;
   public static final int FACE_BOTTOM = 16;
   public static final int FACE_TOP = 32;
   public static final int ALL_FACES = 63;
   private static final VieMeshBuilder linesMesh = new VieMeshBuilder(ViePipelines.WORLD_LINES);
   private static final VieMeshBuilder thinLinesMesh = new VieMeshBuilder(ViePipelines.WORLD_LINES);
   private static final VieMeshBuilder trianglesMesh = new VieMeshBuilder(ViePipelines.WORLD_TRIANGLES);
   private static final VieMeshBuilder additiveTrianglesMesh = new VieMeshBuilder(ViePipelines.WORLD_TRIANGLES_ADD);
   private static final VieMeshBuilder depthTrianglesMesh = new VieMeshBuilder(ViePipelines.WORLD_TRIANGLES_DEPTH);
   private static final VieMeshBuilder additiveDepthTrianglesMesh = new VieMeshBuilder(ViePipelines.WORLD_TRIANGLES_ADD_DEPTH);
   private static boolean allowOutlineThisFrame = true;
   private static boolean allowFillThisFrame = true;
   private static boolean allowTracerThisFrame = true;
   private static boolean cachedCrosshairOriginValid;
   private static double cachedCrosshairOriginX;
   private static double cachedCrosshairOriginY;
   private static double cachedCrosshairOriginZ;
   public static class_243 center;
   private static final IntOpenHashSet drawnTracerEntityIds = new IntOpenHashSet();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private RenderUtil() {
   }

   public static void begin() {
      drawnTracerEntityIds.clear();
      linesMesh.begin();
      thinLinesMesh.begin();
      trianglesMesh.begin();
      additiveTrianglesMesh.begin();
      depthTrianglesMesh.begin();
      additiveDepthTrianglesMesh.begin();
      updateCrosshairOriginCache();
      updateAdaptiveDrawFlags();
   }

   public static void render(class_4587 matrices) {
      renderOne(ViePipelines.WORLD_TRIANGLES, trianglesMesh, matrices);
      renderOne(ViePipelines.WORLD_TRIANGLES_ADD, additiveTrianglesMesh, matrices);
      renderOne(ViePipelines.WORLD_TRIANGLES_DEPTH, depthTrianglesMesh, matrices);
      renderOne(ViePipelines.WORLD_TRIANGLES_ADD_DEPTH, additiveDepthTrianglesMesh, matrices);
      renderOne(ViePipelines.WORLD_LINES, linesMesh, matrices);
      renderOne(ViePipelines.WORLD_LINES, thinLinesMesh, matrices);
   }

   private static void renderOne(RenderPipeline pipeline, VieMeshBuilder mesh, class_4587 matrices) {
      try {
         if (mesh.hasData()) {
            VieMeshRenderer.render(pipeline, mesh, matrices);
         } else if (mesh.isBuilding()) {
            mesh.end();
         }
      } catch (Exception var6) {
         byte[] var10001 = new byte[32];
         var10001[0] = (byte)(97);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-100);
         var10001[5] = (byte)(-5);
         var10001[6] = (byte)(76);
         var10001[7] = (byte)(100);
         var10001[8] = (byte)(60);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(-62);
         var10001[13] = (byte)(61);
         var10001[14] = (byte)(127);
         var10001[15] = (byte)(22);
         var10001[16] = (byte)(24);
         var10001[17] = (byte)(106);
         var10001[18] = (byte)(-66);
         var10001[19] = (byte)(-53);
         var10001[20] = 1;
         var10001[21] = (byte)(3);
         var10001[22] = (byte)(16);
         var10001[23] = (byte)(35);
         var10001[24] = (byte)(-21);
         var10001[25] = (byte)(124);
         var10001[26] = (byte)(-13);
         var10001[27] = (byte)(102);
         var10001[28] = (byte)(58);
         var10001[29] = (byte)(122);
         var10001[30] = (byte)(46);
         var10001[31] = (byte)(-31);
         VieClientMod.LOGGER.error($$v_s(var10001, 2965138108354646047L), var6);
         if (mesh.isBuilding()) {
            try {
               mesh.end();
            } catch (Exception var5) {
            }
         }
      }
   }

   public static class_238 getInterpolatedBox(class_1297 entity, float tickDelta) {
      double var2 = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317());
      double var4 = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318());
      double var6 = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321());
      class_238 var8 = entity.method_5829();
      double var9 = (var8.field_1320 - var8.field_1323) / 2.0;
      double var11 = (var8.field_1324 - var8.field_1321) / 2.0;
      double var13 = var8.field_1325 - var8.field_1322;
      return new class_238(var2 - var9, var4, var6 - var11, var2 + var9, var4 + var13, var6 + var11);
   }

   public static void drawBox(class_238 box, int color) {
      if (box != null) {
         drawBox(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
      }
   }

   public static void drawBox(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      if (allowOutlineThisFrame) {
         float var13 = color >> (16) & (255);
         float var14 = color >> (8) & (255);
         float var15 = color & (255);
         float var16 = color >> (24) & (255);
         if (!(var16 <= 0.0F)) {
            linesMesh.ensureCapacity(8, 24);
            int var17 = linesMesh.vec3(x1, y1, z1).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var18 = linesMesh.vec3(x1, y1, z2).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var19 = linesMesh.vec3(x2, y1, z1).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var20 = linesMesh.vec3(x2, y1, z2).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var21 = linesMesh.vec3(x1, y2, z1).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var22 = linesMesh.vec3(x1, y2, z2).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var23 = linesMesh.vec3(x2, y2, z1).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            int var24 = linesMesh.vec3(x2, y2, z2).color((int)var13, (int)var14, (int)var15, (int)var16).next();
            linesMesh.line(var17, var18);
            linesMesh.line(var19, var20);
            linesMesh.line(var17, var19);
            linesMesh.line(var18, var20);
            linesMesh.line(var21, var22);
            linesMesh.line(var23, var24);
            linesMesh.line(var21, var23);
            linesMesh.line(var22, var24);
            linesMesh.line(var17, var21);
            linesMesh.line(var18, var22);
            linesMesh.line(var19, var23);
            linesMesh.line(var20, var24);
         }
      }
   }

   public static void drawFilledBox(class_238 box, int color) {
      if (box != null) {
         drawFilledBox(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
      }
   }

   public static void drawFilledBox(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      if (allowFillThisFrame) {
         float var13 = color >> ('赿' ^ '赯') & (255);
         float var14 = color >> (8) & (255);
         float var15 = color & (255);
         int var16 = color >> (24) & (255);
         if (var16 > 0) {
            trianglesMesh.ensureCapacity(8, 36);
            int var17 = trianglesMesh.vec3(x1, y1, z1).color((int)var13, (int)var14, (int)var15, var16).next();
            int var18 = trianglesMesh.vec3(x1, y1, z2).color((int)var13, (int)var14, (int)var15, var16).next();
            int var19 = trianglesMesh.vec3(x2, y1, z1).color((int)var13, (int)var14, (int)var15, var16).next();
            int var20 = trianglesMesh.vec3(x2, y1, z2).color((int)var13, (int)var14, (int)var15, var16).next();
            int var21 = trianglesMesh.vec3(x1, y2, z1).color((int)var13, (int)var14, (int)var15, var16).next();
            int var22 = trianglesMesh.vec3(x1, y2, z2).color((int)var13, (int)var14, (int)var15, var16).next();
            int var23 = trianglesMesh.vec3(x2, y2, z1).color((int)var13, (int)var14, (int)var15, var16).next();
            int var24 = trianglesMesh.vec3(x2, y2, z2).color((int)var13, (int)var14, (int)var15, var16).next();
            trianglesMesh.quad(var17, var18, var22, var21);
            trianglesMesh.quad(var19, var23, var24, var20);
            trianglesMesh.quad(var17, var21, var23, var19);
            trianglesMesh.quad(var18, var20, var24, var22);
            trianglesMesh.quad(var17, var19, var20, var18);
            trianglesMesh.quad(var21, var22, var24, var23);
         }
      }
   }

   public static void drawFilledBoxAdditive(class_238 box, int color) {
      if (box != null) {
         drawFilledBoxAdditive(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
      }
   }

   public static void drawFilledBoxAdditive(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      if (allowFillThisFrame && (color >>> (24) & (255)) > 0) {
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x1, y1, z2, color, x1, y2, z2, color);
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x1, y2, z2, color, x1, y2, z1, color);
         drawTriangleAdditiveGouraud(x2, y1, z1, color, x2, y2, z2, color, x2, y1, z2, color);
         drawTriangleAdditiveGouraud(x2, y1, z1, color, x2, y2, z1, color, x2, y2, z2, color);
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x1, y2, z1, color, x2, y2, z1, color);
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x2, y2, z1, color, x2, y1, z1, color);
         drawTriangleAdditiveGouraud(x1, y1, z2, color, x2, y1, z2, color, x2, y2, z2, color);
         drawTriangleAdditiveGouraud(x1, y1, z2, color, x2, y2, z2, color, x1, y2, z2, color);
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x2, y1, z1, color, x2, y1, z2, color);
         drawTriangleAdditiveGouraud(x1, y1, z1, color, x2, y1, z2, color, x1, y1, z2, color);
         drawTriangleAdditiveGouraud(x1, y2, z1, color, x1, y2, z2, color, x2, y2, z2, color);
         drawTriangleAdditiveGouraud(x1, y2, z1, color, x2, y2, z2, color, x2, y2, z1, color);
      }
   }

   public static void drawFilledBoxFaces(class_238 box, int color, int visibleFaces) {
      if (allowFillThisFrame && visibleFaces != 0) {
         int var3 = color >> (16) & (255);
         int var4 = color >> (8) & (255);
         int var5 = color & (255);
         int var6 = color >> (24) & (255);
         if (var6 > 0) {
            double var7 = box.field_1323;
            double var9 = box.field_1322;
            double var11 = box.field_1321;
            double var13 = box.field_1320;
            double var15 = box.field_1325;
            double var17 = box.field_1324;
            trianglesMesh.ensureCapacity(8, Integer.bitCount(visibleFaces) * (6));
            int var19 = trianglesMesh.vec3(var7, var9, var11).color(var3, var4, var5, var6).next();
            int var20 = trianglesMesh.vec3(var7, var9, var17).color(var3, var4, var5, var6).next();
            int var21 = trianglesMesh.vec3(var13, var9, var11).color(var3, var4, var5, var6).next();
            int var22 = trianglesMesh.vec3(var13, var9, var17).color(var3, var4, var5, var6).next();
            int var23 = trianglesMesh.vec3(var7, var15, var11).color(var3, var4, var5, var6).next();
            int var24 = trianglesMesh.vec3(var7, var15, var17).color(var3, var4, var5, var6).next();
            int var25 = trianglesMesh.vec3(var13, var15, var11).color(var3, var4, var5, var6).next();
            int var26 = trianglesMesh.vec3(var13, var15, var17).color(var3, var4, var5, var6).next();
            if ((visibleFaces & 1) != 0) {
               trianglesMesh.quad(var19, var20, var24, var23);
            }

            if ((visibleFaces & (2)) != 0) {
               trianglesMesh.quad(var21, var25, var26, var22);
            }

            if ((visibleFaces & (4)) != 0) {
               trianglesMesh.quad(var19, var23, var25, var21);
            }

            if ((visibleFaces & (8)) != 0) {
               trianglesMesh.quad(var20, var22, var26, var24);
            }

            if ((visibleFaces & (16)) != 0) {
               trianglesMesh.quad(var19, var21, var22, var20);
            }

            if ((visibleFaces & (32)) != 0) {
               trianglesMesh.quad(var23, var24, var26, var25);
            }
         }
      }
   }

   public static void drawFilledBoxDepth(class_238 box, int color) {
      if (allowFillThisFrame) {
         float var2 = color >> (16) & (255);
         float var3 = color >> (8) & (255);
         float var4 = color & (255);
         int var5 = color >> (24) & (255);
         if (var5 > 0) {
            double var6 = box.field_1323;
            double var8 = box.field_1322;
            double var10 = box.field_1321;
            double var12 = box.field_1320;
            double var14 = box.field_1325;
            double var16 = box.field_1324;
            VieMeshBuilder var18 = getDepthTrianglesMesh();
            var18.ensureCapacity(8, 36);
            int var19 = var18.vec3(var6, var8, var10).color((int)var2, (int)var3, (int)var4, var5).next();
            int var20 = var18.vec3(var6, var8, var16).color((int)var2, (int)var3, (int)var4, var5).next();
            int var21 = var18.vec3(var12, var8, var10).color((int)var2, (int)var3, (int)var4, var5).next();
            int var22 = var18.vec3(var12, var8, var16).color((int)var2, (int)var3, (int)var4, var5).next();
            int var23 = var18.vec3(var6, var14, var10).color((int)var2, (int)var3, (int)var4, var5).next();
            int var24 = var18.vec3(var6, var14, var16).color((int)var2, (int)var3, (int)var4, var5).next();
            int var25 = var18.vec3(var12, var14, var10).color((int)var2, (int)var3, (int)var4, var5).next();
            int var26 = var18.vec3(var12, var14, var16).color((int)var2, (int)var3, (int)var4, var5).next();
            var18.quad(var19, var20, var24, var23);
            var18.quad(var21, var25, var26, var22);
            var18.quad(var19, var23, var25, var21);
            var18.quad(var20, var22, var26, var24);
            var18.quad(var19, var21, var22, var20);
            var18.quad(var23, var24, var26, var25);
         }
      }
   }

   private static VieMeshBuilder getDepthTrianglesMesh() {
      return depthTrianglesMesh;
   }

   public static void drawTracer(class_1297 target, int color, float tickDelta, float width) {
      if (target != null && drawnTracerEntityIds.add(target.method_5628())) {
         class_238 var4 = getInterpolatedBox(target, tickDelta);
         double var5 = (var4.field_1323 + var4.field_1320) * 0.5;
         double var7 = (var4.field_1322 + var4.field_1325) * 0.5;
         double var9 = (var4.field_1321 + var4.field_1324) * 0.5;
         drawTracerToPos(var5, var7, var9, color, tickDelta, width);
      }
   }

   public static void drawTracer(class_1297 target, int color, float tickDelta) {
      drawTracer(target, color, tickDelta, getGlobalTracerWidth());
   }

   @Deprecated
   public static void drawTracer(class_1297 target, int color) {
      drawTracer(target, color, 1.0F);
   }

   public static void drawTracerQuad(class_1297 target, int color, float tickDelta) {
      if (target != null && drawnTracerEntityIds.add(target.method_5628())) {
         class_310 var3 = class_310.method_1551();
         if (var3.field_1724 != null && var3.field_1773 != null) {
            class_243 var4 = target.method_30950(tickDelta);
            double var5 = var4.field_1352;
            double var7 = var4.field_1351 + target.method_17682() / 2.0;
            double var9 = var4.field_1350;
            drawTracerToPos(var5, var7, var9, color, tickDelta);
         }
      }
   }

   public static void drawTracerToPos(double tx, double ty, double tz, int color) {
      drawTracerToPos(tx, ty, tz, color, 1.0F);
   }

   public static boolean isPointInFrontOfCamera(double tx, double ty, double tz) {
      class_310 var6 = class_310.method_1551();
      if (var6 != null && var6.field_1773 != null) {
         class_4184 var7 = var6.field_1773.method_19418();
         if (var7 == null) {
            return true;
         } else {
            class_243 var8 = var7.method_71156();
            double var9 = tx - var8.field_1352;
            double var11 = ty - var8.field_1351;
            double var13 = tz - var8.field_1350;
            if (VieMeshRenderer.hasCapturedWorldMatrices) {
               Matrix4f var15 = VieMeshRenderer.lastPositionMatrix;
               float var16 = (float)(var15.m02() * var9 + var15.m12() * var11 + var15.m22() * var13 + var15.m32());
               return var16 < -0.05F;
            } else {
               return true;
            }
         }
      } else {
         return true;
      }
   }

   public static boolean recordTracerEntity(int entityId) {
      return drawnTracerEntityIds.add(entityId);
   }

   public static void drawTracerToPos(double tx, double ty, double tz, int color, float tickDelta, float width) {
      if (allowTracerThisFrame) {
         int var9 = color >>> (24) & (255);
         if (var9 > 0) {
            int var10 = color & (16777215) | var9 << (24);
            if (cachedCrosshairOriginValid) {
               drawLine(cachedCrosshairOriginX, cachedCrosshairOriginY, cachedCrosshairOriginZ, tx, ty, tz, var10);
            } else {
               class_310 var11 = class_310.method_1551();
               if (var11 != null && var11.field_1773 != null) {
                  class_4184 var12 = var11.field_1773.method_19418();
                  if (var12 != null) {
                     class_243 var13 = var12.method_71156();
                     drawLine(var13.field_1352, var13.field_1351, var13.field_1350, tx, ty, tz, var10);
                  }
               }
            }
         }
      }
   }

   public static void drawTracerToPos(double tx, double ty, double tz, int color, float tickDelta) {
      drawTracerToPos(tx, ty, tz, color, tickDelta, getGlobalTracerWidth());
   }

   private static float getGlobalTracerWidth() {
      VieClientMod var0 = VieClientMod.getInstance();
      if (var0 != null && var0.getModuleManager() != null) {
         Tracers var1 = (Tracers)var0.getModuleManager().getModule(Tracers.class);
         if (var1 != null && var1.isEnabled()) {
            return (float)var1.getWidth();
         }

         PlayerESP var2 = (PlayerESP)var0.getModuleManager().getModule(PlayerESP.class);
         if (var2 != null && var2.isEnabled() && var2.hasTracer()) {
            return (float)var2.getTracerWidth();
         }

         if (var1 != null) {
            return (float)var1.getWidth();
         }

         if (var2 != null) {
            return (float)var2.getTracerWidth();
         }
      }

      return 1.0F;
   }

   private static class_243 computeCrosshairOrigin() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.field_1773 != null && VieMeshRenderer.hasCapturedWorldMatrices) {
         class_4184 var1 = var0.field_1773.method_19418();
         if (var1 == null) {
            return null;
         }

         Matrix4f var2 = new Matrix4f(VieMeshRenderer.lastProjectionMatrix).invert();
         Matrix4f var3 = new Matrix4f(VieMeshRenderer.lastPositionMatrix).invert();
         Vector4f var4 = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F).mul(var2).mul(var3);
         if (Float.isFinite(var4.w) && !(Math.abs(var4.w) <= 1.0E-6F)) {
            var4.div(var4.w);
            if (Float.isFinite(var4.x) && Float.isFinite(var4.y) && Float.isFinite(var4.z)) {
               class_243 var5 = var1.method_71156();
               return Double.isFinite(var5.field_1352) && Double.isFinite(var5.field_1351) && Double.isFinite(var5.field_1350)
                  ? new class_243(var5.field_1352 + var4.x, var5.field_1351 + var4.y, var5.field_1350 + var4.z)
                  : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void updateCrosshairOriginCache() {
      cachedCrosshairOriginValid = false;
      class_243 var0 = computeCrosshairOrigin();
      if (var0 == null) {
         center = null;
      } else {
         cachedCrosshairOriginX = var0.field_1352;
         cachedCrosshairOriginY = var0.field_1351;
         cachedCrosshairOriginZ = var0.field_1350;
         cachedCrosshairOriginValid = true;
         center = var0;
      }
   }

   private static void updateAdaptiveDrawFlags() {
      allowOutlineThisFrame = true;
      allowFillThisFrame = true;
      allowTracerThisFrame = true;
   }

   public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      float var13 = color >> (16) & (255);
      float var14 = color >> (8) & (255);
      float var15 = color & (255);
      float var16 = color >> (24) & (255);
      if (!(var16 <= 0.0F)) {
         linesMesh.ensureCapacity(2, 2);
         int var17 = linesMesh.vec3(x1, y1, z1).color((int)var13, (int)var14, (int)var15, (int)var16).next();
         int var18 = linesMesh.vec3(x2, y2, z2).color((int)var13, (int)var14, (int)var15, (int)var16).next();
         linesMesh.line(var17, var18);
      }
   }

   public static void drawThickLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
      int var14 = color >>> (24) & (255);
      if (var14 > 0) {
         int var15 = color & (16777215) | var14 << (24);
         drawLine(x1, y1, z1, x2, y2, z2, var15);
      }
   }

   public static void drawFlatPlane(double x1, double z1, double x2, double z2, double y, int color) {
      int var11 = color >> (16) & (255);
      int var12 = color >> (8) & (255);
      int var13 = color & (255);
      int var14 = color >> (24) & (255);
      if (var14 > 0) {
         trianglesMesh.ensureCapacity(4, 12);
         int var15 = trianglesMesh.vec3(x1, y, z1).color(var11, var12, var13, var14).next();
         int var16 = trianglesMesh.vec3(x1, y, z2).color(var11, var12, var13, var14).next();
         int var17 = trianglesMesh.vec3(x2, y, z2).color(var11, var12, var13, var14).next();
         int var18 = trianglesMesh.vec3(x2, y, z1).color(var11, var12, var13, var14).next();
         trianglesMesh.quad(var15, var16, var17, var18);
         trianglesMesh.quad(var18, var17, var16, var15);
      }
   }

   public static void drawFlatPlaneSingleSided(double x1, double z1, double x2, double z2, double y, int color) {
      int var11 = color >> (16) & (255);
      int var12 = color >> (8) & (255);
      int var13 = color & (255);
      int var14 = color >> (24) & (255);
      if (var14 > 0) {
         trianglesMesh.ensureCapacity(4, 6);
         int var15 = trianglesMesh.vec3(x1, y, z1).color(var11, var12, var13, var14).next();
         int var16 = trianglesMesh.vec3(x1, y, z2).color(var11, var12, var13, var14).next();
         int var17 = trianglesMesh.vec3(x2, y, z2).color(var11, var12, var13, var14).next();
         int var18 = trianglesMesh.vec3(x2, y, z1).color(var11, var12, var13, var14).next();
         trianglesMesh.quad(var15, var16, var17, var18);
      }
   }

   public static void drawTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, int color) {
      int var19 = color >> (16) & (255);
      int var20 = color >> (8) & (255);
      int var21 = color & (255);
      int var22 = color >> (24) & (255);
      if (var22 > 0) {
         trianglesMesh.ensureCapacity(3, 12);
         int var23 = trianglesMesh.vec3(x1, y1, z1).color(var19, var20, var21, var22).next();
         int var24 = trianglesMesh.vec3(x2, y2, z2).color(var19, var20, var21, var22).next();
         int var25 = trianglesMesh.vec3(x3, y3, z3).color(var19, var20, var21, var22).next();
         trianglesMesh.quad(var23, var24, var25, var25);
         trianglesMesh.quad(var23, var25, var24, var24);
      }
   }

   public static void drawTriangleGouraud(
      double x1, double y1, double z1, int c1, double x2, double y2, double z2, int c2, double x3, double y3, double z3, int c3
   ) {
      int var21 = c1 >> (24) & (255);
      int var22 = c2 >> (24) & (255);
      int var23 = c3 >> (24) & (255);
      if (var21 > 0 || var22 > 0 || var23 > 0) {
         trianglesMesh.ensureCapacity(3, 12);
         int var24 = trianglesMesh.vec3(x1, y1, z1)
            .color(c1 >> (16) & (255), c1 >> (8) & (255), c1 & (255), var21)
            .next();
         int var25 = trianglesMesh.vec3(x2, y2, z2)
            .color(c2 >> (16) & (255), c2 >> (8) & (255), c2 & (255), var22)
            .next();
         int var26 = trianglesMesh.vec3(x3, y3, z3)
            .color(c3 >> (16) & (255), c3 >> (8) & (255), c3 & (255), var23)
            .next();
         trianglesMesh.quad(var24, var25, var26, var26);
         trianglesMesh.quad(var24, var26, var25, var25);
      }
   }

   public static void drawTriangleAdditiveGouraud(
      double x1, double y1, double z1, int c1, double x2, double y2, double z2, int c2, double x3, double y3, double z3, int c3
   ) {
      int var21 = c1 >> (24) & (255);
      int var22 = c2 >> (24) & (255);
      int var23 = c3 >> (24) & (255);
      if (var21 > 0 || var22 > 0 || var23 > 0) {
         additiveTrianglesMesh.ensureCapacity(3, 12);
         int var24 = additiveTrianglesMesh.vec3(x1, y1, z1)
            .color(
               Math.round((c1 >> (16) & (255)) * (var21 / 255.0F)),
               Math.round((c1 >> (8) & (255)) * (var21 / 255.0F)),
               Math.round((c1 & (255)) * (var21 / 255.0F)),
               255
            )
            .next();
         int var25 = additiveTrianglesMesh.vec3(x2, y2, z2)
            .color(
               Math.round((c2 >> (16) & (255)) * (var22 / 255.0F)),
               Math.round((c2 >> (8) & (255)) * (var22 / 255.0F)),
               Math.round((c2 & (255)) * (var22 / 255.0F)),
               255
            )
            .next();
         int var26 = additiveTrianglesMesh.vec3(x3, y3, z3)
            .color(
               Math.round((c3 >> (16) & (255)) * (var23 / 255.0F)),
               Math.round((c3 >> (8) & (255)) * (var23 / 255.0F)),
               Math.round((c3 & (255)) * (var23 / 255.0F)),
               255
            )
            .next();
         additiveTrianglesMesh.quad(var24, var25, var26, var26);
         additiveTrianglesMesh.quad(var24, var26, var25, var25);
      }
   }

   public static void drawTriangleAdditiveDepthGouraud(
      double x1, double y1, double z1, int c1, double x2, double y2, double z2, int c2, double x3, double y3, double z3, int c3
   ) {
      int var21 = c1 >> (24) & (255);
      int var22 = c2 >> (24) & (255);
      int var23 = c3 >> (24) & (255);
      if (var21 > 0 || var22 > 0 || var23 > 0) {
         additiveDepthTrianglesMesh.ensureCapacity(3, 12);
         int var24 = additiveDepthTrianglesMesh.vec3(x1, y1, z1)
            .color(
               Math.round((c1 >> (16) & (255)) * (var21 / 255.0F)),
               Math.round((c1 >> (8) & (255)) * (var21 / 255.0F)),
               Math.round((c1 & (255)) * (var21 / 255.0F)),
               255
            )
            .next();
         int var25 = additiveDepthTrianglesMesh.vec3(x2, y2, z2)
            .color(
               Math.round((c2 >> (16) & (255)) * (var22 / 255.0F)),
               Math.round((c2 >> (8) & (255)) * (var22 / 255.0F)),
               Math.round((c2 & (255)) * (var22 / 255.0F)),
               255
            )
            .next();
         int var26 = additiveDepthTrianglesMesh.vec3(x3, y3, z3)
            .color(
               Math.round((c3 >> (16) & (255)) * (var23 / 255.0F)),
               Math.round((c3 >> (8) & (255)) * (var23 / 255.0F)),
               Math.round((c3 & (255)) * (var23 / 255.0F)),
               255
            )
            .next();
         additiveDepthTrianglesMesh.quad(var24, var25, var26, var26);
         additiveDepthTrianglesMesh.quad(var24, var26, var25, var25);
      }
   }

   public static void drawTriangleDepth(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, int color) {
      if (allowFillThisFrame) {
         float var19 = color >> (16) & (255);
         float var20 = color >> (8) & (255);
         float var21 = color & (255);
         int var22 = color >> (24) & (255);
         if (var22 > 0) {
            VieMeshBuilder var23 = getDepthTrianglesMesh();
            var23.ensureCapacity(3, 12);
            int var24 = var23.vec3(x1, y1, z1).color((int)var19, (int)var20, (int)var21, var22).next();
            int var25 = var23.vec3(x2, y2, z2).color((int)var19, (int)var20, (int)var21, var22).next();
            int var26 = var23.vec3(x3, y3, z3).color((int)var19, (int)var20, (int)var21, var22).next();
            var23.quad(var24, var25, var26, var26);
            var23.quad(var24, var26, var25, var25);
         }
      }
   }

   public static void drawTriangleDepthGouraud(
      double x1, double y1, double z1, int c1, double x2, double y2, double z2, int c2, double x3, double y3, double z3, int c3
   ) {
      if (allowFillThisFrame) {
         int var21 = c1 >> (24) & (255);
         int var22 = c2 >> (24) & (255);
         int var23 = c3 >> (24) & (255);
         if (var21 > 0 && var22 > 0 && var23 > 0) {
            VieMeshBuilder var24 = getDepthTrianglesMesh();
            var24.ensureCapacity(3, 12);
            int var25 = var24.vec3(x1, y1, z1)
               .color(
                  c1 >> (16) & (255),
                  c1 >> (8) & (255),
                  c1 & (255),
                  c1 >> (24) & (255)
               )
               .next();
            int var26 = var24.vec3(x2, y2, z2)
               .color(
                  c2 >> (16) & (255),
                  c2 >> (8) & (255),
                  c2 & (255),
                  c2 >> (24) & (255)
               )
               .next();
            int var27 = var24.vec3(x3, y3, z3)
               .color(
                  c3 >> (16) & (255),
                  c3 >> (8) & (255),
                  c3 & (255),
                  c3 >> (24) & (255)
               )
               .next();
            var24.quad(var25, var26, var27, var27);
            var24.quad(var25, var27, var26, var26);
         }
      }
   }

   public static void drawFlatPlaneBorder(double x1, double z1, double x2, double z2, double y, int color) {
      int var11 = color >> (16) & (255);
      int var12 = color >> (8) & (255);
      int var13 = color & (255);
      int var14 = color >> (24) & (255);
      if (var14 > 0) {
         linesMesh.ensureCapacity(4, 8);
         int var15 = linesMesh.vec3(x1, y, z1).color(var11, var12, var13, var14).next();
         int var16 = linesMesh.vec3(x1, y, z2).color(var11, var12, var13, var14).next();
         int var17 = linesMesh.vec3(x2, y, z2).color(var11, var12, var13, var14).next();
         int var18 = linesMesh.vec3(x2, y, z1).color(var11, var12, var13, var14).next();
         linesMesh.line(var15, var16);
         linesMesh.line(var16, var17);
         linesMesh.line(var17, var18);
         linesMesh.line(var18, var15);
      }
   }

   public static void drawFlatPlaneBorderThin(double x1, double z1, double x2, double z2, double y, int color) {
      int var11 = color >> (16) & (255);
      int var12 = color >> (8) & (255);
      int var13 = color & (255);
      int var14 = color >> (24) & (255);
      if (var14 > 0) {
         thinLinesMesh.ensureCapacity(4, 8);
         int var15 = thinLinesMesh.vec3(x1, y, z1).color(var11, var12, var13, var14).next();
         int var16 = thinLinesMesh.vec3(x1, y, z2).color(var11, var12, var13, var14).next();
         int var17 = thinLinesMesh.vec3(x2, y, z2).color(var11, var12, var13, var14).next();
         int var18 = thinLinesMesh.vec3(x2, y, z1).color(var11, var12, var13, var14).next();
         thinLinesMesh.line(var15, var16);
         thinLinesMesh.line(var16, var17);
         thinLinesMesh.line(var17, var18);
         thinLinesMesh.line(var18, var15);
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
}
