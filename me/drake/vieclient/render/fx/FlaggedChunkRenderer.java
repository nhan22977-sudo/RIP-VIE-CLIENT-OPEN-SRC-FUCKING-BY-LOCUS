package me.drake.vieclient.render.fx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.modules.donut.chunkdebug.ChunkConfidence;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkEvidenceStore;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.chunkprobe.Confidence;
import me.drake.vieclient.render.nvg.NVGRenderer;
import net.minecraft.class_1923;
import org.lwjgl.nanovg.NanoVG;

public final class FlaggedChunkRenderer {
   private static final double CHUNK_SIZE = 16.0;
   private static final float FADE_IN_SPEED = 3.5F;
   private static final float FADE_OUT_SPEED = 2.8F;
   private final Map visuals = new ConcurrentHashMap();
   private final double[][] scratchQuad;
   private final List screenPoints;
   private final float[] centerScreen;
   private long lastFrameNanos;
   private float outlineWidth;
   private float outlineAlphaMultiplier;
   private int outlineColorRgb;
   private boolean drawCenterBeacon;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FlaggedChunkRenderer() {
      double[][] var10001 = new double[4][];
      var10001[0] = new double[3];
      var10001[1] = new double[3];
      var10001[2] = new double[3];
      var10001[3] = new double[3];
      this.scratchQuad = var10001;
      this.screenPoints = new ArrayList(8);
      this.centerScreen = new float[3];
      this.lastFrameNanos = 0L;
      this.outlineWidth = 0.5F;
      this.outlineAlphaMultiplier = 1.0F;
      this.outlineColorRgb = -1;
      this.drawCenterBeacon = false;
   }

   public float getOutlineWidth() {
      return this.outlineWidth;
   }

   public void setOutlineWidth(float outlineWidth) {
      this.outlineWidth = Math.max(0.1F, outlineWidthx);
   }

   public float getOutlineAlphaMultiplier() {
      return this.outlineAlphaMultiplier;
   }

   public void setOutlineAlphaMultiplier(float outlineAlphaMultiplier) {
      this.outlineAlphaMultiplier = Math.max(0.0F, Math.min(1.0F, outlineAlphaMultiplierx));
   }

   public int getOutlineColorRgb() {
      return this.outlineColorRgb;
   }

   public void setOutlineColorRgb(int outlineColorRgb) {
      this.outlineColorRgb = outlineColorRgbx;
   }

   public void syncPositions(Collection positions, int colorRgb, int alphaByte, double y) {
      if (positions != null && !positions.isEmpty()) {
         HashSet var6 = new HashSet(positions.size());

         for (class_1923 var8 : positions) {
            if (var8 != null) {
               long var9 = ChunkKey.pack(var8.field_9181, var8.field_9180);
               var6.add(var9);
               double var11 = var8.field_9181 * 16.0;
               double var13 = var8.field_9180 * 16.0;
               FlaggedChunkRenderer.FlaggedChunkVisual var15 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var9);
               if (var15 != null) {
                  var15.setTarget(var9, var11, var13, y, 16.0, colorRgb, alphaByte);
               } else {
                  this.visuals.put(var9, new FlaggedChunkRenderer.FlaggedChunkVisual(var9, var11, var13, y, 16.0, colorRgb, alphaByte, true));
               }
            }
         }

         for (Entry var17 : this.visuals.entrySet()) {
            if (!var6.contains(var17.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var17.getValue()).active = false;
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   public void syncKeys(Collection chunkKeys, int colorRgb, int alphaByte, double y) {
      if (chunkKeys != null && !chunkKeys.isEmpty()) {
         Set var6 = chunkKeys instanceof Set ? (Set)chunkKeys : new HashSet(chunkKeys);

         for (Entry var8 : this.visuals.entrySet()) {
            if (!var6.contains(var8.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var8.getValue()).active = false;
            }
         }

         for (long var18 : var6) {
            int var10 = ChunkKey.unpackX(var18);
            int var11 = ChunkKey.unpackZ(var18);
            double var12 = var10 * 16.0;
            double var14 = var11 * 16.0;
            FlaggedChunkRenderer.FlaggedChunkVisual var16 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var18);
            if (var16 != null) {
               var16.setTarget(var18, var12, var14, y, 16.0, colorRgb, alphaByte);
            } else {
               this.visuals.put(var18, new FlaggedChunkRenderer.FlaggedChunkVisual(var18, var12, var14, y, 16.0, colorRgb, alphaByte, true));
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   public void syncColoredKeys(Map chunkColors, int alphaByte, double y) {
      if (chunkColors != null && !chunkColors.isEmpty()) {
         Set var5 = chunkColors.keySet();

         for (Entry var7 : this.visuals.entrySet()) {
            if (!var5.contains(var7.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var7.getValue()).active = false;
            }
         }

         for (Entry var19 : chunkColors.entrySet()) {
            long var8 = (Long)var19.getKey();
            int var10 = (Integer)var19.getValue();
            int var11 = ChunkKey.unpackX(var8);
            int var12 = ChunkKey.unpackZ(var8);
            double var13 = var11 * 16.0;
            double var15 = var12 * 16.0;
            FlaggedChunkRenderer.FlaggedChunkVisual var17 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var8);
            if (var17 != null) {
               var17.setTarget(var8, var13, var15, y, 16.0, var10, alphaByte);
            } else {
               this.visuals.put(var8, new FlaggedChunkRenderer.FlaggedChunkVisual(var8, var13, var15, y, 16.0, var10, alphaByte, true));
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   public void syncZones(Collection centerKeys, int colorRgb, int alphaByte, double y, double sizeBlocks) {
      if (centerKeys != null && !centerKeys.isEmpty()) {
         Set var8 = centerKeys instanceof Set ? (Set)centerKeys : new HashSet(centerKeys);

         for (Entry var10 : this.visuals.entrySet()) {
            if (!var8.contains(var10.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var10.getValue()).active = false;
            }
         }

         for (long var20 : var8) {
            int var12 = ChunkKey.unpackX(var20);
            int var13 = ChunkKey.unpackZ(var20);
            double var14 = (var12 - 1) * 16.0;
            double var16 = (var13 - 1) * 16.0;
            FlaggedChunkRenderer.FlaggedChunkVisual var18 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var20);
            if (var18 != null) {
               var18.setTarget(var20, var14, var16, y, sizeBlocks, colorRgb, alphaByte);
            } else {
               this.visuals.put(var20, new FlaggedChunkRenderer.FlaggedChunkVisual(var20, var14, var16, y, sizeBlocks, colorRgb, alphaByte, true));
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   public void syncAlerts(Map alerts, int alphaByte, double y, double sizeBlocks) {
      if (alerts != null && !alerts.isEmpty()) {
         Set var7 = alerts.keySet();

         for (Entry var9 : this.visuals.entrySet()) {
            if (!var7.contains(var9.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var9.getValue()).active = false;
            }
         }

         for (Entry var22 : alerts.entrySet()) {
            long var10 = (Long)var22.getKey();
            ChunkConfidence var12 = (ChunkConfidence)var22.getValue();
            int var13 = ChunkKey.unpackX(var10);
            int var14 = ChunkKey.unpackZ(var10);
            double var15 = var13 * 16.0;
            double var17 = var14 * 16.0;
            int var19 = var12 == ChunkConfidence.HIGH ? 16727375 : (var12 == ChunkConfidence.MEDIUM ? 16758062 : 3594239);
            FlaggedChunkRenderer.FlaggedChunkVisual var20 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var10);
            if (var20 != null) {
               var20.setTarget(var10, var15, var17, y, sizeBlocks, var19, alphaByte);
            } else {
               this.visuals.put(var10, new FlaggedChunkRenderer.FlaggedChunkVisual(var10, var15, var17, y, sizeBlocks, var19, alphaByte, true));
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   public void syncEvidence(Collection evidence, int possibleColor, int highColor, int alphaByte, double y) {
      if (evidence != null && !evidence.isEmpty()) {
         HashSet var7 = new HashSet(evidence.size());

         for (ChunkEvidenceStore.ChunkEvidence var9 : evidence) {
            if (var9 != null) {
               var7.add(var9.chunkKey());
            }
         }

         for (Entry var22 : this.visuals.entrySet()) {
            if (!var7.contains(var22.getKey())) {
               ((FlaggedChunkRenderer.FlaggedChunkVisual)var22.getValue()).active = false;
            }
         }

         for (ChunkEvidenceStore.ChunkEvidence var23 : evidence) {
            if (var23 != null) {
               long var10 = var23.chunkKey();
               int var12 = ChunkKey.unpackX(var10);
               int var13 = ChunkKey.unpackZ(var10);
               double var14 = var12 * 16.0;
               double var16 = var13 * 16.0;
               int var18 = var23.confidence() == Confidence.HIGH ? highColor : possibleColor;
               FlaggedChunkRenderer.FlaggedChunkVisual var19 = (FlaggedChunkRenderer.FlaggedChunkVisual)this.visuals.get(var10);
               if (var19 != null) {
                  var19.setTarget(var10, var14, var16, y, 16.0, var18, alphaByte);
               } else {
                  this.visuals.put(var10, new FlaggedChunkRenderer.FlaggedChunkVisual(var10, var14, var16, y, 16.0, var18, alphaByte, true));
               }
            }
         }
      } else {
         this.markAllInactive();
      }
   }

   private void markAllInactive() {
      for (FlaggedChunkRenderer.FlaggedChunkVisual var2 : this.visuals.values()) {
         var2.active = false;
      }
   }

   public void renderNanoVG(WorldProjector projector) {
      if (projector != null && projector.isValid() && !this.visuals.isEmpty()) {
         long var2 = System.nanoTime();
         double var4 = this.lastFrameNanos > 0L ? (var2 - this.lastFrameNanos) / 1.0E9 : 0.016;
         if (var4 > 0.1) {
            var4 = 0.1;
         }

         this.lastFrameNanos = var2;

         for (FlaggedChunkRenderer.FlaggedChunkVisual var7 : this.visuals.values()) {
            if (var7.active) {
               var7.alpha = Math.min(1.0F, var7.alpha + (float)(var4 * 3.5));
            } else {
               var7.alpha = Math.max(0.0F, var7.alpha - (float)(var4 * 2.8F));
            }
         }

         this.visuals.values().removeIf(v -> !v.active && v.alpha <= 0.005F);
         long var46 = NVGRenderer.getContext();
         if (var46 != 0L) {
            double var8 = 147456.0;
            Iterator var10 = this.visuals.values().iterator();

            while (true) {
               FlaggedChunkRenderer.FlaggedChunkVisual var11;
               double var12;
               double var14;
               double var16;
               double var18;
               double var20;
               double var22;
               double var24;
               while (true) {
                  if (!var10.hasNext()) {
                     return;
                  }

                  var11 = (FlaggedChunkRenderer.FlaggedChunkVisual)var10.next();
                  if (!(var11.alpha <= 0.005F)) {
                     var12 = var11.currentX;
                     var14 = var11.currentZ;
                     var16 = var12 + var11.sizeBlocks;
                     var18 = var14 + var11.sizeBlocks;
                     var20 = var11.y;
                     var22 = (var12 + var16) * 0.5;
                     var24 = (var14 + var18) * 0.5;
                     if (!(projector.distanceSqTo(var22, var20, var24) > var8)) {
                        double var26 = var11.sizeBlocks * 0.75;
                        double var28 = projector.getCameraDepth(var22, var20, var24);
                        if (!(var28 < -var26)) {
                           if (!(var28 > 0.0) || !projector.project(var22, var20, var24, this.centerScreen)) {
                              break;
                           }

                           float var30 = Math.max(160.0F, (float)(var11.sizeBlocks * 2.0 / var28 * 800.0));
                           if (projector.onScreen(this.centerScreen, var30)) {
                              break;
                           }
                        }
                     }
                  }
               }

               this.scratchQuad[0][0] = var12;
               this.scratchQuad[0][1] = var20;
               this.scratchQuad[0][2] = var14;
               this.scratchQuad[1][0] = var16;
               this.scratchQuad[1][1] = var20;
               this.scratchQuad[1][2] = var14;
               this.scratchQuad[2][0] = var16;
               this.scratchQuad[2][1] = var20;
               this.scratchQuad[2][2] = var18;
               this.scratchQuad[3][0] = var12;
               this.scratchQuad[3][1] = var20;
               this.scratchQuad[3][2] = var18;
               if (projector.projectPolygon(this.scratchQuad, this.screenPoints) && this.screenPoints.size() >= 3) {
                  int var47 = var11.colorRgb;
                  float var31 = (var47 >> 16 & 0xFF) / 255.0F;
                  float var32 = (var47 >> 8 & 0xFF) / 255.0F;
                  float var33 = (var47 & 0xFF) / 255.0F;
                  int var34 = this.outlineColorRgb != -1 ? this.outlineColorRgb : var47;
                  float var35 = (var34 >> 16 & 0xFF) / 255.0F;
                  float var36 = (var34 >> 8 & 0xFF) / 255.0F;
                  float var37 = (var34 & 0xFF) / 255.0F;
                  float var38 = var11.alpha * (Math.max(5, Math.min(255, var11.baseAlpha)) / 255.0F);
                  float var39 = Math.max(0.0F, Math.min(1.0F, var38 * 135.0F / 255.0F));
                  float var40 = Math.max(5, Math.min(255, var11.baseAlpha)) / 255.0F;
                  float var41 = Math.max(0.0F, Math.min(1.0F, var11.alpha * 105.0F * this.outlineAlphaMultiplier * (0.35F + 0.65F * var40) / 255.0F));
                  float var42 = Math.max(0.0F, Math.min(1.0F, var11.alpha * 16.0F * this.outlineAlphaMultiplier * (0.35F + 0.65F * var40) / 255.0F));
                  NanoVG.nvgBeginPath(var46);
                  float[] var43 = (float[])this.screenPoints.get(0);
                  NanoVG.nvgMoveTo(var46, var43[0], var43[1]);

                  for (int var44 = 1; var44 < this.screenPoints.size(); var44++) {
                     float[] var45 = (float[])this.screenPoints.get(var44);
                     NanoVG.nvgLineTo(var46, var45[0], var45[1]);
                  }

                  NanoVG.nvgClosePath(var46);
                  if (var39 > 0.005F) {
                     NanoVG.nvgRGBAf(var31, var32, var33, var39, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgFillColor(var46, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgFill(var46);
                  }

                  if (var42 > 0.005F) {
                     NanoVG.nvgRGBAf(var35, var36, var37, var42, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeColor(var46, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeWidth(var46, this.outlineWidth * 1.6F);
                     NanoVG.nvgStroke(var46);
                  }

                  if (var41 > 0.005F) {
                     NanoVG.nvgRGBAf(var35, var36, var37, var41, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeColor(var46, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeWidth(var46, this.outlineWidth);
                     NanoVG.nvgStroke(var46);
                  }

                  if (this.drawCenterBeacon && projector.project(var22, var20 + 0.05, var24, this.centerScreen) && projector.onScreen(this.centerScreen, 32.0F)
                     )
                   {
                     float var48 = (float)(0.85 + 0.15 * Math.sin(System.currentTimeMillis() * 0.006));
                     NanoVG.nvgBeginPath(var46);
                     NanoVG.nvgCircle(var46, this.centerScreen[0], this.centerScreen[1], 2.8F * var48);
                     NanoVG.nvgRGBAf(var31, var32, var33, var41, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgFillColor(var46, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgFill(var46);
                     NanoVG.nvgBeginPath(var46);
                     NanoVG.nvgCircle(var46, this.centerScreen[0], this.centerScreen[1], 6.0F * var48);
                     NanoVG.nvgRGBAf(var31, var32, var33, var42, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeColor(var46, NVGRenderer.NVG_COLOR_1);
                     NanoVG.nvgStrokeWidth(var46, 1.2F);
                     NanoVG.nvgStroke(var46);
                  }
               }
            }
         }
      }
   }

   public boolean isDrawCenterBeacon() {
      return this.drawCenterBeacon;
   }

   public void setDrawCenterBeacon(boolean drawCenterBeacon) {
      this.drawCenterBeacon = drawCenterBeaconx;
   }

   public Collection getVisuals() {
      return Collections.unmodifiableCollection(this.visuals.values());
   }

   public void clear() {
      this.visuals.clear();
      this.lastFrameNanos = 0L;
   }

   public static final class FlaggedChunkVisual {
      private long id;
      private double targetX;
      private double targetZ;
      private double currentX;
      private double currentZ;
      private double y;
      private double sizeBlocks;
      private float alpha;
      private int colorRgb;
      private int baseAlpha;
      private boolean active;
      private long lastActiveTimeMs;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public FlaggedChunkVisual(long id, double x, double z, double y, double sizeBlocks, int colorRgb, int baseAlpha, boolean startFaded) {
         this.id = idx;
         this.targetX = x;
         this.targetZ = z;
         this.currentX = x;
         this.currentZ = z;
         this.y = yx;
         this.sizeBlocks = sizeBlocksx;
         this.colorRgb = colorRgbx;
         this.baseAlpha = baseAlphax;
         this.alpha = startFaded ? 0.0F : 1.0F;
         this.active = true;
         this.lastActiveTimeMs = System.currentTimeMillis();
      }

      public long getId() {
         return this.id;
      }

      public double getCurrentX() {
         return this.currentX;
      }

      public double getCurrentZ() {
         return this.currentZ;
      }

      public double getTargetX() {
         return this.targetX;
      }

      public double getTargetZ() {
         return this.targetZ;
      }

      public double getY() {
         return this.y;
      }

      public double getSizeBlocks() {
         return this.sizeBlocks;
      }

      public float getAlpha() {
         return this.alpha;
      }

      public int getColorRgb() {
         return this.colorRgb;
      }

      public int getBaseAlpha() {
         return this.baseAlpha;
      }

      public boolean isActive() {
         return this.active;
      }

      public void setTarget(long id, double x, double z, double y, double sizeBlocks, int colorRgb, int baseAlpha) {
         this.id = idx;
         this.targetX = x;
         this.targetZ = z;
         this.currentX = x;
         this.currentZ = z;
         this.y = yx;
         this.sizeBlocks = sizeBlocksx;
         this.colorRgb = colorRgbx;
         this.baseAlpha = baseAlphax;
         this.active = true;
         this.lastActiveTimeMs = System.currentTimeMillis();
      }
   }
}
