package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_3532;

public class TargetESP extends Module {
   private final EnumSetting style = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Style",
         "Target lock indicator visual style",
         TargetESP.Style.CYBER_RINGS
      )
   );
   private final EnumSetting colorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Color Mode",
         "Neon color mode (THEME syncs with Vie Client accent)",
         TargetESP.ColorMode.THEME
      )
   );
   private final ColorSetting customColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Custom Color",
         "Custom color when color mode is set to Custom",
         -16718337
      )
   );
   private final ColorSetting hitColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Hit Color",
         "Flash color when target takes damage",
         -52395
      )
   );
   private final BoolSetting hitFlash = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hit Flash",
         "Flash red impact burst when target is struck",
         true
      )
   );
   private final DoubleSetting scale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Scale",
         "Overall scale size of target indicator",
         1.0,
         0.5,
         2.0,
         0.05
      )
   );
   private final DoubleSetting tempo = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Speed",
         "Rotation and animation motion speed",
         1.0,
         0.2,
         3.0,
         0.1
      )
   );
   private final DoubleSetting opacity = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Opacity",
         "Target indicator opacity and brightness",
         0.9,
         0.2,
         1.0,
         0.05
      )
   );
   private final DoubleSetting memory = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Memory",
         "Target retention duration after last combat hit in seconds",
         3.5,
         1.0,
         10.0,
         0.5
      )
   );
   private final BoolSetting playersOnly = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Players Only",
         "Only lock onto other player characters",
         false
      )
   );
   private final BoolSetting showInfo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Show Info",
         "Display target distance and health badge",
         true
      )
   );
   private class_1309 targetEntity = null;
   private long lastTargetTime = 0L;
   private float presence = 0.0F;
   private final WorldProjector projector = new WorldProjector();
   private final float[] scratchPoint = new float[3];
   private final float[] scratchBox = new float[5];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TargetESP() {
      super(
         "Target ESP",
         "Futuristic holographic Cyber target lock-on rings and badge",
         Category.RENDER
      );
      this.customColor.visibleWhen(() -> this.colorMode.get() == TargetESP.ColorMode.CUSTOM);
   }

   @Override
   public void onDisable() {
      this.targetEntity = null;
      this.presence = 0.0F;
      super.onDisable();
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (event.getTarget() instanceof class_1309 var2 && var2 != mc.field_1724 && (!(Boolean)this.playersOnly.get() || var2 instanceof class_1657)) {
         this.targetEntity = var2;
         this.lastTargetTime = System.currentTimeMillis();
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            if (mc.field_1692 instanceof class_1309 var2
               && var2 != mc.field_1724
               && var2.method_5805()
               && (!(Boolean)this.playersOnly.get() || var2 instanceof class_1657)) {
               this.targetEntity = var2;
               this.lastTargetTime = System.currentTimeMillis();
            }

            long var4 = (long)((Double)this.memory.get() * 1000.0);
            if (this.targetEntity != null
               && (
                  !this.targetEntity.method_5805()
                     || this.targetEntity.method_31481()
                     || this.targetEntity.method_73183() != mc.field_1687
                     || System.currentTimeMillis() - this.lastTargetTime > var4
               )) {
               this.targetEntity = null;
            }
         } else {
            this.targetEntity = null;
         }
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         float var2 = mc.method_61966() != null ? mc.method_61966().method_60636() / 20.0F : 0.016F;
         if (this.targetEntity != null) {
            this.presence = Math.min(1.0F, this.presence + var2 * 4.0F);
         } else {
            this.presence = Math.max(0.0F, this.presence - var2 * 3.5F);
         }

         if (!(this.presence <= 0.005F) && this.targetEntity != null) {
            if (this.projector.snapshot()) {
               float var3 = event.tickDelta;
               double var4 = class_3532.method_16436(var3, this.targetEntity.field_6038, this.targetEntity.method_23317());
               double var6 = class_3532.method_16436(var3, this.targetEntity.field_5971, this.targetEntity.method_23318());
               double var8 = class_3532.method_16436(var3, this.targetEntity.field_5989, this.targetEntity.method_23321());
               float var10 = this.targetEntity.method_17681();
               float var11 = this.targetEntity.method_17682();
               float var12 = 0.0F;
               if ((Boolean)this.hitFlash.get() && this.targetEntity.field_6235 > 0) {
                  var12 = this.targetEntity.field_6235 / 10.0F;
               }

               double var13 = System.currentTimeMillis() / 1000.0 * (Double)this.tempo.get();
               int var15 = this.resolveColor(var13);
               int var16 = var12 > 0.01F ? lerpColor(var15, (Integer)this.hitColor.get(), var12) : var15;
               int var17 = Math.max(0, Math.min(255, (int)(this.presence * (Double)this.opacity.get() * 255.0)));
               var16 = var17 << 24 | var16 & 16777215;

               try (NanoVGFrame var18 = NanoVGRenderer.frame()) {
                  if (!var18.isActive()) {
                     return;
                  }

                  float var19 = (float)((Double)this.scale.get()).doubleValue();
                  switch ((TargetESP.Style)this.style.get()) {
                     case CYBER_RINGS:
                        this.drawCyberRings(var4, var6, var8, var10, var11, var16, var17, var13, var19, var12);
                        break;
                     case RETICLE_BRACKETS:
                        this.drawReticleBrackets(var4, var6, var8, var10, var11, var16, var17, var13, var19, var12);
                        break;
                     case ENERGY_HALO:
                        this.drawEnergyHalo(var4, var6, var8, var10, var11, var16, var17, var13, var19, var12);
                        break;
                     case ORBITAL_SHARDS:
                        this.drawOrbitalShards(var4, var6, var8, var10, var11, var16, var17, var13, var19, var12);
                  }

                  if ((Boolean)this.showInfo.get()) {
                     this.drawTargetTag(var4, var6 + var11 + 0.35F * var19, var8, var16, var17);
                  }
               }
            }
         }
      }
   }

   private void drawCyberRings(double tx, double ty, double tz, float ew, float eh, int color, int alpha, double time, float sc, float hurt) {
      float var15 = (ew * 0.75F + 0.3F) * sc * (1.0F + hurt * 0.18F);
      byte var16 = 28;
      double var17 = time * 2.2;
      float var19 = 0.0F;
      float var20 = 0.0F;
      boolean var21 = true;

      for (int var22 = 0; var22 <= var16; var22++) {
         double var23 = var17 + var22 * Math.PI * 2.0 / var16;
         double var25 = tx + Math.cos(var23) * var15;
         double var27 = tz + Math.sin(var23) * var15;
         if (this.projector.project(var25, ty + 0.05, var27, this.scratchPoint)) {
            if (!var21) {
               float var29 = Math.max(1.5F, this.scratchPoint[2]);
               float var30 = Math.max(0.35F, Math.min(1.5F, 4.0F / var29));
               NanoVGRenderer.drawLine(var19, var20, this.scratchPoint[0], this.scratchPoint[1], Math.max(0.5F, 1.8F * var30), color);
            }

            var19 = this.scratchPoint[0];
            var20 = this.scratchPoint[1];
            var21 = false;
         } else {
            var21 = true;
         }
      }

      double var34 = -time * 1.6;
      float var24 = var15 * 1.32F;

      for (int var35 = 0; var35 < 4; var35++) {
         double var26 = var34 + var35 * (Math.PI / 2);
         double var28 = var26 + (Math.PI / 3);
         this.drawArc3D(tx, ty + 0.05, tz, var24, var26, var28, 8, color, 2.2F);
      }

      if (this.projector.project(tx, ty + 0.05, tz, this.scratchPoint)) {
         float var36 = TrueStretched.getStretchFactor();
         boolean var37 = Math.abs(var36 - 1.0F) > 0.001F;
         if (var37) {
            NanoVGRenderer.save();
            NanoVGRenderer.translate(this.scratchPoint[0], this.scratchPoint[1]);
            NanoVGRenderer.scale(var36, 1.0F);
            NanoVGRenderer.translate(-this.scratchPoint[0], -this.scratchPoint[1]);
         }

         try {
            NanoVGRenderer.drawRadialGlow(
               this.scratchPoint[0], this.scratchPoint[1], var15 * 60.0F / Math.max(1.0F, this.scratchPoint[2]), withAlpha(color, (int)(alpha * 0.38F))
            );
         } finally {
            if (var37) {
               NanoVGRenderer.restore();
            }
         }
      }
   }

   private void drawReticleBrackets(double tx, double ty, double tz, float ew, float eh, int color, int alpha, double time, float sc, float hurt) {
      class_238 var15 = new class_238(tx - ew * 0.5, ty, tz - ew * 0.5, tx + ew * 0.5, ty + eh, tz + ew * 0.5);
      if (this.projector.projectBox(var15, this.scratchBox)) {
         float var16 = this.scratchBox[0];
         float var17 = this.scratchBox[1];
         float var18 = this.scratchBox[2];
         float var19 = this.scratchBox[3];
         float var20 = (6.0F + hurt * 8.0F) * sc;
         var16 -= var20;
         var17 -= var20;
         var18 += var20 * 2.0F;
         var19 += var20 * 2.0F;
         float var21 = Math.min(var18, var19) * 0.26F;
         float var22 = Math.min(1.5F, Math.min(var18, var19) / 40.0F);
         float var23 = Math.max(0.6F, 2.0F * var22);
         NanoVGRenderer.drawLine(var16, var17, var16 + var21, var17, var23, color);
         NanoVGRenderer.drawLine(var16, var17, var16, var17 + var21, var23, color);
         NanoVGRenderer.drawLine(var16 + var18, var17, var16 + var18 - var21, var17, var23, color);
         NanoVGRenderer.drawLine(var16 + var18, var17, var16 + var18, var17 + var21, var23, color);
         NanoVGRenderer.drawLine(var16, var17 + var19, var16 + var21, var17 + var19, var23, color);
         NanoVGRenderer.drawLine(var16, var17 + var19, var16, var17 + var19 - var21, var23, color);
         NanoVGRenderer.drawLine(var16 + var18, var17 + var19, var16 + var18 - var21, var17 + var19, var23, color);
         NanoVGRenderer.drawLine(var16 + var18, var17 + var19, var16 + var18, var17 + var19 - var21, var23, color);
         float var24 = var16 + var18 * 0.5F;
         float var25 = var17 + var19 * 0.5F;
         float var26 = 5.0F * sc * var22;
         NanoVGRenderer.drawLine(var24 - var26, var25, var24 + var26, var25, Math.max(0.5F, 1.2F * var22), withAlpha(color, (int)(alpha * 0.6F)));
         NanoVGRenderer.drawLine(var24, var25 - var26, var24, var25 + var26, Math.max(0.5F, 1.2F * var22), withAlpha(color, (int)(alpha * 0.6F)));
      }
   }

   private void drawEnergyHalo(double tx, double ty, double tz, float ew, float eh, int color, int alpha, double time, float sc, float hurt) {
      double var15 = ty + eh + 0.35 * sc + Math.sin(time * 3.0) * 0.08;
      if (this.projector.project(tx, var15, tz, this.scratchPoint)) {
         float var17 = this.scratchPoint[0];
         float var18 = this.scratchPoint[1];
         float var19 = Math.max(1.0F, this.scratchPoint[2]);
         float var20 = 32.0F / var19 * sc * (1.0F + hurt * 0.3F);
         float var21 = Math.min(1.5F, var20 / 16.0F);
         float var22 = Math.max(0.6F, 2.0F * var21);
         float var23 = TrueStretched.getStretchFactor();
         boolean var24 = Math.abs(var23 - 1.0F) > 0.001F;
         if (var24) {
            NanoVGRenderer.save();
            NanoVGRenderer.translate(var17, var18);
            NanoVGRenderer.scale(var23, 1.0F);
            NanoVGRenderer.translate(-var17, -var18);
         }

         try {
            NanoVGRenderer.drawRadialGlow(var17, var18, var20 * 2.2F, withAlpha(color, (int)(alpha * 0.5F)));
            float var25 = (float)(time * 2.0);
            float var26 = (float)Math.cos(var25);
            float var27 = (float)Math.sin(var25);
            float var28 = var17 - var27 * var20;
            float var29 = var18 - var26 * var20 * 0.5F;
            float var30 = var17 + var26 * var20;
            float var31 = var18 - var27 * var20 * 0.5F;
            float var32 = var17 + var27 * var20;
            float var33 = var18 + var26 * var20 * 0.5F;
            float var34 = var17 - var26 * var20;
            float var35 = var18 + var27 * var20 * 0.5F;
            NanoVGRenderer.drawLine(var28, var29, var30, var31, var22, color);
            NanoVGRenderer.drawLine(var30, var31, var32, var33, var22, color);
            NanoVGRenderer.drawLine(var32, var33, var34, var35, var22, color);
            NanoVGRenderer.drawLine(var34, var35, var28, var29, var22, color);
            NanoVGRenderer.drawCircle(var17, var18, Math.max(0.5F, var20 * 0.22F), (int)(alpha * 0.9F) << 24 | 16777215);
         } finally {
            if (var24) {
               NanoVGRenderer.restore();
            }
         }
      }
   }

   private void drawOrbitalShards(double tx, double ty, double tz, float ew, float eh, int color, int alpha, double time, float sc, float hurt) {
      int var15 = 4;
      float var16 = (ew * 0.8F + 0.45F) * sc;
      double var17 = ty + eh * 0.5;

      for (int var19 = 0; var19 < var15; var19++) {
         double var20 = time * 2.4 + var19 * Math.PI * 2.0 / var15;
         double var22 = tx + Math.cos(var20) * var16;
         double var24 = tz + Math.sin(var20) * var16;
         double var26 = var17 + Math.sin(time * 3.0 + var19) * 0.25;
         if (this.projector.project(var22, var26, var24, this.scratchPoint)) {
            float var28 = this.scratchPoint[0];
            float var29 = this.scratchPoint[1];
            float var30 = 18.0F / Math.max(1.0F, this.scratchPoint[2]) * sc;
            float var31 = TrueStretched.getStretchFactor();
            boolean var32 = Math.abs(var31 - 1.0F) > 0.001F;
            if (var32) {
               NanoVGRenderer.save();
               NanoVGRenderer.translate(var28, var29);
               NanoVGRenderer.scale(var31, 1.0F);
               NanoVGRenderer.translate(-var28, -var29);
            }

            try {
               NanoVGRenderer.drawRadialGlow(var28, var29, var30 * 2.0F, withAlpha(color, (int)(alpha * 0.45F)));
               NanoVGRenderer.beginFillBatch(color);
               NanoVGRenderer.batchTri(var28, var29 - var30, var28 + var30 * 0.6F, var29, var28, var29 + var30);
               NanoVGRenderer.batchTri(var28, var29 - var30, var28, var29 + var30, var28 - var30 * 0.6F, var29);
               NanoVGRenderer.endFillBatch();
            } finally {
               if (var32) {
                  NanoVGRenderer.restore();
               }
            }
         }
      }
   }

   private void drawTargetTag(double tx, double ty, double tz, int color, int alpha) {
      if (this.projector.project(tx, ty, tz, this.scratchPoint)) {
         float var9 = this.scratchPoint[0];
         float var10 = this.scratchPoint[1];
         float var11 = Math.max(1.5F, this.scratchPoint[2]);
         float var12 = Math.max(0.35F, Math.min(1.6F, 4.0F / var11));
         float var13 = this.targetEntity.method_6032();
         float var14 = this.targetEntity.method_6063();
         double var15 = mc.field_1724.method_5739(this.targetEntity);
         String var17 = this.targetEntity.method_5477().getString();
         String var18 = String.format(
            "%s §7|§f %.1f❤ §7(%.1fm)",
            var17,
            var13,
            var15
         );
         float var19 = Math.max(3.5F, 8.0F * var12);
         float var20 = NanoVGRenderer.getTextWidth(var18, var19);
         float var21 = var20 + 12.0F * var12;
         float var22 = 14.0F * var12;
         float var23 = var9 - var21 * 0.5F;
         float var24 = var10 - var22;
         int var25 = (int)(alpha * 0.72F) << 24 | 987930;
         NanoVGRenderer.drawRoundedRect(var23, var24, var21, var22, Math.max(1.0F, 3.5F * var12), var25);
         NanoVGRenderer.drawRoundedRectOutline(
            var23, var24, var21, var22, Math.max(1.0F, 3.5F * var12), Math.max(0.5F, 1.0F * var12), withAlpha(color, (int)(alpha * 0.65F))
         );
         float var26 = Math.max(0.0F, Math.min(1.0F, var13 / Math.max(1.0F, var14)));
         int var27 = var26 > 0.5F ? -16711809 : (var26 > 0.25F ? -10496 : -48060);
         NanoVGRenderer.drawRoundedRect(
            var23 + 1.5F * var12,
            var24 + var22 - 2.0F * var12,
            (var21 - 3.0F * var12) * var26,
            Math.max(0.8F, 1.5F * var12),
            Math.max(0.5F, 1.0F * var12),
            withAlpha(var27, alpha)
         );
         NanoVGRenderer.drawText(var18, var23 + 6.0F * var12, var24 + 3.0F * var12, var19, (int)(alpha * 0.95F) << 24 | 16777215);
      }
   }

   private void drawArc3D(double cx, double cy, double cz, float r, double a0, double a1, int steps, int color, float thickness) {
      float var15 = 0.0F;
      float var16 = 0.0F;
      boolean var17 = true;

      for (int var18 = 0; var18 <= steps; var18++) {
         double var19 = a0 + (a1 - a0) * ((double)var18 / steps);
         double var21 = cx + Math.cos(var19) * r;
         double var23 = cz + Math.sin(var19) * r;
         if (this.projector.project(var21, cy, var23, this.scratchPoint)) {
            if (!var17) {
               float var25 = Math.max(1.5F, this.scratchPoint[2]);
               float var26 = Math.max(0.35F, Math.min(1.5F, 4.0F / var25));
               NanoVGRenderer.drawLine(var15, var16, this.scratchPoint[0], this.scratchPoint[1], Math.max(0.5F, thickness * var26), color);
            }

            var15 = this.scratchPoint[0];
            var16 = this.scratchPoint[1];
            var17 = false;
         } else {
            var17 = true;
         }
      }
   }

   private int resolveColor(double time) {
      return switch ((TargetESP.ColorMode)this.colorMode.get()) {
         case THEME -> {
            ClientSettings var4 = ClientSettings.get();
            yield var4 != null ? var4.getPrimaryColor() : -7643914;
         }
         case CYAN_NEON -> -16718337;
         case PURPLE_HAZE -> -5028609;
         case GOLDEN_SUN -> -10496;
         case RAINBOW -> {
            float var3 = (float)(time * 0.2 % 1.0);
            yield Color.HSBtoRGB(var3, 0.85F, 1.0F);
         }
         case CUSTOM -> this.customColor.get();
      };
   }

   private static int withAlpha(int argb, int a) {
      int var2 = Math.max(0, Math.min(255, a));
      return var2 << (24) | argb & (16777215);
   }

   private static int lerpColor(int from, int to, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int var3 = from >>> (24) & (255);
      int var4 = from >> (16) & (255);
      int var5 = from >> (8) & (255);
      int var6 = from & (255);
      int var7 = to >>> (24) & (255);
      int var8 = to >> (16) & (255);
      int var9 = to >> (8) & (255);
      int var10 = to & (255);
      int var11 = (int)(var3 + (var7 - var3) * t);
      int var12 = (int)(var4 + (var8 - var4) * t);
      int var13 = (int)(var5 + (var9 - var5) * t);
      int var14 = (int)(var6 + (var10 - var6) * t);
      return var11 << (24) | var12 << (16) | var13 << (8) | var14;
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

   public enum ColorMode {
      THEME,
      CYAN_NEON,
      PURPLE_HAZE,
      GOLDEN_SUN,
      RAINBOW,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TargetESP.ColorMode[] $values() {
         TargetESP.ColorMode[] var10000 = new TargetESP.ColorMode[6];
         var10000[0] = THEME;
         var10000[1] = CYAN_NEON;
         var10000[2] = PURPLE_HAZE;
         var10000[3] = GOLDEN_SUN;
         var10000[4] = RAINBOW;
         var10000[5] = CUSTOM;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(77);
         THEME = new TargetESP.ColorMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(87);
         var10002[5] = (byte)(-87);
         var10002[6] = (byte)(-14);
         var10002[7] = (byte)(126);
         var10002[8] = (byte)(27);
         CYAN_NEON = new TargetESP.ColorMode();
         var10002 = new byte[11];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(68);
         var10002[5] = (byte)(-94);
         var10002[6] = (byte)(-24);
         var10002[7] = (byte)(121);
         var10002[8] = (byte)(20);
         var10002[9] = (byte)(-8);
         var10002[10] = (byte)(-95);
         PURPLE_HAZE = new TargetESP.ColorMode();
         var10002 = new byte[10];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(77);
         var10002[5] = (byte)(-87);
         var10002[6] = (byte)(-24);
         var10002[7] = (byte)(98);
         var10002[8] = 0;
         var10002[9] = (byte)(-20);
         GOLDEN_SUN = new TargetESP.ColorMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(74);
         var10002[5] = (byte)(-88);
         var10002[6] = (byte)(-32);
         RAINBOW = new TargetESP.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(71);
         var10002[5] = (byte)(-86);
         CUSTOM = new TargetESP.ColorMode();
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

   public enum Style {
      CYBER_RINGS,
      RETICLE_BRACKETS,
      ENERGY_HALO,
      ORBITAL_SHARDS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TargetESP.Style[] $values() {
         TargetESP.Style[] var10000 = new TargetESP.Style[4];
         var10000[0] = CYBER_RINGS;
         var10000[1] = RETICLE_BRACKETS;
         var10000[2] = ENERGY_HALO;
         var10000[3] = ORBITAL_SHARDS;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[11];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-49);
         var10002[5] = (byte)(126);
         var10002[6] = (byte)(-117);
         var10002[7] = (byte)(-79);
         var10002[8] = (byte)(25);
         var10002[9] = (byte)(-27);
         var10002[10] = (byte)(-73);
         CYBER_RINGS = new TargetESP.Style();
         var10002 = new byte[16];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-34);
         var10002[5] = (byte)(109);
         var10002[6] = (byte)(-100);
         var10002[7] = (byte)(-89);
         var10002[8] = (byte)(21);
         var10002[9] = (byte)(-16);
         var10002[10] = (byte)(-91);
         var10002[11] = (byte)(-75);
         var10002[12] = (byte)(-114);
         var10002[13] = (byte)(-68);
         var10002[14] = (byte)(-91);
         var10002[15] = (byte)(-45);
         RETICLE_BRACKETS = new TargetESP.Style();
         var10002 = new byte[11];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-38);
         var10002[5] = (byte)(120);
         var10002[6] = (byte)(-122);
         var10002[7] = (byte)(-80);
         var10002[8] = (byte)(22);
         var10002[9] = (byte)(-18);
         var10002[10] = (byte)(-85);
         ENERGY_HALO = new TargetESP.Style();
         var10002 = new byte[14];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-55);
         var10002[5] = (byte)(96);
         var10002[6] = (byte)(-107);
         var10002[7] = (byte)(-89);
         var10002[8] = (byte)(4);
         var10002[9] = (byte)(-22);
         var10002[10] = (byte)(-91);
         var10002[11] = (byte)(-92);
         var10002[12] = (byte)(-127);
         var10002[13] = (byte)(-86);
         ORBITAL_SHARDS = new TargetESP.Style();
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
