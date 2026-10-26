package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Material;
import java.util.Arrays;

class Materials {
   static Material createDefaultMaterial(String techniqueId) {
      Material material = new Material();
      material.addValues("emission", Arrays.asList(0.5, 0.5, 0.5, 1.0));
      material.setTechnique(techniqueId);
      return material;
   }

   private Materials() {
   }
}
