package io.github.kosmx.emotes.api.events.server;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.event.Event;
import com.zigythebird.playeranimcore.event.EventResult;
import java.util.UUID;

public final class ServerEmoteEvents {
   public static final Event<ServerEmoteEvents.EmoteVerifier> EMOTE_VERIFICATION = new Event<>(listeners -> (emote, userID) -> {
      for (ServerEmoteEvents.EmoteVerifier listener : listeners) {
         EventResult result = listener.verify(emote, userID);
         if (result == EventResult.FAIL || result == EventResult.SUCCESS) {
            return result;
         }
      }

      return EventResult.PASS;
   });
   public static final Event<ServerEmoteEvents.EmotePlayEvent> EMOTE_PLAY = new Event<>(listeners -> (emote, tick, userID) -> {
      for (ServerEmoteEvents.EmotePlayEvent listener : listeners) {
         listener.onEmotePlay(emote, tick, userID);
      }
   });
   public static final Event<ServerEmoteEvents.EmoteStopEvent> EMOTE_STOP_BY_USER = new Event<>(listeners -> (emoteID, userID) -> {
      for (ServerEmoteEvents.EmoteStopEvent listener : listeners) {
         listener.onStopEmote(emoteID, userID);
      }
   });

   @FunctionalInterface
   public interface EmotePlayEvent {
      void onEmotePlay(Animation var1, float var2, UUID var3);
   }

   @FunctionalInterface
   public interface EmoteStopEvent {
      void onStopEmote(UUID var1, UUID var2);
   }

   @FunctionalInterface
   public interface EmoteVerifier {
      EventResult verify(Animation var1, UUID var2);
   }
}
