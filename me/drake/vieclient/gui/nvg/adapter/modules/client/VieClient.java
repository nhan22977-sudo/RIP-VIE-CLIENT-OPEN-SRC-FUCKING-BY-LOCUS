package me.drake.vieclient.gui.nvg.adapter.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.setting.BindSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BooleanSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ModeSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.NumberSetting;
import me.drake.vieclient.gui.nvg.ui.NewClickGUI;
import me.drake.vieclient.modules.client.ClientSettings;
import net.minecraft.class_310;
import net.minecraft.class_437;

public class VieClient extends Module {
   public static final VieClient instance = new VieClient();
   public static final NumberSetting redColor = channel(16);
   public static final NumberSetting greenColor = channel(8);
   public static final NumberSetting blueColor = channel(0);
   public static final NumberSetting guiScale = new NumberSetting(
      "GUI Scale", 0.5, 2.0, 1.0, 0.05
   );
   public static final ModeSetting animationMode = new ModeSetting(
      "Animation Mode",
      VieClient.AnimationMode.NORMAL,
      VieClient.AnimationMode.class
   );
   public static final ModeSetting animationSpeed = new ModeSetting(
      "Animation Speed",
      VieClient.AnimSpeed.MEDIUM,
      VieClient.AnimSpeed.class
   );
   public static final ModeSetting guiStyle = new ModeSetting(
      "Style",
      VieClient.GuiStyleMode.CLEAN,
      VieClient.GuiStyleMode.class,
      () -> clientSettings() != null ? clientSettings().getGuiStyleSetting() : null
   );
   public static final ModeSetting clickGuiMode = new ModeSetting(
      "ClickGUI Mode",
      VieClient.ClickGuiMode.DASHBOARD,
      VieClient.ClickGuiMode.class,
      () -> clientSettings() != null ? clientSettings().getClickGuiModeSetting() : null
   );
   public static final ModeSetting languageSetting = new ModeSetting(
      "Language",
      ClientSettings.LanguageMode.VIE,
      ClientSettings.LanguageMode.class,
      () -> clientSettings() != null ? clientSettings().getLanguageSetting() : null
   );
   public static final ModeSetting fontSetting = new ModeSetting(
      "Font",
      ClientSettings.FontMode.DEFAULT,
      ClientSettings.FontMode.class,
      () -> clientSettings() != null ? clientSettings().getFontModeSetting() : null
   );
   public static final ModeSetting themePreset = new ModeSetting(
      "Theme",
      ClientSettings.ThemePreset.OBSIDIAN,
      ClientSettings.ThemePreset.class,
      () -> clientSettings() != null ? clientSettings().getThemePresetSetting() : null
   );
   public static final BooleanSetting interactionSfxSetting = new BooleanSetting(
      "Interaction SFX", true
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      public boolean getValue() {
         return VieClient.clientSettings() != null ? VieClient.clientSettings().isInteractionSfxEnabled() : super.getValue();
      }

      @Override
      public void setValue(boolean a) {
         if (VieClient.clientSettings() != null) {
            VieClient.clientSettings().setInteractionSfxEnabled(a);
         }

         super.setValue(a);
      }

      @Override
      public void toggle() {
         this.setValue(!this.getValue());
      }
   };
   public static final BooleanSetting lightMode = new BooleanSetting(
         "Light Mode", false
      )
      .setVisible(false);
   public static final BooleanSetting enableRainbowEffect = new BooleanSetting(
         "Rainbow Colors", false
      )
      .setVisible(false);
   public static final BooleanSetting enableBreathingEffect = new BooleanSetting(
         "Breathing Colors", false
      )
      .setVisible(false);
   public static final BooleanSetting enableMSAA = new BooleanSetting("MSAA", false).setVisible(false);
   public static final BooleanSetting preventClose = new BooleanSetting(
      "Prevent Close", true
   );
   private static int guiKey = 344;
   private static class_437 previousScreen = null;
   private BindSetting keybindSetting;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static ColorSetting accentRaw() {
      try {
         ClientSettings var0 = clientSettings();
         if (var0 == null) {
            return null;
         }

         for (Setting var2 : var0.getSettings()) {
            if (var2 instanceof ColorSetting var3) {
               return var3;
            }
         }
      } catch (Throwable var4) {
      }

      return null;
   }

   private static int accentArgb() {
      try {
         ColorSetting var0 = accentRaw();
         if (var0 != null) {
            return (Integer)var0.get();
         }

         ClientSettings var1 = clientSettings();
         if (var1 != null) {
            return var1.getPrimaryColor();
         }
      } catch (Throwable var2) {
      }

      return -10050049;
   }

   private static ClientSettings clientSettings() {
      try {
         ClientSettings var0 = ClientSettings.get();
         if (var0 != null) {
            return var0;
         }

         VieClientMod var1 = VieClientMod.getInstance();
         if (var1 != null && var1.getModuleManager() != null) {
            return (ClientSettings)var1.getModuleManager().getModule(ClientSettings.class);
         }
      } catch (Throwable var2) {
      }

      return null;
   }

   private static NumberSetting channel(int shift) {
      return new NumberSetting(
         "",
         0.0,
         255.0,
         accentArgb() >> shift & (255),
         1.0,
         () -> accentArgb() >> shift & (255),
         v -> {
            ColorSetting var3 = accentRaw();
            if (var3 != null) {
               int var4 = (Integer)var3.get();
               int var5 = (int)Math.max(0L, Math.min(255L, Math.round(v)));
               var3.setRGBA(
                  shift == (16) ? var5 : var4 >> (16) & (255),
                  shift == (8) ? var5 : var4 >> (8) & (255),
                  shift == 0 ? var5 : var4 & (255),
                  var4 >> (24) & (255)
               );
            }
         }
      );
   }

   private VieClient() {
      super(new VieClient.SyntheticRaw());
   }

   @Override
   public CharSequence getName() {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(120);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-74);
      var10000[5] = (byte)(-25);
      var10000[6] = (byte)(-62);
      var10000[7] = (byte)(20);
      var10000[8] = (byte)(40);
      var10000[9] = (byte)(-42);
      return $$v_s(var10000, 6460507451617955851L);
   }

   @Override
   public boolean isEnabled() {
      class_310 var1 = class_310.method_1551();
      return var1 != null && var1.field_1755 instanceof NewClickGUI;
   }

   @Override
   public void setEnabled(boolean enabled) {
      class_310 var2 = class_310.method_1551();
      if (var2 != null) {
         boolean var3 = this.isEnabled();
         if (enabled && !var3) {
            previousScreen = var2.field_1755 != null && !(var2.field_1755 instanceof NewClickGUI) ? var2.field_1755 : null;
            me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE.GUI = new NewClickGUI();
            me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE.GUI.reset();
            var2.method_1507(me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE.GUI);
         } else if (!enabled && var3) {
            var2.method_1507(previousScreen != null && !(previousScreen instanceof NewClickGUI) ? previousScreen : null);
            previousScreen = null;
         }
      }
   }

   public static void rememberOpenedFrom() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null) {
         instance.setEnabled(true);
      }
   }

   @Override
   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   @Override
   public Category getCategory() {
      return Category.CLIENT;
   }

   @Override
   public int rawKeybind() {
      ClientSettings var1 = clientSettings();
      return var1 != null ? var1.getClickGuiKey() : guiKey;
   }

   @Override
   public int getKeybind() {
      return BindSetting.toMeilaaKey(this.rawKeybind());
   }

   @Override
   public void setKeybind(int key) {
      int var2 = BindSetting.toVieKey(key);
      guiKey = var2;
      ClientSettings var3 = clientSettings();
      if (var3 != null) {
         var3.setClickGuiKey(var2);
      }
   }

   public BooleanSetting getPreventClose() {
      return preventClose;
   }

   @Override
   public List getSettings() {
      if (this.keybindSetting == null) {
         byte[] var10003 = new byte[7];
         var10003[0] = (byte)(101);
         var10003[1] = (byte)(-1);
         var10003[2] = (byte)(101);
         var10003[3] = (byte)(-68);
         var10003[4] = (byte)(-100);
         var10003[5] = (byte)(-27);
         var10003[6] = (byte)(-49);
         this.keybindSetting = new BindSetting($$v_s(var10003, 6460507451617955851L), this.getKeybind(), true, this);
      }

      ArrayList var1 = new ArrayList();
      var1.add(languageSetting);
      var1.add(fontSetting);
      var1.add(interactionSfxSetting);
      var1.add(this.keybindSetting);
      var1.add(guiScale);
      if (lightMode.isVisible()) {
         var1.add(lightMode);
      }

      if (enableRainbowEffect.isVisible()) {
         var1.add(enableRainbowEffect);
      }

      if (enableBreathingEffect.isVisible()) {
         var1.add(enableBreathingEffect);
      }

      if (enableMSAA.isVisible()) {
         var1.add(enableMSAA);
      }

      var1.add(preventClose);
      var1.add(animationMode);
      var1.add(animationSpeed);
      return var1;
   }

   static {
      languageSetting.setDescription(
         "Client display language (VIE / ENG)"
      );
      languageSetting.setDescriptionSupplier(() -> {
         byte[] var10000 = new byte[8];
         var10000[0] = (byte)(98);
         var10000[1] = (byte)(-5);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-71);
         var10000[4] = (byte)(-128);
         var10000[5] = (byte)(-22);
         var10000[6] = (byte)(-52);
         var10000[7] = (byte)(20);
         String var0 = $$v_s(var10000, 6460507451617955851L);
         byte[] var10001 = new byte[35];
         var10001[0] = (byte)(109);
         var10001[1] = (byte)(-10);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-101);
         var10001[5] = (byte)(-1);
         var10001[6] = (byte)(-117);
         var10001[7] = (byte)(21);
         var10001[8] = (byte)(47);
         var10001[9] = (byte)(-47);
         var10001[10] = (byte)(-108);
         var10001[11] = (byte)(-102);
         var10001[12] = (byte)(-52);
         var10001[13] = (byte)(42);
         var10001[14] = (byte)(-93);
         var10001[15] = (byte)(101);
         var10001[16] = (byte)(31);
         var10001[17] = (byte)(36);
         var10001[18] = (byte)(-85);
         var10001[19] = (byte)(-37);
         var10001[20] = (byte)(36);
         var10001[21] = (byte)(28);
         var10001[22] = (byte)(-98);
         var10001[23] = 1;
         var10001[24] = (byte)(-66);
         var10001[25] = (byte)(68);
         var10001[26] = (byte)(-35);
         var10001[27] = (byte)(3);
         var10001[28] = (byte)(93);
         var10001[29] = (byte)(44);
         var10001[30] = (byte)(-13);
         var10001[31] = (byte)(-68);
         var10001[32] = (byte)(-128);
         var10001[33] = (byte)(125);
         var10001[34] = (byte)(85);
         return Localization.getSettingDescription(var0, $$v_s(var10001, 6460507451617955851L));
      });
      fontSetting.setDescription(
         "Client UI font (Default / Minecraft)"
      );
      interactionSfxSetting.setDescription(
         "Play sound effects on UI clicks and interactions"
      );
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

   public enum AnimSpeed {
      SUPER_SLOW,
      SLOW,
      MEDIUM,
      FAST;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static VieClient.AnimSpeed[] $values() {
         VieClient.AnimSpeed[] var10000 = new VieClient.AnimSpeed[4];
         var10000[0] = SUPER_SLOW;
         var10000[1] = SLOW;
         var10000[2] = MEDIUM;
         var10000[3] = FAST;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[10];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-75);
         var10002[5] = (byte)(123);
         var10002[6] = (byte)(114);
         var10002[7] = (byte)(-69);
         var10002[8] = (byte)(24);
         var10002[9] = (byte)(-11);
         SUPER_SLOW = new VieClient.AnimSpeed();
         var10002 = new byte[4];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-119);
         SLOW = new VieClient.AnimSpeed();
         var10002 = new byte[6];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-78);
         var10002[5] = (byte)(105);
         MEDIUM = new VieClient.AnimSpeed();
         var10002 = new byte[4];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         FAST = new VieClient.AnimSpeed();
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

   public enum AnimationMode {
      OFF,
      SLOW,
      NORMAL,
      POSITIVE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static VieClient.AnimationMode[] $values() {
         VieClient.AnimationMode[] var10000 = new VieClient.AnimationMode[4];
         var10000[0] = OFF;
         var10000[1] = SLOW;
         var10000[2] = NORMAL;
         var10000[3] = POSITIVE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new VieClient.AnimationMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-119);
         SLOW = new VieClient.AnimationMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(55);
         var10002[5] = (byte)(56);
         NORMAL = new VieClient.AnimationMode();
         var10002 = new byte[8];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(34);
         var10002[5] = (byte)(61);
         var10002[6] = (byte)(-74);
         var10002[7] = (byte)(-120);
         POSITIVE = new VieClient.AnimationMode();
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

   public enum ClickGuiMode {
      DASHBOARD;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static VieClient.ClickGuiMode[] $values() {
         return new VieClient.ClickGuiMode[]{DASHBOARD};
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(45);
         var10002[5] = (byte)(-52);
         var10002[6] = (byte)(73);
         var10002[7] = (byte)(-83);
         var10002[8] = (byte)(22);
         DASHBOARD = new VieClient.ClickGuiMode();
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

   public enum GuiStyleMode {
      CLEAN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static VieClient.GuiStyleMode[] $values() {
         return new VieClient.GuiStyleMode[]{CLEAN};
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(6);
         CLEAN = new VieClient.GuiStyleMode();
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

   private static final class SyntheticRaw extends me.drake.vieclient.core.module.Module {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SyntheticRaw() {
         byte[] var10001 = new byte[10];
         var10001[0] = (byte)(111);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-2);
         var10001[4] = (byte)(113);
         var10001[5] = (byte)(-61);
         var10001[6] = (byte)(-57);
         var10001[7] = (byte)(56);
         var10001[8] = (byte)(63);
         var10001[9] = (byte)(-42);
         String var1 = $$v_s(var10001, 8479558516472008732L);
         byte[] var10002 = new byte[22];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(110);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(87);
         var10002[5] = (byte)(-53);
         var10002[6] = (byte)(-114);
         var10002[7] = (byte)(19);
         var10002[8] = (byte)(48);
         var10002[9] = (byte)(-52);
         var10002[10] = (byte)(-117);
         var10002[11] = (byte)(-96);
         var10002[12] = (byte)(45);
         var10002[13] = (byte)(87);
         var10002[14] = (byte)(-59);
         var10002[15] = (byte)(73);
         var10002[16] = 0;
         var10002[17] = (byte)(41);
         var10002[18] = (byte)(-89);
         var10002[19] = (byte)(-23);
         var10002[20] = (byte)(-41);
         var10002[21] = (byte)(22);
         super(var1, $$v_s(var10002, 8479558516472008732L), me.drake.vieclient.core.module.Category.CLIENT);
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
