package de.javagl.jgltf.model.io;

import de.javagl.jgltf.model.io.v1.RawBinaryGltfDataReaderV1;
import de.javagl.jgltf.model.io.v2.RawBinaryGltfDataReaderV2;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class RawGltfDataReader {
   private static final int MAGIC_BINARY_GLTF_HEADER = 1179937895;
   private static final int BINARY_GLTF_VERSION_1 = 1;
   private static final int BINARY_GLTF_VERSION_2 = 2;

   public static RawGltfData read(InputStream inputStream) throws IOException {
      byte[] rawData = IO.readStream(inputStream);
      if (rawData.length >= 8) {
         ByteBuffer data = ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN);
         IntBuffer intData = data.asIntBuffer();
         int magic = intData.get(0);
         if (magic == 1179937895) {
            int version = intData.get(1);
            if (version == 1) {
               return RawBinaryGltfDataReaderV1.readBinaryGltf(data);
            }

            if (version == 2) {
               return RawBinaryGltfDataReaderV2.readBinaryGltf(data);
            }

            throw new IOException("Unknown binary glTF version: " + version);
         }
      }

      ByteBuffer jsonData = Buffers.create(rawData);
      return new RawGltfData(jsonData, null);
   }

   private RawGltfDataReader() {
   }
}
