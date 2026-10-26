package me.drake.vieclient.modules.player;

import com.google.common.collect.LinkedHashMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_310;
import net.minecraft.class_8685;

public class SkinProtect extends Module {
   private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8L)).build();
   public final StringSetting ign = (StringSetting)this.addSetting(
      new StringSetting(
         "Skin IGN",
         "Username whose skin is applied",
         "67skibidi"
      )
   );
   public final EnumSetting applyTo = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Apply To",
         "Whose skin gets replaced",
         SkinProtect.ApplyTo.SELF
      )
   );
   private volatile class_8685 replacement;
   private volatile String fetchedFor = "";
   private volatile boolean fetching;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SkinProtect() {
      super(
         "SkinProtect",
         "Replaces skins so clips can't dox skins",
         Category.PROTECT
      );
   }

   @Override
   public void onEnable() {
      if (this.replacement == null) {
         this.fetchedFor = "";
      }

      this.ensureFetched();
   }

   @Override
   public void onTick() {
      this.ensureFetched();
   }

   public class_8685 replacementSkin() {
      return this.replacement;
   }

   public boolean shouldReplace(UUID uuid) {
      if (uuid == null) {
         return false;
      } else {
         class_310 var2 = class_310.method_1551();
         UUID var3 = var2.field_1724 == null ? null : var2.field_1724.method_5667();
         if (this.applyTo.get() == SkinProtect.ApplyTo.EVERYONE) {
            return true;
         } else {
            return this.applyTo.get() == SkinProtect.ApplyTo.SELF ? var3 != null && var3.equals(uuid) : var3 == null || !var3.equals(uuid);
         }
      }
   }

   private void ensureFetched() {
      String var1 = ((String)this.ign.get()).trim();
      if (!var1.isEmpty() && !this.fetching && !var1.equalsIgnoreCase(this.fetchedFor)) {
         this.fetching = true;
         CompletableFuture.runAsync(() -> this.resolve(var1)).whenComplete((v, t) -> this.fetching = false);
      }
   }

   private void resolve(String username) {
      try {
         JsonObject var2 = getJson("https://api.mojang.com/users/profiles/minecraft/" + username);
         if (var2 == null || !var2.has("id")) {
            VieClientMod.LOGGER
               .warn(
                  "[SkinProtect] Unknown username: {}",
                  username
               );
            return;
         }

         UUID var3 = dashify(var2.get("id").getAsString());
         String var4 = var2.has("name")
            ? var2.get("name").getAsString()
            : username;
         JsonObject var5 = getJson("https://sessionserver.mojang.com/session/minecraft/profile/" + var3.toString().replace("-", "") + "?unsigned=false");
         if (var5 == null || !var5.has("properties")) {
            VieClientMod.LOGGER
               .warn(
                  "[SkinProtect] No profile textures for {}",
                  username
               );
            return;
         }

         LinkedHashMultimap var6 = LinkedHashMultimap.create();

         for (JsonElement var8 : var5.getAsJsonArray("properties")) {
            JsonObject var9 = var8.getAsJsonObject();
            if ("textures"
               .equals(var9.get("name").getAsString())) {
               String var10 = var9.get("value").getAsString();
               String var11 = var9.has("signature")
                  ? var9.get("signature").getAsString()
                  : null;
               var6.put(
                  "textures",
                  var11 == null
                     ? new Property("textures", var10)
                     : new Property("textures", var10, var11)
               );
            }
         }

         GameProfile var13 = new GameProfile(var3, var4, new PropertyMap(var6));
         class_310.method_1551()
            .method_1582()
            .method_52863(var13)
            .thenAccept(
               opt -> {
                  if (opt.isPresent()) {
                     this.replacement = (class_8685)opt.get();
                     this.fetchedFor = username;
                     VieClientMod.LOGGER
                        .info(
                           "[SkinProtect] Loaded skin for {}",
                           var4
                        );
                  } else {
                     VieClientMod.LOGGER
                        .warn(
                           "[SkinProtect] Could not load skin texture for {}",
                           var4
                        );
                  }
               }
            );
      } catch (Exception var12) {
         VieClientMod.LOGGER
            .warn(
               "[SkinProtect] Failed to fetch skin for {}: {}",
               username,
               var12.toString()
            );
      }
   }

   private static JsonObject getJson(String url) throws Exception {
      Builder var10000 = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(8L));
      byte[] var10001 = new byte[6];
      var10001[0] = (byte)(100);
      var10001[1] = (byte)(-7);
      var10001[2] = (byte)(127);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-30);
      var10001[5] = (byte)(-92);
      String var3 = $$v_s(var10001, 3073722324775849984L);
      byte[] var10002 = new byte[16];
      var10002[0] = (byte)(68);
      var10002[1] = (byte)(-22);
      var10002[2] = (byte)(108);
      var10002[3] = (byte)(-78);
      var10002[4] = (byte)(-5);
      var10002[5] = (byte)(-77);
      var10002[6] = (byte)(-54);
      var10002[7] = (byte)(118);
      var10002[8] = (byte)(36);
      var10002[9] = (byte)(-51);
      var10002[10] = (byte)(-118);
      var10002[11] = (byte)(-39);
      var10002[12] = (byte)(-96);
      var10002[13] = (byte)(123);
      var10002[14] = (byte)(-20);
      var10002[15] = (byte)(20);
      HttpRequest var1 = var10000.header(var3, $$v_s(var10002, 3073722324775849984L)).GET().build();
      HttpResponse var2 = HTTP.send(var1, BodyHandlers.ofString());
      return var2.statusCode() == (200) && var2.body() != null && !((String)var2.body()).isBlank()
         ? JsonParser.parseString((String)var2.body()).getAsJsonObject()
         : null;
   }

   private static UUID dashify(String undashed) {
      byte[] var10001 = new byte[36];
      var10001[0] = (byte)(13);
      var10001[1] = (byte)(-58);
      var10001[2] = (byte)(107);
      var10001[3] = (byte)(-91);
      var10001[4] = (byte)(-86);
      var10001[5] = (byte)(-83);
      var10001[6] = (byte)(-126);
      var10001[7] = (byte)(42);
      var10001[8] = (byte)(17);
      var10001[9] = (byte)(-43);
      var10001[10] = (byte)(-97);
      var10001[11] = (byte)(-62);
      var10001[12] = (byte)(-73);
      var10001[13] = (byte)(33);
      var10001[14] = (byte)(-85);
      var10001[15] = (byte)(38);
      var10001[16] = (byte)(2);
      var10001[17] = (byte)(49);
      var10001[18] = (byte)(-8);
      var10001[19] = (byte)(-45);
      var10001[20] = (byte)(11);
      var10001[21] = (byte)(8);
      var10001[22] = (byte)(-89);
      var10001[23] = (byte)(37);
      var10001[24] = (byte)(-26);
      var10001[25] = (byte)(38);
      var10001[26] = (byte)(-23);
      var10001[27] = (byte)(111);
      var10001[28] = (byte)(50);
      var10001[29] = (byte)(4);
      var10001[30] = (byte)(-92);
      var10001[31] = (byte)(-15);
      var10001[32] = (byte)(-12);
      var10001[33] = (byte)(8);
      var10001[34] = 1;
      var10001[35] = (byte)(87);
      String var1 = $$v_s(var10001, 3073722324775849984L);
      byte[] var10002 = new byte[14];
      var10002[0] = 1;
      var10002[1] = (byte)(-85);
      var10002[2] = (byte)(49);
      var10002[3] = (byte)(-6);
      var10002[4] = (byte)(-96);
      var10002[5] = (byte)(-3);
      var10002[6] = (byte)(-113);
      var10002[7] = (byte)(49);
      var10002[8] = (byte)(96);
      var10002[9] = (byte)(-122);
      var10002[10] = (byte)(-48);
      var10002[11] = (byte)(-37);
      var10002[12] = (byte)(-18);
      var10002[13] = (byte)(61);
      return UUID.fromString(undashed.replaceFirst(var1, $$v_s(var10002, 3073722324775849984L)));
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

   public enum ApplyTo {
      EVERYONE,
      OTHERS,
      SELF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SkinProtect.ApplyTo[] $values() {
         SkinProtect.ApplyTo[] var10000 = new SkinProtect.ApplyTo[3];
         var10000[0] = EVERYONE;
         var10000[1] = OTHERS;
         var10000[2] = SELF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-52);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-116);
         var10002[4] = 0;
         var10002[5] = (byte)(-55);
         var10002[6] = (byte)(-126);
         var10002[7] = (byte)(-117);
         EVERYONE = new SkinProtect.ApplyTo();
         var10002 = new byte[6];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(84);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(11);
         var10002[5] = (byte)(-43);
         OTHERS = new SkinProtect.ApplyTo();
         var10002 = new byte[4];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-104);
         SELF = new SkinProtect.ApplyTo();
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
