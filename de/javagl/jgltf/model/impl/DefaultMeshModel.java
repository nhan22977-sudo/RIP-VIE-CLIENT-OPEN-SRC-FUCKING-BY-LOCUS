package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultMeshModel extends AbstractNamedModelElement implements MeshModel {
   private final List<MeshPrimitiveModel> meshPrimitiveModels = new ArrayList<>();
   private float[] weights;

   public void addMeshPrimitiveModel(MeshPrimitiveModel meshPrimitiveModel) {
      this.meshPrimitiveModels.add(meshPrimitiveModel);
   }

   public void setWeights(float[] weights) {
      this.weights = weights;
   }

   @Override
   public List<MeshPrimitiveModel> getMeshPrimitiveModels() {
      return Collections.unmodifiableList(this.meshPrimitiveModels);
   }

   @Override
   public float[] getWeights() {
      return this.weights;
   }
}
