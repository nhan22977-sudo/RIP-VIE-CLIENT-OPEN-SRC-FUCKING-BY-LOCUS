package me.drake.vieclient.modules.client.music;

import com.tagtraum.japlscript.language.Picture;
import com.tagtraum.macos.music.Application;
import com.tagtraum.macos.music.Artwork;
import com.tagtraum.macos.music.Epls;
import com.tagtraum.macos.music.Track;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

public final class AppleMusicProvider implements MusicProvider {
   private final Application application;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AppleMusicProvider() {
      this(createApplication());
   }

   AppleMusicProvider(Application application) {
      this.application = applicationx;
   }

   static boolean isSupportedOs(String osName) {
      if (osName != null) {
         String var10000 = osName.toLowerCase(Locale.ROOT);
         byte[] var10001 = new byte[3];
         var10001[0] = (byte)(85);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(127);
         if (var10000.contains($$v_s(var10001, -8034486695360212963L))) {
            return true;
         }
      }

      return false;
   }

   private static Application createApplication() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(87);
      var10000[1] = (byte)(-23);
      var10000[2] = (byte)(50);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(51);
      var10000[5] = (byte)(119);
      var10000[6] = (byte)(25);
      if (!isSupportedOs(System.getProperty($$v_s(var10000, -8034486695360212963L), ""))) {
         return null;
      }

      try {
         return Application.getInstance();
      } catch (Throwable var1) {
         return null;
      }
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[15];
      var10000[0] = (byte)(89);
      var10000[1] = (byte)(-22);
      var10000[2] = (byte)(108);
      var10000[3] = (byte)(-78);
      var10000[4] = (byte)(55);
      var10000[5] = (byte)(55);
      var10000[6] = (byte)(17);
      var10000[7] = (byte)(-51);
      var10000[8] = (byte)(35);
      var10000[9] = (byte)(-53);
      var10000[10] = (byte)(-121);
      var10000[11] = (byte)(-37);
      var10000[12] = (byte)(107);
      var10000[13] = (byte)(-78);
      var10000[14] = (byte)(61);
      return $$v_s(var10000, -8034486695360212963L);
   }

   @Override
   public Optional poll() {
      if (this.application == null) {
         return Optional.empty();
      }

      try {
         Track var1 = this.application.getCurrentTrack();
         if (var1 != null && var1.getName() != null && !var1.getName().isBlank()) {
            Epls var2 = this.application.getPlayerState();
            if (!hasCurrentTrack(var2)) {
               return Optional.empty();
            }

            String var3 = var1.getPersistentId();
            String var4 = var3 != null && !var3.isBlank() ? "apple:" + var3 : "apple:" + var1.getName() + ":" + var1.getArtist();
            return Optional.of(
               new MusicSnapshot(
                  MusicSource.APPLE_MUSIC_API,
                  var1.getName(),
                  var1.getArtist(),
                  secondsToMillis(var1.getDuration()),
                  secondsToMillis(this.application.getPlayerPosition()),
                  var2 == Epls.PLAYING,
                  "",
                  var4,
                  readArtwork(var1)
               )
            );
         } else {
            return Optional.empty();
         }
      } catch (Throwable var5) {
         return Optional.empty();
      }
   }

   static boolean hasCurrentTrack(Epls state) {
      return state != null && state != Epls.STOPPED;
   }

   private static int secondsToMillis(double seconds) {
      return Double.isFinite(seconds) && !(seconds <= 0.0) ? (int)Math.min(2147483647L, Math.round(seconds * 1000.0)) : 0;
   }

   private static BufferedImage readArtwork(Track track) {
      try {
         Artwork[] var1 = track.getArtworks();
         if (var1 != null && var1.length != 0) {
            Picture var2 = var1[0].getData();
            if (var2 == null) {
               return null;
            } else {
               Image var3 = var2.getImage();
               if (var3 == null) {
                  return null;
               } else if (var3 instanceof BufferedImage var13) {
                  return var13;
               } else {
                  int var4 = var3.getWidth(null);
                  int var5 = var3.getHeight(null);
                  if (var4 > 0 && var5 > 0) {
                     BufferedImage var6 = new BufferedImage(var4, var5, 2);
                     Graphics2D var7 = var6.createGraphics();

                     try {
                        var7.drawImage(var3, 0, 0, null);
                     } finally {
                        var7.dispose();
                     }

                     return var6;
                  } else {
                     return null;
                  }
               }
            }
         } else {
            return null;
         }
      } catch (Throwable var12) {
         return null;
      }
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
