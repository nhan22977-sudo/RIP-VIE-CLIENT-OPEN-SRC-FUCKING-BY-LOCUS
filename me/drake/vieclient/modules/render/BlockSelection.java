package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_265;
import net.minecraft.class_3965;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_239.class_240;

public class BlockSelection extends Module {
   private static final int[][] EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
   private static final int[][] FACES = new int[][]{{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 3, 7, 4}, {1, 2, 6, 5}};
   private static final double ANIM_MS = 320.0;
   private static final double EPS = 1.0E-4;
   private double[] displayBounds = null;
   private double[] targetBounds = null;
   private double[] fromBounds = null;
   private double[] toBounds = null;
   private long animStartMs = 0L;
   private boolean animating = false;
   private final EnumSetting effectStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "EffectStyle",
         "Visual effect style for the block selection.",
         BlockSelection.EffectStyle.CORNERS
      )
   );
   private final EnumSetting shapeMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "ShapeMode",
         "How the shapes are rendered.",
         BlockSelection.ShapeMode.Both,
         BlockSelection.ShapeMode.values()
      )
   );
   private final EnumSetting fillStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "FillStyle",
         "Lighting & animation mode for face fills.",
         BlockSelection.FillStyle.SOLID
      )
   );
   private final DoubleSetting cornerLength = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "CornerLength",
         "Length fraction of corner brackets (CORNERS mode)",
         0.32,
         0.15,
         0.5,
         0.01
      )
   );
   private final DoubleSetting lineWidth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "LineWidth",
         "Thickness of selection outline and bracket edges",
         1.8,
         0.5,
         4.5,
         0.1
      )
   );
   private final BoolSetting neonBloom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "NeonBloom",
         "Renders a soft glowing outer halo for high-end aesthetics",
         true
      )
   );
   private final DoubleSetting animSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "AnimSpeed",
         "Speed of dynamic pulse, scanline, and rainbow animations",
         1.8,
         0.5,
         5.0,
         0.1
      )
   );
   private final ColorSetting sideColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "SideColor",
         "The side color of the block selection.",
         855638015
      )
   );
   private final ColorSetting lineColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "LineColor",
         "The line color of the block selection.",
         -10177034
      )
   );
   private final BoolSetting advanced = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Advanced",
         "Shows a more advanced outline on different types of shape blocks.",
         true
      )
   );
   private final BoolSetting oneSide = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SingleSide",
         "Only renders the side you are looking at.",
         false
      )
   );
   private final BoolSetting hideInside = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HideInside",
         "Hides the block selection when you are inside the target block.",
         true
      )
   );
   private final WorldProjector projector = new WorldProjector();
   private final float[] scratchPt = new float[3];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlockSelection() {
      super(
         "BlockSelection",
         "Modifies how your block selection is rendered with futuristic effects.",
         Category.RENDER
      );
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.displayBounds = null;
      this.targetBounds = null;
      this.fromBounds = null;
      this.toBounds = null;
      this.animating = false;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (!(mc.field_1765 != null && mc.field_1765 instanceof class_3965 var2)) {
         this.displayBounds = null;
         this.targetBounds = null;
      } else if (var2.method_17783() == class_240.field_1333) {
         this.displayBounds = null;
         this.targetBounds = null;
      } else if (mc.field_1687 != null) {
         if (!(Boolean)this.hideInside.get() || !var2.method_17781()) {
            class_2338 var30 = var2.method_17777();
            class_2350 var4 = var2.method_17780();
            class_265 var5 = mc.field_1687.method_8320(var30).method_26218(mc.field_1687, var30);
            if (!var5.method_1110()) {
               double var6 = var30.method_10263() + var5.method_1091(class_2351.field_11048);
               double var8 = var30.method_10264() + var5.method_1091(class_2351.field_11052);
               double var10 = var30.method_10260() + var5.method_1091(class_2351.field_11051);
               double var12 = var30.method_10263() + var5.method_1105(class_2351.field_11048);
               double var14 = var30.method_10264() + var5.method_1105(class_2351.field_11052);
               double var16 = var30.method_10260() + var5.method_1105(class_2351.field_11051);
               int var18 = (Integer)this.sideColor.get();
               int var19 = (Integer)this.lineColor.get();
               BlockSelection.ShapeMode var20 = (BlockSelection.ShapeMode)this.shapeMode.get();
               if (this.projector.snapshot()) {
                  try (NanoVGFrame var21 = NanoVGRenderer.frame()) {
                     if (!var21.isActive()) {
                        return;
                     }

                     double[] var22 = new double[]{var6, var8, var10, var12, var14, var16};
                     this.animGlide(var22);
                     if ((Boolean)this.oneSide.get()) {
                        double[] var23 = this.displayBounds;

                        double[] var24 = switch (<unrepresentable>.$SwitchMap$net$minecraft$util$math$Direction[var4.ordinal()]) {
                           case 1, 2 -> {
                              double var36 = var4 == class_2350.field_11033 ? var23[1] : var23[4];
                              yield new double[]{var23[0], var36, var23[2], var23[3], var36, var23[5]};
                           }
                           case 3, 4 -> {
                              double var35 = var4 == class_2350.field_11043 ? var23[2] : var23[5];
                              yield new double[]{var23[0], var23[1], var35, var23[3], var23[4], var35};
                           }
                           case 5, 6 -> {
                              double var25 = var4 == class_2350.field_11039 ? var23[0] : var23[3];
                              yield new double[]{var25, var23[1], var23[2], var25, var23[4], var23[5]};
                           }
                           default -> new double[]{var23[0], var23[1], var23[2], var23[3], var23[4], var23[5]};
                        };
                        this.drawBox(var24[0], var24[1], var24[2], var24[3], var24[4], var24[5], var18, var19, var20);
                     } else if ((Boolean)this.advanced.get()) {
                        ArrayList var31 = new ArrayList();
                        var5.method_1089((x1, y1, z1, x2, y2, z2) -> {
                           double[][] var10001 = new double[2][];
                           double[] var10004 = new double[3];
                           var10004[0] = var30.method_10263() + x1;
                           var10004[1] = var30.method_10264() + y1;
                           var10004[2] = var30.method_10260() + z1;
                           var10001[0] = var10004;
                           var10004 = new double[3];
                           var10004[0] = var30.method_10263() + x2;
                           var10004[1] = var30.method_10264() + y2;
                           var10004[2] = var30.method_10260() + z2;
                           var10001[1] = var10004;
                           var31.add(var10001);
                        });

                        for (double[][] var37 : var31) {
                           double[] var26 = this.mapGlide(var37[0][0], var37[0][1], var37[0][2]);
                           double[] var27 = this.mapGlide(var37[1][0], var37[1][1], var37[1][2]);
                           this.drawBox(var26[0], var26[1], var26[2], var27[0], var27[1], var27[2], var18, var19, BlockSelection.ShapeMode.Sides);
                        }

                        if (var20 == BlockSelection.ShapeMode.Both || var20 == BlockSelection.ShapeMode.Lines) {
                           ArrayList var34 = new ArrayList();
                           var5.method_1104((x1, y1, z1, x2, y2, z2) -> {
                              double[][] var10001 = new double[2][];
                              double[] var10004 = new double[3];
                              var10004[0] = var30.method_10263() + x1;
                              var10004[1] = var30.method_10264() + y1;
                              var10004[2] = var30.method_10260() + z1;
                              var10001[0] = var10004;
                              var10004 = new double[3];
                              var10004[0] = var30.method_10263() + x2;
                              var10004[1] = var30.method_10264() + y2;
                              var10004[2] = var30.method_10260() + z2;
                              var10001[1] = var10004;
                              var34.add(var10001);
                           });
                           this.drawEdgeLines(var34, var19);
                        }
                     } else {
                        double[] var32 = this.displayBounds;
                        this.drawBox(var32[0], var32[1], var32[2], var32[3], var32[4], var32[5], var18, var19, var20);
                     }
                  }
               }
            }
         }
      }
   }

   private void animGlide(double[] target) {
      if (this.targetBounds == null) {
         this.targetBounds = (double[])target.clone();
         this.displayBounds = (double[])target.clone();
      } else {
         boolean var2 = Math.abs(this.targetBounds[0] - target[0]) < 1.0E-4
            && Math.abs(this.targetBounds[1] - target[1]) < 1.0E-4
            && Math.abs(this.targetBounds[2] - target[2]) < 1.0E-4
            && Math.abs(this.targetBounds[3] - target[3]) < 1.0E-4
            && Math.abs(this.targetBounds[4] - target[4]) < 1.0E-4
            && Math.abs(this.targetBounds[5] - target[5]) < 1.0E-4;
         if (!var2) {
            this.targetBounds = (double[])target.clone();
            this.fromBounds = (double[])this.displayBounds.clone();
            this.toBounds = (double[])target.clone();
            this.animStartMs = System.currentTimeMillis();
            this.animating = true;
         } else {
            if (this.animating) {
               double var3 = Math.min(1.0, (System.currentTimeMillis() - this.animStartMs) / 320.0);
               double var5 = easeOutCubic(var3);

               for (int var7 = 0; var7 < (6); var7++) {
                  this.displayBounds[var7] = this.fromBounds[var7] + (this.targetBounds[var7] - this.fromBounds[var7]) * var5;
               }

               if (var3 >= 1.0) {
                  this.displayBounds = (double[])this.targetBounds.clone();
                  this.animating = false;
               }
            }
         }
      }
   }

   private double[] mapGlide(double x, double y, double z) {
      double[] var7 = this.targetBounds;
      double[] var8 = this.displayBounds;
      if (var7 != null && var8 != null) {
         if (var7[0] != var7[3] && var7[1] != var7[4] && var7[2] != var7[5]) {
            double var9 = (var8[3] - var8[0]) / (var7[3] - var7[0]);
            double var11 = (var8[4] - var8[1]) / (var7[4] - var7[1]);
            double var13 = (var8[5] - var8[2]) / (var7[5] - var7[2]);
            double[] var16 = new double[3];
            var16[0] = var8[0] + (x - var7[0]) * var9;
            var16[1] = var8[1] + (y - var7[1]) * var11;
            var16[2] = var8[2] + (z - var7[2]) * var13;
            return var16;
         } else {
            double[] var15 = new double[3];
            var15[0] = x;
            var15[1] = y;
            var15[2] = z;
            return var15;
         }
      } else {
         double[] var10000 = new double[3];
         var10000[0] = x;
         var10000[1] = y;
         var10000[2] = z;
         return var10000;
      }
   }

   private static double easeOutCubic(double t) {
      double var2 = 1.0 - t;
      return 1.0 - var2 * var2 * var2;
   }

   private void drawBox(double x1, double y1, double z1, double x2, double y2, double z2, int sideC, int lineC, BlockSelection.ShapeMode mode) {
      double[][] var16 = this.projectBox(x1, y1, z1, x2, y2, z2);
      if (var16 != null) {
         BlockSelection.EffectStyle var17 = (BlockSelection.EffectStyle)this.effectStyle.get();
         double var18 = System.currentTimeMillis() / 1000.0 * (Double)this.animSpeed.get();
         if (this.fillStyle.get() != BlockSelection.FillStyle.OFF && (mode == BlockSelection.ShapeMode.Both || mode == BlockSelection.ShapeMode.Sides)) {
            Color var20 = this.computeFillColor(new Color(sideC, true), var18);
            NanoVGRenderer.drawEspFill(var16, FACES, 1.0, var20);
         }

         if (var17 == BlockSelection.EffectStyle.SCANLINE) {
            this.drawScanline(x1, y1, z1, x2, y2, z2, new Color(lineC, true), var18);
         }

         if (var17 == BlockSelection.EffectStyle.GRID) {
            this.drawFaceGrid(x1, y1, z1, x2, y2, z2, new Color(lineC, true));
         }

         if (mode == BlockSelection.ShapeMode.Both || mode == BlockSelection.ShapeMode.Lines) {
            float var22 = (float)((Double)this.lineWidth.get()).doubleValue();
            Color var21 = new Color(lineC, true);
            if (var17 == BlockSelection.EffectStyle.CORNERS) {
               this.drawCornersBox(x1, y1, z1, x2, y2, z2, var21, var22, var18);
            } else {
               this.drawStandardEdges(var16, var21, var22, var17, var18);
            }
         }
      }
   }

   private Color computeFillColor(Color base, double time) {
      if (this.fillStyle.get() == BlockSelection.FillStyle.PULSE) {
         float var4 = (float)(0.45 + 0.55 * Math.sin(time * 2.5));
         int var5 = Math.max(0, Math.min(255, (int)(base.getAlpha() * var4)));
         return new Color(base.getRed(), base.getGreen(), base.getBlue(), var5);
      } else {
         return base;
      }
   }

   private void drawStandardEdges(double[][] proj, Color baseColor, float baseW, BlockSelection.EffectStyle effect, double time) {
      float var7 = baseW;
      Color var8 = baseColor;
      if (effect == BlockSelection.EffectStyle.PULSE) {
         float var9 = (float)(0.55 + 0.45 * Math.sin(time * 3.0));
         var7 = baseW * (0.85F + 0.35F * var9);
         int var10 = Math.max(20, Math.min(255, (int)(baseColor.getAlpha() * var9)));
         var8 = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), var10);
      } else if (effect == BlockSelection.EffectStyle.NEON) {
         var7 = baseW * 1.15F;
      }

      boolean var17 = (Boolean)this.neonBloom.get() || effect == BlockSelection.EffectStyle.NEON;
      boolean var18 = effect == BlockSelection.EffectStyle.RAINBOW;
      if (var17) {
         float var11 = var7 * 2.6F;

         for (int var12 = 0; var12 < EDGES.length; var12++) {
            int[] var13 = EDGES[var12];
            double[] var14 = proj[var13[0]];
            double[] var15 = proj[var13[1]];
            if (var14 != null && var15 != null) {
               Color var16 = var18 ? this.getRainbowColor(var12, time, 75) : withAlpha(var8, (int)(var8.getAlpha() * 0.35F));
               NanoVGRenderer.beginLineBatch(var16, var11);
               NanoVGRenderer.batchLine((float)var14[0], (float)var14[1], (float)var15[0], (float)var15[1]);
               NanoVGRenderer.endLineBatch();
            }
         }
      }

      for (int var19 = 0; var19 < EDGES.length; var19++) {
         int[] var20 = EDGES[var19];
         double[] var21 = proj[var20[0]];
         double[] var22 = proj[var20[1]];
         if (var21 != null && var22 != null) {
            Color var23 = var18 ? this.getRainbowColor(var19, time, 255) : var8;
            NanoVGRenderer.beginLineBatch(var23, var7);
            NanoVGRenderer.batchLine((float)var21[0], (float)var21[1], (float)var22[0], (float)var22[1]);
            NanoVGRenderer.endLineBatch();
         }
      }
   }

   private void drawCornersBox(double x1, double y1, double z1, double x2, double y2, double z2, Color baseColor, float baseW, double time) {
      double[][] var17 = new double[][]{{x1, y1, z1}, {x2, y1, z1}, {x2, y1, z2}, {x1, y1, z2}, {x1, y2, z1}, {x2, y2, z1}, {x2, y2, z2}, {x1, y2, z2}};
      float var18 = (float)((Double)this.cornerLength.get()).doubleValue();
      boolean var19 = (Boolean)this.neonBloom.get();

      for (int var20 = 0; var20 < EDGES.length; var20++) {
         int var21 = EDGES[var20][0];
         int var22 = EDGES[var20][1];
         double[] var23 = var17[var21];
         double[] var24 = var17[var22];
         double var25 = var24[0] - var23[0];
         double var27 = var24[1] - var23[1];
         double var29 = var24[2] - var23[2];
         double[] var31 = this.worldToScreen(var23[0], var23[1], var23[2]);
         double[] var32 = this.worldToScreen(var23[0] + var25 * var18, var23[1] + var27 * var18, var23[2] + var29 * var18);
         double[] var33 = this.worldToScreen(var24[0], var24[1], var24[2]);
         double[] var34 = this.worldToScreen(var24[0] - var25 * var18, var24[1] - var27 * var18, var24[2] - var29 * var18);
         if (var31 != null && var32 != null && var33 != null && var34 != null) {
            float var35 = (float)var31[0];
            float var36 = (float)var31[1];
            float var37 = (float)var32[0];
            float var38 = (float)var32[1];
            float var39 = (float)var33[0];
            float var40 = (float)var33[1];
            float var41 = (float)var34[0];
            float var42 = (float)var34[1];
            if (var19) {
               Color var43 = withAlpha(baseColor, (int)(baseColor.getAlpha() * 0.35F));
               NanoVGRenderer.beginLineBatch(var43, baseW * 2.6F);
               NanoVGRenderer.batchLine(var35, var36, var37, var38);
               NanoVGRenderer.batchLine(var39, var40, var41, var42);
               NanoVGRenderer.endLineBatch();
            }

            NanoVGRenderer.beginLineBatch(baseColor, baseW);
            NanoVGRenderer.batchLine(var35, var36, var37, var38);
            NanoVGRenderer.batchLine(var39, var40, var41, var42);
            NanoVGRenderer.endLineBatch();
         }
      }
   }

   private void drawScanline(double x1, double y1, double z1, double x2, double y2, double z2, Color baseColor, double time) {
      double var16 = 0.5 + 0.5 * Math.sin(time * 2.4);
      double var18 = y1 + (y2 - y1) * var16;
      double[] var20 = this.worldToScreen(x1, var18, z1);
      double[] var21 = this.worldToScreen(x2, var18, z1);
      double[] var22 = this.worldToScreen(x2, var18, z2);
      double[] var23 = this.worldToScreen(x1, var18, z2);
      if (var20 != null && var21 != null && var22 != null && var23 != null) {
         float var24 = (float)var20[0];
         float var25 = (float)var20[1];
         float var26 = (float)var21[0];
         float var27 = (float)var21[1];
         float var28 = (float)var22[0];
         float var29 = (float)var22[1];
         float var30 = (float)var23[0];
         float var31 = (float)var23[1];
         Color var32 = withAlpha(baseColor, 40);
         NanoVGRenderer.beginFillBatch(var32);
         NanoVGRenderer.batchTri(var24, var25, var26, var27, var28, var29);
         NanoVGRenderer.batchTri(var24, var25, var28, var29, var30, var31);
         NanoVGRenderer.endFillBatch();
         NanoVGRenderer.beginLineBatch(baseColor, 1.2F);
         NanoVGRenderer.batchLine(var24, var25, var26, var27);
         NanoVGRenderer.batchLine(var26, var27, var28, var29);
         NanoVGRenderer.batchLine(var28, var29, var30, var31);
         NanoVGRenderer.batchLine(var30, var31, var24, var25);
         NanoVGRenderer.endLineBatch();
      }
   }

   private void drawFaceGrid(double x1, double y1, double z1, double x2, double y2, double z2, Color baseColor) {
      Color var14 = withAlpha(baseColor, 60);
      NanoVGRenderer.beginLineBatch(var14, 1.0F);
      int var15 = 3;

      for (int var16 = 1; var16 < var15; var16++) {
         double var17 = x1 + (x2 - x1) * ((double)var16 / var15);
         double var19 = y1 + (y2 - y1) * ((double)var16 / var15);
         double var21 = z1 + (z2 - z1) * ((double)var16 / var15);
         this.drawLine3D(var17, y1, z1, var17, y1, z2);
         this.drawLine3D(var17, y2, z1, var17, y2, z2);
         this.drawLine3D(x1, var19, z1, x2, var19, z1);
         this.drawLine3D(x1, var19, z2, x2, var19, z2);
         this.drawLine3D(x1, y1, var21, x1, y2, var21);
         this.drawLine3D(x2, y1, var21, x2, y2, var21);
      }

      NanoVGRenderer.endLineBatch();
   }

   private void drawLine3D(double x1, double y1, double z1, double x2, double y2, double z2) {
      double[] var13 = this.worldToScreen(x1, y1, z1);
      double[] var14 = this.worldToScreen(x2, y2, z2);
      if (var13 != null && var14 != null) {
         NanoVGRenderer.batchLine((float)var13[0], (float)var13[1], (float)var14[0], (float)var14[1]);
      }
   }

   private void drawEdgeLines(List edges, int lineC) {
      Color var3 = new Color(lineC, true);
      float var4 = (float)((Double)this.lineWidth.get()).doubleValue();
      BlockSelection.EffectStyle var5 = (BlockSelection.EffectStyle)this.effectStyle.get();
      double var6 = System.currentTimeMillis() / 1000.0 * (Double)this.animSpeed.get();
      if (var5 == BlockSelection.EffectStyle.CORNERS) {
         float var32 = (float)((Double)this.cornerLength.get()).doubleValue();

         for (double[][] var34 : edges) {
            double[] var35 = this.mapGlide(var34[0][0], var34[0][1], var34[0][2]);
            double[] var36 = this.mapGlide(var34[1][0], var34[1][1], var34[1][2]);
            double var37 = var36[0] - var35[0];
            double var15 = var36[1] - var35[1];
            double var17 = var36[2] - var35[2];
            double[] var19 = this.worldToScreen(var35[0], var35[1], var35[2]);
            double[] var20 = this.worldToScreen(var35[0] + var37 * var32, var35[1] + var15 * var32, var35[2] + var17 * var32);
            double[] var21 = this.worldToScreen(var36[0], var36[1], var36[2]);
            double[] var22 = this.worldToScreen(var36[0] - var37 * var32, var36[1] - var15 * var32, var36[2] - var17 * var32);
            if (var19 != null && var20 != null && var21 != null && var22 != null) {
               float var23 = (float)var19[0];
               float var24 = (float)var19[1];
               float var25 = (float)var20[0];
               float var26 = (float)var20[1];
               float var27 = (float)var21[0];
               float var28 = (float)var21[1];
               float var29 = (float)var22[0];
               float var30 = (float)var22[1];
               if ((Boolean)this.neonBloom.get()) {
                  Color var31 = withAlpha(var3, (int)(var3.getAlpha() * 0.35F));
                  NanoVGRenderer.beginLineBatch(var31, var4 * 2.6F);
                  NanoVGRenderer.batchLine(var23, var24, var25, var26);
                  NanoVGRenderer.batchLine(var27, var28, var29, var30);
                  NanoVGRenderer.endLineBatch();
               }

               NanoVGRenderer.beginLineBatch(var3, var4);
               NanoVGRenderer.batchLine(var23, var24, var25, var26);
               NanoVGRenderer.batchLine(var27, var28, var29, var30);
               NanoVGRenderer.endLineBatch();
            }
         }
      } else {
         for (int var8 = 0; var8 < edges.size(); var8++) {
            double[][] var9 = (double[][])edges.get(var8);
            double[] var10 = this.mapGlide(var9[0][0], var9[0][1], var9[0][2]);
            double[] var11 = this.mapGlide(var9[1][0], var9[1][1], var9[1][2]);
            double[] var12 = this.worldToScreen(var10[0], var10[1], var10[2]);
            double[] var13 = this.worldToScreen(var11[0], var11[1], var11[2]);
            if (var12 != null && var13 != null) {
               Color var14 = var5 == BlockSelection.EffectStyle.RAINBOW ? this.getRainbowColor(var8, var6, 255) : var3;
               if ((Boolean)this.neonBloom.get()) {
                  NanoVGRenderer.beginLineBatch(withAlpha(var14, (int)(var14.getAlpha() * 0.35F)), var4 * 2.6F);
                  NanoVGRenderer.batchLine((float)var12[0], (float)var12[1], (float)var13[0], (float)var13[1]);
                  NanoVGRenderer.endLineBatch();
               }

               NanoVGRenderer.beginLineBatch(var14, var4);
               NanoVGRenderer.batchLine((float)var12[0], (float)var12[1], (float)var13[0], (float)var13[1]);
               NanoVGRenderer.endLineBatch();
            }
         }
      }
   }

   private Color getRainbowColor(int index, double time, int alpha) {
      float var5 = (float)(time * 0.25 + index * 0.08) % 1.0F;
      int var6 = Color.HSBtoRGB(var5, 0.85F, 1.0F);
      return new Color(
         var6 >> (16) & (255), var6 >> (8) & (255), var6 & (255), alpha
      );
   }

   private static Color withAlpha(Color c, int a) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, a)));
   }

   private double[][] projectBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      double[][] var13 = new double[8][];
      var13[0] = this.worldToScreen(minX, minY, minZ);
      var13[1] = this.worldToScreen(maxX, minY, minZ);
      var13[2] = this.worldToScreen(maxX, minY, maxZ);
      var13[3] = this.worldToScreen(minX, minY, maxZ);
      var13[4] = this.worldToScreen(minX, maxY, minZ);
      var13[5] = this.worldToScreen(maxX, maxY, minZ);
      var13[6] = this.worldToScreen(maxX, maxY, maxZ);
      var13[7] = this.worldToScreen(minX, maxY, maxZ);
      boolean var14 = false;

      for (double[] var18 : var13) {
         if (var18 != null) {
            var14 = true;
            break;
         }
      }

      return var14 ? var13 : null;
   }

   private double[] worldToScreen(double wx, double wy, double wz) {
      if (!this.projector.isValid()) {
         return null;
      } else if (this.projector.project(wx, wy, wz, this.scratchPt)) {
         double[] var10000 = new double[2];
         var10000[0] = this.scratchPt[0];
         var10000[1] = this.scratchPt[1];
         return var10000;
      } else {
         return null;
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

   public enum EffectStyle {
      DEFAULT,
      CORNERS,
      PULSE,
      RAINBOW,
      GRID,
      SCANLINE,
      NEON;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BlockSelection.EffectStyle[] $values() {
         BlockSelection.EffectStyle[] var10000 = new BlockSelection.EffectStyle[7];
         var10000[0] = DEFAULT;
         var10000[1] = CORNERS;
         var10000[2] = PULSE;
         var10000[3] = RAINBOW;
         var10000[4] = GRID;
         var10000[5] = SCANLINE;
         var10000[6] = NEON;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(120);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(49);
         var10002[5] = (byte)(-72);
         var10002[6] = (byte)(-67);
         DEFAULT = new BlockSelection.EffectStyle();
         var10002 = new byte[7];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(33);
         var10002[5] = (byte)(-90);
         var10002[6] = (byte)(-70);
         CORNERS = new BlockSelection.EffectStyle();
         var10002 = new byte[5];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(33);
         PULSE = new BlockSelection.EffectStyle();
         var10002 = new byte[7];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(38);
         var10002[5] = (byte)(-69);
         var10002[6] = (byte)(-66);
         RAINBOW = new BlockSelection.EffectStyle();
         var10002 = new byte[4];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-102);
         GRID = new BlockSelection.EffectStyle();
         var10002 = new byte[8];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(40);
         var10002[5] = (byte)(-67);
         var10002[6] = (byte)(-89);
         var10002[7] = (byte)(57);
         SCANLINE = new BlockSelection.EffectStyle();
         var10002 = new byte[4];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-112);
         NEON = new BlockSelection.EffectStyle();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   public enum FillStyle {
      SOLID,
      PULSE,
      OFF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BlockSelection.FillStyle[] $values() {
         BlockSelection.FillStyle[] var10000 = new BlockSelection.FillStyle[3];
         var10000[0] = SOLID;
         var10000[1] = PULSE;
         var10000[2] = OFF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-82);
         SOLID = new BlockSelection.FillStyle();
         var10002 = new byte[5];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-81);
         PULSE = new BlockSelection.FillStyle();
         var10002 = new byte[3];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new BlockSelection.FillStyle();
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

   public enum ShapeMode {
      Lines,
      Sides,
      Both;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BlockSelection.ShapeMode[] $values() {
         BlockSelection.ShapeMode[] var10000 = new BlockSelection.ShapeMode[3];
         var10000[0] = Lines;
         var10000[1] = Sides;
         var10000[2] = Both;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-13);
         var10002[2] = (byte)(114);
         var10002[3] = (byte)(-69);
         var10002[4] = (byte)(-109);
         Lines = new BlockSelection.ShapeMode();
         var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-13);
         var10002[2] = (byte)(120);
         var10002[3] = (byte)(-69);
         var10002[4] = (byte)(-109);
         Sides = new BlockSelection.ShapeMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(104);
         var10002[3] = (byte)(-74);
         Both = new BlockSelection.ShapeMode();
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
}
