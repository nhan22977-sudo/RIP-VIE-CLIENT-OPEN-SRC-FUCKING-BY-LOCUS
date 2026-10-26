package io.github.kosmx.emotes.common.network.objects;

import io.github.kosmx.emotes.common.network.CommonNetwork;
import io.github.kosmx.emotes.common.network.PacketBound;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Set;

public class EmoteHeaderPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !EmoteHeaderPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return 17;
   }

   @Override
   public byte getVer() {
      return 2;
   }

   @Override
   public void read(ByteBuf byteBuf, NetData config, byte version) {
      config.extraData.put("name", CommonNetwork.readString(byteBuf));
      config.extraData.put("description", CommonNetwork.readString(byteBuf));
      config.extraData.put("author", CommonNetwork.readString(byteBuf));
      if (version >= 2) {
         config.extraData.put("folderpath", CommonNetwork.readString(byteBuf));
         config.extraData.put("bages", CommonNetwork.readList(byteBuf, CommonNetwork::readString));
      }
   }

   @Override
   public void write(ByteBuf byteBuf, NetData config, byte version) {
      assert config.emoteData != null;
      CommonNetwork.writeString(byteBuf, (String)config.emoteData.data().getRaw("name"));
      CommonNetwork.writeString(byteBuf, (String)config.emoteData.data().getRaw("description"));
      CommonNetwork.writeString(byteBuf, (String)config.emoteData.data().getRaw("author"));
      if (version >= 2) {
         CommonNetwork.writeString(byteBuf, (String)config.emoteData.data().getRaw("folderpath"));
         CommonNetwork.writeList(byteBuf, (List<String>)config.emoteData.data().getRaw("bages"), CommonNetwork::writeString);
      }
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.emoteData != null && config.purpose.exchangeHeader;
   }

   @Override
   public Set<PacketBound> boundsTo() {
      return PacketBound.TO_CLIENT;
   }
}
