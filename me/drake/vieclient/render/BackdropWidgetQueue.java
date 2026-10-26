package me.drake.vieclient.render;

import java.nio.charset.StandardCharsets;

final class BackdropWidgetQueue {
   static final int X = 0;
   static final int Y = 1;
   static final int WIDTH = 2;
   static final int HEIGHT = 3;
   static final int RADIUS = 4;
   static final int SQUIRCLE = 5;
   static final int HOVER = 6;
   static final int FOCUS = 7;
   private static final int STRIDE = 8;
   private final float[] values;
   private int size;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   BackdropWidgetQueue(int capacity) {
      if (capacity < 1) {
         byte[] var10002 = new byte[25];
         var10002[0] = (byte)(90);
         var10002[1] = (byte)(-5);
         var10002[2] = (byte)(108);
         var10002[3] = (byte)(-65);
         var10002[4] = (byte)(-113);
         var10002[5] = (byte)(52);
         var10002[6] = (byte)(-49);
         var10002[7] = (byte)(-90);
         var10002[8] = (byte)(113);
         var10002[9] = (byte)(-49);
         var10002[10] = (byte)(-111);
         var10002[11] = (byte)(-123);
         var10002[12] = (byte)(-64);
         var10002[13] = (byte)(-91);
         var10002[14] = (byte)(-15);
         var10002[15] = (byte)(-62);
         var10002[16] = (byte)(73);
         var10002[17] = (byte)(58);
         var10002[18] = (byte)(-93);
         var10002[19] = (byte)(-35);
         var10002[20] = (byte)(53);
         var10002[21] = (byte)(-39);
         var10002[22] = (byte)(-126);
         var10002[23] = (byte)(-7);
         var10002[24] = (byte)(-28);
         throw new IllegalArgumentException($$v_s(var10002, -596582548282028004L));
      }

      this.values = new float[capacity * (8)];
   }

   boolean offer(float x, float y, float width, float height, float radius, float squircle, float hover, float focus) {
      if (!(width < 1.0F) && !(height < 1.0F) && this.size < this.capacity()) {
         int var9 = this.size++ * (8);
         this.values[var9 + 0] = x;
         this.values[var9 + 1] = y;
         this.values[var9 + (2)] = width;
         this.values[var9 + (3)] = height;
         this.values[var9 + (4)] = Math.max(0.0F, radius);
         this.values[var9 + (5)] = squircle;
         this.values[var9 + (6)] = hover;
         this.values[var9 + (7)] = focus;
         return true;
      } else {
         return false;
      }
   }

   int size() {
      return this.size;
   }

   float value(int widget, int field) {
      if (widget >= 0 && widget < this.size && field >= 0 && field < (8)) {
         return this.values[widget * (8) + field];
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   void truncate(int newSize) {
      if (newSize >= 0 && newSize <= this.size) {
         this.size = newSize;
      } else {
         byte[] var10002 = new byte[12];
         var10002[0] = (byte)(80);
         var10002[1] = (byte)(-12);
         var10002[2] = (byte)(106);
         var10002[3] = (byte)(-65);
         var10002[4] = (byte)(-128);
         var10002[5] = (byte)(52);
         var10002[6] = (byte)(-33);
         var10002[7] = (byte)(-1);
         var10002[8] = (byte)(34);
         var10002[9] = (byte)(-53);
         var10002[10] = (byte)(-98);
         var10002[11] = (byte)(-109);
         throw new IllegalArgumentException($$v_s(var10002, -596582548282028004L));
      }
   }

   void clear() {
      this.size = 0;
   }

   private int capacity() {
      return this.values.length / (8);
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
