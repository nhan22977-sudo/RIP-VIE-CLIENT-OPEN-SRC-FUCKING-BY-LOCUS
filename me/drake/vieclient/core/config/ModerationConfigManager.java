package me.drake.vieclient.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.Setting;
import net.minecraft.class_310;

public final class ModerationConfigManager {
   private static final int MAX_SLOTS = 6;
   private static final int CODE_LENGTH = 6;
   private static final String OWN_CODE = "OWNED";
   private static final String CONFIG_FILE = "configs.json";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final SecureRandom RNG = new SecureRandom();
   private final Map slots = new ConcurrentHashMap();
   private final Path configFile;
   private boolean dirty = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ModerationConfigManager() {
      Path var10000 = class_310.method_1551().field_1697.toPath();
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(83);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(52);
      var10001[5] = (byte)(63);
      var10001[6] = (byte)(104);
      var10001[7] = (byte)(-107);
      var10001[8] = (byte)(57);
      var10000 = var10000.resolve($$v_s(var10001, -3238500580834684928L));
      var10001 = new byte[12];
      var10001[0] = (byte)(70);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-72);
      var10001[4] = (byte)(49);
      var10001[5] = (byte)(49);
      var10001[6] = (byte)(126);
      var10001[7] = (byte)(-43);
      var10001[8] = (byte)(39);
      var10001[9] = (byte)(-47);
      var10001[10] = (byte)(-117);
      var10001[11] = (byte)(-104);
      Path var1 = var10000.resolve($$v_s(var10001, -3238500580834684928L));
      var10000 = class_310.method_1551().field_1697.toPath();
      var10001 = new byte[9];
      var10001[0] = (byte)(83);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(48);
      var10001[5] = (byte)(51);
      var10001[6] = (byte)(108);
      var10001[7] = (byte)(-113);
      var10001[8] = (byte)(62);
      var10000 = var10000.resolve($$v_s(var10001, -3238500580834684928L));
      var10001 = new byte[12];
      var10001[0] = (byte)(70);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-72);
      var10001[4] = (byte)(49);
      var10001[5] = (byte)(49);
      var10001[6] = (byte)(126);
      var10001[7] = (byte)(-43);
      var10001[8] = (byte)(39);
      var10001[9] = (byte)(-47);
      var10001[10] = (byte)(-117);
      var10001[11] = (byte)(-104);
      Path var2 = var10000.resolve($$v_s(var10001, -3238500580834684928L));
      if (!Files.exists(var1) && Files.exists(var2)) {
         try {
            Files.createDirectories(var1.getParent());
            Files.copy(var2, var1, StandardCopyOption.REPLACE_EXISTING);
         } catch (Throwable var4) {
         }
      }

      this.configFile = var1;
      this.loadAll();
   }

   public List getSlots() {
      return this.slots.values().stream().sorted((a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt())).toList();
   }

   public ConfigSlot getOwnSlot() {
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(106);
      var10001[1] = (byte)(-51);
      var10001[2] = (byte)(82);
      var10001[3] = (byte)(-101);
      var10001[4] = (byte)(28);
      return (ConfigSlot)this.slots.get($$v_s(var10001, -3238500580834684928L));
   }

   public String saveCurrentAs(String code, String name, boolean markAsOwn) {
      String var4 = sanitizeCode(code);
      if (var4 == null || var4.isEmpty()) {
         var4 = this.generateUniqueCode();
      }

      if (this.slots.size() >= ('鬕' ^ '鬓') && !this.slots.containsKey(var4)) {
         this.evictOldestNonOwn();
      }

      JsonObject var5 = this.serializeCurrentState();
      ConfigSlot var6 = new ConfigSlot(var4, name, var5, System.currentTimeMillis(), markAsOwn);
      this.slots.put(var4, var6);
      this.dirty = true;
      this.saveAll();
      return var4;
   }

   public void saveOwn(String name) {
      JsonObject var2 = this.serializeCurrentState();
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(106);
      var10001[1] = (byte)(-51);
      var10001[2] = (byte)(82);
      var10001[3] = (byte)(-101);
      var10001[4] = (byte)(28);
      ConfigSlot var3 = (ConfigSlot)this.slots.get($$v_s(var10001, -3238500580834684928L));
      if (var3 != null) {
         var3.setName(name);
         var3.setData(var2);
         var3.setUpdatedAt(System.currentTimeMillis());
         var3.setOwn(true);
      } else {
         Map var10000 = this.slots;
         var10001 = new byte[5];
         var10001[0] = (byte)('뼐' ^ '뽺');
         var10001[1] = (byte)(-51);
         var10001[2] = (byte)(82);
         var10001[3] = (byte)(-101);
         var10001[4] = (byte)(28);
         String var5 = $$v_s(var10001, -3238500580834684928L);
         byte[] var10004 = new byte[5];
         var10004[0] = (byte)(106);
         var10004[1] = (byte)(-51);
         var10004[2] = (byte)(82);
         var10004[3] = (byte)(-101);
         var10004[4] = (byte)(28);
         var10000.put(var5, new ConfigSlot($$v_s(var10004, -3238500580834684928L), name, var2, System.currentTimeMillis(), true));
      }

      this.dirty = true;
      this.saveAll();
   }

   public boolean applySlot(String code) {
      ConfigSlot var2 = (ConfigSlot)this.slots.get(code);
      if (var2 == null) {
         return false;
      }

      this.deserializeIntoModules(var2.getData());
      return true;
   }

   public boolean deleteSlot(String code) {
      byte[] var10000 = new byte[5];
      var10000[0] = (byte)(106);
      var10000[1] = (byte)(-51);
      var10000[2] = (byte)(82);
      var10000[3] = (byte)(-101);
      var10000[4] = (byte)(28);
      if ($$v_s(var10000, -3238500580834684928L).equals(code)) {
         return false;
      } else {
         ConfigSlot var2 = (ConfigSlot)this.slots.remove(code);
         if (var2 != null) {
            this.dirty = true;
            this.saveAll();
            return true;
         } else {
            return false;
         }
      }
   }

   public int slotCount() {
      return this.slots.size();
   }

   public boolean canAddSlot() {
      return this.slots.size() < (6);
   }

   public String generateUniqueCode() {
      for (int var1 = 0; var1 < (100); var1++) {
         String var2 = generateCode();
         if (!this.slots.containsKey(var2)) {
            return var2;
         }
      }

      return generateCode() + System.currentTimeMillis() % 10L;
   }

   private void loadAll() {
      if (Files.exists(this.configFile)) {
         try (InputStreamReader var1 = new InputStreamReader(Files.newInputStream(this.configFile), StandardCharsets.UTF_8)) {
            JsonObject var2 = (JsonObject)GSON.fromJson(var1, JsonObject.class);
            if (var2 != null && var2.has("slots")) {
               for (JsonElement var5 : var2.getAsJsonArray("slots")) {
                  if (var5.isJsonObject()) {
                     ConfigSlot var6 = ConfigSlot.fromJson(var5.getAsJsonObject());
                     if (!var6.getCode().isEmpty()) {
                        this.slots.put(var6.getCode(), var6);
                     }
                  }
               }
            }
         } catch (IOException var9) {
            VieClientMod.LOGGER
               .warn(
                  "[VieClient] Failed to load configs: {}",
                  var9.toString()
               );
         }
      }
   }

   private void saveAll() {
      if (this.dirty) {
         try {
            Files.createDirectories(this.configFile.getParent());
            JsonObject var1 = new JsonObject();
            JsonArray var2 = new JsonArray();

            for (ConfigSlot var4 : this.slots.values()) {
               var2.add(var4.toJson());
            }

            var1.add("slots", var2);
            Path var10 = this.configFile.resolveSibling(this.configFile.getFileName() + ".tmp");

            try (OutputStreamWriter var11 = new OutputStreamWriter(Files.newOutputStream(var10), StandardCharsets.UTF_8)) {
               GSON.toJson(var1, var11);
            }

            Files.move(var10, this.configFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            this.dirty = false;
         } catch (IOException var9) {
            VieClientMod.LOGGER
               .warn(
                  "[VieClient] Failed to save configs: {}",
                  var9.toString()
               );
         }
      }
   }

   private void evictOldestNonOwn() {
      this.slots
         .values()
         .stream()
         .filter(s -> !s.isOwn())
         .min((a, b) -> Long.compare(a.getUpdatedAt(), b.getUpdatedAt()))
         .ifPresent(s -> this.slots.remove(s.getCode()));
   }

   private JsonObject serializeCurrentState() {
      JsonObject var1 = new JsonObject();
      JsonObject var2 = new JsonObject();

      for (Module var4 : VieClientMod.getInstance().getModuleManager().getModules()) {
         var2.add(var4.getName(), this.serializeModule(var4));
      }

      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(72);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-85);
      var10001[4] = (byte)(52);
      var10001[5] = (byte)(51);
      var10001[6] = (byte)(126);
      var1.add($$v_s(var10001, -3238500580834684928L), var2);
      return var1;
   }

   private JsonObject serializeModule(Module module) {
      JsonObject var2 = new JsonObject();
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(52);
      var10001[5] = (byte)(51);
      var10001[6] = (byte)(105);
      var2.addProperty($$v_s(var10001, -3238500580834684928L), module.isEnabled());
      var10001 = new byte[7];
      var10001[0] = (byte)(78);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(101);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(49);
      var10001[5] = (byte)(56);
      var10001[6] = (byte)(105);
      var2.add($$v_s(var10001, -3238500580834684928L), module.getKeybind().toJson());
      JsonObject var3 = new JsonObject();

      for (Setting var5 : module.getSettings()) {
         var3.add(var5.getName(), var5.toJson());
      }

      var10001 = new byte[8];
      var10001[0] = (byte)(86);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(49);
      var10001[5] = (byte)(56);
      var10001[6] = (byte)(106);
      var10001[7] = (byte)(-120);
      var2.add($$v_s(var10001, -3238500580834684928L), var3);
      return var2;
   }

   private void deserializeIntoModules(JsonObject data) {
      if (data != null && data.has("modules")) {
         JsonObject var2 = data.getAsJsonObject("modules");

         for (Module var4 : VieClientMod.getInstance().getModuleManager().getModules()) {
            JsonObject var5 = this.resolveMod(var2, var4.getName());
            if (var5 != null) {
               JsonElement var6 = var5.get("settings");
               JsonObject var7 = var6 != null && var6.isJsonObject() ? var6.getAsJsonObject() : null;
               if (var5.has("enabled")) {
                  boolean var8 = var5.get("enabled").getAsBoolean();
                  boolean var9 = ModuleStateMigration.resolveEnabled(var4.getClass(), var8, var7);
                  if (var9 != var4.isEnabled()) {
                     var4.setEnabled(var9);
                  }
               }

               if (var5.has("keybind")) {
                  var4.getKeybind().fromJson(var5.get("keybind"));
               }

               if (var7 != null) {
                  for (Setting var13 : var4.getSettings()) {
                     try {
                        JsonElement var10 = this.resolveSetting(var7, var13.getName());
                        if (var10 != null) {
                           var13.fromJson(var10);
                        }
                     } catch (Throwable var11) {
                     }
                  }
               }
            }
         }
      }
   }

   private JsonObject resolveMod(JsonObject root, String name) {
      if (root.has(name)) {
         return root.getAsJsonObject(name);
      }

      byte[] var10000 = new byte[8];
      var10000[0] = (byte)(104);
      var10000[1] = (byte)(-17);
      var10000[2] = (byte)(111);
      var10000[3] = (byte)(-73);
      var10000[4] = (byte)(59);
      var10000[5] = (byte)(30);
      var10000[6] = (byte)(88);
      var10000[7] = (byte)(-65);
      if ($$v_s(var10000, -3238500580834684928L).equals(name)) {
         byte[] var10001 = new byte[11];
         var10001[0] = (byte)(118);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(49);
         var10001[5] = (byte)(48);
         var10001[6] = (byte)(116);
         var10001[7] = (byte)(-37);
         var10001[8] = (byte)(5);
         var10001[9] = (byte)(-9);
         var10001[10] = (byte)(-96);
         if (root.has($$v_s(var10001, -3238500580834684928L))) {
            var10001 = new byte[11];
            var10001[0] = (byte)(118);
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(49);
            var10001[5] = (byte)(48);
            var10001[6] = (byte)(116);
            var10001[7] = (byte)(-37);
            var10001[8] = (byte)(5);
            var10001[9] = (byte)(-9);
            var10001[10] = (byte)(-96);
            return root.getAsJsonObject($$v_s(var10001, -3238500580834684928L));
         }
      }

      String var3 = normalize(name);

      for (String var5 : root.keySet()) {
         if (normalize(var5).equals(var3)) {
            return root.getAsJsonObject(var5);
         }
      }

      return null;
   }

   private JsonElement resolveSetting(JsonObject obj, String name) {
      if (obj.has(name)) {
         return obj.get(name);
      }

      String var3 = normalize(name);

      for (String var5 : obj.keySet()) {
         if (normalize(var5).equals(var3)) {
            return obj.get(var5);
         }
      }

      return null;
   }

   private static String normalize(String v) {
      if (v == null) {
         return "";
      }

      String var1 = v.toLowerCase(Locale.ROOT);
      StringBuilder var2 = new StringBuilder(var1.length());

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var4 >= (97) && var4 <= (122) || var4 >= (48) && var4 <= (57)) {
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   private static String generateCode() {
      byte[] var10000 = new byte[32];
      var10000[0] = (byte)(100);
      var10000[1] = (byte)(-40);
      var10000[2] = (byte)(95);
      var10000[3] = (byte)(-102);
      var10000[4] = (byte)(29);
      var10000[5] = (byte)(16);
      var10000[6] = (byte)(74);
      var10000[7] = (byte)(-77);
      var10000[8] = (byte)(7);
      var10000[9] = (byte)(-23);
      var10000[10] = (byte)(-88);
      var10000[11] = (byte)(-69);
      var10000[12] = (byte)(78);
      var10000[13] = (byte)(-34);
      var10000[14] = (byte)(116);
      var10000[15] = (byte)(-47);
      var10000[16] = (byte)(38);
      var10000[17] = (byte)(30);
      var10000[18] = (byte)(-103);
      var10000[19] = (byte)(-8);
      var10000[20] = (byte)(-65);
      var10000[21] = (byte)(-2);
      var10000[22] = (byte)(4);
      var10000[23] = (byte)(-15);
      var10000[24] = (byte)(-81);
      var10000[25] = (byte)(33);
      var10000[26] = (byte)(-96);
      var10000[27] = (byte)(115);
      var10000[28] = (byte)(-26);
      var10000[29] = (byte)(-23);
      var10000[30] = (byte)(77);
      var10000[31] = (byte)(74);
      String var0 = $$v_s(var10000, -3238500580834684928L);
      StringBuilder var1 = new StringBuilder(6);

      for (int var2 = 0; var2 < (6); var2++) {
         var1.append(var0.charAt(RNG.nextInt(var0.length())));
      }

      return var1.toString();
   }

   private static String sanitizeCode(String input) {
      if (input == null) {
         return null;
      }

      String var10000 = input.trim().toUpperCase(Locale.ROOT);
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(126);
      var10001[1] = (byte)(-60);
      var10001[2] = (byte)(93);
      var10001[3] = (byte)(-13);
      var10001[4] = (byte)(2);
      var10001[5] = (byte)(102);
      var10001[6] = (byte)(32);
      var10001[7] = (byte)(-62);
      var10001[8] = (byte)(16);
      String var1 = var10000.replaceAll($$v_s(var10001, -3238500580834684928L), "");
      return !var1.isEmpty() && var1.length() <= (6) ? var1 : null;
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
