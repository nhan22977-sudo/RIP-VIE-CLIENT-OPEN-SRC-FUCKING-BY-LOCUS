package me.drake.vieclient.internal;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public final class MsgpackEncoder {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MsgpackEncoder() {
   }

   public static byte[] encodeMap(Map map) {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(256);
      writeMap(var1, map);
      return var1.toByteArray();
   }

   private static void writeValue(ByteArrayOutputStream out, Object val) {
      if (val == null) {
         out.write(192);
      } else if (val instanceof Boolean) {
         out.write((Boolean)val ? 195 : 194);
      } else if (val instanceof Byte) {
         writeInt(out, ((Byte)val).longValue());
      } else if (val instanceof Short) {
         writeInt(out, ((Short)val).longValue());
      } else if (val instanceof Integer) {
         writeInt(out, ((Integer)val).longValue());
      } else if (val instanceof Long) {
         writeInt(out, (Long)val);
      } else if (val instanceof Number) {
         writeFloat64(out, ((Number)val).doubleValue());
      } else if (val instanceof String) {
         writeString(out, (String)val);
      } else if (val instanceof byte[]) {
         writeBin(out, (byte[])val);
      } else if (val instanceof Map var2) {
         writeMap(out, var2);
      } else if (val instanceof Collection) {
         writeArray(out, (Collection)val);
      } else if (val instanceof Object[]) {
         writeArray(out, Arrays.asList((Object[])val));
      } else {
         writeString(out, val.toString());
      }
   }

   private static void writeMap(ByteArrayOutputStream out, Map map) {
      int var2 = map.size();
      if (var2 <= (15)) {
         out.write(128 | var2);
      } else if (var2 <= (65535)) {
         out.write(222);
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      } else {
         out.write(223);
         out.write(var2 >> (24) & (255));
         out.write(var2 >> (16) & (255));
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      }

      for (Entry var4 : map.entrySet()) {
         writeString(out, (String)var4.getKey());
         writeValue(out, var4.getValue());
      }
   }

   private static void writeArray(ByteArrayOutputStream out, Collection list) {
      int var2 = list.size();
      if (var2 <= (15)) {
         out.write(144 | var2);
      } else if (var2 <= (65535)) {
         out.write(220);
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      } else {
         out.write(221);
         out.write(var2 >> (24) & (255));
         out.write(var2 >> (16) & (255));
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      }

      for (Object var4 : list) {
         writeValue(out, var4);
      }
   }

   private static void writeString(ByteArrayOutputStream out, String s) {
      byte[] var2 = s.getBytes(StandardCharsets.UTF_8);
      int var3 = var2.length;
      if (var3 <= (31)) {
         out.write(160 | var3);
      } else if (var3 <= (255)) {
         out.write(217);
         out.write(var3);
      } else if (var3 <= (65535)) {
         out.write(218);
         out.write(var3 >> (8) & (255));
         out.write(var3 & (255));
      } else {
         out.write(219);
         out.write(var3 >> (24) & (255));
         out.write(var3 >> (16) & (255));
         out.write(var3 >> (8) & (255));
         out.write(var3 & (255));
      }

      out.write(var2, 0, var3);
   }

   private static void writeBin(ByteArrayOutputStream out, byte[] bytes) {
      int var2 = bytes.length;
      if (var2 <= (255)) {
         out.write(196);
         out.write(var2);
      } else if (var2 <= (65535)) {
         out.write(197);
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      } else {
         out.write(198);
         out.write(var2 >> (24) & (255));
         out.write(var2 >> (16) & (255));
         out.write(var2 >> (8) & (255));
         out.write(var2 & (255));
      }

      out.write(bytes, 0, var2);
   }

   private static void writeInt(ByteArrayOutputStream out, long val) {
      if (val >= 0L) {
         if (val <= 127L) {
            out.write((int)val);
         } else if (val <= 255L) {
            out.write(204);
            out.write((int)val);
         } else if (val <= 65535L) {
            out.write(205);
            out.write((int)(val >> (8) & 255L));
            out.write((int)(val & 255L));
         } else if (val <= 4294967295L) {
            out.write(206);
            out.write((int)(val >> (24) & 255L));
            out.write((int)(val >> (16) & 255L));
            out.write((int)(val >> (8) & 255L));
            out.write((int)(val & 255L));
         } else {
            out.write(207);

            for (int var3 = 56; var3 >= 0; var3 -= 8) {
               out.write((int)(val >> var3 & 255L));
            }
         }
      } else if (val >= -32L) {
         out.write((int)(val & 255L));
      } else if (val >= -128L) {
         out.write(208);
         out.write((int)(val & 255L));
      } else if (val >= -32768L) {
         out.write(209);
         out.write((int)(val >> (8) & 255L));
         out.write((int)(val & 255L));
      } else if (val >= -2147483648L) {
         out.write(210);
         out.write((int)(val >> ('鰅' ^ '鰝') & 255L));
         out.write((int)(val >> (16) & 255L));
         out.write((int)(val >> (8) & 255L));
         out.write((int)(val & 255L));
      } else {
         out.write(211);

         for (int var4 = 56; var4 >= 0; var4 -= 8) {
            out.write((int)(val >> var4 & 255L));
         }
      }
   }

   private static void writeFloat64(ByteArrayOutputStream out, double val) {
      long var3 = Double.doubleToRawLongBits(val);
      out.write(203);

      for (int var5 = 56; var5 >= 0; var5 -= 8) {
         out.write((int)(var3 >> var5 & 255L));
      }
   }
}
