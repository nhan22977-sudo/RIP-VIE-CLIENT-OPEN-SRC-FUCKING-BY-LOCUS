package me.drake.vieclient.auth;

import j2cc.internal.Loader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import javax.crypto.SecretKey;

public final class ChimeraEngineV5 {
   public static final String PROTOCOL = "CHIMERA-PROTECT";
   private static final char[] DNA_MAP;
   private static final byte[] REV_DNA_MAP = new byte[256];
   private static final int GCM_NONCE_BYTES = 12;
   private static final int GCM_TAG_BYTES = 16;
   private static final int GCM_TAG_BITS = 128;
   private static final int PBKDF2_ITERATIONS = 100000;
   private static final int AES_KEY_BYTES = 32;
   private static final int SALT_BYTES = 16;
   private final byte[] masterKey;
   private final SecureRandom rng;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChimeraEngineV5(byte[] masterKey) {
      if (masterKeyx != null && masterKeyx.length != 0) {
         this.masterKey = (byte[])masterKeyx.clone();
         this.rng = new SecureRandom();
      } else {
         byte[] var10002 = new byte[33];
         var10002[0] = (byte)(87);
         var10002[1] = (byte)(-5);
         var10002[2] = (byte)(111);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(-48);
         var10002[5] = (byte)(-6);
         var10002[6] = (byte)(-25);
         var10002[7] = (byte)(-68);
         var10002[8] = (byte)(43);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-121);
         var10002[11] = (byte)(-105);
         var10002[12] = (byte)(-125);
         var10002[13] = (byte)(62);
         var10002[14] = (byte)(-21);
         var10002[15] = (byte)(-43);
         var10002[16] = (byte)(74);
         var10002[17] = (byte)(40);
         var10002[18] = (byte)(-87);
         var10002[19] = (byte)(-114);
         var10002[20] = (byte)(107);
         var10002[21] = (byte)(13);
         var10002[22] = (byte)(-112);
         var10002[23] = (byte)(-27);
         var10002[24] = (byte)(-94);
         var10002[25] = (byte)(125);
         var10002[26] = (byte)(-26);
         var10002[27] = (byte)(102);
         var10002[28] = (byte)(88);
         var10002[29] = (byte)(109);
         var10002[30] = (byte)(-92);
         var10002[31] = (byte)(37);
         var10002[32] = (byte)(-93);
         throw new IllegalArgumentException($$v_s(var10002, -1031511178855854049L));
      }
   }

   public static native byte[] deriveMasterKey(String password);

   public static native byte[] deriveMasterKey(String password, byte[] salt);

   public native Map encode(String rawStr);

   public native String decode(String dnaSequence, String saltHex);

   public native byte[] decodeBytes(String dnaSequence, String saltHex);

   public native String encodeToJson(String rawStr);

   public native String decodeFromJson(String base64Json);

   private native byte[] deriveKey(byte[] salt);

   private native char[] toChars(byte[] bytes);

   private native String bytesToDna(byte[] data);

   private native byte[] dnaToBytes(String dna);

   private native byte[] randomBytes(int len);

   private static native SecretKey aesKey(byte[] keyBytes);

   private static native String bytesToHex(byte[] bytes);

   private static native byte[] hexToBytes(String hex);

   static {
      Loader.doInit(ChimeraEngineV5.class);
      char[] var10000 = new char[4];
      var10000[0] = (char)(65);
      var10000[1] = (char)(84);
      var10000[2] = (char)(71);
      var10000[3] = (char)(67);
      DNA_MAP = var10000;
      REV_DNA_MAP[65] = 0;
      REV_DNA_MAP[84] = 1;
      REV_DNA_MAP[71] = (byte)(2);
      REV_DNA_MAP[67] = (byte)(3);
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
