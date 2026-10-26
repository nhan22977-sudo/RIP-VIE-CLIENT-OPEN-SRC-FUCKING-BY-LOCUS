package de.javagl.jgltf.model.io;

import java.nio.ByteBuffer;

public class GltfAssets {
   public static boolean isDefault(GltfAsset gltfAsset) {
      ByteBuffer binaryData = gltfAsset.getBinaryData();
      return binaryData != null && binaryData.capacity() > 0 ? false : !containsDataUriReferences(gltfAsset);
   }

   public static boolean isEmbedded(GltfAsset gltfAsset) {
      ByteBuffer binaryData = gltfAsset.getBinaryData();
      return binaryData != null && binaryData.capacity() > 0 ? false : !containsFileUriReferences(gltfAsset);
   }

   public static boolean isBinary(GltfAsset gltfAsset) {
      return containsFileUriReferences(gltfAsset) ? false : !containsDataUriReferences(gltfAsset);
   }

   private static boolean containsFileUriReferences(GltfAsset gltfAsset) {
      for (GltfReference reference : gltfAsset.getReferences()) {
         String uriString = reference.getUri();
         if (!IO.isDataUriString(uriString)) {
            return true;
         }
      }

      return false;
   }

   private static boolean containsDataUriReferences(GltfAsset gltfAsset) {
      for (GltfReference reference : gltfAsset.getReferences()) {
         String uriString = reference.getUri();
         if (!IO.isDataUriString(uriString)) {
            return true;
         }
      }

      return false;
   }

   private GltfAssets() {
   }
}
