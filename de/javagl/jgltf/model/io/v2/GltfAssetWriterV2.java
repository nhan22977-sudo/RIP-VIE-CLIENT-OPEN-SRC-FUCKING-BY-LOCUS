package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

public final class GltfAssetWriterV2 {
   private static final int MAGIC_BINARY_GLTF_HEADER = 1179937895;
   private static final int BINARY_GLTF_VERSION = 2;
   private static final int CHUNK_TYPE_JSON = 1313821514;
   private static final int CHUNK_TYPE_BIN = 5130562;

   public void writeBinary(GltfAssetV2 gltfAsset, OutputStream outputStream) throws IOException {
      GlTF gltf = gltfAsset.getGltf();

      byte[] jsonData;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
         GltfWriter gltfWriter = new GltfWriter();
         gltfWriter.setIndenting(false);
         gltfWriter.write(gltf, baos);
         jsonData = baos.toByteArray();
      }

      if (jsonData.length % 4 != 0) {
         int oldLength = jsonData.length;
         int padding = 4 - oldLength % 4;
         jsonData = Arrays.copyOf(jsonData, oldLength + padding);

         for (int i = 0; i < padding; i++) {
            jsonData[oldLength + i] = 32;
         }
      }

      ByteBuffer binaryData = gltfAsset.getBinaryData();
      if (binaryData == null) {
         binaryData = ByteBuffer.wrap(new byte[0]);
      }

      if (binaryData.capacity() % 4 != 0) {
         int padding = 4 - binaryData.capacity() % 4;
         binaryData = Buffers.copyOf(binaryData, binaryData.capacity() + padding);
      }

      GltfAssetWriterV2.ChunkData jsonChunkData = new GltfAssetWriterV2.ChunkData();
      jsonChunkData.append(jsonData.length);
      jsonChunkData.append(1313821514);
      jsonChunkData.append(ByteBuffer.wrap(jsonData));
      GltfAssetWriterV2.ChunkData binChunkData = new GltfAssetWriterV2.ChunkData();
      binChunkData.append(binaryData.capacity());
      binChunkData.append(5130562);
      binChunkData.append(binaryData);
      GltfAssetWriterV2.ChunkData headerData = new GltfAssetWriterV2.ChunkData();
      headerData.append(1179937895);
      headerData.append(2);
      int length = 12 + jsonData.length + 8 + binaryData.capacity() + 8;
      headerData.append(length);
      WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
      writableByteChannel.write(headerData.get());
      writableByteChannel.write(jsonChunkData.get());
      writableByteChannel.write(binChunkData.get());
   }

   private static class ChunkData {
      private ByteArrayOutputStream baos = new ByteArrayOutputStream();

      ChunkData() {
      }

      void append(int value) throws IOException {
         this.baos.write(value >> 0 & 0xFF);
         this.baos.write(value >> 8 & 0xFF);
         this.baos.write(value >> 16 & 0xFF);
         this.baos.write(value >> 24 & 0xFF);
      }

      void append(ByteBuffer buffer) throws IOException {
         WritableByteChannel writableByteChannel = Channels.newChannel(this.baos);
         writableByteChannel.write(buffer.slice());
      }

      ByteBuffer get() {
         return ByteBuffer.wrap(this.baos.toByteArray());
      }
   }
}
