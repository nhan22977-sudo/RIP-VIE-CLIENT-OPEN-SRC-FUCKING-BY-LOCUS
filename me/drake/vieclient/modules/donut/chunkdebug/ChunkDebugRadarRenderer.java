package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Vector3fc;

public final class ChunkDebugRadarRenderer {
   public static final double BASE_Y = 63.0;
   private static final double MARKER_SIZE = 48.0;
   private static final double TWO_PI = Math.PI * 2;
   private static final int CYAN = 3594239;
   private static final int GOLD = 16758062;
   private static final int RED = 16727375;
   private double visualSeconds;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void reset() {
      this.visualSeconds = 0.0;
   }

   public void advance(float frameTime) {
      this.visualSeconds = advanceTime(this.visualSeconds, frameTime);
   }

   public void render(long chunkKey, ChunkConfidence confidence, int alpha, double distance) {
      ChunkDebugRadarRenderer.MarkerBounds var7 = markerBoundsFor(chunkKey);
      this.renderAnimated(var7.minX(), var7.minZ(), 48.0, chunkKey, confidence, alpha, distance);
   }

   public void renderAnimated(double minX, double minZ, double size, long chunkKey, ChunkConfidence confidence, int alpha, double distance) {
      if (alpha > 0 && confidence != null) {
         ChunkDebugRadarRenderer.OrbitalProfile var13 = orbitalProfileFor(confidence, distance);
         ChunkDebugRadarRenderer.RadarPalette var14 = paletteFor(confidence, alpha);
         ChunkDebugRadarRenderer.MarkerBounds var15 = new ChunkDebugRadarRenderer.MarkerBounds(minX, minZ, minX + size, minZ + size);
         double var16 = var15.centerX();
         double var18 = var15.centerZ();
         double var20 = orbitalPhaseFor(chunkKey, this.visualSeconds, var13.phaseSpeed());
         double var22 = pulseFor(this.visualSeconds);
         drawCornerBrackets(var15, var14);
         drawCenterChunkTacticalField(var16, var18, var14, var22);
         drawBeam(var16, var18, confidence, var14, var22, distance);
         class_310 var24 = class_310.method_1551();
         class_4184 var25 = var24 != null && var24.field_1773 != null ? var24.field_1773.method_19418() : null;
         if (var25 != null && var25.method_19332() && distance < 128.0) {
            this.drawAscendingParticles(var16, var18, confidence, var14, var22, var25.method_19335(), var25.method_19336(), distance);
         }

         if (distance < 384.0) {
            if (var13.ringCount() > 0) {
               drawRings(var16, var18, var13, var14, var20);
            }

            if (var13.drawSweep()) {
               drawSweep(var16, var18, var14, var20, var22);
            }

            if (var13.drawBeacons()) {
               drawCornerBeacons(var15, var14, var20);
            }

            if (var13.drawLockRing()) {
               drawLockRing(var16, var18, var14, var20, var22);
               drawLockCrosshair(var16, var18, var14, var22);
            }
         }
      }
   }

   public void drawTracer(long chunkKey, ChunkConfidence confidence, int alpha, float tickDelta) {
      ChunkDebugRadarRenderer.MarkerBounds var6 = markerBoundsFor(chunkKey);
      this.drawTracer(var6.centerX(), var6.centerZ(), confidence, alpha, tickDelta);
   }

   public void drawTracer(double targetX, double targetZ, ChunkConfidence confidence, int alpha, float tickDelta) {
      if (alpha > 0 && confidence != null) {
         RenderUtil.drawTracerToPos(targetX, tracerTargetY(), targetZ, paletteFor(confidence, alpha).accent(), tickDelta);
      }
   }

   public static ChunkDebugRadarRenderer.MarkerBounds markerBoundsFor(long chunkKey) {
      int var2 = (int)chunkKey;
      int var3 = (int)(chunkKey >>> (32));
      double var4 = (var2 - 1) * 16.0;
      double var6 = (var3 - 1) * 16.0;
      return new ChunkDebugRadarRenderer.MarkerBounds(var4, var6, var4 + 48.0, var6 + 48.0);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static double beamHeightFor(ChunkConfidence confidence) {
      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> 4.0;
         case 2 -> 8.0;
         case 3 -> 16.0;
         default -> throw new MatchException(null, null);
      };
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static ChunkDebugRadarRenderer.OrbitalProfile orbitalProfileFor(ChunkConfidence confidence, double distance) {
      ChunkDebugRadarRenderer.TracerStyle var3 = tracerStyleFor(confidence);
      if (distance < 256.0) {
         return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
            case 1 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 16, false, false, false, 0.0, var3);
            case 2 -> new ChunkDebugRadarRenderer.OrbitalProfile(2, 32, true, true, false, 1.0, var3);
            case 3 -> new ChunkDebugRadarRenderer.OrbitalProfile(3, 40, true, true, true, 1.45, var3);
            default -> throw new MatchException(null, null);
         };
      } else if (distance < 768.0) {
         return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
            case 1 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 10, false, false, false, 0.0, var3);
            case 2 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 16, true, false, false, 1.0, var3);
            case 3 -> new ChunkDebugRadarRenderer.OrbitalProfile(2, 20, true, false, true, 1.45, var3);
            default -> throw new MatchException(null, null);
         };
      } else {
         return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
            case 1 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 8, false, false, false, 0.0, var3);
            case 2 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 8, false, false, false, 1.0, var3);
            case 3 -> new ChunkDebugRadarRenderer.OrbitalProfile(1, 8, false, false, true, 1.45, var3);
            default -> throw new MatchException(null, null);
         };
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static ChunkDebugRadarRenderer.TracerStyle tracerStyleFor(ChunkConfidence confidence) {
      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> ChunkDebugRadarRenderer.TracerStyle.SINGLE;
         case 2 -> ChunkDebugRadarRenderer.TracerStyle.PULSE;
         case 3 -> ChunkDebugRadarRenderer.TracerStyle.PAIRED;
         default -> throw new MatchException(null, null);
      };
   }

   static double tracerTargetY() {
      return 63.0;
   }

   static int tracerLineCount(ChunkDebugRadarRenderer.TracerStyle style) {
      return style == ChunkDebugRadarRenderer.TracerStyle.PAIRED ? 2 : 1;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static ChunkDebugRadarRenderer.RadarPalette paletteFor(ChunkConfidence confidence, int alpha) {
      int var2 = Math.max(0, Math.min(255, alpha));
      if (confidence != null && var2 != 0) {
         int var3 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
            case 1 -> 3594239;
            case 2 -> 16758062;
            case 3 -> 16727375;
            default -> throw new MatchException(null, null);
         };
         return new ChunkDebugRadarRenderer.RadarPalette(
            withAlpha(var3, var2), withAlpha(var3, var2), withAlpha(var3, var2), withAlpha(var3, Math.max(1, Math.round(var2 * 0.65F)))
         );
      } else {
         return new ChunkDebugRadarRenderer.RadarPalette(0, 0, 0, 0);
      }
   }

   static double phaseFor(long chunkKey, double seconds) {
      double var4 = orbitalPhaseFor(chunkKey, seconds, 1.0);
      return var4 - Math.floor(var4 / (Math.PI * 2)) * (Math.PI * 2);
   }

   static double orbitalPhaseFor(long chunkKey, double seconds, double speed) {
      long var6 = chunkKey ^ chunkKey >>> (33) ^ -7046029254386353131L;
      double var8 = (var6 & 65535L) / 65536.0 * (Math.PI * 2);
      return seconds * Math.max(0.0, speed) * 1.35 + var8;
   }

   static double advanceTime(double seconds, float frameTime) {
      return seconds + Math.max(0.0, Math.min(0.05, frameTime));
   }

   static double pulseFor(double seconds) {
      return 0.55 + 0.45 * Math.sin((Math.PI * 2) * seconds / 1.6);
   }

   static int pulseAlpha(int alpha, double pulse) {
      return pulseAlpha(alpha, pulse, 1.0);
   }

   static int pulseAlpha(int alpha, double pulse, double divisor) {
      return (int)Math.max(1L, Math.round(Math.max(0, alpha) * pulse / divisor));
   }

   private static void drawDoubleFrame(ChunkDebugRadarRenderer.MarkerBounds bounds, ChunkDebugRadarRenderer.RadarPalette palette) {
      RenderUtil.drawFlatPlaneBorderThin(bounds.minX(), bounds.minZ(), bounds.maxX(), bounds.maxZ(), 63.01, palette.base());
      RenderUtil.drawFlatPlaneBorderThin(bounds.minX() + 0.9, bounds.minZ() + 0.9, bounds.maxX() - 0.9, bounds.maxZ() - 0.9, 63.025, palette.glow());
   }

   private static void drawCornerBrackets(ChunkDebugRadarRenderer.MarkerBounds bounds, ChunkDebugRadarRenderer.RadarPalette palette) {
      double var2 = 4.25;
      double var4 = 63.05;
      double[] var10000 = new double[2];
      var10000[0] = bounds.minX();
      var10000[1] = bounds.maxX();

      for (double var9 : var10000) {
         double var11 = var9 == bounds.minX() ? 1.0 : -1.0;
         var10000 = new double[2];
         var10000[0] = bounds.minZ();
         var10000[1] = bounds.maxZ();

         for (double var16 : var10000) {
            double var18 = var16 == bounds.minZ() ? 1.0 : -1.0;
            RenderUtil.drawLine(var9, var4, var16, var9 + var11 * var2, var4, var16, palette.accent());
            RenderUtil.drawLine(var9, var4, var16, var9, var4, var16 + var18 * var2, palette.accent());
         }
      }
   }

   private static void drawRings(
      double centerX, double centerZ, ChunkDebugRadarRenderer.OrbitalProfile profile, ChunkDebugRadarRenderer.RadarPalette palette, double phase
   ) {
      for (int var8 = 0; var8 < profile.ringCount(); var8++) {
         int var9 = profile.segmentsPerRing();
         double var10 = 5.0 + var8 * 4.25;
         double var12 = phase * (var8 % (2) == 0 ? 1.0 : -0.7) + var8 * Math.PI * 0.5;
         double var14 = 63.08 + var8 * 0.03;

         for (int var16 = 0; var16 < var9; var16++) {
            double var17 = var12 + (Math.PI * 2) * var16 / var9;
            double var19 = var12 + (Math.PI * 2) * (var16 + 0.72) / var9;
            RenderUtil.drawLine(
               centerX + Math.cos(var17) * var10,
               var14,
               centerZ + Math.sin(var17) * var10,
               centerX + Math.cos(var19) * var10,
               var14,
               centerZ + Math.sin(var19) * var10,
               var8 == 0 ? palette.base() : palette.glow()
            );
         }
      }
   }

   private static void drawCoreGlow(double centerX, double centerZ, ChunkDebugRadarRenderer.RadarPalette palette, double pulse) {
      int var7 = pulseAlpha(palette.glow() >>> (24) & (255), pulse, 1.5);
      RenderUtil.drawFlatPlane(centerX - 1.1, centerZ - 1.1, centerX + 1.1, centerZ + 1.1, 63.035, withAlpha(palette.glow(), var7));
   }

   private static void drawCenterChunkTacticalField(double centerX, double centerZ, ChunkDebugRadarRenderer.RadarPalette palette, double pulse) {
      int var7 = palette.accent() >>> (24) & (255);
      if (var7 > (2)) {
         double var8 = centerX - 8.0;
         double var10 = centerZ - 8.0;
         double var12 = centerX + 8.0;
         double var14 = centerZ + 8.0;
         int var16 = Math.max(1, (int)(var7 * 0.1F * pulse));
         RenderUtil.drawFlatPlane(var8, var10, var12, var14, 63.02, withAlpha(palette.plate(), var16));
         int var17 = Math.max(1, (int)(var7 * 0.32F));
         RenderUtil.drawFlatPlaneBorderThin(var8, var10, var12, var14, 63.025, withAlpha(palette.glow(), var17));
         double var18 = 2.4;
         double var20 = 63.035;
         int var22 = withAlpha(palette.accent(), Math.max(1, (int)(var7 * 0.55F * pulse)));
         double[] var10000 = new double[2];
         var10000[0] = var8;
         var10000[1] = var12;

         for (double var26 : var10000) {
            double var28 = var26 == var8 ? 1.0 : -1.0;
            var10000 = new double[2];
            var10000[0] = var10;
            var10000[1] = var14;

            for (double var33 : var10000) {
               double var35 = var33 == var10 ? 1.0 : -1.0;
               RenderUtil.drawLine(var26, var20, var33, var26 + var28 * var18, var20, var33, var22);
               RenderUtil.drawLine(var26, var20, var33, var26, var20, var33 + var35 * var18, var22);
            }
         }
      }
   }

   private static void drawBeam(double centerX, double centerZ, ChunkConfidence confidence, ChunkDebugRadarRenderer.RadarPalette palette, double pulse) {
      drawBeam(centerX, centerZ, confidence, palette, pulse, 0.0);
   }

   private static void drawBeam(
      double centerX, double centerZ, ChunkConfidence confidence, ChunkDebugRadarRenderer.RadarPalette palette, double pulse, double distance
   ) {
      int var10 = pulseAlpha(palette.accent() >>> (24) & (255), pulse);
      double var11 = beamHeightFor(confidence);
      int var13 = withAlpha(palette.accent(), var10);
      int var14 = withAlpha(palette.glow(), (int)(var10 * 0.75F));
      RenderUtil.drawLine(centerX, 63.03, centerZ, centerX, 63.0 + var11, centerZ, var13);
      RenderUtil.drawLine(centerX - 0.22, 63.03, centerZ - 0.22, centerX - 0.22, 63.0 + var11 * 0.88, centerZ - 0.22, var14);
      RenderUtil.drawLine(centerX + 0.22, 63.03, centerZ + 0.22, centerX + 0.22, 63.0 + var11 * 0.88, centerZ + 0.22, var14);
      RenderUtil.drawLine(centerX - 0.22, 63.03, centerZ + 0.22, centerX - 0.22, 63.0 + var11 * 0.88, centerZ + 0.22, var14);
      RenderUtil.drawLine(centerX + 0.22, 63.03, centerZ - 0.22, centerX + 0.22, 63.0 + var11 * 0.88, centerZ - 0.22, var14);
      drawCyberAscensionBeam(centerX, centerZ, confidence, palette, pulse, distance);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static void drawCyberAscensionBeam(
      double centerX, double centerZ, ChunkConfidence confidence, ChunkDebugRadarRenderer.RadarPalette palette, double pulse, double distance
   ) {
      double var10 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> 96.0;
         case 2 -> 160.0;
         case 3 -> 256.0;
         default -> throw new MatchException(null, null);
      };
      int var12 = palette.accent() >>> (24) & (255);
      if (var12 > (2)) {
         int var13 = Math.max(1, (int)(var12 * 0.45F * pulse));
         int var14 = withAlpha(palette.accent(), var13);
         double var15 = 0.24;
         RenderUtil.drawFilledBoxAdditive(centerX - var15, 63.03, centerZ - var15, centerX + var15, 63.0 + var10, centerZ + var15, var14);
         int var17 = Math.max(1, (int)(var12 * 0.14F * pulse));
         int var18 = withAlpha(palette.glow(), var17);
         double var19 = 0.75;
         RenderUtil.drawFilledBoxAdditive(centerX - var19, 63.03, centerZ - var19, centerX + var19, 63.0 + var10 * 0.88, centerZ + var19, var18);
      }
   }

   private void drawAscendingParticles(
      double centerX, double centerZ, ChunkConfidence confidence, ChunkDebugRadarRenderer.RadarPalette palette, double pulse, Vector3fc right, Vector3fc up
   ) {
      this.drawAscendingParticles(centerX, centerZ, confidence, palette, pulse, right, up, 0.0);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void drawAscendingParticles(
      double centerX,
      double centerZ,
      ChunkConfidence confidence,
      ChunkDebugRadarRenderer.RadarPalette palette,
      double pulse,
      Vector3fc right,
      Vector3fc up,
      double distance
   ) {
      int var13 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> 10;
         case 2 -> 16;
         case 3 -> 26;
         default -> throw new MatchException(null, null);
      };
      if (distance >= 128.0) {
         var13 = Math.max(4, var13 / 2);
      }
      double var14 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> 18.0;
         case 2 -> 32.0;
         case 3 -> 48.0;
         default -> throw new MatchException(null, null);
      };

      double var16 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$chunkdebug$ChunkConfidence[confidence.ordinal()]) {
         case 1 -> 0.14;
         case 2 -> 0.18;
         case 3 -> 0.24;
         default -> throw new MatchException(null, null);
      };
      int var18 = palette.accent() >>> 24 & 0xFF;

      for (int var19 = 0; var19 < var13; var19++) {
         double var20 = (double)var19 / var13;
         double var22 = (this.visualSeconds * var16 + var20) % 1.0;
         double var24 = 63.0 + var22 * var14;
         double var26 = var19 % 2 == 0 ? 0.0 : Math.PI;
         double var28 = this.visualSeconds * 2.6 + var22 * 12.0 + var26;
         double var30 = 0.55 + 1.8 * Math.sin(var22 * Math.PI);
         double var32 = centerX + Math.cos(var28) * var30;
         double var34 = centerZ + Math.sin(var28) * var30;
         float var36 = (float)Math.sin(var22 * Math.PI);
         int var37 = (int)(var36 * var18);
         if (var37 > 3) {
            int var38 = withAlpha(-1, (int)(var37 * 0.95F));
            int var39 = withAlpha(palette.accent(), (int)(var37 * 0.85F));
            int var40 = withAlpha(palette.glow(), (int)(var37 * 0.9F));
            float var41 = (float)(0.24 + 0.16 * Math.sin(var22 * Math.PI));
            float var42 = (float)(this.visualSeconds * 3.5 + var19);
            float var43 = (float)Math.cos(var42);
            float var44 = (float)Math.sin(var42);
            float var45 = right.x() * var43 - up.x() * var44;
            float var46 = right.y() * var43 - up.y() * var44;
            float var47 = right.z() * var43 - up.z() * var44;
            float var48 = right.x() * var44 + up.x() * var43;
            float var49 = right.y() * var44 + up.y() * var43;
            float var50 = right.z() * var44 + up.z() * var43;
            if (var19 % 2 == 0) {
               float var51 = (float)var32 + var45 * var41;
               float var52 = (float)var24 + var46 * var41;
               float var53 = (float)var34 + var47 * var41;
               float var54 = (float)var32 + var48 * var41;
               float var55 = (float)var24 + var49 * var41;
               float var56 = (float)var34 + var50 * var41;
               float var57 = (float)var32 - var45 * var41;
               float var58 = (float)var24 - var46 * var41;
               float var59 = (float)var34 - var47 * var41;
               float var60 = (float)var32 - var48 * var41;
               float var61 = (float)var24 - var49 * var41;
               float var62 = (float)var34 - var50 * var41;
               float var63 = var41 * 0.3F;
               float var64 = (float)var32 + (var45 + var48) * var63;
               float var65 = (float)var24 + (var46 + var49) * var63;
               float var66 = (float)var34 + (var47 + var50) * var63;
               float var67 = (float)var32 + (-var45 + var48) * var63;
               float var68 = (float)var24 + (-var46 + var49) * var63;
               float var69 = (float)var34 + (-var47 + var50) * var63;
               float var70 = (float)var32 + (-var45 - var48) * var63;
               float var71 = (float)var24 + (-var46 - var49) * var63;
               float var72 = (float)var34 + (-var47 - var50) * var63;
               float var73 = (float)var32 + (var45 - var48) * var63;
               float var74 = (float)var24 + (var46 - var49) * var63;
               float var75 = (float)var34 + (var47 - var50) * var63;
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var51, var52, var53, var39, var64, var65, var66, var40);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var64, var65, var66, var40, var54, var55, var56, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var54, var55, var56, var39, var67, var68, var69, var40);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var67, var68, var69, var40, var57, var58, var59, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var57, var58, var59, var39, var70, var71, var72, var40);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var70, var71, var72, var40, var60, var61, var62, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var60, var61, var62, var39, var73, var74, var75, var40);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var73, var74, var75, var40, var51, var52, var53, var39);
            } else {
               float var76 = var41 * 0.65F;
               float var77 = var41 * 1.15F;
               float var78 = (float)var32 + var45 * var76;
               float var79 = (float)var24 + var46 * var76;
               float var80 = (float)var34 + var47 * var76;
               float var81 = (float)var32 + var48 * var77;
               float var82 = (float)var24 + var49 * var77;
               float var83 = (float)var34 + var50 * var77;
               float var84 = (float)var32 - var45 * var76;
               float var85 = (float)var24 - var46 * var76;
               float var86 = (float)var34 - var47 * var76;
               float var87 = (float)var32 - var48 * var77;
               float var88 = (float)var24 - var49 * var77;
               float var89 = (float)var34 - var50 * var77;
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var78, var79, var80, var39, var81, var82, var83, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var81, var82, var83, var39, var84, var85, var86, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var84, var85, var86, var39, var87, var88, var89, var39);
               RenderUtil.drawTriangleAdditiveGouraud(var32, var24, var34, var38, var87, var88, var89, var39, var78, var79, var80, var39);
            }
         }
      }
   }

   private static void drawLockRing(double centerX, double centerZ, ChunkDebugRadarRenderer.RadarPalette palette, double phase, double pulse) {
      double var9 = 15.0 + 0.55 * pulse;
      double var11 = 63.18;
      int var13 = withAlpha(palette.accent(), pulseAlpha(palette.accent() >>> (24) & (255), pulse, 1.35));

      for (int var14 = 0; var14 < (4); var14++) {
         double var15 = phase * -0.35 + (Math.PI / 4) + var14 * Math.PI * 0.5;
         double var17 = var15 + 1.0053096491487339;
         RenderUtil.drawLine(
            centerX + Math.cos(var15) * var9,
            var11,
            centerZ + Math.sin(var15) * var9,
            centerX + Math.cos(var17) * var9,
            var11,
            centerZ + Math.sin(var17) * var9,
            var13
         );
      }
   }

   private static void drawLockCrosshair(double centerX, double centerZ, ChunkDebugRadarRenderer.RadarPalette palette, double pulse) {
      double var7 = 3.8 + pulse;
      double var9 = 63.2;
      int var11 = withAlpha(palette.accent(), pulseAlpha(palette.accent() >>> (24) & (255), pulse, 1.2));
      RenderUtil.drawLine(centerX - var7, var9, centerZ, centerX + var7, var9, centerZ, var11);
      RenderUtil.drawLine(centerX, var9, centerZ - var7, centerX, var9, centerZ + var7, var11);
   }

   private static void drawSweep(double centerX, double centerZ, ChunkDebugRadarRenderer.RadarPalette palette, double phase, double pulse) {
      double var9 = phase - 0.22;
      double var11 = phase + 0.22;
      double var13 = 20.0;
      double var15 = 63.11;
      RenderUtil.drawTriangle(
         centerX,
         var15,
         centerZ,
         centerX + Math.cos(var9) * var13,
         var15,
         centerZ + Math.sin(var9) * var13,
         centerX + Math.cos(var11) * var13,
         var15,
         centerZ + Math.sin(var11) * var13,
         withAlpha(palette.accent(), pulseAlpha(palette.accent() >>> (24) & (255), pulse, 4.0))
      );
      RenderUtil.drawLine(
         centerX,
         var15 + 0.005,
         centerZ,
         centerX + Math.cos(phase) * var13,
         var15 + 0.005,
         centerZ + Math.sin(phase) * var13,
         withAlpha(palette.accent(), pulseAlpha(palette.accent() >>> (24) & (255), pulse))
      );
   }

   private static void drawCornerBeacons(ChunkDebugRadarRenderer.MarkerBounds bounds, ChunkDebugRadarRenderer.RadarPalette palette, double phase) {
      double var4 = 2.2;
      double var6 = 1.25 + 0.5 * (0.5 + 0.5 * Math.sin(phase * 1.6));
      double[] var10000 = new double[2];
      var10000[0] = bounds.minX() + var4;
      var10000[1] = bounds.maxX() - var4;

      for (double var11 : var10000) {
         var10000 = new double[2];
         var10000[0] = bounds.minZ() + var4;
         var10000[1] = bounds.maxZ() - var4;

         for (double var16 : var10000) {
            RenderUtil.drawLine(var11, 63.04, var16, var11, 63.0 + var6, var16, palette.glow());
         }
      }
   }

   private static int withAlpha(int color, int alpha) {
      return Math.max(0, Math.min(255, alpha)) << (24) | color & (16777215);
   }

   public record MarkerBounds(double minX, double minZ, double maxX, double maxZ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public double centerX() {
         return (this.minX + this.maxX) * 0.5;
      }

      public double centerZ() {
         return (this.minZ + this.maxZ) * 0.5;
      }
   }

   public record OrbitalProfile(
      int ringCount,
      int segmentsPerRing,
      boolean drawSweep,
      boolean drawBeacons,
      boolean drawLockRing,
      double phaseSpeed,
      ChunkDebugRadarRenderer.TracerStyle tracerStyle
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record RadarPalette(int base, int accent, int plate, int glow) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum TracerStyle {
      SINGLE,
      PULSE,
      PAIRED;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ChunkDebugRadarRenderer.TracerStyle[] $values() {
         ChunkDebugRadarRenderer.TracerStyle[] var10000 = new ChunkDebugRadarRenderer.TracerStyle[3];
         var10000[0] = SINGLE;
         var10000[1] = PULSE;
         var10000[2] = PAIRED;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-103);
         var10002[4] = (byte)(-77);
         var10002[5] = (byte)(51);
         SINGLE = new ChunkDebugRadarRenderer.TracerStyle();
         var10002 = new byte[5];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-70);
         PULSE = new ChunkDebugRadarRenderer.TracerStyle();
         var10002 = new byte[6];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-70);
         var10002[5] = (byte)(50);
         PAIRED = new ChunkDebugRadarRenderer.TracerStyle();
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
