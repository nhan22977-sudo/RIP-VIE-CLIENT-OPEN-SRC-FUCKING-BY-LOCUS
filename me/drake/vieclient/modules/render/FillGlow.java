package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.post.EdgeGlowPass;
import net.minecraft.class_310;

public class FillGlow extends Module {
   private static volatile FillGlow instance;
   private final BoolSetting applyItems = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dropped Items",
         "Apply fill glow to dropped item entities",
         true
      )
   );
   private final BoolSetting applyHand = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hand & Held Item",
         "Apply fill glow to first-person hand and held item",
         true
      )
   );
   private final BoolSetting applyPlayers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Players",
         "Apply fill glow to other player characters",
         false
      )
   );
   private final EnumSetting colorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Color Mode",
         "Glow & fill color palette (THEME syncs with Vie Client accent)",
         FillGlow.ColorMode.THEME
      )
   );
   private final ColorSetting customColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Glow Color",
         "Custom fill and glow aura color",
         -16718337
      )
   );
   private final ColorSetting sheenColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Sheen Color",
         "Shimmering sheen sweep highlight color",
         -1
      )
   );
   private final DoubleSetting fillOpacity = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Fill Opacity",
         "Interior color fill strength (0% = texture only, 100% = solid fill)",
         70.0,
         0.0,
         100.0,
         5.0
      )
   );
   private final DoubleSetting aura = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Aura",
         "Radiant bloom aura intensity spreading outwards",
         70.0,
         0.0,
         100.0,
         5.0
      )
   );
   private final DoubleSetting reach = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Reach",
         "Glow bloom spread radius (pixels)",
         8.0,
         2.0,
         20.0,
         1.0
      )
   );
   private final DoubleSetting rim = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Rim Stroke",
         "Edge outline sharpness around objects",
         45.0,
         0.0,
         100.0,
         5.0
      )
   );
   private final DoubleSetting sheen = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Sheen",
         "Shimmering sheen sweep brightness",
         30.0,
         0.0,
         100.0,
         5.0
      )
   );
   private final BoolSetting breathe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Breathe",
         "Smooth breathing pulsation glow animation",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FillGlow() {
      super(
         "FillGlow",
         "Fill color overlay and radiant glowing aura for items",
         Category.RENDER
      );
      this.customColor.visibleWhen(() -> this.colorMode.get() == FillGlow.ColorMode.CUSTOM);
   }

   @Override
   public void onEnable() {
      instance = this;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      instance = null;
      if (!EdgeGlow.isGlowActive()) {
         EdgeGlowPass.release();
      }

      super.onDisable();
   }

   public static FillGlow get() {
      return instance;
   }

   public static boolean isGlowActive() {
      return instance != null && instance.isEnabled();
   }

   public boolean shouldGlowItems() {
      return this.isEnabled() && (Boolean)this.applyItems.get();
   }

   public boolean shouldGlowHand() {
      return this.isEnabled() && (Boolean)this.applyHand.get();
   }

   public boolean shouldGlowPlayers() {
      return this.isEnabled() && (Boolean)this.applyPlayers.get();
   }

   public static boolean beginCaptureHand() {
      FillGlow var0 = instance;
      return var0 != null && var0.shouldGlowHand() ? EdgeGlowPass.beginCapture() : false;
   }

   public static void endCaptureHand(boolean drawn) {
      if (EdgeGlowPass.endCapture() && drawn) {
         FillGlow var1 = instance;
         if (var1 != null) {
            var1.composePass();
         }
      }
   }

   public static boolean beginCaptureEntity(boolean isPlayer, boolean isItem) {
      FillGlow var2 = instance;
      if (var2 == null) {
         return false;
      } else if (isPlayer && !var2.shouldGlowPlayers()) {
         return false;
      } else {
         return isItem && !var2.shouldGlowItems() ? false : EdgeGlowPass.beginCapture(true);
      }
   }

   public static void endCaptureEntity(boolean drawn) {
      if (EdgeGlowPass.endCapture() && drawn) {
         FillGlow var1 = instance;
         if (var1 != null) {
            var1.composePass();
         }
      }
   }

   public void composePass() {
      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.method_22683() != null) {
         float var2 = Math.max(1.0F, var1.method_22683().method_4507() / 1080.0F);
         long var3 = System.nanoTime();
         float var5 = (float)(var3 % 4400000000L / 4.4E9);
         int var6 = this.resolveGlowColor();
         int var7 = (Integer)this.sheenColor.get();
         float var8 = (float)((Double)this.aura.get() / 100.0);
         float var9 = (float)((Double)this.fillOpacity.get() / 100.0);
         if ((Boolean)this.breathe.get()) {
            double var10 = var3 % 3200000000L / 3.2E9;
            float var12 = (float)Math.sin(var10 * Math.PI);
            var8 *= 0.65F + 0.35F * var12 * var12;
            var9 *= 0.8F + 0.2F * var12 * var12;
         }

         EdgeGlowPass.compose(
            var6,
            var8,
            (float)((Double)this.reach.get() * var2),
            (float)((Double)this.rim.get() / 100.0),
            2.0F * var2,
            var7,
            (float)((Double)this.sheen.get() / 100.0),
            var5,
            var9
         );
      }
   }

   public int resolveGlowColor() {
      return switch ((FillGlow.ColorMode)this.colorMode.get()) {
         case THEME -> {
            ClientSettings var4 = ClientSettings.get();
            yield var4 != null ? var4.getPrimaryColor() : -7643914;
         }
         case CYAN_NEON -> -16718337;
         case NEON_PURPLE -> -5028609;
         case GOLDEN -> -10496;
         case RAINBOW -> {
            double var1 = System.currentTimeMillis() / 1000.0;
            float var3 = (float)(var1 * 0.22 % 1.0);
            yield Color.HSBtoRGB(var3, 0.85F, 1.0F);
         }
         case CUSTOM -> this.customColor.get();
      };
   }

   public BoolSetting getApplyItemsSetting() {
      return this.applyItems;
   }

   public BoolSetting getApplyHandSetting() {
      return this.applyHand;
   }

   public BoolSetting getApplyPlayersSetting() {
      return this.applyPlayers;
   }

   public EnumSetting getColorModeSetting() {
      return this.colorMode;
   }

   public ColorSetting getCustomColorSetting() {
      return this.customColor;
   }

   public DoubleSetting getFillOpacitySetting() {
      return this.fillOpacity;
   }

   public DoubleSetting getAuraSetting() {
      return this.aura;
   }

   public DoubleSetting getReachSetting() {
      return this.reach;
   }

   public DoubleSetting getRimSetting() {
      return this.rim;
   }

   public DoubleSetting getSheenSetting() {
      return this.sheen;
   }

   public BoolSetting getBreatheSetting() {
      return this.breathe;
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
      NEON_PURPLE,
      GOLDEN,
      RAINBOW,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static FillGlow.ColorMode[] $values() {
         FillGlow.ColorMode[] var10000 = new FillGlow.ColorMode[6];
         var10000[0] = THEME;
         var10000[1] = CYAN_NEON;
         var10000[2] = NEON_PURPLE;
         var10000[3] = GOLDEN;
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
         var10002[4] = (byte)(-104);
         THEME = new FillGlow.ColorMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-126);
         var10002[5] = (byte)(113);
         var10002[6] = (byte)(-4);
         var10002[7] = (byte)(127);
         var10002[8] = (byte)(27);
         CYAN_NEON = new FillGlow.ColorMode();
         var10002 = new byte[11];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-126);
         var10002[5] = (byte)(111);
         var10002[6] = (byte)(-20);
         var10002[7] = (byte)(98);
         var10002[8] = (byte)(5);
         var10002[9] = (byte)(-18);
         var10002[10] = (byte)(-95);
         NEON_PURPLE = new FillGlow.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(-104);
         var10002[5] = (byte)(113);
         GOLDEN = new FillGlow.ColorMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-97);
         var10002[5] = (byte)(112);
         var10002[6] = (byte)(-18);
         RAINBOW = new FillGlow.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-110);
         var10002[5] = (byte)(114);
         CUSTOM = new FillGlow.ColorMode();
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
