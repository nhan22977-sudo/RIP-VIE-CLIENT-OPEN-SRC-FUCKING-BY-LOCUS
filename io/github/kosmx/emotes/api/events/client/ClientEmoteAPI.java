package io.github.kosmx.emotes.api.events.client;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.api.services.IEmotecraftService;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import java.util.Collection;
import org.jetbrains.annotations.Nullable;

public abstract class ClientEmoteAPI implements IEmotecraftService {
   protected static final ClientEmoteAPI INSTANCE = ServiceLoaderUtil.loadService(ClientEmoteAPI.class);

   public static boolean stopEmote() {
      return playEmote(null);
   }

   public static boolean playEmote(@Nullable Animation animation) {
      return playEmote(animation, 0.0F);
   }

   public static boolean playEmote(@Nullable Animation animation, float tick) {
      return INSTANCE.playEmoteImpl(animation, tick);
   }

   public static Collection<Animation> clientEmoteList() {
      return INSTANCE.clientEmoteListImpl();
   }

   protected abstract boolean playEmoteImpl(Animation var1, float var2);

   protected abstract Collection<Animation> clientEmoteListImpl();

   @Override
   public boolean isActive() {
      return true;
   }
}
