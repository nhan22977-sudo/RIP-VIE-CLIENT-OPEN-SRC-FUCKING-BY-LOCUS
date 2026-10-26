package me.drake.vieclient.gui.nvg.adapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_310;

public final class ConfigManager {
   private static final Path DIR;
   private static final Path FALLBACK_DIR;
   private static final Path LEGACY_DIR;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Path settingsFile() {
      try {
         class_310 var1 = class_310.method_1551();
         if (var1 != null && var1.field_1697 != null) {
            Path var6 = var1.field_1697
               .toPath()
               .resolve("vieclient")
               .resolve("settings.json");
            if (Files.exists(var6)) {
               return var6;
            }

            Path var3 = var1.field_1697
               .toPath()
               .resolve("viecheats")
               .resolve("settings.json");
            if (Files.exists(var3)) {
               return var3;
            }

            return var6;
         }
      } catch (Throwable var4) {
      }

      Path var5 = Path.of(
         "vieclient",
         "settings.json"
      );
      if (Files.exists(var5)) {
         return var5;
      }

      Path var2 = Path.of(
         "viecheats",
         "settings.json"
      );
      return Files.exists(var2) ? var2 : var5;
   }

   private boolean saveNow() {
      try {
         me.drake.vieclient.core.config.ConfigManager var1 = this.cfg();
         if (var1 != null) {
            var1.save();
         }

         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   private boolean loadNow() {
      try {
         me.drake.vieclient.core.config.ConfigManager var1 = this.cfg();
         if (var1 != null) {
            var1.load();
         }

         return true;
      } catch (Throwable var2) {
         return false;
      }
   }

   private me.drake.vieclient.core.config.ConfigManager cfg() {
      VieClientMod var1 = VieClientMod.getInstance();
      return var1 != null ? var1.getConfigManager() : null;
   }

   public static Path getConfigsDir() {
      try {
         class_310 var0 = class_310.method_1551();
         if (var0 != null && var0.field_1697 != null) {
            Path var1 = var0.field_1697
               .toPath()
               .resolve("vieclient")
               .resolve("configs");
            if (Files.exists(var1)) {
               return var1;
            }

            Path var2 = var0.field_1697
               .toPath()
               .resolve("Vie Client")
               .resolve("configs");
            if (Files.exists(var2)) {
               return var2;
            }

            Path var3 = var0.field_1697
               .toPath()
               .resolve("DonutBBC")
               .resolve("configs");
            if (Files.exists(var3)) {
               return var3;
            }

            return var1;
         }
      } catch (Throwable var4) {
      }

      if (Files.exists(DIR)) {
         return DIR;
      } else if (Files.exists(FALLBACK_DIR)) {
         return FALLBACK_DIR;
      } else {
         return Files.exists(LEGACY_DIR) ? LEGACY_DIR : DIR;
      }
   }

   public boolean saveProfile(String name) {
      if (name != null && !name.trim().isEmpty()) {
         String var2 = sanitize(name);

         try {
            this.saveNow();
            Path var3 = getConfigsDir();
            Files.createDirectories(var3);
            Path var4 = var3.resolve(var2 + ".json");
            me.drake.vieclient.core.config.ConfigManager var5 = this.cfg();
            if (var5 != null) {
               var5.saveToFile(var4);
               if (Files.exists(var4)) {
                  return true;
               }
            }

            Path var6 = this.settingsFile();
            if (Files.exists(var6)) {
               Files.copy(var6, var4, StandardCopyOption.REPLACE_EXISTING);
               return Files.exists(var4);
            } else {
               return false;
            }
         } catch (Throwable var7) {
            VieClientMod.LOGGER.error("[ConfigManager] Failed to save profile " + name, var7);
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean loadProfile(String name) {
      if (name != null && !name.trim().isEmpty()) {
         String var2 = sanitize(name);

         try {
            Path var3 = getConfigsDir();
            Path var4 = var3.resolve(var2 + ".json");
            if (!Files.exists(var4) && !var3.equals(DIR)) {
               var4 = DIR.resolve(var2 + ".json");
            }

            if (!Files.exists(var4) && !var3.equals(FALLBACK_DIR)) {
               var4 = FALLBACK_DIR.resolve(var2 + ".json");
            }

            if (!Files.exists(var4) && !var3.equals(LEGACY_DIR)) {
               var4 = LEGACY_DIR.resolve(var2 + ".json");
            }

            if (!Files.exists(var4)) {
               return false;
            }

            me.drake.vieclient.core.config.ConfigManager var5 = this.cfg();
            if (var5 != null && var5.loadFromFile(var4)) {
               return true;
            }

            Path var6 = this.settingsFile();
            if (var6.getParent() != null) {
               Files.createDirectories(var6.getParent());
            }

            Files.copy(var4, var6, StandardCopyOption.REPLACE_EXISTING);
            return this.loadNow();
         } catch (Throwable var7) {
            VieClientMod.LOGGER.error("[ConfigManager] Failed to load profile " + name, var7);
            return false;
         }
      } else {
         return false;
      }
   }

   public void deleteProfile(String name) {
      if (name != null && !name.isEmpty()) {
         try {
            Files.deleteIfExists(getConfigsDir().resolve(sanitize(name) + ".json"));
            if (!getConfigsDir().equals(DIR)) {
               Files.deleteIfExists(DIR.resolve(sanitize(name) + ".json"));
            }

            if (!getConfigsDir().equals(FALLBACK_DIR)) {
               Files.deleteIfExists(FALLBACK_DIR.resolve(sanitize(name) + ".json"));
            }

            if (!getConfigsDir().equals(LEGACY_DIR)) {
               Files.deleteIfExists(LEGACY_DIR.resolve(sanitize(name) + ".json"));
            }
         } catch (IOException var3) {
         }
      }
   }

   private static String sanitize(String name) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : name.toCharArray()) {
         if (Character.isLetterOrDigit(var5) || var5 == (45) || var5 == (95) || var5 == (32)) {
            var1.append(var5);
         }
      }

      String var6 = var1.toString().trim();
      String var7;
      if (var6.isEmpty()) {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(77);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-72);
         var10000[4] = (byte)(80);
         var10000[5] = (byte)(-45);
         var7 = $$v_s(var10000, -164264287161561077L);
      } else {
         var7 = var6;
      }

      return var7;
   }

   static {
      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(88);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(85);
      var10000[5] = (byte)(-35);
      var10000[6] = (byte)(-34);
      var10000[7] = (byte)(-69);
      var10000[8] = (byte)(50);
      String var0 = $$v_s(var10000, -164264287161561077L);
      String[] var10001 = new String[1];
      byte[] var10004 = new byte[7];
      var10004[0] = (byte)(77);
      var10004[1] = (byte)(-11);
      var10004[2] = (byte)(114);
      var10004[3] = (byte)(-72);
      var10004[4] = (byte)(80);
      var10004[5] = (byte)(-45);
      var10004[6] = (byte)(-56);
      var10001[0] = $$v_s(var10004, -164264287161561077L);
      DIR = Path.of(var0, var10001);
      var10000 = new byte[10];
      var10000[0] = (byte)(120);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(122);
      var10000[5] = (byte)(-40);
      var10000[6] = (byte)(-46);
      var10000[7] = (byte)(-80);
      var10000[8] = (byte)(40);
      var10000[9] = (byte)(-42);
      String var2 = $$v_s(var10000, -164264287161561077L);
      var10001 = new String[1];
      var10004 = new byte[7];
      var10004[0] = (byte)(77);
      var10004[1] = (byte)(-11);
      var10004[2] = (byte)(114);
      var10004[3] = (byte)(-72);
      var10004[4] = (byte)(80);
      var10004[5] = (byte)(-45);
      var10004[6] = (byte)(-56);
      var10001[0] = $$v_s(var10004, -164264287161561077L);
      FALLBACK_DIR = Path.of(var2, var10001);
      var10000 = new byte[8];
      var10000[0] = (byte)(106);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-85);
      var10000[4] = (byte)(77);
      var10000[5] = (byte)(-10);
      var10000[6] = (byte)(-7);
      var10000[7] = (byte)(-106);
      String var4 = $$v_s(var10000, -164264287161561077L);
      var10001 = new String[1];
      var10004 = new byte[7];
      var10004[0] = (byte)(77);
      var10004[1] = (byte)(-11);
      var10004[2] = (byte)(114);
      var10004[3] = (byte)(-72);
      var10004[4] = (byte)(80);
      var10004[5] = (byte)(-45);
      var10004[6] = (byte)(-56);
      var10001[0] = $$v_s(var10004, -164264287161561077L);
      LEGACY_DIR = Path.of(var4, var10001);
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
