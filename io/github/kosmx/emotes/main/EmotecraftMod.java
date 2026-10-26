package io.github.kosmx.emotes.main;

import io.github.kosmx.emotes.arch.network.CommonServerNetworkHandler;
import io.github.kosmx.emotes.common.SerializableConfig;
import io.github.kosmx.emotes.main.config.ClientConfig;
import io.github.kosmx.emotes.main.config.ClientConfigSerializer;
import io.github.kosmx.emotes.server.config.ConfigSerializer;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public abstract class EmotecraftMod {
   protected void onInitialize(boolean isClient) {
      if (isClient) {
         Serializer.INSTANCE = new Serializer<>(new ClientConfigSerializer(), ClientConfig.class);
      } else {
         Serializer.INSTANCE = new Serializer<>(new ConfigSerializer<>(SerializableConfig::new), SerializableConfig.class);
         UniversalEmoteSerializer.loadEmotes();
      }
   }

   protected void onStartTracking(class_1297 entity, class_1657 player) {
      if (entity instanceof class_3222 tracked && player instanceof class_3222 tracker) {
         CommonServerNetworkHandler.getInstance()
            .playerStartTracking(
               CommonServerNetworkHandler.getInstance().getPlayerNetworkInstance(tracked),
               CommonServerNetworkHandler.getInstance().getPlayerNetworkInstance(tracker)
            );
      }
   }
}
