package me.ayydxn.luminescence;

import org.jetbrains.annotations.NotNull;

public final class Ultralight {
   private static native String nulVersionString();

   private static native int nulVersionMajor();

   private static native int nulVersionMinor();

   private static native int nulVersionPatch();

   private static native String nulWebKitVersionString();

   private static native int nulWebKitVersionMajor();

   private static native int nulWebKitVersionMinor();

   private static native int nulWebKitVersionTiny();

   private static native int nulWebKitVersionMicro();

   private static native int nulWebKitVersionNano();

   @NotNull
   public static String ulVersionString() {
      return nulVersionString();
   }

   public static int ulVersionMajor() {
      return nulVersionMajor();
   }

   public static int ulVersionMinor() {
      return nulVersionMinor();
   }

   public static int ulVersionPatch() {
      return nulVersionPatch();
   }

   @NotNull
   public static String ulWebKitVersionString() {
      return nulWebKitVersionString();
   }

   public static int ulWebKitVersionMajor() {
      return nulWebKitVersionMajor();
   }

   public static int ulWebKitVersionMinor() {
      return nulWebKitVersionMinor();
   }

   public static int ulWebKitVersionTiny() {
      return nulWebKitVersionTiny();
   }

   public static int ulWebKitVersionMicro() {
      return nulWebKitVersionMicro();
   }

   public static int ulWebKitVersionNano() {
      return nulWebKitVersionNano();
   }
}
