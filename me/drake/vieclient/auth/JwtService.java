package me.drake.vieclient.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class JwtService {
   private static final String HMAC_ALG = "HmacSHA256";
   private static final long MAX_TOKEN_AGE_SECONDS = 604800L;
   private final byte[] secretBytes;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public JwtService(String secret) {
      if (secret != null && !secret.isBlank()) {
         this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
      } else {
         byte[] var10002 = new byte[38];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-48);
         var10002[2] = (byte)(107);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(-68);
         var10002[5] = (byte)(26);
         var10002[6] = (byte)(112);
         var10002[7] = (byte)(35);
         var10002[8] = (byte)(54);
         var10002[9] = (byte)(-63);
         var10002[10] = (byte)(-127);
         var10002[11] = (byte)(-85);
         var10002[12] = (byte)(-105);
         var10002[13] = (byte)(-44);
         var10002[14] = (byte)(79);
         var10002[15] = (byte)(78);
         var10002[16] = (byte)(21);
         var10002[17] = (byte)(47);
         var10002[18] = (byte)(-72);
         var10002[19] = (byte)(-114);
         var10002[20] = (byte)(50);
         var10002[21] = (byte)(-6);
         var10002[22] = (byte)(33);
         var10002[23] = (byte)(113);
         var10002[24] = (byte)(-81);
         var10002[25] = (byte)(124);
         var10002[26] = (byte)(-5);
         var10002[27] = (byte)(50);
         var10002[28] = (byte)(71);
         var10002[29] = (byte)(-107);
         var10002[30] = (byte)(31);
         var10002[31] = (byte)(-3);
         var10002[32] = (byte)(-75);
         var10002[33] = (byte)(86);
         var10002[34] = (byte)(29);
         var10002[35] = (byte)(16);
         var10002[36] = (byte)(100);
         var10002[37] = (byte)(-15);
         throw new IllegalArgumentException($$v_s(var10002, 9007658121706065938L));
      }
   }

   public JwtService.Claims verify(String token, String expectedKey, String expectedHwid) {
      if (token != null && !token.isBlank()) {
         String[] var4 = token.split("\\.");
         if (var4.length != 3) {
            return null;
         }

         String var5 = var4[0] + "." + var4[1];
         byte[] var6 = hmacSha256(var5.getBytes(StandardCharsets.UTF_8), this.secretBytes);

         byte[] var7;
         try {
            var7 = Base64.getUrlDecoder().decode(var4[2]);
         } catch (IllegalArgumentException var21) {
            return null;
         }

         if (!Arrays.equals(var6, var7)) {
            return null;
         }

         JsonObject var8;
         try {
            String var9 = new String(Base64.getUrlDecoder().decode(var4[1]), StandardCharsets.UTF_8);
            var8 = JsonParser.parseString(var9).getAsJsonObject();
         } catch (Exception var20) {
            return null;
         }

         long var22 = getLong(var8, "exp", 0L);
         long var11 = getLong(var8, "iat", 0L);
         if (var22 <= 0L) {
            return null;
         }

         Instant var13 = Instant.now();
         Instant var14 = Instant.ofEpochSecond(var22);
         Instant var15 = var11 > 0L ? Instant.ofEpochSecond(var11) : null;
         if (var13.isAfter(var14.plusSeconds(30L))) {
            return null;
         }

         if (var15 != null && var15.isAfter(var13.plusSeconds(120L))) {
            return null;
         }

         if (var15 != null && var13.isAfter(var15.plusSeconds(604800L))) {
            return null;
         }

         String var16 = getString(
            var8, "key", getString(var8, "sub", "")
         );
         String var17 = getString(var8, "hwid", "");
         String var18 = getString(var8, "plan", "");
         if (expectedKey == null || expectedKey.isBlank() || !var16.isBlank() && normalizeKey(var16).equals(normalizeKey(expectedKey))) {
            if (expectedHwid != null && !expectedHwid.isBlank()) {
               String var19 = expectedHwid.trim().toLowerCase(Locale.ROOT);
               if (var17.isBlank() || !var17.trim().toLowerCase(Locale.ROOT).equals(var19)) {
                  return null;
               }
            }

            return new JwtService.Claims(var16, var17, var18, var15, var14);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isStructurallyValid(String token) {
      if (token != null && !token.isBlank()) {
         byte[] var10001 = new byte[2];
         var10001[0] = (byte)(107);
         var10001[1] = (byte)(-76);
         String[] var2 = token.split($$v_s(var10001, 9007658121706065938L));
         if (var2.length != (3)) {
            return false;
         }

         String var3 = var2[0] + "." + var2[1];
         byte[] var4 = hmacSha256(var3.getBytes(StandardCharsets.UTF_8), this.secretBytes);

         byte[] var5;
         try {
            var5 = Base64.getUrlDecoder().decode(var2[2]);
         } catch (IllegalArgumentException var7) {
            return false;
         }

         return Arrays.equals(var4, var5);
      } else {
         return false;
      }
   }

   private static byte[] hmacSha256(byte[] data, byte[] key) {
      try {
         byte[] var10000 = new byte[10];
         var10000[0] = (byte)(127);
         var10000[1] = (byte)(-9);
         var10000[2] = (byte)(125);
         var10000[3] = (byte)(-67);
         var10000[4] = (byte)(-68);
         var10000[5] = (byte)(55);
         var10000[6] = (byte)(67);
         var10000[7] = (byte)(103);
         var10000[8] = (byte)(106);
         var10000[9] = (byte)(-108);
         Mac var2 = Mac.getInstance($$v_s(var10000, 9007658121706065938L));
         byte[] var10004 = new byte[10];
         var10004[0] = (byte)(127);
         var10004[1] = (byte)(-9);
         var10004[2] = (byte)(125);
         var10004[3] = (byte)(-67);
         var10004[4] = (byte)(-68);
         var10004[5] = (byte)(55);
         var10004[6] = (byte)(67);
         var10004[7] = (byte)(103);
         var10004[8] = (byte)(106);
         var10004[9] = (byte)(-108);
         var2.init(new SecretKeySpec(key, $$v_s(var10004, 9007658121706065938L)));
         return var2.doFinal(data);
      } catch (Exception var3) {
         byte[] var10002 = new byte[31];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-48);
         var10002[2] = (byte)(107);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(-68);
         var10002[5] = (byte)(26);
         var10002[6] = (byte)(112);
         var10002[7] = (byte)(35);
         var10002[8] = (byte)(54);
         var10002[9] = (byte)(-63);
         var10002[10] = (byte)(-127);
         var10002[11] = (byte)(-85);
         var10002[12] = (byte)(-105);
         var10002[13] = (byte)(-17);
         var10002[14] = (byte)(103);
         var10002[15] = (byte)(108);
         var10002[16] = (byte)(36);
         var10002[17] = (byte)(103);
         var10002[18] = (byte)(-97);
         var10002[19] = (byte)(-26);
         var10002[20] = (byte)(30);
         var10002[21] = (byte)(-67);
         var10002[22] = (byte)(103);
         var10002[23] = (byte)(51);
         var10002[24] = (byte)(-81);
         var10002[25] = (byte)(116);
         var10002[26] = (byte)(-11);
         var10002[27] = (byte)(47);
         var10002[28] = (byte)(11);
         var10002[29] = (byte)(-110);
         var10002[30] = (byte)(30);
         throw new RuntimeException($$v_s(var10002, 9007658121706065938L), var3);
      }
   }

   private static String getString(JsonObject obj, String key, String defaultValue) {
      if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
         try {
            return obj.get(key).getAsString();
         } catch (Exception var4) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private static long getLong(JsonObject obj, String key, long defaultValue) {
      if (obj != null && obj.has(key) && !obj.get(key).isJsonNull()) {
         try {
            return obj.get(key).getAsLong();
         } catch (Exception var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private static String normalizeKey(String key) {
      return key == null ? "" : key.trim().toUpperCase(Locale.ROOT);
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

   public static final class Claims {
      private final String key;
      private final String hwid;
      private final String plan;
      private final Instant issuedAt;
      private final Instant expiresAt;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public Claims(String key, String hwid, String plan, Instant issuedAt, Instant expiresAt) {
         this.key = keyx == null ? "" : keyx;
         this.hwid = hwidx == null ? "" : hwidx;
         this.plan = planx == null ? "" : planx;
         this.issuedAt = issuedAtx;
         this.expiresAt = expiresAtx;
      }

      public String key() {
         return this.key;
      }

      public String hwid() {
         return this.hwid;
      }

      public String plan() {
         return this.plan;
      }

      public Instant issuedAt() {
         return this.issuedAt;
      }

      public Instant expiresAt() {
         return this.expiresAt;
      }

      @Override
      public String toString() {
         return "Claims{key='" + this.key + "', hwid='" + this.hwid + "', plan='" + this.plan + "', iat=" + this.issuedAt + ", exp=" + this.expiresAt + "}";
      }
   }
}
