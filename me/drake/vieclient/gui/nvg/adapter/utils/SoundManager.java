package me.drake.vieclient.gui.nvg.adapter.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.util.Mp3Player;
import me.drake.vieclient.util.Sfx;
import net.minecraft.class_310;
import net.minecraft.class_3414;

public final class SoundManager {
   private static final class_310 mc = class_310.method_1551();
   private static Mp3Player.Handle menuMusic = null;
   private static final MenuMusicState menuMusicState = new MenuMusicState();
   private static final String[] N_THEME = new String[]{"theme"};
   private static final String[] N_JUMP = new String[]{
      "cartoon-jump",
      "carton-jump",
      "carton_jump",
      "cartoon_jump",
      "jump"
   };
   private static final String[] N_TRANSITION = new String[]{
      "transitions",
      "transition"
   };
   private static final String[] N_MAGIC = new String[]{
      "appear-magic",
      "appear_magic",
      "magic"
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SoundManager() {
   }

   private static String resolveMp3(String[] names) {
      try {
         File var1 = new File(
            mc.field_1697, "vieclient/sounds"
         );
         if (!var1.exists()) {
            var1.mkdirs();
         }

         for (String var5 : names) {
            File var6 = new File(var1, var5 + ".mp3");
            if (var6.isFile()) {
               return var6.getAbsolutePath();
            }
         }

         File var12 = new File(
            mc.field_1697, "Vie Client/sounds"
         );
         if (var12.isDirectory()) {
            for (String var23 : names) {
               File var7 = new File(var12, var23 + ".mp3");
               if (var7.isFile()) {
                  return var7.getAbsolutePath();
               }
            }
         }

         File var15 = new File(mc.field_1697, "DonutBBC/sounds");
         if (var15.isDirectory()) {
            for (String var26 : names) {
               File var8 = new File(var15, var26 + ".mp3");
               if (var8.isFile()) {
                  return var8.getAbsolutePath();
               }
            }
         }
      } catch (Throwable var10) {
      }

      for (String var19 : names) {
         for (String var28 : new String[]{
            "assets/vie_client/vie/sounds/",
            "assets/vie_client/vie/theme/",
            "assets/vie_client/sounds/"
         }) {
            String var9 = var28 + var19 + ".mp3";
            if (SoundManager.class.getClassLoader().getResource(var9) != null) {
               return var9;
            }
         }
      }

      return null;
   }

   public static void startMenuMusic(float volume) {
      if (!menuMusicState.isMuted() && menuMusic == null) {
         String var1 = resolveMp3(N_THEME);
         if (var1 != null) {
            menuMusic = Mp3Player.playLoop(var1, volume);
         }
      }
   }

   public static void stopMenuMusic() {
      if (menuMusic != null) {
         menuMusic.stop();
         menuMusic = null;
      }
   }

   public static boolean isMenuMusicPlaying() {
      return menuMusic != null;
   }

   public static boolean isMenuMusicMuted() {
      return menuMusicState.isMuted();
   }

   public static boolean toggleMenuMusicMuted() {
      boolean var0 = menuMusicState.toggle();
      if (var0) {
         stopMenuMusic();
      }

      return var0;
   }

   public static void play(String name) {
      play(name, 1.0F);
   }

   public static void play(String name, float volume) {
      String var2 = null;
      switch (name) {
         case "carton_jump":
            var2 = resolveMp3(N_JUMP);
            break;
         case "transition":
            var2 = resolveMp3(N_TRANSITION);
            break;
         case "appear_magic":
            var2 = resolveMp3(N_MAGIC);
      }

      if (!name.equals("carton_jump")
         && !name.equals("transition")
         && !name.equals("appear_magic")) {
         class_3414 var5 = sfx(name);
         if (var5 != null) {
            Sfx.play(var5, volume);
         }
      } else {
         if (var2 != null) {
            Mp3Player.playOnce(var2, 1.0F);
         }
      }
   }

   private static class_3414 sfx(String name) {
      switch (name) {
         case "module_activate":
            return Sfx.MODULE_ACTIVATE;
         case "module_deactivate":
            return Sfx.MODULE_DEACTIVATE;
         case "boolean_activate":
            return Sfx.BOOLEAN_ACTIVATE;
         case "boolean_deactivate":
            return Sfx.BOOLEAN_DEACTIVATE;
         case "hovering_sound":
            return Sfx.HOVERING;
         case "module_collapse":
            return Sfx.MODULE_COLLAPSE;
         case "module_expand":
            return Sfx.MODULE_EXPAND;
         case "module_keybind_activate":
            return Sfx.MODULE_KEYBIND_ACTIVATE;
         case "module_keybind_deactivate":
            return Sfx.MODULE_KEYBIND_DEACTIVATE;
         case "no_module_settings":
            return Sfx.NO_MODULE_SETTINGS;
         case "tap":
            return Sfx.TAP;
         case "theme_change":
            return Sfx.THEME_CHANGE;
         case "gui_open":
         case "guiopen":
            return Sfx.GUI_OPEN;
         case "gui_close":
         case "guiclose":
            return Sfx.GUI_CLOSE;
         case "slider":
            return Sfx.SLIDER;
         case "bind_success":
            return Sfx.BIND_SUCCESS;
         case "bind_remove":
            return Sfx.BIND_REMOVE;
         case "cfgload":
            return Sfx.CFGLOAD;
         default:
            return null;
      }
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
