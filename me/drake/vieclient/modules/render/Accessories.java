package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import org.joml.Matrix4f;

public class Accessories extends Module {
   private final BoolSetting wings = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Wings",
         "Render deeply articulated 3D wings anchored to your spine",
         true
      )
   );
   private final EnumSetting wingType = (EnumSetting)this.addSetting(
      new EnumSetting(
         "WingType",
         "Style of wings to render",
         Accessories.WingType.ANGEL
      )
   );
   private final BoolSetting halo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Halo",
         "Render a floating glowing halo above your head",
         false
      )
   );
   private final BoolSetting devilHorns = (BoolSetting)this.addSetting(
      new BoolSetting(
         "DevilHorns",
         "Render curved devil horns on your forehead",
         false
      )
   );
   private final BoolSetting catEars = (BoolSetting)this.addSetting(
      new BoolSetting(
         "CatEars",
         "Render cute anime neko cat ears with flick animation",
         false
      )
   );
   private final BoolSetting witchHat = (BoolSetting)this.addSetting(
      new BoolSetting(
         "WitchHat",
         "Render a pointed wizard/witch hat with gold buckle",
         false
      )
   );
   private final BoolSetting crown = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crown",
         "Render a royal golden gem-studded crown",
         false
      )
   );
   private final BoolSetting katana = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Katana",
         "Render a sheathed samurai katana diagonally across back",
         false
      )
   );
   private final BoolSetting backpack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Backpack",
         "Render an adventurer leather backpack with bedroll",
         false
      )
   );
   private final BoolSetting bandana = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Bandana",
         "Render a shinobi headband with wind-trailing ribbons",
         false
      )
   );
   private final BoolSetting tail = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tail",
         "Render an animated fluffy cat/fox tail on your lower back",
         false
      )
   );
   private final BoolSetting cyberVisor = (BoolSetting)this.addSetting(
      new BoolSetting(
         "CyberVisor",
         "Render a futuristic glowing HUD visor across your eyes",
         false
      )
   );
   private final BoolSetting scarf = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Scarf",
         "Render a cozy winter wrapped scarf with wind-fluttering tails",
         false
      )
   );
   private final BoolSetting dualKatana = (BoolSetting)this.addSetting(
      new BoolSetting(
         "DualKatana",
         "Render twin crossed samurai swords across your back",
         false
      )
   );
   private final BoolSetting magicAura = (BoolSetting)this.addSetting(
      new BoolSetting(
         "MagicAura",
         "Render a rotating sacred magic runic circle around you",
         false
      )
   );
   private final DoubleSetting scale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Scale",
         "Overall accessory size scale multiplier",
         1.0,
         0.5,
         2.0,
         0.05
      )
   );
   private final ColorSetting color = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Color",
         "Primary accent color for accessories",
         -16720641
      )
   );
   private final BoolSetting chroma = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Chroma",
         "Cycle colors smoothly in rainbow RGB",
         false
      )
   );
   private static final class_2960 WHITE_TEX;
   private static final float U = 0.0625F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Accessories() {
      super(
         "Accessories",
         "Customizable 3D cosmetics and accessories for your player avatar",
         Category.CUSTOMIZE
      );
      this.wingType.visibleWhen(this.wings::get);
   }

   public boolean shouldApplyTo(class_10055 state) {
      if (!this.isEnabled()) {
         return false;
      } else {
         return mc.field_1724 != null && state != null ? state.field_53528 == mc.field_1724.method_5628() || state.field_53528 == (-6969) : false;
      }
   }

   private int getEffectiveColor(float age, int fallbackArgb) {
      if ((Boolean)this.chroma.get()) {
         float var5 = (float)(System.currentTimeMillis() % 4000L / 4000.0);
         int var4 = Color.HSBtoRGB(var5, 0.85F, 1.0F);
         return -16777216 | var4 & (16777215);
      } else {
         Integer var3 = (Integer)this.color.get();
         return var3 != null ? var3 : fallbackArgb;
      }
   }

   public void renderAccessories(class_10055 state, class_4587 matrices, class_11659 queue) {
      int var4 = state.field_61820;
      int var5 = class_4608.field_21444;
      float var6 = state.field_53328;
      float var7 = state.field_53450;
      float var8 = Math.min(1.0F, state.field_53451);
      float var9 = state.field_53447;
      float var10 = state.field_53448;
      boolean var11 = state.field_53410;
      float var12 = ((Double)this.scale.get()).floatValue();
      queue.method_73483(
         matrices,
         class_12249.method_75994(WHITE_TEX),
         (entry, vc) -> {
            class_4587 var12x = new class_4587();
            var12x.method_23760().method_66521(entry);
            var12x.method_22903();
            if (var11) {
               var12x.method_46416(0.0F, 0.2F, 0.0F);
            }

            var12x.method_22905(var12, var12, var12);
            if ((Boolean)this.halo.get()
               || (Boolean)this.devilHorns.get()
               || (Boolean)this.catEars.get()
               || (Boolean)this.witchHat.get()
               || (Boolean)this.crown.get()
               || (Boolean)this.bandana.get()
               || (Boolean)this.cyberVisor.get()) {
               var12x.method_22903();
               var12x.method_22907(class_7833.field_40716.rotationDegrees(var9));
               var12x.method_22907(class_7833.field_40714.rotationDegrees(var10));
               if ((Boolean)this.halo.get()) {
                  this.renderHalo(var12x, vc, var6, var4, var5);
               }

               if ((Boolean)this.devilHorns.get()) {
                  this.renderDevilHorns(var12x, vc, var6, var4, var5);
               }

               if ((Boolean)this.catEars.get()) {
                  this.renderCatEars(var12x, vc, var6, var4, var5);
               }

               if ((Boolean)this.witchHat.get()) {
                  this.renderWitchHat(var12x, vc, var6, var4, var5);
               }

               if ((Boolean)this.crown.get()) {
                  this.renderCrown(var12x, vc, var6, var4, var5);
               }

               if ((Boolean)this.bandana.get()) {
                  this.renderBandana(var12x, vc, var6, var7, var8, var4, var5);
               }

               if ((Boolean)this.cyberVisor.get()) {
                  this.renderCyberVisor(var12x, vc, var6, var4, var5);
               }

               var12x.method_22909();
            }

            var12x.method_22903();
            if (var11) {
               var12x.method_22907(class_7833.field_40714.rotationDegrees(28.0F));
            }

            if ((Boolean)this.scarf.get()) {
               this.renderScarf(var12x, vc, var6, var7, var8, var4, var5);
            }

            if ((Boolean)this.wings.get()) {
               this.renderWings(var12x, vc, var6, var7, var8, var4, var5);
            }

            if ((Boolean)this.katana.get()) {
               this.renderKatana(var12x, vc, var6, var4, var5);
            }

            if ((Boolean)this.dualKatana.get()) {
               this.renderDualKatana(var12x, vc, var6, var4, var5);
            }

            if ((Boolean)this.backpack.get()) {
               this.renderBackpack(var12x, vc, var6, var4, var5);
            }

            if ((Boolean)this.tail.get()) {
               this.renderTail(var12x, vc, var6, var7, var8, var4, var5);
            }

            if ((Boolean)this.magicAura.get()) {
               this.renderMagicAura(var12x, vc, var6, var4, var5);
            }

            var12x.method_22909();
            var12x.method_22909();
         }
      );
   }

   private void renderHalo(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = this.getEffectiveColor(age, -10496);
      int var7 = -1426064986;
      m.method_22903();
      float var8 = (float)Math.sin(age * 0.08) * 0.5F;
      m.method_46416(0.0F, (-12.0F + var8) * 0.0625F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(age * 1.8F));
      m.method_22907(class_7833.field_40714.rotationDegrees(8.0F));
      int var9 = 12;
      float var10 = 5.2F;
      float var11 = 2.8F;

      for (int var12 = 0; var12 < var9; var12++) {
         float var13 = (float)(var12 * 2.0 * Math.PI / var9);
         float var14 = (float)Math.cos(var13) * var10;
         float var15 = (float)Math.sin(var13) * var10;
         m.method_22903();
         m.method_46416(var14 * 0.0625F, 0.0F, var15 * 0.0625F);
         m.method_22907(class_7833.field_40716.rotationDegrees((float)Math.toDegrees(-var13) + 90.0F));
         box(m, vc, -var11 * 0.5F, -0.45F, -0.45F, var11, 0.9F, 0.9F, var6, l, o);
         box(m, vc, -var11 * 0.5F, -0.65F, -0.65F, var11, 1.3F, 1.3F, var7, l, o);
         m.method_22909();
      }

      m.method_22909();
   }

   private void renderDevilHorns(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = this.getEffectiveColor(age, -3403503);
      int var7 = -11205371;
      int var8 = -48060;

      for (int var9 = -1; var9 <= 1; var9 += 2) {
         m.method_22903();
         m.method_46416(var9 * 2.8F * 0.0625F, -0.46875F, -0.1375F);
         m.method_22907(class_7833.field_40718.rotationDegrees(var9 * -18.0F));
         m.method_22907(class_7833.field_40714.rotationDegrees(-24.0F));
         box(m, vc, -0.9F, -1.8F, -0.9F, 1.8F, 1.8F, 1.8F, var7, l, o);
         m.method_46416(0.0F, -0.1F, -0.0125F);
         m.method_22907(class_7833.field_40718.rotationDegrees(var9 * 14.0F));
         m.method_22907(class_7833.field_40714.rotationDegrees(12.0F));
         box(m, vc, -0.7F, -1.6F, -0.7F, 1.4F, 1.6F, 1.4F, var6, l, o);
         m.method_46416(0.0F, -0.0875F, -0.0125F);
         m.method_22907(class_7833.field_40718.rotationDegrees(var9 * 12.0F));
         m.method_22907(class_7833.field_40714.rotationDegrees(14.0F));
         box(m, vc, -0.45F, -1.4F, -0.45F, 0.9F, 1.4F, 0.9F, var8, l, o);
         m.method_22909();
      }
   }

   private void renderCatEars(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = this.getEffectiveColor(age, -14013910);
      int var7 = -18751;
      int var8 = -131330;
      float var9 = Math.sin(age * 0.05) > 0.88 ? (float)Math.sin(age * 0.6) * 16.0F : 0.0F;
      float var10 = Math.sin(age * 0.05 + 2.0) > 0.88 ? (float)Math.sin(age * 0.6) * 16.0F : 0.0F;

      for (int var11 = -1; var11 <= 1; var11 += 2) {
         m.method_22903();
         m.method_46416(var11 * 2.8F * 0.0625F, -0.5F, 0.0F);
         m.method_22907(class_7833.field_40718.rotationDegrees(var11 * 12.0F + (var11 == 1 ? var9 : var10)));
         box(m, vc, -1.6F, -1.2F, -1.2F, 3.2F, 1.2F, 2.4F, var6, l, o);
         box(m, vc, -1.2F, -2.4F, -1.0F, 2.4F, 1.2F, 2.0F, var6, l, o);
         box(m, vc, -0.8F, -3.4F, -0.8F, 1.6F, 1.0F, 1.6F, var6, l, o);
         box(m, vc, -0.4F, -4.2F, -0.5F, 0.8F, 0.8F, 1.0F, var6, l, o);
         box(m, vc, -0.9F, -2.2F, -1.25F, 1.8F, 1.6F, 0.3F, var7, l, o);
         box(m, vc, -0.5F, -3.2F, -1.05F, 1.0F, 1.0F, 0.3F, var7, l, o);
         box(m, vc, -1.4F, -0.8F, -1.35F, 0.8F, 0.7F, 0.4F, var8, l, o);
         box(m, vc, 0.6F, -0.8F, -1.35F, 0.8F, 0.7F, 0.4F, var8, l, o);
         m.method_22909();
      }
   }

   private void renderWitchHat(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -14412739;
      int var7 = this.getEffectiveColor(age, -6543440);
      int var8 = -10496;
      m.method_22903();
      m.method_46416(0.0F, -0.5F, 0.0F);
      m.method_22907(class_7833.field_40714.rotationDegrees(-4.0F));
      box(m, vc, -6.5F, -0.6F, -6.5F, 13.0F, 0.6F, 13.0F, var6, l, o);
      box(m, vc, -5.5F, -1.0F, -5.5F, 11.0F, 0.4F, 11.0F, var6, l, o);
      box(m, vc, -4.2F, -2.2F, -4.2F, 8.4F, 1.2F, 8.4F, var7, l, o);
      box(m, vc, -1.2F, -2.4F, -4.35F, 2.4F, 1.6F, 0.3F, var8, l, o);
      box(m, vc, -0.6F, -2.1F, -4.4F, 1.2F, 1.0F, 0.2F, var7, l, o);
      box(m, vc, -4.0F, -4.4F, -4.0F, 8.0F, 2.2F, 8.0F, var6, l, o);
      m.method_46416(0.0F, -0.275F, 0.0F);
      m.method_22907(class_7833.field_40714.rotationDegrees(8.0F));
      box(m, vc, -3.2F, -2.4F, -3.2F, 6.4F, 2.4F, 6.4F, var6, l, o);
      m.method_46416(0.0F, -0.15F, 0.0F);
      m.method_22907(class_7833.field_40714.rotationDegrees(12.0F));
      box(m, vc, -2.2F, -2.2F, -2.2F, 4.4F, 2.2F, 4.4F, var6, l, o);
      m.method_46416(0.0F, -0.1375F, 0.0F);
      m.method_22907(class_7833.field_40714.rotationDegrees(16.0F));
      box(m, vc, -1.4F, -2.0F, -1.4F, 2.8F, 2.0F, 2.8F, var6, l, o);
      box(m, vc, -0.7F, -3.2F, -0.7F, 1.4F, 1.2F, 1.4F, var6, l, o);
      m.method_22909();
   }

   private void renderCrown(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -10496;
      int var7 = -2839539;
      int var8 = -59580;
      int var9 = -14059009;
      int var10 = -16718218;
      m.method_22903();
      m.method_46416(0.0F, -0.5125F, 0.0F);
      box(m, vc, -4.4F, -1.6F, -4.4F, 8.8F, 1.6F, 8.8F, var6, l, o);
      box(m, vc, -4.1F, -1.7F, -4.1F, 8.2F, 1.8F, 8.2F, var7, l, o);
      box(m, vc, -1.0F, -4.8F, -4.6F, 2.0F, 3.2F, 0.5F, var6, l, o);
      box(m, vc, -0.6F, -4.2F, -4.75F, 1.2F, 1.4F, 0.25F, var8, l, o);
      box(m, vc, -3.8F, -4.0F, -4.6F, 1.8F, 2.4F, 0.5F, var6, l, o);
      box(m, vc, -3.4F, -3.5F, -4.75F, 1.0F, 1.2F, 0.25F, var9, l, o);
      box(m, vc, 2.0F, -4.0F, -4.6F, 1.8F, 2.4F, 0.5F, var6, l, o);
      box(m, vc, 2.4F, -3.5F, -4.75F, 1.0F, 1.2F, 0.25F, var10, l, o);
      box(m, vc, -4.6F, -3.8F, -1.0F, 0.5F, 2.2F, 2.0F, var6, l, o);
      box(m, vc, 4.1F, -3.8F, -1.0F, 0.5F, 2.2F, 2.0F, var6, l, o);
      m.method_22909();
   }

   private void renderBandana(class_4587 m, class_4588 vc, float age, float swing, float amp, int l, int o) {
      int var8 = this.getEffectiveColor(age, -15064194);
      int var9 = -3155748;
      int var10 = -13154481;
      m.method_22903();
      m.method_46416(0.0F, -0.46875F, 0.0F);
      box(m, vc, -4.25F, -0.8F, -4.25F, 8.5F, 1.6F, 8.5F, var8, l, o);
      box(m, vc, -2.6F, -0.6F, -4.45F, 5.2F, 1.4F, 0.35F, var9, l, o);
      box(m, vc, -0.8F, -0.3F, -4.55F, 1.6F, 0.8F, 0.15F, var10, l, o);
      box(m, vc, -2.3F, -0.2F, -4.5F, 0.35F, 0.35F, 0.1F, var10, l, o);
      box(m, vc, 1.95F, -0.2F, -4.5F, 0.35F, 0.35F, 0.1F, var10, l, o);
      box(m, vc, -0.9F, -0.6F, 4.15F, 1.8F, 1.4F, 0.7F, var8, l, o);
      float var11 = (float)Math.sin(age * 0.22 + swing * 0.5) * 0.25F;
      float var12 = (float)Math.cos(age * 0.18 + swing * 0.4) * 0.22F;
      m.method_22903();
      m.method_46416(-0.03125F, 0.0125F, 0.2875F);
      m.method_22907(class_7833.field_40716.rotation(var11));
      m.method_22907(class_7833.field_40714.rotation(-0.4F + var12 * 0.5F));
      box(m, vc, -0.6F, 0.0F, 0.0F, 1.2F, 7.5F, 0.25F, var8, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.03125F, 0.0125F, 0.2875F);
      m.method_22907(class_7833.field_40716.rotation(-var12));
      m.method_22907(class_7833.field_40714.rotation(-0.55F + var11 * 0.5F));
      box(m, vc, -0.6F, 0.0F, 0.0F, 1.2F, 8.2F, 0.25F, var8, l, o);
      m.method_22909();
      m.method_22909();
   }

   private void renderCyberVisor(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -14605015;
      int var7 = this.getEffectiveColor(age, -16718337);
      int var8 = -570425345;
      int var9 = -59580;
      m.method_22903();
      m.method_46416(0.0F, -0.28125F, -0.2625F);
      box(m, vc, -3.8F, -1.1F, -0.4F, 7.6F, 2.2F, 0.6F, var7, l, o);
      box(m, vc, -3.2F, -0.7F, -0.45F, 6.4F, 1.4F, 0.4F, var8, l, o);
      box(m, vc, -4.1F, -1.3F, -0.5F, 8.2F, 0.4F, 0.8F, var6, l, o);
      box(m, vc, -3.9F, 0.9F, -0.5F, 7.8F, 0.35F, 0.8F, var6, l, o);
      box(m, vc, -4.25F, -1.2F, -0.4F, 0.45F, 2.0F, 4.5F, var6, l, o);
      box(m, vc, 3.8F, -1.2F, -0.4F, 0.45F, 2.0F, 4.5F, var6, l, o);
      box(m, vc, 3.85F, -0.6F, 1.0F, 0.45F, 0.5F, 0.5F, var9, l, o);
      m.method_22909();
   }

   private void renderWings(class_4587 m, class_4588 vc, float age, float swing, float amp, int l, int o) {
      Accessories.WingType var8 = (Accessories.WingType)this.wingType.get();
      float var9 = (float)Math.sin(age * 0.12) * 14.0F;
      float var10 = (float)Math.cos(swing * 0.6662) * amp * 30.0F;
      float var11 = var9 + var10;
      float var12 = (float)Math.sin(age * 0.12 + 0.4) * 6.0F;
      this.renderWingSpineBracket(m, vc, age, var8, l, o);

      for (int var13 = -1; var13 <= 1; var13 += 2) {
         m.method_22903();
         m.method_46416(var13 * 2.2F * 0.0625F, 0.21875F, 0.134375F);
         m.method_22907(class_7833.field_40716.rotationDegrees(var13 * (24.0F + var11)));
         m.method_22907(class_7833.field_40718.rotationDegrees(var13 * (-8.0F + var12)));
         m.method_22907(class_7833.field_40714.rotationDegrees(4.0F));
         m.method_22905(var13, 1.0F, 1.0F);
         switch (var8) {
            case ANGEL:
               this.renderAngelWing(m, vc, age, l, o);
               break;
            case DEMON:
               this.renderDemonWing(m, vc, age, l, o);
               break;
            case CYBER:
               this.renderCyberWing(m, vc, age, l, o);
               break;
            case DRAGON:
               this.renderDragonWing(m, vc, age, l, o);
         }

         m.method_22909();
      }
   }

   private void renderWingSpineBracket(class_4587 m, class_4588 vc, float age, Accessories.WingType type, int l, int o) {
      m.method_22903();
      m.method_46416(0.0F, 0.21875F, 0.128125F);
      switch (type) {
         case ANGEL:
            short var12 = -10496;
            int var15 = -4745973;
            int var17 = this.getEffectiveColor(age, -16718337);
            box(m, vc, -2.4F, -1.8F, 0.0F, 4.8F, 3.6F, 0.6F, var12, l, o);
            box(m, vc, -1.4F, -1.2F, 0.4F, 2.8F, 2.4F, 0.4F, var15, l, o);
            box(m, vc, -0.7F, -0.7F, 0.6F, 1.4F, 1.4F, 0.35F, var17, l, o);
            box(m, vc, -2.6F, -0.6F, 0.1F, 0.8F, 1.2F, 0.6F, var12, l, o);
            box(m, vc, 1.8F, -0.6F, 0.1F, 0.8F, 1.2F, 0.6F, var12, l, o);
            break;
         case DEMON:
            int var11 = -15261654;
            int var14 = this.getEffectiveColor(age, -56832);
            box(m, vc, -2.6F, -2.0F, 0.0F, 5.2F, 4.0F, 0.7F, var11, l, o);
            box(m, vc, -0.6F, -1.6F, 0.5F, 1.2F, 3.2F, 0.4F, var14, l, o);
            box(m, vc, -2.8F, -0.8F, 0.1F, 0.9F, 1.6F, 0.7F, var11, l, o);
            box(m, vc, 1.9F, -0.8F, 0.1F, 0.9F, 1.6F, 0.7F, var11, l, o);
            break;
         case CYBER:
            int var10 = -14605015;
            int var13 = -11972521;
            int var16 = this.getEffectiveColor(age, -16718337);
            box(m, vc, -2.8F, -2.2F, 0.0F, 5.6F, 4.4F, 1.1F, var10, l, o);
            box(m, vc, -1.8F, -1.4F, 0.9F, 3.6F, 2.8F, 0.4F, var13, l, o);
            box(m, vc, -1.0F, -0.8F, 1.15F, 2.0F, 1.6F, 0.35F, var16, l, o);
            box(m, vc, -3.0F, -0.7F, 0.2F, 0.9F, 1.4F, 0.8F, var13, l, o);
            box(m, vc, 2.1F, -0.7F, 0.2F, 0.9F, 1.4F, 0.8F, var13, l, o);
            break;
         case DRAGON:
            int var7 = -14931917;
            int var8 = -15657187;
            int var9 = -812014;
            box(m, vc, -2.8F, -2.0F, 0.0F, 5.6F, 4.2F, 0.8F, var7, l, o);
            box(m, vc, -0.6F, -2.8F, 0.4F, 1.2F, 5.5F, 0.8F, var8, l, o);
            box(m, vc, -0.4F, -3.4F, 0.6F, 0.8F, 1.2F, 0.8F, var9, l, o);
            box(m, vc, -3.0F, -0.8F, 0.1F, 1.0F, 1.6F, 0.8F, var7, l, o);
            box(m, vc, 2.0F, -0.8F, 0.1F, 1.0F, 1.6F, 0.8F, var7, l, o);
      }

      m.method_22909();
   }

   private void renderAngelWing(class_4587 m, class_4588 vc, float age, int l, int o) {
      byte var6 = -1;
      int var7 = -985864;
      int var8 = -2497812;
      int var9 = this.getEffectiveColor(age, -10496);
      int var10 = -2838729;
      box(m, vc, 0.0F, -1.2F, -0.6F, 6.5F, 1.8F, 1.2F, var7, l, o);
      box(m, vc, 0.0F, -0.6F, -0.65F, 6.2F, 0.6F, 1.3F, var10, l, o);
      m.method_22903();
      m.method_46416(0.3875F, -0.05F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(35.0F));
      box(m, vc, -1.0F, -1.0F, -0.7F, 2.2F, 2.2F, 1.4F, var10, l, o);
      box(m, vc, 0.0F, -1.0F, -0.5F, 8.5F, 1.6F, 1.0F, var7, l, o);

      for (int var11 = 0; var11 < 4; var11++) {
         float var12 = var11 * 2.2F;
         box(m, vc, var12, 0.4F, -0.4F, 2.4F, 4.5F + var11 * 0.8F, 0.8F, var6, l, o);
      }

      for (int var15 = 0; var15 < 5; var15++) {
         float var17 = 1.0F + var15 * 2.4F;
         float var13 = 9.0F + var15 * 1.8F;
         int var14 = var15 % 2 == 0 ? var7 : var8;
         box(m, vc, var17, 2.0F, -0.3F, 2.6F, var13, 0.6F, var14, l, o);
      }

      for (int var16 = 0; var16 < 6; var16++) {
         float var18 = 3.0F + var16 * 2.2F;
         float var19 = 15.0F + var16 * 2.5F - (var16 > 3 ? (var16 - 3) * 3.0F : 0.0F);
         float var20 = (float)Math.sin(age * 0.2 + var16 * 0.4) * 0.5F;
         m.method_22903();
         m.method_46416(var18 * 0.0625F, 0.21875F, 0.0F);
         m.method_22907(class_7833.field_40718.rotationDegrees(-6.0F + var16 * 5.0F + var20));
         box(m, vc, 0.0F, 0.0F, -0.2F, 2.2F, var19, 0.45F, var6, l, o);
         box(m, vc, 0.2F, var19 - 3.2F, -0.25F, 1.8F, 3.2F, 0.5F, var9, l, o);
         m.method_22909();
      }

      m.method_22909();
   }

   private void renderDemonWing(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -15066598;
      int var7 = -15921907;
      int var8 = this.getEffectiveColor(age, -56832);
      int var9 = -11923945;
      int var10 = -8907483;
      int var11 = -2041141;
      box(m, vc, 0.0F, -1.4F, -0.7F, 7.5F, 2.0F, 1.4F, var6, l, o);
      box(m, vc, 1.0F, -0.7F, -0.8F, 5.5F, 0.6F, 1.6F, var8, l, o);
      m.method_22903();
      m.method_46416(0.45F, -0.0625F, 0.0F);
      box(m, vc, -1.0F, -1.0F, -0.8F, 2.4F, 2.4F, 1.6F, var7, l, o);
      box(m, vc, -0.5F, -3.8F, -0.6F, 1.6F, 3.0F, 1.2F, var11, l, o);
      box(m, vc, -0.2F, -5.2F, -0.4F, 1.0F, 1.6F, 0.8F, var11, l, o);
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(-15.0F));
      box(m, vc, 0.0F, -0.8F, -0.4F, 9.0F, 1.5F, 0.8F, var6, l, o);
      box(m, vc, 8.8F, -1.2F, -0.5F, 1.8F, 1.2F, 1.0F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(28.0F));
      box(m, vc, 0.0F, -0.7F, -0.4F, 11.0F, 1.4F, 0.8F, var6, l, o);
      box(m, vc, 10.8F, -1.0F, -0.5F, 1.8F, 1.2F, 1.0F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(65.0F));
      box(m, vc, 0.0F, -0.6F, -0.4F, 10.0F, 1.3F, 0.8F, var6, l, o);
      box(m, vc, 9.8F, -0.9F, -0.5F, 1.8F, 1.2F, 1.0F, var11, l, o);
      m.method_22909();
      box(m, vc, 1.0F, -0.5F, -0.2F, 7.5F, 7.0F, 0.4F, var10, l, o);
      box(m, vc, 3.0F, 5.0F, -0.2F, 7.5F, 8.5F, 0.4F, var9, l, o);
      box(m, vc, 1.5F, 11.5F, -0.2F, 6.5F, 7.0F, 0.4F, var9, l, o);
      box(m, vc, 2.5F, 3.0F, -0.25F, 5.5F, 0.5F, 0.5F, var8, l, o);
      box(m, vc, 4.0F, 8.5F, -0.25F, 5.0F, 0.5F, 0.5F, var8, l, o);
      m.method_22909();
   }

   private void renderCyberWing(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -14605015;
      int var7 = -11972521;
      int var8 = this.getEffectiveColor(age, -16718337);
      int var9 = -1;
      int var10 = -21760;
      box(m, vc, 0.0F, -1.0F, -0.6F, 7.0F, 1.8F, 1.2F, var6, l, o);
      box(m, vc, 1.0F, -0.4F, -0.7F, 5.0F, 0.6F, 1.4F, var10, l, o);
      m.method_22903();
      m.method_46416(0.425F, -0.03125F, 0.0F);
      box(m, vc, -1.0F, -1.0F, -0.8F, 2.2F, 2.2F, 1.6F, var7, l, o);
      m.method_22907(class_7833.field_40718.rotationDegrees(25.0F));
      box(m, vc, 0.0F, -0.8F, -0.5F, 8.0F, 1.5F, 1.0F, var6, l, o);

      for (int var11 = 0; var11 < (6); var11++) {
         float var12 = 1.0F + var11 * 2.2F;
         float var13 = 8.0F + var11 * 3.2F - (var11 > (3) ? (var11 - (3)) * 4.0F : 0.0F);
         float var14 = (float)Math.sin(age * 0.25 + var11 * 0.5) * 0.4F;
         m.method_22903();
         m.method_46416(var12 * 0.0625F, 0.09375F, 0.0F);
         m.method_22907(class_7833.field_40718.rotationDegrees(-12.0F + var11 * 8.0F + var14));
         box(m, vc, -0.4F, -0.4F, -0.4F, 0.8F, 0.8F, 0.8F, var7, l, o);
         box(m, vc, 0.0F, 0.0F, -0.2F, 1.6F, var13, 0.4F, var8, l, o);
         box(m, vc, 0.3F, 0.5F, -0.25F, 1.0F, var13 - 1.5F, 0.5F, var9, l, o);
         m.method_22909();
      }

      m.method_22909();
   }

   private void renderDragonWing(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -15063764;
      int var7 = -15722723;
      int var8 = -812014;
      int var9 = this.getEffectiveColor(age, -7660002);
      int var10 = -10808814;
      box(m, vc, 0.0F, -1.6F, -0.8F, 8.5F, 2.4F, 1.6F, var6, l, o);
      box(m, vc, 2.0F, -2.6F, -0.5F, 1.2F, 1.2F, 1.0F, var7, l, o);
      box(m, vc, 5.0F, -2.8F, -0.5F, 1.4F, 1.4F, 1.0F, var7, l, o);
      m.method_22903();
      m.method_46416(0.5125F, -0.0625F, 0.0F);
      box(m, vc, -1.2F, -1.2F, -1.0F, 2.6F, 2.6F, 2.0F, var6, l, o);
      box(m, vc, -0.6F, -4.5F, -0.7F, 1.6F, 3.5F, 1.4F, var8, l, o);
      box(m, vc, -0.3F, -6.0F, -0.5F, 1.0F, 1.8F, 1.0F, var8, l, o);
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(-10.0F));
      box(m, vc, 0.0F, -1.0F, -0.6F, 11.0F, 1.8F, 1.2F, var6, l, o);
      box(m, vc, 10.8F, -1.4F, -0.7F, 2.0F, 1.6F, 1.4F, var8, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(42.0F));
      box(m, vc, 0.0F, -0.9F, -0.6F, 13.0F, 1.7F, 1.2F, var6, l, o);
      box(m, vc, 12.8F, -1.3F, -0.7F, 2.0F, 1.6F, 1.4F, var8, l, o);
      m.method_22909();
      box(m, vc, 1.0F, 0.0F, -0.25F, 9.5F, 8.5F, 0.5F, var9, l, o);
      box(m, vc, 3.0F, 7.5F, -0.25F, 9.5F, 9.5F, 0.5F, var10, l, o);
      box(m, vc, 1.5F, 15.0F, -0.25F, 7.5F, 8.0F, 0.5F, var10, l, o);
      m.method_22909();
   }

   private void renderKatana(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -15261654;
      int var7 = this.getEffectiveColor(age, -10496);
      int var8 = -131330;
      int var9 = -13877680;
      m.method_22903();
      m.method_46416(0.0F, 0.3125F, 0.15625F);
      m.method_22907(class_7833.field_40718.rotationDegrees(42.0F));
      box(m, vc, -0.6F, -12.0F, -0.6F, 1.2F, 18.0F, 1.2F, var6, l, o);
      box(m, vc, -0.65F, -12.8F, -0.65F, 1.3F, 0.8F, 1.3F, var7, l, o);
      box(m, vc, -0.85F, -2.0F, -0.85F, 1.7F, 1.8F, 1.7F, var7, l, o);
      box(m, vc, -1.8F, 5.8F, -1.8F, 3.6F, 0.5F, 3.6F, var7, l, o);
      box(m, vc, -0.55F, 6.3F, -0.55F, 1.1F, 6.0F, 1.1F, var8, l, o);
      box(m, vc, -0.6F, 7.2F, -0.6F, 1.2F, 0.6F, 1.2F, var9, l, o);
      box(m, vc, -0.6F, 8.8F, -0.6F, 1.2F, 0.6F, 1.2F, var9, l, o);
      box(m, vc, -0.6F, 10.4F, -0.6F, 1.2F, 0.6F, 1.2F, var9, l, o);
      box(m, vc, -0.7F, 12.3F, -0.7F, 1.4F, 0.8F, 1.4F, var7, l, o);
      m.method_22909();
   }

   private void renderDualKatana(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -10808814;
      int var7 = this.getEffectiveColor(age, -10496);
      int var8 = -14935012;
      int var9 = -131330;

      for (int var10 = -1; var10 <= 1; var10 += 2) {
         m.method_22903();
         m.method_46416(var10 * 0.8F * 0.0625F, 0.3125F, (2.5F + (var10 == 1 ? 0.35F : 0.0F)) * 0.0625F);
         m.method_22907(class_7833.field_40718.rotationDegrees(var10 * 42.0F));
         box(m, vc, -0.6F, -12.0F, -0.6F, 1.2F, 18.0F, 1.2F, var6, l, o);
         box(m, vc, -0.65F, -12.8F, -0.65F, 1.3F, 0.8F, 1.3F, var7, l, o);
         box(m, vc, -0.85F, -2.0F, -0.85F, 1.7F, 1.8F, 1.7F, var7, l, o);
         box(m, vc, -1.8F, 5.8F, -1.8F, 3.6F, 0.5F, 3.6F, var7, l, o);
         box(m, vc, -0.55F, 6.3F, -0.55F, 1.1F, 6.0F, 1.1F, var8, l, o);
         box(m, vc, -0.6F, 7.5F, -0.6F, 1.2F, 0.6F, 1.2F, var9, l, o);
         box(m, vc, -0.6F, 9.5F, -0.6F, 1.2F, 0.6F, 1.2F, var9, l, o);
         box(m, vc, -0.7F, 12.3F, -0.7F, 1.4F, 0.8F, 1.4F, var7, l, o);
         m.method_22909();
      }
   }

   private void renderBackpack(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = -10665929;
      int var7 = -12703965;
      int var8 = this.getEffectiveColor(age, -11171025);
      int var9 = -10929;
      m.method_22903();
      m.method_46416(0.0F, 0.28125F, 0.1375F);
      box(m, vc, -3.6F, -3.8F, 0.0F, 7.2F, 8.2F, 3.8F, var6, l, o);
      box(m, vc, -2.6F, -1.2F, 3.8F, 5.2F, 4.8F, 1.5F, var7, l, o);
      box(m, vc, -1.0F, -0.6F, 5.35F, 2.0F, 0.4F, 0.2F, var9, l, o);
      box(m, vc, 3.6F, -1.0F, 0.6F, 1.2F, 4.0F, 2.4F, var7, l, o);
      box(m, vc, -4.2F, -6.0F, 0.4F, 8.4F, 2.2F, 2.6F, var8, l, o);
      box(m, vc, -2.8F, -6.1F, 0.35F, 0.6F, 2.4F, 2.7F, var7, l, o);
      box(m, vc, 2.2F, -6.1F, 0.35F, 0.6F, 2.4F, 2.7F, var7, l, o);
      box(m, vc, -2.85F, -5.2F, 3.08F, 0.7F, 0.6F, 0.2F, var9, l, o);
      box(m, vc, 2.15F, -5.2F, 3.08F, 0.7F, 0.6F, 0.2F, var9, l, o);
      m.method_22909();
   }

   private void renderTail(class_4587 m, class_4588 vc, float age, float swing, float amp, int l, int o) {
      int var8 = this.getEffectiveColor(age, -1683200);
      int var9 = -131330;
      m.method_22903();
      m.method_46416(0.0F, 0.65625F, 0.128125F);
      float var10 = (float)Math.sin(age * 0.12) * 16.0F + class_3532.method_15362(swing * 0.6662F) * amp * 20.0F;
      float var11 = (float)Math.sin(age * 0.08) * 8.0F;
      m.method_22907(class_7833.field_40716.rotationDegrees(var10));
      m.method_22907(class_7833.field_40714.rotationDegrees(35.0F + var11));
      box(m, vc, -1.0F, 0.0F, 0.0F, 2.0F, 2.2F, 2.4F, var8, l, o);
      m.method_46416(0.0F, 0.1125F, 0.1125F);
      m.method_22907(class_7833.field_40714.rotationDegrees(15.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(var10 * 0.25F));
      box(m, vc, -1.3F, 0.0F, 0.0F, 2.6F, 2.6F, 2.8F, var8, l, o);
      m.method_46416(0.0F, 0.1375F, 0.1375F);
      m.method_22907(class_7833.field_40714.rotationDegrees(20.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(var10 * 0.35F));
      box(m, vc, -1.6F, 0.0F, 0.0F, 3.2F, 3.0F, 3.2F, var8, l, o);
      m.method_46416(0.0F, 0.1625F, 0.1375F);
      m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(var10 * 0.45F));
      box(m, vc, -1.3F, 0.0F, 0.0F, 2.6F, 2.8F, 2.8F, var8, l, o);
      m.method_46416(0.0F, 0.15F, 0.1125F);
      m.method_22907(class_7833.field_40714.rotationDegrees(15.0F));
      box(m, vc, -0.9F, 0.0F, 0.0F, 1.8F, 2.4F, 2.0F, var9, l, o);
      m.method_22909();
   }

   private void renderScarf(class_4587 m, class_4588 vc, float age, float swing, float amp, int l, int o) {
      int var8 = this.getEffectiveColor(age, -4179669);
      int var9 = -131330;
      m.method_22903();
      box(m, vc, -4.6F, -0.8F, -2.6F, 9.2F, 2.8F, 5.2F, var8, l, o);
      box(m, vc, -4.9F, 0.2F, -2.9F, 9.8F, 1.8F, 5.8F, var8, l, o);
      float var10 = (float)Math.sin(age * 0.2 + swing * 0.5) * 0.22F;
      m.method_22903();
      m.method_46416(-0.125F, 0.09375F, -0.175F);
      m.method_22907(class_7833.field_40714.rotation(0.2F + var10 * 0.4F));
      box(m, vc, -1.2F, 0.0F, -0.3F, 2.4F, 7.5F, 0.6F, var8, l, o);
      box(m, vc, -1.2F, 7.5F, -0.3F, 2.4F, 1.0F, 0.6F, var9, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.09375F, 0.09375F, 0.15625F);
      m.method_22907(class_7833.field_40714.rotation(-0.35F + var10 * 0.5F));
      box(m, vc, -1.1F, 0.0F, 0.0F, 2.2F, 8.5F, 0.6F, var8, l, o);
      box(m, vc, -1.1F, 8.5F, 0.0F, 2.2F, 1.0F, 0.6F, var9, l, o);
      m.method_22909();
      m.method_22909();
   }

   private void renderMagicAura(class_4587 m, class_4588 vc, float age, int l, int o) {
      int var6 = this.getEffectiveColor(age, -6543440);
      int var7 = -1982745;
      m.method_22903();
      m.method_46416(0.0F, 1.40625F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(age * 1.5F));
      int var8 = 8;
      float var9 = 14.0F;

      for (int var10 = 0; var10 < var8; var10++) {
         float var11 = (float)(var10 * 2.0 * Math.PI / var8);
         float var12 = (float)Math.cos(var11) * var9;
         float var13 = (float)Math.sin(var11) * var9;
         float var14 = (float)Math.sin(age * 0.15 + var10) * 0.8F;
         m.method_22903();
         m.method_46416(var12 * 0.0625F, var14 * 0.0625F, var13 * 0.0625F);
         m.method_22907(class_7833.field_40716.rotationDegrees(age * 3.0F + var10 * 45.0F));
         m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
         box(m, vc, -0.6F, -1.6F, -0.6F, 1.2F, 3.2F, 1.2F, var6, l, o);
         box(m, vc, -0.3F, -1.0F, -0.3F, 0.6F, 2.0F, 0.6F, var7, l, o);
         m.method_22909();
      }

      m.method_22909();
   }

   private static void box(class_4587 m, class_4588 vc, float x, float y, float z, float w, float h, float d, int argb, int light, int overlay) {
      float var11 = Math.min(x, x + w) * 0.0625F;
      float var12 = Math.max(x, x + w) * 0.0625F;
      float var13 = Math.min(y, y + h) * 0.0625F;
      float var14 = Math.max(y, y + h) * 0.0625F;
      float var15 = Math.min(z, z + d) * 0.0625F;
      float var16 = Math.max(z, z + d) * 0.0625F;
      int var17 = argb >> (24) & (255);
      int var18 = argb >> (16) & (255);
      int var19 = argb >> (8) & (255);
      int var20 = argb & (255);
      Matrix4f var21 = m.method_23760().method_23761();
      quad(
         vc,
         var21,
         var11,
         var13,
         var16,
         var12,
         var13,
         var16,
         var12,
         var14,
         var16,
         var11,
         var14,
         var16,
         0.0F,
         0.0F,
         1.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
      quad(
         vc,
         var21,
         var12,
         var13,
         var15,
         var11,
         var13,
         var15,
         var11,
         var14,
         var15,
         var12,
         var14,
         var15,
         0.0F,
         0.0F,
         -1.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
      quad(
         vc,
         var21,
         var11,
         var13,
         var15,
         var12,
         var13,
         var15,
         var12,
         var13,
         var16,
         var11,
         var13,
         var16,
         0.0F,
         -1.0F,
         0.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
      quad(
         vc,
         var21,
         var11,
         var14,
         var16,
         var12,
         var14,
         var16,
         var12,
         var14,
         var15,
         var11,
         var14,
         var15,
         0.0F,
         1.0F,
         0.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
      quad(
         vc,
         var21,
         var11,
         var13,
         var15,
         var11,
         var13,
         var16,
         var11,
         var14,
         var16,
         var11,
         var14,
         var15,
         -1.0F,
         0.0F,
         0.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
      quad(
         vc,
         var21,
         var12,
         var13,
         var16,
         var12,
         var13,
         var15,
         var12,
         var14,
         var15,
         var12,
         var14,
         var16,
         1.0F,
         0.0F,
         0.0F,
         var18,
         var19,
         var20,
         var17,
         light,
         overlay
      );
   }

   private static void quad(
      class_4588 vc,
      Matrix4f mat,
      float x0,
      float y0,
      float z0,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float x3,
      float y3,
      float z3,
      float nx,
      float ny,
      float nz,
      int r,
      int g,
      int b,
      int a,
      int light,
      int overlay
   ) {
      vc.method_22918(mat, x0, y0, z0).method_1336(r, g, b, a).method_22913(0.0F, 0.0F).method_22922(overlay).method_60803(light).method_22914(nx, ny, nz);
      vc.method_22918(mat, x1, y1, z1).method_1336(r, g, b, a).method_22913(1.0F, 0.0F).method_22922(overlay).method_60803(light).method_22914(nx, ny, nz);
      vc.method_22918(mat, x2, y2, z2).method_1336(r, g, b, a).method_22913(1.0F, 1.0F).method_22922(overlay).method_60803(light).method_22914(nx, ny, nz);
      vc.method_22918(mat, x3, y3, z3).method_1336(r, g, b, a).method_22913(0.0F, 1.0F).method_22922(overlay).method_60803(light).method_22914(nx, ny, nz);
   }

   static {
      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(126);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-52);
      var10000[5] = (byte)(-110);
      var10000[6] = (byte)(-6);
      var10000[7] = (byte)(-56);
      var10000[8] = (byte)(15);
      String var0 = $$v_s(var10000, -8748174010707881930L);
      byte[] var10001 = new byte[33];
      var10001[0] = (byte)(103);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-38);
      var10001[5] = (byte)(-110);
      var10001[6] = (byte)(-2);
      var10001[7] = (byte)(-35);
      var10001[8] = (byte)(84);
      var10001[9] = (byte)(-64);
      var10001[10] = (byte)(-120);
      var10001[11] = (byte)(-103);
      var10001[12] = (byte)(-108);
      var10001[13] = (byte)(83);
      var10001[14] = (byte)(-100);
      var10001[15] = (byte)(-95);
      var10001[16] = (byte)(43);
      var10001[17] = (byte)(35);
      var10001[18] = (byte)(-72);
      var10001[19] = (byte)(-53);
      var10001[20] = (byte)(64);
      var10001[21] = (byte)(115);
      var10001[22] = (byte)(-92);
      var10001[23] = (byte)(-112);
      var10001[24] = (byte)(-56);
      var10001[25] = (byte)(96);
      var10001[26] = (byte)(-15);
      var10001[27] = (byte)(50);
      var10001[28] = (byte)(66);
      var10001[29] = (byte)(70);
      var10001[30] = (byte)(-109);
      var10001[31] = (byte)(72);
      var10001[32] = (byte)(-108);
      WHITE_TEX = class_2960.method_60655(var0, $$v_s(var10001, -8748174010707881930L));
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

   public enum WingType {
      ANGEL,
      DEMON,
      CYBER,
      DRAGON;

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      WingType(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static Accessories.WingType[] $values() {
         Accessories.WingType[] var10000 = new Accessories.WingType[4];
         var10000[0] = ANGEL;
         var10000[1] = DEMON;
         var10000[2] = CYBER;
         var10000[3] = DRAGON;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-74);
         String var0 = $$v_s(var10002, 4490549812872335382L);
         byte[] var10004 = new byte[5];
         var10004[0] = (byte)(114);
         var10004[1] = (byte)(-12);
         var10004[2] = (byte)(123);
         var10004[3] = (byte)(-69);
         var10004[4] = (byte)(-106);
         ANGEL = new Accessories.WingType($$v_s(var10004, 4490549812872335382L));
         var10002 = new byte[5];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(-76);
         String var2 = $$v_s(var10002, 4490549812872335382L);
         var10004 = new byte[5];
         var10004[0] = (byte)(119);
         var10004[1] = (byte)(-1);
         var10004[2] = (byte)(113);
         var10004[3] = (byte)(-79);
         var10004[4] = (byte)(-108);
         DEMON = new Accessories.WingType($$v_s(var10004, 4490549812872335382L));
         var10002 = new byte[5];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-88);
         String var4 = $$v_s(var10002, 4490549812872335382L);
         int var10003 = 2;
         var10004 = new byte[5];
         var10004[0] = (byte)(112);
         var10004[1] = (byte)(-29);
         var10004[2] = (byte)(126);
         var10004[3] = (byte)(-69);
         var10004[4] = (byte)(-120);
         CYBER = new Accessories.WingType($$v_s(var10004, 4490549812872335382L));
         var10002 = new byte[6];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-103);
         var10002[4] = (byte)(-75);
         var10002[5] = (byte)(51);
         String var6 = $$v_s(var10002, 4490549812872335382L);
         var10003 = 3;
         var10004 = new byte[6];
         var10004[0] = (byte)(119);
         var10004[1] = (byte)(-24);
         var10004[2] = (byte)(125);
         var10004[3] = (byte)(-71);
         var10004[4] = (byte)(-107);
         var10004[5] = (byte)(19);
         DRAGON = new Accessories.WingType($$v_s(var10004, 4490549812872335382L));
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
