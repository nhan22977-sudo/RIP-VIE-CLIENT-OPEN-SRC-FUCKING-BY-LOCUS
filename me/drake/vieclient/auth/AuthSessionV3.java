package me.drake.vieclient.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Set;

public final class AuthSessionV3 {
   public static final int MAX_TOKEN_BYTES = 4096;
   public static final String EXPECTED_ISSUER = "vie-auth";
   public static final String EXPECTED_AUDIENCE = "vie-client";
   public static final int EXPECTED_VERSION = 3;
   public static final long CLOCK_SKEW_GRACE_SECONDS = 30L;
   public static final long FUTURE_TOLERANCE_SECONDS = 120L;
   public static final byte[] ED25519_SPKI_PREFIX;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static PublicKey parseEd25519PublicKey(String hexOrBase64) {
      if (hexOrBase64 != null && !hexOrBase64.isBlank()) {
         String var1 = hexOrBase64.trim();
         if (var1.startsWith("-----BEGIN")) {
            var1 = var1.replace(
                  "-----BEGIN PUBLIC KEY-----",
                  ""
               )
               .replace(
                  "-----END PUBLIC KEY-----",
                  ""
               )
               .replaceAll("\\s+", "");
         }

         try {
            byte[] var2;
            if (!var1.matches("(?i)^[0-9a-f]{64}$")
               && !var1.matches("(?i)^[0-9a-f]{88}$")
               && !var1.startsWith("302a")) {
               try {
                  var2 = Base64.getDecoder().decode(var1);
               } catch (IllegalArgumentException var4) {
                  var2 = Base64.getUrlDecoder().decode(var1);
               }
            } else {
               var2 = HexFormat.of().parseHex(var1);
            }

            if (var2.length == 32) {
               byte[] var3 = new byte[ED25519_SPKI_PREFIX.length + 32];
               System.arraycopy(ED25519_SPKI_PREFIX, 0, var3, 0, ED25519_SPKI_PREFIX.length);
               System.arraycopy(var2, 0, var3, ED25519_SPKI_PREFIX.length, 32);
               var2 = var3;
            }

            KeyFactory var6 = KeyFactory.getInstance("Ed25519");
            return var6.generatePublic(new X509EncodedKeySpec(var2));
         } catch (Exception var5) {
            throw new IllegalArgumentException("Failed to parse Ed25519 public key: " + var5.getMessage(), var5);
         }
      } else {
         throw new IllegalArgumentException(
            "Public key string must not be blank."
         );
      }
   }

   public static AuthSessionV3.Claims verify(String token, PublicKey verifierKey, String expectedKey, String expectedHwid, String expectedNonce, Instant now) throws SecurityException {
      if (token != null && !token.isBlank()) {
         if (token.length() > 4096) {
            throw new SecurityException(
               "Token exceeds maximum size of 4096 bytes."
            );
         }

         if (verifierKey == null) {
            throw new SecurityException(
               "Verifier public key is required."
            );
         }

         String[] var6 = token.split("\\.");
         if (var6.length != 2) {
            throw new SecurityException(
               "Malformed token: expected exactly two dot-separated parts."
            );
         }

         byte[] var7;
         byte[] var8;
         try {
            var7 = Base64.getUrlDecoder().decode(var6[0]);
            var8 = Base64.getUrlDecoder().decode(var6[1]);
         } catch (IllegalArgumentException var31) {
            throw new SecurityException(
               "Malformed Base64URL encoding in token.",
               var31
            );
         }

         if (var8.length != 64) {
            throw new SecurityException("Invalid Ed25519 signature length: expected 64 bytes, got " + var8.length);
         }

         try {
            Signature var9 = Signature.getInstance("Ed25519");
            var9.initVerify(verifierKey);
            var9.update(var7);
            if (!var9.verify(var8)) {
               throw new SecurityException(
                  "Cryptographic signature mismatch."
               );
            }
         } catch (SecurityException var32) {
            throw var32;
         } catch (Exception var33) {
            throw new SecurityException("Signature verification failed: " + var33.getMessage(), var33);
         }

         JsonObject var34;
         try {
            String var10 = new String(var7, StandardCharsets.UTF_8);
            var34 = JsonParser.parseString(var10).getAsJsonObject();
         } catch (Exception var30) {
            throw new SecurityException(
               "Failed to parse token payload JSON.",
               var30
            );
         }

         int var35 = getInt(var34, "v", -1);
         if (var35 != 3) {
            throw new SecurityException("Unsupported token version: " + var35 + " (expected 3)");
         }

         String var11 = getString(var34, "iss", "");
         if (!"vie-auth".equals(var11)) {
            throw new SecurityException("Invalid issuer: " + var11 + " (expected vie-auth)");
         }

         String var12 = getString(var34, "aud", "");
         if (!"vie-client".equals(var12)) {
            throw new SecurityException("Invalid audience: " + var12 + " (expected vie-client)");
         }

         String var13 = getString(var34, "sub", "").toUpperCase(Locale.ROOT);
         if (!var13.isBlank()
            && (
               expectedKey == null
                  || expectedKey.isBlank()
                  || MessageDigest.isEqual(var13.getBytes(StandardCharsets.UTF_8), expectedKey.trim().toUpperCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8))
            )) {
            String var14 = getString(var34, "hwid", "").toLowerCase(Locale.ROOT);
            if (!var14.isBlank()
               && (
                  expectedHwid == null
                     || expectedHwid.isBlank()
                     || MessageDigest.isEqual(
                        var14.getBytes(StandardCharsets.UTF_8), expectedHwid.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8)
                     )
               )) {
               String var15 = getString(var34, "nonce", "");
               if (expectedNonce == null
                  || expectedNonce.isBlank()
                  || !var15.isBlank() && MessageDigest.isEqual(var15.getBytes(StandardCharsets.UTF_8), expectedNonce.trim().getBytes(StandardCharsets.UTF_8))) {
                  long var16 = getLong(var34, "iat", 0L);
                  long var18 = getLong(var34, "exp", 0L);
                  if (var18 <= 0L) {
                     throw new SecurityException(
                        "Token expiration (exp) is missing or non-positive."
                     );
                  }

                  Instant var20 = var16 > 0L ? Instant.ofEpochSecond(var16) : null;
                  Instant var21 = Instant.ofEpochSecond(var18);
                  Instant var22 = now != null ? now : Instant.now();
                  if (var22.isAfter(var21.plusSeconds(30L))) {
                     throw new SecurityException("Token has expired at " + var21 + " (current time: " + var22 + ")");
                  }

                  if (var20 != null && var20.isAfter(var22.plusSeconds(120L))) {
                     throw new SecurityException("Token issued in the future at " + var20 + " (current time: " + var22 + ")");
                  }

                  String var23 = getString(var34, "kid", "");
                  String var24 = getString(var34, "jti", "");
                  String var25 = getString(
                     var34,
                     "tier",
                     "unknown"
                  );
                  HashSet var26 = new HashSet();
                  if (var34.has("entitlements")
                     && var34.get("entitlements").isJsonArray()) {
                     for (JsonElement var29 : var34.getAsJsonArray(
                        "entitlements"
                     )) {
                        if (var29.isJsonPrimitive()) {
                           var26.add(var29.getAsString().toLowerCase(Locale.ROOT).trim());
                        }
                     }
                  }

                  return new AuthSessionV3.Claims(
                     var35, var11, var12, var23, var24, var13, var14, var15, var25, Collections.unmodifiableSet(var26), var20, var21
                  );
               } else {
                  throw new SecurityException(
                     "Token nonce mismatch (anti-replay check failed)."
                  );
               }
            } else {
               throw new SecurityException("Token HWID mismatch: got '" + var14 + "', expected '" + expectedHwid + "'");
            }
         } else {
            throw new SecurityException("Token subject mismatch: got '" + var13 + "', expected '" + expectedKey + "'");
         }
      } else {
         throw new SecurityException(
            "Token is null or blank."
         );
      }
   }

   private static String getString(JsonObject obj, String key, String defaultValue) {
      if (obj.has(key) && !obj.get(key).isJsonNull()) {
         try {
            return obj.get(key).getAsString().trim();
         } catch (Exception var4) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private static int getInt(JsonObject obj, String key, int defaultValue) {
      if (obj.has(key) && !obj.get(key).isJsonNull()) {
         try {
            return obj.get(key).getAsInt();
         } catch (Exception var4) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private static long getLong(JsonObject obj, String key, long defaultValue) {
      if (obj.has(key) && !obj.get(key).isJsonNull()) {
         try {
            return obj.get(key).getAsLong();
         } catch (Exception var5) {
            return defaultValue;
         }
      } else {
         return defaultValue;
      }
   }

   private AuthSessionV3() {
   }

   static {
      HexFormat var10000 = HexFormat.of();
      byte[] var10001 = new byte[24];
      var10001[0] = (byte)(45);
      var10001[1] = (byte)(-86);
      var10001[2] = (byte)(46);
      var10001[3] = (byte)(-65);
      var10001[4] = (byte)(-103);
      var10001[5] = (byte)(-118);
      var10001[6] = (byte)(-127);
      var10001[7] = (byte)(50);
      var10001[8] = (byte)(70);
      var10001[9] = (byte)(-108);
      var10001[10] = (byte)(-44);
      var10001[11] = (byte)(-59);
      var10001[12] = (byte)(-64);
      var10001[13] = 0;
      var10001[14] = (byte)(-81);
      var10001[15] = (byte)(74);
      var10001[16] = (byte)(121);
      var10001[17] = (byte)(122);
      var10001[18] = (byte)(-4);
      var10001[19] = (byte)(-99);
      var10001[20] = (byte)(40);
      var10001[21] = (byte)(123);
      var10001[22] = (byte)(-47);
      var10001[23] = (byte)(103);
      ED25519_SPKI_PREFIX = var10000.parseHex($$v_s(var10001, 3436919499653369915L));
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

   public record Claims(
      int version,
      String issuer,
      String audience,
      String keyId,
      String tokenId,
      String subjectKey,
      String hwid,
      String nonce,
      String tier,
      Set<String> entitlements,
      Instant issuedAt,
      Instant expiresAt
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean hasEntitlement(String category) {
         if (category != null && !category.isBlank()) {
            String var2 = category.toLowerCase(Locale.ROOT).trim();
            return this.entitlements.contains(var2);
         } else {
            return false;
         }
      }

      public boolean isExpiredAt(Instant checkInstant) {
         return checkInstant.isAfter(this.expiresAt.plusSeconds(30L));
      }
   }
}
