package io.github.kosmx.emotes.server.network;

import com.zigythebird.playeranimcore.animation.Animation;
import it.unimi.dsi.fastutil.Pair;
import java.time.Duration;
import java.time.Instant;
import org.jetbrains.annotations.Nullable;

public class EmotePlayTracker {
   private Animation currentEmote = null;
   private Instant startTime = null;
   private boolean isForced = false;

   public void setPlayedEmote(@Nullable Animation data, boolean isForced) {
      this.currentEmote = data;
      if (data == null) {
         this.startTime = null;
         this.isForced = false;
      } else {
         this.startTime = Instant.now();
         this.isForced = isForced;
      }
   }

   public boolean isForced() {
      return this.getPlayedEmote() != null ? this.isForced : false;
   }

   @Nullable
   public Pair<Animation, Float> getPlayedEmote() {
      if (this.currentEmote == null) {
         return null;
      } else {
         float tick = (float)Duration.between(this.startTime, Instant.now()).toMillis() / 50.0F;
         if (!this.currentEmote.isPlayingAt(tick)) {
            this.currentEmote = null;
            this.startTime = null;
            this.isForced = false;
            return null;
         } else {
            return Pair.of(this.currentEmote, tick);
         }
      }
   }
}
