package me.drake.vieclient.gui.nvg.adapter.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.setting.ModeSetting;

public class MusicHUD extends Module {
   private static final ModeSetting SOURCE_PRIORITY = new ModeSetting(me.drake.vieclient.modules.client.MusicHUD.sourcePriority);
   public static final MusicHUD instance = new MusicHUD();
   private static me.drake.vieclient.modules.client.MusicHUD rawModule;
   private List settingBindings;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MusicHUD() {
      super(new MusicHUD.Raw());
   }

   @Override
   public CharSequence getName() {
      byte[] var10000 = new byte[8];
      var10000[0] = (byte)(119);
      var10000[1] = (byte)(-17);
      var10000[2] = (byte)(111);
      var10000[3] = (byte)(-73);
      var10000[4] = (byte)(-103);
      var10000[5] = (byte)(21);
      var10000[6] = (byte)(-115);
      var10000[7] = (byte)(57);
      return $$v_s(var10000, 6186682838158659615L);
   }

   private void ensureSettingBindings(me.drake.vieclient.modules.client.MusicHUD raw) {
      if (this.settingBindings == null) {
         ArrayList var2 = new ArrayList();

         for (Setting var4 : raw.getSettings()) {
            try {
               if (var4.isDeclaredVisibleInGui()) {
                  me.drake.vieclient.gui.nvg.adapter.setting.Setting var5 = var4 == me.drake.vieclient.modules.client.MusicHUD.sourcePriority
                     ? SOURCE_PRIORITY
                     : Module.mapSetting(var4);
                  if (var5 != null) {
                     var2.add(new MusicHUD.SettingBinding(var4, var5));
                  }
               }
            } catch (Throwable var6) {
            }
         }

         this.settingBindings = Collections.unmodifiableList(var2);
      }
   }

   @Override
   public List getSettings() {
      me.drake.vieclient.modules.client.MusicHUD var1 = resolve();
      if (var1 == null) {
         return List.of(SOURCE_PRIORITY);
      }

      this.ensureSettingBindings(var1);
      ArrayList var2 = new ArrayList();

      for (MusicHUD.SettingBinding var4 : this.settingBindings) {
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
      if (rawModule == null) {
         rawModule = resolve();
      }

      return rawModule != null && rawModule.isEnabled();
   }

   @Override
   public void setEnabled(boolean b) {
      if (rawModule == null) {
         rawModule = resolve();
      }

      if (rawModule != null) {
         rawModule.setEnabled(b);
      }
   }

   @Override
   public void toggle() {
      if (rawModule == null) {
         rawModule = resolve();
      }

      if (rawModule != null) {
         rawModule.toggle();
      }
   }

   private static me.drake.vieclient.modules.client.MusicHUD resolve() {
      try {
         VieClientMod var0 = VieClientMod.getInstance();
         if (var0 != null && var0.getModuleManager() != null) {
            return (me.drake.vieclient.modules.client.MusicHUD)var0.getModuleManager().getModule(me.drake.vieclient.modules.client.MusicHUD.class);
         }
      } catch (Throwable var1) {
      }

      return null;
   }

   public void pollForTitle() {
      try {
         me.drake.vieclient.modules.client.MusicHUD var1 = resolve();
         if (var1 != null && var1.isEnabled()) {
            var1.pollForTitle();
         }
      } catch (Throwable var2) {
      }
   }

   public static String spTrack() {
      return me.drake.vieclient.modules.client.MusicHUD.spTrack();
   }

   public static String spArtist() {
      return me.drake.vieclient.modules.client.MusicHUD.spArtist();
   }

   public static boolean spPlaying() {
      return me.drake.vieclient.modules.client.MusicHUD.spPlaying();
   }

   public static int spLength() {
      return me.drake.vieclient.modules.client.MusicHUD.spLength();
   }

   public static int spImageId() {
      return me.drake.vieclient.modules.client.MusicHUD.spImageId();
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
         byte[] var10001 = new byte[8];
         var10001[0] = (byte)(116);
         var10001[1] = (byte)(-17);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-73);
         var10001[4] = (byte)(-123);
         var10001[5] = (byte)(-1);
         var10001[6] = (byte)(-61);
         var10001[7] = (byte)(-63);
         String var1 = $$v_s(var10001, -5938724675626217444L);
         byte[] var10002 = new byte[23];
         var10002[0] = (byte)(120);
         var10002[1] = (byte)(-17);
         var10002[2] = (byte)(104);
         var10002[3] = (byte)(-79);
         var10002[4] = (byte)(-117);
         var10002[5] = (byte)(-42);
         var10002[6] = (byte)(-30);
         var10002[7] = (byte)(-20);
         var10002[8] = (byte)(50);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-119);
         var10002[11] = (byte)(-125);
         var10002[12] = (byte)(-51);
         var10002[13] = (byte)(6);
         var10002[14] = (byte)(-35);
         var10002[15] = (byte)(-35);
         var10002[16] = (byte)(6);
         var10002[17] = (byte)(60);
         var10002[18] = (byte)(-87);
         var10002[19] = (byte)(-36);
         var10002[20] = (byte)(58);
         var10002[21] = (byte)(38);
         var10002[22] = (byte)(-65);
         super(var1, $$v_s(var10002, -5938724675626217444L), Category.PROTECT, -1);
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
