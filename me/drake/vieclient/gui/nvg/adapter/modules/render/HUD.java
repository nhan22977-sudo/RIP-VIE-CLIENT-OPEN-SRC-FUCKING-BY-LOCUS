package me.drake.vieclient.gui.nvg.adapter.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.setting.MinMaxSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.setting.NumberSetting;

public class HUD extends Module {
   public static final HUD instance = new HUD();
   private static me.drake.vieclient.modules.client.HUD rawModule;
   private List settingBindings;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private HUD() {
      super(new HUD.Raw());
   }

   public static me.drake.vieclient.modules.client.HUD rawModule() {
      if (rawModule == null) {
         try {
            VieClientMod var0 = VieClientMod.getInstance();
            if (var0 != null && var0.getModuleManager() != null) {
               rawModule = (me.drake.vieclient.modules.client.HUD)var0.getModuleManager().getModule(me.drake.vieclient.modules.client.HUD.class);
            }
         } catch (Throwable var1) {
         }
      }

      return rawModule;
   }

   @Override
   public CharSequence getName() {
      byte[] var10000 = new byte[3];
      var10000[0] = (byte)(114);
      var10000[1] = (byte)(-49);
      var10000[2] = (byte)(88);
      return $$v_s(var10000, 4964882453399588895L);
   }

   private void ensureSettingBindings(me.drake.vieclient.modules.client.HUD raw) {
      if (this.settingBindings == null) {
         ArrayList var2 = new ArrayList();

         for (Setting var4 : raw.getSettings()) {
            try {
               if (var4.isDeclaredVisibleInGui()) {
                  if (var4 instanceof MinMaxSetting var5) {
                     NumberSetting var6 = new NumberSetting(
                           var5.getName() + " Min",
                           var5.getMinValue(),
                           var5.getMaxValue(),
                           var5.getCurrentMin(),
                           var5.getStep(),
                           () -> var5.getCurrentMin(),
                           v -> var5.setCurrentMin(v)
                        )
                        .setDescription(var5.getDescription());
                     var6.setDescriptionSupplier(var5::getDescription);
                     NumberSetting var7 = new NumberSetting(
                           var5.getName() + " Max",
                           var5.getMinValue(),
                           var5.getMaxValue(),
                           var5.getCurrentMax(),
                           var5.getStep(),
                           () -> var5.getCurrentMax(),
                           v -> var5.setCurrentMax(v)
                        )
                        .setDescription(var5.getDescription());
                     var7.setDescriptionSupplier(var5::getDescription);
                     var2.add(new HUD.SettingBinding(var4, var6));
                     var2.add(new HUD.SettingBinding(var4, var7));
                  } else {
                     me.drake.vieclient.gui.nvg.adapter.setting.Setting var9 = Module.mapSetting(var4);
                     if (var9 != null) {
                        var9.setDescriptionSupplier(var4::getDescription);
                        var2.add(new HUD.SettingBinding(var4, var9));
                     }
                  }
               }
            } catch (Throwable var8) {
            }
         }

         this.settingBindings = Collections.unmodifiableList(var2);
      }
   }

   @Override
   public List getSettings() {
      me.drake.vieclient.modules.client.HUD var1 = rawModule();
      if (var1 == null) {
         return super.getSettings();
      }

      this.ensureSettingBindings(var1);
      ArrayList var2 = new ArrayList();

      for (HUD.SettingBinding var4 : this.settingBindings) {
         try {
            if (var4.rawSetting().isVisibleInGui()) {
               var2.add(var4.mappedSetting());
            }
         } catch (Throwable var6) {
            var2.add(var4.mappedSetting());
         }
      }

      return var2;
   }

   @Override
   public boolean isEnabled() {
      return rawModule() != null && rawModule().isEnabled();
   }

   @Override
   public void setEnabled(boolean b) {
      if (rawModule() != null) {
         rawModule().setEnabled(b);
      }
   }

   @Override
   public void toggle() {
      if (rawModule() != null) {
         rawModule().toggle();
      }
   }

   @Override
   public Category getCategory() {
      return Category.PROTECT;
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
         byte[] var10001 = new byte[3];
         var10001[0] = (byte)(113);
         var10001[1] = (byte)(-49);
         var10001[2] = (byte)(88);
         String var1 = $$v_s(var10001, -8592207122371719140L);
         byte[] var10002 = new byte[11];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-12);
         var10002[2] = (byte)(49);
         var10002[3] = (byte)(-71);
         var10002[4] = (byte)(-3);
         var10002[5] = (byte)(-22);
         var10002[6] = (byte)(-92);
         var10002[7] = (byte)(-128);
         var10002[8] = (byte)(25);
         var10002[9] = (byte)(-9);
         var10002[10] = (byte)(-96);
         super(var1, $$v_s(var10002, -8592207122371719140L), me.drake.vieclient.core.module.Category.PROTECT);
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

   private record SettingBinding(Setting<?> rawSetting, me.drake.vieclient.gui.nvg.adapter.setting.Setting<?> mappedSetting) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
