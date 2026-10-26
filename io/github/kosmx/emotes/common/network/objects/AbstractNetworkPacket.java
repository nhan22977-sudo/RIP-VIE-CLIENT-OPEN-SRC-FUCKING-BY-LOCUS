package io.github.kosmx.emotes.common.network.objects;

import io.github.kosmx.emotes.common.network.PacketBound;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public abstract class AbstractNetworkPacket {
   public abstract byte getID();

   public abstract byte getVer();

   public byte getVer(Map<Byte, Byte> versions) {
      if (!versions.containsKey(this.getID())) {
         throw new IllegalArgumentException("Versions should contain it's id");
      } else {
         return (byte)Math.min(this.getVer(), versions.get(this.getID()));
      }
   }

   public abstract void read(ByteBuf var1, NetData var2, byte var3) throws IOException;

   public abstract void write(ByteBuf var1, NetData var2, byte var3) throws IOException;

   public abstract boolean doWrite(NetData var1);

   public boolean isOptional() {
      return false;
   }

   public Set<PacketBound> boundsTo() {
      return PacketBound.BOTH;
   }
}
