package me.ayydxn.luminescence.util;

import java.nio.ByteBuffer;

public class ULBuffer implements AutoCloseable {
   private long handle;

   private ULBuffer(long handle) {
      this.handle = handle;
   }

   public static ULBuffer create(ByteBuffer data) {
      if (data != null && data.isDirect()) {
         return new ULBuffer(ULBuffer.NativeMethods.nulCreateBufferFromDirect(data, data.capacity()));
      } else {
         throw new IllegalArgumentException("Buffer must be a non-null direct ByteBuffer!");
      }
   }

   public static ULBuffer createFromCopy(byte[] data) {
      if (data == null) {
         throw new IllegalArgumentException("Cannot create a ULBuffer from null data!");
      } else {
         return new ULBuffer(ULBuffer.NativeMethods.nulCreateBufferFromCopy(data));
      }
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULBuffer.NativeMethods.nulDestroyBuffer(this.handle);
         this.handle = 0L;
      }
   }

   public long getSize() {
      return ULBuffer.NativeMethods.nulBufferGetSize(this.handle);
   }

   public boolean ownsData() {
      return ULBuffer.NativeMethods.nulBufferOwnsData(this.handle);
   }

   public byte[] toByteArray() {
      return ULBuffer.NativeMethods.nulBufferToByteArray(this.handle);
   }

   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateBufferFromCopy(byte[] var0);

      private static native long nulCreateBufferFromDirect(ByteBuffer var0, int var1);

      private static native void nulDestroyBuffer(long var0);

      private static native long nulBufferGetSize(long var0);

      private static native boolean nulBufferOwnsData(long var0);

      private static native byte[] nulBufferToByteArray(long var0);
   }
}
