package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4184;

public class JumpCircle extends Module {
   private final ColorSetting color = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Color",
         "Ring color",
         -1
      )
   );
   private final DoubleSetting duration = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Duration",
         "How long each ring lasts (ticks)",
         10.0,
         10.0,
         100.0,
         1.0
      )
   );
   private final DoubleSetting maxRadius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Max Radius",
         "Radius the ring grows to",
         1.0,
         0.5,
         6.0,
         0.1
      )
   );
   private final DoubleSetting thickness = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Thickness",
         "Ring line thickness",
         2.0,
         0.5,
         5.0,
         0.1
      )
   );
   private final DoubleSetting segments = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Segments",
         "Ring smoothness segments",
         48.0,
         12.0,
         96.0,
         1.0
      )
   );
   private final BoolSetting fade = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Fade Out",
         "Fade the ring out over time",
         true
      )
   );
   private final BoolSetting neonGlow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Neon Glow",
         "Neon glow styling",
         true
      )
   );
   private final BoolSetting dashed = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dashed",
         "Dashed ring segments",
         false
      )
   );
   private final DoubleSetting rotateSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Rotate Speed",
         "Ring rotation speed",
         60.0,
         -360.0,
         360.0,
         5.0
      )
   );
   private final DoubleSetting wave = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Wave",
         "Flower/morph edge amount",
         0.0,
         0.0,
         1.0,
         0.05
      )
   );
   private final DoubleSetting petals = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Petals",
         "Wave petal count",
         6.0,
         2.0,
         12.0,
         1.0
      )
   );
   private final BoolSetting rainbow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Rainbow",
         "Rainbow ring colors",
         false
      )
   );
   private final BoolSetting innerRing = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Inner Ring",
         "Counter-rotating inner ring",
         true
      )
   );
   private final BoolSetting glowDisc = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Glow Disc",
         "Soft glow disc under the ring",
         true
      )
   );
   private final DoubleSetting discAlpha = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Disc Alpha",
         "Glow disc opacity",
         55.0,
         0.0,
         255.0,
         1.0
      )
   );
   private final BoolSetting particles = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Particles",
         "Outward sparks",
         true
      )
   );
   private final DoubleSetting particleCount = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Particle Count",
         "Number of sparks",
         14.0,
         0.0,
         40.0,
         1.0
      )
   );
   private final BoolSetting onLand = (BoolSetting)this.addSetting(
      new BoolSetting(
         "On Land",
         "Shockwave when landing",
         true
      )
   );
   private final BoolSetting showText = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Text",
         "Show the logo billboard",
         true
      )
   );
   private final DoubleSetting textSize = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Text Size",
         "Billboard text size",
         20.0,
         8.0,
         48.0,
         1.0
      )
   );
   private final DoubleSetting textHeight = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Text Height",
         "Billboard height above ground",
         1.0,
         0.0,
         3.0,
         0.1
      )
   );
   private static final String TEXT_A = "vieclient";
   private static final String TEXT_B = ".cc";
   private static final int MAX_CIRCLES = 32;
   private static final float P_GRAVITY = 14.0F;
   private final List circles = new ArrayList();
   private boolean wasOnGround = true;
   private final JumpCircle.CamProj camProj = new JumpCircle.CamProj();
   private final double[] scratchPrev = new double[2];
   private final double[] scratchCur = new double[2];
   private final double[] scratchCenter = new double[3];
   private final double[] scratchP1 = new double[2];
   private final double[] scratchP2 = new double[2];
   private final double[] scratch3D = new double[3];
   private int fbW;
   private int fbH;
   private double scale;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public JumpCircle() {
      super(
         "Jump Circle",
         "Renders an expanding ring on the ground when you jump",
         Category.CUSTOMIZE
      );
      this.fade.setVisibleInGui(false);
      this.segments.setVisibleInGui(false);
      this.discAlpha.visibleWhen(this.glowDisc::get);
      this.particleCount.visibleWhen(this.particles::get);
      this.petals.visibleWhen(() -> (Double)this.wave.get() > 0.0);
      this.textSize.visibleWhen(this.showText::get);
      this.textHeight.visibleWhen(this.showText::get);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.circles.clear();
      this.wasOnGround = true;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc != null && mc.field_1724 != null) {
            boolean var2 = mc.field_1724.method_24828();
            if (!var2 && this.wasOnGround && mc.field_1724.method_18798().field_1351 > 0.0) {
               this.spawn(
                  mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), (long)((Double)this.duration.get() * 50.0), false
               );
            }

            if (var2 && !this.wasOnGround && (Boolean)this.onLand.get()) {
               this.spawn(
                  mc.field_1724.method_23317(),
                  mc.field_1724.method_23318(),
                  mc.field_1724.method_23321(),
                  (long)((Double)this.duration.get() * 50.0 * 0.6),
                  true
               );
            }

            this.wasOnGround = var2;
            this.circles.removeIf(c -> c.expired());
         }
      }
   }

   private void spawn(double x, double y, double z, long totalMs, boolean land) {
      if (this.circles.size() >= (32)) {
         this.circles.remove(0);
      }

      int var10 = this.particles.get() ? (int)((Double)this.particleCount.get()).doubleValue() : 0;
      this.circles.add(new JumpCircle.Circle(new class_243(x, y, z), totalMs, land, var10));
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc != null && mc.field_1724 != null && mc.field_1687 != null) {
         if (!this.circles.isEmpty() && this.getCamera() != null) {
            this.fbW = mc.method_22683().method_4489();
            this.fbH = mc.method_22683().method_4506();
            this.scale = mc.method_22683().method_4495();

            try (NanoVGFrame var2 = NanoVGRenderer.frame()) {
               if (!var2.isActive()) {
                  return;
               }

               this.updateCamProj(this.fbW, this.fbH);
               int var3 = (Integer)this.color.get();
               float var4 = (float)((Double)this.thickness.get()).doubleValue();
               int var5 = (int)((Double)this.segments.get()).doubleValue();
               boolean var6 = (Boolean)this.neonGlow.get();
               boolean var7 = (Boolean)this.dashed.get();
               boolean var8 = (Boolean)this.rainbow.get();
               float var9 = (float)((Double)this.wave.get()).doubleValue();
               int var10 = (int)((Double)this.petals.get()).doubleValue();
               double var11 = Math.toRadians((Double)this.rotateSpeed.get());

               for (JumpCircle.Circle var14 : this.circles) {
                  float var15 = var14.progress();
                  float var16 = var14.land ? easeOutExpo(var15) : easeOutCubic(var15);
                  float var17 = var16 * (float)((Double)this.maxRadius.get()).doubleValue() * (var14.land ? 1.25F : 1.0F);
                  float var18 = this.fade.get() ? 1.0F - var15 : 1.0F;
                  if (var14.land && var15 < 0.12F) {
                     var18 = Math.min(1.0F, var18 + 0.4F);
                  }

                  float var19 = (float)(var11 * var14.elapsedSec());
                  float var20 = var14.land ? var4 * 1.3F : var4;
                  if ((Boolean)this.glowDisc.get() && (int)((Double)this.discAlpha.get()).doubleValue() > 0) {
                     int var21 = clamp((int)(var18 * (Double)this.discAlpha.get()));
                     int var22 = var21 << 24 | var3 & 16777215;
                     this.drawDisc(var14, var17, var19, var9, var10, var5, var22);
                  }

                  this.drawRing(var14, var17, var19, var9, var10, var5, var18, var3, var8, var7, var6, var20);
                  if ((Boolean)this.innerRing.get()) {
                     this.drawRing(var14, var17 * 0.55F, -var19 * 1.6F, var9, var10, var5, var18 * 0.8F, var3, var8, var7, var6, var20 * 0.8F);
                  }

                  if ((Boolean)this.particles.get()) {
                     this.drawParticles(var14, var18, var3, var6);
                  }

                  if ((Boolean)this.showText.get() && !var14.land) {
                     this.drawLabel(var14, var15, var18);
                  }
               }
            }
         }
      }
   }

   private void drawRing(
      JumpCircle.Circle c,
      float radius,
      float rot,
      float waveAmt,
      int petalN,
      int segs,
      float a,
      int baseArgb,
      boolean rain,
      boolean dash,
      boolean neon,
      float th
   ) {
      double var13 = (Math.PI * 2) / segs;
      float var15 = c.elapsedSec();
      boolean var16 = this.ringPoint(c, 0, var13, radius, rot, waveAmt, petalN, var15, this.scratchPrev);
      int var17 = baseArgb >>> (24) & (255);
      int var18 = baseArgb & (16777215);
      float var19 = this.cameraDistanceTo(c.pos);
      float var20 = this.getDistanceScale(var19, 4.0F);
      float var21 = Math.max(0.5F, th * var20);

      for (int var22 = 1; var22 <= segs; var22++) {
         boolean var23 = this.ringPoint(c, var22, var13, radius, rot, waveAmt, petalN, var15, this.scratchCur);
         if ((!dash || var22 % (2) != 0) && var16 && var23) {
            int var24 = rain ? hsbArgb((float)var22 / segs + var15 * 0.2F, clamp((int)(a * 255.0F))) : clamp((int)(a * var17)) << (24) | var18;
            float var25 = (float)(this.scratchPrev[0] / this.scale);
            float var26 = (float)(this.scratchPrev[1] / this.scale);
            float var27 = (float)(this.scratchCur[0] / this.scale);
            float var28 = (float)(this.scratchCur[1] / this.scale);
            if (neon) {
               NanoVGRenderer.drawEspTracer(var25, var26, var27, var28, var21, var24);
            } else {
               NanoVGRenderer.drawLine(var25, var26, var27, var28, var21, var24);
            }
         }

         this.scratchPrev[0] = this.scratchCur[0];
         this.scratchPrev[1] = this.scratchCur[1];
         var16 = var23;
      }
   }

   private void drawDisc(JumpCircle.Circle c, float radius, float rot, float waveAmt, int petalN, int segs, int col) {
      double var8 = (Math.PI * 2) / segs;
      float var10 = c.elapsedSec();
      if (this.worldToScreen(c.pos.field_1352, c.pos.field_1351 + 0.02, c.pos.field_1350, this.scratchCenter)) {
         float var11 = (float)(this.scratchCenter[0] / this.scale);
         float var12 = (float)(this.scratchCenter[1] / this.scale);
         NanoVGRenderer.beginFillBatch(col);
         boolean var13 = this.ringPoint(c, 0, var8, radius, rot, waveAmt, petalN, var10, this.scratchPrev);

         for (int var14 = 1; var14 <= segs; var14++) {
            boolean var15 = this.ringPoint(c, var14, var8, radius, rot, waveAmt, petalN, var10, this.scratchCur);
            if (var13 && var15) {
               NanoVGRenderer.batchTri(
                  var11,
                  var12,
                  (float)(this.scratchPrev[0] / this.scale),
                  (float)(this.scratchPrev[1] / this.scale),
                  (float)(this.scratchCur[0] / this.scale),
                  (float)(this.scratchCur[1] / this.scale)
               );
            }

            this.scratchPrev[0] = this.scratchCur[0];
            this.scratchPrev[1] = this.scratchCur[1];
            var13 = var15;
         }

         NanoVGRenderer.endFillBatch();
      }
   }

   private boolean ringPoint(JumpCircle.Circle c, int i, double step, float radius, float rot, float waveAmt, int petalN, float phase, double[] out) {
      double var11 = i * step + rot;
      double var13 = radius * (1.0 + waveAmt * Math.sin(var11 * petalN + phase * 3.0));
      return this.worldToScreen(c.pos.field_1352 + Math.cos(var11) * var13, c.pos.field_1351 + 0.02, c.pos.field_1350 + Math.sin(var11) * var13, out);
   }

   private void drawParticles(JumpCircle.Circle c, float a, int baseArgb, boolean neon) {
      float var5 = c.elapsedSec();
      int var6 = clamp((int)(a * 255.0F));
      int var7 = var6 << (24) | baseArgb & (16777215);
      float var8 = this.cameraDistanceTo(c.pos);
      float var9 = this.getDistanceScale(var8, 4.0F);
      float var10 = Math.max(0.5F, 2.0F * var9);
      float var11 = Math.max(0.5F, 1.6F * var9);

      for (int var12 = 0; var12 < c.pCount; var12++) {
         this.particlePos(c, var12, var5, this.scratch3D);
         if (this.worldToScreen(this.scratch3D[0], this.scratch3D[1], this.scratch3D[2], this.scratchP2)) {
            float var13 = (float)(this.scratchP2[0] / this.scale);
            float var14 = (float)(this.scratchP2[1] / this.scale);
            this.particlePos(c, var12, Math.max(0.0F, var5 - 0.06F), this.scratch3D);
            if (this.worldToScreen(this.scratch3D[0], this.scratch3D[1], this.scratch3D[2], this.scratchP1)) {
               float var15 = (float)(this.scratchP1[0] / this.scale);
               float var16 = (float)(this.scratchP1[1] / this.scale);
               if (neon) {
                  NanoVGRenderer.drawEspTracer(var15, var16, var13, var14, var10, var7);
               } else {
                  NanoVGRenderer.drawLine(var15, var16, var13, var14, var10, var7);
               }
            } else {
               float var20 = TrueStretched.getStretchFactor();
               boolean var21 = Math.abs(var20 - 1.0F) > 0.001F;
               if (var21) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var13, var14);
                  NanoVGRenderer.scale(var20, 1.0F);
                  NanoVGRenderer.translate(-var13, -var14);
               }

               try {
                  NanoVGRenderer.drawCircle(var13, var14, var11, var7);
               } finally {
                  if (var21) {
                     NanoVGRenderer.restore();
                  }
               }
            }
         }
      }
   }

   private void particlePos(JumpCircle.Circle c, int i, float t, double[] out) {
      float var5 = c.pSpeed[i] * t;
      float var6 = c.pUp[i] * t - 7.0F * t * t;
      out[0] = c.pos.field_1352 + Math.cos(c.pAngle[i]) * var5;
      out[1] = c.pos.field_1351 + 0.05 + var6;
      out[2] = c.pos.field_1350 + Math.sin(c.pAngle[i]) * var5;
   }

   private void drawLabel(JumpCircle.Circle c, float progress, float a) {
      if (this.worldToScreen(c.pos.field_1352, c.pos.field_1351 + (Double)this.textHeight.get(), c.pos.field_1350, this.scratchCenter)) {
         float var4 = this.getDistanceScale(this.scratchCenter[2], 4.0F);
         float var5 = Math.max(3.5F, (float)((Double)this.textSize.get()).doubleValue() * (0.7F + 0.3F * easeOutCubic(Math.min(1.0F, progress * 3.0F))) * var4);
         int var6 = clamp((int)(a * 255.0F));
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(125);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(121);
         var10000[3] = (byte)(-67);
         var10000[4] = (byte)(77);
         var10000[5] = (byte)(83);
         var10000[6] = (byte)(107);
         var10000[7] = (byte)(101);
         var10000[8] = (byte)(23);
         float var7 = NanoVGRenderer.getTextWidth($$v_s(var10000, 2525926308759982126L), var5);
         var10000 = new byte[3];
         var10000[0] = (byte)(37);
         var10000[1] = (byte)(-7);
         var10000[2] = (byte)(127);
         float var8 = NanoVGRenderer.getTextWidth($$v_s(var10000, 2525926308759982126L), var5);
         float var9 = (float)(this.scratchCenter[0] / this.scale) - (var7 + var8) / 2.0F;
         float var10 = (float)(this.scratchCenter[1] / this.scale) - var5 / 2.0F;
         var10000 = new byte[9];
         var10000[0] = (byte)(125);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(121);
         var10000[3] = (byte)(-67);
         var10000[4] = (byte)(77);
         var10000[5] = (byte)(83);
         var10000[6] = (byte)(107);
         var10000[7] = (byte)(101);
         var10000[8] = (byte)(23);
         NanoVGRenderer.drawText($$v_s(var10000, 2525926308759982126L), var9, var10, var5, var6 << (24) | 16777215);
         var10000 = new byte[3];
         var10000[0] = (byte)(37);
         var10000[1] = (byte)(-7);
         var10000[2] = (byte)(127);
         NanoVGRenderer.drawText($$v_s(var10000, 2525926308759982126L), var9 + var7, var10, var5, var6 << (24) | 5622015);
      }
   }

   private boolean worldToScreen(double x, double y, double z, double[] out) {
      return worldToScreen(this.camProj, (float)x, (float)y, (float)z, this.fbW, this.fbH, out);
   }

   private float cameraDistanceTo(class_243 pos) {
      if (this.camProj == null) {
         return 4.0F;
      }

      double var2 = pos.field_1352 - this.camProj.camX;
      double var4 = pos.field_1351 - this.camProj.camY;
      double var6 = pos.field_1350 - this.camProj.camZ;
      return (float)Math.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   private float getDistanceScale(double depth, float refDist) {
      if (depth <= 0.001) {
         return 1.0F;
      }

      float var4 = (float)(refDist / Math.max(refDist * 0.35, depth));
      return Math.max(0.15F, Math.min(1.8F, var4));
   }

   private float getDistanceScale(double depth) {
      return this.getDistanceScale(depth, 4.0F);
   }

   private class_4184 getCamera() {
      return mc.field_1773.method_19418();
   }

   private void updateCamProj(int fw, int fh) {
      class_4184 var3 = class_310.method_1551().field_1773.method_19418();
      class_243 var4 = var3.method_71156();
      double var5 = Math.toRadians(var3.method_19330() + 180.0);
      double var7 = Math.toRadians(var3.method_19329());
      float var9 = (float)Math.tan(Math.toRadians(((Integer)class_310.method_1551().field_1690.method_41808().method_41753()).intValue()) / 2.0);
      float var10 = TrueStretched.getEffectiveAspectRatio(fw, fh);
      this.camProj.update(var4.field_1352, var4.field_1351, var4.field_1350, Math.cos(var5), Math.sin(var5), Math.cos(var7), Math.sin(var7), var9, var10);
   }

   private static boolean worldToScreen(JumpCircle.CamProj cp, float wx, float wy, float wz, int fw, int fh, double[] out) {
      float var7 = (float)(wx - cp.camX);
      float var8 = (float)(wy - cp.camY);
      float var9 = (float)(wz - cp.camZ);
      float var10 = (float)(var7 * cp.cosYaw + var9 * cp.sinYaw);
      float var11 = var8;
      float var12 = (float)(-var7 * cp.sinYaw + var9 * cp.cosYaw);
      float var13 = (float)(var11 * cp.cosPitch - var12 * cp.sinPitch);
      float var14 = (float)(var11 * cp.sinPitch + var12 * cp.cosPitch);
      if (var14 >= 0.0F) {
         return false;
      }

      out[0] = (float)(var10 / -var14 / (cp.tanHalfFov * cp.aspect) * (fw / 2.0) + fw / 2.0);
      out[1] = (float)(-var13 / -var14 / cp.tanHalfFov * (fh / 2.0) + fh / 2.0);
      if (out.length >= (3)) {
         out[2] = -var14;
      }

      return true;
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }

   private static float easeOutCubic(float t) {
      float var1 = 1.0F - t;
      return 1.0F - var1 * var1 * var1;
   }

   private static float easeOutExpo(float t) {
      return t >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0 * t);
   }

   private static int hsbArgb(float hue, int alpha) {
      hue -= (float)Math.floor(hue);
      int var2 = Color.HSBtoRGB(hue, 0.85F, 1.0F);
      return alpha << (24) | var2 & (16777215);
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

   private static final class CamProj {
      double camX;
      double camY;
      double camZ;
      double cosYaw;
      double sinYaw;
      double cosPitch;
      double sinPitch;
      float tanHalfFov;
      float aspect;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void update(double camX, double camY, double camZ, double cosYaw, double sinYaw, double cosPitch, double sinPitch, float tanHalfFov, float aspect) {
         this.camX = camXx;
         this.camY = camYx;
         this.camZ = camZx;
         this.cosYaw = cosYawx;
         this.sinYaw = sinYawx;
         this.cosPitch = cosPitchx;
         this.sinPitch = sinPitchx;
         this.tanHalfFov = tanHalfFovx;
         this.aspect = aspectx;
      }
   }

   private static final class Circle {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final boolean land;
      final int pCount;
      final float[] pAngle;
      final float[] pSpeed;
      final float[] pUp;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Circle(class_243 pos, long totalMs, boolean land, int pCount) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = Math.max(1L, totalMsx);
         this.land = landx;
         this.pCount = Math.max(0, pCountx);
         this.pAngle = new float[this.pCount];
         this.pSpeed = new float[this.pCount];
         this.pUp = new float[this.pCount];
         Random var6 = new Random();

         for (int var7 = 0; var7 < this.pCount; var7++) {
            this.pAngle[var7] = var6.nextFloat() * (float) (Math.PI * 2);
            this.pSpeed[var7] = 2.5F + var6.nextFloat() * 4.5F;
            this.pUp[var7] = 2.0F + var6.nextFloat() * 3.5F;
         }
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      float elapsedSec() {
         return (float)(System.currentTimeMillis() - this.spawnMs) / 1000.0F;
      }

      boolean expired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }
}
