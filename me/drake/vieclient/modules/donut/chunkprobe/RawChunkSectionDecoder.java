package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.IntPredicate;

public final class RawChunkSectionDecoder {
   private static final int BLOCK_ENTRY_COUNT = 4096;
   private static final int BIOME_ENTRY_COUNT = 64;
   private static final int MAX_BLOCK_LOCAL_BITS = 8;
   private static final int MAX_BIOME_LOCAL_BITS = 3;
   private static final int MAX_SUPPORTED_BITS = 32;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Optional decode(int chunkX, int chunkZ, int bottomSectionY, int sectionCount, byte[] payload, long receivedNanos, IntPredicate isMaskedDeepslate) {
      if (sectionCount >= 0 && payload != null && isMaskedDeepslate != null) {
         try {
            RawChunkSectionDecoder.Cursor var9 = new RawChunkSectionDecoder.Cursor(payload);
            ArrayList var10 = new ArrayList(sectionCount);

            for (int var11 = 0; var11 < sectionCount; var11++) {
               int var12 = var9.readUnsignedShort();
               if (var12 > (4096)) {
                  throw new RawChunkSectionDecoder.DecodeFailure();
               }

               RawChunkSectionDecoder.Container var13 = readContainer(var9, 4096, 8, true);
               readContainer(var9, 64, 3, false);
               boolean var14 = var13.allValuesMatch(isMaskedDeepslate);
               boolean var15 = var12 == (4096) && var13.bits() == 0 && var13.paletteRawIds().length == 1 && var14;
               var10.add(new RawChunkProbe.Section(bottomSectionY + var11, var12, var13.bits(), var13.paletteRawIds(), var13.labels(), var14, var15));
            }

            return var9.remaining() != 0 ? Optional.empty() : Optional.of(new RawChunkProbe(chunkX, chunkZ, payload.length, receivedNanos, var10));
         } catch (RawChunkSectionDecoder.DecodeFailure var16) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private static RawChunkSectionDecoder.Container readContainer(RawChunkSectionDecoder.Cursor cursor, int entryCount, int maxLocalBits, boolean blockContainer) throws RawChunkSectionDecoder.DecodeFailure {
      int var4 = cursor.readUnsignedByte();
      if (var4 > (32)) {
         throw new RawChunkSectionDecoder.DecodeFailure();
      }

      if (blockContainer && var4 > 0 && var4 < (4)) {
         throw new RawChunkSectionDecoder.DecodeFailure();
      }

      int[] var5;
      if (var4 == 0) {
         var5 = new int[]{cursor.readVarInt()};
      } else if (var4 <= maxLocalBits) {
         int var6 = cursor.readVarInt();
         int var7 = 1 << var4;
         if (var6 <= 0 || var6 > var7) {
            throw new RawChunkSectionDecoder.DecodeFailure();
         }

         var5 = new int[var6];

         for (int var8 = 0; var8 < var6; var8++) {
            var5[var8] = cursor.readVarInt();
         }
      } else {
         var5 = new int[0];
      }

      int[] var11 = unpack(cursor, entryCount, var4);
      if (var4 > 0 && var4 <= maxLocalBits) {
         for (int var10 : var11) {
            if (var10 < 0 || var10 >= var5.length) {
               throw new RawChunkSectionDecoder.DecodeFailure();
            }
         }
      }

      return new RawChunkSectionDecoder.Container(var4, var5, var11);
   }

   private static int[] unpack(RawChunkSectionDecoder.Cursor cursor, int entryCount, int bits) throws RawChunkSectionDecoder.DecodeFailure {
      int[] var3 = new int[entryCount];
      int var4 = bits == 0 ? 0 : (64) / bits;
      if (bits > 0 && var4 <= 0) {
         throw new RawChunkSectionDecoder.DecodeFailure();
      }

      int var5 = bits == 0 ? 0 : (entryCount + var4 - 1) / var4;
      if (bits == 0) {
         return var3;
      }

      long[] var6 = new long[var5];

      for (int var7 = 0; var7 < var5; var7++) {
         var6[var7] = cursor.readLong();
      }

      long var12 = bits == (64) ? -1L : (1L << bits) - 1L;

      for (int var9 = 0; var9 < entryCount; var9++) {
         int var10 = var9 / var4;
         int var11 = var9 % var4 * bits;
         var3[var9] = (int)(var6[var10] >>> var11 & var12);
      }

      return var3;
   }

   private record Container(int bits, int[] paletteRawIds, int[] labels) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private Container(int bits, int[] paletteRawIds, int[] labels) {
         paletteRawIds = (int[])paletteRawIds.clone();
         labels = (int[])labels.clone();
         this.bits = bits;
         this.paletteRawIds = paletteRawIds;
         this.labels = labels;
      }

      public int[] paletteRawIds() {
         return (int[])this.paletteRawIds.clone();
      }

      public int[] labels() {
         return (int[])this.labels.clone();
      }

      boolean allValuesMatch(IntPredicate predicate) {
         if (this.bits == 0) {
            return predicate.test(this.paletteRawIds[0]);
         }

         if (this.paletteRawIds.length == 0) {
            return Arrays.stream(this.labels).allMatch(predicate);
         }

         boolean[] var2 = new boolean[this.paletteRawIds.length];

         for (int var6 : this.labels) {
            var2[var6] = true;
         }

         for (int var7 = 0; var7 < var2.length; var7++) {
            if (var2[var7] && !predicate.test(this.paletteRawIds[var7])) {
               return false;
            }
         }

         return true;
      }
   }

   private static final class Cursor {
      private final byte[] data;
      private int index;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private Cursor(byte[] data) {
         this.data = datax;
      }

      int remaining() {
         return this.data.length - this.index;
      }

      int readUnsignedByte() throws RawChunkSectionDecoder.DecodeFailure {
         this.require(1);
         return this.data[this.index++] & (255);
      }

      int readUnsignedShort() throws RawChunkSectionDecoder.DecodeFailure {
         this.require(2);
         int var1 = (this.data[this.index] & (255)) << (8) | this.data[this.index + 1] & (255);
         this.index += 2;
         return var1;
      }

      int readVarInt() throws RawChunkSectionDecoder.DecodeFailure {
         int var1 = 0;

         for (int var2 = 0; var2 < (5); var2++) {
            int var3 = this.readUnsignedByte();
            var1 |= (var3 & (127)) << var2 * (7);
            if ((var3 & (128)) == 0) {
               return var1;
            }
         }

         throw new RawChunkSectionDecoder.DecodeFailure();
      }

      long readLong() throws RawChunkSectionDecoder.DecodeFailure {
         this.require(8);
         long var1 = 0L;

         for (int var3 = 0; var3 < (8); var3++) {
            var1 = var1 << (8) | this.data[this.index + var3] & 255L;
         }

         this.index += 8;
         return var1;
      }

      private void require(int length) throws RawChunkSectionDecoder.DecodeFailure {
         if (length < 0 || this.remaining() < length) {
            throw new RawChunkSectionDecoder.DecodeFailure();
         }
      }
   }

   private static final class DecodeFailure extends Exception {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
