package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.GltfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class GltfAssetWriterV1 {
   private static final int MAGIC_BINARY_GLTF_HEADER = 1179937895;
   private static final int BINARY_GLTF_VERSION = 1;
   private static final int CONTENT_FORMAT_JSON = 0;

   public void writeBinary(GltfAssetV1 gltfAsset, OutputStream outputStream) throws IOException {
      GlTF gltf = gltfAsset.getGltf();

      byte[] sceneData;
      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
         GltfWriter gltfWriter = new GltfWriter();
         gltfWriter.setIndenting(false);
         gltfWriter.write(gltf, baos);
         sceneData = baos.toByteArray();
      }

      ByteBuffer binaryData = gltfAsset.getBinaryData();
      if (binaryData == null) {
         binaryData = ByteBuffer.wrap(new byte[0]);
      }

      byte[] headerData = new byte[20];
      int magic = 1179937895;
      int version = 1;
      int length = headerData.length + sceneData.length + binaryData.capacity();
      int contentLength = sceneData.length;
      int contentFormat = 0;
      IntBuffer headerBuffer = ByteBuffer.wrap(headerData).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
      headerBuffer.put(magic);
      headerBuffer.put(version);
      headerBuffer.put(length);
      headerBuffer.put(contentLength);
      headerBuffer.put(contentFormat);
      WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
      writableByteChannel.write(ByteBuffer.wrap(headerData));
      writableByteChannel.write(ByteBuffer.wrap(sceneData));
      writableByteChannel.write(binaryData.slice());
   }
}
