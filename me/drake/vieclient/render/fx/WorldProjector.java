package me.drake.vieclient.render.fx;

import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.modules.render.TrueStretched;
import me.drake.vieclient.render.VieMeshRenderer;
import net.minecraft.class_1041;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4184;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public final class WorldProjector {
   private static final float NEAR_EPSILON = 0.05F;
   private static final int[][] BOX_EDGES;
   private final Matrix4f viewProjMatrix = new Matrix4f();
   private final Matrix4f viewMatrix = new Matrix4f();
   private final Vector4f clipScratch = new Vector4f();
   private final double[][] cornersScratch = new double[8][3];
   private final float[] projBoxScratch = new float[3];
   private boolean useMatrices = false;
   private double camX;
   private double camY;
   private double camZ;
   private double cosYaw;
   private double sinYaw;
   private double cosPitch;
   private double sinPitch;
   private float tanHalfFov;
   private float aspect;
   private float guiScale = 1.0F;
   private int fbW;
   private int fbH;
   private float logicalW;
   private float logicalH;
   private boolean valid;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean snapshot() {
      this.valid = false;
      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.field_1687 != null && var1.field_1773 != null && var1.field_1690 != null) {
         class_4184 var2 = var1.field_1773.method_19418();
         if (var2 == null) {
            return false;
         }

         class_1041 var3 = var1.method_22683();
         if (var3 == null) {
            return false;
         }

         this.fbW = var3.method_4489();
         this.fbH = var3.method_4506();
         if (this.fbW > 0 && this.fbH > 0) {
            this.guiScale = var3.method_4495();
            if (this.guiScale <= 0.0F) {
               this.guiScale = 1.0F;
            }

            this.logicalW = this.fbW / this.guiScale;
            this.logicalH = this.fbH / this.guiScale;
            class_243 var4 = var2.method_71156();
            this.camX = var4.field_1352;
            this.camY = var4.field_1351;
            this.camZ = var4.field_1350;
            if (VieMeshRenderer.hasCapturedWorldMatrices) {
               this.viewMatrix.set(VieMeshRenderer.lastPositionMatrix);
               this.viewProjMatrix.set(VieMeshRenderer.lastProjectionMatrix).mul(this.viewMatrix);
               this.useMatrices = true;
            } else {
               this.useMatrices = false;
            }

            double var5 = Math.toRadians(var2.method_19330() + 180.0);
            double var7 = Math.toRadians(var2.method_19329());
            this.cosYaw = Math.cos(var5);
            this.sinYaw = Math.sin(var5);
            this.cosPitch = Math.cos(var7);
            this.sinPitch = Math.sin(var7);
            if (var1.method_61966() != null) {
               var1.method_61966().method_60637(true);
            } else {
               float var10000 = 1.0F;
            }

            float var10;
            try {
               var10 = ((Integer)var1.field_1690.method_41808().method_41753()).intValue();
            } catch (Throwable var12) {
               var10 = 70.0F;
            }

            this.tanHalfFov = (float)Math.tan(Math.toRadians(var10) / 2.0);
            if (this.tanHalfFov <= 0.0F) {
               this.tanHalfFov = 0.7F;
            }

            this.aspect = TrueStretched.getEffectiveAspectRatio(this.fbW, this.fbH);
            this.valid = true;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isValid() {
      return this.valid;
   }

   void setupForTest(double camX, double camY, double camZ, float yaw, float pitch, float fov, int fbW, int fbH, float guiScale) {
      this.camX = camXx;
      this.camY = camYx;
      this.camZ = camZx;
      this.fbW = fbWx;
      this.fbH = fbHx;
      this.guiScale = guiScalex <= 0.0F ? 1.0F : guiScalex;
      this.logicalW = fbWx / this.guiScale;
      this.logicalH = fbHx / this.guiScale;
      double var13 = Math.toRadians(yaw + 180.0);
      double var15 = Math.toRadians(pitch);
      this.cosYaw = Math.cos(var13);
      this.sinYaw = Math.sin(var13);
      this.cosPitch = Math.cos(var15);
      this.sinPitch = Math.sin(var15);
      this.tanHalfFov = (float)Math.tan(Math.toRadians(fov) / 2.0);
      if (this.tanHalfFov <= 0.0F) {
         this.tanHalfFov = 0.7F;
      }

      this.aspect = (float)fbWx / fbHx;
      this.useMatrices = false;
      this.valid = true;
   }

   public boolean project(double wx, double wy, double wz, float[] out) {
      if (!this.valid || out == null || out.length < 3) {
         return false;
      }

      if (this.useMatrices) {
         float var17 = (float)(wx - this.camX);
         float var18 = (float)(wy - this.camY);
         float var19 = (float)(wz - this.camZ);
         this.viewProjMatrix.transform(var17, var18, var19, 1.0F, this.clipScratch);
         if (this.clipScratch.w <= 0.05F) {
            return false;
         }

         float var20 = 1.0F / this.clipScratch.w;
         float var21 = this.clipScratch.x * var20;
         float var22 = this.clipScratch.y * var20;
         out[0] = (var21 * 0.5F + 0.5F) * this.fbW / this.guiScale;
         out[1] = (1.0F - (var22 * 0.5F + 0.5F)) * this.fbH / this.guiScale;
         out[2] = this.clipScratch.w;
         return true;
      } else {
         float var8 = (float)(wx - this.camX);
         float var9 = (float)(wy - this.camY);
         float var10 = (float)(wz - this.camZ);
         float var11 = (float)(var8 * this.cosYaw + var10 * this.sinYaw);
         float var12 = var9;
         float var13 = (float)(-var8 * this.sinYaw + var10 * this.cosYaw);
         float var14 = (float)(var12 * this.cosPitch - var13 * this.sinPitch);
         float var15 = (float)(var12 * this.sinPitch + var13 * this.cosPitch);
         if (var15 >= -0.05F) {
            return false;
         }

         float var16 = -var15;
         out[0] = (float)(var11 / var16 / (this.tanHalfFov * this.aspect) * (this.fbW / 2.0) + this.fbW / 2.0) / this.guiScale;
         out[1] = (float)(-var14 / var16 / this.tanHalfFov * (this.fbH / 2.0) + this.fbH / 2.0) / this.guiScale;
         out[2] = var16;
         return true;
      }
   }

   public boolean projectBox(class_238 box, float[] out) {
      if (this.valid && box != null && out != null && out.length >= 5) {
         this.cornersScratch[0][0] = box.field_1323;
         this.cornersScratch[0][1] = box.field_1322;
         this.cornersScratch[0][2] = box.field_1321;
         this.cornersScratch[1][0] = box.field_1320;
         this.cornersScratch[1][1] = box.field_1322;
         this.cornersScratch[1][2] = box.field_1321;
         this.cornersScratch[2][0] = box.field_1320;
         this.cornersScratch[2][1] = box.field_1322;
         this.cornersScratch[2][2] = box.field_1324;
         this.cornersScratch[3][0] = box.field_1323;
         this.cornersScratch[3][1] = box.field_1322;
         this.cornersScratch[3][2] = box.field_1324;
         this.cornersScratch[4][0] = box.field_1323;
         this.cornersScratch[4][1] = box.field_1325;
         this.cornersScratch[4][2] = box.field_1321;
         this.cornersScratch[5][0] = box.field_1320;
         this.cornersScratch[5][1] = box.field_1325;
         this.cornersScratch[5][2] = box.field_1321;
         this.cornersScratch[6][0] = box.field_1320;
         this.cornersScratch[6][1] = box.field_1325;
         this.cornersScratch[6][2] = box.field_1324;
         this.cornersScratch[7][0] = box.field_1323;
         this.cornersScratch[7][1] = box.field_1325;
         this.cornersScratch[7][2] = box.field_1324;
         float[] var3 = this.projBoxScratch;
         float var4 = Float.MAX_VALUE;
         float var5 = -Float.MAX_VALUE;
         float var6 = Float.MAX_VALUE;
         float var7 = -Float.MAX_VALUE;
         float var8 = Float.MAX_VALUE;
         int var9 = 0;

         for (double[] var13 : this.cornersScratch) {
            if (this.project(var13[0], var13[1], var13[2], var3)) {
               var4 = Math.min(var4, var3[0]);
               var5 = Math.max(var5, var3[0]);
               var6 = Math.min(var6, var3[1]);
               var7 = Math.max(var7, var3[1]);
               var8 = Math.min(var8, var3[2]);
               var9++;
            }
         }

         if (var9 < 8) {
            for (int[] var37 : BOX_EDGES) {
               double[] var14 = this.cornersScratch[var37[0]];
               double[] var15 = this.cornersScratch[var37[1]];
               boolean var16 = this.project(var14[0], var14[1], var14[2], var3);
               boolean var17 = this.project(var15[0], var15[1], var15[2], var3);
               if (var16 != var17) {
                  double var18 = this.getCameraDepth(var14[0], var14[1], var14[2]);
                  double var20 = this.getCameraDepth(var15[0], var15[1], var15[2]);
                  double var22 = var20 - var18;
                  if (Math.abs(var22) > 1.0E-6) {
                     double var24 = (0.07500000111758709 - var18) / var22;
                     if (var24 >= 0.0 && var24 <= 1.0) {
                        double var26 = var14[0] + var24 * (var15[0] - var14[0]);
                        double var28 = var14[1] + var24 * (var15[1] - var14[1]);
                        double var30 = var14[2] + var24 * (var15[2] - var14[2]);
                        if (this.project(var26, var28, var30, var3)) {
                           var4 = Math.min(var4, var3[0]);
                           var5 = Math.max(var5, var3[0]);
                           var6 = Math.min(var6, var3[1]);
                           var7 = Math.max(var7, var3[1]);
                           var8 = Math.min(var8, var3[2]);
                           var9++;
                        }
                     }
                  }
               }
            }
         }

         if (var9 == 0) {
            return false;
         } else {
            float var33 = var5 - var4;
            float var35 = var7 - var6;
            if (!(var33 < 1.0F) && !(var35 < 1.0F)) {
               out[0] = var4;
               out[1] = var6;
               out[2] = var33;
               out[3] = var35;
               out[4] = var8;
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean projectPolygon(double[][] worldVertices, List outScreenPoints) {
      if (this.valid && worldVertices != null && worldVertices.length >= 3 && outScreenPoints != null) {
         outScreenPoints.clear();
         double var3 = 0.07500000111758709;
         int var5 = worldVertices.length;
         boolean var6 = true;
         boolean var7 = true;

         for (int var8 = 0; var8 < var5; var8++) {
            double[] var9 = worldVertices[var8];
            if (var9 == null || var9.length < 3) {
               return false;
            }

            double var10 = this.getCameraDepth(var9[0], var9[1], var9[2]);
            if (var10 >= var3) {
               var7 = false;
            } else {
               var6 = false;
            }
         }

         if (var7) {
            return false;
         }

         if (var6) {
            float var24 = 64.0F;
            float var26 = -var24;
            float var29 = this.logicalW + var24;
            float var11 = -var24;
            float var12 = this.logicalH + var24;
            boolean var13 = true;
            ArrayList var14 = new ArrayList(var5);
            float[] var15 = new float[3];

            for (int var16 = 0; var16 < var5; var16++) {
               double[] var17 = worldVertices[var16];
               if (!this.project(var17[0], var17[1], var17[2], var15)) {
                  var13 = false;
                  break;
               }

               if (var15[0] < var26 || var15[0] > var29 || var15[1] < var11 || var15[1] > var12) {
                  var13 = false;
               }

               var14.add(new float[]{var15[0], var15[1], var15[2]});
            }

            if (var13 && var14.size() == var5) {
               outScreenPoints.addAll(var14);
               return true;
            }

            if (var14.size() == var5) {
               List var47 = var14;
               var47 = clipPolygonAxis(var47, 0, var26, true);
               if (var47.size() < 3) {
                  return false;
               }

               var47 = clipPolygonAxis(var47, 0, var29, false);
               if (var47.size() < 3) {
                  return false;
               }

               var47 = clipPolygonAxis(var47, 1, var11, true);
               if (var47.size() < 3) {
                  return false;
               }

               var47 = clipPolygonAxis(var47, 1, var12, false);
               if (var47.size() < 3) {
                  return false;
               }

               outScreenPoints.addAll(var47);
               return true;
            }
         }

         ArrayList var25 = new ArrayList(worldVertices.length);

         for (double[] var36 : worldVertices) {
            if (var36 != null && var36.length >= 3) {
               var25.add(var36);
            }
         }

         if (var25.size() < 3) {
            return false;
         }

         ArrayList var28 = new ArrayList(var25.size() + 2);
         var5 = var25.size();

         for (int var31 = 0; var31 < var5; var31++) {
            double[] var34 = (double[])var25.get(var31);
            double[] var37 = (double[])var25.get((var31 + 1) % var5);
            double var40 = this.getCameraDepth(var34[0], var34[1], var34[2]);
            double var44 = this.getCameraDepth(var37[0], var37[1], var37[2]);
            boolean var52 = var40 >= var3;
            boolean var18 = var44 >= var3;
            if (var52) {
               if (var18) {
                  var28.add(var37);
               } else {
                  double var19 = var44 - var40;
                  double var21 = Math.abs(var19) > 1.0E-7 ? (var3 - var40) / var19 : 0.0;
                  var21 = Math.max(0.0, Math.min(1.0, var21));
                  var28.add(
                     new double[]{var34[0] + var21 * (var37[0] - var34[0]), var34[1] + var21 * (var37[1] - var34[1]), var34[2] + var21 * (var37[2] - var34[2])}
                  );
               }
            } else if (var18) {
               double var58 = var44 - var40;
               double var60 = Math.abs(var58) > 1.0E-7 ? (var3 - var40) / var58 : 0.0;
               var60 = Math.max(0.0, Math.min(1.0, var60));
               var28.add(
                  new double[]{var34[0] + var60 * (var37[0] - var34[0]), var34[1] + var60 * (var37[1] - var34[1]), var34[2] + var60 * (var37[2] - var34[2])}
               );
               var28.add(var37);
            }
         }

         if (var28.size() < 3) {
            return false;
         }

         ArrayList var32 = new ArrayList(var28.size());
         float[] var35 = new float[3];

         for (double[] var41 : var28) {
            if (this.project(var41[0], var41[1], var41[2], var35)) {
               var32.add(new float[]{var35[0], var35[1], var35[2]});
            }
         }

         if (var32.size() < 3) {
            return false;
         }

         float var39 = 64.0F;
         float var42 = -var39;
         float var43 = this.logicalW + var39;
         float var45 = -var39;
         float var46 = this.logicalH + var39;
         List var53 = var32;
         var53 = clipPolygonAxis(var53, 0, var42, true);
         if (var53.size() < 3) {
            return false;
         }

         var53 = clipPolygonAxis(var53, 0, var43, false);
         if (var53.size() < 3) {
            return false;
         }

         var53 = clipPolygonAxis(var53, 1, var45, true);
         if (var53.size() < 3) {
            return false;
         }

         var53 = clipPolygonAxis(var53, 1, var46, false);
         if (var53.size() < 3) {
            return false;
         }

         outScreenPoints.addAll(var53);
         return true;
      } else {
         return false;
      }
   }

   private static List clipPolygonAxis(List poly, int coordIndex, float boundary, boolean keepGreater) {
      int var4 = poly.size();
      if (var4 < 3) {
         return poly;
      }

      ArrayList var5 = new ArrayList(var4 + 2);

      for (int var6 = 0; var6 < var4; var6++) {
         float[] var7 = (float[])poly.get(var6);
         float[] var8 = (float[])poly.get((var6 + 1) % var4);
         boolean var9 = keepGreater ? var7[coordIndex] >= boundary : var7[coordIndex] <= boundary;
         boolean var10 = keepGreater ? var8[coordIndex] >= boundary : var8[coordIndex] <= boundary;
         if (var9) {
            if (var10) {
               var5.add(var8);
            } else {
               float var11 = var8[coordIndex] - var7[coordIndex];
               float var12 = Math.abs(var11) > 1.0E-7F ? (boundary - var7[coordIndex]) / var11 : 0.0F;
               var12 = Math.max(0.0F, Math.min(1.0F, var12));
               var5.add(new float[]{var7[0] + var12 * (var8[0] - var7[0]), var7[1] + var12 * (var8[1] - var7[1]), var7[2] + var12 * (var8[2] - var7[2])});
            }
         } else if (var10) {
            float var13 = var8[coordIndex] - var7[coordIndex];
            float var15 = Math.abs(var13) > 1.0E-7F ? (boundary - var7[coordIndex]) / var13 : 0.0F;
            var15 = Math.max(0.0F, Math.min(1.0F, var15));
            var5.add(new float[]{var7[0] + var15 * (var8[0] - var7[0]), var7[1] + var15 * (var8[1] - var7[1]), var7[2] + var15 * (var8[2] - var7[2])});
            var5.add(var8);
         }
      }

      return var5;
   }

   public double getCameraDepth(double wx, double wy, double wz) {
      if (this.useMatrices) {
         float var12 = (float)(wx - this.camX);
         float var13 = (float)(wy - this.camY);
         float var14 = (float)(wz - this.camZ);
         this.viewMatrix.transform(var12, var13, var14, 1.0F, this.clipScratch);
         return -this.clipScratch.z;
      } else {
         float var7 = (float)(wx - this.camX);
         float var8 = (float)(wy - this.camY);
         float var9 = (float)(wz - this.camZ);
         float var10 = (float)(-var7 * this.sinYaw + var9 * this.cosYaw);
         float var11 = (float)(this.y1(var7, var8, var9) * this.sinPitch + var10 * this.cosPitch);
         return -var11;
      }
   }

   private float y1(float rx, float ry, float rz) {
      return ry;
   }

   public boolean project(class_243 pos, float[] out) {
      return pos != null && this.project(pos.field_1352, pos.field_1351, pos.field_1350, out);
   }

   public boolean onScreen(float[] p, float margin) {
      return this.valid && p != null && p.length >= (2) ? this.onScreen(p[0], p[1], margin) : false;
   }

   public boolean onScreen(float x, float y, float margin) {
      return !this.valid ? false : x >= -margin && x <= this.logicalW + margin && y >= -margin && y <= this.logicalH + margin;
   }

   public float logicalWidth() {
      return this.logicalW;
   }

   public float logicalHeight() {
      return this.logicalH;
   }

   public float guiScale() {
      return this.guiScale;
   }

   public class_243 cameraPos() {
      return new class_243(this.camX, this.camY, this.camZ);
   }

   public double distanceSqTo(double wx, double wy, double wz) {
      double var7 = wx - this.camX;
      double var9 = wy - this.camY;
      double var11 = wz - this.camZ;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public static boolean hasLineOfSight(class_243 target) {
      if (target == null) {
         return true;
      }

      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.field_1687 != null && var1.field_1724 != null) {
         try {
            class_243 var2 = var1.field_1724.method_33571();
            class_3965 var3 = var1.field_1687.method_17742(new class_3959(var2, target, class_3960.field_17558, class_242.field_1348, var1.field_1724));
            return var3 == null || var3.method_17783() == class_240.field_1333;
         } catch (Throwable var4) {
            return true;
         }
      } else {
         return true;
      }
   }

   public static class_243 hitboxCentre(class_1297 entity, float tickDelta) {
      if (entity == null) {
         return class_243.field_1353;
      }

      double var2 = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317());
      double var4 = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318()) + entity.method_17682() * 0.5;
      double var6 = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321());
      return new class_243(var2, var4, var6);
   }

   static {
      int[][] var10000 = new int[12][];
      int[] var10003 = new int[2];
      var10003[0] = 0;
      var10003[1] = 1;
      var10000[0] = var10003;
      var10003 = new int[2];
      var10003[0] = 1;
      var10003[1] = 2;
      var10000[1] = var10003;
      int var10002 = 2;
      var10003 = new int[2];
      var10003[0] = 2;
      var10003[1] = 3;
      var10000[var10002] = var10003;
      var10002 = 3;
      var10003 = new int[2];
      var10003[0] = 3;
      var10003[1] = 0;
      var10000[var10002] = var10003;
      var10002 = 4;
      var10003 = new int[2];
      var10003[0] = 4;
      var10003[1] = 5;
      var10000[var10002] = var10003;
      var10002 = 5;
      var10003 = new int[2];
      var10003[0] = 5;
      var10003[1] = 6;
      var10000[var10002] = var10003;
      var10002 = 6;
      var10003 = new int[2];
      var10003[0] = 6;
      var10003[1] = 7;
      var10000[var10002] = var10003;
      var10002 = 7;
      var10003 = new int[2];
      var10003[0] = 7;
      var10003[1] = 4;
      var10000[var10002] = var10003;
      var10002 = 8;
      var10003 = new int[2];
      var10003[0] = 0;
      var10003[1] = 4;
      var10000[var10002] = var10003;
      var10002 = 9;
      var10003 = new int[2];
      var10003[0] = 1;
      var10003[1] = 5;
      var10000[var10002] = var10003;
      var10002 = 10;
      var10003 = new int[2];
      var10003[0] = 2;
      var10003[1] = 6;
      var10000[var10002] = var10003;
      var10002 = 11;
      var10003 = new int[2];
      var10003[0] = 3;
      var10003[1] = 7;
      var10000[var10002] = var10003;
      BOX_EDGES = var10000;
   }
}
