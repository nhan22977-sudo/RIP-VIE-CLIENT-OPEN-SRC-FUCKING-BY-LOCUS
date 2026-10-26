package de.javagl.jgltf.model.io;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class GltfAssetReader {
   private Consumer<? super JsonError> jsonErrorConsumer;

   public void setJsonErrorConsumer(Consumer<? super JsonError> jsonErrorConsumer) {
      this.jsonErrorConsumer = jsonErrorConsumer;
   }

   public GltfAsset read(URI uri) throws IOException {
      try (InputStream inputStream = uri.toURL().openStream()) {
         GltfAsset gltfAsset = this.readWithoutReferences(inputStream);
         URI baseUri = IO.getParent(uri);
         GltfReferenceResolver.resolveAll(gltfAsset.getReferences(), baseUri);
         return gltfAsset;
      }
   }

   public GltfAsset read(Path path) throws IOException {
      try (InputStream inputStream = path.toUri().toURL().openStream()) {
         GltfAsset gltfAsset = this.readWithoutReferences(inputStream);
         Path basePath = IO.getParent(path);
         GltfReferenceResolver.resolveAll(gltfAsset.getReferences(), basePath);
         return gltfAsset;
      }
   }

   public GltfAsset readWithoutReferences(URI uri) throws IOException {
      try (InputStream inputStream = uri.toURL().openStream()) {
         return this.readWithoutReferences(inputStream);
      }
   }

   public GltfAsset readWithoutReferences(InputStream inputStream) throws IOException {
      RawGltfData rawGltfData = RawGltfDataReader.read(inputStream);
      return this.read(rawGltfData);
   }

   GltfAsset read(RawGltfData rawGltfData) throws IOException {
      GltfReader gltfReader = new GltfReader();
      gltfReader.setJsonErrorConsumer(this.jsonErrorConsumer);
      ByteBuffer jsonData = rawGltfData.getJsonData();

      try (InputStream jsonInputStream = Buffers.createByteBufferInputStream(jsonData)) {
         gltfReader.read(jsonInputStream);
         int majorVersion = gltfReader.getMajorVersion();
         if (majorVersion == 1) {
            GlTF gltfV1 = gltfReader.getAsGltfV1();
            return new GltfAssetV1(gltfV1, rawGltfData.getBinaryData());
         } else if (majorVersion == 2) {
            de.javagl.jgltf.impl.v2.GlTF gltfV2 = gltfReader.getAsGltfV2();
            return new GltfAssetV2(gltfV2, rawGltfData.getBinaryData());
         } else {
            throw new IOException("Unsupported major version: " + majorVersion);
         }
      }
   }
}
