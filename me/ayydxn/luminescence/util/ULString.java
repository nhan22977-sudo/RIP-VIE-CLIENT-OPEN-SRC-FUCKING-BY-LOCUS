package me.ayydxn.luminescence.util;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULString implements AutoCloseable {
   private long handle;

   private ULString(long handle) {
      this.handle = handle;
   }

   public static ULString create(String string) {
      if (string == null) {
         throw new IllegalArgumentException("Cannot create a ULString with a null Java string!");
      } else {
         return new ULString(ULString.NativeMethods.nulCreateString(string));
      }
   }

   public static ULString createUTF8(String string) {
      return new ULString(ULString.NativeMethods.nulCreateStringUTF8(string));
   }

   public static ULString createUTF16(String string) {
      return new ULString(ULString.NativeMethods.nulCreateStringUTF16(string));
   }

   public static ULString createFromCopy(ULString otherString) {
      return new ULString(ULString.NativeMethods.nulCreateStringFromCopy(otherString.handle));
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULString.NativeMethods.nulDestroyString(this.handle);
         this.handle = 0L;
      }
   }

   public String getData() {
      return ULString.NativeMethods.nulStringGetData(this.handle);
   }

   public long getLength() {
      return this.getData().length();
   }

   public long getByteLength() {
      return ULString.NativeMethods.nulStringGetLength(this.handle);
   }

   public boolean isEmpty() {
      return ULString.NativeMethods.nulStringIsEmpty(this.handle);
   }

   public void assignString(ULString otherString) {
      ULString.NativeMethods.nulStringAssignString(this.handle, otherString.handle);
   }

   public void assignCString(String string) {
      ULString.NativeMethods.nulAssignCString(this.handle, string);
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateString(String var0);

      private static native long nulCreateStringUTF8(String var0);

      private static native long nulCreateStringUTF16(String var0);

      private static native long nulCreateStringFromCopy(long var0);

      private static native void nulDestroyString(long var0);

      private static native String nulStringGetData(long var0);

      private static native long nulStringGetLength(long var0);

      private static native boolean nulStringIsEmpty(long var0);

      private static native void nulStringAssignString(long var0, long var2);

      private static native void nulAssignCString(long var0, String var2);
   }
}
