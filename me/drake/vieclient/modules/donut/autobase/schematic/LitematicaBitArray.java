package me.drake.vieclient.modules.donut.autobase.schematic;

public final class LitematicaBitArray {
   private final long[] longArray;
   private final int bitsPerEntry;
   private final long maxEntryValue;
   private final long size;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static int calculateBitsPerEntry(int paletteSize) {
      if (paletteSize <= 1) {
         return 2;
      }

      int var1 = (32) - Integer.numberOfLeadingZeros(paletteSize - 1);
      return Math.max(2, var1);
   }

   public LitematicaBitArray(int bitsPerEntry, long size) {
      this(bitsPerEntryx, sizex, null);
   }

   public LitematicaBitArray(int bitsPerEntry, long size, long[] backingArray) {
      if (bitsPerEntryx < 1 || bitsPerEntryx > (32)) {
         throw new IllegalArgumentException("bitsPerEntry must be between 1 and 32, was " + bitsPerEntryx);
      }

      if (sizex < 0L) {
         throw new IllegalArgumentException("size cannot be negative: " + sizex);
      }

      this.bitsPerEntry = bitsPerEntryx;
      this.size = sizex;
      this.maxEntryValue = (1L << bitsPerEntryx) - 1L;
      if (backingArray != null) {
         this.longArray = backingArray;
      } else {
         long var5 = sizex * bitsPerEntryx;
         int var7 = (int)((var5 + 63L) / 64L);
         this.longArray = new long[var7];
      }
   }

   public void setAt(long index, int value) {
      if (index >= 0L && index < this.size) {
         long var4 = index * this.bitsPerEntry;
         int var6 = (int)(var4 >> (6));
         int var7 = (int)((index + 1L) * this.bitsPerEntry - 1L >> (6));
         int var8 = (int)(var4 & 63L);
         long var9 = value & this.maxEntryValue;
         this.longArray[var6] = this.longArray[var6] & ~(this.maxEntryValue << var8) | var9 << var8;
         if (var6 != var7) {
            int var11 = (64) - var8;
            int var12 = this.bitsPerEntry - var11;
            this.longArray[var7] = this.longArray[var7] >>> var12 << var12 | var9 >> var11;
         }
      } else {
         throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + this.size);
      }
   }

   public int getAt(long index) {
      if (index >= 0L && index < this.size) {
         long var3 = index * this.bitsPerEntry;
         int var5 = (int)(var3 >> (6));
         int var6 = (int)((index + 1L) * this.bitsPerEntry - 1L >> (6));
         int var7 = (int)(var3 & 63L);
         if (var5 >= this.longArray.length) {
            return 0;
         }

         if (var5 == var6) {
            return (int)(this.longArray[var5] >>> var7 & this.maxEntryValue);
         }

         int var8 = (64) - var7;
         long var9 = this.longArray[var5] >>> var7;
         long var11 = var6 < this.longArray.length ? this.longArray[var6] << var8 : 0L;
         return (int)((var9 | var11) & this.maxEntryValue);
      } else {
         throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + this.size);
      }
   }

   public long size() {
      return this.size;
   }

   public int getBitsPerEntry() {
      return this.bitsPerEntry;
   }

   public long[] getBackingLongArray() {
      return this.longArray;
   }
}
