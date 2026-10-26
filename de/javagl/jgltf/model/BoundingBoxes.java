package de.javagl.jgltf.model;

import java.util.Objects;

public class BoundingBoxes {
   public static float[] computeBoundingBoxMinMax(GltfModel gltfModel) {
      Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
      BoundingBoxComputer boundingBoxComputer = new BoundingBoxComputer(gltfModel);
      BoundingBox boundingBox = boundingBoxComputer.compute();
      return new float[]{
         boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ()
      };
   }

   private BoundingBoxes() {
   }
}
