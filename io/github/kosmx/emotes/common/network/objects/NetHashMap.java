package io.github.kosmx.emotes.common.network.objects;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectLinkedOpenHashMap;

public class NetHashMap extends Byte2ObjectLinkedOpenHashMap<AbstractNetworkPacket> {
   public NetHashMap(AbstractNetworkPacket... packets) {
      for (AbstractNetworkPacket packet : packets) {
         this.put(packet);
      }
   }

   public void put(AbstractNetworkPacket packet) {
      this.put(packet.getID(), packet);
   }
}
