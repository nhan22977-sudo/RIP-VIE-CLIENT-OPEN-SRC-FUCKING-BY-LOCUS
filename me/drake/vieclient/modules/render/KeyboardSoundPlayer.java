package me.drake.vieclient.modules.render;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class KeyboardSoundPlayer {
   private static final Map BUFFER_CACHE = new ConcurrentHashMap();
   private static final int NUM_SOURCES = 8;
   private static final int[] SOURCES = new int[8];
   private static int sourceIndex = 0;
   private static boolean initialized = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private KeyboardSoundPlayer() {
   }

   private static synchronized void init() {
      try {
         for (int var0 = 0; var0 < (8); var0++) {
            int var1 = AL10.alGenSources();
            if (var1 != 0) {
               AL10.alSourcei(var1, 514, 1);
               AL10.alSourcef(var1, 4129, 0.0F);
               AL10.alSource3f(var1, 4100, 0.0F, 0.0F, 0.0F);
               AL10.alSource3f(var1, 4102, 0.0F, 0.0F, 0.0F);
               SOURCES[var0] = var1;
            }
         }

         initialized = true;
      } catch (Throwable var2) {
         System.err.println("[KeyboardSFX] OpenAL source init warning: " + var2.getMessage());
      }
   }

   public static void play(String relativePath, float volume) {
      if (!(volume <= 0.0F)) {
         if (!initialized) {
            init();
         }

         if (initialized) {
            Integer var2 = BUFFER_CACHE.computeIfAbsent(relativePath, KeyboardSoundPlayer::loadBuffer);
            if (var2 != null && var2 != 0) {
               synchronized (SOURCES) {
                  int var4 = SOURCES[sourceIndex];
                  sourceIndex = (sourceIndex + 1) % (8);
                  if (!AL10.alIsSource(var4)) {
                     initialized = false;
                     init();
                     var4 = SOURCES[sourceIndex];
                  }

                  AL10.alSourceStop(var4);
                  AL10.alSourcei(var4, 514, 1);
                  AL10.alSourcef(var4, 4129, 0.0F);
                  AL10.alSource3f(var4, 4100, 0.0F, 0.0F, 0.0F);
                  AL10.alSourcei(var4, 4105, var2);
                  AL10.alSourcef(var4, 4106, volume);
                  AL10.alSourcePlay(var4);
               }
            }
         }
      }
   }

   private static Integer loadBuffer(String path) {
      try {
         Path var1 = FabricLoader.getInstance().getGameDir();
         byte[] var10001 = new byte[10];
         var10001[0] = (byte)(23);
         var10001[1] = (byte)(-20);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-123);
         var10001[5] = (byte)(53);
         var10001[6] = (byte)(-88);
         var10001[7] = 0;
         var10001[8] = (byte)(63);
         var10001[9] = (byte)(-42);
         Path var10000 = var1.resolve($$v_s(var10001, 5603189729743065116L));
         var10001 = new byte[3];
         var10001[0] = (byte)(74);
         var10001[1] = (byte)(-4);
         var10001[2] = (byte)(100);
         Path var2 = var10000.resolve($$v_s(var10001, 5603189729743065116L)).resolve(path);
         if (Files.exists(var2) && Files.size(var2) > 0L) {
            return decodeFileToAlBuffer(var2);
         }
      } catch (Throwable var5) {
      }

      try (InputStream var8 = KeyboardSoundPlayer.class.getClassLoader().getResourceAsStream(path)) {
         if (var8 != null) {
            byte[] var9 = var8.readAllBytes();
            return decodeMemoryToAlBuffer(var9);
         }
      } catch (Throwable var7) {
      }

      return 0;
   }

   private static Integer decodeFileToAlBuffer(Path file) {
      try {
         MemoryStack var1 = MemoryStack.stackPush();

         Integer var9;
         label48: {
            try {
               IntBuffer var2 = var1.mallocInt(1);
               IntBuffer var3 = var1.mallocInt(1);
               ShortBuffer var4 = STBVorbis.stb_vorbis_decode_filename(file.toAbsolutePath().toString(), var2, var3);
               if (var4 == null) {
                  var9 = 0;
                  break label48;
               }

               var9 = createAlBuffer(var4, var2.get(0), var3.get(0));
            } catch (Throwable var7) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (var1 != null) {
               var1.close();
            }

            return var9;
         }

         if (var1 != null) {
            var1.close();
         }

         return var9;
      } catch (Throwable var8) {
         return 0;
      }
   }

   private static Integer decodeMemoryToAlBuffer(byte[] bytes) {
      ByteBuffer var1 = MemoryUtil.memAlloc(bytes.length);

      Integer var16;
      try {
         MemoryStack var2 = MemoryStack.stackPush();

         label105: {
            try {
               var1.put(bytes).flip();
               IntBuffer var3 = var2.mallocInt(1);
               IntBuffer var4 = var2.mallocInt(1);
               ShortBuffer var5 = STBVorbis.stb_vorbis_decode_memory(var1, var3, var4);
               if (var5 == null) {
                  var16 = 0;
                  break label105;
               }

               var16 = createAlBuffer(var5, var3.get(0), var4.get(0));
            } catch (Throwable var13) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (var2 != null) {
               var2.close();
            }

            return var16;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (Throwable var14) {
         return 0;
      } finally {
         MemoryUtil.memFree(var1);
      }

      return var16;
   }

   private static int createAlBuffer(ShortBuffer pcm, int channels, int sampleRate) {
      int var3 = channels == 1 ? 4353 : 4355;
      int var4 = AL10.alGenBuffers();
      AL10.alBufferData(var4, var3, pcm, sampleRate);
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
}
