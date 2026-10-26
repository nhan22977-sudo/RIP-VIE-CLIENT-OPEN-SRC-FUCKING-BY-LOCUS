package me.drake.vieclient.core.macro;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.class_310;

public final class ChatMacroSystem {
   public static final ChatMacroSystem INSTANCE = new ChatMacroSystem();
   private static final Gson GSON = new Gson();
   private static final int MAX_MACROS = 64;
   private final List macros = new CopyOnWriteArrayList();
   private final List addListeners = new ArrayList();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ChatMacroSystem() {
      this.load();
   }

   public void onKey(int key) {
      if (key > 0) {
         for (ChatMacroSystem.Macro var3 : this.macros) {
            if (var3.keybind() == key) {
               sendChat(var3.command(), false);
            }
         }
      }
   }

   public List macros() {
      return Collections.unmodifiableList(this.macros);
   }

   public synchronized void add(String command, int keybind) {
      if (command != null && !command.isBlank()) {
         if (this.macros.size() < (64)) {
            this.macros.add(new ChatMacroSystem.Macro(command.trim(), keybind));
            this.save();
         }
      }
   }

   public synchronized void remove(int index) {
      if (index >= 0 && index < this.macros.size()) {
         this.macros.remove(index);
         this.save();
      }
   }

   public synchronized void updateKeybind(int index, int keybind) {
      if (index >= 0 && index < this.macros.size()) {
         ChatMacroSystem.Macro var3 = (ChatMacroSystem.Macro)this.macros.get(index);
         this.macros.set(index, new ChatMacroSystem.Macro(var3.command(), keybind));
         this.save();
      }
   }

   public synchronized void updateCommand(int index, String command) {
      if (index >= 0 && index < this.macros.size()) {
         if (command != null && !command.isBlank()) {
            ChatMacroSystem.Macro var3 = (ChatMacroSystem.Macro)this.macros.get(index);
            this.macros.set(index, new ChatMacroSystem.Macro(command.trim(), var3.keybind()));
            this.save();
         }
      }
   }

   public static void sendChat(String command, boolean addPrefixSlash) {
      class_310 var2 = class_310.method_1551();
      if (var2 != null && var2.field_1724 != null) {
         String var3 = command == null ? "" : command.trim();
         if (!var3.isEmpty()) {
            if (addPrefixSlash && !var3.startsWith("/")) {
               var3 = "/" + var3;
            }

            if (var3.startsWith("/")) {
               var2.field_1724.field_3944.method_45730(var3.substring(1));
            } else {
               var2.field_1724.field_3944.method_45729(var3);
            }
         }
      }
   }

   private Path resolveConfigFile() {
      Path var10000 = class_310.method_1551().field_1697.toPath();
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(-14);
      var10001[5] = (byte)(-31);
      var10001[6] = (byte)(-52);
      var10001[7] = (byte)(30);
      var10001[8] = (byte)(42);
      var10000 = var10000.resolve($$v_s(var10001, 6389013748131287059L));
      var10001 = new byte[15];
      var10001[0] = (byte)(85);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-13);
      var10001[5] = (byte)(-23);
      var10001[6] = (byte)(-54);
      var10001[7] = (byte)(2);
      var10001[8] = (byte)(49);
      var10001[9] = (byte)(-47);
      var10001[10] = (byte)(-54);
      var10001[11] = (byte)(-100);
      var10001[12] = (byte)(-75);
      var10001[13] = (byte)(63);
      var10001[14] = (byte)(-17);
      return var10000.resolve($$v_s(var10001, 6389013748131287059L));
   }

   private Path resolveLegacyConfigFile() {
      Path var10000 = class_310.method_1551().field_1697.toPath();
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(-10);
      var10001[5] = (byte)(-19);
      var10001[6] = (byte)(-56);
      var10001[7] = (byte)(4);
      var10001[8] = (byte)(45);
      var10000 = var10000.resolve($$v_s(var10001, 6389013748131287059L));
      var10001 = new byte[15];
      var10001[0] = (byte)(85);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-13);
      var10001[5] = (byte)(-23);
      var10001[6] = (byte)(-54);
      var10001[7] = (byte)(2);
      var10001[8] = (byte)(49);
      var10001[9] = (byte)(-47);
      var10001[10] = (byte)(-54);
      var10001[11] = (byte)(-100);
      var10001[12] = (byte)(-75);
      var10001[13] = (byte)(63);
      var10001[14] = (byte)(-17);
      return var10000.resolve($$v_s(var10001, 6389013748131287059L));
   }

   public synchronized void load() {
      Path var1 = this.resolveConfigFile();
      if (!Files.exists(var1)) {
         Path var2 = this.resolveLegacyConfigFile();
         if (!Files.exists(var2)) {
            return;
         }

         var1 = var2;
      }

      try {
         byte[] var12 = Files.readAllBytes(var1);
         String var3 = new String(var12, StandardCharsets.UTF_8);
         JsonObject var4 = (JsonObject)GSON.fromJson(var3, JsonObject.class);
         if (var4 == null || !var4.has("macros")) {
            return;
         }

         JsonArray var5 = var4.getAsJsonArray("macros");
         this.macros.clear();

         for (JsonElement var7 : var5) {
            JsonObject var8 = var7.getAsJsonObject();
            String var9 = var8.has("command")
               ? var8.get("command").getAsString()
               : "";
            int var10 = var8.has("keybind")
               ? var8.get("keybind").getAsInt()
               : -1;
            if (!var9.isBlank()) {
               this.macros.add(new ChatMacroSystem.Macro(var9.trim(), var10));
            }
         }
      } catch (Exception var11) {
      }
   }

   public synchronized void save() {
      Path var1 = this.resolveConfigFile();

      try {
         Files.createDirectories(var1.getParent());
         JsonObject var2 = new JsonObject();
         JsonArray var3 = new JsonArray();

         for (ChatMacroSystem.Macro var5 : this.macros) {
            JsonObject var6 = new JsonObject();
            byte[] var10001 = new byte[7];
            var10001[0] = (byte)(85);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(113);
            var10001[3] = (byte)(-77);
            var10001[4] = (byte)(-1);
            var10001[5] = (byte)(-26);
            var10001[6] = (byte)(-51);
            var6.add($$v_s(var10001, 6389013748131287059L), new JsonPrimitive(var5.command()));
            var10001 = new byte[7];
            var10001[0] = (byte)(93);
            var10001[1] = (byte)(-1);
            var10001[2] = (byte)(101);
            var10001[3] = (byte)(-68);
            var10001[4] = (byte)(-9);
            var10001[5] = (byte)(-26);
            var10001[6] = (byte)(-51);
            var6.add($$v_s(var10001, 6389013748131287059L), new JsonPrimitive(var5.keybind()));
            var3.add(var6);
         }

         byte[] var9 = new byte[6];
         var9[0] = (byte)(91);
         var9[1] = (byte)(-5);
         var9[2] = (byte)(127);
         var9[3] = (byte)(-84);
         var9[4] = (byte)(-15);
         var9[5] = (byte)(-5);
         var2.add($$v_s(var9, 6389013748131287059L), var3);
         Files.writeString(var1, GSON.toJson(var2), StandardCharsets.UTF_8);
      } catch (IOException var7) {
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

   public record Macro(String command, int keybind) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
