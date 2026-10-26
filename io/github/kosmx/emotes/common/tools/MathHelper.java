package io.github.kosmx.emotes.common.tools;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MathHelper {
   public static ByteBuffer readFromIStream(InputStream stream) throws IOException {
      return ByteBuffer.wrap(stream.readAllBytes());
   }

   public static byte[] safeGetBytesFromBuffer(ByteBuffer byteBuffer) {
      if (!byteBuffer.isDirect() && !byteBuffer.isReadOnly()) {
         return byteBuffer.array();
      }

      byte[] bytes = new byte[byteBuffer.remaining()];
      byteBuffer.get(bytes);
      return bytes;
   }

   public static byte[] readBytes(ByteBuf buf) {
      byte[] bytes = new byte[buf.readableBytes()];
      buf.readBytes(bytes);
      return bytes;
   }
}
