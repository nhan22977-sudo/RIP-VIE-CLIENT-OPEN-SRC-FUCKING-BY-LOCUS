package me.drake.vieclient.gui.nvg.adapter.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.setting.BindSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BooleanSetting;
import me.drake.vieclient.util.Sfx;

public class Sounds extends Module {
   public static final Sounds instance = new Sounds();
   public final BooleanSetting booleanActivate = flag(true);
   public final BooleanSetting booleanDeactivate = flag(true);
   public final BooleanSetting hovering = flag(true);
   public final BooleanSetting moduleActivate = flag(true);
   public final BooleanSetting moduleDeactivate = flag(true);
   public final BooleanSetting moduleCollapse = flag(true);
   public final BooleanSetting moduleExpand = flag(true);
   public final BooleanSetting keybindActivate = flag(true);
   public final BooleanSetting keybindDeactivate = flag(true);
   public final BooleanSetting noModuleSettings = flag(true);
   public final BooleanSetting themeChange = flag(true);
   public final BooleanSetting tap = flag(true);
   public final BooleanSetting guiOpenClose = flag(true);
   public final BooleanSetting slider = flag(true);
   public final BooleanSetting bindSuccess = flag(true);
   public final BooleanSetting bindRemove = flag(true);
   public final BooleanSetting cfgLoad = flag(true);
   public boolean soundEnabled = true;
   private List settingsCache;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static BooleanSetting flag(boolean v) {
      return new BooleanSetting("", v);
   }

   private Sounds() {
      super(new Sounds.Raw());
   }

   @Override
   public CharSequence getName() {
      byte[] var10000 = new byte[6];
      var10000[0] = (byte)(121);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(105);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-112);
      var10000[5] = (byte)(-78);
      return $$v_s(var10000, 3631061616389509135L);
   }

   @Override
   public boolean isEnabled() {
      return this.soundEnabled;
   }

   @Override
   public void setEnabled(boolean b) {
      this.soundEnabled = b;
   }

   @Override
   public void toggle() {
      this.soundEnabled = !this.soundEnabled;
   }

   @Override
   public Category getCategory() {
      return Category.CLIENT;
   }

   private boolean can(BooleanSetting s) {
      return this.soundEnabled && s.getValue();
   }

   public void playBooleanActivate() {
      if (this.can(this.booleanActivate)) {
         Sfx.play(Sfx.BOOLEAN_ACTIVATE);
      }
   }

   public void playBooleanDeactivate() {
      if (this.can(this.booleanDeactivate)) {
         Sfx.play(Sfx.BOOLEAN_DEACTIVATE);
      }
   }

   public void playHovering() {
      if (this.can(this.hovering)) {
         Sfx.play(Sfx.HOVERING, 0.6F);
      }
   }

   public void playModuleActivate() {
      if (this.can(this.moduleActivate)) {
         Sfx.play(Sfx.MODULE_ACTIVATE);
      }
   }

   public void playModuleDeactivate() {
      if (this.can(this.moduleDeactivate)) {
         Sfx.play(Sfx.MODULE_DEACTIVATE);
      }
   }

   public void playModuleCollapse() {
      if (this.can(this.moduleCollapse)) {
         Sfx.play(Sfx.MODULE_COLLAPSE);
      }
   }

   public void playModuleExpand() {
      if (this.can(this.moduleExpand)) {
         Sfx.play(Sfx.MODULE_EXPAND);
      }
   }

   public void playKeybindActivate() {
      if (this.can(this.keybindActivate)) {
         Sfx.play(Sfx.MODULE_KEYBIND_ACTIVATE);
      }
   }

   public void playKeybindDeactivate() {
      if (this.can(this.keybindDeactivate)) {
         Sfx.play(Sfx.MODULE_KEYBIND_DEACTIVATE);
      }
   }

   public void playNoModuleSettings() {
      if (this.can(this.noModuleSettings)) {
         Sfx.play(Sfx.NO_MODULE_SETTINGS);
      }
   }

   public void playThemeChange() {
      if (this.can(this.themeChange)) {
         Sfx.play(Sfx.THEME_CHANGE);
      }
   }

   public void playTap() {
      if (this.can(this.tap)) {
         Sfx.play(Sfx.TAP);
      }
   }

   public void playGuiOpen() {
      if (this.can(this.guiOpenClose)) {
         Sfx.guiOpen();
      }
   }

   public void playGuiClose() {
      if (this.can(this.guiOpenClose)) {
         Sfx.guiClose();
      }
   }

   public void playSlider() {
      if (this.can(this.slider)) {
         Sfx.sliderTick();
      }
   }

   public void sliderTick() {
      this.playSlider();
   }

   public void playBindSuccess() {
      if (this.can(this.bindSuccess)) {
         Sfx.play(Sfx.BIND_SUCCESS);
      }
   }

   public void playBindRemove() {
      if (this.can(this.bindRemove)) {
         Sfx.play(Sfx.BIND_REMOVE);
      }
   }

   public void playCfgLoad() {
      if (this.can(this.cfgLoad)) {
         Sfx.play(Sfx.CFGLOAD);
      }
   }

   @Override
   public List getSettings() {
      if (this.settingsCache == null) {
         ArrayList var1 = new ArrayList();
         var1.add(new BindSetting("Keybind", 0, true, this));
         var1.add(new BooleanSetting("GUI Open/Close", true));
         var1.add(new BooleanSetting("Module Activate", true));
         var1.add(
            new BooleanSetting("Module Deactivate", true)
         );
         var1.add(new BooleanSetting("Boolean Activate", true));
         var1.add(
            new BooleanSetting("Boolean Deactivate", true)
         );
         var1.add(new BooleanSetting("Slider Tick", true));
         var1.add(new BooleanSetting("Module Expand", true));
         var1.add(new BooleanSetting("Module Collapse", true));
         var1.add(new BooleanSetting("Keybind Set", true));
         var1.add(new BooleanSetting("Keybind Remove", true));
         var1.add(new BooleanSetting("Config Load", true));
         var1.add(new BooleanSetting("Hovering", true));
         var1.add(
            new BooleanSetting(
               "No Module Settings", true
            )
         );
         var1.add(new BooleanSetting("Theme Change", true));
         var1.add(new BooleanSetting("Tap", true));
         this.settingsCache = List.copyOf(var1);
      }

      return this.settingsCache;
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

   private static final class Raw extends me.drake.vieclient.core.module.Module {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Raw() {
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(106);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-80);
         var10001[4] = (byte)(-12);
         var10001[5] = (byte)(33);
         String var1 = $$v_s(var10001, 7320755303261392924L);
         byte[] var10002 = new byte[17];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-2);
         var10002[4] = (byte)(-29);
         var10002[5] = (byte)(61);
         var10002[6] = (byte)(-18);
         var10002[7] = (byte)(35);
         var10002[8] = (byte)(53);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-127);
         var10002[11] = (byte)(-112);
         var10002[12] = (byte)(-82);
         var10002[13] = (byte)(-17);
         var10002[14] = (byte)(-48);
         var10002[15] = (byte)(65);
         var10002[16] = (byte)(26);
         super(var1, $$v_s(var10002, 7320755303261392924L), me.drake.vieclient.core.module.Category.CLIENT);
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
