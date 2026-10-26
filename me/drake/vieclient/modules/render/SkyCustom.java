package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4184;

public class SkyCustom extends Module {
   private static final double SKY_RADIUS = 220.0;
   private static final long STAR_SEED = 1403633886L;
   private static final int METEOR_SEGMENTS = 8;
   private static final int TEXTURE_RINGS = 48;
   private static final double DOME_RADIUS = 300.0;
   private final EnumSetting preset = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Preset",
         "Nuit skybox preset profile (CUSTOM preserves manual settings)",
         SkyCustom.Preset.CLEAN,
         SkyCustom.Preset.values()
      )
   );
   private final EnumSetting smoothness = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Smoothness",
         "Spherical dome mesh density - higher is a smoother gradient",
         SkyCustom.Smoothness.HIGH
      )
   );
   private final EnumSetting blendMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Blend Mode",
         "Nuit OpenGL blending equation for sky overlays",
         SkyCustom.BlendMode.ALPHA
      )
   );
   private final DoubleSetting blend = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Blend",
         "Sky tint blend intensity (0 = vanilla, 1 = fully custom)",
         0.65,
         0.0,
         1.0,
         0.01
      )
   );
   private final ColorSetting topColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Top Color",
         "Zenith sky dome tint (CUSTOM preset)",
         -12947488
      )
   );
   private final ColorSetting horizonColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Horizon Color",
         "Horizon & fog tint (CUSTOM preset)",
         -4664065
      )
   );
   private final ColorSetting domeVoidColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Void Color",
         "Lower void hemisphere gradient color",
         -15657700
      )
   );
   private final ColorSetting domeNadirColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Nadir Color",
         "Nadir ground color straight down",
         -16447990
      )
   );
   private final BoolSetting affectFog = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Affect Fog",
         "Synchronize atmospheric fog to match the custom horizon",
         true
      )
   );
   private final BoolSetting timeIndependent = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Time Independent",
         "Keep sky at constant daytime illumination even at night",
         false
      )
   );
   private final BoolSetting rotateSky = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Rotate Sky",
         "Celestial rotation synchronized with world time or spin",
         true
      )
   );
   private final BoolSetting followCelestial = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Celestial Sync",
         "Synchronize rotation directly with sun and moon orbit",
         true
      )
   );
   private final DoubleSetting rotationSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Rotation Speed",
         "Custom angular velocity multiplier for sky spin",
         1.0,
         0.0,
         5.0,
         0.1
      )
   );
   private final BoolSetting stars = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Stars",
         "Render sparkling celestial starfield",
         false
      )
   );
   private final BoolSetting nebula = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Nebula",
         "Render deep-space galactic nebula bands",
         false
      )
   );
   private final BoolSetting meteors = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Meteors",
         "Render periodic atmospheric shooting stars",
         false
      )
   );
   private final BoolSetting horizonHalo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Horizon Halo",
         "Render glowing solar horizon halo around the sun",
         false
      )
   );
   private final BoolSetting depthOcclusion = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Depth Occlusion",
         "Occlude celestial effects behind terrain and structures instead of drawing over them",
         true
      )
   );
   private final BoolSetting rgbCycle = (BoolSetting)this.addSetting(
      new BoolSetting(
         "RGB Cycle",
         "Cycle sky colors smoothly through the spectrum over time",
         false
      )
   );
   private final DoubleSetting rgbSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "RGB Speed",
         "Speed of color spectrum cycling",
         1.0,
         0.1,
         10.0,
         0.1
      )
   );
   private int auroraColorA = -13244752;
   private int auroraColorB = -5219073;
   private int nebulaColor = -9801473;
   private int starColor = -1;
   private int haloColor = -10870;
   private static final int STAR_COUNT = 800;
   private static final double STAR_SIZE = 0.55;
   private static final boolean TWINKLE = true;
   private static final boolean DITHER = true;
   private static final int DITHER_STRENGTH = 2;
   private SkyEffectGeometry.Meteor activeMeteor;
   private long nextMeteorMillis;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SkyCustom() {
      super(
         "SkyCustom",
         "100% Nuit custom skybox engine with smooth transitions and 3D rotation",
         Category.CUSTOMIZE
      );
      this.depthOcclusion.setVisibleInGui(false);
      this.rotationSpeed.visibleWhen(this.rotateSky::get);
      this.rgbSpeed.visibleWhen(this.rgbCycle::get);
      this.preset.onChanged(value -> {
         this.applyPreset(value);
         this.requestConfigSave();
      });
   }

   @Override
   public String getInfo() {
      return this.preset.get() == SkyCustom.Preset.CUSTOM ? null : ((SkyCustom.Preset)this.preset.get()).name();
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (this.isEnabled() && mc.field_1687 != null) {
            if (mc.field_1687.method_27983() == class_1937.field_25179) {
               class_4184 var2 = mc.field_1773.method_19418();
               if (var2 != null) {
                  class_243 var3 = var2.method_71156();
                  double var4 = System.currentTimeMillis() / 1000.0;
                  float var6 = event.getTickDelta();
                  double var7 = this.computeNuitRotation(var6, var4);
                  this.drawDome(var3, var6, var7);
                  if ((Boolean)this.nebula.get()) {
                     this.drawNebula(var3, var4, var7);
                  }

                  if ((Boolean)this.stars.get()) {
                     this.drawStars(var3, var4, var7);
                  }

                  if ((Boolean)this.horizonHalo.get()) {
                     this.drawHorizonHalo(var3, var6);
                  }

                  if ((Boolean)this.meteors.get()) {
                     this.drawMeteor(var3, System.currentTimeMillis());
                  }
               }
            }
         }
      }
   }

   private void drawDome(class_243 camera, float tickDelta, double rotationAngle) {
      SkyDomeGeometry.DomeMesh var5 = SkyDomeGeometry.mesh(this.domeRings(), this.domeSegments());
      int[] var6 = this.resolveDomeStops();
      double var7 = this.dayNightBrightness(tickDelta);
      class_243[] var9 = var5.directions();
      float[] var10 = var5.dither();
      int[] var11 = new int[var9.length];

      for (int var12 = 0; var12 < var9.length; var12++) {
         class_243 var13 = var9[var12];
         int var14 = SkyDomeGeometry.gradientColor(var13.field_1351, SkyDomeGeometry.STOP_POSITIONS, var6);
         var14 = scaleBrightness(var14, var7);
         var14 = applyDither(var14, var10[var12], 2.0F);
         var11[var12] = var14;
      }

      class_243 var24 = this.getRotationAxis();
      int[] var25 = var5.triangles();

      for (byte var28 = 0; var28 < var25.length; var28 += 3) {
         int var15 = var25[var28];
         int var16 = var25[var28 + 1];
         int var17 = var25[var28 + (2)];
         class_243 var18 = rotateVector(var9[var15], var24, rotationAngle);
         class_243 var19 = rotateVector(var9[var16], var24, rotationAngle);
         class_243 var20 = rotateVector(var9[var17], var24, rotationAngle);
         class_243 var21 = SkyEffectGeometry.relative(camera, var18, 300.0);
         class_243 var22 = SkyEffectGeometry.relative(camera, var19, 300.0);
         class_243 var23 = SkyEffectGeometry.relative(camera, var20, 300.0);
         RenderUtil.drawTriangleDepthGouraud(
            var21.field_1352,
            var21.field_1351,
            var21.field_1350,
            var11[var15],
            var22.field_1352,
            var22.field_1351,
            var22.field_1350,
            var11[var16],
            var23.field_1352,
            var23.field_1351,
            var23.field_1350,
            var11[var17]
         );
      }
   }

   private void drawSkyTexture(class_243 camera, double time, double rotationAngle) {
      SkyDomeGeometry.DomeMesh var6 = SkyDomeGeometry.mesh(48, this.domeSegments());
      class_243[] var7 = var6.directions();
      int[] var8 = var6.triangles();
      if (var8.length >= (3)) {
         class_243 var9 = SkyEffectGeometry.relative(camera, var7[var8[0]], 300.0);
         class_243 var10 = SkyEffectGeometry.relative(camera, var7[var8[1]], 300.0);
         class_243 var11 = SkyEffectGeometry.relative(camera, var7[var8[2]], 300.0);
         RenderUtil.drawTriangleAdditiveGouraud(
            var9.field_1352,
            var9.field_1351,
            var9.field_1350,
            0,
            var10.field_1352,
            var10.field_1351,
            var10.field_1350,
            0,
            var11.field_1352,
            var11.field_1351,
            var11.field_1350,
            0
         );
      }
   }

   private double computeNuitRotation(float tickDelta, double time) {
      if (!(Boolean)this.rotateSky.get()) {
         return 0.0;
      } else if ((Boolean)this.followCelestial.get() && mc.field_1687 != null) {
         long var4 = mc.field_1687.method_8532();
         double var6 = ((float)(var4 % 24000L) + Math.max(0.0F, tickDelta)) / 24000.0;
         return var6 * Math.PI * 2.0;
      } else {
         return time * 0.05 * (Double)this.rotationSpeed.get();
      }
   }

   private class_243 getRotationAxis() {
      return new class_243(0.12, 0.98, 0.08).method_1029();
   }

   private static class_243 rotateVector(class_243 v, class_243 k, double theta) {
      if (Math.abs(theta) < 1.0E-7) {
         return v;
      }

      double var4 = Math.cos(theta);
      double var6 = Math.sin(theta);
      class_243 var8 = k.method_1036(v);
      double var9 = k.method_1026(v);
      return v.method_1021(var4).method_1019(var8.method_1021(var6)).method_1019(k.method_1021(var9 * (1.0 - var4)));
   }

   private double dayNightBrightness(float tickDelta) {
      if (!(Boolean)this.timeIndependent.get() && mc.field_1687 != null) {
         long var2 = Math.floorMod(mc.field_1687.method_8532(), 24000L);
         double var4 = calculateHermiteFade(var2, 22500L, 1500L, 11500L, 13800L);
         return 0.16 + 0.84 * var4;
      } else {
         return 1.0;
      }
   }

   public static double calculateHermiteFade(long t, long startIn, long endIn, long startOut, long endOut) {
      if (isInRange(t, startIn, endIn)) {
         double var12 = diffFraction(t, startIn, endIn);
         return var12 * var12 * (3.0 - 2.0 * var12);
      } else if (isInRange(t, endIn, startOut)) {
         return 1.0;
      } else if (isInRange(t, startOut, endOut)) {
         double var10 = diffFraction(t, startOut, endOut);
         return 1.0 - var10 * var10 * (3.0 - 2.0 * var10);
      } else {
         return 0.0;
      }
   }

   private static boolean isInRange(long t, long a, long b) {
      return a <= b ? t >= a && t <= b : t >= a || t <= b;
   }

   private static double diffFraction(long t, long a, long b) {
      if (a <= b) {
         return (double)(t - a) / Math.max(1L, b - a);
      }

      long var6 = 24000L - a + b;
      long var8 = t >= a ? t - a : 24000L - a + t;
      return (double)var8 / Math.max(1L, var6);
   }

   private void drawAurora(class_243 camera, double time, double rot) {
      int var6 = 6;
      int var7 = 24;
      class_243 var8 = this.getRotationAxis();

      for (int var9 = 0; var9 < (6); var9++) {
         double var10 = var9 * 0.73;
         double var12 = 0.3 + var9 * 0.065;

         for (int var14 = 0; var14 < (24); var14++) {
            double var15 = var14 / 24.0;
            double var17 = (var14 + 1) / 24.0;
            double var19 = -1.55 + var15 * 3.1;
            double var21 = -1.55 + var17 * 3.1;
            class_243 var23 = rotateVector(SkyEffectGeometry.direction(var19, var12 + wave(var15, time, var10)), var8, rot);
            class_243 var24 = rotateVector(SkyEffectGeometry.direction(var21, var12 + wave(var17, time, var10)), var8, rot);
            class_243 var25 = rotateVector(SkyEffectGeometry.direction(var19, var12 - 0.17 + wave(var15, time, var10)), var8, rot);
            class_243 var26 = rotateVector(SkyEffectGeometry.direction(var21, var12 - 0.17 + wave(var17, time, var10)), var8, rot);
            int var27 = RainbowUtil.withAlpha(SkyDomeGeometry.lerpArgbHsv(this.auroraColorA, this.auroraColorB, var9 / 5.0), 108);
            int var28 = RainbowUtil.withAlpha(SkyDomeGeometry.lerpArgbHsv(this.auroraColorA, this.auroraColorB, (var9 + 1) / 6.0), 108);
            this.drawAdditiveQuad(camera, var23, var24, var26, var25, var27, var28, RainbowUtil.withAlpha(var28, 0), RainbowUtil.withAlpha(var27, 0));
         }
      }
   }

   private void drawNebula(class_243 camera, double time, double rot) {
      class_243 var6 = this.getRotationAxis();

      for (int var7 = 0; var7 < (4); var7++) {
         double var8 = time * 0.035 + var7 * 1.58;
         class_243 var10 = rotateVector(SkyEffectGeometry.direction(var8, 0.25 + var7 * 0.11), var6, rot);
         class_243 var11 = rotateVector(SkyEffectGeometry.direction(var8 + 1.15, 0.48 + var7 * 0.06), var6, rot);
         class_243 var12 = rotateVector(SkyEffectGeometry.direction(var8 + 1.33, 0.6 + var7 * 0.05), var6, rot);
         class_243 var13 = rotateVector(SkyEffectGeometry.direction(var8 + 0.18, 0.37 + var7 * 0.08), var6, rot);
         int var14 = RainbowUtil.withAlpha(this.nebulaColor, 54);
         this.drawAdditiveQuad(camera, var10, var11, var12, var13, RainbowUtil.withAlpha(var14, 0), var14, var14, RainbowUtil.withAlpha(var14, 0));
      }
   }

   private void drawStars(class_243 camera, double time, double rot) {
      double var6 = 0.0025;
      int var8 = this.starColor;
      class_243 var9 = this.getRotationAxis();

      for (SkyEffectGeometry.Star var11 : SkyEffectGeometry.stars(1403633886L, 800)) {
         double var12 = 0.65 + 0.35 * Math.sin(time * 2.1 + var11.phase());
         int var14 = (int)Math.round(185.0 * var12);
         class_243 var15 = rotateVector(var11.direction(), var9, rot);
         class_243 var16 = var15.method_1036(new class_243(0.0, 1.0, 0.0));
         if (var16.method_1027() < 0.001) {
            var16 = new class_243(1.0, 0.0, 0.0);
         }

         var16 = var16.method_1029().method_1021(var6 * var11.size());
         class_243 var17 = var15.method_1036(var16).method_1029().method_1021(var6 * var11.size());
         this.drawAdditiveQuad(
            camera,
            var15.method_1020(var16).method_1020(var17),
            var15.method_1019(var16).method_1020(var17),
            var15.method_1019(var16).method_1019(var17),
            var15.method_1020(var16).method_1019(var17),
            RainbowUtil.withAlpha(var8, var14),
            RainbowUtil.withAlpha(var8, var14),
            RainbowUtil.withAlpha(var8, var14),
            RainbowUtil.withAlpha(var8, var14)
         );
      }
   }

   private void drawHorizonHalo(class_243 camera, float tickDelta) {
      if (mc.field_1687 != null) {
         double var3 = ((float)(mc.field_1687.method_8532() % 24000L) + tickDelta) / 24000.0;
         double var5 = var3 * Math.PI * 2.0 - (Math.PI / 2);
         class_243 var7 = SkyEffectGeometry.direction(var5, 0.2);
         class_243 var8 = var7.method_1036(new class_243(0.0, 1.0, 0.0)).method_1029();
         class_243 var9 = var7.method_1036(var8).method_1029();
         int var10 = RainbowUtil.withAlpha(this.haloColor, 115);

         for (int var11 = 0; var11 < (32); var11++) {
            double var12 = var11 * Math.PI * 2.0 / 32.0;
            double var14 = (var11 + 1) * Math.PI * 2.0 / 32.0;
            class_243 var16 = var7.method_1019(var8.method_1021(Math.cos(var12) * 0.13)).method_1019(var9.method_1021(Math.sin(var12) * 0.13));
            class_243 var17 = var7.method_1019(var8.method_1021(Math.cos(var14) * 0.13)).method_1019(var9.method_1021(Math.sin(var14) * 0.13));
            this.drawAdditiveTriangle(camera, var7, var16, var17, RainbowUtil.withAlpha(var10, 0), var10, var10);
         }
      }
   }

   private void drawMeteor(class_243 camera, long now) {
      if (this.activeMeteor == null || !this.activeMeteor.isActiveAt(now)) {
         if (now < this.nextMeteorMillis) {
            return;
         }

         this.activeMeteor = SkyEffectGeometry.newMeteor(1403633886L ^ now / 6000L, now);
         this.nextMeteorMillis = now + 6000L + Math.floorMod(this.activeMeteor.headDirection().hashCode(), 8001);
      }

      double var4 = (double)(now - this.activeMeteor.startMillis()) / this.activeMeteor.durationMillis();
      class_243 var6 = lerpDirection(this.activeMeteor.tailDirection(), this.activeMeteor.headDirection(), var4);

      for (int var7 = 0; var7 < (8); var7++) {
         double var8 = var7 / 8.0;
         double var10 = (var7 + 1) / 8.0;
         class_243 var12 = lerpDirection(this.activeMeteor.tailDirection(), var6, var8);
         class_243 var13 = lerpDirection(this.activeMeteor.tailDirection(), var6, var10);
         int var14 = (int)Math.round(16.0 + 190.0 * var8 * var8);
         int var15 = (int)Math.round(16.0 + 190.0 * var10 * var10);
         this.drawAdditiveTriangle(
            camera,
            var12,
            var13,
            var13.method_1019(new class_243(0.0, 0.008, 0.0)),
            RainbowUtil.withAlpha(this.starColor, var14),
            RainbowUtil.withAlpha(this.starColor, var15),
            RainbowUtil.withAlpha(this.starColor, var15)
         );
      }
   }

   private static double wave(double u, double time, double phase) {
      return Math.sin(u * Math.PI * 3.0 + time * 0.7 + phase) * 0.055 + Math.sin(u * Math.PI * 7.0 + time + phase) * 0.025;
   }

   private static class_243 lerpDirection(class_243 from, class_243 to, double t) {
      return from.method_1021(1.0 - t).method_1019(to.method_1021(t)).method_1029();
   }

   private void drawAdditiveQuad(class_243 camera, class_243 a, class_243 b, class_243 c, class_243 d, int ca, int cb, int cc, int cd) {
      this.drawAdditiveTriangle(camera, a, b, c, ca, cb, cc);
      this.drawAdditiveTriangle(camera, a, c, d, ca, cc, cd);
   }

   private void drawAdditiveTriangle(class_243 camera, class_243 a, class_243 b, class_243 c, int ca, int cb, int cc) {
      class_243 var8 = SkyEffectGeometry.relative(camera, a, 220.0);
      class_243 var9 = SkyEffectGeometry.relative(camera, b, 220.0);
      class_243 var10 = SkyEffectGeometry.relative(camera, c, 220.0);
      if ((Boolean)this.depthOcclusion.get()) {
         RenderUtil.drawTriangleAdditiveDepthGouraud(
            var8.field_1352,
            var8.field_1351,
            var8.field_1350,
            ca,
            var9.field_1352,
            var9.field_1351,
            var9.field_1350,
            cb,
            var10.field_1352,
            var10.field_1351,
            var10.field_1350,
            cc
         );
      } else {
         RenderUtil.drawTriangleAdditiveGouraud(
            var8.field_1352,
            var8.field_1351,
            var8.field_1350,
            ca,
            var9.field_1352,
            var9.field_1351,
            var9.field_1350,
            cb,
            var10.field_1352,
            var10.field_1351,
            var10.field_1350,
            cc
         );
      }
   }

   private static int scaleBrightness(int argb, double brightness) {
      int var3 = argb >>> (24) & (255);
      int var4 = clampByte((int)Math.round((argb >> (16) & (255)) * brightness));
      int var5 = clampByte((int)Math.round((argb >> (8) & (255)) * brightness));
      int var6 = clampByte((int)Math.round((argb & (255)) * brightness));
      return var3 << (24) | var4 << (16) | var5 << (8) | var6;
   }

   private static int applyDither(int argb, float unit, float strength) {
      if (strength <= 0.0F) {
         return argb;
      }

      int var3 = Math.round(unit * strength);
      if (var3 == 0) {
         return argb;
      }

      int var4 = argb >>> (24) & (255);
      int var5 = clampByte((argb >> (16) & (255)) + var3);
      int var6 = clampByte((argb >> (8) & (255)) + var3);
      int var7 = clampByte((argb & (255)) + var3);
      return var4 << (24) | var5 << (16) | var6 << (8) | var7;
   }

   private static int clampByte(int v) {
      return Math.max(0, Math.min(255, v));
   }

   public double getBlend() {
      return (Double)this.blend.get();
   }

   public boolean isTimeIndependent() {
      return (Boolean)this.timeIndependent.get();
   }

   public boolean isAffectFog() {
      return (Boolean)this.affectFog.get();
   }

   private int domeRings() {
      return switch ((SkyCustom.Smoothness)this.smoothness.get()) {
         case LOW -> 48;
         case MEDIUM -> 72;
         case HIGH -> 96;
         case ULTRA -> 144;
      };
   }

   private int domeSegments() {
      return switch ((SkyCustom.Smoothness)this.smoothness.get()) {
         case LOW -> 32;
         case MEDIUM -> 40;
         case HIGH -> 48;
         case ULTRA -> 56;
      };
   }

   public int resolveTopColor() {
      return this.applyCycle(this.resolvePreset()[0], 0.0);
   }

   public int resolveHorizonColor() {
      return this.applyCycle(this.resolvePreset()[1], 0.5);
   }

   private int[] resolvePreset() {
      int[] var10000;
      switch ((SkyCustom.Preset)this.preset.get()) {
         case CLEAN:
            var10000 = new int[2];
            var10000[0] = -12947488;
            var10000[1] = -4664065;
            break;
         case AURORA_NIGHT:
            var10000 = new int[2];
            var10000[0] = -16040658;
            var10000[1] = -13244752;
            break;
         case GALAXY:
            var10000 = new int[2];
            var10000[0] = -16184788;
            var10000[1] = -11452783;
            break;
         case SUNSET_DREAM:
            var10000 = new int[2];
            var10000[0] = -37772;
            var10000[1] = -14200;
            break;
         case VOID:
            var10000 = new int[2];
            var10000[0] = -16777216;
            var10000[1] = -16251380;
            break;
         case VAPORWAVE:
            var10000 = new int[2];
            var10000[0] = -45096;
            var10000[1] = -8586753;
            break;
         case CUSTOM:
            var10000 = new int[2];
            var10000[0] = (Integer)this.topColor.get();
            var10000[1] = (Integer)this.horizonColor.get();
            break;
         default:
            throw new MatchException(null, null);
      }

      return var10000;
   }

   private int[] resolveDomeStops() {
      if (this.preset.get() == SkyCustom.Preset.CUSTOM) {
         int var5 = (Integer)this.topColor.get();
         int var6 = (Integer)this.horizonColor.get();
         int var7 = (Integer)this.domeVoidColor.get();
         int var4 = (Integer)this.domeNadirColor.get();
         int[] var10000 = new int[7];
         var10000[0] = var5;
         var10000[1] = lerpArgb(var5, var6, 0.35);
         var10000[2] = lerpArgb(var5, var6, 0.7);
         var10000[3] = var6;
         var10000[4] = lerpArgb(var6, var7, 0.35);
         var10000[5] = var7;
         var10000[6] = var4;
         return var10000;
      }

      int[] var1 = presetDomeStops((SkyCustom.Preset)this.preset.get());
      if (!(Boolean)this.rgbCycle.get()) {
         return var1;
      }

      int[] var2 = new int[var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         var2[var3] = this.applyCycle(var1[var3], var3 * 0.06);
      }

      return var2;
   }

   private static int[] presetDomeStops(SkyCustom.Preset selected) {
      int[] var10000;
      switch (selected) {
         case CLEAN:
            var10000 = new int[7];
            var10000[0] = -13738328;
            var10000[1] = -11763772;
            var10000[2] = -7754019;
            var10000[3] = -3547662;
            var10000[4] = -1511174;
            var10000[5] = -15657700;
            var10000[6] = -16447990;
            break;
         case AURORA_NIGHT:
            var10000 = new int[7];
            var10000[0] = -16510950;
            var10000[1] = -16310229;
            var10000[2] = -16040658;
            var10000[3] = -14779812;
            var10000[4] = -13244752;
            var10000[5] = -16508394;
            var10000[6] = -16709878;
            break;
         case GALAXY:
            var10000 = new int[7];
            var10000[0] = -16447974;
            var10000[1] = -16052683;
            var10000[2] = -14869924;
            var10000[3] = -12964218;
            var10000[4] = -9743176;
            var10000[5] = -15988192;
            var10000[6] = -16579832;
            break;
         case SUNSET_DREAM:
            var10000 = new int[7];
            var10000[0] = -14013597;
            var10000[1] = -8762738;
            var10000[2] = -2860929;
            var10000[3] = -37772;
            var10000[4] = -14200;
            var10000[5] = -12967626;
            var10000[6] = -15463916;
            break;
         case VOID:
            var10000 = new int[7];
            var10000[0] = -16777216;
            var10000[1] = -16579832;
            var10000[2] = -16382450;
            var10000[3] = -16119790;
            var10000[4] = -16251380;
            var10000[5] = -16580090;
            var10000[6] = -16777216;
            break;
         case VAPORWAVE:
            var10000 = new int[7];
            var10000[0] = -13956534;
            var10000[1] = -9822836;
            var10000[2] = -4640848;
            var10000[3] = -45096;
            var10000[4] = -8586753;
            var10000[5] = -15071437;
            var10000[6] = -16252395;
            break;
         case CUSTOM:
            var10000 = new int[7];
            var10000[0] = -13738328;
            var10000[1] = -11763772;
            var10000[2] = -7754019;
            var10000[3] = -3547662;
            var10000[4] = -1511174;
            var10000[5] = -15657700;
            var10000[6] = -16447990;
            break;
         default:
            throw new MatchException(null, null);
      }

      return var10000;
   }

   private void applyPreset(SkyCustom.Preset selected) {
      if (selected != SkyCustom.Preset.CUSTOM) {
         int[] var2 = this.resolvePreset();
         this.topColor.set(var2[0]);
         this.horizonColor.set(var2[1]);
         int[] var3 = presetDomeStops(selected);
         this.domeVoidColor.set(var3[5]);
         this.domeNadirColor.set(var3[6]);
         switch (selected) {
            case CLEAN:
               this.nebula.set(false);
               this.stars.set(false);
               this.meteors.set(false);
               this.horizonHalo.set(false);
               this.blendMode.set(SkyCustom.BlendMode.ALPHA);
               break;
            case AURORA_NIGHT:
               this.nebula.set(false);
               this.stars.set(true);
               this.meteors.set(true);
               this.horizonHalo.set(false);
               this.blendMode.set(SkyCustom.BlendMode.ADD);
               this.auroraColorA = -13244752;
               this.auroraColorB = -5219073;
               this.nebulaColor = -9801473;
               this.starColor = -1900545;
               break;
            case GALAXY:
               this.nebula.set(true);
               this.stars.set(true);
               this.meteors.set(true);
               this.horizonHalo.set(false);
               this.blendMode.set(SkyCustom.BlendMode.ADD);
               this.nebulaColor = -8885249;
               this.starColor = -1516289;
               break;
            case SUNSET_DREAM:
               this.nebula.set(true);
               this.stars.set(false);
               this.meteors.set(false);
               this.horizonHalo.set(true);
               this.blendMode.set(SkyCustom.BlendMode.SCREEN);
               this.auroraColorA = -38003;
               this.auroraColorB = -14997;
               this.nebulaColor = -29496;
               this.haloColor = -10870;
               break;
            case VOID:
               this.nebula.set(true);
               this.stars.set(true);
               this.meteors.set(false);
               this.horizonHalo.set(false);
               this.blendMode.set(SkyCustom.BlendMode.REPLACE);
               this.nebulaColor = -13030807;
               this.starColor = -4872193;
               break;
            case VAPORWAVE:
               this.nebula.set(true);
               this.stars.set(true);
               this.meteors.set(true);
               this.horizonHalo.set(true);
               this.blendMode.set(SkyCustom.BlendMode.SCREEN);
               this.auroraColorA = -45096;
               this.auroraColorB = -8586753;
               this.nebulaColor = -6587137;
               this.starColor = -1;
               this.haloColor = -29496;
            case CUSTOM:
         }
      }
   }

   private int applyCycle(int argb, double huePhaseOffset) {
      if (!(Boolean)this.rgbCycle.get()) {
         return argb;
      }

      int var4 = argb >>> (24) & (255);
      double var5 = (System.currentTimeMillis() / 1000.0 * (Double)this.rgbSpeed.get() * 0.1 + huePhaseOffset) % 1.0;
      if (var5 < 0.0) {
         var5++;
      }

      int var7 = Color.HSBtoRGB((float)var5, 0.65F, 0.95F);
      return var4 << (24) | var7 & (16777215);
   }

   public static int lerpArgb(int from, int to, double t) {
      double var4 = Math.max(0.0, Math.min(1.0, t));
      int var6 = from >>> (24) & (255);
      int var7 = from >>> (16) & (255);
      int var8 = from >>> (8) & (255);
      int var9 = from & (255);
      int var10 = to >>> (24) & (255);
      int var11 = to >>> (16) & (255);
      int var12 = to >>> (8) & (255);
      int var13 = to & (255);
      int var14 = (int)Math.round(var6 + (var10 - var6) * var4);
      int var15 = (int)Math.round(var7 + (var11 - var7) * var4);
      int var16 = (int)Math.round(var8 + (var12 - var8) * var4);
      int var17 = (int)Math.round(var9 + (var13 - var9) * var4);
      return var14 << (24) | var15 << (16) | var16 << (8) | var17;
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

   public enum BlendMode {
      ALPHA,
      ADD,
      SCREEN,
      MULTIPLY,
      REPLACE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SkyCustom.BlendMode[] $values() {
         SkyCustom.BlendMode[] var10000 = new SkyCustom.BlendMode[5];
         var10000[0] = ALPHA;
         var10000[1] = ADD;
         var10000[2] = SCREEN;
         var10000[3] = MULTIPLY;
         var10000[4] = REPLACE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(-8);
         ALPHA = new SkyCustom.BlendMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(88);
         ADD = new SkyCustom.BlendMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-4);
         var10002[5] = (byte)(-88);
         SCREEN = new SkyCustom.BlendMode();
         var10002 = new byte[8];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-16);
         var10002[5] = (byte)(-74);
         var10002[6] = (byte)(53);
         var10002[7] = (byte)(-77);
         MULTIPLY = new SkyCustom.BlendMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-8);
         var10002[5] = (byte)(-91);
         var10002[6] = (byte)(60);
         REPLACE = new SkyCustom.BlendMode();
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

   public enum Preset {
      CLEAN,
      AURORA_NIGHT,
      GALAXY,
      SUNSET_DREAM,
      VOID,
      VAPORWAVE,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SkyCustom.Preset[] $values() {
         SkyCustom.Preset[] var10000 = new SkyCustom.Preset[7];
         var10000[0] = CLEAN;
         var10000[1] = AURORA_NIGHT;
         var10000[2] = GALAXY;
         var10000[3] = SUNSET_DREAM;
         var10000[4] = VOID;
         var10000[5] = VAPORWAVE;
         var10000[6] = CUSTOM;
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

   public enum Smoothness {
      LOW,
      MEDIUM,
      HIGH,
      ULTRA;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SkyCustom.Smoothness[] $values() {
         SkyCustom.Smoothness[] var10000 = new SkyCustom.Smoothness[4];
         var10000[0] = LOW;
         var10000[1] = MEDIUM;
         var10000[2] = HIGH;
         var10000[3] = ULTRA;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(75);
         LOW = new SkyCustom.Smoothness();
         var10002 = new byte[6];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-115);
         var10002[5] = (byte)(-20);
         MEDIUM = new SkyCustom.Smoothness();
         var10002 = new byte[4];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-106);
         HIGH = new SkyCustom.Smoothness();
         var10002 = new byte[5];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-103);
         ULTRA = new SkyCustom.Smoothness();
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
}
