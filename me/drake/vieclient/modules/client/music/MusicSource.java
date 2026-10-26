package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;

public enum MusicSource {
   SPOTIFY_API,
   APPLE_MUSIC_API,
   SOUNDCLOUD_APP,
   PROCESS,
   YOUTUBE_BROWSER;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static MusicSource[] $values() {
      MusicSource[] var10000 = new MusicSource[5];
      var10000[0] = SPOTIFY_API;
      var10000[1] = APPLE_MUSIC_API;
      var10000[2] = SOUNDCLOUD_APP;
      var10000[3] = PROCESS;
      var10000[4] = YOUTUBE_BROWSER;
      return var10000;
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
