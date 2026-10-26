package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.RawGltfData;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RawBinaryGltfDataReaderV2 {
   private static final Logger logger = Logger.getLogger(RawBinaryGltfDataReaderV2.class.getName());
   private static final int BINARY_GLTF_VERSION_2_HEADER_LENGTH_IN_BYTES = 12;
   private static final int CHUNK_TYPE_JSON = 1313821514;
   private static final int CHUNK_TYPE_BIN = 5130562;

   public static RawGltfData readBinaryGltf(ByteBuffer data) throws IOException {
      ByteBuffer d = data;
      int headerLength = 12;
      if (d.capacity() < headerLength) {
         throw new IOException("Expected header of size " + headerLength + ", but only found " + d.capacity() + " bytes");
      }

      int length = d.getInt(8);
      if (length > d.capacity()) {
         throw new IOException("Data length is " + d.capacity() + ", expected " + length);
      }

      if (length < d.capacity()) {
         logger.info("Data length is " + d.capacity() + ", expected " + length + " - truncating");
         d = Buffers.createSlice(d, 0, length);
      }

      List<RawBinaryGltfDataReaderV2.Chunk> chunks = readChunks(d);
      if (chunks.isEmpty()) {
         throw new IOException("Found no chunks in binary glTF data");
      }

      RawBinaryGltfDataReaderV2.Chunk jsonChunk = chunks.get(0);
      if (jsonChunk.type != 1313821514) {
         throw new IOException("First chunk must be of type JSON (1313821514), but found " + jsonChunk.type);
      }

      ByteBuffer jsonData = jsonChunk.data;
      ByteBuffer binData = null;
      if (chunks.size() > 1) {
         RawBinaryGltfDataReaderV2.Chunk binChunk = chunks.get(1);
         if (binChunk.type != 5130562) {
            throw new IOException("Second chunk must be of type BIN (5130562), but found " + jsonChunk.type);
         }

         binData = binChunk.data;
      }

      return new RawGltfData(jsonData, binData);
   }

   private static List<RawBinaryGltfDataReaderV2.Chunk> readChunks(ByteBuffer data) throws IOException {
      int headerLength = 12;
      List<RawBinaryGltfDataReaderV2.Chunk> chunks = new ArrayList<>();
      int offset = headerLength;

      while (offset < data.capacity()) {
         RawBinaryGltfDataReaderV2.Chunk chunk = new RawBinaryGltfDataReaderV2.Chunk();
         chunk.length = data.getInt(offset);
         offset += 4;
         chunk.type = data.getInt(offset);
         offset += 4;
         if (offset + chunk.length > data.capacity()) {
            throw new IOException(
               "The offset for the data of chunk "
                  + chunks.size()
                  + " is "
                  + offset
                  + ", its length is "
                  + chunk.length
                  + ", but "
                  + (offset + chunk.length)
                  + " is larger than capacity of the buffer, which is only "
                  + data.capacity()
            );
         }

         if (chunk.length > 0) {
            chunk.data = Buffers.createSlice(data, offset, chunk.length);
         }

         offset += chunk.length;
         chunks.add(chunk);
      }

      return chunks;
   }

   private RawBinaryGltfDataReaderV2() {
   }

   private static class Chunk {
      int length;
      int type;
      ByteBuffer data;

      private Chunk() {
      }
   }
}
