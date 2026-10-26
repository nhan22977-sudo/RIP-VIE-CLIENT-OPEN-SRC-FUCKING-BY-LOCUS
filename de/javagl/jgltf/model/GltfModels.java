package de.javagl.jgltf.model;

import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelCreatorV2;

public class GltfModels {
   public static GltfModel create(GltfAsset gltfAsset) {
      if (gltfAsset instanceof GltfAssetV1) {
         GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
         return new GltfModelV1(gltfAssetV1);
      } else if (gltfAsset instanceof GltfAssetV2) {
         GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
         return GltfModelCreatorV2.create(gltfAssetV2);
      } else {
         throw new IllegalArgumentException("The glTF asset has an unknown version: " + gltfAsset);
      }
   }

   private GltfModels() {
   }
}
