package me.drake.vieclient.modules.render;

import de.javagl.jgltf.model.AccessorByteData;
import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AccessorIntData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AccessorShortData;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import org.joml.Matrix4f;

public class NonLa extends Module {
   private final DoubleSetting size = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Size",
         "Hat size.",
         1.0,
         0.4,
         2.5,
         0.05
      )
   );
   private final BoolSetting onlySelf = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnlySelf",
         "Only render on your own head.",
         false
      )
   );
   private final BoolSetting flagSkin = (BoolSetting)this.addSetting(
      new BoolSetting(
         "VietnameseHat",
         "Wear the Vietnamese flag skin (red + yellow star).",
         true
      )
   );
   private final DoubleSetting opacity = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Opacity",
         "Hat transparency (lower = more see-through).",
         1.0,
         0.1,
         1.0,
         0.05
      )
   );
   private static final int SEGMENTS = 36;
   private static final int FLAG_RED = -2480867;
   private static final int STRAW = -1914481;
   private static final int STAR_YELLOW = -10496;
   private static final float[] LIGHT = normalize(0.45F, 0.85F, 0.3F);
   private static List modelTris;
   private static double modelHalfW = 0.42;
   private static double modelHeight = 0.4;
   private static long lastModelTry;
   private final Map headTopSmooth = new HashMap();
   private static final Map BODY_TRANSFORMS = new ConcurrentHashMap();
   private static final long TRANSFORM_TTL_MS = 250L;
   private static final double HEAD_STANDING = 1.82;
   private static final double HEAD_SNEAKING = 1.52;
   private static double tiltAx;
   private static double tiltAy;
   private static double tiltAz;
   private static double tiltKx;
   private static double tiltKy;
   private static double tiltKz;
   private static double tiltSin;
   private static double tiltCos;
   private static boolean tiltActive;
   private long lastFrameMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void captureBodyTransform(int entityId, Matrix4f matrix) {
      if (matrix != null) {
         double var2 = matrix.m10();
         double var4 = matrix.m11();
         double var6 = matrix.m12();
         double var8 = Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
         if (var8 < 1.0E-6) {
            var2 = 0.0;
            var4 = 1.0;
            var6 = 0.0;
         } else {
            var2 /= var8;
            var4 /= var8;
            var6 /= var8;
         }

         Map var10000 = BODY_TRANSFORMS;
         Integer var10001 = entityId;
         double[] var10002 = new double[7];
         var10002[0] = matrix.m30();
         var10002[1] = matrix.m31();
         var10002[2] = matrix.m32();
         var10002[3] = var2;
         var10002[4] = var4;
         var10002[5] = var6;
         var10002[6] = System.currentTimeMillis();
         var10000.put(var10001, var10002);
      }
   }

   private static double[] bodyTransform(int entityId) {
      double[] var1 = (double[])BODY_TRANSFORMS.get(entityId);
      if (var1 == null) {
         return null;
      } else if (System.currentTimeMillis() - (long)var1[6] > 250L) {
         BODY_TRANSFORMS.remove(entityId);
         return null;
      } else {
         return var1;
      }
   }

   private static void setTilt(double ax, double ay, double az, double ux, double uy, double uz) {
      tiltAx = ax;
      tiltAy = ay;
      tiltAz = az;
      double var12 = uz;
      double var14 = 0.0;
      double var16 = -ux;
      double var18 = Math.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
      if (var18 < 1.0E-6) {
         if (uy >= 0.0) {
            tiltActive = false;
         } else {
            tiltKx = 1.0;
            tiltKy = 0.0;
            tiltKz = 0.0;
            tiltSin = 0.0;
            tiltCos = -1.0;
            tiltActive = true;
         }
      } else {
         tiltKx = var12 / var18;
         tiltKy = var14 / var18;
         tiltKz = var16 / var18;
         tiltSin = var18;
         tiltCos = uy;
         tiltActive = true;
      }
   }

   private static void clearTilt() {
      tiltActive = false;
   }

   private static double[] tiltDir(double x, double y, double z) {
      if (!tiltActive) {
         double[] var16 = new double[3];
         var16[0] = x;
         var16[1] = y;
         var16[2] = z;
         return var16;
      } else {
         double var6 = tiltKx * x + tiltKy * y + tiltKz * z;
         double var8 = tiltKy * z - tiltKz * y;
         double var10 = tiltKz * x - tiltKx * z;
         double var12 = tiltKx * y - tiltKy * x;
         double var14 = 1.0 - tiltCos;
         double[] var10000 = new double[3];
         var10000[0] = x * tiltCos + var8 * tiltSin + tiltKx * var6 * var14;
         var10000[1] = y * tiltCos + var10 * tiltSin + tiltKy * var6 * var14;
         var10000[2] = z * tiltCos + var12 * tiltSin + tiltKz * var6 * var14;
         return var10000;
      }
   }

   private static double[] tiltPoint(double x, double y, double z) {
      if (!tiltActive) {
         double[] var7 = new double[3];
         var7[0] = x;
         var7[1] = y;
         var7[2] = z;
         return var7;
      } else {
         double[] var6 = tiltDir(x - tiltAx, y - tiltAy, z - tiltAz);
         double[] var10000 = new double[3];
         var10000[0] = tiltAx + var6[0];
         var10000[1] = tiltAy + var6[1];
         var10000[2] = tiltAz + var6[2];
         return var10000;
      }
   }

   public NonLa() {
      super(
         "NonLa",
         "30/4/1975",
         Category.CUSTOMIZE
      );
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (this.isInGame() && mc.field_1724 != null && mc.field_1687 != null) {
            if (VieClient.INSTANCE == null || VieClient.INSTANCE.GUI == null || mc.field_1755 != VieClient.INSTANCE.GUI) {
               float var2 = event.getTickDelta();
               float var3 = ((Double)this.size.get()).floatValue();
               boolean var4 = mc.field_1690.method_31044().method_31034();
               int var5 = this.flagSkin.get() ? -2480867 : -1914481;
               float var6 = ((Double)this.opacity.get()).floatValue();
               long var7 = System.currentTimeMillis();
               float var9 = this.lastFrameMs > 0L ? Math.min(0.05F, (float)(var7 - this.lastFrameMs) / 1000.0F) : 0.016F;
               this.lastFrameMs = var7;
               ensureModel();
               List var10 = modelTris;
               boolean var11 = false;

               try {
                  Freecam var12 = Freecam.getInstance();
                  var11 = var12 != null && var12.isActive();
               } catch (Throwable var34) {
               }

               for (class_1657 var13 : mc.field_1687.method_18456()) {
                  if (var13 != null
                     && var13.method_5805()
                     && (var13 != mc.field_1724 || !var4 || var11)
                     && (!(Boolean)this.onlySelf.get() || var13 == mc.field_1724)) {
                     double[] var20 = bodyTransform(var13.method_5628());
                     double var14;
                     double var16;
                     double var18;
                     if (var20 != null) {
                        double var21 = var13.method_5715() ? 1.52 : 1.82;
                        double var23 = this.headTopSmooth.getOrDefault(var13.method_5628(), var21);
                        var23 += (var21 - var23) * (1.0 - Math.exp(-12.0 * var9));
                        this.headTopSmooth.put(var13.method_5628(), var23);
                        var14 = lerp(var2, var13.field_6038, var13.method_23317()) + var20[3] * var23;
                        var16 = lerp(var2, var13.field_5971, var13.method_23318()) + var20[4] * var23;
                        var18 = lerp(var2, var13.field_5989, var13.method_23321()) + var20[5] * var23;
                        setTilt(var14, var16, var18, var20[3], var20[4], var20[5]);
                     } else {
                        var14 = lerp(var2, var13.field_6038, var13.method_23317());
                        double var36 = lerp(var2, var13.field_5971, var13.method_23318());
                        var18 = lerp(var2, var13.field_5989, var13.method_23321());
                        double var39 = var13.method_5829().field_1325 - var13.method_23318();
                        double var25 = this.headTopSmooth.getOrDefault(var13.method_5628(), var39);
                        var25 += (var39 - var25) * (1.0 - Math.exp(-12.0 * var9));
                        this.headTopSmooth.put(var13.method_5628(), var25);
                        var16 = var36 + var25 + 0.02;
                        clearTilt();
                     }

                     class_243 var37 = var13.method_5720();
                     double var22 = Math.hypot(var37.field_1352, var37.field_1350);
                     double var24 = var22 > 1.0E-6 ? var37.field_1352 / var22 : 0.0;
                     double var26 = var22 > 1.0E-6 ? var37.field_1350 / var22 : -1.0;
                     double var28 = Math.atan2(var26, var24);
                     double var30 = -var26;
                     double var32 = var24;
                     if (var10 != null) {
                        drawModel(var10, var14, var16, var18, var30, var32, var5, var6);
                        if ((Boolean)this.flagSkin.get()) {
                           this.drawStarDecal(var14, var16, var18, var3, modelHalfW, modelHeight, var28);
                        }
                     } else {
                        this.drawProcedural(var14, var16, var18, var28, var5, var3, var6);
                        if ((Boolean)this.flagSkin.get()) {
                           this.drawStarDecal(var14, var16, var18, var3, 0.5 * var3, 0.44 * var3, var28);
                        }
                     }

                     clearTilt();
                  }
               }
            }
         }
      }
   }

   private static void ensureModel() {
      if (modelTris == null) {
         long var0 = System.currentTimeMillis();
         if (var0 - lastModelTry >= 2000L) {
            lastModelTry = var0;

            try {
               class_310 var2 = class_310.method_1551();
               Path var3 = var2.field_1697
                  .toPath()
                  .resolve(
                     "config/vie/non_la.glb"
                  );
               if (!Files.exists(var3)) {
                  return;
               }

               GltfModel var4 = new GltfModelReader().read(var3);
               ArrayList var5 = new ArrayList();

               for (MeshModel var7 : var4.getMeshModels()) {
                  for (MeshPrimitiveModel var9 : var7.getMeshPrimitiveModels()) {
                     AccessorModel var10 = var9.getAttributes().get("POSITION");
                     if (var10 != null) {
                        AccessorFloatData var11 = AccessorDatas.createFloat(var10);
                        if (var11 != null) {
                           int var12 = var11.getNumElements();
                           List var13 = collectFaces(var9, var11, var12);
                           float[] var14 = new float[var12 * 3];

                           for (int[] var16 : var13) {
                              accumulate(var14, var11, var16);
                           }

                           for (int var18 = 0; var18 < var12; var18++) {
                              float var20 = (float)Math.sqrt(
                                 var14[var18 * 3] * var14[var18 * 3]
                                    + var14[var18 * 3 + 1] * var14[var18 * 3 + 1]
                                    + var14[var18 * 3 + 2] * var14[var18 * 3 + 2]
                              );
                              if (var20 < 1.0E-6F) {
                                 var14[var18 * 3 + 1] = 1.0F;
                              } else {
                                 var14[var18 * 3] = var14[var18 * 3] / var20;
                                 var14[var18 * 3 + 1] = var14[var18 * 3 + 1] / var20;
                                 var14[var18 * 3 + 2] = var14[var18 * 3 + 2] / var20;
                              }
                           }

                           for (int[] var21 : var13) {
                              var5.add(
                                 new NonLa.Tri(
                                    var11.get(var21[0], 0),
                                    var11.get(var21[0], 1),
                                    var11.get(var21[0], 2),
                                    var11.get(var21[1], 0),
                                    var11.get(var21[1], 1),
                                    var11.get(var21[1], 2),
                                    var11.get(var21[2], 0),
                                    var11.get(var21[2], 1),
                                    var11.get(var21[2], 2),
                                    var14[var21[0] * 3],
                                    var14[var21[0] * 3 + 1],
                                    var14[var21[0] * 3 + 2],
                                    var14[var21[1] * 3],
                                    var14[var21[1] * 3 + 1],
                                    var14[var21[1] * 3 + 2],
                                    var14[var21[2] * 3],
                                    var14[var21[2] * 3 + 1],
                                    var14[var21[2] * 3 + 2]
                                 )
                              );
                           }
                        }
                     }
                  }
               }

               if (var5.isEmpty()) {
                  return;
               }

               modelTris = normalize(var5);
               VieClientMod.LOGGER
                  .info(
                     "[NonLa] Loaded {} triangles from {}",
                     var5.size(),
                     var3
                  );
            } catch (Exception var17) {
               VieClientMod.LOGGER
                  .error(
                     "[NonLa] Failed to load GLB model",
                     var17
                  );
            }
         }
      }
   }

   private static List collectFaces(MeshPrimitiveModel prim, AccessorFloatData pos, int vc) {
      ArrayList var3 = new ArrayList();
      AccessorData var4 = prim.getIndices() == null ? null : AccessorDatas.create(prim.getIndices());
      int var5 = prim.getMode();
      if (var4 == null) {
         for (byte var6 = 0; var6 + (2) < vc; var6 += 3) {
            int[] var10001 = new int[3];
            var10001[0] = var6;
            var10001[1] = var6 + 1;
            var10001[2] = var6 + (2);
            var3.add(var10001);
         }
      } else if (var5 == (5)) {
         for (int var7 = 0; var7 + (2) < var4.getNumElements(); var7++) {
            int[] var9 = new int[3];
            var9[0] = indexAt(var4, var7);
            var9[1] = indexAt(var4, var7 + 1);
            var9[2] = indexAt(var4, var7 + (2));
            var3.add(var9);
         }
      } else {
         for (byte var8 = 0; var8 + (2) < var4.getNumElements(); var8 += 3) {
            int[] var10 = new int[3];
            var10[0] = indexAt(var4, var8);
            var10[1] = indexAt(var4, var8 + 1);
            var10[2] = indexAt(var4, var8 + (2));
            var3.add(var10);
         }
      }

      return var3;
   }

   private static void accumulate(float[] acc, AccessorFloatData pos, int[] f) {
      double var3 = pos.get(f[1], 0) - pos.get(f[0], 0);
      double var5 = pos.get(f[1], 1) - pos.get(f[0], 1);
      double var7 = pos.get(f[1], 2) - pos.get(f[0], 2);
      double var9 = pos.get(f[2], 0) - pos.get(f[0], 0);
      double var11 = pos.get(f[2], 1) - pos.get(f[0], 1);
      double var13 = pos.get(f[2], 2) - pos.get(f[0], 2);
      float var15 = (float)(var5 * var13 - var7 * var11);
      float var16 = (float)(var7 * var9 - var3 * var13);
      float var17 = (float)(var3 * var11 - var5 * var9);

      for (int var21 : f) {
         acc[var21 * (3)] = acc[var21 * (3)] + var15;
         acc[var21 * (3) + 1] = acc[var21 * (3) + 1] + var16;
         acc[var21 * (3) + (2)] = acc[var21 * (3) + (2)] + var17;
      }
   }

   private static int indexAt(AccessorData d, int i) {
      if (d instanceof AccessorIntData var4) {
         return var4.get(i);
      } else if (d instanceof AccessorShortData var3) {
         return var3.get(i) & (65535);
      } else {
         return d instanceof AccessorByteData var2 ? var2.get(i) & (255) : i;
      }
   }

   private static List normalize(List raw) {
      double var1 = Double.MAX_VALUE;
      double var3 = Double.MAX_VALUE;
      double var5 = Double.MAX_VALUE;
      double var7 = -Double.MAX_VALUE;
      double var9 = -Double.MAX_VALUE;
      double var11 = -Double.MAX_VALUE;

      for (NonLa.Tri var14 : raw) {
         var1 = Math.min(var1, var14.minX());
         var3 = Math.min(var3, var14.minY());
         var5 = Math.min(var5, var14.minZ());
         var7 = Math.max(var7, var14.maxX());
         var9 = Math.max(var9, var14.maxY());
         var11 = Math.max(var11, var14.maxZ());
      }

      double var24 = Math.max(var7 - var1, Math.max(var9 - var3, var11 - var5));
      if (var24 <= 1.0E-6) {
         return null;
      }

      double var15 = 0.9 / var24;
      double var17 = (var1 + var7) / 2.0;
      double var19 = (var5 + var11) / 2.0;
      ArrayList var21 = new ArrayList(raw.size());

      for (NonLa.Tri var23 : raw) {
         var21.add(
            new NonLa.Tri(
               (var23.v0x - var17) * var15,
               (var23.v0y - var3) * var15,
               (var23.v0z - var19) * var15,
               (var23.v1x - var17) * var15,
               (var23.v1y - var3) * var15,
               (var23.v1z - var19) * var15,
               (var23.v2x - var17) * var15,
               (var23.v2y - var3) * var15,
               (var23.v2z - var19) * var15,
               var23.n0x,
               var23.n0y,
               var23.n0z,
               var23.n1x,
               var23.n1y,
               var23.n1z,
               var23.n2x,
               var23.n2y,
               var23.n2z
            )
         );
      }

      modelHalfW = (var7 - var1) / 2.0 * var15;
      modelHeight = (var9 - var3) * var15;
      return var21;
   }

   private static void drawModel(List model, double ox, double oy, double oz, double cs, double sn, int argb, float op) {
      int var13 = argb >> (16) & (255);
      int var14 = argb >> (8) & (255);
      int var15 = argb & (255);

      for (NonLa.Tri var17 : model) {
         double[] var18 = rot(var17.v0x, var17.v0z, cs, sn);
         double[] var19 = rot(var17.v1x, var17.v1z, cs, sn);
         double[] var20 = rot(var17.v2x, var17.v2z, cs, sn);
         double[] var21 = rot(var17.n0x, var17.n0z, cs, sn);
         double[] var22 = rot(var17.n1x, var17.n1z, cs, sn);
         double[] var23 = rot(var17.n2x, var17.n2z, cs, sn);
         double[] var24 = tiltDir(var21[0], var17.n0y, var21[1]);
         double[] var25 = tiltDir(var22[0], var17.n1y, var22[1]);
         double[] var26 = tiltDir(var23[0], var17.n2y, var23[1]);
         int var27 = shadeColor(var13, var14, var15, brightness(var24[0], var24[1], var24[2]), op);
         int var28 = shadeColor(var13, var14, var15, brightness(var25[0], var25[1], var25[2]), op);
         int var29 = shadeColor(var13, var14, var15, brightness(var26[0], var26[1], var26[2]), op);
         double[] var30 = tiltPoint(ox + var18[0], oy + var17.v0y, oz + var18[1]);
         double[] var31 = tiltPoint(ox + var19[0], oy + var17.v1y, oz + var19[1]);
         double[] var32 = tiltPoint(ox + var20[0], oy + var17.v2y, oz + var20[1]);
         RenderUtil.drawTriangleDepthGouraud(
            var30[0],
            var30[1],
            var30[2],
            var27,
            var31[0],
            var31[1],
            var31[2],
            var28,
            var32[0],
            var32[1],
            var32[2],
            var29
         );
      }
   }

   private static double[] rot(double x, double z, double cs, double sn) {
      double[] var10000 = new double[2];
      var10000[0] = x * cs - z * sn;
      var10000[1] = x * sn + z * cs;
      return var10000;
   }

   private static float brightness(double nx, double ny, double nz) {
      float var6 = Math.max(0.0F, (float)(nx * LIGHT[0] + ny * LIGHT[1] + nz * LIGHT[2]));
      return 0.5F + 0.5F * var6 * var6 * (3.0F - 2.0F * var6);
   }

   private static int shadeColor(int r, int g, int b, float bri) {
      return -16777216 | (int)(r * bri) << (16) | (int)(g * bri) << (8) | (int)(b * bri);
   }

   private static int shadeColor(int r, int g, int b, float bri, float a) {
      int var5 = (int)(255.0F * Math.max(0.0F, Math.min(1.0F, a)));
      return var5 << (24) | (int)(r * bri) << (16) | (int)(g * bri) << (8) | (int)(b * bri);
   }

   private void drawStarDecal(double px, double baseY, double pz, double scale, double halfW, double height, double frontAz) {
      double var15 = Math.max(0.05, height);
      double var17 = halfW / var15;
      double var19 = baseY + 0.2 * scale;
      double var21 = 0.2 * scale;
      double var23 = var21 * 0.4;
      double var25 = Math.sqrt(1.0 + var17 * var17);
      double var27 = Math.max(0.05, halfW * (var15 - (var19 - baseY)) / var15);
      double var29 = px + Math.cos(frontAz) * var27 + Math.cos(frontAz) / var25 * 0.008;
      double var31 = pz + Math.sin(frontAz) * var27 + Math.sin(frontAz) / var25 * 0.008;
      double var33 = var19 + var17 / var25 * 0.008;

      for (int var35 = 0; var35 < (10); var35++) {
         int var36 = (var35 + 1) % (10);
         double var37 = var35 * Math.PI / 5.0 + (Math.PI / 2);
         double var39 = var36 * Math.PI / 5.0 + (Math.PI / 2);
         double var41 = var35 % (2) == 0 ? var21 : var23;
         double var43 = var36 % (2) == 0 ? var21 : var23;
         double[] var45 = onSurface(px, baseY, pz, frontAz, var19, halfW, var15, var17, Math.cos(var37) * var41, Math.sin(var37) * var41);
         double[] var46 = onSurface(px, baseY, pz, frontAz, var19, halfW, var15, var17, Math.cos(var39) * var43, Math.sin(var39) * var43);
         double[] var47 = tiltPoint(var29, var33, var31);
         double[] var48 = tiltPoint(var45[0], var45[1], var45[2]);
         double[] var49 = tiltPoint(var46[0], var46[1], var46[2]);
         RenderUtil.drawTriangleDepthGouraud(
            var47[0],
            var47[1],
            var47[2],
            -10496,
            var48[0],
            var48[1],
            var48[2],
            -10496,
            var49[0],
            var49[1],
            var49[2],
            -10496
         );
      }
   }

   private static double[] onSurface(double px, double baseY, double pz, double frontAz, double scy, double halfW, double H, double kk, double ox, double oy) {
      double var20 = scy + oy;
      double var22 = Math.max(0.02, halfW * (H - Math.max(0.0, var20 - baseY)) / H);
      double var24 = frontAz + ox / var22;
      double var26 = Math.sqrt(1.0 + kk * kk);
      double var28 = Math.cos(var24) / var26;
      double var30 = kk / var26;
      double var32 = Math.sin(var24) / var26;
      double[] var10000 = new double[3];
      var10000[0] = px + Math.cos(var24) * var22 + var28 * 0.008;
      var10000[1] = var20 + var30 * 0.008;
      var10000[2] = pz + Math.sin(var24) * var22 + var32 * 0.008;
      return var10000;
   }

   private void drawProcedural(double px, double baseY, double pz, double frontAz, int color, float scale, float op) {
      double var12 = 0.5 * scale;
      double var14 = 0.44 * scale;
      double var16 = baseY + var14;
      double var18 = frontAz;
      double var20 = var18 + Math.toRadians(45.0);
      int var22 = color >> 16 & 0xFF;
      int var23 = color >> 8 & 0xFF;
      int var24 = color & 0xFF;
      double[] var25 = new double[36];
      double[] var26 = new double[36];
      double[] var27 = new double[36];
      double[] var28 = new double[36];

      for (int var29 = 0; var29 < 36; var29++) {
         double var30 = var18 + var29 / 36.0 * Math.PI * 2.0;
         var25[var29] = px + Math.cos(var30) * var12;
         var26[var29] = pz + Math.sin(var30) * var12;
         var27[var29] = px + Math.cos(var30) * (var12 + 0.06);
         var28[var29] = pz + Math.sin(var30) * (var12 + 0.06);
      }

      int var38 = shadeColor(var22, var23, var24, 0.95F, op);

      for (int var39 = 0; var39 < 36; var39++) {
         int var31 = (var39 + 1) % 36;
         double[] var32 = tiltPoint(px, baseY, pz);
         double[] var33 = tiltPoint(var27[var39], baseY, var28[var39]);
         double[] var34 = tiltPoint(var27[var31], baseY, var28[var31]);
         RenderUtil.drawTriangleDepth(var32[0], var32[1], var32[2], var33[0], var33[1], var33[2], var34[0], var34[1], var34[2], var38);
      }

      for (int var40 = 0; var40 < 36; var40++) {
         int var41 = (var40 + 1) % 36;
         double var42 = var18 + (var40 + 0.5) / 36.0 * Math.PI * 2.0;
         float var43 = 0.5F + 0.5F * (float)Math.max(0.0, Math.cos(var42 - var20));
         var43 = 0.5F + 0.5F * var43 * (3.0F - 2.0F * var43);
         double[] var35 = tiltPoint(px, var16, pz);
         double[] var36 = tiltPoint(var25[var40], baseY, var26[var40]);
         double[] var37 = tiltPoint(var25[var41], baseY, var26[var41]);
         RenderUtil.drawTriangleDepth(
            var35[0], var35[1], var35[2], var36[0], var36[1], var36[2], var37[0], var37[1], var37[2], shadeColor(var22, var23, var24, var43, op)
         );
      }
   }

   private static float[] normalize(float x, float y, float z) {
      float var3 = (float)Math.sqrt(x * x + y * y + z * z);
      float[] var10000;
      if (var3 < 1.0E-6F) {
         var10000 = new float[3];
         var10000[0] = 0.0F;
         var10000[1] = 1.0F;
         var10000[2] = 0.0F;
      } else {
         var10000 = new float[3];
         var10000[0] = x / var3;
         var10000[1] = y / var3;
         var10000[2] = z / var3;
      }

      return var10000;
   }

   private static double lerp(float delta, double prev, double curr) {
      return prev + (curr - prev) * delta;
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

   private static final class Tri {
      final double v0x;
      final double v0y;
      final double v0z;
      final double v1x;
      final double v1y;
      final double v1z;
      final double v2x;
      final double v2y;
      final double v2z;
      final double n0x;
      final double n0y;
      final double n0z;
      final double n1x;
      final double n1y;
      final double n1z;
      final double n2x;
      final double n2y;
      final double n2z;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Tri(
         double v0x,
         double v0y,
         double v0z,
         double v1x,
         double v1y,
         double v1z,
         double v2x,
         double v2y,
         double v2z,
         double n0x,
         double n0y,
         double n0z,
         double n1x,
         double n1y,
         double n1z,
         double n2x,
         double n2y,
         double n2z
      ) {
         this.v0x = v0xx;
         this.v0y = v0yx;
         this.v0z = v0zx;
         this.v1x = v1xx;
         this.v1y = v1yx;
         this.v1z = v1zx;
         this.v2x = v2xx;
         this.v2y = v2yx;
         this.v2z = v2zx;
         this.n0x = n0xx;
         this.n0y = n0yx;
         this.n0z = n0zx;
         this.n1x = n1xx;
         this.n1y = n1yx;
         this.n1z = n1zx;
         this.n2x = n2xx;
         this.n2y = n2yx;
         this.n2z = n2zx;
      }

      double minX() {
         return Math.min(this.v0x, Math.min(this.v1x, this.v2x));
      }

      double maxX() {
         return Math.max(this.v0x, Math.max(this.v1x, this.v2x));
      }

      double minY() {
         return Math.min(this.v0y, Math.min(this.v1y, this.v2y));
      }

      double maxY() {
         return Math.max(this.v0y, Math.max(this.v1y, this.v2y));
      }

      double minZ() {
         return Math.min(this.v0z, Math.min(this.v1z, this.v2z));
      }

      double maxZ() {
         return Math.max(this.v0z, Math.max(this.v1z, this.v2z));
      }
   }
}
