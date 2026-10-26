package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.model.v1.GltfModelV1;

public class GltfAssetsV1 {
   public static GltfAssetV1 createDefault(GltfModelV1 gltfModel) {
      DefaultAssetCreatorV1 assetCreator = new DefaultAssetCreatorV1();
      return assetCreator.create(gltfModel);
   }

   public static GltfAssetV1 createBinary(GltfModelV1 gltfModel) {
      BinaryAssetCreatorV1 assetCreator = new BinaryAssetCreatorV1();
      return assetCreator.create(gltfModel);
   }

   public static GltfAssetV1 createEmbedded(GltfModelV1 gltfModel) {
      EmbeddedAssetCreatorV1 assetCreator = new EmbeddedAssetCreatorV1();
      return assetCreator.create(gltfModel);
   }

   private GltfAssetsV1() {
   }
}
