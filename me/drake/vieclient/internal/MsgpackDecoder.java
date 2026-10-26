package me.drake.vieclient.internal;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MsgpackDecoder {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MsgpackDecoder() {
   }

   public static Map decodeMap(byte[] data) {
      ByteBuffer var1 = ByteBuffer.wrap(data);
      if (readValue(var1) instanceof Map var3) {
         return var3;
      } else {
         byte[] var10002 = new byte[36];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(108);
         var10002[3] = (byte)(-13);
         var10002[4] = (byte)(56);
         var10002[5] = (byte)(-93);
         var10002[6] = (byte)(37);
         var10002[7] = (byte)(103);
         var10002[8] = (byte)(62);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-119);
         var10002[11] = (byte)(-123);
         var10002[12] = (byte)(107);
         var10002[13] = (byte)(110);
         var10002[14] = (byte)(26);
         var10002[15] = (byte)(25);
         var10002[16] = 1;
         var10002[17] = (byte)(106);
         var10002[18] = (byte)(-70);
         var10002[19] = (byte)(-49);
         var10002[20] = (byte)(-120);
         var10002[21] = (byte)(67);
         var10002[22] = (byte)(102);
         var10002[23] = (byte)(114);
         var10002[24] = (byte)(-21);
         var10002[25] = (byte)(97);
         var10002[26] = (byte)(-76);
         var10002[27] = (byte)(40);
         var10002[28] = (byte)(-77);
         var10002[29] = (byte)(58);
         var10002[30] = (byte)(11);
         var10002[31] = (byte)(-21);
         var10002[32] = (byte)(-6);
         var10002[33] = (byte)(87);
         var10002[34] = (byte)(29);
         var10002[35] = (byte)(14);
         throw new IllegalArgumentException($$v_s(var10002, 3048964355165245471L));
      }
   }

   private static Object readValue(ByteBuffer buf) {
      int var1 = buf.get() & 255;
      if (var1 <= 127) {
         return var1;
      } else if (var1 >= 128 && var1 <= 143) {
         return readMap(buf, var1 & 15);
      } else if (var1 >= 144 && var1 <= 159) {
         return readArray(buf, var1 & 15);
      } else if (var1 >= 160 && var1 <= 191) {
         return readString(buf, var1 & 31);
      } else if (var1 == 192) {
         return null;
      } else if (var1 == 194) {
         return false;
      } else if (var1 == 195) {
         return true;
      } else if (var1 == 196) {
         int var5 = buf.get() & 255;
         byte[] var7 = new byte[var5];
         buf.get(var7);
         return var7;
      } else if (var1 == 197) {
         int var4 = buf.getShort() & '\uffff';
         byte[] var6 = new byte[var4];
         buf.get(var6);
         return var6;
      } else if (var1 == 198) {
         int var2 = buf.getInt();
         byte[] var3 = new byte[var2];
         buf.get(var3);
         return var3;
      } else if (var1 == 202) {
         buf.position(buf.position() + 4);
         return null;
      } else if (var1 == 203) {
         buf.position(buf.position() + 8);
         return null;
      } else if (var1 == 204) {
         return buf.get() & 255;
      } else if (var1 == 205) {
         return buf.getShort() & 65535;
      } else if (var1 == 206) {
         return buf.getInt() & 4294967295L;
      } else if (var1 == 207) {
         return buf.getLong();
      } else if (var1 == 208) {
         return Integer.valueOf(buf.get());
      } else if (var1 == 209) {
         return Integer.valueOf(buf.getShort());
      } else if (var1 == 210) {
         return buf.getInt();
      } else if (var1 == 211) {
         return buf.getLong();
      } else if (var1 == 217) {
         return readString(buf, buf.get() & 255);
      } else if (var1 == 218) {
         return readString(buf, buf.getShort() & 65535);
      } else if (var1 == 219) {
         return readString(buf, buf.getInt());
      } else if (var1 == 220) {
         return readArray(buf, buf.getShort() & 65535);
      } else if (var1 == 221) {
         return readArray(buf, buf.getInt());
      } else if (var1 == 222) {
         return readMap(buf, buf.getShort() & 65535);
      } else if (var1 == 223) {
         return readMap(buf, buf.getInt());
      } else if (var1 >= 224) {
         return (byte)var1;
      } else {
         throw new IllegalArgumentException("Unsupported msgpack type: 0x" + Integer.toHexString(var1));
      }
   }

   private static Map readMap(ByteBuffer buf, int size) {
      LinkedHashMap var2 = new LinkedHashMap(size);

      for (int var3 = 0; var3 < size; var3++) {
         Object var4 = readValue(buf);
         Object var5 = readValue(buf);
         var2.put(String.valueOf(var4), var5);
      }

      return var2;
   }

   private static List readArray(ByteBuffer buf, int size) {
      ArrayList var2 = new ArrayList(size);

      for (int var3 = 0; var3 < size; var3++) {
         var2.add(readValue(buf));
      }

      return var2;
   }

   private static String readString(ByteBuffer buf, int len) {
      byte[] var2 = new byte[len];
      buf.get(var2);
      return new String(var2, StandardCharsets.UTF_8);
   }

   public static Map decodeToMap(byte[] data) {
      return decodeMap(data);
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
