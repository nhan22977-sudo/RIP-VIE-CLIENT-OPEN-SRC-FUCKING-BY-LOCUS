package de.javagl.jgltf.model;

import de.javagl.jgltf.impl.v1.Asset;
import de.javagl.jgltf.impl.v1.GlTF;
import java.util.Objects;

public class GltfUtils {
   public static String getVersion(GlTF gltf) {
      Objects.requireNonNull(gltf, "The gltf is null");
      Asset asset = gltf.getAsset();
      if (asset == null) {
         return "1.0";
      }

      String version = asset.getVersion();
      return version == null ? "1.0" : version;
   }

   private GltfUtils() {
   }
}
