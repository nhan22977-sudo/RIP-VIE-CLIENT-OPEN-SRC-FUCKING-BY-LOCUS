package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1297;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2626;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_4184;
import net.minecraft.class_8143;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;

public class AnimationFX extends Module {
   private final EnumSetting auraPreset = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Aura Preset",
         "Clean and eye-catching personal aura preset",
         AnimationFX.AuraPreset.CELESTIAL
      )
   );
   private final DoubleSetting auraScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Aura Scale",
         "Radius multiplier for personal aura",
         1.0,
         0.5,
         2.5,
         0.1
      )
   );
   private final DoubleSetting auraSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Aura Speed",
         "Aura rotation and pulse speed",
         1.0,
         0.2,
         3.0,
         0.1
      )
   );
   private final BoolSetting auraRings = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Aura Rings",
         "Render rotating glowing energy rings at feet",
         true
      )
   );
   private final BoolSetting auraParticles = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Aura Particles",
         "Spawn rising swirling aura particles",
         true
      )
   );
   private final BoolSetting footprints = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Footprints",
         "Leave stylish animated footprints where you walk",
         true
      )
   );
   private final EnumSetting footprintStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Footprint Style",
         "Select footprint design or custom VIE logo",
         AnimationFX.FootprintStyle.VIE_LOGO
      )
   );
   private final ColorSetting footprintColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Footprint Color",
         "Color for footprints and step runes",
         -2601993
      )
   );
   private final DoubleSetting footprintDuration = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Footprint Life",
         "Duration in seconds before footprint fades",
         2.6,
         0.6,
         8.0,
         0.2
      )
   );
   private final DoubleSetting footprintScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Footprint Scale",
         "Size multiplier for footprints",
         1.0,
         0.4,
         2.5,
         0.1
      )
   );
   private final BoolSetting footprintGlow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Footprint Glow",
         "Render soft radiant bloom around footprints",
         true
      )
   );
   private final BoolSetting eatingFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Eating Glow FX",
         "Glowing sparkles and floating essence when eating or drinking",
         true
      )
   );
   private final BoolSetting damageFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Damage Glow FX",
         "Vibrant glowing crimson hit sparks and energy trails on damage",
         true
      )
   );
   private final BoolSetting miningFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Mining Glow FX",
         "Crystalline glowing dust and flecks floating from mined blocks",
         true
      )
   );
   private final BoolSetting placeFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Place Glow FX",
         "Expanding glowing construct pulse when placing blocks",
         true
      )
   );
   private final BoolSetting particleGlow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Particle Glow",
         "Multi-pass soft radial bloom halo on all custom particles",
         true
      )
   );
   private final DoubleSetting particleScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Particle Size",
         "Size multiplier for custom particles",
         1.0,
         0.5,
         2.5,
         0.1
      )
   );
   private final DoubleSetting glowIntensity = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Glow Opacity",
         "Luminance of the radial particle bloom halo",
         160.0,
         40.0,
         255.0,
         5.0
      )
   );
   private static final int MAX_FOOTPRINTS = 64;
   private static final int MAX_PARTICLES = 160;
   private static final int MAX_AURA_PARTICLES = 32;
   private static final Color[] POTION_PALETTE = new Color[]{
      new Color(100, 210, 255), new Color(138, 153, 255), new Color(192, 132, 252), new Color(224, 242, 254)
   };
   private static final Color[] FOOD_PALETTE = new Color[]{
      new Color(255, 213, 79), new Color(255, 224, 130), new Color(255, 249, 196), new Color(255, 202, 40)
   };
   private static final Color[] AURA_CELESTIAL = new Color[]{new Color(0, 225, 255), new Color(135, 240, 255), new Color(175, 215, 255), Color.WHITE};
   private static final Color[] AURA_CYBER_NEON = new Color[]{new Color(0, 255, 230), new Color(255, 0, 128), new Color(190, 0, 255), Color.WHITE};
   private static final Color[] AURA_SAKURA = new Color[]{
      new Color(255, 182, 198), new Color(255, 214, 224), new Color(255, 240, 245), new Color(245, 140, 170)
   };
   private static final Color[] AURA_VOID_ABYSS = new Color[]{
      new Color(147, 51, 234), new Color(192, 132, 252), new Color(79, 70, 229), new Color(230, 180, 255)
   };
   private static final Color[] AURA_DIVINE_HALO = new Color[]{new Color(255, 215, 0), new Color(255, 245, 160), Color.WHITE, new Color(245, 195, 65)};
   private static final Color[] AURA_INFERNAL_EMBER = new Color[]{
      new Color(255, 69, 0), new Color(255, 140, 0), new Color(255, 215, 0), new Color(230, 25, 25)
   };
   private static final Color[] AURA_DEFAULT = new Color[]{Color.WHITE};
   private final List footprintList = new ArrayList();
   private final List particleList = new ArrayList();
   private final List auraParticleList = new ArrayList(36);
   private class_243 lastStepPos = null;
   private boolean isLeftStep = false;
   private int vieLogoImgId = -1;
   private int lastPlayerHurtTime = 0;
   private final AnimationFX.CamProj camProj = new AnimationFX.CamProj();
   private final float[] scratchP0 = new float[3];
   private final float[] scratchP1 = new float[3];
   private int fbW;
   private int fbH;
   private double scale;
   private final Random rng = new Random();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AnimationFX() {
      super(
         "AnimationFX",
         "Animated footprints with VIE logo and beautified glowing particles for CPVP and survival",
         Category.CUSTOMIZE
      );
      this.auraScale.visibleWhen(() -> this.auraPreset.get() != AnimationFX.AuraPreset.OFF);
      this.auraSpeed.visibleWhen(() -> this.auraPreset.get() != AnimationFX.AuraPreset.OFF);
      this.auraRings.visibleWhen(() -> this.auraPreset.get() != AnimationFX.AuraPreset.OFF);
      this.auraParticles.visibleWhen(() -> this.auraPreset.get() != AnimationFX.AuraPreset.OFF);
      this.footprintStyle.visibleWhen(this.footprints::get);
      this.footprintColor.visibleWhen(this.footprints::get);
      this.footprintGlow.visibleWhen(this.footprints::get);
      this.footprintDuration.setVisibleInGui(false);
      this.footprintScale.setVisibleInGui(false);
      this.particleScale.setVisibleInGui(false);
      this.glowIntensity.setVisibleInGui(false);
   }

   @Override
   public void onDisable() {
      this.footprintList.clear();
      this.particleList.clear();
      this.auraParticleList.clear();
      this.lastStepPos = null;
      super.onDisable();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isInGame() && mc.field_1724 != null) {
         long var2 = System.currentTimeMillis();
         this.footprintList.removeIf(f -> f.isExpired(var2));
         this.particleList.removeIf(p -> p.isExpired(var2));
         this.auraParticleList.removeIf(p -> p.isExpired(var2));

         for (AnimationFX.GlowParticle var5 : this.particleList) {
            var5.tick();
         }

         for (AnimationFX.AuraParticle var21 : this.auraParticleList) {
            var21.tick();
         }

         if ((Boolean)this.footprints.get() && mc.field_1724.method_24828()) {
            class_243 var18 = mc.field_1724.method_73189();
            if (this.lastStepPos == null) {
               this.lastStepPos = var18;
            } else {
               double var22 = var18.method_1022(this.lastStepPos);
               if (var22 >= 0.65) {
                  float var7 = mc.field_1724.method_36454();
                  double var8 = Math.toRadians(var7);
                  this.isLeftStep = !this.isLeftStep;
                  double var10 = this.isLeftStep ? -0.17 : 0.17;
                  double var12 = Math.cos(var8) * var10;
                  double var14 = Math.sin(var8) * var10;
                  class_243 var16 = new class_243(var18.field_1352 + var12, var18.field_1351 + 0.015, var18.field_1350 + var14);
                  if (this.footprintList.size() >= 64) {
                     this.footprintList.remove(0);
                  }

                  this.footprintList.add(new AnimationFX.Footprint(var16, var7, this.isLeftStep, var2, (long)((Double)this.footprintDuration.get() * 1000.0)));
                  this.lastStepPos = var18;
               }
            }
         }

         if ((Boolean)this.eatingFX.get() && mc.field_1724.method_6115()) {
            class_1799 var19 = mc.field_1724.method_6030();
            if (!var19.method_7960()
               && (var19.method_57826(class_9334.field_50075) || var19.method_57826(class_9334.field_49651))
               && mc.field_1724.method_6048() % 4 == 0) {
               this.spawnEatingParticles();
            }
         }

         if ((Boolean)this.miningFX.get()
            && mc.field_1761 != null
            && mc.field_1765 instanceof class_3965 var20
            && var20.method_17783() == class_240.field_1332
            && mc.field_1690.field_1886.method_1434()
            && this.rng.nextFloat() < 0.45F) {
            this.spawnMiningParticles(var20);
         }

         if ((Boolean)this.damageFX.get()) {
            if (mc.field_1724.field_6235 > 0 && this.lastPlayerHurtTime == 0) {
               this.spawnDamageBurst(mc.field_1724.method_73189().method_1031(0.0, 1.0, 0.0), 14, -54707);
            }

            this.lastPlayerHurtTime = mc.field_1724.field_6235;
         }

         if (this.auraPreset.get() != AnimationFX.AuraPreset.OFF && (Boolean)this.auraParticles.get() && mc.field_1724 != null) {
            this.spawnAuraParticles((AnimationFX.AuraPreset)this.auraPreset.get());
         }
      }
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (this.isInGame() && (Boolean)this.damageFX.get() && event.getTarget() != null) {
         if (event.getTarget() instanceof class_1309 var3) {
            this.spawnDamageBurst(var3.method_73189().method_1031(0.0, var3.method_17682() * 0.55, 0.0), 16, -51108);
         }
      }
   }

   @EventHandler
   public void onPacketReceive(ReceivePacketEvent event) {
      if (this.isInGame() && mc.field_1687 != null) {
         if ((Boolean)this.damageFX.get() && event.getPacket() instanceof class_8143 var2) {
            class_1297 var5 = mc.field_1687.method_8469(var2.comp_1267());
            if (var5 != null) {
               this.spawnDamageBurst(var5.method_73189().method_1031(0.0, var5.method_17682() * 0.5, 0.0), 14, -57792);
            }
         }

         if ((Boolean)this.placeFX.get() && event.getPacket() instanceof class_2626 var4) {
            class_2338 var7 = var4.method_11309();
            if (mc.field_1724 != null && mc.field_1724.method_5649(var7.method_10263(), var7.method_10264(), var7.method_10260()) < 36.0) {
               this.spawnBlockPlaceFX(var7);
            }
         }
      }
   }

   private void spawnEatingParticles() {
      if (this.particleList.size() < (160) && mc.field_1724 != null) {
         class_243 var1 = mc.field_1724.method_33571();
         class_243 var2 = mc.field_1724.method_5720();
         class_243 var3 = var2.method_1036(new class_243(0.0, 1.0, 0.0));
         double var4 = var3.method_1033();
         if (var4 > 1.0E-4) {
            var3 = var3.method_1021(1.0 / var4);
         }

         double var6 = mc.field_1724.method_6068() == class_1306.field_6183 ? 0.2 : -0.2;
         class_243 var8 = var1.method_1019(var2.method_1021(0.38)).method_1019(var3.method_1021(var6)).method_1031(0.0, -0.22, 0.0);
         class_1799 var9 = mc.field_1724.method_6030();
         boolean var10 = !var9.method_7960() && var9.method_57826(class_9334.field_49651);
         Color[] var11 = var10 ? POTION_PALETTE : FOOD_PALETTE;
         Color var12 = var11[this.rng.nextInt(var11.length)];
         double var13 = (this.rng.nextDouble() - 0.5) * 0.012;
         double var15 = 0.016 + this.rng.nextDouble() * 0.022;
         double var17 = (this.rng.nextDouble() - 0.5) * 0.012;
         this.particleList
            .add(
               new AnimationFX.GlowParticle(
                  var8.method_1031((this.rng.nextDouble() - 0.5) * 0.08, (this.rng.nextDouble() - 0.5) * 0.08, (this.rng.nextDouble() - 0.5) * 0.08),
                  new class_243(var13, var15, var17),
                  var12,
                  2.4F * (float)((Double)this.particleScale.get()).doubleValue(),
                  500L,
                  AnimationFX.ParticleType.EATING
               )
            );
      }
   }

   private void spawnMiningParticles(class_3965 bHit) {
      if (this.particleList.size() < (160)) {
         class_243 var2 = bHit.method_17784();
         Color var3 = new Color(
            (200) + this.rng.nextInt(55), (220) + this.rng.nextInt(35), 255
         );

         for (int var4 = 0; var4 < (2); var4++) {
            double var5 = (this.rng.nextDouble() - 0.5) * 0.06;
            double var7 = 0.03 + this.rng.nextDouble() * 0.04;
            double var9 = (this.rng.nextDouble() - 0.5) * 0.06;
            this.particleList
               .add(
                  new AnimationFX.GlowParticle(
                     var2.method_1031((this.rng.nextDouble() - 0.5) * 0.2, (this.rng.nextDouble() - 0.5) * 0.2, (this.rng.nextDouble() - 0.5) * 0.2),
                     new class_243(var5, var7, var9),
                     var3,
                     3.8F * (float)((Double)this.particleScale.get()).doubleValue(),
                     480L,
                     AnimationFX.ParticleType.MINING
                  )
               );
         }
      }
   }

   private void spawnDamageBurst(class_243 pos, int count, int colorHex) {
      Color var4 = new Color(colorHex);

      for (int var5 = 0; var5 < count && this.particleList.size() < (160); var5++) {
         double var6 = this.rng.nextDouble() * Math.PI * 2.0;
         double var8 = 0.08 + this.rng.nextDouble() * 0.14;
         double var10 = Math.cos(var6) * var8;
         double var12 = Math.sin(var6) * var8;
         double var14 = (this.rng.nextDouble() - 0.3) * 0.12;
         this.particleList
            .add(
               new AnimationFX.GlowParticle(
                  pos.method_1031((this.rng.nextDouble() - 0.5) * 0.25, (this.rng.nextDouble() - 0.5) * 0.25, (this.rng.nextDouble() - 0.5) * 0.25),
                  new class_243(var10, var14, var12),
                  var4,
                  4.8F * (float)((Double)this.particleScale.get()).doubleValue(),
                  620L,
                  AnimationFX.ParticleType.DAMAGE
               )
            );
      }
   }

   private void spawnBlockPlaceFX(class_2338 pos) {
      class_243 var2 = new class_243(pos.method_10263() + 0.5, pos.method_10264() + 0.5, pos.method_10260() + 0.5);
      Color var3 = new Color(0, 245, 255);

      for (int var4 = 0; var4 < (8) && this.particleList.size() < (160); var4++) {
         double var5 = (this.rng.nextDouble() - 0.5) * 0.07;
         double var7 = (this.rng.nextDouble() - 0.2) * 0.07;
         double var9 = (this.rng.nextDouble() - 0.5) * 0.07;
         this.particleList
            .add(
               new AnimationFX.GlowParticle(
                  var2.method_1031((this.rng.nextDouble() - 0.5) * 0.8, (this.rng.nextDouble() - 0.5) * 0.8, (this.rng.nextDouble() - 0.5) * 0.8),
                  new class_243(var5, var7, var9),
                  var3,
                  4.5F * (float)((Double)this.particleScale.get()).doubleValue(),
                  520L,
                  AnimationFX.ParticleType.PLACING
               )
            );
      }
   }

   private void spawnAuraParticles(AnimationFX.AuraPreset preset) {
      if (this.auraParticleList.size() < (32) && mc.field_1724 != null) {
         Color[] var2 = switch (preset) {
            case CELESTIAL -> AURA_CELESTIAL;
            case CYBER_NEON -> AURA_CYBER_NEON;
            case SAKURA -> AURA_SAKURA;
            case VOID_ABYSS -> AURA_VOID_ABYSS;
            case DIVINE_HALO -> AURA_DIVINE_HALO;
            case INFERNAL_EMBER -> AURA_INFERNAL_EMBER;
            default -> AURA_DEFAULT;
         };
         double var3 = (Double)this.auraScale.get();
         double var5 = (Double)this.auraSpeed.get();
         double var7 = this.rng.nextDouble() * (Math.PI * 2);
         double var9 = (0.4 + this.rng.nextDouble() * 0.42) * var3;
         double var11 = 0.08 + this.rng.nextDouble() * 0.4;
         double var13 = (this.rng.nextBoolean() ? 1.0 : -1.0) * (0.04 + this.rng.nextDouble() * 0.035) * var5;
         double var15 = (0.016 + this.rng.nextDouble() * 0.02) * var5;
         Color var17 = var2[this.rng.nextInt(var2.length)];
         float var18 = (preset == AnimationFX.AuraPreset.SAKURA ? 3.0F : 3.4F) * (float)((Double)this.particleScale.get()).doubleValue();
         long var19 = 950L + this.rng.nextInt(350);
         this.auraParticleList
            .add(
               new AnimationFX.AuraParticle(
                  var7, var9, var11, var13, var15, var17, var18, var19, this.rng.nextFloat() * 360.0F, (this.rng.nextFloat() - 0.5F) * 6.0F
               )
            );
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame() && mc.field_1724 != null) {
         boolean var2 = this.auraPreset.get() != AnimationFX.AuraPreset.OFF && ((Boolean)this.auraRings.get() || (Boolean)this.auraParticles.get());
         boolean var3 = !this.footprintList.isEmpty() || !this.particleList.isEmpty() || !this.auraParticleList.isEmpty() || var2;
         if (var3) {
            this.fbW = mc.method_22683().method_4489();
            this.fbH = mc.method_22683().method_4506();
            this.scale = mc.method_22683().method_4495();
            float var4 = event.tickDelta;
            double var5 = class_3532.method_16436(var4, mc.field_1724.field_6038, mc.field_1724.method_23317());
            double var7 = class_3532.method_16436(var4, mc.field_1724.field_5971, mc.field_1724.method_23318());
            double var9 = class_3532.method_16436(var4, mc.field_1724.field_5989, mc.field_1724.method_23321());

            try (NanoVGFrame var11 = NanoVGRenderer.frame()) {
               if (!var11.isActive()) {
                  return;
               }

               this.computeCamProj(this.fbW, this.fbH);
               long var12 = System.currentTimeMillis();
               float var14 = (float)this.scale;
               if (this.auraPreset.get() != AnimationFX.AuraPreset.OFF && (Boolean)this.auraRings.get()) {
                  this.renderAuraRings(var12, var14, var5, var7 + 0.02, var9);
               }

               if (this.auraPreset.get() != AnimationFX.AuraPreset.OFF && (Boolean)this.auraParticles.get()) {
                  this.renderAuraParticles(var12, var14, var5, var7, var9);
               }

               if ((Boolean)this.footprints.get()) {
                  this.renderFootprints(var12, var14);
               }

               this.renderGlowParticles(var12, var14);
            }
         }
      }
   }

   private void renderAuraRings(long now, float gs, double px, double py, double pz) {
      AnimationFX.AuraPreset var10 = (AnimationFX.AuraPreset)this.auraPreset.get();
      if (var10 != AnimationFX.AuraPreset.OFF && mc.field_1724 != null) {
         Color var11 = switch (var10) {
            case CELESTIAL -> new Color(0, 225, 255);
            case CYBER_NEON -> new Color(0, 255, 230);
            case SAKURA -> new Color(255, 182, 198);
            case VOID_ABYSS -> new Color(168, 85, 247);
            case DIVINE_HALO -> new Color(255, 215, 0);
            case INFERNAL_EMBER -> new Color(255, 95, 0);
            default -> Color.WHITE;
         };
         if (this.projectToScreen(px, py, pz, this.scratchP0)) {
            float var12 = this.scratchP0[0] / gs;
            float var13 = this.scratchP0[1] / gs;
            float var14 = this.getDistanceScale(this.scratchP0[2], 3.5F);
            float var15 = 48.0F * (float)((Double)this.auraScale.get()).doubleValue() * var14;
            float var16 = TrueStretched.getStretchFactor();
            boolean var17 = Math.abs(var16 - 1.0F) > 0.001F;
            if (var17) {
               NanoVGRenderer.save();
               NanoVGRenderer.translate(var12, var13);
               NanoVGRenderer.scale(var16, 1.0F);
               NanoVGRenderer.translate(-var12, -var13);
            }

            try {
               NanoVGRenderer.drawRadialGlow(var12, var13, var15, withAlpha(var11, 45));
            } finally {
               if (var17) {
                  NanoVGRenderer.restore();
               }
            }
         }

         double var39 = (Double)this.auraScale.get();
         double var40 = (Double)this.auraSpeed.get();
         double var41 = now * 0.001;
         float var18 = this.getDistanceScale(this.scratchP0[2], 3.5F);
         float var19 = (float)(0.85 * var39);
         byte var20 = 24;
         double var21 = var41 * 1.2 * var40;
         this.drawAuraCircle(px, py, pz, var19, var20, var21, gs, withAlpha(var11, 175), Math.max(0.5F, 1.4F * var18));
         float var23 = (float)(0.52 * var39);
         byte var24 = 16;
         double var25 = -var41 * 0.9 * var40;
         this.drawAuraCircle(px, py, pz, var23, var24, var25, gs, withAlpha(var11, 140), Math.max(0.5F, 1.1F * var18));

         for (int var27 = 0; var27 < 4; var27++) {
            double var28 = var21 + var27 * (Math.PI / 2);
            double var30 = px + Math.cos(var28) * var19;
            double var32 = pz + Math.sin(var28) * var19;
            if (this.projectToScreen(var30, py, var32, this.scratchP0)) {
               float var34 = this.scratchP0[0] / gs;
               float var35 = this.scratchP0[1] / gs;
               float var36 = this.getDistanceScale(this.scratchP0[2], 3.5F);
               NanoVGRenderer.drawCircle(var34, var35, Math.max(0.6F, 2.2F * var36), withAlpha(Color.WHITE, 230));
               NanoVGRenderer.drawCircleOutline(var34, var35, Math.max(1.0F, 3.8F * var36), Math.max(0.5F, 1.0F * var36), withAlpha(var11, 190));
            }
         }
      }
   }

   private void drawAuraCircle(double px, double py, double pz, float radius, int segments, double rot, float gs, Color color, float strokeWidth) {
      double var14 = (Math.PI * 2) / segments;
      NanoVGRenderer.beginLineBatch(color, strokeWidth);

      for (int var16 = 0; var16 < segments; var16++) {
         double var17 = rot + var16 * var14;
         double var19 = rot + (var16 + 1) * var14;
         double var21 = px + Math.cos(var17) * radius;
         double var23 = pz + Math.sin(var17) * radius;
         double var25 = px + Math.cos(var19) * radius;
         double var27 = pz + Math.sin(var19) * radius;
         if (this.projectToScreen(var21, py, var23, this.scratchP0) && this.projectToScreen(var25, py, var27, this.scratchP1)) {
            NanoVGRenderer.batchLine(this.scratchP0[0] / gs, this.scratchP0[1] / gs, this.scratchP1[0] / gs, this.scratchP1[1] / gs);
         }
      }

      NanoVGRenderer.endLineBatch();
   }

   private void renderAuraParticles(long now, float gs, double px, double py, double pz) {
      AnimationFX.AuraPreset var10 = (AnimationFX.AuraPreset)this.auraPreset.get();

      for (AnimationFX.AuraParticle var12 : this.auraParticleList) {
         double var13 = px + Math.cos(var12.relAngle) * var12.relRadius;
         double var15 = py + var12.relY;
         double var17 = pz + Math.sin(var12.relAngle) * var12.relRadius;
         if (this.projectToScreen(var13, var15, var17, this.scratchP0)) {
            float var19 = this.scratchP0[0] / gs;
            float var20 = this.scratchP0[1] / gs;
            float var21 = this.getDistanceScale(this.scratchP0[2], 3.5F);
            float var22 = var12.progress(now);
            float var23 = (float)Math.sin(var22 * Math.PI);
            int var24 = clamp((int)(var23 * 255.0F));
            if (var24 > 3) {
               Color var25 = withAlpha(var12.color, var24);
               float var26 = Math.max(0.5F, var12.radius * (0.85F + 0.35F * var23) * var21);
               float var27 = TrueStretched.getStretchFactor();
               boolean var28 = Math.abs(var27 - 1.0F) > 0.001F;
               if (var28) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var19, var20);
                  NanoVGRenderer.scale(var27, 1.0F);
                  NanoVGRenderer.translate(-var19, -var20);
               }

               try {
                  NanoVGRenderer.drawCircle(var19, var20, var26 * 1.8F, withAlpha(var12.color, (int)(55.0F * var23)));
                  if (var10 == AnimationFX.AuraPreset.SAKURA) {
                     float var29 = var26 * 1.6F;
                     float var30 = var26 * 0.9F;
                     float var31 = var12.rotation + (float)(now - var12.spawnMs) * 0.05F * var12.rotSpeed;
                     NanoVGRenderer.drawRotatedRoundedRect(var19 - var29 * 0.5F, var20 - var30 * 0.5F, var29, var30, var30 * 0.5F, var25, var31);
                     NanoVGRenderer.drawCircle(var19, var20, Math.max(0.5F, var26 * 0.25F), withAlpha(Color.WHITE, (int)(200.0F * var23)));
                  } else if (var10 == AnimationFX.AuraPreset.CYBER_NEON) {
                     float var35 = var26 * 1.25F;
                     NanoVGRenderer.drawRotatedRoundedRect(var19 - var35 * 0.5F, var20 - var35 * 0.5F, var35, var35, Math.max(0.5F, var35 * 0.2F), var25, 45.0F);
                     NanoVGRenderer.drawCircle(var19, var20, Math.max(0.5F, var35 * 0.35F), withAlpha(Color.WHITE, (int)(240.0F * var23)));
                  } else {
                     NanoVGRenderer.drawCircle(var19, var20, var26, var25);
                     NanoVGRenderer.drawCircle(var19, var20, Math.max(0.5F, var26 * 0.45F), withAlpha(Color.WHITE, (int)(240.0F * var23)));
                  }
               } finally {
                  if (var28) {
                     NanoVGRenderer.restore();
                  }
               }
            }
         }
      }
   }

   private void renderFootprints(long now, float gs) {
      AnimationFX.FootprintStyle var4 = (AnimationFX.FootprintStyle)this.footprintStyle.get();
      Color var5 = new Color((Integer)this.footprintColor.get());
      boolean var6 = (Boolean)this.footprintGlow.get();
      float var7 = (float)((Double)this.footprintScale.get()).doubleValue();
      if (var4 == AnimationFX.FootprintStyle.VIE_LOGO && this.vieLogoImgId == -1) {
         this.vieLogoImgId = NVGImage.loadImageWhite(
            "assets/vie_client/vie/textures/icons/logovie.png"
         );
      }

      for (AnimationFX.Footprint var9 : this.footprintList) {
         if (this.projectToScreen(var9.pos.field_1352, var9.pos.field_1351, var9.pos.field_1350, this.scratchP0)) {
            float var10 = this.scratchP0[0] / gs;
            float var11 = this.scratchP0[1] / gs;
            float var12 = this.getDistanceScale(this.scratchP0[2], 3.5F);
            float var13 = var9.progress(now);
            float var14 = (float)Math.pow(1.0F - var13, 1.4);
            int var15 = clamp((int)(var14 * 255.0F));
            if (var15 > 3) {
               Color var16 = withAlpha(var5, var15);
               float var17 = Math.max(2.0F, 18.0F * var7 * var12);
               float var18 = TrueStretched.getStretchFactor();
               boolean var19 = Math.abs(var18 - 1.0F) > 0.001F;
               if (var19) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var10, var11);
                  NanoVGRenderer.scale(var18, 1.0F);
                  NanoVGRenderer.translate(-var10, -var11);
               }

               try {
                  switch (var4) {
                     case VIE_LOGO:
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var17 * 1.5F, withAlpha(var5, (int)(70.0F * var14)));
                        }

                        if (this.vieLogoImgId != -1) {
                           NVGImage.drawImage(this.vieLogoImgId, var10 - var17 / 2.0F, var11 - var17 / 2.0F, var17, var17, var16);
                        } else {
                           this.drawVectorVieLogo(var10, var11, var17, var16);
                        }
                        break;
                     case BOOT_PRINT:
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var17 * 1.3F, withAlpha(var5, (int)(60.0F * var14)));
                        }

                        this.drawBootPrint(var10, var11, var17, var9.yaw, var9.isLeft, var16);
                        break;
                     case PAW_PRINT:
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var17 * 1.3F, withAlpha(var5, (int)(65.0F * var14)));
                        }

                        this.drawPawPrint(var10, var11, var17, var9.yaw, var16);
                        break;
                     case NEON_CIRCLE:
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var17 * 1.6F, withAlpha(var5, (int)(85.0F * var14)));
                        }

                        NanoVGRenderer.drawCircleOutline(var10, var11, var17 * 0.45F, 1.4F, var16);
                        NanoVGRenderer.drawCircle(var10, var11, 2.5F, withAlpha(Color.WHITE, var15));
                        break;
                     case ENERGY_PULSE:
                        float var20 = var17 * (0.35F + 0.85F * var13);
                        NanoVGRenderer.drawCircleOutline(var10, var11, var20, 1.5F, var16);
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var20 * 1.4F, withAlpha(var5, (int)(60.0F * var14)));
                        }
                        break;
                     case CUSTOM:
                        if (var6) {
                           NanoVGRenderer.drawRadialGlow(var10, var11, var17 * 1.5F, withAlpha(var5, (int)(80.0F * var14)));
                        }

                        this.drawRunePolygon(var10, var11, var17 * 0.5F, 6, var16);
                        NanoVGRenderer.drawCircle(var10, var11, 2.2F, withAlpha(Color.WHITE, var15));
                  }
               } finally {
                  if (var19) {
                     NanoVGRenderer.restore();
                  }
               }
            }
         }
      }
   }

   private void renderGlowParticles(long now, float gs) {
      boolean var4 = (Boolean)this.particleGlow.get();
      float var5 = (float)((Double)this.glowIntensity.get() / 255.0);

      for (AnimationFX.GlowParticle var7 : this.particleList) {
         if (this.projectToScreen(var7.pos.field_1352, var7.pos.field_1351, var7.pos.field_1350, this.scratchP0)) {
            float var8 = this.scratchP0[0] / gs;
            float var9 = this.scratchP0[1] / gs;
            float var10 = this.getDistanceScale(this.scratchP0[2], 3.5F);
            float var11 = var7.progress(now);
            float var12 = (float)Math.pow(1.0F - var11, 1.3);
            int var13 = clamp((int)(var12 * 255.0F));
            if (var13 > (2)) {
               Color var14 = withAlpha(var7.color, var13);
               float var15 = Math.max(0.6F, var7.radius * (1.0F - var11 * 0.3F) * var10);
               float var16 = TrueStretched.getStretchFactor();
               boolean var17 = Math.abs(var16 - 1.0F) > 0.001F;
               if (var17) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var8, var9);
                  NanoVGRenderer.scale(var16, 1.0F);
                  NanoVGRenderer.translate(-var8, -var9);
               }

               try {
                  if (var4) {
                     float var18 = var7.type == AnimationFX.ParticleType.EATING ? var15 * 2.8F : var15 * 4.2F;
                     int var19 = clamp((int)(var13 * var5 * (var7.type == AnimationFX.ParticleType.EATING ? 0.35F : 0.45F)));
                     NanoVGRenderer.drawRadialGlow(var8, var9, var18, withAlpha(var7.color, var19));
                  }

                  if (var7.type == AnimationFX.ParticleType.EATING) {
                     this.drawEatingSparkle(var8, var9, var15, var14, var12);
                  } else {
                     NanoVGRenderer.drawCircle(var8, var9, var15, var14);
                     NanoVGRenderer.drawCircle(var8, var9, Math.max(1.0F, var15 * 0.45F), withAlpha(Color.WHITE, (int)(220.0F * var12)));
                  }
               } finally {
                  if (var17) {
                     NanoVGRenderer.restore();
                  }
               }
            }
         }
      }
   }

   private void drawEatingSparkle(float cx, float cy, float r, Color col, float alpha) {
      float var6 = r * 1.5F;
      NanoVGRenderer.drawRotatedRoundedRect(cx - var6 * 0.5F, cy - var6 * 0.5F, var6, var6, Math.max(0.5F, var6 * 0.18F), col, 45.0F);
      float var7 = var6 * 1.25F;
      NanoVGRenderer.drawLine(cx, cy - var7, cx, cy + var7, 1.0F, col);
      NanoVGRenderer.drawLine(cx - var7, cy, cx + var7, cy, 1.0F, col);
      NanoVGRenderer.drawCircle(cx, cy, Math.max(0.8F, var6 * 0.28F), withAlpha(Color.WHITE, (int)(240.0F * alpha)));
   }

   private void drawBootPrint(float cx, float cy, float s, float yaw, boolean isLeft, Color col) {
      float var7 = s * 0.38F;
      float var8 = s * 0.28F;
      NanoVGRenderer.drawRoundedRect(cx - var7 / 2.0F, cy + s * 0.16F, var7, var8, 2.0F, col);
      float var9 = s * 0.42F;
      float var10 = s * 0.46F;
      NanoVGRenderer.drawRoundedRect(cx - var9 / 2.0F, cy - s * 0.38F, var9, var10, 3.0F, col);
   }

   private void drawPawPrint(float cx, float cy, float s, float yaw, Color col) {
      NanoVGRenderer.drawCircle(cx, cy + s * 0.08F, s * 0.24F, col);
      float var6 = s * 0.1F;
      NanoVGRenderer.drawCircle(cx - s * 0.22F, cy - s * 0.18F, var6, col);
      NanoVGRenderer.drawCircle(cx - s * 0.08F, cy - s * 0.28F, var6, col);
      NanoVGRenderer.drawCircle(cx + s * 0.08F, cy - s * 0.28F, var6, col);
      NanoVGRenderer.drawCircle(cx + s * 0.22F, cy - s * 0.18F, var6, col);
   }

   private void drawVectorVieLogo(float cx, float cy, float s, Color col) {
      float var5 = s * 0.45F;
      NanoVGRenderer.drawCircleOutline(cx, cy, var5, 1.4F, col);
      NanoVGRenderer.drawLine(cx - var5 * 0.6F, cy - var5 * 0.3F, cx, cy + var5 * 0.6F, 1.6F, col);
      NanoVGRenderer.drawLine(cx, cy + var5 * 0.6F, cx + var5 * 0.6F, cy - var5 * 0.3F, 1.6F, col);
   }

   private void drawRunePolygon(float cx, float cy, float r, int sides, Color col) {
      double var6 = (Math.PI * 2) / sides;

      for (int var8 = 0; var8 < sides; var8++) {
         double var9 = var8 * var6;
         double var11 = (var8 + 1) * var6;
         float var13 = cx + (float)Math.cos(var9) * r;
         float var14 = cy + (float)Math.sin(var9) * r;
         float var15 = cx + (float)Math.cos(var11) * r;
         float var16 = cy + (float)Math.sin(var11) * r;
         NanoVGRenderer.drawLine(var13, var14, var15, var16, 1.4F, col);
      }
   }

   private void computeCamProj(int fw, int fh) {
      class_4184 var3 = mc.field_1773.method_19418();
      class_243 var4 = var3.method_71156();
      double var5 = Math.toRadians(var3.method_19330() + 180.0);
      double var7 = Math.toRadians(var3.method_19329());
      float var9 = (float)Math.tan(Math.toRadians(((Integer)mc.field_1690.method_41808().method_41753()).intValue()) / 2.0);
      float var10 = TrueStretched.getEffectiveAspectRatio(fw, fh);
      this.camProj.update(var4.field_1352, var4.field_1351, var4.field_1350, Math.cos(var5), Math.sin(var5), Math.cos(var7), Math.sin(var7), var9, var10);
   }

   private boolean projectToScreen(double wx, double wy, double wz, float[] out) {
      float var8 = (float)(wx - this.camProj.camX);
      float var9 = (float)(wy - this.camProj.camY);
      float var10 = (float)(wz - this.camProj.camZ);
      float var11 = (float)(var8 * this.camProj.cosYaw + var10 * this.camProj.sinYaw);
      float var12 = var9;
      float var13 = (float)(-var8 * this.camProj.sinYaw + var10 * this.camProj.cosYaw);
      float var14 = (float)(var12 * this.camProj.cosPitch - var13 * this.camProj.sinPitch);
      float var15 = (float)(var12 * this.camProj.sinPitch + var13 * this.camProj.cosPitch);
      if (var15 >= -0.05F) {
         return false;
      }

      out[0] = (float)(var11 / -var15 / (this.camProj.tanHalfFov * this.camProj.aspect) * (this.fbW / 2.0) + this.fbW / 2.0);
      out[1] = (float)(-var14 / -var15 / this.camProj.tanHalfFov * (this.fbH / 2.0) + this.fbH / 2.0);
      if (out.length >= (3)) {
         out[2] = -var15;
      }

      return true;
   }

   private float getDistanceScale(float depth, float refDist) {
      if (depth <= 0.001F) {
         return 1.0F;
      }

      float var3 = refDist / Math.max(refDist * 0.35F, depth);
      return Math.max(0.15F, Math.min(1.8F, var3));
   }

   private float getDistanceScale(float depth) {
      return this.getDistanceScale(depth, 3.5F);
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }

   private static Color withAlpha(Color c, int a) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), clamp(a));
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

   private static final class AuraParticle {
      double relAngle;
      double relRadius;
      double relY;
      final double orbitSpeed;
      final double riseSpeed;
      final Color color;
      final float radius;
      final long spawnMs;
      final long durationMs;
      final float rotation;
      final float rotSpeed;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      AuraParticle(
         double relAngle,
         double relRadius,
         double relY,
         double orbitSpeed,
         double riseSpeed,
         Color color,
         float radius,
         long durationMs,
         float rotation,
         float rotSpeed
      ) {
         this.relAngle = relAnglex;
         this.relRadius = relRadiusx;
         this.relY = relYx;
         this.orbitSpeed = orbitSpeedx;
         this.riseSpeed = riseSpeedx;
         this.color = colorx;
         this.radius = radiusx;
         this.spawnMs = System.currentTimeMillis();
         this.durationMs = durationMsx;
         this.rotation = rotationx;
         this.rotSpeed = rotSpeedx;
      }

      void tick() {
         this.relAngle = this.relAngle + this.orbitSpeed;
         this.relY = this.relY + this.riseSpeed;
      }

      float progress(long now) {
         return Math.min(1.0F, (float)(now - this.spawnMs) / (float)this.durationMs);
      }

      boolean isExpired(long now) {
         return now - this.spawnMs >= this.durationMs || this.relY > 2.2;
      }
   }

   public enum AuraPreset {
      OFF("Off"),
      CELESTIAL("Celestial"),
      CYBER_NEON("Cyberpunk Neon"),
      SAKURA("Sakura Blossom"),
      VOID_ABYSS("Void Abyss"),
      DIVINE_HALO("Divine Halo"),
      INFERNAL_EMBER("Infernal Ember");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      AuraPreset(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static AnimationFX.AuraPreset[] $values() {
         AnimationFX.AuraPreset[] var10000 = new AnimationFX.AuraPreset[7];
         var10000[0] = OFF;
         var10000[1] = CELESTIAL;
         var10000[2] = CYBER_NEON;
         var10000[3] = SAKURA;
         var10000[4] = VOID_ABYSS;
         var10000[5] = DIVINE_HALO;
         var10000[6] = INFERNAL_EMBER;
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

   private static final class Footprint {
      final class_243 pos;
      final float yaw;
      final boolean isLeft;
      final long spawnMs;
      final long durationMs;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Footprint(class_243 pos, float yaw, boolean isLeft, long spawnMs, long durationMs) {
         this.pos = posx;
         this.yaw = yawx;
         this.isLeft = isLeftx;
         this.spawnMs = spawnMsx;
         this.durationMs = durationMsx;
      }

      float progress(long now) {
         return Math.min(1.0F, (float)(now - this.spawnMs) / (float)this.durationMs);
      }

      boolean isExpired(long now) {
         return now - this.spawnMs >= this.durationMs;
      }
   }

   public enum FootprintStyle {
      VIE_LOGO("VIE Logo"),
      BOOT_PRINT("Boot Print"),
      PAW_PRINT("Paw Print"),
      NEON_CIRCLE("Neon Circle"),
      ENERGY_PULSE("Energy Pulse"),
      CUSTOM("Custom Rune");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      FootprintStyle(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static AnimationFX.FootprintStyle[] $values() {
         AnimationFX.FootprintStyle[] var10000 = new AnimationFX.FootprintStyle[6];
         var10000[0] = VIE_LOGO;
         var10000[1] = BOOT_PRINT;
         var10000[2] = PAW_PRINT;
         var10000[3] = NEON_CIRCLE;
         var10000[4] = ENERGY_PULSE;
         var10000[5] = CUSTOM;
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

   private static final class GlowParticle {
      class_243 pos;
      class_243 vel;
      final Color color;
      final float radius;
      final long spawnMs;
      final long durationMs;
      final AnimationFX.ParticleType type;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      GlowParticle(class_243 pos, class_243 vel, Color color, float radius, long durationMs, AnimationFX.ParticleType type) {
         this.pos = posx;
         this.vel = velx;
         this.color = colorx;
         this.radius = radiusx;
         this.spawnMs = System.currentTimeMillis();
         this.durationMs = durationMsx;
         this.type = typex;
      }

      void tick() {
         this.pos = this.pos.method_1019(this.vel);
         this.vel = new class_243(
            this.vel.field_1352 * 0.94,
            this.vel.field_1351 * 0.96 + (this.type == AnimationFX.ParticleType.DAMAGE ? -0.004 : 0.001),
            this.vel.field_1350 * 0.94
         );
      }

      float progress(long now) {
         return Math.min(1.0F, (float)(now - this.spawnMs) / (float)this.durationMs);
      }

      boolean isExpired(long now) {
         return now - this.spawnMs >= this.durationMs;
      }
   }

   public enum ParticleType {
      EATING,
      DAMAGE,
      MINING,
      PLACING;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AnimationFX.ParticleType[] $values() {
         AnimationFX.ParticleType[] var10000 = new AnimationFX.ParticleType[4];
         var10000[0] = EATING;
         var10000[1] = DAMAGE;
         var10000[2] = MINING;
         var10000[3] = PLACING;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(31);
         var10002[5] = (byte)(53);
         EATING = new AnimationFX.ParticleType();
         var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(22);
         var10002[5] = (byte)(55);
         DAMAGE = new AnimationFX.ParticleType();
         var10002 = new byte[6];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(31);
         var10002[5] = (byte)(53);
         MINING = new AnimationFX.ParticleType();
         var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(24);
         var10002[5] = (byte)(60);
         var10002[6] = (byte)(-2);
         PLACING = new AnimationFX.ParticleType();
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
