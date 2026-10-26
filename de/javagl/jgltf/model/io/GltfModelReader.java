package de.javagl.jgltf.model.io;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelCreatorV2;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class GltfModelReader {
   private Consumer<? super JsonError> jsonErrorConsumer = JsonErrorConsumers.createLogging();

   public void setJsonErrorConsumer(Consumer<? super JsonError> jsonErrorConsumer) {
      this.jsonErrorConsumer = jsonErrorConsumer;
   }

   public GltfModel read(URI uri) throws IOException {
      GltfAssetReader gltfAssetReader = new GltfAssetReader();
      gltfAssetReader.setJsonErrorConsumer(this.jsonErrorConsumer);
      GltfAsset gltfAsset = gltfAssetReader.read(uri);
      return createModel(gltfAsset);
   }

   public GltfModel read(Path path) throws IOException {
      GltfAssetReader gltfAssetReader = new GltfAssetReader();
      gltfAssetReader.setJsonErrorConsumer(this.jsonErrorConsumer);
      GltfAsset gltfAsset = gltfAssetReader.read(path);
      return createModel(gltfAsset);
   }

   public GltfModel readWithoutReferences(URI uri) throws IOException {
      try (InputStream inputStream = uri.toURL().openStream()) {
         return this.readWithoutReferences(inputStream);
      }
   }

   public GltfModel readWithoutReferences(InputStream inputStream) throws IOException {
      GltfAssetReader gltfAssetReader = new GltfAssetReader();
      gltfAssetReader.setJsonErrorConsumer(this.jsonErrorConsumer);
      GltfAsset gltfAsset = gltfAssetReader.readWithoutReferences(inputStream);
      return createModel(gltfAsset);
   }

   private static GltfModel createModel(GltfAsset gltfAsset) throws IOException {
      if (gltfAsset instanceof GltfAssetV1) {
         GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
         return new GltfModelV1(gltfAssetV1);
      } else if (gltfAsset instanceof GltfAssetV2) {
         GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
         return GltfModelCreatorV2.create(gltfAssetV2);
      } else {
         throw new IOException("The glTF asset has an unknown version: " + gltfAsset);
      }
   }
}
