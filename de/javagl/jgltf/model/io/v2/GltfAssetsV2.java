package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.model.GltfModel;

public class GltfAssetsV2 {
   public static GltfAssetV2 createDefault(GltfModel gltfModel) {
      DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
      return assetCreator.create(gltfModel);
   }

   public static GltfAssetV2 createBinary(GltfModel gltfModel) {
      BinaryAssetCreatorV2 assetCreator = new BinaryAssetCreatorV2();
      return assetCreator.create(gltfModel);
   }

   public static GltfAssetV2 createEmbedded(GltfModel gltfModel) {
      EmbeddedAssetCreatorV2 assetCreator = new EmbeddedAssetCreatorV2();
      return assetCreator.create(gltfModel);
   }

   private GltfAssetsV2() {
   }
}
