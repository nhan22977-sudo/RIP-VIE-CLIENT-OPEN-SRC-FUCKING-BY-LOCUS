package me.drake.vieclient.modules.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.mixin.MixinClientPlayerInteractionManagerAccessor;
import me.drake.vieclient.mixin.WorldRendererAccessor;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NVGTextRenderer;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_3191;
import net.minecraft.class_2350.class_2351;

public class BreakingProgress extends Module {
   public static BreakingProgress INSTANCE;
   private static final int[][] EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
   private static final int[][] FACES = new int[][]{{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 3, 7, 4}, {1, 2, 6, 5}};
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "Animation visual style for breaking progress.",
         BreakingProgress.ProgressMode.RISING,
         BreakingProgress.ProgressMode.values()
      )
   );
   private final EnumSetting colorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "ColorMode",
         "Color theme and gradient transition mode.",
         BreakingProgress.ColorMode.DYNAMIC,
         BreakingProgress.ColorMode.values()
      )
   );
   private final BoolSetting fill = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Fill",
         "Renders 3D transparent volume fill.",
         true
      )
   );
   private final DoubleSetting fillAlpha = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "FillAlpha",
         "Opacity of the inner volume fill.",
         0.2,
         0.05,
         1.0,
         0.05
      )
   );
   private final BoolSetting outline = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Outline",
         "Renders glowing wireframe edges.",
         true
      )
   );
   private final DoubleSetting lineWidth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "LineWidth",
         "Thickness of wireframe edges.",
         1.8,
         0.5,
         5.0,
         0.1
      )
   );
   private final BoolSetting neonBloom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "NeonBloom",
         "Multi-pass outer glowing neon bloom halo.",
         true
      )
   );
   private final EnumSetting textStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "TextStyle",
         "Format of the floating 3D percentage badge.",
         BreakingProgress.TextStyle.BADGE,
         BreakingProgress.TextStyle.values()
      )
   );
   private final DoubleSetting textScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "TextScale",
         "Scale of the floating percentage text.",
         1.0,
         0.5,
         2.0,
         0.1
      )
   );
   private final BoolSetting showOthers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ShowOthers",
         "Renders breaking progress for nearby players mining blocks.",
         true
      )
   );
   private final BoolSetting hideVanillaCracks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HideVanillaCracks",
         "Hides default vanilla black crack textures.",
         true
      )
   );
   private final BoolSetting smoothAnimation = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SmoothAnimation",
         "Smooth lerp interpolation between break ticks.",
         true
      )
   );
   private final BoolSetting breakFlash = (BoolSetting)this.addSetting(
      new BoolSetting(
         "BreakFlash",
         "Glowing energy flash burst when the block breaks.",
         true
      )
   );
   private final ColorSetting startColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "StartColor",
         "Starting color at 0% progress (Custom mode).",
         -2147424650
      )
   );
   private final ColorSetting endColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "EndColor",
         "Ending color at 100% progress (Custom mode).",
         -855697596
      )
   );
   private final WorldProjector projector = new WorldProjector();
   private final float[] scratchPt = new float[3];
   private final Map activeTracks = new ConcurrentHashMap();
   private long lastFrameMs = System.currentTimeMillis();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BreakingProgress() {
      super(
         "BreakingProgress",
         "Customizes block breaking progress overlay with futuristic animations and aesthetic visuals.",
         Category.RENDER
      );
      INSTANCE = this;
   }

   public BoolSetting getHideVanillaCracks() {
      return this.hideVanillaCracks;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.activeTracks.clear();
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         long var2 = System.currentTimeMillis();
         float var4 = Math.max(0.001F, Math.min(0.1F, (float)(var2 - this.lastFrameMs) / 1000.0F));
         this.lastFrameMs = var2;
         this.updateLocalBreaking(var2);
         if ((Boolean)this.showOthers.get()) {
            this.updateOtherBreaking(var2);
         }

         this.pruneTracks(var2, var4);
         if (!this.activeTracks.isEmpty()) {
            if (this.projector.snapshot()) {
               try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
                  if (!var5.isActive()) {
                     return;
                  }

                  for (BreakingProgress.BreakTrack var7 : this.activeTracks.values()) {
                     this.renderBreakTrack(var7, var2, var4);
                  }
               }
            }
         }
      } else {
         this.activeTracks.clear();
      }
   }

   private void updateLocalBreaking(long now) {
      if (mc.field_1761 != null) {
         MixinClientPlayerInteractionManagerAccessor var3 = (MixinClientPlayerInteractionManagerAccessor)mc.field_1761;
         boolean var4 = var3.getBreakingBlock();
         class_2338 var5 = var3.getCurrentBreakingPos();
         float var6 = var3.getCurrentBreakingProgress();
         if (var4 && var5 != null && var6 > 0.0F) {
            BreakingProgress.BreakTrack var7 = this.activeTracks.computeIfAbsent(var5, p -> new BreakingProgress.BreakTrack(p, var6, null, true));
            var7.targetProgress = Math.min(1.0F, var6);
            var7.lastSeenMs = now;
            var7.isLocal = true;
         }
      }
   }

   private void updateOtherBreaking(long now) {
      if (mc.field_1769 != null) {
         try {
            Int2ObjectMap var3 = ((WorldRendererAccessor)mc.field_1769).getBlockBreakingInfos();
            if (var3 == null || var3.isEmpty()) {
               return;
            }

            int var4 = mc.field_1724 != null ? mc.field_1724.method_5628() : -1;
            ObjectIterator var5 = var3.values().iterator();

            while (var5.hasNext()) {
               class_3191 var6 = (class_3191)var5.next();
               if (var6 != null && var6.method_34868() != var4) {
                  class_2338 var7 = var6.method_13991();
                  if (var7 != null) {
                     int var8 = var6.method_13988();
                     if (var8 >= 0 && var8 <= (9)) {
                        float var9 = (var8 + 1) / 10.0F;
                        class_1297 var10 = mc.field_1687.method_8469(var6.method_34868());
                        String var11 = var10 != null ? var10.method_5477().getString() : null;
                        BreakingProgress.BreakTrack var12 = this.activeTracks
                           .computeIfAbsent(var7, p -> new BreakingProgress.BreakTrack(p, var9, var11, false));
                        var12.targetProgress = var9;
                        var12.lastSeenMs = now;
                        if (var11 != null) {
                           var12.minerName = var11;
                        }
                     }
                  }
               }
            }
         } catch (Throwable var13) {
         }
      }
   }

   private void pruneTracks(long now, float dt) {
      Iterator var4 = this.activeTracks.entrySet().iterator();

      while (var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         class_2338 var6 = (class_2338)var5.getKey();
         BreakingProgress.BreakTrack var7 = (BreakingProgress.BreakTrack)var5.getValue();
         boolean var8 = mc.field_1687 == null || mc.field_1687.method_22347(var6);
         if ((Boolean)this.breakFlash.get() && (var8 || var7.targetProgress >= 0.99F) && !var7.flashTriggered) {
            var7.flashTriggered = true;
            var7.flashTimer = 1.0F;
         }

         if ((Boolean)this.smoothAnimation.get()) {
            var7.currentProgress = var7.currentProgress + (var7.targetProgress - var7.currentProgress) * Math.min(1.0F, dt * 16.0F);
         } else {
            var7.currentProgress = var7.targetProgress;
         }

         if (var7.flashTimer > 0.0F) {
            var7.flashTimer -= dt * 3.2F;
            if (var7.flashTimer <= 0.0F) {
               var4.remove();
               continue;
            }
         }

         if ((now - var7.lastSeenMs > 750L && !this.isBreakingPos(var6) || var8) && var7.flashTimer <= 0.0F) {
            var4.remove();
         }
      }
   }

   private boolean isBreakingPos(class_2338 pos) {
      if (mc.field_1761 == null) {
         return false;
      }

      MixinClientPlayerInteractionManagerAccessor var2 = (MixinClientPlayerInteractionManagerAccessor)mc.field_1761;
      return var2.getBreakingBlock() && pos.equals(var2.getCurrentBreakingPos());
   }

   private void renderBreakTrack(BreakingProgress.BreakTrack track, long now, float dt) {
      class_2338 var5 = track.pos;
      class_265 var6 = mc.field_1687.method_8320(var5).method_26218(mc.field_1687, var5);
      double var7 = var5.method_10263();
      double var9 = var5.method_10264();
      double var11 = var5.method_10260();
      double var13 = var5.method_10263() + 1.0;
      double var15 = var5.method_10264() + 1.0;
      double var17 = var5.method_10260() + 1.0;
      if (!var6.method_1110()) {
         var7 = var5.method_10263() + var6.method_1091(class_2351.field_11048);
         var9 = var5.method_10264() + var6.method_1091(class_2351.field_11052);
         var11 = var5.method_10260() + var6.method_1091(class_2351.field_11051);
         var13 = var5.method_10263() + var6.method_1105(class_2351.field_11048);
         var15 = var5.method_10264() + var6.method_1105(class_2351.field_11052);
         var17 = var5.method_10260() + var6.method_1105(class_2351.field_11051);
      }

      float var19 = Math.max(0.01F, Math.min(1.0F, track.currentProgress));
      Color var20 = this.computeProgressColor(var19, now);
      if (track.flashTimer > 0.0F) {
         this.renderBreakFlash(var7, var9, var11, var13, var15, var17, var20, track.flashTimer);
      } else {
         BreakingProgress.ProgressMode var21 = (BreakingProgress.ProgressMode)this.mode.get();
         switch (var21) {
            case RISING:
               this.renderRising(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case GROW:
               this.renderGrow(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case SHRINK:
               this.renderShrink(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case RADIAL_DIAL:
               this.renderRadialDial(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case CORNER_LOCK:
               this.renderCornerLock(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case NEON_WIREFRAME:
               this.renderNeonWireframe(var7, var9, var11, var13, var15, var17, var19, var20);
               break;
            case SCANLINE:
               this.renderScanline(var7, var9, var11, var13, var15, var17, var19, var20, now);
         }

         if (this.textStyle.get() != BreakingProgress.TextStyle.NONE) {
            this.renderFloatingText(var5, var7, var9, var11, var13, var15, var17, var19, var20, track.minerName);
         }
      }
   }

   private void renderRising(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, this.withAlpha(color, 25), false, true, 1.0F);
      double var15 = y1 + (y2 - y1) * p;
      this.drawVolume(x1, y1, z1, x2, var15, z2, color, (Boolean)this.fill.get(), (Boolean)this.outline.get());
      double[][] var10000 = new double[4][];
      var10000[0] = this.projectPoint(x1, var15, z1);
      var10000[1] = this.projectPoint(x2, var15, z1);
      var10000[2] = this.projectPoint(x2, var15, z2);
      var10000[3] = this.projectPoint(x1, var15, z2);
      double[][] var17 = var10000;
      if (this.isValidQuad(var17)) {
         float var18 = (float)((Double)this.lineWidth.get()).doubleValue() * 1.25F;
         Color var19 = this.withAlpha(color, 240);
         if ((Boolean)this.neonBloom.get()) {
            this.drawQuadOutline(var17, this.withAlpha(color, 60), var18 * 2.2F);
         }

         this.drawQuadOutline(var17, var19, var18);
         int[][] var10001 = new int[1][];
         int[] var10004 = new int[4];
         var10004[0] = 0;
         var10004[1] = 1;
         var10004[2] = 2;
         var10004[3] = 3;
         var10001[0] = var10004;
         NanoVGRenderer.drawEspFill(var17, var10001, 1.0, this.withAlpha(color, (int)((Double)this.fillAlpha.get() * 180.0)));
      }
   }

   private void renderGrow(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, this.withAlpha(color, 25), false, true, 1.0F);
      float var15 = p * (2.0F - p);
      double var16 = (x1 + x2) * 0.5;
      double var18 = (y1 + y2) * 0.5;
      double var20 = (z1 + z2) * 0.5;
      double var22 = (x2 - x1) * 0.5 * var15;
      double var24 = (y2 - y1) * 0.5 * var15;
      double var26 = (z2 - z1) * 0.5 * var15;
      this.drawVolume(
         var16 - var22, var18 - var24, var20 - var26, var16 + var22, var18 + var24, var20 + var26, color, (Boolean)this.fill.get(), (Boolean)this.outline.get()
      );
   }

   private void renderShrink(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, this.withAlpha(color, 25), false, true, 1.0F);
      double var15 = Math.max(0.04, 1.0 - p * 0.94);
      double var17 = (x1 + x2) * 0.5;
      double var19 = (y1 + y2) * 0.5;
      double var21 = (z1 + z2) * 0.5;
      double var23 = (x2 - x1) * 0.5 * var15;
      double var25 = (y2 - y1) * 0.5 * var15;
      double var27 = (z2 - z1) * 0.5 * var15;
      this.drawVolume(
         var17 - var23, var19 - var25, var21 - var27, var17 + var23, var19 + var25, var21 + var27, color, (Boolean)this.fill.get(), (Boolean)this.outline.get()
      );
   }

   private void renderRadialDial(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, this.withAlpha(color, 25), (Boolean)this.fill.get(), (Boolean)this.outline.get());
      double var15 = (x1 + x2) * 0.5;
      double var17 = (y1 + y2) * 0.5;
      double var19 = (z1 + z2) * 0.5;
      double[] var21 = this.projectPoint(var15, var17, var19);
      if (var21 != null) {
         float var22 = (float)var21[0];
         float var23 = (float)var21[1];
         float var24 = (float)var21[2];
         float var25 = Math.max(16.0F, Math.min(46.0F, 130.0F / Math.max(1.0F, var24)));
         float var26 = Math.max(2.0F, (float)((Double)this.lineWidth.get()).doubleValue());
         NanoVGRenderer.drawCircle(var22, var23, var25 + var26 * 1.5F, -1878126056);
         NanoVGRenderer.drawCircleOutline(var22, var23, var25, var26, this.withAlpha(color, 45));
         float var27 = (float) (-Math.PI / 2);
         float var28 = (float)(p * 2.0 * Math.PI);
         if ((Boolean)this.neonBloom.get()) {
            NanoVGRenderer.drawArc(var22, var23, var25, var27, var27 + var28, var26 * 2.0F, this.withAlpha(color, 65));
         }

         NanoVGRenderer.drawArc(var22, var23, var25, var27, var27 + var28, var26, color);
         int var29 = Math.min(100, Math.max(0, (int)(p * 100.0F)));
         float var30 = Math.max(9.0F, var25 * 0.75F);
         NVGTextRenderer.drawCenteredTextWithShadow(var29 + "%", var22, var23 - var30 * 0.45F, var30, Color.WHITE);
      }
   }

   private void renderCornerLock(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      if ((Boolean)this.fill.get()) {
         double[][] var15 = this.projectBox(x1, y1, z1, x2, y2, z2);
         if (var15 != null) {
            int var16 = (int)((Double)this.fillAlpha.get() * 255.0 * (0.3 + p * 0.7));
            int[][] var17 = this.computeVisibleFaces(x1, y1, z1, x2, y2, z2);
            NanoVGRenderer.drawEspFill(var15, var17, 1.0, this.withAlpha(color, var16));
         }
      }

      double var31 = (x2 - x1) * (0.16 + p * 0.18);
      float var32 = (float)((Double)this.lineWidth.get()).doubleValue() * 1.15F;
      double[][] var18 = new double[][]{{x1, y1, z1}, {x2, y1, z1}, {x2, y1, z2}, {x1, y1, z2}, {x1, y2, z1}, {x2, y2, z1}, {x2, y2, z2}, {x1, y2, z2}};
      double[][] var19 = new double[][]{
         {1.0, 1.0, 1.0}, {-1.0, 1.0, 1.0}, {-1.0, 1.0, -1.0}, {1.0, 1.0, -1.0}, {1.0, -1.0, 1.0}, {-1.0, -1.0, 1.0}, {-1.0, -1.0, -1.0}, {1.0, -1.0, -1.0}
      };
      if ((Boolean)this.neonBloom.get()) {
         NanoVGRenderer.beginLineBatch(this.withAlpha(color, 55), var32 * 2.2F);

         for (int var20 = 0; var20 < 8; var20++) {
            double var21 = var18[var20][0];
            double var23 = var18[var20][1];
            double var25 = var18[var20][2];
            double[] var27 = this.projectPoint(var21, var23, var25);
            if (var27 != null) {
               double[] var28 = this.projectPoint(var21 + var19[var20][0] * var31, var23, var25);
               double[] var29 = this.projectPoint(var21, var23 + var19[var20][1] * var31, var25);
               double[] var30 = this.projectPoint(var21, var23, var25 + var19[var20][2] * var31);
               if (var28 != null) {
                  NanoVGRenderer.batchLine((float)var27[0], (float)var27[1], (float)var28[0], (float)var28[1]);
               }

               if (var29 != null) {
                  NanoVGRenderer.batchLine((float)var27[0], (float)var27[1], (float)var29[0], (float)var29[1]);
               }

               if (var30 != null) {
                  NanoVGRenderer.batchLine((float)var27[0], (float)var27[1], (float)var30[0], (float)var30[1]);
               }
            }
         }

         NanoVGRenderer.endLineBatch();
      }

      NanoVGRenderer.beginLineBatch(color, var32);

      for (int var33 = 0; var33 < 8; var33++) {
         double var34 = var18[var33][0];
         double var35 = var18[var33][1];
         double var36 = var18[var33][2];
         double[] var37 = this.projectPoint(var34, var35, var36);
         if (var37 != null) {
            double[] var38 = this.projectPoint(var34 + var19[var33][0] * var31, var35, var36);
            double[] var39 = this.projectPoint(var34, var35 + var19[var33][1] * var31, var36);
            double[] var40 = this.projectPoint(var34, var35, var36 + var19[var33][2] * var31);
            if (var38 != null) {
               NanoVGRenderer.batchLine((float)var37[0], (float)var37[1], (float)var38[0], (float)var38[1]);
            }

            if (var39 != null) {
               NanoVGRenderer.batchLine((float)var37[0], (float)var37[1], (float)var39[0], (float)var39[1]);
            }

            if (var40 != null) {
               NanoVGRenderer.batchLine((float)var37[0], (float)var37[1], (float)var40[0], (float)var40[1]);
            }
         }
      }

      NanoVGRenderer.endLineBatch();
   }

   private void renderNeonWireframe(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color) {
      float var15 = 0.85F + 0.15F * (float)Math.sin(System.currentTimeMillis() / 160.0 + p * 6.0);
      Color var16 = this.withAlpha(color, (int)(color.getAlpha() * var15));
      float var17 = (float)((Double)this.lineWidth.get()).doubleValue() * 1.1F;
      this.drawVolume(x1, y1, z1, x2, y2, z2, var16, (Boolean)this.fill.get(), (Boolean)this.outline.get(), var17);
   }

   private void renderScanline(double x1, double y1, double z1, double x2, double y2, double z2, float p, Color color, long now) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, this.withAlpha(color, 35), (Boolean)this.fill.get(), (Boolean)this.outline.get());
      double var17 = now % 1100L / 1100.0;
      double var19 = y1 + (y2 - y1) * var17;
      double[][] var10000 = new double[4][];
      var10000[0] = this.projectPoint(x1, var19, z1);
      var10000[1] = this.projectPoint(x2, var19, z1);
      var10000[2] = this.projectPoint(x2, var19, z2);
      var10000[3] = this.projectPoint(x1, var19, z2);
      double[][] var21 = var10000;
      if (this.isValidQuad(var21)) {
         Color var22 = this.withAlpha(color, 240);
         float var23 = (float)((Double)this.lineWidth.get()).doubleValue() * 1.3F;
         if ((Boolean)this.neonBloom.get()) {
            this.drawQuadOutline(var21, this.withAlpha(color, 65), var23 * 2.2F);
         }

         this.drawQuadOutline(var21, var22, var23);
         int[][] var10001 = new int[1][];
         int[] var10004 = new int[4];
         var10004[0] = 0;
         var10004[1] = 1;
         var10004[2] = 2;
         var10004[3] = 3;
         var10001[0] = var10004;
         NanoVGRenderer.drawEspFill(var21, var10001, 1.0, this.withAlpha(color, 85));
      }
   }

   private void renderBreakFlash(double x1, double y1, double z1, double x2, double y2, double z2, Color color, float flashTimer) {
      double var15 = (1.0 - flashTimer) * 0.12;
      int var17 = Math.max(0, Math.min(255, (int)(flashTimer * flashTimer * 160.0F)));
      Color var18 = this.withAlpha(color, var17);
      this.drawVolume(
         x1 - var15, y1 - var15, z1 - var15, x2 + var15, y2 + var15, z2 + var15, var18, true, true, (float)((Double)this.lineWidth.get()).doubleValue() * 1.3F
      );
   }

   private void renderFloatingText(class_2338 pos, double x1, double y1, double z1, double x2, double y2, double z2, float p, Color accent, String minerName) {
      double var17 = (x1 + x2) * 0.5;
      double var19 = y2 + 0.22;
      double var21 = (z1 + z2) * 0.5;
      double[] var23 = this.projectPoint(var17, var19, var21);
      if (var23 != null) {
         float var24 = (float)var23[0];
         float var25 = (float)var23[1];
         float var26 = (float)var23[2];
         float var27 = (float)Math.max(0.75, Math.min(1.25, 5.5 / Math.max(1.0, var26))) * (float)((Double)this.textScale.get()).doubleValue();
         int var28 = Math.min(100, Math.max(0, (int)(p * 100.0F)));
         BreakingProgress.TextStyle var29 = (BreakingProgress.TextStyle)this.textStyle.get();
         if (var29 == BreakingProgress.TextStyle.MINIMAL) {
            float var48 = 13.0F * var27;
            NVGTextRenderer.drawCenteredTextWithShadow(var28 + "%", var24, var25 - var48 * 0.5F, var48, accent);
         } else if (var29 == BreakingProgress.TextStyle.ICON) {
            String var47 = "[ " + var28 + "% ]";
            float var49 = 12.0F * var27;
            NVGTextRenderer.drawCenteredTextWithShadow(var47, var24, var25 - var49 * 0.5F, var49, accent);
         } else {
            String var30 = minerName != null ? minerName + "  " + var28 + "%" : var28 + "%";
            float var31 = 11.5F * var27;
            float var32 = NVGTextRenderer.getTextWidth(var30, var31);
            float var33 = 8.0F * var27;
            float var34 = 5.0F * var27;
            float var35 = 2.5F * var27;
            float var36 = 6.0F * var27;
            float var37 = var32 + var33 * 2.0F + (var35 * 2.0F + var36);
            float var38 = var31 + var34 * 2.0F + 2.0F * var27;
            float var39 = var24 - var37 * 0.5F;
            float var40 = var25 - var38 * 0.5F;
            NanoVGRenderer.drawRoundedRect(var39, var40, var37, var38, 4.0F * var27, -670232297);
            NanoVGRenderer.drawRoundedRectOutline(var39, var40, var37, var38, 4.0F * var27, 1.0F * var27, this.withAlpha(accent, 140));
            float var41 = var39 + var33 + var35;
            float var42 = var40 + var34 + var31 * 0.5F - 1.0F * var27;
            NanoVGRenderer.drawCircle(var41, var42, var35, accent);
            float var43 = var41 + var35 + var36;
            NVGTextRenderer.drawTextWithShadow(var30, var43, var40 + var34, var31, Color.WHITE);
            float var44 = var37 - 8.0F * var27;
            float var45 = var44 * p;
            float var46 = var40 + var38 - 3.0F * var27;
            NanoVGRenderer.drawRoundedRect(var39 + 4.0F * var27, var46, var44, 1.5F * var27, 0.75F * var27, 1090519039);
            if (var45 > 0.5F) {
               NanoVGRenderer.drawRoundedRect(var39 + 4.0F * var27, var46, var45, 1.5F * var27, 0.75F * var27, accent);
            }
         }
      }
   }

   private void drawVolume(double x1, double y1, double z1, double x2, double y2, double z2, Color color, boolean doFill, boolean doOutline) {
      this.drawVolume(x1, y1, z1, x2, y2, z2, color, doFill, doOutline, (float)((Double)this.lineWidth.get()).doubleValue());
   }

   private void drawVolume(double x1, double y1, double z1, double x2, double y2, double z2, Color color, boolean doFill, boolean doOutline, float width) {
      double[][] var17 = this.projectBox(x1, y1, z1, x2, y2, z2);
      if (var17 != null) {
         if (doFill) {
            int var18 = (int)((Double)this.fillAlpha.get() * 255.0);
            if (var18 > 0) {
               int[][] var19 = this.computeVisibleFaces(x1, y1, z1, x2, y2, z2);
               NanoVGRenderer.drawEspFill(var17, var19, 1.0, this.withAlpha(color, var18));
            }
         }

         if (doOutline) {
            float var26 = Math.max(0.5F, width);
            if ((Boolean)this.neonBloom.get()) {
               int var27 = Math.max(10, Math.min(50, (int)(color.getAlpha() * 0.3F)));
               NanoVGRenderer.beginLineBatch(this.withAlpha(color, var27), var26 * 2.2F);

               for (int[] var23 : EDGES) {
                  double[] var24 = var17[var23[0]];
                  double[] var25 = var17[var23[1]];
                  if (var24 != null && var25 != null) {
                     NanoVGRenderer.batchLine((float)var24[0], (float)var24[1], (float)var25[0], (float)var25[1]);
                  }
               }

               NanoVGRenderer.endLineBatch();
            }

            NanoVGRenderer.beginLineBatch(color, var26);

            for (int[] var31 : EDGES) {
               double[] var32 = var17[var31[0]];
               double[] var33 = var17[var31[1]];
               if (var32 != null && var33 != null) {
                  NanoVGRenderer.batchLine((float)var32[0], (float)var32[1], (float)var33[0], (float)var33[1]);
               }
            }

            NanoVGRenderer.endLineBatch();
         }
      }
   }

   private int[][] computeVisibleFaces(double x1, double y1, double z1, double x2, double y2, double z2) {
      class_243 var13 = this.projector.cameraPos();
      if (var13 == null) {
         return FACES;
      }

      boolean var14 = var13.field_1352 >= x1
         && var13.field_1352 <= x2
         && var13.field_1351 >= y1
         && var13.field_1351 <= y2
         && var13.field_1350 >= z1
         && var13.field_1350 <= z2;
      if (var14) {
         return FACES;
      }

      ArrayList var15 = new ArrayList(3);
      if (var13.field_1351 < y1) {
         var15.add(FACES[0]);
      }

      if (var13.field_1351 > y2) {
         var15.add(FACES[1]);
      }

      if (var13.field_1350 < z1) {
         var15.add(FACES[2]);
      }

      if (var13.field_1350 > z2) {
         var15.add(FACES[3]);
      }

      if (var13.field_1352 < x1) {
         var15.add(FACES[4]);
      }

      if (var13.field_1352 > x2) {
         var15.add(FACES[5]);
      }

      return var15.isEmpty() ? FACES : var15.toArray(new int[0][]);
   }

   private double[][] projectBox(double x1, double y1, double z1, double x2, double y2, double z2) {
      double[][] var10000 = new double[8][];
      double[] var10003 = new double[3];
      var10003[0] = x1;
      var10003[1] = y1;
      var10003[2] = z1;
      var10000[0] = var10003;
      var10003 = new double[3];
      var10003[0] = x2;
      var10003[1] = y1;
      var10003[2] = z1;
      var10000[1] = var10003;
      int var10002 = 2;
      var10003 = new double[3];
      var10003[0] = x2;
      var10003[1] = y1;
      var10003[2] = z2;
      var10000[var10002] = var10003;
      var10002 = 3;
      var10003 = new double[3];
      var10003[0] = x1;
      var10003[1] = y1;
      var10003[2] = z2;
      var10000[var10002] = var10003;
      var10002 = 4;
      var10003 = new double[3];
      var10003[0] = x1;
      var10003[1] = y2;
      var10003[2] = z1;
      var10000[var10002] = var10003;
      var10002 = 5;
      var10003 = new double[3];
      var10003[0] = x2;
      var10003[1] = y2;
      var10003[2] = z1;
      var10000[var10002] = var10003;
      var10002 = 6;
      var10003 = new double[3];
      var10003[0] = x2;
      var10003[1] = y2;
      var10003[2] = z2;
      var10000[var10002] = var10003;
      var10002 = 7;
      var10003 = new double[3];
      var10003[0] = x1;
      var10003[1] = y2;
      var10003[2] = z2;
      var10000[var10002] = var10003;
      double[][] var13 = var10000;
      double[][] var14 = new double[8][3];

      for (int var15 = 0; var15 < (8); var15++) {
         if (!this.projector.project(var13[var15][0], var13[var15][1], var13[var15][2], this.scratchPt)) {
            return null;
         }

         var14[var15][0] = this.scratchPt[0];
         var14[var15][1] = this.scratchPt[1];
         var14[var15][2] = this.scratchPt[2];
      }

      return var14;
   }

   private double[] projectPoint(double x, double y, double z) {
      if (!this.projector.project(x, y, z, this.scratchPt)) {
         return null;
      }

      double[] var10000 = new double[3];
      var10000[0] = this.scratchPt[0];
      var10000[1] = this.scratchPt[1];
      var10000[2] = this.scratchPt[2];
      return var10000;
   }

   private boolean isValidQuad(double[][] quad) {
      return quad != null && quad[0] != null && quad[1] != null && quad[2] != null && quad[3] != null;
   }

   private void drawQuadOutline(double[][] quad, Color color, float width) {
      NanoVGRenderer.beginLineBatch(color, width);

      for (int var4 = 0; var4 < (4); var4++) {
         double[] var5 = quad[var4];
         double[] var6 = quad[(var4 + 1) % (4)];
         NanoVGRenderer.batchLine((float)var5[0], (float)var5[1], (float)var6[0], (float)var6[1]);
      }

      NanoVGRenderer.endLineBatch();
   }

   private Color computeProgressColor(float p, long now) {
      BreakingProgress.ColorMode var4 = (BreakingProgress.ColorMode)this.colorMode.get();

      return switch (var4) {
         case DYNAMIC -> {
            if (p < 0.5F) {
               float var6 = p / 0.5F;
               yield this.lerpColor(new Color(62880), new Color(16762935), var6);
            } else {
               float var7 = (p - 0.5F) / 0.5F;
               yield this.lerpColor(new Color(16762935), new Color(16722541), var7);
            }
         }
         case CUSTOM -> this.lerpColor(new Color((Integer)this.startColor.get(), true), new Color((Integer)this.endColor.get(), true), p);
         case RAINBOW -> {
            float var5 = (float)(now % 3600L / 3600.0 + p * 0.35);
            yield new Color(Color.HSBtoRGB(var5, 0.82F, 1.0F));
         }
         case CYBERPUNK -> this.lerpColor(new Color(383464), new Color(16722541), p);
         case GOLDEN -> this.lerpColor(new Color(16766720), new Color(16742144), p);
      };
   }

   private Color lerpColor(Color c1, Color c2, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int var4 = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * t);
      int var5 = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * t);
      int var6 = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * t);
      int var7 = (int)(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * t);
      return new Color(var4, var5, var6, var7);
   }

   private Color withAlpha(Color c, int alpha) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
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

   private static class BreakTrack {
      final class_2338 pos;
      float currentProgress;
      float targetProgress;
      long lastSeenMs;
      String minerName;
      boolean isLocal;
      float flashTimer = 0.0F;
      boolean flashTriggered = false;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      BreakTrack(class_2338 pos, float initialProgress, String minerName, boolean isLocal) {
         this.pos = posx;
         this.targetProgress = initialProgress;
         this.currentProgress = initialProgress;
         this.lastSeenMs = System.currentTimeMillis();
         this.minerName = minerNamex;
         this.isLocal = isLocalx;
      }
   }

   public enum ColorMode {
      DYNAMIC(
         "Dynamic (Green-Yellow-Red)"
      ),
      CUSTOM("Custom Gradient"),
      RAINBOW("Chromatic Wave"),
      CYBERPUNK("Cyberpunk (Cyan-Pink)"),
      GOLDEN("Sunfire Gold");

      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ColorMode(String label) {
         this.label = labelx;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static BreakingProgress.ColorMode[] $values() {
         BreakingProgress.ColorMode[] var10000 = new BreakingProgress.ColorMode[5];
         var10000[0] = DYNAMIC;
         var10000[1] = CUSTOM;
         var10000[2] = RAINBOW;
         var10000[3] = CYBERPUNK;
         var10000[4] = GOLDEN;
         return var10000;
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

   public enum ProgressMode {
      RISING("Rising"),
      GROW("Grow"),
      SHRINK("Shrink"),
      RADIAL_DIAL("Radial Dial"),
      CORNER_LOCK("Corner Lock"),
      NEON_WIREFRAME("Neon Wireframe"),
      SCANLINE("Scanline");

      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ProgressMode(String label) {
         this.label = labelx;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static BreakingProgress.ProgressMode[] $values() {
         BreakingProgress.ProgressMode[] var10000 = new BreakingProgress.ProgressMode[7];
         var10000[0] = RISING;
         var10000[1] = GROW;
         var10000[2] = SHRINK;
         var10000[3] = RADIAL_DIAL;
         var10000[4] = CORNER_LOCK;
         var10000[5] = NEON_WIREFRAME;
         var10000[6] = SCANLINE;
         return var10000;
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

   public enum TextStyle {
      BADGE("Glassmorphic Badge"),
      MINIMAL("Minimal Percentage"),
      ICON("Icon & Percentage"),
      NONE("Disabled");

      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      TextStyle(String label) {
         this.label = labelx;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static BreakingProgress.TextStyle[] $values() {
         BreakingProgress.TextStyle[] var10000 = new BreakingProgress.TextStyle[4];
         var10000[0] = BADGE;
         var10000[1] = MINIMAL;
         var10000[2] = ICON;
         var10000[3] = NONE;
         return var10000;
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
