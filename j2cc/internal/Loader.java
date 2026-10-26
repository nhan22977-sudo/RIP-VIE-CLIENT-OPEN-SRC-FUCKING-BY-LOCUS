package j2cc.internal;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class Loader {
   private static final AtomicBoolean loaded = new AtomicBoolean(false);

   public static void init() {
      if (loaded.compareAndSet(false, true)) {
         String var0 = Platform.RESOURCE_PREFIX;
         String var10000 = "j2cc loader v1.0.1%nPlatform: %s";
         ClassLoader var1 = Loader.class.getClassLoader();

         try {
            InputStream var2 = var1.getResourceAsStream("j2cc/relocationInfo.dat");

            try {
               DataInputStream var3 = new DataInputStream(Objects.requireNonNull(var2));

               try {
                  InputStream var4 = Objects.requireNonNull(var1.getResourceAsStream("j2cc/natives.bin"));

                  try {
                     while (!var3.readUTF().equals(var0)) {
                        var3.skipBytes(64);
                     }

                     long var5 = var3.readLong();
                     long var7 = var3.readLong();
                     byte[] var9 = new byte[48];
                     var3.readFully(var9);
                     long var10 = 0L;

                     long var12;
                     while (var10 < var5 && (var12 = (int)var4.skip(var5 - var10)) > 0L) {
                        var10 += var12;
                     }

                     if (var10 != var5) {
                        throw new IOException("Failed to read natives file");
                     }

                     Path var14 = Files.createTempFile("j2cc", Platform.osType == 2 ? ".dll" : "");
                     var14.toFile().deleteOnExit();
                     OutputStream var15 = Files.newOutputStream(var14);

                     try {
                        long var16 = 0L;
                        short var18 = 8192;
                        byte[] var19 = new byte[8192];

                        while (var16 < var7) {
                           long var20 = var7 - var16;
                           int var22 = (int)Math.min(8192L, var20);
                           int var23 = var4.read(var19, 0, var22);
                           var15.write(var19, 0, var23);
                           var16 += var23;
                        }
                     } catch (Throwable var28) {
                        if (var15 != null) {
                           try {
                              var15.close();
                           } catch (Throwable var27) {
                              var28.addSuppressed(var27);
                           }
                        }

                        throw var28;
                     }

                     if (var15 != null) {
                        var15.close();
                     }

                     String var34 = var14.toString();
                     var10000 = "Loading library from %s...";
                     System.load(var34);
                     bootstrap(var9);
                  } catch (Throwable var29) {
                     if (var4 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var26) {
                           var29.addSuppressed(var26);
                        }
                     }

                     throw var29;
                  }

                  if (var4 != null) {
                     var4.close();
                  }
               } catch (Throwable var30) {
                  try {
                     var3.close();
                  } catch (Throwable var25) {
                     var30.addSuppressed(var25);
                  }

                  throw var30;
               }

               var3.close();
            } catch (Throwable var31) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var24) {
                     var31.addSuppressed(var24);
                  }
               }

               throw var31;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (EOFException var32) {
            System.err.println("Your platform (" + var0 + ") isn't supported by this application. Please contact the developers to find out more.");
            System.exit(127);
         } catch (IOException var33) {
            throw new Error(var33);
         }
      }
   }

   private static native void bootstrap(byte[] var0);

   private static native void initClass(Class<?> var0);

   public static void doInit(Class<?> var0) {
      init();
      initClass(var0);
   }
}
