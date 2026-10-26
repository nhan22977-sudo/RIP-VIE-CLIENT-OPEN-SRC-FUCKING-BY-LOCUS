package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketBound;
import net.minecraft.class_2540;
import net.minecraft.class_8710;
import net.minecraft.class_9139;
import net.minecraft.class_8710.class_9154;
import org.jetbrains.annotations.NotNull;

public record EmotePacketPayload(@NotNull class_9154<?> id, @NotNull EmotePacket packet) implements class_8710 {
   public static final class_9139<class_2540, EmotePacketPayload> EMOTE_CHANNEL_READER_S2C = reader(NetworkPlatformTools.EMOTE_CHANNEL_ID, PacketBound.CLIENT);
   public static final class_9139<class_2540, EmotePacketPayload> EMOTE_CHANNEL_READER_C2S = reader(NetworkPlatformTools.EMOTE_CHANNEL_ID, PacketBound.SERVER);
   public static final class_9139<class_2540, EmotePacketPayload> STREAM_CHANNEL_READER_S2C = reader(NetworkPlatformTools.STREAM_CHANNEL_ID, PacketBound.CLIENT);
   public static final class_9139<class_2540, EmotePacketPayload> STREAM_CHANNEL_READER_C2S = reader(NetworkPlatformTools.STREAM_CHANNEL_ID, PacketBound.SERVER);

   @NotNull
   public class_9154<? extends class_8710> method_56479() {
      return (class_9154<? extends class_8710>)this.id;
   }

   @NotNull
   public static class_8710 playPacket(@NotNull EmotePacket packet) {
      return new EmotePacketPayload(NetworkPlatformTools.EMOTE_CHANNEL_ID, packet);
   }

   @NotNull
   public static class_8710 streamPacket(@NotNull EmotePacket packet) {
      return new EmotePacketPayload(NetworkPlatformTools.STREAM_CHANNEL_ID, packet);
   }

   @NotNull
   public static class_9139<class_2540, EmotePacketPayload> reader(@NotNull class_9154<?> channel, PacketBound bound) {
      return class_8710.method_56484((payload, buf) -> payload.packet().write(buf, bound), buf -> new EmotePacketPayload(channel, new EmotePacket(buf, bound)));
   }
}
