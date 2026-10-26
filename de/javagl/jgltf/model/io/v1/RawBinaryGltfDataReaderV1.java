package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.RawGltfData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Logger;

public class RawBinaryGltfDataReaderV1 {
   private static final Logger logger = Logger.getLogger(RawBinaryGltfDataReaderV1.class.getName());
   private static final int BINARY_GLTF_VERSION_1_HEADER_LENGTH_IN_BYTES = 20;
   private static final int CONTENT_FORMAT_JSON = 0;

   public static RawGltfData readBinaryGltf(ByteBuffer data) throws IOException {
      ByteBuffer d = data;
      int headerLength = 20;
      if (d.capacity() < headerLength) {
         throw new IOException("Expected header of size " + headerLength + ", but only found " + d.capacity() + " bytes");
      }

      IntBuffer intData = d.asIntBuffer();
      int length = intData.get(2);
      if (length > d.capacity()) {
         throw new IOException("Data length is " + d.capacity() + ", expected " + length);
      }

      if (length < d.capacity()) {
         logger.info("Data length is " + d.capacity() + ", expected " + length + " - truncating");
         d = Buffers.createSlice(d, 0, length);
      }

      int contentLength = intData.get(3);
      int contentFormat = intData.get(4);
      if (contentFormat != 0) {
         throw new IOException("Expected content format to be JSON (0), but found " + contentFormat);
      }

      ByteBuffer contentData = Buffers.createSlice(d, headerLength, contentLength);
      int bodyByteOffset = headerLength + contentLength;
      int bodyByteLength = length - bodyByteOffset;
      ByteBuffer bodyData = null;
      if (bodyByteLength > 0) {
         bodyData = Buffers.createSlice(d, bodyByteOffset, bodyByteLength);
      }

      return new RawGltfData(contentData, bodyData);
   }

   private RawBinaryGltfDataReaderV1() {
   }
}
