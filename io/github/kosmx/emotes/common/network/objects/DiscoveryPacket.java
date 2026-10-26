package io.github.kosmx.emotes.common.network.objects;

import io.github.kosmx.emotes.common.network.PacketTask;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;

public class DiscoveryPacket extends AbstractNetworkPacket {
   @Override
   public void read(ByteBuf buf, NetData data, byte version) {
      int size = buf.readInt();
      HashMap<Byte, Byte> map = new HashMap<>();

      for (int i = 0; i < size; i++) {
         byte id = buf.readByte();
         byte ver = buf.readByte();
         map.put(id, ver);
      }

      data.versions.clear();
      data.versions.putAll(map);
      data.versionsUpdated = true;
   }

   @Override
   public void write(ByteBuf buf, NetData data, byte version) {
      buf.writeInt(data.versions.size());
      data.versions.forEach((aByte, integer) -> {
         buf.writeByte(aByte);
         buf.writeByte(integer);
      });
   }

   @Override
   public byte getID() {
      return 8;
   }

   @Override
   public byte getVer() {
      return 8;
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.purpose == PacketTask.CONFIG;
   }
}
