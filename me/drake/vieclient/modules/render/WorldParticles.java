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
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_243;
import net.minecraft.class_4184;

public class WorldParticles extends Module {
   private final EnumSetting particleType = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Particle Type",
         "Style of particles scattered across the world",
         WorldParticles.ParticleType.VIE_LOGO
      )
   );
   private final EnumSetting logoStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Logo Style",
         "Rendering style for Vie Logo particles",
         WorldParticles.LogoStyle.COLORED
      )
   );
   private final EnumSetting colorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Color Mode",
         "Color palette for particles and glowing halos (THEME matches Vie Client)",
         WorldParticles.ColorMode.THEME
      )
   );
   private final ColorSetting customColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Custom Color",
         "Base particle and glow tint",
         -6461953
      )
   );
   private final IntSetting amount = (IntSetting)this.addSetting(
      new IntSetting(
         "Amount",
         "Maximum active particle count",
         110,
         20,
         300,
         5
      )
   );
   private final DoubleSetting radius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Radius",
         "Spawn radius around player (blocks)",
         20.0,
         8.0,
         40.0,
         1.0
      )
   );
   private final DoubleSetting heightRange = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Height Range",
         "Vertical spread of particles (blocks)",
         10.0,
         4.0,
         24.0,
         1.0
      )
   );
   private final DoubleSetting baseSize = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Size",
         "Base particle screen size",
         22.0,
         8.0,
         50.0,
         1.0
      )
   );
   private final DoubleSetting speed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Speed",
         "Drift and floating movement speed",
         0.9,
         0.2,
         3.0,
         0.1
      )
   );
   private final BoolSetting glowBloom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Glow Bloom",
         "Renders soft radial bloom halo behind each particle",
         true
      )
   );
   private final DoubleSetting glowRadius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Glow Radius",
         "Bloom halo radius multiplier",
         1.8,
         1.2,
         3.5,
         0.1
      )
   );
   private final BoolSetting windDrift = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Wind Breeze",
         "Gentle swaying wind simulation",
         true
      )
   );
   private final BoolSetting distanceFade = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Distance Fade",
         "Smooth alpha fade when near camera or at outer boundary",
         true
      )
   );
   private final List particles = new ArrayList();
   private final Random random = new Random();
   private int fbW;
   private int fbH;
   private final WorldParticles.CamProj camProj = new WorldParticles.CamProj();
   private final double[] scratchScreen = new double[2];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public WorldParticles() {
      super(
         "World Particles",
         "Scatters gorgeous glowing ambient particles across the world (including Vie Logo)",
         Category.CUSTOMIZE
      );
      this.customColor.visibleWhen(() -> this.colorMode.get() == WorldParticles.ColorMode.CUSTOM);
      this.logoStyle.visibleWhen(() -> this.particleType.get() == WorldParticles.ParticleType.VIE_LOGO);
   }

   @Override
   public void onDisable() {
      this.particles.clear();
      super.onDisable();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            class_243 var2 = mc.field_1773.method_19418().method_71156();
            double var3 = (Double)this.radius.get();
            double var5 = var3 * 1.35 * (var3 * 1.35);
            double var7 = (Double)this.speed.get() * 0.035;
            double var9 = System.currentTimeMillis() / 1000.0;
            int var11 = (Integer)this.amount.get();

            while (this.particles.size() < var11) {
               this.particles.add(this.spawnParticle(var2, var3, (Double)this.heightRange.get()));
            }

            if (this.particles.size() > var11) {
               this.particles.subList(var11, this.particles.size()).clear();
            }

            for (int var12 = 0; var12 < this.particles.size(); var12++) {
               WorldParticles.WorldParticle var13 = (WorldParticles.WorldParticle)this.particles.get(var12);
               var13.age++;
               double var14 = var13.x - var2.field_1352;
               double var16 = var13.y - var2.field_1351;
               double var18 = var13.z - var2.field_1350;
               double var20 = var14 * var14 + var16 * var16 + var18 * var18;
               if (var13.age < var13.maxAge && !(var20 > var5)) {
                  var13.x = var13.x + var13.vx * var7;
                  var13.y = var13.y + (var13.vy * var7 + Math.sin(var13.phase) * 0.008);
                  var13.z = var13.z + var13.vz * var7;
                  if ((Boolean)this.windDrift.get()) {
                     var13.x = var13.x + Math.sin(var9 * 0.6 + var13.seed) * 0.006;
                     var13.z = var13.z + Math.cos(var9 * 0.6 + var13.seed) * 0.006;
                  }

                  var13.phase = var13.phase + var13.phaseSpeed;
                  var13.rotation = var13.rotation + var13.rotSpeed;
               } else {
                  this.particles.set(var12, this.spawnParticle(var2, var3, (Double)this.heightRange.get()));
               }
            }
         } else {
            this.particles.clear();
         }
      }
   }

   private WorldParticles.WorldParticle spawnParticle(class_243 camPos, double maxR, double maxH) {
      double var6 = this.random.nextDouble() * Math.PI * 2.0;
      double var8 = (0.2 + 0.8 * Math.sqrt(this.random.nextDouble())) * maxR;
      double var10 = camPos.field_1352 + Math.cos(var6) * var8;
      double var12 = camPos.field_1350 + Math.sin(var6) * var8;
      double var14 = camPos.field_1351 + (this.random.nextDouble() * 2.0 - 1.0) * (maxH * 0.5);
      WorldParticles.WorldParticle var16 = new WorldParticles.WorldParticle();
      var16.x = var10;
      var16.y = var14;
      var16.z = var12;
      var16.vx = (this.random.nextDouble() - 0.5) * 0.6;
      var16.vy = (this.random.nextDouble() * 0.5 - 0.1) * 0.4;
      var16.vz = (this.random.nextDouble() - 0.5) * 0.6;
      var16.phase = (float)(this.random.nextDouble() * Math.PI * 2.0);
      var16.phaseSpeed = (float)(0.04 + this.random.nextDouble() * 0.06);
      var16.sizeScale = (float)(0.75 + this.random.nextDouble() * 0.55);
      var16.rotation = (float)(this.random.nextDouble() * Math.PI * 2.0);
      var16.rotSpeed = (float)((this.random.nextDouble() - 0.5) * 0.08);
      var16.age = 0;
      var16.maxAge = (120) + this.random.nextInt(180);
      var16.seed = this.random.nextInt(10000);
      return var16;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null && !this.particles.isEmpty()) {
         this.fbW = mc.method_22683().method_4489();
         this.fbH = mc.method_22683().method_4506();
         float var2 = mc.method_22683().method_4495();

         try (NanoVGFrame var3 = NanoVGRenderer.frame()) {
            if (!var3.isActive()) {
               return;
            }

            this.updateCamProj(this.fbW, this.fbH);
            class_4184 var4 = mc.field_1773.method_19418();
            class_243 var5 = var4.method_71156();
            double var6 = (Double)this.radius.get();
            double var8 = System.currentTimeMillis() / 1000.0;
            WorldParticles.ParticleType var10 = (WorldParticles.ParticleType)this.particleType.get();
            float var11 = (float)((Double)this.baseSize.get()).doubleValue();
            boolean var12 = (Boolean)this.glowBloom.get();
            float var13 = (float)((Double)this.glowRadius.get()).doubleValue();

            for (WorldParticles.WorldParticle var15 : this.particles) {
               double var16 = var15.x - var5.field_1352;
               double var18 = var15.y - var5.field_1351;
               double var20 = var15.z - var5.field_1350;
               double var22 = Math.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
               if (!(var22 > var6 * 1.25) && !(var22 < 0.6) && this.worldToScreen(var15.x, var15.y, var15.z, this.scratchScreen)) {
                  float var24 = (float)(this.scratchScreen[0] / var2);
                  float var25 = (float)(this.scratchScreen[1] / var2);
                  float var26 = mc.method_22683().method_4486();
                  float var27 = mc.method_22683().method_4502();
                  if (!(var24 < -60.0F) && !(var24 > var26 + 60.0F) && !(var25 < -60.0F) && !(var25 > var27 + 60.0F)) {
                     float var28 = (float)(var11 * 8.0 / Math.max(1.8, var22) * var15.sizeScale);
                     var28 = Math.max(1.0F, Math.min(72.0F, var28));
                     float var29 = 1.0F;
                     float var30 = (float)var15.age / var15.maxAge;
                     if (var30 < 0.15F) {
                        var29 *= var30 / 0.15F;
                     } else if (var30 > 0.82F) {
                        var29 *= (1.0F - var30) / 0.18F;
                     }

                     if ((Boolean)this.distanceFade.get()) {
                        if (var22 < 2.0) {
                           var29 *= (float)((var22 - 0.6) / 1.4);
                        }

                        if (var22 > var6 * 0.7) {
                           var29 *= (float)((var6 * 1.25 - var22) / (var6 * 0.55));
                        }
                     }

                     int var31 = Math.max(0, Math.min(255, (int)(var29 * 255.0F)));
                     if (var31 > 3) {
                        int var32 = this.getParticleColor(var15, var8, var31);
                        float var33 = TrueStretched.getStretchFactor();
                        boolean var34 = Math.abs(var33 - 1.0F) > 0.001F;
                        if (var34) {
                           NanoVGRenderer.save();
                           NanoVGRenderer.translate(var24, var25);
                           NanoVGRenderer.scale(var33, 1.0F);
                           NanoVGRenderer.translate(-var24, -var25);
                        }

                        try {
                           switch (var10) {
                              case VIE_LOGO:
                                 this.drawVieLogo(var24, var25, var28, var15, var31, var32, var12, var13);
                                 break;
                              case FIREFLY:
                                 this.drawFirefly(var24, var25, var28, var15, var8, var31, var32, var12, var13);
                                 break;
                              case COSMIC_STAR:
                                 this.drawCosmicStar(var24, var25, var28, var15, var31, var32, var12, var13);
                                 break;
                              case NEON_ORB:
                                 this.drawNeonOrb(var24, var25, var28, var31, var32, var12, var13);
                                 break;
                              case SNOWFLAKE:
                                 this.drawSnowflake(var24, var25, var28, var15, var31, var32, var12);
                                 break;
                              case HEARTS:
                                 this.drawHeart(var24, var25, var28, var15, var31, var32, var12, var13);
                           }
                        } finally {
                           if (var34) {
                              NanoVGRenderer.restore();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void drawVieLogo(float sx, float sy, float sz, WorldParticles.WorldParticle p, int alpha, int glowColor, boolean bloom, float gMult) {
      if (bloom) {
         int var9 = withAlpha(glowColor, (int)(alpha * 0.42F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * gMult, var9);
      }

      float var10 = sz * 0.5F;
      if (this.logoStyle.get() == WorldParticles.LogoStyle.COLORED) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(73);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(123);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(-14);
         var10000[5] = (byte)(13);
         var10000[6] = (byte)(118);
         IconManager.drawColored($$v_s(var10000, 3535530479890714624L), sx - var10, sy - var10, sz, alpha);
      } else {
         byte[] var11 = new byte[7];
         var11[0] = (byte)(73);
         var11[1] = (byte)(-11);
         var11[2] = (byte)(123);
         var11[3] = (byte)(-79);
         var11[4] = (byte)(-14);
         var11[5] = (byte)(13);
         var11[6] = (byte)(118);
         IconManager.draw($$v_s(var11, 3535530479890714624L), sx - var10, sy - var10, sz, alpha);
      }
   }

   private void drawFirefly(float sx, float sy, float sz, WorldParticles.WorldParticle p, double time, int alpha, int color, boolean bloom, float gMult) {
      float var11 = (float)(0.7 + 0.3 * Math.sin(time * 5.0 + p.seed));
      int var12 = Math.max(0, Math.min(255, (int)(alpha * var11)));
      if (bloom) {
         int var13 = withAlpha(color, (int)(var12 * 0.38F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * gMult * 1.3F, var13);
      }

      NanoVGRenderer.drawCircle(sx, sy, sz * 0.4F, withAlpha(color, var12));
      NanoVGRenderer.drawCircle(sx, sy, sz * 0.18F, (int)(var12 * 0.9F) << (24) | 16777215);
   }

   private void drawCosmicStar(float sx, float sy, float sz, WorldParticles.WorldParticle p, int alpha, int color, boolean bloom, float gMult) {
      if (bloom) {
         int var9 = withAlpha(color, (int)(alpha * 0.45F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * gMult, var9);
      }

      float var29 = sz * 0.55F;
      float var10 = var29 * 0.22F;
      float var11 = (float)Math.cos(p.rotation);
      float var12 = (float)Math.sin(p.rotation);
      NanoVGRenderer.beginFillBatch(color);
      float var13 = sx - var12 * var29;
      float var14 = sy - var11 * var29;
      float var15 = sx + var11 * var29;
      float var16 = sy - var12 * var29;
      float var17 = sx + var12 * var29;
      float var18 = sy + var11 * var29;
      float var19 = sx - var11 * var29;
      float var20 = sy + var12 * var29;
      float var21 = sx + (var11 - var12) * var10;
      float var22 = sy - (var12 + var11) * var10;
      float var23 = sx + (var11 + var12) * var10;
      float var24 = sy + (var11 - var12) * var10;
      float var25 = sx - (var11 - var12) * var10;
      float var26 = sy + (var12 + var11) * var10;
      float var27 = sx - (var11 + var12) * var10;
      float var28 = sy - (var11 - var12) * var10;
      NanoVGRenderer.batchTri(sx, sy, var13, var14, var21, var22);
      NanoVGRenderer.batchTri(sx, sy, var21, var22, var15, var16);
      NanoVGRenderer.batchTri(sx, sy, var15, var16, var23, var24);
      NanoVGRenderer.batchTri(sx, sy, var23, var24, var17, var18);
      NanoVGRenderer.batchTri(sx, sy, var17, var18, var25, var26);
      NanoVGRenderer.batchTri(sx, sy, var25, var26, var19, var20);
      NanoVGRenderer.batchTri(sx, sy, var19, var20, var27, var28);
      NanoVGRenderer.batchTri(sx, sy, var27, var28, var13, var14);
      NanoVGRenderer.endFillBatch();
      NanoVGRenderer.drawCircle(sx, sy, sz * 0.12F, alpha << (24) | 16777215);
   }

   private void drawNeonOrb(float sx, float sy, float sz, int alpha, int color, boolean bloom, float gMult) {
      if (bloom) {
         int var8 = withAlpha(color, (int)(alpha * 0.5F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * gMult * 1.2F, var8);
      }

      NanoVGRenderer.drawCircle(sx, sy, sz * 0.38F, color);
      NanoVGRenderer.drawCircle(sx, sy, sz * 0.16F, alpha << (24) | 16777215);
   }

   private void drawSnowflake(float sx, float sy, float sz, WorldParticles.WorldParticle p, int alpha, int color, boolean bloom) {
      float var8 = sz * 0.5F;
      float var9 = p.rotation;
      if (bloom) {
         int var10 = withAlpha(color, (int)(alpha * 0.35F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * 1.3F, var10);
      }

      NanoVGRenderer.beginLineBatch(color, Math.max(1.0F, sz * 0.08F));

      for (int var24 = 0; var24 < (6); var24++) {
         double var11 = var9 + var24 * (Math.PI / 3);
         float var13 = (float)Math.cos(var11);
         float var14 = (float)Math.sin(var11);
         float var15 = sx + var13 * var8;
         float var16 = sy + var14 * var8;
         NanoVGRenderer.batchLine(sx, sy, var15, var16);
         float var17 = sx + var13 * (var8 * 0.6F);
         float var18 = sy + var14 * (var8 * 0.6F);
         float var19 = var8 * 0.22F;
         float var20 = (float)Math.cos(var11 + 0.65);
         float var21 = (float)Math.sin(var11 + 0.65);
         float var22 = (float)Math.cos(var11 - 0.65);
         float var23 = (float)Math.sin(var11 - 0.65);
         NanoVGRenderer.batchLine(var17, var18, var17 + var20 * var19, var18 + var21 * var19);
         NanoVGRenderer.batchLine(var17, var18, var17 + var22 * var19, var18 + var23 * var19);
      }

      NanoVGRenderer.endLineBatch();
   }

   private void drawHeart(float sx, float sy, float sz, WorldParticles.WorldParticle p, int alpha, int color, boolean bloom, float gMult) {
      if (bloom) {
         int var9 = withAlpha(color, (int)(alpha * 0.45F));
         NanoVGRenderer.drawRadialGlow(sx, sy, sz * gMult, var9);
      }

      float var13 = sz * 0.22F;
      float var10 = sx - var13 * 0.95F;
      float var11 = sx + var13 * 0.95F;
      float var12 = sy - var13 * 0.5F;
      NanoVGRenderer.drawCircle(var10, var12, var13, color);
      NanoVGRenderer.drawCircle(var11, var12, var13, color);
      NanoVGRenderer.beginFillBatch(color);
      NanoVGRenderer.batchTri(var10 - var13 * 0.85F, var12 + var13 * 0.1F, var11 + var13 * 0.85F, var12 + var13 * 0.1F, sx, sy + var13 * 1.8F);
      NanoVGRenderer.endFillBatch();
   }

   private int getParticleColor(WorldParticles.WorldParticle p, double time, int alpha) {
      return switch ((WorldParticles.ColorMode)this.colorMode.get()) {
         case THEME -> {
            ClientSettings var7 = ClientSettings.get();
            int var8 = var7 != null ? var7.getPrimaryColor() : -7643914;
            yield alpha << (24) | var8 & (16777215);
         }
         case CUSTOM -> withAlpha((Integer)this.customColor.get(), alpha);
         case RAINBOW -> {
            float var5 = (float)(time * 0.18 + p.seed % (100) * 0.01) % 1.0F;
            int var6 = Color.HSBtoRGB(var5, 0.82F, 1.0F);
            yield alpha << (24) | var6 & (16777215);
         }
         case NEON_PURPLE -> alpha << (24) | 12124160 | 24320 | 255;
         case CYAN_GLOW -> alpha << (24) | 0 | 60160 | 255;
         case GOLDEN -> alpha << (24) | 16711680 | 52480 | 55;
      };
   }

   private static int withAlpha(int argb, int a) {
      int var2 = Math.max(0, Math.min(255, a));
      return var2 << (24) | argb & (16777215);
   }

   private void updateCamProj(int fw, int fh) {
      class_4184 var3 = mc.field_1773.method_19418();
      class_243 var4 = var3.method_71156();
      double var5 = Math.toRadians(var3.method_19330() + 180.0);
      double var7 = Math.toRadians(var3.method_19329());
      float var9 = (float)Math.tan(Math.toRadians(((Integer)mc.field_1690.method_41808().method_41753()).intValue()) / 2.0);
      float var10 = TrueStretched.getEffectiveAspectRatio(fw, fh);
      this.camProj.update(var4.field_1352, var4.field_1351, var4.field_1350, Math.cos(var5), Math.sin(var5), Math.cos(var7), Math.sin(var7), var9, var10);
   }

   private boolean worldToScreen(double wx, double wy, double wz, double[] out) {
      WorldParticles.CamProj var8 = this.camProj;
      float var9 = (float)(wx - var8.camX);
      float var10 = (float)(wy - var8.camY);
      float var11 = (float)(wz - var8.camZ);
      float var12 = (float)(var9 * var8.cosYaw + var11 * var8.sinYaw);
      float var13 = var10;
      float var14 = (float)(-var9 * var8.sinYaw + var11 * var8.cosYaw);
      float var15 = (float)(var13 * var8.cosPitch - var14 * var8.sinPitch);
      float var16 = (float)(var13 * var8.sinPitch + var14 * var8.cosPitch);
      if (var16 >= -0.05F) {
         return false;
      }

      out[0] = (float)(var12 / -var16 / (var8.tanHalfFov * var8.aspect) * (this.fbW / 2.0) + this.fbW / 2.0);
      out[1] = (float)(-var15 / -var16 / var8.tanHalfFov * (this.fbH / 2.0) + this.fbH / 2.0);
      return true;
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

   public enum ColorMode {
      THEME,
      CUSTOM,
      RAINBOW,
      NEON_PURPLE,
      CYAN_GLOW,
      GOLDEN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static WorldParticles.ColorMode[] $values() {
         WorldParticles.ColorMode[] var10000 = new WorldParticles.ColorMode[6];
         var10000[0] = THEME;
         var10000[1] = CUSTOM;
         var10000[2] = RAINBOW;
         var10000[3] = NEON_PURPLE;
         var10000[4] = CYAN_GLOW;
         var10000[5] = GOLDEN;
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
         var10002[4] = (byte)(35);
         THEME = new WorldParticles.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(41);
         var10002[5] = (byte)(35);
         CUSTOM = new WorldParticles.ColorMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(36);
         var10002[5] = (byte)(33);
         var10002[6] = (byte)(-10);
         RAINBOW = new WorldParticles.ColorMode();
         var10002 = new byte[11];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(57);
         var10002[5] = (byte)(62);
         var10002[6] = (byte)(-12);
         var10002[7] = (byte)(-31);
         var10002[8] = (byte)(5);
         var10002[9] = (byte)(-18);
         var10002[10] = (byte)(-95);
         NEON_PURPLE = new WorldParticles.ColorMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(57);
         var10002[5] = (byte)(41);
         var10002[6] = (byte)(-19);
         var10002[7] = (byte)(-4);
         var10002[8] = (byte)(2);
         CYAN_GLOW = new WorldParticles.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(35);
         var10002[5] = (byte)(32);
         GOLDEN = new WorldParticles.ColorMode();
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

   public enum LogoStyle {
      COLORED,
      WHITE_GLOW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static WorldParticles.LogoStyle[] $values() {
         WorldParticles.LogoStyle[] var10000 = new WorldParticles.LogoStyle[2];
         var10000[0] = COLORED;
         var10000[1] = WHITE_GLOW;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(20);
         var10002[5] = (byte)(-20);
         var10002[6] = (byte)(-70);
         COLORED = new WorldParticles.LogoStyle();
         var10002 = new byte[10];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(3);
         var10002[5] = (byte)(-10);
         var10002[6] = (byte)(-71);
         var10002[7] = (byte)(-45);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-11);
         WHITE_GLOW = new WorldParticles.LogoStyle();
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

   public enum ParticleType {
      VIE_LOGO,
      FIREFLY,
      COSMIC_STAR,
      NEON_ORB,
      SNOWFLAKE,
      HEARTS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static WorldParticles.ParticleType[] $values() {
         WorldParticles.ParticleType[] var10000 = new WorldParticles.ParticleType[6];
         var10000[0] = VIE_LOGO;
         var10000[1] = FIREFLY;
         var10000[2] = COSMIC_STAR;
         var10000[3] = NEON_ORB;
         var10000[4] = SNOWFLAKE;
         var10000[5] = HEARTS;
         return var10000;
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

   private static class WorldParticle {
      double x;
      double y;
      double z;
      double vx;
      double vy;
      double vz;
      float phase;
      float phaseSpeed;
      float sizeScale;
      float rotation;
      float rotSpeed;
      int age;
      int maxAge;
      int seed;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
