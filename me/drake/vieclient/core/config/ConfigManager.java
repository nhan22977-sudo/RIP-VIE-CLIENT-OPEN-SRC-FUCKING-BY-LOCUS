package me.drake.vieclient.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.client.MusicHUD;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.modules.client.hud.HudLayoutCodec;
import net.minecraft.class_310;

public class ConfigManager {
   private static final String LEGACY_SPOTIFY_HUD_NAME = "Spotify HUD";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final long AUTOSAVE_DELAY_MS = 1200L;
   private final Path configDir;
   private final Path settingsFile;
   private final Path settingsBackupFile;
   private final Path legacyConfigFile;
   private boolean loading = false;
   private boolean dirty = false;
   private long lastDirtyAtMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ConfigManager() {
      this(resolveDefaultConfigDir());
   }

   public ConfigManager(Path configDir) {
      this.configDir = configDirx;
      byte[] var10002 = new byte[13];
      var10002[0] = (byte)(61);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(104);
      var10002[3] = (byte)(-86);
      var10002[4] = (byte)(79);
      var10002[5] = (byte)(-58);
      var10002[6] = (byte)(-98);
      var10002[7] = 1;
      var10002[8] = (byte)(8);
      var10002[9] = (byte)(-56);
      var10002[10] = (byte)(-105);
      var10002[11] = (byte)(-103);
      var10002[12] = (byte)(16);
      this.settingsFile = configDirx.resolve($$v_s(var10002, 6555682634112159851L));
      var10002 = new byte[20];
      var10002[0] = (byte)(61);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(104);
      var10002[3] = (byte)(-86);
      var10002[4] = (byte)(79);
      var10002[5] = (byte)(-58);
      var10002[6] = (byte)(-98);
      var10002[7] = 1;
      var10002[8] = (byte)(8);
      var10002[9] = (byte)(-64);
      var10002[10] = (byte)(-123);
      var10002[11] = (byte)(-107);
      var10002[12] = (byte)(21);
      var10002[13] = (byte)(5);
      var10002[14] = (byte)(-95);
      var10002[15] = (byte)(36);
      var10002[16] = (byte)(116);
      var10002[17] = (byte)(57);
      var10002[18] = (byte)(-93);
      var10002[19] = (byte)(-64);
      this.settingsBackupFile = configDirx.resolve($$v_s(var10002, 6555682634112159851L));
      var10002 = new byte[11];
      var10002[0] = (byte)(45);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(114);
      var10002[3] = (byte)(-72);
      var10002[4] = (byte)(79);
      var10002[5] = (byte)(-49);
      var10002[6] = (byte)(-41);
      var10002[7] = (byte)(24);
      var10002[8] = (byte)(85);
      var10002[9] = (byte)(-51);
      var10002[10] = (byte)(-118);
      this.legacyConfigFile = configDirx.resolve($$v_s(var10002, 6555682634112159851L));
   }

   private static Path resolveDefaultConfigDir() {
      try {
         if (class_310.method_1551() != null && class_310.method_1551().field_1697 != null) {
            Path var2 = class_310.method_1551().field_1697.toPath();
            byte[] var10001 = new byte[9];
            var10001[0] = (byte)(56);
            var10001[1] = (byte)(-13);
            var10001[2] = (byte)(121);
            var10001[3] = (byte)(-67);
            var10001[4] = (byte)(74);
            var10001[5] = (byte)(-63);
            var10001[6] = (byte)(-100);
            var10001[7] = (byte)(28);
            var10001[8] = (byte)(82);
            return var2.resolve($$v_s(var10001, 6555682634112159851L));
         }
      } catch (Throwable var1) {
      }

      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(56);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(74);
      var10000[5] = (byte)(-63);
      var10000[6] = (byte)(-100);
      var10000[7] = (byte)(28);
      var10000[8] = (byte)(82);
      return Path.of($$v_s(var10000, 6555682634112159851L));
   }

   public void save() {
      if (!this.loading) {
         this.saveNow();
      }
   }

   public void requestSave() {
      if (!this.loading) {
         this.dirty = true;
         this.lastDirtyAtMs = System.currentTimeMillis();
      }
   }

   public void tickAutosave() {
      if (this.dirty && !this.loading) {
         long var1 = System.currentTimeMillis();
         if (var1 - this.lastDirtyAtMs >= 1200L) {
            this.saveNow();
         }
      }
   }

   public JsonObject buildConfigJson() {
      JsonObject var1 = new JsonObject();
      JsonObject var2 = new JsonObject();
      JsonObject var3 = new JsonObject();
      if (VieClientMod.getInstance() != null && VieClientMod.getInstance().getModuleManager() != null) {
         for (Module var5 : VieClientMod.getInstance().getModuleManager().getModules()) {
            JsonObject var6 = this.serializeModule(var5);
            var2.add(var5.getName(), var6);
            var3.add(moduleConfigId(var5), var6.deepCopy());
         }
      }

      var1.add("modules", var2);
      var1.add("modulesById", var3);
      HUD var12 = HUD.instance();
      if (var12 != null) {
         var1.add("hud", HudLayoutCodec.encode(var12.getWidgetPositions(), var12.getWidgetScales()));
      }

      JsonArray var13 = new JsonArray();
      if (VieClientMod.getInstance() != null && VieClientMod.getInstance().getFriendManager() != null) {
         for (String var7 : VieClientMod.getInstance().getFriendManager().getFriends()) {
            var13.add(var7);
         }
      }

      var1.add("friends", var13);
      JsonArray var15 = new JsonArray();

      try {
         if (NameProtect.get() != null) {
            for (String var10 : NameProtect.get().getProtectedNames()) {
               var15.add(var10);
            }
         }
      } catch (Throwable var11) {
      }

      var1.add("protectedNames", var15);
      return var1;
   }

   public void saveToFile(Path targetFile) throws IOException {
      if (targetFile.getParent() != null) {
         Files.createDirectories(targetFile.getParent());
      }

      JsonObject var2 = this.buildConfigJson();
      Path var3 = targetFile.resolveSibling(targetFile.getFileName() + ".tmp");

      try (OutputStreamWriter var4 = new OutputStreamWriter(new FileOutputStream(var3.toFile()), StandardCharsets.UTF_8)) {
         GSON.toJson(var2, var4);
      }

      moveAtomicOrReplace(var3, targetFile);
   }

   public boolean loadFromFile(Path file) {
      if (file != null && Files.exists(file)) {
         this.loading = true;

         try {
            JsonObject var2 = this.readRoot(file);
            if (var2 == null) {
               return false;
            }

            this.applyRoot(var2);

            try {
               if (!file.equals(this.settingsFile)) {
                  Files.createDirectories(this.configDir);
                  Files.copy(file, this.settingsFile, StandardCopyOption.REPLACE_EXISTING);
               }
            } catch (Throwable var8) {
            }

            VieClientMod.LOGGER
               .info(
                  "[VieClient] Profile loaded from {}.",
                  file.getFileName()
               );
            return true;
         } catch (Exception var9) {
            VieClientMod.LOGGER
               .warn(
                  "[VieClient] Failed to load profile from {}: {}",
                  file.getFileName(),
                  var9.toString()
               );
            return false;
         } finally {
            this.loading = false;
            this.dirty = false;
         }
      } else {
         return false;
      }
   }

   private void saveNow() {
      try {
         Files.createDirectories(this.configDir);
         if (Files.exists(this.settingsFile)) {
            Files.copy(this.settingsFile, this.settingsBackupFile, StandardCopyOption.REPLACE_EXISTING);
         }

         this.saveToFile(this.settingsFile);
         this.dirty = false;
         VieClientMod.LOGGER
            .info(
               "[VieClient] Settings saved."
            );
      } catch (IOException var2) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] Failed to save settings!",
               var2
            );
      }
   }

   public void load() {
      List var1 = this.buildLoadCandidates();
      if (var1.isEmpty()) {
         VieClientMod.LOGGER
            .info(
               "[VieClient] No settings file found - using defaults."
            );
      } else {
         this.loading = true;

         try {
            boolean var2 = false;

            for (Path var4 : var1) {
               try {
                  JsonObject var5 = this.readRoot(var4);
                  if (var5 != null) {
                     this.applyRoot(var5);
                     VieClientMod.LOGGER
                        .info(
                           "[VieClient] Settings loaded from {}.",
                           var4.getFileName()
                        );
                     var2 = true;
                     if (!var4.equals(this.settingsFile)) {
                        try {
                           Files.createDirectories(this.configDir);
                           Files.copy(var4, this.settingsFile, StandardCopyOption.REPLACE_EXISTING);
                        } catch (Throwable var11) {
                        }
                     }
                     break;
                  }
               } catch (Exception var12) {
                  VieClientMod.LOGGER
                     .warn(
                        "[VieClient] Failed to read settings from {}: {}",
                        var4.getFileName(),
                        var12.toString()
                     );
               }
            }

            if (!var2) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Could not load any settings file - keeping defaults."
                  );
            }
         } finally {
            this.loading = false;
            this.dirty = false;
         }
      }
   }

   public Path getConfigDir() {
      return this.configDir;
   }

   public Path getSettingsFile() {
      return this.settingsFile;
   }

   private JsonObject serializeModule(Module module) {
      JsonObject var2 = new JsonObject();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(32);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-69);
      var2.addProperty($$v_s(var10001, 6555682634112159851L), module.getName());
      var10001 = new byte[8];
      var10001[0] = (byte)(45);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(65);
      var10001[5] = (byte)(-57);
      var10001[6] = (byte)(-117);
      var10001[7] = (byte)(11);
      var2.addProperty($$v_s(var10001, 6555682634112159851L), module.getCategory().name());
      var10001 = new byte[7];
      var10001[0] = (byte)(43);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(74);
      var10001[5] = (byte)(-51);
      var10001[6] = (byte)(-99);
      var2.addProperty($$v_s(var10001, 6555682634112159851L), module.isEnabled());
      var10001 = new byte[7];
      var10001[0] = (byte)(37);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(101);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(79);
      var10001[5] = (byte)(-58);
      var10001[6] = (byte)(-99);
      var2.add($$v_s(var10001, 6555682634112159851L), module.getKeybind().toJson());
      JsonObject var3 = new JsonObject();

      for (Setting var5 : module.getSettings()) {
         var3.add(var5.getName(), var5.toJson());
      }

      var10001 = new byte[8];
      var10001[0] = (byte)(61);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(79);
      var10001[5] = (byte)(-58);
      var10001[6] = (byte)(-98);
      var10001[7] = 1;
      var2.add($$v_s(var10001, 6555682634112159851L), var3);
      return var2;
   }

   List buildLoadCandidates() {
      ArrayList var1 = new ArrayList();
      Path var2 = this.resolveDefaultProfilePath();
      if (var2 != null && Files.exists(var2)) {
         var1.add(var2);
      }

      if (Files.exists(this.settingsFile)) {
         var1.add(this.settingsFile);
      }

      if (Files.exists(this.settingsBackupFile)) {
         var1.add(this.settingsBackupFile);
      }

      if (Files.exists(this.legacyConfigFile)) {
         var1.add(this.legacyConfigFile);
      }

      if (this.configDir != null && this.configDir.getParent() != null) {
         Path var10000 = this.configDir.getParent();
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(56);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(78);
         var10001[5] = (byte)(-51);
         var10001[6] = (byte)(-104);
         var10001[7] = (byte)(6);
         var10001[8] = (byte)(85);
         var10000 = var10000.resolve($$v_s(var10001, 6555682634112159851L));
         var10001 = new byte[13];
         var10001[0] = (byte)(61);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(79);
         var10001[5] = (byte)(-58);
         var10001[6] = (byte)(-98);
         var10001[7] = 1;
         var10001[8] = (byte)(8);
         var10001[9] = (byte)(-56);
         var10001[10] = (byte)(-105);
         var10001[11] = (byte)(-103);
         var10001[12] = (byte)(16);
         Path var3 = var10000.resolve($$v_s(var10001, 6555682634112159851L));
         if (Files.exists(var3)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   Path resolveDefaultProfilePath() {
      ArrayList var1 = new ArrayList();

      try {
         if (class_310.method_1551() != null && class_310.method_1551().field_1697 != null) {
            var1.add(
               class_310.method_1551()
                  .field_1697
                  .toPath()
                  .resolve("vieclient")
                  .resolve("configs")
                  .resolve(".default")
            );
            var1.add(
               class_310.method_1551()
                  .field_1697
                  .toPath()
                  .resolve("Vie Client")
                  .resolve("configs")
                  .resolve(".default")
            );
            var1.add(
               class_310.method_1551()
                  .field_1697
                  .toPath()
                  .resolve("DonutBBC")
                  .resolve("configs")
                  .resolve(".default")
            );
         }
      } catch (Throwable var8) {
      }

      var1.add(
         Path.of(
            "vieclient",
            "configs",
            ".default"
         )
      );
      var1.add(
         Path.of(
            "Vie Client",
            "configs",
            ".default"
         )
      );
      var1.add(
         Path.of(
            "DonutBBC",
            "configs",
            ".default"
         )
      );
      if (this.configDir != null && this.configDir.getParent() != null) {
         var1.add(
            this.configDir
               .getParent()
               .resolve("vieclient")
               .resolve("configs")
               .resolve(".default")
         );
         var1.add(
            this.configDir
               .getParent()
               .resolve("Vie Client")
               .resolve("configs")
               .resolve(".default")
         );
         var1.add(
            this.configDir
               .getParent()
               .resolve("DonutBBC")
               .resolve("configs")
               .resolve(".default")
         );
      }

      for (Path var3 : var1) {
         if (Files.exists(var3)) {
            try {
               String var4 = Files.readString(var3, StandardCharsets.UTF_8).trim();
               if (!var4.isEmpty()) {
                  String var5 = var4.endsWith(".json") ? var4 : var4 + ".json";
                  Path var6 = var3.getParent().resolve(var5);
                  if (Files.exists(var6)) {
                     return var6;
                  }
               }
            } catch (Throwable var7) {
            }
         }
      }

      return null;
   }

   private JsonObject readRoot(Path file) throws IOException {
      try (InputStreamReader var2 = new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8)) {
         return (JsonObject)GSON.fromJson(var2, JsonObject.class);
      }
   }

   private void applyRoot(JsonObject root) {
      if (root != null) {
         JsonObject var2 = root.has("modules")
               && root.get("modules").isJsonObject()
            ? root.getAsJsonObject("modules")
            : null;
         JsonObject var3 = root.has("modulesById")
               && root.get("modulesById").isJsonObject()
            ? root.getAsJsonObject("modulesById")
            : null;
         if (var2 != null || var3 != null) {
            for (Module var5 : VieClientMod.getInstance().getModuleManager().getModules()) {
               try {
                  JsonObject var6 = this.resolveModuleObject(var5, var2, var3);
                  if (var6 != null) {
                     JsonElement var7 = var6.get("settings");
                     JsonObject var8 = var7 != null && var7.isJsonObject() ? var7.getAsJsonObject() : null;
                     if (var6.has("enabled")
                        && var6.get("enabled").isJsonPrimitive()) {
                        boolean var9 = var6.get("enabled").getAsBoolean();
                        var5.setEnabled(ModuleStateMigration.resolveEnabled(var5.getClass(), var9, var8));
                     }

                     if (var6.has("keybind")
                        && var6.get("keybind") != null) {
                        var5.getKeybind().fromJson(var6.get("keybind"));
                     }

                     if (var8 != null) {
                        for (Setting var10 : var5.getSettings()) {
                           try {
                              JsonElement var11 = this.resolveSettingElement(var10, var8);
                              if (var11 == null && var5 instanceof ClientSettings && var2 != null) {
                                 JsonObject var12 = var2.getAsJsonObject("VieClient");
                                 if (var12 != null
                                    && var12.has("settings")
                                    && var12.get("settings").isJsonObject()) {
                                    var11 = this.resolveSettingElement(
                                       var10, var12.getAsJsonObject("settings")
                                    );
                                 }

                                 if (var11 == null) {
                                    JsonObject var13 = var2.getAsJsonObject("Vie");
                                    if (var13 != null
                                       && var13.has("settings")
                                       && var13.get("settings").isJsonObject()) {
                                       var11 = this.resolveSettingElement(
                                          var10, var13.getAsJsonObject("settings")
                                       );
                                    }
                                 }

                                 if (var11 == null) {
                                    JsonObject var30 = var2.getAsJsonObject("DonutBBC");
                                    if (var30 != null
                                       && var30.has("settings")
                                       && var30.get("settings").isJsonObject()) {
                                       var11 = this.resolveSettingElement(
                                          var10, var30.getAsJsonObject("settings")
                                       );
                                    }
                                 }
                              }

                              if (var11 != null) {
                                 var10.fromJson(var11);
                              }
                           } catch (Throwable var15) {
                              VieClientMod.LOGGER
                                 .warn(
                                    "[VieClient] Failed to apply setting '{}' on module {}",
                                    new Object[]{var10.getName(), var5.getName(), var15}
                                 );
                           }
                        }
                     }
                  }
               } catch (Throwable var18) {
                  VieClientMod.LOGGER
                     .warn(
                        "[VieClient] Failed to apply config entry for module {}",
                        var5.getName(),
                        var18
                     );
               }
            }
         }

         JsonObject var19 = null;
         if (root.has("hud")
            && root.get("hud") != null
            && root.get("hud").isJsonObject()) {
            var19 = root.getAsJsonObject("hud");
         }

         if (var19 != null) {
            HUD var20 = HUD.instance();
            if (var20 != null) {
               try {
                  HudLayoutCodec.Layout var23 = HudLayoutCodec.decode(var19);
                  var20.setWidgetPositions(var23.positions());
                  var20.setWidgetScales(var23.scales());
               } catch (Throwable var14) {
                  VieClientMod.LOGGER
                     .warn(
                        "[VieClient] Failed to decode HUD layout",
                        var14
                     );
               }
            }
         }

         if (root.has("friends")
            && root.get("friends").isJsonArray()) {
            JsonArray var21 = root.getAsJsonArray("friends");

            try {
               for (JsonElement var26 : var21) {
                  if (var26 != null && var26.isJsonPrimitive()) {
                     VieClientMod.getInstance().getFriendManager().add(var26.getAsString());
                  }
               }
            } catch (Throwable var17) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Failed to load friends list",
                     var17
                  );
            }
         }

         if (root.has("protectedNames")
            && root.get("protectedNames").isJsonArray()) {
            JsonArray var22 = root.getAsJsonArray("protectedNames");

            try {
               String[] var25 = new String[var22.size()];

               for (int var27 = 0; var27 < var22.size(); var27++) {
                  JsonElement var28 = var22.get(var27);
                  var25[var27] = var28 != null && var28.isJsonPrimitive() ? var28.getAsString() : null;
               }

               NameProtect.get().setProtectedNames(var25);
            } catch (Throwable var16) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Failed to load protected names",
                     var16
                  );
            }
         }
      }
   }

   private JsonObject resolveModuleObject(Module module, JsonObject modulesByName, JsonObject modulesById) {
      if (modulesByName != null) {
         JsonObject var4 = modulesByName.getAsJsonObject(module.getName());
         if (var4 != null) {
            return var4;
         }

         if (module instanceof MusicHUD) {
            byte[] var10001 = new byte[11];
            var10001[0] = (byte)('멵' ^ '멨');
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(79);
            var10001[5] = (byte)(-50);
            var10001[6] = (byte)(-128);
            var10001[7] = (byte)(82);
            var10001[8] = (byte)(110);
            var10001[9] = (byte)(-9);
            var10001[10] = (byte)(-96);
            JsonObject var5 = modulesByName.getAsJsonObject($$v_s(var10001, 6555682634112159851L));
            if (var5 != null) {
               return var5;
            }
         }

         if (module instanceof ClientSettings) {
            byte[] var14 = new byte[9];
            var14[0] = (byte)(24);
            var14[1] = (byte)(-13);
            var14[2] = (byte)(121);
            var14[3] = (byte)(-99);
            var14[4] = (byte)(74);
            var14[5] = (byte)(-63);
            var14[6] = (byte)(-100);
            var14[7] = (byte)(28);
            var14[8] = (byte)(82);
            JsonObject var10 = modulesByName.getAsJsonObject($$v_s(var14, 6555682634112159851L));
            if (var10 != null) {
               return var10;
            }

            var14 = new byte[3];
            var14[0] = (byte)(24);
            var14[1] = (byte)(-13);
            var14[2] = (byte)(121);
            JsonObject var6 = modulesByName.getAsJsonObject($$v_s(var14, 6555682634112159851L));
            if (var6 != null) {
               return var6;
            }

            var14 = new byte[8];
            var14[0] = (byte)(10);
            var14[1] = (byte)(-11);
            var14[2] = (byte)(114);
            var14[3] = (byte)(-85);
            var14[4] = (byte)(82);
            var14[5] = (byte)(-22);
            var14[6] = (byte)(-69);
            var14[7] = (byte)(49);
            JsonObject var7 = modulesByName.getAsJsonObject($$v_s(var14, 6555682634112159851L));
            if (var7 != null) {
               return var7;
            }
         }
      }

      if (modulesById != null) {
         JsonObject var8 = modulesById.getAsJsonObject(moduleConfigId(module));
         if (var8 != null) {
            return var8;
         }
      }

      if (modulesByName != null) {
         String var9 = normalizeKey(module.getName());

         for (String var12 : modulesByName.keySet()) {
            if (normalizeKey(var12).equals(var9)) {
               JsonObject var13 = modulesByName.getAsJsonObject(var12);
               if (var13 != null) {
                  return var13;
               }
            }
         }
      }

      return null;
   }

   JsonElement resolveSettingElement(Setting setting, JsonObject settingsObj) {
      if (settingsObj.has(setting.getName())) {
         return settingsObj.get(setting.getName());
      }

      String var3 = normalizeKey(setting.getName());

      for (String var5 : settingsObj.keySet()) {
         if (normalizeKey(var5).equals(var3)) {
            return settingsObj.get(var5);
         }
      }

      if (setting.getName().equalsIgnoreCase("GuiStyle")) {
         for (String var14 : settingsObj.keySet()) {
            String var17 = normalizeKey(var14);
            if (var17.equals("style")
               || var17.equals("guistyle")
               || var17.equals("uistyle")
               || var17.equals("stylemode")) {
               return settingsObj.get(var14);
            }
         }
      } else if (setting.getName().equalsIgnoreCase("ClickGuiMode")) {
         for (String var13 : settingsObj.keySet()) {
            String var16 = normalizeKey(var13);
            if (var16.equals("clickguimode")
               || var16.equals("clickgui")
               || var16.equals("clickguilayout")
               || var16.equals("guimode")
               || var16.equals("mode")) {
               return settingsObj.get(var13);
            }
         }
      } else if (setting.getName().equalsIgnoreCase("Modules")) {
         for (String var12 : settingsObj.keySet()) {
            String var15 = normalizeKey(var12);
            if (var15.equals("modules")
               || var15.equals("modulesmanager")
               || var15.equals("modulemanager")
               || var15.equals("showmodules")) {
               return settingsObj.get(var12);
            }
         }
      } else if (setting.getName().equalsIgnoreCase("Notifier")) {
         for (String var11 : settingsObj.keySet()) {
            String var6 = normalizeKey(var11);
            if (var6.equals("notifier")
               || var6.equals("notify")
               || var6.equals("staffnotifier")) {
               return settingsObj.get(var11);
            }
         }
      }

      return null;
   }

   private static String moduleConfigId(Module module) {
      return module.getClass().getName();
   }

   private static String normalizeKey(String value) {
      if (value == null) {
         return "";
      }

      String var1 = value.toLowerCase(Locale.ROOT);
      StringBuilder var2 = new StringBuilder(var1.length());

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var4 >= (97) && var4 <= (122) || var4 >= (48) && var4 <= (57)) {
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   private static void moveAtomicOrReplace(Path from, Path to) throws IOException {
      try {
         CopyOption[] var10002 = new CopyOption[2];
         var10002[0] = StandardCopyOption.REPLACE_EXISTING;
         var10002[1] = StandardCopyOption.ATOMIC_MOVE;
         Files.move(from, to, var10002);
      } catch (Exception var3) {
         Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
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
