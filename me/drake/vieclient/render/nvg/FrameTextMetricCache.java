package me.drake.vieclient.render.nvg;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

final class FrameTextMetricCache {
   private static final ThreadLocal LOOKUP_KEY = ThreadLocal.withInitial(FrameTextMetricCache.LookupKey::new);
   private final int capacity;
   private final Map values = new LinkedHashMap(256, 0.75F, true);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   FrameTextMetricCache(int capacity) {
      if (capacityx < 1) {
         byte[] var10002 = new byte[25];
         var10002[0] = (byte)(89);
         var10002[1] = (byte)(-5);
         var10002[2] = (byte)(108);
         var10002[3] = (byte)(-65);
         var10002[4] = (byte)(-80);
         var10002[5] = (byte)(45);
         var10002[6] = (byte)(-34);
         var10002[7] = (byte)(-86);
         var10002[8] = (byte)(114);
         var10002[9] = (byte)(-49);
         var10002[10] = (byte)(-111);
         var10002[11] = (byte)(-123);
         var10002[12] = (byte)(-1);
         var10002[13] = (byte)(-68);
         var10002[14] = (byte)(-32);
         var10002[15] = (byte)(-50);
         var10002[16] = (byte)(74);
         var10002[17] = (byte)(58);
         var10002[18] = (byte)(-93);
         var10002[19] = (byte)(-35);
         var10002[20] = (byte)(10);
         var10002[21] = (byte)(-64);
         var10002[22] = (byte)(-109);
         var10002[23] = (byte)(-11);
         var10002[24] = (byte)(-25);
         throw new IllegalArgumentException($$v_s(var10002, -312548917819224033L));
      }

      this.capacity = capacityx;
   }

   void beginFrame() {
   }

   void clear() {
      this.values.clear();
   }

   float get(String text, float size, int fontId, float rasterScale) {
      FrameTextMetricCache.LookupKey var5 = (FrameTextMetricCache.LookupKey)LOOKUP_KEY.get();
      var5.set(text, size, fontId, rasterScale);
      Float var6 = (Float)this.values.get(var5);
      return var6 != null ? var6 : -1.0F;
   }

   void put(String text, float size, int fontId, float rasterScale, float value) {
      if (Float.isFinite(value)) {
         if (this.values.size() >= this.capacity) {
            Iterator var6 = this.values.keySet().iterator();
            var6.next();
            var6.remove();
         }

         this.values.put(new FrameTextMetricCache.Key(text, size, fontId, rasterScale), value);
      }
   }

   float measure(FrameTextMetricCache.Key key, FrameTextMetricCache.FloatSupplier supplier) {
      Float var3 = (Float)this.values.get(key);
      if (var3 != null) {
         return var3;
      }

      float var4 = supplier.getAsFloat();
      if (!Float.isFinite(var4)) {
         return var4;
      }

      if (this.values.size() >= this.capacity) {
         Iterator var5 = this.values.keySet().iterator();
         var5.next();
         var5.remove();
      }

      this.values.put(key, var4);
      return var4;
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

   @FunctionalInterface
   interface FloatSupplier {
      float getAsFloat();
   }

   record Key(String text, float size, int fontId, float rasterScale) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Key(String text, float size, int fontId) {
         this(textx, sizex, fontIdx, 1.0F);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o instanceof FrameTextMetricCache.Key var3) {
            return this.fontId == var3.fontId
               && Float.compare(this.size, var3.size) == 0
               && Float.compare(this.rasterScale, var3.rasterScale) == 0
               && this.text.equals(var3.text);
         } else {
            return !(o instanceof FrameTextMetricCache.LookupKey var2)
               ? false
               : this.fontId == var2.fontId
                  && Float.compare(this.size, var2.size) == 0
                  && Float.compare(this.rasterScale, var2.rasterScale) == 0
                  && this.text.equals(var2.text);
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.text.hashCode();
         var1 = (31) * var1 + Float.floatToIntBits(this.size);
         var1 = (31) * var1 + this.fontId;
         return (31) * var1 + Float.floatToIntBits(this.rasterScale);
      }
   }

   static final class LookupKey {
      String text;
      float size;
      int fontId;
      float rasterScale;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void set(String text, float size, int fontId, float rasterScale) {
         this.text = textx;
         this.size = sizex;
         this.fontId = fontIdx;
         this.rasterScale = rasterScalex;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o instanceof FrameTextMetricCache.Key var3) {
            return this.fontId == var3.fontId()
               && Float.compare(this.size, var3.size()) == 0
               && Float.compare(this.rasterScale, var3.rasterScale()) == 0
               && this.text.equals(var3.text());
         } else {
            return !(o instanceof FrameTextMetricCache.LookupKey var2)
               ? false
               : this.fontId == var2.fontId
                  && Float.compare(this.size, var2.size) == 0
                  && Float.compare(this.rasterScale, var2.rasterScale) == 0
                  && this.text.equals(var2.text);
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.text.hashCode();
         var1 = (31) * var1 + Float.floatToIntBits(this.size);
         var1 = (31) * var1 + this.fontId;
         return (31) * var1 + Float.floatToIntBits(this.rasterScale);
      }
   }
}
