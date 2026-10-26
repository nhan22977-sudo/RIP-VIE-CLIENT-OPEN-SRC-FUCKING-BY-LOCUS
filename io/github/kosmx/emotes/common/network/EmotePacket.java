package io.github.kosmx.emotes.common.network;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.objects.AbstractNetworkPacket;
import io.github.kosmx.emotes.common.network.objects.DiscoveryPacket;
import io.github.kosmx.emotes.common.network.objects.EmoteDataPacket;
import io.github.kosmx.emotes.common.network.objects.EmoteHeaderPacket;
import io.github.kosmx.emotes.common.network.objects.EmoteIconPacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.common.network.objects.NetHashMap;
import io.github.kosmx.emotes.common.network.objects.NewAnimPacket;
import io.github.kosmx.emotes.common.network.objects.PlayerDataPacket;
import io.github.kosmx.emotes.common.network.objects.SongPacket;
import io.github.kosmx.emotes.common.network.objects.StopPacket;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMap;
import it.unimi.dsi.fastutil.bytes.Byte2ByteMaps;
import it.unimi.dsi.fastutil.bytes.Byte2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EmotePacket {
   public static final Byte2ByteMap defaultVersions;
   private static final NetHashMap SUB_PACKETS;
   public final NetData data;

   private EmotePacket(@NotNull NetData data) {
      if (data.versions.isEmpty()) {
         data.versions.putAll(defaultVersions);
      }

      this.data = data;
   }

   public EmotePacket(@NotNull ByteBuf byteBuf, PacketBound target) {
      if (byteBuf.readableBytes() < 6) {
         throw new RuntimeException("Invalid packet header");
      }

      if (byteBuf.readInt() > 8) {
         throw new RuntimeException("Can't read newer version");
      }

      this.data = new NetData();
      this.data.purpose = PacketTask.getTaskFromID(byteBuf.readByte());
      short count = byteBuf.readUnsignedByte();

      for (int i = 0; i < count; i++) {
         if (byteBuf.readableBytes() < 6) {
            throw new RuntimeException("Invalid sub-packet header");
         }

         AbstractNetworkPacket packet = (AbstractNetworkPacket)SUB_PACKETS.get(byteBuf.readByte());
         byte subVersion = byteBuf.readByte();
         int size = byteBuf.readInt();
         int currentPos = byteBuf.readerIndex();
         if (size < 0 || size > byteBuf.readableBytes()) {
            throw new RuntimeException("Invalid sub-packet size: " + size);
         }

         if (packet != null && packet.boundsTo().contains(target)) {
            try {
               packet.read(byteBuf.slice(currentPos, size), this.data, subVersion);
            } catch (Throwable th) {
               if (!packet.isOptional()) {
                  throw new RuntimeException("Invalid " + packet + " sub-packet received", th);
               }

               CommonData.LOGGER.warn("Invalid {} sub-packet received!", packet, th);
            }
         }

         byteBuf.readerIndex(currentPos + size);
      }

      if (!this.data.prepareAndValidate()) {
         throw new RuntimeException("no valid data");
      }
   }

   public void write(ByteBuf buf, PacketBound target) {
      if (this.data.purpose == PacketTask.UNKNOWN) {
         throw new IllegalArgumentException("Can't send packet without any purpose...");
      }

      int packetStart = buf.writerIndex();
      buf.writeInt(this.data.versions.getOrDefault(8, (byte)8));
      buf.writeByte(this.data.purpose.id);
      int countIndex = buf.writerIndex();
      buf.writeByte(0);
      int count = 0;

      try {
         ObjectIterator ex = SUB_PACKETS.values().iterator();

         while (ex.hasNext()) {
            AbstractNetworkPacket packet = (AbstractNetworkPacket)ex.next();
            if (packet.doWrite(this.data) && packet.boundsTo().contains(target)) {
               boolean optional = packet.isOptional();
               int subPacketStart = buf.writerIndex();

               try {
                  byte packetVersion = packet.getVer(this.data.versions);
                  buf.writeByte(packet.getID());
                  buf.writeByte(packetVersion);
                  int sizeIndex = buf.writerIndex();
                  buf.writeInt(0);
                  int contentStart = buf.writerIndex();
                  packet.write(buf, this.data, packetVersion);
                  int contentSize = buf.writerIndex() - contentStart;
                  buf.setInt(sizeIndex, contentSize);
                  int totalSize = buf.writerIndex() - packetStart;
                  if (optional && totalSize > this.data.sizeLimit) {
                     buf.writerIndex(subPacketStart);
                     this.data.skippedPackets.add(packet.getID());
                     CommonData.LOGGER.warn("Writing {} skipped!", packet);
                  } else {
                     count++;
                  }
               } catch (IOException exx) {
                  buf.writerIndex(subPacketStart);
                  if (!optional) {
                     throw exx;
                  }

                  CommonData.LOGGER.warn("Exception while writing {} sub-packet!", packet, exx);
               }
            }
         }

         int sizeSum = buf.writerIndex() - packetStart;
         if (this.data.strictSizeLimit && sizeSum > this.data.sizeLimit) {
            throw new RuntimeException(String.format("Can't send emote, packet's size (%s) is bigger than max allowed (%s)!", sizeSum, this.data.sizeLimit));
         }

         buf.setByte(countIndex, count);
      } catch (IOException ex) {
         throw new UncheckedIOException(ex);
      }
   }

   static {
      Byte2ByteOpenHashMap map = new Byte2ByteOpenHashMap();

      for (AbstractNetworkPacket packet : new AbstractNetworkPacket[]{
         new NewAnimPacket(),
         new EmoteDataPacket(),
         new PlayerDataPacket(),
         new DiscoveryPacket(),
         new StopPacket(),
         new SongPacket(),
         new EmoteHeaderPacket(),
         new EmoteIconPacket()
      }) {
         map.put(packet.getID(), packet.getVer());
      }

      defaultVersions = Byte2ByteMaps.unmodifiable(map);
      SUB_PACKETS = new NetHashMap(
         new DiscoveryPacket(),
         new NewAnimPacket(),
         new EmoteDataPacket(),
         new PlayerDataPacket(),
         new StopPacket(),
         new EmoteHeaderPacket(),
         new SongPacket(),
         new EmoteIconPacket()
      );
   }

   public record Builder(NetData data) {
      public Builder() {
         this(new NetData());
      }

      public EmotePacket.Builder setVersion(Map<Byte, Byte> versions) {
         this.data.versions.clear();
         this.data.versions.putAll(versions);
         return this;
      }

      public NetData data() {
         return this.data.copy();
      }

      public EmotePacket.Builder copy() {
         return new EmotePacket.Builder(this.data.copy());
      }

      public EmotePacket build() {
         return new EmotePacket(this.data);
      }

      public EmotePacket build(int sizeLimit, boolean strict) {
         return this.setSizeLimit(sizeLimit, strict).build();
      }

      public EmotePacket.Builder setSizeLimit(int sizeLimit, boolean strict) {
         if (sizeLimit <= 0) {
            throw new IllegalArgumentException("Size limit must be positive");
         }

         this.data.sizeLimit = sizeLimit;
         this.data.strictSizeLimit = strict;
         return this;
      }

      public EmotePacket.Builder configureToStreamEmote(Animation emoteData, @Nullable UUID player) {
         if (this.data.purpose != PacketTask.UNKNOWN) {
            throw new IllegalArgumentException("Can't send and stop emote at the same time");
         }

         this.data.purpose = PacketTask.STREAM;
         this.data.emoteData = emoteData;
         this.data.player = player;
         return this;
      }

      public EmotePacket.Builder configureToSaveEmote(Animation emoteData) {
         if (this.data.purpose != PacketTask.UNKNOWN) {
            throw new IllegalArgumentException("already configured?!");
         }

         this.data.purpose = PacketTask.FILE;
         this.data.sizeLimit = Integer.MAX_VALUE;
         this.data.emoteData = emoteData;
         return this;
      }

      public EmotePacket.Builder configureEmoteTick(float tick) {
         this.data.tick = tick;
         return this;
      }

      public EmotePacket.Builder configureTarget(@Nullable UUID target) {
         this.data.player = target;
         return this;
      }

      public EmotePacket.Builder configureToStreamEmote(Animation emoteData) {
         return this.configureToStreamEmote(emoteData, null);
      }

      public EmotePacket.Builder configureToSendStop(UUID emoteID, @Nullable UUID player) {
         if (this.data.purpose != PacketTask.UNKNOWN) {
            throw new IllegalArgumentException("Can't send emote and stop at the same time");
         }

         this.data.purpose = PacketTask.STOP;
         this.data.stopEmoteID = emoteID;
         this.data.player = player;
         return this;
      }

      public EmotePacket.Builder configureToSendStop(UUID emoteID) {
         return this.configureToSendStop(emoteID, null);
      }

      public EmotePacket.Builder configureToConfigExchange() {
         if (this.data.purpose != PacketTask.UNKNOWN) {
            throw new IllegalArgumentException("Can't send config with emote or stop data...");
         }

         this.data.purpose = PacketTask.CONFIG;
         this.setVersion(EmotePacket.defaultVersions);
         return this;
      }

      public void removePlayerID() {
         this.data.player = null;
      }

      public EmotePacket.Builder strictSizeLimit(boolean strict) {
         this.data.strictSizeLimit = strict;
         return this;
      }
   }
}
