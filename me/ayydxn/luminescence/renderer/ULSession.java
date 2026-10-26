package me.ayydxn.luminescence.renderer;

import java.nio.file.Path;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULSession {
   private final boolean isDefault;
   private long handle;

   private ULSession(long handle, boolean isDefault) {
      this.handle = handle;
      this.isDefault = isDefault;
   }

   public static ULSession create(ULRenderer renderer, boolean isPersistent, String name) {
      long handle = ULSession.NativeMethods.nulCreateSession(renderer.getHandle(), isPersistent, name);
      return new ULSession(handle, false);
   }

   public static ULSession createDefault(ULRenderer renderer) {
      long handle = ULSession.NativeMethods.nulDefaultSession(renderer.getHandle());
      return new ULSession(handle, true);
   }

   public void destroy() {
      if (this.isDefault) {
         throw new UnsupportedOperationException("Cannot destroy default ULSessions as they are owned by the renderer!");
      }

      if (this.handle != 0L) {
         ULSession.NativeMethods.nulDestroySession(this.handle);
         this.handle = 0L;
      }
   }

   public boolean isPersistent() {
      return ULSession.NativeMethods.nulSessionIsPersistent(this.handle);
   }

   public String getName() {
      return ULSession.NativeMethods.nulSessionGetName(this.handle);
   }

   public long getID() {
      return ULSession.NativeMethods.nulSessionGetID(this.handle);
   }

   public Path getDiskPath() {
      String diskPathString = ULSession.NativeMethods.nulSessionGetDiskPath(this.handle);
      return Path.of(diskPathString);
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateSession(long var0, boolean var2, String var3);

      private static native void nulDestroySession(long var0);

      private static native long nulDefaultSession(long var0);

      private static native boolean nulSessionIsPersistent(long var0);

      private static native String nulSessionGetName(long var0);

      private static native long nulSessionGetID(long var0);

      private static native String nulSessionGetDiskPath(long var0);
   }
}
