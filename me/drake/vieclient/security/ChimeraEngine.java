package me.drake.vieclient.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class ChimeraEngine {
   private static final char[] DNA_MAP;
   private static final int[] DNA_REV_MAP = new int[256];
   public static final String PROTOCOL = "CHIMERA-PROTECT";
   private static final int SALT_BYTES = 16;
   private static final int NONCE_BYTES = 16;
   private static final int TAG_BYTES = 16;
   private static final int KDF_ITERATIONS = 100000;
   private static final int KEY_BITS = 256;
   private static final SecureRandom RNG = new SecureRandom();
   private final String masterKey;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static Optional configuredMasterKey() {
      return configuredMasterKey(System.getenv());
   }

   static Optional configuredMasterKey(Map environment) {
      byte[] var10001 = new byte[15];
      var10001[0] = (byte)(97);
      var10001[1] = (byte)(-45);
      var10001[2] = (byte)(89);
      var10001[3] = (byte)(-127);
      var10001[4] = (byte)(47);
      var10001[5] = (byte)(35);
      var10001[6] = (byte)(9);
      var10001[7] = (byte)(-29);
      var10001[8] = (byte)(26);
      var10001[9] = (byte)(-16);
      var10001[10] = (byte)(-91);
      var10001[11] = (byte)(-87);
      var10001[12] = (byte)(127);
      var10001[13] = (byte)(-10);
      var10001[14] = (byte)(49);
      String var1 = (String)environment.get($$v_s(var10001, -8771967721505828846L));
      if (var1 == null || var1.isBlank()) {
         var10001 = new byte[18];
         var10001[0] = (byte)(116);
         var10001[1] = (byte)(-46);
         var10001[2] = (byte)(85);
         var10001[3] = (byte)(-109);
         var10001[4] = (byte)(41);
         var10001[5] = (byte)(57);
         var10001[6] = 1;
         var10001[7] = (byte)(-15);
         var10001[8] = (byte)(18);
         var10001[9] = (byte)(-29);
         var10001[10] = (byte)(-73);
         var10001[11] = (byte)(-94);
         var10001[12] = (byte)(113);
         var10001[13] = (byte)(-31);
         var10001[14] = (byte)(55);
         var10001[15] = (byte)(-99);
         var10001[16] = (byte)(34);
         var10001[17] = (byte)(19);
         var1 = (String)environment.get($$v_s(var10001, -8771967721505828846L));
      }

      return var1 != null && !var1.isBlank() ? Optional.of(var1.trim()) : Optional.empty();
   }

   public ChimeraEngine(String masterKey) {
      if (masterKeyx != null && !masterKeyx.isBlank()) {
         this.masterKey = masterKeyx;
      } else {
         byte[] var10002 = new byte[38];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(116);
         var10002[3] = (byte)(-73);
         var10002[4] = 1;
         var10002[5] = (byte)(14);
         var10002[6] = (byte)(50);
         var10002[7] = (byte)(-49);
         var10002[8] = (byte)(2);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-119);
         var10002[11] = (byte)(-105);
         var10002[12] = (byte)(71);
         var10002[13] = (byte)(-57);
         var10002[14] = (byte)(13);
         var10002[15] = (byte)(-92);
         var10002[16] = (byte)(44);
         var10002[17] = (byte)(47);
         var10002[18] = (byte)(-75);
         var10002[19] = (byte)(-114);
         var10002[20] = (byte)(-79);
         var10002[21] = (byte)(-18);
         var10002[22] = (byte)(99);
         var10002[23] = (byte)(-118);
         var10002[24] = (byte)(-81);
         var10002[25] = (byte)(124);
         var10002[26] = (byte)(-5);
         var10002[27] = (byte)(50);
         var10002[28] = (byte)(-60);
         var10002[29] = (byte)(-127);
         var10002[30] = (byte)(93);
         var10002[31] = (byte)(6);
         var10002[32] = (byte)(-75);
         var10002[33] = (byte)(86);
         var10002[34] = (byte)(29);
         var10002[35] = (byte)('ﰃ' ^ 'ﰓ');
         var10002[36] = (byte)(-25);
         var10002[37] = (byte)(-27);
         throw new IllegalArgumentException($$v_s(var10002, -8771967721505828846L));
      }
   }

   public ChimeraEngine.GenomePacket encode(String plainText) {
      try {
         byte[] var2 = new byte[16];
         RNG.nextBytes(var2);
         SecretKey var3 = this.deriveKey(var2);
         byte[] var10000 = new byte[17];
         var10000[0] = (byte)(118);
         var10000[1] = (byte)(-33);
         var10000[2] = (byte)(79);
         var10000[3] = (byte)(-15);
         var10000[4] = (byte)(43);
         var10000[5] = (byte)(40);
         var10000[6] = (byte)(13);
         var10000[7] = (byte)(-127);
         var10000[8] = (byte)(17);
         var10000[9] = (byte)(-51);
         var10000[10] = (byte)(-76);
         var10000[11] = (byte)(-105);
         var10000[12] = (byte)(80);
         var10000[13] = (byte)(-41);
         var10000[14] = 1;
         var10000[15] = (byte)(-72);
         var10000[16] = 0;
         Cipher var4 = Cipher.getInstance($$v_s(var10000, -8771967721505828846L));
         var4.init(1, var3);
         byte[] var5 = var4.getIV();
         byte[] var6 = var4.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
         byte[] var7 = Arrays.copyOf(var6, var6.length - (16));
         byte[] var8 = Arrays.copyOfRange(var6, var6.length - (16), var6.length);
         byte[] var9 = padTo(var5, 16);
         byte[][] var14 = new byte[3][];
         var14[0] = var9;
         var14[1] = var8;
         var14[2] = var7;
         byte[] var10 = concat(var14);
         String var11 = bytesToDna(var10);
         String var12 = bytesToHex(var2);
         return new ChimeraEngine.GenomePacket(var11, var12);
      } catch (Exception var13) {
         throw new ChimeraEngine.ChimeraException("Chimera encode failed: " + var13.getMessage(), var13);
      }
   }

   public String decode(ChimeraEngine.GenomePacket packet) {
      try {
         byte[] var2 = hexToBytes(packet.locus());
         byte[] var3 = dnaToBytes(packet.sequence());
         if (var3.length < 32) {
            throw new ChimeraEngine.ChimeraException(
               "Chimera: blob too short."
            );
         }

         byte[] var4 = Arrays.copyOf(var3, 16);
         byte[] var5 = Arrays.copyOfRange(var3, 16, 32);
         byte[] var6 = Arrays.copyOfRange(var3, 32, var3.length);
         byte[] var7 = Arrays.copyOf(var4, 12);
         SecretKey var8 = this.deriveKey(var2);
         Cipher var9 = Cipher.getInstance("AES/GCM/NoPadding");
         var9.init(2, var8, new GCMParameterSpec(128, var7));
         byte[] var10 = concat(var6, var5);
         byte[] var11 = var9.doFinal(var10);
         return new String(var11, StandardCharsets.UTF_8);
      } catch (ChimeraEngine.ChimeraException var12) {
         throw var12;
      } catch (Exception var13) {
         throw new ChimeraEngine.ChimeraException("Chimera decode failed (invalid key or corrupted DNA): " + var13.getMessage(), var13);
      }
   }

   public String encodeToCompact(String plainText) {
      ChimeraEngine.GenomePacket var2 = this.encode(plainText);
      String var3 = Base64.getEncoder().encodeToString(var2.sequence().getBytes(StandardCharsets.UTF_8));
      return var3 + "." + var2.locus();
   }

   public String decodeFromCompact(String compact) {
      int var2 = compact.lastIndexOf(46);
      if (var2 < 0) {
         byte[] var10002 = new byte[32];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-14);
         var10002[2] = (byte)(117);
         var10002[3] = (byte)(-77);
         var10002[4] = (byte)(9);
         var10002[5] = (byte)(25);
         var10002[6] = (byte)(33);
         var10002[7] = (byte)(-108);
         var10002[8] = (byte)(127);
         var10002[9] = (byte)(-63);
         var10002[10] = (byte)(-117);
         var10002[11] = (byte)(-101);
         var10002[12] = (byte)(68);
         var10002[13] = (byte)(-46);
         var10002[14] = (byte)(11);
         var10002[15] = (byte)(-94);
         var10002[16] = (byte)(71);
         var10002[17] = (byte)(44);
         var10002[18] = (byte)(-93);
         var10002[19] = (byte)(-36);
         var10002[20] = (byte)(-79);
         var10002[21] = (byte)(-6);
         var10002[22] = (byte)(100);
         var10002[23] = (byte)(-34);
         var10002[24] = (byte)(-26);
         var10002[25] = (byte)(124);
         var10002[26] = (byte)(-30);
         var10002[27] = (byte)(39);
         var10002[28] = (byte)(-120);
         var10002[29] = (byte)(-118);
         var10002[30] = (byte)(92);
         var10002[31] = (byte)(8);
         throw new ChimeraEngine.ChimeraException($$v_s(var10002, -8771967721505828846L));
      } else {
         String var3 = compact.substring(0, var2);
         String var4 = compact.substring(var2 + 1);
         String var5 = new String(Base64.getDecoder().decode(var3), StandardCharsets.UTF_8);
         return this.decode(new ChimeraEngine.GenomePacket(var5, var4));
      }
   }

   private SecretKey deriveKey(byte[] salt) throws Exception {
      char[] var2 = this.masterKey.toCharArray();
      PBEKeySpec var3 = new PBEKeySpec(var2, salt, 100000, 256);
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(103);
      var10000[1] = (byte)(-40);
      var10000[2] = (byte)(87);
      var10000[3] = (byte)(-102);
      var10000[4] = (byte)(42);
      var10000[5] = (byte)(89);
      var10000[6] = (byte)(23);
      var10000[7] = (byte)(-57);
      var10000[8] = (byte)(43);
      var10000[9] = (byte)(-54);
      var10000[10] = (byte)(-84);
      var10000[11] = (byte)(-101);
      var10000[12] = (byte)(85);
      var10000[13] = (byte)(-48);
      var10000[14] = (byte)(59);
      var10000[15] = (byte)(-98);
      var10000[16] = (byte)(38);
      var10000[17] = (byte)(120);
      var10000[18] = (byte)(-7);
      var10000[19] = (byte)(-104);
      SecretKeyFactory var4 = SecretKeyFactory.getInstance($$v_s(var10000, -8771967721505828846L));
      byte[] var5 = var4.generateSecret(var3).getEncoded();
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(118);
      var10003[1] = (byte)(-33);
      var10003[2] = (byte)(79);
      return new SecretKeySpec(var5, $$v_s(var10003, -8771967721505828846L));
   }

   private static String bytesToDna(byte[] data) {
      StringBuilder var1 = new StringBuilder(data.length * (4));

      for (byte var5 : data) {
         int var6 = var5 & (255);

         for (int var7 = 0; var7 < (4); var7++) {
            var1.append(DNA_MAP[var6 >> var7 * (2) & (3)]);
         }
      }

      return var1.toString();
   }

   private static byte[] dnaToBytes(String dna) {
      if (dna.length() % 4 != 0) {
         throw new ChimeraEngine.ChimeraException(
            "Chimera: DNA sequence length must be multiple of 4."
         );
      }

      byte[] var1 = new byte[dna.length() / 4];

      for (byte var2 = 0; var2 < dna.length(); var2 += 4) {
         int var3 = 0;

         for (int var4 = 0; var4 < 4; var4++) {
            char var5 = dna.charAt(var2 + var4);
            if (var5 >= DNA_REV_MAP.length || var5 != 'A' && var5 != 'T' && var5 != 'G' && var5 != 'C') {
               throw new ChimeraEngine.ChimeraException("Chimera: Invalid DNA char '" + var5 + "' at index " + (var2 + var4));
            }

            var3 |= DNA_REV_MAP[var5] << var4 * 2;
         }

         var1[var2 / 4] = (byte)var3;
      }

      return var1;
   }

   private static byte[] concat(byte[]... arrays) {
      int var1 = 0;

      for (byte[] var5 : arrays) {
         var1 += var5.length;
      }

      byte[] var8 = new byte[var1];
      int var9 = 0;

      for (byte[] var7 : arrays) {
         System.arraycopy(var7, 0, var8, var9, var7.length);
         var9 += var7.length;
      }

      return var8;
   }

   private static byte[] padTo(byte[] src, int targetLen) {
      if (src.length >= targetLen) {
         return Arrays.copyOf(src, targetLen);
      }

      byte[] var2 = new byte[targetLen];
      System.arraycopy(src, 0, var2, 0, src.length);
      return var2;
   }

   private static String bytesToHex(byte[] bytes) {
      StringBuilder var1 = new StringBuilder(bytes.length * (2));

      for (byte var5 : bytes) {
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(18);
         var10001[1] = (byte)(-86);
         var10001[2] = (byte)(46);
         var10001[3] = (byte)(-90);
         var1.append(String.format($$v_s(var10001, -8771967721505828846L), var5 & (255)));
      }

      return var1.toString();
   }

   private static byte[] hexToBytes(String hex) {
      if (hex.length() % (2) != 0) {
         byte[] var10002 = new byte[33];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-14);
         var10002[2] = (byte)(117);
         var10002[3] = (byte)(-77);
         var10002[4] = (byte)(9);
         var10002[5] = (byte)(25);
         var10002[6] = (byte)(33);
         var10002[7] = (byte)(-108);
         var10002[8] = (byte)(127);
         var10002[9] = (byte)(-54);
         var10002[10] = (byte)(-127);
         var10002[11] = (byte)(-114);
         var10002[12] = (byte)(20);
         var10002[13] = (byte)(-33);
         var10002[14] = (byte)(13);
         var10002[15] = (byte)(-72);
         var10002[16] = 0;
         var10002[17] = (byte)(62);
         var10002[18] = (byte)(-92);
         var10002[19] = (byte)(-114);
         var10002[20] = (byte)(-79);
         var10002[21] = (byte)(-18);
         var10002[22] = (byte)(99);
         var10002[23] = (byte)(-118);
         var10002[24] = (byte)(-81);
         var10002[25] = (byte)(112);
         var10002[26] = (byte)(-15);
         var10002[27] = (byte)(102);
         var10002[28] = (byte)(-127);
         var10002[29] = (byte)(-107);
         var10002[30] = (byte)(93);
         var10002[31] = (byte)(72);
         var10002[32] = (byte)(-7);
         throw new ChimeraEngine.ChimeraException($$v_s(var10002, -8771967721505828846L));
      }

      byte[] var1 = new byte[hex.length() / (2)];

      for (byte var2 = 0; var2 < hex.length(); var2 += 2) {
         var1[var2 / (2)] = (byte)Integer.parseInt(hex.substring(var2, var2 + (2)), 16);
      }

      return var1;
   }

   static {
      char[] var10000 = new char[4];
      var10000[0] = (char)(65);
      var10000[1] = (char)(84);
      var10000[2] = (char)(71);
      var10000[3] = (char)(67);
      DNA_MAP = var10000;
      DNA_REV_MAP[65] = 0;
      DNA_REV_MAP[84] = 1;
      DNA_REV_MAP[71] = 2;
      DNA_REV_MAP[67] = 3;
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

   public static final class ChimeraException extends RuntimeException {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public ChimeraException(String message) {
         super(message);
      }

      public ChimeraException(String message, Throwable cause) {
         super(message, cause);
      }
   }

   public record GenomePacket(String sequence, String locus) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public GenomePacket(String sequence, String locus) {
         if (sequence == null || sequence.isBlank()) {
            throw new ChimeraEngine.ChimeraException(
               "Chimera: sequence must not be blank."
            );
         }

         if (locus != null && !locus.isBlank()) {
            this.sequence = sequence;
            this.locus = locus;
         } else {
            throw new ChimeraEngine.ChimeraException(
               "Chimera: locus must not be blank."
            );
         }
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
