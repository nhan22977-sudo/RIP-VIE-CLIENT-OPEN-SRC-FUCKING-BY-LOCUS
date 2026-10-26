package io.github.kosmx.emotes.api.events.server;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.api.services.IEmotecraftService;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import it.unimi.dsi.fastutil.Pair;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public abstract class ServerEmoteAPI implements IEmotecraftService {
   protected static final ServerEmoteAPI INSTANCE = ServiceLoaderUtil.loadService(ServerEmoteAPI.class);

   public static void setPlayerPlayingEmote(UUID player, @Nullable Animation emote) {
      setPlayerPlayingEmote(player, emote, 0.0F);
   }

   public static void setPlayerPlayingEmote(UUID player, @Nullable Animation emote, float tick) {
      INSTANCE.setPlayerPlayingEmoteImpl(player, emote, tick, false);
   }

   public static void forcePlayEmote(UUID player, @Nullable Animation emote) {
      forcePlayEmote(player, emote, 0.0F);
   }

   public static void forcePlayEmote(UUID player, @Nullable Animation emote, float tick) {
      INSTANCE.setPlayerPlayingEmoteImpl(player, emote, tick, true);
   }

   public static void playEmote(UUID player, @Nullable Animation emote, boolean forced) {
      playEmote(player, emote, 0.0F, forced);
   }

   public static void playEmote(UUID player, @Nullable Animation emote, float tick, boolean forced) {
      INSTANCE.setPlayerPlayingEmoteImpl(player, emote, tick, forced);
   }

   @Nullable
   public static Pair<Animation, Float> getPlayedEmote(UUID player) {
      return INSTANCE.getPlayedEmoteImpl(player);
   }

   public static boolean isForcedEmote(UUID player) {
      return INSTANCE.isForcedEmoteImpl(player);
   }

   protected abstract void setPlayerPlayingEmoteImpl(UUID var1, @Nullable Animation var2, float var3, boolean var4);

   protected abstract Pair<Animation, Float> getPlayedEmoteImpl(UUID var1);

   protected abstract boolean isForcedEmoteImpl(UUID var1);

   @Override
   public boolean isActive() {
      return true;
   }
}
