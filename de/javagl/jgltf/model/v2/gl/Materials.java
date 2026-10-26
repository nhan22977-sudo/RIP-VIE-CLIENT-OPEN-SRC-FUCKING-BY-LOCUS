package de.javagl.jgltf.model.v2.gl;

import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;

public class Materials {
   public static Material createDefaultMaterial() {
      Material material = new Material();
      material.setPbrMetallicRoughness(createDefaultMaterialPbrMetallicRoughness());
      material.setNormalTexture(null);
      material.setOcclusionTexture(null);
      material.setEmissiveTexture(null);
      material.setEmissiveFactor(material.defaultEmissiveFactor());
      material.setAlphaMode(material.defaultAlphaMode());
      material.setAlphaCutoff(material.defaultAlphaCutoff());
      material.setDoubleSided(material.defaultDoubleSided());
      return material;
   }

   public static MaterialPbrMetallicRoughness createDefaultMaterialPbrMetallicRoughness() {
      MaterialPbrMetallicRoughness result = new MaterialPbrMetallicRoughness();
      result.setBaseColorFactor(result.defaultBaseColorFactor());
      result.setMetallicFactor(result.defaultMetallicFactor());
      result.setRoughnessFactor(result.defaultRoughnessFactor());
      return result;
   }

   private Materials() {
   }
}
