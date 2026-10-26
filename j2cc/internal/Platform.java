package j2cc.internal;

import java.util.Locale;

public final class Platform {
   public static final int UNSPECIFIED = -1;
   public static final int MAC = 0;
   public static final int LINUX = 1;
   public static final int WINDOWS = 2;
   public static final int SOLARIS = 3;
   public static final int FREEBSD = 4;
   public static final int OPENBSD = 5;
   public static final int WINDOWSCE = 6;
   public static final int AIX = 7;
   public static final int ANDROID = 8;
   public static final int GNU = 9;
   public static final int KFREEBSD = 10;
   public static final int NETBSD = 11;
   public static final String RESOURCE_PREFIX;
   public static final int osType;
   public static final String ARCH;
   public static final String JNI_INCLUDE;

   private Platform() {
   }

   static String getCanonicalArchitecture(String var0) {
      var0 = var0.toLowerCase(Locale.ROOT).trim();
      switch (var0) {
         case "i386":
         case "i686":
            var0 = "x86";
            break;
         case "x86_64":
         case "amd64":
            var0 = "x86_64";
            break;
         case "zarch_64":
            var0 = "s390x";
      }

      if ("powerpc64".equals(var0) && "little".equals(System.getProperty("sun.cpu.endian"))) {
         var0 = "powerpc64le";
      }

      return var0;
   }

   static String getNativeLibraryResourcePrefix() {
      return getNativeLibraryResourcePrefix(osType, System.getProperty("os.arch"), System.getProperty("os.name"));
   }

   static String getNativeLibraryResourcePrefix(int var0, String var1, String var2) {
      var1 = getCanonicalArchitecture(var1);
      String var3;
      switch (var0) {
         case 0:
            var3 = var1 + "-macos";
            break;
         case 1:
            var3 = var1 + "-linux";
            break;
         case 2:
         case 6:
            var3 = var1 + "-windows";
            break;
         case 3:
            var3 = var1 + "-sunos";
            break;
         case 4:
            var3 = var1 + "-freebsd";
            break;
         case 5:
            var3 = var1 + "-openbsd";
            break;
         case 7:
         case 9:
         default:
            var3 = var2.toLowerCase(Locale.ROOT);
            int var4 = var3.indexOf(" ");
            if (var4 != -1) {
               var3 = var3.substring(0, var4);
            }

            var3 = var1 + "-" + var3;
            break;
         case 8:
            if (var1.startsWith("arm")) {
               var1 = "arm";
            }

            var3 = var1 + "-android";
            break;
         case 10:
            var3 = var1 + "-kfreebsd";
            break;
         case 11:
            var3 = var1 + "-netbsd";
      }

      return var3;
   }

   static {
      String var0 = System.getProperty("os.name");
      if (var0.startsWith("Linux")) {
         if ("dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"))) {
            osType = 8;
         } else {
            osType = 1;
         }

         JNI_INCLUDE = "linux";
      } else if (var0.startsWith("AIX")) {
         osType = 7;
         JNI_INCLUDE = "linux";
      } else if (var0.startsWith("Mac") || var0.startsWith("Darwin")) {
         osType = 0;
         JNI_INCLUDE = "darwin";
      } else if (var0.startsWith("Windows CE")) {
         osType = 6;
         JNI_INCLUDE = "win32";
      } else if (var0.startsWith("Windows")) {
         osType = 2;
         JNI_INCLUDE = "win32";
      } else if (var0.startsWith("Solaris") || var0.startsWith("SunOS")) {
         osType = 3;
         JNI_INCLUDE = "linux";
      } else if (var0.startsWith("FreeBSD")) {
         osType = 4;
         JNI_INCLUDE = "linux";
      } else if (var0.startsWith("OpenBSD")) {
         osType = 5;
         JNI_INCLUDE = "linux";
      } else if (var0.equalsIgnoreCase("gnu")) {
         osType = 9;
         JNI_INCLUDE = "linux";
      } else if (var0.equalsIgnoreCase("gnu/kfreebsd")) {
         osType = 10;
         JNI_INCLUDE = "linux";
      } else if (var0.equalsIgnoreCase("netbsd")) {
         osType = 11;
         JNI_INCLUDE = "linux";
      } else {
         osType = -1;
         JNI_INCLUDE = "linux";
      }

      ARCH = getCanonicalArchitecture(System.getProperty("os.arch"));
      RESOURCE_PREFIX = getNativeLibraryResourcePrefix();
   }
}
