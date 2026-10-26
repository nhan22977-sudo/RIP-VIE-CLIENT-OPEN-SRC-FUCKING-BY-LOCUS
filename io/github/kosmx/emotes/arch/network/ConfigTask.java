package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import java.util.function.Consumer;
import net.minecraft.class_2596;
import net.minecraft.class_8605;
import net.minecraft.class_8605.class_8606;
import org.jetbrains.annotations.NotNull;

public class ConfigTask implements class_8605 {
   public static final class_8606 TYPE = new class_8606("emotes:config");

   public void method_52376(@NotNull Consumer<class_2596<?>> consumer) {
      NetData configData = new EmotePacket.Builder().configureToConfigExchange().build().data;
      configData.versions.put((byte)-128, (byte)1);

      try {
         EmotePacket packet = new EmotePacket.Builder(configData).build();
         consumer.accept(NetworkPlatformTools.playPacket(packet));
      } catch (Throwable e) {
         CommonData.LOGGER.warn("Failed to configure client!", e);
      }
   }

   @NotNull
   public class_8606 method_52375() {
      return TYPE;
   }
}
