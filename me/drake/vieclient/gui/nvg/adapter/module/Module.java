package me.drake.vieclient.gui.nvg.adapter.module;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.core.setting.MinMaxSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BindSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BooleanSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ModeSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.NumberSetting;

public class Module {
   protected final me.drake.vieclient.core.module.Module raw;
   private final CharSequence keybindName;
   private List settingBindings;
   private BindSetting keybindSetting;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Module(me.drake.vieclient.core.module.Module raw) {
      this.raw = rawx;
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(113);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(101);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-116);
      var10001[5] = (byte)(64);
      var10001[6] = (byte)(60);
      this.keybindName = $$v_s(var10001, -8909263158645632993L);
   }

   public Module(me.drake.vieclient.core.module.Module raw, CharSequence keybindName) {
      this.raw = rawx;
      this.keybindName = keybindNamex;
   }

   public CharSequence getName() {
      return this.raw.getName();
   }

   public CharSequence getDisplayName() {
      return this.raw.getDisplayName();
   }

   public CharSequence getDescription() {
      return this.raw.getDescription();
   }

   public boolean isEnabled() {
      return this.raw.isEnabled();
   }

   public void toggle() {
      this.raw.toggle();
   }

   public void setEnabled(boolean enabled) {
      this.raw.setEnabled(enabled);
   }

   public Category getCategory() {
      return ModuleManager.getCategory(this.raw.getCategory());
   }

   public int getKeybind() {
      return BindSetting.toMeilaaKey((Integer)this.raw.getKeybind().get());
   }

   public int rawKeybind() {
      return (Integer)this.raw.getKeybind().get();
   }

   public void setKeybind(int key) {
      this.raw.getKeybind().set(BindSetting.toVieKey(key));
   }

   public me.drake.vieclient.core.module.Module raw() {
      return this.raw;
   }

   public List getSettings() {
      this.ensureSettingBindings();
      ArrayList var1 = new ArrayList();
      var1.add(this.keybindSetting);

      for (Module.SettingBinding var3 : this.settingBindings) {
         try {
            if (var3.rawSetting().isVisibleInGui() && var3.mappedSetting().isVisible()) {
               var1.add(var3.mappedSetting());
            }
         } catch (Throwable var5) {
            if (var3.mappedSetting().isVisible()) {
               var1.add(var3.mappedSetting());
            }
         }
      }

      return Collections.unmodifiableList(var1);
   }

   public List getAllSettings() {
      this.ensureSettingBindings();
      ArrayList var1 = new ArrayList();
      var1.add(this.keybindSetting);

      for (Module.SettingBinding var3 : this.settingBindings) {
         var1.add(var3.mappedSetting());
      }

      return Collections.unmodifiableList(var1);
   }

   private void ensureSettingBindings() {
      if (this.settingBindings == null) {
         this.keybindSetting = new BindSetting(this.keybindName, this.getKeybind(), true, this);
         ArrayList var1 = new ArrayList();

         for (Setting var3 : this.raw.getSettings()) {
            try {
               if (var3.isDeclaredVisibleInGui()) {
                  if (var3 instanceof MinMaxSetting var4) {
                     NumberSetting var5 = new NumberSetting(
                           var4.getName() + " Min",
                           var4.getMinValue(),
                           var4.getMaxValue(),
                           var4.getCurrentMin(),
                           var4.getStep(),
                           () -> var4.getCurrentMin(),
                           v -> var4.setCurrentMin(v)
                        )
                        .setDescription(var4.getDescription());
                     var5.setDescriptionSupplier(var4::getDescription);
                     NumberSetting var6 = new NumberSetting(
                           var4.getName() + " Max",
                           var4.getMinValue(),
                           var4.getMaxValue(),
                           var4.getCurrentMax(),
                           var4.getStep(),
                           () -> var4.getCurrentMax(),
                           v -> var4.setCurrentMax(v)
                        )
                        .setDescription(var4.getDescription());
                     var6.setDescriptionSupplier(var4::getDescription);
                     var1.add(new Module.SettingBinding(var3, var5));
                     var1.add(new Module.SettingBinding(var3, var6));
                  } else {
                     me.drake.vieclient.gui.nvg.adapter.setting.Setting var8 = mapSetting(var3);
                     if (var8 != null) {
                        var8.setDescriptionSupplier(var3::getDescription);
                        var1.add(new Module.SettingBinding(var3, var8));
                     }
                  }
               }
            } catch (Throwable var7) {
            }
         }

         this.settingBindings = Collections.unmodifiableList(var1);
      }
   }

   public static me.drake.vieclient.gui.nvg.adapter.setting.Setting mapSetting(Setting vs) {
      me.drake.vieclient.gui.nvg.adapter.setting.Setting var1 = null;
      if (vs instanceof BoolSetting var2) {
         var1 = new BooleanSetting(var2);
      } else if (vs instanceof DoubleSetting var3) {
         var1 = new NumberSetting(var3);
      } else if (vs instanceof IntSetting var4) {
         var1 = new NumberSetting(var4);
      } else if (vs instanceof ColorSetting var5) {
         var1 = new me.drake.vieclient.gui.nvg.adapter.setting.ColorSetting(var5);
      } else if (vs instanceof StringSetting var6) {
         var1 = new me.drake.vieclient.gui.nvg.adapter.setting.StringSetting(var6);
      } else if (vs instanceof EnumSetting var7) {
         var1 = wrapEnum(var7);
      } else if (vs instanceof ButtonSetting var8) {
         var1 = new me.drake.vieclient.gui.nvg.adapter.setting.ButtonSetting(var8);
      } else if (vs instanceof me.drake.vieclient.core.setting.BindSetting var9) {
         var1 = new BindSetting(var9.getName(), BindSetting.toMeilaaKey((Integer)var9.get()), var9.isModuleKey(), null, null, var9);
      } else if (vs instanceof KeybindSetting var10) {
         var1 = new BindSetting(var10.getName(), BindSetting.toMeilaaKey((Integer)var10.get()), false, null, var10, null);
      }

      if (var1 != null) {
         var1.setDescription(vs.getDescription());
         var1.setDescriptionSupplier(vs::getDescription);
      }

      return var1;
   }

   private static ModeSetting wrapEnum(EnumSetting e) {
      return new ModeSetting(e);
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

   private record SettingBinding(Setting<?> rawSetting, me.drake.vieclient.gui.nvg.adapter.setting.Setting<?> mappedSetting) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
