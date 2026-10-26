package io.github.kosmx.emotes.common.network.objects;

import io.github.kosmx.emotes.common.network.PacketBound;
import io.github.kosmx.emotes.common.network.PacketTask;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;

public class EmoteIconPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !EmoteIconPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return 18;
   }

   @Override
   public byte getVer() {
      return 18;
   }

   @Override
   public void read(ByteBuf byteBuf, NetData config, byte version) throws IOException {
      if (byteBuf.readableBytes() < 4) {
         throw new IOException("Invalid icon packet: missing size");
      }

      int size = byteBuf.readInt();
      if (size > 0) {
         if (size > byteBuf.readableBytes()) {
            throw new IOException("Invalid icon packet size: " + size);
         }

         ByteBuffer buffer = ByteBuffer.allocate(size);
         byteBuf.readBytes(buffer);
         buffer.flip();
         config.extraData.put("iconData", buffer.asReadOnlyBuffer());
      }
   }

   @Override
   public void write(ByteBuf byteBuf, NetData config, byte version) {
      assert config.emoteData != null;
      ByteBuffer iconData = config.emoteData.data().getBinary("iconData");
      if (iconData != null && iconData.hasRemaining()) {
         byteBuf.writeInt(iconData.remaining());
         byteBuf.writeBytes(iconData.duplicate());
      } else {
         byteBuf.writeInt(0);
      }
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.purpose == PacketTask.FILE && config.emoteData != null && config.emoteData.data().has("iconData");
   }

   @Override
   public boolean isOptional() {
      return true;
   }

   @Override
   public Set<PacketBound> boundsTo() {
      return PacketBound.TO_CLIENT;
   }
}
