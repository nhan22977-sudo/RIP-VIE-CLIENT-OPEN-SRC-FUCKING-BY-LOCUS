package io.github.kosmx.emotes.api.events.client;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.event.Event;
import com.zigythebird.playeranimcore.event.EventResult;
import java.util.UUID;

public final class ClientEmoteEvents {
   public static final Event<ClientEmoteEvents.EmoteVerifier> EMOTE_VERIFICATION = new Event<>(listeners -> (emote, userID) -> {
      for (ClientEmoteEvents.EmoteVerifier listener : listeners) {
         EventResult result = listener.verify(emote, userID);
         if (result == EventResult.FAIL || result == EventResult.SUCCESS) {
            return result;
         }
      }

      return EventResult.PASS;
   });
   public static final Event<ClientEmoteEvents.EmotePlayEvent> EMOTE_PLAY = new Event<>(listeners -> (emote, tick, userID) -> {
      for (ClientEmoteEvents.EmotePlayEvent listener : listeners) {
         listener.onEmotePlay(emote, tick, userID);
      }
   });
   public static final Event<ClientEmoteEvents.EmoteStopEvent> EMOTE_STOP = new Event<>(listeners -> (emote, userID) -> {
      for (ClientEmoteEvents.EmoteStopEvent listener : listeners) {
         listener.onEmoteStop(emote, userID);
      }
   });
   public static final Event<ClientEmoteEvents.LocalEmoteStopEvent> LOCAL_EMOTE_STOP = new Event<>(listeners -> () -> {
      for (ClientEmoteEvents.LocalEmoteStopEvent listener : listeners) {
         listener.onEmoteStop();
      }
   });

   @FunctionalInterface
   public interface EmotePlayEvent {
      void onEmotePlay(Animation var1, float var2, UUID var3);
   }

   @FunctionalInterface
   public interface EmoteStopEvent {
      void onEmoteStop(UUID var1, UUID var2);
   }

   @FunctionalInterface
   public interface EmoteVerifier {
      EventResult verify(Animation var1, UUID var2);
   }

   @FunctionalInterface
   public interface LocalEmoteStopEvent {
      void onEmoteStop();
   }
}
